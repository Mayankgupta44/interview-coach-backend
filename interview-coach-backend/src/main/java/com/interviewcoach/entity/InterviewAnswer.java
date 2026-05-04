package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;
import com.interviewcoach.enums.AnswerMode;

@Entity
@Table(name = "interview_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private InterviewQuestion interviewQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_mode", nullable = false, length = 20)
    private AnswerMode answerMode;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "transcript_text", columnDefinition = "TEXT")
    private String transcriptText;
}