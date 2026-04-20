package com.interviewcoach.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.response.DashboardResponse;
import com.interviewcoach.dto.response.RecommendationResponse;
import com.interviewcoach.dto.response.ScoreHistoryItemResponse;
import com.interviewcoach.entity.AnswerEvaluation;
import com.interviewcoach.entity.InterviewAnswer;
import com.interviewcoach.entity.InterviewSession;
import com.interviewcoach.entity.User;
import com.interviewcoach.enums.SessionStatus;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.AnswerEvaluationRepository;
import com.interviewcoach.repository.InterviewAnswerRepository;
import com.interviewcoach.repository.InterviewSessionRepository;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.DashboardService;
import com.interviewcoach.service.RecommendationService;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final AnswerEvaluationRepository answerEvaluationRepository;
    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    @Override
    public DashboardResponse getDashboard() {
        User user = getCurrentUser();

        List<InterviewSession> sessions = interviewSessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        int totalSessions = sessions.size();
        int completedSessions = (int) sessions.stream()
                .filter(session -> session.getStatus() == SessionStatus.COMPLETED)
                .count();

        List<InterviewAnswer> allAnswers = interviewAnswerRepository
                .findByInterviewQuestionInterviewSessionUserIdOrderByCreatedAtDesc(user.getId());

        int totalAnswersSubmitted = allAnswers.size();

        List<AnswerEvaluation> evaluations = allAnswers.stream()
                .map(answer -> answerEvaluationRepository.findByInterviewAnswerId(answer.getId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        int averageOverallScore = evaluations.isEmpty()
                ? 0
                : (int) Math.round(
                evaluations.stream()
                .mapToInt(AnswerEvaluation::getOverallScore)
                .average()
                .orElse(0)
        );

        List<String> weakAreas = extractWeakAreas(evaluations);

        List<ScoreHistoryItemResponse> recentScoreHistory = sessions.stream()
                .limit(5)
                .map(this::mapToScoreHistory)
                .toList();

        List<RecommendationResponse> recommendations = recommendationService.getMyRecommendations();

        return DashboardResponse.builder()
                .totalSessions(totalSessions)
                .completedSessions(completedSessions)
                .totalAnswersSubmitted(totalAnswersSubmitted)
                .averageOverallScore(averageOverallScore)
                .weakAreas(weakAreas)
                .recentScoreHistory(recentScoreHistory)
                .recommendations(recommendations)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private List<String> extractWeakAreas(List<AnswerEvaluation> evaluations) {
        Map<String, Long> frequencyMap = new HashMap<>();

        for (AnswerEvaluation evaluation : evaluations) {
            List<String> weaknesses = readJsonArray(evaluation.getWeaknessesJson());
            List<String> missingPoints = readJsonArray(evaluation.getMissingPointsJson());

            for (String weakness : weaknesses) {
                frequencyMap.put(weakness, frequencyMap.getOrDefault(weakness, 0L) + 1);
            }

            for (String point : missingPoints) {
                frequencyMap.put(point, frequencyMap.getOrDefault(point, 0L) + 1);
            }
        }

        return frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    private ScoreHistoryItemResponse mapToScoreHistory(InterviewSession session) {
        List<InterviewAnswer> sessionAnswers = interviewAnswerRepository
                .findByInterviewQuestionInterviewSessionIdOrderByInterviewQuestionQuestionOrderAsc(session.getId());

        List<AnswerEvaluation> sessionEvaluations = sessionAnswers.stream()
                .map(answer -> answerEvaluationRepository.findByInterviewAnswerId(answer.getId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        int averageScore = sessionEvaluations.isEmpty()
                ? 0
                : (int) Math.round(
                sessionEvaluations.stream()
                .mapToInt(AnswerEvaluation::getOverallScore)
                .average()
                .orElse(0)
        );

        return ScoreHistoryItemResponse.builder()
                .sessionId(session.getId())
                .targetRole(session.getTargetRole())
                .averageScore(averageScore)
                .totalQuestions(session.getTotalQuestions())
                .createdAt(session.getCreatedAt())
                .build();
    }

    private List<String> readJsonArray(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize dashboard JSON", ex);
        }
    }
}