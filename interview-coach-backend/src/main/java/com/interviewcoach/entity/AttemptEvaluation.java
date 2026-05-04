package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attempt_evaluations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptEvaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relevance_score", nullable = false)
    private Integer relevanceScore;

    @Column(name = "technical_score", nullable = false)
    private Integer technicalScore;

    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "depth_level")
    private String depthLevel;

    @Column(name = "is_shallow")
    private Boolean isShallow;

    @Column(name = "missing_keywords_json", columnDefinition = "TEXT")
    private String missingKeywordsJson;

    @Column(name = "strengths_json", nullable = false, columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "weaknesses_json", nullable = false, columnDefinition = "TEXT")
    private String weaknessesJson;

    @Column(name = "missing_points_json", nullable = false, columnDefinition = "TEXT")
    private String missingPointsJson;

    @Column(name = "improved_answer", nullable = false, columnDefinition = "TEXT")
    private String improvedAnswer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false, unique = true)
    private AnswerAttempt attempt;
}