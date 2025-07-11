package com.wiley.laptopstore.controller;

import com.wiley.laptopstore.entity.Cart;
import com.wiley.laptopstore.entity.Laptop;
import com.wiley.laptopstore.service.CartService;
import com.wiley.laptopstore.service.LaptopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private LaptopService laptopService;
    
    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam("laptopId") Long laptopId,
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            logger.info("Adding laptop {} to cart with quantity {}", laptopId, quantity);
            
            String sessionId = session.getId();
            Optional<Laptop> laptopOpt = laptopService.findById(laptopId);
            
            if (laptopOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Laptop not found!");
                return "redirect:/";
            }
            
            Laptop laptop = laptopOpt.get();
            cartService.addToCart(sessionId, laptop, quantity);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                laptop.getName() + " has been added to your cart!");
            
            logger.info("Successfully added laptop {} to cart", laptopId);
            
        } catch (Exception e) {
            logger.error("Error adding laptop to cart: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to add laptop to cart. Please try again.");
        }
        
        return "redirect:/";
    }
    
    /**
     * View cart
     */
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        logger.info("Viewing cart for session: {}", session.getId());
        
        String sessionId = session.getId();
        Optional<Cart> cartOpt = cartService.getCartBySessionId(sessionId);
        
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cart.getItems());
            model.addAttribute("totalPrice", cart.getTotalPrice());
            model.addAttribute("totalItems", cart.getTotalItems());
            logger.info("Cart found with {} items, total: {}", 
                       cart.getTotalItems(), cart.getTotalPrice());
        } else {
            model.addAttribute("cart", null);
            model.addAttribute("cartItems", null);
            logger.info("No cart found for session");
        }
        
        return "cart";
    }
    
    /**
     * Update item quantity in cart
     */
    @PostMapping("/update")
    public String updateCartItem(@RequestParam("laptopId") Long laptopId,
                                @RequestParam("quantity") int quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            logger.info("Updating cart item {} to quantity {}", laptopId, quantity);
            
            String sessionId = session.getId();
            Optional<Laptop> laptopOpt = laptopService.findById(laptopId);
            
            if (laptopOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Laptop not found!");
                return "redirect:/cart/view";
            }
            
            Laptop laptop = laptopOpt.get();
            cartService.updateQuantity(sessionId, laptop, quantity);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully!");
            logger.info("Successfully updated cart item {}", laptopId);
            
        } catch (Exception e) {
            logger.error("Error updating cart item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update cart. Please try again.");
        }
        
        return "redirect:/cart/view";
    }
    
    /**
     * Remove item from cart
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("laptopId") Long laptopId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            logger.info("Removing laptop {} from cart", laptopId);
            
            String sessionId = session.getId();
            Optional<Laptop> laptopOpt = laptopService.findById(laptopId);
            
            if (laptopOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Laptop not found!");
                return "redirect:/cart/view";
            }
            
            Laptop laptop = laptopOpt.get();
            cartService.removeFromCart(sessionId, laptop);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                laptop.getName() + " has been removed from your cart!");
            
            logger.info("Successfully removed laptop {} from cart", laptopId);
            
        } catch (Exception e) {
            logger.error("Error removing laptop from cart: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to remove laptop from cart. Please try again.");
        }
        
        return "redirect:/cart/view";
    }
    
    /**
     * Clear entire cart
     */
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Clearing cart for session: {}", session.getId());
            
            String sessionId = session.getId();
            cartService.clearCart(sessionId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully!");
            logger.info("Successfully cleared cart");
            
        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to clear cart. Please try again.");
        }
        
        return "redirect:/cart/view";
    }
    
    /**
     * Get cart item count (for AJAX requests)
     */
    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount(HttpSession session) {
        String sessionId = session.getId();
        return cartService.getCartItemCount(sessionId);
    }
}
