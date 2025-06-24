package com.codewise.codewise_backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // NEW: Import for login error handling
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewise.codewise_backend.exceptions.UsernameAlreadyExistsException; // NEW: Import custom exception
import com.codewise.codewise_backend.user.User;
import com.codewise.codewise_backend.user.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = userService.registerNewUser(registerRequest.getUsername(), registerRequest.getPassword());
            return new ResponseEntity<>("User registered successfully: " + registeredUser.getUsername(), HttpStatus.CREATED);
        } catch (UsernameAlreadyExistsException e) { // MODIFIED: Catch specific exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Return 409 Conflict
        } catch (Exception e) { // Catch any other unexpected errors
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
        }
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return new ResponseEntity<>(new LoginResponse(jwt), HttpStatus.OK); // Return 200 OK with JWT
        } catch (BadCredentialsException e) { // Catch specific authentication failures
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); // Return 401 Unauthorized for bad credentials
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Return 500 for other errors
        }
    }
}