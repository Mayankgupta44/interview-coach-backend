package com.interviewcoach.service.impl;

import com.interviewcoach.dto.response.AudioTranscriptResponse;
import com.interviewcoach.integration.ai.GroqTranscriptionClient;
import com.interviewcoach.service.AudioStorageService;
import com.interviewcoach.service.AudioTranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AudioTranscriptionServiceImpl implements AudioTranscriptionService {

    private final AudioStorageService audioStorageService;
    private final GroqTranscriptionClient groqTranscriptionClient;

    @Override
    public AudioTranscriptResponse transcribeAudio(MultipartFile audioFile) {
        AudioStorageService.StoredAudioResult storedAudio =
                audioStorageService.storeAudio(audioFile);

        String transcript = groqTranscriptionClient.transcribe(storedAudio.getFilePath());

        return AudioTranscriptResponse.builder()
                .audioUrl(storedAudio.getAudioUrl())
                .transcriptText(transcript)
                .build();
    }
}