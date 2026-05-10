package com.interviewcoach.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file.profile-upload-dir:uploads/profile-images}")
    private String profileUploadDir;

    @PostConstruct
    public void createProfileUploadDirectory() {
        try {
            Path uploadPath = Paths.get(profileUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
        } catch (Exception e) {
            throw new RuntimeException("Could not create profile image upload directory", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String profileUploadPath = Paths.get(profileUploadDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        if (!profileUploadPath.endsWith("/")) {
            profileUploadPath = profileUploadPath + "/";
        }

        registry.addResourceHandler("/uploads/profile-images/**")
                .addResourceLocations(profileUploadPath);
    }
}
