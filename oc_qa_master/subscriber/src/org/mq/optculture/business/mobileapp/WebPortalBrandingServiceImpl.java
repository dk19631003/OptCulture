package org.mq.optculture.business.mobileapp;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.PrerequisiteInfo;
import org.mq.optculture.model.mobileapp.WebPortalBrandingRequest;
import org.mq.optculture.model.mobileapp.WebPortalBrandingResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class WebPortalBrandingServiceImpl implements WebPortalBrandingService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		WebPortalBrandingResponse brandingResponse=new WebPortalBrandingResponse();
		try {
			
			logger.debug("-------entered processRequest---------");
			String serviceRequest = baseRequestObject.getAction();
			String requestJson = baseRequestObject.getJsonValue();
			
			String responseJson = null;
			Gson gson = new Gson();
			WebPortalBrandingRequest brandingRequest = null;
			try {
				brandingRequest = gson.fromJson(requestJson, WebPortalBrandingRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				Status status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				brandingResponse = prepareFinalResponse(null,null,null,status);
				String json=gson.toJson(brandingResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.WEB_PORTAL_BRANDING_SERVICE_REQUEST);
				return baseResponseObject;
			}
			try{
				WebPortalBrandingService webPortalBrandingService = (WebPortalBrandingService) ServiceLocator.getInstance().getServiceByName(OCConstants.WEB_PORTAL_BRANDING_SERVICE);
				brandingResponse = webPortalBrandingService.processBrandingRequest(brandingRequest);
				responseJson = gson.toJson(brandingResponse);
				
				baseResponseObject = new BaseResponseObject();
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
			}catch(Exception e){
				logger.error("Exception in process brand request.",e);
				throw new BaseServiceException("Server Error.");
			}
			logger.info("Completed processing baserequest... ");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return baseResponseObject;
	}
	@Override
	public WebPortalBrandingResponse processBrandingRequest(WebPortalBrandingRequest brandingRequest) throws BaseServiceException {
		WebPortalBrandingResponse response = null;
		Status status=null;
		PrerequisiteInfo prerequisiteInforespone=null;

		try {
			String webPortalUrl=brandingRequest.getHomePageUrl();
			String orgId = brandingRequest.getOrgId();
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			LoyaltySettings ltySettings =null;
			Users user=null;
			if((webPortalUrl == null || webPortalUrl.isEmpty()) && (orgId!=null && !orgId.trim().isEmpty())){
				UserOrganization userorg = usersDao.findByOrgName(orgId.trim());
				if(userorg == null){
					status = new Status("800020", PropertyUtil.getErrorMessage(800020, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					WebPortalBrandingResponse resp= prepareFinalResponse(null,null,null,status);
					return resp;
				}
				
				/*List<Users> userList = usersDao.getUsersListByOrg(userorg.getUserOrgId());
				user = userList.get(0);*/
				ltySettings = loyaltySettingsDao.findByOrgId(userorg.getUserOrgId());
			}else if(webPortalUrl != null && !webPortalUrl.isEmpty()) {
				if(webPortalUrl.contains("http") ) {
					String[] portalUrl=webPortalUrl.split("//");
					if(portalUrl[1]!=null && !portalUrl[1].isEmpty()) webPortalUrl = portalUrl[1];
				}
				List<LoyaltySettings> list = loyaltySettingsDao.matchByUrl(webPortalUrl);
				if(list == null || list.isEmpty()){
					status = new Status("800020", PropertyUtil.getErrorMessage(800020, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					WebPortalBrandingResponse resp= prepareFinalResponse(null,null,null,status);
					return resp;
				}
				ltySettings = list.get(0);
			}
			if(ltySettings == null){
				status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				WebPortalBrandingResponse resp= prepareFinalResponse(null,null,null,status);
				return resp;
			}
			user = usersDao.find(ltySettings.getUserId());
			
			status = new Status("0", "Web portal branding request successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			response=prepareFinalResponse(user,ltySettings,prerequisiteInforespone,status);
		
	}catch (Exception e) {
		status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		WebPortalBrandingResponse resp= prepareFinalResponse(null,null,null,status);
		return resp;
	}
	return response;
	}
	private WebPortalBrandingResponse prepareFinalResponse(Users user,LoyaltySettings ltySetting,PrerequisiteInfo prerequisiteInfo,Status status)throws BaseServiceException {
		WebPortalBrandingResponse response = new WebPortalBrandingResponse();
		try {
		String ltyImgUrl = PropertyUtil.getPropertyValue("LoyaltyImageServerUrl");
		String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl")+"UserData";
		String bannerPath = Constants.STRING_NILL;
		String homePagePath =Constants.STRING_NILL;
		String logoPath=Constants.STRING_NILL;
		UserOrganization userOrg = user.getUserOrganization();
		String userDir = PropertyUtil.getPropertyValue("usersParentDirectory");
		String ltyDir = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory");
		if(ltySetting!=null) {
			logoPath = ltySetting.getPath().replace(ltyDir.trim(),ltyImgUrl);
			if(ltySetting.getPath().contains("RewardApp"))
				logoPath = ltySetting.getPath().replace(userDir.trim(),imgUrl);
		}
		if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
			bannerPath = userOrg.getBannerPath().replace(userDir.trim(),imgUrl);
			if(userOrg.getBannerPath().contains("RewardApp"))
				bannerPath = userOrg.getBannerPath().replace(userDir.trim(),imgUrl);
		}
		if(ltySetting!=null && ltySetting.getHomePageImagePath()!=null && !ltySetting.getHomePageImagePath().isEmpty()) {
			homePagePath = ltySetting.getHomePageImagePath().replace(ltyDir.trim(),ltyImgUrl);
			if(ltySetting.getHomePageImagePath().contains("RewardApp"))
			homePagePath = ltySetting.getHomePageImagePath().replace(userDir.trim(),imgUrl);
		}
		
		response.setOrgId(user!=null?userOrg.getOrgExternalId():Constants.STRING_NILL);
		response.setUsername(user!=null?Utility.getOnlyUserName(user.getUserName()):Constants.STRING_NILL);
		response.setToken(user!=null?(user.getToken()!=null ? user.getToken() : userOrg.getOptSyncKey()):Constants.STRING_NILL);
		/*response.setUsername(user!=null?Utility.getOnlyUserName(user.getUserName()):Constants.STRING_NILL);
		response.setPassword(user!=null?user.getPassword():Constants.STRING_NILL);
		response.setToken(user!=null?(user.getToken()!=null ? user.getToken() : userOrg.getOptSyncKey()):Constants.STRING_NILL);*/
		response.setThemecolor(ltySetting!=null && ltySetting.getHomePageColorCode()!=null ?ltySetting.getHomePageColorCode():Constants.STRING_NILL);
		response.setBalanceCardThemeColor(ltySetting!=null && ltySetting.getBalanceCardColorCode()!=null?ltySetting.getBalanceCardColorCode():Constants.STRING_NILL);
		response.setBalanceCardTextColor(ltySetting!=null && ltySetting.getBalanceCardTextColorCode()!=null?ltySetting.getBalanceCardTextColorCode():Constants.STRING_NILL);
		response.setBannerImage(Utility.encodeSpace(bannerPath));
		response.setCoverimage(Utility.encodeSpace(homePagePath));
		response.setLogoimage(Utility.encodeSpace(logoPath));
		response.setCustomerName(Utility.getOnlyUserName(user.getUserName()));
		response.setBannerName(ltySetting.getBannerName()!=null && !ltySetting.getBannerName().isEmpty()?ltySetting.getBannerName():Constants.STRING_NILL);
		response.setTabName(ltySetting.getTabName()!=null && !ltySetting.getTabName().isEmpty()?ltySetting.getTabName():Constants.STRING_NILL);
		response.setTabImage(ltySetting.getTabImagePath()!=null ? Utility.encodeSpace(ltySetting.getTabImagePath().replace(userDir.trim(),imgUrl)):Constants.STRING_NILL);
		response.setFontName(ltySetting.getFontName()!=null?ltySetting.getFontName():Constants.STRING_NILL);
		response.setFontURL(ltySetting.getFontURL()!=null?ltySetting.getFontURL():Constants.STRING_NILL);
		response.setStatus(status);
		response.setCurrencySymbol(user != null && Utility.countryCurrencyMap.get(user.getCountryType()) != null ? Utility.countryCurrencyMap.get(user.getCountryType()) :Constants.STRING_NILL);
		response.setEmail(ltySetting.getEmail()!=null?ltySetting.getEmail():Constants.STRING_NILL);
		response.setMobile(ltySetting.getMobile()!=null?ltySetting.getMobile():Constants.STRING_NILL);
		String contactsList = getUserPOSList(user);
		response.setContactsList(contactsList);
		response.setLoginType(ltySetting.getLoginType()!=null?ltySetting.getLoginType(): 0);
		response.setCardSettings(ltySetting.getCardSettings()!=null?ltySetting.getCardSettings() : 0);
		response.setReferralImage(ltySetting.getReferralImage()!=null ? ltySetting.getReferralImage(): Constants.STRING_NILL );
		response.setLoyaltyImage(ltySetting.getLoyaltyImage()!=null ? ltySetting.getLoyaltyImage() : Constants.STRING_NILL);
		
		if (prerequisiteInfo == null ) {
			
			prerequisiteInfo = new PrerequisiteInfo();
		
			prerequisiteInfo.setFirstName(ltySetting.isIncludeFirstname() ? "Yes" : "No") ;
			prerequisiteInfo.setLastName(ltySetting.isIncludeLastname() ? "Yes" : "No");
			prerequisiteInfo.setMobilenumber(ltySetting.isIncludeMobilenumber() ?"Yes" : "No");
			prerequisiteInfo.setEmailAddress(ltySetting.isIncludeEmailaddress() ?"Yes" : "No");
			
			prerequisiteInfo.setStreet(ltySetting.isIncludeStreet() ? "Yes" :"No");
			prerequisiteInfo.setCity(ltySetting.isIncludeCity() ? "Yes" :"No");
			prerequisiteInfo.setState(ltySetting.isIncludeState()? "Yes" :"No");
			prerequisiteInfo.setPostalcode(ltySetting.isIncludePostalCode() ? "Yes" :"No");
			
			prerequisiteInfo.setCountry(ltySetting.isIncludeCountry() ? "Yes" :"No");
			prerequisiteInfo.setBirthday(ltySetting.isIncludeBirthday()? "Yes" :"No");
			prerequisiteInfo.setAnniversary(ltySetting.isIncludeAnniversary() ? "Yes" :"No");
			prerequisiteInfo.setGender(ltySetting.isIncludeGender() ? "Yes" :"No");
		}
		response.setPrerequisiteInfo(prerequisiteInfo);
		
		}catch(Exception e) {
			logger.error("Exception ",e);
		}
		return response;
	}//prepareFinalResponse

	public String getUserPOSList(Users user){
		
		String contactsList = Constants.STRING_NILL;
		try {
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList posML = mailingListDao.findPOSMailingList(user);
			if(posML != null) contactsList =  posML.getListName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		return contactsList;
	}
}
