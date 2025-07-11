package com.wiley.laptopstore.repository;

import com.wiley.laptopstore.entity.Cart;
import com.wiley.laptopstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Find a cart by session ID
     */
    Optional<Cart> findBySessionId(String sessionId);
    
    /**
     * Find a cart by user
     */
    Optional<Cart> findByUser(User user);
    
    /**
     * Find a cart by session ID with items and laptops loaded
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.laptop WHERE c.sessionId = :sessionId")
    Optional<Cart> findBySessionIdWithItems(@Param("sessionId") String sessionId);
    
    /**
     * Find a cart by user with items and laptops loaded
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.laptop WHERE c.user = :user")
    Optional<Cart> findByUserWithItems(@Param("user") User user);
    
    /**
     * Delete carts older than a certain number of days
     */
    @Query("DELETE FROM Cart c WHERE c.updatedAt < :cutoffDate")
    void deleteOldCarts(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}
