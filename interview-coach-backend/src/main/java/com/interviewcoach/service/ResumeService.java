package com.interviewcoach.service;

import com.interviewcoach.dto.request.JobDescriptionRequest;
import com.interviewcoach.dto.request.ResumeRequest;
import com.interviewcoach.dto.response.JobDescriptionResponse;
import com.interviewcoach.dto.response.ResumeResponse;

public interface ResumeService {
    ResumeResponse saveResume(ResumeRequest request);
    ResumeResponse getLatestResume();

    JobDescriptionResponse saveJobDescription(JobDescriptionRequest request);
    JobDescriptionResponse getLatestJobDescription();
}