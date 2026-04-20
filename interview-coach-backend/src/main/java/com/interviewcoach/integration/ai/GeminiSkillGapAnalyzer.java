package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GeminiSkillGapAnalyzer {

    private final GeminiChatClient geminiChatClient;
    private final ObjectMapper objectMapper;

    public SkillGapAiResult analyze(String systemInstruction, String userPrompt) {
        try {
            String json = geminiChatClient.getStructuredJsonResponse(systemInstruction, userPrompt);
            SkillGapAiResult result = objectMapper.readValue(json, SkillGapAiResult.class);
            validate(result);
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Gemini skill-gap analysis failed: " + ex.getMessage(), ex);
        }
    }

    private void validate(SkillGapAiResult result) {
        if (result.getFitScore() == null || result.getFitScore() < 0 || result.getFitScore() > 100) {
            throw new IllegalStateException("Invalid fit score from Gemini");
        }
        if (result.getMatchedSkills() == null
                || result.getMissingSkills() == null
                || result.getRecommendedTopics() == null
                || result.getSummary() == null
                || result.getSummary().isBlank()) {
            throw new IllegalStateException("Incomplete Gemini response");
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillGapAiResult {
        private Integer fitScore;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private List<String> recommendedTopics;
        private String summary;
    }
}