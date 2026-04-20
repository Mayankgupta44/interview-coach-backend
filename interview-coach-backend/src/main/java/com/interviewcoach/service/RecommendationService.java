package com.interviewcoach.service;

import com.interviewcoach.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> generateRecommendationsForCurrentUser();
    List<RecommendationResponse> getMyRecommendations();
}