package com.interviewcoach.controller;

import com.interviewcoach.dto.request.CreateInterviewSessionRequest;
import com.interviewcoach.dto.response.InterviewSessionResponse;
import com.interviewcoach.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewSessionResponse> createSession(
            @Valid @RequestBody CreateInterviewSessionRequest request
    ) {
        return new ResponseEntity<>(interviewService.createSession(request), HttpStatus.CREATED);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<InterviewSessionResponse> getSessionById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(interviewService.getSessionById(sessionId));
    }

    @GetMapping
    public ResponseEntity<List<InterviewSessionResponse>> getMySessions() {
        return ResponseEntity.ok(interviewService.getMySessions());
    }
}