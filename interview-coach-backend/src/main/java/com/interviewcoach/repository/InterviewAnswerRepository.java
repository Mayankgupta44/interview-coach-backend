package com.interviewcoach.repository;

import com.interviewcoach.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {
    Optional<InterviewAnswer> findByInterviewQuestionId(Long questionId);
    List<InterviewAnswer> findByInterviewQuestionInterviewSessionIdOrderByInterviewQuestionQuestionOrderAsc(Long sessionId);
    List<InterviewAnswer> findByInterviewQuestionInterviewSessionUserIdOrderByCreatedAtDesc(Long userId);
    long countByInterviewQuestionInterviewSessionId(Long sessionId);
}