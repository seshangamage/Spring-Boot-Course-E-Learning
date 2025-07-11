package com.wiley.laptopstore.controller;

import com.wiley.laptopstore.entity.User;
import com.wiley.laptopstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Show registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Showing user registration form");
        model.addAttribute("user", new User());
        return "register";
    }
    
    /**
     * Process user registration
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                             @RequestParam("confirmPassword") String confirmPassword,
                             RedirectAttributes redirectAttributes) {
        logger.info("Processing user registration for: {}", user.getUsername());
        
        try {
            // Validate password confirmation
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match!");
                return "redirect:/user/register";
            }
            
            // Validate password length
            if (user.getPassword().length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters long!");
                return "redirect:/user/register";
            }
            
            // Register user
            userService.registerUser(user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! You can now login with your credentials.");
            
            logger.info("User registered successfully: {}", user.getUsername());
            return "redirect:/login";
            
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/register";
        }
    }
    
    /**
     * Show user profile
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        logger.info("Showing user profile");
        // This would typically get the current authenticated user
        // For now, we'll implement this after updating the security config
        return "profile";
    }
    
    /**
     * Check if username is available (AJAX endpoint)
     */
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username) {
        logger.debug("Checking username availability: {}", username);
        return !userService.existsByUsername(username);
    }
    
    /**
     * Check if email is available (AJAX endpoint)
     */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email) {
        logger.debug("Checking email availability: {}", email);
        return !userService.existsByEmail(email);
    }
}
