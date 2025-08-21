package com.example.PdfReader.Service;
import com.example.PdfReader.exception.ApiException;
import com.example.PdfReader.modelDto.*;
import com.example.PdfReader.pdfUtils.FileValidators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PdfService {

    private final PdfTextExtractor pdfTextExtractor;
    private final SearchService searchService;
    private final FileValidators fileValidators;
    private final ChunkingService chunkingService;
    private final OpenAIEmbeddingService embeddingService;
    private final SemanticSearchService semanticSearchService;

    private final Map<UUID, DocumentData> documents = new ConcurrentHashMap<>();
    private final Map<UUID, List<ChunkData>> documentChunks = new ConcurrentHashMap<>();

    public PdfService(PdfTextExtractor pdfTextExtractor,
                      SearchService searchService,
                      FileValidators fileValidators,
                      ChunkingService chunkingService,
                      OpenAIEmbeddingService embeddingService,
                      SemanticSearchService semanticSearchService)
    {
        this.pdfTextExtractor = pdfTextExtractor;this.searchService = searchService;this.fileValidators = fileValidators;this.chunkingService = chunkingService;this.embeddingService = embeddingService;this.semanticSearchService = semanticSearchService;
    }

    public UploadResponse uploadPdf(MultipartFile file) {
        try {
            log.info("[PdfService] :: uploadPdf :: Validating the uploaded document");
            fileValidators.validatePdfFile(file);
            UUID fileId = UUID.randomUUID();
            List<String> pages = pdfTextExtractor.extractTextByPages(file);
            DocumentData docData = new DocumentData(file.getOriginalFilename(), pages, System.currentTimeMillis());
            documents.put(fileId, docData);

            try {
                List<ChunkData> chunks = chunkingService.chunkDocument(pages, fileId.toString());

                if (!chunks.isEmpty()) {
                    List<String> chunkTexts = chunks.stream().map(ChunkData::getContent)
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                    List<List<Double>> embeddings = embeddingService.generateBatchEmbeddings(chunkTexts);

                    for (int index = 0; index < Math.min(chunks.size(), embeddings.size()); index++) {
                        chunks.get(index).setEmbedding(embeddings.get(index));
                    }
                    documentChunks.put(fileId, chunks);
                }
            } catch (Exception e) {
                log.error("[PdfService] :: uploadPdf :: Failed to create semantic index: {}", e.getMessage());
            }
            return new UploadResponse(fileId, file.getOriginalFilename(), pages.size());
        } catch (Exception e) {
            throw new ApiException("Failed to upload PDF: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public SearchResponse searchInUploaded(UUID fileId, String query, Boolean caseSensitive, Integer contextChars, Boolean useSemantic) {
        log.info("[PdfService] :: searchInUploaded :: Starting search for file ID: {}, query: '{}', semantic: {}", fileId, query, useSemantic);

        DocumentData document = Optional.ofNullable(documents.get(fileId))
                .orElseThrow(() -> new ApiException("PDF not found with ID: " + fileId, HttpStatus.NOT_FOUND));

        List<Match> matches;

        if (Boolean.TRUE.equals(useSemantic)) {
            List<ChunkData> chunks = documentChunks.get(fileId);
            List<SemanticMatch> semanticMatches = semanticSearchService.semanticSearch(query, chunks, 0.75, 10);
            matches = new ArrayList<>(semanticMatches);
            matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        } else {
            matches = searchService.searchInDocument(document, query, caseSensitive, contextChars);
        }
        return new SearchResponse(fileId, document.getFilename(), query, matches, matches.size());
    }



    public SearchResponse uploadAndSearch(MultipartFile file, String query, Boolean caseSensitive, Integer contextChars, Boolean useSemantic) {
        log.info("[PdfService] :: uploadAndSearch :: Starting upload and search for file: {}, query: '{}'", Objects.nonNull(file) ? file.getOriginalFilename() : "null", query);
        try {
            UploadResponse uploadResponse = uploadPdf(file);
            UUID fileId = uploadResponse.getFileId();
            return searchInUploaded(fileId, query, caseSensitive, contextChars, useSemantic);

        } catch (Exception e) {
            throw new ApiException("Failed to upload and search PDF: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}