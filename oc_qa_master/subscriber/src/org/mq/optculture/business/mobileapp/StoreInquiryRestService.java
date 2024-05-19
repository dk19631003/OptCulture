package org.mq.optculture.business.mobileapp;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.FilterBy;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.StoreInquiry;
import org.mq.optculture.model.mobileapp.StoreInquiryRequest;
import org.mq.optculture.model.mobileapp.StoreInquiryResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class StoreInquiryRestService extends AbstractController{

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		
		
		Date date = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		

		String transDate = df.format(date);
		String transactionID = System.currentTimeMillis()+Constants.STRING_NILL;
		String userName = null;
		try {
			logger.info("store Inquiry request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			
		//	baseRequestObject.setAction(OCConstants.COUPONS_HISTORY_SERVICE_REQUEST);
			baseRequestObject.setTransactionId(transactionID);
			
			baseRequestObject.setTransactionDate(transDate);
			Gson gson=new Gson();
			StoreInquiryRequest storeInquiryRequest;
			StoreInquiryResponse storeInquiryResponse=null;
			String responseJson = Constants.STRING_NILL;
			Status status = null;
			try {
				storeInquiryRequest = gson.fromJson(baseRequestObject.getJsonValue(), StoreInquiryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				storeInquiryResponse = prepareFinalResponse(header, status);
				responseJson = gson.toJson(storeInquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			
			}
			if(storeInquiryRequest == null){
				status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader reponseheader = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				storeInquiryResponse = prepareFinalResponse(reponseheader, status);
				responseJson = gson.toJson(storeInquiryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logTransactionRequest(storeInquiryRequest, jsonValue,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return null;
			}
			
			String userDetails = storeInquiryRequest.getUser().getUserName()+"__"+storeInquiryRequest.getUser().getOrganizationId();
			userName = storeInquiryRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					storeInquiryRequest.getUser().getOrganizationId();
			
		
			String requestId = storeInquiryRequest.getHeader().getRequestId();
			String requestDate = storeInquiryRequest.getHeader().getRequestDate();

				LoyaltyTransaction trans = findDuplicateRequest(requestId, userDetails);
				if(trans != null){
					ResponseHeader reponseheader = new ResponseHeader(requestId,
							requestDate, transDate, transactionID);
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					storeInquiryResponse = prepareFinalResponse(reponseheader, status);
					responseJson = gson.toJson(storeInquiryResponse);
					
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					logTransactionRequest(storeInquiryRequest, jsonValue,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return null;
				}
				else {
					trans = logTransactionRequest(storeInquiryRequest, jsonValue,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				}
				
			
		
			
			
			
			
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.STORE_INQUIRY_SERVICE);
		//	logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
			storeInquiryResponse = gson.fromJson(baseResponseObject.getJsonValue(), StoreInquiryResponse.class);
			if(storeInquiryResponse != null){
				updateTransactionStatus(trans, baseResponseObject.getJsonValue(), storeInquiryResponse);
			}
				responseJson = baseResponseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
			//	logger.info("Response = "+responseJson);
				return null;
				
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		return null;
	}
	private StoreInquiryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		
		StoreInquiryResponse storeInquiryResponse = new StoreInquiryResponse();
		
		storeInquiryResponse.setHeader(header);
		
		StoreInquiry storeInquiry=new StoreInquiry();
		storeInquiryResponse.setStoreInquiry(storeInquiry);
		FilterBy filterBy = new FilterBy();
		storeInquiryResponse.setFilterBy(filterBy);
		storeInquiryResponse.setStatus(status);
	
		return storeInquiryResponse;
	}//prepareFinalResponse
	
	private LoyaltyTransaction findDuplicateRequest(String requestId, String userDetails) {
		LoyaltyTransaction loyaltyTransaction = null;
		try {
			LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransaction =  loyaltyTransactionDao.findRequestByTypeReqId(requestId, userDetails);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyTransaction;
	}
private LoyaltyTransaction logTransactionRequest(StoreInquiryRequest storeInquiryRequest, String jsonRequest,String status){
		
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(storeInquiryRequest.getHeader().getRequestId());
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.TRANS_TYPE_STOREINQUIRY);
			transaction.setUserDetail(storeInquiryRequest.getUser().getUserName()+"__"+storeInquiryRequest.getUser().getOrganizationId());
			
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, StoreInquiryResponse response){
	LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
	try {
		loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_DAO_FOR_DML);
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
