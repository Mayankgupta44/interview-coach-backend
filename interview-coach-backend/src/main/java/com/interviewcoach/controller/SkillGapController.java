package com.interviewcoach.controller;

import com.interviewcoach.dto.request.SkillGapAnalysisRequest;
import com.interviewcoach.dto.response.SkillGapReportResponse;
import com.interviewcoach.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skill-gap")
@RequiredArgsConstructor
public class SkillGapController {

    private final SkillGapService skillGapService;

    @PostMapping("/analyze")
    public ResponseEntity<SkillGapReportResponse> analyze(@RequestBody(required = false) SkillGapAnalysisRequest request) {
        return new ResponseEntity<>(skillGapService.analyzeSkillGap(request), HttpStatus.CREATED);
    }

    @GetMapping("/latest")
    public ResponseEntity<SkillGapReportResponse> getLatest() {
        return ResponseEntity.ok(skillGapService.getLatestSkillGapReport());
    }
}