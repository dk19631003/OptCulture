/**
 * 
 */
package org.mq.optculture.restservice.magento;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class MagentoRestService extends AbstractController{

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseRequestObject baseRequestObject=null;
		BaseResponseObject baseResponseObject=null;
		BaseService baseService=null;
		try {
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.MAGENTO_BUSINESS_SERVICE);
			baseResponseObject=baseService.processRequest(baseRequestObject);
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
