package com.interviewcoach.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.request.SubmitAnswerRequest;
import com.interviewcoach.dto.response.AnswerEvaluationResponse;
import com.interviewcoach.dto.response.InterviewAnswerResponse;
import com.interviewcoach.entity.*;
import com.interviewcoach.enums.SessionStatus;
import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.integration.ai.GeminiAnswerEvaluator;
import com.interviewcoach.repository.*;
import com.interviewcoach.service.AnswerService;
import com.interviewcoach.util.AnswerEvaluationPromptBuilder;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerServiceImpl implements AnswerService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final AnswerEvaluationRepository answerEvaluationRepository;
    private final GeminiAnswerEvaluator geminiAnswerEvaluator;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public InterviewAnswerResponse submitAnswer(Long sessionId, Long questionId, SubmitAnswerRequest request) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        if (interviewAnswerRepository.findByInterviewQuestionId(questionId).isPresent()) {
            throw new BadRequestException("Answer already submitted for this question");
        }

        InterviewAnswer answer = InterviewAnswer.builder()
                .interviewQuestion(question)
                .answerText(request.getAnswerText().trim())
                .build();

        InterviewAnswer savedAnswer = interviewAnswerRepository.save(answer);

        GeminiAnswerEvaluator.AnswerEvaluationResult aiResult = geminiAnswerEvaluator.evaluate(
                AnswerEvaluationPromptBuilder.buildSystemInstruction(),
                AnswerEvaluationPromptBuilder.buildUserPrompt(
                        session.getTargetRole(),
                        session.getInterviewType().name(),
                        question.getQuestionText(),
                        savedAnswer.getAnswerText()
                )
        );

        AnswerEvaluation savedEvaluation = answerEvaluationRepository.save(
                AnswerEvaluation.builder()
                        .interviewAnswer(savedAnswer)
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

        updateSessionStatusIfCompleted(session);

        return mapToAnswerResponse(savedAnswer, savedEvaluation);
    }

    @Override
    public InterviewAnswerResponse getAnswerByQuestionId(Long sessionId, Long questionId) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview question not found"));

        if (!question.getInterviewSession().getId().equals(session.getId())) {
            throw new BadRequestException("Question does not belong to the given session");
        }

        InterviewAnswer answer = interviewAnswerRepository.findByInterviewQuestionId(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found for this question"));

        AnswerEvaluation evaluation = answerEvaluationRepository.findByInterviewAnswerId(answer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found for this answer"));

        return mapToAnswerResponse(answer, evaluation);
    }

    @Override
    public List<InterviewAnswerResponse> getAnswersBySessionId(Long sessionId) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        List<InterviewAnswer> answers = interviewAnswerRepository
                .findByInterviewQuestionInterviewSessionIdOrderByInterviewQuestionQuestionOrderAsc(session.getId());

        return answers.stream()
                .map(answer -> {
                    AnswerEvaluation evaluation = answerEvaluationRepository.findByInterviewAnswerId(answer.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found for answer: " + answer.getId()));
                    return mapToAnswerResponse(answer, evaluation);
                })
                .toList();
    }

    @Override
    public AnswerEvaluationResponse getEvaluationByAnswerId(Long answerId) {
        User user = getCurrentUser();

        InterviewAnswer answer = interviewAnswerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Long sessionOwnerId = answer.getInterviewQuestion().getInterviewSession().getUser().getId();
        if (!sessionOwnerId.equals(user.getId())) {
            throw new ResourceNotFoundException("Answer not found");
        }

        AnswerEvaluation evaluation = answerEvaluationRepository.findByInterviewAnswerId(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found"));

        return mapToEvaluationResponse(evaluation);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void updateSessionStatusIfCompleted(InterviewSession session) {
        long submittedAnswers = interviewAnswerRepository.countByInterviewQuestionInterviewSessionId(session.getId());

        if (submittedAnswers >= session.getTotalQuestions()) {
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
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize evaluation JSON", ex);
        }
    }

    private InterviewAnswerResponse mapToAnswerResponse(InterviewAnswer answer, AnswerEvaluation evaluation) {
        return InterviewAnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getInterviewQuestion().getId())
                .questionOrder(answer.getInterviewQuestion().getQuestionOrder())
                .questionText(answer.getInterviewQuestion().getQuestionText())
                .answerText(answer.getAnswerText())
                .createdAt(answer.getCreatedAt())
                .evaluation(mapToEvaluationResponse(evaluation))
                .build();
    }

    private AnswerEvaluationResponse mapToEvaluationResponse(AnswerEvaluation evaluation) {
        return AnswerEvaluationResponse.builder()
                .id(evaluation.getId())
                .relevanceScore(evaluation.getRelevanceScore())
                .technicalScore(evaluation.getTechnicalScore())
                .communicationScore(evaluation.getCommunicationScore())
                .overallScore(evaluation.getOverallScore())
                .strengths(readJsonArray(evaluation.getStrengthsJson()))
                .weaknesses(readJsonArray(evaluation.getWeaknessesJson()))
                .missingPoints(readJsonArray(evaluation.getMissingPointsJson()))
                .improvedAnswer(evaluation.getImprovedAnswer())
                .createdAt(evaluation.getCreatedAt())
                .build();
    }
}