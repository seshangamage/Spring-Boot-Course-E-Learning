package com.api.laptopstore.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login Request DTO")
public class LoginRequest {
    @Schema(description = "Username of the user", example = "admin", required = true)
    private String username;

    @Schema(description = "Password of the user", example = "admin123", required = true)
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
