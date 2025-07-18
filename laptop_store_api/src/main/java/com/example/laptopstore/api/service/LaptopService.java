package com.example.laptopstore.api.service;

import com.example.laptopstore.api.entity.Laptop;
import com.example.laptopstore.api.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {
    
    private final LaptopRepository laptopRepository;
    
    @Autowired
    public LaptopService(LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }
    
    /**
     * Create a new laptop
     */
    public Laptop createLaptop(Laptop laptop) {
        // Check if laptop with same brand and model already exists
        if (laptopRepository.existsByBrandAndModel(laptop.getBrand(), laptop.getModel())) {
            throw new IllegalArgumentException("Laptop with brand '" + laptop.getBrand() + 
                                             "' and model '" + laptop.getModel() + "' already exists");
        }
        return laptopRepository.save(laptop);
    }
    
    /**
     * Get all laptops
     */
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }
    
    /**
     * Get laptop by ID
     */
    public Optional<Laptop> getLaptopById(Long id) {
        return laptopRepository.findById(id);
    }
    
    /**
     * Update an existing laptop
     */
    public Laptop updateLaptop(Long id, Laptop laptopDetails) {
        Laptop laptop = laptopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laptop not found with id: " + id));
        
        // Check if the updated brand/model combination already exists for a different laptop
        if (!laptop.getBrand().equals(laptopDetails.getBrand()) || 
            !laptop.getModel().equals(laptopDetails.getModel())) {
            if (laptopRepository.existsByBrandAndModel(laptopDetails.getBrand(), laptopDetails.getModel())) {
                throw new IllegalArgumentException("Laptop with brand '" + laptopDetails.getBrand() + 
                                                 "' and model '" + laptopDetails.getModel() + "' already exists");
            }
        }
        
        // Update laptop properties
        laptop.setBrand(laptopDetails.getBrand());
        laptop.setModel(laptopDetails.getModel());
        laptop.setPrice(laptopDetails.getPrice());
        laptop.setDescription(laptopDetails.getDescription());
        laptop.setProcessor(laptopDetails.getProcessor());
        laptop.setRamSizeGB(laptopDetails.getRamSizeGB());
        laptop.setStorageSizeGB(laptopDetails.getStorageSizeGB());
        laptop.setStorageType(laptopDetails.getStorageType());
        laptop.setScreenSize(laptopDetails.getScreenSize());
        
        return laptopRepository.save(laptop);
    }
    
    /**
     * Delete a laptop
     */
    public void deleteLaptop(Long id) {
        if (!laptopRepository.existsById(id)) {
            throw new RuntimeException("Laptop not found with id: " + id);
        }
        laptopRepository.deleteById(id);
    }
    
    /**
     * Search laptops by brand
     */
    public List<Laptop> getLaptopsByBrand(String brand) {
        return laptopRepository.findByBrandIgnoreCase(brand);
    }
    
    /**
     * Search laptops by model keyword
     */
    public List<Laptop> getLaptopsByModel(String model) {
        return laptopRepository.findByModelContainingIgnoreCase(model);
    }
    
    /**
     * Search laptops by price range
     */
    public List<Laptop> getLaptopsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return laptopRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Search laptops by RAM size
     */
    public List<Laptop> getLaptopsByRamSize(Integer ramSize) {
        return laptopRepository.findByRamSizeGB(ramSize);
    }
    
    /**
     * Search laptops by minimum storage size
     */
    public List<Laptop> getLaptopsByMinStorageSize(Integer storageSize) {
        return laptopRepository.findByStorageSizeGBGreaterThanEqual(storageSize);
    }
    
    /**
     * Search laptops by processor keyword
     */
    public List<Laptop> getLaptopsByProcessor(String processor) {
        return laptopRepository.findByProcessorContainingIgnoreCase(processor);
    }
    
    /**
     * Advanced search with multiple filters
     */
    public List<Laptop> searchLaptops(String brand, BigDecimal minPrice, BigDecimal maxPrice, 
                                     Integer minRam, Integer minStorage) {
        return laptopRepository.findLaptopsWithFilters(brand, minPrice, maxPrice, minRam, minStorage);
    }
}
