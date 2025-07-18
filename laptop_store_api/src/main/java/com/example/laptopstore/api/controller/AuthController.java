package com.example.laptopstore.api.controller;

import com.example.laptopstore.api.entity.JwtToken;
import com.example.laptopstore.api.entity.User;
import com.example.laptopstore.api.security.JwtTokenUtil;
import com.example.laptopstore.api.service.JwtTokenService;
import com.example.laptopstore.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * Login endpoint to get JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            // Get user details
            Optional<User> userOpt = userService.findByUsernameOrEmail(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(401).body(error);
            }
            
            User user = userOpt.get();

            // Generate and save JWT token
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            JwtToken jwtToken = jwtTokenService.generateAndSaveToken(user, ipAddress, userAgent);
            
            // Update last login time
            userService.updateLastLogin(user.getId());

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", jwtToken.getTokenValue());
            response.put("token_type", "Bearer");
            response.put("expires_in", 86400); // 24 hours
            response.put("username", user.getUsername());
            response.put("role", user.getRole().name());
            response.put("token_id", jwtToken.getId());
            response.put("issued_at", jwtToken.getIssuedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

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
     * Register new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Create new user
            User user = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                User.Role.USER // Default role
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Logout endpoint - revoke current token
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtTokenService.revokeToken(token);
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Logged out successfully");
                return ResponseEntity.ok(response);
            }
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token");
            return ResponseEntity.status(400).body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Logout from all devices - revoke all user tokens
     * POST /api/auth/logout-all
     */
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(token);
                
                Optional<User> userOpt = userService.findByUsername(username);
                if (userOpt.isPresent()) {
                    jwtTokenService.revokeAllUserTokens(userOpt.get());
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Logged out from all devices successfully");
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token");
            return ResponseEntity.status(400).body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get user's active tokens
     * GET /api/auth/tokens
     */
    @GetMapping("/tokens")
    public ResponseEntity<?> getUserTokens(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(token);
                
                Optional<User> userOpt = userService.findByUsername(username);
                if (userOpt.isPresent()) {
                    List<JwtToken> tokens = jwtTokenService.getValidTokensForUser(userOpt.get());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("active_tokens", tokens.size());
                    response.put("tokens", tokens.stream().map(t -> {
                        Map<String, Object> tokenInfo = new HashMap<>();
                        tokenInfo.put("id", t.getId());
                        tokenInfo.put("issued_at", t.getIssuedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        tokenInfo.put("expires_at", t.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        tokenInfo.put("ip_address", t.getIpAddress());
                        tokenInfo.put("user_agent", t.getUserAgent());
                        tokenInfo.put("is_current", t.getTokenValue().equals(token));
                        return tokenInfo;
                    }).toList());
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
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
                
                if (jwtTokenService.validateToken(token)) {
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    String role = jwtTokenUtil.getRoleFromToken(token);
                    
                    Optional<JwtToken> tokenDetailsOpt = jwtTokenService.getTokenDetails(token);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("role", role);
                    
                    if (tokenDetailsOpt.isPresent()) {
                        JwtToken tokenDetails = tokenDetailsOpt.get();
                        response.put("token_id", tokenDetails.getId());
                        response.put("issued_at", tokenDetails.getIssuedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        response.put("expires_at", tokenDetails.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                    
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

    // Helper method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }

    // Inner classes for request objects
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

    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;

        // Constructors
        public RegisterRequest() {}

        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
