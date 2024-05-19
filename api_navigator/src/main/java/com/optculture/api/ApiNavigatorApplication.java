package com.optculture.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.optculture.shared.entities")
public class ApiNavigatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiNavigatorApplication.class, args);
	}

}
