package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeakAreaStat {
    private String topic;
    private Integer count;
}