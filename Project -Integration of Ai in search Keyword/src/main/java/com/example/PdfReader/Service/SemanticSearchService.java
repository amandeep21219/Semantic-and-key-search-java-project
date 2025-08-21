package com.example.PdfReader.Service;
import com.example.PdfReader.enums.Enums;
import com.example.PdfReader.modelDto.ChunkData;
import com.example.PdfReader.modelDto.SemanticMatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SemanticSearchService {

    private final OpenAIEmbeddingService embeddingService;

    @Value("${pdf.semantic.similarity-threshold:0.75}")
    private double similarityThreshold;

    @Value("${pdf.semantic.top-n-results:10}")
    private int topNResults;

    public SemanticSearchService(OpenAIEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public List<SemanticMatch> semanticSearch(String query, List<ChunkData> chunks, Double threshold, Integer maxResults) {
        log.info("[SemanticSearchService] :: semanticSearch :: Starting semantic search for query: '{}' across {} chunks", query, chunks.size());

        double useThreshold = threshold != null ? threshold : similarityThreshold;
        int useMaxResults = maxResults != null ? maxResults : topNResults;

        try {
            List<Double> queryEmbedding = embeddingService.generateEmbedding(query);

            List<SemanticMatch> semanticMatches = new ArrayList<>();

            for (ChunkData chunk : chunks) {
                if (chunk.getEmbedding() == null || chunk.getEmbedding().isEmpty()) {
                    continue;
                }

                double similarity = cosineSimilarity(queryEmbedding, chunk.getEmbedding());

                if (similarity >= useThreshold) {
                    String snippet = createSemanticSnippet(chunk.getContent(), 300);
                    SemanticMatch match = new SemanticMatch(chunk.getPageNumber(), snippet, similarity, similarity, chunk.getChunkId(),
                            Enums.SEMANTIC.getValue()
                    );
                    semanticMatches.add(match);
                }
            }
            return semanticMatches.stream().sorted((a,b)->Double.compare(b.getCosineSimilarity(),a.getCosineSimilarity())).limit(useMaxResults).collect(Collectors.toList());


        } catch (Exception e) {
            log.error("[SemanticSearchService] :: semanticSearch :: Error while semantic search", e);
            return new ArrayList<>();
        }
    }

    public double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Vector dimensions did not match");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            double valueA = vectorA.get(i);
            double valueB = vectorB.get(i);

            dotProduct += valueA * valueB;
            normA += valueA * valueA;
            normB += valueB * valueB;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String createSemanticSnippet(String content, int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        if (content.length() <= maxLength) {
            return content;
        }

        int endPos = content.lastIndexOf('.', maxLength);
        if (endPos < maxLength / 2) {
            endPos = content.lastIndexOf(' ', maxLength);
        }
        if (endPos < 0) {
            endPos = maxLength;
        }

        return content.substring(0, endPos).trim() + "...";
    }
}