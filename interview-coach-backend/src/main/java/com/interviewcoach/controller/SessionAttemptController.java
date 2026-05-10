package com.interviewcoach.controller;

import com.interviewcoach.dto.response.AnswerAttemptResponse;
import com.interviewcoach.service.AnswerAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews/{sessionId}/attempts")
@RequiredArgsConstructor
public class SessionAttemptController {

    private final AnswerAttemptService answerAttemptService;

    @GetMapping
    public ResponseEntity<List<AnswerAttemptResponse>> getSessionAttempts(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(answerAttemptService.getAttemptsBySessionId(sessionId));
    }
}
