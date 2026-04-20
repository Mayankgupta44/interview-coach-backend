package com.interviewcoach.repository;

import com.interviewcoach.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    Optional<JobDescription> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}