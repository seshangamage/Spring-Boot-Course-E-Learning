package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("laptop-service", r -> {
                    logger.info("Configuring route for laptop-service");
                    return r.path("/laptop/**")
                            .filters(f -> f.circuitBreaker(config -> config
                                    .setName("laptopCircuitBreaker")
                                    .setFallbackUri("forward:/fallback")))
                            .uri("lb://laptop-service");
                })
                .route("desktop-service", r -> {
                    logger.info("Configuring route for desktop-service");
                    return r.path("/desktop/**")
                            .filters(f -> f.circuitBreaker(config -> config
                                    .setName("desktopCircuitBreaker")
                                    .setFallbackUri("forward:/fallback")))
                            .uri("lb://desktop-service");
                })
                .route("mobile-phone-service", r -> {
                    logger.info("Configuring route for mobile-phone-service");
                    return r.path("/mobile/**")
                            .filters(f -> f.circuitBreaker(config -> config
                                    .setName("mobileCircuitBreaker")
                                    .setFallbackUri("forward:/fallback")))
                            .uri("lb://mobile-phone-service");
                })
                .build();
    }
}
