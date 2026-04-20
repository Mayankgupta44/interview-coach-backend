package com.interviewcoach.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {

    @NotBlank(message = "Answer text is required")
    @Size(min = 20, message = "Answer must be at least 20 characters long")
    private String answerText;
}