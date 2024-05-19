package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optculture.launchpad.configs.CommunicationEventHandler;
import com.optculture.launchpad.repositories.CommunicationTemplateRepository;
import com.optculture.launchpad.repositories.UserOrganizationRepository;
import com.optculture.launchpad.repositories.UserRepository;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.org.UserOrganization;
import com.optculture.shared.util.Constants;


@Component("Interact")
public class InteractDispatcher implements ChannelDispatcher {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserOrganizationRepository userOrganizationRepository;
	
	@Autowired
	CommunicationEventHandler communicationEventHandler;

	@Autowired
	CommunicationTemplateRepository communicationTemplateRepository ;

	Logger logger = LoggerFactory.getLogger(InteractDispatcher.class);

	private final RestTemplate restTemplate = new RestTemplate();


	@Override
	public void dispatch(String jsonContent, Contact contactobj, ChannelAccount channelAccount,
			ChannelSetting channelSetting, UserChannelSetting userChannelSetting, Communication communicationObj, CustomCommunication in)
					throws IOException {

		try {

			logger.debug("----------< Entered InteractDispatcher >----------");

			String apiEndPoint = channelSetting.getEndPoint(); //https://api.interakt.ai/v1/public/message/
			String accessToken = channelAccount.getApiKey();

			CommunicationEvent communicationEvent = null;

			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", "Basic "+accessToken);	//Basic NlI2cjF0amp0a0dyMld0Y1dMaE5QbVNEckFvSkFacW43bjdfhERodFNCMDo=



			// Create an HttpEntity with headers and the request body
			HttpEntity<String> requestEntity = new HttpEntity<>(jsonContent, headers);

			// Send the POST request
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiEndPoint, requestEntity, String.class);

			String response = responseEntity.getBody();
			logger.debug("Response from Server:\n" + response);

			String status = null;

			if (responseEntity.getStatusCode() == HttpStatus.CREATED) {//for Interact success code = 201
				logger.debug("Success : HTTP success code : " +responseEntity.getStatusCode().value());
				status = "Sent";
			} else {
				logger.debug("Failed : HTTP error code : " + responseEntity.getStatusCode().value());
				status = "UnSent";
			}

			//create the event object and Publish to rabbitMQ
			communicationEvent = new CommunicationEvent(in.getCommReportId(),in.getCampId(),contactobj.getMobilePhone(),status,LocalDateTime.now(),
					Constants.STRING_NILL,communicationObj.getChannelType(),contactobj.getUserId(),contactobj.getContactId());

			communicationEventHandler.createCommunicationEvent(communicationEvent);

		} catch (Exception e) {
			logger.error("Exception :: ", e);
		}


	}//dispatch

	@Override
	public String beforeMailMerge(Contact contact, Communication communication, ChannelAccount channelAccount, 
			UserChannelSetting userChannelSettingObj, CustomCommunication in){

		//convert message content to json, which is supposed to submit to Interact gateway
		String finalJson = convertToJson(contact, communication, channelAccount,userChannelSettingObj, in);

		return finalJson;

	}//beforeMailMerge

	public String convertToJson(Contact contact, Communication communication, ChannelAccount channelAccount, 
			UserChannelSetting userChannelSettingObj, CustomCommunication in){
		
		logger.debug("------< raw message content to json conversion started >------");

		//Sample json
		String json = "{\"countryCode\": \"<<countryCarrier>>\",\"phoneNumber\": \"<<mobileNo>>\",\"callbackData\": \"<<trackingId>>\",\"type\": \"Template\","
				+ "\"template\": {\"name\": \"<<templateName>>\",\"languageCode\": \"en\","
				+ "\"headerValues\": <<headerValues>>,"
				+ "\"bodyValues\": <<bodyValues>>}}";

		CommunicationTemplate communicationTemplate = null;
		communicationTemplate = communicationTemplateRepository.findByTemplateId(communication.getTemplateId());
		if(communicationTemplate==null) {
			logger.info("communicationTemplate is not found...");
			return null;
		}

		if(channelAccount.getApiKey()==null  || contact.getMobilePhone()==null || communicationTemplate.getTemplateName()==null) {
			logger.info("------< One of the requiered parameter is not found >-------");
			return null;
		}
		
		String mobileNo = contact.getMobilePhone().trim();
		User user = userRepository.findByuserId(communication.getUserId());

		//strip the country carrier if present
		String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
		
		//user.getUserOrganization().getgetMinNumberOfDigits() throwing LazyInitializationException
		UserOrganization userOrganization = userOrganizationRepository.findByUserOrgId(user.getUserOrganization().getUserOrgId());
		
		if (mobileNo.startsWith(countryCarrier) && mobileNo.length() > userOrganization.getMinNumberOfDigits()) {

			mobileNo = mobileNo.substring(countryCarrier.length());
			logger.debug("Mobile Number after trimming country carrier is = "+mobileNo);
		}
		
		json = json.replace("<<countryCarrier>>", user.getCountryCarrier()+Constants.STRING_NILL)
				.replace("<<mobileNo>>", mobileNo)
				.replace("<<templateName>>", communicationTemplate.getTemplateName())
				.replace("<<trackingId>>", 	in.getCommReportId() + Constants.DELIMETER_DOUBLECOLON + 
											in.getCampId() + Constants.DELIMETER_DOUBLECOLON + 
											contact.getContactId() + Constants.DELIMETER_DOUBLECOLON + 
											contact.getUserId());//pass cr_id::camp_id::cid::user_id to track delivery reports

		String templateHeaderType = communicationTemplate.getMsgType();//TEXT,IMAGE,VIDEO,DOCUMENT
		String mergeTags = communication.getPlaceholderMappings();//${coupon.CC_13232}||${contact.firstName ! 'N/A'}
		String mergeTagsArr[] = mergeTags != null && !mergeTags.isEmpty() ? mergeTags.split("\\|\\|", -1) : new String[0];

		//Gson gson = new Gson();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		
		//generate the header and body components
		try {
			List<String> headerValues = generateHeaderValues(templateHeaderType, communication.getMediaUrl());
			List<String> bodyValues = generateBodyValues(mergeTagsArr);
			String headerValuesStr = gson.toJson(headerValues);
			String bodyValuesStr = gson.toJson(bodyValues);
			json = json.replace("<<headerValues>>", headerValuesStr);
			json = json.replace("<<bodyValues>>", bodyValuesStr);

		} catch (Exception e) {
			logger.error("Something went wrong while generating components for Interact :: ",e);
		}

		logger.info("final json content is : \n"+json);

		return json;
	}//convertToJson

	private List<String> generateHeaderValues(String templateHeaderType, String mediaUrl) throws Exception {

		// Create a header component
		List<String> headerValues = new ArrayList<String>();

		if (templateHeaderType!=null && !templateHeaderType.equalsIgnoreCase("TEXT") && mediaUrl != null) {

			headerValues = Arrays.asList(mediaUrl);

		}
		return headerValues;
	}//generateHeaderValues

	private List<String> generateBodyValues(String[] phArr) throws Exception {

		// Create a body component
		List<String> bodyValues = new ArrayList<String>();
		try {

			if(phArr.length>0) {
				bodyValues = Arrays.asList(phArr);
			}

		} catch (Exception e) {
			logger.error("Exception while creating body variables creation :: ",e);
		}

		return bodyValues;
	}//generateBodyComponents
	
	/*
	{
	    "countryCode": "+91",
	    "phoneNumber": "XXXXXXXXXX",
	    "callbackData": "some text here",
	    "type": "Template",
	    "template": {
	        "name": "sample_flight_confirmation",
	        "languageCode": "en",
	        "headerValues": [
	            "https://abc.pdf"
	        ],
	        "bodyValues": [
	            "Hyd",
	            "DEL",
	            "20-April-2024"
	        ]
	    }
	}
	 */
	@Override
	public DispatchStatus dispatchTestMsg(String finalContent, String recepient, ChannelAccount channelAccount,
			ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj,
			CustomCommunication in) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
