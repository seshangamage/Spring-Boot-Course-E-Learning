package com.example.laptopstore.api.repository;

import com.example.laptopstore.api.entity.JwtToken;
import com.example.laptopstore.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    
    /**
     * Find token by token value
     */
    Optional<JwtToken> findByTokenValue(String tokenValue);
    
    /**
     * Find valid token by token value
     */
    @Query("SELECT t FROM JwtToken t WHERE t.tokenValue = :tokenValue AND t.revoked = false AND t.expiresAt > :now")
    Optional<JwtToken> findValidTokenByValue(@Param("tokenValue") String tokenValue, @Param("now") LocalDateTime now);
    
    /**
     * Find all tokens for a user
     */
    List<JwtToken> findByUser(User user);
    
    /**
     * Find valid tokens for a user
     */
    @Query("SELECT t FROM JwtToken t WHERE t.user = :user AND t.revoked = false AND t.expiresAt > :now")
    List<JwtToken> findValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Find tokens by type
     */
    List<JwtToken> findByTokenType(JwtToken.TokenType tokenType);
    
    /**
     * Find expired tokens
     */
    @Query("SELECT t FROM JwtToken t WHERE t.expiresAt <= :now")
    List<JwtToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find tokens expiring soon
     */
    @Query("SELECT t FROM JwtToken t WHERE t.expiresAt BETWEEN :now AND :threshold AND t.revoked = false")
    List<JwtToken> findTokensExpiringSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    /**
     * Revoke all tokens for a user
     */
    @Modifying
    @Transactional
    @Query("UPDATE JwtToken t SET t.revoked = true, t.revokedAt = :revokedAt WHERE t.user = :user AND t.revoked = false")
    void revokeAllUserTokens(@Param("user") User user, @Param("revokedAt") LocalDateTime revokedAt);
    
    /**
     * Revoke specific token
     */
    @Modifying
    @Transactional
    @Query("UPDATE JwtToken t SET t.revoked = true, t.revokedAt = :revokedAt WHERE t.tokenValue = :tokenValue")
    void revokeToken(@Param("tokenValue") String tokenValue, @Param("revokedAt") LocalDateTime revokedAt);
    
    /**
     * Delete expired tokens
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM JwtToken t WHERE t.expiresAt <= :cutoffDate")
    void deleteExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Count valid tokens for user
     */
    @Query("SELECT COUNT(t) FROM JwtToken t WHERE t.user = :user AND t.revoked = false AND t.expiresAt > :now")
    long countValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Find tokens by IP address
     */
    List<JwtToken> findByIpAddress(String ipAddress);
    
    /**
     * Find recent tokens for user
     */
    @Query("SELECT t FROM JwtToken t WHERE t.user = :user AND t.issuedAt >= :since ORDER BY t.issuedAt DESC")
    List<JwtToken> findRecentTokensByUser(@Param("user") User user, @Param("since") LocalDateTime since);
}
