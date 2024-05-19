package com.optculture.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import com.google.gson.Gson;
import com.optculture.api.model.EquenceDLRRequestObject;
import com.optculture.api.service.BaseService;
import com.optculture.shared.util.Constants;
import com.optculture.shared.util.OCConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class EquenceHTTPDLRService extends AbstractController{

	@Autowired
	@Qualifier("equenceBusinessServiceImpl")
	BaseService baseService;

	@Override
	@PostMapping("/equenceDLR.mqrm")
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.debug("----------< Ready to process the Equence SMS DLR request >----------");
		
		
		response.setContentType("application/json");
		//EquenceDLRRequestObject newHTTPDLRRequestObject = new EquenceDLRRequestObject();
		String jsonValue = getParameterJsonValue(request);
		Gson gson = new Gson();
		logger.debug(""+jsonValue);
		EquenceDLRRequestObject requestObject = null;
		
		try{
		requestObject = gson.fromJson(jsonValue, EquenceDLRRequestObject.class);
		requestObject.setStatus(requestObject.getStatus());
		requestObject.setDeliveredTime(requestObject.getDeliveredTime());
		requestObject.setMobileNumber(requestObject.getMobileNumber());
		requestObject.setMessageID(requestObject.getMessageID()!=null ?requestObject.getMessageID() :null);
		requestObject.setMSGID(requestObject.getMSGID()!=null ? requestObject.getMSGID() : null);
		requestObject.setMrId(requestObject.getMrId());
		requestObject.setReason(requestObject.getReason());
		requestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
		requestObject.setUserSMSTool(Constants.USER_SMSTOOL_EQUENCE);
		logger.info("requestObject.getMSGID() "+requestObject.getMSGID());
		logger.info("requestObject.getMessageId() "+requestObject.getMessageID());
		
			logger.debug("=======got the service ======");
		
			logger.debug("=======calling the service ======");
			baseService.processRequest(requestObject);
			logger.debug("=======ending the service ======");
			
			String responseJson = "{\"MESSAGE\":\"Success.\"}";
				logger.info("Response = "+responseJson);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Completed EquenceHTTPDLRService.");
			
		}catch(Exception e){
			logger.info("Exception",e);
			String responseJson = "{\"MESSAGE\":\"Invalid request.\"}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			
			return null;
		}

		
		return null;
	}
	
	public static String getParameterJsonValue(HttpServletRequest request) throws IOException{
		String jsonValue = request.getParameter(OCConstants.JSON_VALUE);
		if(jsonValue == null || jsonValue.trim().isEmpty()){
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char[] cbuf = new char[1024];
			int bytesRead = 0;
			StringBuffer sb = new StringBuffer();
			while((bytesRead = br.read(cbuf)) > 0){
				sb.append(cbuf, 0, bytesRead);
			}
			jsonValue = sb.toString();
			br.close();
		}
		return jsonValue;
	}
	

}
