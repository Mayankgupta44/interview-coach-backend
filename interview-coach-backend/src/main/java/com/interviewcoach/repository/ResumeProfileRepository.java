package com.interviewcoach.repository;

import com.interviewcoach.entity.ResumeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeProfileRepository extends JpaRepository<ResumeProfile, Long> {
    Optional<ResumeProfile> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}