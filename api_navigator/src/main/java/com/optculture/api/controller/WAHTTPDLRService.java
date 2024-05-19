package com.optculture.api.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.optculture.api.general.StatusCodes;
//import com.optculture.api.model.BaseResponseObject;
import com.optculture.api.model.WAHTTPDLRRequestObject;
import com.optculture.api.service.BaseService;
import com.optculture.shared.util.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class WAHTTPDLRService extends AbstractController{

	@Autowired
	@Qualifier("waBusinessServiceImpl")
	BaseService baseService;

	@Override
	@PostMapping("/updateWADLR.mqrm")
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//logger.debug("----------< Ready to process the WhatsApp DLR request >----------");//TODO keep only for testing
		
		

		/* CM webhook response
		{
		    "messages": {
		        "msg": {
		            "received": "2023-09-15T13:24:03",
		            "to": "00917073935263",
		            "reference": "trackingId",
		            "status": {
		                "code": "2",
		                "errorCode": "",
		                "errorDescription": "Delivered"
		            },
		            "operator": ""
		        }
		    }
		}
		 */
		
		/* Gupshup webhook response
		[
		    {
		        "srcAddr": "TESTSM",
		        "channel": "WHATSAPP",
		        "externalId": "5110097072144920654-96858143828344140",
		        "extra":"trackingId",
		        "cause": "READ",
		        "errorCode": "026",
		        "destAddr": "91XXXXXXXXXX",
		        "eventType": "READ",
		        "eventTs": 1708682686000
		    }
		] */

		InputStream is = request.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		char[] chr = new char[1024];
		int bytesRead = 0;
		StringBuilder sb = new StringBuilder();
		
		JsonObject jsonObj = null;
		JsonArray jsonArr = null;
		JsonObject messageObj_CM = null;
		JsonObject messageObj_Interact = null;
		JsonObject messageObj_GUPSHUP = null;

		while ((bytesRead = br.read(chr)) > 0) {
			sb.append(chr, 0, bytesRead);
		}

		//logger.info("Rest body value is \n"+ sb.toString());//TODO keep only for testing

		try {

			try {
				// Parse the JSON string to a JsonObject
				jsonObj = JsonParser.parseString(sb.toString()).getAsJsonObject();
				
			} catch (Exception e) {
				//receives as array for Gupshup, hence parse to JsonArray
				jsonArr = JsonParser.parseString(sb.toString()).getAsJsonArray();
			}
			
			if(jsonObj!=null) {
				
				//One time Interact webhook verification
				try {
					boolean isInteractWebhookVerificationRequest = jsonObj.get("type")!=null && jsonObj.get("type").getAsString().equalsIgnoreCase("Webhook Test");
					if(isInteractWebhookVerificationRequest) {
						// Set the HTTP status code to 200 (OK) for successful verification
						response.setStatus(HttpServletResponse.SC_OK);
						logger.debug("Interact webhook verified successfully...");
						return null;
					}
				} catch (Exception e) {
					logger.error("Something went wrong while verifying Interact webhook..."+e);
					return null;
				}
				
				try {
					//CM //Retrieve the "msg" child object
					messageObj_CM = jsonObj.getAsJsonObject("messages")!=null ? jsonObj.getAsJsonObject("messages").getAsJsonObject("msg") : null;
					
					//Interact
					messageObj_Interact = jsonObj.getAsJsonObject("data")!=null ? jsonObj.getAsJsonObject("data").getAsJsonObject("message") : null;
					
					//if value of 'reference' (i.e. cr_id) not receiving, then dont process
					if (messageObj_CM!=null && messageObj_CM.get("reference") == null )
						return null;
					
					//if value of data.message.meta_data.source_data.callback_data(i.e. cr_id) not receiving, then dont process
					if (messageObj_Interact!=null && messageObj_Interact.getAsJsonObject("meta_data").getAsJsonObject("source_data").get("callback_data") == null )
						return null;
					
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				
			} else if (jsonArr!=null && jsonArr.size()>0) {
				//Gupshup
				try {
					messageObj_GUPSHUP = jsonArr.get(0).getAsJsonObject()!=null ? jsonArr.get(0).getAsJsonObject() : null;
					
					//if value of 'extra' (i.e. cr_id) not receiving, then dont process
					if(messageObj_GUPSHUP!=null && messageObj_GUPSHUP.get("extra") == null )
						return null;
					
				} catch (Exception e) {
					logger.error("Exception",e);
				}
			}

		} catch(Throwable e) {

			logger.error("Error : Invalid json Object .. Returning. ****",e);
			return null;
		}		

		//if( messageObj_CM == null && messageObj_GUPSHUP == null) return null;

		String status = null;
		String mobileNumber = null;
		String trackingId = null;
		String[] trackingIdArr = new String[4];
		
		if(messageObj_CM !=null) {
			
			status = messageObj_CM.getAsJsonObject("status").get("code").getAsString();
			//0 = accepted 1 = rejected by CM 2 = delivered 3 = failed 4 = Read
			
			mobileNumber = messageObj_CM.get("to").getAsString();
			trackingId = messageObj_CM.get("reference").getAsString();

		}else if(messageObj_GUPSHUP !=null) {
			
			status = messageObj_GUPSHUP.get("eventType").getAsString();
			mobileNumber = messageObj_GUPSHUP.get("destAddr").getAsString();
			trackingId = messageObj_GUPSHUP.get("extra").getAsString();
			
		}else if(messageObj_Interact !=null) {
			
			status = messageObj_Interact.get("message_status").getAsString();
			mobileNumber = jsonObj.getAsJsonObject("data").getAsJsonObject("customer").get("phone_number").getAsString();
			trackingId = messageObj_Interact.getAsJsonObject("meta_data").getAsJsonObject("source_data").get("callback_data").getAsString();

		}else return null;

		//after getting all the required request parameters construct the request VO object
		WAHTTPDLRRequestObject newHTTPDLRRequestObject = new WAHTTPDLRRequestObject();
		newHTTPDLRRequestObject.setStatus(status);
		newHTTPDLRRequestObject.setMobileNumber(mobileNumber);
		
		//Split the received tracking id and set to corresponding fields
		try {
			if(trackingId.startsWith("DR") && trackingId.contains(Constants.DELIMETER_DOUBLECOLON)) { //for DR OPS-620
				//if DR(Utility message) is failed , then don't process the marketing message too
				trackingIdArr = trackingId.split(Constants.DELIMETER_DOUBLECOLON); //DR::sent_id::0::user_id
				if(trackingIdArr.length != 4) return null;
				newHTTPDLRRequestObject.setCrId(0L);
				//newHTTPDLRRequestObject.setMessageID(trackingIdArr[0]);//DR
				newHTTPDLRRequestObject.setCampId(Long.parseLong(trackingIdArr[1]));//sent_id
				newHTTPDLRRequestObject.setContactId(0L);
				newHTTPDLRRequestObject.setUserId(Long.parseLong(trackingIdArr[3]));
			}
			else if(trackingId.contains(Constants.DELIMETER_DOUBLECOLON)) { //for campaigns
				trackingIdArr = trackingId.split(Constants.DELIMETER_DOUBLECOLON); //cr_id::camp_id::cid::user_id
				if(trackingIdArr.length != 4) return null;
				newHTTPDLRRequestObject.setCrId(Long.parseLong(trackingIdArr[0]));
				newHTTPDLRRequestObject.setCampId(Long.parseLong(trackingIdArr[1]));
				newHTTPDLRRequestObject.setContactId(Long.parseLong(trackingIdArr[2]));
				newHTTPDLRRequestObject.setUserId(Long.parseLong(trackingIdArr[3]));
			}
			else {
				logger.debug("Invalid tracking Id = "+trackingId);
				return null;
			}

		} catch (Exception e) {	//OPS-461
			//logger.debug("Error : Invalid trackingId : "+trackingId);//TODO keep only for testing
			return null;
		}
		newHTTPDLRRequestObject.setAction("DLR");

		//if status does not belongs to any of the StatusCodes, then return
		if(status==null || (!StatusCodes.BouncedSet.contains(status) && !StatusCodes.DeliveredSet.contains(status) && !StatusCodes.ReadSet.contains(status) )  ) {
			logger.debug("Invalid status,Please check again :: "+status);
			return null;
		}
			
		try {
			logger.debug("=======calling the service for recepient mobileNo = "+mobileNumber+" and status = "+status+" and trackingId = "+trackingId +" =====");

			//BaseResponseObject retResponse  = null ;
			baseService.processRequest(newHTTPDLRRequestObject);

		} catch (Exception e) {
			logger.error("Exception : ",e);
		}

		return null;
	}

}
