package com.api.laptopstore.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login Response DTO")
public class LoginResponse {
    @Schema(description = "JWT token for authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    private String token;

    @Schema(description = "Role of the authenticated user", example = "ROLE_ADMIN", required = true)
    private String role;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
