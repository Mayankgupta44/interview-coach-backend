package com.interviewcoach.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public interface PdfExtractionService {

    ExtractionResult extractText(MultipartFile file);

    @Getter
    @AllArgsConstructor
    class ExtractionResult {
        private boolean success;
        private String extractedText;
        private String errorMessage;
    }
}