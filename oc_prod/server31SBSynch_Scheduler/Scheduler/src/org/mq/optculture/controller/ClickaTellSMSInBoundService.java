package org.mq.optculture.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSInBoundRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ClickaTellSMSInBoundService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse arg1) throws Exception {
		
		//get all the request parameteres and construct request VO object
		SMSInBoundRequestObject newInBoundReqObj = new SMSInBoundRequestObject();
		
		
		/*String moFrom = request.getParameter("from");//which is nothing but the sent id we set
		
		String moTo = request.getParameter("to");
		String timeStamp = request.getParameter("timestamp");//need to know the actual status msg if incase 
															//of any error we need the description specifying the error reason.
		
		String repMsgContent = request.getParameter("text");*/
		
		newInBoundReqObj.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_SMS);//base class's method
		newInBoundReqObj.setSource(request.getParameter(OCConstants.REQUEST_PARAM_FROM));
		newInBoundReqObj.setDestination(request.getParameter(OCConstants.REQUEST_PARAM_TO));
		newInBoundReqObj.setReceivedTimeStr(request.getParameter(OCConstants.REQUEST_PARAM_TIMESTAMP));
		newInBoundReqObj.setMsgContent(request.getParameter(OCConstants.REQUEST_PARAM_TEXT));//base class's method
		newInBoundReqObj.setTimeFormat(MyCalendar.FORMAT_DATETIME_STYEAR_ONE);
		newInBoundReqObj.setFromCountry(Constants.SMS_COUNTRY_US);
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.GATEWAY_BUSINESS_SERVICE);
		
		logger.debug("=======got the service ======");
		
		
		//PrintWriter pw = response.getWriter();
		
		try {
			logger.debug("=======calling the service ======");
			
			BaseResponseObject retResponse  = baseService.processRequest(newInBoundReqObj);
			
			logger.debug("=======ending the service ======"+retResponse);
			
			/*response.setContentType("text/html");
			
			pw.write(retResponse.getResponseStr());*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}finally {
			
			/*pw.flush();
			pw.close();*/
		}
		return null;
	}
	
}
