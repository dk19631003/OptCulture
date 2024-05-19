package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.optculture.launchpad.configs.CommunicationEventHandler;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.util.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("Equence")
public class EquenceSmsDispatcher implements ChannelDispatcher {

	Logger logger = LoggerFactory.getLogger(EquenceSmsDispatcher.class);

	@Autowired
	CommunicationEventHandler communicationEventHandler;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
	public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in)
			throws IOException {
	
		
		logger.trace("entering Equence SMS dispatcher");

    	// need to add Escaping HtmlUtils
		CommunicationEvent communicationEvent;

		String url = channelSetting.getEndPoint();		        
        
		HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
		// Create an instance of your custom request body object
		PrepareEquenceSingleJsonRequest prepareSingleJson = new PrepareEquenceSingleJsonRequest();
		prepareSingleJson.setUsername(channelAccount.getAccountName());
		prepareSingleJson.setPassword(channelAccount.getAccountPwd());
		prepareSingleJson.setFrom(usechannelSetting.getSenderId());
		prepareSingleJson.setPeId(channelAccount.getApiKey());           
		prepareSingleJson.setTo(contactobj.getMobilePhone());
		prepareSingleJson.setText(msgContent);
		prepareSingleJson.setTmplId(communicationObj.getTemplateId().toString());   
		prepareSingleJson.setMSGID(in.getCommReportId().toString() + Constants.DELIMETER_DOUBLECOLON + communicationObj.getCommunicationId().toString() +
        		Constants.DELIMETER_DOUBLECOLON + contactobj.getUserId() + Constants.DELIMETER_DOUBLECOLON + contactobj.getContactId());
		prepareSingleJson.setCharset("auto");
		
		
		Gson gson = new Gson();


		// Convert the request body object to a JSON string
		String requestBody = gson.toJson(prepareSingleJson);
		
		logger.debug("Equence requestBody value is {} ", requestBody);


		// Create an HttpEntity with headers and the request body
		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    	
		// Send the POST request
		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			String response = responseEntity.getBody();
			int statusCode = responseEntity.getStatusCode().value();
	        String mridValue = parseJsonAndGetMrid(response);
			String mobileNumber = contactobj.getMobilePhone();
			if(mobileNumber.length()> 10) {
				mobileNumber= mobileNumber.substring(mobileNumber.length() - 10);
			}
			logger.debug("Response from Server:\n {} ",response);
		    logger.debug("mridValue value is {} ",mridValue);
			String Status= null;
			ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(response);
	        String status = jsonNode.get("response").get(0).get("status").asText();
			logger.debug("status value is {} ",status);

			Status = ("success".equals(status)) ? "Sent" : "UnSent";

	        communicationEvent = new CommunicationEvent(in.getCommReportId(),in.getCampId(),mobileNumber,Status,LocalDateTime.now(),
					   mridValue!=null ? mridValue : "",communicationObj.getChannelType(),contactobj.getUserId(),contactobj.getContactId());
			    
			communicationEventHandler.createCommunicationEvent(communicationEvent);

	        
		} catch (RestClientException e) {
			logger.debug("Exception while calling Equence API", e);
		}
	
	}
    private String parseJsonAndGetMrid(String jsonResponse) {
        
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(jsonResponse);
            return jsonNode.get("response").get(0).get("mrid").asText();
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return null;
        }
    }
			
}
