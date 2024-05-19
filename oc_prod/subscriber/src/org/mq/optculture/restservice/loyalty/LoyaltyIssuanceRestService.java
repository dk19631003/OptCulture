package org.mq.optculture.restservice.loyalty;

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
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

/**
 * Loyalty Issuance(Points, USD) RESTFul Service Controller.
 *  
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyIssuanceRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Issuance business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("Started Loyalty Issuance Rest Service...");
		try{
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			
			Gson gson = new Gson();
			LoyaltyIssuanceJsonRequest jsonRequest = null;
			
			try{
				jsonRequest = gson.fromJson(requestJson, LoyaltyIssuanceJsonRequest.class);
			}catch(Exception e){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101002\",\"MESSAGE\":\"Error \"101002\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			//TODO: CHANGE RESPONSE
			if(jsonRequest == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101002\",\"MESSAGE\":\"Error \"101002\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(jsonRequest.getLOYALTYISSUANCEREQ() == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"200001\",\"MESSAGE\":\"Error \"200001\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO() == null){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"1004\",\"MESSAGE\":\"Error \"1004\": Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}

			//			String pcFlag = jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getPCFLAG();
			String requestId = jsonRequest.getLOYALTYISSUANCEREQ().getHEADERINFO().getREQUESTID();
			LoyaltyTransaction transaction = null;
			//transaction = findTransactionByRequestId(requestId);
			//			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
			transaction = findTransactionByRequestId(requestId);
			if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
				String responseJson = transaction.getJsonResponse();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
				String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101020\": Request is being processed..\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			//			}

			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(jsonRequest.getLOYALTYISSUANCEREQ(), requestJson, "online");
			}
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE);
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ISSUANCE_BUSINESS_SERVICE);
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			
			LoyaltyIssuanceJsonResponse jsonResponseObject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyIssuanceJsonResponse.class);
			
			updateTransactionStatus(transaction, responseObject.getJsonValue(), jsonResponseObject.getLOYALTYISSUANCERESPONSE());
			
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE)){
				String responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
		}catch(Exception e){
			logger.error("Error in issuancerestservice", e);
			String responseJson = "{\"LOYALTYISSUANCERESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			logger.info("Completed Loyalty Issuance Rest Service.");
		}
		return null;
	}
	
	public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestIdAndType(requestId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyIssuanceRequestObject requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
						
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponseObject response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			transaction.setCardNumber(response.getISSUANCEINFO().getCARDNUMBER());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
}
