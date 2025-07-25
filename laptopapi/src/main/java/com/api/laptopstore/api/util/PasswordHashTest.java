package com.api.laptopstore.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("Generated hash for 'admin123': " + hash);
        System.out.println("Hash length: " + hash.length());
        System.out.println("Does it match existing hash? " + 
            encoder.matches(password, "$2a$12$LQrW6dA1SC3gWGF0ISFVkeb5Yr930j.EVSTYGb2zHOZ1.lP8ydC4m"));
        
        // Generate a few more hashes to verify
        System.out.println("\nGenerating multiple hashes for verification:");
        for (int i = 0; i < 3; i++) {
            String newHash = encoder.encode(password);
            System.out.println("Hash " + (i+1) + ": " + newHash);
            System.out.println("Matches password? " + encoder.matches(password, newHash));
        }
    }
}
