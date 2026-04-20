package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InterviewQuestionResponse {
    private Long id;
    private Integer questionOrder;
    private String questionText;
}