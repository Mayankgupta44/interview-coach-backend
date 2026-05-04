package com.interviewcoach.service.impl;

import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.service.AudioStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class AudioStorageServiceImpl implements AudioStorageService {

    @Value("${app.audio.upload-dir:uploads/audio}")
    private String uploadDir;

    @Value("${app.audio.max-size-mb:10}")
    private long maxSizeMb;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".webm", ".wav", ".mp3", ".m4a", ".ogg"
    );

    @Override
    public StoredAudioResult storeAudio(MultipartFile file) {
        validateAudio(file);

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename() == null
                    ? "answer.webm"
                    : file.getOriginalFilename();

            String extension = getExtension(originalName);
            String storedFileName = UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return StoredAudioResult.builder()
                    .storedFileName(storedFileName)
                    .filePath(targetPath.toString())
                    .audioUrl("/uploads/audio/" + storedFileName)
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to store audio file", ex);
        }
    }

    private void validateAudio(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Audio file is required");
        }

        long maxBytes = maxSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("Audio file size must be " + maxSizeMb + "MB or smaller");
        }

        String fileName = file.getOriginalFilename() == null
                ? ""
                : file.getOriginalFilename().toLowerCase();

        String extension = getExtension(fileName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Supported audio formats: webm, wav, mp3, m4a, ogg");
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index >= 0 ? fileName.substring(index) : ".webm";
    }
}