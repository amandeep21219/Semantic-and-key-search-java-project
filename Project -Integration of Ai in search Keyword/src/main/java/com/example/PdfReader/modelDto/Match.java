package com.example.PdfReader.modelDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Match {
    private int pageNumber;

    private String snippet;

    private double score;

}