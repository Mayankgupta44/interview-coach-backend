package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer_evaluations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerEvaluation extends BaseEntity {

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

    @Column(name = "strengths", nullable = false, columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "weaknesses", nullable = false, columnDefinition = "TEXT")
    private String weaknessesJson;

    @Column(name = "missing_points", nullable = false, columnDefinition = "TEXT")
    private String missingPointsJson;

    @Column(name = "improved_answer", nullable = false, columnDefinition = "TEXT")
    private String improvedAnswer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private InterviewAnswer interviewAnswer;
}