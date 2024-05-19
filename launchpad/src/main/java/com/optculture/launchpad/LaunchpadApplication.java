package com.optculture.launchpad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.optculture.shared.entities")
public class LaunchpadApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaunchpadApplication.class, args);
	}

}
