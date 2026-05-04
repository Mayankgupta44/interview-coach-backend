package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttemptComparisonResponse {
    private AnswerAttemptResponse previousAttempt;
    private AnswerAttemptResponse currentAttempt;
    private Integer overallScoreDifference;
    private Integer technicalScoreDifference;
    private Integer relevanceScoreDifference;
    private Integer communicationScoreDifference;
}