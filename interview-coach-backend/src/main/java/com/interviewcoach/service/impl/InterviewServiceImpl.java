package com.interviewcoach.service.impl;

import com.interviewcoach.dto.request.CreateInterviewSessionRequest;
import com.interviewcoach.dto.response.InterviewQuestionResponse;
import com.interviewcoach.dto.response.InterviewSessionResponse;
import com.interviewcoach.entity.*;
import com.interviewcoach.enums.SessionStatus;
import com.interviewcoach.exception.ResourceNotFoundException;
import com.interviewcoach.integration.ai.GeminiInterviewQuestionGenerator;
import com.interviewcoach.repository.*;
import com.interviewcoach.service.InterviewService;
import com.interviewcoach.util.InterviewPromptBuilder;
import com.interviewcoach.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewServiceImpl implements InterviewService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final ResumeProfileRepository resumeProfileRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final GeminiInterviewQuestionGenerator geminiInterviewQuestionGenerator;

    @Override
    @Transactional
    public InterviewSessionResponse createSession(CreateInterviewSessionRequest request) {
        User user = getCurrentUser();

        String resumeText = resumeProfileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .map(ResumeProfile::getResumeText)
                .orElse("");

        String jobDescriptionText = jobDescriptionRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .map(JobDescription::getJobDescriptionText)
                .orElse("");

        GeminiInterviewQuestionGenerator.QuestionGenerationResult aiResult =
                geminiInterviewQuestionGenerator.generateQuestions(
                        InterviewPromptBuilder.buildSystemInstruction(),
                        InterviewPromptBuilder.buildUserPrompt(
                                request.getTargetRole(),
                                request.getInterviewType(),
                                user.getSkills(),
                                resumeText,
                                jobDescriptionText,
                                request.getQuestionCount()
                        )
                );

        InterviewSession session = InterviewSession.builder()
                .user(user)
                .targetRole(request.getTargetRole().trim())
                .interviewType(request.getInterviewType())
                .status(SessionStatus.STARTED)
                .totalQuestions(aiResult.getQuestions().size())
                .build();

        InterviewSession savedSession = interviewSessionRepository.save(session);

        List<InterviewQuestion> savedQuestions = new ArrayList<>();
        int order = 1;

        for (String questionText : aiResult.getQuestions()) {
            InterviewQuestion question = InterviewQuestion.builder()
                    .interviewSession(savedSession)
                    .questionOrder(order++)
                    .questionText(questionText.trim())
                    .build();

            savedQuestions.add(interviewQuestionRepository.save(question));
        }

        return mapToSessionResponse(savedSession, savedQuestions);
    }

    @Override
    public InterviewSessionResponse getSessionById(Long sessionId) {
        User user = getCurrentUser();

        InterviewSession session = interviewSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

        List<InterviewQuestion> questions =
                interviewQuestionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(session.getId());

        return mapToSessionResponse(session, questions);
    }

    @Override
    public List<InterviewSessionResponse> getMySessions() {
        User user = getCurrentUser();

        List<InterviewSession> sessions = interviewSessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return sessions.stream()
                .map(session -> {
                    List<InterviewQuestion> questions =
                            interviewQuestionRepository.findByInterviewSessionIdOrderByQuestionOrderAsc(session.getId());
                    return mapToSessionResponse(session, questions);
                })
                .toList();
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private InterviewSessionResponse mapToSessionResponse(InterviewSession session, List<InterviewQuestion> questions) {
        return InterviewSessionResponse.builder()
                .id(session.getId())
                .targetRole(session.getTargetRole())
                .interviewType(session.getInterviewType())
                .status(session.getStatus())
                .totalQuestions(session.getTotalQuestions())
                .createdAt(session.getCreatedAt())
                .questions(
                        questions.stream()
                                .map(this::mapToQuestionResponse)
                                .toList()
                )
                .build();
    }

    private InterviewQuestionResponse mapToQuestionResponse(InterviewQuestion question) {
        return InterviewQuestionResponse.builder()
                .id(question.getId())
                .questionOrder(question.getQuestionOrder())
                .questionText(question.getQuestionText())
                .build();
    }
}