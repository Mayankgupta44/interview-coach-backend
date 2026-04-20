package com.interviewcoach.util;

public final class SkillGapPromptBuilder {

    private SkillGapPromptBuilder() {
    }

    public static String buildUserPrompt(String resumeText, String jobDescriptionText) {
        return """
                Analyze this candidate resume against this job description.

                Return only valid JSON.

                Resume:
                %s

                Job Description:
                %s
                """.formatted(resumeText, jobDescriptionText);
    }

    public static String buildSystemInstruction() {
        return """
                You are an expert interview preparation and hiring analysis assistant.

                Compare the candidate resume with the given job description and return a strict JSON object.

                Required JSON structure:
                {
                  "fitScore": 0,
                  "matchedSkills": ["..."],
                  "missingSkills": ["..."],
                  "recommendedTopics": ["..."],
                  "summary": "..."
                }

                Rules:
                - fitScore must be an integer from 0 to 100.
                - matchedSkills, missingSkills, recommendedTopics must be arrays of unique strings.
                - summary must be 3 to 6 concise sentences.
                - Be practical, interview-focused, and realistic.
                - Do not include markdown.
                - Do not return anything outside the JSON object.
                """;
    }
}