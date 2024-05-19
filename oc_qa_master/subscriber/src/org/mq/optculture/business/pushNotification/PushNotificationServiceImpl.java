package org.mq.optculture.business.pushNotification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.beans.NotificationCampaignSent;
import org.mq.marketer.campaign.beans.NotificationSchedule;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.NotificationCampaignSentDao;
import org.mq.marketer.campaign.dao.NotificationCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.NotificationDao;
import org.mq.marketer.campaign.dao.NotificationScheduleDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.PushNotification.PushNotificationInfo;
import org.mq.optculture.model.PushNotification.PushNotificationRequest;
import org.mq.optculture.model.PushNotification.PushNotificationResponse;
import org.mq.optculture.model.magento.HeaderInfo;
import org.mq.optculture.model.magento.StatusInfo;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;

public class PushNotificationServiceImpl implements PushNotificationService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static String USER_DATA_URL;
	private static String IMAGES_URL;
	private static String NOTIFICATION_CLICK_TRACK_URL;
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		PushNotificationResponse pushNotificationResponse=null;
		try {
				Gson gson = new Gson();
				PushNotificationRequest pushnotificationRequest = gson.fromJson(baseRequestObject.getJsonValue(), PushNotificationRequest.class);
				PushNotificationService pushNotificationBusinessService=(PushNotificationService) ServiceLocator.getInstance().getServiceByName(OCConstants.PUSH_NOTIFICATION_SERVICE);
				pushNotificationResponse =(PushNotificationResponse) pushNotificationBusinessService.processPushNotificationRequest(pushnotificationRequest);
				String jsonValue=gson.toJson(pushNotificationResponse);
				baseResponseObject.setJsonValue(jsonValue);
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return baseResponseObject;
	}//processRequest

	@Override
	public PushNotificationResponse processPushNotificationRequest(PushNotificationRequest pushNotificationRequest) throws BaseServiceException {
		PushNotificationResponse pushNotificationResponse = null;
		StatusInfo statusInfo=null;
		String unreadCount = "0";
		List<PushNotificationInfo> pushNotificationInfoList=null ;
		try {
			Users user = getUser(pushNotificationRequest.getUser().getUserName(), pushNotificationRequest.getUser().getOrganizationId(),pushNotificationRequest.getUser().getToken());
			if(user == null){
				statusInfo = new StatusInfo("9001",PropertyUtil.getErrorMessage(9001,OCConstants.ERROR_PUSH_NOTIFICATION),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			    return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
			}
			if(!user.isEnabled()){
				statusInfo = new StatusInfo("9002",PropertyUtil.getErrorMessage(9002,OCConstants.ERROR_PUSH_NOTIFICATION),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			    return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				statusInfo = new StatusInfo("9003",PropertyUtil.getErrorMessage(9003,OCConstants.ERROR_PUSH_NOTIFICATION),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			    return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
			}
			String sourceType = pushNotificationRequest.getHeaderInfo().getSourceType();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = pushNotificationRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					statusInfo = new StatusInfo("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					 return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					statusInfo = new StatusInfo("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
				}
				
				/*String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(couponsHistoryRequest.getLookup().getMembershipNumber() != null && 
						couponsHistoryRequest.getLookup().getMembershipNumber().trim().length() > 0 && 
						!couponsHistoryRequest.getLookup().getMembershipNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
					
				}*/
				
			}

				if(pushNotificationRequest.getSentId()!=null && !pushNotificationRequest.getSentId().isEmpty() && pushNotificationRequest.getNotificationRead()!=null) {
					NotificationCampaignSentDaoForDML notificationCampaignSentDaoForDml = (NotificationCampaignSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("notificationCampaignSentDaoForDML");
					if(pushNotificationRequest.getSentId().contains(",")) {
						String pushNotificationSentIds[] = pushNotificationRequest.getSentId().split(",");
						for (String pushNotificationSentId : pushNotificationSentIds) {
							notificationCampaignSentDaoForDml.updateNotificationRead(pushNotificationSentId);
						}
					}else {
						notificationCampaignSentDaoForDml.updateNotificationRead(pushNotificationRequest.getSentId());
					}
				}
			
			
				if(pushNotificationRequest!=null && pushNotificationRequest.getInstanceId()!=null && !pushNotificationRequest.getInstanceId().isEmpty()) {
					NotificationCampaignSentDao notificationCampaignSentDao = (NotificationCampaignSentDao) ServiceLocator.getInstance().getDAOByName("notificationCampaignSentDao");
							Calendar tempCal = Calendar.getInstance();
							tempCal.set(Calendar.DATE, 1);
							tempCal.set(Calendar.HOUR,0);
							tempCal.set(Calendar.MINUTE,0);
							tempCal.set(Calendar.SECOND,0);
							IMAGES_URL = PropertyUtil.getPropertyValue("imagesUrl");
							USER_DATA_URL = IMAGES_URL+"UserData/";
							NOTIFICATION_CLICK_TRACK_URL=PropertyUtil.getPropertyValue("NotificationClickTrackUrl");
							
							
				
							int startCount = pushNotificationRequest.getOffSet()*5;
							List<NotificationCampaignSent> notificationSentList = notificationCampaignSentDao.getNotificationByContactId(user.getUserId(),pushNotificationRequest.getInstanceId(),pushNotificationRequest.getMobileNumber(),tempCal,Calendar.getInstance(),startCount,5);
							if(notificationSentList!=null && !notificationSentList.isEmpty()) {
							pushNotificationInfoList = new ArrayList<PushNotificationInfo>();
							for (NotificationCampaignSent notificationSent : notificationSentList) {
								/*
								 * getCustomFields(notificationSent.getNotificationCampaignReport().
								 * getNotificationHeaderContent()); String headerContent =
								 * getNotificationPlaceHolders(contact,
								 * totalPhSet,user,notificationSent.getNotificationCampaignReport().
								 * getNotificationHeaderContent());
								 * getCustomFields(notificationSent.getNotificationCampaignReport().
								 * getNotificationContent()); String description =
								 * getNotificationPlaceHolders(contact, totalPhSet,
								 * user,notificationSent.getNotificationCampaignReport().getNotificationContent(
								 * ));
								 */
								
								
								String sentId=EncryptDecryptUrlParameters.encrypt(notificationSent.getSentId().toString());
								
								Long crId = notificationSent.getNotificationCampaignReport().getNotificationCrId();
								NotificationScheduleDao notificationScheduleDao = (NotificationScheduleDao)ServiceLocator.getInstance().getDAOByName(OCConstants.NOTIFICATION_SCHEDULE_DAO);
								NotificationDao notificationDao = (NotificationDao)ServiceLocator.getInstance().getDAOByName(OCConstants.NOTIFICATION_DAO);

								NotificationSchedule notificationScheduleObj =  notificationScheduleDao.findByNotificationCampRepId(crId);
								Long Id = notificationScheduleObj.getNotificationId();
								Notification notificationObj=notificationDao.findByCampaignId(Id);
								logger.info("redirectURL from NotificationObj:"+notificationObj.getRedirectUrl());

								String redirectURL = Utility.encodeUrl(notificationObj.getRedirectUrl());
								String modifiedURL = NOTIFICATION_CLICK_TRACK_URL;
								logger.info("PNredirectURL:"+redirectURL);
								
								
								if(redirectURL!=null && !redirectURL.isEmpty()) {
									modifiedURL = modifiedURL.replace("|^clickUrl^|", redirectURL);
									logger.info("PNreplacedredirectURL clicks:"+modifiedURL);
									modifiedURL = modifiedURL.replace("|^sentId^|", sentId);
									
									logger.info("PNreplacedredirectURL sentId:"+modifiedURL);
								}else {
									redirectURL = "";
								}
								
								logger.info("PNreplacedredirectURL:"+modifiedURL);
								String headerContent = getCustomFields(notificationSent.getNotificationCampaignReport().getNotificationHeaderContent(),notificationSent.getContactPhValStr());
								String description = getCustomFields(notificationSent.getNotificationCampaignReport().getNotificationContent(),notificationSent.getContactPhValStr());
								PushNotificationInfo pushNotificationInfo = new PushNotificationInfo(); 
								pushNotificationInfo.setHeader(headerContent);
								pushNotificationInfo.setDescription(description);
								//pushNotificationInfo.setRedirectUrl(notificationSent.getNotificationCampaignReport().getNotificationUrl());
								pushNotificationInfo.setRedirectUrl(modifiedURL);
								pushNotificationInfo.setLogoImage(USER_DATA_URL+user.getUserName()+"/Notification/logoImage/"+notificationSent.getNotificationCampaignReport().getNotificationCampaignName()+"/"+notificationSent.getNotificationCampaignReport().getNotificationLogoImage());
								if(notificationSent.getNotificationRead()!=null) {
									pushNotificationInfo.setRead(notificationSent.getNotificationRead());
								}else {
									pushNotificationInfo.setRead(false);
								}
								if(notificationSent!=null && notificationSent.getNotificationCampaignReport() != null && notificationSent.getNotificationCampaignReport().getNotificationBannerImage()!=null)
									pushNotificationInfo.setBannerImage(USER_DATA_URL+user.getUserName()+"/Notification/bannerImage/"+notificationSent.getNotificationCampaignReport().getNotificationCampaignName()+"/"+notificationSent.getNotificationCampaignReport().getNotificationBannerImage());
								pushNotificationInfo.setSentId(notificationSent.getSentId().toString());
								pushNotificationInfoList.add(pushNotificationInfo);
							}
							unreadCount = notificationCampaignSentDao.getNotificationUnreadCountByContactId(pushNotificationRequest.getMobileNumber(),user.getUserId(),tempCal,Calendar.getInstance());
							statusInfo = new StatusInfo( "0","records loaded successfully",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							
						}else {
								statusInfo = new StatusInfo("9000",PropertyUtil.getErrorMessage(9000,OCConstants.ERROR_PUSH_NOTIFICATION),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							    return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
						}
					
				}else{
					statusInfo = new StatusInfo( "9004",PropertyUtil.getErrorMessage(9004,OCConstants.ERROR_PUSH_NOTIFICATION),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				    return pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
				}
			pushNotificationResponse = finalJsonResponse(pushNotificationRequest.getHeaderInfo().getRequestId(),pushNotificationInfoList,statusInfo,unreadCount);
			return pushNotificationResponse;
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing PushNotification::::: ", e);
		}
	}
	
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}

	private PushNotificationResponse finalJsonResponse(String requestId ,List<PushNotificationInfo> pushNotificationInfoList,StatusInfo statusInfo,String unreadCount) throws BaseServiceException {
		PushNotificationResponse pushNotificationResponse = new PushNotificationResponse();
		pushNotificationResponse.setPushNotificationInfo(pushNotificationInfoList);
		pushNotificationResponse.setStatusInfo(statusInfo);
		HeaderInfo headerInfo = new HeaderInfo(requestId,null);
		pushNotificationResponse.setHeaderInfo(headerInfo);
		pushNotificationResponse.setUnreadCount(unreadCount);
		return pushNotificationResponse;
	}
	
	
private String getCustomFields(String notificationHeaderContent, String contactPhValStr) {
	String content = notificationHeaderContent;
	if(contactPhValStr != null && !contactPhValStr.trim().isEmpty()) {
		if(contactPhValStr != null) {
			
			String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
			String keyStr = "";
			String ValStr = "";
			for (String phToken : phTokenArr) {
				keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
				if((keyStr.equalsIgnoreCase("[GEN_ContactHomeStore]") ) && (keyStr.equalsIgnoreCase("[GEN_ContactLastPurchasedStore]")) &&  ( keyStr.startsWith("[CC_")) ){
					continue;
				}
				ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());
				content = content.replace(keyStr, ValStr);
			}
		}
	}
	return content;
}
	/*
	 * private String getCustomFields(String content) { String cfpattern =
	 * "\\[([^\\[]*?)\\]"; Pattern r = Pattern.compile(cfpattern,
	 * Pattern.CASE_INSENSITIVE); Matcher m = r.matcher(content);
	 * 
	 * String ph = null; totalPhSet = new HashSet<String>(); String cfNameListStr =
	 * "";
	 * 
	 * try { while (m.find()) {
	 * 
	 * ph = m.group(1); // .toUpperCase() if (logger.isInfoEnabled())
	 * logger.info("Ph holder :" + ph);
	 * 
	 * if (ph.startsWith("GEN_")) { totalPhSet.add(ph); } if
	 * (ph.startsWith(Constants.UDF_TOKEN)) { totalPhSet.add(ph); } else if
	 * (ph.startsWith("CC_")) { totalPhSet.add(ph); } else if (ph.startsWith("CF_"))
	 * { totalPhSet.add(ph); cfNameListStr = cfNameListStr +
	 * (cfNameListStr.equals("") ? "" : Constants.DELIMETER_COMMA) + "'" +
	 * ph.substring(3) + "'"; } } // while if (logger.isDebugEnabled())
	 * logger.debug("+++ Exiting : " + totalPhSet); } catch (Exception e) {
	 * logger.error("Exception while getting the place holders ", e); }
	 * 
	 * if (logger.isInfoEnabled()) logger.info("CF PH cfNameListStr :" +
	 * cfNameListStr);
	 * 
	 * return cfNameListStr; }
	 */
	
	public String getNotificationPlaceHolders(Contacts contact,Set<String> totalPhSet,Users user,String notificationContent) {
			if(totalPhSet != null && totalPhSet.size() >0) {
			for (String cfStr : totalPhSet) {
				logger.debug("<<<<   cfStr : "+ cfStr);
				String preStr = "["+cfStr+"]";


				String value = Constants.STRING_NILL;

				try {
					if(cfStr.startsWith("GEN_")) {
						value = getNotifiactionGeneralPlaceHolderVal(cfStr, contact, user);
					}
				} catch (Exception e) {
					value = Constants.STRING_NILL;
				}
				notificationContent = notificationContent.replace(preStr, value);
			}
		}
		return notificationContent;


	}

	private String getNotifiactionGeneralPlaceHolderVal(String cfStr, Contacts contact, Users user) {

		String defVal="";
		try {
			String value = Constants.STRING_NILL;

			cfStr = cfStr.substring(4);
			int defIndex = cfStr.indexOf('=');

			if(defIndex != -1) {
				defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
				cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
			} // if

			if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email")){
				value = contact.getEmailId()==null ? defVal:contact.getEmailId();
			}

			else if(cfStr.equalsIgnoreCase("firstName")){
				value = contact.getFirstName()==null ? defVal :contact.getFirstName();
			}
			else if(cfStr.equalsIgnoreCase("lastName")) {
				value = contact.getLastName()==null ? defVal :contact.getLastName();
			}
			else if(cfStr.equalsIgnoreCase("addressOne")) {
				value = contact.getAddressOne()==null ?defVal :contact.getAddressOne();
			}
			else if(cfStr.equalsIgnoreCase("addressTwo")) {
				value = contact.getAddressTwo()==null ? defVal :contact.getAddressTwo();
			}
			else if(cfStr.equalsIgnoreCase("city")) {
				value = contact.getCity()==null ? defVal :contact.getCity();
			}
			else if(cfStr.equalsIgnoreCase("state")) {
				value = contact.getState()==null ? defVal :contact.getState();
			}
			else if(cfStr.equalsIgnoreCase("country")) {
				value = contact.getCountry()==null ? defVal :contact.getCountry();
			}
			else if(cfStr.equalsIgnoreCase("pin")) {
				value = contact.getZip()==null ? defVal :contact.getZip();

			}
			else if(cfStr.equalsIgnoreCase("phone")) {
				value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : defVal;
			}
			else if(cfStr.equalsIgnoreCase("gender")) {
				value = contact.getGender()==null ? defVal :contact.getGender();
			}
			else if(cfStr.equalsIgnoreCase("birthday") ) {

				value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);
				value = ( value.equals("--") &&  defVal != null) ? defVal : value;	
			}
			else if(cfStr.equalsIgnoreCase("anniversary") ) {


				value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);

				value = ( value.equals("--") &&  defVal != null) ? defVal : value;

			}
			else if(cfStr.equalsIgnoreCase("createdDate") ) {

				value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
				value = ( value.equals("--") &&  defVal != null) ? defVal : value;
			}
			else if(cfStr.equalsIgnoreCase("organizationName") ) {
				value = getUserOrganization(user, defVal);
			}
			
			else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {
				String posLocId = contact.getHomeStore();

				if(posLocId == null) {

					value = defVal;//getDefaultStorePhValue(cfStr, user, null, defVal );
					return value;
				}

			}
			if(value == null || value.trim().isEmpty()) value = defVal;
			return value;
		} catch (Exception e) {
			return defVal;
		}

	}
	
	private String getUserOrganization(Users user, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
		String organizationName = defVal;
		try{
			organizationName = user.getUserOrganization().getOrganizationName();
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getUserOrganization");
		return organizationName;
	}

}
