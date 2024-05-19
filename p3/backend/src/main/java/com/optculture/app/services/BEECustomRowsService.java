package com.optculture.app.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.optculture.app.repositories.ApplicationPropertiesRepository;
import com.optculture.app.repositories.UserDesignedCustomRowsRepository;
import com.optculture.shared.entities.communication.email.UserDesignedCustomRows;
import com.optculture.shared.entities.config.ApplicationProperties;

@Component
public class BEECustomRowsService {
	
	private final Logger logger  = LoggerFactory.getLogger(BEECustomRowsService.class);
	
	@Autowired
	ApplicationPropertiesRepository applicationPropertiesRepository;
	
	@Autowired
	UserDesignedCustomRowsRepository  userDesignedCustomRowsRepository;

	public String getCustomRowsByType(String name) {
		// TODO Auto-generated method stub
	//	JSONObject  resultJson = null;
		logger.debug(" while getting custom row from application properties : "+name);

		try {
		String jsonRowContent = applicationPropertiesRepository.findByKey(name);
		
		  return jsonRowContent;
		}catch(Exception e) {
			logger.error("Caught Exception while getting custom row from application properties : "+e);
			return null;
		}
	}

	public String getUserDesignedCustomRowsByUser(Long userId, String name) {
		// TODO Auto-generated method stub
		JSONObject resultJson = null;
		try {
			logger.debug(" while getting custom row from user designed rows : "+name);
			UserDesignedCustomRows userDesignedCustomRows =  userDesignedCustomRowsRepository.findByUserIdAndTemplateName(userId, name);
			if(userDesignedCustomRows != null)
				return userDesignedCustomRows.getRowJsonData();
			
			return null;
		}catch(Exception e) {
			logger.error("Caught Exception while getting userdesigned row : "+e);
			return null;

		}
	}
	
	
	

}
