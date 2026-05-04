package com.interviewcoach.util;

public final class SkillGapPromptBuilder {

    private SkillGapPromptBuilder() {
    }

    public static String buildSystemInstruction() {
        return """
                You are an expert interview preparation assistant.

                Return ONLY valid JSON in this exact format:
                {
                  "fitScore": 0,
                  "matchedSkills": ["..."],
                  "missingSkills": ["..."],
                  "recommendedTopics": ["..."],
                  "summary": "..."
                }

                Rules:
                - fitScore must be an integer from 0 to 100
                - matchedSkills, missingSkills, recommendedTopics must be arrays of strings
                - summary must be concise and practical
                - no markdown
                - no extra text outside JSON
                """;
    }

    public static String buildUserPrompt(String resumeText, String jobDescriptionText) {
        return """
                Compare this resume with this job description and identify the skill gap.

                Resume:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescriptionText);
    }
}