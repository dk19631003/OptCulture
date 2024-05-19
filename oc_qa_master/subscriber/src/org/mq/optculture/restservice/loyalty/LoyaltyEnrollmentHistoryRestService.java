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

public class LoyaltyEnrollmentHistoryRestService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject = null;
		
		try{
			logger.info("LoyaltyDataRestService request calling >>>> : ");
			String jsonValue = OptCultureUtils.getParameterJsonValue(request);
			
			BaseRequestObject baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ENROLLMENT_HISTORY_SERVICE_IMPL);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject = baseService.processRequest(baseRequestObject);
		}catch (Exception e) {
			logger.error("Exception :: "+e.getStackTrace());
		}
		finally {
			if(baseResponseObject != null) {
				logger.info("baseResponseObject >>>> : "+baseResponseObject.getJsonValue());
			}else {
				logger.info("baseResponseObject is  : "+baseResponseObject);
			}
			response.setContentType("application/json");
			PrintWriter pw = response.getWriter();
			pw.write(baseResponseObject.getJsonValue());
			pw.flush();
			pw.close();
		}
		
		return null;
	}

}
