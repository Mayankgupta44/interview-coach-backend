package com.interviewcoach.repository;

import com.interviewcoach.entity.AnswerAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerAttemptRepository extends JpaRepository<AnswerAttempt, Long> {

    List<AnswerAttempt> findByQuestionIdAndUserIdOrderByAttemptNumberAsc(Long questionId, Long userId);

    Optional<AnswerAttempt> findTopByQuestionIdAndUserIdOrderByAttemptNumberDesc(Long questionId, Long userId);

    long countByQuestionInterviewSessionIdAndUserId(Long sessionId, Long userId);

    List<AnswerAttempt> findByUserId(Long userId);
}