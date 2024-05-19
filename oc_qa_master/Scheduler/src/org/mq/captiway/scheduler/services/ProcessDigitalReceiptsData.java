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
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.captiway.scheduler.POSFieldsEnum;
import org.mq.captiway.scheduler.UpdateCustomerSalesData;
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ProcessDigitalReceiptsData extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private ApplicationContext context;

	public ProcessDigitalReceiptsData(){}
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
		
	}
	
	
	
	//private static Map<String, String> drReceiptDataMap  = new HashMap<String, String>();
	

	UsersDao usersDao;
	DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	
	
	
	@Override
	public void run() {
		ExecutorService executor = null;
		try {
			// TODO Auto-generated method stub
			logger.debug("===started Process DR Extraction ====");
			usersDao = (UsersDao)context.getBean("usersDao");
			digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getBean("digitalReceiptsJSONDao");
			
			
			
			
			List<Users> usersList = usersDao.findByDigiReceptExtraction(true);
			
			if(usersList == null || usersList.size() == 0){
				logger.debug("Digital Receipts Users Not Found");
				return;
			}
			
			//user wise process digital receipt data 
			
			int NumOfUserThreads =  usersList.size();
			logger.debug("===NumOfUserThreads ===="+NumOfUserThreads);
			executor = Executors.newFixedThreadPool(NumOfUserThreads);
			for (Users user : usersList) {	
				
				 long totalCount = digitalReceiptsJSONDao.findDRJSONsCountByUserId(user.getUserId(), Constants.DR_JSON_PROCESS_STATUS_NEW);
				 if(totalCount <= 0){
					 logger.debug("totalCount ==="+totalCount);
					 continue;
				 }
				 
				 Runnable  DRExtractionUserService = new DRExtractionUserService(user, totalCount);
			     executor.execute(DRExtractionUserService);
			}//store-subsidiary outer
			executor.shutdown();			
			while (!executor.isTerminated()) {
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in Process DR Extraction ", e);
		}finally{
			if(executor != null) {
				executor.shutdown();			
				while (!executor.isTerminated()) {
				}
				logger.debug("===end Process DR Extraction ====");
				
			}
			
		}
		
	}
	
	
	//process contact data
	}
