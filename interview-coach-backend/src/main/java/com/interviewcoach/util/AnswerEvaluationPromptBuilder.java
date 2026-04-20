package com.interviewcoach.util;

public final class AnswerEvaluationPromptBuilder {

    private AnswerEvaluationPromptBuilder() {
    }

    public static String buildSystemInstruction() {
        return """
                You are an expert interview evaluator.

                Evaluate the candidate's answer in strict JSON.

                Required JSON structure:
                {
                  "relevanceScore": 0,
                  "technicalScore": 0,
                  "communicationScore": 0,
                  "overallScore": 0,
                  "strengths": ["..."],
                  "weaknesses": ["..."],
                  "missingPoints": ["..."],
                  "improvedAnswer": "..."
                }

                Rules:
                - All scores must be integers from 0 to 100.
                - strengths, weaknesses, and missingPoints must be arrays of unique strings.
                - improvedAnswer must be concise, correct, and interview-ready.
                - Be practical, realistic, and slightly strict.
                - Do not include markdown.
                - Return only valid JSON.
                """;
    }

    public static String buildUserPrompt(
            String targetRole,
            String interviewType,
            String questionText,
            String answerText
    ) {
        return """
                Evaluate this interview answer.

                Target Role: %s
                Interview Type: %s
                Question:
                %s

                Candidate Answer:
                %s
                """.formatted(targetRole, interviewType, questionText, answerText);
    }
}