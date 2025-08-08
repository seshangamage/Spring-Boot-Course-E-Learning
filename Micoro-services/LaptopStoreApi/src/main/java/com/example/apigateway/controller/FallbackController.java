package com.example.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FallbackController {
    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);
    
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/")
    public String welcome() {
        List<String> services = discoveryClient.getServices();
        logger.info("Available services in Eureka: {}", services);
        return "Welcome to the Device Store API Gateway. Available endpoints: /laptop/**, /desktop/**, /mobile/**\n" +
               "Registered services: " + services;
    }

    @GetMapping("/fallback")
    public String fallback() {
        List<String> services = discoveryClient.getServices();
        logger.warn("Fallback triggered. Available services: {}", services);
        return "Service is currently unavailable. Please ensure the requested service is running and registered with Eureka.\n" +
               "Currently registered services: " + services;
    }
}
