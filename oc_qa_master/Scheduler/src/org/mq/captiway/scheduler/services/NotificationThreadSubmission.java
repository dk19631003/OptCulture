package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mq.captiway.scheduler.NotificationRecipientProvider;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.IosNotification;
import org.mq.captiway.scheduler.beans.Notification;
import org.mq.captiway.scheduler.beans.NotificationCampaignSent;
import org.mq.captiway.scheduler.beans.NotificationSuppressedContacts;
import org.mq.captiway.scheduler.beans.PushNotificationAndroid;
import org.mq.captiway.scheduler.beans.PushNotificationIos;
import org.mq.captiway.scheduler.beans.PushNotificationData;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.NotificationCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.NotificationSuppressedDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.EncryptDecryptUrlParameters;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationThreadSubmission extends Thread {

	 
	private NotificationRecipientProvider notificationProvider;
	private Notification notification;
	private Users users;
	private Set<String> totalPhSet;
	private String notificationBodyContent;
	private String notificationHeaderContent;
	private final static String uri = "https://fcm.googleapis.com/fcm/send";
	private ServiceLocator locator ;
	private NotificationCampaignSentDaoForDML notificationCampaignSentDaoForDML;
	private NotificationSuppressedDaoForDML notificationSuppressedDaoForDML; 
	private UsersDao usersDao;
	private static String optcultureFireBaseServerKey;
	private static String NOTIFICATION_CLICK_TRACK_URL;
	private static String IMAGES_URL;
	private static String USER_DATA_URL;
	private static String CLICKS_URL;
	//static final String NOTIFICATION_CLICK_TRACK_URL = PropertyUtil.getPropertyValue("NotificationClickTrackUrl");
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 

	public NotificationThreadSubmission() {
	}
	// For Sending Campaign .
	/**
	 * NotificationMultiThreadedSubmission
	 * @param notificationProvider
	 * @param user
	 * @param notification
	 * @param textContent
	 * @param totalPhSet
	 * @param urlSet
	 * @param campaignType
	 * @param context
	 */
		public NotificationThreadSubmission(Users user, Notification notification,NotificationRecipientProvider notificationRecipientProvider,
				Set<String> totalPhSet ,String notificationBodyContent,String notificationHeaderContent,String campaignType) { 
			    this.users = user;
				this.notification = notification;
				this.totalPhSet = totalPhSet;
				this.notificationProvider = notificationRecipientProvider;
				this.notificationHeaderContent = notificationHeaderContent;
				this.notificationBodyContent = notificationBodyContent; 
		}


	public void run()  {
		try {
		locator = ServiceLocator.getInstance();
		NotificationCampaignSent notificationCampaignSent = null;
		String sentId = "";
		optcultureFireBaseServerKey = PropertyUtil.getPropertyValueFromDB("optcultureFireBaseServerKey");
		NOTIFICATION_CLICK_TRACK_URL=PropertyUtil.getPropertyValue("NotificationClickTrackUrl");
		IMAGES_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		USER_DATA_URL = IMAGES_URL+"UserData/";
		String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
		usersDao = (UsersDao)locator.getDAOByName(OCConstants.USERS_DAO);
		Users userData = usersDao.find(notification.getUserId());
		notificationCampaignSentDaoForDML = (NotificationCampaignSentDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_CAMPAIGN_SENT_DAO_FOR_DML);
		notificationSuppressedDaoForDML = (NotificationSuppressedDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_SUPPRESSED_DAO_FOR_DML);
		boolean placeHolders = false;
		ReplacePlaceHolders replacePlaceHolders  = null;
		if(totalPhSet != null && totalPhSet.size() > 0) {
			placeHolders = true;
			replacePlaceHolders = new ReplacePlaceHolders();
		}
		Contacts contact = null;
		while((contact = notificationProvider.getNext()) != null) {
			if(contact.getPushNotification()) {
				
				String mobileNumber = contact.getMobilePhone();
				notificationCampaignSent = (NotificationCampaignSent)contact.getTempObj();
				if(notificationCampaignSent == null ) {
		    		if(logger.isInfoEnabled()) logger.info("--------- No sent obj is attached to this contact, hence Notification Sender Thread exiting -------"+ Thread.currentThread().getName());
		    		return;
				}
				sentId = ""+notificationCampaignSent.getSentId();
				int lengthOfSentId = sentId.length();  
				for(int j=lengthOfSentId; j<8; j++){
					
					sentId = "0"+sentId;
					
				}
				if(logger.isInfoEnabled()) logger.info("sent id is=====>"+sentId+" in Thread :: "+Thread.currentThread().getName());
				if(logger.isInfoEnabled()) logger.info(mobileNumber+"  , "+contact.getEmailId());
			
				contact.setTempObj(notificationCampaignSent.getSentId());
			
				logger.info("contains placeHolders "+placeHolders);
				/**
				 * if notification contains place holders we submit individually
				 */
					PushNotificationIos pushnotificationIos = new PushNotificationIos();
					PushNotificationAndroid pushnotificationAndroid = new PushNotificationAndroid();
					
					String[] msgDescriptionToBeSent = {notificationBodyContent};
					//String[] msgHeaderToBeSent = {notificationHeaderContent};
					String	contactPhValStr = null;
					if(totalPhSet != null && totalPhSet.size() >0) {
						if(logger.isInfoEnabled()) logger.info("<<<<<<<<<<<<<<<calling replace place holders >>>>>>>>>>>>>>>>");
						msgDescriptionToBeSent = replacePlaceHolders.getNotificationPlaceHolders(contact, 
								totalPhSet, userData, notificationCampaignSent.getSentId(),
								notificationProvider, notificationBodyContent,null, notificationHeaderContent);
						//msgHeaderToBeSent = replacePlaceHolders.getNotificationPlaceHolders(contact, totalPhSet, userData, notificationCampaignSent.getSentId(), notificationProvider, notificationHeaderContent,null);
						//msgHeaderToBeSent = replacePlaceHolders.getNotificationPlaceHolders(contact, totalPhSet, userData, notificationCampaignSent.getSentId(), notificationProvider, notificationHeaderContent);
				
						//msgHeaderToBeSent = replacePlaceHolders.getNotificationPlaceHolders(contact, totalPhSet, userData, notificationCampaignSent.getSentId(), notificationProvider, notificationHeaderContent);
			    	}
					String clickSentId = EncryptDecryptUrlParameters.encrypt(notificationCampaignSent.getSentId()+Constants.STRING_NILL);
					String urlSentId = String.valueOf(clickSentId);
					
					String redirectURL = notification.getRedirectUrl();
					redirectURL=Utility.encodeUrl(redirectURL);
					String modifiedURL = NOTIFICATION_CLICK_TRACK_URL;
					logger.info("redirectURL:"+redirectURL);
					
					
					if(redirectURL!=null && !redirectURL.isEmpty()) {
						//redirectURL = ;
						modifiedURL = modifiedURL.replace("|^clickUrl^|", redirectURL);
						logger.info("replacedredirectURL clicks:"+modifiedURL);
						modifiedURL = modifiedURL.replace("|^sentId^|", urlSentId);
						
						logger.info("replacedredirectURL sentId:"+modifiedURL);
					}else {
						redirectURL = "";
					}
					
					logger.info("replacedredirectURL:"+modifiedURL);
				String json = null;
				if(contact.getDeviceType()!=null && contact.getDeviceType().equals(Constants.ANDROID)) {
					PushNotificationData data = new PushNotificationData();
					try {
						String[] registration_ids = {contact.getInstanceId()};
						pushnotificationAndroid.setRegistration_ids(registration_ids);
						//MA-90 
						String bannerimage = notification.getBannerImageUrl();
						if(bannerimage!=null && !bannerimage.isEmpty()) {
							bannerimage = USER_DATA_URL+userData.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
						}else {
							bannerimage = "";
						}
						data.setImage(bannerimage);
						data.setIcon("notification_icon");
						//use the image
						data.setVisibility(0);
						data.setPriority(2);
						if(data.getImage()!=null && !data.getImage().isEmpty()) {
							data.setStyle(OCConstants.NOTIFICATION_IMAGE_TYPE_PICTURE);
						}else {
							data.setStyle(OCConstants.NOTIFICATION_IMAGE_TYPE_INBOX);
						}
						data.setTitle(msgDescriptionToBeSent[2]);
						data.setMessage(msgDescriptionToBeSent[0]);
						data.setBody(msgDescriptionToBeSent[0]);
						data.setSummaryText(msgDescriptionToBeSent[0]);
						//data.setUrl(notification.getRedirectUrl());
						data.setUrl(modifiedURL);
						data.setNotId(getRandomNumberInts());
						data.setMutable_content(true);
						pushnotificationAndroid.setNotification(data );
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						json = ow.writeValueAsString(pushnotificationAndroid);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("exception cannot continue ");
						return;
					}
				}else if(contact.getDeviceType()!=null && contact.getDeviceType().equals(Constants.IOS)) {
					try {
						String[] registration_ids = {contact.getInstanceId()};
						pushnotificationIos.setRegistration_ids(registration_ids);
						IosNotification iosNotification = new IosNotification();
						iosNotification.setTitle(msgDescriptionToBeSent[2]);
						iosNotification.setBody(msgDescriptionToBeSent[0]);
						iosNotification.setSound("default");
						iosNotification.setMutable_content(true);
						PushNotificationData data =  new PushNotificationData();
						data.setNotId(getRandomNumberInts());
						String imagePath = USER_DATA_URL+userData.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
						String extension = FilenameUtils.getExtension(imagePath);
						if(extension.equalsIgnoreCase("jpg")) {
							data.setImage_url_jpg(imagePath);
						}else if(extension.equalsIgnoreCase("png")) {
							data.setImage_url_png(imagePath);
						}else if(extension.equalsIgnoreCase("gif")) {
							data.setImage_url_gif(imagePath);
						}
							//data.setUrl(notification.getRedirectUrl());
						data.setUrl(modifiedURL);
						pushnotificationIos.setNotification(iosNotification);
						pushnotificationIos.setData(data);
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						json = ow.writeValueAsString(pushnotificationIos);
						if(data.getImage_url_jpg()!=null && !data.getImage_url_jpg().isEmpty()) {
							json = json.replace("image_url_jpg", "image-url-jpg");
						}else if(data.getImage_url_png()!=null && !data.getImage_url_png().isEmpty()) {
							json = json.replace("image_url_png", "image-url-png");
						}else if(data.getImage_url_gif()!=null && !data.getImage_url_gif().isEmpty()) {
							json = json.replace("image_url_gif", "image-url-gif");
						}
					}catch (Exception e) {
						e.printStackTrace();
						logger.error("exception cannot continue ");
						return;
					}
				}else if(contact.getDeviceType()!=null && contact.getDeviceType().equals(Constants.WEB)) {
					try {
						String[] registration_ids = {contact.getInstanceId()};
						pushnotificationIos.setRegistration_ids(registration_ids);
						//used IOS pojo here as the json generated for IOS and WEB are same. 
						IosNotification webNotification = new IosNotification();
						webNotification.setTitle(msgDescriptionToBeSent[2]);
						webNotification.setBody(msgDescriptionToBeSent[0]);
						webNotification.setSound("default");
						webNotification.setMutable_content(true);
						String bannerimage = notification.getBannerImageUrl();
						if(bannerimage!=null && !bannerimage.isEmpty()) {
							bannerimage = USER_DATA_URL+userData.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
							bannerimage = bannerimage.replace(IMAGES_URL, imgUrl);
						}else {
							bannerimage = "";
						}
						String logo=notification.getLogoImageUrl();
						if(logo!=null && !logo.isEmpty()) {
							logo=USER_DATA_URL+userData.getUserName()+"/Notification/logoImage/"+notification.getNotificationName()+"/"+notification.getLogoImageUrl();
							logo=logo.replace(IMAGES_URL, imgUrl);
						}
						webNotification.setIcon(logo);
						webNotification.setImage(bannerimage);
						pushnotificationIos.setNotification(webNotification);
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						json = ow.writeValueAsString(pushnotificationIos);
						logger.info("web json"+json);
					}catch (Exception e) {
					}
				}
				    String status = postToURL(json);
				    logger.info("pushNotification status ::"+status);
				    String[] statusArray = status.split("::");
				    if(statusArray!=null && statusArray.length > 0) {
				    	JSONParser parse = new JSONParser();
				    	JSONObject jobj = (JSONObject)parse.parse(statusArray[0]);
				    	Long successCount = (Long) jobj.get("success");
				    	Long failureCount = (Long) jobj.get("failure");
				    	JSONArray result = (JSONArray) jobj.get("results");
				    	for (int i = 0; i < result.size(); i++) {
				    		JSONObject objects = (JSONObject) result.get(i);
				    		if(objects.containsKey("error")) {
					    		String error  = (String)objects.get("error");
					    		if(error.equalsIgnoreCase("NotRegistered")) {
					    			NotificationSuppressedContacts suppressedContact = new NotificationSuppressedContacts();
					    			suppressedContact.setContactId(contact.getContactId());
					    			suppressedContact.setUserId(users.getUserId());
					    			suppressedContact.setOrgId(users.getUserOrganization().getUserOrgId());
					    			suppressedContact.setInstanceId(contact.getInstanceId());
					    			suppressedContact.setSuppressedtime(Calendar.getInstance());
					    			suppressedContact.setType(error);
					    			suppressedContact.setReason("user may have uninstalled the app.");
					    			notificationSuppressedDaoForDML.saveOrUpdate(suppressedContact);
					    		}
				    		}
						}
				    	
				    	
				    	if(statusArray[1].equals("statusCode:200".trim()) && successCount!=0 && failureCount == 0) {
				    		notificationCampaignSent.setStatus(Constants.EQ_STATUS_SENT);
				    	}else {
				    		notificationCampaignSent.setStatus(Constants.EQ_STATUS_FAILURE);
				    	}
				    	if(totalPhSet != null && totalPhSet.size() >0) {
				    			if(msgDescriptionToBeSent[1] != null && msgDescriptionToBeSent[1].length() > 0) {
				    				contactPhValStr = msgDescriptionToBeSent[1];
				    			}
			    				if(contactPhValStr != null) {
			    					notificationCampaignSent.setContactPhValStr(contactPhValStr);
			    					notificationCampaignSentDaoForDML.saveOrUpdate(notificationCampaignSent);
			    				}
						}
				    	notificationCampaignSentDaoForDML.updateInitialStatus(notificationCampaignSent.getSentId().toString(), notificationCampaignSent.getStatus());
				    }
			}
		}
	}catch (Exception e) {
		logger.error("Push notification error :: "+e);
	}
	}
	
	
	private static String postToURL(String message) throws IOException, IllegalStateException, UnsupportedEncodingException, RuntimeException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(uri);
        StringEntity input = new StringEntity(message,"UTF-8");
        input.setContentType("application/json; charset=utf-8");
        postRequest.setEntity(input);
        postRequest.setHeader("Authorization", optcultureFireBaseServerKey);
        HttpResponse response = httpClient.execute(postRequest);
        logger.info("mobile response ::"+response);
        int  statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            logger.error("Failed : HTTP error code : " + statusCode);
        }else if(statusCode == 200) {
        	logger.info("Success : HTTP Success code : " + statusCode);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String output;
        StringBuffer totalOutput = new StringBuffer();
        while ((output = br.readLine()) != null) {
            totalOutput.append(output).append("::").append("statusCode:"+statusCode);
        }
        return totalOutput.toString();
    }
	
	

	public static int getRandomNumberInts(){
		    Random r = new Random( System.currentTimeMillis() );
		    return 10000 + r.nextInt(20000);
	}
	
	/*private static final String plArr[] ={"|^GEN_firstName^|", "|^GEN_lastName^|", 
		"|^GEN_phone^|", "|^GEN_emailId^|", "|^GEN_addressOne^|",
		"|^GEN_addressTwo^|", "|^GEN_city^|","|^GEN_state^|", 
		"|^GEN_country^|", "|^GEN_pin^|"};*/

	
	/**
	 * this method replaces the placeholder value with the contact value
	 * @param orgMsg
	 * @param contact
	 * @return
	 *//*
	private String replacePlaceHolders(String orgMsg, Contacts contact) {
		
		String retMsg = orgMsg;
		
		for (String ph : plArr) {
		
			String cfStr = ph.substring(ph.indexOf('^')+1, ph.lastIndexOf('^'));
			String value;
		
			if(cfStr.startsWith("GEN_")) {
				
				cfStr = cfStr.substring(4);
				if(cfStr.equalsIgnoreCase("emailId"))	value = contact.getEmailId();
				else if(cfStr.equalsIgnoreCase("firstName"))	value = contact.getFirstName();
				else if(cfStr.equalsIgnoreCase("lastName"))	value = contact.getLastName();
				else if(cfStr.equalsIgnoreCase("addressOne"))	value = contact.getAddressOne();
				else if(cfStr.equalsIgnoreCase("addressTwo"))	value = contact.getAddressTwo();
				else if(cfStr.equalsIgnoreCase("city"))	value = contact.getCity();
				else if(cfStr.equalsIgnoreCase("state"))	value = contact.getState();
				else if(cfStr.equalsIgnoreCase("country"))	value = contact.getCountry();
				else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getZip() ;
				else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getMobilePhone();
				else if(cfStr.equalsIgnoreCase("zip"))	value = contact.getZip() ;
				else if(cfStr.equalsIgnoreCase("mobile"))	value = contact.getMobilePhone();
				else value = "";
				
				if(logger.isInfoEnabled()) logger.info("Gen token :" + cfStr + " - Value :" + value);
				try {
					if(value == null || value.trim().length() == 0) {
						retMsg = retMsg.replace(ph, "");
					}
					else if	(value != null && value.trim().length() > 0) {
					
						retMsg = retMsg.replace(ph, value);
					}
					
				} catch (Exception e) {
					logger.error("Exception ::::" , e);
				}
			} // if(cfStr.startsWith("GEN_"))
	
		
		} // for 
		
		return retMsg;
	} // replacePlaceHolders
	*/
	

	/**
	 *  handles SystemData images in background urls
	 */
}
