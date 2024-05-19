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
/**
 * 
 * @author vinod.bokare
 *
 */
public class InfobipSMSInBoundService  extends AbstractController{

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Started Processing InfobipSMSInBoundService >> handleRequestInternal>> ");
		//get all the request parameteres and construct request VO object
		SMSInBoundRequestObject newInBoundReqObj = new SMSInBoundRequestObject();
		
		newInBoundReqObj.setAction(request.getParameter(OCConstants.REQUEST_PARAM_TYPE));//base class's method
		newInBoundReqObj.setSource(request.getParameter(OCConstants.REQUEST_PARAM_SOURCE));
		newInBoundReqObj.setDestination(request.getParameter(OCConstants.REQUEST_PARAM_DEST));
		newInBoundReqObj.setReceivedTimeStr(request.getParameter(OCConstants.REQUEST_PARAM_MSG_STIME));
		newInBoundReqObj.setMsgContent(request.getParameter(OCConstants.REQUEST_PARAM_MSG));//base class's method
		newInBoundReqObj.setTimeFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		newInBoundReqObj.setFromCountry(Constants.SMS_COUNTRY_UAE);
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.GATEWAY_BUSINESS_SERVICE);
		
		logger.debug("=======got the service ======");
		
		
		//PrintWriter pw = response.getWriter();
		
		try {
			logger.debug("======= source ======"+newInBoundReqObj.getSource() );
			logger.debug("=======Dest ======"+newInBoundReqObj.getDestination() );
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
		logger.info("Completed Processing InfobipSMSInBoundService >> handleRequestInternal>> ");
		return null;
	}
	
}

