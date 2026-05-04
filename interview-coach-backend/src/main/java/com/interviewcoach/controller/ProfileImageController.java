package com.interviewcoach.controller;

import com.interviewcoach.dto.response.ProfileImageUploadResponse;
import com.interviewcoach.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile-image")
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @PostMapping
    public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) {
        return new ResponseEntity<>(
                profileImageService.uploadProfileImage(file),
                HttpStatus.CREATED
        );
    }
}