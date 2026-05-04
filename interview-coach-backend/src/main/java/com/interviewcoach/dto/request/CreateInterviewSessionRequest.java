package com.interviewcoach.dto.request;

import com.interviewcoach.enums.DifficultyLevel;
import com.interviewcoach.enums.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInterviewSessionRequest {

    @NotBlank(message = "Target role is required")
    private String targetRole;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel = DifficultyLevel.FRESHER;

    private String questionStyle = "SINGLE_FOCUSED";

    @Min(value = 3, message = "Question count must be at least 3")
    @Max(value = 10, message = "Question count must not exceed 10")
    private Integer questionCount = 5;
}