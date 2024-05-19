package org.mq.optculture.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutosmsSenturlShortcode;
import org.mq.marketer.campaign.beans.AutosmsUrls;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.couponcode.ReferralcodeProvider;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.AutoSmsUrlShortCodeDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;


public class ReplacePlaceHolders {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private ReferralcodeProvider referralcodeProvider;
	private ReferralcodesIssuedDao referralcodesIssuedDao;

	public ReferralcodesIssuedDao getReferralcodesIssuedDao() {
		return referralcodesIssuedDao;
	}

	public void setReferralcodesIssuedDao(ReferralcodesIssuedDao referralcodesIssuedDao) {
		this.referralcodesIssuedDao = referralcodesIssuedDao;
	}

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



							value = getStorePlaceholders(contact,cfStr, defVal, user.getUserOrganization().getUserOrgId());

						}

						else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

							value = getLoyaltyPlaceholders(user.getUserId(), contact,cfStr, defVal, loyaltyId, true, user.getUserOrganization().getUserOrgId());

						}

						else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)) {

							value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal, user.getUserOrganization().getUserOrgId());


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
							value = getConatctCustFields(contact, UDFIdx,  defVal);

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
						 
						  value="";
						
						 //to make coupon providing logic to be sync ,let the scheduler
						 //only offer a coupon code
						String postData = "cfStr="+cfStr+"&issuedTo="+issuedTo+"&type="+OCConstants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS+(contact != null ? 
								"&cid="+contact.getContactId() : "");
						URL url = new URL(PropertyUtil.getPropertyValue(Constants.COUP_PROVIDER_FOR_SUBSCRIBER_URL));
						logger.debug("url==="+url);
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						urlconnection.setRequestMethod("POST");
						urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
						urlconnection.setDoOutput(true);

						OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
						out.write(postData);
						out.flush();
						out.close();
						
						
						BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
						
						String decodedString;
						while ((decodedString = in.readLine()) != null) {
							value += decodedString;
						}
						in.close();
						logger.info("response is======>"+value);

						
						if(value == null) value = "";
						
						content = content.replace("[" + cfStr + "]", value);
						
					}
					
					else if(cfStr.startsWith("REF_CC_")) {
						
							if(referralcodeProvider == null) { //get coupon provider
								logger.info("entered as referral provider is null");
								referralcodeProvider = ReferralcodeProvider.getReferralcodeProviderInstance(null, referralcodesIssuedDao);
								if(referralcodeProvider == null) {
									if(logger.isInfoEnabled()) logger.info("No Referral provider found....");{
										return null;

									}
								}//if

								logger.info("Couponset value before adding"+referralcodeProvider.couponSet);

								if(referralcodeProvider.couponSet != null ) {	
									logger.info("Coupon Set starting");
									if(!referralcodeProvider.couponSet.contains(cfStr)) {
					
										logger.info("CouponId"+cfStr);
										referralcodeProvider.couponSet.add(cfStr);
									}
								}
								logger.info("CouponSet :"+referralcodeProvider.couponSet);
							}else {
								logger.info("entered as referralProvider not null");
								referralcodeProvider.couponSet.add(cfStr);
							}
			
							ReferralcodesIssuedDao refcodesissued=null;
							List<ReferralcodesIssued> refissuedobj=null;
							refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
							
							refissuedobj = refcodesissued.getRefcodebycontactid(contact.getContactId());
							logger.info("Ref Object :"+refissuedobj);
			
							if(refissuedobj != null && refissuedobj.size()>0) {	
								logger.info("Ref code exists");
								value = refissuedobj.get(0).getRefcode();
							}
							else {
								logger.info("Ref code not exist");
								value = referralcodeProvider.getNextCouponCode(cfStr,"",
										"AutoSMS", contact.getMobilePhone(),null, null,contact.getContactId(),true,user.getUserId());;
							}
			
							if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Referral-code "+ value);

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
						smsCampaignSentUrlShortCode.setAutoSmsQueueSentId(sentId+"");
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
	private String getStorePlaceholders(Contacts contact, String placeholder, String defVal, Long orgId) throws Exception{

		OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName("organizationStoresDao");

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
	
	
	private String getLoyaltyPlaceholders(Long userId, Contacts contact, String placeholder, String defVal, Long loyaltyId, boolean isSms, Long orgId) {


		try {


			String loyaltyPlaceholder =defVal;
			DecimalFormat f=new DecimalFormat("#0.00"); 
			ContactsLoyalty contactsLoyalty = null;
			
			ContactsLoyaltyDao	contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

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
	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
		logger.info("--Start of fetchExpiryValues--");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		logger.info("--Exit of fetchExpiryValues--");
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag, false);
	}//fetchExpiryValues

	

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
	private String getLastBonusValue(Long userId, Long loyaltyId,String transactionType, String defVal) {
		logger.info("--Start of getLastBonusValue--");
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType( loyaltyId, transactionType, userId);
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
	private String getLastPurchaseStorePlaceholders(Contacts contact, String placeholder, String defVal, Long orgId){
		try {
			RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName("organizationStoresDao");RetailProSalesCSV lastSaleRecord = retailProSalesDao.findLastpurchaseRecord(contact.getContactId(), contact.getUsers().getUserId());//(contact.getExternalId(),contact.getUsers().getUserId());
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
	private  String getConatctCustFields(Contacts contact , int index, String defVal)  {

		String retVal = defVal;
		// if(contactPHValue==null) return retVal;

		try {
			switch(index){
			case 1: retVal = contact.getUdf1(); 
			
			break; 
			case 2: retVal = contact.getUdf2(); 
			
			break;  
			case 3: retVal = contact.getUdf3();
			
			break;  
			case 4: retVal = contact.getUdf4();
			
			break;  
			case 5: retVal = contact.getUdf5(); 
			
			break;  
			case 6: retVal = contact.getUdf6(); 
			break;  
			case 7: retVal = contact.getUdf7(); 
			break;  
			case 8: retVal = contact.getUdf8(); 
			break;  
			case 9: retVal = contact.getUdf9(); 
			break;  
			case 10: retVal = contact.getUdf10(); 
			break;  
			case 11: retVal = contact.getUdf11(); 
			break;  
			case 12: retVal = contact.getUdf12(); 
			break;  
			case 13: retVal = contact.getUdf13(); 
			break;  
			case 14: retVal = contact.getUdf14(); 
			break;  
			case 15: retVal = contact.getUdf15(); 
			break;  
			//logger.info("fo contact "+contact.getEmailId()+" val ::"+retVal);
			//default : return retVal;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//logger.error("excp   ",e);
			return defVal;
		}
		if (retVal == null) retVal = defVal; 	

		return retVal;



	} // setConatctCustFields
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
		child = ltyPrgmService.getTransByMembershipNoAndTransType( loyaltyId, loyaltyTransTypeIssuance, userId);
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
		child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeRedemption, userId);
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

}
