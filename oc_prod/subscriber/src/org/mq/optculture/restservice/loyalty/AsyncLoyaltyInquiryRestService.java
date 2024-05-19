package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class AsyncLoyaltyInquiryRestService extends AbstractController {
	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);

	/**
	 * Delegates request. Calls Loyalty Inquiry business service to process
	 * request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("Entered Async Loyalty Inquiry Rest Service.");
		Gson gson = new Gson();
		try {
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = " + requestJson);
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject
					.setAction(OCConstants.ASYNC_LOYALTY_SERVICE_ACTION_INQUIRY);

			LoyaltyInquiryJsonRequest jsonRequestObject = null;
			try {
				jsonRequestObject = gson.fromJson(requestJson,
						LoyaltyInquiryJsonRequest.class);
			} catch (Exception e) {
				StatusInfo statusInfo = new StatusInfo("101002",
						PropertyUtil.getErrorMessage(101002,
								OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				LoyaltyInquiryResponseObject inquiryResponse = new LoyaltyInquiryResponseObject();
				inquiryResponse.setSTATUS(statusInfo);
				LoyaltyInquiryJsonResponse jsonResponseObject = new LoyaltyInquiryJsonResponse();
				jsonResponseObject.setLOYALTYINQUIRYRESPONSE(inquiryResponse);
				String responseJson = gson.toJson(jsonResponseObject);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = " + responseJson);
				return null;
			}

			LoyaltyTransaction transaction = null;
			transaction = logTransactionRequest(
					jsonRequestObject.getLOYALTYINQUIRYREQ(), requestJson,
					OCConstants.LOYALTY_ONLINE_MODE);
			
			Date date = transaction.getRequestDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			requestObject.setTransactionId(""+transaction.getId());
			requestObject.setTransactionDate(transDate);
			
			BaseService baseService = ServiceLocator.getInstance()
					.getServiceByName(
							OCConstants.ASYNC_LOYALTY_INQUIRY_BUSINESS_SERVICE);
			
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			
			logger.info("JSON Response: = " + responseObject.getJsonValue());

			updateTransactionStatus(transaction, responseObject.getJsonValue(),
					jsonRequestObject.getLOYALTYINQUIRYREQ());

			if (responseObject.getAction().equals(
					OCConstants.ASYNC_LOYALTY_SERVICE_ACTION_INQUIRY)) {
				String responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
			}
		} catch (Exception e) {
			logger.error("Error in inquiry rest service", e);
			String responseJson = "{\"LOYALTYINQUIRYRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = " + responseJson);
			return null;
		} finally {
			logger.info("Completed  Async Loyalty Inquiry Rest Service.");
		}
		return null;
	}

	public LoyaltyTransaction logTransactionRequest(
			LoyaltyInquiryRequestObject requestObject, String jsonRequest,
			String mode) {
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao) ServiceLocator
					.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");

			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setStoreNumber(requestObject.getHEADERINFO().getSTORENUMBER());
			transaction.setRequestId(requestObject.getHEADERINFO()
					.getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO()
					.getPCFLAG()));
			transaction.setMode(mode);// online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_INQUIRY);
			transaction.setUserDetail(requestObject.getUSERDETAILS()
					.getUSERNAME()
					+ "__"
					+ requestObject.getUSERDETAILS().getORGANISATION());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}

	public void updateTransactionStatus(LoyaltyTransaction transaction,
			String responseJson, LoyaltyInquiryRequestObject response) {
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao) ServiceLocator
					.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction
					.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			transaction
					.setCardNumber(response.getINQUIRYINFO().getCARDNUMBER());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in updating transaction", e);
		}
	}
}
