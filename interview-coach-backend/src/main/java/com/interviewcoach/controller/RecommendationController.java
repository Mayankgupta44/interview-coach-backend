package com.interviewcoach.controller;

import com.interviewcoach.dto.response.RecommendationResponse;
import com.interviewcoach.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/generate")
    public ResponseEntity<List<RecommendationResponse>> generateRecommendations() {
        return new ResponseEntity<>(
                recommendationService.generateRecommendationsForCurrentUser(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getMyRecommendations() {
        return ResponseEntity.ok(recommendationService.getMyRecommendations());
    }
}