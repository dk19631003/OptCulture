package com.optculture.app.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.app.repositories.CommunicationTemplateRepository;
import com.optculture.app.repositories.MyTemplatesRepository;
import com.optculture.shared.entities.communication.email.MyTemplates;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.util.Constants;
@Service
public class EmailTemplateService {
	
    Logger logger = LoggerFactory.getLogger(EmailTemplateService.class);

	@Autowired 
	private CommunicationTemplateRepository communicationTemplateRepository;
	
	
	
//	public Page<TemplateDto> getAllUserTemplates(User user,String templateName,int pageNumber,int pageSize){
//		try {
//			if(templateName.equalsIgnoreCase("--"))
//				templateName = null; // made null to retrieve all the template. else it if retrieve only required template. 
//		Page<TemplateDto> result =  communicationTemplateRepository.findByUserAndTemplateNameOrderByModifiedDateDesc(user.getUserId(),templateName,Constants.TYPE_EMAIL_CAMPAIGN,"beeEditor",PageRequest.of(pageNumber,pageSize));
//		
//		logger.info("result set is : "+result.toString());
//
//		if(!result.isEmpty()) {
//			return result;
//		}
//		return null;
//	}catch(Exception e) {
//		e.printStackTrace();
//		return null;
//	}
//	}
	
	// get the system templates. 
	
}
