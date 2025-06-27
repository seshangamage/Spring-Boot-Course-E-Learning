package com.wiley.laptopstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Wiley Laptop Store");
        model.addAttribute("welcomeMessage", "Welcome to Wiley Laptop Store!");
        model.addAttribute("description", "Discover the latest laptops with cutting-edge technology and unbeatable prices.");
        return "home";
    }
}
