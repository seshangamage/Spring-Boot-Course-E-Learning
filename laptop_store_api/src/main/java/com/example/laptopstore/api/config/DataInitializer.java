package com.example.laptopstore.api.config;

import com.example.laptopstore.api.entity.Laptop;
import com.example.laptopstore.api.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final LaptopRepository laptopRepository;
    
    @Autowired
    public DataInitializer(LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (laptopRepository.count() == 0) {
            initializeData();
        }
    }
    
    private void initializeData() {
        // Create sample laptops
        Laptop laptop1 = new Laptop();
        laptop1.setBrand("Dell");
        laptop1.setModel("XPS 13");
        laptop1.setPrice(new BigDecimal("1299.99"));
        laptop1.setDescription("Premium ultrabook with stunning display");
        laptop1.setProcessor("Intel Core i7-11th Gen");
        laptop1.setRamSizeGB(16);
        laptop1.setStorageSizeGB(512);
        laptop1.setStorageType("SSD");
        laptop1.setScreenSize("13.3 inches");
        
        Laptop laptop2 = new Laptop();
        laptop2.setBrand("Apple");
        laptop2.setModel("MacBook Air M2");
        laptop2.setPrice(new BigDecimal("1199.99"));
        laptop2.setDescription("Powerful and efficient with Apple M2 chip");
        laptop2.setProcessor("Apple M2");
        laptop2.setRamSizeGB(8);
        laptop2.setStorageSizeGB(256);
        laptop2.setStorageType("SSD");
        laptop2.setScreenSize("13.6 inches");
        
        Laptop laptop3 = new Laptop();
        laptop3.setBrand("HP");
        laptop3.setModel("Pavilion 15");
        laptop3.setPrice(new BigDecimal("699.99"));
        laptop3.setDescription("Reliable laptop for everyday use");
        laptop3.setProcessor("AMD Ryzen 5 5500U");
        laptop3.setRamSizeGB(8);
        laptop3.setStorageSizeGB(512);
        laptop3.setStorageType("SSD");
        laptop3.setScreenSize("15.6 inches");
        
        Laptop laptop4 = new Laptop();
        laptop4.setBrand("Lenovo");
        laptop4.setModel("ThinkPad X1 Carbon");
        laptop4.setPrice(new BigDecimal("1599.99"));
        laptop4.setDescription("Business laptop with exceptional build quality");
        laptop4.setProcessor("Intel Core i7-12th Gen");
        laptop4.setRamSizeGB(16);
        laptop4.setStorageSizeGB(1000);
        laptop4.setStorageType("SSD");
        laptop4.setScreenSize("14 inches");
        
        Laptop laptop5 = new Laptop();
        laptop5.setBrand("ASUS");
        laptop5.setModel("ROG Strix G15");
        laptop5.setPrice(new BigDecimal("1399.99"));
        laptop5.setDescription("Gaming laptop with high-performance graphics");
        laptop5.setProcessor("AMD Ryzen 7 5800H");
        laptop5.setRamSizeGB(16);
        laptop5.setStorageSizeGB(512);
        laptop5.setStorageType("SSD");
        laptop5.setScreenSize("15.6 inches");
        
        // Save all laptops
        laptopRepository.save(laptop1);
        laptopRepository.save(laptop2);
        laptopRepository.save(laptop3);
        laptopRepository.save(laptop4);
        laptopRepository.save(laptop5);
        
        System.out.println("Sample laptop data initialized successfully!");
    }
}
