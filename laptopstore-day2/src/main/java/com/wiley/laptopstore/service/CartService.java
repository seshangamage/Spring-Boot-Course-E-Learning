package com.wiley.laptopstore.service;

import com.wiley.laptopstore.entity.Cart;
import com.wiley.laptopstore.entity.CartItem;
import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.repository.CartRepository;
import com.wiley.laptopstore.repository.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    /**
     * Get or create a cart for the given session ID
     */
    public Cart getOrCreateCart(String sessionId) {
        logger.info("Getting or creating cart for session: {}", sessionId);
        
        Optional<Cart> existingCart = cartRepository.findBySessionIdWithItems(sessionId);
        if (existingCart.isPresent()) {
            logger.info("Found existing cart with ID: {}", existingCart.get().getId());
            return existingCart.get();
        }
        
        Cart newCart = new Cart(sessionId);
        Cart savedCart = cartRepository.save(newCart);
        logger.info("Created new cart with ID: {}", savedCart.getId());
        return savedCart;
    }
    
    /**
     * Add a laptop to the cart
     */
    public void addToCart(String sessionId, Laptop laptop, int quantity) {
        logger.info("Adding laptop {} to cart for session: {}, quantity: {}", 
                   laptop.getId(), sessionId, quantity);
        
        Cart cart = getOrCreateCart(sessionId);
        
        // Check if laptop is already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndLaptop(cart, laptop);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            logger.info("Updated quantity for laptop {} in cart. New quantity: {}", 
                       laptop.getId(), item.getQuantity());
        } else {
            // Add new item
            CartItem newItem = new CartItem(cart, laptop, quantity);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
            logger.info("Added new item for laptop {} to cart", laptop.getId());
        }
        
        cartRepository.save(cart);
    }
    
    /**
     * Remove a laptop from the cart
     */
    public void removeFromCart(String sessionId, Laptop laptop) {
        logger.info("Removing laptop {} from cart for session: {}", laptop.getId(), sessionId);
        
        Optional<Cart> cartOpt = cartRepository.findBySessionIdWithItems(sessionId);
        if (cartOpt.isEmpty()) {
            logger.warn("No cart found for session: {}", sessionId);
            return;
        }
        
        Cart cart = cartOpt.get();
        Optional<CartItem> itemOpt = cartItemRepository.findByCartAndLaptop(cart, laptop);
        
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            cart.removeItem(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
            logger.info("Removed laptop {} from cart", laptop.getId());
        } else {
            logger.warn("Laptop {} not found in cart for session: {}", laptop.getId(), sessionId);
        }
    }
    
    /**
     * Update quantity of a laptop in the cart
     */
    public void updateQuantity(String sessionId, Laptop laptop, int quantity) {
        logger.info("Updating quantity for laptop {} in cart for session: {}, new quantity: {}", 
                   laptop.getId(), sessionId, quantity);
        
        if (quantity <= 0) {
            removeFromCart(sessionId, laptop);
            return;
        }
        
        Optional<Cart> cartOpt = cartRepository.findBySessionIdWithItems(sessionId);
        if (cartOpt.isEmpty()) {
            logger.warn("No cart found for session: {}", sessionId);
            return;
        }
        
        Cart cart = cartOpt.get();
        Optional<CartItem> itemOpt = cartItemRepository.findByCartAndLaptop(cart, laptop);
        
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            cartRepository.save(cart);
            logger.info("Updated quantity for laptop {} to {}", laptop.getId(), quantity);
        } else {
            logger.warn("Laptop {} not found in cart for session: {}", laptop.getId(), sessionId);
        }
    }
    
    /**
     * Get cart by session ID
     */
    public Optional<Cart> getCartBySessionId(String sessionId) {
        logger.info("Getting cart for session: {}", sessionId);
        return cartRepository.findBySessionIdWithItems(sessionId);
    }
    
    /**
     * Clear all items from the cart
     */
    public void clearCart(String sessionId) {
        logger.info("Clearing cart for session: {}", sessionId);
        
        Optional<Cart> cartOpt = cartRepository.findBySessionIdWithItems(sessionId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItemRepository.deleteByCart(cart);
            cart.getItems().clear();
            cartRepository.save(cart);
            logger.info("Cleared all items from cart for session: {}", sessionId);
        } else {
            logger.warn("No cart found for session: {}", sessionId);
        }
    }
    
    /**
     * Get total items count in cart
     */
    public int getCartItemCount(String sessionId) {
        Optional<Cart> cartOpt = cartRepository.findBySessionIdWithItems(sessionId);
        return cartOpt.map(Cart::getTotalItems).orElse(0);
    }
}
