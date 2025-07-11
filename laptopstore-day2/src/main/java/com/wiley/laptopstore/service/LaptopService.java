package com.wiley.laptopstore.service;

import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Service
public class LaptopService {
    
    @Autowired
    private LaptopRepository laptopRepository;
    
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }
    
    public Laptop getLaptopById(Long id) {
        return laptopRepository.findById(id).orElse(null);
    }
    
    public Laptop saveLaptop(Laptop laptop) {
        return laptopRepository.save(laptop);
    }
    
    public void deleteLaptop(Long id) {
        laptopRepository.deleteById(id);
    }
    
    @PostConstruct
    public void initData() {
        // Initialize with sample data
        if (laptopRepository.count() == 0) {
            laptopRepository.save(new Laptop(
                "Dell XPS 13", 
                "Dell", 
                "Ultrabook", 
                new BigDecimal("1299"),
                "Ultra-portable laptop with 13.3\" InfinityEdge display",
                "Intel Core i7-1165G7",
                "/images/dell-xps-13.jpg"
            ));
            
            laptopRepository.save(new Laptop(
                "MacBook Pro 14\"", 
                "Apple", 
                "Professional", 
                new BigDecimal("1999"),
                "Powerful laptop with M2 Pro chip and Liquid Retina XDR display",
                "Apple M2 Pro",
                "/images/macbook-pro-14.jpg"
            ));
            
            laptopRepository.save(new Laptop(
                "HP Spectre x360", 
                "HP", 
                "2-in-1 Convertible", 
                new BigDecimal("1149"),
                "Versatile 2-in-1 laptop with 360-degree hinge",
                "Intel Core i5-1235U",
                "/images/hp-spectre-x360.jpg"
            ));
            
            laptopRepository.save(new Laptop(
                "ASUS ROG Strix G15", 
                "ASUS", 
                "Gaming", 
                new BigDecimal("1599"),
                "High-performance gaming laptop with RGB keyboard",
                "AMD Ryzen 7 5800H",
                "/images/asus-rog-strix-g15.jpg"
            ));
        }
    }
}
