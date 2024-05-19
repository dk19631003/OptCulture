package org.mq.captiway.scheduler.services;

import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.captiway.scheduler.POSFieldsEnum;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.DigitalReceiptsJSON;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.MastersToTransactionMappings;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.MyTemplates;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.POSMapping;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.SalesLiteralHashCode;
import org.mq.captiway.scheduler.beans.SkuFile;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CustomTemplatesDao;
import org.mq.captiway.scheduler.dao.DigitalReceiptsJSONDao;
import org.mq.captiway.scheduler.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MailingListDaoForDML;
import org.mq.captiway.scheduler.dao.MastersToTransactionMappingsDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.MyTemplatesDao;
import org.mq.captiway.scheduler.dao.POSMappingDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SalesLiteralHashCodeGenerateDao;
import org.mq.captiway.scheduler.dao.SalesLiteralHashCodeGenerateDaoForDML;
import org.mq.captiway.scheduler.dao.SkuFileDao;
import org.mq.captiway.scheduler.dao.SkuFileDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRHead;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;
import org.mq.optculture.utils.HeartlandRequestTranslator;
import org.mq.optculture.utils.MagentoRequestTranslator;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OrionRequestTranslator;
import org.mq.optculture.utils.PrismRequestTranslator;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.ShopifyRequestTranslator;
import org.mq.optculture.utils.WooCommerceRequestTranslator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.gson.Gson;

import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;//1178

public class DRExtractionRecordThread implements Runnable, ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	
	private  Class strArg[] = new Class[] { String.class };
	private  Class calArg[] = new Class[] { Calendar.class };
	private  Class doubleArg[] = new Class[] { Double.class };
	private  Class longArg[] = new Class[] { Long.class };
	
	
	private ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
		
	}
	DigitalReceiptsJSON digiReceiptsJSON ;
	MailingList mailingList ;
	Users user;
	List<EventTrigger> contactTypeeventTriggers = null;
	List<EventTrigger> salesTypeEventTriggers = null;
	public DRExtractionRecordThread(){}
	
	
	public DRExtractionRecordThread(DigitalReceiptsJSON digiReceiptsJSON, Users user, 
			MailingList mailingList, boolean isEnableContactEvent,boolean isEnableSalesEvent, 
			List<EventTrigger> contactTypeeventTriggers,List<EventTrigger> salesTypeEventTriggers, SMSSettings smsSettings ,
	OCSMSGateway ocsmsGateway ){
		this.digiReceiptsJSON = digiReceiptsJSON; 
		this.user = user;
		this.mailingList = mailingList;
		this.isEnableContactEvent = isEnableContactEvent;
		this.isEnableSalesEvent = isEnableSalesEvent;
		this.contactTypeeventTriggers = contactTypeeventTriggers;
		this.salesTypeEventTriggers = salesTypeEventTriggers;
		this.smsSettings = smsSettings;
		this.ocsmsGateway  = ocsmsGateway;
		
	}
	
	UsersDao usersDao;
	UsersDaoForDML usersDaoForDML;
	MailingListDao mailingListDao;
	MailingListDaoForDML  mailingListDaoForDML = null;
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
	CouponCodesDaoForDML couponCodesDaoForDML;//1178
	//DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	MastersToTransactionMappingsDao mastersToTransactionMappingsDao;
	private CustomTemplatesDao customTemplatesDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	
	private Set<String> optinMobileSet;
	private SMSSettings smsSettings ;
	private OCSMSGateway ocsmsGateway;
	
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
	private MyTemplatesDao myTemplatesDao;
	
	@Override
	public void run() {

		try {
			
			// TODO Auto-generated method stub
			ServiceLocator context = ServiceLocator.getInstance();
			usersDao = (UsersDao)context.getDAOByName("usersDao");
			usersDaoForDML = (UsersDaoForDML)context.getDAOForDMLByName("usersDaoForDML");
			mailingListDao = (MailingListDao)context.getDAOByName("mailingListDao");
			mailingListDaoForDML = (MailingListDaoForDML)context.getDAOForDMLByName("mailingListDaoForDML");
			digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getDAOByName("digitalReceiptsJSONDao");
			digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)context.getDAOForDMLByName("digitalReceiptsJSONDaoForDML");
			posMappingDao = (POSMappingDao)context.getDAOByName("posMappingDao");
			contactsDao = (ContactsDao)context.getDAOByName("contactsDao");
			contactsDaoForDML = (ContactsDaoForDML)context.getDAOForDMLByName("contactsDaoForDML");
			smsSettingsDao = (SMSSettingsDao)context.getDAOByName("smsSettingsDao");
			skuFileDao = (SkuFileDao)context.getDAOByName("skuFileDao");
			skuFileDaoForDML = (SkuFileDaoForDML)context.getDAOForDMLByName("skuFileDaoForDML");
			salesLiteralHashCodeGenerateDao = (SalesLiteralHashCodeGenerateDao)context.getDAOByName("salesLiteralHashCodeGenerateDao");
			salesLiteralHashCodeGenerateDaoForDML = (SalesLiteralHashCodeGenerateDaoForDML)context.getDAOForDMLByName("salesLiteralHashCodeGenerateDaoForDML");
			retailProSalesDao = (RetailProSalesDao)context.getDAOByName("retailProSalesDao");
			retailProSalesDaoForDML = (RetailProSalesDaoForDML)context.getDAOForDMLByName("retailProSalesDaoForDML");
			mastersToTransactionMappingsDao = (MastersToTransactionMappingsDao)context.getDAOByName("mastersToTransactionMappingsDao");
			//digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)context.getDAOForDMLByName("digitalReceiptUserSettingsDao");
			customTemplatesDao=(CustomTemplatesDao) context.getDAOByName("customTemplatesDao");
			emailQueueDao = (EmailQueueDao) context.getDAOByName("emailQueueDao");
			emailQueueDaoForDML = (EmailQueueDaoForDML) context.getDAOForDMLByName("emailQueueDaoForDML");
			eventTriggerEventsObservable = (EventTriggerEventsObservable)context.getBeanByName("eventTriggerEventsObservable");
			eventTriggerEventsObserver = (EventTriggerEventsObserver)context.getBeanByName("eventTriggerEventsObserver");
			eventTriggerDao = (EventTriggerDao)context.getDAOByName("eventTriggerDao");
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			myTemplatesDao = (MyTemplatesDao) context.getDAOByName("myTemplatesDao");
			couponCodesDaoForDML=(CouponCodesDaoForDML)context.getDAOForDMLByName("couponCodesDaoForDML");//1178
			smsSuppressedContactsDao=(SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);;

							//process json string
				
			//for(DigitalReceiptsJSON digiReceiptsJSON : drJsonList) {
					
							
			String isLoyaltyCustomer=Constants.STRING_NILL;
				try {
					String drJsonStr = digiReceiptsJSON.getJsonStr();
					
					if(drJsonStr == null){ 
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
						return;
					}
					
					if(digiReceiptsJSON.getSource() !=null && digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_PRISM)){
						
						Gson gson = new Gson();
						PrismRequestTranslator PrismRequestTranslator = new PrismRequestTranslator();
						
						PrismBasedDRRequest prismRequest = gson.fromJson(drJsonStr, PrismBasedDRRequest.class);
						//DRBody drbody = PrismRequestTranslator.convertPrismRequest(prismRequest.getBody());
						DRBody drbody = PrismRequestTranslator.convertPrismRequest(prismRequest,user);
						drJsonStr = gson.toJson(drbody);
						//JSONObject jsonMainObj = (JSONObject)JSONValue.parse(drRequestJson);

					}
					if(digiReceiptsJSON.getSource() !=null && digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Magento)){
						
						Gson gson = new Gson();
						MagentoRequestTranslator magentoRequestTranslator = new MagentoRequestTranslator();
						
						MagentoBasedDRRequest magentoRequest = gson.fromJson(drJsonStr, MagentoBasedDRRequest.class);
						DRBody drbody = magentoRequestTranslator.convertMagentoRequest(magentoRequest,user);
						drJsonStr = gson.toJson(drbody);
						//JSONObject jsonMainObj = (JSONObject)JSONValue.parse(drRequestJson);

					}
					if(digiReceiptsJSON.getSource() !=null && digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce)){
						/*Gson gson = new Gson();
						WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
						WooCommerceDRRequest wooCommerceRequest = gson.fromJson(drJsonStr, WooCommerceDRRequest.class);
						if(wooCommerceRequest!=null) {
							DRBody drbody = wooCommerceRequestTranslator.convertWooCommerceRequest(wooCommerceRequest,user);
							drJsonStr = gson.toJson(drbody);
						}else {
							WooCommerceReturnDRRequest wooCommerceReturnRequest = gson.fromJson(drJsonStr, WooCommerceReturnDRRequest.class);
							DRBody drbody = wooCommerceRequestTranslator.convertWooCommerceRefundRequest(wooCommerceReturnRequest,user);
							drJsonStr = gson.toJson(drbody);
						}*/

						try {
						Gson gson = new Gson();
						WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
						
						JSONObject jsonMainObj = (JSONObject)JSONValue.parse(drJsonStr);
						WooCommerceDRHead headJson = gson.fromJson(jsonMainObj.get("Head").toString(), WooCommerceDRHead.class);
						String receiptStatus="";
						try {
						WooCommerceDRBody receipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceDRBody.class);
						receiptStatus = receipt.getOrderdetails().getStatus();
						}catch(Exception e) {
						WooCommerceReturnDRBody refundReceipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceReturnDRBody.class);
						receiptStatus = refundReceipt.getOrderdetails().getStatus();
						}
						if(headJson.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE) ||
								receiptStatus.equalsIgnoreCase("cancelled")){
						WooCommerceDRRequest wooCommerceRequest = gson.fromJson(drJsonStr, WooCommerceDRRequest.class);
						DRBody drbody = wooCommerceRequestTranslator.convertWooCommerceRequest(wooCommerceRequest,user);
						drJsonStr = gson.toJson(drbody);
						}else {
							WooCommerceReturnDRRequest wooCommerceReturnRequest = gson.fromJson(drJsonStr, WooCommerceReturnDRRequest.class);
							DRBody drbody = wooCommerceRequestTranslator.convertWooCommerceRefundRequest(wooCommerceReturnRequest,user);
							drJsonStr = gson.toJson(drbody);
						}
						}catch (Exception e) {
							// TODO Auto-generated catch block
							logger.debug("could not convert the json", e);
						}
						
					
					}
					if (digiReceiptsJSON.getSource() != null
							&& digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify)) {
						try {
							logger.debug("digiReceiptsJSON for shopify-"+digiReceiptsJSON.getDrjsonId());
							Gson gson = new Gson();
							ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();

							ShopifyBasedDRRequest shopifyRequest = gson.fromJson(drJsonStr, ShopifyBasedDRRequest.class);
							DRBody drbody = shopifyRequestTranslator.convertShopifyRequest(shopifyRequest, user);
							drJsonStr = gson.toJson(drbody);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.debug("Exception :", e);
						}
					}
			
					if (digiReceiptsJSON.getSource() != null
							&& digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_HeartLand)) {
						try {
							logger.debug("digiReceiptsJSON for Heartland-"+digiReceiptsJSON.getDrjsonId());
							Gson gson = new Gson();
							HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();

							HeartlandDRRequest heartlandRequest = gson.fromJson(drJsonStr, HeartlandDRRequest.class);
							DRBody drbody = heartlandRequestTranslator.convertHeartlandRequest(heartlandRequest, user);
							drJsonStr = gson.toJson(drbody);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.debug("Exception :", e);
						}
					}
					//APP-4773
					if (digiReceiptsJSON.getSource() != null
							&& digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_ORION)) {
						try {
							logger.debug("digiReceiptsJSON for Orion-"+digiReceiptsJSON.getDrjsonId());
							Gson gson = new Gson();
							OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();

							OrionDRRequest orionRequest = gson.fromJson(drJsonStr, OrionDRRequest.class);
							DRBody drbody = orionRequestTranslator.convertOrionRequest(orionRequest, user);
							drJsonStr = gson.toJson(drbody);
						} catch (Exception e) {
							logger.debug("Exception :", e);
						}
					}
					JSONObject jsonObject = null;
					JSONObject jsonBody = null;
					try {
						jsonObject = (JSONObject)JSONValue.parse(drJsonStr);
						if(jsonObject == null){ 
							digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
							digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
							return;
						}
						
						jsonBody = (JSONObject)jsonObject.get("Body");
						
						if(jsonBody == null){
							
							if((digiReceiptsJSON.getMode() != null && 
									digiReceiptsJSON.getMode().equals(OCConstants.DR_OFFLINE_MODE))|| (
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_PRISM))||(
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Magento))||(
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce))||(
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify))||(
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_HeartLand))||(
											digiReceiptsJSON.getSource() != null && 
											digiReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_ORION))){
								
								jsonBody = jsonObject;
								
							}else{
								digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
								digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
								return;
							}
							
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
						return;
					}
					
					
					JSONObject receiptJsonObj = (JSONObject)jsonBody.get("Receipt");
					JSONObject headJsonObj = (JSONObject)jsonObject.get("Head");
					if(headJsonObj!=null) {
						if(headJsonObj.get("requestType") != null && headJsonObj.get("requestType").toString().equalsIgnoreCase("Resend")){
							logger.debug("duplicate receipt==");
							
							digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
							digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
							return;
						}
						if(headJsonObj.get("isLoyaltyCustomer")!=null) isLoyaltyCustomer = (String)(headJsonObj.get("isLoyaltyCustomer"));
					}
					
					//For invalid receipt , do not process sku and sales
					if(receiptJsonObj == null || !(receiptJsonObj instanceof JSONObject)){
						
						logger.info("In DR data extraction, Receipt json object is null or is not an jsonobject");
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
						return;
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
						return;
					}
					
					//set null to contact, sku and sales objects 
					inputContact = null;
					dbskuFileList = null;
					retailProSalesList = null;
					contactProcessed = false;
					contactExistInOC = false;
					
					//extract contact data from json and process it 
					
					if(digiReceiptsJSON.getRetryForLtyExtraction() == 0 && user.isDigitalReceiptExtraction()){
					try {
						processContactData(user, mailingList,receiptJsonObj);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
					}
					//logger.debug(">>>>>>>>>>>..after contact processed"+contactProcessed);
						
					
					if(contactProcessed == true){
						if(isEnableContactEvent && isEnableEventForInputContact) {
							if(startId == null) {
								startId = inputContact.getContactId();
							}
							endId = inputContact.getContactId();
						}
					}
					//extract sku data from json and process it
					try {
						processSKUData(user, mailingList, itemsJsonArray, receiptJsonObj);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}
					//getMastToTransMappings(mailingList, user.getUserId());
		
					//extract sales data from json and process it
					try {
						processSalesData(user, mailingList, itemsJsonArray, receiptJsonObj);
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
					//getMastToTransMappings(mailingList, user.getUserId());
				
					/*//update status of json as processed
					digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_PROCESSED);
					//digitalReceiptsJSONDao.saveOrUpdate(digiReceiptsJSON);
					digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
					else{
						digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
						digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
					}*/
					
				
				
			logger.debug("Completed Digital Receipts Data Extraction >>>>>>>"+user.getUserName());
			if(isEnableContactEvent && startId != null && endId	!= null ) {
				
				eventTriggerEventsObservable.notifyToObserver(contactTypeeventTriggers, startId, endId, user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);
			}
			if(isEnableSalesEvent && salesStartId != null && salesEndId != null) {
				
				eventTriggerEventsObservable.notifyToObserver(salesTypeEventTriggers, salesStartId, salesEndId, user.getUserId(), Constants.POS_MAPPING_TYPE_SALES);
			}
			
			//needed to ensure for each user these will start from scratch.
			startId = null;
			endId = null;
			salesStartId = null;
			salesEndId = null;
			
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				logger.error("Exception in DR json data extraction...");
			}
			
			//update status of json as processed
			if(digiReceiptsJSON.getRetryForLtyExtraction()==0){
			digiReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_PROCESSED);
			}
			digitalReceiptsJSONDaoForDML.saveOrUpdate(digiReceiptsJSON);
			
			//extract loyalty
			if(user.isEnableLoyaltyExtraction() || user.isEnablePromoRedemption()){
			if(digiReceiptsJSON!=null && !digiReceiptsJSON.getMode().equalsIgnoreCase(Constants.DR_JSON_MODE_OFFLINE)&&
					user.getUserOrganization().getLoyaltyDisplayTemplate() != null && 
					  user.getUserOrganization().isSendRealtimeLoyaltyStatus()) { 
					  //&& isLoyaltyCustomer != null && isLoyaltyCustomer.trim().equals("Y")) {
						logger.info("Loyalty transactions happen real time. Exiting extraction");
						return;
			}
				String url =  PropertyUtil.getPropertyValue("loyaltyExractionUrl");
				String finalUrl=url.replace("|^", "").replace("^|", "");
				finalUrl=finalUrl.replaceAll("jsonId",digiReceiptsJSON.getDrjsonId().toString());
				logger.info("finalUrl "+finalUrl);
				Utility.pingSubscriberService(finalUrl);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
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
			contactObj = contactsDao.findContactByUniqPriority(prioMap, inputContact, user);
			logger.info("contactObj "+contactObj );
		}
		
		
		try {
			long contactBit = 0l;
			boolean purgeFlag = false;
			
			if(contactObj != null){

				contactBit = contactObj.getMlBits().longValue();
				logger.info("contactObj "+contactObj.getMobilePhone() );
				/*if(contactBit != 0l){
					inputContact = contactObj;
					contactBit = (contactBit | mailingList.getMlBit().longValue());
					inputContact.setMlBits(contactBit);
					contactsDao.saveOrUpdate(inputContact);
					logger.info("contact already exist in OC... do not merge with pos data...");
					contactProcessed = true;
					return;
				}*/
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
				boolean isNeedToPerformMO =  inputContact.getMobilePhone()!= null && user.isEnableSMS() && user.isConsiderSMSSettings();
				if(isNeedToPerformMO ) {
					try {
							performMobileOptIn(inputContact, false, contactObj);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception while performing mobile optin",e);
							if(inputContact.getMobileStatus() == null )inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
						}
					
				}
				
				if(Utility.isModifiedContact(inputContact, contactObj ))
				{
					logger.info("entered Modified date");
					contactObj.setModifiedDate(Calendar.getInstance());
				}
				
				inputContact = Utility.mergeContacts(inputContact, contactObj);

				if(inputContact.getMobilePhone() == null) {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					
				}else if(!isNeedToPerformMO){
					
					//checking with input mobile number
					List<SMSSuppressedContacts> SuprList =  smsSuppressedContactsDao.searchContactsById(user.getUserId(), inputContact.getMobilePhone());

					logger.info("inputContact mobile status"+inputContact.getMobileStatus());
					
					if(SuprList.isEmpty() && SuprList.size()== 0) {    //checking in Surpress list
					//APP-4253
						inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);  //this line
					
					}
				}
				//*****************************************************************
				//logger.info("before contact exist: mobile :"+inputContact.getMobilePhone());
				//logger.info("after contact exist: mobile :"+inputContact.getMobilePhone());
				
				inputContact.setEmailStatus(emailStatus);
				inputContact.setPurged(emailFlag);
				if(!((contactBit & mailingList.getMlBit().longValue())>0)){
					mailingList.setListSize(mailingList.getListSize()+1);
				}
				contactBit = (contactBit | mailingList.getMlBit().longValue());
			}
			else{
				mailingList.setListSize(mailingList.getListSize() + 1);
				contactBit = mailingList.getMlBit().longValue();
				purgeFlag = true;
				inputContact.setCreatedDate(Calendar.getInstance());
				inputContact.setModifiedDate(Calendar.getInstance());
				inputContact.setPurged(false);
				inputContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				inputContact.setUsers(user);
				inputContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				isEnableEventForInputContact = true;
				//perform mobile status
				if(inputContact.getMobilePhone()!= null) {
					if(user.isEnableSMS() && user.isConsiderSMSSettings()){//contactObj.setMobilePhone(contactObj.getMobilePhone());
					try {
						performMobileOptIn(inputContact, true, null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception while performing mobile optin",e);
						if(inputContact.getMobileStatus() == null )inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
					}else{
						inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
				}else {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			}
			
			
			
			mailingList.setLastModifiedDate(new Date());
			
			inputContact.setMlBits(contactBit);
			
			if(purgeFlag) {
				purgeList = (PurgeList)ServiceLocator.getInstance().getBeanByName("purgeList");
				purgeList.checkForValidDomainByEmailId(inputContact);
				
			}
			//logger.info("contact exist: mobile :"+inputContact.getMobilePhone());
			contactsDaoForDML.saveOrUpdate(inputContact);
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(user, inputContact, inputContact.getMobilePhone());
			contactProcessed = true;
			mailingListDaoForDML.saveOrUpdate(mailingList);
			
			if(mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
				
				//sendWelcomeEmail(contactObj, mailingList, mailingList.getUsers());
				sendWelcomeEmail(inputContact, mailingList, mailingList.getUsers());
			
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
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
		
		if(custFieldAttribute.startsWith("UDF") && dateTypeStr.startsWith("Number")) fieldValue = Utility.validateNumberValue(fieldValue);
		logger.info("DR formatted number value is -----"+fieldValue);
		if(custFieldAttribute.startsWith("UDF") && dateTypeStr.startsWith("Double")) fieldValue = Utility.validateDoubleValue(fieldValue);
		logger.info("DR formatted double value is -----"+fieldValue);
		
	//	if(fieldValue == null && fieldValue.trim().length() <= 0) continue;
		
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
				logger.error("Exception ::::" , e);
			}
			
		}*/
		
		
		Object[] params = null;
		Method method = null;
		/*try {             
			   Method method = contact.getClass().getMethod("set" + OCAttribute, new Class[] { 
					   drReceiptDataMap.get(drAttribute[1]).getClass() });
			   
			   method.invoke(contact, drReceiptDataMap.get(drAttribute[1]));
			 } catch (Exception ex) {
			        logger.error("Exception ::::", ex); 
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
				logger.info("before phone parse: "+phoneParse);
				if(phoneParse != null){
				logger.info("after phone parse: "+phoneParse);
				phoneParse=LoyaltyProgramHelper.preparePhoneNumber(users,phoneParse);// //APP-4485 saving without country code in contacts
				logger.info("after prepare phone number: "+phoneParse);
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
			
			else if (custFieldAttribute.equals(POSFieldsEnum.subsidiaryNumber.getOcAttr()) && fieldValue.length() > 0) {
				method = Contacts.class.getMethod("set" + POSFieldsEnum.subsidiaryNumber.getPojoField(), strArg);
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
				logger.error("Exception ::::" , e);
			} /*catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			}*/
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//logger.info("securityexception");
			logger.error("Exception ::::" , e);
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
				logger.error("Exception ::::" , e);
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
				skuFileObj.setModifiedDate(Calendar.getInstance());

				//skuFileDao.saveOrUpdate(skuFileObj);
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
				//logger.info("existing sku object sku id : "+skuFileObj.getSkuId());
				
			}
			else {
				skuFileObj = new SkuFile(); 
				skuFileObj = Utility.mergeSkuFile(skuFile, skuFileObj);
				skuFileObj.setUserId(user.getUserId());
				skuFileObj.setCreatedDate(Calendar.getInstance());
				skuFileObj.setModifiedDate(Calendar.getInstance());;
				
				//skuFileDao.saveOrUpdate(skuFileObj);
				skuFileDaoForDML.saveOrUpdate(skuFileObj);
				//logger.info("existing sku object sku id : "+skuFileObj.getSkuId());
				
			}
		
			dbskuFileList.add(skuFileObj);
			//logger.debug("dbskuFileList =="+dbskuFileList.size());
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
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Number")) fieldValue = Utility.validateNumberValue(fieldValue);
					logger.info("DR formatted number value is -----"+fieldValue);
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Double")) fieldValue = Utility.validateDoubleValue(fieldValue);
					logger.info("DR formatted double value is -----"+fieldValue);
					
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
							logger.error("Exception ::::" , e);
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
					}else if (custFieldAttribute.equals(POSFieldsEnum.classCode.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.classCode.getPojoField(), strArg);
						params = new Object[] { fieldValue };
						logger.debug("classCode =="+method +" " +params);
					}else if (custFieldAttribute.equals(POSFieldsEnum.Sclass.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.Sclass.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}else if (custFieldAttribute.equals(POSFieldsEnum.dcs.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.dcs.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}else if (custFieldAttribute.equals(POSFieldsEnum.vendorCode.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.vendorCode.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}else if (custFieldAttribute.equals(POSFieldsEnum.departMentCode.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.departMentCode.getPojoField(), strArg);
						params = new Object[] { fieldValue };
						logger.debug("departMentCode =="+method +" " +params);
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.itemSid.getOcAttr()) && fieldValue.length() > 0) {

						method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemSid.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}
					else if (custFieldAttribute.equals(POSFieldsEnum.storeNumber.getOcAttr()) && fieldValue.length() > 0) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.storeNumber.getPojoField(), strArg);
						params = new Object[] { fieldValue };
					}else if (custFieldAttribute.equals(POSFieldsEnum.SBSNumber.getOcAttr()) && fieldValue.length() > 0) {
						
						method = SkuFile.class.getMethod("set" + POSFieldsEnum.SBSNumber.getPojoField(), strArg);
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
							logger.error("Exception ::::" , e);
						} /*catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						}*/
					}
					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					} /*catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					}*/
					
			}
			
			//add inputskuFile object to list
			
			inputSkuFileList.add(inputSkuFile);
				
				
			}
			
			
			return inputSkuFileList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
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
			String docsid = Constants.STRING_NILL;


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
						
						if(skufileObj.getItemSid() != null && skufileObj.getItemSid().equals(salerecord.getItemSid())) {
								//skufileObj.getStoreNumber().equals(salerecord.getStoreNumber())){
							salerecord.setInventoryId(skufileObj.getSkuId());
							//logger.info("skufileObj sku id: "+skufileObj.getSkuId());
							//break;
						}
						/*if(inputContact != null && 
								salerecord.getCustomerId() != null && 
								inputContact.getExternalId().equals(salerecord.getCustomerId())){
							
							salerecord.setCid(inputContact.getContactId());
						}*/
					}
					
					salerecord.setCid(inputContact.getContactId());
					salerecord.setUserId(user.getUserId());
				/*						
					if(salerecord.getCustomerId()!=null)
					{
					UpdateCustomerSalesData updateCustomerSalesData = new UpdateCustomerSalesData();
					updateCustomerSalesData.updateOrInsertSalesAggData(salerecord,user.getUserId(),uniqueAcrossCustValue);
					}
					else
					{
						logger.info("customer_id is null so, customer_sales_updated_data table is not updated");
					}*/
							
					//retailProSalesDao.saveOrUpdate(salerecord);
					retailProSalesDaoForDML.saveOrUpdate(salerecord);
					//if(isEnableSalesEvent) {
						if(salesStartId == null) {
							salesStartId = salerecord.getSalesId().longValue();
						}
						salesEndId = salerecord.getSalesId().longValue();
					//}//if
					docsid =salerecord.getDocSid();
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
			//APP-3944
			if(user.getCountryType() != null && user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) { //lets test n monitor only for indian accounts first
				//offing this  to investigate the missing sales data for stylun  
				
				if((user.getUserId()==1323 || user.getUserId()==1309) && (inputContact.getUdf15()!=null && inputContact.getUdf15().equals("store number")) ) {//OPS-516
					//for SISM & RSB contacts that have store mobile number, skip sales aggr calculation
					logger.info("not updating sale aggr data bcz its a dummy contact, having store mobile number ... cid is "+inputContact.getContactId());
				}
				else
					retailProSalesDaoForDML.updateSalesAggregateData(mailingList.getListId().longValue(), user.getUserId(), inputContact.getContactId());
			}
            //APP-1178
			//to reduce the the it takes for execution keep the lock , and  only one user needed this 
			/*if(user.getCountryType() != null && 
					user.getCountryType().equals(Constants.SMS_COUNTRY_US  ) && !user.isDigitalReceiptExtraction()) { //This fix saves connections
				couponCodesDaoForDML.updateReceiptNumberFromDRExtraction(user.getUserId(), user.getUserOrganization().getUserOrgId(),docsid,salesStartId,salesEndId);
			}*/
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
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
                boolean isDiscountMapped = false;
				
				int salesPOSMapCounter = 0;

				
				for(POSMapping posMapping : salesPOSMap){
                    salesPOSMapCounter++;
					
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
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Number")) fieldValue = Utility.validateNumberValue(fieldValue);
					logger.info("DR formatted number value is -----"+fieldValue);
					if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Double")) fieldValue = Utility.validateDoubleValue(fieldValue);
					logger.info("DR formatted double value is -----"+fieldValue);
					
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
							logger.error("Exception ::::" , e);
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

						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.recieptNumber.getPojoField(), strArg);
						String receiptNum = new String(fieldValue);
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
					else if (custFieldAttribute.equals(POSFieldsEnum.SBSNumber.getOcAttr()) && fieldValue.length() > 0) {
						
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.SBSNumber.getPojoField(), strArg);
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
                    else if(custFieldAttribute.equals(POSFieldsEnum.discount.getOcAttr()) && fieldValue.length() > 0){// added in 2.4.6
						method = RetailProSalesCSV.class.getMethod("set" + POSFieldsEnum.discount.getPojoField(), doubleArg);
						Double discountVal = new Double(fieldValue);
						params = new Object[] { discountVal };
					}
					
					if (method != null) {

						try {
							method.invoke(inputSales, params);
							//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						} /*catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						}*/
					}
					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					} /*catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					}*/
					
			}
			
			//add inputskuFile object to list
			
				inputSalesList.add(inputSales);
				
				//logger.info("sales object is added to the list>>>>>>>>>>>>");
				
				
			}
			
			
			return inputSalesList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			logger.error("Exception in preparing sales object data, DR data extraction");
			return null;
		}
		
	}
	
	
	
	public void performMobileOptIn(Contacts inputContactObj, boolean isNew, Contacts existingContact) throws BaseServiceException {

		try {
			//SMSSettings smsSettings = smsSettingsDao.findByUser(inputContactObj.getUsers().getUserId());
			List<SMSSuppressedContacts> SuprList =  smsSuppressedContactsDao.searchContactsById(user.getUserId(), inputContactObj.getMobilePhone());

			if(smsSettings == null || ocsmsGateway == null) {
			
			
			String noSMSComplaincyMsg = "No SMS settings found for your user Account," +
										"SMS may not be sent to the mobile contacts.";
			
			
			Messages messages = new Messages("Contact" ,"Mobile contacts may not be reachable" ,noSMSComplaincyMsg ,
					Calendar.getInstance(),"Inbox",false ,"Info", inputContactObj.getUsers()); 
			
			MessagesDao messagesDao = (MessagesDao)ServiceLocator.getInstance().getDAOByName("messagesDao");
			MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("messagesDaoForDML");
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);
			
				
			inputContactObj.setMobileOptin(false);
			if(SuprList.isEmpty() && SuprList.size()== 0) {  
				
				inputContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);//TODO need to finalize
			}
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
					
						CaptiwayToSMSApiGateway captiwayToSMSApiGateway =  new CaptiwayToSMSApiGateway(context);//(CaptiwayToSMSApiGateway)context.getBean("captiwayToSMSApiGateway");
					
					if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
						
						logger.debug("low credits with clickatell");
						return;
					}
					
					if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
						
						UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
						UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("usersDaoForDML");
						
						String msgContent = smsSettings.getAutoResponse();
						if(msgContent != null) {
							if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
								
								msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
							}
							//msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
						logger.debug("Digital Receipt sendSingleMobileDoubleOptin ..msgContent"+msgContent +"mobile"+mobile );
						String mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, mobile, msgContent, smsSettings);
						if(mobileStatus == null) {
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
						 * Update SMS Queue.
						 */
						SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
						smsQueueHelper.updateSMSQueue(mobile, msgContent, Constants.SMS_MSG_TYPE_OPTIN,  currentUser, smsSettings.getSenderId());
						
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
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
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active", contact.getEmailId(), contact.getUsers(),new Date(),
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
        else if (salesFieldStr.equals(POSFieldsEnum.SBSNumber.getOcAttr())) {return ""+salesObj.getSubsidiaryNumber();}
		else return null;

	}
	//this method is no longer useful
	/*private  void getMastToTransMappings( MailingList mailingList,Long userId ){

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
		retailProSalesDaoForDML.updateSalesToContactsMappings(mastToTransSet, mailingList.getListId(), userId, salesStartId, salesEndId);
		
	}//getMastToTransMappings
*/	

}
