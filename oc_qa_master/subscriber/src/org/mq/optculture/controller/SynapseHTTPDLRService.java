package org.mq.optculture.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.jruby.ext.LateLoadingLibrary;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.SMSBounces;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSBouncesDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.business.gateway.SynapseBusinessService;
import org.mq.optculture.business.gateway.SynapseDLRProcessThread;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DLRRequests;
import org.mq.optculture.model.EquenceDLRRequestObject;
import org.mq.optculture.model.SynapseDLRRequestObject;
import org.mq.optculture.model.SynapseDLRResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class SynapseHTTPDLRService extends AbstractController{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Entered ");
		//get all the request parameteres and construct request VO object
		response.setContentType("application/json");
		String jsonValue = OptCultureUtils.getParameterJsonValue(request);
		Gson gson = new Gson();
		logger.debug("jsonValue "+jsonValue);
		SynapseDLRResponseObject retResponse =null;
		DLRRequests dlr= new DLRRequests();
		try{
		dlr = gson.fromJson(jsonValue, DLRRequests.class);
		List<SynapseDLRRequestObject> requestList =null;
		requestList=dlr.getDlrRequests();
		
		
		
		String responseJson = "{\"MESSAGE\":\"Success.\"}";
		SynapseDLRProcessThread synapseProcess = new SynapseDLRProcessThread(requestList);
		synapseProcess.start();
		/*retResponse.setAction(requestList.get(0).getAction());
		retResponse.setJsonValue(responseJson);*/
		PrintWriter printWriter = response.getWriter();
		printWriter.write(responseJson);
		printWriter.flush();
		printWriter.close();
		//processCampaignDLR(requestList);
			return null;
		}catch(Exception e){
			logger.info("Exception",e);
			String responseJson = "{\"MESSAGE\":\"Invalid request.\"}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			
			return null;
		}
	}
	
	
	
	
}
