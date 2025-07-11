package com.wiley.laptopstore.repository;

import com.wiley.laptopstore.entity.Cart;
import com.wiley.laptopstore.entity.CartItem;
import com.wiley.laptopstore.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Find a cart item by cart and laptop
     */
    Optional<CartItem> findByCartAndLaptop(Cart cart, Laptop laptop);
    
    /**
     * Delete all cart items for a specific cart
     */
    void deleteByCart(Cart cart);
}
