package com.example.PdfReader.modelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor

public class SearchResponse {

    private UUID fileId;

    private String filename;

    private String query;

    private List<Match> matches;

    private int totalMatches;

}