package com.example.laptopstore.api.entity;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Laptop {
    
    private Long id;
    
    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;
    
    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Processor is required")
    @Size(max = 100, message = "Processor must not exceed 100 characters")
    private String processor;
    
    @NotNull(message = "RAM is required")
    @Min(value = 1, message = "RAM must be at least 1 GB")
    private Integer ramSizeGB;
    
    @NotNull(message = "Storage is required")
    @Min(value = 1, message = "Storage must be at least 1 GB")
    private Integer storageSizeGB;
    
    @Size(max = 50, message = "Storage type must not exceed 50 characters")
    private String storageType; // SSD, HDD, etc.
    
    @Size(max = 50, message = "Screen size must not exceed 50 characters")
    private String screenSize;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Laptop() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Laptop(String brand, String model, BigDecimal price, String description, 
                  String processor, Integer ramSizeGB, Integer storageSizeGB) {
        this();
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.description = description;
        this.processor = processor;
        this.ramSizeGB = ramSizeGB;
        this.storageSizeGB = storageSizeGB;
    }
    
    // Method to update timestamps
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProcessor() {
        return processor;
    }
    
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
    public Integer getRamSizeGB() {
        return ramSizeGB;
    }
    
    public void setRamSizeGB(Integer ramSizeGB) {
        this.ramSizeGB = ramSizeGB;
    }
    
    public Integer getStorageSizeGB() {
        return storageSizeGB;
    }
    
    public void setStorageSizeGB(Integer storageSizeGB) {
        this.storageSizeGB = storageSizeGB;
    }
    
    public String getStorageType() {
        return storageType;
    }
    
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
    
    public String getScreenSize() {
        return screenSize;
    }
    
    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Laptop{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                ", processor='" + processor + '\'' +
                ", ramSizeGB=" + ramSizeGB +
                ", storageSizeGB=" + storageSizeGB +
                '}';
    }
}
