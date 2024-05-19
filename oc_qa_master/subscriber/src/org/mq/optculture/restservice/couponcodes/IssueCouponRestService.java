package org.mq.optculture.restservice.couponcodes;

import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.Coupon;
import org.mq.optculture.model.couponcodes.IssueCouponRequest;
import org.mq.optculture.model.couponcodes.IssueCouponResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.updatecontacts.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class IssueCouponRestService extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		Status status = null;
		String responseJson = "";
		IssueCouponResponse issueCouponResponse =null;
		IssueCouponRequest issueCouponRequest=null;
		Gson gson = new Gson();
		try {
			logger.info("issue coupon request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			logger.info("issue coupon request calling >>>> jsonValue: "+jsonValue);
			baseRequestObject = new BaseRequestObject(); 
			baseRequestObject.setJsonValue(jsonValue);
		    baseRequestObject.setAction(OCConstants.IMPORT_CONTACTS_SERVICE_REQUEST);
		    baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.ISSUE_COUPON_BUSINESS_SERVICE);
		    baseResponseObject =baseService.processRequest(baseRequestObject);
		    logger.info("BaseResponseObj value >>>> : "+baseResponseObject);
//		    JSONObject  data = (JSONObject) JSONValue.parse(jsonValue);
//			JSONObject coupondata = (JSONObject) data.get("coupon");
//			logger.info("json object coupon values >>>> : "+coupondata);
//			String couponCode=coupondata.get("coupon_code").toString();
//			String couponValue=coupondata.get("coupon_value").toString();
			//String couponValue=data.get("coupon_value").toString();
			
			// this was needed - but since u already had all those service classes
				
		} catch (Exception e) {
			logger.error("Exception value hai line-73 IssueCouponRestService ::" +e.getStackTrace());
		}
		finally {
			try {
				if(baseResponseObject != null) {
					logger.info("baseResponseObject 334>>>> : "+baseResponseObject.getJsonValue());
				}else {
					logger.info("baseResponseObject is new : "+baseResponseObject);
					logger.info("BaseRequestObj value 123>>>> : "+baseRequestObject);
				}
				response.setContentType("application/json");
				logger.info("reponse set content>>>> : "+response);
				PrintWriter pw = response.getWriter();
				logger.info("reponse set content> pw>>> : "+pw);
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				logger.error("Exception ::" +e.getStackTrace());
			}
		}
		return null;
	}
//	private IssueCouponResponse prepareFinalResponse( Status status, IssueCouponRequest issueCouponRequest)
//			throws BaseServiceException {
//		logger.debug("-------entered prepareFinalResponse---------");
//		IssueCouponResponse issueCouponResponse = new IssueCouponResponse();
//		
//		issueCouponResponse.setStatus(status);
//		logger.debug("-------exit  prepareFinalResponse---------");
//		return issueCouponResponse;
//	}// prepareFinalResponse
}
