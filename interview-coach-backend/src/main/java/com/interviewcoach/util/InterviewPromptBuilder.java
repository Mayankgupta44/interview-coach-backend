package com.interviewcoach.util;

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

                Rules:
                - Return only valid JSON.
                - Do not include markdown.
                - Do not include numbering like 1., 2., 3.
                - Questions must be realistic and interview-ready.
                - Questions must match the candidate role, skills, and interview type.
                - Keep questions concise but meaningful.
                - Avoid duplicates.
                """;
    }

    public static String buildUserPrompt(
            String targetRole,
            InterviewType interviewType,
            Set<String> skills,
            String resumeText,
            String jobDescriptionText,
            int questionCount
    ) {
        return """
                Generate %d interview questions for this candidate.

                Target Role: %s
                Interview Type: %s
                Skills: %s

                Resume Context:
                %s

                Job Description Context:
                %s
                """.formatted(
                questionCount,
                targetRole,
                interviewType,
                skills == null || skills.isEmpty() ? "Not provided" : String.join(", ", skills),
                resumeText == null || resumeText.isBlank() ? "Not provided" : resumeText,
                jobDescriptionText == null || jobDescriptionText.isBlank() ? "Not provided" : jobDescriptionText
        );
    }
}