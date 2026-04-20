package com.interviewcoach.dto.response;

import com.interviewcoach.enums.ExperienceLevel;
import com.interviewcoach.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class ProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private String targetRole;
    private ExperienceLevel experienceLevel;
    private Set<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}