package com.wiley.laptopstore.controller;

import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.service.LaptopService;
import com.wiley.laptopstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private LaptopService laptopService;
    
    @Autowired
    private CartService cartService;

    // Directory to store uploaded images
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        logger.info("Accessing home page - displaying all laptops");
        try {
            List<Laptop> laptops = laptopService.getAllLaptops();
            logger.debug("Successfully retrieved {} laptops from database", laptops.size());
            
            // Get cart count for current session
            String sessionId = session.getId();
            int cartCount = cartService.getCartItemCount(sessionId);
            logger.debug("Cart count for session {}: {}", sessionId, cartCount);
            
            model.addAttribute("title", "Wiley Laptop Store");
            model.addAttribute("welcomeMessage", "Welcome to Wiley Laptop Store!");
            model.addAttribute("description", "Discover the latest laptops with cutting-edge technology and unbeatable prices.");
            model.addAttribute("laptops", laptops);
            model.addAttribute("cartCount", cartCount);
            
            logger.info("Home page loaded successfully with {} laptops, cart count: {}", laptops.size(), cartCount);
            return "home";
        } catch (Exception e) {
            logger.error("Error loading home page: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading laptops");
            return "error";
        }
    }
    
    @GetMapping("/add-laptop")
    public String showAddLaptopForm(Model model) {
        logger.info("Accessing add laptop form");
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("title", "Add New Laptop");
        logger.debug("Add laptop form loaded successfully");
        return "add-laptop";
    }
    
    @PostMapping("/add-laptop")
    public String addLaptop(@ModelAttribute("laptop") Laptop laptop, 
                           @RequestParam("imageFile") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        logger.info("Attempting to add new laptop: {}", laptop.getName());
        logger.debug("Laptop details - Brand: {}, Category: {}, Price: {}", 
                    laptop.getBrand(), laptop.getCategory(), laptop.getPrice());
        
        try {
            // Handle image upload
            if (!imageFile.isEmpty()) {
                logger.debug("Processing image upload for file: {}", imageFile.getOriginalFilename());
                String imagePath = saveImage(imageFile);
                laptop.setImagePath(imagePath);
                logger.info("Image uploaded successfully: {}", imagePath);
            } else {
                logger.debug("No image uploaded, using default image");
                laptop.setImagePath("/images/default-laptop.svg");
            }
            
            Laptop savedLaptop = laptopService.saveLaptop(laptop);
            logger.info("Successfully added laptop with ID: {} - {}", savedLaptop.getId(), savedLaptop.getName());
            
            redirectAttributes.addFlashAttribute("successMessage", "Laptop added successfully!");
        } catch (Exception e) {
            logger.error("Error adding laptop {}: {}", laptop.getName(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding laptop: " + e.getMessage());
        }
        return "redirect:/";
    }
    
    @PostMapping("/delete-laptop")
    public String deleteLaptop(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete laptop with ID: {}", id);
        
        try {
            Laptop laptop = laptopService.getLaptopById(id);
            if (laptop != null) {
                String laptopName = laptop.getName();
                laptopService.deleteLaptop(id);
                logger.info("Successfully deleted laptop: {} (ID: {})", laptopName, id);
                redirectAttributes.addFlashAttribute("successMessage", "Laptop deleted successfully!");
            } else {
                logger.warn("Attempted to delete non-existent laptop with ID: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Laptop not found!");
            }
        } catch (Exception e) {
            logger.error("Error deleting laptop with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting laptop: " + e.getMessage());
        }
        return "redirect:/";
    }
    
    @GetMapping("/search-laptops")
    public String searchLaptops(@RequestParam(value = "searchQuery", required = false) String searchQuery, 
                               Model model) {
        logger.info("Searching laptops with query: {}", searchQuery);
        
        try {
            List<Laptop> laptops = laptopService.searchLaptops(searchQuery);
            model.addAttribute("laptops", laptops);
            logger.info("Search completed successfully, found {} laptops", laptops.size());
            return "search-results :: laptop-results";
        } catch (Exception e) {
            logger.error("Error searching laptops with query '{}': {}", searchQuery, e.getMessage(), e);
            model.addAttribute("laptops", List.of());
            return "search-results :: laptop-results";
        }
    }
    
    private String saveImage(MultipartFile imageFile) throws IOException {
        logger.debug("Starting image save process for file: {}", imageFile.getOriginalFilename());
        
        // Get the original filename
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        
        // Generate a unique filename to avoid conflicts
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        logger.debug("Generated unique filename: {}", uniqueFilename);
        
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            logger.debug("Creating upload directory: {}", uploadPath);
            Files.createDirectories(uploadPath);
        }
        
        // Save the file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        String webPath = "/images/" + uniqueFilename;
        logger.info("Image saved successfully to: {} (web path: {})", filePath, webPath);
        
        // Return the web path for the image
        return webPath;
    }
}
