package org.mq.optculture.sales.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.digitalReceipt.SendDRBusinessService;
import org.mq.optculture.model.digitalReceipt.SendDRRequest;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonProcessor implements Processor
{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String jsonString = (String) exchange.getIn().getBody();
		JsonParser parser = new JsonParser();
	    JsonObject json = parser.parse(jsonString).getAsJsonObject();

	     Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	      String prettyJson = gson.toJson(json);
		 // System.out.println(prettyJson);
		  
		  
		    SendDRBusinessService drService = (SendDRBusinessService)ServiceLocator.getInstance().getServiceById(OCConstants.SEND_DR_BUSINESS_SERVICE);
		    
			SendDRRequest sendDrRequest = new SendDRRequest();
			sendDrRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
			sendDrRequest.setJsonValue(prettyJson);
			sendDrRequest.setUserName(exchange.getProperty("userName").toString());
			sendDrRequest.setUserOrg(exchange.getProperty("orgId").toString());
			
			SendDRResponse drResponse = drService.processSendDRRequest(sendDrRequest, OCConstants.DR_OFFLINE_MODE);
			logger.info("status message = "+drResponse.getRESPONSEINFO().getSTATUS().getMESSAGE());
		  
		  
		  
		  exchange.getIn().setBody(prettyJson,String.class);	 	

	}

}