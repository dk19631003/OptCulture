package com.optculture.launchpad.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfiguration {
	
	/*
	 * @Bean EmailDeliveryEventService EmailDeliveryEventService() { return new
	 * EmailDeliveryEventService(); }
	 * 
*/
    
    @Bean
    com.optculture.launchpad.utility.Utility Utility()
	{
		return new com.optculture.launchpad.utility.Utility();
		}
	}
