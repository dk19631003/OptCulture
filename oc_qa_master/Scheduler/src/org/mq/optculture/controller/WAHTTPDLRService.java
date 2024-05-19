package org.mq.optculture.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.WAStatusCodes;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.WAHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class WAHTTPDLRService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String qryStr = request.getQueryString();
		if(qryStr!=null && !qryStr.isEmpty())
			logger.info("WA DLR request query String : "+qryStr);//hub.mode=subscribe&hub.challenge=81353130&hub.verify_token=OptCulture
		
		// Retrieve the request parameter for endpoint verification
		if(request.getParameter("hub.challenge")!=null && !request.getParameter("hub.challenge").isEmpty() && 
				request.getParameter("hub.verify_token")!=null && !request.getParameter("hub.verify_token").isEmpty()) {

			try {
				String verificationToken = request.getParameter("hub.verify_token");

				if (verificationToken.equals("OptCulture")) {//to be given at meta webhook
					// Set the HTTP status code to 200 (OK) for successful verification
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write(request.getParameter("hub.challenge"));
					logger.info("Meta webhook verified successfully...");
					
				} else {
					// Set the HTTP status code to 403 (Forbidden) for failed verification
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().write("Verification failed");
					logger.info("Please check your Verification Token ...");
				}
				// Return null to indicate that the response has been handled directly
				return null;
			} catch (Exception e) {
				logger.error("Exception while getting query parameter :",e);
				return null;
			}
		}

		
		/* MessageBird webhook response
		{
		    "message": {
		        "id": "117f405765f94ef191b7dfad20ae8491",
		        "status": "delivered"
		    },
		    "type": "message.status",
		    "workspaceId": 11475942
		}
		*/
		
		/* CM webhook response
		{
		    "messages": {
		        "msg": {
		            "received": "2023-09-15T13:24:03",
		            "to": "00917073935263",
		            "reference": "[sentId]",
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
		
		/*Meta webhook
		{
	        "entry": [
	            {
	                "id": "152205054643714",
	                "changes": [
	                    {
	                        "value": {
	                            "messaging_product": "whatsapp",
	                            "statuses": [
	                                {
	                                    "id": "wamid.HBgMOTE3MDczOTM1MjYzFQIAERgSMDhDMjg0NkM2QjA1MzU4QkE3AA==",
	                                    "status": "read"
	                                    ...more */
		
		
		
	  	InputStream is = request.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		char[] chr = new char[1024];
		int bytesRead = 0;
		StringBuilder sb = new StringBuilder();
		//JSONParser parser = new JSONParser();
		JSONObject jsonObj ;
		JSONObject messageObj_MB=null;
		JSONObject messageObj_CM=null;
		JSONObject messageObj_META=null;
		
		while ((bytesRead = br.read(chr)) > 0) {
	         sb.append(chr, 0, bytesRead);
	    }
		
		//logger.info("Rest body value is "+ sb.toString());//TODO keep only for local testing
		
		try {
			
			 //parser = new JSONParser();
			//jsonObj = (JSONObject) parser.parse(sb.toString());
			jsonObj = new JSONObject(sb.toString());
			
			
			if(jsonObj!=null) {
				
				//MsgBird
				messageObj_MB = jsonObj.isNull("message") ? null : jsonObj.getJSONObject("message");
				
				//CM
				messageObj_CM =jsonObj.isNull("messages")?null:(jsonObj.getJSONObject("messages").isNull("msg")?null:jsonObj.getJSONObject("messages").getJSONObject("msg"));
				
				//meta
				//entry[0].changes[0].value.statuses[0]
				try {
					messageObj_META = jsonObj.getJSONArray("entry").getJSONObject(0).getJSONArray("changes").getJSONObject(0).getJSONObject("value")
									.getJSONArray("statuses").getJSONObject(0);
				} catch (Exception e) {
					messageObj_META = null;
				}
			}

		} catch(Exception e) {
			
			logger.error("Error : Invalid json Object .. Returning. ****");
			return null;
		}		
		
		if(messageObj_MB == null && messageObj_CM == null && messageObj_META == null) return null;
		
		//get all the request parameteres and construct request VO object
		WAHTTPDLRRequestObject newHTTPDLRRequestObject = new WAHTTPDLRRequestObject();
		
		if(messageObj_MB !=null) {
			
			long sentId = 0;
			try {
				//https://localhost:8080/Scheduler/updateWADLR.mqrm?sentId=[sentId]
				sentId = Long.parseLong(request.getParameter("sentId"));
				
			} catch (Exception e1) {
				logger.error("Invalid sentId : "+request.getParameter("sentId"));
				return null;
			}
			
			newHTTPDLRRequestObject.setStatus(messageObj_MB.getString("status"));
			//newHTTPDLRRequestObject.setDeliveredTimeStr(messageObj_MB.get("updatedDatetime").toString());
			//newHTTPDLRRequestObject.setMobileNumber(messageObj_MB.get("to").toString());
			newHTTPDLRRequestObject.setMessageID(messageObj_MB.getString("id"));
			newHTTPDLRRequestObject.setSentId(sentId);
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
		}
		
		else if(messageObj_CM !=null) {
			
			newHTTPDLRRequestObject.setStatus(messageObj_CM.getJSONObject("status").getString("code"));
													//0 = accepted 1 = rejected by CM 2 = delivered 3 = failed 4 = Read
			
			newHTTPDLRRequestObject.setMobileNumber(messageObj_CM.getString("to"));
			
			try {
				newHTTPDLRRequestObject.setSentId(Long.parseLong(messageObj_CM.getString("reference")));//sent_id
				
			} catch (Exception e) {	//OPS-461
				//logger.debug("Error : Invalid send ID .. Returning. ****");//keep for testing only
				return null;
			}
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
			
		}
		
		else if(messageObj_META !=null) {
			
			newHTTPDLRRequestObject.setStatus(messageObj_META.getString("status"));
													//sent, delivered, read or failed
			
			newHTTPDLRRequestObject.setMobileNumber(messageObj_META.isNull("recipient_id")?null:messageObj_META.getString("recipient_id"));
			newHTTPDLRRequestObject.setMessageID(messageObj_META.isNull("id")?null:messageObj_META.getString("id"));
			
			//newHTTPDLRRequestObject.setSentId(Long.parseLong(messageObj_CM.getString("reference")));//sent_id not receiving for meta
	
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
			
		}
		
		String status = newHTTPDLRRequestObject.getStatus();
		//if status does not belongs to any of the WAStatusCodes, then return
		if(status==null || (!WAStatusCodes.BouncedSet.contains(status) && !WAStatusCodes.DeliveredSet.contains(status) && !WAStatusCodes.ReadSet.contains(status) )  ) 
			return null;
		
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.WA_BUSINESS_SERVICE);
		
		try {
			logger.debug("=======calling the service for recepient mobileNo = "+newHTTPDLRRequestObject.getMobileNumber()+" and status = "+newHTTPDLRRequestObject.getStatus()+" =====");
			
			BaseResponseObject retResponse  = baseService.processRequest(newHTTPDLRRequestObject);
			
			//logger.debug("=======ending the service ======"+retResponse);
			
		} catch (Exception e) {
			logger.error("Exception : ",e);
		}
		
		return null;
	}
}
