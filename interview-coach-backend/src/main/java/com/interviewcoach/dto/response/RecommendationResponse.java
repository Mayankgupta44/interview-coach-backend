package com.interviewcoach.dto.response;

import com.interviewcoach.enums.RecommendationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecommendationResponse {
    private Long id;
    private RecommendationType type;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}