package com.interviewcoach.service.impl;

import com.interviewcoach.dto.response.ResumeUploadResponse;
import com.interviewcoach.entity.ResumeDocument;
import com.interviewcoach.entity.User;
import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.ResumeDocumentRepository;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.FileStorageService;
import com.interviewcoach.service.PdfExtractionService;
import com.interviewcoach.service.ResumeUploadService;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeUploadServiceImpl implements ResumeUploadService {

    private final UserRepository userRepository;
    private final ResumeDocumentRepository resumeDocumentRepository;
    private final FileStorageService fileStorageService;
    private final PdfExtractionService pdfExtractionService;

    @Override
    @Transactional
    public ResumeUploadResponse uploadResume(MultipartFile file) {
        validateFile(file);

        User user = getCurrentUser();

        FileStorageService.StoredFileResult storedFile = fileStorageService.storeResumePdf(file);
        PdfExtractionService.ExtractionResult extractionResult = pdfExtractionService.extractText(file);

        ResumeDocument resumeDocument = ResumeDocument.builder()
                .user(user)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFile.getStoredFileName())
                .filePath(storedFile.getFilePath())
                .fileSize(file.getSize())
                .contentType(file.getContentType() == null ? "application/pdf" : file.getContentType())
                .extractedText(extractionResult.getExtractedText())
                .extractionSuccess(extractionResult.isSuccess())
                .extractionError(extractionResult.getErrorMessage())
                .build();

        ResumeDocument saved = resumeDocumentRepository.save(resumeDocument);

        return mapToResponse(saved);
    }

    @Override
    public ResumeUploadResponse getLatestUploadedResume() {
        User user = getCurrentUser();

        ResumeDocument latest = resumeDocumentRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No uploaded resume found"));

        return mapToResponse(latest);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is required");
        }

        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();

        boolean isPdfByName = fileName.endsWith(".pdf");
        boolean isPdfByType = contentType.contains("pdf");

        if (!isPdfByName && !isPdfByType) {
            throw new BadRequestException("Only PDF resume upload is supported");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Resume file size must be 5MB or smaller");
        }
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ResumeUploadResponse mapToResponse(ResumeDocument document) {
        return ResumeUploadResponse.builder()
                .id(document.getId())
                .originalFileName(document.getOriginalFileName())
                .fileSize(document.getFileSize())
                .contentType(document.getContentType())
                .extractedText(document.getExtractedText())
                .extractionSuccess(document.getExtractionSuccess())
                .extractionError(document.getExtractionError())
                .createdAt(document.getCreatedAt())
                .build();
    }
}