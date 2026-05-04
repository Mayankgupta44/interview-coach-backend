package com.interviewcoach.repository;

import com.interviewcoach.entity.ResumeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeDocumentRepository extends JpaRepository<ResumeDocument, Long> {
    Optional<ResumeDocument> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}