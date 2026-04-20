package com.interviewcoach.service;

import com.interviewcoach.dto.request.CreateInterviewSessionRequest;
import com.interviewcoach.dto.response.InterviewSessionResponse;

import java.util.List;

public interface InterviewService {
    InterviewSessionResponse createSession(CreateInterviewSessionRequest request);
    InterviewSessionResponse getSessionById(Long sessionId);
    List<InterviewSessionResponse> getMySessions();
}