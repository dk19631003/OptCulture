package org.mq.optculture.business.mobileapp;

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
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.UserDetails;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.PurchaseHistoryRequest;
import org.mq.optculture.model.mobileapp.PurchaseHistoryResponse;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatesku.SkuInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PurchaseHistoryRestService extends AbstractController{
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
			
			baseRequestObject.setAction(OCConstants.PURCHASE_HISTORY_SERVICE_REQUEST);
			baseRequestObject.setTransactionId(transactionID);
			
			baseRequestObject.setTransactionDate(transDate);
			Gson gson=new Gson();
			PurchaseHistoryRequest purchaseHistoryRequest;
			PurchaseHistoryResponse purchaseHistoryResponse=null;
			String responseJson = Constants.STRING_NILL;
			Status status = null;
			try {
				purchaseHistoryRequest = gson.fromJson(baseRequestObject.getJsonValue(), PurchaseHistoryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				purchaseHistoryResponse = prepareFinalResponse(header, status);
				responseJson = gson.toJson(purchaseHistoryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			
			}
			
			if(purchaseHistoryRequest == null){
				status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transDate, transactionID);
				purchaseHistoryResponse = prepareFinalResponse(header, status);
				responseJson = gson.toJson(purchaseHistoryResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			baseRequestObject.setAction(OCConstants.PURCHASE_HISTORY_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PURCHASE_HISTORY_SERVICE);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
			if(baseResponseObject.getAction().equals(OCConstants.PURCHASE_HISTORY_SERVICE_REQUEST)){
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
	private PurchaseHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		PurchaseHistoryResponse purchaseHistoryResponse = new PurchaseHistoryResponse();
		
		purchaseHistoryResponse.setHeader(header);
		List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
		MatchedCustomers matchedCustomer = new MatchedCustomers();
		matchedCustomers.add(matchedCustomer);
		purchaseHistoryResponse.setMatchedCustomers(matchedCustomers);
		//purchaseHistoryResponse.setReport(new ResponseReport());
		purchaseHistoryResponse.setStatus(status);
	
		return purchaseHistoryResponse;
	}//prepareFinalResponse
	
}
