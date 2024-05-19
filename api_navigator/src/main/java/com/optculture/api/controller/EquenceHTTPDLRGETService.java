package com.optculture.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import com.optculture.api.model.EquenceDLRRequestObject;
import com.optculture.api.service.BaseService;
import com.optculture.shared.util.Constants;
import com.optculture.shared.util.OCConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class EquenceHTTPDLRGETService extends AbstractController{

	Logger logger = LoggerFactory.getLogger(EquenceHTTPDLRGETService.class);

	@Autowired
	@Qualifier("equenceBusinessServiceImpl")
	BaseService baseService;

	@Override
	@GetMapping("/equenceDLR.mqrm")
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.debug("----------< Ready to process the Equence SMS DLR GET request >----------");

		
		response.setContentType("application/json");

		EquenceDLRRequestObject requestObject = new EquenceDLRRequestObject();

		String status = request.getParameter("sms_delv_status") == null ?  request.getParameter("Sms_delv_status") : request.getParameter("sms_delv_status");
		String deliveredTime = request.getParameter("sms_delv_dttime") == null ?  request.getParameter("Sms_delv_dttime") : request.getParameter("sms_delv_dttime");
		String mobileNumber = request.getParameter("mobile_no") == null ? request.getParameter("Mobile_NO") : request.getParameter("mobile_no");
		String mesagegId = request.getParameter("msg_id");
		String msgId = request.getParameter("MSGID");
		String mrId = request.getParameter("mr_id") == null ? request.getParameter("Mr_id") : request.getParameter("mr_id");
		String reason = request.getParameter("remarks");

		logger.info("status value is {}",status);
		logger.info("deliveredTime value is {}",deliveredTime);
		logger.info("mobileNumber value is {}",mobileNumber);
		logger.info("mesagegId value is {}",mesagegId);
		logger.info("msgId value is {}",msgId);
		logger.info("mrId value is {}",mrId);
		logger.info("reason value is {}",reason);
		try{
			requestObject.setStatus(status);
			requestObject.setDeliveredTime(deliveredTime);
			requestObject.setMobileNumber(mobileNumber);
			requestObject.setMessageID(mesagegId);
			requestObject.setMSGID(msgId);
			requestObject.setMrId(mrId);
			requestObject.setReason(reason);
			requestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
			requestObject.setUserSMSTool(Constants.USER_SMSTOOL_EQUENCE);
			logger.info("requestObject.getMSGID()  {}",requestObject.getMSGID());
			logger.info("requestObject.getMessageId() {}",requestObject.getMessageID());
			logger.debug("=======got the service ======");
			logger.debug("=======calling the service ======");

			baseService.processRequest(requestObject);
			logger.debug("=======ending the service ======");

			String responseJson = "{\"MESSAGE\":\"Success.\"}";
			
			logger.info("Response =  {}",responseJson);
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
			logger.info("Response =  {}",responseJson);

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
