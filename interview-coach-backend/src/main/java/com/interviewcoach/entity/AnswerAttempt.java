package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;
import com.interviewcoach.enums.AnswerMode;

@Entity
@Table(name = "answer_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_improved", nullable = false)
    private Boolean isImproved;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_mode", nullable = false, length = 20)
    private AnswerMode answerMode;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "transcript_text", columnDefinition = "TEXT")
    private String transcriptText;
}