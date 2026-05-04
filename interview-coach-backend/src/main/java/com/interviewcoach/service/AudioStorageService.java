package com.interviewcoach.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public interface AudioStorageService {

    StoredAudioResult storeAudio(MultipartFile file);

    @Getter
    @Builder
    class StoredAudioResult {
        private String storedFileName;
        private String filePath;
        private String audioUrl;
    }
}