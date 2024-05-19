package org.mq.optculture.business.faq;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.FAQ;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.FAQDao;
import org.mq.marketer.campaign.dao.FAQDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.faq.Status;
import org.mq.optculture.model.mobileapp.FaqRequest;
import org.mq.optculture.model.mobileapp.FaqResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class FaqServiceBusinessImpl implements FaqBusinesService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		FaqResponse faqResponse = new FaqResponse();

		try {
			logger.debug("-------entered processRequest---------");
			//String serviceRequest = baseRequestObject.getAction();
			// json to object
			Gson gson = new Gson();
			FaqRequest faqRequest = null;
			try {
				faqRequest = gson.fromJson(baseRequestObject.getJsonValue(), FaqRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				Status status = new Status("1000002",
						PropertyUtil.getErrorMessage(1000002, OCConstants.ERROR_FAQ_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				faqResponse = prepareFinalResponse(null,null, status);
				String json = gson.toJson(faqResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.FAQ_SERVICE_REQUEST);
				return baseResponseObject;
			}
			FaqBusinesService faqBusinessService = (FaqBusinesService) ServiceLocator.getInstance().getServiceByName(OCConstants.FAQ_BUSINESS_SERVICE);
			faqResponse = faqBusinessService.processFaqRequest(faqRequest);

			// object to json
			String json = gson.toJson(faqResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.FAQ_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
}
	@Override
	public FaqResponse processFaqRequest(FaqRequest faqRequest) throws BaseServiceException {
		// TODO Auto-generated method stub
		FaqResponse response = null;
		Status status=null;
		
		try {
			String orgId = faqRequest.getorganizationId();
			String userName = faqRequest.getuserName();
			String token = faqRequest.getToken();
			
			FAQ faq = new FAQ();
			FAQ eng = null, spanish = null;
			FAQDao faqdao = (FAQDao) ServiceLocator.getInstance().getDAOByName(OCConstants.FAQ_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users userObj = null;
			if(token != null && !token.isEmpty()) {
				userObj = usersDao.findUserByToken(userName + Constants.USER_AND_ORG_SEPARATOR + orgId, token);
			}
			else {
				userObj = usersDao.findByUsername(userName + Constants.USER_AND_ORG_SEPARATOR + orgId);
			}
			if(orgId==null || orgId.isEmpty()) {
				status = new Status("1000000", PropertyUtil.getErrorMessage(1000000, OCConstants.ERROR_FAQ_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				FaqResponse res = prepareFinalResponse(null,null,status);
				return res;
			}
			else if(userObj==null) {
				status = new Status("1000001", PropertyUtil.getErrorMessage(1000001, OCConstants.ERROR_FAQ_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				FaqResponse res = prepareFinalResponse(null,null,status);
				return res;
			}
			else {
				UserOrganization userorg = usersDao.findByOrgName(orgId.trim());
				if(userorg == null){
					status = new Status("1000001", PropertyUtil.getErrorMessage(1000001, OCConstants.ERROR_FAQ_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					FaqResponse res= prepareFinalResponse(null,null,status);
					return res;
				}
				List<FAQ> list = faqdao.findByUserId(userObj.getUserId());
				
				
				for(FAQ f:list) {
					if(f.getLanguage()!=null && f.getLanguage().equalsIgnoreCase("English")) {
						eng = f;
					}
					if(f.getLanguage()!=null && f.getLanguage().equalsIgnoreCase("Spanish")) {
						spanish = f;
					}
				}
			}
			/*if(faq == null){
				status = new Status("1000004", PropertyUtil.getErrorMessage(1000004, OCConstants.ERROR_FAQ_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				FaqResponse res = prepareFinalResponse(null,status);
				return res;
			}*/
			//user = usersDao.find(faq.getUserId());
			
			status = new Status("0", "FAQ request successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			response=prepareFinalResponse(eng,spanish,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*status = new Status("1000001", PropertyUtil.getErrorMessage(1000001, OCConstants.ERROR_FAQ_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			FaqResponse res = prepareFinalResponse(null,status);
			return res;*/
		}
		
		return response;
	}
	public FaqResponse prepareFinalResponse (FAQ eng,FAQ span, Status status) {
		
		FaqResponse response= new FaqResponse();
		    response.setStatus(status);
		    if(eng!=null && eng.getLanguage().equalsIgnoreCase("English")) {
		    	response.setEngContent(eng.getFaqContent());
		    	response.setEngTermsContent(eng.getTermsAndCondition());
		    }
		    if(span!=null && span.getLanguage().equalsIgnoreCase("Spanish")) {
		    	response.setSpanishContent(span.getFaqContent());
		    	response.setSpanishTermsContent(span.getTermsAndCondition());
		    }
		
		return response;
	}
}
