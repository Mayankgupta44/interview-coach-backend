package com.interviewcoach.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFileResult storeResumePdf(MultipartFile file);

    @Getter
    @Builder
    class StoredFileResult {
        private String storedFileName;
        private String filePath;
    }
}