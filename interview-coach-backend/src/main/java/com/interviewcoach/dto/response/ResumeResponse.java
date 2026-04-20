package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResumeResponse {
    private Long id;
    private Long userId;
    private String resumeText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}