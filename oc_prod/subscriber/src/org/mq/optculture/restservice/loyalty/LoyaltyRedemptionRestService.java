package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
/**
 * Loyalty Redemption(Points, USD) RESTFul Service Controller.
 * 
 * @author venkatd
 *
 */
public class LoyaltyRedemptionRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Redemption business service to process request.
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("Entered Loyalty Redemption Rest Service.");
		try{
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION);
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_BUSINESS_SERVICE);
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION)){
				String responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
			}
		}catch(Exception e){
			logger.error("Error in redemption rest service", e);
			String responseJson = "{\"LOYALTYREDEMPTIONRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			logger.info("Completed Loyalty redemption Rest Service.");
		}
		return null;
	}
}
