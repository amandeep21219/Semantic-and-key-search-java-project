package com.example.PdfReader.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse {
    private String object;
    private List<EmbeddingData> data;
    private String model;
    private Usage usage;
    private ErrorInfo error;
}
