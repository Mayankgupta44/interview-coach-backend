package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill_gap_reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_snapshot", nullable = false, columnDefinition = "TEXT")
    private String resumeSnapshot;

    @Column(name = "job_description_snapshot", nullable = false, columnDefinition = "TEXT")
    private String jobDescriptionSnapshot;

    @Column(name = "matched_skills", nullable = false, columnDefinition = "TEXT")
    private String matchedSkillsJson;

    @Column(name = "missing_skills", nullable = false, columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Column(name = "recommended_topics", nullable = false, columnDefinition = "TEXT")
    private String recommendedTopicsJson;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "fit_score", nullable = false)
    private Integer fitScore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}