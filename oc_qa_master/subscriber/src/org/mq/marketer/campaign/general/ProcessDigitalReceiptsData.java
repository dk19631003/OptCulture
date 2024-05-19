package org.mq.marketer.campaign.general;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeMap;
//import java.util.Map.Entry;













import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


//import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.beans.SalesLiteralHashCode;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
//import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SalesLiteralHashCodeGenerateDaoForDML;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.SkuFileDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.SalesLiteralHashCodeGenerateDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ProcessDigitalReceiptsData extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ApplicationContext context;

	public ProcessDigitalReceiptsData(){}
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
		
	}
	
	private static Class strArg[] = new Class[] { String.class };
	private static Class calArg[] = new Class[] { Calendar.class };
	private static Class doubleArg[] = new Class[] { Double.class };
	private static Class longArg[] = new Class[] { Long.class };
	
	
	//private static Map<String, String> drReceiptDataMap  = new HashMap<String, String>();
	

	UsersDao usersDao;
	MailingListDao mailingListDao;
	MailingListDaoForDML mailingListDaoForDML;
	POSMappingDao posMappingDao;
	DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML;
	ContactsDao contactsDao;
	ContactsDaoForDML contactsDaoForDML;
	SMSSettingsDao smsSettingsDao;
	SkuFileDao skuFileDao;
	SkuFileDaoForDML skuFileDaoForDML;
	SalesLiteralHashCodeGenerateDao salesLiteralHashCodeGenerateDao;
	SalesLiteralHashCodeGenerateDaoForDML salesLiteralHashCodeGenerateDaoForDML;
	RetailProSalesDao retailProSalesDao;
	RetailProSalesDaoForDML retailProSalesDaoForDML;
	//DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	MastersToTransactionMappingsDao mastersToTransactionMappingsDao;
	private CustomTemplatesDao customTemplatesDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private MyTemplatesDao myTemplatesDao;
	
	private Set<String> optinMobileSet;
	private SMSSettings smsSettings ;
	private org.mq.marketer.campaign.beans.OCSMSGateway ocsmsGateway;
	
	Contacts inputContact;
	List<SkuFile> dbskuFileList;
	List<RetailProSalesCSV> retailProSalesList;
	PurgeList purgeList;
	
	boolean contactProcessed = false;
	boolean contactExistInOC = false;
	
	//added for autoresponder events(EVENT TRIGGER)
	private EventTriggerEventsObservable eventTriggerEventsObservable;
	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerDao eventTriggerDao;
	private boolean isEnableContactEvent ;
	private boolean isEnableEventForInputContact;
	private boolean isEnableSalesEvent;
	private Long startId ;
	private Long endId ;
	private Long salesStartId;
	private Long salesEndId ;
	
	private void resetFields() {
		smsSettings = null;
		//userSMSGateway = null;
		ocsmsGateway = null;
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		usersDao = (UsersDao)context.getBean("usersDao");
		mailingListDao = (MailingListDao)context.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)context.getBean("mailingListDaoForDML");
		digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getBean("digitalReceiptsJSONDao");
		digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)context.getBean("digitalReceiptsJSONDaoForDML");
		posMappingDao = (POSMappingDao)context.getBean("posMappingDao");
		contactsDao = (ContactsDao)context.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
		smsSettingsDao = (SMSSettingsDao)context.getBean("smsSettingsDao");
		skuFileDao = (SkuFileDao)context.getBean("skuFileDao");
		skuFileDaoForDML = (SkuFileDaoForDML)context.getBean("skuFileDaoForDML");
		salesLiteralHashCodeGenerateDao = (SalesLiteralHashCodeGenerateDao)context.getBean("salesLiteralHashCodeGenerateDao");
		salesLiteralHashCodeGenerateDaoForDML = (SalesLiteralHashCodeGenerateDaoForDML)context.getBean("salesLiteralHashCodeGenerateDaoForDML");
		retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		retailProSalesDaoForDML = (RetailProSalesDaoForDML)context.getBean("retailProSalesDaoForDML");
		mastersToTransactionMappingsDao = (MastersToTransactionMappingsDao)context.getBean("mastersToTransactionMappingsDao");
		//digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)context.getBean("digitalReceiptUserSettingsDao");
		customTemplatesDao=(CustomTemplatesDao) context.getBean("customTemplatesDao");
		emailQueueDao = (EmailQueueDao) context.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML) context.getBean("emailQueueDaoForDML");
		eventTriggerEventsObservable = (EventTriggerEventsObservable)context.getBean("eventTriggerEventsObservable");
		eventTriggerEventsObserver = (EventTriggerEventsObserver)context.getBean("eventTriggerEventsObserver");
		eventTriggerDao = (EventTriggerDao)context.getBean("eventTriggerDao");
		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
		myTemplatesDao = (MyTemplatesDao) context.getBean("myTemplatesDao");
		
		//Fetch all users who have enabled data extraction flag
		//List<DigitalReceiptUserSettings> drUserSetttingsList = digitalReceiptUserSettingsDao.getAllUsersByDRExtractionFlag(true);
		
		//logger.debug("Total Digital Receipts enabled users: "+drUserSetttingsList.size());
		//if(drUserSetttingsList == null){return; }
		
		
		//String drUserIdStr = "";
		//for(DigitalReceiptUserSettings drUserSettings : drUserSetttingsList){
			
			//if(drUserIdStr.length() > 0 ){
				//drUserIdStr += ", "+drUserSettings.getUserId();
				//continue;
			//}
			//else
				//drUserIdStr += drUserSettings.getUserId();
			
		//}
		
		
		List<Users> usersList = usersDao.findByDigiReceptExtraction(true);
		
		if(usersList == null || usersList.size() == 0){
			logger.debug("Digital Receipts Users Not Found");
			return;
		}
		
		//user wise process digital receipt data 
		
		for(Users user : usersList){
			
			List<DigitalReceiptsJSON> drJsonList = null;
			Long userId = user.getUserId();
			
			 resetFields();
			
			 
			drJsonList= digitalReceiptsJSONDao.findDRJSONsByUserId(userId, Constants.DR_JSON_PROCESS_STATUS_NEW);
			
			if(drJsonList == null || drJsonList.size() == 0){
				//logger.debug("DR Json List is empty for the User : "+user.getUserName());
				continue;
			}
			
			logger.debug("Started Digital Receipts Data Extraction >>>>>>>"+user.getUserName());
			MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, userId);
			
			if(mailingList == null){
				logger.debug("In DR data extraction, POS list not found for the user: "+user.getUserName());
				continue;
			}
			
			if(user.isEnableSMS() && user.isConsiderSMSSettings()){
				 
				if(SMSStatusCodes.smsProgramlookupOverUserMap.get(user.getCountryType())) smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				else  smsSettings = smsSettingsDao.findByOrg(user.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			
				
				// smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				 try {
					ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				  
			 }
			
			List<EventTrigger> contactTypeeventTriggers = eventTriggerDao.findAllUserAutoRespondTriggers(userId.longValue(),
					Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED);
			
			isEnableContactEvent = (contactTypeeventTriggers != null && contactTypeeventTriggers.size() > 0);

			List<EventTrigger> salesTypeEventTriggers = eventTriggerDao.findAllUserAutoRespondTriggers(userId.longValue(), 
														Constants.ET_TYPE_ON_PRODUCT+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE
														+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE_AMOUNT);
			
			isEnableSalesEvent =  (salesTypeEventTriggers != null && salesTypeEventTriggers.size() > 0);
			
			
			//process json string
			
			for(DigitalReceiptsJSON digiReceiptsJSON : drJsonList) {
				
						
				try {
					String drJsonStr = digiReceiptsJSON.getJsonStr();
					
					if(drJsonStr == null){ continue;}
					
					JSONObject jsonObject = (JSONObject)JSONValue.parse(drJsonStr);
					
					if(jsonObject == null){ continue;}
							
					JSONObject jsonBody = (JSONObject)jsonObject.get("Body");
					
					if(jsonBody == null){ continue;}
					
					JSONObject receiptJsonObj = (JSONObject)jsonBody.get("Receipt");
					
					//For invalid receipt , do not process sku and sales
					if(receiptJsonObj == null || !(receiptJsonObj instanceof JSONObject)){
						
						logger.info("In DR data extraction, Receipt json object is null or is not an jsonobject");
						continue;
					}
					
					//prepare receipt map which can be used in sku and sales
					
					/*Set<Map.Entry<String, Object>> KeyValue = receiptJsonObj.entrySet();
					
					if(KeyValue == null){ continue;}
					
					 for(Entry<String, Object> ent : KeyValue){
						String key1 = ent.getKey();
						String val1 = ent.getValue().toString();
					 	drReceiptDataMap.put(key1, val1);
					 }*/
					
					
					JSONArray itemsJsonArray = (JSONArray)jsonBody.get("Items");
					
					if(itemsJsonArray == null || !(itemsJsonArray instanceof JSONArray)){
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						//digitalReceiptsJSONDao.saveOrUpdate(digiReceiptsJSON);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
						
						logger.info("In DR data extraction, Items json array is null or is not an jsonarray");
						continue;
					}
					
					//set null to contact, sku and sales objects 
					inputContact = null;
					dbskuFileList = null;
					retailProSalesList = null;
					contactProcessed = false;
					contactExistInOC = false;
					
					//extract contact data from json and process it 
					processContactData(user, mailingList,receiptJsonObj);
					//logger.debug(">>>>>>>>>>>..after contact processed"+contactProcessed);
						
					
					if(contactProcessed == true){
						if(isEnableContactEvent && isEnableEventForInputContact) {
							if(startId == null) {
								startId = inputContact.getContactId();
							}
							endId = inputContact.getContactId();
						}
						//extract sku data from json and process it
						processSKUData(user, mailingList, itemsJsonArray, receiptJsonObj);
						//getMastToTransMappings(mailingList, user.getUserId());

						//extract sales data from json and process it
						processSalesData(user, mailingList, itemsJsonArray, receiptJsonObj);
						//getMastToTransMappings(mailingList, user.getUserId());
					
						//update status of json as processed
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_PROCESSED);
						//digitalReceiptsJSONDao.saveOrUpdate(digiReceiptsJSON);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
					}
					else{
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						//digitalReceiptsJSONDao.saveOrUpdate(digiReceiptsJSON);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
					logger.error("Exception in DR json data extraction...");
				}
				
				
			}
			logger.debug("Completed Digital Receipts Data Extraction >>>>>>>"+user.getUserName());
			if(isEnableContactEvent && startId != null && endId	!= null) {
				
				eventTriggerEventsObservable.notifyToObserver(contactTypeeventTriggers, startId, endId, userId, Constants.POS_MAPPING_TYPE_CONTACTS);
			}
			if(isEnableSalesEvent && salesStartId != null && salesEndId != null) {
				
				eventTriggerEventsObservable.notifyToObserver(salesTypeEventTriggers, salesStartId, salesEndId, userId, Constants.POS_MAPPING_TYPE_SALES);
			}
			
			//needed to ensure for each user these will start from scratch.
			startId = null;
			endId = null;
			salesStartId = null;
			salesEndId = null;
			
		}
			
		
	}
	
	//process contact data
	private void processContactData(Users user, MailingList mailingList, JSONObject recptJsonObj){
		
		List<POSMapping> contactPOSMap = null;
		contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		isEnableEventForInputContact = false;
		
		if(contactPOSMap == null || contactPOSMap.size() == 0){
			logger.debug("POS Mapping type CONTACTS not exists for the user: "+user.getUserId());
			return;
		}
		
		inputContact = new Contacts();
		Contacts contactObj = null;
		inputContact.setUsers(user);
		
		//prepare and set data to inputcontact from receipt json object
		setContactFields(inputContact, contactPOSMap, recptJsonObj);
		
		
		//logger.info("inputContact phone : "+inputContact.getMobilePhone());
			
		if((inputContact.getExternalId() == null || inputContact.getExternalId().trim().isEmpty())
				&& (inputContact.getEmailId() == null || inputContact.getEmailId().trim().isEmpty()) 
				&& (inputContact.getMobilePhone() == null || inputContact.getMobilePhone().trim().isEmpty())){
			contactProcessed = false;
			return;
		}
		
		optinMobileSet = new HashSet<String>();
		//smsSettings = smsSettingsDao.findByUser(user.getUserId());
		
		//validate emailId and mobile
		
		TreeMap<String, List<String>> prioMap = null;
		
		prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		
		if(prioMap == null || prioMap.size() == 0){
			logger.info("Unique Priority Map NOT configured to the user: "+user.getUserName());
			contactProcessed = false;
			return;
		}
		
		//Contacts contactObj = null;
		if(prioMap != null || prioMap.size() > 0){
			contactObj = contactsDao.findContactByUniqPriority(prioMap, inputContact, user.getUserId(), user);
		}
		
		
		try {
			long contactBit = 0l;
			boolean purgeFlag = false;
			
			if(contactObj != null){

				contactBit = contactObj.getMlBits().longValue();
				
				if(contactBit != 0l){
					inputContact = contactObj;
					contactBit = (contactBit | mailingList.getMlBit().longValue());
					inputContact.setMlBits(contactBit);
					contactsDaoForDML.saveOrUpdate(inputContact);
					logger.info("contact already exist in OC... do not merge with pos data...");
					contactProcessed = true;
					return;
				}
				String emailStatus = contactObj.getEmailStatus();
				boolean emailFlag = contactObj.getPurged();
				
				if((contactObj.getEmailId() != null && inputContact.getEmailId() != null &&
						! contactObj.getEmailId().equalsIgnoreCase(inputContact.getEmailId())
						)||(contactObj.getEmailId() == null && inputContact.getEmailId() != null) ) {
					emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
					emailFlag = false;
					purgeFlag = true;
				}
				
				if( contactBit == 0l ) {
					
					isEnableEventForInputContact = true;
					contactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
					emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
					emailFlag = false;
					purgeFlag = true;
					
				}
				
											
				//perform mobile status
				boolean isNeedToPerformMO = inputContact.getMobilePhone()!= null && user.isConsiderSMSSettings();
				if(isNeedToPerformMO) {
				
					performMobileOptIn(inputContact, false, contactObj);
				}
				//*****************************************************************
				//logger.info("before contact exist: mobile :"+inputContact.getMobilePhone());
				inputContact = Utility.mergeContacts(inputContact, contactObj);
				//logger.info("after contact exist: mobile :"+inputContact.getMobilePhone());
				if(inputContact.getMobilePhone() == null) {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}else if(!isNeedToPerformMO){
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				}
				inputContact.setEmailStatus(emailStatus);
				inputContact.setPurged(emailFlag);
				contactBit = (contactBit | mailingList.getMlBit().longValue());
			}
			else{
				
				contactBit = mailingList.getMlBit().longValue();
				purgeFlag = true;
				inputContact.setCreatedDate(Calendar.getInstance());
				inputContact.setPurged(false);
				inputContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				inputContact.setUsers(user);
				inputContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				isEnableEventForInputContact = true;
				//perform mobile status
				if(inputContact.getMobilePhone()!= null) {
					performMobileOptIn(inputContact, true, null);
					//contactObj.setMobilePhone(contactObj.getMobilePhone());
					
				}else {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			}
			
			

			mailingList.setLastModifiedDate(Calendar.getInstance());
			
			inputContact.setMlBits(contactBit);
			
			if(purgeFlag) {
				purgeList = (PurgeList)context.getBean("purgeList");
				purgeList.checkForValidDomainByEmailId(inputContact);
				
			}
			
			mailingList.setListSize(mailingList.getListSize() + 1);
			//logger.info("contact exist: mobile :"+inputContact.getMobilePhone());
			contactsDaoForDML.saveOrUpdate(inputContact);
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(inputContact, inputContact.getMobilePhone());
			contactProcessed = true;
			mailingListDaoForDML.saveOrUpdate(mailingList);
			
			if(mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
				
				sendWelcomeEmail(contactObj, mailingList, mailingList.getUsers());
			
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			logger.error("Exception while saving contact object in DR data extraction...");
			contactProcessed = false;
		}
		
		
	}
	
	
	public void setContactFields(Contacts inputContact, List<POSMapping> contactPOSMap, JSONObject receiptJsonObj){
		
		for(POSMapping posMapping : contactPOSMap){
			
			
		if(posMapping.getDigitalReceiptAttribute() == null || posMapping.getDigitalReceiptAttribute().trim().length() <= 0){
			continue;
		}
		
		String[] drAttribute = posMapping.getDigitalReceiptAttribute().split(Constants.DELIMETER_DOUBLECOLON);
		
		String custFieldAttribute = posMapping.getCustomFieldName();
		
		//For user defined field name, Json does not contain field value
		if(drAttribute.length < 2) continue;
		
		String fieldValue = (String)receiptJsonObj.get(drAttribute[1]);
		//String fieldValue = drReceiptDataMap.get(drAttribute[1]);
		
		if(fieldValue == null || fieldValue.trim().length() <= 0){
			logger.info("DR field value is empty ...for field : "+drAttribute[1]);
			continue;
		}
		
		fieldValue = fieldValue.trim();
		//logger.info("posMapping att: "+drAttribute[1]+"  custname: "+custFieldAttribute+" value: "+fieldValue);
		
		String dateTypeStr = null;
		dateTypeStr = posMapping.getDrDataType();
		if(dateTypeStr == null || dateTypeStr.trim().length() <=0){
			continue;
		}
		
		if(custFieldAttribute.startsWith("UDF") && dateTypeStr.startsWith("Date")){
			
			String dateValue = getDateFormattedData(posMapping, fieldValue);
			
			if(dateValue == null) continue;
			
			fieldValue = dateValue;
		}
		/*else if(custFieldAttribute.startsWith("UDF") && !dateTypeStr.startsWith("Date")){
			try{
			String dateFormat = dateTypeStr;
			DateFormat formatter ; 
			Date date ; 
			formatter = new SimpleDateFormat(dateFormat);
			date = (Date)formatter.parse(fieldValue); 
			Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
			cal.setTime(date);
			fieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			}catch(Exception e){
				logger.error("Exception ::" , e);
			}
			
		}*/
		
		
		Object[] params = null;
		Method method = null;
		/*try {             
			   Method method = contact.getClass().getMethod("set" + OCAttribute, new Class[] { 
					   drReceiptDataMap.get(drAttribute[1]).getClass() });
			   
			   method.invoke(contact, drReceiptDataMap.get(drAttribute[1]));
			 } catch (Exception ex) {
			        logger.error("Exception ::",ex);
			 }	 
		*/
		try {
			
			if (custFieldAttribute.equals(POSFieldsEnum.emailId.getOcAttr()) && fieldValue.length() > 0 &&
					Utility.validateEmail(fieldValue)) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.emailId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.firstName.getOcAttr()) && fieldValue.length() > 0) {
				
				method = Contacts.class.getMethod("set" + POSFieldsEnum.firstName.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.lastName.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.lastName.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.addressOne.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.addressOne.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.addressTwo.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.addressTwo.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.city.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.city.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.state.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.state.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.country.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.country.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.zip.getOcAttr()) && fieldValue.length() > 0) {

				method = Contacts.class.getMethod("set" + POSFieldsEnum.zip.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.mobilePhone.getOcAttr()) && fieldValue.length() > 0) {
				Users users = inputContact.getUsers();
				UserOrganization organization=  users!=null ? users.getUserOrganization() : null ;
				String phoneParse = Utility.phoneParse(fieldValue,organization);
				if(phoneParse != null){
				//logger.info("after phone parse: "+phoneParse);
					method = Contacts.class.getMethod("set" + POSFieldsEnum.mobilePhone.getPojoField(), strArg);
					params = new Object[] { phoneParse };
				}
			}

			else if (custFieldAttribute.equals(POSFieldsEnum.externalId.getOcAttr()) && fieldValue.length() > 0) {
				
				method = Contacts.class.getMethod("set" + POSFieldsEnum.externalId.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			
			else if (custFieldAttribute.equals(POSFieldsEnum.gender.getOcAttr()) && fieldValue.length() > 0) {
				
				method = Contacts.class.getMethod("set" + POSFieldsEnum.gender.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			
			else if (custFieldAttribute.equals(POSFieldsEnum.birthDay.getOcAttr()) && fieldValue.length() > 0) {
			
				method = Contacts.class.getMethod("set" + POSFieldsEnum.birthDay.getPojoField(), calArg);
				try {
					String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
					DateFormat formatter ; 
					Date date ; 
					formatter = new SimpleDateFormat(dateformat);
					date = (Date)formatter.parse(fieldValue); 
					Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
					dobCal.setTime(date);
					params = new Object[] { dobCal };
					//contact.setBirthDay(dobCal);
				} catch (Exception e) {
					logger.info("BirthDay date format not matched with data",e);
				}
				
			}
			
			else if (custFieldAttribute.equals(POSFieldsEnum.anniversary.getOcAttr()) && fieldValue.length() > 0) {
				
				method = Contacts.class.getMethod("set" + POSFieldsEnum.anniversary.getPojoField(), calArg);
				try {
					String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
					DateFormat formatter ; 
					Date date ; 
					formatter = new SimpleDateFormat(dateformat);
					date = (Date)formatter.parse(fieldValue); 
					Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
					dobCal.setTime(date);
					params = new Object[] { dobCal };
					//contact.setBirthDay(dobCal);
				} catch (Exception e) {
					logger.info("Anniversary date format not matched with data",e);
				}
				
			}
			
			else if (custFieldAttribute.equals(POSFieldsEnum.homeStore.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.homeStore.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			
			
			else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
				params = new Object[] { fieldValue };
			}
			
			
		if (method != null) {

			try {
				method.invoke(inputContact, params);
				//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} /*catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}*/
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//logger.info("securityexception");
			logger.error("Exception ::" , e);
		} 
		
		
	}
		//logger.info("set contact data input contact: mobile: "+inputContact.getMobilePhone());
	
	}
	
	
	private String getDateFormattedData(POSMapping posmapping, String fieldValue){
		
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
				Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
				cal.setTime(date);
				dateFieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
		}
		
		return dateFieldValue;
	}
	
	
	//process SKU data
	private void processSKUData(Users user, MailingList mailingList, JSONArray itemsJsonArray, JSONObject receptJsonObj){
		
		TreeMap<String, List<String>> prioMap =null;
			prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_SKU, posMappingDao);
		
		//logger.info("SKU prioMap :: "+prioMap);	
			
		List<POSMapping> SKUPOSMappingList = null;
			SKUPOSMappingList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", user.getUserId());
		
		//logger.info("SKUMapping List size:: "+SKUPOSMappingList.size());	
			
		if(SKUPOSMappingList == null || SKUPOSMappingList.size() == 0) {
			logger.debug("Not found POS Mapping type SKU for the userName"+user.getUserName());
			return;
		}
		
		
		SkuFile skuFileObj = null;
		
		List<SkuFile> skuFileList = null;
		dbskuFileList = new ArrayList<SkuFile>();
		skuFileList = prepareSkuObjectData(SKUPOSMappingList, itemsJsonArray, receptJsonObj);
		
		for(SkuFile skuFile : skuFileList){
		
			if(!prioMap.isEmpty()) {
				skuFileObj = skuFileDao.findSKUByPriority(prioMap, skuFile, user.getUserId());
			}
			
			
			if(skuFileObj != null) {
				
				skuFileObj = Utility.mergeSkuFile(skuFile, skuFileObj);
				skuFileObj.setUserId(user.getUserId());
				
				//skuFileDao.saveOrUpdate(skuFileObj);
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
				//logger.info("existing sku object sku id : "+skuFileObj.getSkuId());
				
			}
			else {
				skuFileObj = new SkuFile(); 
				skuFileObj = Utility.mergeSkuFile(skuFile, skuFileObj);
				skuFileObj.setUserId(user.getUserId());
				
				//skuFileDao.saveOrUpdate(skuFileObj);
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
				//logger.info("existing sku object sku id : "+skuFileObj.getSkuId());
				
			}
		
			dbskuFileList.add(skuFileObj);
		}	
		
	}
	
	public List<SkuFile> prepareSkuObjectData(List<POSMapping> skuPOSMap, JSONArray itemsJsonArr, JSONObject receptJsonObj){
		
		try {
			Iterator<Object> objIter = itemsJsonArr.iterator();
			
			List<SkuFile> inputSkuFileList = new ArrayList<SkuFile>();
			
			SkuFile inputSkuFile = null;
			
			Class dataTypeArg[] = null;
			
			while(objIter.hasNext()){
				//Object obj = objIter.next();
				JSONObject itemjsnObj = (JSONObject)objIter.next();
				inputSkuFile = new SkuFile();
				String fieldValue = null;
				
				for(POSMapping posMapping : skuPOSMap){
					
					if(posMapping.getDigitalReceiptAttribute() == null){
						continue;
					}
					
					String[] drAttribute = posMapping.getDigitalReceiptAttribute().split(Constants.DELIMETER_DOUBLECOLON);
					
					String custFieldAttribute = posMapping.getCustomFieldName();
					
					//For user defined field name, Json does not contain field value
					if(drAttribute.length < 2) continue;
					
					if(drAttribute[0].equals("Receipt")){
						fieldValue = (String)receptJsonObj.get(drAttribute[1]);
						//logger.info("receipt data:");
					}
					else{
						fieldValue = (String)itemjsnObj.get(drAttribute[1]);
					}
					
					if(fieldValue == null || fieldValue.trim().length() <= 0){
						logger.info("field value is null, In DR data processing>>>>>..");
						continue;
					}
					
					//logger.info("posMapping att: "+drAttribute[1]+"  custname: "+custFieldAttribute+" value: "+fieldValue);
					
					String dataTypeStr = posMapping.getDrDataType();
					
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Date")){
						
						String dateValue = getDateFormattedData(posMapping, fieldValue);
						
						if(dateValue == null) continue;
						
						fieldValue = dateValue;
					}
					/*else if(custFieldAttribute.startsWith("UDF") && !dataTypeStr.startsWith("Date")){
						try{
						String dateFormat = dataTypeStr;
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateFormat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
						cal.setTime(date);
						fieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
						}catch(Exception e){
							logger.error("Exception ::" , e);
						}
						
					}*/
					
					//String dateFormat = null;
					
					Object[] params = null;
					Method method = null;
					
					/*if(dataTypeStr.trim().equals("String")){
						dataTypeArg = new Class[] { String.class };
					}
					else if(dataTypeStr.trim().equals("Date")){
						dataTypeArg	= new Class[] { Calendar.class };
						dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
					}
					else if(dataTypeStr.trim().equals("Number")){
						dataTypeArg	= new Class[] { Integer.class };
					}
					else if(dataTypeStr.trim().equals("Double")){
						dataTypeArg	= new Class[] { Double.class };
						
					}*/
					
					//logger.info("datatype arg: "+dataTypeStr+" method arg class:  "+dataTypeArg);
					try {
					if (custFieldAttribute.equals(POSFieldsEnum.listPrice.getOcAttr()) && fieldValue.length() > 0 ) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.listPrice.getPojoField(), doubleArg);
						Double listPriceVal = new Double(fieldValue);
						params = new Object[] { listPriceVal };
						//logger.info("parseDouble value: "+Double.parseDouble(fieldValue.trim()));
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.description.getOcAttr()) && fieldValue.length() > 0) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.description.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.sku.getOcAttr()) && fieldValue.length() > 0) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.sku.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.itemCategory.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemCategory.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.itemSid.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemSid.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.storeNumber.getOcAttr()) && fieldValue.length() > 0) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.storeNumber.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					
					else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					
					if (method != null) {

						try {
							method.invoke(inputSkuFile, params);
							//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						} /*catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						}*/
					}
					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					} /*catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					}*/
					
			}
			
			//add inputskuFile object to list
			
			inputSkuFileList.add(inputSkuFile);
				
				
			}
			
			
			return inputSkuFileList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			logger.error("Exception in SKU object preparation (DR data extraction)");
			return null;
		}
		
	}
	
	
	//process Sales data
	private void processSalesData(Users user, MailingList mailingList, JSONArray itemsJsonArray, JSONObject receptJsonObj){
		
		try {
			//logger.debug(">>>>>>>>>>..process sales");
			TreeMap<String, List<String>> salesPrioMap =null;
			salesPrioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_SALES, posMappingDao);

			//logger.info("Sales prioMap :: "+salesPrioMap);	
			
			List<POSMapping> salesPOSMappingList = null;
				salesPOSMappingList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SALES+"'", user.getUserId());

			//logger.info("Sales Mapping List size:: "+salesPOSMappingList.size());	
			
			if(salesPOSMappingList == null || salesPOSMappingList.size() == 0) {
				logger.debug("Not found POS Mapping type SALES for the userName"+user.getUserName());
				return;
			}

			String uniqueAcrossCustField = null;
			String uniqueAcrossCustValue = null;
			//String uniqueAcross
			
			for(POSMapping posMapping : salesPOSMappingList){
				if(posMapping.getUniqueInAcrossFiles() != null){
					uniqueAcrossCustField=posMapping.getCustomFieldName();
					break;
				}
			}
			
			
			RetailProSalesCSV retailProSalesCSV = null;

			//List<RetailProSalesCSV> retailProSalesList = null;

			retailProSalesList = prepareSalesObjectData(salesPOSMappingList, itemsJsonArray, receptJsonObj);
			
			if(retailProSalesList == null || retailProSalesList.size() <= 0){ 
				logger.info("sales object list is empty in DR data extraction ");
				return ;
			}
			
			uniqueAcrossCustValue = getSalesFieldValue(retailProSalesList.get(0), uniqueAcrossCustField);
			
			
			boolean existFlag = false;
			if(uniqueAcrossCustValue != null ) {
				existFlag  = salesLiteralHashCodeGenerateDao.isCodeExist(user.getUserId(),  uniqueAcrossCustValue);
			}
			
			if(existFlag == false) {
			
				//logger.info("sales list size : "+retailProSalesList.size());
				
				for(RetailProSalesCSV salerecord : retailProSalesList){
					
					for(SkuFile skufileObj : dbskuFileList){
						
						if(skufileObj.getItemSid().equals(salerecord.getItemSid()) && 
								skufileObj.getStoreNumber().equals(salerecord.getStoreNumber())){
							salerecord.setCid(inputContact.getContactId());
							salerecord.setInventoryId(skufileObj.getSkuId());
							//logger.info("skufileObj sku id: "+skufileObj.getSkuId());
							break;
						}
					}
					
					
					salerecord.setUserId(user.getUserId());
					if(isEnableSalesEvent) {
						//retailProSalesDao.saveOrUpdate(salerecord);
						retailProSalesDaoForDML.saveOrUpdate(salerecord);
						if(salesStartId == null) {
							salesStartId = salerecord.getSalesId().longValue();
						}
						salesEndId = salerecord.getSalesId().longValue();
					}//if
				}
				//retailProSalesDao.saveByCollection(retailProSalesList);
				
				SalesLiteralHashCode saleLiteralObj = new SalesLiteralHashCode();
				saleLiteralObj.setListId(mailingList.getListId());
				saleLiteralObj.setUserId(user.getUserId());
				saleLiteralObj.setSalesLiteralHashCode(uniqueAcrossCustValue);
				saleLiteralObj.setCurrentFile(false);
				saleLiteralObj.setCreatedDate(Calendar.getInstance());
			
				//salesLiteralHashCodeGenerateDao.saveOrUpdate(saleLiteralObj);
				salesLiteralHashCodeGenerateDaoForDML.saveOrUpdate(saleLiteralObj);
				
			}
			
			logger.info("updating sales aggregate info....");
			//retailProSalesDao.updateSalesAggregateData(mailingList.getListId().longValue(), user.getUserId());
			retailProSalesDaoForDML.updateSalesAggregateData(mailingList.getListId().longValue(), user.getUserId());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			logger.error("Exception in saving sales data in DR data extraction");
			return;
		}
		
		
	}	
	
	
	public List<RetailProSalesCSV> prepareSalesObjectData(List<POSMapping> salesPOSMap, JSONArray itemsJsonArr, JSONObject receptJsonObj){
		
		try {
			Iterator<Object> objIter = itemsJsonArr.iterator();
			
			List<RetailProSalesCSV> inputSalesList = new ArrayList<RetailProSalesCSV>();
			
			RetailProSalesCSV inputSales = null;
			
			Class dataTypeArg[] = null;
			
			while(objIter.hasNext()){
				//Object obj = objIter.next();
				JSONObject itemjsnObj = (JSONObject)objIter.next();
				inputSales = new RetailProSalesCSV();
				String fieldValue = null;
				
				for(POSMapping posMapping : salesPOSMap){
					
					if(posMapping.getDigitalReceiptAttribute() == null){
						continue;
					}
					
					String[] drAttribute = posMapping.getDigitalReceiptAttribute().split(Constants.DELIMETER_DOUBLECOLON);
					
					String custFieldAttribute = posMapping.getCustomFieldName();
					
					if(drAttribute.length < 2) continue;
					
					if(drAttribute[0].equals("Receipt")){
						fieldValue = (String)receptJsonObj.get(drAttribute[1]);
						//logger.info("receipt data:");
					}
					else{
						fieldValue = (String)itemjsnObj.get(drAttribute[1]);
					}
					
					if(fieldValue == null || fieldValue.trim().length() <= 0){
						logger.info("field value is null in DR sales >>>>>..");
						continue;
					}
					
					//logger.info("posMapping att: "+drAttribute[1]+"  custname: "+custFieldAttribute+" value: "+fieldValue);
					
					String dataTypeStr = posMapping.getDrDataType();
					
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Date")){
						
						String dateValue = getDateFormattedData(posMapping, fieldValue);
						
						if(dateValue == null) continue;
						
						fieldValue = dateValue;
					}
					/*else if(custFieldAttribute.startsWith("UDF") && !dataTypeStr.startsWith("Date")){
						try{
						String dateFormat = dataTypeStr;
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateFormat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
						cal.setTime(date);
						fieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
						}catch(Exception e){
							logger.error("Exception ::" , e);
						}
						
					}*/
					//String dateFormat = null;
					
					Object[] params = null;
					Method method = null;
					
					
					//logger.info("datatype arg: "+dataTypeStr+" method arg class:  "+dataTypeArg);
					try {
					if (custFieldAttribute.equals(POSFieldsEnum.docSid.getOcAttr()) && fieldValue.length() > 0 ) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.docSid.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.quantity.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.quantity.getPojoField(), doubleArg);
						Double quantityVal = new Double(fieldValue);
						params = new Object[] { quantityVal };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.salesDate.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.salesDate.getPojoField(), calArg);
						
						fieldValue += " "+ receptJsonObj.get("DocTime");
						
						try {
							//String dateformat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
							
							String dateformat = "MM/dd/yyyy HH:mm:ss";
							DateFormat formatter ; 
							Date date ; 
							formatter = new SimpleDateFormat(dateformat);
							date = (Date)formatter.parse(fieldValue.trim()); 
							Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
							dobCal.setTime(date);
							params = new Object[] { dobCal };
							//contact.setBirthDay(dobCal);
						} catch (Exception e) {
							logger.info("salesdate format not matched with data",e);
						}
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.salesPrice.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.salesPrice.getPojoField(), doubleArg);
						Double salesPriceVal = new Double(fieldValue);
						params = new Object[] { salesPriceVal };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.tenderType.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.tenderType.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.sku.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.sku.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.customerId.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.customerId.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.promoCode.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.promoCode.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.tax.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.tax.getPojoField(), doubleArg);
						Double taxVal = new Double(fieldValue);
						params = new Object[] { taxVal };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.recieptNumber.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.recieptNumber.getPojoField(), longArg);
						Long receiptNum = new Long(fieldValue);
						params = new Object[] { receiptNum };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.itemSid.getOcAttr()) && fieldValue.length() > 0) {

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.itemSid.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.storeNumber.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.storeNumber.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					
					else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					
					if (method != null) {

						try {
							method.invoke(inputSales, params);
							//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						} /*catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::" , e);
						}*/
					}
					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					} /*catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					}*/
					
			}
			
			//add inputskuFile object to list
			
				inputSalesList.add(inputSales);
				
				//logger.info("sales object is added to the list>>>>>>>>>>>>");
				
				
			}
			
			
			return inputSalesList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			logger.error("Exception in preparing sales object data, DR data extraction");
			return null;
		}
		
	}
	
	
	
	public void performMobileOptIn(Contacts inputContactObj, boolean isNew, Contacts existingContact) throws BaseServiceException {

		//SMSSettings smsSettings = smsSettingsDao.findByUser(inputContactObj.getUsers().getUserId());
		
		if(smsSettings == null || ocsmsGateway == null) {
		
		
		String noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
									"SMS may not be sent to the mobile contacts.";
		
		
		Messages messages = new Messages("Contact" ,"Mobile contacts may not reachable" ,noSMSComplaincyMsg ,
				Calendar.getInstance(),"Inbox",false ,"Info", inputContactObj.getUsers()); 
		
		MessagesDao messagesDao = (MessagesDao)context.getBean("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)context.getBean("messagesDaoForDML");
		//messagesDao.saveOrUpdate(messages);
		messagesDaoForDML.saveOrUpdate(messages);
			
		inputContactObj.setMobileOptin(false);
		inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
			
		if(existingContact != null) {
			
			existingContact.setMobileOptin(false);
			existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
			
		}
		
		return;
		
		
		
	}
	
	String optinMedium = null;
	
	if(inputContactObj.getMobilePhone() != null && !inputContactObj.getMobilePhone().isEmpty()) {
		
		//if
		boolean isDifferentMobile = false;
		String mobile = inputContactObj.getMobilePhone();
		
		if(existingContact != null) {
			
			String conMobile = existingContact.getMobilePhone();
			optinMedium = existingContact.getOptinMedium();
		//to identify whether entered one is same as previous mobile
			if(conMobile != null ) {
				
				if(!mobile.equals(conMobile)) {
					
					if( (mobile.length() < conMobile.length() && !conMobile.endsWith(mobile) ) ||
							(conMobile.length() < mobile.length() && !mobile.endsWith(conMobile)) || mobile.length() == conMobile.length()) {
						
						isDifferentMobile = true;
						
					}//if
					
					
				}//if
				
			}//if
		
			else{
				existingContact.setMobilePhone(inputContactObj.getMobilePhone());
				isDifferentMobile = true;
				
			}
			
		}//if
		else{
			
			optinMedium = inputContactObj.getOptinMedium();
			
			
		}
		//contact.setPhone(mPhoneIBoxId.getValue());
		
		//Users currentUser = inputContactObj.getUsers();
		Users currentUser = smsSettings.getUserId();
		
		boolean canProceed = false;
		//do only when the existing phone number is not same with the entered
		byte optin = 0;
		if(optinMedium != null) {
			
			if(optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
				optin = 1;
			}
			else if(optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
				optin = 2;
			}
			else if(optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
				optin = 4;
			}
			
		}//if
		Users contactOwner = inputContactObj.getUsers();
		Byte userOptinVal =	smsSettings.getOptInMedium();
		
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
				contactOwner.getOptInMedium() : userOptinVal;
				
		if(smsSettings.isEnable() && 
				userOptinVal != null && 
				(userOptinVal.byteValue() & optin ) > 0 ) {						
			
			if( (existingContact != null && 
					(existingContact.getLastSMSDate() == null && existingContact.isMobileOptin() != true) ||
					(existingContact != null && isDifferentMobile) )  ) {
				
					existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
					existingContact.setLastSMSDate(Calendar.getInstance());
					existingContact.setMobileOptin(false);
					canProceed = true;
					
				
				
			}
			if(canProceed || isNew) {	
				
				//logger.info("is a new contact=====");
				
					
					inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
					inputContactObj.setMobileOptin(false);
				
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)context.getBean("captiwayToSMSApiGateway");
				
				if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
					
					logger.debug("low credits with clickatell");
					return;
				}
				
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					
					//UsersDao usersDao = (UsersDao)context.getBean("usersDao");
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML)context.getBean("usersDaoForDML");
					
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						
						msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader()  + " "+ msgContent;
					}
					
					String mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway,
							smsSettings.getSenderId(), mobile, msgContent, smsSettings.getUserId());
	
					if(mobileStatus== null) {
						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					}
					if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
						
						contactsDaoForDML.updatemobileStatus(mobile, mobileStatus, currentUser);
						
					}
					
					
					if(canProceed) {
						
						existingContact.setMobileStatus(mobileStatus);
					}
					if(isNew) {
						
						inputContactObj.setMobileStatus(mobileStatus);
					}
									
					//clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobile, msgContent);
					/*if(currentUser.getParentUser() != null) {
						currentUser = currentUser.getParentUser();
					}*/
					/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() )+1);
					usersDao.saveOrUpdate(currentUser);*/
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
					
					/**
					 * Update Sms Queue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					smsQueueHelper.updateSMSQueue(mobile,msgContent,Constants.SMS_MSG_TYPE_OPTIN,  currentUser, smsSettings.getSenderId());
					
				}else {
					logger.debug("low credits with user...");
					
					return;
					
				}
				
			}//if
		}//if
		else {
			
			if(existingContact != null) {
				
				if(existingContact.getMobilePhone() != null && isDifferentMobile){
					
					existingContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					existingContact.setMobileOptin(false);
					
				}
				
			}//if existing contact
			else {
				
				if(inputContactObj.getMobilePhone() != null ){
					
					inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					inputContactObj.setMobileOptin(false);
					
				}
				
				
				
			}//if is new contact
			
		}//else
		
	}
	
	
}//performMobileOptIn
	
 public void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {

		
		//to send the loyalty related email
		 CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(mailingList.getWelcomeCustTempId() != null) {
			  
			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			   if(custTemplate != null) {
				   if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {  
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
				  }
			  }
		  }
		  //logger.debug("-----------email----------"+tempContact.getEmailId());
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active", null, contact.getUsers(),Calendar.getInstance(),
					"Welcome Mail", contact.getEmailId(), contact.getFirstName(), MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY), contact.getContactId());
			
			//testEmailQueue.setChildEmail(childEmail);
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		
		
	}//sendWelcomeEmail
	
 	public String getSalesFieldValue(RetailProSalesCSV salesObj, String salesFieldStr) {

		if (salesFieldStr.equals(POSFieldsEnum.docSid.getOcAttr())) {return "" + salesObj.getDocSid();}
		else if (salesFieldStr.equals(POSFieldsEnum.quantity.getOcAttr())) {return ""+salesObj.getQuantity();}
		else if (salesFieldStr.equals(POSFieldsEnum.salesDate.getOcAttr())) {return ""+salesObj.getSalesDate();} 
		else if (salesFieldStr.equals(POSFieldsEnum.salesPrice.getOcAttr())) {return ""+salesObj.getSalesPrice();}
		else if (salesFieldStr.equals(POSFieldsEnum.tenderType.getOcAttr())) {return salesObj.getTenderType();}
		else if (salesFieldStr.equals(POSFieldsEnum.sku.getOcAttr())) {return salesObj.getSku();}
		else if (salesFieldStr.equals(POSFieldsEnum.externalId.getOcAttr())) {return salesObj.getCustomerId();}
		else if (salesFieldStr.equals(POSFieldsEnum.promoCode.getOcAttr())) {return salesObj.getPromoCode();}
		else if (salesFieldStr.equals(POSFieldsEnum.tax.getOcAttr())) {return ""+salesObj.getTax();}
		else if (salesFieldStr.equals(POSFieldsEnum.recieptNumber.getOcAttr())) {return ""+salesObj.getRecieptNumber();}
		else if (salesFieldStr.equals(POSFieldsEnum.itemSid.getOcAttr())) {return salesObj.getItemSid();}
		else if (salesFieldStr.equals(POSFieldsEnum.storeNumber.getOcAttr())) {return salesObj.getStoreNumber();}
		else if (salesFieldStr.equals(POSFieldsEnum.discount.getOcAttr())) {return ""+salesObj.getDiscount();}
		else return null;

	}
	
	private  void getMastToTransMappings( MailingList mailingList,Long userId ){

		//MastersToTransactionMappingsDao mastersToTransactionMappingsDao =(MastersToTransactionMappingsDao)context.getBean("mastersToTransactionMappingsDao");
		List<MastersToTransactionMappings> mastList = mastersToTransactionMappingsDao.findByUserId(userId);
		if(mastList == null || mastList.size() == 0){
			return;
		}
		//logger.info(">>>>>>>>>>>>>>>>>> mast To trans List is >>>>>>>>>>>>>>>>>>>>"+mastList.size());
		Set<MastersToTransactionMappings> mastToTransSet=new HashSet<MastersToTransactionMappings>();
		for (MastersToTransactionMappings eachObj : mastList) {
			mastToTransSet.add(eachObj);
		}
			
		//retailProSalesDao.updateSalesToContactsMappings(mastToTransSet, mailingList.getListId(), userId);
		retailProSalesDaoForDML.updateSalesToContactsMappings(mastToTransSet, mailingList.getListId(), userId);
		
	}//getMastToTransMappings
	
}
