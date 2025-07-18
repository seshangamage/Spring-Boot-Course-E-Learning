package com.example.laptopstore.api.repository;

import com.example.laptopstore.api.entity.Laptop;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class LaptopRepository {
    
    private final Map<Long, Laptop> laptops = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Save a laptop (create or update)
     */
    public Laptop save(Laptop laptop) {
        if (laptop.getId() == null) {
            laptop.setId(idGenerator.getAndIncrement());
        } else {
            laptop.updateTimestamp();
        }
        laptops.put(laptop.getId(), laptop);
        return laptop;
    }
    
    /**
     * Find all laptops
     */
    public List<Laptop> findAll() {
        return new ArrayList<>(laptops.values());
    }
    
    /**
     * Find laptop by ID
     */
    public Optional<Laptop> findById(Long id) {
        return Optional.ofNullable(laptops.get(id));
    }
    
    /**
     * Delete laptop by ID
     */
    public void deleteById(Long id) {
        laptops.remove(id);
    }
    
    /**
     * Check if laptop exists by ID
     */
    public boolean existsById(Long id) {
        return laptops.containsKey(id);
    }
    
    /**
     * Count total laptops
     */
    public long count() {
        return laptops.size();
    }
    
    /**
     * Find laptops by brand (case-insensitive)
     */
    public List<Laptop> findByBrandIgnoreCase(String brand) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getBrand().toLowerCase().equals(brand.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Find laptops by model containing a keyword (case-insensitive)
     */
    public List<Laptop> findByModelContainingIgnoreCase(String model) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getModel().toLowerCase().contains(model.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Find laptops within a price range
     */
    public List<Laptop> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getPrice().compareTo(minPrice) >= 0 && 
                                laptop.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }
    
    /**
     * Find laptops by RAM size
     */
    public List<Laptop> findByRamSizeGB(Integer ramSize) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getRamSizeGB().equals(ramSize))
                .collect(Collectors.toList());
    }
    
    /**
     * Find laptops by storage size greater than or equal to specified value
     */
    public List<Laptop> findByStorageSizeGBGreaterThanEqual(Integer storageSize) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getStorageSizeGB() >= storageSize)
                .collect(Collectors.toList());
    }
    
    /**
     * Find laptops by processor containing keyword (case-insensitive)
     */
    public List<Laptop> findByProcessorContainingIgnoreCase(String processor) {
        return laptops.values().stream()
                .filter(laptop -> laptop.getProcessor().toLowerCase().contains(processor.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Advanced search with multiple filters
     */
    public List<Laptop> findLaptopsWithFilters(String brand, BigDecimal minPrice, BigDecimal maxPrice, 
                                              Integer minRam, Integer minStorage) {
        return laptops.values().stream()
                .filter(laptop -> brand == null || 
                        laptop.getBrand().toLowerCase().contains(brand.toLowerCase()))
                .filter(laptop -> minPrice == null || 
                        laptop.getPrice().compareTo(minPrice) >= 0)
                .filter(laptop -> maxPrice == null || 
                        laptop.getPrice().compareTo(maxPrice) <= 0)
                .filter(laptop -> minRam == null || 
                        laptop.getRamSizeGB() >= minRam)
                .filter(laptop -> minStorage == null || 
                        laptop.getStorageSizeGB() >= minStorage)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a laptop with same brand and model exists
     */
    public boolean existsByBrandAndModel(String brand, String model) {
        return laptops.values().stream()
                .anyMatch(laptop -> laptop.getBrand().equalsIgnoreCase(brand) && 
                                  laptop.getModel().equalsIgnoreCase(model));
    }
}
