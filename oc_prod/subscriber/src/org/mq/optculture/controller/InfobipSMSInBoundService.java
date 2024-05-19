/**
 * 
 */
package org.mq.optculture.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSInBoundRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author vinod.bokare
 *
 */
public class InfobipSMSInBoundService extends AbstractController{

	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		//get all the request parameteres and construct request VO object
		SMSInBoundRequestObject newInBoundReqObj = new SMSInBoundRequestObject();
		
		newInBoundReqObj.setAction(request.getParameter(OCConstants.REQUEST_PARAM_TYPE));//base class's method
		newInBoundReqObj.setSource(request.getParameter(OCConstants.REQUEST_PARAM_SOURCE));
		newInBoundReqObj.setDestination(request.getParameter(OCConstants.REQUEST_PARAM_DEST));
		newInBoundReqObj.setReceivedTimeStr(request.getParameter(OCConstants.REQUEST_PARAM__MSG_STIME));
		newInBoundReqObj.setMsgContent(request.getParameter(OCConstants.REQUEST_PARAM__MSG));//base class's method
		
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.GATEWAY_BUSINESS_SERVICE);
		PrintWriter pw = response.getWriter();
		
		try {
			BaseResponseObject retResponse  = baseService.processRequest(newInBoundReqObj);
			
			response.setContentType("text/html");
			
			pw.write(retResponse.getResponseStr());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}finally {
			
			pw.flush();
			pw.close();
		}
		return null;
	}
	
}

