package com.interviewcoach.service.impl;

import com.interviewcoach.dto.response.ProfileImageUploadResponse;
import com.interviewcoach.entity.User;
import com.interviewcoach.exception.BadRequestException;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.ProfileImageService;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {

    private final UserRepository userRepository;

    @Value("${app.file.profile-upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    @Override
    @Transactional
    public ProfileImageUploadResponse uploadProfileImage(MultipartFile file) {
        validateFile(file);

        User user = getCurrentUser();

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename() == null
                    ? "profile.png"
                    : file.getOriginalFilename();

            String extension = getExtension(originalName);
            String storedFileName = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/profile-images/" + storedFileName;

            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);

            return ProfileImageUploadResponse.builder()
                    .profileImageUrl(imageUrl)
                    .build();

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to upload profile image", ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image is required");
        }

        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();

        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPG, JPEG, and PNG files are allowed");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BadRequestException("Profile image must be 2MB or smaller");
        }
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index >= 0 ? fileName.substring(index).toLowerCase() : ".png";
    }
}