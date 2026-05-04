package com.interviewcoach.entity;

import com.interviewcoach.enums.InterviewType;
import com.interviewcoach.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import com.interviewcoach.enums.DifficultyLevel;

@Entity
@Table(name = "interview_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_role", nullable = false, length = 100)
    private String targetRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 30)
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 30)
    private DifficultyLevel difficultyLevel;

    @Column(name = "question_style", length = 100)
    private String questionStyle;
}