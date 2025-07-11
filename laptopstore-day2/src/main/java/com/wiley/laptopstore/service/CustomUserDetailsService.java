package com.wiley.laptopstore.service;

import com.wiley.laptopstore.entity.User;
import com.wiley.laptopstore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        // Check if it's the admin user (hardcoded for backwards compatibility)
        if ("admin".equals(username)) {
            logger.debug("Loading admin user");
            return org.springframework.security.core.userdetails.User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .authorities("ROLE_ADMIN")
                    .build();
        }
        
        // Try to find user in database
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);
        
        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        User user = userOpt.get();
        logger.debug("User found: {}", user.getUsername());
        
        // Check if user is enabled
        if (!user.isEnabled()) {
            logger.warn("User is disabled: {}", username);
            throw new UsernameNotFoundException("User is disabled: " + username);
        }
        
        // Build authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoles() != null) {
            for (User.Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            }
        }
        
        logger.debug("User authorities: {}", authorities);
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}
