package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResumeUploadResponse {
    private Long id;
    private String originalFileName;
    private Long fileSize;
    private String contentType;
    private String extractedText;
    private Boolean extractionSuccess;
    private String extractionError;
    private LocalDateTime createdAt;
}