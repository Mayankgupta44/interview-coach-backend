package com.interviewcoach.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI interviewCoachOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Interview Coach API")
                        .description("Backend APIs for AI-Powered Interview Preparation and Communication Coach")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mayank")
                                .email("mayankagarwal.ma35@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"));
    }
}