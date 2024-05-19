package org.mq.optculture.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ClickATellHTTPDLRService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		/*CLIENTSERVERIP/PATH?sid=21232324243432424234&dest=919886430811&stime
				=2011-04-21 12:32:04&dtime=2011-04-21 12:32:16&status=001&reason=DELIVRD
*/
		/*String apiMsgId = request.getParameter("apiMsgId");
		String cliMsgId = request.getParameter("cliMsgId");//which is nothing but the sent id we set
		
		String mobile = request.getParameter("to");
		String statusCode = request.getParameter("status");		//need to know the actual status msg if incase 
		*/
		//get all the request parameteres and construct request VO object
		SMSHTTPDLRRequestObject newHTTPDLRRequestObject = new SMSHTTPDLRRequestObject();
		
		newHTTPDLRRequestObject.setStatus(request.getParameter(OCConstants.REQUEST_PARAM_STATUS));
		newHTTPDLRRequestObject.setDeliveredTimeStr(request.getParameter(OCConstants.REQUEST_PARAM_TIMESTAMP));
		newHTTPDLRRequestObject.setMobileNumber(request.getParameter(OCConstants.REQUEST_PARAM_TO));
		newHTTPDLRRequestObject.setMessageID(request.getParameter(OCConstants.REQUEST_PARAM_MSGID_APIMSGID));
		newHTTPDLRRequestObject.setClientMsgId(request.getParameter(OCConstants.REQUEST_PARAM_MSGID_CLIENTMSGID));
		//newHTTPDLRRequestObject.setReason(request.getParameter(OCConstants.REQUEST_PARAM_REASON));
		newHTTPDLRRequestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
		newHTTPDLRRequestObject.setUserSMSTool(Constants.USER_SMSTOOL_CLICKATELL);
		//newHTTPDLRRequestObject.setTimeFormat(MyCalendar.FORMAT_DATETIME_STYEAR_ONE);
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.GATEWAY_BUSINESS_SERVICE);
		
		logger.debug("=======got the service ======");
		
		
		//PrintWriter pw = response.getWriter();
		
		try {
			logger.debug("=======calling the service ======");
			
			BaseResponseObject retResponse  = baseService.processRequest(newHTTPDLRRequestObject);
			
			logger.debug("=======ending the service ======"+retResponse);
			
		/*	response.setContentType("text/html");
			
			pw.write(retResponse.getResponseStr());*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}finally {
			
			/*pw.flush();
			pw.close();*/
		}
		return null;
	
		
	}
	
	
}
