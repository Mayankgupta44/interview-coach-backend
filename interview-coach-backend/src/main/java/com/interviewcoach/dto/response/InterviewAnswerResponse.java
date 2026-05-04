package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewAnswerResponse {
    private Long id;
    private Long questionId;
    private Integer questionOrder;
    private String questionText;
    private String answerText;
    private LocalDateTime createdAt;
    private AnswerEvaluationResponse evaluation;
    private String answerMode;
    private String audioUrl;
    private String transcriptText;
}