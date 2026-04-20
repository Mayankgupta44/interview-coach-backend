package com.interviewcoach.service;

import com.interviewcoach.dto.request.SkillGapAnalysisRequest;
import com.interviewcoach.dto.response.SkillGapReportResponse;

public interface SkillGapService {
    SkillGapReportResponse analyzeSkillGap(SkillGapAnalysisRequest request);
    SkillGapReportResponse getLatestSkillGapReport();
}