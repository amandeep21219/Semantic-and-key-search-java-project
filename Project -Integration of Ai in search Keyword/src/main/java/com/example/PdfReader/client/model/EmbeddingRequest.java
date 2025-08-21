package com.example.PdfReader.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {
    private String model;
    private String input;
    private String encoding_format;
}