package com.interviewcoach.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeRequest {

    @NotBlank(message = "Resume text is required")
    @Size(min = 50, message = "Resume text must be at least 50 characters")
    private String resumeText;
}