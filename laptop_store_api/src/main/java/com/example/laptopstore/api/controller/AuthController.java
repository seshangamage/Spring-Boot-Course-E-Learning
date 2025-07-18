package com.example.laptopstore.api.controller;

import com.example.laptopstore.api.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Login endpoint to get JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(loginRequest.getUsername(), "ADMIN");

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", token);
            response.put("token_type", "Bearer");
            response.put("expires_in", 86400); // 24 hours
            response.put("username", loginRequest.getUsername());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Username or password is incorrect");
            return ResponseEntity.status(401).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get a demo token (for testing purposes)
     * POST /api/auth/demo-token
     */
    @PostMapping("/demo-token")
    public ResponseEntity<?> getDemoToken() {
        // Generate demo token
        String token = jwtTokenUtil.generateToken("demo-user", "ADMIN");

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", token);
        response.put("token_type", "Bearer");
        response.put("expires_in", 86400);
        response.put("username", "demo-user");
        response.put("note", "This is a demo token for testing purposes");

        return ResponseEntity.ok(response);
    }

    /**
     * Validate token endpoint
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtTokenUtil.validateToken(token)) {
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    String role = jwtTokenUtil.getRoleFromToken(token);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("role", role);
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid token");
            
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(401).body(response);
        }
    }

    // Inner class for login request
    public static class LoginRequest {
        private String username;
        private String password;

        // Constructors
        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getters and setters
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
}
