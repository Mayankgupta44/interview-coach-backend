package com.interviewcoach.util;

import com.interviewcoach.enums.DifficultyLevel;
import com.interviewcoach.enums.InterviewType;

import java.util.Set;

public final class InterviewPromptBuilder {

    private InterviewPromptBuilder() {
    }

    public static String buildSystemInstruction() {
        return """
                You are an expert technical interviewer.

                Generate high-quality interview questions in strict JSON.

                Required JSON structure:
                {
                  "questions": [
                    "Question 1",
                    "Question 2"
                  ]
                }

                Strict Rules:
                - Return only valid JSON.
                - Do not include markdown.
                - Do not include numbering like 1., 2., 3.
                - Each item must be exactly ONE focused question.
                - Do NOT combine multiple concepts in one question.
                - Do NOT ask multiple questions using "and", "also", or "then".
                - Questions must match the selected difficulty level.
                - Avoid advanced system design or deep internals for FRESHER level.
                - Questions must be realistic and interview-ready.
                - Avoid duplicates.
                """;
    }

    public static String buildUserPrompt(
            String targetRole,
            InterviewType interviewType,
            DifficultyLevel difficultyLevel,
            String questionStyle,
            Set<String> skills,
            String resumeText,
            String jobDescriptionText,
            int questionCount
    ) {
        return """
                Generate %d interview questions.

                Target Role: %s
                Interview Type: %s
                Difficulty Level: %s
                Question Style: %s
                Candidate Skills: %s

                Difficulty Guidelines:
                - FRESHER: Ask fundamentals, definitions, basic syntax, simple OOP/DB/API concepts.
                - JUNIOR: Ask practical implementation, common framework usage, debugging, simple scenarios.
                - INTERMEDIATE: Ask deeper design decisions, performance, trade-offs, real project scenarios.
                - ADVANCED: Ask architecture, scaling, concurrency, internals, optimization, and complex trade-offs.

                Mandatory Question Rules:
                - Every question must be single-focused.
                - Do not combine two or more questions into one.
                - Do not ask "Explain X and also Y".
                - Do not ask advanced questions if level is FRESHER.
                - Keep question wording clear and concise.

                Resume Context:
                %s

                Job Description Context:
                %s
                """.formatted(
                questionCount,
                targetRole,
                interviewType,
                difficultyLevel,
                questionStyle == null || questionStyle.isBlank() ? "SINGLE_FOCUSED" : questionStyle,
                skills == null || skills.isEmpty() ? "Not provided" : String.join(", ", skills),
                resumeText == null || resumeText.isBlank() ? "Not provided" : resumeText,
                jobDescriptionText == null || jobDescriptionText.isBlank() ? "Not provided" : jobDescriptionText
        );
    }
}