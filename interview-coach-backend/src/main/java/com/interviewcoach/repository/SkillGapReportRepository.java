package com.interviewcoach.repository;

import com.interviewcoach.entity.SkillGapReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillGapReportRepository extends JpaRepository<SkillGapReport, Long> {
    Optional<SkillGapReport> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    List<SkillGapReport> findByUserIdOrderByCreatedAtDesc(Long userId);
}