package com.interviewcoach.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedRoute(Authentication authentication) {
        return "Hello, " + authentication.getName() + ". You have accessed a protected endpoint.";
    }
}