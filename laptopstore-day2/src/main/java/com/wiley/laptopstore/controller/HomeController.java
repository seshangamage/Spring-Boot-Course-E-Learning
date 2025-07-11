package com.wiley.laptopstore.controller;

import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class HomeController {

    @Autowired
    private LaptopService laptopService;

    // Directory to store uploaded images
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Wiley Laptop Store");
        model.addAttribute("welcomeMessage", "Welcome to Wiley Laptop Store!");
        model.addAttribute("description", "Discover the latest laptops with cutting-edge technology and unbeatable prices.");
        model.addAttribute("laptops", laptopService.getAllLaptops());
        return "home";
    }
    
    @GetMapping("/add-laptop")
    public String showAddLaptopForm(Model model) {
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("title", "Add New Laptop");
        return "add-laptop";
    }
    
    @PostMapping("/add-laptop")
    public String addLaptop(@ModelAttribute("laptop") Laptop laptop, 
                           @RequestParam("imageFile") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            // Handle image upload
            if (!imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile);
                laptop.setImagePath(imagePath);
            } else {
                // Set default image path if no image uploaded
                laptop.setImagePath("/images/default-laptop.svg");
            }
            
            laptopService.saveLaptop(laptop);
            redirectAttributes.addFlashAttribute("successMessage", "Laptop added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding laptop: " + e.getMessage());
        }
        return "redirect:/";
    }
    
    private String saveImage(MultipartFile imageFile) throws IOException {
        // Get the original filename
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        
        // Generate a unique filename to avoid conflicts
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Save the file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the web path for the image
        return "/images/" + uniqueFilename;
    }
}
