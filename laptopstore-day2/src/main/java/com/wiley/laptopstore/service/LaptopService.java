package com.wiley.laptopstore.service;

import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.repository.LaptopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {
    
    private static final Logger logger = LoggerFactory.getLogger(LaptopService.class);
    
    @Autowired
    private LaptopRepository laptopRepository;
    
    public List<Laptop> getAllLaptops() {
        logger.debug("Retrieving all laptops from database");
        try {
            List<Laptop> laptops = laptopRepository.findAll();
            logger.info("Successfully retrieved {} laptops from database", laptops.size());
            return laptops;
        } catch (Exception e) {
            logger.error("Error retrieving laptops from database: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Laptop> searchLaptops(String query) {
        logger.debug("Searching laptops with query: {}", query);
        try {
            if (query == null || query.trim().isEmpty()) {
                logger.debug("Empty search query, returning all laptops");
                return getAllLaptops();
            }
            
            String searchTerm = query.trim().toLowerCase();
            List<Laptop> allLaptops = laptopRepository.findAll();
            
            List<Laptop> searchResults = allLaptops.stream()
                .filter(laptop -> 
                    laptop.getName().toLowerCase().contains(searchTerm) ||
                    laptop.getBrand().toLowerCase().contains(searchTerm) ||
                    laptop.getCategory().toLowerCase().contains(searchTerm) ||
                    laptop.getProcessor().toLowerCase().contains(searchTerm) ||
                    (laptop.getInfo() != null && laptop.getInfo().toLowerCase().contains(searchTerm))
                )
                .collect(java.util.stream.Collectors.toList());
            
            logger.info("Search for '{}' returned {} laptops", query, searchResults.size());
            return searchResults;
        } catch (Exception e) {
            logger.error("Error searching laptops with query '{}': {}", query, e.getMessage(), e);
            throw e;
        }
    }
    
    public Optional<Laptop> findById(Long id) {
        logger.debug("Finding laptop with ID: {}", id);
        try {
            Optional<Laptop> laptop = laptopRepository.findById(id);
            if (laptop.isPresent()) {
                logger.info("Found laptop: {} (ID: {})", laptop.get().getName(), id);
            } else {
                logger.warn("Laptop with ID {} not found", id);
            }
            return laptop;
        } catch (Exception e) {
            logger.error("Error finding laptop with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    public Laptop getLaptopById(Long id) {
        logger.debug("Retrieving laptop with ID: {}", id);
        try {
            Optional<Laptop> laptop = laptopRepository.findById(id);
            if (laptop.isPresent()) {
                logger.info("Successfully retrieved laptop: {} (ID: {})", laptop.get().getName(), id);
                return laptop.get();
            } else {
                logger.warn("Laptop with ID {} not found", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving laptop with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    public Laptop saveLaptop(Laptop laptop) {
        if (laptop.getId() == null) {
            logger.info("Creating new laptop: {}", laptop.getName());
            logger.debug("New laptop details - Brand: {}, Category: {}, Price: {}", 
                        laptop.getBrand(), laptop.getCategory(), laptop.getPrice());
        } else {
            logger.info("Updating existing laptop: {} (ID: {})", laptop.getName(), laptop.getId());
        }
        
        try {
            Laptop savedLaptop = laptopRepository.save(laptop);
            if (laptop.getId() == null) {
                logger.info("Successfully created laptop with ID: {} - {}", savedLaptop.getId(), savedLaptop.getName());
            } else {
                logger.info("Successfully updated laptop with ID: {} - {}", savedLaptop.getId(), savedLaptop.getName());
            }
            return savedLaptop;
        } catch (Exception e) {
            logger.error("Error saving laptop {}: {}", laptop.getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    public void deleteLaptop(Long id) {
        logger.info("Attempting to delete laptop with ID: {}", id);
        try {
            Optional<Laptop> laptop = laptopRepository.findById(id);
            if (laptop.isPresent()) {
                String laptopName = laptop.get().getName();
                laptopRepository.deleteById(id);
                logger.info("Successfully deleted laptop: {} (ID: {})", laptopName, id);
            } else {
                logger.warn("Attempted to delete non-existent laptop with ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting laptop with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    @PostConstruct
    public void initData() {
        logger.info("Initializing sample laptop data");
        
        // Initialize with sample data
        if (laptopRepository.count() == 0) {
            logger.debug("No existing laptops found, creating sample data");
            
            try {
                Laptop laptop1 = new Laptop(
                    "Dell XPS 13", 
                    "Dell", 
                    "Ultrabook", 
                    new BigDecimal("1299"),
                    "Ultra-portable laptop with 13.3\" InfinityEdge display",
                    "Intel Core i7-1165G7",
                    "/images/dell-xps-13.jpg"
                );
                laptopRepository.save(laptop1);
                logger.debug("Created sample laptop: {}", laptop1.getName());
                
                Laptop laptop2 = new Laptop(
                    "MacBook Pro 14\"", 
                    "Apple", 
                    "Professional", 
                    new BigDecimal("1999"),
                    "Powerful laptop with M2 Pro chip and Liquid Retina XDR display",
                    "Apple M2 Pro",
                    "/images/macbook-pro-14.jpg"
                );
                laptopRepository.save(laptop2);
                logger.debug("Created sample laptop: {}", laptop2.getName());
                
                Laptop laptop3 = new Laptop(
                    "HP Spectre x360", 
                    "HP", 
                    "2-in-1 Convertible", 
                    new BigDecimal("1149"),
                    "Versatile 2-in-1 laptop with 360-degree hinge",
                    "Intel Core i5-1235U",
                    "/images/hp-spectre-x360.jpg"
                );
                laptopRepository.save(laptop3);
                logger.debug("Created sample laptop: {}", laptop3.getName());
                
                Laptop laptop4 = new Laptop(
                    "ASUS ROG Strix G15", 
                    "ASUS", 
                    "Gaming", 
                    new BigDecimal("1599"),
                    "High-performance gaming laptop with RGB keyboard",
                    "AMD Ryzen 7 5800H",
                    "/images/asus-rog-strix-g15.jpg"
                );
                laptopRepository.save(laptop4);
                logger.debug("Created sample laptop: {}", laptop4.getName());
                
                logger.info("Successfully initialized {} sample laptops", laptopRepository.count());
            } catch (Exception e) {
                logger.error("Error initializing sample data: {}", e.getMessage(), e);
            }
        } else {
            logger.info("Sample data already exists, skipping initialization. Total laptops: {}", laptopRepository.count());
        }
    }
}
