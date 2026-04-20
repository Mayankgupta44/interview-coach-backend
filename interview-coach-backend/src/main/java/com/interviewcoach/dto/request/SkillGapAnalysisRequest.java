package com.interviewcoach.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillGapAnalysisRequest {
    private String resumeText;
    private String jobDescriptionText;
}