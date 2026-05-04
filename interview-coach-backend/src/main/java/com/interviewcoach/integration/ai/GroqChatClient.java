package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroqChatClient {

    private final RestTemplate restTemplate;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    public String chatJson(String systemPrompt, String userPrompt) {
        return chatJsonWithFallback(
                aiProperties.getModel(),
                aiProperties.getFallbackModel(),
                systemPrompt,
                userPrompt
        );
    }

    public String chatJsonWithFallback(
            String primaryModel,
            String fallbackModel,
            String systemPrompt,
            String userPrompt
    ) {
        Exception lastException = null;

        for (String model : List.of(primaryModel, fallbackModel)) {
            if (model == null || model.isBlank()) {
                continue;
            }

            int attempts = Math.max(1, aiProperties.getMaxRetries() + 1);

            for (int attempt = 1; attempt <= attempts; attempt++) {
                try {
                    return callGroq(model, systemPrompt, userPrompt);
                } catch (HttpStatusCodeException ex) {
                    lastException = ex;

                    // Retry only on transient/server/rate-limit errors
                    int status = ex.getStatusCode().value();
                    boolean retryable = status == 429 || status == 500 || status == 502 || status == 503 || status == 504;

                    if (!retryable || attempt == attempts) {
                        break;
                    }

                    sleepBackoff(attempt);
                } catch (Exception ex) {
                    lastException = ex;
                    if (attempt == attempts) {
                        break;
                    }
                    sleepBackoff(attempt);
                }
            }
        }

        throw new IllegalStateException("Groq API call failed after retries and fallback", lastException);
    }

    private String callGroq(String model, String systemPrompt, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.3,
                "response_format", Map.of("type", "json_object")
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                aiProperties.getBaseUrl() + "/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        return extractAssistantContent(response.getBody());
    }

    private String extractAssistantContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                throw new IllegalStateException("No choices returned by Groq");
            }

            JsonNode contentNode = choices.get(0).path("message").path("content");
            if (contentNode.isMissingNode() || contentNode.isNull()) {
                throw new IllegalStateException("Groq response does not contain assistant content");
            }

            return contentNode.asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse Groq response", ex);
        }
    }

    private void sleepBackoff(int attempt) {
        try {
            long baseDelay = Math.max(200, aiProperties.getRetryDelayMs());
            long delay = baseDelay * attempt;
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", ie);
        }
    }
}