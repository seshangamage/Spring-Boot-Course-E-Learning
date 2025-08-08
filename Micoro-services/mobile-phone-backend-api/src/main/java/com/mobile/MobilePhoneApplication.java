package com.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MobilePhoneApplication {
    public static void main(String[] args) {
        SpringApplication.run(MobilePhoneApplication.class, args);
    }
}
