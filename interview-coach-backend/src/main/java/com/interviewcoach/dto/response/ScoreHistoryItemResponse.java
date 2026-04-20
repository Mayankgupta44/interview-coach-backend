package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScoreHistoryItemResponse {
    private Long sessionId;
    private String targetRole;
    private Integer averageScore;
    private Integer totalQuestions;
    private LocalDateTime createdAt;
}