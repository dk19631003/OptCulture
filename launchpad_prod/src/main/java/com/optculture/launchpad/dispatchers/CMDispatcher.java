package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.optculture.launchpad.dto.Components;
import com.optculture.launchpad.dto.Parameter;
import com.optculture.launchpad.repositories.CommunicationTemplateRepository;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.util.Constants;


@Component("CM")
public class CMDispatcher implements ChannelDispatcher {
	
	@Autowired
	CommunicationEventHandler communicationEventHandler;
	
	@Autowired
	CommunicationTemplateRepository communicationTemplateRepository ;
	
	Logger logger = LoggerFactory.getLogger(CMDispatcher.class);

	private final RestTemplate restTemplate = new RestTemplate();


	@Override
	public void dispatch(String jsonContent, Contact contactobj, ChannelAccount channelAccount,
                         ChannelSetting channelSetting, UserChannelSetting userChannelSetting, Communication communicationObj, CustomCommunication in)
			throws IOException {

		try {
			
			logger.debug("----------< Entered CMDispatcher >----------");
			
//			String mobile = Utility.phoneParse(mobileNumber.trim(), user.getUserOrganization());
//			String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
//			if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {
//
//				mobile = mobile.substring(countryCarrier.length());// needed for CM
//
//			}
			
			//String mobile = contactobj.getMobilePhone();

			String apiEndPoint = channelSetting.getEndPoint(); //https://gw.cmtelecom.com/v1.0/message
			String accessToken = channelAccount.getApiKey();
			
			CommunicationEvent communicationEvent = null;

			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Content-Type", "application/json");
			headers.set("apikey", accessToken);	//CM



			// Create an HttpEntity with headers and the request body
			HttpEntity<String> requestEntity = new HttpEntity<>(jsonContent, headers);

			// Send the POST request
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiEndPoint, requestEntity, String.class);
			
			String response = responseEntity.getBody();
			logger.debug("Response from Server:\n" + response);
			
			String status = null;

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
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
	public String beforeMailMerge(Contact contact, Communication communication, ChannelAccount channelAccount, UserChannelSetting userChannelSettingObj, CustomCommunication in){
		
		//convert message content to json, which is supposed to submit to CM gateway
		String finalJson = convertToJson(contact, communication, channelAccount,userChannelSettingObj, in);
		
		return finalJson;
		
	}//beforeMailMerge
	
	public String convertToJson(Contact contact, Communication communication, ChannelAccount channelAccount, UserChannelSetting userChannelSettingObj, CustomCommunication in){
		
		//Sample json
		String json = "{\"messages\":{\"reference\":\"<<trackingId>>\",\"authentication\": {\"producttoken\": \"<<apiKey>>\"},"
				+ "\"msg\": [{\"from\": \"<<senderId>>\",\"to\": [{\"number\": \"<<mobileNo>>\"}],\"body\": {\"type\": \"auto\",\"content\": \"This is a WhatsApp message\"},\"allowedChannels\": [\"WhatsApp\"],"
				+ "\"richContent\": {\"conversation\": [{\"template\": {\"whatsapp\": {\"namespace\": \"<<templateRegisteredId>>\",\"element_name\": \"<<templateName>>\",\"language\": {\"policy\": \"deterministic\",\"code\": \"en\"},"
				+ "\"components\": <<components>> }}}]}}]}}";
		
		CommunicationTemplate communicationTemplate = null;
		communicationTemplate = communicationTemplateRepository.findByTemplateId(communication.getTemplateId());
		if(communicationTemplate==null) {
			logger.info("communicationTemplate is not found...");
			return null;
		}
		
		if(channelAccount.getApiKey()==null  || contact.getMobilePhone()==null || communicationTemplate.getTemplateRegisteredId()==null || communicationTemplate.getTemplateName()==null) {
			logger.info("------< One of the requiered parameter is not found >-------");
			return null;
		}
		json = json.replace("<<apiKey>>", channelAccount.getApiKey())
					.replace("<<senderId>>", communication.getSenderId()!=null?communication.getSenderId(): userChannelSettingObj.getSenderId())
					.replace("<<mobileNo>>", contact.getMobilePhone())
					.replace("<<templateRegisteredId>>", communicationTemplate.getTemplateRegisteredId())
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
        try {
        	List<Components> components = generateComponents(templateHeaderType, communication.getMediaUrl(), mergeTagsArr);
            String componentsStr = gson.toJson(components);
            json = json.replace("<<components>>", componentsStr);
            
        } catch (Exception e) {
        	logger.error("Something went wrong while generating components for CM :: ",e);
        }
		
        logger.info("final json content is : \n"+json);
        
		return json;
	}//convertToJson
	
	private List<Components> generateComponents(String templateHeaderType, String mediaUrl, String[] phArr) throws Exception{

		// Create a list of components
		List<Components> components = new ArrayList<>();

		// Create a header component
		Components headerComponent = null;
		if (templateHeaderType!=null && !templateHeaderType.equalsIgnoreCase("TEXT") && mediaUrl != null) {
			
			Map<String, Object> media = new HashMap<>();
			media.put("mediaName", "conversational-commerce");
			media.put("mediaUri", mediaUrl);
			
			//extract the file url extention eg. jpg/png/pdf/mp4
	        String extension = extractExtension(mediaUrl);
	        
	        String contentType = null;
			
	        switch (extension) {
	        case "jpg":
	        case "jpeg":
	        	contentType = MediaType.IMAGE_JPEG_VALUE;
	        	break;
	        case "png":
	        	contentType = MediaType.IMAGE_PNG_VALUE;
	        	break;
	        case "pdf":
	        	contentType = MediaType.APPLICATION_PDF_VALUE;
	        	break;
	        case "mp4":
	        	contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
	        	break;
	        default:
	        	throw new IllegalArgumentException("Unsupported file extension: " + extension);
	        }
	        
	        media.put("mimeType", contentType);
			
			headerComponent = new Components("header", Arrays.asList(new Parameter(templateHeaderType.toLowerCase(), media)));
			
		}
		
		// Create a body component
		Components bodyComponent = null;
		try {

			List<Parameter> parameterList = new ArrayList<Parameter>();

			Parameter parameter = null;

			if(phArr.length>0) {

				for(String ph : phArr) {
					
					parameter = new Parameter("text",ph);
					
					parameterList.add(parameter);
				}
			}

			bodyComponent = new Components("body",parameterList);

		} catch (Exception e) {
			logger.error("Exception while creating Parameters for the body variables :: ",e);
		}

		// Add the components to the list
		if(headerComponent!=null)	components.add(headerComponent);
		if(bodyComponent!=null)		components.add(bodyComponent);

		return components;
		/*
		"components": [
                       {
                           "type": "header",
                           "parameters": [
                               {
                           "type": "document",
                           "media": {
                               "mediaName": "conversational-commerce",
                               "mediaUri": "https://path/abc.pdf",
                               "mimeType": "document/pdf"
                           }
                       }
                           ]
                       },
                       {
                           "type": "body",
                           "parameters": [
                               {
                                   "type": "text",
                                   "text": "{{1}}"
                               },
                               {
                                   "type": "text",
                                   "text": "{{2}}"
                               }
                           ]
                       }
                   ]
		*/
	}//generateComponents
	
	public String extractExtension(String url) {
        Pattern pattern = Pattern.compile("\\.([^.]+)$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }//extractExtension

}
