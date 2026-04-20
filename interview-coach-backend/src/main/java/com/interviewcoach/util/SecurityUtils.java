package com.interviewcoach.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                authentication.getName() == null ||
                "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("No authenticated user found");
        }

        return authentication.getName();
    }
}