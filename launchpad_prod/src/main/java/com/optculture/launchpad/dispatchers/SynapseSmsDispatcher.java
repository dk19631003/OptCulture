package com.optculture.launchpad.dispatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.optculture.launchpad.dispatchers.SynapseSMSList.MessageParams;
import com.optculture.launchpad.repositories.ScheduleRepository;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component("Synapse")
public class SynapseSmsDispatcher implements ChannelDispatcher {

    
	Logger logger = LoggerFactory.getLogger(SynapseSmsDispatcher.class);

	ScheduleRepository schrepo;
    
    public SynapseSmsDispatcher(ScheduleRepository schrepo) {
		super();
		this.schrepo = schrepo;
	}

    private final RestTemplate restTemplate = new RestTemplate();

	

	List<MessageParams> textlist = null;

       
	@Override
	public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in)
			throws IOException {
	
		
		logger.trace("entering Synapse SMS dispatcher");

    	// need to add Escaping HtmlUtils
		
    	
		String url = channelSetting.getEndPoint();		        
        
		HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        
		// Create an instance of your custom request body object
        List<MessageParams> textLst = new ArrayList<MessageParams>();
		SynapseSMSList synapseSMSList = new SynapseSMSList();
		MessageParams messageParams = synapseSMSList.new MessageParams();
		messageParams.setMobileNumber(contactobj.getMobilePhone());
		synapseSMSList.setMobileNumbers(textLst);
		textLst.add(messageParams);
        PrepareSynapseJsonRequest synapseJson = new PrepareSynapseJsonRequest();

        synapseJson.setMsgType("1");
        synapseJson.setSenderId(usechannelSetting.getSenderId());
        synapseJson.setPassword(channelAccount.getAccountPwd());
        synapseJson.setMessage(msgContent);
        synapseJson.setUserName(channelAccount.getAccountName());
		synapseJson.setMobileNumbers(synapseSMSList);
        
		Gson gson = new Gson();
		
		// Convert the request body object to a JSON string
		String requestBody = gson.toJson(synapseJson);

		logger.info("Synapse requestBody value is {}",requestBody);


		// Create an HttpEntity with headers and the request body
		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    	try {
    			// Send the POST request
    			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
				String response = responseEntity.getBody();

				
    			if (responseEntity.getStatusCode() == HttpStatus.OK) {
    				

    					logger.info("Response from Server in if: {}",response);
    		
    			} else {
    					logger.warn("Failed : HTTP error code : {}",responseEntity.getStatusCode());
    					logger.info("Response from Server: {}",response);

    			}
	
    	   }catch (RestClientException e) {
    		   
   				logger.error("Exception caught in catch block: {} " , e.getMessage());
 
    	   }
    	
    }

		
}
