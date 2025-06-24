package com.codewise.codewise_backend;

import org.springframework.security.access.prepost.PreAuthorize; // NEW: Import PreAuthorize
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // Changed base path to /api
public class HomeController {

    // This endpoint remains publicly accessible
    @GetMapping("/")
    public String home() {
        return "Welcome to the Codewise Backend!";
    }

    // MODIFIED: This endpoint now requires authentication (a valid JWT)
    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()") // Requires the user to be authenticated
    public String hello() {
        return "Hello, authenticated user! You accessed a protected endpoint.";
    }
}