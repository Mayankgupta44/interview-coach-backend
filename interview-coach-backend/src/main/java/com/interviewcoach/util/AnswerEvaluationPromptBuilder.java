package com.interviewcoach.util;

public final class AnswerEvaluationPromptBuilder {

    private AnswerEvaluationPromptBuilder() {
    }

    public static String buildSystemInstruction() {
        return """
            You are a strict technical interviewer.
    
            Evaluate the user's answer and return ONLY valid JSON in this format:
    
            {
              "relevanceScore": 0,
              "technicalScore": 0,
              "communicationScore": 0,
              "overallScore": 0,
              "depthLevel": "LOW | MEDIUM | HIGH",
              "isShallow": true,
              "missingKeywords": ["..."],
              "strengths": ["..."],
              "weaknesses": ["..."],
              "missingPoints": ["..."],
              "improvedAnswer": "..."
            }
    
            Evaluation Rules:
            - Scores must be 0–100 integers
            - Be strict (real interview level)
            - depthLevel:
                LOW → superficial / incomplete
                MEDIUM → partial understanding
                HIGH → strong understanding
    
            - isShallow = true if:
                * answer is vague
                * lacks technical detail
                * too short
    
            - missingKeywords:
                * important technical terms NOT mentioned
    
            - improvedAnswer:
                * must be ideal interview answer
                * structured + concise + technical
    
            - No markdown
            - No explanations outside JSON
        """;
    }

    public static String buildUserPrompt(
            String role,
            String type,
            String question,
            String answer
    ) {
        return """
            Role: %s
            Interview Type: %s
    
            Question:
            %s
    
            User Answer:
            %s
    
            Evaluate this answer strictly like a real interviewer.
        """.formatted(role, type, question, answer);
    }

    public static String buildUserPrompt(
            String role,
            String type,
            String question,
            String answer,
            String answerMode
    ) {
        String modeInstruction = "AUDIO".equalsIgnoreCase(answerMode)
                ? """
              This answer was transcribed from user's recorded speech.
              Evaluate technical correctness and communication clarity.
              Ignore minor speech-to-text transcription mistakes.
              Do not penalize small grammar errors caused by transcription.
              Still penalize vague, shallow, incomplete, or technically incorrect answers.
              """
                : """
              This answer was typed by the user.
              Evaluate normally based on technical correctness, relevance, and communication.
              """;

        return """
            Role: %s
            Interview Type: %s
            Answer Mode: %s

            Special Instruction:
            %s

            Question:
            %s

            User Answer:
            %s

            Evaluate this answer strictly like a real interviewer.
            """.formatted(role, type, answerMode, modeInstruction, question, answer);
    }
}