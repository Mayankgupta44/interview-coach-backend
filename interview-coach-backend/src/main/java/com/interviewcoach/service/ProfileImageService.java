package com.interviewcoach.service;

import com.interviewcoach.dto.response.ProfileImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
    ProfileImageUploadResponse uploadProfileImage(MultipartFile file);
}