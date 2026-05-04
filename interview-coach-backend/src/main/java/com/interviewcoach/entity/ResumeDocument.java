package com.interviewcoach.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resume_documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false, unique = true)
    private String storedFileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    @Column(name = "extraction_success", nullable = false)
    private Boolean extractionSuccess;

    @Column(name = "extraction_error")
    private String extractionError;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}