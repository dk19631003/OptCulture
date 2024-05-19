package org.mq.optculture.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mq.captiway.scheduler.utility.Constants;
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
		
		
		
	  	InputStream is = request.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		char[] chr = new char[1024];
		int bytesRead = 0;
		StringBuilder sb = new StringBuilder();
		JSONParser parser = new JSONParser();
		JSONObject jsonObj ;
		JSONObject messageObj_MB=null;
		JSONObject messageObj_CM=null;
		
		while ((bytesRead = br.read(chr)) > 0) {
	         sb.append(chr, 0, bytesRead);
	    }
		
		//logger.info("Rest body value is "+ sb.toString());//TODO keep only for local testing
		
		try {
			
			 parser = new JSONParser();
			jsonObj = (JSONObject) parser.parse(sb.toString());
			
			if(jsonObj!=null) {
				messageObj_MB =(JSONObject)jsonObj.get("message");//MsgBird
				
				//CM
				if(jsonObj.get("messages")!=null) {
					messageObj_CM = (JSONObject)((JSONObject)jsonObj.get("messages")).get("msg");
				}
			}

		} catch(Exception e) {
			
			logger.error("Error : Invalid json Object .. Returning. ****");
			return null;
		}		

		
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
			
			newHTTPDLRRequestObject.setStatus(messageObj_MB.get("status").toString());
			//newHTTPDLRRequestObject.setDeliveredTimeStr(messageObj_MB.get("updatedDatetime").toString());
			//newHTTPDLRRequestObject.setMobileNumber(messageObj_MB.get("to").toString());
			newHTTPDLRRequestObject.setMessageID(messageObj_MB.get("id").toString());
			newHTTPDLRRequestObject.setSentId(sentId);
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
		}
		
		else if(messageObj_CM !=null) {
			
			newHTTPDLRRequestObject.setStatus( ((JSONObject)messageObj_CM.get("status")).get("code").toString() );
													//0 = accepted 1 = rejected by CM 2 = delivered 3 = failed 4 = Read
			
			newHTTPDLRRequestObject.setMobileNumber(messageObj_CM.get("to").toString());
			
			try {
				newHTTPDLRRequestObject.setSentId(Long.parseLong(messageObj_CM.get("reference").toString()));//sent_id
				
			} catch (Exception e) {	//OPS-461
				//logger.debug("Error : Invalid send ID .. Returning. ****");//keep for testing only
				return null;
			}
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
			
		}
		
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.WA_BUSINESS_SERVICE);
		
		try {
			logger.debug("=======calling the service for sent ID = "+newHTTPDLRRequestObject.getSentId()+" and status = "+newHTTPDLRRequestObject.getStatus()+" =====");
			
			BaseResponseObject retResponse  = baseService.processRequest(newHTTPDLRRequestObject);
			
			//logger.debug("=======ending the service ======"+retResponse);
			
		} catch (Exception e) {
			logger.error("Exception : ",e);
		}
		
		return null;
	}
}
