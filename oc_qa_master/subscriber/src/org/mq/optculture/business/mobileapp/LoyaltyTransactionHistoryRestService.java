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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.LoyaltyMatchedCustomers;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryRequest;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryResponse;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.PurchaseHistoryRequest;
import org.mq.optculture.model.mobileapp.PurchaseHistoryResponse;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class LoyaltyTransactionHistoryRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		
		
		Date date = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		String transactionID = System.currentTimeMillis()+Constants.STRING_NILL;
		try {
			logger.info("purchase history request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			
			baseRequestObject.setAction(OCConstants.LTYTRX_HISTORY_SERVICE_REQUEST);
			baseRequestObject.setTransactionId(transactionID);
			
			baseRequestObject.setTransactionDate(transDate);
			Gson gson=new Gson();
			LoyaltyTransactionHistoryRequest ltyTrxHistoryRequest;
			LoyaltyTransactionHistoryResponse ltyTrxHistoryResponse=null;
			String responseJson = Constants.STRING_NILL;
			Status status = null;
			try {
				ltyTrxHistoryRequest = gson.fromJson(baseRequestObject.getJsonValue(), LoyaltyTransactionHistoryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				ltyTrxHistoryResponse = prepareFinalResponse(header, status);
				responseJson = gson.toJson(ltyTrxHistoryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			
			}
			
			if(ltyTrxHistoryRequest == null){
				status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				ltyTrxHistoryResponse = prepareFinalResponse(header, status);
				responseJson = gson.toJson(ltyTrxHistoryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			baseRequestObject.setAction(OCConstants.LTYTRX_HISTORY_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.LTYTRX_HISTORY_SERVICE);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
			if(baseResponseObject.getAction().equals(OCConstants.LTYTRX_HISTORY_SERVICE_REQUEST)){
				responseJson = baseResponseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		return null;
	}
	private LoyaltyTransactionHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		LoyaltyTransactionHistoryResponse ltyTrxHistoryResponse = new LoyaltyTransactionHistoryResponse();
		
		ltyTrxHistoryResponse.setHeader(header);
		List<LoyaltyMatchedCustomers> matchedCustomers = new ArrayList<LoyaltyMatchedCustomers>();
		LoyaltyMatchedCustomers matchedCustomer = new LoyaltyMatchedCustomers();
		matchedCustomers.add(matchedCustomer);
		ltyTrxHistoryResponse.setMatchedCustomers(matchedCustomers);
		//ltyTrxHistoryResponse.setReport(new ResponseReport());
		ltyTrxHistoryResponse.setStatus(status);
	
		return ltyTrxHistoryResponse;
	}//prepareFinalResponse
	


}
