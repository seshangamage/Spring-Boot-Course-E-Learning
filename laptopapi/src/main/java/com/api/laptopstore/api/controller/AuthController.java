package com.api.laptopstore.api.controller;

import com.api.laptopstore.api.model.User;
import com.api.laptopstore.api.repository.UserRepository;
import com.api.laptopstore.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> credentials) {
        String password = credentials.get("password");
        String encodedPassword = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, encodedPassword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("originalPassword", password);
        response.put("encodedPassword", encodedPassword);
        response.put("matches", matches);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) {
        try {
            // First check if user exists
            User existingUser = userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + user.getUsername()));

            // Attempt authentication
            try {
                System.out.println("Attempting authentication for user: " + user.getUsername());
                System.out.println("Provided password length: " + (user.getPassword() != null ? user.getPassword().length() : 0));
                
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
                );
                
                System.out.println("Authentication successful");
            } catch (Exception e) {
                System.out.println("Authentication failed: " + e.getClass().getName() + ": " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }

            // Generate token
            try {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                final String jwt = jwtUtil.generateToken(userDetails);

                Map<String, String> response = new HashMap<>();
                response.put("token", jwt);
                response.put("role", existingUser.getRole());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error generating token: " + e.getMessage());
            }

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during authentication: " + e.getMessage());
        }
    }
}
