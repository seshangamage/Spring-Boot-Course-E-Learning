package com.example.laptopstore.api.service;

import com.example.laptopstore.api.entity.JwtToken;
import com.example.laptopstore.api.entity.User;
import com.example.laptopstore.api.repository.JwtTokenRepository;
import com.example.laptopstore.api.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JwtTokenService {
    
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;
    
    @Value("${jwt.max-tokens-per-user:5}")
    private int maxTokensPerUser;
    
    @Autowired
    public JwtTokenService(JwtTokenRepository jwtTokenRepository, JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    /**
     * Generate and save JWT token
     */
    public JwtToken generateAndSaveToken(User user, String ipAddress, String userAgent) {
        // Clean up old tokens for user if they exceed the limit
        cleanupExcessTokensForUser(user);
        
        // Generate JWT token
        String tokenValue = jwtTokenUtil.generateToken(user.getUsername(), user.getRole().name());
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtExpiration);
        
        // Create and save token entity
        JwtToken jwtToken = new JwtToken(tokenValue, user, expiresAt);
        jwtToken.setIpAddress(ipAddress);
        jwtToken.setUserAgent(userAgent);
        jwtToken.setTokenType(JwtToken.TokenType.ACCESS_TOKEN);
        
        return jwtTokenRepository.save(jwtToken);
    }
    
    /**
     * Validate token and check if it exists in database
     */
    public boolean validateToken(String tokenValue) {
        // First check JWT validity
        if (!jwtTokenUtil.validateToken(tokenValue)) {
            return false;
        }
        
        // Then check if token exists in database and is not revoked
        Optional<JwtToken> tokenOpt = jwtTokenRepository.findValidTokenByValue(tokenValue, LocalDateTime.now());
        return tokenOpt.isPresent();
    }
    
    /**
     * Get token details from database
     */
    public Optional<JwtToken> getTokenDetails(String tokenValue) {
        return jwtTokenRepository.findByTokenValue(tokenValue);
    }
    
    /**
     * Revoke a specific token
     */
    public void revokeToken(String tokenValue) {
        jwtTokenRepository.revokeToken(tokenValue, LocalDateTime.now());
    }
    
    /**
     * Revoke all tokens for a user
     */
    public void revokeAllUserTokens(User user) {
        jwtTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());
    }
    
    /**
     * Get all valid tokens for a user
     */
    public List<JwtToken> getValidTokensForUser(User user) {
        return jwtTokenRepository.findValidTokensByUser(user, LocalDateTime.now());
    }
    
    /**
     * Get recent tokens for a user
     */
    public List<JwtToken> getRecentTokensForUser(User user, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return jwtTokenRepository.findRecentTokensByUser(user, since);
    }
    
    /**
     * Count valid tokens for user
     */
    public long countValidTokensForUser(User user) {
        return jwtTokenRepository.countValidTokensByUser(user, LocalDateTime.now());
    }
    
    /**
     * Clean up excess tokens for user (keep only the most recent ones)
     */
    private void cleanupExcessTokensForUser(User user) {
        List<JwtToken> validTokens = getValidTokensForUser(user);
        
        if (validTokens.size() >= maxTokensPerUser) {
            // Sort by issued date and revoke the oldest tokens
            validTokens.sort((t1, t2) -> t1.getIssuedAt().compareTo(t2.getIssuedAt()));
            
            int tokensToRevoke = validTokens.size() - maxTokensPerUser + 1;
            for (int i = 0; i < tokensToRevoke; i++) {
                JwtToken oldToken = validTokens.get(i);
                oldToken.revoke();
                jwtTokenRepository.save(oldToken);
            }
        }
    }
    
    /**
     * Get tokens by IP address
     */
    public List<JwtToken> getTokensByIpAddress(String ipAddress) {
        return jwtTokenRepository.findByIpAddress(ipAddress);
    }
    
    /**
     * Get expired tokens
     */
    public List<JwtToken> getExpiredTokens() {
        return jwtTokenRepository.findExpiredTokens(LocalDateTime.now());
    }
    
    /**
     * Get tokens expiring soon
     */
    public List<JwtToken> getTokensExpiringSoon(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(hours);
        return jwtTokenRepository.findTokensExpiringSoon(now, threshold);
    }
    
    /**
     * Scheduled task to clean up expired tokens
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        // Delete tokens that expired more than 24 hours ago
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        jwtTokenRepository.deleteExpiredTokens(cutoffDate);
    }
    
    /**
     * Get token statistics
     */
    public TokenStatistics getTokenStatistics() {
        long totalTokens = jwtTokenRepository.count();
        long expiredTokens = jwtTokenRepository.findExpiredTokens(LocalDateTime.now()).size();
        long validTokens = totalTokens - expiredTokens;
        
        return new TokenStatistics(totalTokens, validTokens, expiredTokens);
    }
    
    /**
     * Token statistics class
     */
    public static class TokenStatistics {
        private final long totalTokens;
        private final long validTokens;
        private final long expiredTokens;
        
        public TokenStatistics(long totalTokens, long validTokens, long expiredTokens) {
            this.totalTokens = totalTokens;
            this.validTokens = validTokens;
            this.expiredTokens = expiredTokens;
        }
        
        public long getTotalTokens() { return totalTokens; }
        public long getValidTokens() { return validTokens; }
        public long getExpiredTokens() { return expiredTokens; }
    }
}
