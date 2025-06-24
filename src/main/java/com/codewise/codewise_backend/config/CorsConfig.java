package com.codewise.codewise_backend.config; // This is the package name based on your structure

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply CORS to all endpoints
                        .allowedOrigins(
                            // "http://localhost:3000", // For local frontend testing
                            // "http://localhost:5173", // For local Vite/React dev server testing
                            "https://your-frontend-app-name.onrender.com" // IMPORTANT: Replace with your actual deployed frontend URL on Render
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true) // Allow credentials (cookies, auth headers)
                        .maxAge(3600); // Max age for preflight requests
            }
        };
    }
}