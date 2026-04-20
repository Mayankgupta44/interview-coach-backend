package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.GeminiProperties;
import lombok.RequiredArgsConstructor;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiInterviewQuestionGenerator {

    private final RestTemplate restTemplate;
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    public QuestionGenerationResult generateQuestions(String systemInstruction, String userPrompt) {
        try {
            String responseText = callGemini(systemInstruction, userPrompt);
            QuestionGenerationResult result = objectMapper.readValue(responseText, QuestionGenerationResult.class);
            validate(result);
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Gemini question generation failed: " + ex.getMessage(), ex);
        }
    }

    private String callGemini(String systemInstruction, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiProperties.getApiKey());

        String url = "%s/models/%s:generateContent".formatted(
                geminiProperties.getBaseUrl(),
                geminiProperties.getModel()
        );

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
                                        "questions", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        )
                                ),
                                "required", List.of("questions")
                        ),
                        "temperature", 0.5
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

            JsonNode textNode = parts.get(0).path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                throw new IllegalStateException("Gemini response does not contain text output");
            }

            return textNode.asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse Gemini response: " + ex.getMessage(), ex);
        }
    }

    private void validate(QuestionGenerationResult result) {
        if (result.getQuestions() == null || result.getQuestions().isEmpty()) {
            throw new IllegalStateException("Gemini returned no questions");
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionGenerationResult {
        private List<String> questions;
    }
}