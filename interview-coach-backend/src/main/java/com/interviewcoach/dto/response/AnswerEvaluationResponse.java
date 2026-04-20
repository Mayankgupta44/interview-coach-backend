package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AnswerEvaluationResponse {
    private Long id;
    private Integer relevanceScore;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer overallScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingPoints;
    private String improvedAnswer;
    private LocalDateTime createdAt;
}