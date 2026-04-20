package com.interviewcoach.service.impl;

import com.interviewcoach.dto.request.JobDescriptionRequest;
import com.interviewcoach.dto.request.ResumeRequest;
import com.interviewcoach.dto.response.JobDescriptionResponse;
import com.interviewcoach.dto.response.ResumeResponse;
import com.interviewcoach.entity.JobDescription;
import com.interviewcoach.entity.ResumeProfile;
import com.interviewcoach.entity.User;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.JobDescriptionRepository;
import com.interviewcoach.repository.ResumeProfileRepository;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.ResumeService;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeProfileRepository resumeProfileRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResumeResponse saveResume(ResumeRequest request) {
        User user = getCurrentUser();

        ResumeProfile resumeProfile = ResumeProfile.builder()
                .resumeText(request.getResumeText().trim())
                .user(user)
                .build();

        ResumeProfile savedResume = resumeProfileRepository.save(resumeProfile);
        return mapToResumeResponse(savedResume);
    }

    @Override
    public ResumeResponse getLatestResume() {
        User user = getCurrentUser();

        ResumeProfile latestResume = resumeProfileRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No resume found for current user"));

        return mapToResumeResponse(latestResume);
    }

    @Override
    public JobDescriptionResponse saveJobDescription(JobDescriptionRequest request) {
        User user = getCurrentUser();

        JobDescription jobDescription = JobDescription.builder()
                .jobDescriptionText(request.getJobDescriptionText().trim())
                .user(user)
                .build();

        JobDescription savedJobDescription = jobDescriptionRepository.save(jobDescription);
        return mapToJobDescriptionResponse(savedJobDescription);
    }

    @Override
    public JobDescriptionResponse getLatestJobDescription() {
        User user = getCurrentUser();

        JobDescription latestJobDescription = jobDescriptionRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No job description found for current user"));

        return mapToJobDescriptionResponse(latestJobDescription);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ResumeResponse mapToResumeResponse(ResumeProfile resumeProfile) {
        return ResumeResponse.builder()
                .id(resumeProfile.getId())
                .userId(resumeProfile.getUser().getId())
                .resumeText(resumeProfile.getResumeText())
                .createdAt(resumeProfile.getCreatedAt())
                .updatedAt(resumeProfile.getUpdatedAt())
                .build();
    }

    private JobDescriptionResponse mapToJobDescriptionResponse(JobDescription jobDescription) {
        return JobDescriptionResponse.builder()
                .id(jobDescription.getId())
                .userId(jobDescription.getUser().getId())
                .jobDescriptionText(jobDescription.getJobDescriptionText())
                .createdAt(jobDescription.getCreatedAt())
                .updatedAt(jobDescription.getUpdatedAt())
                .build();
    }
}