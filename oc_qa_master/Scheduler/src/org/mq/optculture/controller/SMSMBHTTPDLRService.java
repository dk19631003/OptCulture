package org.mq.optculture.controller;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class SMSMBHTTPDLRService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		JSONArray jsonRootObject;
		
		//String req=request.toString();
		//getting body from request where body is null
		//req = "datacoding=plain&id=71fd32a4e41a4380af1efa0fe7676f99&mccmnc=302370&messageLength=47&messagePartCount=1&ported=0&price%5Bamount%5D=0.00225&price%5Bcurrency%5D=USD&recipient=16478958837&reference=SenderId&status=delivered&statusDatetime=2023-10-11T10%3A30%3A21%2B00%3A00&statusReason=successfully+delivered";

	 	//InputStream is = request.getInputStream();
	  	request.getQueryString();
	  	request.getParameter("reference");
	  	logger.info("reference is "+request.getParameter("reference"));
	  	logger.info("status  is "+request.getParameter("status"));

		
	  	//get all the request parameteres and construct request VO object
		SMSHTTPDLRRequestObject newHTTPDLRRequestObject = new SMSHTTPDLRRequestObject();
		
			
			if(request.getQueryString()!=null) {
			newHTTPDLRRequestObject.setStatus(request.getParameter("status"));
			//newHTTPDLRRequestObject.setDeliveredTimeStr(messageObj_MB.get("updatedDatetime").toString());
			//newHTTPDLRRequestObject.setMobileNumber(messageObj_MB.get("to").toString());
			newHTTPDLRRequestObject.setMessageID(request.getParameter("id"));
			newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
			
			}
		
	
		
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.SMSMB_BUSINESS_SERVICE);
		
		try {
			logger.debug("=======calling the service ======");
			
			BaseResponseObject retResponse  = baseService.processRequest(newHTTPDLRRequestObject);
			
			logger.debug("=======ending the service ======"+retResponse);
			
		} catch (Exception e) {
			logger.error("Exception : ",e);
		}
		
		return null;
	}
}


