package com.interviewcoach.dto.response;

import com.interviewcoach.enums.InterviewType;
import com.interviewcoach.enums.SessionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import com.interviewcoach.enums.DifficultyLevel;

@Getter
@Builder
public class InterviewSessionResponse {
    private Long id;
    private String targetRole;
    private InterviewType interviewType;
    private DifficultyLevel difficultyLevel;
    private String questionStyle;
    private SessionStatus status;
    private Integer totalQuestions;
    private LocalDateTime createdAt;
    private List<InterviewQuestionResponse> questions;
}