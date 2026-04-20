package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.GeminiProperties;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiAnswerEvaluator {

    private final RestTemplate restTemplate;
    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;

    public AnswerEvaluationResult evaluate(String systemInstruction, String userPrompt) {
        try {
            String responseText = callGemini(systemInstruction, userPrompt);
            AnswerEvaluationResult result = objectMapper.readValue(responseText, AnswerEvaluationResult.class);
            validate(result);
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Gemini answer evaluation failed: " + ex.getMessage(), ex);
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
                                        "relevanceScore", Map.of(
                                                "type", "integer",
                                                "minimum", 0,
                                                "maximum", 100
                                        ),
                                        "technicalScore", Map.of(
                                                "type", "integer",
                                                "minimum", 0,
                                                "maximum", 100
                                        ),
                                        "communicationScore", Map.of(
                                                "type", "integer",
                                                "minimum", 0,
                                                "maximum", 100
                                        ),
                                        "overallScore", Map.of(
                                                "type", "integer",
                                                "minimum", 0,
                                                "maximum", 100
                                        ),
                                        "strengths", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "weaknesses", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "missingPoints", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string")
                                        ),
                                        "improvedAnswer", Map.of(
                                                "type", "string"
                                        )
                                ),
                                "required", List.of(
                                        "relevanceScore",
                                        "technicalScore",
                                        "communicationScore",
                                        "overallScore",
                                        "strengths",
                                        "weaknesses",
                                        "missingPoints",
                                        "improvedAnswer"
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

            JsonNode textNode = parts.get(0).path("text");

            if (textNode.isMissingNode() || textNode.isNull()) {
                throw new IllegalStateException("Gemini response does not contain text output");
            }

            return textNode.asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse Gemini response: " + ex.getMessage(), ex);
        }
    }

    private void validate(AnswerEvaluationResult result) {
        validateScore(result.getRelevanceScore(), "relevanceScore");
        validateScore(result.getTechnicalScore(), "technicalScore");
        validateScore(result.getCommunicationScore(), "communicationScore");
        validateScore(result.getOverallScore(), "overallScore");

        if (result.getStrengths() == null
                || result.getWeaknesses() == null
                || result.getMissingPoints() == null
                || result.getImprovedAnswer() == null
                || result.getImprovedAnswer().isBlank()) {
            throw new IllegalStateException("Incomplete Gemini evaluation response");
        }
    }

    private void validateScore(Integer score, String fieldName) {
        if (score == null || score < 0 || score > 100) {
            throw new IllegalStateException("Invalid " + fieldName + " from Gemini");
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerEvaluationResult {
        private Integer relevanceScore;
        private Integer technicalScore;
        private Integer communicationScore;
        private Integer overallScore;
        private List<String> strengths;
        private List<String> weaknesses;
        private List<String> missingPoints;
        private String improvedAnswer;
    }
}