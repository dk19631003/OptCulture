package org.mq.optculture.restservice.generalMailSender;

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

public class GeneralMailSenderRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		try {
			logger.info("General mail; sender request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.GENERAL_MAIL_SENDER_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.GENERAL_MAIL_SENDER_BUSINESS_SERVICE);
			//logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		finally {
			try {
				if(baseResponseObject != null) {
					logger.info("baseResponseObject >>>> : "+baseResponseObject.getJsonValue().replaceAll("\\\\u0027", "'"));
				}else {
					logger.info("baseResponseObject is  : "+baseResponseObject);
				}
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue().replaceAll("\\\\u0027", "'"));
				pw.flush();
				pw.close();
			}catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}
		return null;
	}
}
