package com.interviewcoach.service;

import com.interviewcoach.dto.request.SubmitAnswerAttemptRequest;
import com.interviewcoach.dto.response.AnswerAttemptResponse;
import com.interviewcoach.dto.response.AttemptComparisonResponse;

import java.util.List;
import com.interviewcoach.dto.request.SubmitAudioTranscriptRequest;

public interface AnswerAttemptService {

    AnswerAttemptResponse submitAttempt(Long sessionId, Long questionId, SubmitAnswerAttemptRequest request);

    List<AnswerAttemptResponse> getAttemptsByQuestionId(Long sessionId, Long questionId);

    AttemptComparisonResponse getLatestAttemptComparison(Long sessionId, Long questionId);

    AnswerAttemptResponse submitAudioAttempt(Long sessionId, Long questionId, org.springframework.web.multipart.MultipartFile audioFile);

    AnswerAttemptResponse submitAudioTranscriptAttempt(
            Long sessionId,
            Long questionId,
            SubmitAudioTranscriptRequest request
    );
}