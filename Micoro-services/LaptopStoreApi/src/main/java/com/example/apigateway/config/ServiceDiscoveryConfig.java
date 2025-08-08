package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableScheduling
public class ServiceDiscoveryConfig {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryConfig.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Scheduled(fixedRate = 10000)  // Log every 10 seconds
    public void logAvailableServices() {
        logger.info("Currently registered services:");
        discoveryClient.getServices().forEach(serviceId -> {
            logger.info("Service: {} - Instances: {}", serviceId, 
                discoveryClient.getInstances(serviceId).size());
        });
    }
}
