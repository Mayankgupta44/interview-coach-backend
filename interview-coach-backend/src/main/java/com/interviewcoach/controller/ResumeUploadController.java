package com.interviewcoach.controller;

import com.interviewcoach.dto.response.ResumeUploadResponse;
import com.interviewcoach.service.ResumeUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume-upload")
@RequiredArgsConstructor
public class ResumeUploadController {

    private final ResumeUploadService resumeUploadService;

    @PostMapping
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @RequestParam("file") MultipartFile file
    ) {
        return new ResponseEntity<>(resumeUploadService.uploadResume(file), HttpStatus.CREATED);
    }

    @GetMapping("/latest")
    public ResponseEntity<ResumeUploadResponse> getLatestUploadedResume() {
        return ResponseEntity.ok(resumeUploadService.getLatestUploadedResume());
    }
}