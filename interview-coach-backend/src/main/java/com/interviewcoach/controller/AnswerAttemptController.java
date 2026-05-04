package com.interviewcoach.controller;

import com.interviewcoach.dto.request.SubmitAnswerAttemptRequest;
import com.interviewcoach.dto.response.AnswerAttemptResponse;
import com.interviewcoach.dto.response.AttemptComparisonResponse;
import com.interviewcoach.service.AnswerAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.interviewcoach.dto.request.SubmitAudioTranscriptRequest;

@RestController
@RequestMapping("/api/interviews/{sessionId}/questions/{questionId}/attempts")
@RequiredArgsConstructor
public class AnswerAttemptController {

    private final AnswerAttemptService answerAttemptService;

    @PostMapping
    public ResponseEntity<AnswerAttemptResponse> submitAttempt(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAnswerAttemptRequest request
    ) {
        return new ResponseEntity<>(
                answerAttemptService.submitAttempt(sessionId, questionId, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<AnswerAttemptResponse>> getAttempts(
            @PathVariable Long sessionId,
            @PathVariable Long questionId
    ) {
        return ResponseEntity.ok(answerAttemptService.getAttemptsByQuestionId(sessionId, questionId));
    }

    @GetMapping("/compare-latest")
    public ResponseEntity<AttemptComparisonResponse> compareLatest(
            @PathVariable Long sessionId,
            @PathVariable Long questionId
    ) {
        return ResponseEntity.ok(answerAttemptService.getLatestAttemptComparison(sessionId, questionId));
    }

    @PostMapping("/audio")
    public ResponseEntity<AnswerAttemptResponse> submitAudioAttempt(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @RequestParam("audio") MultipartFile audioFile
    ) {
        return new ResponseEntity<>(
                answerAttemptService.submitAudioAttempt(sessionId, questionId, audioFile),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/audio-transcript")
    public ResponseEntity<AnswerAttemptResponse> submitAudioTranscriptAttempt(
            @PathVariable Long sessionId,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAudioTranscriptRequest request
    ) {
        return new ResponseEntity<>(
                answerAttemptService.submitAudioTranscriptAttempt(sessionId, questionId, request),
                HttpStatus.CREATED
        );
    }
}