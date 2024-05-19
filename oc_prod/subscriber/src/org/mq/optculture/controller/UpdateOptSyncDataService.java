package org.mq.optculture.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OptSyncDataRequestObject;
import org.mq.optculture.model.sparkbase.TokenRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController; 

import com.newrelic.api.agent.Trace;

public class UpdateOptSyncDataService extends AbstractController{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Trace
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {


		BaseService baseService=null;
		BaseResponseObject baseResponseObject=null;
		OptSyncDataRequestObject optSyncDataRequestObject= null;
		try {


			optSyncDataRequestObject = new OptSyncDataRequestObject();
			String jsonValue = OptCultureUtils.getParameterDRJsonValue(request) ;
			optSyncDataRequestObject.setJsonValue(jsonValue);
		
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.OPT_SYNC_UPDATE_SERVICE);
			baseResponseObject = baseService.processRequest(optSyncDataRequestObject);

			//logger.debug("Optsync caller ip address value is"+request.getRemoteAddr());

		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally {
			try {
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				
				logger.error("Exception  occured from Optsync ::" +e.getMessage()+ "----> Remote Ip Address:" +request.getRemoteAddr());
			}
		}
		return null;

	}

}


