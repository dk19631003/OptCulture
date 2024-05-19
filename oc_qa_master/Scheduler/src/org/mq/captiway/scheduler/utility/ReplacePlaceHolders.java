package org.mq.captiway.scheduler.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.captiway.scheduler.NotificationRecipientProvider;
import org.mq.captiway.scheduler.RecipientProvider;
import org.mq.captiway.scheduler.SMSCampaignSubmitter;
import org.mq.captiway.scheduler.SMSRecipientProvider;
import org.mq.captiway.scheduler.WACampaignSubmitter;
import org.mq.captiway.scheduler.WARecipientProvider;
import org.mq.captiway.scheduler.beans.AutosmsSenturlShortcode;
import org.mq.captiway.scheduler.beans.AutosmsUrls;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.LoyaltyBalance;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.LoyaltySettings;
import org.mq.captiway.scheduler.beans.LoyaltyThresholdBonus;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionChild;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.captiway.scheduler.beans.SMSCampaignUrls;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.AutoSmsUrlShortCodeDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.LoyaltyBalanceDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentUrlShortCodeDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentUrlShortCodeDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.services.ExternalSMTPSender;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.beans.LoyaltyBalance;
import org.mq.captiway.scheduler.dao.LoyaltyBalanceDao;
import org.mq.captiway.scheduler.beans.LoyaltyThresholdBonus;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.zkoss.zkplus.spring.SpringUtil;
/**
 * this calss helps us to replace the placeholders of </br>
 * content with the corresponding contact's values
 * @author proumya
 *
 */
public class ReplacePlaceHolders {



	private ApplicationContext context;
	private RetailProSalesDao retailProSalesDao;
	private OrganizationStoresDao organizationStoresDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private LoyaltySettingsDao loyaltySettingsDao;
	private Long orgId;
	private volatile long shortCodeOffSet=0l;
	//private RecipientProvider provider;
	private CouponProvider couponProvider;
	private ReferralcodeProvider referralcodeProvider;
	
	private String campaignName;
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	private ReferralcodesIssuedDao refcodesissuedDao;

	public ReplacePlaceHolders() { }


	public ReplacePlaceHolders(ApplicationContext context) {

		this.context = context;

	}
	//for sms camapign
	public ReplacePlaceHolders(ApplicationContext context, Long orgId, 
			String SMScampaignName ) {

		this.context = context;
		this.orgId = orgId;
		//this.provider = provider;
		this.campaignName = SMScampaignName;

		this.retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		this.organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
		this.loyaltySettingsDao = (LoyaltySettingsDao)context.getBean("loyaltySettingsDao");
		this.mailingListDao = (MailingListDao)context.getBean("mailingListDao");
	
		this.usersDao = (UsersDao)context.getBean("usersDao");
		shortCodeOffSet = 0l;
	}
	public ReplacePlaceHolders(ApplicationContext context, Long orgId, 
			RecipientProvider provider, String campaignName) {

		this.context = context;
		this.orgId = orgId;
		//this.provider = provider;
		this.campaignName = campaignName;

		this.retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		this.organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
		this.mailingListDao = (MailingListDao)context.getBean("mailingListDao");
		this.usersDao = (UsersDao)context.getBean("usersDao");
	}

	public ReplacePlaceHolders(RetailProSalesDao retailProSalesDao, OrganizationStoresDao organizationStoresDao,
			ContactsLoyaltyDao contactsLoyaltyDao, MailingListDao mailingListDao, UsersDao usersDao,  Long orgId) {

		this.retailProSalesDao = retailProSalesDao;
		this.organizationStoresDao = organizationStoresDao;
		this.contactsLoyaltyDao = contactsLoyaltyDao;
		this.mailingListDao = mailingListDao;
		this.usersDao = usersDao;
		this.orgId = orgId;
		
	}

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

// used for replacing the merge tags for the auto sms queue 
	public  String getAutoSMSPhValue(Long userId, Contacts contact, String content,Set<String> totalPhSet, Set<String> urlSet, Set<AutosmsUrls> autosmsUrlList, String type, 
			String issuedTo, Users user , Long sentId, Long loyaltyId) {
		logger.info("entered getAutoSMSPhValue...");
		List<AutosmsSenturlShortcode> autoSmsSentUrlShortCodeObjList =  new ArrayList<AutosmsSenturlShortcode>();
		if(totalPhSet != null && totalPhSet.size() >0) {
			try {

				String value=Constants.STRING_NILL;

				String preStr = Constants.STRING_NILL; 

				for (String cfStr : totalPhSet) {
					logger.debug("<<<<   cfStr : "+ cfStr);
					preStr = cfStr;
					if(contact != null && cfStr.startsWith("GEN_")) {
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {

							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if



						if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email") ) {
							value = contact.getEmailId();
						}

						else if(cfStr.equalsIgnoreCase("firstName")) {
							value = contact.getFirstName();
						}
						else if(cfStr.equalsIgnoreCase("lastName"))	{
							value = contact.getLastName();
						}
						else if(cfStr.equalsIgnoreCase("addressOne")) {
							value = contact.getAddressOne();
						}
						else if(cfStr.equalsIgnoreCase("addressTwo")) {
							value = contact.getAddressTwo();
						}
						else if(cfStr.equalsIgnoreCase("city"))	{ 
							value = contact.getCity();
						}
						else if(cfStr.equalsIgnoreCase("state")) {
							value = contact.getState();
						}
						else if(cfStr.equalsIgnoreCase("country")) {
							value = contact.getCountry();
						}
						else if(cfStr.equalsIgnoreCase("pin")) {
							value = contact.getZip();
						}
						else if(cfStr.equalsIgnoreCase("phone")) {
							value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
						}
						else if(cfStr.equalsIgnoreCase("gender")) {
							value = contact.getGender();
						}

						else if(cfStr.equalsIgnoreCase("birthday") ) {

							value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);



						}
						else if(cfStr.equalsIgnoreCase("anniversary") ) {


							value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						else if(cfStr.equalsIgnoreCase("createdDate") ) {


							value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						else if(cfStr.equalsIgnoreCase("organizationName") ) {

							value = getUserOrganization(user, defVal);
						}
						

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {



							value = getStorePlaceholders(contact,cfStr, defVal);

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

							value = getLoyaltyPlaceholders(userId, contact,cfStr, defVal, loyaltyId, true);

						}

						else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)) {

							value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal);


						}

						else {
							value = Constants.STRING_NILL;
						}

						if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
						try {

							if(value != null && !value.trim().isEmpty()) {

								value = ( value.equals("--") &&  defVal != null) ? defVal : value;
								content = content.replace("["+preStr+"]", value);

							} else {

								value = defVal;
								content = content.replace("["+preStr+"]", value);

							}

						} catch (Exception e) {
							logger.error("Exception while adding the General Fields as place holders ", e);
						}
					} 
					//Changes to add mapped UDF fields as placeholders
					else if(contact != null && cfStr.startsWith(Constants.UDF_TOKEN) ) {

						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {
							/*defVal = cfStr.substring(defIndex+1);
									cfStr = cfStr.substring(0,defIndex);*/
							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if

						int UDFIdx = Integer.parseInt(cfStr.substring("UDF".length()));
						try {
							//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
							value = getConatctCustFields(contact, UDFIdx, null, defVal);

							if(value==null || value.isEmpty()) value=defVal;

						} catch (Exception e) {
							logger.error("Exception ::::", e);
							logger.info("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
							value = Constants.STRING_NILL;
						}

						if(value != null && !value.trim().isEmpty()) {

							//cfStr = cfStr.toLowerCase();
							content = content.replace("["+preStr+"]", value);

						} else {
							value =defVal ;
							content = content.replace("["+preStr+"]",value);
						}
					}
					else if(cfStr.startsWith("CC_")) {
						if(couponProvider == null) { //get coupon provider

							
							

							couponProvider = CouponProvider.getCouponProviderInstance(context, null);
							if(couponProvider == null) {
								if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
								continue;
							}

						}//if

						if(couponProvider.couponSet != null ) {

							if(!couponProvider.couponSet.contains(cfStr)) {

								couponProvider.couponSet.add(cfStr);
							}

						}
						if(contact != null && contact.getContactId() != null) {
							value = couponProvider.getAlreadyIssuedCoupon(cfStr, contact.getContactId());
						}
						if(value == null || value.isEmpty())value = couponProvider.getNextCouponCode(cfStr,"",type, issuedTo,sentId.longValue(), null,contact != null ? contact.getContactId() : null);


						if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Promo-code "+ value);

						if(value == null) value = Constants.STRING_NILL;

						content = content.replace("[" + cfStr + "]", value);

					}
					
					else if(cfStr.startsWith("REF_CC_")) {
						
						
						if(referralcodeProvider == null) { //get coupon provider
							referralcodeProvider = referralcodeProvider.getReferralcodeProviderInstance(context, null);
							if(referralcodeProvider == null) {
								if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
								continue;
								}
							}//if

						if(referralcodeProvider.couponSet != null ) {	
							if(!referralcodeProvider.couponSet.contains(cfStr)) {

								referralcodeProvider.couponSet.add(cfStr);
							}}



						value = referralcodeProvider.getNextCouponCode(cfStr,"",type, issuedTo,sentId.longValue(), null,contact != null ? contact.getContactId() : null);


						if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Promo-code "+ value);

						if(value == null) value = Constants.STRING_NILL;

						content = content.replace("[" + cfStr + "]", value);

					
					}else if(cfStr.startsWith("VC_")) { // APP-4083
						//cfstr will be [VC_1234]
						//will fetch with the 1234 id
						int beginIndex = cfStr.indexOf('_');
						String valueCode = cfStr.substring(++beginIndex);
				LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);	
				LoyaltyBalance loyaltyBalance = loyaltyBalanceDao.findByLoyaltyIdUserIdValueCode(userId,loyaltyId, valueCode);

						value = loyaltyBalance.getBalance().toString(); // need a method to get the value code or the code
						logger.info("logging the value of the loyalty value_code being replaced."+value);
						
						if(value == null) value = Constants.STRING_NILL;

						content = content.replace("[" + cfStr + "]", value);

						
					}
				} 
			}catch (Exception e) {
				logger.error("Exception while adding the Custom Fields as place holders ", e);
			}
		} // If PH exist
		
		
		//TODO need to create the sent shortcode object(id of smscampurl object,sent id, the actual code)
		logger.info("entering url block");
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
		for (AutosmsUrls eachSentUrl : autosmsUrlList) {
			String shortUrl = eachSentUrl.getShortUrl();
			boolean isDone = false ; 
			/*for (String barCodeUrl : barcodeUrlSet) {

				if(eachSentUrl.getShortUrl().equalsIgnoreCase(barCodeUrl)){
					isDone = true;
					break;
				}

			}*/

			if(!isDone) {

				String value = eachSentUrl.getShortCode();
				logger.info("url short code "+value);
				if(value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_COUPONPH)) continue;

				String sourceShortCode = value + Constants.ADDR_COL_DELIMETER +eachSentUrl.getId().longValue() + Constants.ADDR_COL_DELIMETER + sentId.longValue();
				logger.info("url sourceShortCode code "+sourceShortCode);

				/*List<String> md5List = Utility.couponGenarationCode(sourceShortCode + System.nanoTime(), 8);
				logger.info("url md5List code "+md5List);
				Iterator<String> iterator = md5List.iterator();
				String generatedShortCode = null;
				while (iterator.hasNext()) {

					generatedShortCode = OCConstants.SHORTURL_CODE_PREFIX_a + iterator.next();
					break;

				}*/
				String generatedShortCode = generateShortCode(OCConstants.SHORTURL_CODE_PREFIX_a);
				AutosmsSenturlShortcode smsCampaignSentUrlShortCode = new AutosmsSenturlShortcode();
				smsCampaignSentUrlShortCode.setAutoSmsQueueSentId(sentId);
				smsCampaignSentUrlShortCode.setOriginalShortCode(value);
				smsCampaignSentUrlShortCode.setGeneratedShortCode(generatedShortCode);
				smsCampaignSentUrlShortCode.setShortCodeId(eachSentUrl.getId());
				
				logger.info("url smsCampaignSentUrlShortCode "+smsCampaignSentUrlShortCode);

				autoSmsSentUrlShortCodeObjList.add(smsCampaignSentUrlShortCode);
				content = content.replace(shortUrl, appShortUrl + generatedShortCode);
				
				logger.info("url content "+content);
			}
		}
		logger.info("autoSmsSentUrlShort"+autoSmsSentUrlShortCodeObjList.size());
		if(autoSmsSentUrlShortCodeObjList.size() > 0){

			try {
				AutoSmsUrlShortCodeDaoForDML autoSmsSentUrlShortCodeDaoForDML = (AutoSmsUrlShortCodeDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTOSMS_SENTURLSHORTCODE_DAO_FOR_DML);
				autoSmsSentUrlShortCodeDaoForDML.saveByCollection(autoSmsSentUrlShortCodeObjList);
				logger.info("autoSmsSentUrlShortCodeDaoForDML saved "+content);
			} catch (Exception e) {
				logger.error("Exception while saving the url sent objects ", e);
			}
		}

		logger.info("after replacing phset inside method..."+content);
		return content;

	}
	// used for ses / sendgrid
	public  String[] getContactPhValue(Contacts contact, String tempHtmlContent,
			String tempTextContent, String tempCampSubject, Set<String> totalPhSet, String type, String issuedTo, Users user , 
			RecipientProvider provider, Long sentId ) {
		String[] contentsStrArr = null;
		ReferralcodesIssuedDao refcodesissued=null;
		try {
			refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<ReferralcodesIssued> refissuedobj=refcodesissued.getRefcodebycontactid(contact.getContactId());
		logger.info("refissuedobj is "+refissuedobj);

		
		logger.info("Entered in getContactPhValue method");
		try {
			logger.info("size of the ph is : "+totalPhSet.size());
		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			contentsStrArr = new String[4];

				String value=Constants.STRING_NILL;

				String preStr = Constants.STRING_NILL; 

				for (String cfStr : totalPhSet) {
					//	logger.debug("<<<<   cfStr : "+ cfStr);
					preStr = cfStr;
					
			//		logger.info("Contact placeholder : "+cfStr +","+preStr);

				try {
					if(cfStr.startsWith("GEN_")) {
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {

							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						//	logger.debug("definition value : "+defVal);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						//	logger.info("value in cfStr in replace method : "+cfStr);
						} // if
						value = getGeneralPlaceHolderVal(cfStr, contact, user, provider);
						
				//		logger.info("Replacing PH for ses: for contact : "+contact.getEmailId()+value);

						try {
							
							if(value != null && !value.trim().isEmpty()) {
								
								value = ( value.equals("--") &&  defVal != null) ? defVal : value;
								//cfStr = cfStr.toLowerCase();
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
								}
																					
							} else {
								
								value = defVal;
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
								}
								
							}
								
						} catch (Exception e) {
							logger.error("Exception while adding the General Fields as place holders ", e);
						}

					}else if(cfStr.startsWith("CC_")) {

						try{

							//String[] ccPhTokens = cfStr.split("_");
							//String baseccPh = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
							//String ccvalue = Constants.STRING_NILL;

							//if(couponPhMap1 != null && couponPhMap1.keySet()!= null && couponPhMap1.keySet().size() >0
							//		&& couponPhMap1.keySet().contains(baseccPh)){

							//	value = couponPhMap1.get(baseccPh);
							//}
							//else{

							value = getCouponPlaceHolderVal(cfStr, type, issuedTo, sentId , null,contact.getContactId());
							if(value == null) value = Constants.STRING_NILL;
					    	
				//	    	logger.info("Issuing the coupon ses : "+cfStr+":"+value);
					    	tempHtmlContent = tempHtmlContent.replace("[" + cfStr + "]", value);
							tempTextContent = tempTextContent.replace("[" + cfStr + "]", value);
				//			logger.info("Replacing PH : "+value+","+cfStr);

							/*
							 * i can save the coupon code in the list along with the particular tag. and then replace it with the original
							 * value in the sending campaign and substitution
							 */

							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+ cfStr+"]")) {
								tempCampSubject = tempCampSubject.replace("[" + cfStr+"]", value);
							}

						}catch(Exception e){
							logger.error("Exception in coupon placeholder........");
						}
					}
					
					
					
					else if(cfStr.startsWith("REF_CC_")) {
						
						logger.info("entering ref block SES email");
						
						if(refissuedobj!=null ) {
							
							logger.info("refissuedobj not equal to null "+refissuedobj);

							value = refissuedobj.get(0).getRefcode();
							
						}else {
						
						if(referralcodeProvider == null) { //get coupon provider
				                                                                                                                                        		
							referralcodeProvider = referralcodeProvider.getReferralcodeProviderInstance(context, null);
							if(referralcodeProvider == null) {
								if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
								continue;
								}
							}//if

						if(referralcodeProvider.couponSet != null ) {	
							if(!referralcodeProvider.couponSet.contains(cfStr)) {

								referralcodeProvider.couponSet.add(cfStr);
							}}


						
					
							value = referralcodeProvider.getNextCouponCode(cfStr,"",type, issuedTo,sentId.longValue(), null,contact != null ? contact.getContactId() : null);
						
						}
						logger.info("Entered value"+value);

						if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Promo-code "+ value);

						if(value == null) value = Constants.STRING_NILL;

						tempHtmlContent = tempHtmlContent.replace("[" + cfStr + "]", value);
						tempTextContent = tempTextContent.replace("[" + cfStr + "]", value);

						if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+ cfStr+"]")) {
							tempCampSubject = tempCampSubject.replace("[" + cfStr+"]", value);
						}

					
					}
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					else if(cfStr.startsWith(Constants.UDF_TOKEN)) {
					 	
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');
						
						if(defIndex != -1) {
							/*defVal = cfStr.substring(defIndex+1);
							cfStr = cfStr.substring(0,defIndex);*/
							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if
						
						int UDFIdx = Integer.parseInt(cfStr.substring("UDF".length()));
						try {
						//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
						value = getConatctCustFields(contact, UDFIdx, null, defVal);
				//		logger.info("Replacing PH : "+value+","+cfStr);

						if(value==null || value.isEmpty()) value=defVal;
						
					} catch (Exception e) {
						logger.error("Exception ::::", e);
						logger.info("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
						value = Constants.STRING_NILL;
					}
					
					if(value != null && !value.trim().isEmpty()) {
						
						//cfStr = cfStr.toLowerCase();
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
						tempTextContent = tempTextContent.replace("["+preStr+"]", value);
				//		logger.info("Replacing PH : "+value+","+cfStr);

						
						if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
							tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
						}
																			
					} else {
						
						
						value =defVal ;
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]",value);
						tempTextContent = tempTextContent.replace("["+preStr+"]", value);
				//		logger.info("Replacing PH : "+value+","+cfStr);

						
						if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
							tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
						}
						
					}
				}else if(cfStr.startsWith("MLS_")){
						
						value = getMilestonesPlaceHolderVal(cfStr, user, sentId , contact);
						String defVal="";
						if(value != null && !value.trim().isEmpty()) {
							
							//cfStr = cfStr.toLowerCase();
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							tempTextContent = tempTextContent.replace("["+preStr+"]", value);
					//		logger.info("Replacing PH : "+value+","+cfStr);

							
							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
								tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
						//		logger.info("Replacing PH : "+value+","+cfStr);

							}
							
																				
						}
						else {
							
							value = defVal;
							tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
							tempTextContent = tempTextContent.replace("["+preStr+"]", value);
							
							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
								tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
							}
					//		logger.info("Replacing PH : "+value);

						}


						
					}else {
						if(logger.isDebugEnabled()) logger.debug("<<<< --2-->>>");
						cfStr = cfStr.substring(3);
						// Removing if the PH if key is not found..
						tempHtmlContent = tempHtmlContent.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
						tempTextContent = tempTextContent.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
						
						if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+"CF_" + cfStr+"]")) {
							tempCampSubject = tempCampSubject.replace("["+"CF_" + cfStr+"]",Constants.STRING_NILL);
						}
						
						
					}
					
					//Placeholders key value pair preparation for weblink url replacement
					
					
					if(phKeyValue.length() > 0) phKeyValue.append(Constants.ADDR_COL_DELIMETER)  ;
					
					phKeyValue.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
					
		//		logger.info("replacing the weburl here :"+preStr +" , value : "+value);

				} catch (Exception e) {
					value = Constants.STRING_NILL;

				}
			}
				
				
				contentsStrArr[0] = tempHtmlContent;
				contentsStrArr[1] = tempTextContent;
				contentsStrArr[2] = tempCampSubject;
				if(phKeyValue.toString().trim().length() > 0) {
					
					contentsStrArr[3] = phKeyValue.toString();
					
				}else{
					
					contentsStrArr[3] = null;
				}
		}
		}catch(Exception e) {
			logger.error("Exception while replacing PH : "+e);
		}
		return contentsStrArr;
		
	}
	// This method is used to replace placeholders for emails - comment added by Sameera - ignore
	public  String[] getContactPhValue(Contacts contact, String tempHtmlContent,
			String tempTextContent, String tempCampSubject, Set<String> totalPhSet, String type, String issuedTo, Users user , Long sentId,Long loyaltyId  ) {
		String[] contentsStrArr = null;
		
		logger.info("Entered in getContactPhValue method");

		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			contentsStrArr = new String[4];


			try {

				String value=Constants.STRING_NILL;

				String preStr = Constants.STRING_NILL; 

				for (String cfStr : totalPhSet) {
					//	logger.debug("<<<<   cfStr : "+ cfStr);
					preStr = cfStr;
					if(contact != null && cfStr.startsWith("GEN_")) {
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {

							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if



						if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email") ) {
							value = contact.getEmailId();
						}

						else if(cfStr.equalsIgnoreCase("firstName")) {
							value = contact.getFirstName();
						}
						else if(cfStr.equalsIgnoreCase("lastName"))	{
							value = contact.getLastName();
						}
						else if(cfStr.equalsIgnoreCase("addressOne")) {
							value = contact.getAddressOne();
						}
						else if(cfStr.equalsIgnoreCase("addressTwo")) {
							value = contact.getAddressTwo();
						}
						else if(cfStr.equalsIgnoreCase("city"))	{ 
							value = contact.getCity();
						}
						else if(cfStr.equalsIgnoreCase("state")) {
							value = contact.getState();
						}
						else if(cfStr.equalsIgnoreCase("country")) {
							value = contact.getCountry();
						}
						else if(cfStr.equalsIgnoreCase("pin")) {
							value = contact.getZip();
						}
						else if(cfStr.equalsIgnoreCase("phone")) {
							value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
						}
						else if(cfStr.equalsIgnoreCase("gender")) {
							value = contact.getGender();
						}

						else if(cfStr.equalsIgnoreCase("birthday") ) {

							value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);



						}
						else if(cfStr.equalsIgnoreCase("anniversary") ) {


							value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						else if(cfStr.equalsIgnoreCase("createdDate") ) {


							value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						
						else if(cfStr.equalsIgnoreCase("organizationName") ) {


							value = getUserOrganization(user, defVal);


						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {



							value = getStorePlaceholders(contact,cfStr, defVal);

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

							value = getLoyaltyPlaceholders(user.getUserId(), contact,cfStr, defVal, loyaltyId, false);

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS) || cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) {
							defVal = getDefaultUserAddress(contact.getUsers());
							value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal);
							logger.info("CAMPAIGN_PH_STARTSWITH_LASETPURCHASE"+value);


						}
						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)) {
							defVal = getDefaultUserAddress(contact.getUsers());
                            value = getstoreaddress(contact,cfStr, defVal);
                            logger.info("CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE"+value);
                        }

						else {
							value = Constants.STRING_NILL;
						}

	
						  

							if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
							try {
								
								if(value != null && !value.trim().isEmpty()) {
									
									value = ( value.equals("--") &&  defVal != null) ? defVal : value;
									//cfStr = cfStr.toLowerCase();
									tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
									tempTextContent = tempTextContent.replace("["+preStr+"]", value);
									
									if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
										tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
									}
																						
								} else {
									
									value = defVal;
									tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
									tempTextContent = tempTextContent.replace("["+preStr+"]", value);
									
									if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
										tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
									}
									
								}
									
							} catch (Exception e) {
								logger.error("Exception while adding the General Fields as place holders ", e);
							}
						} 
						//Changes to add mapped UDF fields as placeholders
						else if(contact != null && cfStr.startsWith(Constants.UDF_TOKEN) ) {
							 	
								cfStr = cfStr.substring(4);
								String defVal="";
								int defIndex = cfStr.indexOf('=');
								
								if(defIndex != -1) {
									/*defVal = cfStr.substring(defIndex+1);
									cfStr = cfStr.substring(0,defIndex);*/
									defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
									cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
								} // if
								
								int UDFIdx = Integer.parseInt(cfStr.substring("UDF".length()));
								try {
								//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
								value = getConatctCustFields(contact, UDFIdx, null, defVal);
								
								if(value==null || value.isEmpty()) value=defVal;
								
							} catch (Exception e) {
								logger.error("Exception ::::", e);
								logger.info("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
								value = Constants.STRING_NILL;
							}
							
							if(value != null && !value.trim().isEmpty()) {
								
								//cfStr = cfStr.toLowerCase();
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
								}
																					
							} else {
								
								
								value =defVal ;
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]",value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
								}
								
							}
						}
						else if(cfStr.startsWith("CC_")) {
							if(couponProvider == null) { //get coupon provider
								
								
								couponProvider = CouponProvider.getCouponProviderInstance(context, null);
								if(couponProvider == null) {
									if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
									continue;
								}
								
							}//if
							
							if(couponProvider.couponSet != null ) {
								
								if(!couponProvider.couponSet.contains(cfStr)) {
									
									couponProvider.couponSet.add(cfStr);
								}
								
							}
							
							
							
							if(contact !=null && contact.getContactId() != null) {
								value = couponProvider.getAlreadyIssuedCoupon(cfStr, contact.getContactId());
							}
							if(value == null || value.isEmpty()) {
								value =couponProvider.getNextCouponCode(cfStr,campaignName,type, issuedTo,sentId.longValue(), null,contact != null ? contact.getContactId(): null);
							}
							
						
					    	if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Promo-code "+ value);
					    	
					    	
					    	if(value == null) value = Constants.STRING_NILL;
					    	
					    	
					    	tempHtmlContent = tempHtmlContent.replace("[" + cfStr + "]", value);
							tempTextContent = tempTextContent.replace("[" + cfStr + "]", value);

							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+ cfStr+"]")) {
								tempCampSubject = tempCampSubject.replace("[" + cfStr+"]", value);
							}
					    	
						}
						
						
						else if(cfStr.startsWith("REF_CC_")) {
							
							logger.info("entering ref block");
						
							if(referralcodeProvider == null) { //get coupon provider
					                                                                                                                                        		
								referralcodeProvider = referralcodeProvider.getReferralcodeProviderInstance(context, null);
								if(referralcodeProvider == null) {
									if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
									continue;
									}
								}//if

							if(referralcodeProvider.couponSet != null ) {	
								if(!referralcodeProvider.couponSet.contains(cfStr)) {

									referralcodeProvider.couponSet.add(cfStr);
								}}



							value = referralcodeProvider.getNextCouponCode(cfStr,"",type, issuedTo,sentId.longValue(), null,contact != null ? contact.getContactId() : null);

							logger.info("Entered value"+value);

							if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Promo-code "+ value);

							if(value == null) value = Constants.STRING_NILL;

							tempHtmlContent = tempHtmlContent.replace("[" + cfStr + "]", value);
							tempTextContent = tempTextContent.replace("[" + cfStr + "]", value);

							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+ cfStr+"]")) {
								tempCampSubject = tempCampSubject.replace("[" + cfStr+"]", value);
							}

						
						}
						
						else if(contact!= null &&cfStr.startsWith("MLS_")) {
							
							String defVal="";
							
							logger.info("Placeholder of the type ----MLS");
							
							value = getMilestonesPlaceHolderVal(cfStr, user, sentId , contact);
							
							logger.info("Value is returned----"+value);
							
							if(value != null && !value.trim().isEmpty()) {
								
								//cfStr = cfStr.toLowerCase();
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]",value);
								}
																					
							}
							else {
								
								value = defVal;
								tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
								tempTextContent = tempTextContent.replace("["+preStr+"]", value);
								
								if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+preStr+"]")) {
									tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
								}
								
							}

						}
						else {
							if(logger.isDebugEnabled()) logger.debug("<<<< --2-->>>");
							cfStr = cfStr.substring(3);
							// Removing if the PH if key is not found..
							tempHtmlContent = tempHtmlContent.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
							tempTextContent = tempTextContent.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
							
							if(tempCampSubject.trim().length() > 0 && tempCampSubject.contains("["+"CF_" + cfStr+"]")) {
								tempCampSubject = tempCampSubject.replace("["+"CF_" + cfStr+"]",Constants.STRING_NILL);
							}
							
							
						}
						
						//Placeholders key value pair preparation for weblink url replacement
						
						
						if(phKeyValue.length() > 0) phKeyValue.append(Constants.ADDR_COL_DELIMETER)  ;
						
						phKeyValue.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
						
						
						
					}	
					
					contentsStrArr[0] = tempHtmlContent;
					contentsStrArr[1] = tempTextContent;
					contentsStrArr[2] = tempCampSubject;
					if(phKeyValue.toString().trim().length() > 0) {
						
						contentsStrArr[3] = phKeyValue.toString();
						
					}else{
						
						contentsStrArr[3] = null;
					}
					
			} catch (Exception e) {
						logger.error("Exception while adding the Custom Fields as place holders ", e);
			}
		} // If PH exist
		
		
		return contentsStrArr;

	}
	
	private String getstoreaddress(Contacts contact, String placeholder, String defVal){
	    String storeNum = contact.getHomeStore();
	    if(orgId != null && storeNum != null) {
	        OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum );
	        return  getLastPurchaseStoreAddr( organizationStores, defVal);
	    }
	    return defVal;
	}
	
	private String getDefaultUserAddress(Users user) {
		String[] usraddr = {""};
		if(user != null) {
			if(user.getAddressOne()!=null && !user.getAddressOne().trim().equals("")){
				usraddr[0] = usraddr[0]  + user.getAddressOne(); 
			}
			if(user.getAddressTwo()!=null && !user.getAddressTwo().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getAddressTwo(); 
			}
			if(user.getCity()!=null && !user.getCity().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getCity(); 
			}
			if(user.getState()!=null && !user.getState().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getState(); 
			}
			if(user.getCountry()!=null && !user.getCountry().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getCountry(); 
			}
			if(user.getPinCode()!=null && !user.getPinCode().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getPinCode(); 
			}
			usraddr[0] = usraddr[0];
		}
		return usraddr[0];
	}

	/*
	 * replacePlaceHolders.getSubStitutions(contact, totalPhSet, substitutionValues, Constants.COUP_GENT_CAMPAIGN_TYPE_EMAIL,
				    					contact.getEmailId(), campaign.getUsers(), ((CampaignSent)contact.getTempObj()).getSentId());

	 */



	public String getSMSPlaceHolders(Contacts contact, 
			Set<String> totalPhSet, Set<String> urlSet, Set<SMSCampaignUrls> smsCampUrlList, 
			String type, String issuedTo, Users user , Long sentId ,
			SMSRecipientProvider smsRp , String smsContent) {

		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
		Set<String> barcodeUrlSet = new HashSet<String>();
		ReferralcodesIssuedDao refcodesissued=null;
		try {
			refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		List<SMSCampaignSentUrlShortCode> smsCampaignSentUrlShortCodeObjList =  new ArrayList<SMSCampaignSentUrlShortCode>();
		List<ReferralcodesIssued> refissuedobj=refcodesissued.getRefcodebycontactid(contact.getContactId());
		logger.info("refissuedobj is "+refissuedobj);

		
		
		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			List<Object> contentsStrArr = new ArrayList<Object>();
			for (String cfStr : totalPhSet) {

				logger.debug("<<<<   cfStr : "+ cfStr);
				String preStr = Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET;


				String value = Constants.STRING_NILL;

				try {
					if(cfStr.startsWith("GEN_")) {

						value = getSMSGeneralPlaceHolderVal(cfStr, contact, user, smsRp);

					}else if(cfStr.startsWith("CC_")) {

						try{

							value = getCouponPlaceHolderVal(cfStr, type, issuedTo, sentId , smsContent,contact.getContactId() );


						}catch(Exception e){
							logger.error("Exception in coupon placeholder........");
							value = Constants.STRING_NILL;
						}
						
					}	else if(cfStr.startsWith("REF_CC_")) {
						
						logger.info("entering ref block");

						if(refissuedobj!=null ) {
						logger.info("refissuedobj not equal to null "+refissuedobj);
						value = refissuedobj.get(0).getRefcode();
						}
						
						else {
							
							logger.info("entering ref block else");

							try{

							value = getreferralPlaceHolderVal(cfStr,type, issuedTo,sentId.longValue(), smsContent,contact != null ? contact.getContactId() : null);

						//	value = getreferralPlaceHolderVal(cfStr, type, issuedTo, sentId , null,contact.getContactId());

							}catch(Exception e){
								logger.error("Exception in coupon placeholder........");
								value = Constants.STRING_NILL;
							}
						}

					
					}
					
					else if(cfStr.startsWith(Constants.UDF_TOKEN)) {

						String defVal="";
						try {
							cfStr = cfStr.substring(4);
							int defIndex = cfStr.indexOf('=');

							if(defIndex != -1) {
								/*defVal = cfStr.substring(defIndex+1);
									cfStr = cfStr.substring(0,defIndex);*/
								defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
								cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
							} // if

							value = getConatctCustFields(contact, Integer.parseInt(cfStr.substring("UDF".length())), null, defVal) ;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							value = defVal;
						}


					}

				} catch (Exception e) {
					value = Constants.STRING_NILL;

				}


				if(cfStr.startsWith("CC_")) {
					if(value == null || value.trim().isEmpty()){
						smsContent = smsContent.replace(appShortUrl+preStr, "");
						smsContent = smsContent.replace(preStr, "");
						continue;
					}

					if(smsCampUrlList != null && smsCampUrlList.size() > 0) {

						for (SMSCampaignUrls SMSCampaignUrl : smsCampUrlList) {
							String shortUrl = SMSCampaignUrl.getShortUrl();
							if(shortUrl.endsWith(preStr)) {

								barcodeUrlSet.add(shortUrl);
								//generate the shortcode

								String sourceShortCode = value + Constants.ADDR_COL_DELIMETER +
										SMSCampaignUrl.getId().longValue() + Constants.ADDR_COL_DELIMETER + sentId.longValue();

								List<String> md5List = Utility.couponGenarationCode(sourceShortCode + System.nanoTime(), 8);
								Iterator<String> iterator = md5List.iterator();
								String generatedShortCode = null;
								while (iterator.hasNext()) {

									generatedShortCode = OCConstants.SHORTURL_CODE_PREFIX_S + iterator.next();
									break;

								}
								SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode = new SMSCampaignSentUrlShortCode();
								smsCampaignSentUrlShortCode.setSentId(sentId);
								smsCampaignSentUrlShortCode.setOriginalShortCode(value);
								smsCampaignSentUrlShortCode.setGeneratedShortCode(generatedShortCode);
								smsCampaignSentUrlShortCode.setSmsCampaignUrlId(SMSCampaignUrl.getId());

								smsCampaignSentUrlShortCodeObjList.add(smsCampaignSentUrlShortCode);

								smsContent = smsContent.replace(shortUrl, appShortUrl + generatedShortCode);
							}//barcode url


						}//for
						if(value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_LINEAR) || 
								value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_QR) ||
								value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_DATAMATRIX) || 
								value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_BARCODE_TYPE_AZETEC)){

							smsContent = smsContent.replace(preStr, value.substring(1));//becoz for sms extra char will be given as prefix to couponcode
						}
					}//if

					//smsContent = smsContent.replace(preStr, value);//becoz for sms extra char will be given as prefix to couponcode
					//continue;

					/*String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim(); 
						String barcodeLink = appShortUrl.trim()+preStr.trim();

						if(value == null || value.trim().isEmpty()){
							smsContent = smsContent.replace(appShortUrl+preStr, "");
							smsContent = smsContent.replace(preStr, "");
						}
						else if(smsContent.contains(barcodeLink)){	

							//if(barcodeUrlSet == null) barcodeUrlSet = new HashSet<String>();
							//if(urlSet != null && urlSet.contains(o))
							smsContent = smsContent.replace(barcodeLink, appShortUrl+value);
							smsContent = smsContent.replace(preStr, value.substring(1));
						}
						else {
							smsContent = smsContent.replace(preStr, value); 
						}*/
					//continue;
				}

				smsContent = smsContent.replace(preStr, value);


			}//for



		}//if

		//TODO need to create the sent shortcode object(id of smscampurl object,sent id, the actual code)
		for (SMSCampaignUrls eachSentUrl : smsCampUrlList) {
			String shortUrl = eachSentUrl.getShortUrl();
			boolean isDone = false ; 
			for (String barCodeUrl : barcodeUrlSet) {

				if(eachSentUrl.getShortUrl().equalsIgnoreCase(barCodeUrl)){
					isDone = true;
					break;
				}

			}

			if(!isDone) {

				String value = eachSentUrl.getShortCode();
				if(value.startsWith(OCConstants.SHORTURL_CODE_PREFIX_COUPONPH)) continue;

				/*String sourceShortCode = value + Constants.ADDR_COL_DELIMETER +
						eachSentUrl.getId().longValue() + Constants.ADDR_COL_DELIMETER + sentId.longValue();

				List<String> md5List = Utility.couponGenarationCode(sourceShortCode + System.nanoTime(), 8);
				Iterator<String> iterator = md5List.iterator();
				String generatedShortCode = null;
				while (iterator.hasNext()) {

					generatedShortCode = OCConstants.SHORTURL_CODE_PREFIX_S + iterator.next();
					break;

				}*/
				String generatedShortCode = generateShortCode(OCConstants.SHORTURL_CODE_PREFIX_S);
				
				SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode = new SMSCampaignSentUrlShortCode();
				smsCampaignSentUrlShortCode.setSentId(sentId);
				smsCampaignSentUrlShortCode.setOriginalShortCode(value);
				smsCampaignSentUrlShortCode.setGeneratedShortCode(generatedShortCode);
				smsCampaignSentUrlShortCode.setSmsCampaignUrlId(eachSentUrl.getId());

				smsCampaignSentUrlShortCodeObjList.add(smsCampaignSentUrlShortCode);

				smsContent = smsContent.replace(shortUrl, appShortUrl + generatedShortCode);


			}

		}

		if(smsCampaignSentUrlShortCodeObjList.size() > 0){

			try {
				SMSCampaignSentUrlShortCodeDao smsCampaignSentUrlShortCodeDao = 
						(SMSCampaignSentUrlShortCodeDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSCAMPAIGNSENTURLSHORTCODE_DAO);
				SMSCampaignSentUrlShortCodeDaoForDML smsCampaignSentUrlShortCodeDaoForDML = 
						(SMSCampaignSentUrlShortCodeDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSCAMPAIGNSENTURLSHORTCODE_DAO_FOR_DML);
				//smsCampaignSentUrlShortCodeDao.saveByCollection(smsCampaignSentUrlShortCodeObjList);
				smsCampaignSentUrlShortCodeDaoForDML.saveByCollection(smsCampaignSentUrlShortCodeObjList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while saving the url sent objects ", e);
			}
		}
		return smsContent;


	}
	
	public String getWAPlaceHolders(Contacts contact, 
			Set<String> totalPhSet, 
			String type, String issuedTo, Users user , Long sentId ,
			WARecipientProvider waRp , String waJsonContent) {

		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			List<Object> contentsStrArr = new ArrayList<Object>();
			for (String cfStr : totalPhSet) {

				logger.debug("<<<<   cfStr : "+ cfStr);
				String preStr = Constants.LT+cfStr+Constants.GT;


				String value = Constants.STRING_NILL;

				try {
					if(cfStr.startsWith("GEN_")) {

						value = getWAGeneralPlaceHolderVal(cfStr, contact, user, waRp);

					}else 
					if(cfStr.startsWith("CC_")) {

						try{

							value = getCouponPlaceHolderVal(cfStr, type, issuedTo, sentId , waJsonContent,contact.getContactId() );
							logger.debug("replace coupon code="+value);

						}catch(Exception e){
							logger.error("Exception in coupon placeholder........");
							value = Constants.STRING_NILL;
						}
					}else if(cfStr.startsWith(Constants.UDF_TOKEN)) {

						String defVal="";
						try {
							cfStr = cfStr.substring(4);
							int defIndex = cfStr.indexOf('=');

							if(defIndex != -1) {
								defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
								cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
							} // if

							value = getConatctCustFields(contact, Integer.parseInt(cfStr.substring("UDF".length())), null, defVal) ;
						} catch (Exception e) {
							value = defVal;
						}

					}

				} catch (Exception e) {
					value = Constants.STRING_NILL;

				}

				waJsonContent = waJsonContent.replace(preStr, value);


			}//for

		}//if

		return waJsonContent;


	}
	
	public List<Object> getSubStitutions(JSONObject substitutionVal, Contacts contact, 
			Set<String> totalPhSet, String type, String issuedTo, Users user , Long sentId ,
			RecipientProvider rp, ContactPHValue contactPHValue ) {

		ReferralcodesIssuedDao refcodesissued=null;
		try {
			refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<ReferralcodesIssued> refissuedobj=refcodesissued.getRefcodebycontactid(contact.getContactId());
		logger.info("refissuedobj is "+refissuedobj);

		
		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			List<Object> contentsStrArr = new ArrayList<Object>();

			for (String cfStr : totalPhSet) {

				//	logger.debug("<<<<   cfStr : "+ cfStr);
				String preStr = Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET;

				JSONArray subValJsonObject = (JSONArray)substitutionVal.get(preStr);
				if(subValJsonObject == null ) continue;

				String value = Constants.STRING_NILL;

				try {
					if(cfStr.startsWith("GEN_")) {

						value = getGeneralPlaceHolderVal(cfStr, contact, user, rp, contactPHValue);

					}else if(cfStr.startsWith("CC_")) {

						try{
							//String[] ccPhTokens = cfStr.split("_");
							//String baseccPh = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
							//String ccvalue = Constants.STRING_NILL;

							//if(couponPhMap1 != null && couponPhMap1.keySet()!= null && couponPhMap1.keySet().size() >0
							//		&& couponPhMap1.keySet().contains(baseccPh)){

							//	value = couponPhMap1.get(baseccPh);
							//}
							//else{

							value = getCouponPlaceHolderVal(cfStr, type, issuedTo, sentId , null,contact.getContactId());
							//	couponPhMap1.put(baseccPh, value);
							//}

							/*String[] ccPhTokens = cfStr.split("_");
							if(ccPhTokens != null && ccPhTokens.length == 6){
								String COUPON_CODE_URL = PropertyUtil.getPropertyValue("CouponCodeUrl").replace("|^code^|",value)
										.replace("|^width^|", ccPhTokens[4]).replace("|^height^|", ccPhTokens[5])
										.replace("|^type^|", ccPhTokens[3]);
								String bcstr = "<img width='"+ccPhTokens[4]+"' height= '"+ccPhTokens[5]+"' src= '"+COUPON_CODE_URL+"' />";
								value = bcstr;
							}*/

							String coupStr = contactPHValue.getCouponPHStr();

							if(coupStr == null) contactPHValue.setCouponPHStr( value);

							else if(coupStr != null && !coupStr.isEmpty() ){

								coupStr += Constants.DELIMETER_DOUBLECOLON+value; 
								contactPHValue.setCouponPHStr( coupStr);
							}
						}catch(Exception e){
							logger.error("Exception in coupon placeholder........");
						}
				
					}
					else if(cfStr.startsWith("REF_CC_")) {

					logger.info("entering ref block");

					if(refissuedobj!=null ) {
						
					logger.info("refissuedobj not equal to null "+refissuedobj);

					value = refissuedobj.get(0).getRefcode();
					
					}else {
						
					logger.info("refissuedobj is null "+refissuedobj);

					value = getreferralPlaceHolderVal(cfStr, type, issuedTo, sentId , null,contact.getContactId());
					
					}
					
					logger.info("value of referral"+value);
					String coupStr = contactPHValue.getCouponPHStr();

					if(coupStr == null) contactPHValue.setCouponPHStr( value);

					else if(coupStr != null && !coupStr.isEmpty() ){

						coupStr += Constants.DELIMETER_DOUBLECOLON+value; 
						contactPHValue.setCouponPHStr( coupStr);
					}
					
					
					}
						else if(cfStr.startsWith(Constants.UDF_TOKEN)) {

						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {
							/*defVal = cfStr.substring(defIndex+1);
								cfStr = cfStr.substring(0,defIndex);*/
							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if

						value = getConatctCustFields(contact, Integer.parseInt(cfStr.substring("UDF".length())), contactPHValue, defVal) ;

						if(value==null || value.isEmpty()) value=defVal;

					}else if(cfStr.startsWith("MLS_")){
						
						value = getMilestonesPlaceHolderVal(cfStr, user, sentId , contact);

						
					}

				} catch (Exception e) {
					
					logger.info("exception is "+e);
					value = Constants.STRING_NILL;

				}

				subValJsonObject.add(value);
				substitutionVal.put(preStr, subValJsonObject);

				if(phKeyValue.length() > 0) phKeyValue.append(Constants.ADDR_COL_DELIMETER)  ;

				phKeyValue.append( preStr +  Constants.DELIMETER_DOUBLECOLON + value);

			}//for


			contentsStrArr.add(substitutionVal);


			if(phKeyValue.toString().trim().length() > 0) {

				contentsStrArr.add(phKeyValue.toString());

			}else{

				contentsStrArr.add(null);
			}

			return contentsStrArr;

		}//if
		return null;

	}//getSubStitutions
	private String getGeneralPlaceHolderVal(String cfStr, Contacts contact,
			Users user, RecipientProvider rp) {
		
		


		String value=Constants.STRING_NILL;

	//	cfStr = cfStr.substring(4);
		String defVal="";
		int defIndex = cfStr.indexOf('=');

		if(defIndex != -1) {
			defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
			cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
			logger.debug("DefValue : "+defVal+" , cfStr "+cfStr);
		} // if

		logger.info("here is the placeholder to replace : "+cfStr);

		if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email")){
			value = contact.getEmailId()==null ? defVal:contact.getEmailId();
		}
		/*else if(cfStr.equalsIgnoreCase("email")) {
			value = (contact.getEmailId();==null )? defVal :contact.getEmailId();
			logger.debug("-----------value---------"+value);
			contactPHValue.setEmail(value);
		}*/
		else if(cfStr.equalsIgnoreCase("firstName")){
			value = contact.getFirstName()==null ? defVal :contact.getFirstName();
			logger.info("value : "+contact.getFirstName());
		}
		else if(cfStr.equalsIgnoreCase("lastName")) {
			value = contact.getLastName()==null ? defVal :contact.getLastName();
			logger.info("value : "+contact.getLastName());

		}
		else if(cfStr.equalsIgnoreCase("addressOne")) {
			value = contact.getAddressOne()==null ?defVal :contact.getAddressOne();
			logger.info("value : "+contact.getAddressOne());

		}
		else if(cfStr.equalsIgnoreCase("addressTwo")) {
			value = contact.getAddressTwo()==null ? defVal :contact.getAddressTwo();
			logger.info("value : "+contact.getAddressTwo());

		}
		else if(cfStr.equalsIgnoreCase("city")) {
			value = contact.getCity()==null ? defVal :contact.getCity();
			logger.info("value : "+contact.getCity());

		}
		else if(cfStr.equalsIgnoreCase("state")) {
			value = contact.getState()==null ? defVal :contact.getState();
			logger.info("value : "+contact.getState());

		}
		else if(cfStr.equalsIgnoreCase("country")) {
			value = contact.getCountry()==null ? defVal :contact.getCountry();
		}
		/*else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getPin() != 0 ? contact.getPin()+Constants.STRING_NILL : Constants.STRING_NILL;
		else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getPhone()!= null && contact.getPhone() != 0 ? contact.getPhone()+Constants.STRING_NILL : Constants.STRING_NILL;*/
		else if(cfStr.equalsIgnoreCase("pin")) {
			value = contact.getZip()==null ? defVal :contact.getZip();
			logger.info("value : "+contact.getZip());

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

			/*if(contact.getAnniversary() != null) {*/

			value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			/*}

			else{

				value=defVal;
			}*/
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

				value = getDefaultStorePhValue(cfStr, user, null, defVal );
				return value;
			}

			value = ExternalSMTPSender.getStorePlaceholder(contact.getHomeStore(), cfStr, null, defVal);

		}

		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE)) {

			value = ExternalSMTPSender.getStoreAddress(contact.getHomeStore(), true);

		}
		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)) {

			value = ExternalSMTPSender.getStoreAddress(contact.getHomeStore(), true);

		}


		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) {

			value = rp.getlastPurchaseStore(contact.getContactId().longValue()+Constants.STRING_NILL, true, defVal);
		}
		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

			value = rp.getLoyaltyPlaceHolderVal(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr, null, defVal);

		}
		else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE) ) {

			value = rp.getlastPurchaseDetails(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr, null, defVal);//(contact.getContactId().longValue()+Constants.STRING_NILL, false, defVal);
			//contactPHValue.setLastPurchaseStoreAddress(value);


		}

		if(value == null || value.trim().isEmpty()) value = defVal;
		return value;

	
	}

		
// replacing the contacts basic info ph(email, mobile,address.. so on )- comment added by sameera.
	private String getGeneralPlaceHolderVal(String cfStr, Contacts contact,
			Users user, RecipientProvider rp, ContactPHValue contactPHValue ) {

		String value=Constants.STRING_NILL;

		cfStr = cfStr.substring(4);
		String defVal="";
		int defIndex = cfStr.indexOf('=');

		if(defIndex != -1) {
			defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
			cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
		} // if



		if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email")){
			value = contact.getEmailId()==null ? defVal:contact.getEmailId();
			contactPHValue.setEmail(value );
		}
		/*else if(cfStr.equalsIgnoreCase("email")) {
			value = (contact.getEmailId();==null )? defVal :contact.getEmailId();
			logger.debug("-----------value---------"+value);
			contactPHValue.setEmail(value);
		}*/
		else if(cfStr.equalsIgnoreCase("firstName")){
			value = contact.getFirstName()==null ? defVal :contact.getFirstName();
			contactPHValue.setFirstName(value);
		}
		else if(cfStr.equalsIgnoreCase("lastName")) {
			value = contact.getLastName()==null ? defVal :contact.getLastName();
			contactPHValue.setLastName(value );
		}
		else if(cfStr.equalsIgnoreCase("addressOne")) {
			value = contact.getAddressOne()==null ?defVal :contact.getAddressOne();
			contactPHValue.setAddressOne(value );
		}
		else if(cfStr.equalsIgnoreCase("addressTwo")) {
			value = contact.getAddressTwo()==null ? defVal :contact.getAddressTwo();
			contactPHValue.setAddressTwo(value);
		}
		else if(cfStr.equalsIgnoreCase("city")) {
			value = contact.getCity()==null ? defVal :contact.getCity();
			contactPHValue.setCity(value);
		}
		else if(cfStr.equalsIgnoreCase("state")) {
			value = contact.getState()==null ? defVal :contact.getState();
			contactPHValue.setState(value );
		}
		else if(cfStr.equalsIgnoreCase("country")) {
			value = contact.getCountry()==null ? defVal :contact.getCountry();
			contactPHValue.setCountry(value );
		}
		/*else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getPin() != 0 ? contact.getPin()+Constants.STRING_NILL : Constants.STRING_NILL;
		else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getPhone()!= null && contact.getPhone() != 0 ? contact.getPhone()+Constants.STRING_NILL : Constants.STRING_NILL;*/
		else if(cfStr.equalsIgnoreCase("pin")) {
			value = contact.getZip()==null ? defVal :contact.getZip();
			contactPHValue.setPin(value );

		}
		else if(cfStr.equalsIgnoreCase("phone")) {
			value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : defVal;
			contactPHValue.setPhone(value );
		}
		else if(cfStr.equalsIgnoreCase("gender")) {
			value = contact.getGender()==null ? defVal :contact.getGender();
			contactPHValue.setGender(value );
		}
		else if(cfStr.equalsIgnoreCase("birthday") ) {

			value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			value = ( value.equals("--") &&  defVal != null) ? defVal : value;	
			contactPHValue.setBirthday(value);
		}
		else if(cfStr.equalsIgnoreCase("anniversary") ) {

			/*if(contact.getAnniversary() != null) {*/

			value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			/*}

			else{

				value=defVal;
			}*/
			value = ( value.equals("--") &&  defVal != null) ? defVal : value;

			contactPHValue.setAnniversary(value);
		}
		else if(cfStr.equalsIgnoreCase("createdDate") ) {


			value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
			value = ( value.equals("--") &&  defVal != null) ? defVal : value;
			contactPHValue.setCreatedDate(value);
		}
		else if(cfStr.equalsIgnoreCase("organizationName") ) {
			value = getUserOrganization(user, defVal);
		}
		
		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {
			String posLocId = contact.getHomeStore();
			if(posLocId == null) {

				value = getDefaultStorePhValue(cfStr, user, contactPHValue, defVal );
				return value;
			}

			value = ExternalSMTPSender.getStorePlaceholder(contact.getHomeStore(), cfStr, contactPHValue, defVal);

		}

		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE)) {

			value = ExternalSMTPSender.getStoreAddress(contact.getHomeStore(), true);

			contactPHValue.setContactHomeStore(value);
		}
		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)) {

			value = ExternalSMTPSender.getStoreAddress(contact.getHomeStore(), true);

			contactPHValue.setContactHomeStore(value);
		}


		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) {

			value = rp.getlastPurchaseStore(contact.getContactId().longValue()+Constants.STRING_NILL, true, null);
			contactPHValue.setContactLastPurchasedStore(value);
		}
		else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

			value = rp.getLoyaltyPlaceHolderVal(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr, contactPHValue, defVal);

		}
		else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE) ) {

			value = rp.getlastPurchaseDetails(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr, contactPHValue, defVal);//(contact.getContactId().longValue()+Constants.STRING_NILL, false, defVal);
			//contactPHValue.setLastPurchaseStoreAddress(value);


		}

		if(value == null || value.trim().isEmpty()) value = defVal;
		return value;

	}

	private String getSMSGeneralPlaceHolderVal(String cfStr, Contacts contact,
			Users user, SMSRecipientProvider smsRp ) {

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
			/*else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getPin() != 0 ? contact.getPin()+Constants.STRING_NILL : Constants.STRING_NILL;
			else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getPhone()!= null && contact.getPhone() != 0 ? contact.getPhone()+Constants.STRING_NILL : Constants.STRING_NILL;*/
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

				value = SMSCampaignSubmitter.getStorePlaceholder(contact.getHomeStore(), cfStr);

			}
			else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

				value = smsRp.getLoyaltyPlaceHolderVal(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr);

			}
			else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE) ) {

				value = smsRp.getlastPurchaseDetails(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr);//(contact.getContactId().longValue()+Constants.STRING_NILL, false, defVal);
				//contactPHValue.setLastPurchaseStoreAddress(value);


			}


			if(value == null || value.trim().isEmpty()) value = defVal;
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return defVal;
		}

	}//getSMSGeneralPlaceHolderVal
	
	private String getWAGeneralPlaceHolderVal(String cfStr, Contacts contact,
			Users user, WARecipientProvider waRp ) {

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
			/*else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getPin() != 0 ? contact.getPin()+Constants.STRING_NILL : Constants.STRING_NILL;
			else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getPhone()!= null && contact.getPhone() != 0 ? contact.getPhone()+Constants.STRING_NILL : Constants.STRING_NILL;*/
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

				value = WACampaignSubmitter.getStorePlaceholder(contact.getHomeStore(), cfStr);

			}
			else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

				value = waRp.getLoyaltyPlaceHolderVal(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr);

			}
			else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE) ) {

				value = waRp.getlastPurchaseDetails(contact.getContactId().longValue()+Constants.STRING_NILL, cfStr);//(contact.getContactId().longValue()+Constants.STRING_NILL, false, defVal);
				//contactPHValue.setLastPurchaseStoreAddress(value);


			}


			if(value == null || value.trim().isEmpty()) value = defVal;
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return defVal;
		}

	}//getWAGeneralPlaceHolderVal



	private String getDefaultStorePhValue(String placeholder, Users user, ContactPHValue contactPHValue, String defVal) {

		String retVal = Constants.STRING_NOTAVAILABLE;

		if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORENAME)) { 
			//retVal = Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null) contactPHValue.setStoreName(defVal);
		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREMANAGER)){
			//retVal = user.getUserName() != null ? Utility.getOnlyUserName(user.getUserName()) : Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null) contactPHValue.setStoreManager(defVal);
		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREEMAIL)){
			//retVal = user.getEmailId() != null  ? user.getEmailId() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null) contactPHValue.setStoreEmail(defVal);
		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREPHONE)){
			//retVal =user.getPhone() != null  ? user.getPhone() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null) contactPHValue.setStorePhone(defVal);


		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTREET)){
			//retVal = user.getAddressOne() != null  ? user.getAddressOne() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null)contactPHValue.setStoreStreet(defVal);
		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORECITY)){
			//retVal = user.getCity() != null  ? user.getCity() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null)contactPHValue.setStoreCity(defVal);
		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTATE)){
			//retVal = user.getState() != null  ? user.getState() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null)contactPHValue.setStoreState(defVal);

		}
		else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREZIP)){
			//retVal = user.getAddressOne() != null  ? user.getAddressOne() :  Constants.STRING_NOTAVAILABLE;
			if(contactPHValue != null)contactPHValue.setStoreZip(defVal);
		}

		/*if(retVal != null) return retVal;
		else */
		return defVal;

	}

	private String getreferralPlaceHolderVal( String cfStr,  String type, String issuedTo,  Long sentId , String smsContent,Long contactId) {

		logger.info("entering getreferral"+contactId);

		String value=Constants.STRING_NILL;


		if(referralcodeProvider == null) { //get coupon provider

			logger.info("entering referralcodeProvider");

			referralcodeProvider = ReferralcodeProvider.getReferralcodeProviderInstance(context, null);
		//	referralcodeProvider = new ReferralcodeProvider(this.context);
			
			logger.info("referralcodeProvider value"+referralcodeProvider);

			if(referralcodeProvider == null) {
				logger.info("No Coup provider found....");
				return value;
			}

		}//if

		if(referralcodeProvider.couponSet != null ) {

			logger.info("entering couponset getreferral");

			if(!referralcodeProvider.couponSet.contains(cfStr)) {

				referralcodeProvider.couponSet.add(cfStr);
			}

		}
		logger.info("entering getreferral value"+value);
		
		//condition to check with CID
		
		
		value = referralcodeProvider.getNextCouponCode(cfStr,campaignName,type, issuedTo,sentId.longValue(), smsContent,contactId,true);

		logger.info("entering getreferral value"+value);
 


		//logger.debug(">>>>>>>>> Promo-code "+ value);


		if(value == null) value = Constants.STRING_NILL;


		return value;


	}
	
	
	
	
	private String getCouponPlaceHolderVal( String cfStr,  String type, String issuedTo,  Long sentId , String smsContent,Long contactId) {


		String value=Constants.STRING_NILL;


		if(couponProvider == null) { //get coupon provider


			couponProvider = CouponProvider.getCouponProviderInstance(context, null);
			if(couponProvider == null) {
				logger.info("No Coup provider found....");
				return value;
			}

		}//if

		if(couponProvider.couponSet != null ) {

			if(!couponProvider.couponSet.contains(cfStr)) {

				couponProvider.couponSet.add(cfStr);
			}

		}
		
		if(contactId != null) {
			value = couponProvider.getAlreadyIssuedCoupon(cfStr, contactId);
		}
		if(value == null || value.isEmpty())value = couponProvider.getNextCouponCode(cfStr, campaignName, type, issuedTo, sentId.longValue(), smsContent, contactId);



		//logger.debug(">>>>>>>>> Promo-code "+ value);


		if(value == null) value = Constants.STRING_NILL;


		return value;


	}
	
	private String getMilestonesPlaceHolderVal(String cfstr, Users user , Long sentId, Contacts contact){
		//  |^MLS_LB_8^| |^MLS_LP_2^| |^MLS_LPT_12^|
			String value=Constants.STRING_NILL;		
			String defvalue=Constants.STRING_NILL;		

			cfstr = cfstr.substring(4);
			
			Double milestone=0.0 ;
			Double threshold =0.0 ;
			Double current_balance =0.0 ;			
				
			ContactsLoyalty contactsLoyalty = null;
			contactsLoyalty = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
			logger.info("contact_id======"+contact.getContactId());
		
			if(cfstr.contains("LPT"))
			{
			try{
				cfstr = cfstr.substring(4);
				Long tier_id = Long.parseLong(cfstr);
				LoyaltyProgramTierDao loyaltyProgramTierDao;
				loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				
				LoyaltyProgramTier MilestoneTier =loyaltyProgramTierDao.findByTierId(tier_id); 
				
				logger.info("Milestone tier-----"+tier_id);
				logger.info("tier id of the contact======"+contactsLoyalty.getProgramTierId());
				LoyaltyProgramTier currtier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());
				if(currtier == null || currtier.getTierUpgdConstraint() == null || currtier.getTierUpgdConstraintValue() == null) return defvalue;
				
				logger.info("The contact currently is in the tier:----"+currtier.getTierType());
				
				threshold = currtier.getTierUpgdConstraintValue();
				
				logger.info("to upgrade to the next tier TierUpgdConstraintValue is : ---"+threshold);
				List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
				if (tempTiersList == null || tempTiersList.size() <= 0) {
					return defvalue;
				}
				else if (tempTiersList.size() >= 1) {
					Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
						@Override
						public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

							int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
							int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
							if(num1 < num2){
								return -1;
							}
							else if(num1 == num2){
								return 0;
							}
							else{
								return 1;
							}
						}
					});
				}
				
				/*List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
				boolean flag = false;
				for(LoyaltyProgramTier tier : tempTiersList) {
					logger.info("tier level : "+tier.getTierType());
					if(currtier.getTierType().equalsIgnoreCase(tier.getTierType())){
						flag = true;
					}
					if(flag){
						tiersList.add(tier);
					}
				}*/

				//Prepare eligible tiers map
				Iterator<LoyaltyProgramTier> iterTier = tempTiersList.iterator();
				Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
				LoyaltyProgramTier prevtier = null;
				LoyaltyProgramTier nexttier = null;

				/*while(iterTier.hasNext()){
					nexttier = iterTier.next();
					if((Integer.valueOf(currtier.getTierType().substring(5))+1)==Integer.valueOf(nexttier.getTierType().substring(5)) && 
							(currtier.getTierType().equalsIgnoreCase(prevtier.getTierType()))){
						eligibleMap.put(nexttier, currtier);
					}
					 if(currtier.getTierType().equals(nexttier.getTierType())){
						 eligibleMap.put(nexttier, null);
					}
					else{
						if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
								== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
							eligibleMap.put(nexttier, prevtier);
							logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
							break;
						}
					}
					prevtier = nexttier;
				}*/
				while(iterTier.hasNext()){
					nexttier = iterTier.next();
					if((Integer.valueOf(currtier.getTierType().substring(5))+1)==Integer.valueOf(nexttier.getTierType().substring(5)) && 
							(currtier.getTierType().equalsIgnoreCase(prevtier.getTierType()))){
						//eligibleMap.put(nexttier, currtier);
						break;
					}
					prevtier = nexttier;
				}
				logger.info("previous tier, next tier---------"+currtier.getTierType()  +nexttier.getTierType() +MilestoneTier.getTierType());
			//	logger.info("Eligible map is -------"+eligibleMap);
				if(!currtier.getTierType().equalsIgnoreCase(MilestoneTier.getTierType()) &&( (Integer.valueOf(currtier.getTierType().substring(5))+1) 
						== Integer.valueOf(MilestoneTier.getTierType().substring(5)) )){
					
					logger.info("Inside the if block...");
					//LoyaltyProgramTier newTier = applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currtier,eligibleMap);
					//if(newTier.getTierType().equalsIgnoreCase(MilestoneTier.getTierType())){
						
						if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(currtier.getTierUpgdConstraint())){
							logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);

						   current_balance = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
						}
						else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(currtier.getTierUpgdConstraint())){
							logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
							
						//	current_balance = contactsLoyalty.getCummulativePurchaseValue()== null ? 0.00 : contactsLoyalty.getCummulativePurchaseValue();
							current_balance = LoyaltyProgramHelper.getLPV(contactsLoyalty);

							//LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
							//current_balance = Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
					//		current_balance = Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
							//logger.info("purchase value = "+totPurchaseValue);
						}
						else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(currtier.getTierUpgdConstraint())){
							
							
							ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
							LoyaltyProgramTier nextKeyTier = null;
							/*while(it.hasPrevious()){
								nextKeyTier = it.previous();
								logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
								logger.info("-------------currTier::"+currtier.getTierType());
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();
								endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

								String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
								String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);

								LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
								current_balance = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));
						         }	*/	
								nextKeyTier = MilestoneTier;
								logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
								logger.info("-------------currTier::"+currtier.getTierType());
								Calendar startCal = Calendar.getInstance();
								Calendar endCal = Calendar.getInstance();
								logger.info(nextKeyTier);
							//	logger.info("Map--------"+eligibleMap.get(nextKeyTier));
							//	logger.info(eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue());
							//	logger.info(eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());
								
								logger.info("currtier============="+currtier.getTierUpgradeCumulativeValue().intValue());
								endCal.add(Calendar.MONTH, -currtier.getTierUpgradeCumulativeValue().intValue());
								String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
								String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);

								LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
								current_balance = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));
						         
						}
						logger.info("current balance is--------"+current_balance);
						
							
							milestone = threshold - current_balance;
							logger.info("milestone value is ===="+milestone);
							if(milestone>0) return milestone.toString();
						
				//}	
				}
	//----------------------------------------------------------------------------------------			
			  }catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("exception while giving the LPT ph value ",e);
				return defvalue;
		  }
		}else if (cfstr.contains("LB")){
			try {
						logger.info("----Loyalty Bonus----");
						String threshold_id = cfstr.substring(3);
						Long thresholdId=Long.parseLong(threshold_id);
						logger.info("threshold id ======"+thresholdId);
						LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
						
							loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
							LoyaltyThresholdBonus bonus =loyaltyThresholdBonusDao.getThresholdById(thresholdId);
							logger.info("bonus.getEarnedLevelType()====="+bonus.getEarnedLevelType());
							logger.info("bonus.getEarnedLevelValue()======"+bonus.getEarnedLevelValue());
							threshold=bonus.getEarnedLevelValue();
							
							
								if(bonus.getEarnedLevelType().equalsIgnoreCase("Points")) 	current_balance=contactsLoyalty.getLoyaltyBalance()== null ? 0.00 : contactsLoyalty.getLoyaltyBalance();
								else if(bonus.getEarnedLevelType().equalsIgnoreCase("Amount")) 		current_balance=contactsLoyalty.getGiftcardBalance()== null ? 0.00 : contactsLoyalty.getGiftcardBalance();
				//				else if(bonus.getEarnedLevelType().equalsIgnoreCase("LPV"))		current_balance = contactsLoyalty.getCummulativePurchaseValue()== null ? 0.00 : contactsLoyalty.getCummulativePurchaseValue();
								else if(bonus.getEarnedLevelType().equalsIgnoreCase("LPV"))		current_balance = LoyaltyProgramHelper.getLPV(contactsLoyalty);

								
								logger.info("current balance   "+current_balance);    
								/*if(!bonus.isRecurring()){	
									logger.info("-----Not Recurring----");
									if(threshold - current_balance>0){
										
										milestone = threshold - current_balance;
									}
								}else{
									//Double amountToIgnore = contactsLoyalty.getAmountToIgnore()== null ? 0.00 : contactsLoyalty.getAmountToIgnore();
									logger.info("-----Is Recurring----");
									Double rem = (current_balance % threshold);
									//milestone = (threshold+amountToIgnore)-rem.intValue();	
									milestone=threshold-rem;
									
									
									
								}*/
								String lastThreshold=contactsLoyalty.getLastThreshold();
								String[] thresholdArr=null;
								String[] repeatOfThresholdArr=null;
								Double repeatOfThresholdArrDbl=null;
								int factor =(int) (current_balance/threshold);
					if((bonus.getThresholdLimit() == null || 
										(bonus.getThresholdLimit() !=null && current_balance<=bonus.getThresholdLimit() ))){
							if(lastThreshold!=null){
								if(!bonus.isRecurring()){	
									logger.info("-----Not Recurring----");
									if(threshold - current_balance>0){
										if(lastThreshold.contains(Constants.DELIMETER_DOUBLEPIPE)){
											thresholdArr = lastThreshold.split("\\|\\|");
											String reachingThreshold = thresholdArr[0];
											if(!reachingThreshold.contains(Constants.DELIMETER_DOUBLECOLON)){
												if(Double.parseDouble(reachingThreshold)<threshold){
													milestone = threshold - current_balance;
												}
											}
										}else{
											if(!lastThreshold.contains(Constants.DELIMETER_DOUBLECOLON)){
												if(Double.parseDouble(lastThreshold)<threshold){
													milestone = threshold - current_balance;
												}
											}
										}
									}
								}else{
									logger.info("-----Is Recurring----");
									if(lastThreshold.contains(Constants.DELIMETER_DOUBLEPIPE)){
										thresholdArr = lastThreshold.split("\\|\\|");
										for (String repeatOfThreshold : thresholdArr) {
											if(!repeatOfThreshold.contains(Constants.DELIMETER_DOUBLECOLON)) continue;
											
											repeatOfThresholdArr = repeatOfThreshold.split(Constants.DELIMETER_DOUBLECOLON);
											
											repeatOfThresholdArrDbl = Double.parseDouble(repeatOfThresholdArr[0]);
											int limit = Integer.parseInt(repeatOfThresholdArr[1]);
											
											if(repeatOfThresholdArrDbl.equals(threshold)){
												if(factor>limit){
													Double rem = (current_balance % threshold);
													milestone=threshold-rem;
												}else{ 
													Double rem = (current_balance % threshold);
													milestone=threshold-rem+((limit-factor)*threshold);
												}
											}
									}
											
									}else{
										if(lastThreshold.contains(Constants.DELIMETER_DOUBLECOLON)){
											repeatOfThresholdArr = lastThreshold.split(Constants.DELIMETER_DOUBLECOLON);
										
											repeatOfThresholdArrDbl = Double.parseDouble(repeatOfThresholdArr[0]);
											int limit = Integer.parseInt(repeatOfThresholdArr[1]);
										
											if(repeatOfThresholdArrDbl.equals(threshold)){
												if(factor>limit){
													Double rem = (current_balance % threshold);
													milestone=threshold-rem;
												}else{ 
													Double rem = (current_balance % threshold);
													milestone=threshold-rem+((limit-factor)*threshold);
												}
											}
										}
								}
									
							}
						}else{
							if(!bonus.isRecurring()){	
								logger.info("---2nd--Not Recurring----");
								if(threshold - current_balance>0){
									milestone = threshold - current_balance;
								}
							}else{
								logger.info("--2nd---Is Recurring----");
								Double rem = (current_balance % threshold);
								milestone=threshold-rem;							
							}
						}	
					}
					
					
								logger.info("Milestone for threshold bonus is =="+milestone);
									
					
				 } catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("exception while giving the LB ph value ",e);
							return defvalue;
						}
		}else if(cfstr.contains("LP")){
				cfstr= cfstr.substring(3);
				Long couponId=Long.parseLong(cfstr);
				CouponsDao couponsDao;
				try {
					
					couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
					Coupons coupons=couponsDao.findById(couponId);
					
					
					threshold = coupons.getRequiredLoyltyPoits().doubleValue();
					String value_code= coupons.getValueCode();
					logger.info("value code ====="+value_code);
					
					if(value_code == null ||value_code.equalsIgnoreCase("Points")){
						
						current_balance = contactsLoyalty.getLoyaltyBalance();	
						
					}
					else{
						
						LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);	
						LoyaltyBalance loyaltyBalance = loyaltyBalanceDao.findByLoyaltyIdUserIdValueCode(user.getUserId(),contactsLoyalty.getLoyaltyId(), value_code);
						current_balance = loyaltyBalance.getBalance().doubleValue();
					}
		logger.info("current balance ------------"+current_balance);
		logger.info("threshold for promotion================"+threshold);
					if(threshold - current_balance>0){
						
						milestone = threshold - current_balance;
					}
				
				} catch (Exception e) {
					logger.error("exception while giving the LP ph value ",e);
				}
				
								

			    
		 } 
			
			value = milestone.toString();
			logger.info("milestone inserted is -----"+value);
			return value;
	 }

	
	
	
/*	public static LoyaltyProgramTier applyTierUpgdRules(Long contactId, ContactsLoyalty contactsLoyalty, LoyaltyProgramTier currTier,Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception{

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tempTiersList == null || tempTiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return currTier;
		}
		else if (tempTiersList.size() >= 1) {
			Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				}
			});
		}
		
		List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
		boolean flag = false;
		for(LoyaltyProgramTier tier : tempTiersList) {
			logger.info("tier level : "+tier.getTierType());
			if(currTier.getTierType().equalsIgnoreCase(tier.getTierType())){
				flag = true;
			}
			if(flag){
				tiersList.add(tier);
			}
		}

		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(currTier.getTierType().equals(nexttier.getTierType())){
				eligibleMap.put(nexttier, null);
			}
			else{
				if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
						== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}

		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);

			Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
			logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);

			if(totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0){
				logger.info("totLoyaltyPointsValue value is empty...");
				return currTier;
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+currTier.getTierType());
					if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totLoyaltyPointsValue > 0 && totLoyaltyPointsValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+currTier.getTierType());
							return currTier;
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totLoyaltyPointsValue > 0 && totLoyaltyPointsValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return currTier;
			}//else
		}
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return currTier;
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
		//	ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(contactsLoyalty.getUserId(), contactId);
			Double totPurchaseValue = null;
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			logger.info("purchase value = "+totPurchaseValue);

			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return currTier;
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+currTier.getTierType());
					if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+currTier.getTierType());
							return currTier;
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return currTier;
			}//else
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			
			Double cumulativeAmount = 0.0;
//			Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//			LoyaltyProgramTier prevKeyTier = null;
			LoyaltyProgramTier nextKeyTier = null;
			while(it.hasPrevious()){
				nextKeyTier = it.previous();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------currTier::"+currTier.getTierType());
				if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
					return currTier;
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			
			return currTier;
		}
		else{
			return currTier;
		}
	
	}*/

//---------------------------------------------------------------------------------------------------------------------



	/*	private String getlastpurchasePlaceHolders(Contacts contact, String placeholder, String defVal ) {
		String value = Constants.STRING_NILL;


		//logger.debug("placeholders***************************"+placeholder);
		if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) {
			//logger.debug("orgid"+orgId);
			if(orgId != null) {
				//logger.debug("last purchase store address >>>>>>>>>>>>>>>>>>>>>.");
				value = getLastPurchaseStoreAddr(contact, defVal);
			}

		}else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE)) {
			//logger.debug("last purchase date>>>>>>>>>>>>>>>>>>");
			value = getContactLastPurchasedDate(contact, defVal);
		}

		return value;

	}
	 */	
	private String getLastPurchaseStoreAddr(OrganizationStores organizationStores, String defVal) {
		//need to get the contact last puchased store address
		//RetailProSalesDao retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		//OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");



		String storeAddress = null;

		//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

		if(organizationStores != null) {

			//String pattern = "[;=;]+";
			//logger.debug("entered into organization stores=====================");
			if(storeAddress == null) storeAddress = Constants.STRING_NILL;

			String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
			int count = 0;
			for(String str : strAddr){
				count++;

				if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
					storeAddress = storeAddress+" | Phone: "+str;
				}
				else if(storeAddress.length()==0 && str.trim().length()>0){
					storeAddress = storeAddress+str;
				}
				else if(storeAddress.length()>0 && str.trim().length()>0){
					storeAddress = storeAddress+", "+str;
				}
			}

			//storeAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
			//storeAddress = organizationStores.getAddressStr().replaceAll(pattern, " | ");
		}
		else {
			storeAddress = defVal;
		}
		return storeAddress != null && !storeAddress.trim().isEmpty() ? storeAddress : defVal;
	}//getLastPurchaseStoreAddr();



	private String getContactLastPurchasedDate(Contacts contact, String defVal) {

		String date = defVal;
		//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

		Calendar Mycalender = retailProSalesDao.findLastpurchasedDate(contact.getExternalId(),contact.getUsers().getUserId());
		if(Mycalender != null){
			date = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
		}
		else {
			date=defVal;
		}

		return date == null || date.trim().isEmpty() ? defVal : date;
	}//getContactLastPurchasedDate();

	/* 
	 * This method retrieves organization stores details to replace placeholders in campaign subject line
	 */
	private String getStorePlaceholders(Contacts contact, String placeholder, String defVal){

		//OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");

		String storePlaceholder = defVal;//Constants.STRING_NILL;

		if(contact.getHomeStore() != null && orgId != null) {
			//logger.info("-----------1----------------");
			OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, contact.getHomeStore());

			if(organizationStores != null){

				if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORENAME)){ 
					storePlaceholder = organizationStores.getStoreName() ;
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREMANAGER)){
					storePlaceholder = organizationStores.getStoreManagerName();
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREEMAIL)){
					storePlaceholder = organizationStores.getEmailId();
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREPHONE)){
					storePlaceholder = organizationStores.getAddress().getPhone();
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTREET)){
					storePlaceholder = organizationStores.getAddress().getAddressOne();
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORECITY)){
					storePlaceholder = organizationStores.getAddress().getCity() ;
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTATE)){
					storePlaceholder = organizationStores.getAddress().getState();
				}
				else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREZIP)){
					storePlaceholder = organizationStores.getAddress().getPin() != null ? organizationStores.getAddress().getPin()+Constants.STRING_NILL:Constants.STRING_NILL;
				}



			}	
		}		
		return storePlaceholder == null || storePlaceholder.trim().isEmpty() ? defVal : storePlaceholder;
	}//getStorePlaceholders()

	private String getLastPurchaseStorePlaceholders(Contacts contact, String placeholder, String defVal){
		try {

			RetailProSalesCSV lastSaleRecord = retailProSalesDao.findLastpurchaseRecord(contact.getContactId(), contact.getUsers().getUserId());//(contact.getExternalId(),contact.getUsers().getUserId());
			if(lastSaleRecord == null) return defVal;

			OrganizationStores organizationStores = null;
			String value = Constants.STRING_NILL;
			String storeNum = lastSaleRecord.getStoreNumber();

			if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS.toLowerCase()) || placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE.toLowerCase())){
				if(orgId != null && storeNum != null) {

					organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum );
					value = getLastPurchaseStoreAddr( organizationStores, defVal);
				}
			}
			else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE.toLowerCase())) {

				//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

				Calendar Mycalender = lastSaleRecord.getSalesDate();//retailProSalesDao.findLastpurchasedDate(contact.getExternalId(),contact.getUsers().getUserId());
				if(Mycalender != null){
					value = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}
				else {
					value=defVal;
				}

			}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_AMOUNT.toLowerCase())) {
		
						Double lastPurchaseDetails = lastSaleRecord.getSalesPrice();
						if(lastPurchaseDetails != null){
								value = lastPurchaseDetails.toString();
						}
						else {
							value=defVal;
						}
					
				}
			else {
				if(storeNum != null  ) {
					if( organizationStores == null) organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum);

					if(organizationStores == null) {
						value = defVal;
					}
					else {
						if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORENAME.toLowerCase())){ 
							value = organizationStores.getStoreName() ;
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREMANAGER.toLowerCase())){
							value = organizationStores.getStoreManagerName();
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREEMAIL.toLowerCase())){
							value = organizationStores.getEmailId();
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREPHONE.toLowerCase())){
							value = organizationStores.getAddress().getPhone();
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORESTREET.toLowerCase())){
							value = organizationStores.getAddress().getAddressOne();
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORECITY.toLowerCase())){
							value = organizationStores.getAddress().getCity() ;
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORESTATE.toLowerCase())){
							value = organizationStores.getAddress().getState();
						}
						else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREZIP.toLowerCase())){
							value = organizationStores.getAddress().getPin() != null ? organizationStores.getAddress().getPin()+Constants.STRING_NILL:Constants.STRING_NILL;
						}
					}	
				}else{

					value = defVal;
				}
			}

			if(value == null || value.trim().isEmpty()) {

				return defVal;
			}
			return value;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			return defVal;
		}
	}//getLastPurchaseStorePlaceholders()

	/*
	 * This method retrieves contacts loyalty details to replace place holders in campaign subject line	
	 */

	private String getLoyaltyPlaceholders(Long userId, Contacts contact, String placeholder, String defVal, Long loyaltyId, boolean isSms) {


		try {


			String loyaltyPlaceholder =defVal;
			DecimalFormat f=new DecimalFormat("#0.00"); 
			ContactsLoyalty contactsLoyalty = null;
			
			if(contactsLoyaltyDao == null) {
				contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			}

			if(loyaltyId != null){
				logger.info("contactsLoyaltyDao ="+contactsLoyaltyDao);

				contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyId.longValue());
				logger.info("contactsLoyalty ="+contactsLoyaltyDao);
			}
			else{
				contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contact.getContactId());
			}
			if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED.equalsIgnoreCase(contactsLoyalty.getMembershipStatus()) && contactsLoyalty.getTransferedTo() != null){
				
				contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactsLoyalty.getTransferedTo());
			}
			//if(contact.getContactId() != null && contact.getLoyaltyCustomer() != null ){ 
			//ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findByContactId(campaign.getUsers().getUserOrganization().getUserOrgId(), contact.getContactId());


			if(logger.isInfoEnabled()) logger.info("gotloyalty obj ==="+contactsLoyalty+" "+placeholder);
			if(contactsLoyalty != null){

				//TODO new Place holders to be added
				loyaltyPlaceholder = replaceLoyaltyPlaceHolders(placeholder,contactsLoyalty,defVal, isSms);
				logger.info("**************************** loyaltyPlaceholder Id ************* "+loyaltyPlaceholder);
			}
			return loyaltyPlaceholder != null && !loyaltyPlaceholder.trim().isEmpty() ? loyaltyPlaceholder : defVal;


		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Contacts loyalty place holders: "+e);
			//logger.error("Exception ::::", e);
			return null;
		}
	}


	/**
	 * This method replace loyalty place holders
	 * @param placeholder
	 * @param contactsLoyalty
	 * @param defVal
	 * @param isSms 
	 * @return Loyalty Place Holder
	 * @throws Exception 
	 */
	private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) throws Exception {

		if(contactsLoyalty == null){
			logger.error("contactsLoyalty is null :: "+contactsLoyalty);
			logger.error("defVal is null :: "+defVal);
			return defVal;
		}
		if(placeholder == null  && defVal == null){
			logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
			return defVal;
		}

		logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String loyaltyPlaceholder="";
		//OC LOYALTYCARDPIN
		if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
			logger.info("OC Membership Pin ::"+contactsLoyalty.getCardPin());
		}
		//SB LOYALTYCARDPIN
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTYCARDPIN.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
			logger.info("SB Membership Pin ::"+contactsLoyalty.getCardPin());
		}
		//REFRESHEDON
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
			loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
		}
		//OC MEMBERSHIP_NUMBER
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
			logger.info("OC Membership Number ::"+contactsLoyalty.getCardNumber());
		}
		//SB MEMBERSHIP_NUMBER
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CARDNUMBER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
			logger.info("SB Membership Number ::"+contactsLoyalty.getCardNumber());
		}
		//MEMBER_TIER
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
			logger.info("Member Tier ::"+loyaltyPlaceholder);
		}
		//MEMBER_STATUS
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
			logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
			loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
			logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
		}
		//LOYALTY_ENROLLMENT_DATE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
			logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_ENROLLMENT_SOURCE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getContactLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getContactLoyaltyType() , defVal) : defVal;
			logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
		}//LOYALTY_ENROLLMENT_STORE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
			logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_REGISTERED_PHONE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone():defVal;
			logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_POINTS_BALANCE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
			loyaltyPlaceholder = (contactsLoyalty.getLoyaltyBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? contactsLoyalty.getLoyaltyBalance().longValue()+"" : defVal;
		}
		//OC LOYALTY_MEMBERSHIP_CURRENCY_BALANCE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = (contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE))?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
			logger.info("LOYALTY_MEMBERSHIP_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
		}
		//SB GIFTCARD_BALANCE
		else if(PlaceHolders.CAMPAIGN_PH_GIFTCARD_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = (contactsLoyalty.getGiftcardBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
			logger.info("GIFTCARD_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_GIFT_BALANCE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = (contactsLoyalty.getGiftBalance() != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE))? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
			logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_LIFETIME_PURCHASE_VALUE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LIFETIME_PURCHASE_VALUE.equalsIgnoreCase(placeholder)){
			//defVal=userCurrencySymbol+"0.00";
			Double lpv = null;
			lpv= LoyaltyProgramHelper.getLPV(contactsLoyalty);
			loyaltyPlaceholder = (lpv != null && contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE))? decimalFormat.format(lpv) : defVal;
			logger.info("CAMPAIGN_PH_LOYALTY_LIFETIME_PURCHASE_VALUE ::"+loyaltyPlaceholder);
		}
		//OC LOYALTY_LIFETIME_POINTS
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LIFETIME_POINTS.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = (contactsLoyalty.getTotalLoyaltyEarned() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)) ? contactsLoyalty.getTotalLoyaltyEarned().longValue()+" Points" : defVal;
			logger.info("LOYALTY_LIFETIME_POINTS ::"+loyaltyPlaceholder);
		}
		
		//redeemable currency
    	else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_RC.equalsIgnoreCase(placeholder)){
    		
    		if(contactsLoyalty.getProgramTierId() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE )) {
    			try {
    				double loyaltyAmount = (contactsLoyalty.getGiftcardBalance() == null)? 0.0 : contactsLoyalty.getGiftcardBalance();
    				double giftAmount = (contactsLoyalty.getGiftBalance() == null)? 0.0 : contactsLoyalty.getGiftBalance();
    				double pointsAmount = 0.0;
    				double totalReedmCurr = 0.0;
    				
    				pointsAmount = LoyaltyProgramHelper.calculatePointsAmount(contactsLoyalty, new LoyaltyProgramService().getTierObj(contactsLoyalty.getProgramTierId()));
    				logger.info("Points amount"+pointsAmount);
    				totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
    				Double totalRedeemableAmount = 	loyaltyAmount + pointsAmount + giftAmount;
    				loyaltyPlaceholder = totalRedeemableAmount.toString();
    				logger.info("CAMPAIGN_PH_LOYALTY_LRC"+loyaltyPlaceholder);

    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				logger.error("Exception ::"+e);
    				
    			}
    			
    		}
    	}
		
		//LOYALTY_GIFT_CARD_EXPIRATION
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
			logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
		}
		//LOYALTY_HOLD_BALANCE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
			loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
			logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_REWARD_ACTIVATION_PERIOD
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
			logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_EARNED_VALUE(changes for transfer)
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal) : defVal;
			logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_REDEEMED_VALUE(changes for transfer)
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal) : defVal;
			logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
		}
		else if(PlaceHolders. CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getMembershipPassword(contactsLoyalty,defVal, isSms);
			logger.info("LOYALTY_MEMBERSHIP_PASSWORD::"+loyaltyPlaceholder);
		} 
		//LOYALTY_LOGIN_URL
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LOGIN_URL.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getLoyaltyURL(contactsLoyalty,defVal,isSms);
			logger.info("LOYALTY_LOGIN_URL::"+loyaltyPlaceholder);
		}
		/*//ORGANIZATION_NAME
		else if(PlaceHolders.CAMPAIGN_PH_ORGANIZATION_NAME.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getUserOrganization(contactsLoyalty,defVal);
			logger.info("PH_ORGANIZATION_NAME::"+loyaltyPlaceholder);
		} */
		//REWARD_EXPIRATION_PERIOD
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getRewardExpirationPeriod(contactsLoyalty ,defVal);
			logger.info("LOYALTY_REWARD_EXPIRATION_Period ::"+loyaltyPlaceholder);
		}
		//MEMBERSHIP_EXPIRATION_DATE
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getLoyaltyMembershipExpirationDate(contactsLoyalty, defVal);	
			logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftAmountExpirationPeriod(contactsLoyalty,defVal);
			logger.info("LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD :: "+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_BONUS_VALUE(changes for transfer)
		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastBonusValue(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_BONUS,defVal) : defVal;
			logger.info("LOYALTY_LAST_BONUS_VALUE :: "+loyaltyPlaceholder);
		}
		//REWARD_EXPIRING_VALUE(changes for transfer)
		else if(PlaceHolders.CAMPAIGN_PH_REWARD_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getRewardExpiringValue(contactsLoyalty,defVal);
			logger.info("REWARD_EXPIRING_VALUE :: "+loyaltyPlaceholder);
		}
		//GIFT_AMOUNT_EXPIRING_VALUE(changes for transfer)
		else if(PlaceHolders.CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftAmountExpiringValue(contactsLoyalty,defVal);
			logger.info("GIFT_AMOUNT_EXPIRING_VALUE :: "+loyaltyPlaceholder);
		}

		logger.info("Completed replace holder method");
		return loyaltyPlaceholder;
	}//replaceLoyaltyPlaceHolders
	
	/**
	 * This is to show gift amount expiry value
	 * @param contactsLoyalty
	 * @param defVal
	 * @return
	 */

	private String getGiftAmountExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.info("--Start of getGiftAmountExpiringValue--");
		String giftExpValue = defVal;
		try {
			if(contactsLoyalty.getProgramId()== null) return giftExpValue;
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());
		
		if(OCConstants.FLAG_YES == program.getGiftAmountExpiryFlag() && program.getGiftAmountExpiryDateType() != null 
				&& program.getGiftAmountExpiryDateValue() != null){
			
			Calendar cal = Calendar.getInstance();
			if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
				cal.add(Calendar.MONTH, -(program.getGiftAmountExpiryDateValue().intValue()));
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
				cal.add(Calendar.YEAR, -(program.getGiftAmountExpiryDateValue().intValue()));
			}
			String expDate = "";
			if(cal.get(Calendar.MONTH) == 11) {
				expDate = cal.get(Calendar.YEAR)+"-12";
			} 
			else {
				cal.add(Calendar.MONTH, 1);
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}
			logger.info("expDate = "+expDate);
			
			Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);
			
			if(expiryValueArr != null && expiryValueArr[2] != null){
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				double expGift = Double.valueOf(expiryValueArr[2].toString());
				if(expGift > 0){
					giftExpValue = decimalFormat.format(expGift);  
				}
			}
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.info("--Exit of getGiftAmountExpiringValue--");
		return giftExpValue;
	}//getGiftAmountExpiringValue

	/**
	 * This is to show reward expiry value
	 * @param contactsLoyalty
	 * @param defVal
	 * @return
	 */
	private String getRewardExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.info("--Start of getRewardExpiringValue--");
		String rewardExpVal = defVal ;
		try {
		if(contactsLoyalty.getProgramId()== null) return rewardExpVal;
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());
		
		if(contactsLoyalty.getProgramTierId()== null) return rewardExpVal;
		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());
		
		if(OCConstants.FLAG_YES == program.getRewardExpiryFlag() && loyaltyProgramTier.getRewardExpiryDateType() != null 
				&& loyaltyProgramTier.getRewardExpiryDateValue() != null){

			Calendar cal = Calendar.getInstance();
			if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgramTier.getRewardExpiryDateType())){
				cal.add(Calendar.MONTH, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgramTier.getRewardExpiryDateType())){
				cal.add(Calendar.YEAR, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
			}
			
			String expDate = "";
			if(cal.get(Calendar.MONTH) == 11) {
				expDate = cal.get(Calendar.YEAR)+"-12";
			} 
			else {
				cal.add(Calendar.MONTH, 1);
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}
			logger.info("expDate = "+expDate);
			Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

			if(expiryValueArr != null ) { 
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && expiryValueArr[2] != null
						&& Double.valueOf(expiryValueArr[2].toString()) >  0.0){
					rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points"+
												" & "+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
				}
				else if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && (expiryValueArr[2] == null ||
						Double.valueOf(expiryValueArr[2].toString()) == 0.0)) {
					rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points";
				}
				else if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) >  0.0
						&& (expiryValueArr[1] == null || Long.valueOf(expiryValueArr[1].toString()) == 0)){
					rewardExpVal = decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
				}
				else {
					rewardExpVal = defVal;
				}
			}
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.info("--Exit of getRewardExpiringValue--");
		return rewardExpVal;
	}//getRewardExpiringValue

	/**
	 * To fetch expiry values
	 * @param cardNumber
	 * @param expDate
	 * @param rewardFlag
	 * @return
	 * @throws Exception
	 */
	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
		logger.info("--Start of fetchExpiryValues--");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		logger.info("--Exit of fetchExpiryValues--");
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag);
	}//fetchExpiryValues

	/**
	 * This is to show last bonus value earned
	 * @param cardNumber
	 * @param transactionType
	 * @param defVal
	 * @return
	 */
	private String getLastBonusValue(Long userId, Long loyaltyId,String transactionType, String defVal) {
		logger.info("--Start of getLastBonusValue--");
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType(userId, loyaltyId, transactionType);
		if(loyaltyTransactionChild != null){
			if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount())+" & "+loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() == null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
			}
			else if(loyaltyTransactionChild.getEarnedAmount() == null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else{
				loyaltyPlaceholder = defVal;
			}
		}
		else{
			loyaltyPlaceholder = defVal;
		}
		logger.info("--Exit of getLastBonusValue--");
		return loyaltyPlaceholder;
	}//getLastBonusValue


	/**
	 * This method return's the decrypt password
	 * @param contactsLoyalty
	 * @param defVal
	 * @return password
	 */
	private String getMembershipPassword(ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) {
		logger.debug(">>>>>>>>>>>>> entered in getMembershipPassword");
		String password = defVal;
		try {
			
			if(!contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				password =getresetPasswordURL(contactsLoyalty, defVal, isSms);//contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt(contactsLoyalty.getMembershipPwd()) : defVal;
			}
		} catch (Exception e) {
			logger.error("Expection while replacing place holder :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getMembershipPassword ");
		return password;
	}//getMembershipPassword
	
private String getresetPasswordURL(ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) {
		
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
		String loyaltyUrl = defVal;
		try {
		LoyaltyProgramService loyaltyProgramService =  new LoyaltyProgramService();
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.find(contactsLoyalty.getUserId());
		LoyaltySettings loyaltySettings = loyaltyProgramService.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

		if(loyaltySettings != null){
			loyaltyUrl = loyaltySettings.getUrlStr()+PropertyUtil.getPropertyValueFromDB("forgotPasswordLink");
			if(!isSms){
				loyaltyUrl = "<a href="+loyaltyUrl+">"+PropertyUtil.getPropertyValueFromDB("forgotPasswordText")+"</a>";
			}
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyUrl;
	}//getLoyaltyURL
	/**
	 * This method returns the Loyalty Url
	 * @param contactsLoyalty
	 * @param defVal
	 * @param isSms 
	 * @return loyaltyUrl
	 */
	private String getLoyaltyURL(ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) {
		
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
		String loyaltyUrl = defVal;
		try {
		LoyaltyProgramService loyaltyProgramService =  new LoyaltyProgramService();
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.find(contactsLoyalty.getUserId());
		LoyaltySettings loyaltySettings = loyaltyProgramService.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

		if(loyaltySettings != null){
			loyaltyUrl = loyaltySettings.getUrlStr();
			if(!isSms){
				loyaltyUrl = "<a href="+loyaltyUrl+">"+loyaltyUrl+"</a>";
			}
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyUrl;
	}//getLoyaltyURL
	
	/**
	 * This method get's user Organization.
	 * @param user 
	 * @param defVal 
	 * @return userOrganization
	 */
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
	}//getUserOrganization
	

	/**
	 * This method gets gift Amount Expiration Period
	 * @param contactsLoyalty
	 * @param defVal
	 * @return
	 */
	private String getGiftAmountExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getGiftAmountExpirationPeriod");
		String giftAmountExpirationPeriod = defVal;

		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyProgram loyaltyProgram =  null;

		if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
			loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());

			if(loyaltyProgram != null && loyaltyProgram.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES){

				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) ||
						OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

					if(loyaltyProgram.getGiftAmountExpiryDateValue() != null  && loyaltyProgram.getGiftAmountExpiryDateValue() != 0 
							&& loyaltyProgram.getGiftAmountExpiryDateType() != null && !loyaltyProgram.getGiftAmountExpiryDateType().isEmpty())
					{
						giftAmountExpirationPeriod = loyaltyProgram.getGiftAmountExpiryDateValue()+" "+loyaltyProgram.getGiftAmountExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
					}//if

				}//if oc 
			}//if lty !=null
		}//if cont
		logger.debug("<<<<<<<<<<<<< completed getGiftAmountExpirationPeriod ");
		return giftAmountExpirationPeriod;
	}//getGiftAmountExpirationPeriod
	
	
    /**
     * This method gets  Gift-Card   Expiration Date
     * @param contactsLoyalty
     * @param defVal
     * @return getGiftCardExpirationDate
     */
	private String getGiftCardExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getGiftCardExpirationDate");
		String giftCardExpriationDate = defVal;

		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyProgram loyaltyProgram =  null;

		if(contactsLoyalty.getProgramId() != null){
			loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
			if(loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
				if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y' && loyaltyProgram.getGiftMembrshpExpiryDateType() != null 
						&& loyaltyProgram.getGiftMembrshpExpiryDateValue() != null){
					giftCardExpriationDate = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
							loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
				}//if 
			}
		}
		logger.debug("<<<<<<<<<<<<< completed getGiftCardExpirationDate ");
		return giftCardExpriationDate;
	}//getGiftCardExpirationDate
	
	/**
	 * This method get's Loyalty Membership Expiration Date
	 * @param contactsLoyalty
	 * @param defVal
	 * @return membershipExpriationDate
	 */
	private String getLoyaltyMembershipExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyMembershipExpriationDate");
		String membershipExpriationDate = defVal;
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyProgramTier loyaltyProgramTier = null;
		LoyaltyProgram loyaltyProgram =  null;

		if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
			loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
			loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

			if(loyaltyProgram != null && loyaltyProgramTier != null){

				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
					////if flag L or GL
					if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
							&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){
						
						boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;

						membershipExpriationDate = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
								upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
					}
				}//if

			}//loyaltyProgram && loyaltyProgramTier 
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyMembershipExpriationDate ");
		return membershipExpriationDate;
	}//getLoyaltyMembershipExpriationDate

	/**
	 * This method get's Reward Expiration Period
	 * @param contactsLoyalty
	 * @param defVal
	 * @return
	 */
	private String getRewardExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getRewardExpirationPeriod");
		String rewardExpirationPeriod = defVal;

		Long tierId =  contactsLoyalty.getProgramTierId();

		if(tierId != null){
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null && loyaltyProgram.getRewardExpiryFlag()==OCConstants.FLAG_YES){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgramTier != null && loyaltyProgramTier.getRewardExpiryDateValue() != null 
								&& loyaltyProgramTier.getRewardExpiryDateValue() != 0 
								&& loyaltyProgramTier.getRewardExpiryDateType() != null 
								&& !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())
						{
							rewardExpirationPeriod = loyaltyProgramTier.getRewardExpiryDateValue()+" "
									+loyaltyProgramTier.getRewardExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
						}//if

					}//if oc 
				}//if lty !=null
			}//if cont
		}//tier id
		logger.debug("<<<<<<<<<<<<< completed getRewardExpirationPeriod ");
		return rewardExpirationPeriod;
	}//getRewardExpirationPeriod

	

  // Loyalty Placeholder's Replacement logic .
	/**
	 * This method fetch the place holder for MemberTier
	 * @param programTierId
	 * @return MemberTier
	 */
	private String getMemberTier(Long programTierId,String defValue) {

		LoyaltyProgramTier loyaltyProgramTier = null;
		//helper class obj
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		String tier = "" ,level ="",loyaltyPlaceholder="";

		loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
		if(loyaltyProgramTier != null){
			tier = loyaltyProgramTier.getTierName() ;
			level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? "" : loyaltyProgramTier.getTierType())+" )";
			loyaltyPlaceholder = tier + level ; //it will tier name + level
		}
		else{
			loyaltyPlaceholder = defValue; //default value to be replaced
		}
		return loyaltyPlaceholder;
	}//getMemberTier

	

	/**
	 * This method return Enrollment source
	 * @param loyaltyType
	 * @param defVal
	 * @return EnrollmentSource
	 */
	private String getEnrollmentSource(String loyaltyType, String defVal) {
		String loyaltyPH = "";
		if(Constants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(loyaltyType)) {
			loyaltyPH = Constants.CONTACT_LOYALTY_TYPE_STORE;
		}
		else {
			loyaltyPH = loyaltyType;
		}
		return loyaltyPH;
	}//getEnrollmentSource	


	/**
	 * This method fetch Hold Bal & points
	 * @param contactsLoyalty
	 * @param defVal
	 * @return Hold Balance or Points
	 */

	private String getHoldBalance(ContactsLoyalty contactsLoyalty,String defVal) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String loyaltyPlaceholder ="";
		if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance()) +" & "+contactsLoyalty.getHoldPointsBalance().intValue()+ " Points";;
		}
		else if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() == null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance());
		}
		else if(contactsLoyalty.getHoldAmountBalance() == null && contactsLoyalty.getHoldPointsBalance() != null&& contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE)){
			loyaltyPlaceholder = contactsLoyalty.getHoldPointsBalance().intValue() + " Points";
		}
		else{
			loyaltyPlaceholder =  defVal;
		}
		return loyaltyPlaceholder;
	}//getHoldBalance

	/**
	 * This method fetch the Reward Activation Period
	 * @param programTierId
	 * @param defValue
	 * @return Reward Activation Period
	 */

	private String getRewardActivationPeriod(Long programTierId,String defValue) {
		LoyaltyProgramTier loyaltyProgramTier = null;
		String loyaltyPlaceholder = defValue;
		//helper class obj
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES){
			loyaltyPlaceholder = loyaltyProgramTier.getPtsActiveDateValue() +" "+ loyaltyProgramTier.getPtsActiveDateType()+OCConstants.MORETHANONEOCCURENCE;
		}
		else{
			loyaltyPlaceholder = defValue ;
		}
		return loyaltyPlaceholder;
	}//getRewardActivationPeriod

	/**
	 * This method calculate Last Earned Value
	 * @param cardNumber
	 * @param loyaltyTransTypeIssuance
	 * @return Last Earned Value
	 */
	private String getLastEarnedValue(Long userId , Long loyaltyId,String loyaltyTransTypeIssuance,String defValue) {
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		//helper class obj
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild child = null;
		child = ltyPrgmService.getTransByMembershipNoAndTransType(userId, loyaltyId, loyaltyTransTypeIssuance);
		if(child != null){
			if(child.getEarnedAmount() != null ){
				loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount()); //+ " "+child.getValueCode();//+" & "+child.getEarnedPoints().intValue()+"";
			}
			if(child.getEarnedPoints() != null) {
				loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? "&"  :"";
				loyaltyPlaceholder = child.getEarnedPoints().intValue()+"";//+" "+child.getValueCode();;
			}
			/*if(child.getEarnedReward() != null) {
				loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? "&"  :"";
				loyaltyPlaceholder = child.getEarnedReward().intValue()+""+(child.getValueCode() != null ?  child.getValueCode() :"");//" "+child.getValueCode();;
			}*/
			//loyaltyPlaceholder += !loyaltyPlaceholder.isEmpty() ? " "+(child.getValueCode() != null ?  child.getValueCode() :""):"";
			/*
			 * else if(child.getEarnedAmount() != null && child.getEarnedPoints() == null){
			 * loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount()); } else
			 * if(child.getEarnedAmount() == null && child.getEarnedPoints() != null){
			 * loyaltyPlaceholder = child.getEarnedPoints().intValue()+""; } 
			 */
			if(loyaltyPlaceholder.isEmpty()){
				loyaltyPlaceholder = defValue;
			}
		}
		else{
			loyaltyPlaceholder = defValue;
		}
		return loyaltyPlaceholder;
	}//getLastEarnedValue

	/**
	 * This method calculate Last Redeemed Value
	 * @param cardNumber
	 * @param loyaltyTransTypeIssuance
	 * @return Last Redeemed Value
	 */
	private String getLastRedeemedValue(Long userId, Long loyaltyId,String loyaltyTransTypeRedemption,String defValue) {
		String loyaltyPlaceholder = "";
		//DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		//helper class obj
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild child = null;
		child = ltyPrgmService.getTransByMembershipNoAndTransType(userId, loyaltyId, loyaltyTransTypeRedemption);
		if(child != null){
			if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
				loyaltyPlaceholder = child.getEnteredAmount().intValue()+" Points";
			}
			else if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
				//loyaltyPlaceholder = decimalFormat.format(child.getEnteredAmount());
				loyaltyPlaceholder = child.getEnteredAmount().intValue()+"";//APP-4496 
				logger.info("loyaltyPlaceholder in last redeemed value method is "+child.getEnteredAmount().intValue());
			}
			else{
				loyaltyPlaceholder = defValue;
			}
		}
		else{
			loyaltyPlaceholder = defValue;
		}
		return loyaltyPlaceholder;

	}//getLastRedeemedValue

	

	
	
	
	
	/*private String getContactLastPurchaseStoreAddr(Contacts contact, Users user) {
			//need to get the contact last puchased store address
			//RetailProSalesDao retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");

			String footerAddress = Constants.STRING_NILL;
			//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
			String userDomainStr = Constants.STRING_NILL;
			Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
			if(domainsList != null) {
				domainSet.addAll(domainsList);
				for (UsersDomains usersDomains : domainSet) {

					if(userDomainStr.length()>0) userDomainStr+=",";
					userDomainStr += usersDomains.getDomainName();

				}
			}





			if(contact.getExternalId() != null) {
				//logger.info("-----------1----------------"+retailProSalesDao+"  "+contact);
				String storeNum = retailProSalesDao.findLastpurchasedStore(contact.getExternalId(),contact.getUsers().getUserId());

				if(storeNum != null) {
					OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(user.getUserOrganization().getUserOrgId(), storeNum);


					if(organizationStores == null) {
						//logger.info("-----------2----------------");

						String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
						footerAddress = footerAddr[1];
						//footerAddress = PrepareFinalHTML.getSenderAddress(user, true);

					}else {

						//logger.info("-----------3----------------");
						String storeAddress = Constants.STRING_NILL;
						String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
						int count = 0;
						for(String str : strAddr){
							count++;

							if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
								storeAddress = storeAddress+" | Phone: "+str;
							}
							else if(storeAddress.length()==0 && str.trim().length()>0){
								storeAddress = storeAddress+str;
							}
							else if(storeAddress.length()>0 && str.trim().length()>0){
								storeAddress = storeAddress+", "+str;
							}
						}

						footerAddress = storeAddress;

						//footerAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
					}

				}else  {
					//logger.info("-----------4----------------");
					//footerAddress = PrepareFinalHTML.getSenderAddress(user, true);
					String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
					footerAddress = footerAddr[1];
				}

			}else {
				//logger.info("-----------5----------------");
				//footerAddress = PrepareFinalHTML.getSenderAddress(user, true);
				String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
				footerAddress = footerAddr[1];
			}



			return footerAddress;
		}//getContactLastPurchaseStoreAddr();
	 */

	/*private String getContactHomeStoreAddress(Contacts contact, Users user){
			// need to get the contacts HomeStore Address
			//OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");

			String footerAddress = Constants.STRING_NILL;
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
			String userDomainStr = Constants.STRING_NILL;
			Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
			if(domainsList != null) {
				domainSet.addAll(domainsList);
				for (UsersDomains usersDomains : domainSet) {

					if(userDomainStr.length()>0) userDomainStr+=",";
					userDomainStr += usersDomains.getDomainName();

				}
			}



			//includeBfrStr += userDomainStr;//currentUser.getUserDomainStr();

			if(contact.getHomeStore() != null) {
				//logger.info("-----------1----------------");
				OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(user.getUserOrganization().getUserOrgId(), contact.getHomeStore());


				if(organizationStores == null) {
					//logger.info("-----------2----------------");
					//footerAddress = PrepareFinalHTML.getSenderAddress(user, true);
					String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
					footerAddress = footerAddr[1];

				}else {

					//logger.info("-----------3----------------");
					String storeAddress = Constants.STRING_NILL;
					String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
					int count = 0;
					for(String str : strAddr){
						count++;

						if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
							storeAddress = storeAddress+" | Phone: "+str;
						}
						else if(storeAddress.length()==0 && str.trim().length()>0){
							storeAddress = storeAddress+str;
						}
						else if(storeAddress.length()>0 && str.trim().length()>0){
							storeAddress = storeAddress+", "+str;
						}
					}

					footerAddress = storeAddress;

					//footerAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
				}

			}else  {
				//logger.info("-----------4----------------");
				//footerAddress = PrepareFinalHTML.getSenderAddress(user, true);
				String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
				footerAddress = footerAddr[1];
			}
			return footerAddress;
		}//getContactHomeStoreAddress()
	 */
	public  String replaceSMSAutoResponseContent(String content, Set<String> coupSet, String to,Long contactId) throws BaseServiceException{

		if(coupSet.size() > 0) {

			String value=Constants.STRING_NILL;

			for (String cfStr : coupSet) {

				if(cfStr.startsWith("CC_")) {


					String strArr[] = cfStr.split("_");


					//CouponCodes tempCC = couponCodesDao.getInventorySingleCouponCodeByCouponId(Long.parseLong(strArr[1].trim()));
					//coupon provider logic

					if(couponProvider == null) { //get coupon provider
						CouponCodesDao couponCodesDao = null;

						try {
							couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							throw new BaseServiceException("no dao found with id ::"+OCConstants.COUPONCODES_DAO);
						}

						couponProvider = CouponProvider.getCouponProviderInstance(null, couponCodesDao);
						if(couponProvider == null) {
							if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
							continue;
						}

					}//if

					if(couponProvider.couponSet != null ) {

						if(!couponProvider.couponSet.contains(cfStr)) {

							couponProvider.couponSet.add(cfStr);
						}

					}
					if(contactId != null) {
						value = couponProvider.getAlreadyIssuedCoupon(cfStr, contactId);
					}
					if(value == null || value.isEmpty()) 
						value = couponProvider.getNextCouponCode(cfStr, null, Constants.COUP_GENT_CAMPAIGN_TYPE_SMS, to, null, content,contactId);



					//********END************




					/*
							if(tempCC==null) {
								logger.debug(" Promo-codes exhausted for ::"+cfStr);
								value = Constants.STRING_NILL; // TODO need to add the message in place of CouponCode
							}
							else {
								try {
									value = tempCC.getCouponCode();
									tempCC.setCampaignType(Constants.COUP_GENT_CAMPAIGN_TYPE_SMS);
									tempCC.setIssuedTo(to);
									tempCC.setIssuedOn(Calendar.getInstance());
									tempCC.setStatus(Constants.COUP_GENT_CODE_STATUS_ACTIVE);
									couponCodesDao.saveOrUpdate(tempCC);
								} catch (Exception e) {
									value = Constants.STRING_NILL;
									logger.error("Exception ::::", e);
								}
							}//else



						logger.debug(">>>>>>>>> Promo-code "+ value);*/


					if(value == null) value = Constants.STRING_NILL;

					content = content.replace("[" + cfStr + "]", value);
					logger.debug(">>>>>>>>>content after replacing Promo-code is >>>>>>>>>"+content);

				}//if



			}//for


		}

		return content;




	}//replaceSMS

	private  String getConatctCustFields(Contacts contact , int index, ContactPHValue contactPHValue, String defVal)  {

		String retVal = defVal;
		// if(contactPHValue==null) return retVal;

		try {
			switch(index){
			case 1: retVal = contact.getUdf1(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf1(retVal != null ? retVal : defVal);
			} 
			break; 
			case 2: retVal = contact.getUdf2(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf2(retVal != null ? retVal : defVal);
			} 
			break;  
			case 3: retVal = contact.getUdf3();
			if(contactPHValue != null ) {
				contactPHValue.setUdf3(retVal != null ? retVal : defVal);
			} 
			break;  
			case 4: retVal = contact.getUdf4();
			if(contactPHValue != null ) {
				contactPHValue.setUdf4(retVal != null ? retVal : defVal);
			} 
			break;  
			case 5: retVal = contact.getUdf5(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf5(retVal != null ? retVal : defVal);
			}
			break;  
			case 6: retVal = contact.getUdf6(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf6(retVal != null ? retVal : defVal);
			}
			break;  
			case 7: retVal = contact.getUdf7(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf7(retVal != null ? retVal : defVal);
			} 
			break;  
			case 8: retVal = contact.getUdf8(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf8(retVal != null ? retVal : defVal);
			} 
			break;  
			case 9: retVal = contact.getUdf9(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf9(retVal != null ? retVal : defVal);
			}
			break;  
			case 10: retVal = contact.getUdf10(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf10(retVal != null ? retVal : defVal);
			}  
			break;  
			case 11: retVal = contact.getUdf11(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf11(retVal != null ? retVal : defVal);
			}
			break;  
			case 12: retVal = contact.getUdf12(); 
			if(contactPHValue != null )	{
				contactPHValue.setUdf12(retVal != null ? retVal : defVal);
			} 
			break;  
			case 13: retVal = contact.getUdf13(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf13(retVal != null ? retVal : defVal);
			}
			break;  
			case 14: retVal = contact.getUdf14(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf14(retVal != null ? retVal : defVal);
			} 
			break;  
			case 15: retVal = contact.getUdf15(); 
			if(contactPHValue != null ) {
				contactPHValue.setUdf15(retVal != null ? retVal : defVal);
			}
			break;  
			//logger.info("fo contact "+contact.getEmailId()+" val ::"+retVal);
			//default : return retVal;
			}
			/*if(index == 1) {
					retVal = contact.getUdf1();

				}
				else if(index == 2) {
					retVal = contact.getUdf2();

				}else if(index == 3) {
					retVal = contact.getUdf3();

				}else if(index == 4) {
					retVal = contact.getUdf4();

				}else if(index == 5) {
					retVal = contact.getUdf5();

				}else if(index == 6) {
					retVal = contact.getUdf6();

				}else if(index == 7) {
					retVal = contact.getUdf7();

				}else if(index == 8) {
					retVal = contact.getUdf8();

				}else if(index == 9) {
					retVal = contact.getUdf9();

				}else if(index == 10) {
					retVal = contact.getUdf10();

				}else if(index == 11) {
					retVal = contact.getUdf11();

				}else if(index == 12) {
					retVal = contact.getUdf12();

				}else if(index == 13) {
					retVal = contact.getUdf13();

				}else if(index == 14) {
					retVal = contact.getUdf14();

				}else if(index == 15) {
					retVal = contact.getUdf15();

				}
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//logger.error("excp   ",e);
			return defVal;
		}
		if (retVal == null) retVal = defVal; 	

		return retVal;



	} // setConatctCustFields

	public String[] replacePHForTestEmails(Users user, String htmlContent, Set<String> totalPHSet, String tempCampSubject, String textContent) {

		String[] contentsStrArr = new String[3];
		try {


			String preStr = Constants.STRING_NILL; 

			for (String cfStr : totalPHSet) {
				//	logger.debug("<<<<   cfStr : "+ cfStr);
				String value=Constants.STRING_NILL;
				String defVal = Constants.STRING_NILL;
				preStr = cfStr;
				if(cfStr.startsWith("GEN_") || cfStr.startsWith(Constants.UDF_TOKEN)) {

					cfStr = cfStr.substring(4);
					int defIndex = cfStr.indexOf('=');

					if(defIndex != -1) {

						defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
						defVal = defVal.trim().isEmpty() ?  Constants.STRING_NOTAVAILABLE : defVal;
						cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
					} // if


				}else if(cfStr.startsWith("CC_")) {

					value = "Test-"+cfStr.split("_")[2];//no default value will be there we can give the coupon name instead

				} 
				//replace the cfstr

				if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email") ) {
					value = user.getEmailId();
				}

				else if(cfStr.equalsIgnoreCase("firstName")) {
					value = user.getFirstName();
				}
				else if(cfStr.equalsIgnoreCase("lastName"))	{
					value = user.getLastName();
				}
				else if(cfStr.equalsIgnoreCase("addressOne")) {
					value = user.getAddressOne();
				}
				else if(cfStr.equalsIgnoreCase("addressTwo")) {
					value = user.getAddressTwo();
				}
				else if(cfStr.equalsIgnoreCase("city"))	{ 
					value = user.getCity();
				}
				else if(cfStr.equalsIgnoreCase("state")) {
					value = user.getState();
				}
				else if(cfStr.equalsIgnoreCase("country")) {
					value = user.getCountry();
				}
				else if(cfStr.equalsIgnoreCase("pin")) {
					value = user.getPinCode();
				}
				else if(cfStr.equalsIgnoreCase("phone")) {
					value = user.getPhone() != null && user.getPhone().length() != 0 ? user.getPhone() : Constants.STRING_NILL;
				}
				/*else if(cfStr.equalsIgnoreCase("gender")) {
						value = contact.getGender();
					}

					else if(cfStr.equalsIgnoreCase("birthday") ) {

						value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);



					}
					else if(cfStr.equalsIgnoreCase("anniversary") ) {


						value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);


					}*/
				else if(cfStr.equalsIgnoreCase("createdDate") || 
						cfStr.equalsIgnoreCase("anniversary") || 
						cfStr.equalsIgnoreCase("birthday") || 
						cfStr.equalsIgnoreCase(Constants.DATE_PH_DATE_today) || 
						cfStr.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow) ||
						cfStr.endsWith(Constants.DATE_PH_DAYS)) {

					Calendar createdDate = Calendar.getInstance();
					//createdDate.setTime(user.getCreatedDate());	
					value = MyCalendar.calendarToString(createdDate, MyCalendar.FORMAT_DATEONLY_GENERAL);


				}

				/*else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {



						value = getStorePlaceholders(contact,cfStr, defVal);

					}

					else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

						value = getLoyaltyPlaceholders(contact,cfStr, defVal);

					}

					else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)) {

						value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal);


					}
				 */
				//test emails especialy for DR date placeholders should be replaced with actual values

				else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_UNSUBSCRIBE_LINK)) {
					//TODO need to add the new zul source url
					String TestUnSubscribeUrl = PropertyUtil.getPropertyValue("testUnSubscribeUrl");
					value = "<a href=\""+TestUnSubscribeUrl+"\" target=\"_blank\" >unsubscribe</a>";

				}
				else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH__UPDATE_PREFERENCE_LINK)) {


					//String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
					value = "<a href=\"#\" >update subscription preference</a>";

				}

				else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_WEBPAGE_VERSION_LINK)) {

					String weblinkUrl = PropertyUtil.getPropertyValue("parentalWeblinkUrl").replace("|^", "[").replace("^|", "]");;

					value = "<a style=\"color: inherit; text-decoration: underline; \"  href="+weblinkUrl+" target=\"_blank\">View in web-browser</a>";

					//value = "<a href="+webpagelink+">Webpage Link</a>";
				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_TWEET_URL)) {


					value = "<a style=\"color: blue; text-decoration: underline; \"  href=\"#\" >Share on Twitter</a>";
					//value = "<a href="+webpagelink+">Webpage Link</a>";
				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_FACEBOOK_URL)) {

					//String value = "<a style=\"color: blue;\"  href="+webpagelink+">Share on <img src=\"http://localhost:8080/subscriber/images/closetree.jpg\"></a> ";
					value = "<a style=\"color: blue; text-decoration: underline; \"  href=\"#\"  >Share on Facebook</a>";
					//value = "<a href="+webpagelink+">Webpage Link</a>";
				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_FORWRADFRIEND_LINK)) {

					String farwardFriendLink = PropertyUtil.getPropertyValue("forwardToFriendUrl").replace("|^", "[").replace("^|", "]");

					value = "<a href=\"#\"  >Forward to Friend</a>";
					//value = "<a href="+webpagelink+">Webpage Link</a>";
				}
				/*else {
						value = Constants.STRING_NILL;
					}*/
				try {

					if(value != null && !value.trim().isEmpty()) {

						value = ( value.equals("--") &&  defVal != null ) ? defVal : value;
						//cfStr = cfStr.toLowerCase();

					} else {
						value = defVal ;

					}
					htmlContent = htmlContent.replace("["+preStr+"]", value);	
					if(tempCampSubject != null) tempCampSubject = tempCampSubject.replace("["+preStr+"]", value);
					if(textContent != null) textContent = textContent.replace("["+preStr+"]", value);

					logger.debug("place holder is " + preStr +" value here is for ph "+ value);

					contentsStrArr[0] = htmlContent;
					contentsStrArr[1] = textContent;
					contentsStrArr[2] = tempCampSubject;
					logger.debug("html content "+  htmlContent);
					logger.debug("text content "+  textContent);
					logger.debug("subject content "+  tempCampSubject);

				} catch (Exception e) {
					logger.error("Exception while adding the General Fields as place holders ", e);
				}
				//Changes to add mapped UDF fields as placeholders
			}
		} catch (Exception e) {
			logger.error("Exception while adding the Custom Fields as place holders ", e);
		}
		return contentsStrArr;

	}
	
	public String[] getNotificationPlaceHolders(Contacts contact,Set<String> totalPhSet,
			Users user , Long sentId ,
			NotificationRecipientProvider notificationRp , 
			String notificationContent, Long loyaltyId, String msgHeaderToBeSent) {
		String[] contentsStrArr = null;
		
		logger.info("Entered in getContactPhValue method");

		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
			contentsStrArr = new String[4];


			try {

				String value=Constants.STRING_NILL;

				String preStr = Constants.STRING_NILL; 

				for (String cfStr : totalPhSet) {
					//	logger.debug("<<<<   cfStr : "+ cfStr);
					preStr = cfStr;
					if(cfStr.startsWith("GEN_")) {
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {

							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if



						if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email") ) {
							value = contact.getEmailId();
						}

						else if(cfStr.equalsIgnoreCase("firstName")) {
							value = contact.getFirstName();
						}
						else if(cfStr.equalsIgnoreCase("lastName"))	{
							value = contact.getLastName();
						}
						else if(cfStr.equalsIgnoreCase("addressOne")) {
							value = contact.getAddressOne();
						}
						else if(cfStr.equalsIgnoreCase("addressTwo")) {
							value = contact.getAddressTwo();
						}
						else if(cfStr.equalsIgnoreCase("city"))	{ 
							value = contact.getCity();
						}
						else if(cfStr.equalsIgnoreCase("state")) {
							value = contact.getState();
						}
						else if(cfStr.equalsIgnoreCase("country")) {
							value = contact.getCountry();
						}
						else if(cfStr.equalsIgnoreCase("pin")) {
							value = contact.getZip();
						}
						else if(cfStr.equalsIgnoreCase("phone")) {
							value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
						}
						else if(cfStr.equalsIgnoreCase("gender")) {
							value = contact.getGender();
						}

						else if(cfStr.equalsIgnoreCase("birthday") ) {

							value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);



						}
						else if(cfStr.equalsIgnoreCase("anniversary") ) {


							value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						else if(cfStr.equalsIgnoreCase("createdDate") ) {


							value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						
						else if(cfStr.equalsIgnoreCase("organizationName") ) {


							value = getUserOrganization(user, defVal);


						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)) {



							value = getStorePlaceholders(contact,cfStr, defVal);

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

							value = getLoyaltyPlaceholders(user.getUserId(), contact,cfStr, defVal, loyaltyId, false);

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS) || cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) {
							defVal = getDefaultUserAddress(contact.getUsers());
							value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal);
							logger.info("CAMPAIGN_PH_STARTSWITH_LASETPURCHASE"+value);


						}
						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)) {
							defVal = getDefaultUserAddress(contact.getUsers());
                            value = getstoreaddress(contact,cfStr, defVal);
                            logger.info("CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE"+value);
                        }

						else {
							value = Constants.STRING_NILL;
						}

	
						  

							if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
							try {
								
								if(value != null && !value.trim().isEmpty()) {
									
									value = ( value.equals("--") &&  defVal != null) ? defVal : value;
									//cfStr = cfStr.toLowerCase();
									notificationContent = notificationContent.replace("["+preStr+"]", value);
									msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
								} else {
									
									value = defVal;
									notificationContent = notificationContent.replace("["+preStr+"]", value);
									msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
								}
									
							} catch (Exception e) {
								logger.error("Exception while adding the General Fields as place holders ", e);
							}
						} 
						//Changes to add mapped UDF fields as placeholders
						else if(cfStr.startsWith(Constants.UDF_TOKEN) ) {
							 	
								cfStr = cfStr.substring(4);
								String defVal="";
								int defIndex = cfStr.indexOf('=');
								
								if(defIndex != -1) {
									/*defVal = cfStr.substring(defIndex+1);
									cfStr = cfStr.substring(0,defIndex);*/
									defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
									cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
								} // if
								
								int UDFIdx = Integer.parseInt(cfStr.substring("UDF".length()));
								try {
								//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
								value = getConatctCustFields(contact, UDFIdx, null, defVal);
								
								if(value==null || value.isEmpty()) value=defVal;
								
							} catch (Exception e) {
								logger.error("Exception ::::", e);
								logger.info("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
								value = Constants.STRING_NILL;
							}
							
							if(value != null && !value.trim().isEmpty()) {
								
								notificationContent = notificationContent.replace("["+preStr+"]", value);
								msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
							} else {
								value =defVal ;
								notificationContent = notificationContent.replace("["+preStr+"]",value);
								msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
								
							}
						}
						else if (cfStr.startsWith("CC_")) {
							
							value = getCouponPlaceHolderVal(cfStr, Constants.COUP_GENT_CAMPAIGN_TYPE_PUSHNOTIFICATION, 
									contact.getMobilePhone().toString(), sentId , null,contact.getContactId());
							notificationContent = notificationContent.replace("["+preStr+"]", value);
							msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
									
						}else if(cfStr.startsWith("MLS_")) {
							
							String defVal="";
							
							logger.info("Placeholder of the type ----MLS");
							
							value = getMilestonesPlaceHolderVal(cfStr, user, sentId , contact);
							
							logger.info("Value is returned----"+value);
							
							if(value != null && !value.trim().isEmpty()) {
								
								//cfStr = cfStr.toLowerCase();
								notificationContent = notificationContent.replace("["+preStr+"]", value);
								msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
																					
							}
							else {
								
								value = defVal;
								notificationContent = notificationContent.replace("["+preStr+"]", value);
								msgHeaderToBeSent = msgHeaderToBeSent.replace("["+preStr+"]", value);
							}

						}
						else {
							if(logger.isDebugEnabled()) logger.debug("<<<< --2-->>>");
							cfStr = cfStr.substring(3);
							// Removing if the PH if key is not found..
							notificationContent = notificationContent.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
							
						}
						
						//Placeholders key value pair preparation for weblink url replacement
						
						
						if(phKeyValue.length() > 0) phKeyValue.append(Constants.ADDR_COL_DELIMETER)  ;
						
						phKeyValue.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
						
						
						
					}	
					
					contentsStrArr[0] = notificationContent;
					contentsStrArr[2] = msgHeaderToBeSent;
					if(phKeyValue.toString().trim().length() > 0) {
						
						contentsStrArr[1] = phKeyValue.toString();
						
					}else{
						
						contentsStrArr[1] = null;
					}
					
			} catch (Exception e) {
						logger.error("Exception while adding the Custom Fields as place holders ", e);
			}
		} // If PH exist
		
		
		return contentsStrArr;

	}
	public synchronized String generateShortCode(String prefix) {
		logger.info("inside generate shortcode");
		/*
		 * long LIMIT = 100000000L; long id = System.currentTimeMillis() % LIMIT; if (
		 * id <= shortCodeOffSet ) { id = (shortCodeOffSet + 1) % LIMIT; }
		 * shortCodeOffSet=id;
		 */
			String generatedShortCode = prefix +RandomStringUtils.randomAlphanumeric(8);
			return generatedShortCode;
	}
}
