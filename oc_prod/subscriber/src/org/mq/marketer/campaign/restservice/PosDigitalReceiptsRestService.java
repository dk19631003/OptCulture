package org.mq.marketer.campaign.restservice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class PosDigitalReceiptsRestService extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao;
	
	public PosDigitalReceiptsRestService() {
	}
	public UsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PrintWriter responseWriter = response.getWriter();
		String jsonMessage = "";
		byte statusFlag = 0;
		int errorCode = 0;
		response.setContentType("application/json");
		boolean isfailed = false;
		
	    try {
	    	String userFullDetails = "";
	    	if( (request.getParameter("userName") != null && request.getParameter("userName").length() > 1) &&
	    		(request.getParameter("userOrg") != null &&  request.getParameter("userOrg").length() > 1) ) {	
	    		userFullDetails =  request.getParameter("userName") + Constants.USER_AND_ORG_SEPARATOR +
	    				request.getParameter("userOrg");
	    	} else {
	    		errorCode = 300001;
	    		jsonMessage ="Given User name or Organization details not found .";
	    	    logger.debug("Required user name and Organization fields are not valid ... returning ");
	    	    isfailed = true;
	    	    return null;
	    	}
	    	
	    	ByteArrayInputStream byteArrStrStream  = new ByteArrayInputStream( QuotedPrintableCodec.decodeQuotedPrintable( (request.getParameter("jsonValue") ).getBytes()));
	    	BufferedReader br = new BufferedReader(new InputStreamReader(byteArrStrStream));
	    	String tempStr = "";
	    	StringBuffer sb = new StringBuffer();
	    	while(  (tempStr = br.readLine()) != null ) {
	    		//logger.debug(" JSON VALUE IS  " + jsonStr);
	    		sb.append(tempStr);
	    	}
	    	
	    	String jsonStr = sb.toString();
	    	JSONObject jsonMainObj = null;
	    	JSONObject jsonHeadObj= null;
	    	if(jsonStr != null && jsonStr.length() > 1) {
	    		logger.debug("jsonStr :>>>>>>>" + jsonStr);
	    		jsonMainObj = (JSONObject)JSONValue.parse(jsonStr);
	    	} else {
	    		logger.debug("UNable to creeate main obj");
	    		isfailed = true;
	    		return null;
	    	}
	    	
	    	jsonHeadObj = (JSONObject)jsonMainObj.get("Head");
	    	if(jsonHeadObj == null) {
				logger.info("Error : unable to find the User Details with given token****");
				jsonMessage = "Error : unable to find the User Token in JSON ";
				errorCode = 101011;
				isfailed = true;
				return null;
			}
			
			String userToken = jsonHeadObj.get("token") == null ? null : jsonHeadObj.get("token").toString().trim();
	    	if(userToken == null || userToken.toString().trim().isEmpty() ) {
				logger.info("Error : User Token,UserName,Organisation cannot be empty.");
				jsonMessage = "Error : User Token cannot be empty.";
				errorCode = 101012;
				isfailed = true;
				return null;
			}
			
			Users user = usersDao.findByToken(userFullDetails, userToken);
			if(user == null) {
				errorCode = 100011;
				jsonMessage ="Given User name or Organization details with token not found .";  // Organization
				logger.debug("******** User Object not Found ... returing ");
				isfailed = true;
				return null;
			}
	    	
			request.getRequestDispatcher("/sendDigitalReceipt.mqrm").forward(request,response);
	    	logger.debug("*************** EXITING***********************");
	    		    	
	    	return null;
	    } catch(Exception e) {
	    	logger.error("Exception ::" , e);
	    	return null;
	    } finally {
	    	if(isfailed){
	    	JSONObject rootObject = new JSONObject();
	    	JSONObject replyObject = new JSONObject();
	    	JSONObject returnParams = new JSONObject();
	    	Enumeration<String> reqMap = request.getParameterNames();
	    	while(reqMap.hasMoreElements()) {
	    		String ele = reqMap.nextElement();
	    		if(ele.equals("submit") || ele.equals("userName") || ele.equals("userOrg") || ele.equals("jsonValue")) {
	    			continue;
	    		} 
	    		returnParams.put(ele, request.getParameter(ele));
	    		logger.debug(">>>"+ ele  + "  :  " +request.getParameter(ele) );
	    	} // while
	    	
	    	JSONObject status = new JSONObject();
	    	if(returnParams.size() > 0) {
	    		status.put("RETURNPARAMETERS", returnParams);
	    	}
	    	replyObject.put("STATUS", statusFlag == 1 ? "Success" : "Failure");
	    	replyObject.put("MESSAGE", jsonMessage);
	    	replyObject.put("ERRORCODE", errorCode);
	    	status.put("STATUS",replyObject);
	    	rootObject.put("RESPONSEINFO", status);
	    	responseWriter.println(rootObject.toJSONString());
	    	}
	    	responseWriter.flush();
            responseWriter.close();
	    }
		
	}
}
