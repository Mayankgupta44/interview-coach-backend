package com.interviewcoach.controller;

import com.interviewcoach.dto.response.AudioTranscriptResponse;
import com.interviewcoach.service.AudioTranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioTranscriptionService audioTranscriptionService;

    @PostMapping("/transcribe")
    public ResponseEntity<AudioTranscriptResponse> transcribeAudio(
            @RequestParam("audio") MultipartFile audioFile
    ) {
        return new ResponseEntity<>(
                audioTranscriptionService.transcribeAudio(audioFile),
                HttpStatus.CREATED
        );
    }
}