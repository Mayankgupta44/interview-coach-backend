package com.interviewcoach.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String profileUploadPath = Paths.get("uploads/profile-images")
                .toAbsolutePath()
                .normalize()
                .toString();

        registry.addResourceHandler("/uploads/profile-images/**")
                .addResourceLocations("file:" + profileUploadPath + "/");
    }
}