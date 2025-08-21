package com.example.PdfReader.modelDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemanticMatch extends Match {
    private double cosineSimilarity;
    private String chunkId;
    private String searchType;

    public SemanticMatch(int pageNumber, String snippet, double score, double cosineSimilarity, String chunkId, String searchType) {
        super(pageNumber, snippet, score);
        this.cosineSimilarity = cosineSimilarity;
        this.chunkId = chunkId;
        this.searchType = searchType;
    }
}