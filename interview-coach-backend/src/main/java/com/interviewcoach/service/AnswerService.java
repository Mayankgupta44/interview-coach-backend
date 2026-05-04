package com.interviewcoach.service;

import com.interviewcoach.dto.request.SubmitAnswerRequest;
import com.interviewcoach.dto.response.AnswerEvaluationResponse;
import com.interviewcoach.dto.response.InterviewAnswerResponse;

import java.util.List;

public interface AnswerService {
    InterviewAnswerResponse submitAnswer(Long sessionId, Long questionId, SubmitAnswerRequest request);
    InterviewAnswerResponse getAnswerByQuestionId(Long sessionId, Long questionId);
    List<InterviewAnswerResponse> getAnswersBySessionId(Long sessionId);
    AnswerEvaluationResponse getEvaluationByAnswerId(Long answerId);
    InterviewAnswerResponse submitAudioAnswer(Long sessionId, Long questionId, org.springframework.web.multipart.MultipartFile audioFile);
    InterviewAnswerResponse submitAudioTranscriptAnswer(
            Long sessionId,
            Long questionId,
            com.interviewcoach.dto.request.SubmitAudioTranscriptRequest request
    );
}