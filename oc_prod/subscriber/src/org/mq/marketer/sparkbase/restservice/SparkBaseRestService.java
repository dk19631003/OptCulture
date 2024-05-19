package org.mq.marketer.sparkbase.restservice;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseAdminService;
//import org.mq.marketer.sparkbase.SparkBaseService;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CardsViewResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.commons.net.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.zkoss.zkplus.spring.SpringUtil;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.business.loyalty.SparkbaseCardFinder;
import org.mq.optculture.utils.OCConstants;
public class SparkBaseRestService  extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	private SparkBaseLocationDetailsDaoForDML sparkBaseLocationDetailsDaoForDML;

	public SparkBaseLocationDetailsDaoForDML getSparkBaseLocationDetailsDaoForDML() {
		return sparkBaseLocationDetailsDaoForDML;
	}
	public void setSparkBaseLocationDetailsDaoForDML(
			SparkBaseLocationDetailsDaoForDML sparkBaseLocationDetailsDaoForDML) {
		this.sparkBaseLocationDetailsDaoForDML = sparkBaseLocationDetailsDaoForDML;
	}


	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	public MailingListDaoForDML getMailingListDaoForDML() {
		return mailingListDaoForDML;
	}
	public void setMailingListDaoForDML(MailingListDaoForDML mailingListDaoForDML) {
		this.mailingListDaoForDML = mailingListDaoForDML;
	}


	private ContactsDao contactsDao;
	private UsersDao usersDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;
	public ContactsLoyaltyDaoForDML getContactsLoyaltyDaoForDML() {
		return contactsLoyaltyDaoForDML;
	}
	public void setContactsLoyaltyDaoForDML(
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML) {
		this.contactsLoyaltyDaoForDML = contactsLoyaltyDaoForDML;
	}

	private UsersDaoForDML usersDaoForDML;
	public UsersDaoForDML getUsersDaoForDML() {
		return usersDaoForDML;
	}

	public void setUsersDaoForDML(UsersDaoForDML usersDaoForDML) {
		this.usersDaoForDML = usersDaoForDML;
	}


	
	private SparkBaseCardDao sparkBaseCardDao;
	private SparkBaseCardDaoForDML sparkBaseCardDaoForDML;
	public SparkBaseCardDaoForDML getSparkBaseCardDaoForDML() {
		return sparkBaseCardDaoForDML;
	}
	public void setSparkBaseCardDaoForDML(
			SparkBaseCardDaoForDML sparkBaseCardDaoForDML) {
		this.sparkBaseCardDaoForDML = sparkBaseCardDaoForDML;
	}


	private ContactParentalConsentDao contactParentalConsentDao;
	private ContactParentalConsentDaoForDML contactParentalConsentDaoForDML;
	public ContactParentalConsentDaoForDML getContactParentalConsentDaoForDML() {
		return contactParentalConsentDaoForDML;
	}

	public void setContactParentalConsentDaoForDML(
			ContactParentalConsentDaoForDML contactParentalConsentDaoForDML) {
		this.contactParentalConsentDaoForDML = contactParentalConsentDaoForDML;
	}


	private CustomTemplatesDao customTemplatesDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	
	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}

	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}


	private PurgeList purgeList;
	
	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerEventsObservable eventTriggerEventsObservable;
	private ContactsDaoForDML contactsDaoForDML;

	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}
	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	
	
	public SparkBaseRestService() {
		// TODO Auto-generated constructor stub
	}
	
	public SparkBaseLocationDetailsDao getSparkBaseLocationDetailsDao() {
		return sparkBaseLocationDetailsDao;
	}

	public void setSparkBaseLocationDetailsDao(
			SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao) {
		this.sparkBaseLocationDetailsDao = sparkBaseLocationDetailsDao;
	}

	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	
	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}

	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public ContactsLoyaltyDao getContactsLoyaltyDao() {
		return contactsLoyaltyDao;
	}

	public void setContactsLoyaltyDao(ContactsLoyaltyDao contactsLoyaltyDao) {
		this.contactsLoyaltyDao = contactsLoyaltyDao;
	}

	public SparkBaseCardDao getSparkBaseCardDao() {
		return sparkBaseCardDao;
	}

	public void setSparkBaseCardDao(SparkBaseCardDao sparkBaseCardDao) {
		this.sparkBaseCardDao = sparkBaseCardDao;
	}

	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}

	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}

	public PurgeList getPurgeList() {
		return purgeList;
	}

	public void setPurgeList(PurgeList purgeList) {
		this.purgeList = purgeList;
	}
	
		
	public ContactParentalConsentDao getContactParentalConsentDao() {
		return contactParentalConsentDao;
	}
	public void setContactParentalConsentDao(
			ContactParentalConsentDao contactParentalConsentDao) {
		this.contactParentalConsentDao = contactParentalConsentDao;
	}
	
	public void setCustomTemplatesDao(CustomTemplatesDao customTemplatesDao) {
		this.customTemplatesDao = customTemplatesDao;
	}
	
	public CustomTemplatesDao getCustomTemplatesDao() {
		return customTemplatesDao;
	}
	
	
	private SMSSettingsDao smsSettingsDao;
	
	public SMSSettingsDao getSmsSettingsDao() {
		return smsSettingsDao;
	}

	public void setSmsSettingsDao(SMSSettingsDao smsSettingsDao) {
		this.smsSettingsDao = smsSettingsDao;
	}
	
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	
	public CaptiwayToSMSApiGateway getCaptiwayToSMSApiGateway() {
		return captiwayToSMSApiGateway;
	}

	public void setCaptiwayToSMSApiGateway(
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway) {
		this.captiwayToSMSApiGateway = captiwayToSMSApiGateway;
	}
	
	private POSMappingDao posMappingDao;
	

	public POSMappingDao getPosMappingDao() {
		return posMappingDao;
	}

	public void setPosMappingDao(POSMappingDao posMappingDao) {
		this.posMappingDao = posMappingDao;
	}

	public EventTriggerEventsObserver getEventTriggerEventsObserver() {
		return eventTriggerEventsObserver;
	}

	public void setEventTriggerEventsObserver(
			EventTriggerEventsObserver eventTriggerEventsObserver) {
		this.eventTriggerEventsObserver = eventTriggerEventsObserver;
	}

	public EventTriggerEventsObservable getEventTriggerEventsObservable() {
		return eventTriggerEventsObservable;
	}

	public void setEventTriggerEventsObservable(
			EventTriggerEventsObservable eventTriggerEventsObservable) {
		this.eventTriggerEventsObservable = eventTriggerEventsObservable;
	}
	
	private ContactsLoyaltyStageDao contactsLoyaltyStageDao;
	
	public ContactsLoyaltyStageDao getContactsLoyaltyStageDao() {
		return contactsLoyaltyStageDao;
	}

	public void setContactsLoyaltyStageDao(
			ContactsLoyaltyStageDao contactsLoyaltyStageDao) {
		this.contactsLoyaltyStageDao = contactsLoyaltyStageDao;
	}
	
	private ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML;
	
	public ContactsLoyaltyStageDaoForDML getContactsLoyaltyStageDaoForDML() {
		return contactsLoyaltyStageDaoForDML;
	}

	public void setContactsLoyaltyStageDaoForDML(
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML) {
		this.contactsLoyaltyStageDaoForDML = contactsLoyaltyStageDaoForDML;
	}

	private MyTemplatesDao myTemplatesDao;
	
	public MyTemplatesDao getMyTemplatesDao() {
		return myTemplatesDao;
	}
	public void setMyTemplatesDao(MyTemplatesDao myTemplatesDao) {
		this.myTemplatesDao = myTemplatesDao;
	}
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String msg = null;
		String emailMsg = "";
		int errorCode = -1;

		boolean isEnableEvent = false;
		Long startId = null;
		Long endId = null;

		boolean isNewContact = false;
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		//register the observable with the observer
		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		//Contacts contact = null; 
		JSONObject enrollmentJsonObj = null;
		JSONObject headerInfo = null;
		JSONObject customerInfo = null;
		SparkBaseLocationDetails sparkBaseLoc = null;
		try {
			Enumeration<String> enumerator = request.getParameterNames();
			
			while(enumerator.hasMoreElements()) {
				String reqParaName = enumerator.nextElement();
				logger.info(" QUERY PARAMETERS  >>> : ");
				logger.info("parameters  : " + reqParaName);
				logger.debug(" Value : " +request.getParameter(reqParaName));
			}
			
			// Check if jsonVal parameter is there
			JSONObject jsonRootObject = null;
			
			if(request.getParameter("jsonVal") != null && request.getParameter("jsonVal").length() > 0) {
				
				String jsonValStr = request.getParameter("jsonVal");
				jsonRootObject = (JSONObject)JSONValue.parse(jsonValStr);
				if(jsonRootObject == null) {
					
					logger.info("Error : Invalid json Object as jsonVal .. Returning. ****");
					msg = "Error : Invalid json Object .. Returning. ****"+emailMsg;
					errorCode = 101201;
					return null;
				}
			} else {
			
			  	InputStream is = request.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				char[] chr = new char[1024];
				int bytesRead = 0;
				StringBuilder sb = new StringBuilder();
				
				while ((bytesRead = br.read(chr)) > 0) {
			         sb.append(chr, 0, bytesRead);
			    }
				logger.debug("REst body value is "+ sb.toString());
				try {
					
					jsonRootObject = (JSONObject)JSONValue.parse(sb.toString());
				} catch(Exception e) {
					
					logger.info("Error : Invalid json Object as REST... ****");
					msg = "Error : Invalid json Object ... ****"+emailMsg;
					errorCode = 101001;
					return null;
				}		
			}
			
			if(jsonRootObject == null) {
				
				logger.info("Error : Unable to parse the json.. Returning. ****");
				msg = "Error : Unable to parse the json... ****"+emailMsg;
				errorCode = 101002;
				return null;
			}
			
			JSONObject jsonMainObject =  (JSONObject)jsonRootObject.get("ENROLLMENTREQ");
			
			if(jsonMainObject == null) {
				
				logger.info("Error : unable to find the EnrollementReq Location in JSON ****");
				msg = "Error : Unable to find the EnrollmentReq Location in JSON  "+emailMsg;
				errorCode = 101003;
				return null;
			}
			
			enrollmentJsonObj = (JSONObject)jsonMainObject.get("ENROLLMENTINFO");
			
			if(enrollmentJsonObj == null) {
				
				logger.info("Error : unable to find the Enrollment info in JSON ****");
				msg = "Error : unable to find the Enrollment info in JSON "+emailMsg;
				errorCode = 101004;
				return null;
			}
			
			JSONObject userJSONObj = (JSONObject)jsonMainObject.get("USERDETAILS");
			
			if(userJSONObj == null) {
				
				logger.info("Error : unable to find the User Details in JSON ****");
				msg = "Error : unable to find the User Details in JSON "+emailMsg;
				errorCode = 101011;
				return null;
			}
			
			String userToken = userJSONObj.get("TOKEN") == null ? null : userJSONObj.get("TOKEN").toString().trim();
			String userName = userJSONObj.get("USERNAME") == null ? null : userJSONObj.get("USERNAME").toString().trim();
			String userOrg = userJSONObj.get("ORGANISATION") == null ? null : userJSONObj.get("ORGANISATION").toString().trim();
			
			
			if(userToken == null || userName == null || userOrg == null) {
				
				logger.info("Error : User Token,UserName,Organisation cannot be empty.");
				msg = "Error : User Token,UserName,Organisation cannot be empty."+emailMsg;
				errorCode = 101012;
				return null;
			}
			
			userName = userName + Constants.USER_AND_ORG_SEPARATOR + userOrg;
			
			Users user = usersDao.findByToken(userName, userToken);
			if(user == null) {
				
				logger.info("Unable to find the user Obj with Token : "+ userToken);
				msg = "Error : Unable to find the User with Token : "+ userToken+emailMsg;
				errorCode = 101013;
				return null;
			}
			
			List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findActiveSBLocByOrgId(user.getUserOrganization().getUserOrgId());
			
			if(sbDetailsList == null) {
				
				logger.info(" No spark Base Details Found for this org: "+ userOrg);
				msg = "Error : No spark Base Details Found for this org: "+ userOrg+emailMsg;
				errorCode = 101005;
				return null;
				
			}
			sparkBaseLoc = sbDetailsList.get(0);
			
			if(sparkBaseLoc == null) {
				
				logger.info("Error : No SparkBaseDetails Found with the given credentials. ****");
				msg = "Error : No SparkBase Details Found with the given credentials."+emailMsg;
				errorCode = 101006;
				return null;
			}
			
			logger.debug("Got sparkbaseloc object "+ sparkBaseLoc);
						
			String posStoreLocId = "";
			Object posStoreLocIdObj = enrollmentJsonObj.get("STORELOCATIONID");
			if(posStoreLocIdObj instanceof String) {
				
				posStoreLocId = (String)posStoreLocIdObj;
				posStoreLocId = posStoreLocId.trim();
				
			}
			
			if(posStoreLocId == null || posStoreLocId.trim().length() <= 0 || !posStoreLocId.equals(sparkBaseLoc.getLocationId().trim())){
				
				logger.info("Error: Invalid storelocationid");
				msg = "Error : Invalid STORELOCATIONID."+emailMsg;
				errorCode = 101404;
				return null;
				
			}
			
			customerInfo = (JSONObject)jsonMainObject.get("CUSTOMERINFO");
			
			if(customerInfo == null)  {
				logger.info("Customerinfo object is missing ..");
				msg = "Error : Unable to find Customerinfo in request JSON."+emailMsg;
				errorCode = 101008;
				return null;
			}
			
			headerInfo = (JSONObject)jsonMainObject.get("HEADERINFO");
			
			if(customerInfo.get("EMAIL") == null && customerInfo.get("PHONE") == null && customerInfo.get("CUSTOMERID") == null) {
				
				logger.info("Email,phone and customerid fields are not available, at least one is mandatory.");
				msg = "Error : Email,phone and customerid fields are not available, at least one is mandatory.."+emailMsg;
				errorCode = 101301;
				return null;
			}
			
			if(((customerInfo.get("EMAIL") != null && customerInfo.get("EMAIL").toString().trim().length() == 0)) && 
					((customerInfo.get("PHONE") != null && customerInfo.get("PHONE").toString().trim().length() == 0)) && 
					((customerInfo.get("CUSTOMERID") != null && customerInfo.get("CUSTOMERID").toString().trim().length() == 0)) ) {
				
				logger.info("Email,phone and customerid fields values are empty, atleast one value mandatory.");
				msg = "Error : Email,phone and customerid fields values are empty, atleast one value  is mandatory."+emailMsg;
				errorCode = 101302;
				return null;
			}
			
			String email = customerInfo.get("EMAIL") == null ? "" : customerInfo.get("EMAIL").toString().trim();
			String phone = customerInfo.get("PHONE") == null ? "" : customerInfo.get("PHONE").toString().trim();
			String custId = customerInfo.get("CUSTOMERID") == null ? "" : customerInfo.get("CUSTOMERID").toString().trim();
			String card = enrollmentJsonObj.get("CARDNUMBER") == null ? "" : enrollmentJsonObj.get("CARDNUMBER").toString().trim();
			
			//find the request in stage
			ContactsLoyaltyStage loyaltyStage = null;
			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, card,
					userName, OCConstants.LOYALTY_SERVICE_TYPE_SB, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			if(requestStage != null){
				logger.info("Duplicate request....timed out request...");
				msg = "Error : Request is being processed.";
				errorCode = 101505;
				return null;
			}
			else{
				logger.info("saving request in stage table...");
				loyaltyStage = new ContactsLoyaltyStage();
				loyaltyStage.setCustomerId(custId);
				loyaltyStage.setEmailId(email);
				loyaltyStage.setPhoneNumber(phone);
				loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
				loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
				//loyaltyStage.setCardNumber(card);
				loyaltyStage.setUserName(userName);
				loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);
				//contactsLoyaltyStageDao.saveOrUpdate(loyaltyStage);
				contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);

			}
			
			
			//create a new contact object based on input values
			Contacts tempCont = new Contacts();
			tempCont.setUsers(user);
			Contacts inputContact = setInputData(tempCont, customerInfo);
			Contacts dbContact = null;
			//finding contact by priority map(modified for CC)
			TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId().longValue(), 
					Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			
			dbContact = contactsDao.findContactByUniqPriority(priorMap, inputContact, user.getUserId().longValue());
			
			logger.info("dbcontacts...."+dbContact);
			//boolean isNewContact = false;
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			boolean updateMLFlag = false;
			
			if(dbContact == null) {
				if(mlList == null) {//need to add this new contact to POS list only,if not there, return.
					logger.info("Unable to find the user POS ml List");
					msg = "Error : Unable to find the user POS mailing List."+emailMsg;
					errorCode = 101007;
					return null;
				}
				
				//contact = new Contacts();
				dbContact = inputContact;
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setUsers(user);
				//dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				dbContact.setCreatedDate(Calendar.getInstance());
				isNewContact = true;
				updateMLFlag = true;
				isEnableEvent = true;
				
				dbContact.setPurged(false);
				purgeList.checkForValidDomainByEmailId(dbContact);
				
				mlList.setListSize(mlList.getListSize()+1);
				
				contactsDaoForDML.saveOrUpdate(dbContact);
				logger.info(" NEW contact saved... ");
				logger.info("OC Contact .... custid="+dbContact.getExternalId()+" email="+dbContact.getEmailId()+" emailStatus="+dbContact.getEmailStatus()+
						" mobile="+dbContact.getMobilePhone()+" mobilestatus="+dbContact.getMobileStatus());
			}
			else {
				//contact = dbcontact;
				
				if(dbContact.getExternalId() == null || dbContact.getExternalId().trim().isEmpty())
					dbContact.setExternalId(inputContact.getExternalId());
								
				if(dbContact.getMlBits().longValue() == 0l ) {//marked as deleted
					dbContact.setAddressOne(inputContact.getAddressOne());
					dbContact.setAddressTwo(inputContact.getAddressTwo());
					dbContact.setAnniversary(inputContact.getAnniversary());
					dbContact.setBirthDay(inputContact.getBirthDay());
					dbContact.setCity(inputContact.getCity());
					dbContact.setCountry(inputContact.getCountry());
					dbContact.setEmailId(inputContact.getEmailId());
					dbContact.setExternalId(inputContact.getExternalId());
					dbContact.setFirstName(inputContact.getFirstName());
					dbContact.setGender(inputContact.getGender());
					dbContact.setLastName(inputContact.getLastName());
					dbContact.setMobilePhone(inputContact.getMobilePhone());
					dbContact.setMobileStatus(inputContact.getMobileStatus());
					
					dbContact.setPurged(false);
					dbContact.setMlBits(mlList.getMlBit());
					dbContact.setUsers(user);
					dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
					dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
					
					isEnableEvent = true;
					updateMLFlag = true;
					
					purgeList.checkForValidDomainByEmailId(dbContact);
					contactsDaoForDML.saveOrUpdate(dbContact);
					
					logger.info("Deleted contact saved...");
					logger.info("Deleted OC Contact .... custid="+dbContact.getExternalId()+" email="+dbContact.getEmailId()+" emailStatus="+dbContact.getEmailStatus()+
							" mobile="+dbContact.getMobilePhone()+" mobilestatus="+dbContact.getMobileStatus());
				}
				else{
					long conMlBits = dbContact.getMlBits();
					if(mlList != null && ( (conMlBits & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
						dbContact.setMlBits(conMlBits | mlList.getMlBit());
						
						mlList.setListSize(mlList.getListSize()+1);
						
						contactsDaoForDML.saveOrUpdate(dbContact);
						logger.info("Existing contact saved...");
						updateMLFlag = true;
					}
					logger.info("Existing OC Contact .... custid="+dbContact.getExternalId()+" email="+dbContact.getEmailId()+" emailStatus="+dbContact.getEmailStatus()+
							" mobile="+dbContact.getMobilePhone()+" mobilestatus="+dbContact.getMobileStatus());
				}//else
			}//else
			
			if(updateMLFlag) {
				mlList.setLastModifiedDate(Calendar.getInstance());
				mailingListDaoForDML.saveOrUpdate(mlList);
			}
			
			//purgeList.checkForValidDomainByEmailId( contact);
			//contactsDao.saveOrUpdate(contact);
			
			
			
			// MERGE PROCESS IS NOT APPLICABLE DUE TO SUBSCRIBER PREFERENCE FEATURE
			
		/*	if(contact.getExternalId()==null && customerInfo.get("CUSTOMERID") != null)
				contact.setExternalId(customerInfo.get("CUSTOMERID").toString().trim());
			
			if(customerInfo.get("FIRSTNAME") != null && !customerInfo.get("FIRSTNAME").toString().trim().isEmpty()){
				
				contact.setFirstName(customerInfo.get("FIRSTNAME").toString().trim());
				
			}
				
			if(customerInfo.get("LASTNAME") != null && !customerInfo.get("LASTNAME").toString().trim().isEmpty()) {
				
				contact.setLastName(customerInfo.get("LASTNAME").toString().trim());
				
			}*/
			
			//if(contact.getEmailId()==null && customerInfo.get("EMAIL") != null){
		/*	if((contact.getLoyaltyCustomer() == null || !(contact.getLoyaltyCustomer().intValue() == 1)) && customerInfo.get("EMAIL") != null){
				email = email.trim();
				if(email != null && !Utility.validateEmail(email) ) {
					
					emailMsg += "\n Given Email Id is not valid : Ignored ";
					
					contact.setEmailId(null);
				}else{
					
					contact.setEmailId(email);
					contact.setPurged(false);
					contact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				}
				
				
			}*/
			
		/*	if(customerInfo.get("ADDRESS1") != null && !customerInfo.get("ADDRESS1").toString().trim().isEmpty()) {
				contact.setAddressOne(customerInfo.get("ADDRESS1").toString().trim());
			}
				
			if(customerInfo.get("ADDRESS2") != null && !customerInfo.get("ADDRESS2").toString().trim().isEmpty()) {
				
				contact.setAddressTwo(customerInfo.get("ADDRESS2").toString().trim());
			}
			
			if(customerInfo.get("CITY") != null && !customerInfo.get("CITY").toString().trim().isEmpty()) {
				
				contact.setCity(customerInfo.get("CITY").toString().trim());
			}
			
			if(customerInfo.get("STATE") != null && !customerInfo.get("STATE").toString().trim().isEmpty()) {
				
				contact.setState(customerInfo.get("STATE").toString().trim());
			}
			
			if(customerInfo.get("COUNTRY") != null && !customerInfo.get("COUNTRY").toString().trim().isEmpty()) {
				
				contact.setCountry(customerInfo.get("COUNTRY").toString().trim());
				
			}
			
			
			if( customerInfo.get("POSTAL") != null) {
				try {
					logger.info(customerInfo.get("POSTAL").toString());
					if(customerInfo.get("POSTAL").toString().trim().length() > 0) {
//						contact.setPin(Integer.parseInt(customerInfo.get("POSTAL").toString()));
						contact.setZip(customerInfo.get("POSTAL").toString().trim());
					}	
				} catch(Exception e) {
					logger.info("Error while adding the pin number ..");
				}	
			}
			
			if(customerInfo.get("BIRTHDAY") != null ) {
				try {
					logger.info("birthday avialable ...");
					if(customerInfo.get("BIRTHDAY").toString().trim().length() > 0) {
						Calendar cal = MyCalendar.dateString2Calendar(customerInfo.get("BIRTHDAY").toString().trim());
						contact.setBirthDay(cal);
					}
				} catch(Exception e) {
					logger.info("Exception : Error occured while setting the Birthday : "+  customerInfo.get("BIRTHDAY") );
				}
			}
			
			
		if(customerInfo.get("ANNIVERSARY") != null && customerInfo.get("ANNIVERSARY").toString().length() > 0) {
			
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.get("ANNIVERSARY").toString().trim());
			contact.setAnniversary(cal);
		
		}
		if( customerInfo.get("GENDER") != null && !customerInfo.get("GENDER").toString().trim().isEmpty()) {
			
			contact.setGender(customerInfo.get("GENDER").toString().trim());
		}	*/

		/*if(contact.getLoyaltyCustomer() == null || !(contact.getLoyaltyCustomer().intValue() == 1) ) {
			//logger.info("no mobile........1");
			if( customerInfo.get("PHONE") != null && customerInfo.get("PHONE").toString().trim().length() > 0) {
		
				try {
					//logger.info("no mobile........2");
					String phoneStr = Utility.phoneParse(customerInfo.get("PHONE").toString().trim());
					if(phoneStr != null ) {
						
						contact.setMobilePhone(phoneStr);
						contact.setMobileStatus(performMobileOptin(contact, user));
					}else {
						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					}
				} catch(Exception e) {
					
					if(contact.getEmailId() == null && contact.getExternalId() == null) {
						
						logger.info("Unable to create contact as phone number is not valid and emailId and phone are also empty.");
						msg = "Error : Unable to create contact as phone number is not valid and emailId and phone are also empty."+emailMsg;
						errorCode = 101303;
						return null;
					}
				}	
			
			}
			else{
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				contact.setMobileOptin(false);
			}
		}	*/
			
			//purgeList.checkForValidDomainByEmailId( contact);
			
			//contactsDao.saveOrUpdate(contact);
			
			//for a new / marked as deleted contact opt-in medium setting fire an event-notify to observer
			if(isEnableEvent) {
				eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
						dbContact.getContactId().longValue(), dbContact.getContactId().longValue() );
			}//if
			
			//sendCardCountAlerts(sparkBaseLoc, user);
			
			String storeNumber = headerInfo.get("STORENUMBER") == null ? "" : headerInfo.get("STORENUMBER").toString();
			
			String[] retStrArr = performLoyaltyEnrollment(enrollmentJsonObj,inputContact, dbContact,user, mlList, sparkBaseLoc,storeNumber , contactLoyalty);
			msg = retStrArr[0];
			errorCode = Integer.parseInt(retStrArr[1]);
			
			//update status of stage request
			//loyaltyStage.setStatus(Constants.LOYALTY_STAGE_COMPLETED);
			logger.info("deleting loyalty stage record...");
			//contactsLoyaltyStageDao.delete(loyaltyStage);
			contactsLoyaltyStageDaoForDML.delete(loyaltyStage);
			
			if(isNewContact  && !mlList.getCheckDoubleOptin() && mlList.isCheckWelcomeMsg()) {
				sendWelcomeEmail(dbContact, mlList, user);
			}
			
		} catch(Exception e) {
			logger.error("Exception in sparkbase enrollment contact preparation", e);
			msg = "Error : Server error occured."+emailMsg;
			errorCode = 101000;
			
		} finally {
			try {
				
				LinkedHashMap jsonMainObject2 = new LinkedHashMap();
				
				if(errorCode == 0) {
					jsonMainObject2.put("CUSTOMERINFO",customerInfo);
					
					/*else {
					customerInfo2 = new LinkedHashMap();
					customerInfo2.put("CUSTOMERTYPE", "");
					customerInfo2.put("CUSTOMERID", contact.getExternalId() != null ?contact.getExternalId() :"" );  //newly added.
					customerInfo2.put("FIRSTNAME", contact.getFirstName() != null ?contact.getFirstName() : "");    //newly added.
					customerInfo2.put("MIDDLENAME","");
					customerInfo2.put("LASTNAME",contact.getLastName() != null ?contact.getLastName() : ""); 
					customerInfo2.put("ADDRESS1",contact.getAddressOne() != null ?contact.getAddressOne() : "");  
					customerInfo2.put("ADDRESS2",contact.getAddressTwo() != null ?contact.getAddressTwo() : ""); 
					customerInfo2.put("CITY",contact.getCity() != null ?contact.getCity() : "");  
					customerInfo2.put("STATE",contact.getState() != null ?contact.getState() : "");  
					customerInfo2.put("POSTAL",contact.getZip() != null ?contact.getZip() : "");  
					customerInfo2.put("COUNTRY",contact.getCountry() != null ?contact.getCountry() : ""); 
					customerInfo2.put("MAILPREF","");
//					customerInfo2.put("PHONE",(contact.getPhone() ==null || contact.getPhone() == 0 ) ? "" : contact.getPhone()); 
					customerInfo2.put("PHONE",(contact.getMobilePhone() != null ) ?  contact.getMobilePhone() : ""); 
					//customerInfo2.put("ISMOBILE",""); 
					customerInfo2.put("PHONEPREF",""); 
					customerInfo2.put("EMAIL",contact.getEmailId() != null ? contact.getEmailId() : ""); 
					customerInfo2.put("EMAILPREF",""); 
					customerInfo2.put("BIRTHDAY",MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR)); 
					customerInfo2.put("ANNIVERSARY",MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR)); 
					customerInfo2.put("GENDER",contact.getGender() != null ? contact.getGender() : ""); 
					
					jsonMainObject2.put("CUSTOMERINFO",customerInfo2);
					}*/
				}
				
				if(errorCode == 0) {
					JSONObject amountDetails = new JSONObject();
					amountDetails.put("VALUECODE", "");
					amountDetails.put("ENTEREDAMOUNT", "");
					jsonMainObject2.put("AMOUNTDETAILS",amountDetails);
				}
				
				if((errorCode == 0 || errorCode == 100101) && enrollmentJsonObj != null) {
					if(contactLoyalty != null && contactLoyalty.getCardNumber() != null) {
						enrollmentJsonObj.put("CARDNUMBER", ""+contactLoyalty.getCardNumber());
						enrollmentJsonObj.put("CARDPIN", contactLoyalty.getCardPin() == null ? "" : ""+contactLoyalty.getCardPin());
						enrollmentJsonObj.put("CARDTYPE", contactLoyalty.getCardType() == null ? "" : ""+contactLoyalty.getCardType());
					}
					jsonMainObject2.put("ENROLLMENTINFO",enrollmentJsonObj);
				}
				else if (errorCode != 0 && errorCode != 100101 && enrollmentJsonObj != null){
					jsonMainObject2.put("ENROLLMENTINFO",enrollmentJsonObj);
				}
				
				JSONObject replyObject = new JSONObject();
				replyObject.put("STATUS", errorCode == 0 ? "Success" : "Failure");
				replyObject.put("MESSAGE", msg);
				replyObject.put("ERRORCODE", ""+errorCode);
				jsonMainObject2.put("STATUS",replyObject);
				
				if(headerInfo != null) {
					jsonMainObject2.put("HEADERINFO", headerInfo);
				}	
				
				JSONObject rootObject = new JSONObject();
				rootObject.put("ENROLLMENTRESPONSE", jsonMainObject2);
				
				logger.info("Response json object : "+ rootObject);
				pw.write(rootObject.toJSONString());
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
		}
		return null;
	}
	
	public String[] performLoyaltyEnrollment(JSONObject enrollmentJsonObj, Contacts sbenrollContact, Contacts tempContact, Users user , MailingList mailingList, 
			SparkBaseLocationDetails sparkBaseLoc, String posStoreLocationId, ContactsLoyalty contactLoyalty) {
		
		String retStrArr[] = new String[2];
		
		String msg = "";
		int errorCode = -1;
		
		retStrArr[0]=msg;
		retStrArr[1]=""+errorCode;
		
		try {
			logger.debug("**********Loyalty Enroll***************");
			
			//contactLoyalty =  contactsLoyaltyDao.findByContactId(tempContact.getContactId());
			//ContactsLoyalty contactLoyaltyExist =  contactsLoyaltyDao.findByContactId(tempContact.getContactId());
			List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(tempContact.getUsers().getUserId(), tempContact.getContactId());
			if(loyaltyList != null && loyaltyList.size() > 0){
				Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
				ContactsLoyalty latestLoyalty = null;
				ContactsLoyalty iterLoyalty = null;
				while(iterList.hasNext()){
					iterLoyalty = iterList.next();
					if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())){
						continue;
					}
					latestLoyalty = iterLoyalty;
				}
				
				contactLoyalty.setCardNumber(latestLoyalty.getCardNumber());
				contactLoyalty.setCardPin(latestLoyalty.getCardPin());
				contactLoyalty.setCardType(latestLoyalty.getCardType());
			}
			//String emailMsgContent = "";
			boolean success = false;
			boolean templateEmail = false;
			if(contactLoyalty != null && contactLoyalty.getCardNumber() != null) {
					success = true;
					logger.debug("Contact is already enrolled");
					msg = "Contact already found with card-number.";
					errorCode = 100101;
					retStrArr[0]=msg;
					retStrArr[1]=""+errorCode;
			}
			else {
					try {
						logger.debug("Contact has to be enrolled");
						SparkBaseCard sbCard = null;
						String card = enrollmentJsonObj.get("CARDNUMBER") == null ? null : enrollmentJsonObj.get("CARDNUMBER").toString().trim();
						String pass = "";
						
						logger.debug("card is :: "+card);
						if(enrollmentJsonObj.get("CARDPIN") != null) {
							pass = enrollmentJsonObj.get("CARDPIN").toString().trim();
						}
						
						   // if json has card information
						   boolean isCardGiven = false;
						   if(card != null && card.trim().length() > 0 ) {
							  isCardGiven = true;
							  String cardLong = null;
							    
							  Pattern digitPattern = Pattern.compile("(\\d+)");  // %B974174416697245^?;974174416697245=?
							  Matcher matcher = null;
							    try {
							    	String cardNum = "";
							    	  matcher = digitPattern.matcher(card);
								          while (matcher.find()) {
								        	  if(cardNum.length() == 15 || cardNum.length() == 16 ) break;
								        	  cardNum += matcher.group(1).trim();
								          } // while
							          card = cardNum;
							          
							    	logger.debug("Card NUmber After removing Extra char: "+card);
							    	cardLong = card;
							    } catch(NumberFormatException e) {
							    	logger.debug("card format error");
									msg = "Card format Error, given card is invalid : "+card;
									errorCode = 100107;
									retStrArr[0]=msg;
									retStrArr[1]=""+errorCode;
									return retStrArr;
							    }	
							    
								List<SparkBaseCard> list = sparkBaseCardDao.findByCardId(sparkBaseLoc.getSparkBaseLocationDetails_id(), cardLong.toString());
						    
								if(list != null && list.size() > 0) {
									sbCard = list.get(0);
									logger.fatal("card given and found in sb cards: "+sbCard.getCardId());
									
									if(!sbCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)) {
										logger.debug("Card status is not inventory");
										msg = "Card status is not inventory";
										errorCode = 100120;
										retStrArr[0]=msg;
										retStrArr[1]=""+errorCode;
										return retStrArr;
									}
							 	}//if no such  card in DB 
								else {
								  // Create new one 
								  logger.debug("create new card...."+cardLong+" "+sparkBaseLoc.getLocationId());
								  sbCard = new SparkBaseCard(cardLong, sparkBaseLoc);
								  sbCard.setCardType(Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
								  sbCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
								  sbCard.setCardPin(pass);
								  sbCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
								}// new card
								
							} 
						   	else {
								logger.debug("card is not given ....");
								sbCard =  findAvailableCardByStore(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
								//sbCard = sparkBaseCardDao.findAvailableCardByStore(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
								
								if(sbCard == null) {
									
									logger.debug("No inventory type cards are available to enroll loyalty for this contact");
									msg = "No inventory type cards are available to enroll loyalty for this contact";
									errorCode = 100102;
									retStrArr[0]=msg;
									retStrArr[1]=""+errorCode;
									return retStrArr;
								}
								logger.info("new card from inventory: "+sbCard.getCardId());
								 
							} //random card
						
						String cardNumber = sbCard.getCardId();
						contactLoyalty.setCardNumber(cardNumber+"");
						contactLoyalty.setCreatedDate(Calendar.getInstance());
						if(sbCard.getCardPin() != null) {
						contactLoyalty.setCardPin(sbCard.getCardPin());
						}
						contactLoyalty.setContact(tempContact);
						contactLoyalty.setUserId(user.getUserId());
						//contactLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
						contactLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
						contactLoyalty.setCustomerId(tempContact.getExternalId());
						contactLoyalty.setLocationId(sparkBaseLoc.getLocationId());
						contactLoyalty.setPosStoreLocationId(posStoreLocationId);
						
						if(enrollmentJsonObj.get("CARDTYPE") != null && enrollmentJsonObj.get("CARDTYPE").toString().trim().length() > 0) {
							
							contactLoyalty.setCardType(enrollmentJsonObj.get("CARDTYPE").toString().trim());
						}
						
						if(enrollmentJsonObj.get("EMPID") != null && enrollmentJsonObj.get("EMPID").toString().trim().length() > 0) {
							
							contactLoyalty.setEmpId(enrollmentJsonObj.get("EMPID").toString());
						}
						
						logger.info("<< Inquiry ..");
						ErrorMessageComponent errorMsg = null;
						Object object = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sparkBaseLoc, contactLoyalty, sbenrollContact, null, true);
						if(object instanceof ErrorMessageComponent) {
							
							errorMsg = (ErrorMessageComponent)object;
						} 
						else if ((object instanceof InquiryResponse)){
							
							InquiryResponse response = (InquiryResponse)object;
							ResponseStandardHeaderComponent standardHeader = response.getStandardHeader();
							
							if (standardHeader.getStatus().equals("E")) {
						          errorMsg = response.getErrorMessage();
						    }
						}	
						
						if(errorMsg == null) {         // already enrolled
							// Get another card
							sbCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
							sbCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
							if(sbCard.getActivationDate() == null){
								sbCard.setActivationDate(Calendar.getInstance());
							}
							//sparkBaseCardDao.saveOrUpdate(sbCard);
							sparkBaseCardDaoForDML.saveOrUpdate(sbCard);

							if(isCardGiven) {
								msg = "Given card is already activated at Sparkbase";
								logger.debug(msg);
								errorCode = 100110;
								retStrArr[0]=msg;
								retStrArr[1]=""+errorCode;
								return retStrArr;
							}
							
							boolean flag = true;
							
							while(flag) {
								sbCard = findAvailableCardByStore(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
								//sbCard = sparkBaseCardDao.findAvailableCardByStore(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
								if(sbCard == null) {
									logger.debug("No inventory type cards are available to enroll loyalty for this contact");
									msg = "No inventory type cards are available to enroll loyalty for this contact";
									errorCode = 100102;
									retStrArr[0]=msg;
									retStrArr[1]=""+errorCode;
									return retStrArr;
								}
								logger.info("new card from inventory: "+sbCard.getCardId());
								contactLoyalty.setCardNumber(sbCard.getCardId()+"");
								contactLoyalty.setCardPin(sbCard.getCardPin());
								
								object = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sparkBaseLoc, contactLoyalty, sbenrollContact,null, true);
								
								if(object instanceof ErrorMessageComponent) {
									
									errorMsg = (ErrorMessageComponent)object;
								} else if ((object instanceof InquiryResponse)){
									
									InquiryResponse response = (InquiryResponse)object;
									ResponseStandardHeaderComponent standardHeader = response.getStandardHeader();
									
									if (standardHeader.getStatus().equals("E")) {
								          errorMsg = response.getErrorMessage();
								    }
								}	
								
								if(errorMsg != null) {
									flag = false;
								} else {
									sbCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
									sbCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
									if(sbCard.getActivationDate() == null){
										sbCard.setActivationDate(Calendar.getInstance());	
									}
								//	sparkBaseCardDao.saveOrUpdate(sbCard);
									sparkBaseCardDaoForDML.saveOrUpdate(sbCard);

								}
								
							}	// while
							
							
						} else if (errorMsg != null && Long.parseLong(errorMsg.getErrorCode()) != 114) { // other error
							logger.debug("err msgcode is not 114 "+errorMsg);
							msg = errorMsg.getBriefMessage();
							msg += " ( "+errorMsg.getInDepthMessage() +" ) ";
							errorCode = 100108;
							retStrArr[0]=msg;
							retStrArr[1]=""+errorCode;
							return retStrArr;
						} 
						
						if(errorMsg != null && Long.parseLong(errorMsg.getErrorCode()) == 114) {
							ErrorMessageComponent errMsg = null;
							Object enrollRespObj = null;
							// Get the latest Loyalty balance info
							logger.info("enrollment call..."+contactLoyalty.getCardNumber()+" sbloc: "+sparkBaseLoc.getLocationId());
							enrollRespObj = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ENROLLMENT, sparkBaseLoc, contactLoyalty, sbenrollContact, null, true);
							if(enrollRespObj instanceof ErrorMessageComponent) {
								errMsg = (ErrorMessageComponent)enrollRespObj;
							} else {
								errMsg = null;
							}	
							
							if(errMsg != null) {
									logger.debug("error msg is not null after enrollment "+errMsg);
									msg = errMsg.getBriefMessage();
									msg += " ( "+errMsg.getInDepthMessage() +" ) ";
									errorCode = 100103;
									retStrArr[0]=msg;
									retStrArr[1]=""+errorCode;
									
									sbCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
									//sparkBaseCardDao.saveOrUpdate(sbCard);
									sparkBaseCardDaoForDML.saveOrUpdate(sbCard);

									return retStrArr;
							}
							
							//for empty pin
							if(sbCard.getCardPin() == null || sbCard.getCardPin().trim().isEmpty() ) {
								
								  // call the cards view of admin API to get PIN
								  Object cardresponseObj = SparkBaseAdminService.cardsView(card, sbCard);
								  if(cardresponseObj != null && cardresponseObj instanceof CardsViewResponse) {
									  logger.debug("pin is set from SB through admin API ");
									  contactLoyalty.setCardPin(sbCard.getCardPin());
								  }
								  else{
									  logger.debug("got errors  in cardView ");
								  }
								
							}//if 
							
							if(sbCard.getCardPin() == null || sbCard.getCardPin().trim().isEmpty() ) {
								
								msg = "Unable to find the PIN for the given Card : "+card;
								logger.debug(msg);
								errorCode = 100109;
								retStrArr[0]=msg;
								retStrArr[1]=""+errorCode;
								return retStrArr;
								
							}
							
							sbCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
							sbCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
							if(sbCard.getActivationDate() == null ){
								sbCard.setActivationDate(Calendar.getInstance());
							}
							//sparkBaseCardDao.saveOrUpdate(sbCard);
							sparkBaseCardDaoForDML.saveOrUpdate(sbCard);

							//logger.error("saving loyalty object..." + contactLoyalty.getCardNumber()+"__ "+contactLoyalty.getPosStoreLocationId()+"__"+contactLoyalty.getContact().getContactId());
							
							contactLoyalty.setMode(OCConstants.LOYALTY_ONLINE_MODE);
							contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
							contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
							contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
							
							contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
							success = true;
							templateEmail = true;
							msg = "Loyalty enrollment is successful.";
							errorCode = 0;
							retStrArr[0]=msg;
							retStrArr[1]=""+errorCode;
						}
						
					} catch (Exception e) {
						logger.error("Exception ::::", e);
						msg = "Exception occured while creating new card.";
						errorCode = 100104;
						retStrArr[0]=msg;
						retStrArr[1]=""+errorCode;
					}
				}//if
			
			if(success) {
				tempContact.setLoyaltyCustomer((byte)1);
				contactsDaoForDML.saveOrUpdate(tempContact);
				//to send the loyalty related email
				if (mailingList.isCheckLoyaltyOptin() && templateEmail) {
				 CustomTemplates custTemplate = null;
				  String message = PropertyUtil.getPropertyValueFromDB("loyaltyOptinMsgTemplate");
				  
				  if(mailingList.getLoyaltyCutomTempId() != null) {
					  
					  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getLoyaltyCutomTempId());
					  if(custTemplate!= null) {
						  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
							  message = custTemplate.getHtmlText();
						  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
							  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
							 if(myTemplates != null) message = myTemplates.getContent();
						  }
					  }
				  }
				  logger.debug("-----------email----------"+tempContact.getEmailId());
				  
				  message = message.replace("<OrganisationName>", user.getUserOrganization().getOrganizationName())
						  .replace("[CardNumber]", ""+contactLoyalty.getCardNumber()).replace("[CardPin]", contactLoyalty.getCardPin());
				  
				  EmailQueue testEmailQueue = new EmailQueue(mailingList.getLoyaltyCutomTempId(),Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, message, "Active",
						  				tempContact.getEmailId(), user, MyCalendar.getNewCalendar(), "Loyalty Card Details.",
						  				null, tempContact.getFirstName(), null, tempContact.getContactId());
						
					logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
					
					//emailQueueDao.saveOrUpdate(testEmailQueue);
					emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
				}
				
				
				if(sparkBaseLoc.isEnableAlerts()){
					
					sendCardCountAlerts(sparkBaseLoc, user);
				}
			}//if success
			return retStrArr;
		} catch (Exception e) {
			logger.error("Exception in creating loyalty card.", e);
			msg = "Unable to create loyalty card.";
			errorCode = 100100;
			retStrArr[0]=msg;
			retStrArr[1]=""+errorCode;
			return retStrArr;

		}
		
	}//performLoyaltyEnrollment()
	
	
	public void sendCardCountAlerts(SparkBaseLocationDetails sparkBaseLoc, Users user){
		
		logger.debug("Entered sendCardCountAlerts ");
		if(sparkBaseLoc.isEmailAlerts()) {
			String supportEmailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
			Calendar currentDate = Calendar.getInstance();
			Calendar dbCurrentDate = sparkBaseLoc.getLoyaltyAlertsSentDate();
			logger.debug("dbCurrentDate "+dbCurrentDate);
			
			int dateDiff = 0;
			if(dbCurrentDate != null){
				logger.debug("currentDate.getTimeInMillis()"+currentDate.getTimeInMillis());
				logger.debug(" dbCurrentDate.getTimeInMillis())"+ dbCurrentDate.getTimeInMillis());
				dateDiff = (int)((currentDate.getTimeInMillis() - dbCurrentDate.getTimeInMillis())/(1000*60));
			}
			
			logger.debug("dateDiff  "+dateDiff);
			 if(dateDiff > 24*60 || dbCurrentDate == null) {
//				String message=null;
				 Long totalInventoryCards= sparkBaseCardDao.findTotalInventoryCardsByLocId(sparkBaseLoc.getSparkBaseLocationDetails_id());
				String messageToUser = PropertyUtil.getPropertyValueFromDB("loyaltyEmailAlertsUserMsg");
				messageToUser = messageToUser.replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_FNAME,user.getFirstName()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_NOOFCARDS,totalInventoryCards.toString());
				String messageToSupport = PropertyUtil.getPropertyValueFromDB("loyaltyEmailAlertsSupportMsg");
				messageToSupport = messageToSupport.replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_USERNAME,user.getUserName()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_LOCATIONID,sparkBaseLoc.getLocationId()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_NOOFCARDS,totalInventoryCards.toString());
				Long count=null;
				if(sparkBaseLoc.getCountType().equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE)) {//percentage
					
					Long totalCards=sparkBaseCardDao.findTotalCardsByLocId( sparkBaseLoc.getSparkBaseLocationDetails_id());
					Long percentOfCards=Long.parseLong(sparkBaseLoc.getCountValue());
					count=(percentOfCards * totalCards)/100;
					
					if(totalInventoryCards <= count ) {
						
						EmailQueue testEmailQueue = new EmailQueue("Loyalty Email Alerts",messageToUser, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",user.getEmailId(),MyCalendar.getNewCalendar(), user);
						EmailQueue testEmailQueue1= new EmailQueue("Loyalty Email Alerts",messageToSupport, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",supportEmailId,MyCalendar.getNewCalendar(), user);
						//emailQueueDao.saveOrUpdate(testEmailQueue);
						emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
						//emailQueueDao.saveOrUpdate(testEmailQueue1);
						emailQueueDaoForDML.saveOrUpdate(testEmailQueue1);
						sparkBaseLoc.setLoyaltyAlertsSentDate(Calendar.getInstance());
						//sparkBaseLocationDetailsDao.saveOrUpdate(sparkBaseLoc);
						sparkBaseLocationDetailsDaoForDML.saveOrUpdate(sparkBaseLoc);

					}
					
				}//if
			
				else if(sparkBaseLoc.getCountType().equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT)){//count
					count=Long.parseLong(sparkBaseLoc.getCountValue());
			  		if(totalInventoryCards <= count) {
					
					EmailQueue testEmailQueue = new EmailQueue("Loyalty Email Alerts",messageToUser, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",user.getEmailId(),MyCalendar.getNewCalendar(), user);
					EmailQueue testEmailQueue1= new EmailQueue("Loyalty Email Alerts",messageToSupport, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",supportEmailId,MyCalendar.getNewCalendar(), user);
					//emailQueueDao.saveOrUpdate(testEmailQueue);
					emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
					//emailQueueDao.saveOrUpdate(testEmailQueue1);
					emailQueueDaoForDML.saveOrUpdate(testEmailQueue1);
					sparkBaseLoc.setLoyaltyAlertsSentDate(Calendar.getInstance());
					//sparkBaseLocationDetailsDao.saveOrUpdate(sparkBaseLoc);
					sparkBaseLocationDetailsDaoForDML.saveOrUpdate(sparkBaseLoc);

					logger.debug("--------loyalty alert date-------------"+sparkBaseLoc.getLoyaltyAlertsSentDate());
					}
				} //if
			 }
		}
	
	}//sendCardCountAlerts()
	
	
	public void performParentalConsent(Contacts tempContact, ContactParentalConsent contactConsent,
			MailingList mailingList, String parentEmail, Users user) {
		
		logger.debug("**********Parental Concent***************");
		
		if(mailingList.isCheckParentalConsent() && tempContact.getBirthDay() != null) {
	    	  if(((Calendar.getInstance().getTimeInMillis()-tempContact.getBirthDay().getTimeInMillis())/(1000 * 60*60*24)) <= (365*13)) {
	    		  
	    		 contactConsent = contactParentalConsentDao.findByContactId(tempContact.getContactId());
	    		  if(contactConsent != null) {
	    			  
	    			  logger.debug("email has already been sent to it parent ::"+contactConsent.getEmail());
	    			  return;
	    			  
	    		  }
	    		  contactConsent = new ContactParentalConsent(tempContact.getContactId(), parentEmail,
	    				  Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL, user.getUserId(), Calendar.getInstance(),tempContact.getEmailId());
	    		  
	    		  contactConsent.setChildFirstName(tempContact.getFirstName());
	    		  contactConsent.setChildDOB(tempContact.getBirthDay());
	    		  //contactParentalConsentDao.saveOrUpdate(contactConsent);
	    		  contactParentalConsentDaoForDML.saveOrUpdate(contactConsent);
	    		  
	    		  CustomTemplates custTemplate = null;
	    		  String message = PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
	    		  if(mailingList.getConsentCutomTempId() != null) {
	    			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getConsentCutomTempId());
	    			  if(custTemplate!= null){ 
	    				  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
		    				  message = custTemplate.getHtmlText();
		    			  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
							  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
							  if(myTemplates != null)message = myTemplates.getContent();
						  }
	    			  }
	    		  }
	    		  logger.debug("-----------email----------"+tempContact.getEmailId());
	    		  
	    		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getConsentCutomTempId(),Constants.EQ_TYPE_TEST_PARENTAL_MAIL, message, "Active", parentEmail, user, MyCalendar.getNewCalendar(),
	    				  "Require Parental Consent.", tempContact.getEmailId(), tempContact.getFirstName(), MyCalendar.calendarToString(tempContact.getBirthDay(), MyCalendar.FORMAT_DATEONLY), tempContact.getContactId());
	    				
	    			//testEmailQueue.setChildEmail(childEmail);
	    			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
	    			//emailQueueDao.saveOrUpdate(testEmailQueue);
	    			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		    		  
	    	  }//if
	    	  
	      }//if
		
		
	}//performParentalConsent()

	public void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {
		
		//to send the loyalty related email
		 CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(mailingList.getWelcomeCustTempId() != null) {
			  
			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			  if(custTemplate!= null){ 
				  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
    				  message = custTemplate.getHtmlText();
    			  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
				  }
			  }
		  }
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, MyCalendar.getNewCalendar(), " Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		
		
	}//sendWelcomeEmail

	public String performMobileOptin(Contacts contact, Users currentUser) throws Exception {
		
		
		SMSSettings smsSettings = null;
		if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
	
		//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN );
		
		if(smsSettings == null) {
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
			
		}
		Users user = smsSettings.getUserId();
		OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
				SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
		if(ocsmsGateway == null) {
			
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		
		//currentUser = smsSettings.getUserId();//to avoid lazy=false from contacts
		//do only when the existing phone number is not same with the entered
		byte optin = 0;
		String phone = contact.getMobilePhone();
		String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
		if(contact.getOptinMedium() != null) {
			
			if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
				optin = 1;
			}
			else if(contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
				optin = 2;
			}
			else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
				optin = 4;
			}
		}
		
		Users contactOwner = contact.getUsers();
		Byte userOptinVal =	smsSettings.getOptInMedium();
		
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
				contactOwner.getOptInMedium() : userOptinVal;
				
		if(smsSettings.isEnable() && 
				userOptinVal != null && 
				(userOptinVal.byteValue() & optin ) > 0 ) {	
			//TODO after the above todo done consider only one among these two conditions on contact
			if(contact.getLastSMSDate() == null && contact.isMobileOptin() != true) {
				
				mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
				contact.setMobileStatus(mobileStatus);
				contact.setLastSMSDate(Calendar.getInstance());
				if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
					
					logger.debug("low credits with clickatell");
					return mobileStatus;
				}
				
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
							
							msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
						//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader() + " "+ msgContent;
					}
					
					mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
							smsSettings.getSenderId(), phone, msgContent, smsSettings.getUserId());

					if(mobileStatus == null) {
						
						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					}
					
					if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
						contactsDaoForDML.updatemobileStatus(phone, mobileStatus, currentUser);
					}
					
					/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount()) +1);
					usersDao.saveOrUpdate(currentUser);*/
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
					
					/**
					 * Update Sms Queue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					smsQueueHelper.updateSMSQueue(phone,msgContent, Constants.SMS_MSG_TYPE_OPTIN,  currentUser, smsSettings.getSenderId());
					
				}else {
					logger.debug("low credits with user...");
					return mobileStatus;
				}
			}//if
		}//if
		
		else{
			
			if(contact.getMobilePhone() != null) {
				mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
				contact.setMobileStatus(mobileStatus);
			}
		}
		
		return mobileStatus;
	}
	
	public Contacts setInputData(Contacts inputContact, JSONObject customerInfo) {
		
		inputContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
		if(customerInfo.get("CUSTOMERID") != null && !customerInfo.get("CUSTOMERID").toString().trim().isEmpty())
			inputContact.setExternalId(customerInfo.get("CUSTOMERID").toString());
		
		if(customerInfo.get("EMAIL") != null) {
			String email = customerInfo.get("EMAIL").toString().trim();
			if(email != null && !Utility.validateEmail(email) ) {
				inputContact.setEmailId(null);
			}else{
				inputContact.setEmailId(email);
			}
		}
		
		if(customerInfo.get("FIRSTNAME") != null && !customerInfo.get("FIRSTNAME").toString().trim().isEmpty()){
			inputContact.setFirstName(customerInfo.get("FIRSTNAME").toString().trim());
		}
			
		if(customerInfo.get("LASTNAME") != null && !customerInfo.get("LASTNAME").toString().trim().isEmpty()) {
			inputContact.setLastName(customerInfo.get("LASTNAME").toString().trim());
		}
		
		if(customerInfo.get("ADDRESS1") != null && !customerInfo.get("ADDRESS1").toString().trim().isEmpty()) {
			inputContact.setAddressOne(customerInfo.get("ADDRESS1").toString().trim());
		}
			
		if(customerInfo.get("ADDRESS2") != null && !customerInfo.get("ADDRESS2").toString().trim().isEmpty()) {
			
			inputContact.setAddressTwo(customerInfo.get("ADDRESS2").toString().trim());
		}
		
		if(customerInfo.get("CITY") != null && !customerInfo.get("CITY").toString().trim().isEmpty()) {
			
			inputContact.setCity(customerInfo.get("CITY").toString().trim());
		}
		
		if(customerInfo.get("STATE") != null && !customerInfo.get("STATE").toString().trim().isEmpty()) {
			
			inputContact.setState(customerInfo.get("STATE").toString().trim());
		}
		
		if(customerInfo.get("COUNTRY") != null && !customerInfo.get("COUNTRY").toString().trim().isEmpty()) {
			
			inputContact.setCountry(customerInfo.get("COUNTRY").toString().trim());
			
		}
		
		if(customerInfo.get("POSTAL") != null) {
			try {
				if(customerInfo.get("POSTAL").toString().trim().length() > 0) {
					inputContact.setZip(customerInfo.get("POSTAL").toString().trim());
				}	
			} catch(Exception e) {
				logger.info("Error while adding the pin number ..");
			}	
		}
		
		if(customerInfo.get("BIRTHDAY") != null ) {
			try {
				if(customerInfo.get("BIRTHDAY").toString().trim().length() > 0) {
					Calendar cal = MyCalendar.dateString2Calendar(customerInfo.get("BIRTHDAY").toString().trim());
					inputContact.setBirthDay(cal);
				}
			} catch(Exception e) {
				logger.info("Exception : Error occured while setting the Birthday : "+  customerInfo.get("BIRTHDAY") );
			}
		}
		
		if(customerInfo.get("ANNIVERSARY") != null && customerInfo.get("ANNIVERSARY").toString().trim().length() > 0) {
			
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.get("ANNIVERSARY").toString().trim());
			inputContact.setAnniversary(cal);
		
		}
		if( customerInfo.get("GENDER") != null && !customerInfo.get("GENDER").toString().trim().isEmpty()) {
			
			inputContact.setGender(customerInfo.get("GENDER").toString().trim());
		}	
		
		/*if(customerInfo.get("PHONE") != null) {
			
			if(customerInfo.get("PHONE").toString().trim().length() > 0) {
				try {
					
					String phoneStr = Utility.phoneParse(customerInfo.get("PHONE").toString().trim());
					if(phoneStr != null ) {
						inputContact.setMobilePhone(phoneStr);
						inputContact.setMobileStatus(performMobileOptin(inputContact, inputContact.getUsers()));
					}else {
						inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					}
				} catch(Exception e) {
					logger.error("Exception in phone parse", e);
				}	
			}
		}*/
		
		
		if( customerInfo.get("PHONE") != null && customerInfo.get("PHONE").toString().trim().length() > 0) {
			try {
				Users users = inputContact.getUsers();
				UserOrganization organization=  users!=null ? users.getUserOrganization() : null ;

				String phoneStr = Utility.phoneParse(customerInfo.get("PHONE").toString().trim(),organization);
				if(phoneStr != null ) {
					inputContact.setMobilePhone(phoneStr);
					if(inputContact.getUsers().isEnableSMS() && inputContact.getUsers().isConsiderSMSSettings()){
						inputContact.setMobileStatus(performMobileOptin(inputContact, inputContact.getUsers()));
					}else{
						inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
				}else {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			} catch(Exception e) {
				logger.error("Exception in phone parse", e);
			}	
		}
		else{
			inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			inputContact.setMobileOptin(false);
		}
		logger.info("POS inputContact .... custid="+inputContact.getExternalId()+" email="+inputContact.getEmailId()+" emailStatus="+inputContact.getEmailStatus()+
				" mobile="+inputContact.getMobilePhone()+" mobilestatus="+inputContact.getMobileStatus());
		return inputContact;
	}
	
	/*private synchronized SparkBaseCard findAvailableCardByStore(Long sbLocId, String cardType) {
		
		SparkBaseCard sbcard = null;
		try{
		
			sbcard = sparkBaseCardDao.findAvailableCardByStore(sbLocId, cardType);
		
			if(sbcard != null){
				sbcard.setStatus(Constants.SPARKBASE_CARD_STATUS_SELECTED);
				sparkBaseCardDao.saveOrUpdate(sbcard);
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching new card from inventory..", e);
		}
		return sbcard;
	}*/
	
	private SparkBaseCard findAvailableCardByStore(Long sbLocId, String cardType) {
		
		SparkbaseCardFinder cardFinder = new SparkbaseCardFinder(); 
		return cardFinder.findInventoryCardFromLocation(sbLocId, cardType);
		
	}
}
