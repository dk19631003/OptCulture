package org.mq.optculture.restservice.events;

import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.events.EventRequest;
import org.mq.optculture.model.events.EventResponse;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class EventsService extends AbstractController {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		try {
			logger.info("import contact request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.EVENTS_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.EVENTS_BUSINESS_SERVICE);
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
	private LoyaltyTransaction logTransactionRequest(EventRequest eventRequest, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(eventRequest.getHeader().getRequestId());
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_EVENTS);
			transaction.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			transaction.setUserDetail(eventRequest.getUser().getUserName()+"__"+eventRequest.getUser().getOrganizationId());
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, EventResponse eventResponse){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(eventResponse.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	
}
