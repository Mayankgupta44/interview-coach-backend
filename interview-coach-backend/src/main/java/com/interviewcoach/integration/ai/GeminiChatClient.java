package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.GeminiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiChatClient {

    private final RestTemplate restTemplate;
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    public String getStructuredJsonResponse(String systemInstruction, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "%s/models/%s:generateContent?key=%s".formatted(
                geminiProperties.getBaseUrl(),
                geminiProperties.getModel(),
                geminiProperties.getApiKey()
        );
        System.out.println("apiKey = " + geminiProperties.getApiKey());
        System.out.println("model = " + geminiProperties.getModel());
        System.out.println("baseUrl = " + geminiProperties.getBaseUrl());
        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(
                                Map.of("text", systemInstruction)
                        )
                ),
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", userPrompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseJsonSchema", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.of(
                                        "fitScore", Map.of(
                                                "type", "integer",
                                                "minimum", 0,
                                                "maximum", 100
                                        ),
                                        "matchedSkills", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "missingSkills", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "recommendedTopics", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "summary", Map.of(
                                                "type", "string"
                                        )
                                ),
                                "required", List.of(
                                        "fitScore",
                                        "matchedSkills",
                                        "missingSkills",
                                        "recommendedTopics",
                                        "summary"
                                )
                        ),
                        "temperature", 0.3
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return extractText(response.getBody());
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new IllegalStateException("No candidates returned by Gemini");
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                throw new IllegalStateException("No content parts returned by Gemini");
            }

            JsonNode firstPart = parts.get(0);
            JsonNode textNode = firstPart.path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                throw new IllegalStateException("Gemini response does not contain text output");
            }

            return textNode.asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse Gemini response: " + ex.getMessage(), ex);
        }
    }
}