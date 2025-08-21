package com.example.PdfReader.modelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor


public class UploadResponse {
    private UUID fileId;

    private String filename;

    private int totalPages;

}