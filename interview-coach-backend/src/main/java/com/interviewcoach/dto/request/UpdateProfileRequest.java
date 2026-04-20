package com.interviewcoach.dto.request;

import com.interviewcoach.enums.ExperienceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 100, message = "Target role must not exceed 100 characters")
    private String targetRole;

    private ExperienceLevel experienceLevel;

    private Set<@Size(min = 1, max = 50, message = "Each skill must be between 1 and 50 characters") String> skills = new HashSet<>();
}