package com.optculture.launchpad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
	
	Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@GetMapping("/welcome")
	public String welcome() throws Exception{

		logger.debug("------< OC3.0 Coming soon... >------");

		//template.convertAndSend(MessagingConfigs.EXCHANGE, MessagingConfigs.ROUTING_KEY, "");

		return "<h1>OC3.0 Coming soon...</h1>";
	}


}

