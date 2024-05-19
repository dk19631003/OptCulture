package org.mq.optculture.restservice.digitalReceipt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.digitalReceipt.PosDRRequest;
import org.mq.optculture.model.digitalReceipt.PosDRResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class PosDigitalReceiptsRestService  extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) {
		PosDRResponse posDRResponse = null;

		try {
			String userName = request.getParameter("userName");
			String userOrg = request.getParameter("userOrg");
			String jsonValue = OptCultureUtils.getParameterDRJsonValue(request) ;

			PosDRRequest posDRRequest = new PosDRRequest();
			posDRRequest.setUserName(userName);
			posDRRequest.setUserOrg(userOrg);
			posDRRequest.setJsonValue(jsonValue);
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.POS_DR_BUSINESS_SERVICE);
			posDRResponse = (PosDRResponse) baseService.processRequest(posDRRequest);
			
			if(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(posDRResponse.getResponseInfo().getStatus().getStatus())) {
				request.getRequestDispatcher("/sendDigitalReceipt.mqrm").forward(request,response);
			}
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} finally {
			try {
				Gson gson = new Gson();
				String json = gson.toJson(posDRResponse);
				PrintWriter pw = response.getWriter();
				pw.write(json);
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		
		return null;
	}
}
