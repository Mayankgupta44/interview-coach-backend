package com.interviewcoach.service;

import com.interviewcoach.dto.response.AudioTranscriptResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AudioTranscriptionService {
    AudioTranscriptResponse transcribeAudio(MultipartFile audioFile);
}