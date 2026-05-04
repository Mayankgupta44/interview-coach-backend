package com.interviewcoach.dto.request;

import com.interviewcoach.enums.ExperienceLevel;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 100, message = "Target role must not exceed 100 characters")
    private String targetRole;

    private ExperienceLevel experienceLevel;

    private Set<String> skills;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;

    @Size(max = 500, message = "LinkedIn URL must not exceed 500 characters")
    private String linkedInUrl;

    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    private String githubUrl;

    @Size(max = 500, message = "Portfolio URL must not exceed 500 characters")
    private String portfolioUrl;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 150, message = "Location must not exceed 150 characters")
    private String location;
}