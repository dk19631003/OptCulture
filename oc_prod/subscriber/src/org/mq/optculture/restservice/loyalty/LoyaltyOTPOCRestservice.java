package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
/**
 * === OptCulture Loyalty Program ===
 * Loyalty OTP Issuance RESTFul Service Controller.
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyOTPOCRestservice extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty OTP business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Started Loyalty OTP Rest Service...");
		
		LoyaltyTransactionParent tranParent = createNewTransaction();
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyOTPResponse otpResponse = null;
		String userName = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson();
		response.setContentType("application/json");
		
		try{
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());
			
			LoyaltyOTPRequest otpRequest = null;
			try{
				otpRequest = gson.fromJson(requestJson, LoyaltyOTPRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(otpRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(otpRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(otpRequest.getHeader().getRequestId() == null || otpRequest.getHeader().getRequestId().trim().isEmpty() ||
					otpRequest.getHeader().getRequestDate() == null || otpRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(otpRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(otpRequest.getUser().getUserName() == null || otpRequest.getUser().getOrganizationId().trim().length() <=0 || 
					otpRequest.getUser().getOrganizationId() == null || otpRequest.getUser().getOrganizationId().trim().length() <=0 || 
							otpRequest.getUser().getToken() == null || otpRequest.getUser().getToken().trim().length() <=0) {
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status);
				responseJson = gson.toJson(otpResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, otpResponse, null, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			userName = otpRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					otpRequest.getUser().getOrganizationId();
			
			LoyaltyTransaction	transaction = logTransactionRequest(otpRequest, requestJson, OCConstants.LOYALTY_ONLINE_MODE);
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_OTP);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_OTP_OC_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyOTPResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyOTPResponse.class);
			
			updateTransactionStatus(transaction, responseObject.getJsonValue(), responseobject);
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_OTP)){
				responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				otpResponse = gson.fromJson(responseJson, LoyaltyOTPResponse.class);
				updateTransaction(tranParent, otpResponse, otpRequest.getCustomer().getPhone(), userName);
				logger.info("Response = "+responseJson);
				return null;
			}
		}catch(Exception e){
			logger.error("Error in OTP rest service", e);
			responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			otpResponse = gson.fromJson(responseJson, LoyaltyOTPResponse.class);
			updateTransaction(tranParent, otpResponse, null, userName);
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			
			logger.info("Completed Loyalty OTP Rest Service.");
		}
		return null;
	}

	private LoyaltyTransactionParent createNewTransaction(){
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);			
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_OTP);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
			
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private LoyaltyOTPResponse prepareOTPResponse(ResponseHeader header, Status status) throws BaseServiceException {
		LoyaltyOTPResponse otpResponse = new LoyaltyOTPResponse();
		otpResponse.setHeader(header);
		otpResponse.setStatus(status);
		return otpResponse;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyOTPResponse otpResponse, String phone, 
			String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(otpResponse.getStatus() != null) {
				trans.setStatus(otpResponse.getStatus().getStatus());
				trans.setErrorMessage(otpResponse.getStatus().getMessage());
			}
			if(otpResponse.getHeader() != null){
				trans.setRequestId(otpResponse.getHeader().getRequestId());
				trans.setRequestDate(otpResponse.getHeader().getTransactionDate());
			}
			trans.setMobilePhone(phone);
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
	public LoyaltyTransaction logTransactionRequest(LoyaltyOTPRequest requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;

		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHeader().getPcFlag()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_OTP);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setCustomerId(requestObject.getCustomer().getCustomerId().trim());
			transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			transaction.setStoreNumber(requestObject.getHeader().getStoreNumber().trim());
			transaction.setEmployeeId(requestObject.getHeader().getEmployeeId()!=null && !requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(requestObject.getHeader().getTerminalId()!=null && !requestObject.getHeader().getTerminalId().trim().isEmpty() ? requestObject.getHeader().getTerminalId().trim():null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyOTPResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
}
