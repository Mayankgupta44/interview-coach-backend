package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroqAnswerEvaluator {

    private final GroqChatClient groqChatClient;
    private final ObjectMapper objectMapper;

    public AnswerEvaluationResult evaluate(String systemInstruction, String userPrompt) {
        try {
            String json = groqChatClient.chatJson(systemInstruction, userPrompt);
            AnswerEvaluationResult result = objectMapper.readValue(json, AnswerEvaluationResult.class);

            result.setRelevanceScore(normalize(result.getRelevanceScore()));
            result.setTechnicalScore(normalize(result.getTechnicalScore()));
            result.setCommunicationScore(normalize(result.getCommunicationScore()));
            result.setOverallScore(normalize(result.getOverallScore()));

            applyConsistencyRules(result);

            validate(result);
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Answer evaluation failed", ex);
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
            throw new IllegalStateException("Incomplete evaluation JSON");
        }
    }

    private void validateScore(Integer value, String name) {
        if (value == null) throw new IllegalStateException(name + " missing");

        if (value < 0 || value > 100) {
            throw new IllegalStateException(name + " out of range");
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
        private String depthLevel;
        private Boolean isShallow;
        private List<String> missingKeywords;
        private List<String> strengths;
        private List<String> weaknesses;
        private List<String> missingPoints;
        private String improvedAnswer;
    }
    private int normalize(int value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }

    private void applyConsistencyRules(AnswerEvaluationResult result) {
        if ("LOW".equalsIgnoreCase(result.getDepthLevel()) && result.getOverallScore() > 70) {
            result.setOverallScore(70);
        }

        if ("LOW".equalsIgnoreCase(result.getDepthLevel()) && result.getTechnicalScore() > 75) {
            result.setTechnicalScore(75);
        }

        if (Boolean.TRUE.equals(result.getIsShallow()) && result.getOverallScore() > 65) {
            result.setOverallScore(65);
        }
    }
}