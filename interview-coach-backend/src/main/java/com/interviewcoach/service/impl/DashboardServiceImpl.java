package com.interviewcoach.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.response.*;
import com.interviewcoach.entity.*;
import com.interviewcoach.enums.SessionStatus;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.AnswerEvaluationRepository;
import com.interviewcoach.repository.InterviewAnswerRepository;
import com.interviewcoach.repository.InterviewSessionRepository;
import com.interviewcoach.repository.AnswerAttemptRepository;
import com.interviewcoach.repository.AttemptEvaluationRepository;
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
    private final AnswerAttemptRepository answerAttemptRepository;
    private final AttemptEvaluationRepository attemptEvaluationRepository;
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
                .scoreTrend(buildScoreTrend(user.getId()))
                .weakAreaStats(buildWeakAreaStats(user.getId()))
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
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private List<ScoreTrendPoint> buildScoreTrend(Long userId) {
        List<AnswerAttempt> attempts = answerAttemptRepository.findByUserId(userId);

        return attempts.stream()
                .map(attempt -> {
                    AttemptEvaluation eval = attemptEvaluationRepository
                            .findByAttemptId(attempt.getId())
                            .orElse(null);

                    if (eval == null) return null;

                    return ScoreTrendPoint.builder()
                            .date("Attempt " + attempt.getId())
                            .averageScore(eval.getOverallScore())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<WeakAreaStat> buildWeakAreaStats(Long userId) {
        List<AnswerAttempt> attempts = answerAttemptRepository.findByUserId(userId);

        Map<String, Integer> frequency = new HashMap<>();

        for (AnswerAttempt attempt : attempts) {
            AttemptEvaluation eval = attemptEvaluationRepository
                    .findByAttemptId(attempt.getId())
                    .orElse(null);

            if (eval == null) continue;

            List<String> weaknesses = readJsonArray(eval.getWeaknessesJson());
            List<String> missingPoints = readJsonArray(eval.getMissingPointsJson());
            List<String> missingKeywords = readJsonArray(eval.getMissingKeywordsJson());

            for (String item : weaknesses) {
                addWeakArea(frequency, item);
            }

            for (String item : missingPoints) {
                addWeakArea(frequency, item);
            }

            for (String item : missingKeywords) {
                addWeakArea(frequency, item);
            }
        }

        return frequency.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(6)
                .map(entry -> WeakAreaStat.builder()
                        .topic(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    private void addWeakArea(Map<String, Integer> frequency, String text) {
        if (text == null || text.isBlank()) return;

        String topic = normalizeWeakArea(text);
        frequency.put(topic, frequency.getOrDefault(topic, 0) + 1);
    }

    private String normalizeWeakArea(String text) {
        if (text == null) return "Other";

        String value = text.toLowerCase();

        if (value.contains("exception") || value.contains("error")) return "Exception Handling";
        if (value.contains("cache")) return "Caching";
        if (value.contains("security") || value.contains("auth")) return "Security";
        if (value.contains("spring")) return "Spring Boot";
        if (value.contains("oop")) return "OOP Concepts";
        if (value.contains("microservice")) return "Microservices";
        if (value.contains("rest") || value.contains("api")) return "REST APIs";
        if (value.contains("database") || value.contains("query") || value.contains("sql")) return "Database";
        if (value.contains("optimiz")) return "Optimization";
        if (value.contains("docker") || value.contains("container")) return "Containerization";
        if (value.contains("domain") || value.contains("ddd")) return "Domain Design";
        if (value.contains("test")) return "Testing";

        return text.length() > 30 ? text.substring(0, 30) + "..." : text;
    }
}