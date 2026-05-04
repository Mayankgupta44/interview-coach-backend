package com.interviewcoach.integration.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GroqInterviewQuestionGenerator {

    private final GroqChatClient groqChatClient;
    private final ObjectMapper objectMapper;

    public QuestionGenerationResult generateQuestions(String systemInstruction, String userPrompt) {
        try {
            String json = groqChatClient.chatJson(systemInstruction, userPrompt);
            QuestionGenerationResult result = objectMapper.readValue(json, QuestionGenerationResult.class);
            validate(result);
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Question generation failed", ex);
        }
    }

    public QuestionGenerationResult generateQuestionsOrFallback(
            String systemInstruction,
            String userPrompt,
            String targetRole,
            int questionCount
    ) {
        try {
            return generateQuestions(systemInstruction, userPrompt);
        } catch (Exception ex) {
            return new QuestionGenerationResult(buildStaticFallbackQuestions(targetRole, questionCount));
        }
    }

    private void validate(QuestionGenerationResult result) {
        if (result.getQuestions() == null || result.getQuestions().isEmpty()) {
            throw new IllegalStateException("No questions returned");
        }
    }

    private List<String> buildStaticFallbackQuestions(String targetRole, int questionCount) {
        List<String> javaQuestions = List.of(
                "Explain the difference between HashMap and ConcurrentHashMap.",
                "What is the difference between abstraction and encapsulation in Java?",
                "How does Spring Boot auto-configuration work?",
                "What is the difference between @Component, @Service, and @Repository?",
                "How would you design a REST API for user authentication?",
                "What is the difference between checked and unchecked exceptions?",
                "How does Hibernate manage entity states?",
                "What are the important features of Java 8?"
        );

        List<String> mernQuestions = List.of(
                "Explain the difference between state and props in React.",
                "What is the purpose of useEffect in React?",
                "How does JWT authentication work in a MERN application?",
                "What is the difference between SQL and MongoDB?",
                "How would you structure Express middleware in a production app?",
                "What are controlled components in React?",
                "How does React reconciliation work?",
                "How would you optimize a slow API endpoint in Node.js?"
        );

        List<String> source = targetRole != null && targetRole.toLowerCase().contains("java")
                ? javaQuestions
                : mernQuestions;

        return source.subList(0, Math.min(questionCount, source.size()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionGenerationResult {
        private List<String> questions = Collections.emptyList();
    }
}