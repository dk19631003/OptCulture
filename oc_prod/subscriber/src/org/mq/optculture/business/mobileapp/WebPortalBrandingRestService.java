package org.mq.optculture.business.mobileapp;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.WebPortalBrandingRequest;
import org.mq.optculture.model.mobileapp.WebPortalBrandingResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class WebPortalBrandingRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		
		try {
			logger.info("web portal branding request call");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			Gson gson=new Gson();
			WebPortalBrandingRequest brandingReq;
			WebPortalBrandingResponse brandingResp =null;
			String responseJson = Constants.STRING_NILL;
			Status status = null;
			try {
				brandingReq = gson.fromJson(baseRequestObject.getJsonValue(), WebPortalBrandingRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				WebPortalBrandingResponse resp = new WebPortalBrandingResponse(Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL);
				brandingResp = prepareFinalResponse(resp, status);
				responseJson = gson.toJson(brandingResp);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			
			}
			if(brandingReq == null){
				status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				WebPortalBrandingResponse resp = new WebPortalBrandingResponse(Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL);
				brandingResp = prepareFinalResponse(resp, status);
				responseJson = gson.toJson(brandingResp);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				//logTransactionRequest(brandingReq, responseJson,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,null,null);
				return null;
			}
			if((brandingReq.getHomePageUrl()==null)||
					(brandingReq.getHomePageUrl()!=null && brandingReq.getHomePageUrl().isEmpty() && brandingReq.getOrgId()!=null && brandingReq.getOrgId().isEmpty())) {
				status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				WebPortalBrandingResponse resp = new WebPortalBrandingResponse(Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL);
				brandingResp = prepareFinalResponse(resp, status);
				responseJson = gson.toJson(brandingResp);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				//logTransactionRequest(brandingReq, responseJson,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,null,null);
				return null;
			}
			LoyaltySettings ltySettings =null;
			Users user=null;
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			String webPortalUrl=brandingReq.getHomePageUrl();
			String orgId = brandingReq.getOrgId();
			if(!webPortalUrl.isEmpty()) {
			if(webPortalUrl.contains("http")) {
				String[] portalUrl=webPortalUrl.split("//");
				if(portalUrl[1]!=null && !portalUrl[1].isEmpty()) webPortalUrl = portalUrl[1];
			}
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			List<LoyaltySettings> list = loyaltySettingsDao.matchByUrl(webPortalUrl);
			if(list==null || list.isEmpty()) {
				status = new Status("800005", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				WebPortalBrandingResponse resp = new WebPortalBrandingResponse(Constants.STRING_NILL,Constants.STRING_NILL,
						Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL);
				brandingResp = prepareFinalResponse(resp, status);
				responseJson = gson.toJson(brandingResp);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				//logTransactionRequest(brandingReq, responseJson,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE,null,null);
				return null;
			}
			ltySettings = list.get(0);
			user = usersDao.findByUserId(ltySettings.getUserId());
			}else if(orgId!=null && !orgId.isEmpty()){
				UserOrganization userorg = usersDao.findByOrgName(orgId);
				List<Users> userList = usersDao.getUsersListByOrg(userorg.getUserOrgId());
				if(userList==null || userList.isEmpty()) {
					status = new Status("800005", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					WebPortalBrandingResponse resp = new WebPortalBrandingResponse(Constants.STRING_NILL,Constants.STRING_NILL,
							Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL,Constants.STRING_NILL, Constants.STRING_NILL,Constants.STRING_NILL);
					brandingResp = prepareFinalResponse(resp, status);
					responseJson = gson.toJson(brandingResp);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				user = userList.get(0);
			}
		String requestId = OCConstants.WEB_PORTAL_BRANDING_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
		String userDetails = Utility.getOnlyUserName(user.getUserName().toLowerCase())+"__"+user.getUserOrganization().getOrgExternalId();	
		logTransactionRequest(brandingReq, jsonValue,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE,requestId,userDetails);
		baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.WEB_PORTAL_BRANDING_SERVICE);
		baseResponseObject =baseService.processRequest(baseRequestObject);
		brandingResp = gson.fromJson(baseResponseObject.getJsonValue(), WebPortalBrandingResponse.class);
		LoyaltyTransaction trans = findByRequestId(requestId, userDetails,OCConstants.TRANS_TYPE_WEB_PORTAL_BRANDING_REQ);
			responseJson = baseResponseObject.getJsonValue();
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			if(trans != null){
			updateTransactionStatus(trans,responseJson,brandingResp);
			return null;
			}
			
		
	} catch (Exception e) {
		logger.error("Exception ::", e);
	}
	
	return null;
}
			
private WebPortalBrandingResponse prepareFinalResponse(
			WebPortalBrandingResponse response, Status status)throws BaseServiceException {
				
				WebPortalBrandingResponse webPortalBrandingResponse = new WebPortalBrandingResponse();
				
				webPortalBrandingResponse.setUsername(response.getUsername());
				webPortalBrandingResponse.setOrgId(response.getOrgId());
				webPortalBrandingResponse.setToken(response.getToken());
				//webPortalBrandingResponse.setPassword(response.getPassword());
				webPortalBrandingResponse.setLogoimage(response.getLogoimage());
				webPortalBrandingResponse.setThemecolor(response.getThemecolor());
				webPortalBrandingResponse.setBalanceCardThemeColor(response.getBalanceCardThemeColor());
				webPortalBrandingResponse.setBalanceCardTextColor(response.getBalanceCardTextColor());
				webPortalBrandingResponse.setCoverimage(response.getCoverimage());
				webPortalBrandingResponse.setCustomerName(response.getCustomerName());
				webPortalBrandingResponse.setBannerName(response.getBannerName());
				webPortalBrandingResponse.setFontName(response.getFontName());
				webPortalBrandingResponse.setFontURL(response.getFontURL());
				webPortalBrandingResponse.setStatus(status);
				webPortalBrandingResponse.setEmail(response.getEmail());
				webPortalBrandingResponse.setMobile(response.getMobile());
				webPortalBrandingResponse.setLoginType(response.getLoginType());
				webPortalBrandingResponse.setCardSettings(response.getCardSettings());
				webPortalBrandingResponse.setReferralImage(response.getReferralImage());
				webPortalBrandingResponse.setLoyaltyImage(response.getLoyaltyImage());


				return webPortalBrandingResponse;
	}//prepareFinalResponse
	private LoyaltyTransaction logTransactionRequest(WebPortalBrandingRequest rebPortalBrandingRequest, String jsonRequest,String status,
			String requestId,String userDetails){
		
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			if(requestId!=null) transaction.setRequestId(requestId);
			if(userDetails!=null) transaction.setUserDetail(userDetails);
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(status);
			transaction.setType(OCConstants.TRANS_TYPE_WEB_PORTAL_BRANDING_REQ);
			
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, WebPortalBrandingResponse response){
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_DAO_FOR_DML);
			transaction.setStatus(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			transaction.setJsonResponse(responseJson);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private LoyaltyTransaction findByRequestId(String requestId, String userDetails,String type) {
		LoyaltyTransaction loyaltyTransaction = null;
		try {
			LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransaction =  loyaltyTransactionDao.findByRequestId(requestId, userDetails,type);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyTransaction;
	}
}
