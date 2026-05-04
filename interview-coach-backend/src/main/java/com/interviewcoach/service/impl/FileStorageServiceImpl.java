package com.interviewcoach.service.impl;

import com.interviewcoach.service.FileStorageService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public FileStorageService.StoredFileResult storeResumePdf(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename() == null
                    ? "resume.pdf"
                    : file.getOriginalFilename();

            String extension = getExtension(originalFileName);
            String storedFileName = UUID.randomUUID() + (extension.isBlank() ? ".pdf" : extension);

            Path targetPath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return FileStorageService.StoredFileResult.builder()
                    .storedFileName(storedFileName)
                    .filePath(targetPath.toString())
                    .build();

        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index >= 0 ? fileName.substring(index) : "";
    }

}