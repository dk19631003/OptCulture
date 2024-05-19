package org.mq.optculture.restservice.faq;

import java.io.PrintWriter;
import java.util.Calendar;

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

public class FaqService extends AbstractController {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		try {
			logger.info("faq request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.FAQ_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.FAQ_BUSINESS_SERVICE);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
		} catch (Exception e) {
			logger.error("Exception ::" +e);
			logger.error("Exception ::" +e.getStackTrace());
		}
		finally {
			try {
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
			}catch (Exception e) {
				logger.error("Exception ::" +e.getStackTrace());
			}
		}
		return null;
	}
}