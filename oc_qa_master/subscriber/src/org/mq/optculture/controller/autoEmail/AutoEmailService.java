package org.mq.optculture.controller.autoEmail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.autoEmail.AutoEmailReportRequest;
import org.mq.optculture.model.autoEmail.AutoEmailReportResponse;
import org.mq.optculture.model.campaign.CampaignReportResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class AutoEmailService extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String action =  request.getParameter("action");
		String eqId = request.getParameter("sentId");
		String url = request.getParameter("url");
		if(action == null || eqId == null || (action.equalsIgnoreCase(OCConstants.UPDATE_REPORT_ACTION_CLICK) && url == null) ){
			logger.info("Invallid request in email");
			return null;
		}
		AutoEmailReportRequest autoEmailReportRequest = new AutoEmailReportRequest();
		autoEmailReportRequest.setAction(action);
		autoEmailReportRequest.setSentId(eqId);
		autoEmailReportRequest.setUrl(url); 
		BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.AUTO_EMAIL_REPORT_BUSINESS_SERVICE);
		AutoEmailReportResponse autoEmailReportResponse = (AutoEmailReportResponse) baseService.processRequest(autoEmailReportRequest);
		
		if(OCConstants.RESPONSE_TYPE_OPEN_IMAGE.equalsIgnoreCase(autoEmailReportResponse.getResponseType())) {
			try {
				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setDateHeader("Expires", 0); // prevents caching at the proxy server
				response.setContentType("image/gif");
				File imgFile = new File(PropertyUtil.getPropertyValue("appDirectory") + "/img/transparent.gif");
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				try {
					byte b[] = new byte[8];
					int count;
					while((count=in.read(b)) != -1) {
						out.write(b,0,count);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}finally {
					out.flush();
					out.close();
					in.close();
				}
			}catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
			}

		}
		else if(OCConstants.RESPONSE_TYPE_CLICK_REDIRECT.equalsIgnoreCase(autoEmailReportResponse.getResponseType())) {
			try {
				if(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE.equals(autoEmailReportResponse.getStatus())) {
					return null;
				}
				response.sendRedirect(autoEmailReportResponse.getUrlStr());
				
			}catch (Exception e) {
				logger.error("Exception : Problem while sending the open image \n", e);
			}

		}
		return null;
	}

}
