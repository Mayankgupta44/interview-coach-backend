package com.interviewcoach.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.request.SkillGapAnalysisRequest;
import com.interviewcoach.dto.response.SkillGapReportResponse;
import com.interviewcoach.entity.JobDescription;
import com.interviewcoach.entity.ResumeProfile;
import com.interviewcoach.entity.SkillGapReport;
import com.interviewcoach.entity.User;
import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.integration.ai.GeminiSkillGapAnalyzer;
import com.interviewcoach.repository.JobDescriptionRepository;
import com.interviewcoach.repository.ResumeProfileRepository;
import com.interviewcoach.repository.SkillGapReportRepository;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.SkillGapService;
import com.interviewcoach.util.SecurityUtils;
import com.interviewcoach.util.SkillGapPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillGapServiceImpl implements SkillGapService {

    private final UserRepository userRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final SkillGapReportRepository skillGapReportRepository;
    private final GeminiSkillGapAnalyzer geminiSkillGapAnalyzer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SkillGapReportResponse analyzeSkillGap(SkillGapAnalysisRequest request) {
        User user = getCurrentUser();

        String resumeText = resolveResumeText(user, request);
        String jobDescriptionText = resolveJobDescriptionText(user, request);

        if (resumeText.isBlank() || jobDescriptionText.isBlank()) {
            throw new BadRequestException("Resume text and job description text are required for analysis");
        }

        GeminiSkillGapAnalyzer.SkillGapAiResult aiResult = geminiSkillGapAnalyzer.analyze(
                SkillGapPromptBuilder.buildSystemInstruction(),
                SkillGapPromptBuilder.buildUserPrompt(resumeText, jobDescriptionText)
        );

        SkillGapReport report = SkillGapReport.builder()
                .user(user)
                .resumeSnapshot(resumeText)
                .jobDescriptionSnapshot(jobDescriptionText)
                .matchedSkillsJson(writeJson(aiResult.getMatchedSkills()))
                .missingSkillsJson(writeJson(aiResult.getMissingSkills()))
                .recommendedTopicsJson(writeJson(aiResult.getRecommendedTopics()))
                .summary(aiResult.getSummary())
                .fitScore(aiResult.getFitScore())
                .build();

        SkillGapReport savedReport = skillGapReportRepository.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    public SkillGapReportResponse getLatestSkillGapReport() {
        User user = getCurrentUser();

        SkillGapReport report = skillGapReportRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No skill-gap report found for current user"));

        return mapToResponse(report);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String resolveResumeText(User user, SkillGapAnalysisRequest request) {
        if (request != null && request.getResumeText() != null && !request.getResumeText().isBlank()) {
            return request.getResumeText().trim();
        }

        ResumeProfile latestResume = resumeProfileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No resume found for current user"));

        return latestResume.getResumeText().trim();
    }

    private String resolveJobDescriptionText(User user, SkillGapAnalysisRequest request) {
        if (request != null && request.getJobDescriptionText() != null && !request.getJobDescriptionText().isBlank()) {
            return request.getJobDescriptionText().trim();
        }

        JobDescription latestJd = jobDescriptionRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No job description found for current user"));

        return latestJd.getJobDescriptionText().trim();
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? Collections.emptyList() : values);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize skill-gap report JSON", ex);
        }
    }

    private List<String> readJsonArray(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize skill-gap report JSON", ex);
        }
    }

    private SkillGapReportResponse mapToResponse(SkillGapReport report) {
        return SkillGapReportResponse.builder()
                .id(report.getId())
                .fitScore(report.getFitScore())
                .matchedSkills(readJsonArray(report.getMatchedSkillsJson()))
                .missingSkills(readJsonArray(report.getMissingSkillsJson()))
                .recommendedTopics(readJsonArray(report.getRecommendedTopicsJson()))
                .summary(report.getSummary())
                .createdAt(report.getCreatedAt())
                .build();
    }
}