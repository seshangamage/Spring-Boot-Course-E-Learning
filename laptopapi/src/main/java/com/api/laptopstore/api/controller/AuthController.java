package com.api.laptopstore.api.controller;

import com.api.laptopstore.api.dto.LoginRequest;
import com.api.laptopstore.api.dto.LoginResponse;
import com.api.laptopstore.api.model.User;
import com.api.laptopstore.api.repository.UserRepository;
import com.api.laptopstore.api.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API endpoints for authentication and user management")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with ROLE_USER privilege"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User successfully registered"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Username already exists or invalid input"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration details",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @Schema(implementation = LoginRequest.class)
                )
            )
            @RequestBody LoginRequest request) {
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns JWT token with user role"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User credentials",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @Schema(implementation = LoginRequest.class)
                )
            )
            @RequestBody LoginRequest request) {
        try {
            // First check if user exists
            User existingUser = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

            // Attempt authentication
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: Invalid credentials");
            }

            // Generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            LoginResponse response = new LoginResponse(jwt, existingUser.getRole());
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during authentication");
        }
    }
}
