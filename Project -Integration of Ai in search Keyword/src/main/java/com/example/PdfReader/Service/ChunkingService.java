package com.example.PdfReader.Service;import com.example.PdfReader.modelDto.ChunkData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChunkingService {

    @Value("${pdf.chunking.token-size:400}")
    private int chunkTokenSize;

    @Value("${pdf.chunking.overlap-size:50}")
    private int overlapTokenSize;

    public List<ChunkData> chunkDocument(List<String> pages, String documentId) {
        log.info("[ChunkingService] :: chunkDocument :: Starting chunking for document: {} with {} pages", documentId, pages.size());

        List<ChunkData> chunks = new ArrayList<>();
        int globalPosition = 0;

        for (int pageNum = 0; pageNum < pages.size(); pageNum++) {
            String pageText = pages.get(pageNum);
            if (pageText.trim().isEmpty()) {
                continue;
            }

            List<ChunkData> pageChunks = chunkPage(pageText, pageNum + 1, globalPosition, documentId);
            chunks.addAll(pageChunks);
            globalPosition += pageText.length();
        }

        log.info("[ChunkingService] :: chunkDocument :: Created {} total chunks for document: {}", chunks.size(), documentId);
        return chunks;
    }

    private List<ChunkData> chunkPage(String pageText, int pageNumber, int startOffset, String documentId) {
        List<ChunkData> chunks = new ArrayList<>();

        int chunkSizeChars = chunkTokenSize * 4;
        int overlapSizeChars = overlapTokenSize * 4;

        int start = 0;

        while (start < pageText.length()) {
            int end = Math.min(start + chunkSizeChars, pageText.length());

            if (end < pageText.length()) {
                int lastSpace = findLastWordBoundary(pageText, end, start);
                if (lastSpace > start + chunkSizeChars / 2) {
                    end = lastSpace;
                }
            }

            String chunkContent = pageText.substring(start, end).trim();
            if (!chunkContent.isEmpty() && chunkContent.length() > 20) {
                String chunkId = UUID.randomUUID().toString();
                ChunkData chunk = new ChunkData(
                        chunkId,
                        chunkContent,
                        pageNumber,
                        startOffset + start,
                        startOffset + end,
                        documentId
                );
                chunks.add(chunk);
            }

            int nextStart = Math.max(start + chunkSizeChars - overlapSizeChars, end - overlapSizeChars);
            if (nextStart <= start) {
                nextStart = start + Math.max(1, chunkSizeChars / 2);
            }
            start = nextStart;
        }

        return chunks;
    }

    private int findLastWordBoundary(String text, int position, int minPosition) {
        for (int i = position; i >= minPosition; i--) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                return i + 1;
            }
        }

        for (int i = position; i >= minPosition; i--) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                return i;
            }
        }

        return position;
    }
}