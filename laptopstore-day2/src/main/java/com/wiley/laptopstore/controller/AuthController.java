package com.wiley.laptopstore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        logger.info("Accessing login page");
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password!");
            logger.warn("Login failed - invalid credentials");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully!");
            logger.info("User logged out successfully");
        }
        
        return "login";
    }
}
