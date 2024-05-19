package org.mq.marketer.sparkbase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.EventTriggerEvents;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SparkBaseTransactions;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.EventTriggerEventsDao;
import org.mq.marketer.campaign.dao.EventTriggerEventsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SparkBaseTransactionsDao;
import org.mq.marketer.campaign.dao.SparkBaseTransactionsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.ArrayOfTransactionBalance;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Transaction;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.TransactionBalance;
import org.mq.optculture.utils.ServiceLocator;

public class TransactionTypeEventTrigger implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public Queue<Object[]> uploadQueue = new LinkedList();

	Object[] pollObj;
	
	@Override
	public void run() {
		
		try {
			
			while(pollQueue()) {
				saveTransactionsToDb();
			}
			
			logger.debug("Trasactype event Trigger called here .........");
			processEventTriggerTransactions();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}
	
	
	
	/**
	   * Saves sparkbase transactions in optculture db.
	   * @param transList
	   * 
	   */
	  private void saveTransactionsToDb() {
		  try {
			if(pollObj == null) return;
			  
			  List<Transaction> transList = (List)pollObj[0];
				  //List<SparkBaseTransactions> sbTransList = new ArrayList<SparkBaseTransactions>();
				  SparkBaseTransactions sbTrans = null;
				  Object object = null;
				  ContactsLoyalty contactLoyalty = null;
				  List<SparkBaseTransactions> sbTransactionList = new ArrayList<SparkBaseTransactions>();
				  Map<String, ContactsLoyalty> cardTypeCLoyaltyMap = new HashMap<String, ContactsLoyalty>();
				  
				  ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
				  
				  
				  for (Transaction eachTrns : transList) {
			      	if(eachTrns.getType().equals(Constants.SB_TRANSACTION_INQUIRY) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_ACCOUNT_HISTORY)) {
			      		continue; // Skip these transaction types
			      	}
			      	
			      	if(eachTrns.getType().equals(Constants.SB_TRANSACTION_ADJUSTMENT) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_ENROLLMENT) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_GIFT_ISSUANCE) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_GIFT_REDEMPTION) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_LOYALTY_ISSUANCE) ||
			      			eachTrns.getType().equals(Constants.SB_TRANSACTION_LOYALTY_REDEMPTION)){
			      
			      			sbTrans = new SparkBaseTransactions();
			      			try{
			      				if(eachTrns.getAmountEntered() != null && !eachTrns.getAmountEntered().trim().isEmpty())
			      				sbTrans.setAmountEntered(Double.parseDouble(eachTrns.getAmountEntered()));
			      			}catch(Exception ex){
			      				//logger.error("Exception in paring sparkbase transaction amountentered." , ex);
			      			}
			      			sbTrans.setCardId(eachTrns.getCardId());
			      			sbTrans.setCreatedDate(Calendar.getInstance());
			      			
			      			sbTrans.setLocationId(eachTrns.getLocationId());
			      			sbTrans.setLocationName(eachTrns.getLocationName());
			      			
			      			Calendar procTimeCal = Calendar.getInstance();
			      			DateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
			      			try {
								procTimeCal.setTime(sdf.parse(eachTrns.getProcessedTime()));
							} catch (ParseException ex) {
								//logger.error("Exception ::", e1);
								//logger.error("exception in parsing sb transaction processed time.", ex);
							}
			      			sbTrans.setProcessedTime(procTimeCal);
			      			
			      			
			      			//set server time
			      			Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.US);
			      			cal1.setTime(sdf.parse(eachTrns.getProcessedTime()));
			      			
			      			Calendar serverTimeCal = Calendar.getInstance();
			      			serverTimeCal.setTime(cal1.getTime());
			      			logger.debug(" spark base time in string is :: "+eachTrns.getProcessedTime());
			      			logger.debug("sb cal = "+cal1.getTime());
			      			logger.debug("serverl cal ="+serverTimeCal.getTime());
			      			
			      			sbTrans.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_NEW);
			      			sbTrans.setType(eachTrns.getType());
			      			sbTrans.setTransactionId(eachTrns.getTransactionId());
			      			
			      			ArrayOfTransactionBalance atb = eachTrns.getBalances();
			      			List<TransactionBalance> transBalList = atb.getTransactionBalances();
			      			
			      			for (TransactionBalance tbl : transBalList){
			      				try{
			      					sbTrans.setDifference(Double.parseDouble(tbl.getDifference()));
			      				}catch(Exception e){
			      					//logger.error("Exception in sparkbase transaction difference.", e);
			      				}
			      			}
			      			
			      			contactLoyalty = new ContactsLoyalty();
			      			object = SparkBaseAdminService.fetchData(SparkBaseAdminService.BALANCESVIEW, eachTrns.getAccountId(), contactLoyalty,true);
							if(object == null) {
								 continue;
							}
			      			sbTrans.setGiftcardBalance(contactLoyalty.getGiftcardBalance());
			      			sbTrans.setLoyaltyBalance(contactLoyalty.getLoyaltyBalance());
			      			
			      			ContactsLoyalty contLoyObj = null;
			      			String statusStr = "";
			      			
			      			if(cardTypeCLoyaltyMap.containsKey(sbTrans.getCardId().trim())) {
			      				contLoyObj = cardTypeCLoyaltyMap.get(sbTrans.getCardId());
			      			}else {
			      				try {
			      					List<ContactsLoyalty> contLoyList = contactsLoyaltyDao.getContactLoyatyByCardnumber(Long.parseLong(sbTrans.getCardId()));
			      					
			      					if(contLoyList != null && contLoyList.size() > 0) { 
			      						//If more than one CL Objects exist set the status as suspend
			      						if(contLoyList.size() > 1) statusStr = Constants.SPARKBASE_TRANSACTION_STATUS_SUSPEND;
			      						else {
			      							logger.debug(">>> contLoyList size is ::"+contLoyList.size());
			      							contLoyObj = contLoyList.get(0);
				      						cardTypeCLoyaltyMap.put(""+contLoyObj.getCustomerId(), contLoyObj);
			      						}
			      					}
								} catch (Exception e) {
									logger.error("Exception ::", e);
								}
			      			}
			      			
			      			
			      			if(contLoyObj != null) {
			      				//set Contact Id
			      				sbTrans.setContactId(contLoyObj.getContact().getContactId());
			      				
			      				//set User Id
			      				sbTrans.setUserId(contLoyObj.getUserId());
			      				
			      				statusStr = Constants.SPARKBASE_TRANSACTION_STATUS_NEW;
			      				
			      			}else if(statusStr != null && statusStr.trim().length() == 0) {
			      				statusStr = Constants.SPARKBASE_TRANSACTION_STATUS_UNPROCESSED;
			      			}
			      			
			      			//set Status
			      			sbTrans.setStatus(statusStr);
			      			try{
			      			 saveSbTransaction(sbTrans);
			      			}catch(Exception ex){
			      				try{
			      					logger.info("Exception in saving sb transaction.",  ex);
			      				}catch(EmptyStackException empt){
			      					logger.info("Empty StackException...........................");
			      				}
			      				
			      				
			      				
			      				//logger.debug("exception in saving ....");//
			      				//continue;
			      			}
			      			//logger.debug("After exception..................");
			      			//sbTransList.add(sbTrans);
						}
					}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	  } // saveTransactionsToDb
	
	  private static void saveSbTransaction(SparkBaseTransactions sbTransaction) throws Exception{
  		SparkBaseTransactionsDao sparkbaseTransactionsDao = null;
  		SparkBaseTransactionsDaoForDML sparkbaseTransactionsDaoForDML = null;
		sparkbaseTransactionsDao = (SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
		sparkbaseTransactionsDaoForDML= (SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");

		//sparkbaseTransactionsDao.save(sbTransaction);
		sparkbaseTransactionsDaoForDML.save(sbTransaction);
	  		
	  } // saveSbTransaction
	  
	
	private void processEventTriggerTransactions() throws Exception {
		
		SparkBaseTransactionsDao sparkBaseTransactionsDao = (SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
		List<SparkBaseTransactions> newStatusTrasactionList = sparkBaseTransactionsDao.findAllNewTransaction();
		
		if(newStatusTrasactionList == null || newStatusTrasactionList.size() == 0) return;
		
		logger.debug("newStatusTrasactionList size is  ::"+newStatusTrasactionList.size());
		Map<Long,List<SparkBaseTransactions>> userBasedTransmap = new HashMap<Long,List<SparkBaseTransactions>>();
		List<SparkBaseTransactions> userTransList = null;
		
		//for preparing UserId based Transaction
		for (SparkBaseTransactions eachTransObj : newStatusTrasactionList) {
			
			if (userBasedTransmap.containsKey(eachTransObj.getUserId())) {
				userTransList = userBasedTransmap.get(eachTransObj.getUserId());
				if(userTransList == null) userTransList= new ArrayList<SparkBaseTransactions>();
				userTransList.add(eachTransObj);
			}else {
				userTransList = userBasedTransmap.get(eachTransObj.getUserId());
				if(userTransList == null) userTransList= new ArrayList<SparkBaseTransactions>();
				userTransList.add(eachTransObj);
			}
			
			userBasedTransmap.put(eachTransObj.getUserId(), userTransList);
		} // for
		
		//Each User Id based Event Trigger
		Set<Long> userKeySet = userBasedTransmap.keySet();
		
		for (Long eachUserId : userKeySet) {
			userTransList = userBasedTransmap.get(eachUserId);
			if(userTransList == null || userTransList.size()==0) continue;
			List<SparkBaseTransactions> issuTransaList = new ArrayList<SparkBaseTransactions>();
			List<SparkBaseTransactions> redemptTransaList = new ArrayList<SparkBaseTransactions>();
			List<SparkBaseTransactions> adjustTransaList = new ArrayList<SparkBaseTransactions>();
			
			for (SparkBaseTransactions eachSBTransObj : userTransList) {
				if(eachSBTransObj.getType().trim().equals(Constants.SB_TRANSACTION_LOYALTY_ISSUANCE) || 
						eachSBTransObj.getType().trim().equals(Constants.SB_TRANSACTION_GIFT_ISSUANCE)){
					issuTransaList.add(eachSBTransObj);
				}else if(eachSBTransObj.getType().trim().equals(Constants.SB_TRANSACTION_LOYALTY_REDEMPTION) || 
						eachSBTransObj.getType().trim().equals(Constants.SB_TRANSACTION_GIFT_REDEMPTION)){
					redemptTransaList.add(eachSBTransObj);
				}else if(eachSBTransObj.getType().trim().equals(Constants.SB_TRANSACTION_ADJUSTMENT)){
					adjustTransaList.add(eachSBTransObj);
				}
				
			}
			
			
			//Issuance Trigger
			if(issuTransaList != null && issuTransaList.size() > 0){
//				logger.debug(" issuance ");
				issuanceTransList(eachUserId,issuTransaList);
			}
			
			/*//Redemption Trigger
			if(redemptTransaList != null && redemptTransaList.size() > 0) {
				logger.debug(" redemtion ");
				redemptionTransList(eachUserId,redemptTransaList);
			}
			
			//Adjustment Trigger
			if(adjustTransaList != null && adjustTransaList.size() > 0){
				logger.debug(" adjsutment ");
				adjustmentTransaList(eachUserId,adjustTransaList);
			}*/
			
		} // for
		
		
		
		
	}// processEventTriggerTransactions
	
	private void issuanceTransList(Long userId, List<SparkBaseTransactions> issueanceList) {
		
		try {
//			List<SparkBaseTransactions> unProcessedTrlist = new ArrayList<SparkBaseTransactions>();
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
			SparkBaseTransactionsDao sparkBaseTransactionsDao = 
					(SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
			SparkBaseTransactionsDaoForDML sparkBaseTransactionsDaoForDML = 
					(SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
			
			Users user = usersDao.find(userId);
//			logger.debug("User Dao user id is ::"+user);
			
			if(user == null || user.getClientTimeZone() == null || user.getClientTimeZone().trim().length() == 0) {
				String startAndEndIdxIdStr = issueanceList.get(0).getId()+","+issueanceList.get(issueanceList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(issueanceList);
				
				logger.info("No User exists/Client TimeZone not Existed for the userId "+userId+" "
						+ " So unprocessed the all Issuance type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
			} // if User not exist/Timezone not exist
			
			String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
			int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			int timezoneDiffrenceMinutesInt = 0;
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
			
			
			String contactIdStr  = "";
			Map<Long,List<SparkBaseTransactions>> contBaseTransactionMap = new HashMap<Long,List<SparkBaseTransactions>>();
			List<SparkBaseTransactions> issuTrnsactionList = null;
			
			//For preparing contact based Trsaction List
			for (SparkBaseTransactions sbTransactionObj : issueanceList) {
				
				if(sbTransactionObj.getContactId() == null) {
					logger.debug("Contact Id is not Updated"+sbTransactionObj.getId());
					continue;
				}
				if(contactIdStr.trim().length() == 0)
					contactIdStr = ""+sbTransactionObj.getContactId();
				else contactIdStr += ","+sbTransactionObj.getContactId();
				
				if(contBaseTransactionMap.containsKey(sbTransactionObj.getContactId())) {
					issuTrnsactionList = contBaseTransactionMap.get(sbTransactionObj.getContactId());
					if(issuTrnsactionList == null )issuTrnsactionList= new ArrayList<SparkBaseTransactions>();
				}else{
					issuTrnsactionList = new ArrayList<SparkBaseTransactions>();
				}
				
				issuTrnsactionList.add(sbTransactionObj);
				contBaseTransactionMap.put(sbTransactionObj.getContactId(), issuTrnsactionList);
				
			} // for
			
			if(contactIdStr == null || contactIdStr.trim().length() == 0) {
				
				String startAndEndIdxIdStr = issueanceList.get(0).getId()+","+issueanceList.get(issueanceList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(issueanceList);
				
				logger.info("No Contact Id is existed for this Set of Transaction for this user"+userId+" "
						+ " So unprocessed the all Issuance type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
			
			List<Contacts> contList = contactsDao.findContactBycidStr(contactIdStr);
			
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName("eventTriggerDao");
			List<EventTrigger> issuanceETList = eventTriggerDao.findAllETByUserAndType(userId, Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			
			if(issuanceETList == null || issuanceETList.size() == 0) {
				
				logger.debug("No Issuance Event trigger for this User :"+userId);
				String startAndEndIdxIdStr = issueanceList.get(0).getId()+","+issueanceList.get(issueanceList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(issueanceList);
				
				logger.info("No Issuance Event trigger for this User "+userId+" "
						+ " So unprocessed the all Issuance type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			
			
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName("mailingListDao");
			EventTriggerEventsDao eventTriggerEventsDao = (EventTriggerEventsDao)ServiceLocator.getInstance().getDAOByName("eventTriggerEventsDao");
			EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML = (EventTriggerEventsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("eventTriggerEventsDaoForDML");

			
			for (EventTrigger eachET : issuanceETList) {
				
				logger.debug("eachET id is >>>>>"+eachET.getId());
				long optionsFlag = eachET.getOptionsFlag();
				logger.debug("<<<<<<<<<<1>>>>>>>>");
				if( (optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) != Constants.ET_TRIGGER_IS_ACTIVE_FLAG) continue;
				logger.debug("<<<<<<<<<<2>>>>>>>>");
				//Set<MailingList> mlSet = eventTrigger.getMailingLists();
				List<MailingList> configuredList = mailingListDao.findAllTriggerLists(eachET.getId());
				logger.debug("<<<<<<<<<<3>>>>>>>>");
				
				if(configuredList == null) {
					logger.debug("No lists are configured to this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<< 4 >>>>>>>>");
				
				Set<MailingList> mlSet = new HashSet<MailingList>();
				mlSet.addAll(configuredList);
				long mlbits = Utility.getMlsBit(mlSet);
				
				if(mlbits <= 0) {
					logger.debug("no mlbits are found for this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<<5>>>>>>>>");
				String inputStr = eachET.getInputStr();
				String[] qryArrStr = inputStr.split("\\|");
					
				for (Contacts eachCont : contList) {
					logger.debug("<<<<<<<<<<6 >>>>>>>>"+eachCont.getContactId());
					Long contMlBits = eachCont.getMlBits();
					logger.debug("contBaseTransactionMap is ::"+contBaseTransactionMap);
					List<SparkBaseTransactions> contBaseIssueTrList = contBaseTransactionMap.get(eachCont.getContactId());
					logger.debug(" contact lbite is "+contMlBits+" mlbits is ::"+mlbits);
					logger.debug(" contMlBits & mlbits is ::"+(contMlBits & mlbits));
					
					if((contMlBits & mlbits) > 0){
//						List<EventTriggerEvents> evnetTrEventList = new ArrayList<EventTriggerEvents>();
						
						boolean isTranMatchFlag = false;
						//its Existed in the List
						for (SparkBaseTransactions eachIssuTr : contBaseIssueTrList) {
							logger.debug("eachIssuTr id is :: "+eachIssuTr.getId());
							Double transactionrBalence = null;
							logger.debug("0 is ::"+qryArrStr[0]+" 1 is ::"+qryArrStr[1]+" 2 is ::"+qryArrStr[2]);
							if(qryArrStr[0].trim().equals("loyalty.loyalty_balance")){
								transactionrBalence =  eachIssuTr.getLoyaltyBalance();
								
							}else if(qryArrStr[0].trim().equals("loyalty.giftcard_balance")){
								transactionrBalence = eachIssuTr.getGiftcardBalance();
							}
							logger.debug("triggerBalence ids :: "+transactionrBalence);
							isTranMatchFlag = false;
							
							if(qryArrStr[1].trim().contains(">") && transactionrBalence != null){ //greater than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								
								
								
								if(transactionrBalence > value ) {
								
									isTranMatchFlag = true;
									
								}
								
							}else if(qryArrStr[1].trim().contains("<") && transactionrBalence != null ){ // less than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(transactionrBalence < value ) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("=")  && transactionrBalence != null){ // Equal
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(value == transactionrBalence) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("between")  && transactionrBalence != null){ //Between
								
								Double value1 = Double.parseDouble(qryArrStr[2].trim());
								Double value2 = Double.parseDouble(qryArrStr[3].trim());
								
								if(value1 <= transactionrBalence  && transactionrBalence <= value2){
//									eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
									isTranMatchFlag = true;
								}
							}else {// Is Null
								
								if(transactionrBalence == null) {
									isTranMatchFlag = true;
								}
																	
							}
							
							//Fetch same object for is any associate Trigger match
							eachIssuTr = sparkBaseTransactionsDao.find(eachIssuTr.getId());
							logger.debug(" fetach data once again :: "+eachIssuTr);
							logger.debug(" isTranMatchFlag is  :: "+isTranMatchFlag);
							if(isTranMatchFlag) {
								
								byte eventEmailStatus = Constants.ET_EMAIL_STATUS_NO_ACTION;
								byte eventSMSStatus = Constants.ET_SMS_STATUS_NO_ACTION;
								
								if( (optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG ) == Constants.ET_SEND_CAMPAIGN_FLAG){
									eventEmailStatus +=  Constants.ET_EMAIL_STATUS_ACTIVE;
								}
								
								if( (optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG) {
									eventSMSStatus +=  Constants.ET_SMS_STATUS_ACTIVE;
								}
								
								
								//TODO create Event Trigger Event for this Transactions
								EventTriggerEvents eventTriggerObj = new EventTriggerEvents();
								//event_trigger_id
								eventTriggerObj.setEventTriggerId(eachET.getId());
								//created_time
								eventTriggerObj.setCreatedTime(Calendar.getInstance());
								logger.debug(eachIssuTr.getId()+" and  eachIssuTr.getProcessedTime() is ::"+eachIssuTr.getProcessedTime());
								//event_time
								Calendar eventTimeCal = eachIssuTr.getProcessedTime();
								eventTimeCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								eventTriggerObj.setEventTime(eventTimeCal);
								//user_id
								eventTriggerObj.setUserId(userId);
								//event_category
								eventTriggerObj.setEventCategory("LoyaltyBalance");
								
								//source_id
								eventTriggerObj.setSourceId(eachIssuTr.getId());
								//contact_id
								eventTriggerObj.setContactId(eachCont.getContactId());
								//trigger_type
								eventTriggerObj.setTriggerType(Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
								
								//email_status
								eventTriggerObj.setEmailStatus(eventEmailStatus);
								//sms_status
								eventTriggerObj.setSmsStatus(eventSMSStatus);
								//tr_condition
								eventTriggerObj.setTriggerCondition(eachET.getInputStr());
								
								try {
									//save ETEvent in to DB
									//eventTriggerEventsDao.saveOrUpdate(eventTriggerObj);
									eventTriggerEventsDaoForDML.saveOrUpdate(eventTriggerObj);
								} catch (Exception e) {
									logger.error("Exception ::", e);
								}
								
								String triggerIds = eachIssuTr.getTriggerIds();
								
								if(triggerIds != null ){
									triggerIds = triggerIds.trim().length() > 0 ? triggerIds+","+eachET.getId() : ""+eachET.getId();
//										eachET.getId()
								}else {
									triggerIds = ""+eachET.getId();
								}
								eachIssuTr.setTriggerIds(triggerIds);
								
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
							
							}
							
							if(eachIssuTr.getStatus().equals(Constants.SPARKBASE_TRANSACTION_STATUS_NEW)){
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_UNPROCESSED);
							}
							
							//sparkBaseTransactionsDao.saveOrUpdate(eachIssuTr);
							sparkBaseTransactionsDaoForDML.saveOrUpdate(eachIssuTr);

							
						} // for Each ContactId Based Transaction
					
					}else{
						
						String startAndEndIdxIdStr = contBaseIssueTrList.get(0).getId()+","+contBaseIssueTrList.get(issueanceList.size()-1).getId();
						
						moveToUnprocessedToTrasactionLst(issueanceList);
						
						logger.info("No Issuance Event trigger for this conatct "+eachCont.getContactId()+" "
								+ " So unprocessed the all Issuance type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
						
						moveToUnprocessedToTrasactionLst(contBaseIssueTrList);
						
					}
					
				} // for
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Problem Occured with getting Event trigger with the users :"+userId +  e);
			
		}
	} // issuanceTransList
	
	private void redemptionTransList(Long userId,List<SparkBaseTransactions> redemptionList) {
		
		try {
//			List<SparkBaseTransactions> unProcessedTrlist = new ArrayList<SparkBaseTransactions>();
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
			SparkBaseTransactionsDao sparkBaseTransactionsDao = 
					(SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
			SparkBaseTransactionsDaoForDML sparkBaseTransactionsDaoForDML = 
					(SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
			Users user = usersDao.find(userId);
//			logger.debug("User Dao user id is ::"+user);
			
			if(user == null || user.getClientTimeZone() == null || user.getClientTimeZone().trim().length() == 0) {
				String startAndEndIdxIdStr = redemptionList.get(0).getId()+","+redemptionList.get(redemptionList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(redemptionList);
				
				logger.info("No User exists/Client TimeZone not Existed for the userId "+userId+" "
						+ " So unprocessed the all Redemption type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
			} // if User not exist/Timezone not exist
			
			String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
			int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			int timezoneDiffrenceMinutesInt = 0;
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
			
			
			String contactIdStr  = "";
			Map<Long,List<SparkBaseTransactions>> contBaseTransactionMap = new HashMap<Long,List<SparkBaseTransactions>>();
			List<SparkBaseTransactions> issuTrnsactionList = null;
			
			//For preparing contact based Trsaction List
			for (SparkBaseTransactions sbTransactionObj : redemptionList) {
				
				if(sbTransactionObj.getContactId() == null) {
					logger.debug("Contact Id is not Updated"+sbTransactionObj.getId());
					continue;
				}
				if(contactIdStr.trim().length() == 0)
					contactIdStr = ""+sbTransactionObj.getContactId();
				else contactIdStr += ","+sbTransactionObj.getContactId();
				
				if(contBaseTransactionMap.containsKey(sbTransactionObj.getContactId())) {
					issuTrnsactionList = contBaseTransactionMap.get(sbTransactionObj.getContactId());
					if(issuTrnsactionList == null )issuTrnsactionList= new ArrayList<SparkBaseTransactions>();
				}else{
					issuTrnsactionList = new ArrayList<SparkBaseTransactions>();
				}
				
				issuTrnsactionList.add(sbTransactionObj);
				contBaseTransactionMap.put(sbTransactionObj.getContactId(), issuTrnsactionList);
				
			} // for
			
			if(contactIdStr == null || contactIdStr.trim().length() == 0) {
				
				String startAndEndIdxIdStr = redemptionList.get(0).getId()+","+redemptionList.get(redemptionList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(redemptionList);
				
				logger.info("No Contact Id is existed for this Set of Transaction for this user"+userId+" "
						+ " So unprocessed the all Redemption type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
			
			List<Contacts> contList = contactsDao.findContactBycidStr(contactIdStr);
			
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName("eventTriggerDao");
			List<EventTrigger> redemtionETList = eventTriggerDao.findAllETByUserAndType(userId, Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
			
			if(redemtionETList == null || redemtionETList.size() == 0) {
				
				logger.debug("No Issuance Event trigger for this User :"+userId);
				String startAndEndIdxIdStr = redemptionList.get(0).getId()+","+redemptionList.get(redemptionList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(redemptionList);
				
				logger.info("No Issuance Event trigger for this User "+userId+" "
						+ " So unprocessed the all Redemption type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			
			
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName("mailingListDao");
			EventTriggerEventsDao eventTriggerEventsDao = (EventTriggerEventsDao)ServiceLocator.getInstance().getDAOByName("eventTriggerEventsDao");
			EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML = (EventTriggerEventsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("eventTriggerEventsDaoForDML");

			
			for (EventTrigger eachET : redemtionETList) {
				
				logger.debug("eachET id is >>>>>"+eachET.getId());
				long optionsFlag = eachET.getOptionsFlag();
				logger.debug("<<<<<<<<<<1>>>>>>>>");
				if( (optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) != Constants.ET_TRIGGER_IS_ACTIVE_FLAG) continue;
				logger.debug("<<<<<<<<<<2>>>>>>>>");
				//Set<MailingList> mlSet = eventTrigger.getMailingLists();
				List<MailingList> configuredList = mailingListDao.findAllTriggerLists(eachET.getId());
				logger.debug("<<<<<<<<<<3>>>>>>>>");
				
				if(configuredList == null) {
					logger.debug("No lists are configured to this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<< 4 >>>>>>>>");
				
				Set<MailingList> mlSet = new HashSet<MailingList>();
				mlSet.addAll(configuredList);
				long mlbits = Utility.getMlsBit(mlSet);
				
				if(mlbits <= 0) {
					logger.debug("no mlbits are found for this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<<5>>>>>>>>");
				String inputStr = eachET.getInputStr();
				String[] qryArrStr = inputStr.split("\\|");
					
				for (Contacts eachCont : contList) {
					logger.debug("<<<<<<<<<<6 >>>>>>>>"+eachCont.getContactId());
					Long contMlBits = eachCont.getMlBits();
					logger.debug("contBaseTransactionMap is ::"+contBaseTransactionMap);
					List<SparkBaseTransactions> contBaseRedemptionTrList = contBaseTransactionMap.get(eachCont.getContactId());
					logger.debug(" contact lbite is "+contMlBits+" mlbits is ::"+mlbits);
					logger.debug(" contMlBits & mlbits is ::"+(contMlBits & mlbits));
					
					if((contMlBits & mlbits) > 0){
//						List<EventTriggerEvents> evnetTrEventList = new ArrayList<EventTriggerEvents>();
						
						boolean isTranMatchFlag = false;
						//its Existed in the List
						for (SparkBaseTransactions eachIssuTr : contBaseRedemptionTrList) {
							logger.debug("eachIssuTr id is :: "+eachIssuTr.getId());
							Double transactionrBalence = null;
							logger.debug("0 is ::"+qryArrStr[0]+" 1 is ::"+qryArrStr[1]+" 2 is ::"+qryArrStr[2]);
							if(qryArrStr[0].trim().equals("loyalty.loyalty_balance")){
								transactionrBalence =  eachIssuTr.getLoyaltyBalance();
								
							}else if(qryArrStr[0].trim().equals("loyalty.giftcard_balance")){
								transactionrBalence = eachIssuTr.getGiftcardBalance();
							}
							logger.debug("triggerBalence ids :: "+transactionrBalence);
							isTranMatchFlag = false;
							
							if(qryArrStr[1].trim().contains(">") && transactionrBalence != null){ //greater than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								
								
								
								if(transactionrBalence > value ) {
								
									isTranMatchFlag = true;
									
								}
								
							}else if(qryArrStr[1].trim().contains("<") && transactionrBalence != null ){ // less than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(transactionrBalence < value ) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("=")  && transactionrBalence != null){ // Equal
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(value == transactionrBalence) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("between")  && transactionrBalence != null){ //Between
								
								Double value1 = Double.parseDouble(qryArrStr[2].trim());
								Double value2 = Double.parseDouble(qryArrStr[3].trim());
								
								if(value1 <= transactionrBalence  && transactionrBalence <= value2){
//									eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
									isTranMatchFlag = true;
								}
							}else {// Is Null
								
								if(transactionrBalence == null) {
									isTranMatchFlag = true;
								}
																	
							}
							
							//Fetch same object for is any associate Trigger match
							eachIssuTr = sparkBaseTransactionsDao.find(eachIssuTr.getId());
							logger.debug(" fetach data once again :: "+eachIssuTr);
							logger.debug(" isTranMatchFlag is  :: "+isTranMatchFlag);
							if(isTranMatchFlag) {
								
								byte eventEmailStatus = Constants.ET_EMAIL_STATUS_NO_ACTION;
								byte eventSMSStatus = Constants.ET_SMS_STATUS_NO_ACTION;
								
								if( (optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG ) == Constants.ET_SEND_CAMPAIGN_FLAG){
									eventEmailStatus +=  Constants.ET_EMAIL_STATUS_ACTIVE;
								}
								
								if( (optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG) {
									eventSMSStatus +=  Constants.ET_SMS_STATUS_ACTIVE;
								}
								
								
								//TODO create Event Trigger Event for this Transactions
								EventTriggerEvents eventTriggerObj = new EventTriggerEvents();
								//event_trigger_id
								eventTriggerObj.setEventTriggerId(eachET.getId());
								//created_time
								eventTriggerObj.setCreatedTime(Calendar.getInstance());
								logger.debug(eachIssuTr.getId()+" and  eachIssuTr.getProcessedTime() is ::"+eachIssuTr.getProcessedTime());
								//event_time
								Calendar eventTimeCal = eachIssuTr.getProcessedTime();
								eventTimeCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								eventTriggerObj.setEventTime(eventTimeCal);
								//user_id
								eventTriggerObj.setUserId(userId);
								//event_category
								eventTriggerObj.setEventCategory("LoyaltyBalance");
								
								//source_id
								eventTriggerObj.setSourceId(eachIssuTr.getId());
								//contact_id
								eventTriggerObj.setContactId(eachCont.getContactId());
								//trigger_type
								eventTriggerObj.setTriggerType(Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
								
								//email_status
								eventTriggerObj.setEmailStatus(eventEmailStatus);
								//sms_status
								eventTriggerObj.setSmsStatus(eventSMSStatus);
								//tr_condition
								eventTriggerObj.setTriggerCondition(eachET.getInputStr());
								
								try {
									//save ETEvent in to DB
									//eventTriggerEventsDao.saveOrUpdate(eventTriggerObj);
									eventTriggerEventsDaoForDML.saveOrUpdate(eventTriggerObj);

								} catch (Exception e) {
									logger.error("Exception ::", e);
								}
								
								String triggerIds = eachIssuTr.getTriggerIds();
								
								if(triggerIds != null ){
									triggerIds = triggerIds.trim().length() > 0 ? triggerIds+","+eachET.getId() : ""+eachET.getId();
//										eachET.getId()
								}else {
									triggerIds = ""+eachET.getId();
								}
								eachIssuTr.setTriggerIds(triggerIds);
								
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
							
							}
							
							if(eachIssuTr.getStatus().equals(Constants.SPARKBASE_TRANSACTION_STATUS_NEW)){
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_UNPROCESSED);
							}
							
							//sparkBaseTransactionsDao.saveOrUpdate(eachIssuTr);
							sparkBaseTransactionsDaoForDML.saveOrUpdate(eachIssuTr);

							
						} // for Each ContactId Based Transaction
					
					}else{
						
						String startAndEndIdxIdStr = contBaseRedemptionTrList.get(0).getId()+","+contBaseRedemptionTrList.get(redemptionList.size()-1).getId();
						
						moveToUnprocessedToTrasactionLst(redemptionList);
						
						logger.info("No Issuance Event trigger for this conatct "+eachCont.getContactId()+" "
								+ " So unprocessed the all Redemption type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
						
						moveToUnprocessedToTrasactionLst(contBaseRedemptionTrList);
						
					}
					
				} // for
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Problem Occured with getting Event trigger with the users ::"+userId+":", e);
		}
	} // redemptionTransList
	
	

	private void adjustmentTransaList(Long userId,List<SparkBaseTransactions> adjustList) {
		
		try {
//			List<SparkBaseTransactions> unProcessedTrlist = new ArrayList<SparkBaseTransactions>();
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
			SparkBaseTransactionsDao sparkBaseTransactionsDao = 
					(SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
			SparkBaseTransactionsDaoForDML sparkBaseTransactionsDaoForDML = 
					(SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
			
			Users user = usersDao.find(userId);
//			logger.debug("User Dao user id is ::"+user);
			
			if(user == null || user.getClientTimeZone() == null || user.getClientTimeZone().trim().length() == 0) {
				String startAndEndIdxIdStr = adjustList.get(0).getId()+","+adjustList.get(adjustList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(adjustList);
				
				logger.info("No User exists/Client TimeZone not Existed for the userId "+userId+" "
						+ " So unprocessed the all Redemption type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
			} // if User not exist/Timezone not exist
			
			String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
			int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			int timezoneDiffrenceMinutesInt = 0;
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
			
			
			String contactIdStr  = "";
			Map<Long,List<SparkBaseTransactions>> contBaseTransactionMap = new HashMap<Long,List<SparkBaseTransactions>>();
			List<SparkBaseTransactions> issuTrnsactionList = null;
			
			//For preparing contact based Trsaction List
			for (SparkBaseTransactions sbTransactionObj : adjustList) {
				
				if(sbTransactionObj.getContactId() == null) {
					logger.debug("Contact Id is not Updated"+sbTransactionObj.getId());
					continue;
				}
				if(contactIdStr.trim().length() == 0)
					contactIdStr = ""+sbTransactionObj.getContactId();
				else contactIdStr += ","+sbTransactionObj.getContactId();
				
				if(contBaseTransactionMap.containsKey(sbTransactionObj.getContactId())) {
					issuTrnsactionList = contBaseTransactionMap.get(sbTransactionObj.getContactId());
					if(issuTrnsactionList == null )issuTrnsactionList= new ArrayList<SparkBaseTransactions>();
				}else{
					issuTrnsactionList = new ArrayList<SparkBaseTransactions>();
				}
				
				issuTrnsactionList.add(sbTransactionObj);
				contBaseTransactionMap.put(sbTransactionObj.getContactId(), issuTrnsactionList);
				
			} // for
			
			if(contactIdStr == null || contactIdStr.trim().length() == 0) {
				
				String startAndEndIdxIdStr = adjustList.get(0).getId()+","+adjustList.get(adjustList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(adjustList);
				
				logger.info("No Contact Id is existed for this Set of Transaction for this user"+userId+" "
						+ " So unprocessed the all Adjustment type Transaction From to End Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName("contactsDao");
			
			List<Contacts> contList = contactsDao.findContactBycidStr(contactIdStr);
			
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName("eventTriggerDao");
			List<EventTrigger> adjustmentETList = eventTriggerDao.findAllETByUserAndType(userId, Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
			
			if(adjustmentETList == null || adjustmentETList.size() == 0) {
				
				logger.debug("No Issuance Event trigger for this User :"+userId);
				String startAndEndIdxIdStr = adjustList.get(0).getId()+","+adjustList.get(adjustList.size()-1).getId();
				
				moveToUnprocessedToTrasactionLst(adjustList);
				
				logger.info("No Issuance Event trigger for this User "+userId+" "
						+ " So unprocessed the all Redemption type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
				
				return;
				
			}
			
			
			
			MailingListDao mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName("mailingListDao");
			EventTriggerEventsDao eventTriggerEventsDao = (EventTriggerEventsDao)ServiceLocator.getInstance().getDAOByName("eventTriggerEventsDao");
			EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML = (EventTriggerEventsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("eventTriggerEventsDaoForDML");

			
			for (EventTrigger eachET : adjustmentETList) {
				
				logger.debug("eachET id is >>>>>"+eachET.getId());
				long optionsFlag = eachET.getOptionsFlag();
				logger.debug("<<<<<<<<<<1>>>>>>>>");
				if( (optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) != Constants.ET_TRIGGER_IS_ACTIVE_FLAG) continue;
				logger.debug("<<<<<<<<<<2>>>>>>>>");
				//Set<MailingList> mlSet = eventTrigger.getMailingLists();
				List<MailingList> configuredList = mailingListDao.findAllTriggerLists(eachET.getId());
				logger.debug("<<<<<<<<<<3>>>>>>>>");
				
				if(configuredList == null) {
					logger.debug("No lists are configured to this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<< 4 >>>>>>>>");
				
				Set<MailingList> mlSet = new HashSet<MailingList>();
				mlSet.addAll(configuredList);
				long mlbits = Utility.getMlsBit(mlSet);
				
				if(mlbits <= 0) {
					logger.debug("no mlbits are found for this trigger");
					continue;
				}
				logger.debug("<<<<<<<<<<5>>>>>>>>");
				String inputStr = eachET.getInputStr();
				String[] qryArrStr = inputStr.split("\\|");
					
				for (Contacts eachCont : contList) {
					logger.debug("<<<<<<<<<<6 >>>>>>>>"+eachCont.getContactId());
					Long contMlBits = eachCont.getMlBits();
					logger.debug("contBaseTransactionMap is ::"+contBaseTransactionMap);
					List<SparkBaseTransactions> contBaseRedemptionTrList = contBaseTransactionMap.get(eachCont.getContactId());
					logger.debug(" contact lbite is "+contMlBits+" mlbits is ::"+mlbits);
					logger.debug(" contMlBits & mlbits is ::"+(contMlBits & mlbits));
					
					if((contMlBits & mlbits) > 0){
//						List<EventTriggerEvents> evnetTrEventList = new ArrayList<EventTriggerEvents>();
						
						boolean isTranMatchFlag = false;
						//its Existed in the List
						for (SparkBaseTransactions eachIssuTr : contBaseRedemptionTrList) {
							logger.debug("eachIssuTr id is :: "+eachIssuTr.getId());
							Double transactionrBalence = null;
							logger.debug("0 is ::"+qryArrStr[0]+" 1 is ::"+qryArrStr[1]+" 2 is ::"+qryArrStr[2]);
							if(qryArrStr[0].trim().equals("loyalty.loyalty_balance")){
								transactionrBalence =  eachIssuTr.getLoyaltyBalance();
								
							}else if(qryArrStr[0].trim().equals("loyalty.giftcard_balance")){
								transactionrBalence = eachIssuTr.getGiftcardBalance();
							}
							logger.debug("triggerBalence ids :: "+transactionrBalence);
							isTranMatchFlag = false;
							
							if(qryArrStr[1].trim().contains(">") && transactionrBalence != null){ //greater than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								
								
								
								if(transactionrBalence > value ) {
								
									isTranMatchFlag = true;
									
								}
								
							}else if(qryArrStr[1].trim().contains("<") && transactionrBalence != null ){ // less than
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(transactionrBalence < value ) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("=")  && transactionrBalence != null){ // Equal
								Double value = Double.parseDouble(qryArrStr[2].trim());
								if(value == transactionrBalence) {
									isTranMatchFlag = true;
								}
								
							}else if(qryArrStr[1].trim().contains("between")  && transactionrBalence != null){ //Between
								
								Double value1 = Double.parseDouble(qryArrStr[2].trim());
								Double value2 = Double.parseDouble(qryArrStr[3].trim());
								
								if(value1 <= transactionrBalence  && transactionrBalence <= value2){
//									eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
									isTranMatchFlag = true;
								}
							}else {// Is Null
								
								if(transactionrBalence == null) {
									isTranMatchFlag = true;
								}
																	
							}
							
							//Fetch same object for is any associate Trigger match
							eachIssuTr = sparkBaseTransactionsDao.find(eachIssuTr.getId());
							logger.debug(" fetach data once again :: "+eachIssuTr);
							logger.debug(" isTranMatchFlag is  :: "+isTranMatchFlag);
							if(isTranMatchFlag) {
								
								byte eventEmailStatus = Constants.ET_EMAIL_STATUS_NO_ACTION;
								byte eventSMSStatus = Constants.ET_SMS_STATUS_NO_ACTION;
								
								if( (optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG ) == Constants.ET_SEND_CAMPAIGN_FLAG){
									eventEmailStatus +=  Constants.ET_EMAIL_STATUS_ACTIVE;
								}
								
								if( (optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG) {
									eventSMSStatus +=  Constants.ET_SMS_STATUS_ACTIVE;
								}
								
								
								//TODO create Event Trigger Event for this Transactions
								EventTriggerEvents eventTriggerObj = new EventTriggerEvents();
								//event_trigger_id
								eventTriggerObj.setEventTriggerId(eachET.getId());
								//created_time
								eventTriggerObj.setCreatedTime(Calendar.getInstance());
								logger.debug(eachIssuTr.getId()+" and  eachIssuTr.getProcessedTime() is ::"+eachIssuTr.getProcessedTime());
								//event_time
								Calendar eventTimeCal = eachIssuTr.getProcessedTime();
								eventTimeCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								eventTriggerObj.setEventTime(eventTimeCal);
								//user_id
								eventTriggerObj.setUserId(userId);
								//event_category
								eventTriggerObj.setEventCategory("LoyaltyBalance");
								
								//source_id
								eventTriggerObj.setSourceId(eachIssuTr.getId());
								//contact_id
								eventTriggerObj.setContactId(eachCont.getContactId());
								//trigger_type
								eventTriggerObj.setTriggerType(Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
								
								//email_status
								eventTriggerObj.setEmailStatus(eventEmailStatus);
								//sms_status
								eventTriggerObj.setSmsStatus(eventSMSStatus);
								//tr_condition
								eventTriggerObj.setTriggerCondition(eachET.getInputStr());
								
								try {
									//save ETEvent in to DB
									//eventTriggerEventsDao.saveOrUpdate(eventTriggerObj);
									eventTriggerEventsDaoForDML.saveOrUpdate(eventTriggerObj);

								} catch (Exception e) {
								logger.error("Exception ::", e);
								}
								
								String triggerIds = eachIssuTr.getTriggerIds();
								
								if(triggerIds != null ){
									triggerIds = triggerIds.trim().length() > 0 ? triggerIds+","+eachET.getId() : ""+eachET.getId();
//										eachET.getId()
								}else {
									triggerIds = ""+eachET.getId();
								}
								eachIssuTr.setTriggerIds(triggerIds);
								
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_PROCESSED);
							
							}
							
							if(eachIssuTr.getStatus().equals(Constants.SPARKBASE_TRANSACTION_STATUS_NEW)){
								eachIssuTr.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_UNPROCESSED);
							}
							
							//sparkBaseTransactionsDao.saveOrUpdate(eachIssuTr);
							sparkBaseTransactionsDaoForDML.saveOrUpdate(eachIssuTr);

							
						} // for Each ContactId Based Transaction
					
					}else{
						
						String startAndEndIdxIdStr = contBaseRedemptionTrList.get(0).getId()+","+contBaseRedemptionTrList.get(contBaseRedemptionTrList.size()-1).getId();
						
						moveToUnprocessedToTrasactionLst(contBaseRedemptionTrList);
						
						logger.info("No Adjust Event trigger for this conatct "+eachCont.getContactId()+" "
								+ " So unprocessed the all Adjustmet type Transaction From to End's Ids are :"+startAndEndIdxIdStr);
						
						moveToUnprocessedToTrasactionLst(contBaseRedemptionTrList);
						
					}
					
				} // for
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.error("Problem Occured with getting Event trigger with the users :"+ userId +"::", e);
		}
	} // adjustmentTransaList
	
	
	private void moveToUnprocessedToTrasactionLst(List<SparkBaseTransactions> unProcessedList) throws Exception {
		
		List<SparkBaseTransactions> unProcessedTrlist = new ArrayList<SparkBaseTransactions>();
		SparkBaseTransactionsDao sparkBaseTransactionsDao = 
				(SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
		SparkBaseTransactionsDaoForDML sparkBaseTransactionsDaoForDML = 
				(SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
		for (SparkBaseTransactions eachIssTrObj : unProcessedList) {
			eachIssTrObj.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_UNPROCESSED);
			unProcessedTrlist.add(eachIssTrObj);
		}
		
		//sparkBaseTransactionsDao.saveByCollection(unProcessedTrlist);
		sparkBaseTransactionsDaoForDML.saveByCollection(unProcessedTrlist);

	} // moveToUnprocessedToTrasactionLst
	
	boolean pollQueue(){
		pollObj = uploadQueue.poll();
		if(pollObj!=null) {
			return true;
		} 
		else {
			return false;
		}
	} // pollQueue
	
	
}
