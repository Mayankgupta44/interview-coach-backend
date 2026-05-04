package com.interviewcoach.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.audio.upload-dir:uploads/audio}")
    private String audioUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String audioPath = Paths.get(audioUploadDir).toAbsolutePath().normalize().toUri().toString();

        registry.addResourceHandler("/uploads/audio/**")
                .addResourceLocations(audioPath);
    }
}