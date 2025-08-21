package com.example.PdfReader.modelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChunkData {
    private String chunkId;
    private String content;
    private int pageNumber;
    private int startPosition;
    private int endPosition;
    private List<Double> embedding;
    private String documentId;
    public ChunkData(String chunkId, String content, int pageNumber, int startPosition, int endPosition, String documentId) {
        this.chunkId = chunkId;
        this.content = content;
        this.pageNumber = pageNumber;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.documentId = documentId;
        this.embedding = null;
    }
}
