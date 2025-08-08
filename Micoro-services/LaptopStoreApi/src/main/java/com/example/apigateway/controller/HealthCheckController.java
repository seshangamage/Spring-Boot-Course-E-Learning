package com.example.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    private final WebClient.Builder webClientBuilder;

    public HealthCheckController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/health")
    public ResponseEntity<?> checkHealth() {
        Map<String, Object> status = new HashMap<>();
        status.put("gateway", "UP");
        
        discoveryClient.getServices().forEach(serviceId -> {
            logger.info("Checking service: {}", serviceId);
            status.put(serviceId, discoveryClient.getInstances(serviceId).isEmpty() ? "DOWN" : "UP");
            
            discoveryClient.getInstances(serviceId).forEach(instance -> {
                logger.info("Instance found - ServiceId: {}, Host: {}, Port: {}", 
                    serviceId, instance.getHost(), instance.getPort());
            });
        });
        
        return ResponseEntity.ok(status);
    }

    @GetMapping("/test-routes")
    public Mono<Map<String, String>> testRoutes() {
        Map<String, String> results = new HashMap<>();
        
        Mono<String> laptopTest = webClientBuilder.build()
            .get()
            .uri("lb://laptop-service/")
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> "UP")
            .onErrorResume(e -> {
                logger.error("Error testing laptop-service: ", e);
                return Mono.just("DOWN: " + e.getMessage());
            });
            
        results.put("laptop-service", "testing...");
        
        return laptopTest
            .map(laptopStatus -> {
                results.put("laptop-service", laptopStatus);
                return results;
            });
    }
}
