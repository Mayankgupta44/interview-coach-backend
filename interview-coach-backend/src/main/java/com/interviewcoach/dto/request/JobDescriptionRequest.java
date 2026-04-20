package com.interviewcoach.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDescriptionRequest {

    @NotBlank(message = "Job description text is required")
    @Size(min = 50, message = "Job description text must be at least 50 characters")
    private String jobDescriptionText;
}