package org.mq.optculture.restservice.digitalReceipt;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.digitalReceipt.DRToLtyExtractionImpl;
import org.mq.optculture.business.gateway.EquenceBusinessService;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.AmountDetails;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.EnrollmentInfo;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonResponse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class DRToLtyExtractionRestService extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        
        String drJsonId=request.getParameter("drJsonId");
		logger.info("pinged "+drJsonId);
		
		DRToLtyExtractionService DRToLtyExtractionService = (DRToLtyExtractionImpl) ServiceLocator.getInstance().getServiceByName(OCConstants.DR_TO_LTY_EXTRACTION_IMPL);
		DRToLtyExtractionService.processRequest(drJsonId);
		return null;
	}
	
	
	public static void main(String[] args) throws Exception{
		
		String json ="{\"Head\":{\"user\":{\"userName\":\"ramakrishna\",\"organizationId\":\"ocqa\",\"token\":\"BHANZ1UC1BJPZBJO\"}}}";
		
		JSONObject jsonObject = (JSONObject)JSONValue.parse(json);
		JSONObject jsonHead=(JSONObject)jsonObject.get("Head");
		JSONObject userJsonObj = (JSONObject)jsonHead.get("user");
		System.out.println("userName "+userJsonObj.get("userName"));
		System.out.println("token "+userJsonObj.get("token"));
		System.out.println("userName "+(String)userJsonObj.get("userName"));
		System.out.println("token "+(String)userJsonObj.get("token"));
		
	}
	
}