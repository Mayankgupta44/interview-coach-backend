package com.interviewcoach.controller;

import com.interviewcoach.dto.request.JobDescriptionRequest;
import com.interviewcoach.dto.request.ResumeRequest;
import com.interviewcoach.dto.response.JobDescriptionResponse;
import com.interviewcoach.dto.response.ResumeResponse;
import com.interviewcoach.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resume")
    public ResponseEntity<ResumeResponse> saveResume(@Valid @RequestBody ResumeRequest request) {
        return new ResponseEntity<>(resumeService.saveResume(request), HttpStatus.CREATED);
    }

    @GetMapping("/resume/latest")
    public ResponseEntity<ResumeResponse> getLatestResume() {
        return ResponseEntity.ok(resumeService.getLatestResume());
    }

    @PostMapping("/job-description")
    public ResponseEntity<JobDescriptionResponse> saveJobDescription(@Valid @RequestBody JobDescriptionRequest request) {
        return new ResponseEntity<>(resumeService.saveJobDescription(request), HttpStatus.CREATED);
    }

    @GetMapping("/job-description/latest")
    public ResponseEntity<JobDescriptionResponse> getLatestJobDescription() {
        return ResponseEntity.ok(resumeService.getLatestJobDescription());
    }
}