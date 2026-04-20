package com.interviewcoach.repository;

import com.interviewcoach.entity.AnswerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerEvaluationRepository extends JpaRepository<AnswerEvaluation, Long> {
    Optional<AnswerEvaluation> findByInterviewAnswerId(Long answerId);
}