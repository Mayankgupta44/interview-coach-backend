package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer totalAnswersSubmitted;
    private Integer averageOverallScore;
    private List<String> weakAreas;
    private List<ScoreHistoryItemResponse> recentScoreHistory;
    private List<RecommendationResponse> recommendations;
}