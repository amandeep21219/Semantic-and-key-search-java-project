package com.example.PdfReader.client;


import com.example.PdfReader.client.model.EmbeddingRequest;
import com.example.PdfReader.client.model.EmbeddingResponse;
import com.example.PdfReader.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class OpenAIRestClient {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String baseUrl;

    @Value("${openai.embedding.model}")
    private String embeddingModel;

    private final RestTemplate restTemplate;

    public OpenAIRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EmbeddingResponse generateEmbedding(String text) {
        EmbeddingRequest request = new EmbeddingRequest(embeddingModel, text, "float");
        return callEmbeddingAPI(request);
    }

    private EmbeddingResponse callEmbeddingAPI(EmbeddingRequest request) {
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<EmbeddingRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<EmbeddingResponse> response = restTemplate.exchange(baseUrl + "/embeddings", HttpMethod.POST, entity, EmbeddingResponse.class
            );

            EmbeddingResponse embeddingResponse = response.getBody();

            if (embeddingResponse != null && embeddingResponse.getError() != null) {
                String errorMessage = embeddingResponse.getError().getMessage();
                throw new ApiException("OpenAI API error: " + errorMessage, HttpStatus.BAD_REQUEST);
            }
            return embeddingResponse;

        } catch (RestClientException e) {
            log.error("[OpenAIRestClient] :: callEmbeddingAPI :: Network error calling OpenAI API", e);
            throw new ApiException("Failed to connect to OpenAI API: " + e.getMessage(),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("[OpenAIRestClient] :: callEmbeddingAPI :: Unexpected error", e);
            throw new ApiException("Failed to generate embedding: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ FIXED: Proper Authorization header format
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            headers.set("Authorization", "Bearer " + apiKey);
            log.debug("[OpenAIRestClient] :: createHeaders :: Authorization header set");
        } else {
            log.error("[OpenAIRestClient] :: createHeaders :: API Key is null or empty!");
            throw new ApiException("OpenAI API key is not configured", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return headers;
    }
}
