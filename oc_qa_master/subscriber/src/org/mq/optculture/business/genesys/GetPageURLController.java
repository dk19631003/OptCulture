package org.mq.optculture.business.genesys;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.genesys.Data;
import org.mq.optculture.model.genesys.GetPageURLRequest;
import org.mq.optculture.model.genesys.GetPageURLResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

public class GetPageURLController extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info(">>>Started getPage URL  Rest Service.");
		
		response.setContentType("application/json");
		LoyaltyTransactionParent tranParent = createNewTransaction(); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		GetPageURLResponse getPageURLResponse = null;
		GetPageURLRequest getPageURLRequest = null;
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
				getPageURLRequest = gson.fromJson(requestJson, GetPageURLRequest.class);
			}catch(Exception e){
				
				getPageURLResponse = prepareGetPageURLResponse("", null, "");
				responseJson = gson.toJson(getPageURLResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, getPageURLResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(getPageURLRequest == null){
				getPageURLResponse = prepareGetPageURLResponse("", null, "");
				responseJson = gson.toJson(getPageURLResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, getPageURLResponse, null);
				return null;
			}
			
			if(getPageURLRequest.getHeader() == null){
				getPageURLResponse = prepareGetPageURLResponse("", null, "");
				responseJson = gson.toJson(getPageURLResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, getPageURLResponse, null);
				return null;
			}
			
			if(getPageURLRequest.getHeader().getRequestId() == null || getPageURLRequest.getHeader().getRequestId().trim().isEmpty()){
				getPageURLResponse = prepareGetPageURLResponse("", null, "");
				responseJson = gson.toJson(getPageURLResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, getPageURLResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(getPageURLRequest.getUser() == null || (getPageURLRequest.getUser().getUserName() == null || getPageURLRequest.getUser().getUserName().isEmpty())|| 
					(getPageURLRequest.getUser().getOrganizationId() == null || getPageURLRequest.getUser().getOrganizationId().isEmpty()) ){
				getPageURLResponse = prepareGetPageURLResponse("", null, "");
				responseJson = gson.toJson(getPageURLResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, getPageURLResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			userName = getPageURLRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					getPageURLRequest.getUser().getOrganizationId();
				
			/*userName = getPageURLRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					getPageURLRequest.getUser().getOrganizationId();
			*/
			//log transaction
			/*String pcFlag = getPageURLRequest.getHeader().getPcFlag();
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(getPageURLRequest.getHeader().getRequestId());
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					getPageURLResponse = gson.ufromJson(responseJson, LoyaltyInquiryResponse.class);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					updateTransaction(tranParent, getPageURLResponse, null);
					logger.info("Response = "+getPageURLResponse);
					return null;
				}
			}*/
			LoyaltyTransaction transaction = logTransactionRequest(getPageURLRequest, requestJson, "online", userName);
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.GET_PAGE_URL_ACTION);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.GET_PAGE_URL_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			getPageURLResponse = gson.fromJson(responseObject.getJsonValue(), GetPageURLResponse.class);
			
			//update getPageURLResponse in transaction
			if(getPageURLResponse != null){
				updateTransactionStatus(transaction, responseObject.getJsonValue(), getPageURLResponse);
			}
			if(responseObject.getAction().equals(OCConstants.GET_PAGE_URL_ACTION)){
				String responseJson1 = responseObject.getJsonValue();
				logger.info("Response json = "+responseJson1);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson1);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, getPageURLResponse, userName);
			}
		}catch(Exception e){
			getPageURLResponse = prepareGetPageURLResponse("", null, "");
			responseJson = gson.toJson(getPageURLResponse);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson, e);
			updateTransaction(tranParent, getPageURLResponse, userName);
			return null;
		}
		logger.info("Response = "+responseJson);
		logger.info("Completed Loyalty Inquiry Rest Service.");
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
			tranx.setTransactionType(OCConstants.GET_PAGE_URL);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private GetPageURLResponse prepareGetPageURLResponse(String status, List<Data> data, String error){
		GetPageURLResponse getPageURLResponse = new GetPageURLResponse();
		getPageURLResponse.setStatus(status);
		getPageURLResponse.setError(error);
		getPageURLResponse.setData(data);
		return getPageURLResponse;
		
		
	}
	private LoyaltyTransaction logTransactionRequest(GetPageURLRequest requestObject, String jsonRequest, String mode, String userName){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.GET_PAGE_URL);
			transaction.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			transaction.setUserDetail(userName);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, GetPageURLResponse response){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus() != null ? response.getStatus() : "");
			transaction.setJsonResponse(responseJson);
			
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private void updateTransaction(LoyaltyTransactionParent trans, GetPageURLResponse responseObject, String userName) {
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
				trans.setStatus(responseObject.getStatus());
			}
			
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
		
}
