package com.optculture.app.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.optculture.app.repositories.ContactRepository;
import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.entities.contact.Contact;

@Service
public class EmailSubscribeService {

	
	Logger logger = LoggerFactory.getLogger(EmailSubscribeService.class);
	
	@Autowired
	RabbitTemplate  rabbitTemplate;
	
	
	@Value("${launchpadURL}")
	private String requestUrl;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//emailSubscribeService.processUnsubscribeEvent(email,campId,userId,cId,crId);

	public String processUnsubscribeEvent(String encryptedParam) {
	//String email,String campId,String userId,String cId,String crId ) {
		// TODO Auto-generated method stub
		try {
	//	String decodeParams = EncryptDecryptUrlParams.decrypt(encryptedParam.trim());
		
	byte[] decodedBytes = Base64.decodeBase64(encryptedParam);
	String decodedString = new String(decodedBytes);
			
	String[] paramList = decodedString.split("&");
		String email = "";
		Long campaignId = 0L;
		Long reportId = 0L;
		Long userId = 0L;
		Long contactId = 0L;
		
		//https://test.optculture.app/unsubscribe?
		//emailId=|^emailId^|&
		//campId=|^campId^|&
		//crId=|^crId^|&
		//userId=|^userId^|

		
		for(String param : paramList ) {
			
			String orginalParams[]  = param.split("=");
			
			if(orginalParams[0].contains("email")) {
			 email = orginalParams[1];
			 logger.debug("email : "+email);
			}else if(orginalParams[0].contains("campId")) {
				campaignId  = Long.valueOf(orginalParams[1]);
				 logger.debug("campaignId : "+campaignId);
			}else if(orginalParams[0].contains("crId")) {
				reportId  = Long.valueOf(orginalParams[1]);
				 logger.debug("reportId : "+reportId);

			}else if(orginalParams[0].contains("userId")) {
				userId  = Long.valueOf(orginalParams[1]);
				 logger.debug("userId : "+userId);

			}else if(orginalParams[0].contains("cId")) {
				 contactId = Long.valueOf(orginalParams[1]);
				 logger.debug("cId : "+contactId);

			}
		}
		
		
		Contact contact = contactRepository.findOneByContactIdAndUserId(contactId, userId);
		if(contact != null && !contact.getEmailStatus().equalsIgnoreCase("UnSubscribed")) {
		contact.setEmailStatus("UnSubscribed");
		
		contactRepository.save(contact);
		
//		campaignId = Long.parseLong(campId);
//		reportId = Long.parseLong(crId);
//		Long user = Long.parseLong(userId);
		
		
			
		logger.info("consumed for action "+"Unsubscribe", email+","+campaignId+","+reportId+","+userId);

//			
			pushEventToCommunicationEvent("UnSubscribed",email,campaignId,reportId,userId,contactId);
			
	//	}
		}

		
		
		return null;
	}catch(Exception e) {
		e.printStackTrace();
		return null;
	}
	}

	private ObjectMapper getObjectMapper() {
	
		 // this code is written to deserialize the localDataTime.
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
	}

	private void pushEventToCommunicationEvent(String action,String email, Long campaignId, Long reportId, Long userId,Long contactId) {
		// TODO Auto-generated method stub
		
try {
	
		
	//	rabbitTemplate = messageConfig.template(connection);
		logger.info("Pushing "+action+" in communication event table.");
		CommunicationEvent event = new CommunicationEvent(reportId,campaignId, email, action, LocalDateTime.now(), "", "Email", userId, contactId);
		logger.info("event : "+event.toString());
		String endPoint  = "/ping/createEvent";
		String url =  requestUrl+endPoint;
		 try {
			 
				HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.APPLICATION_JSON);
		       
	            URI postUrl= new URI(url);
	            logger.info("URI is :"+postUrl);
	            RestTemplate restTemplate=new RestTemplate();
	            
	            String requestString = getObjectMapper().writeValueAsString(event);
	           
	    		HttpEntity<String> requestEntity = new HttpEntity<>(requestString, headers);

	    		ResponseEntity<String> response = restTemplate.postForEntity(postUrl,requestEntity,String.class);
	            System.out.println("response is "+response);
	           
	        }
	        catch (Exception e){
	            logger.info("Exception while creating event for "+e);
	        }

		//rabbitTemplate.convertAndSend(MessageConfiguration.COMMUNICATION_EVENT_EXCHANGE, MessageConfiguration.COMMUNICATION_EVENT_ROUTING_KEY , ce);
		//logger.info("consumed for action "+action, email);
		//rabbitTemplate.convertAndSend("communication_event_exchange", "communication_event_key",event);
		logger.info("consumed for action "+action, email);

}catch(Exception e) {
	e.printStackTrace();
}
	}
	

}
