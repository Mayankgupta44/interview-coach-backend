package com.interviewcoach.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AudioTranscriptResponse {
    private String audioUrl;
    private String transcriptText;
}