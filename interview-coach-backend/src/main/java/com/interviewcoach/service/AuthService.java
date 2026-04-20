package com.interviewcoach.service;

import com.interviewcoach.dto.request.LoginRequest;
import com.interviewcoach.dto.request.RegisterRequest;
import com.interviewcoach.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}