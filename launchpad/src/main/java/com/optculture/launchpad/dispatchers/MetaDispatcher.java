package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optculture.shared.entities.communication.*;
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

import com.optculture.launchpad.repositories.CommunicationTemplateRepository;
import com.optculture.launchpad.repositories.ScheduleRepository;
import com.optculture.shared.entities.contact.Contact;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.optculture.shared.events.CommunicationEventHandler;
import com.optculture.launchpad.configs.CommunicationEventHandler;
import com.optculture.launchpad.dto.Components;
import com.optculture.launchpad.dto.Parameter;
import com.optculture.shared.util.Constants;


@Component("Meta")
public class MetaDispatcher implements ChannelDispatcher {
	
	@Autowired
	CommunicationEventHandler communicationEventHandler;
	
	@Autowired
	CommunicationTemplateRepository communicationTemplateRepository ;
	
	Logger logger = LoggerFactory.getLogger(MetaDispatcher.class);

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	ScheduleRepository scheduleRepository;

	private String apiEndPoint;
	private String accessToken;



	@Override
	public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount,
                         ChannelSetting channelSetting, UserChannelSetting userChannelSetting, Communication communicationObj, CustomCommunication in)
			throws IOException {

		try {

//			String mobile = Utility.phoneParse(mobileNumber.trim(), user.getUserOrganization());
//			String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
//			if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {
//
//				mobile = mobile.substring(countryCarrier.length());
//
//			}
			
			//String mobile = contactobj.getMobilePhone();

			this.apiEndPoint = channelSetting.getEndPoint().replace("[FromID]", communicationObj.getSenderId()!=null ? communicationObj.getSenderId() : 
																		(userChannelSetting.getSenderId()!=null ? userChannelSetting.getSenderId() : ""));
																				//https://graph.facebook.com/v17.0/[FromID]/messages
			this.accessToken = channelAccount.getApiKey();

			CommunicationEvent communicationEvent = null;

			msgContent = msgContent.replace("[mobile]", contactobj.getMobilePhone());
			logger.debug("message =="+msgContent);
			
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", "Bearer "+this.accessToken);



			// Create an HttpEntity with headers and the request body
			HttpEntity<String> requestEntity = new HttpEntity<>(msgContent, headers);

			// Send the POST request
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(this.apiEndPoint, requestEntity, String.class);
			
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
		
		//convert message content to json, which is supposed to submit to Meta gateway
		String finalJson = convertToJson(contact, communication, channelAccount,userChannelSettingObj, in);
		
		return finalJson;
		
	}//beforeMailMerge
	
	public String convertToJson(Contact contact, Communication communication, ChannelAccount channelAccount, UserChannelSetting userChannelSettingObj, CustomCommunication in){
		
		//Sample json
		String json = "{\"messaging_product\":\"whatsapp\",\"recipient_type\":\"individual\",\"to\":\"<<mobileNo>>\",\"biz_opaque_callback_data\":\"<<trackingId>>\",\"type\":\"template\","
						+ "\"template\":{\"name\":\"<<templateName>>\",\"language\":{\"code\":\"en\"},\"components\":<<components>>}}";
		
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
		json = json.replace("<<apiKey>>", channelAccount.getApiKey())
					.replace("<<mobileNo>>", contact.getMobilePhone())
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
		Map<String, Object> mediaObj = new HashMap<>();
		mediaObj.put("link", mediaUrl);
		
		Parameter headerParameter = new Parameter();
		headerParameter.setType(templateHeaderType.toLowerCase());//image,video,document

		if (templateHeaderType!=null && templateHeaderType.equalsIgnoreCase("IMAGE") && mediaUrl != null)
			headerParameter.setImage(mediaObj);
			
		else if (templateHeaderType!=null && templateHeaderType.equalsIgnoreCase("DOCUMENT") && mediaUrl != null)
			headerParameter.setDocument(mediaObj);
			
		else if (templateHeaderType!=null && templateHeaderType.equalsIgnoreCase("VIDEO") && mediaUrl != null)
			headerParameter.setVideo(mediaObj);
			
		else 
			headerParameter = null;
		
		if(headerParameter!=null)
			headerComponent = new Components("header", Arrays.asList(headerParameter));
		
		// Create a body component
		Components bodyComponent = null;
		try {

			List<Parameter> bodyParameterList = new ArrayList<Parameter>();

			Parameter bodyParameter = null;

			if(phArr.length>0) {

				for(String ph : phArr) {
					
					bodyParameter = new Parameter("text",ph);
					
					bodyParameterList.add(bodyParameter);
				}
			}

			bodyComponent = new Components("body",bodyParameterList);

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
	            "type": "image",
	            "image": {
	              "link": "https://t.ocmails.com/subscriber/UserData/Ginesys__org__Ginesys/Gallery/My%20Images/NarayaniDRLogo1.JPG"
	            }
	          }
	        ]
	      },
	      {
	        "type": "body",
	        "parameters": [
	          {
	            "type": "text",
	            "text": "[ph1]"
	          },
	          {
	            "type": "text",
	            "text": "[ph2]"
	          }
	        ]
	      }
    	]
		*/
	}//generateComponents
}
