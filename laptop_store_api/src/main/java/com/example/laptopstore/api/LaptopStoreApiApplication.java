package com.example.laptopstore.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LaptopStoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptopStoreApiApplication.class, args);
	}

}
