package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
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
 * Loyalty Inquiry RESTFul Service Controller.
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyInquiryOCRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Inquiry business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info(">>>Started Loyalty Inquiry Rest Service.");
		
		response.setContentType("application/json");
		LoyaltyTransactionParent tranParent = createNewTransaction(); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyInquiryResponse inquiryResponse = null;
		LoyaltyInquiryRequest inquiryRequest = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson();
		String userName = null;
		
		try{
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());
			
			try{
				inquiryRequest = gson.fromJson(requestJson, LoyaltyInquiryRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, inquiryResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(inquiryRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, inquiryResponse, null);
				return null;
			}
			
			if(inquiryRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, inquiryResponse, null);
				return null;
			}
			
			if(inquiryRequest.getHeader().getRequestId() == null || inquiryRequest.getHeader().getRequestId().trim().isEmpty() ||
					inquiryRequest.getHeader().getRequestDate() == null || inquiryRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, inquiryResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			String reqSource = inquiryRequest.getHeader().getSourceType();
			if(reqSource != null && reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)){
				status = new Status("800032", PropertyUtil.getErrorMessage(800032, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, inquiryResponse, null);
				logger.info("Response = "+responseJson);
				return null;
				
			}
			
			if(inquiryRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(inquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, inquiryResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			userName = inquiryRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					inquiryRequest.getUser().getOrganizationId();
			
			//log transaction
			LoyaltyTransaction transaction = null;
			String pcFlag = inquiryRequest.getHeader().getPcFlag();
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(inquiryRequest.getHeader().getRequestId());
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					inquiryResponse = gson.fromJson(responseJson, LoyaltyInquiryResponse.class);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					updateTransaction(tranParent, inquiryResponse, null);
					logger.info("Response = "+inquiryResponse);
					return null;
				}
			}
			if(transaction == null){
				transaction = logTransactionRequest(inquiryRequest, requestJson, "online");
			}
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_INQUIRY);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_INQUIRY_OC_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			inquiryResponse = gson.fromJson(responseObject.getJsonValue(), LoyaltyInquiryResponse.class);
			
			//update inquiryResponse in transaction
			if(inquiryResponse != null){
				updateTransactionStatus(transaction, responseObject.getJsonValue(), inquiryResponse);
			}
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_INQUIRY)){
				String responseJson1 = responseObject.getJsonValue();
				logger.info("Response json = "+responseJson1);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson1);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, inquiryResponse, userName);
			}
		}catch(Exception e){
			ResponseHeader responseHeader = null;
			if(inquiryResponse == null){
				responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
			}
			else{
				responseHeader = inquiryResponse.getHeader();
			}
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
			responseJson = gson.toJson(inquiryResponse);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson, e);
			updateTransaction(tranParent, inquiryResponse, userName);
			return null;
		}
		logger.info("Response = "+responseJson);
		logger.info("Completed Loyalty Inquiry Rest Service.");
		return null;
	}	
	
	private LoyaltyInquiryResponse prepareInquiryResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyInquiryResponse inquiryResponse = new LoyaltyInquiryResponse();
		inquiryResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber("");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}
		if(balances == null){
			balances = new ArrayList<Balance>();
		}
		if(holdBalance == null){
			holdBalance = new HoldBalance();
			holdBalance.setActivationPeriod("");
			holdBalance.setCurrency("");
			holdBalance.setPoints("");
		}
		if(additionalInfo == null){
			additionalInfo = new AdditionalInfo();
			additionalInfo.setOtpEnabled("");
			/*OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount("");
			otpRedeemLimit.setValueCode("");*/
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			//otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency("");
			additionalInfo.setTotalRedeemableCurrency("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		inquiryResponse.setMembership(membershipResponse);
		inquiryResponse.setBalances(balances);
		inquiryResponse.setHoldBalance(holdBalance);
		inquiryResponse.setAdditionalInfo(additionalInfo);
		inquiryResponse.setMatchedCustomers(matchedCustomers);
		inquiryResponse.setStatus(status);
		return inquiryResponse;
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
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyInquiryResponse responseObject, String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(responseObject == null){
				return;
			}
			if(responseObject.getStatus() != null) {
				trans.setStatus(responseObject.getStatus().getStatus());
				trans.setErrorMessage(responseObject.getStatus().getMessage());
			}
			if(responseObject.getHeader() != null){
				trans.setRequestId(responseObject.getHeader().getRequestId());
				trans.setRequestDate(responseObject.getHeader().getRequestDate());
			}
			if(responseObject.getMembership() != null) {
				if(responseObject.getMembership().getCardNumber() != null && !responseObject.getMembership().getCardNumber().trim().isEmpty()){
					trans.setMembershipNumber(responseObject.getMembership().getCardNumber());
				}
				else{
					trans.setMembershipNumber(responseObject.getMembership().getPhoneNumber());
				}
			}
			if(responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().size() >=1 
					&& responseObject.getMatchedCustomers().get(0) != null) {
				trans.setMobilePhone(responseObject.getMatchedCustomers().get(0).getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
	private LoyaltyTransaction logTransactionRequest(LoyaltyInquiryRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_INQUIRY);
			transaction.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			if(requestObject.getHeader().getDocSID() != null && !requestObject.getHeader().getDocSID().trim().isEmpty()){
				transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			}
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
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyInquiryResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			if(response.getMembership() != null && response.getMembership().getCardNumber() != null 
					&& !response.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(response.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestId(requestId, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
}
