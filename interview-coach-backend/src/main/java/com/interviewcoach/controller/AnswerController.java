package com.interviewcoach.controller;

import com.interviewcoach.dto.request.SubmitAnswerRequest;
import com.interviewcoach.dto.response.AnswerEvaluationResponse;
import com.interviewcoach.dto.response.InterviewAnswerResponse;
import com.interviewcoach.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.interviewcoach.dto.request.SubmitAudioTranscriptRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/interviews/{sessionId}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/{questionId}")
    public ResponseEntity<InterviewAnswerResponse> submitAnswer(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAnswerRequest request
    ) {
        return new ResponseEntity<>(
                answerService.submitAnswer(sessionId, questionId, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<InterviewAnswerResponse> getAnswerByQuestionId(
            @PathVariable Long sessionId,
            @PathVariable Long questionId
    ) {
        return ResponseEntity.ok(answerService.getAnswerByQuestionId(sessionId, questionId));
    }

    @GetMapping
    public ResponseEntity<List<InterviewAnswerResponse>> getAnswersBySessionId(@PathVariable Long sessionId) {
        return ResponseEntity.ok(answerService.getAnswersBySessionId(sessionId));
    }

    @GetMapping("/evaluation/{answerId}")
    public ResponseEntity<AnswerEvaluationResponse> getEvaluationByAnswerId(
            @PathVariable Long sessionId,
            @PathVariable Long answerId
    ) {
        return ResponseEntity.ok(answerService.getEvaluationByAnswerId(answerId));
    }

    @PostMapping("/{questionId}/audio")
    public ResponseEntity<InterviewAnswerResponse> submitAudioAnswer(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @RequestParam("audio") MultipartFile audioFile
    ) {
        return new ResponseEntity<>(
                answerService.submitAudioAnswer(sessionId, questionId, audioFile),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{questionId}/audio-transcript")
    public ResponseEntity<InterviewAnswerResponse> submitAudioTranscriptAnswer(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAudioTranscriptRequest request
    ) {
        return new ResponseEntity<>(
                answerService.submitAudioTranscriptAnswer(sessionId, questionId, request),
                HttpStatus.CREATED
        );
    }
}