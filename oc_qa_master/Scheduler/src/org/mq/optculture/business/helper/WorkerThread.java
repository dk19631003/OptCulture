package org.mq.optculture.business.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactParentalConsent;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.beans.HomesPassed;
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
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDao;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CustomTemplatesDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDaoForDML;
import org.mq.captiway.scheduler.dao.HomesPassedDao;
import org.mq.captiway.scheduler.dao.HomesPassedDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MailingListDaoForDML;
import org.mq.captiway.scheduler.dao.MastersToTransactionMappingsDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.MyTemplatesDao;
import org.mq.captiway.scheduler.dao.POSMappingDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;//1178
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SalesLiteralHashCodeGenerateDao;
import org.mq.captiway.scheduler.dao.SalesLiteralHashCodeGenerateDaoForDML;
import org.mq.captiway.scheduler.dao.SkuFileDao;
import org.mq.captiway.scheduler.dao.SkuFileDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.services.CaptiwayToSMSApiGateway;
import org.mq.captiway.scheduler.services.EventTriggerEventsObservable;
import org.mq.captiway.scheduler.services.EventTriggerEventsObserver;
import org.mq.captiway.scheduler.services.PurgeList;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CustomFieldValidator;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;

public class WorkerThread implements Runnable {

	private static final  Logger logger = LogManager.getLogger(Constants.FILE_PROCESS_LOGGER);

	//private static final String csvDelemiterStr ="(?:\"\\s*,\\s*\"|^\\s*\"|\\s*\"\\s*$)";
	//private static final String csvDelemiterStr ="\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)";

	private static final String[] mName = {"January", "February", "March", "April", 
		"May", "June", "July","August", "September",
		"October", "November", "December"};
	private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	private static Map<String, String> genFieldSalesMap = new HashMap<String, String>();
	private static Map<String, String> genFieldSKUMap = new HashMap<String, String>();
	private static Map<String, String> genFieldHomesPassedMap = new HashMap<String, String>();
	static{
		genFieldContMap.put("Email", "emailId");
		genFieldContMap.put("First Name", "firstName");
		genFieldContMap.put("Last Name", "lastName");
		genFieldContMap.put("Street", "addressOne");
		genFieldContMap.put("Address Two", "addressTwo");
		genFieldContMap.put("City", "city");
		genFieldContMap.put("State", "state");
		genFieldContMap.put("Country", "country");

		genFieldContMap.put("ZIP", "zip");
		genFieldContMap.put("Mobile", "mobilePhone");
		genFieldContMap.put("Customer ID", "externalId" );
		genFieldContMap.put("Addressunit ID", "hpId" );
		genFieldContMap.put("Gender", "gender");
		genFieldContMap.put("BirthDay", "birthDay");
		genFieldContMap.put("Anniversary", "anniversary");
		genFieldContMap.put("Home Store", "homeStore");
		genFieldContMap.put("Subsidiary Number", "subsidiaryNumber");
		genFieldContMap.put("Created Date", "createdDate");

		genFieldSalesMap.put("Customer ID", "customerId");
		genFieldSalesMap.put("Receipt Number", "recieptNumber");
		genFieldSalesMap.put("Sale Date", "salesDate");
		genFieldSalesMap.put("Quantity", "quantity");
		genFieldSalesMap.put("Sale Price", "salesPrice");
		genFieldSalesMap.put("Tax", "tax");
		genFieldSalesMap.put("Promo Code", "promoCode");
		genFieldSalesMap.put("Store Number", "storeNumber");
		genFieldSalesMap.put("Subsidiary Number", "subsidiaryNumber");
		genFieldSalesMap.put("SKU", "sku");
		genFieldSalesMap.put("Tender Type", "tenderType");
		genFieldSalesMap.put("External ID", "externalId");
		genFieldSalesMap.put("Item Sid", "itemSid");
		genFieldSalesMap.put("Doc Sid", "docSid");
		genFieldSalesMap.put("Discount", "Discount");

		genFieldSKUMap.put("Store Number", "storeNumber");
		genFieldSKUMap.put("Subsidiary Number", "subsidiaryNumber");
		genFieldSKUMap.put("SKU", "sku");
		genFieldSKUMap.put("Description", "description");
		genFieldSKUMap.put("List Price", "listPrice");
		genFieldSKUMap.put("Item Category", "itemCategory");
		genFieldSKUMap.put("Item Sid", "itemSid");



		genFieldSKUMap.put("Vendor", "vendorCode");
		genFieldSKUMap.put("Department", "departmentCode");
		genFieldSKUMap.put("Class", "classCode");
		genFieldSKUMap.put("Subclass", "subClassCode");
		genFieldSKUMap.put("DCS", "DCS");

		genFieldHomesPassedMap.put("Addressunit Id" , "addressUnitId");
		genFieldHomesPassedMap.put("Country" , "country");
		genFieldHomesPassedMap.put("State" , "state");
		genFieldHomesPassedMap.put("District" , "district");
		genFieldHomesPassedMap.put("City" , "city");
		genFieldHomesPassedMap.put("ZIP" , "zip");
		genFieldHomesPassedMap.put("Area" , "area");
		genFieldHomesPassedMap.put("Street" , "street");
		genFieldHomesPassedMap.put("Address One" , "addressOne");
		genFieldHomesPassedMap.put("Address Two" , "addressTwo");

	}


	private POSMappingDao posMappingDao = null;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	private UsersDao usersDao = null;
	private UsersDaoForDML usersDaoForDML = null;
	private MailingListDao mailingListDao = null;
	private MailingListDaoForDML  mailingListDaoForDML = null;
	private SalesLiteralHashCodeGenerateDao salesLitaeralHashCodeDao = null; 
	private SalesLiteralHashCodeGenerateDaoForDML salesLitaeralHashCodeDaoForDML = null; 
	private ContactParentalConsentDao contactParentalConsentDao = null;
	private ContactParentalConsentDaoForDML contactParentalConsentDaoForDML = null;
	private EmailQueueDao emailQueueDao = null;
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	private CustomTemplatesDao customTemplatesDao; 
	private EventTriggerDao eventTriggerDao;

	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerEventsObservable eventTriggerEventsObservable;

	//	Calendar folderCal = Calendar.getInstance();
	//	String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
	/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	String yearStr = sdf.format(folderCal.getTime());*/

	private ApplicationContext context;

	

	private HomesPassedDao homesPassedDao;
	private HomesPassedDaoForDML homesPassedDaoForDML;

	private RetailProSalesDao retailProsalesDao;
	private CouponCodesDaoForDML couponCodesDaoForDML;//1178
	private RetailProSalesDaoForDML retailProsalesDaoForDML;
	private SkuFileDao skuFileDao;
	private SkuFileDaoForDML skuFileDaoForDML;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML;
	private String userParentDir;
	private SMSSettingsDao smsSettingsDao;
	private SMSSettings smsSettings ;
	private OCSMSGateway ocsmsGateway;
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private int mobileContacts = 0; 
	private String noSMSComplaincyMsg = null;
	private String noSMSCredits = null;
	private Set<String> optinMobileSet;
	String username;

	private Long startSalesId ;
	private Long endSalesId ;

 
	private Users users = null;
	private MailingList mailingList = null;

	public WorkerThread(Users users, MailingList mailingList, ApplicationContext context) {
		this.users = users;
		this.mailingList = mailingList;
		this.context = context;
		username = Utility.getOnlyOrgId(users.getUserName());
		userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory")+"/"+users.getUserName();
		//List<Users> specificDirExists = SpecificDirExists();
		if(users.isSpecificDir()) {
			userParentDir = PropertyUtil.getPropertyValueFromDB("SftpUserPath") + "/" + username;
		}
	}
	
		public List<Users> SpecificDirExists() throws Exception {
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
			List<Users> specificDirUsers = usersDao.findAnySpeciDirBasedUsersExists();
			return specificDirUsers;
			
		}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		logger.debug("....=======> Started  run :: " + startTime);
		try {
			logger.debug("Wroker  Thread started here .."+Thread.currentThread().getName());
			
			
			
			messagesDao = (MessagesDao)context.getBean("messagesDao");
			messagesDaoForDML = (MessagesDaoForDML )context.getBean("messagesDaoForDML");
			posMappingDao = (POSMappingDao)context.getBean("posMappingDao");
			usersDao = (UsersDao)context.getBean("usersDao");
			usersDaoForDML = (UsersDaoForDML)context.getBean("usersDaoForDML");

			smsSettingsDao = (SMSSettingsDao)context.getBean("smsSettingsDao");
			//get mailingList for this User
			externalSMTPEventsDaoForDML = (ExternalSMTPEventsDaoForDML) context.getBean("externalSMTPEventsDaoForDML");

			mailingListDao = (MailingListDao)context.getBean("mailingListDao");
			mailingListDaoForDML = (MailingListDaoForDML)context.getBean("mailingListDaoForDML");
			contactsDao = (ContactsDao)context.getBean("contactsDao");
			contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
			retailProsalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
			
			couponCodesDaoForDML=(CouponCodesDaoForDML)context.getBean("couponCodesDaoForDML");//1178
			retailProsalesDaoForDML = (RetailProSalesDaoForDML)context.getBean("retailProSalesDaoForDML");
			homesPassedDao = (HomesPassedDao)context.getBean("homesPassedDao");
			homesPassedDaoForDML = (HomesPassedDaoForDML)context.getBean("homesPassedDaoForDML");

			salesLitaeralHashCodeDao = (SalesLiteralHashCodeGenerateDao)context.getBean("salesLitaeralHashCodeDao");
			salesLitaeralHashCodeDaoForDML = (SalesLiteralHashCodeGenerateDaoForDML)context.getBean("salesLitaeralHashCodeDaoForDML");

			contactParentalConsentDao = (ContactParentalConsentDao)context.getBean("contactParentalConsentDao");
			contactParentalConsentDaoForDML = (ContactParentalConsentDaoForDML)context.getBean("contactParentalConsentDaoForDML");
			emailQueueDao = (EmailQueueDao)context.getBean("emailQueueDao");
			emailQueueDaoForDML = (EmailQueueDaoForDML)context.getBean("emailQueueDaoForDML");
			customTemplatesDao = (CustomTemplatesDao)context.getBean("customTemplatesDao");

			eventTriggerDao = (EventTriggerDao)context.getBean("eventTriggerDao");

			//clickaTellApi = (ClickaTellApi)context.getBean("clickaTellApi");
			captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(context);

			eventTriggerEventsObserver = (EventTriggerEventsObserver)context.getBean("eventTriggerEventsObserver");
			eventTriggerEventsObservable = (EventTriggerEventsObservable)context.getBean("eventTriggerEventsObservable");

			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);//adding observer 

		//check source directory iff create structure
		checkDir(users);


		//This method uploads contacts from file.
		contactsFileUpload(users, mailingList);
		//This method uploads sku data from file.
		skuFileUpload(users, mailingList);
		//This method uploads sales data from file.
		salesFileUpload(users, mailingList);
		//upload the data of homesPassed
		//homesPassedFileUpload(users, mailingList);
		logger.debug("Worker Thread end here .."+ Thread.currentThread().getName());
		} catch(Exception e) {
			logger.error("Exception ..................:" , e);
		}
		logger.debug("<------------- Completed run in : "
				+ (System.currentTimeMillis() - startTime) + " Millisecond");

	}// run method 

	/**
	 * check source directory iff create structure
	 * @param users
	 */
	private void  checkDir(Users users)  {

		try {
			//String source = userParentDir + "/" + users.getUserName()  + "/POSList";
			String source = userParentDir + "/POSList";
			File sorceFiles = new File(source);

			if(!sorceFiles.exists()) {
				sorceFiles.mkdir();
			}

			String sourceFileStr =  source + "/sourceFiles/";

			sorceFiles = new File(sourceFileStr);
			if(!sorceFiles.exists()) {
				sorceFiles.mkdir();
			}

			String destPath   = source + "/processedFiles";

			File destDir = new File(destPath);
			//			logger.debug("destFile is exist..."+destDir.exists() +"::destFile name is>>"+destDir.getName());

			//File destFile = new File(destPath + "/" + sourceFile.getName());

			if(!destDir.exists()) {

				//				logger.debug("directory not exist create new ...");
				destDir.mkdir();
			}
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}

	} // checkDir


	/**

	 *   
	 */
	private void salesFileUpload(Users users, MailingList mList)   {
		try {

			
			String type = Constants.SALES_FILE_UPLOAD_FAILED;
			
			
			
			//String source = userParentDir+"/" + users.getUserName() + "/POSList/sourceFiles/salesFiles/";
			String source = userParentDir + "/POSList/sourceFiles/salesFiles/";
			File sorceFiles = new File(source);

			//			logger.debug("source is ;;;;"+source +" ;;sorceFiles is exist "+ sorceFiles.exists());

			if(!sorceFiles.exists()) {
				//				logger.debug("*** Exception : Sales Source file "+ sorceFiles.getName() + " does not exist ***");
				sorceFiles.mkdir();
			}

			File[] listFiles = sorceFiles.listFiles();
			if(listFiles == null || listFiles.length <= 0) {

				//logger.debug("NO source files exists...");
				return;
			}
			//logger.debug("uploading the sales listFiles length is ::"+listFiles.length);

			//salesLitaeralHashCodeDao = (SalesLiteralHashCodeGenerateDao)context.getBean("salesLitaeralHashCodeDao");

			//			retailProsalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");

			//String destSalesPath   = userParentDir+"/" + users.getUserName() + "/POSList/processedFiles/salesFiles/";
			String destSalesPath   = userParentDir+ "/POSList/processedFiles/salesFiles/";
			File destSalesDir = new File(destSalesPath);
			if(!destSalesDir.exists()) {
				destSalesDir.mkdir();
			} 

			File sourceFile = null;

			Calendar folderCal = Calendar.getInstance();
			String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String yearStr = sdf.format(folderCal.getTime());

			for (int i = 0; i < listFiles.length; i++) {

				try {
					startSalesId = null;
					endSalesId = null;

					sourceFile =listFiles[i];
					//logger.debug(" permission for the file ::"+sourceFile.getAbsolutePath());

					if(sourceFile.canRead() == false) {

						if(logger.isDebugEnabled()) logger.debug("no read permission for the file ::"+sourceFile.getAbsolutePath());
						continue;
					}

					//String destPath   = userParentDir+"/" + users.getUserName() + "/POSList/processedFiles/salesFiles/"+MonthStr+yearStr;
					String destPath   = userParentDir+ "/POSList/processedFiles/salesFiles/"+MonthStr+yearStr;

					if(sourceFile.getName().contains("_POSINCMPLETEPART") ||  sourceFile.getName().endsWith(".MARKEDINCOMPLETE")) {
						if(logger.isDebugEnabled()) logger.debug(sourceFile.getName()+ " ::  is inComplete File .." );
						continue;
					}

					File destDir = new File(destPath);
					File destFile = new File(destPath + File.separator + sourceFile.getName());

					if(!destDir.exists()) {

						//logger.debug("directory not exist create new ...");
						destDir.mkdir();
					} else {

						//logger.debug("copying the file from source to destination...");
						//destFile = new File(destPath + File.separator + sourceFile.getName());

						Messages messages = null;

						if(destFile.exists()) {
							//								logger.debug("source file already exists...");
							String messagesString = destFile.getName() +" file already exists..";

							//File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles");
							File unProcessedFileDir = new File(userParentDir+ "/POSList/unProcessFiles");

							if(!unProcessedFileDir.exists()) {

								unProcessedFileDir.mkdir();
							}
							//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles/"+sourceFile.getName());
							File unProcessFile = new File(userParentDir+ "/POSList/unProcessFiles/"+sourceFile.getName());
							sourceFile.renameTo(unProcessFile);

							messages = new Messages("Sales files failed", "Sales file upload failed", messagesString, Calendar.getInstance(), 
									"Inbox",false ,"Info", users);
							//messagesDao.saveOrUpdate(messages);
							messagesDaoForDML.saveOrUpdate(messages);
							
							
							/**Alert mail if file is not proccessed
							 */
							saveInEmailqueuse(type, users,sourceFile.getName());
							
							logger.info("sales alert mail is saved on email queue");
							
							continue;
						}
					}

					//logger.debug("destFile is exist..."+destFile.exists() +"::destFile name is>>"+destFile.getName());

					try {
						sourceFile.renameTo(destFile);
					} catch (Exception e) {

						logger.error("Exception ::::" , e);

						if(logger.isErrorEnabled()) logger.error("File moved in to unprocessed Dir");
						//File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles");
						File unProcessedFileDir = new File(userParentDir+ "/POSList/unProcessFiles");

						if(!unProcessedFileDir.exists()) {

							unProcessedFileDir.mkdir();
						}
						//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles/"+sourceFile.getName());
						File unProcessFile = new File(userParentDir+ "/POSList/unProcessFiles/"+sourceFile.getName());
						sourceFile.renameTo(unProcessFile);
					}

					/*******destfile is saved from the DB********/
					if(logger.isDebugEnabled()) logger.debug(destFile.getName()+":: file started here");
					//find all the purchase triggers configured to this user.
					/*
					 * this code is added for new ET implementation.
					 * Acts like an AUTO-RESPONDER(trigger event only when it has occured but not like polling)
					 */
					boolean isEnableEvent = false;

					List<EventTrigger> etList = eventTriggerDao.findAllUserAutoRespondTriggers(users.getUserId().longValue(), Constants.ET_TYPE_ON_PRODUCT
							+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE
							+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_PURCHASE_AMOUNT);
					if(logger.isInfoEnabled()) logger.debug(" >>>>>>>>>>>>>>>etList is>>>>>>>>>>>>>"+etList);
					if(etList != null && etList.size() > 0) {

						isEnableEvent = true;
					}


					saveSalesFile(destFile,users,mList, isEnableEvent);
					
					// updateing the masters to child relationship mappling data since we chnaged the order execute this only after the sales file
					if( startSalesId != null && endSalesId != null) {
						getMastToTransMappings(mList,users.getUserId());
					}
					//notify to the notifier about this event
					if(isEnableEvent && startSalesId != null && endSalesId != null) {
						//inputs are sales records,events list
						//if(logger.isInfoEnabled()) logger.debug(" >>>>>>>>>>>>>>>observer called>>>>>>>>>>>>>");
						eventTriggerEventsObservable.notifyToObserver(etList, startSalesId, endSalesId, users.getUserId(), Constants.POS_MAPPING_TYPE_SALES);


					}//if



					//after insertion of all these record calculate all the aggregate data upon those
					//retailProsalesDao.updateSalesAggregateData(mList.getListId().longValue());


					if(logger.isDebugEnabled()) logger.debug(destFile.getName()+":: file completed here");

				} catch (Exception e) {
					logger.error("Exception ::::" , e);
					continue;
				}

			} //for	

			if(logger.isDebugEnabled()) logger.debug("updating sales aggregate info....");
			//after insertion of all these record calculate all the aggregate data upon those
			//retailProsalesDao.updateSalesAggregateData(mList.getListId().longValue(), users.getUserId());
			List<Long> retCids = retailProsalesDao.executeQuery("SELECT DISTINCT cid from RetailProSalesCSV where "
					+ "userId="+users.getUserId()+" AND salesId BETWEEN "+startSalesId+" AND "+endSalesId +" AND cid IS NOT NULL");
			String cidStr = Constants.STRING_NILL;
			if(retCids != null && !retCids.isEmpty()) {
				for (Long cid : retCids) {
					cidStr += !cidStr.isEmpty() ? "," :Constants.STRING_NILL;
					cidStr += cid+Constants.STRING_NILL;
					
				}
				logger.info("cidStr==="+cidStr);
				if(!cidStr.isEmpty())retailProsalesDaoForDML.updateSalesAggregateData(mList.getListId().longValue(), users.getUserId(), 
						cidStr);
			}
			//APP-1178
			//update receipt number in couponcodes
			logger.info("updateReceiptNumberFromSalesFile");
			//to reduce the the it takes for execution keep the lock , and  only one user needed this 
			/*if(startSalesId != null &&  endSalesId != null && users.getCountryType() != null && 
					users.getCountryType().equals(Constants.SMS_COUNTRY_US)  && !users.isDigitalReceiptExtraction() ) couponCodesDaoForDML.updateReceiptNumberFromSalesFile(users.getUserId(), users.getUserOrganization().getUserOrgId(), startSalesId, endSalesId);
*/
		} catch (Exception e) {
			logger.error("Exception ::::" , e);

		} 

	} // purchaseCSVUpload

	private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
	private ArrayList<String> allMatches = new ArrayList<String>();        
	private Matcher matcher = null;

	public String[] parse(String csvLine) {
		matcher = csvPattern.matcher(csvLine);
		allMatches.clear();
		String match;
		while (matcher.find()) {
			match = matcher.group(1);
			if (match!=null) {
				allMatches.add(match);
			}
			else {
				allMatches.add(matcher.group(2));
			}
		}

		if (allMatches.size() > 0) {
			return allMatches.toArray(new String[allMatches.size()]);
		}
		else {
			return new String[0];
		}                       
	}   



	public void saveSalesFile(File destFile,Users users,MailingList mList, boolean isEnableEvent)  {

		try {
			//logger.debug(destFile.getName() +" is processed ..");
			TreeMap<String, List<String>> prioMap =null;
			prioMap = new TreeMap<String, List<String>>();

			List<POSMapping> salesPOSMappingList = null;

			salesPOSMappingList = posMappingDao.findPOSMappingListByStr("Sales", users.getUserId());


			if(salesPOSMappingList == null || salesPOSMappingList.size() < 1) {
				if(logger.isDebugEnabled()) logger.debug("no POS custField Mapping list from the DB for the userName is ::"+users.getUserName());
				return;
			}

			//logger.debug("salesPOSMappingList size is ::"+salesPOSMappingList.size());

			int idxCustomerID = -1, idxExternalID = -1,idxReceiptNumber = -1,idxSaleDate = -1,idxQty = -1,idxSKU = -1; 
			int idxSalePrice = -1,idxTax = -1,idxPromoCode = -1,idxStoreNumber = -1,idxSBSNumber = -1,idxTenderType = -1,idxItemSid= -1,
					idxDocSid= -1, idxDiscount = -1;


			Hashtable UdfHashtable = new Hashtable();

			List<RetailProSalesCSV> retailProsalesList = new ArrayList<RetailProSalesCSV>();

			int savingCount = 1;

			//			char delimiter = ',';
			String[] nextLineStr ;
			//CSVReader csvreader  = new CSVReader(new FileReader(destFile), delimiter);

			//Read HEADDERS
			FileReader fileReader = new FileReader(destFile);
			BufferedReader br = new BufferedReader(fileReader);

			String lineStr=null;
			String salesdateFormat ="";

			int uniqueAcrossFilesInd =-1;
			StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
			msgStuff.append(destFile.getName());
			//identify the headers from file
			while((lineStr = br.readLine())!= null) {
				try {
					//				lineStr += ",\"0\"";
					if(lineStr.trim().length()==0) continue;

					nextLineStr = parse(lineStr);// lineStr.split(csvDelemiterStr);

					if(nextLineStr.length == 0) {
						continue;
					}
					List<String> existedFieldList = new ArrayList<String>(); 

					if(salesPOSMappingList != null && salesPOSMappingList.size() >0 ) {

						for (POSMapping posMapping : salesPOSMappingList) {

							for(int j=0; j<nextLineStr.length ;j++) {

								String tempStr = nextLineStr[j].trim();

								if(posMapping.getUniqueInAcrossFiles()!=null  && 
										posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)){
									//logger.debug(">>>>>"+posMapping.getUniqueInAcrossFiles()+" ==== J="+j +"==="+tempStr);
									uniqueAcrossFilesInd=j;
								}

								// ,,,,Sale Price,Tax,,,SKU,
								if(existedFieldList.contains(tempStr)) {
									//logger.debug("Field already mapped with the general fields..");
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Customer ID") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//								logger.debug("tempStr.."+tempStr +">> idxCustomerID ::"+j);
									idxCustomerID =  j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 

								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("External ID") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//logger.debug("tempStr.."+tempStr +">> idxCustomerID ::"+j);
									idxExternalID =  j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 

								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Receipt Number") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxReceiptNumber ::"+j);
									idxReceiptNumber = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Sale Date") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxSaleDate ::"+j);
									if(posMapping.getDataType().trim().startsWith("Date")) {

										salesdateFormat = posMapping.getDataType();
										salesdateFormat = salesdateFormat.substring(salesdateFormat.indexOf("(")+1, salesdateFormat.indexOf(")"));
										//						    		logger.debug("Sales Date Format is ::"+salesdateFormat);
									}
									idxSaleDate =  j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Quantity") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxQty ::"+j);
									idxQty = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Sale Price") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxSalePrice ::"+j);
									idxSalePrice = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Tax") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxTax ::"+j);
									idxTax = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Promo Code") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxPromoCode ::"+j);
									idxPromoCode = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Store Number") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxStoreNumber ::"+j);
									idxStoreNumber = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Subsidiary Number") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxStoreNumber ::"+j);
									idxSBSNumber = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("SKU") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxSKU ::"+j);
									idxSKU = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Tender Type") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//						    	logger.debug("tempStr.."+tempStr +">> idxTenderType ::"+j);
									idxTenderType = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Item Sid") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//logger.debug("tempStr.."+tempStr +">> idxTenderType ::"+j);
									idxItemSid = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Doc Sid") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//logger.debug("tempStr.."+tempStr +">> idxTenderType ::"+j);
									idxDocSid = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Discount") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									logger.info("setting discount>>>>>>idxDiscount=="+idxDiscount+"tempStr===="+tempStr);
									idxDiscount = j;
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}

								else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().contains("UDF") 
										&&  posMapping.getPosAttribute().equalsIgnoreCase(tempStr)) {	


									//								logger.debug("posMapping.getPosAttribute() is ::"+posMapping.getPosAttribute() +":: and tempStr is "+tempStr);
									UdfHashtable.put(posMapping.getCustomFieldName(), j);
									existedFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}

							} //for

						} //if

					} // for
					if(logger.isDebugEnabled()) logger.debug("sales UdfHashtable size  is:"+UdfHashtable.size());
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					continue;
				}
			} // while




			//			List totalRecordsInCsvList = csvreader.readAll();
			int totalCountInCSV = 0;
			int existedRecordCount = 0;
			int invalidCount = 0;
			int newEntryRecords = 0;
			//String totalLineStr = "";
			//			Set<BigInteger> generatedUniqSet = new HashSet<BigInteger>();
			Set<String> generatedUniqStringSet = new HashSet<String>();

			SalesLiteralHashCode saleLiteralObj;
			List<SalesLiteralHashCode> salesLitHashCodCollList = new ArrayList<SalesLiteralHashCode>();

			String orgType = users.getUserOrganization().getClientType();

			if(orgType == null) {
				if(logger.isInfoEnabled()) logger.debug(" Got client type as null so assuming it is a POS client....");
				orgType = Constants.CLIENT_TYPE_POS;
			}

			String uniqueAccrossStr=null;

			// Read DATA
			while((lineStr = br.readLine())!= null) {
				try {
					//				lineStr += ",\"0\"";
					if(lineStr.trim().length()==0) continue;
					nextLineStr =  parse(lineStr);// lineStr.split(csvDelemiterStr);
					//calculate total count in a file
					if(nextLineStr.length == 0) {
						invalidCount ++;
						continue;
					}
					totalCountInCSV++;



					/*for(int i=0 ; i< nextLineStr.length ; i++) {

						totalLineStr = totalLineStr + nextLineStr[i].trim();
					}*/

					//TODO need to deviate the code as two flows based on the client type


					RetailProSalesCSV retailProSales = null;

					if(orgType.equalsIgnoreCase(Constants.CLIENT_TYPE_POS)) {

						//BigInteger tempBigInt = Utility.MD5Algorithm(lineStr.trim());

						if(uniqueAcrossFilesInd != -1) {
							uniqueAccrossStr = nextLineStr[uniqueAcrossFilesInd].trim();
							if(uniqueAccrossStr.isEmpty()) uniqueAccrossStr=null;
						}


						//boolean isExistInSet = uniqueAccrossStr!=null && generatedUniqStringSet.contains(uniqueAccrossStr);

						boolean existFlag = false;
						if(uniqueAccrossStr!=null && !generatedUniqStringSet.contains(uniqueAccrossStr)) { 
							existFlag  = salesLitaeralHashCodeDao.isCodeExist(users.getUserId(),  uniqueAccrossStr);
							if(existFlag) existedRecordCount ++;
						}


						if(existFlag == false) {

							if(uniqueAccrossStr !=null ) generatedUniqStringSet.add(uniqueAccrossStr);
							//if(!salesLitaeralHashCodeDao.existSalesLiteralObjByListId(mList.getListId(), ""+tempBigInt)) {

							//add the  salesUnique Identity values
							//generatedUniqSet.add(tempBigInt);	


							try {

								//TODO fetch sales obj.

								//						logger.debug("POSMAP=="+prioMap);


								if(!prioMap.isEmpty()) {
									retailProSales = retailProsalesDao.findSalesByPriority(prioMap,  nextLineStr, users.getUserId());
								}
								if(retailProSales != null) {

									retailProSales  = setSalesGenralFields(retailProSales, nextLineStr, idxCustomerID, idxExternalID, idxReceiptNumber,idxSaleDate,
											idxQty ,idxSalePrice, idxTax, idxPromoCode , idxStoreNumber ,idxSBSNumber,idxSKU ,idxTenderType , idxItemSid, idxDocSid,idxDiscount,UdfHashtable  ,salesPOSMappingList,users,salesdateFormat) ;
									//retailProsalesDao.saveOrUpdate(retailProSales);
									retailProsalesDaoForDML.saveOrUpdate(retailProSales);
									existedRecordCount++;
									continue;

								}
								else if(retailProSales==null) { 

									retailProSales = new RetailProSalesCSV();

									retailProSales  = setSalesGenralFields(retailProSales, nextLineStr, idxCustomerID, idxExternalID, idxReceiptNumber,idxSaleDate,
											idxQty ,idxSalePrice, idxTax, idxPromoCode , idxStoreNumber ,idxSBSNumber, idxSKU ,idxTenderType , idxItemSid ,idxDocSid ,idxDiscount, UdfHashtable  ,salesPOSMappingList,users,salesdateFormat) ;

									//if(retailProsalesDao.saveOrUpdate(retailProSales);
									//newEntryRecords ++;
									/*if(retailProSales.getCustomerId()!=null)
									{
									UpdateCustomerSalesData updateCustomerSalesData = new UpdateCustomerSalesData();
									updateCustomerSalesData.updateOrInsertSalesAggData(retailProSales,users.getUserId(),uniqueAccrossStr);
									}
									else
									{
										logger.debug("customer_id is null so, customer_sales_updated_data table is not updated");
									}*/
									retailProsalesList.add(retailProSales);

								}

							} catch (Exception e1) {


								logger.error("Exception :: error occured while setting the sales field date to due to wrong values existed from the csv file ..so we ignoe it..",e1);
								continue;

							}

						}else {

							//						logger.debug(">>>> Object of the content is already match with salesListeralHashCode Generated of the list Id is .."+mList.getListId());
							continue;
						}


					}//if POS
					else if(orgType.equalsIgnoreCase(Constants.CLIENT_TYPE_BCRM)) {


						if(!prioMap.isEmpty()) {

							retailProSales = retailProsalesDao.findSalesByPriority(prioMap,  nextLineStr, users.getUserId());

						}
						if(retailProSales != null) {

							retailProSales  = setSalesGenralFields(retailProSales, nextLineStr, idxCustomerID, idxExternalID, idxReceiptNumber,idxSaleDate,
									idxQty ,idxSalePrice, idxTax, idxPromoCode , idxStoreNumber ,idxSBSNumber,idxSKU ,idxTenderType , idxItemSid ,idxDocSid,idxDiscount, UdfHashtable  ,salesPOSMappingList,users,salesdateFormat) ;
							//retailProsalesDao.saveOrUpdate(retailProSales);
							retailProsalesDaoForDML.saveOrUpdate(retailProSales);
							existedRecordCount++;
							continue;

						}
						else if(retailProSales==null) { 

							retailProSales = new RetailProSalesCSV();

							retailProSales  = setSalesGenralFields(retailProSales, nextLineStr, idxCustomerID, idxExternalID, idxReceiptNumber,idxSaleDate,
									idxQty ,idxSalePrice, idxTax, idxPromoCode , idxStoreNumber ,idxSBSNumber,idxSKU ,idxTenderType ,idxItemSid, idxDocSid,idxDiscount, UdfHashtable  ,salesPOSMappingList,users,salesdateFormat) ;

							//if(retailProsalesDao.saveOrUpdate(retailProSales);
							//newEntryRecords ++;

							//TODO calling UpdateCustomerSalesData for update customer salesData
							/*	if(retailProSales.getCustomerId()!=null)
						{
						UpdateCustomerSalesData updateCustomerSalesData = new UpdateCustomerSalesData();
						updateCustomerSalesData.updateOrInsertSalesAggData(retailProSales,users.getUserId(),uniqueAccrossStr);
						}
						else
						{
							logger.debug("customer_id is null so, customer_sales_updated_data table is not updated");
						}*/


							retailProsalesList.add(retailProSales);

						}



					}//else 
					//for every 200 records saved in to DB
					if(retailProsalesList.size() >= 1000) {
						try {
							newEntryRecords = newEntryRecords + retailProsalesList.size();
							if(logger.isDebugEnabled()) logger.debug("File :"+destFile.getAbsolutePath()+" Curr LineCount="+totalCountInCSV);
							//						if(logger.isDebugEnabled()) logger.debug("for every 200 record call the gc() method... and save the records in to Db");

							//retailProsalesDao.saveByCollection(retailProsalesList);
							retailProsalesDaoForDML.saveByCollection(retailProsalesList);

							//imp if(isEnableEvent) {
								if( startSalesId == null) {

									startSalesId = retailProsalesList.get(0).getSalesId();

								}
								endSalesId = retailProsalesList.get(retailProsalesList.size()-1).getSalesId();
						//	}
							retailProsalesList.clear();



							//						generatedUniqSet

							savingCount = 0;
							//System.gc();
						} catch (Exception e) {
							logger.error("Exception ::::" , e);
						}
					}

					try {

						if(generatedUniqStringSet.size() >= 1000) {

							for (String eachCodeStr : generatedUniqStringSet) {

								saleLiteralObj = new SalesLiteralHashCode();
								saleLiteralObj.setListId(mList.getListId());
								saleLiteralObj.setUserId(users.getUserId());
								saleLiteralObj.setSalesLiteralHashCode(eachCodeStr);
								saleLiteralObj.setCurrentFile(true);
								saleLiteralObj.setCreatedDate(Calendar.getInstance());
								salesLitHashCodCollList.add(saleLiteralObj);
							} // for

							//salesLitaeralHashCodeDao.saveByCollection(salesLitHashCodCollList);
							salesLitaeralHashCodeDaoForDML.saveByCollection(salesLitHashCodCollList);
							generatedUniqStringSet.clear();
							salesLitHashCodCollList.clear();

						}


					} catch (Exception e) {
						logger.error("Error :", e);
					}


					savingCount++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
					msgStuff.append("\n error occured at record#: "+totalCountInCSV );
					invalidCount ++;
					continue;
				}

			} //while


			br.close();
			fileReader.close();

			if(retailProsalesList.size() > 0) {


				newEntryRecords = newEntryRecords+retailProsalesList.size();

				//retailProsalesDao.saveByCollection(retailProsalesList);
				retailProsalesDaoForDML.saveByCollection(retailProsalesList);
//
				//if(isEnableEvent) {
					if( startSalesId == null) {

						startSalesId = retailProsalesList.get(0).getSalesId();

					}
					endSalesId = retailProsalesList.get(retailProsalesList.size()-1).getSalesId();
				//}


				if(logger.isDebugEnabled()) logger.debug("total  records are saved sucessfully");

				retailProsalesList.clear();
				savingCount = 0;
				//System.gc();
			}


			/**********************/

			//			generatedUniqSet
			if(generatedUniqStringSet.size() > 0) {

				for (String eachCodeStr : generatedUniqStringSet) {

					saleLiteralObj = new SalesLiteralHashCode();
					saleLiteralObj.setListId(mList.getListId());
					saleLiteralObj.setUserId(users.getUserId());
					saleLiteralObj.setSalesLiteralHashCode(eachCodeStr);
					saleLiteralObj.setCurrentFile(false);
					saleLiteralObj.setCreatedDate(Calendar.getInstance());
					salesLitHashCodCollList.add(saleLiteralObj);
				} // for

				//salesLitaeralHashCodeDao.saveByCollection(salesLitHashCodCollList);
				salesLitaeralHashCodeDaoForDML.saveByCollection(salesLitHashCodCollList);

				generatedUniqStringSet.clear();
				salesLitHashCodCollList.clear();

			}


			/*******************/


			//int updatedCount = salesLitaeralHashCodeDao.updateCurrentFileFlag(users.getUserId());
			int updatedCount = salesLitaeralHashCodeDaoForDML.updateCurrentFileFlag(users.getUserId());

			//			logger.debug("updatedCount="+updatedCount);

			
			msgStuff.append("\n Total  records : ");
			msgStuff.append(""+ totalCountInCSV);
			msgStuff.append("\n ignored(which already exists ) : ");
			msgStuff.append(existedRecordCount);
			msgStuff.append("\n Newly added  records : ");
			msgStuff.append(newEntryRecords);
			msgStuff.append("\n invalid  records : ");
			msgStuff.append(invalidCount);


			/*String messagesString = totalCountInCSV  +" Total records. and "+newEntryRecords +" added, "+existedRecordCount+
									" invalid(which already exists ) from "+destFile.getName()+" file.";*/

			//"From "+destFile.getName()+ ":: "+totalRecordsInCsv "records existed"+newEntryRecords+" fields are saved sucessfully remeaining fields "+existedRecordCount+" already existed..";
			Messages messages = new Messages("Sales files loaded", "Uploaded sales file successfully.", msgStuff.toString(), Calendar.getInstance(), 
					"Inbox",false ,"Info", users);
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);


		}  catch (Exception e) {
			logger.error("Exception ::::" , e);
		}

	}





	private RetailProSalesCSV setSalesGenralFields(RetailProSalesCSV retailProSales, String[] nextLineStr, int idxCustomerID , int idxExternalID,
			int idxReceiptNumber ,int idxSaleDate, int idxQty ,int idxSalePrice, int idxTax ,int idxPromoCode ,int idxStoreNumber,int idxSBSNumber,
			int idxSKU ,int idxTenderType,int idxItemSid, int idxDocSid,int idxDiscount ,Hashtable UdfHashtable ,List<POSMapping> salesPOSMappingList,Users user,String salesDateFormat) throws Exception {



		String tempStr  = null;

		//set the customerId
		if(idxCustomerID > -1 && idxCustomerID < nextLineStr.length && (tempStr = nextLineStr[idxCustomerID].trim()).length() !=0){
			retailProSales.setCustomerId(tempStr);
		}

		//set the external Id
		if(idxExternalID > -1 && idxExternalID < nextLineStr.length && (tempStr = nextLineStr[idxExternalID].trim()).length() !=0){
			try{
				retailProSales.setExternalId(Long.parseLong(tempStr));
			}catch (NumberFormatException e) {

				if(logger.isInfoEnabled()) logger.debug("external ID  parsing exception ",e);
			}
		}


		//set the Reciept num
		if(idxReceiptNumber > -1 && idxReceiptNumber < nextLineStr.length && (tempStr = nextLineStr[idxReceiptNumber].trim()).length() !=0){
			retailProSales.setRecieptNumber(tempStr);
		}


		//set the Sales date 
		if(idxSaleDate > -1 && idxSaleDate < nextLineStr.length && (tempStr = nextLineStr[idxSaleDate].trim()).length() !=0) {
			String newDate=tempStr.replaceAll("\\s*:\\s*",":");
			logger.debug("newDate ===========::"+newDate);
			try {
				//				logger.debug("salesDateFormat ::"+salesDateFormat);
				//				logger.debug("tempStr ::"+salesDateFormat);

				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(salesDateFormat);
				//date = (Date)formatter.parse(tempStr);
				date = (Date)formatter.parse(newDate);
				logger.debug("date================== ::"+date);
				Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(salesDateFormat));
				cal.setTime(date);
				retailProSales.setSalesDate(cal);

			} catch (Exception e) {
				//				logger.debug("POS Mapping  sales dateForamt doesnt Match .. so we ignore the data...");
				logger.error("Exception ::::" , e);
			}
		}

		//set the Qty
		if(idxQty > -1 && idxQty < nextLineStr.length && (tempStr = nextLineStr[idxQty].trim()).length() !=0 ){

			try {
				retailProSales.setQuantity(Double.parseDouble(tempStr));
			} catch (Exception e) {
				logger.debug("Quantity  parsing exception ",e);
				//				logger.error("Exception ::::" , e);
			}
		}

		//set the SalesPrice
		if(idxSalePrice > -1 && idxSalePrice < nextLineStr.length && (tempStr = nextLineStr[idxSalePrice].trim()).length() !=0 ){
			try {
				retailProSales.setSalesPrice(Double.parseDouble(tempStr));
			} catch (Exception e) {
				logger.debug("SalesPrice  parsing exception ",e);
			}
		}

		//set the Tax
		if(idxTax > -1 && idxTax < nextLineStr.length && (tempStr = nextLineStr[idxTax].trim()).length() !=0  ){
			retailProSales.setTax(Double.parseDouble(tempStr));
		}

		//set the Promocode
		if(idxPromoCode > -1 && idxPromoCode < nextLineStr.length && (tempStr = nextLineStr[idxPromoCode].trim()).length() !=0 ){
			retailProSales.setPromoCode(tempStr);
		}
		//set the StoreNum
		if(idxStoreNumber > -1 && idxStoreNumber < nextLineStr.length && (tempStr = nextLineStr[idxStoreNumber].trim()).length() !=0 ){
			retailProSales.setStoreNumber(tempStr);
		}
		if(idxSBSNumber > -1 && idxSBSNumber < nextLineStr.length && (tempStr = nextLineStr[idxSBSNumber].trim()).length() !=0 ){
			retailProSales.setSubsidiaryNumber(tempStr);
		}
		//set the SKU
		if(idxSKU > -1 && idxSKU < nextLineStr.length && (tempStr = nextLineStr[idxSKU].trim()).length() !=0){
			retailProSales.setSku(tempStr);
		}

		//set the TenderType
		if(idxTenderType > -1 && idxTenderType < nextLineStr.length && (tempStr = nextLineStr[idxTenderType].trim()).length() !=0){
			retailProSales.setTenderType(tempStr);
		}

		//Set Item Sid 
		if(idxItemSid > -1 && idxItemSid < nextLineStr.length && (tempStr = nextLineStr[idxItemSid].trim()).length() !=0){
			retailProSales.setItemSid(tempStr);
		}

		//Set Doc Sid 
		if(idxDocSid > -1 && idxDocSid < nextLineStr.length && (tempStr = nextLineStr[idxDocSid].trim()).length() !=0){
			retailProSales.setDocSid(tempStr);
		}
        
		//set the Discount
		if(idxDiscount > -1 && idxDiscount < nextLineStr.length && (tempStr = nextLineStr[idxDiscount].trim()).length() !=0  ){
			logger.info("setting discount>>>>>>discount==nextLineStr[idxDiscount].trim()"+nextLineStr[idxDiscount].trim());
			logger.info("setting discount>>>>>>Double.parseDouble(tempStr)=="+Double.parseDouble(tempStr));
			
			retailProSales.setDiscount(Double.parseDouble(tempStr));
		}else if(idxDiscount > -1 && idxDiscount < nextLineStr.length && (tempStr = nextLineStr[idxDiscount].trim()).length() ==0  ){
			retailProSales.setDiscount(0.0);
		}else if(idxDiscount == -1){ // if discount is not mapped on pos settings screen
			retailProSales.setDiscount(0.0);
		}
		//set user Id
		retailProSales.setUserId(user.getUserId());



		//set the customField data 
		//		logger.debug("befor setting the object the UdfHashtable.size() is"+UdfHashtable.size());
		if(UdfHashtable.size() >0 ) {

			String udfDataStr = null;
			String dateFormat = "";
			String dataTypeStr = "";
			for (POSMapping posMapping : salesPOSMappingList) {

				String custFieldName  = posMapping.getCustomFieldName();

				//logger.debug("UdfHashtable is::"+UdfHashtable +"and custField name is ::"+custFieldName);

				//logger.debug("UDF hash table exist the Key is"+UdfHashtable.contains(custFieldName));
				if(UdfHashtable.containsKey(custFieldName)) {
					int udfIdx =(Integer)UdfHashtable.get(custFieldName);

					//					logger.debug("UDF Idx is ::"+udfIdx);

					if(udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() !=0) {
						dataTypeStr = posMapping.getDataType().trim();
						//if UDF data is Date Type 
						if(posMapping.getDataType().trim().startsWith("Date")) {

							try {
								dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
								//							salesdateFormat = salesdateFormat.substring(salesdateFormat.indexOf("(")+1, salesdateFormat.indexOf(")"));

								if(!CustomFieldValidator.validateDate(udfDataStr, dateFormat)) {
									continue;
								}

								DateFormat formatter ; 
								Date date ; 
								formatter = new SimpleDateFormat(dateFormat);
								date = (Date)formatter.parse(udfDataStr); 
								Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
								cal.setTime(date);
								udfDataStr = MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::::" , e);
							}
						}
						if(posMapping.getDataType().trim().startsWith("Number"))
							udfDataStr = Utility.validateNumberValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);
						if(posMapping.getDataType().trim().startsWith("Double"))
							udfDataStr = Utility.validateDoubleValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);

						int UDfIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
						//						logger.debug("UDfIdx is ::"+UDfIdx +":: and udfData is ::"+udfDataStr);

						try {
							if(udfDataStr.length() == 0) {
								continue;
							} 
							retailProSales = setSalesCustFielddata(retailProSales, UDfIdx, udfDataStr);
						} catch (Exception e) {
							logger.error("Exception ::::" , e);
							if(logger.isInfoEnabled()) logger.debug("Exception :: error while setting the sales UDF field data..due to wrong values existed from the sales csv" +
									" file.so we ignoe the udf data.. ");
							continue;
						}
					}

				}
			} //for
		} // if

		return retailProSales;
	}



	private RetailProSalesCSV setSalesCustFielddata ( RetailProSalesCSV retailProSales , int index, String udfData) throws Exception {

		switch(index){
		case 1: retailProSales.setUdf1(udfData); return retailProSales;
		case 2: retailProSales.setUdf2(udfData); return retailProSales;
		case 3: retailProSales.setUdf3(udfData); return retailProSales;
		case 4: retailProSales.setUdf4(udfData); return retailProSales;
		case 5: retailProSales.setUdf5(udfData); return retailProSales;
		case 6: retailProSales.setUdf6(udfData); return retailProSales;
		case 7: retailProSales.setUdf7(udfData); return retailProSales;
		case 8: retailProSales.setUdf8(udfData); return retailProSales;
		case 9: retailProSales.setUdf9(udfData); return retailProSales;
		case 10: retailProSales.setUdf10(udfData); return retailProSales;
		case 11: retailProSales.setUdf11(udfData); return retailProSales;
		case 12: retailProSales.setUdf12(udfData); return retailProSales;
		case 13: retailProSales.setUdf13(udfData); return retailProSales;
		case 14: retailProSales.setUdf14(udfData); return retailProSales;
		case 15: retailProSales.setUdf15(udfData); return retailProSales;
		}
		return retailProSales;

	}



	private void skuFileUpload(Users users, MailingList mList) {
		try {
			
			String type = Constants.SKU_FILE_UPLOAD_FAILED;
						
			TreeMap<String, List<String>> prioMap =null;
			prioMap = new TreeMap<String, List<String>>();

			List<POSMapping> SKUPOSMappingList = null;

			SKUPOSMappingList = posMappingDao.findPOSMappingListByStr("SKU", users.getUserId());

			if(SKUPOSMappingList == null || SKUPOSMappingList.size() == 0) {
				//logger.debug("no SKU type POS custField Mapping list from the DB for this userName"+users.getUserName());
				return;
			}



		//	String source = userParentDir + "/" + users.getUserName()  + "/POSList/sourceFiles/skuFiles";
			String source = userParentDir + "/POSList/sourceFiles/skuFiles";
			logger.info("source is ::"+source);
			File sorceFiles = new File(source);
			//			logger.debug("source is ::"+source +" ::sorceFiles is exist "+ sorceFiles.exists());

			if(!sorceFiles.exists()) {
				//				logger.debug("Exception : No source Directory exist ***");
				sorceFiles.mkdir();
			}

			File[] listFiles = sorceFiles.listFiles();

			if(listFiles == null || listFiles.length <= 0) {

				//logger.debug("NO source files exists...");
				return;
			}



			skuFileDao = (SkuFileDao)context.getBean("skuFileDao");
			skuFileDaoForDML = (SkuFileDaoForDML)context.getBean("skuFileDaoForDML");

			//String destSkuPath   = userParentDir+"/"+users.getUserName()+"/POSList/processedFiles/SkuFiles/";
			String destSkuPath   = userParentDir+"/POSList/processedFiles/SkuFiles/";
			File destSkuDir = new File(destSkuPath);
			if(!destSkuDir.exists()) {

				destSkuDir.mkdir();
			}

			File sourceFile = null;
			Calendar folderCal = Calendar.getInstance();
			String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String yearStr = sdf.format(folderCal.getTime());

			for (int i = 0; i < listFiles.length; i++) {

				try {
					if(listFiles[i].canRead() == false) continue;



					//logger.debug("sku file upload method is called... and sourcepath is"+listFiles[i].getAbsolutePath());

					sourceFile =listFiles[i];

					if(sourceFile.getName().contains("_POSINCMPLETEPART") || sourceFile.getName().endsWith(".MARKEDINCOMPLETE")) {
						if(logger.isDebugEnabled()) logger.debug(sourceFile.getName()+ " ::  is inComplete File .." );
						continue;
					}

					//String destPath   = userParentDir+"/"+users.getUserName()+"/POSList/processedFiles/SkuFiles/"+MonthStr+yearStr;
					String destPath   = userParentDir+"/POSList/processedFiles/SkuFiles/"+MonthStr+yearStr;

					File destDir = new File(destPath);

					//logger.debug("destFile is exist..."+destDir.exists() +"::destFile name is>>"+destDir.getName());


					File destFile = new File(destPath + "/" + sourceFile.getName());

					if(!destDir.exists()) {

						//						logger.debug("directory not exist create new ...");
						destDir.mkdir();
					} else {

						if(destFile.exists()) {
							//							logger.debug("source file already exists...");

							//File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles");
							File unProcessedFileDir = new File(userParentDir+ "/POSList/unProcessFiles");

							if(!unProcessedFileDir.exists()) {

								unProcessedFileDir.mkdir();
							}
							//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles/"+sourceFile.getName());
							File unProcessFile = new File(userParentDir+ "/POSList/unProcessFiles/"+sourceFile.getName());


							sourceFile.renameTo(unProcessFile);


							String messagesString = ""+destFile.getName() +" file already exists..";
							Messages messages = new Messages("SKU file failed", "SKU file upload failed", messagesString, Calendar.getInstance(), 
									"Inbox",false ,"Info", users);
							//messagesDao.saveOrUpdate(messages);
							messagesDaoForDML.saveOrUpdate(messages);
							
							/**Alert mail if file is not proccessed
							 */
							saveInEmailqueuse(type, users,sourceFile.getName());
							
							logger.info("sku alert mail is saved on email queue");
														
							continue;

						}
					}
					sourceFile.renameTo(destFile);
					if(logger.isDebugEnabled()) logger.debug(">>>> "+destFile.getName()+" Sku file processing started");
					/*try {
						//copying the sales file from source to destination
//					FileUtils.copyFile(sourceFile,destFile);
					} catch (Exception e) {
						//moving in to the unprocessed files
						logger.error("Exception ::::" , e);
						logger.debug("exception: error occured from the copying file so moved this file into unProcessed Dir");

						File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/unProcessFiles");

						if(!unProcessedFileDir.exists()) {

							unProcessedFileDir.mkdir();
						}
						File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/unProcessFiles/"+sourceFile.getName());


						sourceFile.renameTo(unProcessFile);
						//FileUtils.copyFile(sourceFile ,unProcessedFile);	
						continue;
					}*/

					int idxSBSNum = -1;
					int idxStoreNum = -1; 
					int idxSKU = -1;
					int idxDescription = -1;
					int idxListPrice = -1;
					int idxItemCategory = -1,idxItemSid= -1,idxDCS=-1,idxVcode= -1,idxDCode =-1,idxCCode =-1,idxSCode =-1;
					//					int idxTax = 0;
					//					Map<String, Object> skuHashMap = new HashMap<String, Object>();
					Hashtable udfHashtable = new Hashtable();
					//					List<SkuFile> skuFileList = new ArrayList<SkuFile>();
					List<String> skuFieldList = new ArrayList<String>();

					//					int savingCount = 1;
					int existedRecords = 0;
					int newRecords	= 0;
					int invalidCount = 0;

					//					char delimiter = ',';
					String[] nextLineStr ;

					FileReader fileReader = new FileReader(destFile);
					BufferedReader br = new BufferedReader(fileReader);

					String lineStr=null;
					String tempStr;

					//identify the headers from file
					while((lineStr = br.readLine())!= null) {
						if(lineStr.trim().length()==0) continue;
						nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);


						if( nextLineStr.length == 0) {	continue;	}


						for(POSMapping posMapping : SKUPOSMappingList) {

							for(int j=0; j<nextLineStr.length ;j++) {

								tempStr = nextLineStr[j].trim();
								if(skuFieldList.contains(posMapping.getPosAttribute())) {
									//logger.debug("SKU Field already mapped with the general fields..");
									continue;
								}

								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Store Number") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxStoreNum IDx is ::"+j );
									idxStoreNum =  j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Subsidiary Number") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxStoreNum IDx is ::"+j );
									idxSBSNum =  j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 

								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("SKU") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxSKU IDx is ::"+j );
									idxSKU =  j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Description") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxDescription is ::"+j );
									idxDescription = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("List Price") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxListPrice is ::"+j );
									idxListPrice = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								} 
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Item Category") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									//										//logger.debug("tempStr is ::"+tempStr +">> idxItemCategory is ::"+j );
									idxItemCategory = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Item Sid") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxItemSid = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;


								}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Vendor") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxVcode = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Department") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxDCode = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Class") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxCCode = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Subclass") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxSCode = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("DCS") 
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
									idxDCS = j;
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}//idxDCS=-1,idxVcode= -1,idxDCode =-1,idxCCode =-1,idxSCode =-1;
								else if(!(skuFieldList.contains(tempStr)) && posMapping.getCustomFieldName().startsWith("UDF")
										&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr) ) {	
									//										logger.debug("tempStr is ::"+tempStr +">> file IDx is ::"+j +" >> CustFieldName is :: " +posMapping.getCustomFieldName() +" >>posMapping.getPosAttribute() is ::"+posMapping.getPosAttribute());
									udfHashtable.put(posMapping.getCustomFieldName(), j);
									skuFieldList.add(tempStr);
									storePriorityVal(j, posMapping, prioMap);
									continue;
								}

							}	
						} // for

						break;

					}	// while

					//					logger.debug("SKU hash table size is "+udfHashtable.size());

					int totalCount =0;

					//identify the data
					while((lineStr = br.readLine()) != null) {		
						if(lineStr.trim().length()==0) continue;
						nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);

						totalCount++;

						if( nextLineStr.length == 0) {
							continue;
						}

						if(prioMap != null) {
							/*boolean isValidEntryFlag = false;

							logger.debug("inValidEntryFlag is ::"+isValidEntryFlag);*/

							if(isValidEntryFromLineStr(prioMap , nextLineStr) == false) {
								invalidCount++;
								continue;
							}
						}


						//						logger.debug("length is >>>>"+nextLineStr.length);

						//RetailProSalesCSV retailProSales = new RetailProSalesCSV();
						SkuFile skuFileObj = null;





						if(!prioMap.isEmpty()) {
							skuFileObj = skuFileDao.findSKUByPriority(prioMap, nextLineStr, users.getUserId());
						}

						if(skuFileObj != null) {

							try {

								//update the record 
								skuFileObj=  setSkuFieldsData(nextLineStr,skuFileObj , idxStoreNum ,idxSBSNum,idxSKU ,idxDescription, idxListPrice,
										idxItemCategory,idxItemSid , idxDCS, idxVcode,idxDCode ,idxCCode ,idxSCode ,
										udfHashtable ,SKUPOSMappingList, invalidCount, users);
							} catch (Exception e) {
								logger.error("Exception ::::" , e);
								if(logger.isDebugEnabled()) logger.debug("while updating the old record is not updated due to wrong value existed from the csv file..so we ignore the record");
								invalidCount++;
								continue;
							}

							//skuFileDao.saveOrUpdate(skuFileObj);
							skuFileDaoForDML.saveOrUpdate(skuFileObj);
							existedRecords++;
							continue;
						}
						else {
							skuFileObj = new SkuFile();

							try {
								skuFileObj =  setSkuFieldsData(nextLineStr,skuFileObj , idxStoreNum ,idxSBSNum,idxSKU ,idxDescription, idxListPrice,
										idxItemCategory, idxItemSid, idxDCS, idxVcode, idxDCode, idxCCode, idxSCode ,
										udfHashtable ,SKUPOSMappingList, invalidCount, users);
								//skuFileDao.saveOrUpdate(skuFileObj);
								skuFileDaoForDML.saveOrUpdate(skuFileObj);
								newRecords++;
							} catch (Exception e) {
								logger.error("Exception ::::" , e);
								if(logger.isDebugEnabled()) logger.debug("Exception : error getting while inserting the sku record due to wrong value existed  from the csv file..so we ignore the record");
								invalidCount++;
								continue;
							}


						}

					} //while

					br.close();
					fileReader.close();
					//System.gc();




					//getMastToTransMappings(mList,users.getUserId());
					if(logger.isDebugEnabled()) logger.debug( ">>>> "+destFile.getName()+" Sku file processing is compleated");
					StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
					msgStuff.append(destFile.getName());
					msgStuff.append("\n Total  records : ");
					msgStuff.append(""+ totalCount);
					msgStuff.append("\n Invalid records : ");
					msgStuff.append(invalidCount);
					msgStuff.append("\n Updated  records : ");
					msgStuff.append(existedRecords);
					msgStuff.append("\n Newly added  records : ");
					msgStuff.append(newRecords);
					/*String messagesString = totalCount +"total records and "+newRecords +
											" added,"+existedRecords+" updated,"+ invalidCount+" invalid from"+destFile.getName()+ "file.";*/

					Messages  messages = new Messages("SKU file loaded", "Uploaded SKU file successfully.", msgStuff.toString(), Calendar.getInstance(), 
							"Inbox",false ,"Info", users);
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);

				} catch (Exception e) {
					logger.error("Exception ::::" , e);
					continue;
				}

			} //for	
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
	} // skuFileUpload


	/********set the Sku genralFields*********/
	private SkuFile setSkuFieldsData(String[] nextLineStr, SkuFile skuFile , int idxStoreNum , int idxSBSNum ,int idxSKU ,int idxDescription, int idxListPrice,
			int idxItemCategory,int idxItemSid,int idxDCS, int idxVcode, int idxDCode ,int idxCCode ,int idxSCode ,
			Hashtable udfHashtable ,List<POSMapping> SKUPOSMappingList,  int invalidCount,Users user) throws Exception {

		String tempStr = null;

		//set the StoreNum
		if(idxStoreNum > -1 && idxStoreNum < nextLineStr.length && (tempStr = nextLineStr[idxStoreNum].trim()).length() !=0 ){
			skuFile.setStoreNumber(tempStr);
		}
		if(idxSBSNum > -1 && idxSBSNum < nextLineStr.length && (tempStr = nextLineStr[idxSBSNum].trim()).length() !=0 ){
			skuFile.setSubsidiaryNumber(tempStr);
		}
		//set the SKU 
		if(idxSKU > -1 &&  idxSKU < nextLineStr.length && (tempStr = nextLineStr[idxSKU].trim()).length() !=0 ){
			skuFile.setSku(tempStr);
		}

		//set the Description
		//		logger.debug("Description value is  ::"+tempStr);
		if(idxDescription > -1 && idxDescription < nextLineStr.length && (tempStr = nextLineStr[idxDescription].trim()).length() !=0){
			skuFile.setDescription(tempStr);
		}

		//set the ListPrice
		if(idxListPrice > -1 && idxListPrice < nextLineStr.length && (tempStr = nextLineStr[idxListPrice].trim()).length() !=0){
			try {
				skuFile.setListPrice(Double.parseDouble(tempStr));
			} catch (Exception e) {
				if(logger.isDebugEnabled()) logger.debug("Double parse exception",e);
			}
		}

		//set the ItemCategory
		//		logger.debug("idxItemCategory value is  ::"+idxItemCategory);

		//		logger.debug("idxItemCategory tempStr value is  ::"+tempStr);
		if(idxItemCategory > -1 && idxItemCategory < nextLineStr.length && (tempStr = nextLineStr[idxItemCategory].trim()).length() !=0){
			skuFile.setItemCategory(tempStr);
		}

		//Set Item Sid 
		if(idxItemSid > -1 && idxItemSid < nextLineStr.length && (tempStr = nextLineStr[idxItemSid].trim()).length() !=0){
			skuFile.setItemSid(tempStr);
		}

		// set DCS
		if(idxDCS > -1 && idxDCS < nextLineStr.length && (tempStr = nextLineStr[idxDCS].trim()).length() !=0){
			skuFile.setDCS(tempStr);
		}

		//Set V Code
		if(idxVcode > -1 && idxVcode < nextLineStr.length && (tempStr = nextLineStr[idxVcode].trim()).length() !=0){
			skuFile.setVendorCode(tempStr);
		}

		//set D Code
		if(idxDCode > -1 && idxDCode < nextLineStr.length && (tempStr = nextLineStr[idxDCode].trim()).length() !=0){
			skuFile.setDepartmentCode(tempStr);
		}


		//Set C Code
		if(idxCCode > -1 && idxCCode < nextLineStr.length && (tempStr = nextLineStr[idxCCode].trim()).length() !=0){
			skuFile.setClassCode(tempStr);
		}

		//Set S_Code
		if(idxSCode > -1 && idxSCode < nextLineStr.length && (tempStr = nextLineStr[idxSCode].trim()).length() !=0){
			skuFile.setSubClassCode(tempStr);
		}

		//set UserId
		skuFile.setUserId(user.getUserId());

		//set the customField data

		if(udfHashtable.size() >0 ) {
			String udfDataStr = null;
			String dateFormat ="";
			String dataTypeStr = "";
			for (POSMapping posMapping : SKUPOSMappingList) {
				String custFieldName  = posMapping.getCustomFieldName();

				if(udfHashtable.containsKey(custFieldName)) {
					int udfIdx =(Integer)udfHashtable.get(custFieldName);

					if(udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() !=0) {
						dataTypeStr = posMapping.getDataType().trim();
						//if UDF data is Date Type 
						if(posMapping.getDataType().trim().startsWith("Date")) {
							try {
								dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));


								if(!CustomFieldValidator.validateDate(udfDataStr, dateFormat)) {
									continue;
								}

								DateFormat formatter ; 
								Date udfdDate ; 
								formatter = new SimpleDateFormat(dateFormat);
								udfdDate = (Date)formatter.parse(udfDataStr); 
								Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
								cal.setTime(udfdDate);
								udfDataStr = MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::::" , e);
							}


						}
						if(posMapping.getDataType().trim().startsWith("Number"))
							udfDataStr = Utility.validateNumberValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);
						if(posMapping.getDataType().trim().startsWith("Double"))
							udfDataStr = Utility.validateDoubleValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);

						int UDFIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
						try {
							skuFile = setSKUCustFielddata(skuFile, UDFIdx, udfDataStr);
						} catch (Exception e) {
							logger.error("Exception ::::" , e);
							if(logger.isErrorEnabled()) logger.error("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
						}
					}
				}
			} //for
		} // if

		return skuFile;
	} // setSkuFieldsData


	/******set the sku custom fields for that object********/
	private SkuFile setSKUCustFielddata ( SkuFile skuFile , int index, String udfData) throws Exception {


		switch(index){
		case 1: skuFile.setUdf1(udfData); return skuFile ;
		case 2: skuFile.setUdf2(udfData); return skuFile ;
		case 3: skuFile.setUdf3(udfData); return skuFile ;
		case 4: skuFile.setUdf4(udfData); return skuFile ;
		case 5: skuFile.setUdf5(udfData); return skuFile ;
		case 6: skuFile.setUdf6(udfData); return skuFile ;
		case 7: skuFile.setUdf7(udfData); return skuFile ;
		case 8: skuFile.setUdf8(udfData); return skuFile ;
		case 9: skuFile.setUdf9(udfData); return skuFile ;
		case 10: skuFile.setUdf10(udfData); return skuFile ;
		case 11: skuFile.setUdf11(udfData); return skuFile ;
		case 12: skuFile.setUdf12(udfData); return skuFile ;
		case 13: skuFile.setUdf13(udfData); return skuFile ;
		case 14: skuFile.setUdf14(udfData); return skuFile ;
		case 15: skuFile.setUdf15(udfData); return skuFile ;

		}
		return skuFile;

	}





	private void storePriorityVal(int valIndex, POSMapping posMapping, TreeMap<String, List<String>> prioMap) {


		//		logger.debug(">>>> "+posMapping.getUniquePriority());
		if(posMapping.getUniquePriority()==null) return;

		String key = posMapping.getUniquePriority().toString();

		List<String> valList = prioMap.get(key);

		if(valList==null) valList = new ArrayList<String>();
		String newVal="";

		if(posMapping.getCustomFieldName().toLowerCase().startsWith("udf")) {
			newVal = posMapping.getCustomFieldName().toLowerCase();
		}
		else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
			newVal = genFieldContMap.get(posMapping.getCustomFieldName());
		}
		else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
			newVal = genFieldSalesMap.get(posMapping.getCustomFieldName());
		}
		else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
			newVal = genFieldSKUMap.get(posMapping.getCustomFieldName());
		}
		else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_HOMES_PASSED)) {
			newVal = genFieldHomesPassedMap.get(posMapping.getCustomFieldName());
		}

		String dataTypeStr = posMapping.getDataType().trim().toLowerCase();
		if(dataTypeStr.startsWith("date")|| dataTypeStr.startsWith("string")) {
			dataTypeStr = "string"; 
		}
		//		logger.debug("...dataTypeStr>>"+dataTypeStr);

		String tempVal = valIndex + "|" + newVal+ "|" +dataTypeStr;
		if(!valList.contains(tempVal)) {
			valList.add(tempVal);
		}

		prioMap.put(key, valList);

	} // 
	private void resetFields() {
		smsSettings = null;
		//userSMSGateway = null;
		ocsmsGateway = null;

	}


	private void contactsFileUpload(Users users, MailingList mList) {
		logger.info("Started Contacts file processing............");
		
		try {
			
			
			String type = Constants.CONTACTS_FILE_UPLOAD_FAILED;
			
			 optinMobileSet = new HashSet<String>();
			 
			 try {
				resetFields();
				 if(users.isEnableSMS() && users.isConsiderSMSSettings()){
					 
					if(SMSStatusCodes.smsProgramlookupOverUserMap.get(users.getCountryType())) smsSettings = smsSettingsDao.findByUser(users.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
					else  smsSettings = smsSettingsDao.findByOrg(users.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
					
					try {
						if(smsSettings != null) ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(users.getCountryType()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
					}
					  
				 }
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e1);
			}
			 
			TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();

			//String source = userParentDir+"/" + users.getUserName() +"/POSList/sourceFiles/contactFiles";  //need to change from here source path
			String source = userParentDir+ "/POSList/sourceFiles/contactFiles";  //need to change from here source path
			
			logger.info("sourcesourcesourcesource"+source);
			File sorceFiles = new File(source);
			
//			logger.debug("source is ::"+source +" ::sorceFiles is exist "+ sorceFiles.exists());
			
			if(!sorceFiles.exists()) {
//				logger.debug("*** Exception : contacts Source file "+ sorceFiles.getName() + " does not exist ***");
				sorceFiles.mkdir();
			}
			
			File[] listFiles = sorceFiles.listFiles();
			if(listFiles == null || listFiles.length <= 0) {
				
//				logger.debug("NO source files exists...");
				return;
			}
			
			
			//String destContactPath   = userParentDir+"/" + users.getUserName() +"/POSList/processedFiles/contactFiles/";
			String destContactPath   = userParentDir+ "/POSList/processedFiles/contactFiles/";
			logger.info("destContactPathdestContactPathdestContactPath"+destContactPath);
			File destContactDir = new File(destContactPath);
			
			if(!destContactDir.exists()) {
				destContactDir.mkdir();
			}
			
			
			
			//logger.debug("mailing lis id is :: "+mList.getListId());
			
			
			List<POSMapping> posMappContactLst = null;
			posMappContactLst = posMappingDao.findPOSMappingListByStr("Contacts", users.getUserId());
			
			
			
			
			if(posMappContactLst == null || posMappContactLst.size() == 0) {
				
				//logger.debug("no data exists contact type mapping from the POSMapping table for this User "+ users.getUserId());
				// Get only general Fields data
					return;
			}
			
			
			PurgeList purgeList = (PurgeList)context.getBean("purgeList");
			
			Calendar folderCal = Calendar.getInstance();
			String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String yearStr = sdf.format(folderCal.getTime());
			
			// PREPARE Ml Structure
			for (int i = 0; i < listFiles.length; i++) {
				
				try {
					if(listFiles[i].canRead() == false) { 
						if(logger.isDebugEnabled()) logger.debug("Unable to read the file : "+ listFiles[i].getName());
						continue;
					}	
					
					File sourceFile =listFiles[i];
					
					String sourceFileName = sourceFile.getName();
					if(sourceFile.getName().contains("_POSINCMPLETEPART") || sourceFile.getName().endsWith(".MARKEDINCOMPLETE")) {
						if(logger.isDebugEnabled()) logger.debug(sourceFile.getName()+ " ::  is inComplete File .." );
						continue;
					}

					//String destPath   = userParentDir+"/" + users.getUserName() +"/POSList/processedFiles/contactFiles/"+MonthStr+yearStr;
					String destPath   = userParentDir+ "/POSList/processedFiles/contactFiles/"+MonthStr+yearStr;
					
					File destDir = new File(destPath);
					//logger.debug("destFile is exist..."+destDir.exists() +"::destFile name is>>"+destDir.getName());
					
					File destFile = new File(destPath + "/" + sourceFile.getName());
					
					//logger.debug("Contact DestFile = "+destFile.getAbsolutePath());
					if(!destDir.exists()) {
						
						//logger.debug("directory not exist create new ...");
						destDir.mkdir();
					} 
						
					
					if(destFile.exists()) {
//						logger.debug("source file already exists...");
						
						//File unProcessedFileDir = new File(userParentDir+"/"+users.getUserName()+"/unProcessFiles");
						File unProcessedFileDir = new File(userParentDir+"/unProcessFiles");
						if(!unProcessedFileDir.exists()) {
							
							unProcessedFileDir.mkdir();
						}
						//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/unProcessFiles/"+sourceFile.getName());
						File unProcessFile = new File(userParentDir+ "/unProcessFiles/"+sourceFile.getName());
						sourceFile.renameTo(unProcessFile);
						
						Messages messages = new Messages("Contacts", "Contacts file upload failed", "File  already exists",Calendar.getInstance(), 
								"Inbox",false ,"Info", users );
						//messagesDao.saveOrUpdate(messages);
						messagesDaoForDML.saveOrUpdate(messages);
						
						
						/**Alert mail if file is not proccessed
						 */
						saveInEmailqueuse(type, users,sourceFile.getName());
						
						logger.info("sales alert mail is saved on email queue");
						
						continue;
						
					}
					

					 logger.debug(sourceFileName+":: file processing is started..");
//					if(logger.isDebugEnabled()) logger.debug("**** Copying the file from source to destination...");
					
					try {
						
						sourceFile.renameTo(destFile);
						
					} catch (Exception e) {
						logger.error("Exception ::::" , e);
						
						
					}
					
					//find all the purchase triggers configured to this user.
					/*
					 * this code is added for new ET implementation.
					 * Acts like an AUTO-RESPONDER(trigger event only when it has occured but not like polling)
					 */
					
					Long startContactId = null;
					Long endContactId = null;
					List<EventTrigger> etList = eventTriggerDao.findAllUserAutoRespondTriggers(users.getUserId().longValue(), 
									(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED));
					if(logger.isInfoEnabled()) logger.info(" >>>>>>>>>>>>>>>etList is>>>>>>>>>>>>>"+etList);
					/*if(etList != null && etList.size() > 0) {
						
						isEnableEvent = true;
					}*/
					
					
					mobileContacts = 0; 
					noSMSComplaincyMsg = null;
					noSMSCredits = null;
					
//					char delimiter = ',';
					String[] lineStrTokens ;
					
					
					int customerIdIdx = -1, addressUntiIdIndx = -1, emailIdIdx = -1,mobileIdx = -1,firstNameIdx = -1;
					int lastNameIdx = -1,streetIdx = -1,addressTwoIdx=-1,cityIdx = -1,stateIdx=-1,zipIdx = -1;
					int countryIdx = -1,genderIdx = -1,birthDayIdx = -1,anniversaryIdx = -1, homeStoreIdx = -1,sbsIndex=-1,createdDateIdx=-1;
					
					String birthDayFormat = "", anniversaryFormat = "",createdDateFormat = "";
					
					List<String> contactHeadersList = new ArrayList<String>();
					Hashtable udfHashtable = new Hashtable();
					FileReader fileReader = new FileReader(destFile);
					BufferedReader br = new BufferedReader(fileReader);
					String csvColumnStr = "";
					String lineStr=null;
					
					
					//Getting the Headers Index
					while((lineStr = br.readLine())!= null) {		
						//lineStr += ",\"0\"";
						if(lineStr.trim().length()==0) continue;
						lineStrTokens = parse(lineStr);//  lineStr.split(csvDelemiterStr);
						
							if(lineStrTokens.length==0) { 
								continue;
							}	
						
							for(POSMapping posMapping : posMappContactLst) {
							
								for(int j=0; j< lineStrTokens.length ;j++) {
									
									csvColumnStr = lineStrTokens[j].trim();
									
									
									if(contactHeadersList.contains(csvColumnStr)) {
										//logger.debug("Contact Field already mapped with the general fields..");
										// already mapped field
										continue;
									}
									//Email,First Name,Last Name,Street,Address Two,City,State,Country,Pin,Mobile,CustomerID,Gender,BirthDay,Anniversary
									if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Customer ID") 
																					&& posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("customerIdIdx  is::"+csvColumnStr + ":: and index is ::"+j );
										customerIdIdx =  j;
										contactHeadersList.add(csvColumnStr);  
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Addressunit ID") 
											 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
										//logger.info("mobileIdx  is::"+csvColumnStr + ":: and index is ::"+j );
										addressUntiIdIndx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
										} 
									
									
									
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Mobile") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("mobileIdx  is::"+csvColumnStr + ":: and index is ::"+j );
										mobileIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Email")
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("Email  is::"+csvColumnStr + ":: and index is ::"+j );
										emailIdIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("First Name") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("firstName  is::"+csvColumnStr + ":: and index is ::"+j );
										firstNameIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Last Name") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("LastName  is::"+csvColumnStr + ":: and index is ::"+j );
										lastNameIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Street") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
										
//										logger.debug("Street  is::"+csvColumnStr + ":: and index is ::"+j );
										streetIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}
									//Changes 2.5.4.0
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Address Two") 
											 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {

										//logger.debug("Street  is::"+csvColumnStr + ":: and index is ::"+j );
										addressTwoIdx =  j;
										contactHeadersList.add(csvColumnStr);	
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}
									
									
									
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("City")	
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("City  is::"+csvColumnStr + ":: and index is ::"+j );
										cityIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("State") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("State  is::"+csvColumnStr + ":: and index is ::"+j );
										stateIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("ZIP") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("pin/Zip  is::"+csvColumnStr + ":: and index is ::"+j );
										zipIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Country") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("Country  is::"+csvColumnStr + ":: and index is ::"+j );
										countryIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									} 
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Gender") 
																						 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
//										logger.debug("Gender  is::"+csvColumnStr + ":: and index is ::"+j );
										genderIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("BirthDay") 
											 											 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
										if(posMapping.getDataType().trim().startsWith("Date")) {
								    		
								    		birthDayFormat = posMapping.getDataType();
								    		birthDayFormat = birthDayFormat.substring(birthDayFormat.indexOf("(")+1, birthDayFormat.indexOf(")"));
								    		
								    	}
										
										birthDayIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}
									else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Anniversary") 
 											  											 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
										
										if(posMapping.getDataType().trim().startsWith("Date")) {
								    		anniversaryFormat = posMapping.getDataType();
								    		anniversaryFormat = anniversaryFormat.substring(anniversaryFormat.indexOf("(")+1, anniversaryFormat.indexOf(")"));
								    		
								    	}
										anniversaryIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Created Date") 
  											 && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
										
										
										if(posMapping.getDataType().trim().startsWith("Date")) {//
								    		createdDateFormat = posMapping.getDataType();
								    		createdDateFormat = createdDateFormat.substring(createdDateFormat.indexOf("(")+1, createdDateFormat.indexOf(")"));
								    		
								    	}
										createdDateIdx =  j;
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
										continue;
									}else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Home Store") 
											 											  && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
											//logger.debug("Gender  is::"+csvColumnStr + ":: and index is ::"+j );
											homeStoreIdx =  j;
											contactHeadersList.add(csvColumnStr);
											storePriorityVal(j, posMapping, prioMap);
											continue;
									}else if(!(contactHeadersList.contains(csvColumnStr)) && posMapping.getCustomFieldName().equals("Subsidiary Number") 
											  && posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr)) {
											//logger.debug("Gender  is::"+csvColumnStr + ":: and index is ::"+j );
											sbsIndex =  j;
											contactHeadersList.add(csvColumnStr);
											storePriorityVal(j, posMapping, prioMap);
											continue;
									}
									
									//City,State,Country,ZIP,,Gender,BirthDay,Anniversary,
									else if(!(contactHeadersList.contains(csvColumnStr))  && posMapping.getCustomFieldName().startsWith("UDF") 
																						  &&  posMapping.getPosAttribute().trim().equalsIgnoreCase(csvColumnStr) ) {
										udfHashtable.put(posMapping.getCustomFieldName(), j);
										contactHeadersList.add(csvColumnStr);
										storePriorityVal(j, posMapping, prioMap);
//										//break;
										continue;
									}
											
								} // for j
							} // for posMapping
						
						break;
					} // while
					
					if(logger.isDebugEnabled()) logger.debug(" identify the headers index .... hashtable size is ::"+udfHashtable.size());
					
//					List<Contacts> contactObjList = new ArrayList<Contacts>();
//					Map<String,Object> conatctsMap = new HashMap<String, Object>();
					
					int totalCount = 0, newContactsCount = 0, updatedCount =  0 , inValidCount =  0;  
//					Map<String, Object> newContObjMap = new HashMap<String, Object>();
					
//					Long mobilePhoneLong = null;
					//identify the data		
					while((lineStr = br.readLine())!= null) {		
	//					lineStr += ",\"0\"";
						if(lineStr.trim().length()==0) continue;
						lineStrTokens = parse(lineStr);//  lineStr.split(csvDelemiterStr);
	
								//logger.debug("lineStr ::"+lineStr+"::: length="+ lineStrTokens.length);
								
								if( lineStrTokens.length == 0) {
									continue;
								}
								
								totalCount++;

								
								String emailStr = null;
								
								boolean isValidCont = false;
								
								//TODO change this code as per priority 
								if(customerIdIdx == -1 && emailIdIdx == -1 && mobileIdx == -1){
									inValidCount++;
									continue;
								}
								
								if(customerIdIdx != -1 && lineStrTokens[customerIdIdx].trim().length()  > 0) {
									isValidCont = true;
								}
								else if(emailIdIdx != -1 && lineStrTokens[emailIdIdx].trim().length()  > 0 &&
										Utility.validateEmail(lineStrTokens[emailIdIdx].trim())) {
									
										isValidCont = true;
										
									
								}else if(mobileIdx != -1 && lineStrTokens[mobileIdx].trim().length() > 0 &&
										(Utility.phoneParse(lineStrTokens[mobileIdx].trim() ,users.getUserOrganization()) != null)) {
									isValidCont = true;
								}
								
								if(!isValidCont) {
									inValidCount++;
									continue;
								}
								
							
								
								//check the contact if already exists
								//contactObj = contactsDao.findContactByValues(lineStrTokens[emailIdIdx].trim() , mobilePhoneLong , lineStrTokens[customerIdIdx].trim()  ,mList.getListId());
								//logger.debug("POSMAP=="+prioMap);
								boolean isEnableEvent = false;
								Contacts contactObj = null;
								
								Contacts inputCon = new Contacts();
								try {
									inputCon = contactObj = setContactFieldsData(lineStrTokens, contactObj ,customerIdIdx, addressUntiIdIndx, emailIdIdx, mobileIdx, firstNameIdx, lastNameIdx, streetIdx,addressTwoIdx,
											cityIdx, stateIdx, zipIdx, countryIdx, genderIdx, birthDayIdx, anniversaryIdx,homeStoreIdx ,sbsIndex,createdDateIdx,birthDayFormat,anniversaryFormat,createdDateFormat,udfHashtable, posMappContactLst);
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									logger.error("Exception ", e1);
								}
								
								
								if(!prioMap.isEmpty()) {
									//contactObj = contactsDao.findContactByPriority(prioMap,  lineStrTokens, mList.getListId());
									contactObj = contactsDao.findContactByPriority(prioMap,  lineStrTokens, users);
									
								}
								
//								long contactMlBits = 0l;
								if(contactObj != null) {
									
									
									try {
										if(emailIdIdx !=-1){
											emailStr = lineStrTokens[emailIdIdx].trim();
											//logger.debug(">>>>3 ::"+emailStr);
										
										if((emailStr != null && emailStr.trim().length() > 0) &&
												(contactObj.getEmailId() == null || 
												(contactObj.getEmailId()!=null && !(contactObj.getEmailId().equalsIgnoreCase(emailStr)))  ) ){
											contactObj.setPurged(false);
											contactObj.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
//											
										}
										else if((emailStr == null || emailStr.trim().length() == 0) &&
												contactObj.getEmailId() !=null) {
											//	logger.debug("contact.getEmailId() is null of contact Id is "+contact.getContactId());
												
												contactObj.setEmailId(null);
												contactObj.setPurged(true);
												contactObj.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
												
											}
										}
										if(contactObj.getOptinMedium() == null) {
											//set Optin Medium
											contactObj.setOptinMedium("POS");
											isEnableEvent = ((etList != null && etList.size() > 0) && true);
											
										}
										try {
											if(Utility.isModifiedContact(contactObj,inputCon ))
											{
												logger.info("entered Modified date");
												contactObj.setModifiedDate(Calendar.getInstance());
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											logger.error("Exception ", e);
										}	
										contactObj = setContactFieldsData(lineStrTokens, contactObj ,customerIdIdx, addressUntiIdIndx, emailIdIdx, mobileIdx, firstNameIdx, lastNameIdx, streetIdx,addressTwoIdx,
													cityIdx, stateIdx, zipIdx, countryIdx, genderIdx, birthDayIdx, anniversaryIdx,homeStoreIdx ,sbsIndex,createdDateIdx,birthDayFormat,anniversaryFormat,createdDateFormat,udfHashtable, posMappContactLst);
										
										
										contactObj.setUsers(users);
										//Set<MailingList> mlset = contactObj.getMlSet();
										if(contactObj.getMlBits().longValue() == 0l) {
											contactObj.setMlBits(mList.getMlBit());
										}
										else if((contactObj.getMlBits().longValue() & mList.getMlBit().longValue())==0){
											contactObj.setMlBits(contactObj.getMlBits().longValue() | mList.getMlBit().longValue());
										}
										
					
										//mlset.add(mList);
										//contactObj.setMlSet(mlset);
										
										if(contactObj.getMobilePhone() == null) {
											
											contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
										}
										contactsDaoForDML.saveOrUpdate(contactObj);
										
										updatedCount++;
										if(isEnableEvent) {
											if(startContactId == null) {
												startContactId = contactObj.getContactId();
											}
											endContactId = contactObj.getContactId();
										}
										
									} catch (Exception e) {
										logger.debug("Exception while updaing the contact..",e);
										inValidCount++;
										continue;
									}
									
								}
								else {
									
									try {
										
//										lineStrTokens[emailIdIdx].trim() , mobilePhoneLong , lineStrTokens[customerIdIdx].trim()  ,mList.getListId()
										
										Calendar date = Calendar.getInstance();
										contactObj = new Contacts(mList,date, Constants.CONT_STATUS_PURGE_PENDING);
										
										//set Optin Medium
										contactObj.setOptinMedium("POS");
										isEnableEvent = ((etList != null && etList.size() > 0) && true);
										contactObj.setUsers(users);
										
										contactObj = setContactFieldsData(lineStrTokens, contactObj ,customerIdIdx, addressUntiIdIndx, emailIdIdx, mobileIdx, firstNameIdx, lastNameIdx, streetIdx,addressTwoIdx,
												cityIdx, stateIdx, zipIdx, countryIdx, genderIdx, birthDayIdx, anniversaryIdx, homeStoreIdx ,sbsIndex,createdDateIdx, birthDayFormat,anniversaryFormat, createdDateFormat, udfHashtable, posMappContactLst);
									
										if(contactObj.getMobilePhone() == null) {
											
											contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
										}
										//Set the Optin
										contactObj.setOptin(new Byte((byte)0));
										//set optinInto (email/sms/both) here we set the both type
										contactObj.setOptedInto((byte)3);
										//set optin per typr (single/double optin type)
										contactObj.setOptinPerType((byte)2);
										
										// set Purge
										contactObj.setPurged(false);
										
										
										//Set<MailingList> mlset = contactObj.getMlSet();
										
										//mlset.add(mList);
										//contactObj.setMlSet(mlset);
										contactObj.setMlBits(mList.getMlBit());
										
										contactsDaoForDML.saveOrUpdate(contactObj);
										
										if(mList.isCheckWelcomeMsg() && !mList.getCheckDoubleOptin()) {
											
											sendWelcomeEmail(contactObj, mList, mList.getUsers());
										
										}
										
										newContactsCount++;
										if(isEnableEvent) {
											if(startContactId == null) {
												startContactId = contactObj.getContactId();
											}
											endContactId = contactObj.getContactId();
										}
										
									} catch (Exception e) {
										
										logger.debug("Exception while adding the new contact..",e);
										inValidCount++;
									}
								
								}
								
								//Check the Birthday is below 12 years for sending parental consent mail.. && POS list is Parental Consent
								/*if(contactObj.getEmailId() != null && contactObj.getMailingList().isCheckParentalConsent() && contactObj.getBirthDay() != null  &&
										((Calendar.getInstance().getTimeInMillis()-contactObj.getBirthDay().getTimeInMillis())/(1000 * 60*60*24)) <= (365*13) ) {
									performParentalConsent(contactObj);
									
								}*/
								
								
							} // while
					
					br.close();
					fileReader.close();
					//System.gc();
					
					
					/********** purging the Mailing list***/
					
					/*List<Long> list = new ArrayList<Long>();
					list.add(mList.getListId());*/
					purgeList.addForPurge(mList.getUsers().getUserId(), mList.getListId());
					
					//change the Last modified date on POS List
					mList.setLastModifiedDate(new Date());
					
					mList.setListSize(mList.getListSize()+ newContactsCount);
					
					mailingListDaoForDML.saveOrUpdate(mList);
					
					//getMastToTransMappings(mList,users.getUserId());
					
					if(logger.isDebugEnabled()) logger.debug(sourceFileName+":: file processing is completed..");
					
					StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
					msgStuff.append(sourceFileName);
					msgStuff.append("\n Total  contacts : ");
					msgStuff.append(""+ totalCount);
					msgStuff.append("\n Invalid  count : ");
					msgStuff.append(inValidCount);
					msgStuff.append("\n Updated count : ");
					msgStuff.append(updatedCount);
					msgStuff.append("\n Newly added  count : ");
					msgStuff.append(newContactsCount);
					
					//Send Message 
					/*String messagesString =""+ totalCount+" total  contacts ."+ inValidCount +": invalid and "+updatedCount +
											" :updated and  "+newContactsCount+" :added from "+sourceFileName+" file." ;*/
	
					Messages messages = new Messages("Contact" ,"Uploaded Successfully" ,msgStuff.toString() ,
												Calendar.getInstance(),"Inbox",false ,"Info", users); 
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
						
					
					if(noSMSComplaincyMsg != null) {
						messages = new Messages("Contact" ,"Mobile contacts may not be reachable" ,"Total mobile contacts:"+mobileContacts+noSMSComplaincyMsg ,
								Calendar.getInstance(),"Inbox",false ,"Info", users); 
						
						//messagesDao.saveOrUpdate(messages);
						messagesDaoForDML.saveOrUpdate(messages);

					}
					
					//to send the optin for multiple messages.....	
					if(optinMobileSet.size() > 0 && smsSettings != null && ocsmsGateway != null) {
						
						
						
						if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, optinMobileSet.size())) {
							
							if(logger.isDebugEnabled()) logger.debug("low credits with clickatell");
							optinMobileSet.clear();
							return ;
						}
						if( (  (users.getSmsCount() == null ? 0 : users.getSmsCount()) - (users.getUsedSmsCount() == null ? 0 : 
							users.getUsedSmsCount() ) ) >=  optinMobileSet.size()) {
							
//							logger.debug("=======Sending to multi threaded class======");
							captiwayToSMSApiGateway.sendMultipleMobileDoubleOptin(ocsmsGateway, optinMobileSet,smsSettings);
//							logger.debug("=======completed submission=========");
							//clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobileStr, msgContent);
							/*if(users.getParentUser() != null) {
								users = users.getParentUser();
							}*/
							/*users.setUsedSmsCount( (users.getUsedSmsCount() == null ? 0 : users.getUsedSmsCount() )+optinMobileSet.size());
							usersDao.saveOrUpdate(users);*/
							//usersDao.updateUsedSMSCount(users.getUserId(), optinMobileSet.size());
							usersDaoForDML.updateUsedSMSCount(users.getUserId(), optinMobileSet.size());
							
							/**
							 * Update SMS Queue.
							 */
							SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
							String mobileNumbers = getCommaSepratedMobileNumber(optinMobileSet);
							smsQueueHelper.updateSMSQueue(mobileNumbers, smsSettings.getAutoResponse(), Constants.SMS_MSG_TYPE_OPTIN,  users, smsSettings.getSenderId());
							
						}else {
							
							if(logger.isDebugEnabled()) logger.debug("low credits with user...");
							optinMobileSet.clear();
							if(noSMSCredits == null) {
								
								noSMSCredits = "Please renew your SMS package.";
								messages = new Messages("Contact" ,"SMS credits are low." ,"Total mobile contacts:"+mobileContacts+" ,"+noSMSCredits,
										Calendar.getInstance(),"Inbox",false ,"Info", users); 
								
								//messagesDao.saveOrUpdate(messages);
								messagesDaoForDML.saveOrUpdate(messages);
							}
							return;
							
						}
						
						optinMobileSet.clear();
						
					}//if
					
					
					//notify to the notifier about this event
					//as this is one obj saving at a time, no flag can be have to justify is ET enabled or not
					if( etList != null && etList.size() > 0 && startContactId != null && endContactId != null) {
						//inputs are sales records,events list
						//if(logger.isInfoEnabled()) logger.info(" >>>>>>>>>>>>>>>observer called>>>>>>>>>>>>>");
						eventTriggerEventsObservable.notifyToObserver(etList, startContactId, endContactId, users.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);
						
						
					}//if
					
				} catch (Exception e) {
					logger.error("Exception ::::" , e);
					continue;
				}
			} //for
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		logger.info("Completed Contacts file processing............");
	} //contactsFileUpload
	
	
	/**
	 * 
	 * @param optinMobileSet
	 * @return string
	 */
	private String getCommaSepratedMobileNumber(Set<String> optinMobileSet) {
		StringBuilder result = new StringBuilder();

		for (String mobile : optinMobileSet) {
			result.append(mobile);
			result.append(",");
		}

		return result.length() > 0 ? result.substring(0, result.length() - 1): "";
	}

	private void contactsFileUpload1(Users users, MailingList mList) {/*
		long startTime = System.currentTimeMillis();
		logger.debug("=======> Started  contactsFileUpload :: " +users.getUserName() );
		try {
			optinMobileSet = new HashSet<String>();

			try {
				resetFields();
				if(users.isEnableSMS() && users.isConsiderSMSSettings()){

					if(SMSStatusCodes.smsProgramlookupOverUserMap.get(users.getCountryType())) smsSettings = smsSettingsDao.findByUser(users.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
					else  smsSettings = smsSettingsDao.findByOrg(users.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);

					try {
						if(smsSettings != null) ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(users.getCountryType()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
					}

				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e1);
			}

			TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();
			//TODO Step 1 : Get the source file from user data  
			//if source file path exits , Don't create the folder and all else create all the directory structure
			String source = userParentDir+"/" + users.getUserName() +"/POSList/sourceFiles/contactFiles";  //need to change from here source path
			File sorceFiles = new File(source);

			logger.debug("source is ::"+source +" ::sorceFiles is exist "+ sorceFiles.exists());

			if(!sorceFiles.exists()) {
				logger.debug("*** Exception : contacts Source file "+ sorceFiles.getName() + " does not exist ***");
				sorceFiles.mkdir();
			}

			//Gives list of Files from a folder.

			File[] listFiles = sorceFiles.listFiles();

			if(listFiles == null || listFiles.length <= 0) {

				logger.debug("NO source files exists...");
				return;
			}

			//TODO Step 2 : Check the Destination directory is Existing ?? If Not Create it
			String destContactPath   = userParentDir+"/" + users.getUserName() +"/POSList/processedFiles/contactFiles/";
			File destContactDir = new File(destContactPath);

			if(!destContactDir.exists()) {
				destContactDir.mkdir();
			}



			logger.debug("mailing lis id is :: "+mList.getListId());


			List<POSMapping> posMappContactLst = null;
			posMappContactLst = posMappingDao.findPOSMappingListByStr("Contacts", users.getUserId());
			if(posMappContactLst == null || posMappContactLst.size() == 0) {
				logger.debug("no data exists contact type mapping from the POSMapping table for this User "+ users.getUserId());
				// Get only general Fields data
				return;
			}


			PurgeList purgeList = (PurgeList)context.getBean("purgeList");
			Calendar folderCal = Calendar.getInstance();
			String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String yearStr = sdf.format(folderCal.getTime());

			logger.debug("listFiles.lengthlistFiles.length"+listFiles.length);
			// PREPARE Ml Structure
			for (int i = 0; i < listFiles.length; i++) {

				try {
					if(listFiles[i].canRead() == false) { 
						if(logger.isDebugEnabled()) logger.debug("Unable to read the file : "+ listFiles[i].getName());
						continue;
					}	

					File sourceFile =listFiles[i];

					//					String sourceFileName = sourceFile.getName();

					//TODO Step 3 : Check Processed Folder path, with MonthStr & Year IF Exists Don't create else create

					String destPath   = userParentDir+"/" + users.getUserName() +"/POSList/processedFiles/contactFiles/"+MonthStr+yearStr;
					File destDir = new File(destPath);
					logger.debug("Directory is exist..."+destDir.exists() +"::dest Directory name is>>"+destDir.getName());
					//TODO Step 4 : Check Processed Folder has Source File
					File destFile = new File(destPath + "/" + sourceFile.getName());
					logger.debug("Contact DestFile = "+destFile.getAbsolutePath());

					if(!destDir.exists()) {
						logger.debug("directory not exist create new ...");
						destDir.mkdir();
					} 


					if(destFile.exists()) {
						logger.debug("source file already exists...");

						File unProcessedFileDir = new File(userParentDir+"/"+users.getUserName()+"/unProcessFiles");
						if(!unProcessedFileDir.exists()) {
							unProcessedFileDir.mkdir();
						}
						File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/unProcessFiles/"+sourceFile.getName());
						sourceFile.renameTo(unProcessFile);

						Messages messages = new Messages("Contacts", "Contacts file upload failed", "File  already exists",Calendar.getInstance(), 
								"Inbox",false ,"Info", users );
						messagesDao.saveOrUpdate(messages);
						continue;
					}


					//						if(logger.isDebugEnabled()) logger.debug("**** Copying the file from source to destination...");
					String sourceFileName = Constants.STRING_NILL;

					//TODO Their are two Possibilities Rename that File or move it to folder
					// Give that file to a thread , this thing should happen in synchronized block		
					*//**
					 *  the application must ensure that access to a given file is synchronized across multiple threads. 
					 * Our first attempt at solving this problem is to make use of a shared table that keeps track of the 
					 * files in use and returns the same instance of a File object to every request that corresponds 
					 * to a given file
					 *
					 *//*
					//Renaming the File
					synchronized  (this){
						try {
							File newName = null;
							sourceFileName = sourceFile.getName();
							//IF the Source File Name end's with ".csv" convert it to ".csv.tmp"
							if(sourceFileName.endsWith(".csv")){
								newName = new File(source+"/"+sourceFileName+".tmp");
								logger.debug("Source File  Absolute Path ......:"+sourceFile.getAbsolutePath());
								logger.debug("New Name Absolute Path ......:"+newName.getAbsolutePath());
								boolean flag = sourceFile.renameTo(newName);
								logger.debug("Source File  Absolute Path ......:"+sourceFile.getAbsolutePath());

								if(flag){
									logger.debug("Source File is Rename to ........................"+newName);
								}
							}

							sourceFileName = newName.getName();
							logger.debug(sourceFileName+":: file processing is started..");
							logger.debug("###################sourceFileName.endsWith(.tmp) #########"+sourceFileName.endsWith(".tmp"));
							ArrayList<File> filesList = new ArrayList<File>();
							filesList.add(newName);

							if(sourceFileName.endsWith(".tmp")){
								logger.debug("################## Job Instantiated  #############");
								UploadContactsFromFileJob  uploadContactsFromFileJob = new 
										UploadContactsFromFileJob(context, users,newName,posMappContactLst,genFieldContMap , 
												genFieldSalesMap,genFieldSKUMap,genFieldHomesPassedMap,mList,sourceFileName, 
												smsSettings, ocsmsGateway,captiwayToSMSApiGateway);
								destFile = new File(destPath + "/" + sourceFile.getName()+".tmp");

								logger.debug("Contact DestFile = "+destFile.getAbsolutePath());

								if(!destDir.exists()) {
									//logger.debug("directory not exist create new ...");
									destDir.mkdir();
								} 
								uploadContactsFromFileJob.start();
								sourceFile.renameTo(destFile);
								logger.debug("################## Job Completed  #############");
							}


						} catch (Exception e) {
							logger.error("Exception ::::" , e);
						}
					}







					*//**
					 * 1. Find the File Size.
					 * 2. Keep all the files in list
					 * 3. Process 5 files in 5 threads.
					 * 4. If file size is huge just continue so that file is in the same source folder in the next 
					 * 	  it will pick the file, our thread is repeated till all the files are processed.
					 * 
					 * 
					 * Other thing
					 * -----------
					 * can Divide each operation in a new thread here 
					 * 
					 * 1. If thread-A has picked up the file thread-B should not pick the same file
					 * 	  so mark this file as tmp and if the file name is .tmp don't process it next time
					 * the application must ensure that access to a given file is synchronized across multiple threads. 
					 * Our first attempt at solving this problem is to make use of a shared table that keeps track of the 
					 * files in use and returns the same instance of a File object to every request that corresponds to a given file
					 *//*
					logger.debug("###################sourceFileName.endsWith(.tmp) #########"+sourceFileName.endsWith(".tmp"));
					ArrayList<File> filesList = new ArrayList<File>();
					filesList.add(sourceFile);
					if(!sourceFileName.endsWith(".tmp")){
						logger.debug("################## Job Instantiated  #############");
						UploadContactsFromFileJob  uploadContactsFromFileJob = new 
								UploadContactsFromFileJob(context, users,destFile,posMappContactLst,genFieldContMap , 
										genFieldSalesMap,genFieldSKUMap,genFieldHomesPassedMap,mList,sourceFileName, 
										smsSettings, ocsmsGateway,captiwayToSMSApiGateway);
						destFile = new File(destPath + "/" + sourceFile.getName()+".tmp");

						logger.debug("Contact DestFile = "+destFile.getAbsolutePath());

						if(!destDir.exists()) {
							//logger.debug("directory not exist create new ...");
							destDir.mkdir();
						} 
						uploadContactsFromFileJob.start();
						sourceFile.renameTo(destFile);
						logger.debug("################## Job Completed  #############");
					}
					 
				} catch (Exception e) {
					logger.error("Exception ::::" , e);


				}

			} //for
			logger.debug("<------------- Completed contactsFileUpload in : "
					+ (System.currentTimeMillis() - startTime) + " Millisecond");
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			logger.debug("<------------- Completed contactsFileUpload in : "
					+ (System.currentTimeMillis() - startTime) + " Millisecond");
		}

	*/} //contactsFileUpload



	private Contacts setContactFieldsData(String[] nextLineStr, Contacts contact, int customerIdIdx, int addressUnitIdIndx, int emailIdIdx, int mobileIdx, int firstNameIdx,
			int lastNameIdx, int streetIdx,int addressTwoIdx, int cityIdx, int stateIdx, 
			int zipIdx, int countryIdx, int genderIdx, int birthDayIdx, int anniversaryIdx, int homeStoreIdx,int sbsIndex,int createdDateIdx,
			String birthDayFormat,String anniversaryFormat,String createdDateFormat,Hashtable udfHashtable, List<POSMapping> posMappContactLst) throws Exception {




		String tempStr=null;

		//set the ExternalId

		//		if(contact.getExternalId()==null) contact.setExternalId("-1");

		/*if(customerIdIdx > -1 && customerIdIdx < nextLineStr.length && (tempStr = nextLineStr[customerIdIdx].trim()).length() !=0  
					   &&  (contact.getExternalId()==null || contact.getExternalId().equals("-1"))) {
			if(!tempStr.equals("-1")){

				contact.setExternalId(tempStr);
			}
		}*/

		if(customerIdIdx > -1 && customerIdIdx < nextLineStr.length && (tempStr = nextLineStr[customerIdIdx].trim()).length() !=0) {
			if(!tempStr.equals("-1")){

				contact.setExternalId(tempStr);
			}
		}


		//set hpId
		if(addressUnitIdIndx > -1 && addressUnitIdIndx < nextLineStr.length && (tempStr=nextLineStr[addressUnitIdIndx].trim()).length() !=0 ){
			contact.setHpId(Long.parseLong(tempStr));
		}

		//set the emailId
		if(emailIdIdx > -1 && emailIdIdx < nextLineStr.length && 
				(Utility.validateEmail(tempStr = nextLineStr[emailIdIdx].trim()))) {
			contact.setEmailId(tempStr);

		}

		//set the Phone

		if(mobileIdx > -1 && mobileIdx < nextLineStr.length && (tempStr = nextLineStr[mobileIdx].trim()).length() !=0 ){
			try {


				tempStr = Utility.phoneParse(tempStr.trim(),users.getUserOrganization());
				//				logger.debug("=============================="+tempStr);
				String conMobile = contact.getMobilePhone();

				/*if(tempStr !=  null && tempStr.trim().length() > 0) {

					contact.setMobileStatus(performMobileOptin(contact, contact.getUsers(), tempStr));
					contact.setMobilePhone(tempStr);
					mobileContacts ++;

				}else {

					if(contact.getMobilePhone() == null) {

						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					}
				}*/

				if(tempStr !=  null && tempStr.trim().length() > 0) {

					if(noSMSComplaincyMsg == null ) {

						contact.setMobileStatus(performMobileOptin(contact, contact.getUsers(), tempStr));
					}
					else{

						contact.setMobileOptin(false);
						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);

					}

					contact.setMobilePhone(tempStr);
					LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(users, contact, tempStr);
					mobileContacts ++;
					//contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				}else {

					if(contact.getMobilePhone() == null ) {

						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);

					}


				}

				//				Long mobilePhone  = Long.parseLong(tempStr);
			} catch (NumberFormatException e) {
				if(logger.isInfoEnabled()) logger.debug("inavlid Phone number"+tempStr);
			}
		}

		//set the First Name
		if(firstNameIdx > -1 && firstNameIdx < nextLineStr.length && (tempStr = nextLineStr[firstNameIdx].trim()).length() !=0  ){

			contact.setFirstName(tempStr);
		}

		//set the Last Name
		if(lastNameIdx  > -1 && lastNameIdx < nextLineStr.length && (tempStr = nextLineStr[lastNameIdx].trim()).length() !=0  ){
			contact.setLastName(tempStr);
		}

		//set the Address One
		if(streetIdx > -1 && streetIdx < nextLineStr.length && (tempStr = nextLineStr[streetIdx].trim()).length() !=0 ){
			contact.setAddressOne(tempStr);
		}

		//set the Address Two Changes 2.5.4.0
		if(addressTwoIdx > -1 && addressTwoIdx < nextLineStr.length && (tempStr = nextLineStr[addressTwoIdx].trim()).length() !=0 ){
			contact.setAddressTwo(tempStr);
		}
		
		
		//set the City
		if(cityIdx > -1 && cityIdx < nextLineStr.length && (tempStr = nextLineStr[cityIdx].trim()).length() !=0 ){
			contact.setCity(tempStr);
		}

		//set the State
		if(stateIdx > -1 && stateIdx < nextLineStr.length && (tempStr = nextLineStr[stateIdx].trim()).length() !=0 ){
			contact.setState(tempStr);
		}

		//set the Pin
		
		String countryType = users.getCountryType();
		try{
		if(Utility.zipValidateMap.containsKey(countryType)){
			
			if(zipIdx > -1 && zipIdx < nextLineStr.length && (tempStr = nextLineStr[zipIdx].trim()).length() !=0 ){
				
				boolean zipCode = Utility.validateZipCode(tempStr, countryType);
				if(zipCode){
					contact.setZip(tempStr);
				}
				
			}
		} else {
			
			if(zipIdx > -1 && zipIdx < nextLineStr.length && (tempStr = nextLineStr[zipIdx].trim()).length() !=0 ){
				
				if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
					int pinNum  = Integer.parseInt(tempStr);
					contact.setZip(""+pinNum);
				}
			}
			
		}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Pin num is "+tempStr+ " out of range Exception...");
		}
		
		/*if(zipIdx > -1 && zipIdx < nextLineStr.length && (tempStr = nextLineStr[zipIdx].trim()).length() !=0 ){
			try {
				//				int pinNum ;
				if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
					//pinNum = Integer.parseInt(tempStr.trim());
					contact.setZip(tempStr);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.debug("Pin num is "+tempStr+ " out of range Exception...");
			}
		}*/

		//set the Country
		if(countryIdx > -1 && countryIdx < nextLineStr.length && (tempStr = nextLineStr[countryIdx].trim()).length() !=0 ){
			contact.setCountry(tempStr);
		}

		//set the Country
		if(genderIdx > -1 && genderIdx < nextLineStr.length && (tempStr = nextLineStr[genderIdx].trim()).length() !=0  ) {
			//set the proper gender field
			contact.setGender(tempStr);
		}

		//set the BirthDay
		//		tempStr = nextLineStr[birthDayIdx].trim();
		if(birthDayIdx > -1 && birthDayIdx < nextLineStr.length && (tempStr = nextLineStr[birthDayIdx].trim()).length() !=0  ) {
			String newDate=tempStr.replaceAll("\\s*:\\s*",":");
			logger.info("newDate::"+newDate);
			try {

				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(birthDayFormat);
				//date = (Date)formatter.parse(tempStr);
				date = (Date)formatter.parse(newDate);
				logger.info("tempStr::"+date);
				Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(birthDayFormat));
				dobCal.setTime(date);
				contact.setBirthDay(dobCal);
			} catch (Exception e) {
				logger.debug("dob date foramt not match with data",e);
			}
		}

		//set the Anniversary
		//		tempStr = nextLineStr[anniversaryIdx].trim();
		if(anniversaryIdx > -1 && anniversaryIdx < nextLineStr.length && (tempStr = nextLineStr[anniversaryIdx].trim()).length() !=0  ) {
			String newDate=tempStr.replaceAll("\\s*:\\s*",":");
			logger.info("newDate ::"+newDate);
			try {

				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(anniversaryFormat);
				//date = (Date)formatter.parse(tempStr);
				date = (Date)formatter.parse(newDate);
				logger.info("tempStr::"+date);
				Calendar anniCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(anniversaryFormat));
				anniCal.setTime(date);
				contact.setAnniversary(anniCal);
			} catch (Exception e) {
				logger.debug("Anniversary date foramt not match with data",e);
			}
		}


		//set homeStoreIdx
		//		tempStr = nextLineStr[homeStoreIdx].trim();
		if(homeStoreIdx > -1 && homeStoreIdx < nextLineStr.length && (tempStr = nextLineStr[homeStoreIdx].trim()).length() !=0  ) {

			contact.setHomeStore(tempStr);
		}
		
		if(sbsIndex > -1 && sbsIndex < nextLineStr.length && (tempStr = nextLineStr[sbsIndex].trim()).length() !=0  ) {

			contact.setSubsidiaryNumber(tempStr);
		}

		//set Created Date
		if(createdDateIdx > -1 && createdDateIdx < nextLineStr.length && (tempStr = nextLineStr[createdDateIdx].trim()).length() !=0  ) {
			String newDate=tempStr.replaceAll("\\s*:\\s*",":");
			logger.info("newDate ::"+newDate);
			try {

				DateFormat formatter ; 
				formatter = new SimpleDateFormat(createdDateFormat);
				//Date date = (Date)formatter.parse(tempStr);
				Date date = (Date)formatter.parse(newDate);
				logger.info("tempStr::"+date);
				Calendar creatDateCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(createdDateFormat));
				creatDateCal.setTime(date);

				if(creatDateCal.before(contact.getCreatedDate())){
					contact.setCreatedDate(creatDateCal);
				}
			} catch (Exception e) {
				logger.debug("created date foramt not match with data",e);
			}
		}

		//set the customField data

		if(udfHashtable.size() >0 ) {


			String udfDataStr= null;
			String dateFormat ="";
			String dataTypeStr ="";

			for (POSMapping posMapping : posMappContactLst) {
				String custFieldName  = posMapping.getCustomFieldName();
				//				logger.debug("$$ dataType Ë�Ë�"+posMapping.getDataType() +">> Ë�Ë� GetCustFieldnameis Ë�Ë�"+posMapping.getCustomFieldName());

				if(udfHashtable.containsKey(custFieldName)) {

					int udfIdx =(Integer)udfHashtable.get(custFieldName);

					if(udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() !=0) {

						dataTypeStr = posMapping.getDataType().trim();
						//						logger.debug("DataTyp>>>>>> ::"+dataTypeStr+"Ë�Ë�Ë�CustField Ë�Ë�"+posMapping.getCustomFieldName());
						// if UDF is Date Type Validate The "udfDataStr"
						if(posMapping.getDataType().trim().startsWith("Date")) {

							try {
								dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
								if(!(CustomFieldValidator.validateDate(udfDataStr,  dateFormat))) {
									continue;
								}

								DateFormat formatter ; 
								Date udfdDate ; 
								formatter = new SimpleDateFormat(dateFormat);
								udfdDate = (Date)formatter.parse(udfDataStr); 
								Calendar udfCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
								udfCal.setTime(udfdDate);
								udfDataStr = MyCalendar.calendarToString(udfCal, MyCalendar.dateFormatMap.get(dateFormat));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::::" , e);
							}


						}
						
						if(posMapping.getDataType().trim().startsWith("Number"))
							udfDataStr = Utility.validateNumberValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);
						if(posMapping.getDataType().trim().startsWith("Double"))
							udfDataStr = Utility.validateDoubleValue(udfDataStr);
							logger.info("udfDataStr number workerThread is ----"+udfDataStr);

						int UDFIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
						try {

							//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
							contact = setConatctCustFields(contact, UDFIdx, udfDataStr);
						} catch (Exception e) {
							logger.error("Exception ::::" , e);
							logger.debug("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
						}

					}



				} // if
			} //for posMapping

		} // if

		return contact;
	}


	private Contacts setConatctCustFields(Contacts contact , int index, String  udfData) throws Exception {


		switch(index){
		case 1: contact.setUdf1(udfData); return contact;
		case 2: contact.setUdf2(udfData); return contact;
		case 3: contact.setUdf3(udfData); return contact;
		case 4: contact.setUdf4(udfData); return contact;
		case 5: contact.setUdf5(udfData); return contact;
		case 6: contact.setUdf6(udfData); return contact;
		case 7: contact.setUdf7(udfData); return contact;
		case 8: contact.setUdf8(udfData); return contact;
		case 9: contact.setUdf9(udfData); return contact;
		case 10: contact.setUdf10(udfData); return contact;
		case 11: contact.setUdf11(udfData); return contact;
		case 12: contact.setUdf12(udfData); return contact;
		case 13: contact.setUdf13(udfData); return contact;
		case 14: contact.setUdf14(udfData); return contact;
		case 15: contact.setUdf15(udfData); return contact;

		}

		return contact;



	} // setConatctCustFields

	private void homesPassedFileUpload(Users users, MailingList mList) {



		try {

			//String source = userParentDir+"/" + users.getUserName() + "/POSList/sourceFiles/homesPassedFiles/";
			String source = userParentDir+ "/POSList/sourceFiles/homesPassedFiles/";
			File sorceFiles = new File(source);

			//logger.debug("source is ;;;;"+source +" ;;sorceFiles is exist "+ sorceFiles.exists());

			if(!sorceFiles.exists()) {
				//				logger.debug("*** Exception : BCRM Source file "+ sorceFiles.getName() + " does not exist ***");
				sorceFiles.mkdir();
			}

			File[] listFiles = sorceFiles.listFiles();
			if(listFiles == null || listFiles.length <= 0) {

				//				logger.debug("NO source files exists...");
				return;
			}
			//			logger.debug("uploading the BCRM listFiles length is ::"+listFiles.length);

			//retailProsalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
			//String destSalesPath   = userParentDir+"/" + users.getUserName() + "/POSList/processedFiles/homesPassedFiles/";
			String destSalesPath   = userParentDir+ "/POSList/processedFiles/homesPassedFiles/";
			File destSalesDir = new File(destSalesPath);
			if(!destSalesDir.exists()) {
				destSalesDir.mkdir();
			} 

			File sourceFile = null;
			Calendar folderCal = Calendar.getInstance();
			String MonthStr =  mName[folderCal.get(Calendar.MONTH)];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			String yearStr = sdf.format(folderCal.getTime());

			for (int i = 0; i < listFiles.length; i++) {

				try {
					sourceFile =listFiles[i];
					//					logger.debug(" permission for the file ::"+sourceFile.getAbsolutePath());

					if(sourceFile.canRead() == false) {

						//						logger.debug("no read permission for the file ::"+sourceFile.getAbsolutePath());
						continue;
					}

					//String destPath   = userParentDir+"/" + users.getUserName() + "/POSList/processedFiles/homesPassedFiles/"+MonthStr+yearStr;
					String destPath   = userParentDir+ "/POSList/processedFiles/homesPassedFiles/"+MonthStr+yearStr;

					//TODO need to know how it will be incompleted
					if(sourceFile.getName().contains("_POSINCMPLETEPART")) {
						//						logger.debug(sourceFile.getName()+ " ::  is inComplete File .." );
						continue;
					}

					if(sourceFile.getName().contains("_POSINCMPLETEPART") || sourceFile.getName().endsWith(".MARKEDINCOMPLETE")) {
						logger.debug(sourceFile.getName()+ " ::  is inComplete File .." );
						continue;
					}

					File destDir = new File(destPath);
					File destFile = new File(destPath + File.separator + sourceFile.getName());

					if(!destDir.exists()) {

						//						logger.debug("directory not exist create new ...");
						destDir.mkdir();
					} else {

						//							logger.debug("copying the file from source to destination...");
						//destFile = new File(destPath + File.separator + sourceFile.getName());

						Messages messages = null;

						if(destFile.exists()) {
							//								logger.debug("source file already exists...");
							String messagesString = destFile.getName() +" file already exists..";

							//File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles");
							File unProcessedFileDir = new File(userParentDir+ "/POSList/unProcessFiles");

							if(!unProcessedFileDir.exists()) {

								unProcessedFileDir.mkdir();
							}
							//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles/"+sourceFile.getName());
							File unProcessFile = new File(userParentDir+ "/POSList/unProcessFiles/"+sourceFile.getName());
							sourceFile.renameTo(unProcessFile);

							messages = new Messages("Sales files failed", "Sales file upload failed", messagesString, Calendar.getInstance(), 
									"Inbox",false ,"Info", users);
							//messagesDao.saveOrUpdate(messages);
							messagesDaoForDML.saveOrUpdate(messages);
							continue;
						}
					}

					if(logger.isInfoEnabled()) logger.debug("destFile is exist..."+destFile.exists() +"::destFile name is>>"+destFile.getName());

					try {
						sourceFile.renameTo(destFile);
					} catch (Exception e) {

						logger.error("Exception ::::" , e);

						//						logger.debug("File moved in to unprocessed Dir");
						//File unProcessedFileDir = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles");
						File unProcessedFileDir = new File(userParentDir+ "/POSList/unProcessFiles");

						if(!unProcessedFileDir.exists()) {

							unProcessedFileDir.mkdir();
						}
						//File unProcessFile = new File(userParentDir+ "/" + users.getUserName() + "/POSList/unProcessFiles/"+sourceFile.getName());
						File unProcessFile = new File(userParentDir+ "/POSList/unProcessFiles/"+sourceFile.getName());
						sourceFile.renameTo(unProcessFile);
					}

					/*******destfile is saved from the DB********/
					saveHomesPassedFile(destFile,users,mList);

					// updateing the masters to child relationship mappling data 
					//getMastToTransMappings(mList,users.getUserId());

				} catch (Exception e) {
					logger.error("Exception ::::" , e);
					continue;
				}

			} //for	

		} catch (Exception e) {
			logger.error("Exception ::::" , e);

		} 


	}//homesPassedFileUpload()

	private void saveHomesPassedFile(File destFile,Users users,MailingList mList)  {

		try {

			TreeMap<String, List<String>> prioMap =null;
			prioMap = new TreeMap<String, List<String>>();

			List<POSMapping> homesPassedPOSMappingList = null;

			homesPassedPOSMappingList = posMappingDao.findPOSMappingListByStr(Constants.POS_MAPPING_TYPE_HOMES_PASSED, users.getUserId());


			if(homesPassedPOSMappingList == null || homesPassedPOSMappingList.size() < 1) {
				//			logger.debug("no BCRM custField Mapping list from the DB for the userName is ::"+users.getUserName());
				return;
			}

			if(logger.isInfoEnabled()) logger.debug("homesPassedPOSMappingList size is ::"+homesPassedPOSMappingList.size());

			int idxCountry= -1,idxState = -1,idxDistrict = -1,idxCity = -1,idxZip= -1; 
			int idxArea = -1,idxStreet = -1,idxAddrOne = -1,idxAddrTwo = -1, idxAddrUnitId = -1;


			Hashtable UdfHashtable = new Hashtable();

			List<HomesPassed> homesPassedList = new ArrayList<HomesPassed>();

			int savingCount = 1;

			//		char delimiter = ',';
			String[] nextLineStr ;

			//Read HEADDERS
			FileReader fileReader = new FileReader(destFile);
			BufferedReader br = new BufferedReader(fileReader);

			String lineStr=null;
			//String salesdateFormat ="";

			//identify the headers from file
			while((lineStr = br.readLine())!= null) {
				//			lineStr += ",\"0\"";
				nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);

				if(nextLineStr.length == 0) {
					continue;
				}
				List<String> existedFieldList = new ArrayList<String>(); 

				if(homesPassedPOSMappingList != null && homesPassedPOSMappingList.size() >0 ) {

					for (POSMapping posMapping : homesPassedPOSMappingList) {

						for(int j=0; j<nextLineStr.length ;j++) {

							String tempStr = nextLineStr[j].trim();
							// ,,,,Sale Price,Tax,,,SKU,
							if(existedFieldList.contains(tempStr)) {
								//logger.debug("Field already mapped with the general fields..");
								continue;
							}
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Addressunit Id") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//logger.debug("tempStr.."+tempStr +">> idxAddrUnitId ::"+j);
								idxAddrUnitId =  j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);

								continue;
							} 

							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Country") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//							logger.debug("tempStr.."+tempStr +">> idxCountryID ::"+j);
								idxCountry =  j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							} 
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("State") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//					    	logger.debug("tempStr.."+tempStr +">> idxState ::"+j);
								idxState = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							} 
							if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("District") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//					    	logger.debug("tempStr.."+tempStr +">> idxDistrict ::"+j);
								idxDistrict =  j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							} 
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("City") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//					    	logger.debug("tempStr.."+tempStr +">> idxCity ::"+j);
								idxCity = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("ZIP") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//					    	logger.debug("tempStr.."+tempStr +">> idxZIP ::"+j);
								idxZip = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							} 
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Area") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//logger.debug("tempStr.."+tempStr +">> idxArea ::"+j);
								idxArea = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Street") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//logger.debug("tempStr.."+tempStr +">> idxStreet ::"+j);
								idxStreet = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Address One") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//logger.debug("tempStr.."+tempStr +">> idxAddress One ::"+j);
								idxAddrOne = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}
							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().equals("Address Two") 
									&& posMapping.getPosAttribute().trim().equalsIgnoreCase(tempStr)) {
								//logger.debug("tempStr.."+tempStr +">> idxAddress Two ::"+j);
								idxAddrTwo = j;
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}


							else if(!(existedFieldList.contains(tempStr)) && posMapping.getCustomFieldName().contains("UDF") 
									&&  posMapping.getPosAttribute().equalsIgnoreCase(tempStr)) {	


								//							logger.debug("posMapping.getPosAttribute() is ::"+posMapping.getPosAttribute() +":: and tempStr is "+tempStr);
								UdfHashtable.put(posMapping.getCustomFieldName(), j);
								existedFieldList.add(tempStr);
								storePriorityVal(j, posMapping, prioMap);
								continue;
							}

						} //for

					} //if

				} // for
				//			logger.debug("sales UdfHashtable size  is:"+UdfHashtable.size());
				break;
			} // while




			//		List totalRecordsInCsvList = csvreader.readAll();
			int totalCountInCSV = 0;
			int existedRecordCount = 0;
			int newEntryRecords = 0;
			// Read DATA
			while((lineStr = br.readLine())!= null) {		
				//			lineStr += ",\"0\"";
				nextLineStr = parse(lineStr);//  lineStr.split(csvDelemiterStr);
				//calculate total count in a file
				totalCountInCSV++;

				if(nextLineStr.length == 0) {
					continue;
				}

				HomesPassed homesPassed = null;


				//			logger.debug(" nextLineStr.length is ::"+nextLineStr.length  +" list is >>"+mList.getListId());

				try {

					//check  homespassed Obj if existed 
					if(!prioMap.isEmpty()) {
						homesPassed = homesPassedDao.findHomesPassedByPriority(prioMap,  nextLineStr, users.getUserId());
					}
					if(homesPassed != null) {

						homesPassed  = setHomesPassedGenralFields(homesPassed, nextLineStr, idxAddrUnitId,idxCountry, idxState, idxDistrict,
								idxCity, idxZip, idxArea, idxStreet , idxAddrOne ,idxAddrTwo, UdfHashtable, homesPassedPOSMappingList);

						//homesPassedDao.saveOrUpdate(homesPassed);
						homesPassedDaoForDML.saveOrUpdate(homesPassed);

						existedRecordCount++;
						continue;

					}
					else if(homesPassed==null) { 

						homesPassed = new HomesPassed(users.getUserId(), Calendar.getInstance());

						homesPassed  = setHomesPassedGenralFields(homesPassed, nextLineStr, idxAddrUnitId,idxCountry, idxState, idxDistrict,
								idxCity, idxZip, idxArea, idxStreet , idxAddrOne ,idxAddrTwo, UdfHashtable, homesPassedPOSMappingList);

						//if(retailProsalesDao.saveOrUpdate(retailProSales);
						//newEntryRecords ++;

						homesPassedList.add(homesPassed);

					}

				}catch (Exception e) {
					logger.debug("Exception :: error occured while setting the Homes passed field date to due to wrong values existed from the csv file ..so we ignoe it..");
					continue;
				}
				//for every 100 records saved in to DB
				if(savingCount == 200) {
					try {
						newEntryRecords = newEntryRecords+savingCount;

						if(logger.isInfoEnabled()) logger.debug("for every 200 record call the gc() method... and save the records in to Db");

						//homesPassedDao.saveByCollection(homesPassedList);
						homesPassedDaoForDML.saveByCollection(homesPassedList);

						
						homesPassedList.clear();
						savingCount = 0;
						//System.gc();
					} catch (Exception e) {
						logger.error("Exception ::::" , e);
					}
				}

				savingCount++;

			} //while

			br.close();
			fileReader.close();

			if(homesPassedList.size() > 0) {

				newEntryRecords = newEntryRecords+homesPassedList.size();
				//homesPassedDao.saveByCollection(homesPassedList);
				homesPassedDaoForDML.saveByCollection(homesPassedList);

				//			logger.debug("total  records are saved sucessfully");

				homesPassedList.clear();
				savingCount = 0;
				//System.gc();
			}

			StringBuffer msgStuff = new StringBuffer(" Uploaded file name : ");
			msgStuff.append(destFile.getName());
			msgStuff.append("\n Total  records : ");
			msgStuff.append(""+ totalCountInCSV);
			msgStuff.append("\n invalid(which already exists ) : ");
			msgStuff.append(existedRecordCount);
			msgStuff.append("\n Newly added  records : ");
			msgStuff.append(newEntryRecords);



			/*String messagesString = totalCountInCSV  +" Total records. and "+newEntryRecords +" added, "+existedRecordCount+
								" invalid(which already exists ) from "+destFile.getName()+" file.";*/

			//"From "+destFile.getName()+ ":: "+totalRecordsInCsv "records existed"+newEntryRecords+" fields are saved sucessfully remeaining fields "+existedRecordCount+" already existed..";
			Messages messages = new Messages("BCRM files loaded", "Uploaded BCRM file successfully.", msgStuff.toString(), Calendar.getInstance(), 
					"Inbox",false ,"Info", users);
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);


		}  catch (Exception e) {
			logger.error("Exception ::::" , e);
		}

	}

	private HomesPassed setHomesPassedGenralFields(HomesPassed homesPassed, String[] nextLineStr, int idxAddrUnitId,int idxCountry ,
			int idxState ,int idxDistrict, int idxCity ,int idxZip, int idxArea ,int idxStreet,int idxAddrOne ,
			int idxAddrTwo, Hashtable UdfHashtable ,List<POSMapping> salesPOSMappingList) throws Exception {



		String tempStr  = null;

		//set AddresUnitId
		if(idxCountry > -1 && idxCountry < nextLineStr.length && (tempStr = nextLineStr[idxAddrUnitId].trim()).length() !=0){
			homesPassed.setAddressUnitId(Long.parseLong(tempStr));
		}

		//set the Country
		if(idxCountry > -1 && idxCountry < nextLineStr.length && (tempStr = nextLineStr[idxCountry].trim()).length() !=0){
			homesPassed.setCountry(tempStr);
		}

		//set the State
		if(idxState > -1 && idxState < nextLineStr.length && (tempStr = nextLineStr[idxState].trim()).length() !=0){
			homesPassed.setState(tempStr);
		}

		//set the District
		if(idxDistrict > -1 && idxDistrict < nextLineStr.length && (tempStr = nextLineStr[idxDistrict].trim()).length() !=0){
			homesPassed.setDistrict(tempStr);
		}


		//set the City
		if(idxCity > -1 && idxCity < nextLineStr.length && (tempStr = nextLineStr[idxCity].trim()).length() !=0 ){

			try {
				homesPassed.setCity(tempStr);
			} catch (Exception e) {
				//				logger.debug("Quantity  parsing exception ",e);
				logger.error("Exception ::::" , e);
			}
		}

		//set the Zip
		if(idxZip > -1 && idxZip< nextLineStr.length && (tempStr = nextLineStr[idxZip].trim()).length() !=0 ){
			try {
				homesPassed.setZip(tempStr);
			} catch (Exception e) {
				logger.debug("SalesPrice  parsing exception ",e);
			}
		}

		//set the area
		if(idxArea > -1 && idxArea< nextLineStr.length && (tempStr = nextLineStr[idxArea].trim()).length() !=0  ){
			homesPassed.setArea(tempStr);
		}

		//set the Street
		if(idxStreet > -1 && idxStreet< nextLineStr.length && (tempStr = nextLineStr[idxStreet].trim()).length() !=0  ){
			homesPassed.setStreet(tempStr);
		}
		//set the Addr one
		if(idxAddrOne > -1 && idxAddrOne < nextLineStr.length && (tempStr = nextLineStr[idxAddrOne].trim()).length() !=0 ){
			homesPassed.setAddressOne(tempStr);
		}
		//set the Addr two
		if(idxAddrTwo > -1 && idxAddrTwo < nextLineStr.length && (tempStr = nextLineStr[idxAddrTwo].trim()).length() !=0 ){
			homesPassed.setAddressTwo(tempStr);
		}


		//Modified date
		homesPassed.setModifiedDate(Calendar.getInstance());

		//set the customField data 
		//	logger.debug("befor setting the object the UdfHashtable.size() is"+UdfHashtable.size());
		if(UdfHashtable.size() >0 ) {

			String udfDataStr = null;
			String dateFormat = "";
			String dataTypeStr = "";
			for (POSMapping posMapping : salesPOSMappingList) {

				String custFieldName  = posMapping.getCustomFieldName();

				//logger.debug("UdfHashtable is::"+UdfHashtable +"and custField name is ::"+custFieldName);

				//logger.debug("UDF hash table exist the Key is"+UdfHashtable.contains(custFieldName));
				if(UdfHashtable.containsKey(custFieldName)) {
					int udfIdx =(Integer)UdfHashtable.get(custFieldName);

					//				logger.debug("UDF Idx is ::"+udfIdx);

					if(udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() !=0) {
						dataTypeStr = posMapping.getDataType().trim();
						//if UDF data is Date Type 
						if(posMapping.getDataType().trim().startsWith("Date")) {

							try {
								dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
								//						salesdateFormat = salesdateFormat.substring(salesdateFormat.indexOf("(")+1, salesdateFormat.indexOf(")"));

								if(!CustomFieldValidator.validateDate(udfDataStr, dateFormat)) {
									//								logger.debug(" Sales Pos dateFormat  not match with value so we ignore the Udf data");
									continue;
								}

								DateFormat formatter ; 
								Date date ; 
								formatter = new SimpleDateFormat(dateFormat);
								date = (Date)formatter.parse(udfDataStr); 
								Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
								cal.setTime(date);
								udfDataStr = MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception ::::" , e);
							}
						}
						int UDfIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
						//					logger.debug("UDfIdx is ::"+UDfIdx +":: and udfData is ::"+udfDataStr);

						try {
							if(udfDataStr.length() == 0) {
								continue;
							} 
							homesPassed = setHomesPassedCustFielddata(homesPassed, UDfIdx, udfDataStr);
						} catch (Exception e) {
							logger.error("Exception ::::" , e);
							if(logger.isInfoEnabled()) logger.debug("Exception :: error while setting the sales UDF field data..due to wrong values existed from the sales csv" +
									" file.so we ignoe the udf data.. ");
							continue;
						}
					}

				}
			} //for
		} // if

		return homesPassed;
	}


	private HomesPassed setHomesPassedCustFielddata(HomesPassed homesPassed, int index, String udfData) {

		switch(index){
		case 1: homesPassed.setUdf1(udfData); 	return homesPassed;
		case 2:	homesPassed.setUdf2(udfData);	return homesPassed;
		case 3: homesPassed.setUdf3(udfData); 	return homesPassed;
		case 4:	homesPassed.setUdf4(udfData);	return homesPassed;
		case 5:	homesPassed.setUdf5(udfData);	return homesPassed;
		case 6: homesPassed.setUdf6(udfData);	return homesPassed;
		case 7:	homesPassed.setUdf7(udfData);	return homesPassed;
		case 8:	homesPassed.setUdf8(udfData);	return homesPassed;
		case 9: homesPassed.setUdf9(udfData); 	return homesPassed;
		case 10: homesPassed.setUdf10(udfData); return homesPassed;
		case 11: homesPassed.setUdf11(udfData); return homesPassed;
		case 12: homesPassed.setUdf12(udfData); return homesPassed;
		case 13: homesPassed.setUdf13(udfData); return homesPassed;
		case 14: homesPassed.setUdf14(udfData); return homesPassed;
		case 15: homesPassed.setUdf15(udfData); return homesPassed;

		}
		return homesPassed;
	} //setHomesPassedCustFielddata

	public void performParentalConsent(Contacts tempContact) {

		//		logger.debug("**********Parental Concent***************");
		ContactParentalConsent contactConsent = null;

		contactConsent = contactParentalConsentDao.findByContactId(tempContact.getContactId());
		if(contactConsent != null) {

			if(logger.isInfoEnabled()) logger.debug("email has already been sent to it parent ::"+contactConsent.getEmail());
			return;

		}
		String parentEmailStr  = null;

		//TODO Need to Change Parent Email
		contactConsent = new ContactParentalConsent(tempContact.getContactId(), parentEmailStr,
				Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL, tempContact.getUsers().getUserId(), Calendar.getInstance(),tempContact.getEmailId());


		//contactParentalConsentDao.saveOrUpdate(contactConsent);
		contactParentalConsentDaoForDML.saveOrUpdate(contactConsent);
		
		//TODO need to check the parentEmailStr

		if(parentEmailStr == null) return;

		CustomTemplates custTemplate = null;
		String message = PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");

		List<MailingList> mlList = mailingListDao.findByContactBit(tempContact.getUsers().getUserId(), tempContact.getMlBits());
		//Set<MailingList> mlset = tempContact.getMlSet();
		Set<MailingList> mlset = new HashSet<MailingList>(mlList);

		Iterator<MailingList> mlItr = mlset.iterator();
		MailingList mailingList = null;
		while(mlItr.hasNext()){
			mailingList = mlItr.next();
			if(mailingList.getConsentCutomTempId() != null) {
				custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getConsentCutomTempId());
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
						message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)context.getBean("myTemplatesDao");
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates!=null) {
						  message = myTemplates.getContent();
					  }
					}
			  }
				
				
			}
			if(logger.isInfoEnabled()) logger.debug("-----------email----------"+tempContact.getEmailId());

			EmailQueue testEmailQueue = new EmailQueue(mailingList.getConsentCutomTempId(),Constants.EQ_TYPE_TEST_PARENTAL_MAIL, message, "Active", parentEmailStr, tempContact.getUsers(), new Date(),
					"Require Parental Consent.", tempContact.getEmailId(), tempContact.getFirstName(), MyCalendar.calendarToString(tempContact.getBirthDay(), MyCalendar.FORMAT_DATEONLY), tempContact.getContactId());

			logger.debug("testEmailQueue"+testEmailQueue.getChildEmail());
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		}//iterator



	} //performParentalConsent



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
				  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)context.getBean("myTemplatesDao");
				  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
				  if(myTemplates!=null) {
					  message = myTemplates.getContent();
				  }
				}
		  }
			
			
			
		}
		//logger.debug("-----------email----------"+tempContact.getEmailId());

		message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				.replace("[senderReplyToEmailID]", user.getEmailId());

		EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active", contact.getEmailId(), contact.getUsers(),new Date(),
				"Welcome Mail", null, null, null, contact.getContactId());

		//testEmailQueue.setChildEmail(childEmail);
		//			logger.debug("testEmailQueue"+testEmailQueue.getChildEmail());
		//emailQueueDao.saveOrUpdate(testEmailQueue);
		emailQueueDaoForDML.saveOrUpdate(testEmailQueue);


	}//sendWelcomeEmail



	//getMastToTransMappings
	private  void getMastToTransMappings( MailingList mailingList,Long userId ){

		MastersToTransactionMappingsDao mastersToTransactionMappingsDao =(MastersToTransactionMappingsDao)context.getBean("mastersToTransactionMappingsDao");
		List<MastersToTransactionMappings> mastList = mastersToTransactionMappingsDao.findByUserId(userId);
		if(mastList == null || mastList.size() == 0){
			return;
		}
		//logger.debug(">>>>>>>>>>>>>>>>>> mast To trans List is >>>>>>>>>>>>>>>>>>>>"+mastList.size());
		Set<MastersToTransactionMappings> mastToTransSet=new HashSet<MastersToTransactionMappings>();
		for (MastersToTransactionMappings eachObj : mastList) {
			mastToTransSet.add(eachObj);
		}

		//retailProsalesDao.updateSalesToContactsMappings(mastToTransSet, mailingList.getListId(), userId);
		retailProsalesDaoForDML.updateSalesToContactsMappings(mastToTransSet, mailingList.getListId(), userId, startSalesId, endSalesId);

	}//getMastToTransMappings

	public String performMobileOptin(Contacts contact, Users currentUser, String mobileStr) {

		if(!currentUser.isEnableSMS() || !currentUser.isConsiderSMSSettings() ) {

			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		//Users currentUser = contact.getUsers();

		if(smsSettings == null || ocsmsGateway == null) {

			noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
					"SMS may not be sent to the mobile contacts.";

			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;

		}


		//TODO need to set lastSMSdate and mobile optin values to null if it is 
		//enabled with smsDouble-optin and medium is matched  
		boolean isDifferentMobile = false;
		//String mobile = mPhoneIBoxId.getValue()+"";
		String conMobile = contact.getMobilePhone();
		//to identify whether entered one is same as previous mobile
		if(conMobile != null ) {
			if(!mobileStr.equals(conMobile)) {

				if( (mobileStr.length() < conMobile.length() && !conMobile.endsWith(mobileStr) ) ||
						(conMobile.length() < mobileStr.length() && !mobileStr.endsWith(conMobile)) || mobileStr.length() == conMobile.length()) {

					isDifferentMobile = true;

				}

			}

		}//if

		//do only when the existing phone number is not same with the entered
		byte optin = 0;
		String phone = contact.getMobilePhone();
		String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;

		if(!isDifferentMobile && contact.getMobileStatus() != null && contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)){

			mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			return mobileStatus;

		}

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

		Byte userOptinVal =	smsSettings.getOptInMedium();

		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(currentUser.getCountryType()) && currentUser.getOptInMedium() != null) ? 
				currentUser.getOptInMedium() : userOptinVal;


				if(smsSettings.isEnable() && 
						userOptinVal != null && 
						(userOptinVal.byteValue() & optin ) > 0 ) {		
					//TODO after the above todo done consider only one among these two conditions on contact
					if((contact.getLastSMSDate() == null && contact.isMobileOptin() != true) ||
							( isDifferentMobile) ) {

						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
						contact.setMobileStatus(mobileStatus);
						contact.setLastSMSDate(Calendar.getInstance());
						contact.setMobileOptin(false);
						optinMobileSet.add(mobileStr);



						if(optinMobileSet.size() >= 100) {


							try {
								if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, optinMobileSet.size())) {

									if(logger.isDebugEnabled()) logger.debug("low credits with clickatell");
									optinMobileSet.clear();
									return mobileStatus;
								}
							} catch (BaseServiceException e) {
								// TODO Auto-generated catch block
								logger.error(" Exception message is ", e.getMessage());
							}
							if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : 
								currentUser.getUsedSmsCount() ) ) >=  optinMobileSet.size()) {

								captiwayToSMSApiGateway.sendMultipleMobileDoubleOptin(ocsmsGateway, optinMobileSet,smsSettings);

								//clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID), mobileStr, msgContent);
								/*if(currentUser.getParentUser() != null) {
							currentUser = currentUser.getParentUser();
						}*/
								/*currentUser.setUsedSmsCount((currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount()) +optinMobileSet.size());
						usersDao.saveOrUpdate(currentUser);*/
								//usersDao.updateUsedSMSCount(currentUser.getUserId(), optinMobileSet.size());
								usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), optinMobileSet.size());
								

								/**
								 * Update SMS Queue.
								 */
								SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
								String mobileNumbers = getCommaSepratedMobileNumber(optinMobileSet);
								smsQueueHelper.updateSMSQueue(mobileNumbers, smsSettings.getAutoResponse(), smsSettings.getType(),  users, smsSettings.getSenderId());
								
								
							}else {
								if(logger.isDebugEnabled()) logger.debug("low credits with user...");
								optinMobileSet.clear();
								if(noSMSCredits == null) {

									noSMSCredits = "Please renew your SMS package.";
									Messages messages = new Messages("Contact" ,"SMS credits are low." ,"Total mobile contacts:"+mobileContacts+" ,"+noSMSCredits,
											Calendar.getInstance(),"Inbox",false ,"Info", currentUser); 

									//messagesDao.saveOrUpdate(messages);
									messagesDaoForDML.saveOrUpdate(messages);
								}


								return mobileStatus;
							}

							optinMobileSet.clear();
						}

						/*if(!clickaTellApi.getBalance(1)) {

					logger.debug("low credits with clickatell");
					return mobileStatus;
				}

				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {

					//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");

					String msgContent = smsSettings.getKeywordResponse();
					if(msgContent != null) {

						msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
					}
					CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");

					clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID),
							phone, msgContent, couponCodesDao);

					if(currentUser.getParentUser() != null) {
						currentUser = currentUser.getParentUser();
					}
					currentUser.setUsedSmsCount(currentUser.getUsedSmsCount()+1);
					usersDao.saveOrUpdate(currentUser);
				}else {
					logger.debug("low credits with user...");
					return mobileStatus;

				}*/

					}//if
				}//if

				else {

					if(contact.getMobilePhone() != null && isDifferentMobile) {

						mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
						contact.setMobileStatus(mobileStatus);

						contact.setMobileOptin(false);
					}
				}

				return mobileStatus;


	}

	private boolean isValidEntryFromLineStr(TreeMap<String, List<String>> prioMap, String[] nextLineStr) {
		boolean isValidEntryFlag = false;

		Set<String> keySet = prioMap.keySet();
		List<String> tempList=null;
		outer: for (String eachStr : keySet) {

			tempList = prioMap.get(eachStr);
			for (String listStr : tempList) {

				int index = Integer.parseInt(listStr.substring(0,listStr.indexOf('|')));
				String	valStr = null;
				if((valStr = nextLineStr[index].trim()).length() !=0 ) {
					isValidEntryFlag = true;
					return isValidEntryFlag;
				}
			}
		}
		return isValidEntryFlag;
	}

	private void saveInEmailqueuse(String type, Users users, String unproccessedFileName){
		
		String status = Constants.MAIL_SENT_STATUS_ACTIVE;
		String toEmailIdStr = PropertyUtil.getPropertyValueFromDB("teamCSEmailId");
		
		String message = PropertyUtil.getPropertyValueFromDB("fileUploadFailedMessageTemplate");
		String subject = PropertyUtil.getPropertyValueFromDB("fileUploadFailedSubject");
		
		
		if(type.equals(Constants.CONTACTS_FILE_UPLOAD_FAILED)){
			subject = subject.replace(Constants.FILE_TYPE, Constants.CONTACTS_FILE);
			message = message.replace(Constants.FILE_TYPE, Constants.CONTACTS_FILE);
		}else if(type.equals(Constants.SKU_FILE_UPLOAD_FAILED)){
			subject = subject.replace(Constants.FILE_TYPE, Constants.SKU_FILE);
			message = message.replace(Constants.FILE_TYPE, Constants.SKU_FILE);
		}else if(type.equals(Constants.SALES_FILE_UPLOAD_FAILED)){
			subject = subject.replace(Constants.FILE_TYPE, Constants.SALES_FILE);
			message = message.replace(Constants.FILE_TYPE, Constants.SALES_FILE);
		}
		
		
		String emailSubject = subject.replace(Constants.FILE_UPLOAD_FAILED_ALERT_PH_USER_NAME, users.getUserName());
		String emailMessage  = message.replace(Constants.FILE_UPLOAD_FAILED_ALERT_PH_USER_NAME, users.getUserName())
				.replace(Constants.FILE_UPLOAD_FAILED_ALERT_PH_ORG_ID, users.getUserOrganization().getOrgExternalId().toString())
				.replace(Constants.FILE_UPLOAD_FAILED_ALERT_PH_FILE_NAME, unproccessedFileName).replace(Constants.FILE_UNPROCCESSED_TIME, Calendar.getInstance().getTime()+"");

		EmailQueue alertEmailQueue = new EmailQueue(emailSubject, emailMessage, type, status, toEmailIdStr, null, new Date(), users);
		//emailQueueDao.saveOrUpdate(alertEmailQueue);
		emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);

	}

} // class
