package com.desktop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DesktopBackendApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesktopBackendApiApplication.class, args);
    }
}
