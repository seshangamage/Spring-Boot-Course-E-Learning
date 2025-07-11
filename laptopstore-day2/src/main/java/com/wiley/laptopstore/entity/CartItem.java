package com.wiley.laptopstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laptop_id")
    private Laptop laptop;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public CartItem() {
        this.createdAt = LocalDateTime.now();
    }
    
    public CartItem(Cart cart, Laptop laptop, int quantity) {
        this();
        this.cart = cart;
        this.laptop = laptop;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cart getCart() {
        return cart;
    }
    
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    
    public Laptop getLaptop() {
        return laptop;
    }
    
    public void setLaptop(Laptop laptop) {
        this.laptop = laptop;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public BigDecimal getSubtotal() {
        return laptop.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
    
    public void increaseQuantity() {
        this.quantity++;
    }
    
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
}
