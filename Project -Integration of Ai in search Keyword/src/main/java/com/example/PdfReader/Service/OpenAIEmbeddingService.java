package com.example.PdfReader.Service;
import com.example.PdfReader.client.OpenAIRestClient;
import com.example.PdfReader.client.model.EmbeddingResponse;
import com.example.PdfReader.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenAIEmbeddingService {

    private final OpenAIRestClient openAIRestClient;

    public OpenAIEmbeddingService(OpenAIRestClient openAIRestClient) {
        this.openAIRestClient = openAIRestClient;
    }

    public List<Double> generateEmbedding(String text) {
        if (Strings.isBlank(text) || text.trim().isEmpty()) {
            throw new ApiException("Text cannot be empty for embedding generation", HttpStatus.BAD_REQUEST);
        }
        try {
            EmbeddingResponse response = openAIRestClient.generateEmbedding(text);

            if (response.getData() == null || response.getData().isEmpty()) {
                throw new ApiException("No embedding data received from OpenAI", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<Double> embedding = response.getData().get(0).getEmbedding();
            return embedding;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[OpenAIEmbeddingService] :: generateEmbedding :: Unexpected error", e);
            throw new ApiException("Failed to generate embedding: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<List<Double>> generateBatchEmbeddings(List<String> texts) {
        if (texts == null || texts.isEmpty()) {

            return new ArrayList<>();
        }
        List<List<Double>> allEmbeddings = new ArrayList<>();

        for (int index = 0; index < texts.size(); index++) {
            String text = texts.get(index);

            if (text == null || text.trim().isEmpty()) {
                 continue;
            }

            try {
                List<Double> embedding = generateEmbedding(text);
                allEmbeddings.add(embedding);

                if (index < texts.size() - 1 && texts.size() > 10) {
                    Thread.sleep(100);
                }

            } catch (Exception e) {
               throw new ApiException("Failed to generate batch embeddings at index " + index + ": " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return allEmbeddings;
    }
}