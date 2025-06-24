package com.codewise.codewise_backend.auth;

import org.springframework.security.authentication.AuthenticationManager; // NEW: Import AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // NEW: Import UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication; // NEW: Import Authentication interface
import org.springframework.security.core.userdetails.UserDetails; // NEW: Import UserDetails
import org.springframework.stereotype.Service;

import com.codewise.codewise_backend.jwt.JwtService; // NEW: Import JwtService
import com.codewise.codewise_backend.user.UserService;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService; // NEW: Inject JwtService
    private final AuthenticationManager authenticationManager; // NEW: Inject AuthenticationManager

    // MODIFIED: Constructor now injects JwtService and AuthenticationManager
    public AuthService(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // New method to handle user login.
    public String loginUser(String username, String password) {
        // Authenticate the user using Spring Security's AuthenticationManager.
        // This will trigger UserService.loadUserByUsername and compare passwords.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // If authentication is successful, generate a JWT.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Get the UserDetails object
        return jwtService.generateToken(userDetails); // Generate JWT for the authenticated user
    }
}