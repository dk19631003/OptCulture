package com.optculture.launchpad.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.optculture.shared.entities.org.User;

public class Utility {
	
	 Logger  logger = LoggerFactory.getLogger(Utility.class);

	public  void sendFailureMessageToSupport(String emailIdOfSupport,String reasonOfFailure,User User) {
		try {
		logger.info("Reason of failure..."+reasonOfFailure);
		
		
	}catch(Exception e) {
		logger.error("Exception while sending failure message to support");
	}

}
}
