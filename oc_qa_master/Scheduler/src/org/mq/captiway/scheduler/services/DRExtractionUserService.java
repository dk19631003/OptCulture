package org.mq.captiway.scheduler.services;

import java.lang.reflect.Method;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DRExtractionUserService implements Runnable {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private Users user;
	 private long totalCount ;
	
	
	//private static Map<String, String> drReceiptDataMap  = new HashMap<String, String>();
	public DRExtractionUserService(){}
	
	public DRExtractionUserService(Users user, long totalCount){
		
		this.user = user;
		this.totalCount = totalCount;
	}

	DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML;
	DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	MailingListDao mailingListDao;
	SMSSettingsDao smsSettingsDao;
	private EventTriggerDao eventTriggerDao;
	
	
	@Override
	public void run() {
		 ExecutorService executor = null;
		try {
			
			logger.debug("===started Process DR Extraction for user ===="+user.getUserId().longValue());
			ServiceLocator context = ServiceLocator.getInstance();
			digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)context.getDAOForDMLByName("digitalReceiptsJSONDaoForDML");
			digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getDAOByName("digitalReceiptsJSONDao");
			mailingListDao = (MailingListDao)context.getDAOByName("mailingListDao");
			smsSettingsDao = (SMSSettingsDao)context.getDAOByName("smsSettingsDao");
			eventTriggerDao = (EventTriggerDao)context.getDAOByName("eventTriggerDao");
			
			List<DigitalReceiptsJSON> drJsonList = null;
			Long userId = user.getUserId();
			logger.debug("Started Digital Receipts Data Extraction >>>>>>>"+user.getUserName());
			MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, userId);
			
			if(mailingList == null){
				logger.debug("In DR data extraction, POS list not found for the user: "+user.getUserName());
				
				return;
			}
			
			
			
			SMSSettings smsSettings = null;
			OCSMSGateway ocsmsGateway =null ;
			if(user.isEnableSMS() && user.isConsiderSMSSettings()){
				 
				try {
				if(SMSStatusCodes.smsProgramlookupOverUserMap.get(user.getCountryType())) smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				else  smsSettings = smsSettingsDao.findByOrg(user.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				
				
				// smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
					if(smsSettings != null) ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}
				  
			 }
			List<EventTrigger> contactTypeeventTriggers = null;
			List<EventTrigger> salesTypeEventTriggers = null;
			boolean isEnableContactEvent = false; ;
			boolean isEnableSalesEvent= false;
			try{
			contactTypeeventTriggers = eventTriggerDao.findAllUserAutoRespondTriggers(userId.longValue(),
					(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED));
			
			isEnableContactEvent = (contactTypeeventTriggers != null && contactTypeeventTriggers.size() > 0);

			salesTypeEventTriggers = eventTriggerDao.findAllUserAutoRespondTriggers(userId.longValue(), 
														Constants.ET_TYPE_ON_PRODUCT+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE
														+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE_AMOUNT);
			
			isEnableSalesEvent =  (salesTypeEventTriggers != null && salesTypeEventTriggers.size() > 0);
			
			}catch(Exception e){
				logger.error(e);
				
			}
			
			
			long NumOfUserThreads =  Integer.parseInt(PropertyUtil.getPropertyValueFromDB(OCConstants.DR_EXTRACTION_THREADS_KEY));
			 
			 int threshold=5000;
			 int initialIndex=0;
			 long num_of_chunks= 1;
			 if(totalCount > threshold){
				 
				  num_of_chunks=(totalCount/threshold);
				 if(totalCount<threshold)
				 {
					 num_of_chunks=1;
				 }
				 else if((totalCount%threshold)>0){
					 num_of_chunks=(totalCount/threshold)+1;
				 }
				 else
				 {
					 num_of_chunks=(totalCount/threshold);
				 }
				
				 
			 }
			 
				
				
			 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
			 {
				 drJsonList= digitalReceiptsJSONDao.findDRJSONsByUserId(userId, Constants.DR_JSON_PROCESS_STATUS_NEW, initialIndex,threshold );
					
				 if(drJsonList == null || drJsonList.size() == 0){
					logger.debug("DR Json List is empty for the chunk : "+loop_var+","+initialIndex +","+threshold);
					 continue;
				 }
				 executor = Executors.newFixedThreadPool((int)NumOfUserThreads);
				 int itercount = 1;
				 for (DigitalReceiptsJSON digitalReceiptsJSON : drJsonList) {
					 /*if(itercount == 100){
						 if(executor != null){
								executor.shutdown();			
								while (!executor.isTerminated()) {
								}
							}
						 executor = Executors.newFixedThreadPool((int)NumOfUserThreads);
					 }*/
					 Runnable  DRExtractionRecordThread = new DRExtractionRecordThread(digitalReceiptsJSON, user, mailingList, 
							 isEnableContactEvent, isEnableSalesEvent, contactTypeeventTriggers, salesTypeEventTriggers, smsSettings, ocsmsGateway);
					 executor.execute(DRExtractionRecordThread);
					 itercount += 1;
				 }
				 if(executor != null){
					executor.shutdown();			
					while (!executor.isTerminated()) {
					}
				}
				 initialIndex =(loop_var*threshold)+1;
			 }
				
			 
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}finally{
			if(executor != null){
				executor.shutdown();			
				while (!executor.isTerminated()) {
				}
			}
			
			logger.debug("===end Process DR Extraction for user ===="+user.getUserId().longValue());
		}
			
			
		
}
	
	}