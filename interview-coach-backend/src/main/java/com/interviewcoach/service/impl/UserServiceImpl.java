package com.interviewcoach.service.impl;

import com.interviewcoach.dto.request.UpdateProfileRequest;
import com.interviewcoach.dto.response.ProfileResponse;
import com.interviewcoach.entity.User;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.repository.UserRepository;
import com.interviewcoach.service.UserService;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ProfileResponse getMyProfile() {
        User user = getCurrentUser();
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        user.setFullName(request.getFullName());
        user.setTargetRole(request.getTargetRole());
        user.setExperienceLevel(request.getExperienceLevel());
        user.setSkills(request.getSkills() != null ? new HashSet<>(request.getSkills()) : new HashSet<>());

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ProfileResponse mapToProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .targetRole(user.getTargetRole())
                .experienceLevel(user.getExperienceLevel())
                .skills(user.getSkills())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}