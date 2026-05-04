package com.interviewcoach.repository;

import com.interviewcoach.entity.AttemptEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttemptEvaluationRepository extends JpaRepository<AttemptEvaluation, Long> {

    Optional<AttemptEvaluation> findByAttemptId(Long attemptId);
}