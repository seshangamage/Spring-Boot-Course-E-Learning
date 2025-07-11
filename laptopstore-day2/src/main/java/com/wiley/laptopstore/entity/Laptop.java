package com.wiley.laptopstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "laptops")
public class Laptop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(columnDefinition = "TEXT")
    private String info;
    
    @Column(nullable = false)
    private String processor;
    
    @Column(name = "image_path")
    private String imagePath;
    
    // Default constructor
    public Laptop() {}
    
    // Constructor with parameters
    public Laptop(String name, String brand, String category, BigDecimal price, String info, String processor, String imagePath) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.info = info;
        this.processor = processor;
        this.imagePath = imagePath;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getInfo() {
        return info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    public String getProcessor() {
        return processor;
    }
    
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Override
    public String toString() {
        return "Laptop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", info='" + info + '\'' +
                ", processor='" + processor + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
