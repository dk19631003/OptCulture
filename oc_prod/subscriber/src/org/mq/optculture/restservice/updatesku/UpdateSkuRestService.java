package org.mq.optculture.restservice.updatesku;

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

public class UpdateSkuRestService extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		try {
		//	logger.info("update sku request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.UPDATE_SKU_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_SKU_BUSINESS_SERVICE);
	//		logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
		} catch (Exception e) {
		//	e.printStackTrace();
		//	logger.error("Exception ::" +e.getStackTrace());
		}
		finally {
			try {
				if(baseResponseObject != null) {
				//	logger.info("baseResponseObject >>>> : "+baseResponseObject.getJsonValue());
				}else {
				//	logger.info("baseResponseObject is  : "+baseResponseObject);
				}
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
			//	e.printStackTrace();
			//	logger.error("Exception ::" +e.getStackTrace());
			}
		}
		return null;
	}

}
