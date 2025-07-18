package com.example.laptopstore.api.config;

import com.example.laptopstore.api.entity.User;
import com.example.laptopstore.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    
    @Autowired
    public UserDataInitializer(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userService.existsByUsername("admin")) {
            userService.createUser("admin", "admin@laptopstore.com", "laptop123", User.Role.ADMIN);
            System.out.println("✅ Default admin user created: admin/laptop123");
        }
        
        // Create default regular user if not exists
        if (!userService.existsByUsername("user")) {
            userService.createUser("user", "user@laptopstore.com", "user123", User.Role.USER);
            System.out.println("✅ Default user created: user/user123");
        }
        
        System.out.println("🔐 User initialization completed!");
    }
}
