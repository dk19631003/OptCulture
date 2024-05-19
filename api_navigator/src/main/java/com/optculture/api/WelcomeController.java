package com.optculture.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

	Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@GetMapping("/welcome")
	public String welcome() throws Exception{
		
		logger.info("Welcome to the API Control Tower...");

		return "<h1>Welcome to the API Control Tower...</h1>";
	}


}
