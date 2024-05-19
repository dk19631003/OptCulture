package org.mq.optculture.restservice.pushNotification;

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



public class PushNotificationRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseRequestObject baseRequestObject=null;
		BaseResponseObject baseResponseObject=null;
		BaseService baseService=null;
		try {
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			logger.info("pushnotificationAPI request::"+jsonValue);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PUSH_NOTIFICATION_SERVICE);
			baseResponseObject=baseService.processRequest(baseRequestObject);
			logger.info("pushnotificationAPI responce::"+baseResponseObject.getJsonValue());
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally {
			try {
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		return null;
	}
}



