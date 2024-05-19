package org.mq.optculture.restservice.digitalReceipt;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.digitalReceipt.SendDRRequest;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

/**
 * This class handles RESTFul service call of digital receipts.
 * 
 * @author venkatd
 *
 */
public class DigitalReceiptsRestService extends AbstractController{

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SendDRResponse sendDRResponse = null;

		try {
			String userName = request.getParameter("userName");
			String userOrg = request.getParameter("userOrg");
			String jsonValue = OptCultureUtils.getParameterDRJsonValue(request);

			SendDRRequest sendDRRequest = new SendDRRequest();
			sendDRRequest.setUserName(userName);
			sendDRRequest.setUserOrg(userOrg);
			sendDRRequest.setJsonValue(jsonValue);
			sendDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.SEND_DR_BUSINESS_SERVICE);
			sendDRResponse = (SendDRResponse) baseService.processRequest(sendDRRequest);
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} finally {
			try {
				Gson gson = new Gson();
				String json = gson.toJson(sendDRResponse);
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
