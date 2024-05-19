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
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyDataRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyDataResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyReportResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomerReport;
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
 * Loyalty Enrolment History RESTFul Service Controller.
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyDataOCRestService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject = null;
		
		LoyaltyTransactionParent tranParent = createNewTransaction();
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyDataResponse dataResponse = null;
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
			
			LoyaltyDataRequest dataRequest = null;
			try{
				dataRequest = gson.fromJson(requestJson, LoyaltyDataRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(dataRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(dataRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(dataRequest.getHeader().getRequestId() == null || dataRequest.getHeader().getRequestId().trim().isEmpty() ||
					dataRequest.getHeader().getRequestDate() == null || dataRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(dataRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(dataRequest.getUser().getUserName() == null || dataRequest.getUser().getOrganizationId().trim().length() <=0 || 
					dataRequest.getUser().getOrganizationId() == null || dataRequest.getUser().getOrganizationId().trim().length() <=0 || 
							dataRequest.getUser().getToken() == null || dataRequest.getUser().getToken().trim().length() <=0) {
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dataResponse = prepareDataResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(dataResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, dataResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			userName = dataRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					dataRequest.getUser().getOrganizationId();
			
			logger.info("LoyaltyDataRestService request calling >>>> : ");
			BaseRequestObject baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(requestJson);
			baseRequestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_FETCHDATA);
			baseRequestObject.setTransactionId(""+tranParent.getTransactionId());
			baseRequestObject.setTransactionDate(transDate);
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_DATA_OC_SERVICE_IMPL);
			baseResponseObject = baseService.processRequest(baseRequestObject);
			if(baseResponseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_FETCHDATA)){
				responseJson = baseResponseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				dataResponse = gson.fromJson(responseJson, LoyaltyDataResponse.class);
				updateTransaction(tranParent, dataResponse, userName);
				logger.info("Response = "+responseJson);
				return null;
			}
		}catch(Exception e){
			logger.error("Error in loyalty data oc  rest service", e);
			responseJson = "{\"Status\":{\"errorCode\":\"101000\",\"message\":\"Server error  101000.\",\"status\":\"Failure\"}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			dataResponse = gson.fromJson(responseJson, LoyaltyDataResponse.class);
			updateTransaction(tranParent, dataResponse, userName);
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			logger.info("Completed Loyalty data oc Rest Service.");
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
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENTHISTORY);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private LoyaltyDataResponse prepareDataResponse(ResponseHeader header, List<MatchedCustomerReport> matchedCustomers,
			LoyaltyReportResponse report, Status status) throws BaseServiceException {
		LoyaltyDataResponse dataResponse = new LoyaltyDataResponse();
		dataResponse.setHeader(header);
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomerReport>();
		}
		if(report == null){
			report = new LoyaltyReportResponse();
		}
		dataResponse.setReport(report);
		dataResponse.setStatus(status);
		return dataResponse;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyDataResponse dataResponse, String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(dataResponse.getStatus() != null) {
				trans.setStatus(dataResponse.getStatus().getStatus());
				trans.setErrorMessage(dataResponse.getStatus().getMessage());
			}
			if(dataResponse.getHeader() != null){
				trans.setRequestId(dataResponse.getHeader().getRequestId());
				trans.setRequestDate(dataResponse.getHeader().getTransactionDate());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
}
