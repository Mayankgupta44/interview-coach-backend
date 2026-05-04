package com.interviewcoach.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.request.SubmitAnswerAttemptRequest;
import com.interviewcoach.dto.response.AnswerAttemptResponse;
import com.interviewcoach.dto.response.AttemptComparisonResponse;
import com.interviewcoach.dto.response.AttemptEvaluationResponse;
import com.interviewcoach.entity.*;
import com.interviewcoach.enums.SessionStatus;
import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.integration.ai.GroqAnswerEvaluator;
import com.interviewcoach.integration.ai.GroqTranscriptionClient;
import com.interviewcoach.repository.*;
import com.interviewcoach.service.AnswerAttemptService;
import com.interviewcoach.service.AudioStorageService;
import com.interviewcoach.util.AnswerEvaluationPromptBuilder;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.interviewcoach.enums.AnswerMode;
import org.springframework.web.multipart.MultipartFile;
import com.interviewcoach.dto.request.SubmitAudioTranscriptRequest;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerAttemptServiceImpl implements AnswerAttemptService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final AnswerAttemptRepository answerAttemptRepository;
    private final AttemptEvaluationRepository attemptEvaluationRepository;
    private final GroqAnswerEvaluator groqAnswerEvaluator;
    private final ObjectMapper objectMapper;
    private final AudioStorageService audioStorageService;
    private final GroqTranscriptionClient groqTranscriptionClient;

    @Override
    @Transactional
    public AnswerAttemptResponse submitAttempt(Long sessionId, Long questionId, SubmitAnswerAttemptRequest request) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        AnswerAttempt previousAttempt = answerAttemptRepository
                .findTopByQuestionIdAndUserIdOrderByAttemptNumberDesc(questionId, user.getId())
                .orElse(null);

        int nextAttemptNumber = previousAttempt == null ? 1 : previousAttempt.getAttemptNumber() + 1;
        boolean isImproved = nextAttemptNumber > 1;

        AnswerAttempt attempt = AnswerAttempt.builder()
                .question(question)
                .user(user)
                .attemptNumber(nextAttemptNumber)
                .answerText(request.getAnswerText().trim())
                .answerMode(AnswerMode.TEXT)
                .transcriptText(null)
                .audioUrl(null)
                .isImproved(isImproved)
                .build();

        AnswerAttempt savedAttempt = answerAttemptRepository.save(attempt);

        GroqAnswerEvaluator.AnswerEvaluationResult aiResult = groqAnswerEvaluator.evaluate(
                AnswerEvaluationPromptBuilder.buildSystemInstruction(),
                AnswerEvaluationPromptBuilder.buildUserPrompt(
                        session.getTargetRole(),
                        session.getInterviewType().name(),
                        question.getQuestionText(),
                        savedAttempt.getAnswerText()
                )
        );

        AttemptEvaluation savedEvaluation = attemptEvaluationRepository.save(
                AttemptEvaluation.builder()
                        .attempt(savedAttempt)
                        .relevanceScore(aiResult.getRelevanceScore())
                        .technicalScore(aiResult.getTechnicalScore())
                        .communicationScore(aiResult.getCommunicationScore())
                        .overallScore(aiResult.getOverallScore())
                        .missingKeywordsJson(writeJson(aiResult.getMissingKeywords()))
                        .depthLevel(aiResult.getDepthLevel())
                        .isShallow(aiResult.getIsShallow())
                        .strengthsJson(writeJson(aiResult.getStrengths()))
                        .weaknessesJson(writeJson(aiResult.getWeaknesses()))
                        .missingPointsJson(writeJson(aiResult.getMissingPoints()))
                        .improvedAnswer(aiResult.getImprovedAnswer())
                        .build()
        );

        updateSessionStatusIfCompleted(session, user);

        return mapToAttemptResponse(savedAttempt, savedEvaluation);
    }

    @Override
    public List<AnswerAttemptResponse> getAttemptsByQuestionId(Long sessionId, Long questionId) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        return answerAttemptRepository.findByQuestionIdAndUserIdOrderByAttemptNumberAsc(questionId, user.getId())
                .stream()
                .map(attempt -> {
                    AttemptEvaluation evaluation = attemptEvaluationRepository.findByAttemptId(attempt.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found for attempt: " + attempt.getId()));
                    return mapToAttemptResponse(attempt, evaluation);
                })
                .toList();
    }

    @Override
    public AttemptComparisonResponse getLatestAttemptComparison(Long sessionId, Long questionId) {
        List<AnswerAttemptResponse> attempts = getAttemptsByQuestionId(sessionId, questionId);

        if (attempts.isEmpty()) {
            throw new ResourceNotFoundException("No attempts found for this question");
        }

        if (attempts.size() == 1) {
            return AttemptComparisonResponse.builder()
                    .previousAttempt(null)
                    .currentAttempt(attempts.get(0))
                    .overallScoreDifference(null)
                    .technicalScoreDifference(null)
                    .relevanceScoreDifference(null)
                    .communicationScoreDifference(null)
                    .build();
        }

        AnswerAttemptResponse previous = attempts.get(attempts.size() - 2);
        AnswerAttemptResponse current = attempts.get(attempts.size() - 1);

        return AttemptComparisonResponse.builder()
                .previousAttempt(previous)
                .currentAttempt(current)
                .overallScoreDifference(current.getEvaluation().getOverallScore() - previous.getEvaluation().getOverallScore())
                .technicalScoreDifference(current.getEvaluation().getTechnicalScore() - previous.getEvaluation().getTechnicalScore())
                .relevanceScoreDifference(current.getEvaluation().getRelevanceScore() - previous.getEvaluation().getRelevanceScore())
                .communicationScoreDifference(current.getEvaluation().getCommunicationScore() - previous.getEvaluation().getCommunicationScore())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void updateSessionStatusIfCompleted(InterviewSession session, User user) {
        long submittedQuestionsCount =
                answerAttemptRepository.countByQuestionInterviewSessionIdAndUserId(session.getId(), user.getId());

        if (submittedQuestionsCount >= session.getTotalQuestions()) {
            session.setStatus(SessionStatus.COMPLETED);
            interviewSessionRepository.save(session);
        }
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? Collections.emptyList() : values);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize evaluation JSON", ex);
        }
    }

    private List<String> readJsonArray(String json) {
        try {
            if (json == null || json.isBlank()) {
                return Collections.emptyList();
            }

            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private AnswerAttemptResponse mapToAttemptResponse(AnswerAttempt attempt, AttemptEvaluation evaluation) {
        return AnswerAttemptResponse.builder()
                .id(attempt.getId())
                .questionId(attempt.getQuestion().getId())
                .attemptNumber(attempt.getAttemptNumber())
                .isImproved(attempt.getIsImproved())
                .answerText(attempt.getAnswerText())
                .createdAt(attempt.getCreatedAt())
                .evaluation(mapToEvaluationResponse(evaluation))
                .answerMode(attempt.getAnswerMode().name())
                .audioUrl(attempt.getAudioUrl())
                .transcriptText(attempt.getTranscriptText())
                .build();
    }

    private AttemptEvaluationResponse mapToEvaluationResponse(AttemptEvaluation evaluation) {
        return AttemptEvaluationResponse.builder()
                .id(evaluation.getId())
                .relevanceScore(evaluation.getRelevanceScore())
                .technicalScore(evaluation.getTechnicalScore())
                .communicationScore(evaluation.getCommunicationScore())
                .overallScore(evaluation.getOverallScore())
                .depthLevel(evaluation.getDepthLevel())
                .isShallow(evaluation.getIsShallow())
                .missingKeywords(readJsonArray(evaluation.getMissingKeywordsJson()))
                .strengths(readJsonArray(evaluation.getStrengthsJson()))
                .weaknesses(readJsonArray(evaluation.getWeaknessesJson()))
                .missingPoints(readJsonArray(evaluation.getMissingPointsJson()))
                .improvedAnswer(evaluation.getImprovedAnswer())
                .createdAt(evaluation.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public AnswerAttemptResponse submitAudioAttempt(Long sessionId, Long questionId, MultipartFile audioFile) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        AnswerAttempt previousAttempt = answerAttemptRepository
                .findTopByQuestionIdAndUserIdOrderByAttemptNumberDesc(questionId, user.getId())
                .orElse(null);

        int nextAttemptNumber = previousAttempt == null ? 1 : previousAttempt.getAttemptNumber() + 1;

        AudioStorageService.StoredAudioResult storedAudio = audioStorageService.storeAudio(audioFile);
        String transcript = groqTranscriptionClient.transcribe(storedAudio.getFilePath());

        AnswerAttempt attempt = AnswerAttempt.builder()
                .question(question)
                .user(user)
                .attemptNumber(nextAttemptNumber)
                .answerText(transcript)
                .answerMode(AnswerMode.AUDIO)
                .audioUrl(storedAudio.getAudioUrl())
                .transcriptText(transcript)
                .isImproved(nextAttemptNumber > 1)
                .build();

        AnswerAttempt savedAttempt = answerAttemptRepository.save(attempt);

        GroqAnswerEvaluator.AnswerEvaluationResult aiResult = groqAnswerEvaluator.evaluate(
                AnswerEvaluationPromptBuilder.buildSystemInstruction(),
                AnswerEvaluationPromptBuilder.buildUserPrompt(
                        session.getTargetRole(),
                        session.getInterviewType().name(),
                        question.getQuestionText(),
                        transcript,
                        AnswerMode.AUDIO.name()
                )
        );

        AttemptEvaluation savedEvaluation = attemptEvaluationRepository.save(
                AttemptEvaluation.builder()
                        .attempt(savedAttempt)
                        .relevanceScore(aiResult.getRelevanceScore())
                        .technicalScore(aiResult.getTechnicalScore())
                        .communicationScore(aiResult.getCommunicationScore())
                        .overallScore(aiResult.getOverallScore())
                        .strengthsJson(writeJson(aiResult.getStrengths()))
                        .weaknessesJson(writeJson(aiResult.getWeaknesses()))
                        .missingPointsJson(writeJson(aiResult.getMissingPoints()))
                        .improvedAnswer(aiResult.getImprovedAnswer())
                        .build()
        );

        updateSessionStatusIfCompleted(session, user);

        return mapToAttemptResponse(savedAttempt, savedEvaluation);
    }

    @Override
    @Transactional
    public AnswerAttemptResponse submitAudioTranscriptAttempt(
            Long sessionId,
            Long questionId,
            SubmitAudioTranscriptRequest request
    ) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        if (request.getTranscriptText() == null || request.getTranscriptText().isBlank()) {
            throw new BadRequestException("Transcript text is required");
        }

        AnswerAttempt previousAttempt = answerAttemptRepository
                .findTopByQuestionIdAndUserIdOrderByAttemptNumberDesc(questionId, user.getId())
                .orElse(null);

        int nextAttemptNumber = previousAttempt == null ? 1 : previousAttempt.getAttemptNumber() + 1;

        String transcript = request.getTranscriptText().trim();

        AnswerAttempt attempt = AnswerAttempt.builder()
                .question(question)
                .user(user)
                .attemptNumber(nextAttemptNumber)
                .answerText(transcript)
                .answerMode(AnswerMode.AUDIO)
                .audioUrl(request.getAudioUrl())
                .transcriptText(transcript)
                .isImproved(nextAttemptNumber > 1)
                .build();

        AnswerAttempt savedAttempt = answerAttemptRepository.save(attempt);

        GroqAnswerEvaluator.AnswerEvaluationResult aiResult = groqAnswerEvaluator.evaluate(
                AnswerEvaluationPromptBuilder.buildSystemInstruction(),
                AnswerEvaluationPromptBuilder.buildUserPrompt(
                        session.getTargetRole(),
                        session.getInterviewType().name(),
                        question.getQuestionText(),
                        transcript,
                        AnswerMode.AUDIO.name()
                )
        );

        AttemptEvaluation savedEvaluation = attemptEvaluationRepository.save(
                AttemptEvaluation.builder()
                        .attempt(savedAttempt)
                        .relevanceScore(aiResult.getRelevanceScore())
                        .technicalScore(aiResult.getTechnicalScore())
                        .communicationScore(aiResult.getCommunicationScore())
                        .overallScore(aiResult.getOverallScore())
                        .missingKeywordsJson(writeJson(aiResult.getMissingKeywords()))
                        .depthLevel(aiResult.getDepthLevel())
                        .isShallow(aiResult.getIsShallow())
                        .strengthsJson(writeJson(aiResult.getStrengths()))
                        .weaknessesJson(writeJson(aiResult.getWeaknesses()))
                        .missingPointsJson(writeJson(aiResult.getMissingPoints()))
                        .improvedAnswer(aiResult.getImprovedAnswer())
                        .build()
        );

        updateSessionStatusIfCompleted(session, user);

        return mapToAttemptResponse(savedAttempt, savedEvaluation);
    }
}