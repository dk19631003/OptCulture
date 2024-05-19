package org.mq.optculture.business.digitalReceipt;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DRSMSChannelSent;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.UserWAConfigs;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.beans.WATemplates;
import org.mq.marketer.campaign.beans.ZoneTemplateSettings;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.ExternalSMTPDigiReceiptSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSMSChannelSentDao;
import org.mq.marketer.campaign.dao.DRSMSChannelSentDaoForDML;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDaoForDML;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UserWAConfigsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.WATemplatesDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.dao.ZoneTemplateSettingsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.ReplacePlaceholderFromStrConetnt;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.RecommendationResponseObject;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.GSTLine;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.prism.PrismDRBody;
import org.mq.optculture.model.DR.prism.PrismDRCoupon;
import org.mq.optculture.model.DR.prism.PrismDRItem;
import org.mq.optculture.model.DR.prism.PrismDRTender;
import org.mq.optculture.model.DR.tender.COD;
import org.mq.optculture.model.DR.tender.Cash;
import org.mq.optculture.model.DR.tender.Charge;
import org.mq.optculture.model.DR.tender.Check;
import org.mq.optculture.model.DR.tender.CreditCard;
import org.mq.optculture.model.DR.tender.CustomTender;
import org.mq.optculture.model.DR.tender.DebitCard;
import org.mq.optculture.model.DR.tender.Deposit;
import org.mq.optculture.model.DR.tender.FC;
import org.mq.optculture.model.DR.tender.FCCheck;
import org.mq.optculture.model.DR.tender.Gift;
import org.mq.optculture.model.DR.tender.GiftCard;
import org.mq.optculture.model.DR.tender.Payments;
import org.mq.optculture.model.DR.tender.StoreCredit;
import org.mq.optculture.model.DR.tender.TravellerCheck;
import org.mq.optculture.model.couponcodes.CouponDiscountInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.PrismDREnum;
import org.mq.optculture.utils.PrismRequestTranslator;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Messagebox;

import com.google.gson.Gson;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

public class ProcessEReceipt extends Thread{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String serverName = PropertyUtil.getPropertyValue("schedulerIp");
	
	private String subject;
	private String displayTemp;
	private String lifeTimePoints;
	private String redeemedPoints;
	public String getRedeemedPoints() {
		return redeemedPoints;
	}

	public void setRedeemedPoints(String redeemedPoints) {
		this.redeemedPoints = redeemedPoints;
	}
	private String loyaltyEarnedToday;
	private String currencyBalance;
	public String getCurrencyBalance() {
		return currencyBalance;
	}

	public void setCurrencyBalance(String currencyBalance) {
		this.currencyBalance = currencyBalance;
	}

	public String getLoyaltyEarnedToday() {
		return loyaltyEarnedToday;
	}

	public void setLoyaltyEarnedToday(String loyaltyEarnedToday) {
		this.loyaltyEarnedToday = loyaltyEarnedToday;
	}

	public String getLifeTimePoints() {
		return lifeTimePoints;
	}

	public void setLifeTimePoints(String lifeTimePoints) {
		this.lifeTimePoints = lifeTimePoints;
	}
	
	private String pointsBalance;
	public String getPointsBalance() {
		return pointsBalance;
	}

	public void setPointsBalance(String pointsBalance) {
		this.pointsBalance = pointsBalance;
	}
	
	
	private String htmlContent;
	private String htmlContentForSMS;
	private String htmlContentForWA;
	private boolean isTaxExists =false, isBillTo =false, isShipTo =false;
	private String toEmailId;
	private String mobile;
	private Users user;
	//private Account account;
	//private PrismDRBody prismDRBody;
	private Long zoneId;
	private String templateName;
	//private String storeNo;
	long drJsonObjId;
	private DigitalReceiptUserSettings digitalReceiptUserSettings;
	private String editorType ;
	String messageHeader =Constants.STRING_NILL;
	String prsnlizeToFld=Constants.STRING_NILL;
	private DRBody drbody;
	private PrismBasedDRRequest prismRequest; 
	private DigitalReceipt digitalReceipt;
	private PrismDRBody prismDRBody;
	//private JSONObject jsonMainObj;
	private final String DR_PDF_URL=PropertyUtil.getPropertyValue("DRPdfGenerationUrl");
	private final String DR_PDF_DIV_TEMPLATE = PropertyUtil.getPropertyValue("DRPDFdivTemplate");
	
	private static Map<String, String> DRITEMS_PH_JSON_MAP = new HashMap<String, String>(); 
	
	static{
		
		/*DRITEMS_PH_JSON_MAP.put(Constants.STRING_NILL, value);
		DRITEMS_PH_JSON_MAP.put(key, value)
		DRITEMS_PH_JSON_MAP.put(key, value)
		DRITEMS_PH_JSON_MAP.put(key, value)
		DRITEMS_PH_JSON_MAP.put(key, value)
		DRITEMS_PH_JSON_MAP.put(key, value)*/
	}
	
	public ProcessEReceipt(String subject,String htmlContent, String toEmailId, String editorType,
			Users user, DRBody drbody, long drJsonObjId, String templateName,
			DigitalReceiptUserSettings digitalReceiptUserSettings, DigitalReceipt digitalReceipt,String mobile)  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        //this.account = account;
        this.drJsonObjId = drJsonObjId;
        this.templateName=templateName;
        this.digitalReceiptUserSettings = digitalReceiptUserSettings;
        this.editorType = editorType ;
        this.zoneId = digitalReceiptUserSettings.getZoneId();
        this.drbody = drbody;
        this.digitalReceipt = digitalReceipt;
        //this.storeNo = storeNo;
        this.mobile=mobile;
		
	}
	
	public ProcessEReceipt(String subject,String htmlContent, String toEmailId, String editorType,
			Users user, PrismDRBody drbody, long drJsonObjId, String templateName,
			DigitalReceiptUserSettings digitalReceiptUserSettings,  PrismBasedDRRequest prismRequest,String mobile)  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        //this.account = account;
        this.drJsonObjId = drJsonObjId;
        this.templateName=templateName;
        this.digitalReceiptUserSettings = digitalReceiptUserSettings;
        this.editorType = editorType ;
        this.zoneId = digitalReceiptUserSettings.getZoneId();
        this.prismDRBody = drbody;
        this.prismRequest = prismRequest;
        //this.storeNo = storeNo;
        this.mobile=mobile;
		
	}
	/*public ProcessEReceipt(String subject,String htmlContent, String toEmailId, String editorType,
			Users user, PrismDRBody prismDRBody, long drJsonObjId, String templateName,
			DigitalReceiptUserSettings digitalReceiptUserSettings, String storeNo)  {
		
		this.subject = subject;
    	this.htmlContent = htmlContent;
        this.toEmailId = toEmailId;
        this.user = user;
        //this.account = account;
        //this.prismDRBody = prismDRBody;
        this.drJsonObjId = drJsonObjId;
        this.templateName=templateName;
        this.digitalReceiptUserSettings = digitalReceiptUserSettings;
        this.editorType = editorType ;
        this.zoneId = digitalReceiptUserSettings.getZoneId();
        this.storeNo = storeNo;
		
	}
	*/
	
	public void run() {
		
		if(drbody != null )processDR();
		if(prismDRBody != null)processPrismDR();
		//if(magentoDRBody!=null)processDR();
	}
				
	public void processPrismDR(){

		
		try {
			DRSentDao drSentDao = null;
			DRSentDaoForDML drSentDaoForDML = null;
			DRSMSSentDao drSmsSentDao = null;
			DRSMSSentDaoForDML drSmsSentDaoForDML = null;
			OrganizationStoresDao organizationStoresDao = null;
			UsersDao usersDao = null;
			SMSSuppressedContactsDao smsSuppressedContactsDao=null;

			try{
				drSentDao = (DRSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
				drSentDaoForDML = (DRSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SENT_DAO_ForDML);
				drSmsSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
				drSmsSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
				organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
				usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				smsSuppressedContactsDao =(SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);;

			}
			catch(Exception e){
				logger.error("Exception while getting daos...");
			}

			
			String DRItemHashTags = PropertyUtil.getPropertyValueFromDB("DRItemHashTags"); 
			String DRReceiptHashTags = PropertyUtil.getPropertyValueFromDB("DRReceiptHashTags"); 
			String DRTenderHashTags = PropertyUtil.getPropertyValueFromDB("DRTenderHashTags"); 
			try{
				Set<String> DRItemHashTagsSet = getUserJsonSettingSet(DRItemHashTags); 
				Set<String> DRReceiptHashTagsSet = getUserJsonSettingSet(DRReceiptHashTags); 
				Set<String> DRTenderHashTagsSet = getUserJsonSettingSet(DRTenderHashTags); 

				
				String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentAdvLoopPlaceHolders"); 
				String paymentChangeSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentChangeLoopPlaceHolders"); 
				String itemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemAdvLoopPlaceHolders");
				String DRRefItemLoopPlaceHolder = PropertyUtil.getPropertyValue("DRRefItemLoopPlaceHolder");
				
				String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
				String[] paymentChnagesSetPlaceHoldersArr = paymentChangeSetPlaceHolders.split(",");
				String[] itemsSetPlaceHoldersArr = itemsSetPlaceHolders.split(",");
				String[] DRRefItemLoopPlaceHolderArr = DRRefItemLoopPlaceHolder.split(",");
				
				
				boolean hasRefSection = false;
				if(htmlContent.indexOf(DRRefItemLoopPlaceHolderArr[0]) != -1 && htmlContent.indexOf(DRRefItemLoopPlaceHolderArr[5]) != -1) {
					hasRefSection = true;
					
				}
				
				htmlContent = replaceItemLoopBlock(htmlContent, itemsSetPlaceHoldersArr, DRItemHashTagsSet, paymentSetPlaceHoldersArr,
						paymentChnagesSetPlaceHoldersArr, DRRefItemLoopPlaceHolderArr, prismDRBody);
				
				htmlContent = replaceTenderPhValues(htmlContent,  DRTenderHashTagsSet, prismDRBody, paymentSetPlaceHoldersArr,paymentChnagesSetPlaceHoldersArr);
				
				htmlContent = htmlContent.replace(paymentSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(paymentSetPlaceHoldersArr[10],Constants.STRING_NILL);
				htmlContent = htmlContent.replace(paymentChnagesSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(paymentChnagesSetPlaceHoldersArr[4],Constants.STRING_NILL);
				htmlContent = htmlContent.replace("[CHANGE_VISIBLE]", isChnageExists ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[CHANGE_ROW_DISPLAYSTYLE]", isChnageExists ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[TENDER_ROW1_DISPLAYSTYLE]", isPaymentNumberNTake ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[TENDER_ROW2_DISPLAYSTYLE]", isAuthTaken ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[TENDER_ROW3_DISPLAYSTYLE]", isBaseTaken ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				if(hasRefSection)htmlContent = htmlContent.replace(DRRefItemLoopPlaceHolderArr[0], Constants.STRING_NILL).replace(DRRefItemLoopPlaceHolderArr[5],Constants.STRING_NILL);
				htmlContent = htmlContent.replace(itemsSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(itemsSetPlaceHoldersArr[8],Constants.STRING_NILL);
				htmlContent = replaceIndividualPhValues(DRReceiptHashTagsSet, htmlContent, prismDRBody);
				}catch(Exception e){
					logger.error("Exception :: ",e);
					return ;
				}
				// Remove ##START## ##END## PH from the template
				
				
				
				// Replace All individual place holders place Holders
				
				htmlContent = htmlContent.replace( "[TOTALSAVED_STYLE]", isTotalYouSaved ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[COUPON_VISIBLE]", isCouponVisible ?  Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[ECOM_VISIBLE]", isECOMVisible ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[EMP_VISIBLE]", isEmpVisible ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[PHONE_VISIBLE]", isPhnVisible ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD1_VISIBLE]", isStoreHeading1 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD2_VISIBLE]", isStoreHeading2 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD3_VISIBLE]", isStoreHeading3 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD4_VISIBLE]", isStoreHeading4 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD5_VISIBLE]", isStoreHeading5 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				htmlContent = htmlContent.replace("[STOREHEAD6_VISIBLE]", isStoreHeading6 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
				
				
				// remove all no-value place holders form template
				htmlContent = removeNoValuePhValues(htmlContent);
				
				
				htmlContent = addDomainNameToImageURLs(htmlContent);
				//Get email from json request..
				//JSONObject receiptObj = (JSONObject)jsonBodyObj.get("Receipt");
				//DRReceipt drReceipt = drJsonRequest.getBody().getReceipt();
				
				
			
				String recieptNumberStr = prismDRBody.getDocument_number();
				
				//chnaged for the requirement docsid instead recpt number
				String documentNumberStr =prismDRBody.getSid();
				
				
				//preparing html content which handles clicks
				htmlContent = PrepareFinalHtml.prepareStuff(htmlContent, editorType, documentNumberStr, recieptNumberStr);
				//logger.info("htmlContent======"+htmlContent);
				
				htmlContent=htmlContent.replace("[DRBCreceiptNumber]",recieptNumberStr);
				htmlContent=htmlContent.replace("[DRBCdocumentNumber]",documentNumberStr);
				
				//logger.info("After replacing |^receiptNumber^| with recieptNumberStr htmlContent======"+htmlContent);
				
				String textContent = htmlContent; 
				
			
				String from = user.getEmailId();
				String jobId = Constants.EQ_TYPE_DIGITALRECIEPT;
				
				
				String nonDynamicFrmName=Constants.STRING_NILL;
			    String nonDynamicFrmEmail=Constants.STRING_NILL;
				String replyTo = user.getEmailId();
					
				String nonDynamicReplyToEmail=replyTo;
					//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
					
						if(digitalReceiptUserSettings != null) {
							
							//subject
							String recieptSub = digitalReceiptUserSettings.getSubject();
							if(recieptSub != null &&
									recieptSub.trim().length() > 0) {
								
								subject = recieptSub;
							}
							
							//from email
							String recieptEmail = digitalReceiptUserSettings.getFromEmail();
							
							if(recieptEmail != null && 
									recieptEmail.trim().length() > 0) {
								
								nonDynamicFrmEmail = from = recieptEmail;
								
								if(digitalReceiptUserSettings.getReplyToEmail() != null && digitalReceiptUserSettings.getReplyToEmail().trim().length() > 0) {
									
									replyTo = digitalReceiptUserSettings.getReplyToEmail();
								}else{
									
									replyTo = recieptEmail;
								}
							}
							logger.info("from2======"+from);
							//fromName
							
							String recieptFromName = digitalReceiptUserSettings.getFromName();
							nonDynamicFrmName = recieptFromName;
							if(recieptFromName != null && 
									recieptFromName.trim().length() > 0) {
								
								nonDynamicFrmEmail = from = recieptFromName + "<" + from + ">";
							}
							
							logger.info("from3======"+from);
							
							
							
							//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  STARTS
							try {
								List<OrganizationStores> organizationStoresList = organizationStoresDao.findByOrganization(user.getUserOrganization().getUserOrgId());
								
								String store=prismDRBody.getStore_number();
								
								
								OrganizationStores requiredOrganizationStore = null;
								for(OrganizationStores anOrganizationStore : organizationStoresList){
									if(anOrganizationStore.getHomeStoreId() != null && anOrganizationStore.getHomeStoreId().equals(store)){
										requiredOrganizationStore = anOrganizationStore;
									}
								}
								
								if(requiredOrganizationStore != null ){
									
									if(requiredOrganizationStore.getFromEmailId() != null){
										
										if(digitalReceiptUserSettings.isIncludeDynamicFrmEmail())
												from = requiredOrganizationStore.getFromEmailId();
										else
											    from = nonDynamicFrmEmail;
									}else if(requiredOrganizationStore.getFromEmailId() == null){
										//keep previous email id
									}
										
									String tempEmail;
									if(requiredOrganizationStore.getFromName() != null){
										
										if(digitalReceiptUserSettings.isIncludeDynamicFrmName()){
											
											
											if(from.contains("<")){
												tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
											}else{
												tempEmail = from;
											}
											from = requiredOrganizationStore.getFromName() +"<"+ tempEmail+">";
											
										}
										else{
											
											if(from.contains("<")){
												tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
											}else{
												tempEmail = from;
											}
											
											if(nonDynamicFrmName != null){
												from = nonDynamicFrmName +"<"+ tempEmail+">";
											}else{
												from = tempEmail;
											}
												
										}
									}else if(requiredOrganizationStore.getFromName() == null){
										
										if(nonDynamicFrmName != null){
											
											if(from.contains("<")){
												tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
											}else{
												tempEmail = from;
											}
											
											from = nonDynamicFrmName + "<"+ tempEmail+">";
										}
										
									}
										
									/*if(requiredOrganizationStore.getReplyToEmailId() != null)
										replyTo = requiredOrganizationStore.getReplyToEmailId();
									*/
									if(requiredOrganizationStore.getReplyToEmailId() != null)
									{
										if(digitalReceiptUserSettings.isIncludeDynamicReplyToEmail()) {
											replyTo = requiredOrganizationStore.getReplyToEmailId();;
										}else
											replyTo = nonDynamicReplyToEmail;
									}
									
								}
							}catch (Exception e) {
								logger.info("Exception>>>>>>>>>>>>>>"+e);
							}	
									
						}
						JSONObject jsonMainObj= null;
						try {
							PrismRequestTranslator PrismRequestTranslator = new PrismRequestTranslator();
							logger.debug("prismDRBody ==="+prismDRBody.getCreated_datetime());
							DRBody drbody = PrismRequestTranslator.convertPrismRequest(prismDRBody);
							Gson gson = new Gson();
							String drRequestJson = gson.toJson(drbody);
							 jsonMainObj = (JSONObject)JSONValue.parse(drRequestJson);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							logger.error("Exception ", e1);
						}
						Object[] obj1={ htmlContent, jsonMainObj, user };
					
					
					//	String docSid = (String)receiptObj.get(Constants.DR_DOCSID);
								
						//String[] docDateArr = POSFieldsEnum.salesDate.getDrKeyField().split(Constants.DELIMETER_DOUBLECOLON);
						
						String docSidStr =prismDRBody.getSid();
						//String docDate = prismDRBody.getCreated_datetime();
						
						
						
						String storeNumberStr = prismDRBody.getStore_number();
						String sbs = prismDRBody.getSubsidiary_number();
						htmlContentForSMS = htmlContent;
						if(digitalReceiptUserSettings.isEnabled()){
						DRSent drSent = null;
						boolean isSameDocSid = false;
						//TODO need to uncomment once the UAT is done
						try {
							if(docSidStr != null ){
								drSent = drSentDao.findBy(user.getUserId(), docSidStr, toEmailId);
								if(drSent == null) {
									
									drSent = drSentDao.findBy(user.getUserId(), docSidStr);
									isSameDocSid = (drSent != null);
								}
							}
						} catch (Exception e) {
							logger.error("Exception ",e );
						}
						ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
						Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj1, drSent);
						StringBuffer phKeyValStr = null;
						if(obj2[0] != null){
							
							htmlContent =(String)obj2[0];
							textContent = htmlContent;
						}
						if(obj2[1] != null){
							
							phKeyValStr= (StringBuffer) obj2[1];
						}
						logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
						String phValStr =null;
						if(phKeyValStr != null && phKeyValStr.length() > 0){
							
							 phValStr =phKeyValStr.toString();
						}
						
						boolean updateCount = (drSent != null && !isSameDocSid);		
						if((drSent == null && !isSameDocSid) ||(drSent != null && isSameDocSid)) {
							 
							/*drSent = new DRSent(Constants.DR_STATUS_SUCCESS, toEmailId, Calendar.getInstance(),
									user.getUserId(), drJsonObjId);*/
							drSent = new DRSent(Constants.DR_STATUS_SUBMITTED, toEmailId, Calendar.getInstance(),
									user.getUserId(), drJsonObjId);
							
							 drSent.setDocSid(docSidStr);
							 drSent.setSentCount(1);
						 }
						drSent.setSubject(subject);
						 drSent.setHtmlStr(htmlContent);
						 drSent.setPhValStr(phValStr);
						 drSent.setTemplateName(templateName);
						 if(storeNumberStr != null && !storeNumberStr.trim().isEmpty())drSent.setStoreNumber(storeNumberStr);
						 if(sbs != null && !sbs.trim().isEmpty())drSent.setSbsNumber(sbs);
						 /**new parameter in drsent table for the sack of drReports
						  * */
						 drSent.setMyTemplateId(digitalReceiptUserSettings!=null ? digitalReceiptUserSettings.getMyTemplateId() : null);
						 drSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
						 

						drSentDaoForDML.saveOrUpdate(drSent);
						if(updateCount) {
							
							try {
								//drSentDao.updateSentCount(drSent.getId());
								drSentDaoForDML.updateSentCount(drSent.getId());
							} catch (BaseDAOException e) {
							logger.error("Exception while updating the sent count", e);
							}
						}
						String sentId = null;
						try {
							sentId = EncryptDecryptUrlParameters.encrypt(drSent.getId()+Constants.STRING_NILL);
						} catch (Exception e) {
							logger.error("Exception ::::", e);
						}
						htmlContent = htmlContent.replace("[sentId]", sentId+"");
						if(displayTemp != null){
							String replacedDispTemplate = displayTemp.replace("\\n", "<br></br>");
							htmlContent = htmlContent.replace("[LoyaltyDisplayTemplate]", replacedDispTemplate+"");
						}
						//if(lifeTimePoints != null){
							htmlContent = htmlContent.replace("[LifeTimePoints]", lifeTimePoints == null ? "" :lifeTimePoints);
							htmlContent = htmlContent.replace("[LoyaltyEarnedToday]", loyaltyEarnedToday == null ? "" :loyaltyEarnedToday);
							htmlContent = htmlContent.replace("[LoyaltyRedeemedToday]", redeemedPoints == null ? "" :redeemedPoints);
						
						//}
						
						String redeemedAmount = getLoyaltyRedemption(prismRequest, user);
						htmlContent = htmlContent.replace("[LoyaltyRedeemedAmount]", redeemedAmount+"");
						
						textContent = textContent.replace("[sentId]", sentId+"");
						drSent.setHtmlStr(htmlContent);
						drSentDaoForDML.saveOrUpdate(drSent);
						//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
						 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
							try {
								 

								if(user != null) {
									messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
								}
								logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
								ExternalSMTPDigiReceiptSender externalSMTPSender =  new ExternalSMTPDigiReceiptSender();
								/*htmlContent = htmlContent.replace("[sentId]", drSent.getId()+"");
								textContent = textContent.replace("[sentId]", drSent.getId()+"");*/
								//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
								externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, toEmailId, replyTo);
							
							} catch (Exception e) {
								
								logger.debug("Exception while sending through sendGridAPI .. returning ",e);
								
							}
						}
						}
						 if(user.isReceiptOnSMS() && digitalReceiptUserSettings.isSmsEnabled() &&  mobile != null && !mobile.isEmpty()){
							 DRSMSSent drSmsSent = null;
							 boolean isSameDocSid = false;
							 try {
							
								//APP-4253
									List<SMSSuppressedContacts> SuprList =  smsSuppressedContactsDao.searchContactsById(user, mobile);
										if(!SuprList.isEmpty() && SuprList.size()==1) {
												
												logger.info("DRsms cannot be sent for Suppressed number");
												return;
										} 
								 
							 drSmsSent = drSmsSentDao.findBy(user.getUserId(), docSidStr, mobile);
							 if(drSmsSent == null) {
									drSmsSent = drSmsSentDao.findBy(user.getUserId(), docSidStr);
										isSameDocSid = (drSmsSent != null);
 								}
							 
							 ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
							 Object[] obj = { htmlContentForSMS, jsonMainObj, user };
								Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj, null);
								StringBuffer phKeyValStr = null;
								if(obj2[0] != null){
									
									htmlContentForSMS =(String)obj2[0];
									textContent = htmlContentForSMS;
								}
								if(obj2[1] != null){
									
									phKeyValStr= (StringBuffer) obj2[1];
								}
								logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
								String phValStr =null;
								if(phKeyValStr != null && phKeyValStr.length() > 0){
									
									 phValStr =phKeyValStr.toString();
								}

							 boolean updateSmsCount = (drSmsSent != null && !isSameDocSid);		
 							if((drSmsSent == null && !isSameDocSid) ||(drSmsSent != null && isSameDocSid)) {
							 								 
								drSmsSent = new DRSMSSent(Constants.DR_STATUS_SUBMITTED, mobile, Calendar.getInstance(),
	 										user.getUserId(), drJsonObjId);
							 								
								 drSmsSent.setDocSid(docSidStr);
								 drSmsSent.setSentCount(1);
							 }
 							 drSmsSent.setStoreNumber(storeNumberStr);
 							 drSmsSent.setSbsNumber(sbs);
 						//APP-3360
 							 drSmsSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
 							 drSmsSent.setHtmlStr(htmlContentForSMS);
 							 drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
 							 logger.info("drSmsSent.getId() "+drSmsSent.getId());
 							logger.info(htmlContentForSMS.contains("[sentId]"));
 							String drSmsSentId = null;
 							try {
 								drSmsSentId = EncryptDecryptUrlParameters.encrypt(drSmsSent.getId()+Constants.STRING_NILL);
 							} catch (Exception e) {
 								logger.error("Exception ::::", e);
 							}
 							htmlContentForSMS = htmlContentForSMS.replace("[sentId]", drSmsSentId+"");
 							if(updateSmsCount) {		
		 						try {
							 		drSmsSentDaoForDML.updateSentCount(drSmsSent.getId());
							 	} catch (BaseDAOException e) {
							 	logger.error("Exception while updating the sent count", e);
							 	}
 							}
 							htmlContentForSMS = htmlContentForSMS.replace("[sentId]", drSmsSentId+"");
							if(displayTemp != null){
								String replacedDispTemplate = displayTemp.replace("\\n", "<br></br>");
								htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyDisplayTemplate]", replacedDispTemplate+"");
							}
							//if(lifeTimePoints != null){
							htmlContentForSMS = htmlContentForSMS.replace("[LifeTimePoints]", lifeTimePoints == null ? "" :lifeTimePoints);
							htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyEarnedToday]", loyaltyEarnedToday == null ? "" :loyaltyEarnedToday);
							htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyRedeemedToday]", redeemedPoints == null ? "" :redeemedPoints);
								//htmlContentForSMS = htmlContentForSMS.replace("[LifeTimePoints]", lifeTimePoints+"");
							
							//}
							//DigitalReceipt digitalReceipt = gson.fromJson(drRequestJson, DigitalReceipt.class);
							String redeemedAmount = getLoyaltyRedemption(prismDRBody, user);
							
							htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyRedeemedAmount]", redeemedAmount+"");
							textContent = textContent.replace("[sentId]", drSmsSentId+"");
							htmlContentForSMS = htmlContentForSMS.replace("requestedAction=pdf","requestedAction=pdfSMS");
							htmlContentForSMS = htmlContentForSMS.replace("requestedAction=click","requestedAction=clickSMS");
							htmlContentForSMS = htmlContentForSMS.replace("requestedAction=open","requestedAction=openSMS");
							
							//html minimizer
						//	htmlContentForSMS= minimizeHtml(htmlContentForSMS);

							drSmsSent.setHtmlStr(htmlContentForSMS);
							drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
							sendSMSFromDR(digitalReceiptUserSettings,drSmsSent, "", "", currencyBalance);
						 } catch (Exception e) {
								logger.error("Exception ",e );
							}
						 }		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception while sending DR",e);
		}
		
		
	}
	
	public String getLoyaltyRedemption(Object DrRequest, Users user){
		
		if(DrRequest == null) return Constants.STRING_NILL;
		String  redeemedAmount = "0";
		try {
			if(DrRequest instanceof PrismBasedDRRequest) {
				PrismBasedDRRequest prismBasedDRRequest = (PrismBasedDRRequest)DrRequest;
				DROptCultureDetails DROptCultureDetails = prismBasedDRRequest.getOptcultureDetails();
				if(DROptCultureDetails != null && DROptCultureDetails.getLoyaltyRedeem()!= null){
					
					 redeemedAmount = DROptCultureDetails.getLoyaltyRedeem().getDiscountAmount() != null 
							? DROptCultureDetails.getLoyaltyRedeem().getDiscountAmount() : Constants.STRING_NILL;
				}
				
			}else if(DrRequest instanceof DigitalReceipt){
				logger.debug("DrRequest instanceof DigitalReceipt===");
				DigitalReceipt DigitalReceipt = (DigitalReceipt)DrRequest;
				DROptCultureDetails DROptCultureDetails = DigitalReceipt.getOptcultureDetails();
				if(DROptCultureDetails != null && DROptCultureDetails.getLoyaltyRedeem() !=null){
					logger.debug("DrRequest instanceof DigitalReceipt===DROptCultureDetails != null");
					redeemedAmount = DROptCultureDetails.getLoyaltyRedeem().getDiscountAmount() != null 
							? DROptCultureDetails.getLoyaltyRedeem().getDiscountAmount() : Constants.STRING_NILL;
				}else if(DROptCultureDetails == null){
					logger.debug("DrRequest instanceof DigitalReceipt===DROptCultureDetails = null");
					String nonInventoryItem = user.getNonInventoryItem();
					if( user.getRedeemTender() != null){
						logger.debug("user.getRedeemTender()==="+user.getRedeemTender()+"   "+user.getRedeemTender().startsWith("Credit Card:"));
						logger.debug("digitalReceipt.getbody=="+DigitalReceipt.getBody());
						DRBody drBody = DigitalReceipt.getBody();
						if(drBody != null && user.getRedeemTender().equalsIgnoreCase("COD")){
							 COD COD = drBody.getCOD();
							 if(COD != null && COD.getAmount() != null) redeemedAmount = COD.getAmount();
							
						}else if(drBody != null && user.getRedeemTender().startsWith("Credit Card:")){//Credit Card:LOYALCredit Card:LOYAL
							logger.debug("user.getRedeemTender()==="+user.getRedeemTender());
							List<CreditCard> CreditCards = drBody.getCreditCard();
							String type= user.getRedeemTender().split(":")[1];
							if(CreditCards != null && !CreditCards.isEmpty()){
								
								for (CreditCard creditCard : CreditCards) {
									logger.debug("creditCard.getType()==="+creditCard.getType()+"===="+type);
									if(creditCard.getType() == null || !creditCard.getType().equalsIgnoreCase(type)) continue;
									
									redeemedAmount = creditCard.getAmount();
									logger.debug("redeemedAmount =="+redeemedAmount);
								}
							}
							
						}else if(drBody != null && nonInventoryItem!=null && !nonInventoryItem.isEmpty()) {
							if(!drBody.getReceipt().getReceiptType().equalsIgnoreCase("2"))  {
							String[] nonInventoryItemValue = nonInventoryItem.split(""+Constants.DELIMETER_COLON);
							String nonInventoryItemField = nonInventoryItemValue[0];
							String nonInventoryItemFieldValue=nonInventoryItemValue[1];
							PropertyDescriptor nonDiscItemNote  = null;
							for (DRItem drItem : drBody.getItems()){
							nonDiscItemNote = new PropertyDescriptor(nonInventoryItemField, drItem.getClass());
							Object nonDiscItemObj = nonDiscItemNote.getReadMethod().invoke(drItem);
							if(nonDiscItemObj == null || nonDiscItemObj.toString().isEmpty() ) continue;
							if(nonInventoryItemFieldValue.equalsIgnoreCase(nonDiscItemObj.toString())) {
								
								if((Double.valueOf(drItem.getExtPrc()))<0) {
									redeemedAmount = (String.valueOf(Math.abs(Double.valueOf(drItem.getDocItemPrc()))));
									break;
								}
							}
							}
							}
						}
						
					}
					
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return redeemedAmount;
		
	}


private List<ContactsLoyalty> FindEnrollListByEmailORPhone(String Email,String phone, Users user) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findmembershipByEmailORPhone(Email, phone, user);
	}
	


		public void processDR() {
			
			try {
				DRSentDao drSentDao = null;
				DRSentDaoForDML drSentDaoForDML = null;
				DRSMSSentDao drSmsSentDao = null;
				DRSMSSentDaoForDML drSmsSentDaoForDML = null;
				DRSMSChannelSentDao drSmsChannelSentDao = null;
				DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = null;
				OrganizationStoresDao organizationStoresDao = null;
				SMSSuppressedContactsDao smsSuppressedContactsDao=null;
				UsersDao usersDao = null;
				RecommendationResponseObject recomm = null;
				 List<RecommendationResponseObject> recommlist = new ArrayList<>();
				JSONObject recommJSON = null;

				try{
					drSentDao = (DRSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
					drSentDaoForDML = (DRSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SENT_DAO_ForDML);
					drSmsSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
					drSmsSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
					drSmsChannelSentDao = (DRSMSChannelSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_Channel_SENT_DAO);
					drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
					organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					smsSuppressedContactsDao =(SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);;

				}
				catch(Exception e){
					logger.error("Exception while getting daos...");
				}
					
					String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting"); 
					Map<String, String> userJSONSettingHM  = null;
					String DRReceiptHashTags = PropertyUtil.getPropertyValueFromDB("DRReceiptHashTags"); 
					String DRTenderHashTags = PropertyUtil.getPropertyValueFromDB("DRTenderHashTags"); 
					
					Set<String> DRReceiptHashTagsSet = getUserJsonSettingSet(DRReceiptHashTags); 
					Set<String> DRTenderHashTagsSet = getUserJsonSettingSet(DRTenderHashTags); 
					Gson gson = new Gson();
					String drRequestBodyJson = gson.toJson(drbody);
					JSONObject jsonMainObj =  (JSONObject)JSONValue.parse(drRequestBodyJson);
					try{
					userJSONSettingHM = getUserJsonSettingMap(userJSONSettings); 
					//for (String phStr : userJSONSettingHM.keySet()) {
						
				//		logger.debug("phStr ==="+phStr+ userJSONSettingHM.get(phStr)); APP-4568
						
						
					//}//for
					
					String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentAdvLoopPlaceHolders"); 
					String paymentChangeSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentChangeLoopPlaceHolders"); 
					String itemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemAdvLoopPlaceHolders");
					String DRRefItemLoopPlaceHolder = PropertyUtil.getPropertyValue("DRRefItemLoopPlaceHolder");
					//String DRDCPlaceHolder = PropertyUtil.getPropertyValue("DRDCPlaceHolder");
					
					String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
					String[] paymentChnagesSetPlaceHoldersArr = paymentChangeSetPlaceHolders.split(",");
					String[] itemsSetPlaceHoldersArr = itemsSetPlaceHolders.split(",");
					String[] DRRefItemLoopPlaceHolderArr = DRRefItemLoopPlaceHolder.split(",");
					//String[] DRDCPlaceHolderArr = DRDCPlaceHolder.split(",");
					
					String billToSetPlaceHolders = PropertyUtil.getPropertyValue("DRBillToLoopPlaceHolders");
					String shipToSetPlaceHolders = PropertyUtil.getPropertyValue("DRShipToLoopPlaceHolders");
					
					
					String[] billToSetPlaceHoldersArr = billToSetPlaceHolders.split(",");
					String[] shipToSetPlaceHoldersArr = shipToSetPlaceHolders.split(",");
					
					boolean hasRefSection = false;
					//boolean hasDCSection = false;
					if(htmlContent.indexOf(DRRefItemLoopPlaceHolderArr[0]) != -1 && htmlContent.indexOf(DRRefItemLoopPlaceHolderArr[5]) != -1) {
						hasRefSection = true;
						
					}
					
					/*if(htmlContent.indexOf(DRDCPlaceHolderArr[0]) != -1 && htmlContent.indexOf(DRDCPlaceHolderArr[3]) != -1) {
						hasDCSection = true;
						
					}*/
					//Lookup employeID from  info1 & info2 and mobilephone from phone1 & phone 2
					
								
					
					/*boolean soTest = testForSONumber(htmlContent, itemsSetPlaceHoldersArr, userJSONSettingHM, 
							jsonMainObj, paymentSetPlaceHoldersArr, DRRefItemLoopPlaceHolderArr);*/
					
					htmlContent = replaceItemPhValues(htmlContent, itemsSetPlaceHoldersArr, userJSONSettingHM, 
							drbody, paymentSetPlaceHoldersArr,paymentChnagesSetPlaceHoldersArr,DRRefItemLoopPlaceHolderArr);
					htmlContent = replaceGSTPhValues(htmlContent,  userJSONSettingHM, drbody);
					
					htmlContent = replaceReceiptPhValues(htmlContent, itemsSetPlaceHoldersArr, userJSONSettingHM, 
							drbody, paymentSetPlaceHoldersArr,paymentChnagesSetPlaceHoldersArr,DRRefItemLoopPlaceHolderArr);
					
					htmlContent = replaceBillTo_ShipToPhValues(htmlContent, userJSONSettingHM, jsonMainObj, billToSetPlaceHoldersArr, shipToSetPlaceHoldersArr);
					htmlContent = htmlContent.replace(billToSetPlaceHoldersArr[0], "").replace(billToSetPlaceHoldersArr[1],"");
					htmlContent = htmlContent.replace(shipToSetPlaceHoldersArr[0], "").replace(shipToSetPlaceHoldersArr[1],"");
					
					htmlContent = htmlContent.replace(paymentSetPlaceHoldersArr[0], "").replace(paymentSetPlaceHoldersArr[4],"");
					htmlContent = htmlContent.replace(itemsSetPlaceHoldersArr[0], "").replace(itemsSetPlaceHoldersArr[5],"");
					htmlContent = htmlContent.replace(paymentSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(paymentSetPlaceHoldersArr[10],Constants.STRING_NILL);
					htmlContent = htmlContent.replace(paymentChnagesSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(paymentChnagesSetPlaceHoldersArr[4],Constants.STRING_NILL);
					htmlContent = htmlContent.replace("[CHANGE_VISIBLE]", isChnageExists ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[CHANGE_ROW_DISPLAYSTYLE]", isChnageExists ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[TENDER_ROW1_DISPLAYSTYLE]", isPaymentNumberNTake ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[TENDER_ROW2_DISPLAYSTYLE]", isAuthTaken ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[TENDER_ROW3_DISPLAYSTYLE]", isBaseTaken ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					if(hasRefSection)htmlContent = htmlContent.replace(DRRefItemLoopPlaceHolderArr[0], Constants.STRING_NILL).replace(DRRefItemLoopPlaceHolderArr[5],Constants.STRING_NILL);
					htmlContent = htmlContent.replace(itemsSetPlaceHoldersArr[0], Constants.STRING_NILL).replace(itemsSetPlaceHoldersArr[8],Constants.STRING_NILL);
					}catch(Exception e){
						logger.error("Exception :: ",e);
						return ;
					}
					// Remove ##START## ##END## PH from the template
					
					
					
					// Replace All individual place holders place Holders
					htmlContent = replaceIndividualPhValues(DRReceiptHashTagsSet, htmlContent, drbody.getReceipt());
					htmlContent = replaceIndividualPhValues(userJSONSettingHM, htmlContent, jsonMainObj);
					
					htmlContent = htmlContent.replace( "[TOTALSAVED_STYLE]", isTotalYouSaved ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[COUPON_VISIBLE]", isCouponVisible ?  Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[ECOM_VISIBLE]", isECOMVisible ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[EMP_VISIBLE]", isEmpVisible ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[PHONE_VISIBLE]", isPhnVisible ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD1_VISIBLE]", isStoreHeading1 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD2_VISIBLE]", isStoreHeading2 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD3_VISIBLE]", isStoreHeading3 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD4_VISIBLE]", isStoreHeading4 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD5_VISIBLE]", isStoreHeading5 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					htmlContent = htmlContent.replace("[STOREHEAD6_VISIBLE]", isStoreHeading6 ? Constants.STRING_NILL: " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					//htmlContent = replaceExisting_ExtPrcWithTax_IfAny(jsonMainObj, htmlContent);
					
					
					// remove all no-value place holders form template
					htmlContent = removeNoValuePhValues(htmlContent);
					
					
					htmlContent = addDomainNameToImageURLs(htmlContent);
					//chnaged for the requirement docsid instead recpt number
					
					String documentNumberStr = drbody.getReceipt().getDocSID();
					String recieptNumberStr = drbody.getReceipt().getInvcNum();
					
					List<CustomTender> customTenderList = drbody!=null ? drbody.getCustomTender() : null; //APP-4189
					if(customTenderList != null) {
						for(CustomTender customTender : customTenderList) {
							if(customTender.getType()!=null && customTender.getType().equalsIgnoreCase(Constants.CNI) ) {
								documentNumberStr = customTender.getNumber()!=null ? customTender.getNumber() : ""; // incase credit note, CN No. should be printed below barcode
							}
						}
					}
					//preparing html content which handles clicks
					htmlContent = PrepareFinalHtml.prepareStuff(htmlContent, editorType, documentNumberStr, recieptNumberStr);
					
					String textContent = htmlContent; 
					
				
					String from = user.getEmailId();
					String jobId = Constants.EQ_TYPE_DIGITALRECIEPT;
					
					
					String nonDynamicFrmName=Constants.STRING_NILL;
				    String nonDynamicFrmEmail=Constants.STRING_NILL;
					String replyTo = user.getEmailId();
						
					String nonDynamicReplyToEmail=replyTo;
						//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
						
							if(digitalReceiptUserSettings != null) {
								
								//subject
								String recieptSub = digitalReceiptUserSettings.getSubject();
								if(recieptSub != null &&
										recieptSub.trim().length() > 0) {
									
									subject = recieptSub;
								}
								
								//from email
								String recieptEmail = digitalReceiptUserSettings.getFromEmail();
								
								if(recieptEmail != null && 
										recieptEmail.trim().length() > 0) {
									
									nonDynamicFrmEmail = from = recieptEmail;
									
									if(digitalReceiptUserSettings.getReplyToEmail() != null && digitalReceiptUserSettings.getReplyToEmail().trim().length() > 0) {
										
										replyTo = digitalReceiptUserSettings.getReplyToEmail();
									}else{
										
										replyTo = recieptEmail;
									}
								}
								logger.info("from2======"+from);
								//fromName
								
								String recieptFromName = digitalReceiptUserSettings.getFromName();
								nonDynamicFrmName = recieptFromName;
								if(recieptFromName != null && 
										recieptFromName.trim().length() > 0) {
									
									nonDynamicFrmEmail = from = recieptFromName + "<" + from + ">";
								}
								
								logger.info("from3======"+from);
								
								
								
								//asana task for 2.4.3 DR "from email" from Multi Stores/Email addresses  STARTS
								try {
									List<OrganizationStores> organizationStoresList = organizationStoresDao.findByOrganization(user.getUserOrganization().getUserOrgId());
									
									String store=drbody.getReceipt().getStore();
									
									
									OrganizationStores requiredOrganizationStore = null;
									for(OrganizationStores anOrganizationStore : organizationStoresList){
										if(anOrganizationStore.getHomeStoreId() != null && anOrganizationStore.getHomeStoreId().equals(store)){
											requiredOrganizationStore = anOrganizationStore;
										}
									}
									
									if(requiredOrganizationStore != null ){
										
										if(requiredOrganizationStore.getFromEmailId() != null){
											
											if(digitalReceiptUserSettings.isIncludeDynamicFrmEmail())
													from = requiredOrganizationStore.getFromEmailId();
											else
												    from = nonDynamicFrmEmail;
										}else if(requiredOrganizationStore.getFromEmailId() == null){
											//keep previous email id
										}
											
										String tempEmail;
										if(requiredOrganizationStore.getFromName() != null){
											
											if(digitalReceiptUserSettings.isIncludeDynamicFrmName()){
												
												
												if(from.contains("<")){
													tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
												}else{
													tempEmail = from;
												}
												from = requiredOrganizationStore.getFromName() +"<"+ tempEmail+">";
												
											}
											else{
												
												if(from.contains("<")){
													tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
												}else{
													tempEmail = from;
												}
												
												if(nonDynamicFrmName != null){
													from = nonDynamicFrmName +"<"+ tempEmail+">";
												}else{
													from = tempEmail;
												}
													
											}
										}else if(requiredOrganizationStore.getFromName() == null){
											
											if(nonDynamicFrmName != null){
												
												if(from.contains("<")){
													tempEmail = from.substring(from.indexOf("<")+1, from.indexOf(">"));
												}else{
													tempEmail = from;
												}
												
												from = nonDynamicFrmName + "<"+ tempEmail+">";
											}
											
										}
											
										/*if(requiredOrganizationStore.getReplyToEmailId() != null)
											replyTo = requiredOrganizationStore.getReplyToEmailId();
										*/
										if(requiredOrganizationStore.getReplyToEmailId() != null)
										{
											if(digitalReceiptUserSettings.isIncludeDynamicReplyToEmail()) {
												replyTo = requiredOrganizationStore.getReplyToEmailId();;
											}else
												replyTo = nonDynamicReplyToEmail;
										}
										
									}
								}catch (Exception e) {
									logger.info("Exception>>>>>>>>>>>>>>"+e);
								}	
								 
								
										
							}
							String drRequestJson = gson.toJson(drbody);
							JSONObject jsonBodyObj =  (JSONObject)JSONValue.parse(drRequestJson);
					//	Object[] obj1 = { htmlContent, jsonBodyObj, user };
						
						//	String docSid = (String)receiptObj.get(Constants.DR_DOCSID);
									
							
							String docSidStr =drbody.getReceipt().getDocSID();
							String docDate = drbody.getReceipt().getDocDate();
							String storeNumberStr = drbody.getReceipt().getStore();
							String sbs = drbody.getReceipt().getSubsidiaryNumber();
							htmlContentForSMS = htmlContent;
							logger.info("here is the in html logger : "+htmlContentForSMS);
							htmlContentForWA = htmlContent;
							
							String sendEreceipt = digitalReceipt.getHead().getEmailReceipt();
							   
						if(digitalReceiptUserSettings.isEnabled() && toEmailId != null && !toEmailId.isEmpty() &&
								sendEreceipt != null && !sendEreceipt.trim().isEmpty() && !sendEreceipt.equals("N")){
							DRSent drSent = null;
							boolean isSameDocSid = false;
							try {
								if(docSidStr != null ){
									drSent = drSentDao.findBy(user.getUserId(), docSidStr, toEmailId);
									if(drSent == null) {
										
										drSent = drSentDao.findBy(user.getUserId(), docSidStr);
										isSameDocSid = (drSent != null);
									}
								}
							} catch (Exception e) {
								logger.error("Exception ",e );
							}
							ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
							htmlContent = replacePlaceHoderClass.replaceRecommTags(htmlContent);
							Object[] obj1 = { htmlContent, jsonBodyObj, user, productId };

							Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj1, drSent);
							StringBuffer phKeyValStr = null;
							if(obj2[0] != null){ 
								
								htmlContent =(String)obj2[0];
								textContent = htmlContent;
								logger.info("My html to :\n"+ htmlContent);
							}
							if(obj2[1] != null){
								
								phKeyValStr= (StringBuffer) obj2[1];
							}
							logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
							String phValStr =null;
							if(phKeyValStr != null && phKeyValStr.length() > 0){
								
								 phValStr =phKeyValStr.toString();
							}
							
							boolean updateCount = (drSent != null && !isSameDocSid);		
							if((drSent == null && !isSameDocSid) ||(drSent != null && isSameDocSid)) {
								 
								/*drSent = new DRSent(Constants.DR_STATUS_SUCCESS, toEmailId, Calendar.getInstance(),
										user.getUserId(), drJsonObjId);*/
								drSent = new DRSent(Constants.DR_STATUS_SUBMITTED, toEmailId, Calendar.getInstance(),
										user.getUserId(), drJsonObjId);
								
								 drSent.setDocSid(docSidStr);
								 drSent.setSentCount(1);
							 }
							drSent.setSubject(subject);
							//PDF******************BEGIN**********
							/*StringBuffer sb = new StringBuffer();
							 String pdflinkUrl = DR_PDF_URL;
							 pdflinkUrl = pdflinkUrl.replace("|^", "[").replace("^|", "]");
							 String pdfUrl = DR_PDF_DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + pdflinkUrl + "'> click here</a>");
							 Document doc1 = Jsoup.parse(htmlContent);
							 Element bodyelements = doc1.select("body").first().prepend(pdfUrl);;
							 String bodyStr = bodyelements.toString();	
							 sb = new StringBuffer(bodyStr);
							 htmlContent = sb.toString();*/
							 drSent.setHtmlStr(htmlContent);
							 drSent.setPhValStr(phValStr);
							 drSent.setTemplateName(templateName);
							 if(storeNumberStr != null && !storeNumberStr.trim().isEmpty())drSent.setStoreNumber(storeNumberStr);
							 if(sbs != null && !sbs.trim().isEmpty())drSent.setSbsNumber(sbs);
							
							 /**new parameter in drsent table for the sack of drReports
							  * */
							 drSent.setMyTemplateId(digitalReceiptUserSettings!=null ? digitalReceiptUserSettings.getMyTemplateId() : null);
							 drSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
							 
							drSentDaoForDML.saveOrUpdate(drSent);
							if(updateCount) {
								
								try {
									//drSentDao.updateSentCount(drSent.getId());
									drSentDaoForDML.updateSentCount(drSent.getId());
								} catch (BaseDAOException e) {
								logger.error("Exception while updating the sent count", e);
								}
							}
							String sentId = null;
							try {
								sentId = EncryptDecryptUrlParameters.encrypt(drSent.getId()+Constants.STRING_NILL);
							} catch (Exception e) {
								logger.error("Exception ::::", e);
							}
						
							htmlContent = htmlContent.replace("[sentId]",sentId+"");
							if(displayTemp != null){
								String replacedDispTemplate = displayTemp.replace("\\n", "<br></br>");
								htmlContent = htmlContent.replace("[LoyaltyDisplayTemplate]", replacedDispTemplate+"");
							}
							/*if(lifeTimePoints != null){
								htmlContent = htmlContent.replace("[LifeTimePoints]", lifeTimePoints+"");
							
							}*/
							htmlContent = htmlContent.replace("[LifeTimePoints]", lifeTimePoints == null ? "" :lifeTimePoints);
							htmlContent = htmlContent.replace("[LoyaltyEarnedToday]", loyaltyEarnedToday == null ? "" :loyaltyEarnedToday);
							htmlContent = htmlContent.replace("[LoyaltyRedeemedToday]", redeemedPoints == null ? "" :redeemedPoints);
							//DigitalReceipt digitalReceipt = gson.fromJson(drRequestJson, DigitalReceipt.class);
							logger.debug("digitalReceipt.getbody=="+digitalReceipt.getBody());
							String redeemedAmount = getLoyaltyRedemption(digitalReceipt, user);
							htmlContent = htmlContent.replace("[LoyaltyRedeemedAmount]", redeemedAmount+"");
							textContent = textContent.replace("[sentId]", sentId+"");
							drSent.setHtmlStr(htmlContent);
							drSentDaoForDML.saveOrUpdate(drSent);
							//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
							 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
								try {

									if(user != null) {
										messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
									}
									logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
									ExternalSMTPDigiReceiptSender externalSMTPSender =  new ExternalSMTPDigiReceiptSender();
									/*htmlContent = htmlContent.replace("[sentId]", drSent.getId()+"");
									textContent = textContent.replace("[sentId]", drSent.getId()+"");*/
									//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
									externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, toEmailId, replyTo);
								
								} catch (Exception e) {
									
									logger.debug("Exception while sending through sendGridAPI .. returning ",e);
									
								}
							}
						}
							 if(user.isReceiptOnSMS() && digitalReceiptUserSettings.isSmsEnabled() && mobile != null && !mobile.isEmpty()	){
								 DRSMSSent drSmsSent = null;
								 boolean isSameDocSid = false;
								 try {
								
								//APP-4253
								List<SMSSuppressedContacts> SuprList =  smsSuppressedContactsDao.searchContactsById(user, mobile);
									if(!SuprList.isEmpty() && SuprList.size()==1) {
											
											logger.info("DRsms cannot be sent for Suppressed number");
											return;
										}
									 
								drSmsSent = drSmsSentDao.findBy(user.getUserId(), docSidStr, mobile);
								 if(drSmsSent == null) {
										drSmsSent = drSmsSentDao.findBy(user.getUserId(), docSidStr);
 										isSameDocSid = (drSmsSent != null);
     								}
								 
								 ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
									htmlContentForSMS = replacePlaceHoderClass.replaceRecommTags(htmlContentForSMS);

								 Object[] obj = { htmlContentForSMS, jsonBodyObj, user , productId };
								 
									Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj, null);
									StringBuffer phKeyValStr = null;
									if(obj2[0] != null){
										
										htmlContentForSMS =(String)obj2[0];
										textContent = htmlContentForSMS;
									}
									if(obj2[1] != null){
										
										phKeyValStr= (StringBuffer) obj2[1];
									}
									logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
									String phValStr =null;
									if(phKeyValStr != null && phKeyValStr.length() > 0){
										
										 phValStr =phKeyValStr.toString();
									}

								 boolean updateSmsCount = (drSmsSent != null && !isSameDocSid);		
	 							if((drSmsSent == null && !isSameDocSid) ||(drSmsSent != null && isSameDocSid)) {
								 								 
									drSmsSent = new DRSMSSent(Constants.DR_STATUS_SUBMITTED, mobile, Calendar.getInstance(),
		 										user.getUserId(), drJsonObjId);
								 								
									 drSmsSent.setDocSid(docSidStr);
									 drSmsSent.setSentCount(1);
								 }
	 							 drSmsSent.setHtmlStr(htmlContentForSMS);
	 							 drSmsSent.setStoreNumber(storeNumberStr);
	 							 drSmsSent.setSbsNumber(sbs);
	 						//	APP-3360 
	 							 drSmsSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
	 							 drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
	 							 logger.info("drSmsSent.getId() "+drSmsSent.getId());
	 							logger.info(htmlContentForSMS.contains("[sentId]"));
	 							String drSmsSentId = null;
	 							try {
	 								drSmsSentId = EncryptDecryptUrlParameters.encrypt(drSmsSent.getId()+Constants.STRING_NILL);
	 							} catch (Exception e) {
	 								logger.error("Exception ::::", e);
	 							}
	 							htmlContentForSMS = htmlContentForSMS.replace("[sentId]", drSmsSentId+"");
	 							if(updateSmsCount) {		
			 						try {
								 		drSmsSentDaoForDML.updateSentCount(drSmsSent.getId());
								 	} catch (BaseDAOException e) {
								 	logger.error("Exception while updating the sent count", e);
								 	}
	 							}
	 							htmlContentForSMS = htmlContentForSMS.replace("[sentId]", drSmsSentId+"");
								if(displayTemp != null){
									String replacedDispTemplate = displayTemp.replace("\\n", "<br></br>");
									htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyDisplayTemplate]", replacedDispTemplate+"");
								}
								/*if(lifeTimePoints != null){
									htmlContentForSMS = htmlContentForSMS.replace("[LifeTimePoints]", lifeTimePoints+"");
								
								}*/
								htmlContentForSMS = htmlContentForSMS.replace("[LifeTimePoints]", lifeTimePoints == null ? "" :lifeTimePoints);
								htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyEarnedToday]", loyaltyEarnedToday == null ? "" :loyaltyEarnedToday);
								htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyRedeemedToday]", redeemedPoints == null ? "" :redeemedPoints);
								//DigitalReceipt digitalReceipt = gson.fromJson(drRequestJson, DigitalReceipt.class);
								String redeemedAmount = getLoyaltyRedemption(digitalReceipt, user);
								htmlContentForSMS = htmlContentForSMS.replace("[LoyaltyRedeemedAmount]", redeemedAmount+"");
								textContent = textContent.replace("[sentId]", drSmsSentId+"");
								htmlContentForSMS = htmlContentForSMS.replace("requestedAction=pdf","requestedAction=pdfSMS");
								htmlContentForSMS = htmlContentForSMS.replace("requestedAction=click","requestedAction=clickSMS");
								htmlContentForSMS = htmlContentForSMS.replace("requestedAction=open","requestedAction=openSMS");
								
								//html minimizer
							//	htmlContentForSMS= minimizeHtml(htmlContentForSMS);
								
								drSmsSent.setHtmlStr(htmlContentForSMS);
								drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
								String receiptAmount = digitalReceipt.getBody().getReceipt().getTotal();
								receiptAmount = receiptAmount == null ? "" : receiptAmount;
								
								String CustomerName =  digitalReceipt.getBody().getReceipt().getBillToFName();
								CustomerName = CustomerName == null || CustomerName.isEmpty() ? "Valued Customer" : CustomerName;
								sendSMSFromDR(digitalReceiptUserSettings,drSmsSent, 
										receiptAmount, CustomerName, currencyBalance);
							 } catch (Exception e) {
									logger.error("Exception ",e );
								}
							 }
							 
							 //to remove dependancy of WA EReceipt with SMS
							 if(user.isReceiptOnWA() &&  mobile != null && !mobile.isEmpty()	){ //OPS-391
								 
								 logger.info("user.isReceiptOnWA() >>"+user.isReceiptOnWA() );
								 DRSMSChannelSent drSmsChannelSent = null;
								 boolean isSameDocSid = false;
								 try {
								 drSmsChannelSent = drSmsChannelSentDao.findBy(user.getUserId(), docSidStr, mobile);
								 if(drSmsChannelSent == null) {
										drSmsChannelSent = drSmsChannelSentDao.findBy(user.getUserId(), docSidStr);
 										isSameDocSid = (drSmsChannelSent != null);
     								}
								 
								 ReplacePlaceholderFromStrConetntOPT replacePlaceHoderClass = new ReplacePlaceholderFromStrConetntOPT();
									htmlContentForWA = replacePlaceHoderClass.replaceRecommTags(htmlContentForWA);

								 Object[] obj = { htmlContentForWA, jsonBodyObj, user,productId };
									Object[] obj2 = replacePlaceHoderClass.replacePlaceHolderFromStrContent(obj, null);
									StringBuffer phKeyValStr = null;
									if(obj2[0] != null){
										
										htmlContentForWA =(String)obj2[0];
										textContent = htmlContentForWA;
									}
									if(obj2[1] != null){
										
										phKeyValStr= (StringBuffer) obj2[1];
									}
									logger.debug("phKeyValStr >>>>>>> ::: "+phKeyValStr);
									String phValStr =null;
									if(phKeyValStr != null && phKeyValStr.length() > 0){
										
										 phValStr =phKeyValStr.toString();
									}

								 boolean updateSmsCount = (drSmsChannelSent != null && !isSameDocSid);		
	 							if((drSmsChannelSent == null && !isSameDocSid) ||(drSmsChannelSent != null && isSameDocSid)) {
								 								 
									drSmsChannelSent = new DRSMSChannelSent(Constants.DR_STATUS_SUBMITTED, mobile, Calendar.getInstance(),
		 										user.getUserId(), drJsonObjId);
								 								
									 drSmsChannelSent.setDocSid(docSidStr);
									 drSmsChannelSent.setSentCount(1);
								 }
	 							 drSmsChannelSent.setHtmlContent(htmlContentForWA);
	 							 drSmsChannelSent.setStoreNumber(storeNumberStr);
	 							 drSmsChannelSent.setSbsNumber(sbs); 
	 							 drSmsChannelSent.setZoneId(digitalReceiptUserSettings!=null ? (digitalReceiptUserSettings.getZoneId()!=null ? digitalReceiptUserSettings.getZoneId() : null) : null);
	 							 drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
	 							 logger.info("drSmsSent.getId() "+drSmsChannelSent.getId());
	 							logger.info(htmlContentForWA.contains("[sentId]"));
	 							String drSmsChannelSentId = null;
	 							try {
	 								drSmsChannelSentId = EncryptDecryptUrlParameters.encrypt(drSmsChannelSent.getId()+Constants.STRING_NILL);
	 							} catch (Exception e) {
	 								logger.error("Exception ::::", e);
	 							}
	 							htmlContentForWA = htmlContentForWA.replace("[sentId]", drSmsChannelSentId+"");
	 							if(updateSmsCount) {		
			 						try {
								 		drSmsChannelSentDaoForDML.updateSentCount(drSmsChannelSent.getId());
								 	} catch (BaseDAOException e) {
								 	logger.error("Exception while updating the sent count", e);
								 	}
	 							}
	 							htmlContentForWA = htmlContentForWA.replace("[sentId]", drSmsChannelSentId+"");
								if(displayTemp != null){
									String replacedDispTemplate = displayTemp.replace("\\n", "<br></br>");
									htmlContentForWA = htmlContentForWA.replace("[LoyaltyDisplayTemplate]", replacedDispTemplate+"");
								}
								htmlContentForWA = htmlContentForWA.replace("[LifeTimePoints]", lifeTimePoints == null ? "" :lifeTimePoints);
								htmlContentForWA = htmlContentForWA.replace("[LoyaltyEarnedToday]", loyaltyEarnedToday == null ? "" :loyaltyEarnedToday);
								htmlContentForWA = htmlContentForWA.replace("[LoyaltyRedeemedToday]", redeemedPoints == null ? "" :redeemedPoints);							
								String redeemedAmount = getLoyaltyRedemption(digitalReceipt, user);
								htmlContentForWA = htmlContentForWA.replace("[LoyaltyRedeemedAmount]", redeemedAmount+"");
								textContent = textContent.replace("[sentId]", drSmsChannelSentId+"");
								htmlContentForWA = htmlContentForWA.replace("requestedAction=pdf","requestedAction=pdfWA");
								htmlContentForWA = htmlContentForWA.replace("requestedAction=click","requestedAction=clickWA");
								htmlContentForWA = htmlContentForWA.replace("requestedAction=open","requestedAction=openWA");
								drSmsChannelSent.setHtmlContent(htmlContentForWA);
								drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
//								String receiptAmount = digitalReceipt.getBody().getReceipt().getTotal();
//								receiptAmount = receiptAmount == null ? "" : receiptAmount;
//								
//								String CustomerName =  digitalReceipt.getBody().getReceipt().getBillToFName();
//								CustomerName = CustomerName == null || CustomerName.isEmpty() ? "Valued Customer" : CustomerName;

								sendWAFromDR(drSmsChannelSent);

								
								 } catch (Exception e) {
									logger.error("Exception ",e );
								}
							 
								 
							 }

			} catch (BaseServiceException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
							
				
		}
		
		
		private String GetMergedPaymentPart(String paymentPart, String number, String type, double Amount, 
				String auth, String BaseTaken, double taken, String currencyName, String tenderTypainArb )
	    {logger.debug("number ===="+number+" type ==="+type);
	    
	        String paymentPartStr = paymentPart.replace("#Payment.Amount#",  BigDecimal.valueOf(Amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL ).replace("#Payment.Taken#",  BigDecimal.valueOf(taken).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL )
	        		.replace("#Payment.Amount_DEC3#",  BigDecimal.valueOf(Amount).setScale(3, RoundingMode.HALF_UP)+Constants.STRING_NILL ).replace("#Payment.Taken_DEC3#",  BigDecimal.valueOf(taken).setScale(3, RoundingMode.HALF_UP)+Constants.STRING_NILL )
	        		.replace("#Payment.Number#", number!= null && !number.isEmpty() ? ("No: "+number) :Constants.STRING_NILL)
	                .replace("#Payment.Type#", type)
	                .replace("#Payment.Auth#", auth != null && !auth.isEmpty() ? ("Auth: "+auth) : Constants.STRING_NILL)
	                .replace("#Payment.BaseTaken#", BaseTaken!= null && !BaseTaken.isEmpty() ? ("Base Taken "+BaseTaken) : Constants.STRING_NILL)
	        		//.replace("#Payment.Taken#", taken)
	        		.replace("#Payment.CurrencyName#", currencyName)
	        		.replace("#Payment.TypeInLangArb#", tenderTypainArb != null && !tenderTypainArb.isEmpty()? tenderTypainArb : Constants.STRING_NILL)
	        		.replace("#Payment.CardTypeInLangArb#",Constants.STRING_NILL)
	        		.replace("[TENDER_ROW1_DISPLAYSTYLE]", (number!= null && !number.isEmpty()) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;")
	        		.replace("[TENDER_ROW2_DISPLAYSTYLE]", ( auth != null && !auth.isEmpty() ) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;")
	        		.replace("[TENDER_ROW3_DISPLAYSTYLE]", (BaseTaken!= null && !BaseTaken.isEmpty()) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
	        
	        
	        return paymentPartStr;
	    }
		
		
		
		private String GetMergedPaymentPart(String paymentPart, String number, String type, double Amount, 
				String auth, String BaseTaken, double taken, String currencyName, String tenderTypainArb, String cardTypeinArb )
	    {
			
	        String paymentPartStr = paymentPart.replace("#Payment.Amount#",  BigDecimal.valueOf(Amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL ).replace("#Payment.Taken#",  BigDecimal.valueOf(taken).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL )
	        		.replace("#Payment.Amount_DEC3#",  BigDecimal.valueOf(Amount).setScale(3, RoundingMode.HALF_UP)+Constants.STRING_NILL ).replace("#Payment.Taken_DEC3#",  BigDecimal.valueOf(taken).setScale(3, RoundingMode.HALF_UP)+Constants.STRING_NILL )
	        		.replace("#Payment.Number#", number!= null && !number.isEmpty() ? ("No: "+number) :Constants.STRING_NILL)
	                .replace("#Payment.Type#", type)
	                .replace("#Payment.Auth#", auth != null && !auth.isEmpty() ? ("Auth: "+auth) : Constants.STRING_NILL)
	                .replace("#Payment.BaseTaken#", BaseTaken!= null && !BaseTaken.isEmpty() ? ("Base Taken "+BaseTaken) : Constants.STRING_NILL)
	        		//.replace("#Payment.Taken#", taken)
	        		.replace("#Payment.CurrencyName#", currencyName)
	        		.replace("#Payment.TypeInLang#", tenderTypainArb != null && !tenderTypainArb.isEmpty()? tenderTypainArb : Constants.STRING_NILL)
	        		.replace("#Payment.CardTypeInLangArb#", cardTypeinArb != null && !cardTypeinArb.isEmpty()? cardTypeinArb : Constants.STRING_NILL)
	        		.replace("[TENDER_ROW1_DISPLAYSTYLE]", (number!= null && !number.isEmpty()) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;")
	        		.replace("[TENDER_ROW2_DISPLAYSTYLE]", ( auth != null && !auth.isEmpty() ) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;")
	        		.replace("[TENDER_ROW3_DISPLAYSTYLE]", (BaseTaken!= null && !BaseTaken.isEmpty()) ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
	        
	        return paymentPartStr;
	    }
		
		private String GetMergedPaymentChangePart(String changePart, String type, double Amount, String tenderTypainArb )
	    {
	        String paymentPartStr = changePart.replace("#Change.Type#", type).replace("#Change.Given_DEC3#", BigDecimal.valueOf(Amount).setScale(3, RoundingMode.HALF_UP)+Constants.STRING_NILL )
	                .replace("#Change.Given#", BigDecimal.valueOf(Amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL).replace("#Change.TypeInLangArb#",tenderTypainArb != null && !tenderTypainArb.isEmpty()? tenderTypainArb : Constants.STRING_NILL);
	        return paymentPartStr;
	    }
		
		private String GetMergedRefPart(String refParts, String jsonkey,String jsonvalarr )
	    {
	    	   
	    	   refParts.replace(jsonkey, jsonvalarr);
	                //.replace("#Item.RefReceipt#", RefReceipt).replace("#Item.RefSubsidiaryNumber#",RefSubsidiaryNumber);
	        return refParts;
	    }
		private String replaceItemLoopBlock(String templateContentStr, String[] itemsSetPlaceHoldersArr, Set<String> DRItemHashTagsSet, 
				 String[] paymentSetPlaceHoldersArr, String[] paymentChnagesSetPlaceHoldersArr,	String[] DRRefItemLoopPlaceHolderArr,
				 PrismDRBody prismDRBody){
			
			if(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) + itemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]));
				
				String loopBlock2 = Constants.STRING_NILL;
				if(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) != -1 && templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]) != -1) {
					loopBlock2 = templateContentStr.substring(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) + DRRefItemLoopPlaceHolderArr[0].length(),
							templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]));
					
				}
				//logger.debug("Items HTML is :"+ loopBlockOne);
				String loopBlock = loopBlockOne;
				
				String itemloop = getReplacedItemBlock(loopBlock, loopBlock2, DRItemHashTagsSet, prismDRBody);
				
				templateContentStr = templateContentStr.replace(loopBlockOne, itemloop);
			}
			return 	templateContentStr;
			
		}
		public List<PrismDRItem> getSortedListForPrismItems( List<PrismDRItem> prismItems){
			
			List<PrismDRItem> list = new ArrayList<PrismDRItem>();
			list.addAll(prismItems);
            /*for (int i = 0; i < prismItems.size(); i++) {
                    list.add((JSONObject)prismItems.get(i));
            }*/
            Collections.sort(list, new SortPrismItemBasedOnItemLine());

            List<PrismDRItem> Sortedlist = new ArrayList<PrismDRItem>();
			Sortedlist.addAll(list);
            return Sortedlist;
			
			
		}
			private String getReplacedItemBlock(String itemLoopBlock, String itemRefLoopBlock, Set<String> DRItemHashTagsSet, PrismDRBody prismDRBody){
				
				List<PrismDRItem> iterateItems = prismDRBody.getItems();
				List<PrismDRItem> items = new ArrayList<PrismDRItem>();
				//items.addAll(iterateItems);
				//keep the items in an order
				String finalHtmlBlockVal = Constants.STRING_NILL;
				/*for (PrismDRItem prismDRItem : iterateItems) {
					int index = 0;
					index = Integer.parseInt(prismDRItem.getItem_pos());
					for (int j = 0; j < iterateItems.size(); j++) {
						
						
						//jsonArr = new JSONArray()[itemsArr.size()];
						if((index-1) != j) continue; 
						items.set(j,prismDRItem);
						break;
						
					}
					
				}*/
				items = getSortedListForPrismItems(iterateItems);
				if(items == null){
					items = iterateItems;
				}
				String so_number = Constants.STRING_NILL;
				//items.addAll(iterateItems);
				//keep the items in an order
				/*for (PrismDRItem prismDRItem : iterateItems) {
					int index = 0;
					index = Integer.parseInt(prismDRItem.getItem_pos());
					for (int j = 0; j < iterateItems.size(); j++) {
						
						
						//jsonArr = new JSONArray()[itemsArr.size()];
						if((index-1) != j) continue; 
						items.set(j,prismDRItem);
						break;
						
					}
					
				}*/
				
				
				for (PrismDRItem prismDRItem : items) {//for each item prepare the block along with ref section and replace the merge tags using PrismDREnum
					String tempStr = itemLoopBlock;
					if(prismDRItem.getSo_number() != null && !prismDRItem.getSo_number().isEmpty()){
						
						if(!so_number.isEmpty())so_number += ",";
						so_number += prismDRItem.getSo_number();
					}
					this.prismDRBody.setSo_number(so_number);
					double vatPerc = 0.0;
					double vatAmt = 0.0;
					double price = 0.0;
					int qty = 0;
					double extPrice = 0.0;
					double discAmtDbl = 0.0;
					double itemCalDisc = 0.0;
					
					String vatPrc = prismDRItem.getTax_percent();
					String Qty = prismDRItem.getQuantity();
					String discAmt = prismDRItem.getDiscount_amt();
					String Price = prismDRItem.getPrice();
					
					price = Price.isEmpty() ? 0.0 : Double.parseDouble(Price);
					vatPerc = vatPrc.isEmpty() ? 0.0 : Double.parseDouble(vatPrc);
					qty = Qty.isEmpty() ? 0 : Integer.parseInt(Qty.contains(".") ? Qty.substring(0, Qty.indexOf("."))  : Qty);
					discAmtDbl = discAmt.isEmpty() ? 0.0 : Double.parseDouble(discAmt);
					itemCalDisc = discAmtDbl*qty;
					
					vatAmt = ((vatPerc*price)/100)*qty;
					extPrice = ( (price*qty)+vatAmt);
					
					
					prismDRItem.setNetPrice(extPrice + Constants.STRING_NILL);
					
					receiptDisc = receiptDisc+itemCalDisc;
					
					boolean isRef = (prismDRItem.getRef_order_doc_sid() != null && 
							!prismDRItem.getRef_order_doc_sid().isEmpty() && 
							!prismDRItem.getRef_order_doc_sid().equals("0")) ||
							(prismDRItem.getOrig_document_number() != null && 
									!prismDRItem.getOrig_document_number().isEmpty() &&
									!prismDRItem.getOrig_document_number().equals("0"));
							
					
					for (String hashTag : DRItemHashTagsSet) {//for each HashTag findout the prismEnum
						
						PrismDREnum prismDREnum = PrismDREnum.getEnumsByHashTag(hashTag); 
						if(prismDREnum == null) continue;
						
						logger.debug("hashTag ==="+hashTag+" element ==="+prismDREnum.getElementName());
						try {
							PropertyDescriptor pd = new PropertyDescriptor(prismDREnum.getElementName(), prismDRItem.getClass());
							  Object retValue =  pd.getReadMethod().invoke(prismDRItem);
							  String replaceVal = Constants.STRING_NILL;
							  if(retValue == null ){
								  logger.debug("retValue is Null " );
								  continue;
							  }
							  
							  replaceVal = retValue.toString().trim();
							  logger.debug("replaceVal is==== "+replaceVal );
							  if(prismDREnum.name().equals("quantity")) {
									 
								   //APP-3935
									if (replaceVal==null || replaceVal.isEmpty() ) {
										replaceVal="0";
									}

									double newQuantity=Double.parseDouble(replaceVal);
									DecimalFormat df= new DecimalFormat("#.####");
									replaceVal=df.format(newQuantity);
									
								}else if(prismDREnum.name().equals("note1") || prismDREnum.name().equals("note2") || prismDREnum.name().equals("note3") ){
									
									if(!replaceVal.isEmpty() && 
											replaceVal.equalsIgnoreCase("HOME")) {
										
										replaceVal = replaceVal + " Delivery Item";
									}
								}
							  
									
								if(prismDREnum.isNumber()){
									Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
									if(prismDREnum.isDecimal()){
										
										replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL;
									}else{
										replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
									}
									
								}
							  
							  tempStr = tempStr.replace(hashTag, replaceVal);
							  
						} catch (IllegalAccessException e) {
							logger.error("Exception ", e);
						} catch (IllegalArgumentException e) {
							logger.error("Exception ", e);
						} catch (InvocationTargetException e) {
							logger.error("Exception ", e);
						} catch (IntrospectionException e) {
							logger.error("Exception ", e);
						}
						
					}//for
					tempStr = tempStr.replace("[REFSECTIONSTYLE]", isRef ?  Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
				}		
				
				return finalHtmlBlockVal;
				
			}
			
			private String replaceGSTLoopBlock(String GSTLoopBlock, String loopBlock,
					DRBody drbody, String templateContentStr) {
				String finalHtmlBlockVal = Constants.STRING_NILL;
				try {
					String DRGSTItemHashTags = PropertyUtil.getPropertyValueFromDB("DRGSTItemHashTags"); 
					Set<String> DRGSTItemHashTagsSet = getUserJsonSettingSet(DRGSTItemHashTags);
					if(drbody.getItems() == null || drbody.getItems().size() <=0 ) {
						return null;
					}
					logger.debug("in replaceGSTLoopBlock ==");
					
					List<DRItem> items = drbody.getItems();

					
					Map<String, GSTLine> GSTLinesMap = new HashMap<String, GSTLine>();
					for(DRItem DRItem : items) {
						// Loop all array elements
						String taxDesc = DRItem.getTaxDescription();
						if(DRItem.getTaxDescription() == null ||DRItem.getTaxDescription().isEmpty() ) continue;
							
							GSTLine GSTLine = GSTLinesMap.get(taxDesc);
							if(GSTLine == null){
								
								GSTLine = new GSTLine(DRItem.getTaxableAmt(), DRItem.getTaxDescription(), 
										DRItem.getIGSTRate(), DRItem.getIGSTAmt(), DRItem.getCGSTRate(), 
										DRItem.getCGSTAmt(), DRItem.getSGSTRate(), DRItem.getSGSTAmt(), 
										DRItem.getCESSRate(), DRItem.getCESSAmt());
							}else{
								
								GSTLine.setTaxableAmt(Double.parseDouble(GSTLine.getTaxableAmt())+Double.parseDouble(DRItem.getTaxableAmt())+"");
								GSTLine.setIGSTAmt(Double.parseDouble(GSTLine.getIGSTAmt())+Double.parseDouble(DRItem.getIGSTAmt())+"");
								GSTLine.setCGSTAmt(Double.parseDouble(GSTLine.getCGSTAmt())+Double.parseDouble(DRItem.getCGSTAmt())+"");
								GSTLine.setSGSTAmt(Double.parseDouble(GSTLine.getSGSTAmt())+Double.parseDouble(DRItem.getSGSTAmt())+"");
								GSTLine.setCESSAmt(Double.parseDouble(GSTLine.getCESSAmt())+Double.parseDouble(DRItem.getCESSAmt())+"");
							}
							 GSTLinesMap.put(taxDesc, GSTLine);
							 
						
					}
					logger.debug("GSTLinesMap =="+GSTLinesMap.size());
					//if(GSTLinesMap.size() > 0){
						for (String GST : GSTLinesMap.keySet()) {
							String tempStr = GSTLoopBlock;
							
							GSTLine GSTLine = GSTLinesMap.get(GST);
							
							
							for (String hashTag : DRGSTItemHashTagsSet) {
								try{
									String OptDRJsonEleName = null;
									OptDRJsonEleName = hashTag.replace("#", "").split("\\.")[1];
									logger.debug("hashTag ==="+hashTag+" element ==="+OptDRJsonEleName);
									if(OptDRJsonEleName == null) continue;
									PropertyDescriptor pd = new PropertyDescriptor(OptDRJsonEleName, GSTLine.getClass());
									  Object retValue =  pd.getReadMethod().invoke(GSTLine);
									  String replaceVal = Constants.STRING_NILL;
									  if(retValue == null ){
										  logger.debug("retValue is Null " );
										  retValue ="";
									  }
									  
									  replaceVal = retValue.toString().trim();
									  if(!hashTag.contains("#GST.TaxDescription#")) {
										  
										  Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
										  replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
									  }
									  tempStr = tempStr.replace(hashTag, replaceVal);
									
								}catch (Exception e) {
									logger.error("Exeption ", e);
								}
							}
							
							
							
							finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
						}
						templateContentStr = templateContentStr.replace(loopBlock, finalHtmlBlockVal);
						String DRTotalGSTHashTags = PropertyUtil.getPropertyValueFromDB("DRTotalGSTHashTags"); 
						Set<String> DRTotalGSTHashTagsSet = getUserJsonSettingSet(DRTotalGSTHashTags);
						
						GSTLine GSTLine = new GSTLine();
						
						for (String GST : GSTLinesMap.keySet()) {
							GSTLine GSTEachLine = GSTLinesMap.get(GST);
							GSTLine.setTotalTaxableAmt(GSTLine.getTotalTaxableAmt() == null || GSTLine.getTotalTaxableAmt().isEmpty() ?
									GSTEachLine.getTaxableAmt() : Double.parseDouble(GSTLine.getTotalTaxableAmt())+Double.parseDouble(GSTEachLine.getTaxableAmt())+"");
							
							GSTLine.setTotalCESSAmt(GSTLine.getTotalCESSAmt() == null || GSTLine.getTotalCESSAmt().isEmpty() ?
									GSTEachLine.getCESSAmt() : Double.parseDouble(GSTLine.getTotalCESSAmt())+Double.parseDouble(GSTEachLine.getCESSAmt())+"");
							GSTLine.setTotalCGSTAmt(GSTLine.getTotalCGSTAmt() == null || GSTLine.getTotalCGSTAmt().isEmpty() ?
									GSTEachLine.getCGSTAmt() : Double.parseDouble(GSTLine.getTotalCGSTAmt())+Double.parseDouble(GSTEachLine.getCGSTAmt())+"");
					
							GSTLine.setTotalIGSTAmt(GSTLine.getTotalIGSTAmt() == null || GSTLine.getTotalIGSTAmt().isEmpty() ?
									GSTEachLine.getIGSTAmt() : Double.parseDouble(GSTLine.getTotalIGSTAmt())+Double.parseDouble(GSTEachLine.getIGSTAmt())+"");
					
							GSTLine.setTotalSGSTAmt(GSTLine.getTotalSGSTAmt() == null || GSTLine.getTotalSGSTAmt().isEmpty() ?
									GSTEachLine.getSGSTAmt() : Double.parseDouble(GSTLine.getTotalSGSTAmt())+Double.parseDouble(GSTEachLine.getSGSTAmt())+"");
					
						}
						for (String hashTag : DRTotalGSTHashTagsSet) {
							try{
								String OptDRJsonEleName = null;
								OptDRJsonEleName = hashTag.replace("#", "").split("\\.")[1];
								logger.debug("hashTag ==="+hashTag+" element ==="+OptDRJsonEleName);
								if(OptDRJsonEleName == null) continue;
								PropertyDescriptor pd = new PropertyDescriptor(OptDRJsonEleName, GSTLine.getClass());
								  Object retValue =  pd.getReadMethod().invoke(GSTLine);
								  String replaceVal = Constants.STRING_NILL;
								  if(retValue == null ){
									  logger.debug("retValue is Null " );
									  retValue = "";
								  }
								  
								  replaceVal = retValue.toString().trim();
								  Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
								  replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
								  templateContentStr  = templateContentStr.replace(hashTag, replaceVal);
								
							}catch (Exception e) {
								logger.error("Exeption ", e);
							}
						}
						
					//}
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					logger.error("Exeption ", e);
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error("Exeption ", e);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exeption ", e);
				}
				return templateContentStr;
			}
			// OPTDR rec tag
			String productId = Constants.STRING_NILL;
			private String replaceItemLoopBlock(String loopBlock,String loopBlock2, 
				Map<String,String> userJSONSettingHM,DRBody drbody) {
			String finalHtmlBlockVal = Constants.STRING_NILL;
				try {
					String DRItemHashTags = PropertyUtil.getPropertyValueFromDB("DRItemHashTags"); 
					//String DRReceiptHashTags = PropertyUtil.getPropertyValueFromDB("DRReceiptHashTags"); 
					//String DRTenderHashTags = PropertyUtil.getPropertyValueFromDB("DRTenderHashTags"); 
					
						Set<String> DRItemHashTagsSet = getUserJsonSettingSet(DRItemHashTags); 
						//Set<String> DRReceiptHashTagsSet = getUserJsonSettingSet(DRReceiptHashTags); 
						//Set<String> DRTenderHashTagsSet = getUserJsonSettingSet(DRTenderHashTags); 
					
					double invcItemDisc =0.0;
					double docItemOrigDisc = 0.0;
					double itemdiscperc = 0.0;
					double calcItemDisc = 0.0;//(origprice-invcprice)
					double vatPerc = 0.0;
					double vatAmt = 0.0;
					double price = 0.0;
					int qty = 0;
					double itemlevelcalcDisc = 0.0;//(calcItemDic*qty);
					double extPrice = 0.0;
					double origPriceDbl =0.0;
					
					if(drbody.getItems() == null || drbody.getItems().size() <=0 ) {
						return null;
					}
					
					
					/*List<PrismDRItem> iterateItems = prismDRBody.getItems();
					List<PrismDRItem> items = new ArrayList<PrismDRItem>();
					items.addAll(iterateItems);
					//keep the items in an order
					String finalHtmlBlockVal = Constants.STRING_NILL;
					for (PrismDRItem prismDRItem : iterateItems) {
						int index = 0;
						index = Integer.parseInt(prismDRItem.getItem_pos());
						for (int j = 0; j < iterateItems.size(); j++) {
							
							
							//jsonArr = new JSONArray()[itemsArr.size()];
							if((index-1) != j) continue; 
							items.set(j,prismDRItem);
							break;
							
						}
						
					}
					*/
					
					
					List<DRItem> items = drbody.getItems();

					
					List<DRItem> orderedItemSet = new ArrayList<DRItem>();
					orderedItemSet.addAll(items);
					for(DRItem DRItem : items)   // Loop all array elements
					{
						int index = 0;
						if(DRItem.getItemLine() != null && !DRItem.getItemLine().trim().isEmpty()){
							index =  Integer.parseInt(DRItem.getItemLine().trim());
						}
						for (int j = 0; j < items.size(); j++) {
							
							
							//jsonArr = new JSONArray()[itemsArr.size()];
							if((index-1) != j) continue; 
							orderedItemSet.set(j, DRItem);
							break;
							
						}
						
					}
					//for each item prepare the block along with ref section and replace the merge tags using PrismDREnum
					String itemNoteUsed = user.getItemNoteUsed();
					
					for(DRItem DRItem : orderedItemSet) {  // Loop all array elements
						
						String itemNoteObjStr=Constants.STRING_NILL;
						
						if(itemNoteUsed!=null) {
							PropertyDescriptor pdi = new PropertyDescriptor(itemNoteUsed, DRItem.getClass());
							Object itemNoteObj = pdi.getReadMethod().invoke(DRItem);
							if(itemNoteObj != null && !itemNoteObj.toString().isEmpty() ) {
								itemNoteObjStr = itemNoteObj.toString();
							}
						}
						String tempStr = loopBlock;
						String tempStr2 = loopBlock2;
						String DCBlock = "";
						String[] promoInfosArr =null;
						try {
							if(!itemNoteObjStr.isEmpty()){
								if(  itemNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
									Double 	totalDiscAmount = 0.0;
									promoInfosArr = itemNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
									for (String promoInfo : promoInfosArr) {
										String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
										//String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
										totalDiscAmount += (Double.parseDouble(promoInfoArr[1]))*(Double.parseDouble(promoInfoArr[2]));
									}
									tempStr = tempStr.replace("#Item.DiscountCode#", "DISCOUNTS").replace("#Item.DiscountOnCode#", totalDiscAmount+"");
								}else {
									String[] promoInfoArr = itemNoteObjStr.split(""+Constants.DELIMETER_COLON);
									//String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
									Double totalDiscAmount = Double.parseDouble(promoInfoArr[1])*Double.parseDouble(promoInfoArr[2]);
									tempStr = tempStr.replace("#Item.DiscountCode#", promoInfoArr[0]).replace("#Item.DiscountOnCode#", totalDiscAmount+"");
								}
							}else{
								tempStr = tempStr.replace("#Item.DiscountCode#", "").replace("#Item.DiscountOnCode#", "");
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							
						}
						String paymentParts = Constants.STRING_NILL;
						
						
						String Price = DRItem.getDocItemPrc();//jsonObj.get("DocItemPrc").toString();
						String origPrice = DRItem.getDocItemOrigPrc();//jsonObj.get("DocItemOrigPrc").toString() ;
						if(origPrice.isEmpty()) origPrice = DRItem.getInvcItemPrc();//jsonObj.get("InvcItemPrc").toString() ;
						String vatPrc = DRItem.getTaxPrc();//jsonObj.get("TaxPrc").toString();
						String Qty = DRItem.getQty();//jsonObj.get("Qty").toString();
					// reco tags for stylun . given the tag in the udf 6 :
						if(productId.length() > 0)
						productId+=Constants.DELIMETER_COMMA;
						productId+="'"+DRItem.getItemSID()+"'";//.getUDF6(); // i am using this product ID for getting recos.
						price = Price.isEmpty() ? 0.0 : Double.parseDouble(Price);
						origPriceDbl = origPrice.isEmpty() ? 0.0 : Double.parseDouble(origPrice);
						vatPerc = vatPrc.isEmpty() ? 0.0 : Double.parseDouble(vatPrc);
						qty = Qty.isEmpty() ? 0 : Integer.parseInt(Qty.contains(".") ? Qty.substring(0, Qty.indexOf("."))  : Qty);
						calcItemDisc = (origPriceDbl - price); 
						itemdiscperc = ((calcItemDisc)/origPriceDbl)*100;
						
						vatAmt = ((vatPerc*price)/100)*qty;
						extPrice = ( (price*qty)+vatAmt);
						
						itemlevelcalcDisc = calcItemDisc*qty;
						//docItemOrigDisc =(( vatPerc*itemlevelcalcDisc)/100)+itemlevelcalcDisc;
						docItemOrigDisc =itemlevelcalcDisc;
						totalVAT = totalVAT+vatAmt;
						totalAfterVAT = totalAfterVAT+extPrice;
						receiptDisc = receiptDisc+docItemOrigDisc;//set discount,extprice to item object  
						totalQty = totalQty+qty;
						
						
						boolean isRef = (DRItem.getRefDocSID() != null && 
								!DRItem.getRefDocSID().isEmpty() && 
								!DRItem.getRefDocSID().equals("0")) ||
								(DRItem.getRefReceipt() != null && 
										!DRItem.getRefReceipt().isEmpty() &&
										!DRItem.getRefReceipt().equals("0"));
						for (String hashTag : DRItemHashTagsSet) {
							try{
								String OptDRJsonEleName = null;
							PrismDREnum prismDREnum = PrismDREnum.getEnumsByHashTag(hashTag); 
							if(prismDREnum == null) { //avoiding the code dependency on new hashtags
								
								OptDRJsonEleName = hashTag.replace("#", "").split("\\.")[1];
							}else{
								
								OptDRJsonEleName = prismDREnum.getOptDRJsonEleName();
							}
							if(OptDRJsonEleName == null) continue;
						//	logger.debug("hashTag ==="+hashTag+" element ==="+OptDRJsonEleName); - APP-4568
							PropertyDescriptor pd = new PropertyDescriptor(OptDRJsonEleName, DRItem.getClass());
							  Object retValue =  pd.getReadMethod().invoke(DRItem);
							  String replaceVal = Constants.STRING_NILL;
							  if(retValue == null ){
							//	  logger.debug("retValue is Null " ); APP-4568
								  continue;
							  }
							  
							  replaceVal = retValue.toString().trim();
							  logger.debug("hashTag ==="+hashTag+" element ==="+OptDRJsonEleName+":: replaceVal is==== "+replaceVal );  //APP-4568
							  if( (prismDREnum != null && prismDREnum.name().equals("quantity"))) {
									
								    //APP-3935
									if (replaceVal==null || replaceVal.isEmpty() ) {
										replaceVal="0";
									}
									
									double newQuantity=Double.parseDouble(replaceVal);
									DecimalFormat df= new DecimalFormat("#.####");
									replaceVal=df.format(newQuantity);
									
								}else if(prismDREnum != null && (prismDREnum.name().equals("note1") || prismDREnum.name().equals("note2") || prismDREnum.name().equals("note3") )){
									
									if(!replaceVal.isEmpty() && 
											replaceVal.equalsIgnoreCase("HOME")) {
										
										replaceVal = replaceVal + " Delivery Item";
									}
								}
							
							
							  if(prismDREnum != null && prismDREnum.isNumber()){
									Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
									if(prismDREnum.isDecimal()){
										
										replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL;
									}else{
										replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
									}
									
								}
									
							  
							  tempStr = tempStr.replace(hashTag, replaceVal);
							  
						} catch (IllegalAccessException e) {
							logger.error("Exception ", e);
						} catch (IllegalArgumentException e) {
							logger.error("Exception ", e);
						} catch (InvocationTargetException e) {
							logger.error("Exception ", e);
						} catch (IntrospectionException e) {
							logger.error("Exception ", e);
						}
						
						
						
					}//for
					tempStr = tempStr.replace("[REFSECTIONSTYLE]", isRef ?  Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					
					
					finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
					
					//logger.info("final block to replace is "+ finalHtmlBlockVal);


				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			} catch (BaseServiceException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::", e);
			}catch(Exception e) {
				logger.error("Exception ::", e);
			}
				if((drbody.getReceipt().getDiscount() == null || drbody.getReceipt().getDiscount().isEmpty()) && receiptDisc>0 ) {
					drbody.getReceipt().setDiscount(receiptDisc+"");
				}
			return finalHtmlBlockVal;
				
		}
		private String replaceExisting_ExtPrcWithTax_IfAny(JSONObject jsonMainObj, String templateContentStr){
			try{
				if(templateContentStr == null){
					return templateContentStr;
				}
				
				JSONArray jsonArr = null;

				jsonArr  = (JSONArray)jsonMainObj.get("Items");

				if(jsonArr == null){
					return templateContentStr;
				}

				String extPrcWithTax_ExtPrc_JsonKey = Constants.STRING_NILL;
				String extPrcWithTax_Tax_JsonKey = Constants.STRING_NILL;

				for(int i = 0; i < jsonArr.size(); i++)
				{
					JSONObject jsonObj = (JSONObject)jsonArr.get(i);

					if(templateContentStr.contains("#Item.extPrcWithTax#")){

						extPrcWithTax_ExtPrc_JsonKey = "Items.ExtPrc";

						extPrcWithTax_Tax_JsonKey = "Items.Tax";

						String[] extPrcWithTax_ExtPrc_JsonKeyValArray = extPrcWithTax_ExtPrc_JsonKey.split(Pattern.quote("."));

						String[] extPrcWithTax_Tax_JsonKeyValArray = extPrcWithTax_Tax_JsonKey.split(Pattern.quote("."));


						Double valueExtPrcFromJson = (Double) (jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString().isEmpty()  ? 
								Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString()));
						Double valueTaxFromJson = (Double) (jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString().isEmpty()  ? 
								Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString()));

						Double sumOfExtPrcAndTax = (valueExtPrcFromJson + valueTaxFromJson); 
						sumOfExtPrcAndTax = Math.round(sumOfExtPrcAndTax*100.0)/100.0;
						logger.debug("sumOfExtPrcAndTax >>>>>>>>> "+sumOfExtPrcAndTax);

						DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
						templateContentStr = templateContentStr.replace("#Item.extPrcWithTax#", decimalFormat.format(sumOfExtPrcAndTax));

					}

				}
				
			}catch(Exception ex){
				logger.error("Exception >>> ",ex);
				return templateContentStr;
			}
			return templateContentStr;
					
		}
		
		private Map<String, String> getUserJsonSettingMap(String userJSONSettings) throws BaseServiceException{
			Map<String, String> userJSONSettingHM = null;
			try{
				String[] settingFieldsArr = userJSONSettings.split(Pattern.quote("^|^"));
		    	userJSONSettingHM = new HashMap<String, String>();
		    	String[] tokensStr;
		    	for(int i=0;i< settingFieldsArr.length;i++) {
		    		tokensStr = settingFieldsArr[i].split(":");
		    		userJSONSettingHM.put(tokensStr[0], tokensStr[1]);
		    	}
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting userjsonsettingmap ", e);
			}
	    	return userJSONSettingHM;
		}
		
		
		private Set<String> getUserJsonSettingSet(String DRHashTags) throws BaseServiceException{
			Set<String> DRHashTagsSet = null;
			try{
				String[] settingFieldsArr = DRHashTags.split(Pattern.quote("^|^"));
				DRHashTagsSet = new HashSet<String>();
		    	for(int i=0;i< settingFieldsArr.length;i++) {
		    		
		    		DRHashTagsSet.add(settingFieldsArr[i]);
		    	}
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting userjsonsettingmap ", e);
			}
	    	return DRHashTagsSet;
		}
		
		/*private boolean testForSONumber(String templateContentStr, String[] itemsSetPlaceHoldersArr, Map<String, String> userJSONSettingHM, 
				JSONObject jsonBodyObj, String[] paymentSetPlaceHoldersArr, String[] DRRefItemLoopPlaceHolderArr){
			
			//JSONObject jsonBodyObj = (JSONObject)jsonMainObj.get("Body");
			JSONObject receiptJSONObj = (JSONObject)jsonBodyObj.get("Receipt");
			
			if(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) + itemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]));
				String loopBlockTwo = Constants.STRING_NILL;
				if(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) != -1 && templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]) != -1) {
					
					loopBlockTwo = templateContentStr.substring(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) + DRRefItemLoopPlaceHolderArr[0].length(),
							templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]));
					
				}//if
				logger.debug("Items HTML is :"+ loopBlockOne);
				
				
				String replacedStr = replaceLoopBlock(loopBlockOne,loopBlockTwo, userJSONSettingHM, jsonBodyObj);
				logger.debug("replacedStr ::"+replacedStr);
				*//**
				 *  Removed after Faye's Request(With out Items and SONumber also We'l send DR)
				 *//*
				
				if((receiptJSONObj.get("SONumber") == null || receiptJSONObj.get("SONumber").toString().isEmpty() )  && replacedStr == null) {
					
					return false;
				}
		   }
			return true;
		}
		*/
		private String addDomainNameToImageURLs(String templateContent){
			// Add domain name to images urls
			String templateContentStr = templateContent;
			String appUrl = PropertyUtil.getPropertyValue("subscriberIp");			
			templateContentStr = templateContentStr.replaceAll("/subscriber/SystemData/digital-templates/",  appUrl +"/subscriber/SystemData/digital-templates/" );
			return templateContentStr;			
		}
		// remove all no-value place holders form template
		private String removeNoValuePhValues(String templateContent){
			// remove all no-value place holders form template
				String templateContentStr = templateContent;
				String digiRcptDefPHStr = PropertyUtil.getPropertyValue("DRPlaceHolders");
				if(digiRcptDefPHStr != null) {
					String[] defPHArr = digiRcptDefPHStr.split(Pattern.quote(","));
					for(int i=0;i<defPHArr.length;i++) {
						templateContentStr = templateContentStr.replaceAll(defPHArr[i],Constants.STRING_NILL);
					}
				}
				return templateContentStr;
		}
		private boolean isCouponVisible = false;
		private boolean isECOMVisible = false;
		private boolean isTotalYouSaved = false;
		private boolean isEmpVisible = false;
		private boolean isPhnVisible = false;
		private boolean isStoreHeading1 = false;
		private boolean isStoreHeading2 = false;
		private boolean isStoreHeading3 = false;
		private boolean isStoreHeading4 = false;
		private boolean isStoreHeading5 = false;
		private boolean isStoreHeading6 = false;
		
		private String replaceBillTo_ShipToPhValues(String templateContentStr, Map<String, String> userJSONSettingHM,JSONObject jsonBodyObj,
				String[] billToSetPlaceHoldersArr , String[] shipToSetPlaceHoldersArr) throws BaseServiceException{

			
			JSONObject jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
			
			if(templateContentStr.indexOf(billToSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(billToSetPlaceHoldersArr[1]) != -1) 
			{
				String loopBlockBillTo = templateContentStr.substring(templateContentStr.indexOf(billToSetPlaceHoldersArr[0]) + billToSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(billToSetPlaceHoldersArr[1]));
				logger.debug("BillTo HTML is :"+ loopBlockBillTo);
				
				//String replacedStr = replaceLoopBlock(loopBlockOne, userJSONSettingHM, jsonBodyObj);
				isBillTo = replaceLoopBlockBillTo(loopBlockBillTo, userJSONSettingHM, jsonReceiptObj);
			}
			
			if(templateContentStr.indexOf(shipToSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(shipToSetPlaceHoldersArr[1]) != -1) 
			{
				String loopBlockShipTo = templateContentStr.substring(templateContentStr.indexOf(shipToSetPlaceHoldersArr[0]) + shipToSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(shipToSetPlaceHoldersArr[1]));
				logger.debug("ShipTo HTML is :"+ loopBlockShipTo);
				
				isShipTo = replaceLoopBlockBillTo(loopBlockShipTo, userJSONSettingHM, jsonReceiptObj);
				
			}
			return templateContentStr;
		
		}
		
		private boolean replaceLoopBlockBillTo(String loopBlock, Map<String,String> userJSONSettingHM,JSONObject jsonReceiptObj) {
			try {
				
				Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(loopBlock);
				//JSONObject jsonObj = null;
				while(matcher.find()) {
					logger.info("--PLACE HOLDERS FOUND --"+ matcher.group(0));
					logger.info("--1--"+ userJSONSettingHM.get(matcher.group(0)));
					if(userJSONSettingHM.get(matcher.group(0)) == null) {
						logger.info("*** Place holder value is not found in HashMap ***");
						return false;
					}
					String[] arrayElement = userJSONSettingHM.get(matcher.group(0)).split(Pattern.quote("."));
					logger.info("arrayElement[0]" + arrayElement[0]);
					logger.info("arrayElement[1]" + arrayElement[1]);
					Object JsonObj = jsonReceiptObj.get(arrayElement[1]);
					if(JsonObj != null && !JsonObj.toString().trim().isEmpty())	return true;
				}
				return false;
			} catch(Exception e) {
				logger.error("Exception ::" , e);
				return false;
			} 
		}
		private String replaceIndividualPhValues(Set<String> DRReceiptHashTagsSet, String templateContent, PrismDRBody prismDRBody){
			//PrismDRBody prismDRBody = prismDRBodyParam;
			
			if(prismDRBody.getCreated_datetime() != null && !prismDRBody.getCreated_datetime().isEmpty()) {
	    		
	    		try {
					OffsetDateTime odt = OffsetDateTime.parse( prismDRBody.getCreated_datetime());
					
					String trxDate = odt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME ).replace( "T" , " " );
					
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = (Date)formatter.parse(trxDate); 
					
					formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
					String docDate = formatter.format(date);
					
					prismDRBody.setCreated_datetime(docDate);
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}catch(Exception e){
					logger.error("Exception ", e);
				}
	    		
	    	}
			
			String templateContentStr = templateContent;
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	
	    	if(prismDRBody.getTotal_discount_amt() == null || prismDRBody.getTotal_discount_amt().isEmpty() || prismDRBody.getTotal_discount_amt().equals("0")) {
	    		
	    		prismDRBody.setTotal_discount_amt(receiptDisc+Constants.STRING_NILL);
	    	}
	    	
	    	prismDRBody.setSold_qty((Integer.parseInt(prismDRBody.getSold_qty()) - Integer.parseInt(prismDRBody.getReturn_qty()))+ Constants.STRING_NILL);
	    		    	
	    	
			for (String phStr : DRReceiptHashTagsSet) {
				String jsonKeyValue = Constants.STRING_NILL;
				
				try {
					logger.debug(" in receipt section hash tag ==="+phStr);
					PrismDREnum prismDREnum = PrismDREnum.getEnumsByHashTag(phStr); 
					if(prismDREnum == null){
						logger.debug(" in receipt section for hash tag ==="+phStr+" No enum found ");
						templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
						continue;
					}
					
					PropertyDescriptor pd = new PropertyDescriptor(prismDREnum.getElementName(), prismDRBody.getClass());
					  Object retValue =  pd.getReadMethod().invoke(prismDRBody);
					  String replaceVal = Constants.STRING_NILL;
					  if(retValue == null ){
						  logger.debug(" in receipt section for hash tag ==="+phStr+" No value returned from getter method ");
						  templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							continue;
					  }
					  if(retValue instanceof List) {
						  List<PrismDRCoupon> coupons = (List<PrismDRCoupon>)retValue;
						  
						  if(coupons.size() > 0) {
							  
							  replaceVal = coupons.get(0).getCoupon_code();
						  }
						  
					  }else {
						  
						  replaceVal = retValue.toString().trim();
						  
					  }
					  logger.debug(" in receipt section hash tag ==="+phStr+" and the value ==="+replaceVal);
					  if(prismDREnum.name().equals("bt_first_name")){
						  replaceVal = replaceVal+" "+(prismDRBody.getBt_last_name() != null && !prismDRBody.getBt_last_name().isEmpty() ?  prismDRBody.getBt_last_name() : Constants.STRING_NILL);
						  
					  }else if(prismDREnum.name().equals("st_first_name")){
						  replaceVal = replaceVal+" "+(prismDRBody.getSt_last_name() != null && !prismDRBody.getSt_last_name().isEmpty() ?  prismDRBody.getSt_last_name() : Constants.STRING_NILL);
						  
					  }
					  else if(prismDREnum.name().equals("tracking_number")) {
							String REFUNDINVOICE = Constants.STRING_NILL; 
							jsonKeyValue = prismDRBody.getTracking_number();
							if(jsonKeyValue.equalsIgnoreCase("Regular")) {
								
								jsonKeyValue="TAX INVOICE/فاتورة ضريبيه";
							}else if(jsonKeyValue.equalsIgnoreCase("Exchange")){
								
								jsonKeyValue="TAX INVOICE-EXCHANGE/فاتورة بدل مع الضريبة";
								 
							}else if(jsonKeyValue.equalsIgnoreCase("Return")) {
								
								jsonKeyValue="TAX CREDIT NOTE/اﻹشعار الدائن الضريبي";
								REFUNDINVOICE = "REFUND INVOICE/فاتورة مرتجع";
							}
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							templateContentStr = templateContentStr.replace("#Receipt.RefundInvoice#", REFUNDINVOICE);
							templateContentStr = templateContentStr.replace("[REFUND_STYLE]" ,!REFUNDINVOICE.isEmpty() ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
						}
					  else if(prismDREnum.name().equals("created_datetime")) {
						  
						  jsonKeyValue = prismDRBody.getCreated_datetime();
						  templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
						  
					  }
					  
					  
					  
					  
					  if(prismDREnum.isNumber()){
							Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
							if(phStr.equalsIgnoreCase("#Receipt.Discount#") || phStr.equalsIgnoreCase("#Receipt.Discount_DEC3#")){
								isTotalYouSaved = (replaceValDbl>0);
							}
							if(prismDREnum.isDecimal()){
								
								replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL;
							}else{
								replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
							}
							
					  }
					  
						  
						  if(phStr.equalsIgnoreCase("#Receipt.Associate#")) isEmpVisible = ! replaceVal.isEmpty();
						  else if(phStr.equalsIgnoreCase("#Receipt.BillToPhone#")) isPhnVisible = ! replaceVal.isEmpty();
						  else if(phStr.equalsIgnoreCase("#Receipt.Coupon#")) isCouponVisible = !replaceVal.isEmpty();
						  else if( phStr.equalsIgnoreCase("#Receipt.ECOMOrderNo#")) isECOMVisible = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading1#")) isStoreHeading1 = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading2#")) isStoreHeading2 = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading3#")) isStoreHeading3 = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading4#")) isStoreHeading4 = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading5#")) isStoreHeading5 = !replaceVal.isEmpty();
						  else  if(phStr.equalsIgnoreCase("#Store.Heading6#")) isStoreHeading6 = !replaceVal.isEmpty();
					  
					  templateContentStr = templateContentStr.replace(phStr, replaceVal);
				/*	if(phStr.equalsIgnoreCase("#Receipt.Associate#")) {
						//jsonKeyValue = jsonMainObj.get();
						
						
						String lookupEmployeeID =(String)prismDRBody.getEmployee1_login_name();
						if(lookupEmployeeID != null && !lookupEmployeeID.isEmpty()) {
								isEmpVisible =  true;
								templateContentStr = templateContentStr.replace(phStr, lookupEmployeeID);
						}
					}else if(phStr.equalsIgnoreCase("#Receipt.BillToPhone#")){
						String lookupMobile_Phone_No =prismDRBody.getBt_primary_phone_no();
							if(lookupMobile_Phone_No != null && !lookupMobile_Phone_No.isEmpty()){
								isPhnVisible =  !lookupMobile_Phone_No.isEmpty();
								templateContentStr = templateContentStr.replace(phStr, lookupMobile_Phone_No);
							}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.TaxAmount#") || phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")) {
						
						Double amount = (Double) (prismDRBody.getSale_total_tax_amt().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(prismDRBody.getSale_total_tax_amt()));
						 //templateContentStr = templateContentStr.replace("#Receipt.Subtotal#", (String)decimalFormat.format(amount));
						
						if(phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")){
							
							templateContentStr = templateContentStr.replace("#Receipt.TaxAmount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
						}else{
							templateContentStr = templateContentStr.replace("#Receipt.TaxAmount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
						}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.InvcTotQty#") ) {
						
						 templateContentStr = templateContentStr.replace("#Receipt.InvcTotQty#", prismDRBody.getSold_qty()+Constants.STRING_NILL);
						
						
					}
					else if(phStr.equalsIgnoreCase("#Receipt.Subtotal#") || phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")) {
						
						Double amount = (Double) (prismDRBody.getSale_subtotal().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(prismDRBody.getSale_subtotal()));
						if(phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")){
							
							templateContentStr = templateContentStr.replace("#Receipt.Subtotal_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
						}else{
							templateContentStr = templateContentStr.replace("#Receipt.Subtotal#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
						}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.TrackingNum_LU#")) {
						String REFUNDINVOICE = Constants.STRING_NILL; 
						jsonKeyValue = prismDRBody.getOrder_tracking_number();
						if(jsonKeyValue.equalsIgnoreCase("Regular")) {
							
							jsonKeyValue="TAX INVOICE/فاتورة ضريبيه";
						}else if(jsonKeyValue.equalsIgnoreCase("Exchange")){
							
							jsonKeyValue="TAX INVOICE-EXCHANGE/فاتورة بدل مع الضريبة";
						}else if(jsonKeyValue.equalsIgnoreCase("Return")) {
							
							jsonKeyValue="TAX CREDIT NOTE/اﻹشعار الدائن الضريبي";
							REFUNDINVOICE = "REFUND INVOICE/فاتورة مرتجع";
						}
						templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
						templateContentStr = templateContentStr.replace("#Receipt.RefundInvoice#", REFUNDINVOICE);
						templateContentStr = templateContentStr.replace("[REFUND_STYLE]" ,!REFUNDINVOICE.isEmpty() ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
					}else if(phStr.equalsIgnoreCase("#Receipt.Amount#") || phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")) {
						jsonKeyValue = prismDRBody.getSale_total_amt();
						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
						
						BigDecimal roundTotal = new BigDecimal(0);
						//BigDecimal tenderedRounded = null;
						if(phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")){
							logger.debug("==insidedec3===");
							roundTotal = BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP);
							templateContentStr = templateContentStr.replace("#Receipt.Amount_DEC3#", roundTotal +Constants.STRING_NILL);
						}else{
							roundTotal = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
							
							templateContentStr = templateContentStr.replace("#Receipt.Amount#", roundTotal+Constants.STRING_NILL);
						}
						
						
					}else if(phStr.equalsIgnoreCase("#Receipt.Fee#")) {
						jsonKeyValue = k.getOrder_fee_amt1();
						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
						 templateContentStr = templateContentStr.replace("#Receipt.Fee#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
						
					}else if(phStr.equalsIgnoreCase("#Receipt.Shipping#")) {
						jsonKeyValue  = prismDRBody.getOrder_shipping_amt();
						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
						 templateContentStr = templateContentStr.replace("#Receipt.Shipping#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
						
					}else if(phStr.equalsIgnoreCase("#Receipt.Coupon#")) {
						List<String> coupons = k.getCoupons();
						if(coupons != null && coupons.size()>0 && coupons.get(0) != null  ) {
							jsonKeyValue = coupons.get(0);
							isCouponVisible = !jsonKeyValue.isEmpty();
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
					}else if(phStr.equalsIgnoreCase("#Receipt.ECOMOrderNo#")) {
						//jsonKeyValue = prismDRBody.getEcomOr
						if(jsonKeyValue != null && !jsonKeyValue.isEmpty()) {
							isECOMVisible =  !jsonKeyValue.isEmpty();
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
					}else if(phStr.equalsIgnoreCase("#Receipt.Discount#") || phStr.equalsIgnoreCase("#Receipt.Discount_DEC3#")) {
						jsonKeyValue  = prismDRBody.getTotal_discount_amt();
						if(jsonKeyValue != null  ) {
							Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
							
							isTotalYouSaved = (amount>0);
							
							if(phStr.equalsIgnoreCase("#Receipt.Discount_DEC3#")){
								templateContentStr = templateContentStr.replace("#Receipt.Discount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
							}else{
								templateContentStr = templateContentStr.replace("#Receipt.Discount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
							}
							
						}
					}else if(phStr.equalsIgnoreCase("#Receipt.InvcTotalRoundAmt#") || phStr.equalsIgnoreCase("#Receipt.InvcTotalRoundAmt_DEC3#")) {
						jsonKeyValue = prismDRBody.getSale_total_amt();
						if(jsonKeyValue != null  ) {
							
							Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
							
							if(phStr.equalsIgnoreCase("#Receipt.InvcTotalRoundAmt_DEC3#")){
								logger.debug("==insidedec3===");
								templateContentStr = templateContentStr.replace("#Receipt.InvcTotalRoundAmt_DEC3#",  BigDecimal.valueOf(amount.doubleValue()).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
							}else{
								templateContentStr = templateContentStr.replace("#Receipt.InvcTotalRoundAmt#",  BigDecimal.valueOf(amount.doubleValue()).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
							}
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.InvcRoundAmt#") || phStr.equalsIgnoreCase("#Receipt.InvcRoundAmt_DEC3#")) {
						jsonKeyValue = prismDRBody.getRounded_due_amt();
						if(jsonKeyValue != null  ) {
							
							Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
							BigDecimal roundTotal = new BigDecimal(0);
							if(phStr.equalsIgnoreCase("#Receipt.InvcRoundAmt_DEC3#")){
								logger.debug("==insidedec3===");
								
								templateContentStr = templateContentStr.replace("#Receipt.InvcRoundAmt_DEC3#",  BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
							}else{
								
								templateContentStr = templateContentStr.replace("#Receipt.InvcRoundAmt#",  BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
							}
							
						}
						
					}
					else if(phStr.equalsIgnoreCase("#Store.Heading1#")) {
						jsonKeyValue = prismDRBody.getStore_address_line1();
						if(jsonKeyValue != null ) {
							isStoreHeading1 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Store.Heading2#")) {
						jsonKeyValue = prismDRBody.getStore_address_line2();
						if(jsonKeyValue != null ) {
							isStoreHeading2 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Store.Heading3#")) {
						jsonKeyValue = prismDRBody.getStore_address_line3();
						if(jsonKeyValue != null ) {
							isStoreHeading3 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
					}else if(phStr.equalsIgnoreCase("#Store.Heading4#")) {
						jsonKeyValue = prismDRBody.getStore_address_line4();
						if(jsonKeyValue != null ) {
							isStoreHeading4 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Store.Heading5#")) {
						jsonKeyValue = prismDRBody.getStore_address_line5();
						if(jsonKeyValue != null ) {
							isStoreHeading5 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
							//jsonKeyValue="TAX CREDIT NOTE/اﻹشعار الدائن الضريبي ";
						}
						//templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
					//}else if(phStr.equalsIgnoreCase("#Receipt.Amount#")) {
						
					}else if(phStr.equalsIgnoreCase("#Store.Heading6#")) {
						jsonKeyValue = prismDRBody.getStore_address_zip();
						if(jsonKeyValue != null ) {
							isStoreHeading6 =  !jsonKeyValue.isEmpty();
							jsonKeyValue = jsonKeyValue.replace("\\n", Constants.STRING_NILL);
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.AL_Extract_Date#")) {
						if(jsonKeyValue != null && !jsonKeyValue.isEmpty()) {
							
							
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							try {
								Date date = (Date) formatter.parse(jsonKeyValue);
								Calendar AL_Extract_Date = Calendar.getInstance();
								AL_Extract_Date.setTime(date);
								
								String AL_Extract_Date_Print_value = MyCalendar.calendarToString(AL_Extract_Date, MyCalendar.FORMAT_MONTHDATE_ONLY);
								templateContentStr = templateContentStr.replace(phStr, AL_Extract_Date_Print_value);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								logger.debug("uable to Parse the value ==="+jsonKeyValue);
								templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							}
							
						}
						
					}else if(phStr.equalsIgnoreCase("#Receipt.Serial Number#")) {
						jsonKeyValue = prismDRBody.getSerial_number();
						if(jsonKeyValue != null ) {
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							
						}
						
					}
*/
				} catch (NumberFormatException e) {
					logger.error("Exception ", e);
				} catch (IllegalAccessException e) {
					logger.error("Exception ", e);
				} catch (IllegalArgumentException e) {
					logger.error("Exception ", e);
				} catch (InvocationTargetException e) {
					logger.error("Exception ", e);
				} catch (IntrospectionException e) {
					logger.error("Exception ", e);
				}catch (Exception e) {
					// TODO: handle exception
					logger.error("Exception ", e);
				}	
				 templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    } // for 
			return templateContentStr;
		
			
			
		}
		private String replaceIndividualPhValues(Map<String, String> userJSONSettingHM, String templateContent, JSONObject jsonMainObj){
			

			
			String templateContentStr = templateContent;
			Set<String> set = userJSONSettingHM.keySet();
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	String jsonKeyValue = "";
	    	
	    	Double discount = 0.0;
	    	Double subtotal = 0.0;
	    	Double total = 0.0;
	    	
			for (String phStr : set) {
				
				jsonKeyValue = "";
	    		JSONObject parentObject = null;
	    		JSONArray parentArrayObject = null;
			
		    		if(templateContentStr.indexOf(phStr) != -1) {			// PH exists in template
		    			
		    			jsonKey = userJSONSettingHM.get(phStr);
		    	//		logger.debug("*******<< jsonKey : "+ jsonKey);
		    			
		    			if(jsonKey.indexOf("_$$_") != -1) {
		    				
		    		//		logger.debug("contains dollar .....");
		    				
		    				String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));
		    				String DocDate=null;
		    				for(int j=0;j<jsonKeytokens.length;j++) {
		    					
					               // . path exists in json key 
			    				
			    				jsonPathArr = jsonKeytokens[j].split(Pattern.quote("."));
			    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
			    				
			    				try {
			    					
			    					//logger.debug("SINGLE OBJ CREATED...");
			    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
			    				} catch(ClassCastException e) {
			    					
			    				//	logger.debug("ARRAY OBJ CREATED...");
			    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
			    				//	logger.debug("--Array---"+ parentArrayObject);
			    					parentObject  = (JSONObject)parentArrayObject.get(0);
			    				//	logger.debug("--First element in array---"+ parentObject);
			    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
			    				}	
			    			
			    				if(parentObject != null) {
                                   try {
			    						if(jsonPathArr[1].equals("DocDate") && (parentObject.get(jsonPathArr[1])!=null || !(parentObject.get(jsonPathArr[1]).toString()).isEmpty())){
												DocDate = parentObject.get(jsonPathArr[1]) +"";
												logger.info("DocDate-------------"+DocDate);
												logger.info("DocDate value------------"+jsonPathArr[1].toString());
												logger.info("DocDate value------------"+jsonPathArr[1]);
												
											}
											if(jsonPathArr[1].equals("DocTime")  && (parentObject.get(jsonPathArr[1]).toString().isEmpty())){
									
											     jsonKeyValue = DocDate;
											     logger.info("Date:--------------->" +jsonKeyValue);
											}
											else if(jsonPathArr[1].equals("DocTime")  && (parentObject.get(jsonPathArr[1])!=null || !(parentObject.get(jsonPathArr[1]).toString()).isEmpty())){
												// DOCTIME = "23:43:23" --> 11:43:23 PM
												if(DocDate != null && !DocDate.toString().isEmpty()){
												String docTime= parentObject.get(jsonPathArr[1]) + "";
												String 	docTime_AM_PM =	MyCalendar.calendarToString(MyCalendar.string2Calendar(DocDate+ " " +docTime),MyCalendar.FORMAT_TIME_AM, null);	
												//String docTime_AM_PM = MyCalendar.calendarToString(MyCalendar.string2Calendar(jsonKeyValue +(String)parentObject.get(jsonPathArr[1])),MyCalendar.FORMAT_TIME_AM, null);
												jsonKeyValue += docTime_AM_PM;
												logger.info("Receipt time (AM/PM) is" +jsonKeyValue);
											}
											}
											
		
			    									
											else
												jsonKeyValue += (String)parentObject.get(jsonPathArr[1]) + " ";
												logger.info("Enterd to else");
								
			    						
			    						
			    					} catch(ClassCastException e) {
			    						try {
			    							jsonKeyValue += ((Double)parentObject.get(jsonPathArr[1]))+ " ";
			    						} catch(ClassCastException f) { 
			    							jsonKeyValue += ((Long)parentObject.get(jsonPathArr[1])) + " ";
			    						}	
			    					}
			 
			    				} else {
			    					jsonKeyValue = Constants.STRING_NILL;
			    		//			logger.debug("** Parent JSON is null *****");
			    				} 
			    			
		    				}
		    				
		    			//	logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
	    					//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				logger.info("phStr------------"+phStr);
		    				logger.info("jsonKeyValue--------------------"+jsonKeyValue);
		    				
		    			} else if(jsonKey.contains(".")) {			               // . path exists in json key 
		    				
		    				jsonPathArr = jsonKey.split(Pattern.quote("."));
		    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
		    				
		    				try {
		    					
		    					//logger.debug("SINGLE OBJ CREATED...");
		    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
		    				} catch(ClassCastException e) {
		    					
		    					//logger.debug("ARRAY OBJ CREATED...");
		    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
		    					//logger.debug("--Array---"+ parentArrayObject);
		    					parentObject  = (JSONObject)parentArrayObject.get(0);
		    				//	logger.debug("--First element in array---"+ parentObject);
		    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
		    				}	
		    			
		    				if(parentObject != null) {
		    					
		    					try {
		    						
		    						if(parentObject.get(jsonPathArr[1]) != null) {  
		    							
		    							jsonKeyValue = (String)parentObject.get(jsonPathArr[1]);
		    						}	
		    					} catch(ClassCastException e) {
		    						try {
		    							jsonKeyValue = ((Double)parentObject.get(jsonPathArr[1])).toString();
		    						} catch(ClassCastException f) { 
		    							jsonKeyValue = ((Long)parentObject.get(jsonPathArr[1])).toString();
		    						}	
		    					} 
		    					
		    				//	logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
		    				} else {
		    					jsonKeyValue = Constants.STRING_NILL;
		    				//	logger.debug("** Parent JSON is null *****");
		    				}
		    				/**
		    				 * APP-1222
		    				 * 0 = Normal sales receipt, 2 = Return receipt, 3 = Check in, 4 = Check out, 6 = Lost sale,7 = High security
		    				 * 10 = open register, 11 = close register, 12 = pay out , 13 = override
		    				 * */
		    				if(phStr.equalsIgnoreCase("#Receipt.InvcHdrReceiptType#")) {
		    					/** TODO get the key values from DB
		    					 * spit and assign the value.
		    					 */
		    					try{	
		    						//0:Regular^|^2:Return^|^3:Check in^|^4:Check out^|^6:Lost sale^|^7:High security^|^10:open register^|^11:close register^|^12:payout^|^13:override
		    						if(jsonKeyValue!=null&&!jsonKeyValue.isEmpty()){
		    							String invcHdrReceiptTypeSettings = PropertyUtil.getPropertyValueFromDB("Receipt_InvcHdrReceiptType"); 
		    							Map<String, String> invcHdrReceiptTypeSettingsHM  = null;

		    							invcHdrReceiptTypeSettingsHM = getUserJsonSettingMap(invcHdrReceiptTypeSettings); 
		    							jsonKeyValue = invcHdrReceiptTypeSettingsHM.containsKey(jsonKeyValue) ? invcHdrReceiptTypeSettingsHM.get(jsonKeyValue) : jsonKeyValue;
		    						}
		    						
		    					}catch(Exception e){
		    						logger.error("Exception :: ",e);

		    					}
		    				}else if(phStr.equalsIgnoreCase("#Receipt.Amount#") || phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")) {
		    					
		    					//BigDecimal tenderedRounded = null;
		    					if(phStr.equalsIgnoreCase("#Receipt.Amount_DEC3#")){
		    						logger.debug("==insidedec3===");
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Amount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Amount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    						
		    					}
		    					
		    					
		    				}else if(phStr.equalsIgnoreCase("#Receipt.Subtotal#") || phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")) {
		    					
		    					
		    					if(phStr.equalsIgnoreCase("#Receipt.Subtotal_DEC3#")){
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Subtotal_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.Subtotal#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}
		    					
		    				}else if(phStr.equalsIgnoreCase("#Receipt.SubtotalWOT#") || phStr.equalsIgnoreCase("#Receipt.SubtotalWOT_DEC3#")) {
		    					
		    					
		    					Double tax = drbody.getReceipt().getTax() != null && !drbody.getReceipt().getTax().isEmpty() ? Double.parseDouble(drbody.getReceipt().getTax()): 0.0;
		    					if(phStr.equalsIgnoreCase("#Receipt.SubtotalWOT_DEC3#")){
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.SubtotalWOT_DEC3#", BigDecimal.valueOf(amount-tax).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						
		    						templateContentStr = templateContentStr.replace("#Receipt.SubtotalWOT#", BigDecimal.valueOf(amount-tax).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}
		    					
		    				}
		    				else if(phStr.equalsIgnoreCase("#Receipt.TaxAmount#") || phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")) {
		    					
		    					
		    					if(phStr.equalsIgnoreCase("#Receipt.TaxAmount_DEC3#")){
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.000") : Double.parseDouble(jsonKeyValue));
		    						
		    						templateContentStr = templateContentStr.replace("#Receipt.TaxAmount_DEC3#", BigDecimal.valueOf(amount).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}else{
		    						Double amount = (Double) (jsonKeyValue.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(jsonKeyValue));
		    						templateContentStr = templateContentStr.replace("#Receipt.TaxAmount#", BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
		    					}
		    					
		    				}
		    				
		    				//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				
		    			}  
		    		}
		    } // for 
			if(templateContentStr.contains("#Receipt.OriginalPrice#") || templateContentStr.contains("#Receipt.GOriginalPrice#")) {//APP-4694
				//for Ginesys GOriginalPrice = Total+Discount
				try {

					JSONObject receiptObject = (JSONObject)jsonMainObj.get("Receipt");
					if(receiptObject != null ) {
						String subTot = ((Object)receiptObject.get("Subtotal")).toString();
						subtotal = subTot!= null && !subTot.isEmpty() ? Double.parseDouble(subTot) : subtotal;
						String tot = ((Object)receiptObject.get("Total")).toString();
						total = tot!= null && !tot.isEmpty() ? Double.parseDouble(tot) : total;
						String disc = ((Object)receiptObject.get("Discount")).toString();
						discount = disc!= null && !disc.isEmpty() ? Double.parseDouble(disc) : discount;
					}
				} catch(Exception e) {
					logger.error("Exception==", e);
				}
				
				logger.debug("GOriginalPrice=Total+Discount="+BigDecimal.valueOf(total+discount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL);
				templateContentStr = templateContentStr.replace("#Receipt.OriginalPrice#", BigDecimal.valueOf(subtotal+discount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL );
				templateContentStr = templateContentStr.replace("#Receipt.GOriginalPrice#", BigDecimal.valueOf(total+discount).setScale(2, RoundingMode.HALF_UP)+Constants.STRING_NILL );
			}//if
			
			return templateContentStr;
		
			
		}
		private String replaceIndividualPhValues(Set<String> DRReceiptHashTagsSet, String templateContent, Receipt receipt){
			
			String templateContentStr = templateContent;
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	String jsonKeyValue = Constants.STRING_NILL;
	    //	logger.debug("receiptDisc==="+receiptDisc+"====receipt.getDiscount()==="+receipt.getDiscount()); APP-4568
	    	if((receipt.getDiscount() == null || receipt.getDiscount().isEmpty() || 
	    			(receipt.getDiscount() != null && !receipt.getDiscount().isEmpty() && Double.parseDouble(receipt.getDiscount())<=0))&& receiptDisc>0) {
	    		receipt.setDiscount(receiptDisc+"");
	    	}
	    	/*if(prismDRBody.getTotal_discount_amt() == null || prismDRBody.getTotal_discount_amt().isEmpty() || prismDRBody.getTotal_discount_amt().equals("0")) {
	    		
	    		prismDRBody.setTotal_discount_amt(receiptDisc+Constants.STRING_NILL);
	    	}
	    	
	    	prismDRBody.setSold_qty((Integer.parseInt(prismDRBody.getSold_qty()) - Integer.parseInt(prismDRBody.getReturn_qty()))+ Constants.STRING_NILL);
	    	*/	    	
	    	
			for (String phStr : DRReceiptHashTagsSet) {
				
				try {
				//	logger.debug(" in receipt section hash tag ==="+phStr); APP-4568
					/*PrismDREnum prismDREnum = PrismDREnum.getEnumsByHashTag(phStr); 
					if(prismDREnum == null){
						logger.debug(" in receipt section for hash tag ==="+phStr+" No enum found ");
						templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
						con
						tinue;
					}*/
					String OptDRJsonEleName = null;
					PrismDREnum prismDREnum = PrismDREnum.getEnumsByHashTag(phStr); 
					if(prismDREnum == null) { //avoiding the code dependency on new hashtags
						
						OptDRJsonEleName = phStr.replace("#", "").split("\\.")[1];
				//		logger.debug("OptDRJsonEleName===="+ OptDRJsonEleName); APP-4568
						if(OptDRJsonEleName.equalsIgnoreCase("TotalInWords")) {
							try {
						//		logger.debug("inside TotalInWords===="); APP-4568
								//app-3634
								String TotalInWords = ""; //Utility.numberToWord(Integer.parseInt(receipt.getTotal().isEmpty() ? "0" :receipt.getTotal() ));
								String amount=receipt.getTotal().isEmpty() ? "0" :receipt.getTotal();
								String[] amtarr={amount,"0"};
								try {
									if(amount.contains(".")) {
										amtarr[0]=amount.split("\\.")[0];
										amtarr[1]="0."+amount.split("\\.")[1];
									}
										
								} catch (Exception e) {
									e.printStackTrace();
								}
								int dec=0;
								dec=(int)(Double.parseDouble(amtarr[1])*100); //for india
								
								String[] totalInWordsArr=new String[2];
								totalInWordsArr[0] = Utility.numberToWord(Integer.parseInt(amtarr[0]));
								totalInWordsArr[1] = Utility.numberToWord(dec);
								
								if(user.getCountryType().equalsIgnoreCase("India"))
									TotalInWords = totalInWordsArr[0]+" Rupees and " + totalInWordsArr[1] +" Paisa";
								
								
						//		logger.debug("inside TotalInWords===="+TotalInWords); APP-4568
								receipt.setTotalInWords(TotalInWords);
							//	logger.debug("done TotalInWords===="+TotalInWords); APP-4568
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception =="+e);
							}
						}else if(OptDRJsonEleName.equalsIgnoreCase("AvgDiscountFactor")) {//APP-4663
							
							logger.debug("----------< inside #Receipt.AvgDiscountFactor# >----------");
							//( (InvoiceAmount-Net Amount Payable)/InvoiceAmount )*100
							Double invcAmt; // Total+Discount
							Double netAmtPaid;//Tendered
							Double avgDiscountFactor ;
							invcAmt = (receipt.getTotal() != null && !receipt.getTotal().isEmpty() ? 
									Double.parseDouble(receipt.getTotal()) :0.00) + 
									(receipt.getDiscount() != null && !receipt.getDiscount().isEmpty() ? Double.parseDouble(receipt.getDiscount()) :0.00);
							netAmtPaid = (receipt.getTendered() != null && !receipt.getTendered().isEmpty() ? 
									Double.parseDouble(receipt.getTendered()) :0.00);
							
							avgDiscountFactor = ( (invcAmt - netAmtPaid)/invcAmt )*100;
							receipt.setAvgDiscountFactor(BigDecimal.valueOf(avgDiscountFactor).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
							logger.debug("====#Receipt.AvgDiscountFactor#==="+receipt.getAvgDiscountFactor());
						}
					}else{
						
						OptDRJsonEleName = prismDREnum.getOptDRJsonEleName();
						
						//app-3706
						if(OptDRJsonEleName.equalsIgnoreCase("DocDate") ) {
							
							String selectedDateFormat = "";
							selectedDateFormat = digitalReceiptUserSettings.getDateFormat();

							if(selectedDateFormat!= null && !selectedDateFormat.isEmpty() ) {
								
								POSMappingDao	posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
								List<POSMapping> posMappingList = posMappingDao.findAllByUserId(user.getUserId());
								for(POSMapping posMapping : posMappingList) {
									if(posMapping.getDigitalReceiptAttribute()!=null && posMapping.getDigitalReceiptAttribute().equalsIgnoreCase("Receipt::DocDate")) {
										
										String formattedDate = "";
										formattedDate = getDateFormattedData(posMapping,receipt.getDocDate(),selectedDateFormat);
										receipt.setDocDate(formattedDate);
									}
								}
							}
						}
					}
					if(OptDRJsonEleName == null) continue;
				//	logger.debug("hashTag ==="+phStr+" element ==="+OptDRJsonEleName); APP-4568
					
					PropertyDescriptor pd = new PropertyDescriptor(OptDRJsonEleName, receipt.getClass());
					  Object retValue =  pd.getReadMethod().invoke(receipt);
					  String replaceVal = Constants.STRING_NILL;
					  if(retValue == null ){
					//	  logger.debug(" in receipt section for hash tag ==="+phStr+" No value returned from getter method "); APP-4568
						  templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							continue;
					  }
						  
					  replaceVal = retValue.toString().trim();
						  
				 logger.debug(" in receipt section hash tag ==="+phStr+" element ==="+OptDRJsonEleName+" and the value ==="+replaceVal);// - APP-4568
					  if(prismDREnum != null && prismDREnum.name().equals("bt_first_name")){
						  replaceVal = replaceVal+" "+(receipt.getBillToLName() != null && 
								  !receipt.getBillToLName().isEmpty() ?  receipt.getBillToLName() : Constants.STRING_NILL);
						  
					  }
					  else if( prismDREnum != null && prismDREnum.name().equals("tracking_number")) {
							String REFUNDINVOICE = Constants.STRING_NILL; 
							jsonKeyValue = receipt.getTrackingNum();
							/*if(jsonKeyValue.equalsIgnoreCase("Regular")) {
								
								jsonKeyValue="TAX INVOICE/فاتورة ضريبيه";
							}else if(jsonKeyValue.equalsIgnoreCase("Exchange")){
								
								jsonKeyValue="TAX INVOICE-EXCHANGE/فاتورة بدل مع الضريبة";
								 
							}else if(jsonKeyValue.equalsIgnoreCase("Return")) {
								
								jsonKeyValue="TAX CREDIT NOTE/اﻹشعار الدائن الضريبي";
								REFUNDINVOICE = "REFUND INVOICE/فاتورة مرتجع";
							}*/
							templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
							templateContentStr = templateContentStr.replace("#Receipt.RefundInvoice#", REFUNDINVOICE);
							templateContentStr = templateContentStr.replace("[REFUND_STYLE]" ,!REFUNDINVOICE.isEmpty() ? Constants.STRING_NILL : " mso-hide:all;display:none;max-height:0px;overflow:hidden;");
						}
					  else if(prismDREnum != null &&  prismDREnum.name().equals("created_datetime")) {
						  
						  jsonKeyValue = receipt.getDocDate();
						  
					  }
					  
					  if(prismDREnum != null &&  prismDREnum.isNumber()){
							Double replaceValDbl = replaceVal.isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(replaceVal);
							if(phStr.equals("#Receipt.TotWithTaxAmount#")) {
								
								replaceValDbl = (receipt.getTotalTax() != null && !receipt.getTotalTax().isEmpty() ? 
										Double.parseDouble(receipt.getTotalTax()) :0.00) + 
										(receipt.getSubtotal() != null && !receipt.getSubtotal().isEmpty() ? Double.parseDouble(receipt.getSubtotal()) :0.00);
						//		logger.debug("====#Receipt.TotWithTaxAmount#==="+replaceValDbl +"===="+receipt.getSubtotal()+"==="+receipt.getTotalTax()); APP -4568
							}
							if(phStr.equalsIgnoreCase("#Receipt.Discount#") || phStr.equalsIgnoreCase("#Receipt.Discount_DEC3#")){
								isTotalYouSaved = (replaceValDbl>0);
							}
							if(prismDREnum.isDecimal()){
								
								replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(3, RoundingMode.HALF_UP) +Constants.STRING_NILL;
							}else{
								replaceVal = BigDecimal.valueOf(replaceValDbl).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL;
							}
							
					  }
						  
					  if(phStr.equalsIgnoreCase("#Receipt.Associate#")) isEmpVisible = ! replaceVal.isEmpty();
					  else if(phStr.equalsIgnoreCase("#Receipt.BillToPhone#")) isPhnVisible = ! replaceVal.isEmpty();
					 // else if(phStr.equalsIgnoreCase("#Receipt.TotalInWords#")) isPhnVisible = ! replaceVal.isEmpty();
					  else if(phStr.equalsIgnoreCase("#Receipt.Coupon#")) isCouponVisible = !replaceVal.isEmpty();
					  else if( phStr.equalsIgnoreCase("#Receipt.ECOMOrderNo#")) isECOMVisible = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading1#")) isStoreHeading1 = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading2#")) isStoreHeading2 = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading3#")) isStoreHeading3 = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading4#")) isStoreHeading4 = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading5#")) isStoreHeading5 = !replaceVal.isEmpty();
					  else  if(phStr.equalsIgnoreCase("#Store.Heading6#")) isStoreHeading6 = !replaceVal.isEmpty();
					  
					  templateContentStr = templateContentStr.replace(phStr, replaceVal);
				
				} catch (NumberFormatException e) {
					
				} catch (IllegalAccessException e) {
					logger.error("Exception ", e);
				} catch (IllegalArgumentException e) {
					logger.error("Exception ", e);
				} catch (InvocationTargetException e) {
					logger.error("Exception ", e);
				} catch (IntrospectionException e) {
					logger.error("Exception ", e);
				}	catch(Exception e) {
					logger.error("Exception ", e);
				}
				
				 templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    } // for 
			return templateContentStr;
		
			
			
		}
		
/*private String replaceItemArrayNew(String loopBlockOne, String ItemPHTag, Double amtVal){
			
			
			String itemHolder = loopBlockOne;
			String preparedString=Constants.STRING_NILL;
			try{
				final String COLSPAN = "COLSPAN";
				final String LABEL = "LABEL";
				final String VALUE = "VALUE";
				final String CELLSPACING = "CELLSPACING";
				final String BENEATH_ITEMS_TR_STRUCTURE = 
						"<tr>"+ 
								"<td colspan="+COLSPAN+">" +
								"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
								"<tr>" +
								"<td width=\"50%\"> </td>" +
								"<td width=\"20%\" style=\"text-align:left;\">"+LABEL+"</td>" +
								"<td width=\"30%\" style=\"text-align:right;\">"+VALUE+"</td>"+
								"</tr>"+
								"</table>"+
								"</td>"+
								"</tr>";


				

				
				
				String [] itemArray = getItemsArray(loopBlockOne);
				logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
				DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
				
				
				
				
				if(ItemPHTag.trim().length() == 0){
					preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+Constants.STRING_NILL).
							replace(LABEL, Constants.STRING_NILL).replace(VALUE, Constants.STRING_NILL).replace(CELLSPACING, "7");
					
					logger.info("line spacing >>> "+preparedString);
					return preparedString;
				}
				
				
				preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+Constants.STRING_NILL).
						replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
						.replace(CELLSPACING, "0");
				
				
				
				
				logger.info("preparedString >>> "+preparedString);
				

			}catch(Exception e){
				logger.error("Exception >>> ",e);
				return itemHolder;
			}
			
			
			
			
			
			return preparedString;
			
		}*/

public String[] getItemsArray(String itemHtml) throws BaseServiceException{
	/*String text = "<tr><td>#Item.Description1#</td><td>#Item.Quantity#</td>"
					+ "<td>#Item.Discount#</td><td>#Item.DiscountPercent#</td>"
					+ "<td>#Item.UnitPrice#</td><td>#Item.Total#</td></tr>";
	*/
	 String pattern = "<td.*?>([^<]+)</td.*?>";
	   Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(itemHtml);
	
			
	 String resultStr =Constants.STRING_NILL;
    while (m.find()) {
    	       	
    	String itemValue = m.group(1);
    	if(! resultStr.isEmpty())	resultStr+=Constants.DELIMETER_COMMA;
    	resultStr += itemValue;
    	
    }
  String [] itemsArray = resultStr.split(Constants.DELIMETER_COMMA);
		  
  return itemsArray;  
}

public String replaceItemArray(String loopBlockOne, String ItemPHTag, Double amtVal) throws BaseServiceException{
	
	String itemHolder = loopBlockOne;
	try {
		//int itemHolderIndex = 0;
		 String [] itemArray = getItemsArray(loopBlockOne);
		 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
		int rightAlignIndex = (itemArray.length-2);
		for (int itemHolderIndex = 0; itemHolderIndex<itemArray.length ; itemHolderIndex++) {
			if(itemHolderIndex == rightAlignIndex) {
				//logger.debug("in if ::"+ItemPHTag + itemHolderIndex+ itemArray[itemHolderIndex] + itemArray[itemHolderIndex+1]);
				try {
					String str = itemArray[itemHolderIndex];
					//String substr = str.substring(0,str.length()-1);
					String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
					String itemSubStr = originalitemSubStr.replace(str, "<strong> "+ItemPHTag+" </strong>");
					//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
					itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
					//logger.debug("itemHolder 1::"+itemHolder);
					itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<div align='right'><strong>"+ decimalFormat.format(amtVal)+"</strong></div>");
					//logger.debug("itemHolder2 ::"+itemHolder);
					
					try{
						itemHolder = itemHolder.replace("text-align:left", "text-align:right");
					}catch(Exception e){
						logger.error("Exception >>>>>>>>>>>>> ",e);
					}
					
					//logger.debug("prepared  itemHolder ::"+itemHolder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				break;
				
			}else{
				//logger.debug("in else ::"+itemHolderIndex);
				
				String str = itemArray[itemHolderIndex];
				//String substr = str.substring(0,str.length()-1);
				String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
				String itemSubStr = originalitemSubStr.replace(str, "&nbsp;");
				//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
				itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
				//logger.debug("itemHolder 1::"+itemHolder);
				//itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<strong>"+ decimalFormat.format(amtVal)+"</strong>");
				//logger.debug("itemHolder2 ::"+itemHolder);
				
				//itemHolder = itemHolder.replace(itemArray[itemHolderIndex], "&nbsp;");
			}
				//itemHolderIndex ++;
		}
		logger.debug("itemHolder3 ::"+itemHolder);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}
	return itemHolder;
}
	boolean isChnageExists = false;
	boolean isPaymentNumberNTake = false;
	boolean isPaymentmescelleneous = false;
	boolean isBaseTaken = false;
	boolean isAuthTaken = false;
	double tenderedAmt = 0.0;
	BigDecimal rounded = new BigDecimal(0);//roundvalue
	BigDecimal tenderedRounded = new BigDecimal(0);
	double receiptDisc = 0.0;//totaldisc
	double totalAfterVAT = 0.0;//total
	double totalVAT = 0.0;//tax
	double totalBeforeVAT=0.0;//subtotal
	int totalQty=0;//subtotal
	
	private String replaceGSTPhValues(String templateContentStr,  Map<String, String> userJSONSettingHM, 
			DRBody drbody ) {
		try {
			//JSONObject jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
			logger.debug("in replaceGSTPhValues =="+templateContentStr.indexOf("##BEGIN GST##") );
			logger.debug("in replaceGSTPhValues =="+templateContentStr.indexOf("##END GST##") );
			if(templateContentStr.indexOf("##BEGIN GST##") != -1 && templateContentStr.indexOf("##END GST##") != -1) 
			{
				String loopBlock = templateContentStr.substring(templateContentStr.indexOf("##BEGIN GST##") + "##BEGIN GST##".length(),
						templateContentStr.indexOf("##END GST##"));
				
				String replaceLoopBlock = loopBlock;
				templateContentStr = replaceGSTLoopBlock(replaceLoopBlock, loopBlock,  drbody,  templateContentStr);
				
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return templateContentStr;
	}
	
	private String replaceItemPhValues(String templateContentStr, String[] itemsSetPlaceHoldersArr, Map<String, String> userJSONSettingHM, 
			DRBody drbody , String[] paymentSetPlaceHoldersArr, String[] paymentChnagesSetPlaceHoldersArr,
			String[] DRRefItemLoopPlaceHolderArr) {
		try {
			//JSONObject jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
			
			Receipt receipt = drbody.getReceipt(); 
			if(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(itemsSetPlaceHoldersArr[0]) + itemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(itemsSetPlaceHoldersArr[8]));
				
				String loopBlock = Constants.STRING_NILL;
				if(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) != -1 && templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]) != -1) {
					loopBlock = templateContentStr.substring(templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[0]) + DRRefItemLoopPlaceHolderArr[0].length(),
							templateContentStr.indexOf(DRRefItemLoopPlaceHolderArr[5]));
					
				}
				
				/*String DCLoopBlock = Constants.STRING_NILL;
				if(templateContentStr.indexOf(DRDCPlaceHolderArr[0]) != -1 && templateContentStr.indexOf(DRDCPlaceHolderArr[3]) != -1) {
					DCLoopBlock = templateContentStr.substring(templateContentStr.indexOf(DRDCPlaceHolderArr[0]) + DRDCPlaceHolderArr[0].length(),
							templateContentStr.indexOf(DRDCPlaceHolderArr[3]));
					
				}*/
				logger.debug("Items HTML is :"+ loopBlockOne);
				String replaceLoopBlock = loopBlockOne;
				String replacedStr = replaceItemLoopBlock(replaceLoopBlock, loopBlock,  userJSONSettingHM, drbody);
				
				
				 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00"); 
				
				 Map<String, Double> mescilleneousAmtMap = new LinkedHashMap<String, Double>();
					
					String subTotalJsonObj = receipt.getSubtotal();//("Subtotal");
					if(subTotalJsonObj != null && !subTotalJsonObj.toString().trim().isEmpty() && !subTotalJsonObj.toString().trim().equals("0") ){
						
						Double subTotal = Double.parseDouble(subTotalJsonObj.toString().trim());
						mescilleneousAmtMap.put("Sub Total", subTotal);
						//amtValues.add(subTotal);
						
					}
					
					 String totTaxJsonObj = receipt.getTotalTax();//jsonReceiptObj.get("TotalTax");
					 if(totTaxJsonObj != null && !totTaxJsonObj.toString().trim().isEmpty() && !totTaxJsonObj.toString().trim().equals("0")){
						 
						 Double totTax = Double.parseDouble(totTaxJsonObj.toString().trim());
						// amtValues.add(totTax);
						 if(totTax>0){
							 mescilleneousAmtMap.put("Tax", totTax);
							 isTaxExists = true;
						 }

					 }
					 
					 
					 String shippingJsonObj = receipt.getShipping();//.get("Shipping");
					 if(shippingJsonObj != null && !shippingJsonObj.toString().trim().isEmpty() && !shippingJsonObj.toString().trim().equals("0")){
						 
						 Double shipping = Double.parseDouble(shippingJsonObj.toString().trim());
						 if(shipping>0) mescilleneousAmtMap.put("Shipping", shipping);
						// amtValues.add(shipping);

					 }
					 
					 String feeJsonObj = receipt.getFee();//jsonReceiptObj.get("Fee");
					 String helperKey="";
					 if(feeJsonObj != null && !feeJsonObj.toString().trim().isEmpty() && !feeJsonObj.toString().trim().equals("0")){
						 
						 String feeType = receipt.getFeeType();//jsonReceiptObj.get("FeeType");
						 Double fee = Double.parseDouble(feeJsonObj.toString().trim());
						/* mescilleneousAmtMap.put("Fee -"+((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL), fee); */
						 
						 helperKey = ((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee";
						 
						 /*mescilleneousAmtMap.put(((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee", fee); */
						 
						 if(fee>0) mescilleneousAmtMap.put(helperKey, fee);
						 //amtValues.add(fee);

					 }
					 
					 String discountJsonObj = receipt.getDiscount();//jsonReceiptObj.get("Discount");
					 if((discountJsonObj == null || discountJsonObj.isEmpty()) && receiptDisc>0) {
						 
						 discountJsonObj = receiptDisc+"";
					 }
					 if(discountJsonObj != null && !discountJsonObj.toString().trim().isEmpty() && !discountJsonObj.toString().trim().equals("0")){
						 
						 Double discount = Double.parseDouble(discountJsonObj.toString().trim());
						if(discount>0) mescilleneousAmtMap.put("Rcpt. Discount", discount); 
						 //amtValues.add(total);
						 
					 }
					 String totalJsonObj = receipt.getTotal();//jsonReceiptObj.get("Total");
					 if(totalJsonObj != null && !totalJsonObj.toString().trim().isEmpty() && !totalJsonObj.toString().trim().equals("0")){
						 
						 Double total = Double.parseDouble(totalJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total", total); 
						 //amtValues.add(total);

						 
					 }
					 
					 String totalSavingJsonObj = receipt.getTotalSavings();//jsonReceiptObj.get("TotalSavings");
					 if(totalSavingJsonObj != null && !totalSavingJsonObj.toString().trim().isEmpty() && !totalSavingJsonObj.toString().trim().equals("0")){
						 
						 Double totalSaving = Double.parseDouble(totalSavingJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total Savings", totalSaving); 
						 //amtValues.add(total);

					 }
					 
			
				 if(replacedStr == null)replacedStr= Constants.STRING_NILL;
				 String itemHolder = "";
					 Set<String> mescelleneousAmtKeySet = mescilleneousAmtMap.keySet();
					 
					 try {
						 
						 DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = 
								 (DigitalReceiptUserSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
						 
						 DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
						 boolean tax = (digitalReceiptUserSettings.isIncludeTax() && (mescilleneousAmtMap.get("Tax") != null));
						 boolean fee = (digitalReceiptUserSettings.isIncludeFee() && (mescilleneousAmtMap.get(helperKey) != null));
						 boolean shipping = (digitalReceiptUserSettings.isIncludeShipping() && (mescilleneousAmtMap.get("Shipping") != null));
						 boolean globalDisc = (digitalReceiptUserSettings.isIncludeGlobalDiscount() && (mescilleneousAmtMap.get("Rcpt. Discount") != null));
						 boolean totalAmount = (digitalReceiptUserSettings.isIncludeTotalAmount() && (mescilleneousAmtMap.get("Total") != null));
						 
						 itemHolder = replaceItemArrayNew(loopBlockOne, Constants.STRING_NILL, 0.0 ); 
						 replacedStr += itemHolder;
						 
						 
						 
						 for (String ItemPHTag : mescelleneousAmtKeySet) {
							 
							 if(ItemPHTag.equalsIgnoreCase("Tax")){
								 if(tax){ 
									 	itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 	replacedStr += itemHolder;
								     }
							 } else if(ItemPHTag.equalsIgnoreCase(helperKey)){ // i.e. for fee
								 if(fee){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 } else if(ItemPHTag.equalsIgnoreCase("Shipping")){
								 if(shipping){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }else if(ItemPHTag.equalsIgnoreCase("Rcpt. Discount")){
								 if(globalDisc){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 } else if(ItemPHTag.equalsIgnoreCase("Sub Total")){
								 if(tax || fee || shipping || globalDisc){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }else if(ItemPHTag.equalsIgnoreCase("Total")){
								 if(totalAmount){
									 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
									 replacedStr += itemHolder;
								 }
							 }
							/* else{
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) );
								 replacedStr += itemHolder;
							 }*/
							 
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("Exception>>>>>>>>>>>>>>"+e);
					}
					 
				
					 logger.info("loopBlockOne ==="+loopBlockOne+ "======"+replacedStr);
					templateContentStr = templateContentStr.replace(loopBlockOne, replacedStr);
					//logger.info("putti....Rama:"+templateContentStr);
					
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ===", e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ===", e);
		}
		return templateContentStr;
	}
	
		private String replaceTenderPhValues(String templateContentStr,  Set<String> DRTenderHashTagsSet, 
				PrismDRBody prismDRBody, String[] paymentSetPlaceHoldersArr, String[] paymentChnagesSetPlaceHoldersArr) throws BaseServiceException{
			
			if(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(paymentSetPlaceHoldersArr[10]) != -1) 
			{
			
				String paymentsLoopBlock = templateContentStr.substring(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) + paymentSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(paymentSetPlaceHoldersArr[10]));
				String changeLoopBlock = null;
				
				boolean ischange = templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[0]) != -1 && 
						 templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[4]) != -1;
				
				if(ischange){
					changeLoopBlock = templateContentStr.substring(templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[0]) + paymentChnagesSetPlaceHoldersArr[0].length(),
							templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[4]));
				}
				List<PrismDRTender> tenders = prismDRBody.getTenders();
				logger.debug("tenders ===="+tenders.size());
				
				for (PrismDRTender prismDRTender : tenders) {
					logger.debug("tender nAme ===="+prismDRTender.getTender_name());
				}
				String paymentParts = Constants.STRING_NILL;
				String changePart = Constants.STRING_NILL;
				
				for (PrismDRTender prismDRTender : tenders) {
					
					String tenderName = prismDRTender.getTender_name();
					
					 DecimalFormat deciFormat = new DecimalFormat("#,###,##0.00"); 
					// ADDED AFTER NEW DR SCHEMA 
					if (tenderName.equalsIgnoreCase("Cash"))  {
						logger.debug("inside ====" +  tenderName);
						  
			                if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().isEmpty()){
			                	
			                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			                	 if(cashVal > 0) {
			                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Cash", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
			                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
			                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			                		 
			                		 
			                	 }
					                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
					               if(change>0) {
					            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, "Cash", change, Constants.STRING_NILL);
					              
					                isChnageExists = true;
					               }
					                
			                }else{
			                	 Double cashVal = null;
			                	 if( (prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty() ) ){
			                		 cashVal =  Double.parseDouble(prismDRTender.getAmount().toString());
			                	 }
								   if(cashVal != null && cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Cash", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
									   
								   }
			                }
			                Double cashVal = 0.00;
			                if(prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty()){
			                	cashVal = Double.parseDouble(prismDRTender.getAmount().toString());
			                }
							  tenderedAmt +=  cashVal;
			            }
					 
					else if (tenderName.equalsIgnoreCase("Foreign Currency")) {
						logger.debug("inside ====" +  tenderName);
			            		String baseTaken = prismDRTender.getBase_taken() != null && !prismDRTender.getBase_taken().toString().isEmpty() ? prismDRTender.getBase_taken().toString() : Constants.STRING_NILL;
			            		if(ischange && prismDRTender.getTaken()!= null && !prismDRTender.getTaken().toString().isEmpty()){
			            			
			            			Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			            			
			            			//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			            			 if(cashVal > 0) {
			            				 
			            				 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Foreign Currency", 0.0,Constants.STRING_NILL, baseTaken, cashVal, 
			            						 (prismDRTender.getForeign_currency_name()!=null && prismDRTender.getForeign_currency_name().toString() != null ? 
			            								 prismDRTender.getForeign_currency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			            				 
			            			 }
			            			Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
			            			if(change > 0) {
			            				
			            				paymentParts += GetMergedPaymentChangePart(changeLoopBlock, "Foreign Currency", change, Constants.STRING_NILL);
			            				isChnageExists = true;
			            			}
			            		}else{
			            			Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            			 if(cashVal > 0) {
			            				 
			            				 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			            				 paymentParts += GetMergedPaymentPart(changePart,Constants.STRING_NILL,"Foreign Currency", cashVal,Constants.STRING_NILL, 
			            						 baseTaken,0.0,(prismDRTender.getForeign_currency_name()!=null && prismDRTender.getForeign_currency_name().toString() != null ? 
			            								 prismDRTender.getForeign_currency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			            			 }
			            		}
			            		Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		 tenderedAmt +=  cashVal;
			            	 isPaymentmescelleneous = true;
			            	  isBaseTaken = true;
			            	
			            }
					 
					else if (tenderName.equalsIgnoreCase("Check") ) {
						logger.debug("inside ====" +  tenderName+" prismDRTender.getCheck_number() "+prismDRTender.getCheck_number());
							 String checkNum = prismDRTender.getCheck_number().toString();
							 isPaymentNumberNTake = !checkNum.isEmpty();
				            	//String codVal = deciFormat.format(depositObj.getAmount()).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.getAmount()).toString() ;
				            	
				            	 if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
					                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,checkNum,"Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						                if(change > 0) {
						                	
						                	changePart += GetMergedPaymentChangePart(changeLoopBlock, "Check", change, Constants.STRING_NILL);
						                	isChnageExists = true;
						                }
				                }else{
				                	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,checkNum,"Check", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(prismDRTender.getCurrency_name()!=null &&
				                				 prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				            	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		 tenderedAmt +=  cashVal;
			            
			            	 
			            }
			            
					else if (prismDRTender.getTender_type() != null && prismDRTender.getTender_type().equals("2") ) {
						
						tenderName = "CreditCard";
						logger.debug("inside ====" +  tenderName);
			            		String ccNum = prismDRTender.getCard_number() != null ? prismDRTender.getCard_number().toString() : Constants.STRING_NILL;
			            		 
			            		String creditCardNum = Constants.STRING_NILL;
								if(!ccNum.isEmpty() && ccNum.length() >= 4 && !ccNum.contains("*")){
									creditCardNum =maskCardNumber(ccNum);
								}
								else if(!ccNum.isEmpty()){
									creditCardNum = ccNum.toString();
								}
								isPaymentNumberNTake = !creditCardNum.isEmpty();
								String type = prismDRTender.getCard_type_name() != null ?  prismDRTender.getCard_type_name().toString() : Constants.STRING_NILL;
			            		if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
									   
				                	 if(cashVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
				             				String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb, String cardTypeinArb )*/
				                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,creditCardNum,"Credit Card - "+type, 0.0, (prismDRTender.getAuthorization_code() != null && 
				                				 !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
				                						 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL,Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						               if(change > 0) {
						                changePart += GetMergedPaymentChangePart(changeLoopBlock, "Credit Card", change, Constants.STRING_NILL);
						                isChnageExists = true;
						                
						               }
				                }else{
				                	Double creditCardVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.getAmount()).toString() ;
				            	
				            		// Added after Payement.Number implementation
				                	 if(creditCardVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,	creditCardNum, "Credit Card - "+type,	creditCardVal, (prismDRTender.getAuthorization_code() != null && 
				                				 !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				                						 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                								 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL,Constants.STRING_NILL );
				                		 
				                	 }
					                }
			            		Double creditCardVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		 tenderedAmt +=  creditCardVal;
			            	isAuthTaken = true;
			            }	
			                
					else if (tenderName.equalsIgnoreCase("COD") ) {
						logger.debug("inside ====" +  tenderName);
			            	
			            	 if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
			                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
								   if(cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
									   paymentParts += GetMergedPaymentPart(paymentParts,Constants.STRING_NILL,"COD", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
											   (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
									   
								   }
					                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
					              if(change > 0) {
					            	  
					            	  changePart += GetMergedPaymentChangePart(changeLoopBlock, "COD", change, Constants.STRING_NILL);
					            	  isChnageExists = true;
					              }
			                }else{
			                	
			                	Double cashVal = null;
			                	 if( (prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty() ) ){
			                		 cashVal =  Double.parseDouble(prismDRTender.getAmount().toString());
			                	 }
								   if(cashVal != null && cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
									   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"COD", 
											   cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(prismDRTender.getCurrency_name()!=null && 
											   prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),
											   Constants.STRING_NILL ) ;
									   
								   }
			                }
			            	 
			            	 Double cashVal = 0.00;
			            	 if(prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty()) {
			            		 cashVal =  Double.parseDouble(prismDRTender.getAmount().toString());
			            	 }
			            	// (Double) (CODObj.getAmount() != null && CODObj.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(CODObj.getAmount().toString()));
			            	 tenderedAmt += cashVal;
			            }
			            
					else  if (tenderName.equalsIgnoreCase("Charge") ) {
						logger.debug("inside ====" +  tenderName);
			            	 if(ischange && prismDRTender.getTaken()!= null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
			                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			                	 if( cashVal > 0) {
			                		 
			                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
			                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Charge", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
			                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			                		 
			                	 }
					                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
					              if(change > 0) {
					                changePart += GetMergedPaymentChangePart(changeLoopBlock, "Charge", change, Constants.STRING_NILL);
					                isChnageExists = true;
					              }
			                }else{
			                	Double cashVal = null;
			                	 if( (prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty() ) ){
			                		 cashVal =  Double.parseDouble(prismDRTender.getAmount().toString());
			                	 }
			                	 if(cashVal != null && cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
									   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Charge", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ?
											   prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
								   }
								   
			                }
			            	 Double cashVal = 0.00;
			            	 if(prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty() ){
			            		 cashVal = Double.parseDouble(prismDRTender.getAmount().toString());
			            	 }
			            	 tenderedAmt += cashVal;
			            	
		           	   }
					else if (tenderName.equalsIgnoreCase("StoreCredit") )   {
						logger.debug("inside ====" +  tenderName);
			            		if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
				                	 if(cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
						                paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Store Credit", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal,
						                		(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						                if(change>0) {
							                changePart += GetMergedPaymentChangePart(changeLoopBlock, "Store Credit", change, Constants.STRING_NILL);
							                isChnageExists = true;
						                }
				                }else{
				                	 Double storeCreditVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()) );
				                	 if(storeCreditVal > 0) {
				                		 
				                		 //	String storeCreditVal = deciFormat.format(tempStoreCreditObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempStoreCreditObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Store Credit",
				                				 storeCreditVal,Constants.STRING_NILL,Constants.STRING_NILL,0.0, (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL  );
				                		 
				                	 }
				                }
			            		
			            		 Double storeCreditVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()) );
			            		 tenderedAmt += storeCreditVal;
			            	
			            }
			            
					else if (tenderName.equalsIgnoreCase("Deposit") ){
						logger.debug("inside ====" +  tenderName);
			            	//String codVal = deciFormat.format(depositObj.getAmount()).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.getAmount()).toString() ;
			            	
			            	 if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
			                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			                	 if(cashVal > 0) {
			                		 
			                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
			                		 paymentParts += GetMergedPaymentPart(paymentParts,Constants.STRING_NILL,"Deposit", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
			                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL) ;
			                		 
			                	 }
					                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
					               if(change > 0) {
					            	   
					            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, "Deposit", change, Constants.STRING_NILL);
					            	   isChnageExists = true;
					               }
			                }else{
			                	
			                	
			                	 Double cashVal = null;
			                	 if((prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty())) {
			                		 
			                		 cashVal = Double.parseDouble(prismDRTender.getAmount().toString());
			                	 }
								   if(cashVal != null && cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
									   paymentParts += GetMergedPaymentPart(paymentParts,Constants.STRING_NILL,"Deposit", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
											   prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
									   
								   }
			                }
			            	 
			            	 Double cashVal = 0.00;
			            	 if(prismDRTender.getAmount() != null && !prismDRTender.getAmount().toString().isEmpty()){
			            		 cashVal = Double.parseDouble(prismDRTender.getAmount().toString());
			            	 }
			            	 tenderedAmt += cashVal;
			            	
			            }
			            
					else  if (tenderName.equalsIgnoreCase("Payments") ) {
								
						logger.debug("inside ====" +  tenderName);
			            		if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
										   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Payments", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
												   (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
														   prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						               if(change > 0) {
						                changePart += GetMergedPaymentChangePart(changeLoopBlock, "Payments", change, Constants.STRING_NILL);
						                isChnageExists = true;
						               }
				                }else{
				                	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,Constants.STRING_NILL,"Payments", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
				                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
			            		
			            		Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		tenderedAmt += cashVal;
			                
			            }
			            
					else if (tenderName.equalsIgnoreCase("Gift Certificate") ) {
						logger.debug("inside ====" +  tenderName + " " + prismDRTender.getCertificate_number());
			            		String giftNum = Constants.STRING_NILL;
			            		if(prismDRTender.getCertificate_number() != null ){
			            			
			            			giftNum = prismDRTender.getCertificate_number();
			            			logger.debug("==giftNum==="+giftNum);
			            			isPaymentNumberNTake = !giftNum.isEmpty();
			            			
			            		}
			            		
			            		 if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
					                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
										   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,giftNum,"Gift Certificate", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
												   (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
														   prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						               if(change > 0) {
						            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, "Gift Certificate", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						               }
				                }else{
				                	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,giftNum,"Gift Certificate", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
				                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
			            		 
			            		 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		 tenderedAmt += cashVal;
			            	isAuthTaken = true;
			            }
			            
					else if (tenderName.equalsIgnoreCase("Gift Card") ) {
			            	
						logger.debug("inside ====" +  tenderName);
								
			            		//String giftCrdVal = deciFormat.format(tempGiftCardObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.getAmount()).toString();
			            		

			            		 // Added after Payement.Number implementation
			            		 String gcNum = prismDRTender.getCard_number().toString();
			            		String giftCardNum = Constants.STRING_NILL;
								if(!gcNum.isEmpty() && gcNum.length() >= 4 ){
									giftCardNum =giftMaskCardNumber(gcNum);
								}
								else if(!gcNum.isEmpty()){
									giftCardNum =gcNum.toString();
								}
								
								isPaymentNumberNTake = !giftCardNum.isEmpty();
								
								if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
									   
									   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
				                	/* paymentParts += GetMergedPaymentPart(loopBlockTwo,creditCardNum,"Credit Card - "+type, Constants.STRING_NILL, (tempObj.get("Authorization") != null && 
				            				 !tempObj.get("Authorization").toString().isEmpty()) ? tempObj.get("Authorization").toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
					                		(tempObj.get("CurrencyName")!=null && tempObj.get("CurrencyName").toString() != null ? tempObj.get("CurrencyName").toString():Constants.STRING_NILL),Constants.TenderTypeArbMap.get("Credit Card"),Constants.CREDITCARDTYPEARBMap.get(type.toUpperCase()) ) ;
					                 */ 
				                	 if(cashVal > 0) {
				                		 
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,giftCardNum,"Gift Card", 0.0, (prismDRTender.getAuthorization_code() != null && 
				                				 !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
				                						 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                								 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						                if(change > 0) {
							                changePart += GetMergedPaymentChangePart(changeLoopBlock, "Gift Card", change, Constants.STRING_NILL);
							                isChnageExists = true;
						                }
				                }else{
				                	 Double debitVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.getAmount()).toString() ;
				            	
				            		// Added after Payement.Number implementation
				                	 if(debitVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,	giftCardNum, "Gift Card",	debitVal, (prismDRTender.getAuthorization_code() != null && 
				                				 !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				                						 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL );
				                		 
				                	 }
					                }
								Double debitVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
								tenderedAmt += debitVal;
			            	
			            		isAuthTaken = true;
			            }
			            
					else if (tenderName.equalsIgnoreCase("Debit Card") )    {
			            	
						logger.debug("inside ====" +  tenderName);
			            		String debitNum = prismDRTender.getCard_number().toString();
			            		
			            		String debitCardNum = Constants.STRING_NILL;
			            		if(!debitNum.isEmpty() && debitNum.length() >= 4){
			            			debitCardNum =maskCardNumber(debitNum);	
			            		}else if(!debitNum.isEmpty()){
			            			debitCardNum=debitNum.toString();
			            		}
			            		
			            		isPaymentNumberNTake = !debitCardNum.isEmpty();
			            		if(ischange && prismDRTender.getTaken()!= null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.getAmount()).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.getAmount()).toString();
										   paymentParts += GetMergedPaymentPart(paymentsLoopBlock,debitNum,"Debit Card", 0.0, (prismDRTender.getAuthorization_code() != null && 
												   !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
														   (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
																   prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						               if(change > 0) {
						            	   
						            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, "Debit Card", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						               }
				                }else{
				                	 Double debitVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
				            	
				            		// Added after Payement.Number implementation
				            		 if(debitVal > 0) {
				            			 
				            			 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				            			 
				            			 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,	debitCardNum, "Debit Card",	debitVal, (prismDRTender.getAuthorization_code() != null && 
				            					 !prismDRTender.getAuthorization_code().toString().isEmpty()) ? prismDRTender.getAuthorization_code().toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				            							 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL );
				            			 
				            		 }
					                }
			            		Double debitVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		tenderedAmt += debitVal;
			            		
			            	isAuthTaken = true; 
							}
			            // Added another 3 tender types(rarely used)
			            
					else if (tenderName.equalsIgnoreCase("TravelerCheck")) {
						logger.debug("inside ====" +  tenderName);
			            		String travCheckNum = prismDRTender.getCheck_number() != null ? prismDRTender.getCheck_number().toString() : Constants.STRING_NILL;
			            		isPaymentNumberNTake = !travCheckNum.isEmpty();
			            		if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
			            			
			            			Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			            			if(cashVal > 0) {
			            				
			            				//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			            				paymentParts += GetMergedPaymentPart(paymentsLoopBlock,travCheckNum,"Traveler Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
			            						(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			            				
			            			}
			            			Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
			            			if(change > 0) {
			            				changePart += GetMergedPaymentChangePart(changeLoopBlock, "Traveler Check", change, Constants.STRING_NILL);
			            				isChnageExists = true;
			            			}
			            		}else{
			            			Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            			if(cashVal > 0) {
			            				
			            				//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			            				paymentParts += GetMergedPaymentPart(paymentsLoopBlock,travCheckNum,"Traveler Check", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
			            						(prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
			            								prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL) ;
			            			}
			            		}
			            		Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            		tenderedAmt += cashVal;
			            		
			            	
			            }
			            
			            
					else  if (tenderName.equalsIgnoreCase("FCCheck")) {
						logger.debug("inside ====" +  tenderName);
			            		String fcCheckNum = prismDRTender.getCheck_number().toString();
			            		fcCheckNum = fcCheckNum.replace("Not all Data available; ", Constants.STRING_NILL);
			            		isPaymentNumberNTake = !fcCheckNum.isEmpty();
				            	if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,fcCheckNum,"FC Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						               Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
						               if(change > 0) {
						            	   
						            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, "FC Check", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						            	   
						               }
				                }else{
				                	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,fcCheckNum,"FC Check", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
				                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
				                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				            	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
				            	tenderedAmt += cashVal;
			            	
			            }else{//custom tender
			            	String redemptionTenderName =  user.getRedeemTenderDispLabel() != null && !user.getRedeemTenderDispLabel().isEmpty() ? user.getRedeemTenderDispLabel() : prismDRTender.getTender_name();
									 
									 
							
			            	String fcCheckNum = prismDRTender.getCard_number() != null ? prismDRTender.getCard_number().toString():"";
			            	if(ischange && prismDRTender.getTaken() != null && !prismDRTender.getTaken().toString().isEmpty()){
			                	 Double cashVal = (Double) (prismDRTender.getTaken().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getTaken().toString()));
			                	 if(cashVal > 0) {
			                		 
			                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,fcCheckNum,redemptionTenderName, 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
			                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
			                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			                		 
			                	 }
					               Double change = (Double) (prismDRTender.getGiven().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getGiven().toString()));
					               if(change > 0) {
					            	   
					            	   changePart += GetMergedPaymentChangePart(changeLoopBlock, redemptionTenderName, change, Constants.STRING_NILL);
					            	   isChnageExists = true;
					            	   
					               }
			                }else{
			                	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			                	 if(cashVal > 0) {
			                		 
			                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
			                		 paymentParts += GetMergedPaymentPart(paymentsLoopBlock,fcCheckNum,redemptionTenderName, cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
			                				 (prismDRTender.getCurrency_name()!=null && prismDRTender.getCurrency_name().toString() != null ? 
			                						 prismDRTender.getCurrency_name().toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
			                	 }
			                }
			            	 Double cashVal = (Double) (prismDRTender.getAmount().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(prismDRTender.getAmount().toString()));
			            	tenderedAmt += cashVal;
			            	
			            }
					
					
				
					
				}//for
				if(paymentParts != null) {
					templateContentStr = templateContentStr.replace(paymentsLoopBlock, paymentParts);
				}
				if(changePart != null && !changePart.isEmpty() && ischange) {
					templateContentStr = templateContentStr.replace(changeLoopBlock, changePart);
				}
				
			}//if
			return templateContentStr;
			
		}
	
		private String replaceReceiptPhValues(String templateContentStr, String[] itemsSetPlaceHoldersArr, Map<String, String> userJSONSettingHM, 
				DRBody DRBody, String[] paymentSetPlaceHoldersArr, String[] paymentChnagesSetPlaceHoldersArr,
				String[] DRRefItemLoopPlaceHolderArr) throws BaseServiceException{
			
			logger.debug("==entered to replace payments=="+paymentSetPlaceHoldersArr[0] +" "+paymentSetPlaceHoldersArr[10]);
			try {
				Receipt receipt = DRBody.getReceipt();
				
				
				 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
				 
				if(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(paymentSetPlaceHoldersArr[10]) != -1) 
				{
					logger.debug("==entered to replace payments==1");
					String loopBlockTwo = templateContentStr.substring(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) + paymentSetPlaceHoldersArr[0].length(),
							templateContentStr.indexOf(paymentSetPlaceHoldersArr[10]));
					String loopBlockThree = null;
					boolean ischange = templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[0]) != -1 && 
							templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[4]) != -1;
					if(ischange) {
						loopBlockThree = templateContentStr.substring(templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[0]) + paymentChnagesSetPlaceHoldersArr[0].length(),
								templateContentStr.indexOf(paymentChnagesSetPlaceHoldersArr[4]));
						
						
					}
					String paymentParts =Constants.STRING_NILL;
					String changeParts = Constants.STRING_NILL;
					 DecimalFormat deciFormat = new DecimalFormat("#,###,##0.00"); 
					// ADDED AFTER NEW DR SCHEMA 
					 
					 Cash cashObj = DRBody.getCash();
					 List<FC> FC = DRBody.getFC();
					 List<Check> Check = DRBody.getCheck();
					 List<CreditCard> CreditCard = DRBody.getCreditCard();
					 COD COD = DRBody.getCOD();
					 Charge Charge = DRBody.getCharge();
					 List<StoreCredit> StoreCredit = DRBody.getStoreCredit();
					 Deposit Deposit = DRBody.getDeposit();
					 List<Payments> Payements = DRBody.getPayments();
					 List<Gift> Gifts = DRBody.getGift();
					 List<GiftCard> GiftCrads = DRBody.getGiftCard();
					 List<FCCheck> FCChecks = DRBody.getFCCheck();
					 List<DebitCard> Debitcards = DRBody.getDebitCard();
					 List<TravellerCheck> TC = DRBody.getTravelerCheck();
					 List<CustomTender> customTenders = DRBody.getCustomTender();
					 String redemptionTenderName =  user.getRedeemTender() != null && !user.getRedeemTender().isEmpty() && user.getRedeemTenderDispLabel() != null ? user.getRedeemTenderDispLabel() : null;
					if (cashObj != null)  {
						logger.debug("==entered to replace payments==3");
						
						String Taken = cashObj.getTaken();
						String given = cashObj.getGiven();
						String amount = cashObj.getAmount();
						  String CurrencyName = cashObj.getCurrencyName();
						  
				            if(ischange && Taken != null && !Taken.toString().isEmpty()){
				            	
				            	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				            	 if(cashVal > 0) {
				            		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Cash", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				            				 (CurrencyName!=null && !CurrencyName.toString().isEmpty()  ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				            		 
				            		 
				            	 }
					               Double change = (Double) (given.toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(given.toString()));
					               if(change>0) {
					                changeParts += GetMergedPaymentChangePart(loopBlockThree, "Cash", change, Constants.STRING_NILL);
					              
					                isChnageExists = true;
					               }
					                
				            }else{
				            	 Double cashVal = null;
				            	 if( (amount != null && !amount.toString().isEmpty() ) ){
				            		 cashVal =  Double.parseDouble(amount.toString());
				            	 }
								   if(cashVal != null && cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Cash", 
											   cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
											   (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
									   
								   }
				            }
				            Double cashVal = 0.00;
				            if(amount != null && !amount.toString().isEmpty()){
				            	cashVal = Double.parseDouble(amount.toString());
				            }
							  tenderedAmt +=  cashVal;
				        }
					 
					 if (FC != null) {
				        	for (FC FCObj : FC) {
				        		String Take = FCObj.getTake();
				        		String baseTaken = FCObj.getBaseTaken() != null && 
				        				!FCObj.getBaseTaken().toString().isEmpty() ? FCObj.getBaseTaken().toString() : Constants.STRING_NILL;
								String FCName =  FCObj.getFCName();
								String Amount = FCObj.getAmount();
								
				        		if(ischange && Take != null && !Take.toString().isEmpty()){
				        			
				        			Double cashVal = (Double) (Take.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Take.toString()));
				        			
				        			//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				        			 if(cashVal > 0) {
				        				 
				        				 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Foreign Currency", 0.0,Constants.STRING_NILL, baseTaken, cashVal, 
				        						 (FCName!=null && FCName.toString() != null ? FCName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				        				 
				        			 }
				        			Double change = (Double) (FCObj.getGive().toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(FCObj.getGive().toString()));
				        			if(change > 0) {
				        				
				        				changeParts += GetMergedPaymentChangePart(loopBlockThree, "Foreign Currency", change, Constants.STRING_NILL);
				        				isChnageExists = true;
				        			}
				        		}else{
				        			Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        			 if(cashVal > 0) {
				        				 
				        				 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				        				 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Foreign Currency", cashVal,Constants.STRING_NILL, 
				        						 baseTaken,0.0,(FCName!=null && FCName.toString() != null ? FCName.toString():Constants.STRING_NILL),Constants.STRING_NILL) ;
				        			 }
				        		}
				        		Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		 tenderedAmt +=  cashVal;
				        	}
				        	 isPaymentmescelleneous = true;
				        	  isBaseTaken = true;
				        	
				        }
					 
					 if (Check != null) {
				        	
						 for (Check checkObj : Check) {
							 String checkNum = checkObj.getNumber().toString();
				        	 String Taken = checkObj.getTaken();
				        	 String CurrencyName = checkObj.getCurrencyName();
				        	 String Given = checkObj.getGiven();
				        	 String Amount = checkObj.getAmount();
				            	//String codVal = deciFormat.format(depositObj.get("Amount")).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.get("Amount")).toString() ;
				            	
				            	 if(ischange && Taken != null && !Taken.toString().isEmpty()){
					                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,checkNum,"Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				                				 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						                if(change > 0) {
						                	
						                	changeParts += GetMergedPaymentChangePart(loopBlockThree, "Check", change, Constants.STRING_NILL);
						                	isChnageExists = true;
						                }
				                }else{
				                	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,checkNum,"Check", cashVal,Constants.STRING_NILL, 
				                				 Constants.STRING_NILL,0.0,(CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				            	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		 tenderedAmt +=  cashVal;
						 }
				        
				        	 
				        }
				        
					  if (CreditCard != null) {
				        	
				        	for (CreditCard CCObj : CreditCard) {
								
				        		String ccNum = CCObj.getNumber()!= null ? CCObj.getNumber().toString() : Constants.STRING_NILL;
				        		 
				        		String creditCardNum = Constants.STRING_NILL;
								if(!ccNum.isEmpty() && ccNum.length() >= 4 && !ccNum.contains("*")){
									creditCardNum =maskCardNumber(ccNum);
								}
								else if(!ccNum.isEmpty()){
									creditCardNum = ccNum.toString();
								}
								isPaymentNumberNTake = !creditCardNum.isEmpty();
								String type = CCObj.getType() != null ?  CCObj.getType().toString() : Constants.STRING_NILL;
								String tenderName = "Credit Card - "+type;
								if(!type.isEmpty() && redemptionTenderName != null && !redemptionTenderName.isEmpty() && 
										user.getRedeemTender().startsWith("Credit Card:")){
									if(type.equalsIgnoreCase(user.getRedeemTender().split(":")[1])){
										tenderName = redemptionTenderName;
									}
									
									
								}
								String Taken = CCObj.getTaken();
								String Auth = CCObj.getAuthorization();
								String CurrencyName = CCObj.getCurrencyName();
								String Given = CCObj.getGiven();
								String Amount = CCObj.getAmount();
				        		if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
									   
				                	 if(cashVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
				             				String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb, String cardTypeinArb )*/
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,creditCardNum,tenderName, 0.0, (Auth != null && 
				                				 !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
				                						 (CurrencyName!=null && !CurrencyName.toString().isEmpty() ? CurrencyName.toString():Constants.STRING_NILL),
				                						 Constants.STRING_NILL,Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change > 0) {
						                changeParts += GetMergedPaymentChangePart(loopBlockThree, tenderName, change, Constants.STRING_NILL);
						                isChnageExists = true;
						                
						               }
				                }else{
				                	Double creditCardVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
				            	
				            		// Added after Payement.Number implementation
				                	 if(creditCardVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,	creditCardNum, tenderName,	creditCardVal, (Auth != null && 
				                				 !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				                						 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL,Constants.STRING_NILL );
				                		 
				                	 }
					                }
				        		Double creditCardVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		 tenderedAmt +=  creditCardVal;
				        		}
				        	isAuthTaken = true;
				        }	
				            
					  if (COD != null) {

				        	//String codVal = deciFormat.format(CODObj.get("Amount")).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(CODObj.get("Amount")).toString() ;
				        	String Taken = COD.getTaken();
				        	String CurrencyName = COD.getCurrencyName();
				        	String Given = COD.getGiven();
				        	String Amount = COD.getAmount();
				        	String tenderName = "COD";
				        	if(redemptionTenderName != null && !redemptionTenderName.isEmpty() && user.getRedeemTender().equalsIgnoreCase("COD")){
				        		tenderName=redemptionTenderName;
							}
				        	 if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				            	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
								   if(cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,tenderName, 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
											   (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
									   
								   }
					                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
					              if(change > 0) {
					            	  
					            	  changeParts += GetMergedPaymentChangePart(loopBlockThree, tenderName, change, Constants.STRING_NILL);
					            	  isChnageExists = true;
					              }
				            }else{
				            	
				            	Double cashVal = null;
				            	 if( (Amount != null && !Amount.toString().isEmpty() ) ){
				            		 cashVal =  Double.parseDouble(Amount.toString());
				            	 }
								   if(cashVal != null && cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,tenderName, 
											   cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,CurrencyName!=null &&
											   !CurrencyName.toString().isEmpty()  ? CurrencyName.toString():Constants.STRING_NILL,Constants.STRING_NILL ) ;
									   
								   }
				            }
				        	 
				        	 Double cashVal = 0.00;
				        	 if(Amount != null && !Amount.toString().isEmpty()) {
				        		 cashVal =  Double.parseDouble(Amount.toString());
				        	 }
				        	// (Double) (CODObj.get("Amount") != null && CODObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(CODObj.get("Amount").toString()));
				        	 tenderedAmt += cashVal;
				        }
				        
				        if (Charge != null) {
				        	String Taken = Charge.getTaken();
				        	String Given = Charge.getGiven();
				        	String CurrencyName = Charge.getCurrencyName();
				        	String Amount = Charge.getAmount();
				        	
				        	 if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				            	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				            	 if( cashVal > 0) {
				            		 
				            		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Charge", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				            				 (CurrencyName!=null && !CurrencyName.toString().isEmpty() ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				            		 
				            	 }
					                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
					              if(change > 0) {
					                changeParts += GetMergedPaymentChangePart(loopBlockThree, "Charge", change, Constants.STRING_NILL);
					                isChnageExists = true;
					              }
				            }else{
				            	Double cashVal = null;
				            	 if( (Amount != null && !Amount.toString().isEmpty() ) ){
				            		 cashVal =  Double.parseDouble(Amount.toString());
				            	 }
				            	 if(cashVal != null && cashVal > 0) {
									   
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Charge", cashVal,Constants.STRING_NILL, 
											   Constants.STRING_NILL,0.0,(CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
								   }
								   
				            }
				        	 Double cashVal = 0.00;
				        	 if(Amount != null && !Amount.toString().isEmpty() ){
				        		 cashVal = Double.parseDouble(Amount.toString());
				        	 }
				        	 tenderedAmt += cashVal;
				        	
				   	   }
				        if (StoreCredit != null)   {
				        	//JSONArray storeCreditCardArr = (JSONArray)jsonBodyObj.get("StoreCredit");
				        	
				        	for (StoreCredit StoreCreditObject : StoreCredit) {
								
				        		String Taken = StoreCreditObject.getTaken();
				        		String Given = StoreCreditObject.getGiven();
				        		String CurrencyName = StoreCreditObject.getCurrencyName();
				        		String Amount = StoreCreditObject.getAmount();
				        		
				        		if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				                	 if(cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
						                paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Store Credit", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal,
						                		(CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						                if(change>0) {
							                changeParts += GetMergedPaymentChangePart(loopBlockThree, "Store Credit", change, Constants.STRING_NILL);
							                isChnageExists = true;
						                }
				                }else{
				                	 Double storeCreditVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()) );
				                	 if(storeCreditVal > 0) {
				                		 
				                		 //	String storeCreditVal = deciFormat.format(tempStoreCreditObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempStoreCreditObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Store Credit",
				                				 storeCreditVal,Constants.STRING_NILL,Constants.STRING_NILL,0.0, 
				                				 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL  );
				                		 
				                	 }
				                }
				        		
				        		 Double storeCreditVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()) );
				        		 tenderedAmt += storeCreditVal;
							}
				        	
				        }
				        
				        if (Deposit != null){
				        	//JSONObject depositObj = (JSONObject)jsonBodyObj.get("Deposit");
				        	//String codVal = deciFormat.format(depositObj.get("Amount")).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.get("Amount")).toString() ;
				        	String Taken = Deposit.getTaken();
				        	String Given = Deposit.getGiven();
				        	String CurrencyName = Deposit.getCurrencyName();
				        	String Amount = Deposit.getAmount();
				        	 if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				            	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				            	 if(cashVal > 0) {
				            		 
				            		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Deposit", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				            				 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),
				            				 Constants.STRING_NILL ) ;
				            		 
				            	 }
					                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
					               if(change > 0) {
					            	   
					            	   changeParts += GetMergedPaymentChangePart(loopBlockThree, "Deposit", change, Constants.STRING_NILL);
					            	   isChnageExists = true;
					               }
				            }else{
				            	
				            	
				            	 Double cashVal = null;
				            	 if((Amount != null && !Amount.toString().isEmpty())) {
				            		 
				            		 cashVal = Double.parseDouble(Amount.toString());
				            	 }
								   if(cashVal != null && cashVal > 0) {
									   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
									   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Deposit", cashVal,Constants.STRING_NILL, 
											   Constants.STRING_NILL,0.0,CurrencyName!=null && CurrencyName.toString() != null ? 
													   CurrencyName.toString():Constants.STRING_NILL,Constants.STRING_NILL) ;
									   
								   }
				            }
				        	 
				        	 Double cashVal = 0.00;
				        	 if(Amount != null && !Amount.toString().isEmpty()){
				        		 cashVal = Double.parseDouble(Amount.toString());
				        	 }
				        	 tenderedAmt += cashVal;
				        	
				        }
				        
				        if (Payements!= null) {
				        	
				        	
				        	for (Payments Payment : Payements) {
								
				        		//JSONObject tempPaymentObj = (JSONObject)object;
				        		String Taken = Payment.getTaken();
				        		String Given = Payment.getGiven();
				        		String currencyName = Payment.getCurrencyName();
				        		String Amount = Payment.getAmount();
				        		
				        		if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
										   paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Payments", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
												   (currencyName!=null && currencyName.toString() != null ? currencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change > 0) {
						                changeParts += GetMergedPaymentChangePart(loopBlockThree, "Payments", change, Constants.STRING_NILL);
						                isChnageExists = true;
						               }
				                }else{
				                	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,Constants.STRING_NILL,"Payments", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(currencyName!=null && 
				                				 currencyName.toString() != null ? currencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL) ;
				                	 }
				                }
				        		
				        		Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : 
				        			Double.parseDouble(Amount.toString()));
				        		tenderedAmt += cashVal;
				        	}
				            
				        }
				        
				        if (Gifts != null) {
				        	
				        	
				        	for (Gift Gift : Gifts) {
								
				        		String Taken = Gift.getTaken();
				        		String Given = Gift.getGiven();
				        		String CurrencyName = Gift.getCurrencyName();
				        		String Amount =Gift.getAmount();
				        		
				        		String giftNum = Constants.STRING_NILL;
				        		if(Gift.getGiftNum() != null ) giftNum = Gift.getGiftNum() ;
				        		else if(Gift.getNumber() != null ) giftNum = Gift.getNumber();
				        		else if(Gift.getGiftCardNo() != null ) giftNum = Gift.getGiftCardNo();
				        		
				        		 isPaymentNumberNTake = !giftNum.isEmpty();
				        		 if(ischange && Taken != null && !Taken.toString().isEmpty()){
					                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
										   paymentParts += GetMergedPaymentPart(loopBlockTwo,giftNum,"Gift Certificate", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
												   (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change > 0) {
						            	   changeParts += GetMergedPaymentChangePart(loopBlockThree, "Gift Certificate", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						               }
				                }else{
				                	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,giftNum,"Gift Certificate", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,
				                				 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				        		 
				        		 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		 tenderedAmt += cashVal;
							}
				        	isAuthTaken = true;
				        }
				        
				        if (GiftCrads != null) {
				        	
				        	
				        		for (GiftCard GiftCard : GiftCrads) {
								
				        		//String giftCrdVal = deciFormat.format(tempGiftCardObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.get("Amount")).toString();
				        		

				        		 // Added after Payement.Number implementation
				        		 String gcNum = GiftCard.getNumber();
				        		String giftCardNum = Constants.STRING_NILL;
								if(!gcNum.isEmpty() && gcNum.length() >= 4 ){
									giftCardNum =giftMaskCardNumber(gcNum);
								}
								else if(!gcNum.isEmpty()){
									giftCardNum =gcNum.toString();
								}
								
								String Taken =GiftCard.getTaken();
								String Given = GiftCard.getGiven();
								String CurrencyName = GiftCard.getCurrencyName();
								String Amount = GiftCard.getAmount();
								String Auth = GiftCard.getAuthorization();
								
								isPaymentNumberNTake = !giftCardNum.isEmpty();
								
								if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
									   
									   
				                	 if(cashVal > 0) {
				                		 
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,giftCardNum,"Gift Card", 0.0, (Auth != null && 
				                				 !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
				                						 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL) ;
				                		 
				                	 }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						                if(change > 0) {
							                changeParts += GetMergedPaymentChangePart(loopBlockThree, "Gift Card", change, Constants.STRING_NILL);
							                isChnageExists = true;
						                }
				                }else{
				                	 Double debitVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
				            	
				            		// Added after Payement.Number implementation
				                	 if(debitVal > 0) {
				                		 
				                		 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,	giftCardNum, "Gift Card",	debitVal, (Auth != null && 
				                				 !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				                						 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL );
				                		 
				                	 }
					                }
								Double debitVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
								tenderedAmt += debitVal;
							}
				        	
				        		isAuthTaken = true;
				        }
				        
				        if (Debitcards != null)    {
				        	
				        	for (DebitCard DebitCard : Debitcards) {
								
				        		String debitNum = DebitCard.getNumber();
				        		
				        		String debitCardNum = Constants.STRING_NILL;
				        		if(!debitNum.isEmpty() && debitNum.length() >= 4){
				        			debitCardNum =maskCardNumber(debitNum);	
				        		}else if(!debitNum.isEmpty()){
				        			debitCardNum=debitNum.toString();
				        		}
				        		
				        		isPaymentNumberNTake = !debitCardNum.isEmpty();
				        		String Taken =DebitCard.getTaken();
								String Given = DebitCard.getGiven();
								String CurrencyName = DebitCard.getCurrencyName();
								String Amount = DebitCard.getAmount();
								String Auth = DebitCard.getAuthorization();
				        		
				        		if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
									   if(cashVal > 0) {
										   
										   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
										   paymentParts += GetMergedPaymentPart(loopBlockTwo,debitNum,"Debit Card", 0.0, (Auth != null && 
												   !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL,Constants.STRING_NILL, cashVal,
														   (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
										   
									   }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change > 0) {
						            	   
						            	   changeParts += GetMergedPaymentChangePart(loopBlockThree, "Debit Card", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						               }
				                }else{
				                	 Double debitVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
				            	
				            		// Added after Payement.Number implementation
				            		 if(debitVal > 0) {
				            			 
				            			 /*GetMergedPaymentPart(String paymentPart, String number, String type, String Amount, 
											String auth, String BaseTaken, String taken, String currencyName, String tenderTypainArb )*/
				            			 paymentParts += GetMergedPaymentPart(loopBlockTwo,	debitCardNum, "Debit Card",	debitVal, (Auth != null && 
				            					 !Auth.toString().isEmpty()) ? Auth.toString() : Constants.STRING_NILL ,Constants.STRING_NILL,0.0,
				            							 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL );
				            			 
				            		 }
					                }
				        		Double debitVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		tenderedAmt += debitVal;
				        		
				        		}
				        	isAuthTaken = true; 
							}
				        // Added another 3 tender types(rarely used)
				        
				        if (TC != null) {
				        	for (TravellerCheck TravellerCheck : TC) {
				        		String travCheckNum = TravellerCheck.getNumber() != null ? TravellerCheck.getNumber().toString() : Constants.STRING_NILL;
				        		isPaymentNumberNTake = !travCheckNum.isEmpty();
				        		
				        		String Taken =TravellerCheck.getTaken();
								String Given = TravellerCheck.getGiven();
								String CurrencyName = TravellerCheck.getCurrencyName();
								String Amount = TravellerCheck.getAmount();
								//String Auth = TravellerCheck.getAmount()horization();
				        		if(ischange && Taken != null && !Taken.toString().isEmpty()){
				        			
				        			Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				        			if(cashVal > 0) {
				        				
				        				//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				        				paymentParts += GetMergedPaymentPart(loopBlockTwo,travCheckNum,"Traveler Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				        						(CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				        				
				        			}
				        			Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
				        			if(change > 0) {
				        				changeParts += GetMergedPaymentChangePart(loopBlockThree, "Traveler Check", change, Constants.STRING_NILL);
				        				isChnageExists = true;
				        			}
				        		}else{
				        			Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        			if(cashVal > 0) {
				        				
				        				//String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				        				paymentParts += GetMergedPaymentPart(loopBlockTwo,travCheckNum,"Traveler Check", cashVal,
				        						Constants.STRING_NILL, Constants.STRING_NILL,0.0,(CurrencyName!=null && 
				        						CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				        			}
				        		}
				        		Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				        		tenderedAmt += cashVal;
				        		
							}
				        	
				        }
				        
				        
				        if (FCChecks != null) {

				        	for (FCCheck FCCheck : FCChecks) {
				        		String fcCheckNum = FCCheck.getNumber();
				        		fcCheckNum = fcCheckNum.replace("Not all Data available; ", Constants.STRING_NILL);
				        		isPaymentNumberNTake = !fcCheckNum.isEmpty();
				        		String Taken =FCCheck.getTaken();
								String Given = FCCheck.getGiven();
								String CurrencyName = FCCheck.getCurrencyName();
								String Amount = FCCheck.getAmount();
				        		
				            	if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,fcCheckNum,"FC Check", 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				                				 (CurrencyName!=null && CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change > 0) {
						            	   
						            	   changeParts += GetMergedPaymentChangePart(loopBlockThree, "FC Check", change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						            	   
						               }
				                }else{
				                	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				                	 if(cashVal > 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,fcCheckNum,"FC Check", cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(CurrencyName!=null && 
				                				 CurrencyName.toString() != null ? CurrencyName.toString():Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				            	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				            	tenderedAmt += cashVal;
							}
				        	
				        }
				        
				        if(customTenders !=null && !customTenders.isEmpty()){
				        	logger.debug("custom tender processing");
				        	for (CustomTender customTender : customTenders) {
								

				        		isPaymentNumberNTake = !customTenders.isEmpty();
				        		String Taken =customTender.getTaken();
								String Given = customTender.getGiven();
								String Amount = customTender.getAmount();
								String type = customTender.getType();
				        		String number = customTender.getNumber();//APP-4195
				            	if(ischange && Taken != null && !Taken.toString().isEmpty()){
				                	
				                	 Double cashVal = (Double) (Taken.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Taken.toString()));
				                	 if(cashVal != 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,number,(type != null && !type.isEmpty() ? type : "Custom"), 0.0,Constants.STRING_NILL, Constants.STRING_NILL, cashVal, 
				                				 (Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                		 
				                	 }
						                Double change = (Double) (Given != null && Given.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Given.toString()));
						               if(change != 0) {
						            	   
						            	   changeParts += GetMergedPaymentChangePart(loopBlockThree, (type != null && !type.isEmpty() ? type : "Custom"), change, Constants.STRING_NILL);
						            	   isChnageExists = true;
						            	   
						               }
				                }else{
				                	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				                	 if(cashVal != 0) {
				                		 
				                		 //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
				                		 paymentParts += GetMergedPaymentPart(loopBlockTwo,number,(type != null && !type.isEmpty() ? type : "Custom"), 
				                				 cashVal,Constants.STRING_NILL, Constants.STRING_NILL,0.0,(Constants.STRING_NILL),Constants.STRING_NILL ) ;
				                	 }
				                }
				            	 Double cashVal = (Double) (Amount.toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(Amount.toString()));
				            	tenderedAmt += cashVal;
							
				        		
				        		
							}//for
				        }
					
					if(paymentParts != null) {
						logger.debug("==exists payments==");
						templateContentStr = templateContentStr.replace(loopBlockTwo, paymentParts);
					}
					if(changeParts != null && !changeParts.isEmpty() && ischange) {
						templateContentStr = templateContentStr.replace(loopBlockThree, changeParts);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error =====", e);
			}
			logger.debug("==entered to replace payments==end");
			return templateContentStr;
		}
		// Added for any card Masking
				public static String maskCardNumber(String ccnum){
				        try {
				        	logger.debug("Cardmask ==="+ccnum);
							int total = ccnum.length();
							int endlen = 4;
							int masklen = total - endlen ;
							StringBuffer maskedbuf = new StringBuffer();
							for(int i=0;i<12;i++) {
							    maskedbuf.append('*');
							}
							maskedbuf.append(ccnum.substring(0, 4));
							String masked = maskedbuf.toString();
    
							return masked;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							return ccnum;
						}
			    }
				
				public static String giftMaskCardNumber(String ccnum){
			        try {
						int total = ccnum.length();
						int endlen = 4;
						int masklen = total - endlen ;
						StringBuffer maskedbuf = new StringBuffer();
						for(int i=0;i<masklen;i++) {
						    maskedbuf.append('*');
						}
						maskedbuf.append(ccnum.substring(masklen, total));
						String masked = maskedbuf.toString();

						return masked;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						return ccnum;
					}
		    }
				private String replaceItemArrayNew(String loopBlockOne, String ItemPHTag, Double amtVal){
					
					String Editor = editorType;
					logger.info("Editor Type..................."+Editor);
					String itemHolder = loopBlockOne;
					String preparedString="";
					try{
						final String COLSPAN = "COLSPAN";
						final String Width1 = "Width1";
						final String Width2 = "Width2";
						final String LABEL = "LABEL";
						final String VALUE = "VALUE";
						final String CELLSPACING = "CELLSPACING";
						final String BENEATH_ITEMS_TR_STRUCTURE;
						String [] itemArray;
						DecimalFormat decimalFormat;
					

						if (Editor!=null && Editor.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {
							BENEATH_ITEMS_TR_STRUCTURE = 
											"<table>"+
											"<tr>"+ 
											"<td colspan="+COLSPAN+">" +
											"<div class=\"block-grid \" style=\"Margin: 0 auto; min-width: 500px; max-width: fit-content; width:inherit; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;;\"> " +
											"<table width=\"100%\"  style=\"color:#333333; font-family:arial; font-size:14px;\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
											"<tr>" +
											"<td width="+Width1+"> </td>" +
											"<td width=\"20%\" style=\"text-align:left; color:#333333; font-family:arial; font-size:14px;\">"+LABEL+"</td>" +
											"<td width="+Width2+" style=\"text-align:right; color:#333333; font-family:arial; font-size:14px;padding-right: 10px;\">"+VALUE+"</td>"+
											"</tr>"+
											"</table>"+
											"</div>" +
											"</td>"+
											"</tr>"+
											"</table>";
							
							itemArray = getItemsArray(loopBlockOne);
							logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
							decimalFormat = new DecimalFormat("#,###,##0.00");
							
							logger.info("EditorType   >>>>>>>>>>>>>>>>>>>>>>> "+Editor );
							logger.info("itemArray.length    >>>>>>>>>>>>>>>>>>>>>>> "+itemArray.length  );
							
							if (itemArray.length == 4){
								
								//logger.info("four itemsssss.........."+ItemPHTag.trim().length());
								if(ItemPHTag.trim().length() == 0){
									preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "50% ").replace(Width2, "30% ").
									replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "0");
							
									logger.info("line spacing >>> "+preparedString);
									return preparedString;
								}
								preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "50% ").replace(Width2, "30% ").
										replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
										.replace(CELLSPACING, "0");
								
							} else if (itemArray.length == 5){
								
								//logger.info("five itemsssss.........."+ItemPHTag.trim().length());
								if(ItemPHTag.trim().length() == 0){
									preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "60% ").replace(Width2, "20% ").
									replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "0");
							
									logger.info("line spacing >>> "+preparedString);
									return preparedString;
								}
								preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "60% ").replace(Width2, "20% ").
										replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
										.replace(CELLSPACING, "0");
							} else {
								
								//logger.info("six itemsssss.........."+ItemPHTag.trim().length());
								if(ItemPHTag.trim().length() == 0){
									preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "66% ").replace(Width2, "14% ").
									replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "7");
							
									logger.info("line spacing >>> "+preparedString);
									return preparedString;
								}
								preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, "0").replace(Width1, "66% ").replace(Width2, "14% ").
										replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
										.replace(CELLSPACING, "0");
							}
						
						} else {
							
							BENEATH_ITEMS_TR_STRUCTURE = 
									"<tr>"+ 
											"<td colspan="+COLSPAN+">" +
											"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
											"<tr>" +
											"<td width="+Width1+"> </td>" +
											"<td width=\"20%\" style=\"text-align:left;\">"+LABEL+"</td>" +
											"<td width="+Width2+" style=\"text-align:right;\">"+VALUE+"</td>"+
											"</tr>"+
											"</table>"+
											"</td>"+
											"</tr>";

							
							itemArray = getItemsArray(loopBlockOne);
							logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
							decimalFormat = new DecimalFormat("#,###,##0.00");
							
						logger.info("EditorType   >>>>>>>>>>>>>>>>>>>>>>> "+Editor );
							if(ItemPHTag.trim().length() == 0){
							preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").replace(Width1, "50% ").replace(Width2, "30% ").
									replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "7");
							
							logger.info("line spacing >>> "+preparedString);
							return preparedString;
							}
							
						
							preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").replace(Width1, "50% ").replace(Width2, "30% ").
								replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
								.replace(CELLSPACING, "0");
					 }//else
						
						logger.info("preparedString >>> "+preparedString);
						

					}catch(Exception e){
						logger.error("Exception >>> ",e);
						return itemHolder;
					}
				
					return preparedString;
					
				}

			public static void main(String[] args) {
				String disc = "2";
				String ccnum = "1234";
				System.out.println(ccnum.substring(0, 4));
				System.out.println((Double.parseDouble(disc)));
				
				/*String linebreak = "\n";
				
				String replaced = linebreak.replace("\n", "<br/>");
				
				System.out.println(replaced);
				String disp = "Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\nCumulative loyalty points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\nRedemption currency available is <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n<TAG:DISCOUNTSHEADER><b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS>\n\n<TAG:NUDGESHEADER><b>Below is the status of programs entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>";
				StringBuffer discountLine = new StringBuffer("");
				StringBuffer nudgeLine = new StringBuffer("");
				for (int i=0; i<10; i++) {
					discountLine.append("discount"+i);
					nudgeLine.append("nudge"+i);
					
				}
				String replacedDispTemplate = disp.replace("<TAG:LOYALTYINFO.Balances.POINTS/>", "0")
						.replace("<TAG:CURRENCYCODE/>", "$").replace("<TAG:LOYALTYINFO.Balances.CURRENCY/>", "1")
						.replace("<TAG:LOYALTYINFO.LIFETIMEPOINTS/>", "1")
						.replace("<TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>", "$").
						replace("<TAG:DISCOUNTSHEADER>", Constants.STRING_NILL).replace("</TAG:DISCOUNTSHEADER>", Constants.STRING_NILL)
						.replace("<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>", discountLine.toString()).replace("</TAG:DISCOUNTS>", "")
						.replace("<TAG:NUDGESHEADER>", "").replace("<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>", nudgeLine.toString()).replace("</TAG:NUDGESHEADER>", "");
				
				String replacedDispTemplate = disp.replace("</TAG:DISCOUNTS>", "").replace("<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>", discountLine.toString())
						.replace("</TAG:NUDGESHEADER>", "").replace("<TAG:NUDGESHEADER>", "").replace("<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>", nudgeLine.toString());
				System.out.println(replacedDispTemplate);*/
			}

			public String isDisplayTemp() {
				return displayTemp;
			}

			public void setDisplayTemp(String displayTemp) {
				this.displayTemp = displayTemp;
				
			}
			
			
			public void sendSMSFromDR(DigitalReceiptUserSettings digitalReceiptUserSettings,DRSMSSent drSmsSent, String receiptAmount, String CustomerName, String currBal) {
				
					String drSmsSentId = null;
					try {
						drSmsSentId = EncryptDecryptUrlParameters.encrypt(drSmsSent.getId()+Constants.STRING_NILL);
					} catch (Exception e) {
						logger.error("Exception ::::", e);
					}
				String enteredURL = PropertyUtil.getPropertyValue("DRSMSLinkUrl").replace("|^sentId^|", drSmsSentId+"");
				String insertedUrl="";
				logger.info("EnableSmartReceipt :"+user.isEnableSmartEReceipt());
				if(user.isEnableSmartEReceipt()){
					insertedUrl = PropertyUtil.getPropertyValue("ApplicationSMSShortUrl");
				}
				else {
					insertedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
				}
				if(!(enteredURL.startsWith("http://")||enteredURL.startsWith("https://"))) {
					
					enteredURL = "http://"+enteredURL;
					
				}
				String mappingUrl = user.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
				
				List<StringBuffer> retList = null;
				String urlShortCode = null;
				DRSMSSentDao drSmsSentDao = null;
				try {
					drSmsSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
				}
				while(urlShortCode == null) {
					
					 retList =  Utility.getEightDigitURLCode(mappingUrl);
					
					Iterator<StringBuffer> iterator = retList.iterator();
					while (iterator.hasNext()) {
						StringBuffer code = iterator.next();
						if(drSmsSentDao.findlongUrlByShortCode("DR"+code) == null) {
							urlShortCode = "DR"+code;
							break;
						} 
					}//while 
					
				}//while 
				if(urlShortCode == null) {
					
					logger.debug("no shortcode ===");
					return;
				}
				//UrlShortCodeMapping urlShortCodeMapping = null;
				//if(retList != null && retList.size() > 0) {
					
					//for (StringBuffer shortCode : retList) {
						
						//urlShortCodeMapping = new UrlShortCodeMapping(urlShortCode, enteredURL, "DR", user.getUserId());
						drSmsSent.setOriginalShortCode(urlShortCode);
						drSmsSent.setOriginalUrl(enteredURL);
						try {
							//UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.URL_SHORTCODE_MAPPING_DAO_FOR_DML);
							//urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);
							DRSMSSentDaoForDML drSmsSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
							drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
							insertedUrl += urlShortCode;
						}catch (DataIntegrityViolationException e) {
							// TODO: handle exception
							logger.error("given Short code is already exists in DB.....",e);
							
						}catch (ConstraintViolationException e) {
							// TODO: handle exception
							logger.error("given Short code is already exists in DB.....",e);
						}catch (Exception e) {
							logger.error("Exception ",e);
						}
						
						
						
					//}//for
					
				//}//if
				if(user.isZoneWise()) {
					String storeNo = digitalReceipt.getBody().getReceipt().getStore();
					String SubsidiaryNo = digitalReceipt.getBody().getReceipt().getSubsidiaryNumber();
					// find a zone of this SBS and store
					OrganizationZone orgZone = null;
					try {
						orgZone = getOrgZone(user, storeNo, SubsidiaryNo);
						logger.info("org id :"+(orgZone!=null?orgZone.getZoneId()+"":"orgZone is null"));
					} catch (BaseServiceException e1) {
						logger.error("exception while getting orgzone :"+e1);
					}
					
					
					ZoneTemplateSettingsDao zoneTemplateSettingsDao = null;
					AutoSMSDao autoSMSDao = null;
					String senderID = null;
					String templateId = null;
					try {
						zoneTemplateSettingsDao = (ZoneTemplateSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ZONE_TEMPLATE_SETTINGS_DAO);
						 autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
					} catch (Exception e) {
						logger.error("exception while getting zoneTemplateSettingsDao :"+e);
					}
					ZoneTemplateSettings zoneTemplateSettings = zoneTemplateSettingsDao.findByZoneId(user.getUserId(),orgZone.getZoneId(),OCConstants.AUTO_COMM_TYPE_SMS);
	
					if(zoneTemplateSettings != null) {
						senderID = zoneTemplateSettings.getSenderORfrom(); //senderId in case autoCommType=SMS
						templateId = zoneTemplateSettings.getTemplateId() ;
						logger.info("senderID :"+senderID+" and templateId :"+templateId);
					
					
						String DRSMSContent = autoSMSDao.Ereceiptmsgtype(Long.parseLong(templateId));
						
						sendSMS(drSmsSent,insertedUrl,DRSMSContent, 
								templateId, senderID, receiptAmount, CustomerName, currBal);
					}else {
						sendSMS(drSmsSent,insertedUrl,user.getDRSMSContent(), 
								user.getDRSMSTempRegID(), user.getDRSMSSenderID(), receiptAmount, CustomerName, currBal);
					}
				}else {//user not zone wise
					sendSMS(drSmsSent,insertedUrl,user.getDRSMSContent(), 
							user.getDRSMSTempRegID(), user.getDRSMSSenderID(), receiptAmount, CustomerName, currBal);
				}
			} //sendSMSFromDR

		public void sendWAFromDR(DRSMSChannelSent drSmsChannelSent) {
				logger.info("entered into sendWAFromDR >>>>>>>>");
				String drSmsChannelSentId = null;
				try {
					drSmsChannelSentId = EncryptDecryptUrlParameters.encrypt(drSmsChannelSent.getId()+Constants.STRING_NILL);
				} catch (Exception e) {
					logger.error("Exception ::::", e);
				}
			String enteredURL = PropertyUtil.getPropertyValue("DRWALinkUrl").replace("|^sentId^|", drSmsChannelSentId+"");
			String insertedUrl="";
			if(user.isEnableSmartEReceipt()){
				insertedUrl = PropertyUtil.getPropertyValue("ApplicationWAShortUrl");
			}
			else {
				insertedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
			}
			if(!(enteredURL.startsWith("http://")||enteredURL.startsWith("https://"))) {
				
				enteredURL = "http://"+enteredURL;
				
			}
			String mappingUrl = user.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
			List<StringBuffer> retList = null;
			String urlShortCode = null;
			DRSMSChannelSentDao drSmsSentDao = null;
			try {
				drSmsSentDao = (DRSMSChannelSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_Channel_SENT_DAO);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
			}
			while(urlShortCode == null) {
				
				 retList =  Utility.getEightDigitURLCode(mappingUrl);
				
				Iterator<StringBuffer> iterator = retList.iterator();
				while (iterator.hasNext()) {
					StringBuffer code = iterator.next();
					if(drSmsSentDao.findlongUrlByShortCode("DR"+code) == null) {
						urlShortCode = "DR"+code;
						break;
					} 
				}//while 
				
			}//while 
			if(urlShortCode == null) {
				
				logger.debug("no shortcode ===");
				return;
			}
		

			//if(retList != null && retList.size() > 0) {
				
				//for (StringBuffer shortCode : retList) {
					
					drSmsChannelSent.setOriginalShortCode(urlShortCode);
					drSmsChannelSent.setOriginalUrl(enteredURL);
					try {
						DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
						drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
						insertedUrl += urlShortCode;
					}catch (DataIntegrityViolationException e) {
						logger.error("given Short code is already exists in DB.....",e);
						
					}catch (ConstraintViolationException e) {
						logger.error("given Short code is already exists in DB.....",e);
					}catch (Exception e) {
						logger.error("Exception ",e);
					}
					
					
					
				//}//for
				
			//}//if

			if(user.isReceiptOnWA())sendOnWA(user, drSmsChannelSent,insertedUrl, user.getWAAPIKey(), user.getWAAPIEndPointURL(), 
						user.getWATemplateID(), user.getWAUserID(), user.getWAJSONTemplate());;
			} //sendWAFromDR
			public void sendSMS(DRSMSSent drSmsSent, String sendingMsg, String userDRSMSContent, 
					String userSMSTempRegID, String userSenderID, String receiptAmount, 
					String CustomerName, String currBal) {

				
				try{
					MessageUtil.clearMessage();
					int charCount =0;
					int usedCount =0;
					long mblNum =0;
					
					String mblNumber=drSmsSent.getMobile();
					UserSMSGatewayDao userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
					OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
					UserOrganization userOrganization = user.getUserOrganization();
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway =(CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName(OCConstants.CAPTIWAY_TO_SMS_API_GATEWAY);
					UserSMSGateway userSmsGateway = null;
					OCSMSGateway ocgateway = null;
					String userSMSDetails = Constants.STRING_NILL;

					LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);	
					logger.info("Loyalty balnace object object :"+loyaltyBalanceDao);
					Long lytId = null;
					Long userId = null;
				//	value = loyaltyBalance.getBalance().toString(); // need a method to get the value code or the code
					LoyaltyBalance loyaltyBalance = null;
						
					ocgateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
							SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
					/*userSmsGateway = userSMSGatewayDao.findByUserId(user.getUserId(),"TR");
						if(userSmsGateway == null) {
							
							logger.debug("Error while getting userSmsGateway details...");
							//MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
							return;
							
						}
						
						ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());*/
						if(ocgateway == null) {
							
							logger.debug("Error while getting ocgateway details...");
							//MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
							return;
							
						}
						
						String UsersenderId = ocgateway.getSenderId();
						UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO); 
						List<UserSMSSenderId> retList =  userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), ocgateway.getAccountType());
						if(retList != null && !retList.isEmpty()) {
							UsersenderId = retList.get(0).getSenderId();
						}
						String senderId = userSenderID == null ? UsersenderId : userSenderID;
					logger.debug(" my sms details are ====>"+userSMSDetails);
					
					try {
							mblNumber = mblNumber.trim();
							mblNumber = Utility.phoneParse(mblNumber,userOrganization);
							if(mblNumber != null ) {
								if(userOrganization.isRequireMobileValidation()){
								if(!mblNumber.startsWith(user.getCountryCarrier().toString()) &&
										( (mblNumber.length() >= userOrganization.getMinNumberOfDigits()) && (mblNumber.length() <= userOrganization.getMaxNumberOfDigits()))
										) {
									
									mblNumber = user.getCountryCarrier().toString()+mblNumber;
								}
								}
								mblNum = Long.parseLong(mblNumber);
							}
							else{
								
								MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
							
							}
							
					} catch (NumberFormatException e1) {

						MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
					
					}
					
					logger.debug("mobile numbers to send Test SMS are====>"+mblNumber);
					
						
					/***split,truncate,ignore  or send the SMS message as it is depend on the selected sending option ******/
					
					logger.info("get the user sms count=====>"+user.getSmsCount());
					if(user.getSmsCount()==0){
						
						MessageUtil.setMessage("SMS credits are not available. Please renew your account.", "color:red;","top");
						
					}
					
				
				AutoSMSDao autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			
				logger.info("mobile number 1is "+mblNumber);

				List<ContactsLoyalty> findEnrollList=null;
				
				String msgContent="";
				String LtymemCurrencybal="";
				String Ltypointsbal="";
				String defVal="0";
			

				String[] Names = getCustomerAndStoreName();

				try {
				findEnrollList = FindEnrollListByEmailORPhone("", mblNumber, user);
				ContactsLoyalty contactsLoyalty = findEnrollList.get(0);
				 lytId = contactsLoyalty.getLoyaltyId();
				 userId = contactsLoyalty.getUserId();
				logger.info(" userid of loyalty : "+userId+"loyalty id for value code"+lytId);

				
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				 LtymemCurrencybal= (contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) :"0";
				 Ltypointsbal=(contactsLoyalty.getLoyaltyBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? contactsLoyalty.getLoyaltyBalance().longValue()+"" :"0";
			
									
				
				
				}catch(Exception e) {
			
					logger.info("Exception occured for non loyalty");
				
					LtymemCurrencybal=defVal;
					Ltypointsbal=defVal;
					
				}

				logger.info("ltycurr bal"+LtymemCurrencybal);
				
				logger.info("ltypoints bal"+Ltypointsbal);

			
				ArrayList<String> msgContentLst = null;
				String Message="";
				if(user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
						
						
						
						
					msgContent= userDRSMSContent == null ? PropertyUtil.getPropertyValueFromDB("drSmsText") : userDRSMSContent;
					msgContent = msgContent.replace("#Name#",CustomerName);
					msgContent = msgContent.replace("#RA#",receiptAmount);
					msgContent = msgContent.replace("#CurrBal#",currBal == null ?"":currBal);
					msgContent = msgContent.replace("#link#",sendingMsg);
					//msgContent = msgContent.replace("#SenderID#",ocgateway.getSenderId());
					msgContent = msgContent.replace("#SenderID#",senderId); //1.users.DRSMSSenderID or zonetemplatesettings.getSenderID 2.usersmssenderid.get(0).getSenderId() 3.ocgateway.getSenderId()
					}else {
						msgContent= PropertyUtil.getPropertyValueFromDB("defaultDrSmsText");
						msgContent = msgContent.replace("#link#",sendingMsg);
					}
					//eReceipt tags by adarsh	
					msgContent = msgContent.replace("[Customer Name]",CustomerName);
					msgContent = msgContent.replace("[Store Name]",Names[1]);
					msgContent = msgContent.replace("[loyaltyMembershipCurrencyBalance]",LtymemCurrencybal== " " ? defVal :LtymemCurrencybal);
					msgContent = msgContent.replace("[loyaltypointsBalance]",Ltypointsbal==" " ? defVal :Ltypointsbal);
					String valCode = "";
					String subString = msgContent;
					//suppose my content has hi {[VC_Gold]}...
					if(msgContent.contains("|^VC_")) {
						logger.info(">>>>>>>>>>> Entered in value code flow >>>>>>> ");
						try {
				do {
					if(subString.contains("|^VC_")) {
						
						int start = subString.indexOf("_");
						int end = subString.indexOf("^|");
						valCode = subString.substring(++start, end);
						subString = subString.substring(++end);
						logger.info("Value code extracted from the merge tag. "+valCode);
						
								loyaltyBalance = loyaltyBalanceDao.findBy(userId,lytId, valCode);
								if(loyaltyBalance == null) {
								logger.info(">>>> entered in the  loyaltyBalance== null");
								msgContent=	msgContent.replace("|^VC_"+valCode+"^|", "0");// no loyalty transaction is given/done on that particular value code.
									continue;
								}
								logger.info("has loyalty Object"+loyaltyBalance);
								logger.info("Replacing value code : "+valCode+" with "+loyaltyBalance.getBalance());
								if(loyaltyBalance.getBalance() == null) {
									logger.info(">>>> entered in the  loyaltyBalance.getBalance== null");
									msgContent = msgContent.replace("|^VC_"+valCode+"^|","0");
									continue;
								}
								msgContent = msgContent.replace("|^VC_"+valCode+"^|",loyaltyBalance.getBalance().toString());

					}
					
					else break; // if no such tag is found.
					
				}while(!subString.isEmpty());
						}catch(Exception e) {
							logger.error("request for valucode : "+e);
						}
			} //

					logger.info("sendingMsg after--- "+msgContent);
					charCount = msgContent.length();
				    usedCount=1;
				     if(charCount>160) usedCount = charCount/160 + 1;
					String msgID = captiwayToSMSApiGateway.sendDRSMS(ocgateway, msgContent, 
							""+mblNumber, "DR", "9848495956", ""+mblNumber, "1", senderId,userSMSTempRegID == null ? "1007030531747089377":userSMSTempRegID,drSmsSent);
					if(msgID != null){
						drSmsSent.setMobile(mblNumber);
						drSmsSent.setMessageId(msgID);
						DRSMSSentDaoForDML drSmsSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
						drSmsSentDaoForDML.saveOrUpdate(drSmsSent);
					}
					/**
				     * Update the Used SMS count
				     */
				    try{
				     UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				     UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
				     //usersDao.updateUsedSMSCount(userId, mobCnt);
				     		
				     usersDaoForDML.updateUsedSMSCount(user.getUserId(), usedCount*1);
				    }catch(Exception exception){
				     logger.error("Exception while updating the Used SMS count",exception);
				    }
					
					/**
					 * Update SmsQueue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					
					
					smsQueueHelper.updateSMSQueue(mblNumber, msgContent, Constants.SMS_MSG_TYPE_DRSMS, user,senderId);
					
				}catch (NumberFormatException e) {
					MessageUtil.setMessage("Exception in sending SMS.", "color:red;","top");
					logger.error("** Exception",e);
				}catch (Exception e) {
					logger.error("** Exception while sending SMS",e);
				}
				
			}
			public void sendOnWA(Users user, DRSMSChannelSent drSmsChannelSent, String sendingMsg, String WAAPIKey,
					String WAAPIEndPoint, String WATemplateID, String WAUserID, String messageTemplate) {
				logger.info(">>> entered in sendOnWA");
				String mobile = Utility.phoneParse(this.mobile.trim(), user.getUserOrganization());
				String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
				if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {

					mobile = mobile.substring(countryCarrier.length());// country code stripped off as 91 already there in template

				}
				
				try {
					
					if(digitalReceipt != null && 
							digitalReceipt.getHead().getRequestSource().equals(OCConstants.DR_SOURCE_TYPE_Shopify)) {	//3784
						if(user.isCoOnWA() && user.getConfirmOrderJSONTemplate() != null && 
								!user.getConfirmOrderJSONTemplate().isEmpty() ){
								//&& user.getCoCookie() != null && !user.getCoCookie().isEmpty()) {
							//send only order confirmatin
							drSmsChannelSent.setStatus("Success");
							try {
								DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
								drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
							}catch(Exception e){
								
							}
							String[] Name = getCustomerAndStoreName();
							
							String messageTemplateReplaced = user.getConfirmOrderJSONTemplate().replace("[mobile]", mobile)
															.replace("[Name]", Name[0]).replace("[orderNo]", Name[3]);
							logger.debug("message =="+messageTemplateReplaced);
							DefaultHttpClient httpClient = new DefaultHttpClient();
							HttpPost postRequest = new HttpPost(WAAPIEndPoint);
							StringEntity input = new StringEntity(messageTemplateReplaced,"UTF-8");
							postRequest.setEntity(input);
							postRequest.setHeader("Content-Type", "application/json");
							postRequest.setHeader("Authorization", WAAPIKey); //messageBird
							postRequest.setHeader("apikey", WAAPIKey); //CM
							postRequest.setHeader("Cookie", user.getCoCookie()); //CM
							HttpResponse response = httpClient.execute(postRequest);
							logger.info("mobile response ::"+response);
							int  statusCode = response.getStatusLine().getStatusCode();
					        if (statusCode != 202 && statusCode !=200) {	//messageBird sucessCode=202 CM=200 meta=200
					            logger.error("Failed : HTTP error code : " + statusCode);
					        }else {
					        	logger.info("Success : HTTP Success code : " + statusCode);
					        }
							 BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
						        String output;
						        StringBuffer totalOutput = new StringBuffer();
						        while ((output = br.readLine()) != null) {
						            totalOutput.append(output).append("::").append("statusCode:"+statusCode);
						        }
								logger.info("mobile response ::"+totalOutput.toString());
						
						
							return;
					}
					}
					
					//to track the delivery reports(DR::sent_id::cid::user_id)
					String trackingId = "DR"+Constants.DELIMETER_DOUBLECOLON+drSmsChannelSent.getId()+Constants.DELIMETER_DOUBLECOLON+0L+Constants.DELIMETER_DOUBLECOLON+user.getUserId();
					
					//Credit Note Issue of StylUN
					List<CustomTender> customTenderList = drbody!=null ? drbody.getCustomTender() : null;
					boolean creditNoteIssued = false ; //APP-4189
					String CNI_Amt="0.0";
					int statusCode=0;
					if(customTenderList != null && customTenderList.size()>0) {
						
						for(CustomTender customTender : customTenderList) {
							if(customTender.getType()!=null && customTender.getType().equalsIgnoreCase(Constants.CNI) )
								creditNoteIssued = true; //APP-4090
							CNI_Amt = customTender.getAmount()!=null ? customTender.getAmount():"";
						}
					}
					if(creditNoteIssued){
						
						logger.debug("<<<<<<< Custom Tender Type is CNI >>>>>>>");
						
						statusCode = sendWAViaHttpClient(Constants.CNI, sendingMsg, CNI_Amt, trackingId);
						
						logger.debug("status code="+statusCode);
						DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
						if(statusCode==202 || statusCode==200)	drSmsChannelSent.setStatus("Success"); //messageBird sucessCode=202
						else drSmsChannelSent.setStatus("Fail");
						drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
					}
					//incase normal ereceipt OR CNI request coming but there is not whatsapp setup for that user
					if(statusCode==0 && WAAPIKey != null && WAAPIEndPoint != null && WATemplateID != null && WAUserID!=null) {
						drSmsChannelSent.setStatus("Success");
						try {
							DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
							drSmsChannelSentDaoForDML.saveOrUpdate(drSmsChannelSent);
						}catch(Exception e){
							
						}
						String[] Name = getCustomerAndStoreName();
						
						String messageTemplateReplaced = messageTemplate.replace("[WATemplateID]", WATemplateID) //namespace
						.replace("[WAUserID]", WAUserID)	//channel ID
						.replace("[WAAPIKey]", WAAPIKey)	//Access Key
						.replace("[mobile]", mobile).replace("[Name]", Name[0]).replace("[storeName]", Name[1])
						.replace("[sendingMsg]", sendingMsg).replace("[GMapLink]", Name[2])
						.replace("[Total]", Name[4])
						.replace("[LoyaltyEarnedToday]", (loyaltyEarnedToday != null ? loyaltyEarnedToday:"0"))
						.replace("[LoyaltyPointsBalance]", (pointsBalance != null ? pointsBalance:"0"))
						.replace("[CountryCarrier]", countryCarrier)
						.replace("[trackingId]", trackingId);
						
						
						logger.debug("message =="+messageTemplateReplaced);
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpPost postRequest;
						
						if (WAAPIEndPoint.equals(OCConstants.Gupshup_Endpoint)) {//gupshup
							//will support only TEXT template(media,header wont support)
							
							String footer = WATemplateID;//if any footer value is there, then store in templateID for now,
														// as templateID not needed for gupshup

							List<BasicNameValuePair> params = new ArrayList<>();
							params.add(new BasicNameValuePair("userid", WAUserID));
							params.add(new BasicNameValuePair("password", WAAPIKey));
							params.add(new BasicNameValuePair("send_to", mobile));
							params.add(new BasicNameValuePair("v", "1.1"));
							params.add(new BasicNameValuePair("format", "json"));
							params.add(new BasicNameValuePair("msg_type", "TEXT"));
							params.add(new BasicNameValuePair("isTemplate", "true"));
							params.add(new BasicNameValuePair("auth_scheme", "PLAIN"));
							params.add(new BasicNameValuePair("method", "SENDMESSAGE"));
							params.add(new BasicNameValuePair("msg", messageTemplateReplaced));
							if(footer!=null && !footer.isEmpty()) {
				                params.add(new BasicNameValuePair("footer", footer));
							}
							URI uri = new URI(WAAPIEndPoint + "?" + URLEncodedUtils.format(params, "UTF-8"));
							
							logger.debug("Final URL is ::\n"+uri);
							postRequest = new HttpPost(uri);

						}else {//CM,meta
							postRequest = new HttpPost(WAAPIEndPoint);
							StringEntity input = new StringEntity(messageTemplateReplaced, "UTF-8");
							//input.setContentType("application/json; charset=utf-8");
							postRequest.setEntity(input);
							postRequest.setHeader("Authorization", WAAPIKey); //messageBird
							postRequest.setHeader("Content-Type", "application/json");
							postRequest.setHeader("apikey", WAAPIKey); //CM
						}
						HttpResponse response = httpClient.execute(postRequest);
						logger.info("mobile response ::"+response);
						statusCode = response.getStatusLine().getStatusCode();
				        if (statusCode != 202 && statusCode != 200) { //messageBird sucessCode=202	CM=200 meta=200
				            logger.error("Failed : HTTP error code : " + statusCode);
				        }else {
				        	logger.info("Success : HTTP Success code : " + statusCode);
				        }
						 BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
					        String output;
					        StringBuffer totalOutput = new StringBuffer();
					        while ((output = br.readLine()) != null) {
					            totalOutput.append(output).append("::").append("statusCode:"+statusCode);
					        }
							logger.info("mobile response ::"+totalOutput.toString());
					}
				} catch (UnsupportedEncodingException e) {
					logger.error("Exception ",e);
				} catch (ClientProtocolException e) {
					logger.error("Exception ",e);
				} catch (IOException e) {
					logger.error("Exception ",e);
				}catch (Exception e) {
					logger.error("Exception ",e);
				}
				
				if(user.isEnableNPS()) {
					try {
						String[] Name = getCustomerAndStoreName();
						
						String messageTemplateReplaced = user.getNPSJSONTemplate().replace("[Mobile]", mobile)
						.replace("[Email]", PropertyUtil.getPropertyValueFromDB("NPSFeedbackEmail")).replace("[Name]", Name[0]);
						
						logger.debug("message =="+messageTemplateReplaced);
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpPost postRequest = new HttpPost(user.getNPSEndPointURL());
						 StringEntity input = new StringEntity(messageTemplateReplaced,"UTF-8");
						//input.setContentType("application/json; charset=utf-8");
						postRequest.setEntity(input);
						//postRequest.setHeader("Authorization", WAAPIKey);
						postRequest.setHeader("Content-Type", "application/json");
						postRequest.setHeader("Authorization", user.getNPSProductKey()); //messageBird
						postRequest.setHeader("X-CM-PRODUCTTOKEN", user.getNPSProductKey());
						postRequest.setHeader("Cookie", user.getNPSCookie());
						HttpResponse response = httpClient.execute(postRequest);
						logger.info("mobile response ::"+response);
						int  statusCode = response.getStatusLine().getStatusCode();
						if (statusCode != 202 && statusCode != 200) { //messageBird sucessCode=202	CM=200 meta=200
						    logger.error("Failed : HTTP error code : " + statusCode);
						}else{
							logger.info("Success : HTTP Success code : " + statusCode);
						}
						 BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
						    String output;
						    StringBuffer totalOutput = new StringBuffer();
						    while ((output = br.readLine()) != null) {
						        totalOutput.append(output).append("::").append("statusCode:"+statusCode);
						    }
							logger.info("mobile response ::"+totalOutput.toString());
					} catch (UnsupportedEncodingException e) {
						
					} catch (ClientProtocolException e) {
					} catch (IllegalStateException e) {
					} catch (IOException e) {
					}
					
				}
				
			logger.info("exit from sendOnWA >>>>>>>>>>>>>>>>>>");	
			} //sendOnWA
			
			/**
			 * 1. get tha WA template basis Template type
			 * 2. get the WA configs
			 * 3. replace the ph and send WA
			 */
			public int sendWAViaHttpClient(String category,String eReceiptLink, String CNI_Amt, String trackingId) {
				
				WATemplatesDao waTemplatesDao = null;
				UserWAConfigsDao userWAConfigsDao = null;
				
				String mobile = Utility.phoneParse(this.mobile.trim(), user.getUserOrganization());
				String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
				if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMaxNumberOfDigits()) {
					mobile = mobile.substring(countryCarrier.length());// country code stripped off as 91 already there in template
				}
				int  statusCode = 0;
				
				try {
					waTemplatesDao = (WATemplatesDao)ServiceLocator.getInstance().getDAOByName("waTemplatesDao");
					userWAConfigsDao = (UserWAConfigsDao)ServiceLocator.getInstance().getDAOByName("userWAConfigsDao");
				
					logger.debug("STEP I :: find configured WA Template");
					WATemplates waTemplate = waTemplatesDao.findTemplateByType(user.getUserId(),category);
					if(waTemplate==null || waTemplate.getJsonContent()==null || waTemplate.getProvider()==null || waTemplate.getType()==null || waTemplate.getTemplateRegisteredId()==null) return statusCode;
					
					logger.debug("STEP II :: find configured user WA Configs for the provider");
					UserWAConfigs userWAConfigs =userWAConfigsDao.findWAConfigsByProvider(user.getUserId(),waTemplate.getProvider());
					if(userWAConfigs==null || userWAConfigs.getAccessToken()==null || userWAConfigs.getWaAPIEndPoint()==null || userWAConfigs.getFromId()==null) return statusCode;
					
					logger.debug("STEP III :: replace the placeholders");
					String[] Name = getCustomerAndStoreName();
					String replacedJsonContent = waTemplate.getJsonContent()
												.replace("[WAAPIKey]", userWAConfigs.getAccessToken()) //access key
												.replace("[WAUserID]", userWAConfigs.getFromId()) //channel Id or fromID
												.replace("[WATemplateID]", waTemplate.getTemplateRegisteredId()) //Template namespace
												.replace("[mobile]", mobile)
												.replace("[sendingMsg]", eReceiptLink!=null && !eReceiptLink.isEmpty()?eReceiptLink:"Not Available") //eReceipt link
												.replace("[Name]", Name[0]!=null && !Name[0].isEmpty()? Name[0]:"Customer")
												.replace("[storeName]", Name[1])
												.replace("[GMapLink]", Name[2]).replace("[orderNo]", Name[3])
												.replace("[trackingId]", trackingId);
					//date ph
					replacedJsonContent = replacedJsonContent.replace("|^", "<").replace("^|", ">"); // for whatsapp, bcz [] not worked
					
					Set<String> datePhSet = getDateFields(replacedJsonContent);
					
					replacedJsonContent = replaceDatePh(replacedJsonContent, datePhSet);
					
					if(category.equalsIgnoreCase(Constants.CNI)) {
						
						replacedJsonContent=replacedJsonContent.replace("[Amount]",String.valueOf(Math.abs(Double.parseDouble(CNI_Amt))));
																//.replace("[Expiry]", LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))); //added 6 months
					}
					
					logger.debug("replacedJsonContent =="+replacedJsonContent);
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(userWAConfigs.getWaAPIEndPoint());
					StringEntity input = new StringEntity(replacedJsonContent,"UTF-8");
					postRequest.setEntity(input);
					postRequest.setHeader("Content-Type", "application/json");
					postRequest.setHeader("Authorization", userWAConfigs.getAccessToken()); //messageBird
					postRequest.setHeader("apikey", userWAConfigs.getAccessToken()); //CM
					//postRequest.setHeader("Cookie", user.getCoCookie()); //CM
					
					//if a template have any other header
					try {
						String headers = waTemplate.getHeaders(); //eg Cookie::__cfruid=4c561e042ebe759324152a99d66c473196b2405a-1670478796
						if(headers!=null && !headers.isEmpty()) {
							String[] headersArr = headers.trim().split(Constants.DELIMETER_COMMA);
							for(String header : headersArr) {
								postRequest.setHeader(header.split(Constants.DELIMETER_DOUBLECOLON)[0], 
														header.split(Constants.DELIMETER_DOUBLECOLON)[1]);
							}
						}
					} catch (Exception e) {
						logger.error("Exception while setting headers",e);
					}
					
					HttpResponse response = httpClient.execute(postRequest);
					logger.info("mobile response ::"+response);
					statusCode = response.getStatusLine().getStatusCode();
			        if (statusCode != 202 && statusCode != 200) { //msgBird=202 CM=200 meta=200
			            logger.error("Failed : HTTP error code : " + statusCode);
			        }else{
			        	logger.info("Success : HTTP Success code : " + statusCode);
			        }
					BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				    String output;
				    StringBuffer totalOutput = new StringBuffer();
				    while ((output = br.readLine()) != null) {
				    	totalOutput.append(output).append("::").append("statusCode:"+statusCode);
				    }
					logger.info("mobile response ::"+totalOutput.toString());
					
				} catch (UnsupportedEncodingException e1) {
					logger.error("Exception ",e1);
				} catch (ClientProtocolException e2) {
					logger.error("Exception ",e2);
				} catch (IOException e3) {
					logger.error("Exception ",e3);
				}catch (Exception e) {
					logger.error("Exception ",e);
				}
				
				return statusCode;
				
			}//sendWAViaHttpClient
			
			private Set<String> getDateFields(String content) {

				content = content.replace("|^", "<").replace("^|", ">");//for whatsapp [] wont work

				String cfpattern = "\\<([^\\[]*?)\\>";
				Pattern r = Pattern.compile(cfpattern, Pattern.CASE_INSENSITIVE);
				Matcher m = r.matcher(content);

				String ph = null;
				Set<String> dateFieldsSet = new HashSet<String>();

				try {
					while (m.find()) {

						ph = m.group(1); // .toUpperCase()
						if (logger.isInfoEnabled())
							logger.info("Ph holder :" + ph);

						if (ph.startsWith(Constants.DATE_PH_DATE_)) {
							dateFieldsSet.add(ph);
						}

					} // while

				} catch (Exception e) {
					logger.error("Exception while getting the symbol place holders ", e);
				}

				if (logger.isInfoEnabled())
					logger.info("symbol PH  Set : " + dateFieldsSet);

				return dateFieldsSet;
			}//getDateFields
			
			public String replaceDatePh(String jsonContent, Set<String> datePhSet) {
				
				if (datePhSet != null && datePhSet.size() > 0) {

					for (String symbol : datePhSet) {
						if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
							if (symbol
									.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
								Calendar cal = MyCalendar.getNewCalendar();
								jsonContent = jsonContent
										.replace(
												"<" + symbol + ">",
												MyCalendar
												.calendarToString(
														cal,
														MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} else if (symbol
									.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
								jsonContent = jsonContent
										.replace(
												"<" + symbol + ">",
												MyCalendar
												.calendarToString(
														cal,
														MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} else if (symbol.endsWith(Constants.DATE_PH_DAYS)) {

								try {
									String[] days = symbol.split("_");
									Calendar cal = MyCalendar.getNewCalendar();
									cal.set(Calendar.DATE, cal.get(Calendar.DATE)
											+ Integer.parseInt(days[1].trim()));
									jsonContent = jsonContent
											.replace(
													"<" + symbol + ">",
													MyCalendar
													.calendarToString(
															cal,
															MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
								} catch (Exception e) {
									logger.debug("exception in parsing date placeholder");
								}
							}// else if
						}// if
					}// for
				}//if
				return jsonContent;
			}//replaceDatePh
			
			
			
			public String minimizeHtml(String htmlContent) {
				 	
					logger.info("entering minimizeHtml method");
					logger.info("original htmlContent length is"+htmlContent.length());
					
					HtmlCompressor compressor = new HtmlCompressor();

			        compressor.setRemoveComments(true); // Remove HTML comments
			        compressor.setRemoveIntertagSpaces(true); // Remove spaces between tags
			        //newly added
			        compressor.setRemoveQuotes(true);
			        compressor.setRemoveScriptAttributes(true);
			        compressor.setRemoveStyleAttributes(true);
			        compressor.setSimpleBooleanAttributes(true);
			        compressor.setRemoveHttpProtocol(true);
			        compressor.setRemoveHttpsProtocol(true);
			        compressor.setRemoveSurroundingSpaces("br,p");
			        
			        // Minimize the HTML content
			        String minimizedHtml = compressor.compress(htmlContent);
		        
					logger.info("minimizeHtml length is"+minimizedHtml.length());

			       return minimizedHtml;
			}
	
			public String[] getCustomerAndStoreName(){
				String[] names = new String[5];
				try {
					OrganizationStoresDao organizationStoresDao =(OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					String Name = digitalReceipt != null ? digitalReceipt.getBody().getReceipt().getBillToFName():prismDRBody != null ? prismDRBody.getBt_first_name():"";
					names[0] =Name;
					String storeNum = digitalReceipt != null ? digitalReceipt.getBody().getReceipt().getStore():prismDRBody != null ? prismDRBody.getStore_number():"";
					OrganizationStores Store = organizationStoresDao.findByStoreLocationId( user.getUserOrganization().getUserOrgId(), storeNum);
					names[1] = Store != null && Store.getStoreName() != null  ? Store.getStoreName():"Store";
					names[2] = Store!=null && Store.getGoogleMapLink() != null && !Store.getGoogleMapLink().isEmpty() ? Store.getGoogleMapLink() :"Not Available";
					names[3] = digitalReceipt != null ? digitalReceipt.getBody().getReceipt().getInvcNum():"Not Available";	//3784
					names[4] = digitalReceipt != null ? digitalReceipt.getBody().getReceipt().getTotal():"Not Available";
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				}
				return names;
				
			}
			//app-3706
			private String getDateFormattedData(POSMapping posmapping, String fieldValue, String selectedDateFormat){
				
				String dataTypeStr = posmapping.getDrDataType();
				String dateFieldValue = null;
				String custfieldName = posmapping.getCustomFieldName();
				
				if(posmapping.getDrDataType().trim().startsWith("Date")) {
					
					try {
						String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
						
						if(!Utility.validateDate(fieldValue, dateFormat)) {
							return null;
						}
						
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateFormat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar cal =  new MyCalendar(Calendar.getInstance(), null, selectedDateFormat);
						cal.setTime(date);
						dateFieldValue= MyCalendar.calendarToString(cal, selectedDateFormat);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					}
				}
				
				return dateFieldValue;
			}
			
			private OrganizationZone getOrgZone(Users user, String storeNo, String SBSNo)
					throws BaseServiceException {
				try {
					UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

					List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());// finding this power user's
																									// domain
					logger.debug("==STEP I====");
					if (domainsList == null)
						return null;

					logger.info("==domainsList.get(0).getDomainId()==" + domainsList.get(0).getDomainId());

					OrganizationStoresDao orgStoresDao = (OrganizationStoresDao) ServiceLocator.getInstance()
							.getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					OrganizationStores orgStore = orgStoresDao.findOrgStoreObject(user.getUserOrganization().getUserOrgId(),
							domainsList.get(0).getDomainId(), SBSNo, storeNo);// find the orgstorerecord to find the which zone
																				// this belongs to
					logger.debug("==STEP II====");
					if (orgStore == null)
						return null;

					ZoneDao zoneDao = (ZoneDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_ZONE_DAO);
					OrganizationZone orgZone = zoneDao.findBy(orgStore.getStoreId());

					if (orgZone == null)
						return null;
					
					return orgZone;

				} catch (Exception e) {
					throw new BaseServiceException("Exception occured while getting org zone ", e);
				}
			}
			
	}
