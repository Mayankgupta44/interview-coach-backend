package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Component
@RequiredArgsConstructor
public class GroqTranscriptionClient {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    public String transcribe(String filePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(aiProperties.getApiKey());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(new File(filePath)));
            body.add("model", aiProperties.getTranscriptionModel());
            body.add("response_format", "json");

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    aiProperties.getBaseUrl() + "/audio/transcriptions",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return extractText(response.getBody());
        } catch (Exception ex) {
            throw new IllegalStateException("Audio transcription failed", ex);
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("text").asText();

            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Empty transcript returned");
            }

            return text.trim();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse transcription response", ex);
        }
    }
}