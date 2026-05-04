package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnswerAttemptResponse {
    private Long id;
    private Long questionId;
    private Integer attemptNumber;
    private Boolean isImproved;
    private String answerText;
    private LocalDateTime createdAt;
    private AttemptEvaluationResponse evaluation;
    private String answerMode;
    private String audioUrl;
    private String transcriptText;
}