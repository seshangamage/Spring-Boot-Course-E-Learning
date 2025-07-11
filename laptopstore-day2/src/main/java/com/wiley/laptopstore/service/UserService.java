package com.wiley.laptopstore.service;

import com.wiley.laptopstore.entity.User;
import com.wiley.laptopstore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user
     */
    public User registerUser(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role
        user.setRoles(Set.of(User.Role.USER));
        
        // Enable user by default
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by username or email
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        logger.debug("Finding user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }
    
    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Update user profile
     */
    public User updateUser(User user) {
        logger.info("Updating user: {}", user.getUsername());
        
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            logger.warn("User not found for update: {}", user.getId());
            throw new RuntimeException("User not found");
        }
        
        User userToUpdate = existingUser.get();
        
        // Update fields (don't update username, email, password here)
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setPhone(user.getPhone());
        userToUpdate.setAddress(user.getAddress());
        userToUpdate.setCity(user.getCity());
        userToUpdate.setState(user.getState());
        userToUpdate.setZipCode(user.getZipCode());
        userToUpdate.setCountry(user.getCountry());
        
        User updatedUser = userRepository.save(userToUpdate);
        logger.info("User updated successfully: {}", updatedUser.getUsername());
        
        return updatedUser;
    }
    
    /**
     * Change user password
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        logger.info("Changing password for user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for password change: {}", username);
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Invalid old password for user: {}", username);
            throw new RuntimeException("Invalid old password");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user: {}", username);
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * Enable/disable user
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        logger.info("Setting user {} enabled status to: {}", userId, enabled);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(enabled);
            userRepository.save(user);
            logger.info("User {} enabled status updated to: {}", userId, enabled);
        } else {
            logger.warn("User not found for enabled status update: {}", userId);
            throw new RuntimeException("User not found");
        }
    }
}
