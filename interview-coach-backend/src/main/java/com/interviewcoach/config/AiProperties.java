package com.interviewcoach.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private String provider;
    private String apiKey;
    private String baseUrl;
    private String model;
    private String fallbackModel;
    private String transcriptionModel;
    private int maxRetries = 2;
    private long retryDelayMs = 1200;
}