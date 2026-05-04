package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreTrendPoint {
    private String date;
    private Integer averageScore;
}