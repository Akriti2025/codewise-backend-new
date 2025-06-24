package com.codewise.codewise_backend.auth;

// This class is a simple Data Transfer Object (DTO) for login responses.
// It holds the generated JWT (access token) to be sent back to the client.
public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}