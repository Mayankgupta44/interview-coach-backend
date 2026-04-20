package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SkillGapReportResponse {
    private Long id;
    private Integer fitScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> recommendedTopics;
    private String summary;
    private LocalDateTime createdAt;
}