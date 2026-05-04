package com.interviewcoach.service;

import com.interviewcoach.dto.response.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeUploadService {
    ResumeUploadResponse uploadResume(MultipartFile file);
    ResumeUploadResponse getLatestUploadedResume();
}