package com.interviewcoach.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAudioTranscriptRequest {

    @NotBlank(message = "Transcript text is required")
    private String transcriptText;

    private String audioUrl;
}