package org.mq.optculture.business.account;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.account.OCUserLoginRequest;
import org.mq.optculture.model.account.OCUserLoginResponse;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginResponse;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class OCUserLoginRestService extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		logger.info(">>>Started User Login Rest Service.");
		
		response.setContentType("application/json");
		
		
		OCUserLoginResponse loginResponse = null;
		OCUserLoginRequest loginRequest = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson();
		String userName = null;
		
		try{
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			
			try{
				loginRequest = gson.fromJson(requestJson, OCUserLoginRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loginResponse = prepareLoginResponse(status, null, null);
				responseJson = gson.toJson(loginResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(loginRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loginResponse = prepareLoginResponse(status, null, null);
				responseJson = gson.toJson(loginResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(loginRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loginResponse = prepareLoginResponse(status, null, null);
				responseJson = gson.toJson(loginResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(loginRequest.getHeader().getRequestId() == null || loginRequest.getHeader().getRequestId().trim().isEmpty() ||
					loginRequest.getHeader().getRequestDate() == null || loginRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loginResponse = prepareLoginResponse(status, null, null);
				responseJson = gson.toJson(loginResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(loginRequest.getUser() == null || (loginRequest.getUser().getUserName() == null || loginRequest.getUser().getUserName().isEmpty())|| 
					(loginRequest.getUser().getOrganizationId() == null || loginRequest.getUser().getOrganizationId().isEmpty()) ){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loginResponse = prepareLoginResponse(status, null, null);
				responseJson = gson.toJson(loginResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			userName = loginRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					loginRequest.getUser().getOrganizationId();
				
			
			LoyaltyTransaction transaction = logTransactionRequest(loginRequest, requestJson, "online", userName);
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_USER_LOGIN);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.OC_USER_LOGIN_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			loginResponse = gson.fromJson(responseObject.getJsonValue(), OCUserLoginResponse.class);
			
			//update loginResponse in transaction
			if(loginResponse != null){
				updateTransactionStatus(transaction, responseObject.getJsonValue(), loginResponse);
			}
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_USER_LOGIN)){
				String responseJson1 = responseObject.getJsonValue();
				logger.info("Response json = "+responseJson1);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson1);
				printWriter.flush();
				printWriter.close();
			}
		}catch(Exception e){
			ResponseHeader responseHeader = null;
			
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			loginResponse = prepareLoginResponse(status, null, null);
			responseJson = gson.toJson(loginResponse);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson, e);
			return null;
		}
		logger.info("Response = "+responseJson);
		logger.info("Completed User Login Rest Service.");
		return null;
	
		
		
	}
	
	private OCUserLoginResponse prepareLoginResponse( Status status, String sessionID, String APItoken) throws BaseServiceException {
		
		OCUserLoginResponse loginResponse = new OCUserLoginResponse();
		loginResponse.setStatus(status);
		loginResponse.setSessionID(sessionID);
		loginResponse.setAPIToken(APItoken);
		
		return loginResponse;
		
	}
	
	private LoyaltyTransaction logTransactionRequest(OCUserLoginRequest requestObject, String jsonRequest, String mode, String userName){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_USER_LOGIN);
			transaction.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			transaction.setUserDetail(userName);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, OCUserLoginResponse response){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
}
