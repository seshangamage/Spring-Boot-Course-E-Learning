package com.api.laptopstore.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.api.laptopstore.api.model.User;
import com.api.laptopstore.api.repository.UserRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Value("${admin.username:admin}")
    private String adminUsername;
    
    @Value("${admin.password:admin123}")
    private String adminPassword;
    
    @Value("${admin.role:ROLE_ADMIN}")
    private String adminRole;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            createAdminIfNotExist();
        } catch (Exception e) {
            logger.error("Failed to initialize admin user", e);
            // Depending on your requirements, you might want to rethrow the exception
            // to prevent the application from starting with no admin user
            throw new RuntimeException("Application initialization failed: Could not create admin user", e);
        }
    }

    private void createAdminIfNotExist() {
        if (userRepository.existsByUsername(adminUsername)) {
            logger.info("Admin user already exists, skipping initialization");
            return;
        }

        try {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(adminRole);
            
            userRepository.save(adminUser);
            logger.info("Admin user created successfully with username: {}", adminUsername);
            
        } catch (Exception e) {
            logger.error("Error creating admin user: {}", e.getMessage());
            throw new RuntimeException("Failed to create admin user", e);
        }
    }
}
