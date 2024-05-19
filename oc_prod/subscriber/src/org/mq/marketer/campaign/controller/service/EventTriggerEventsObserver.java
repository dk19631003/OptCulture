package org.mq.marketer.campaign.controller.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.AddSingleControllerNew;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.EventTriggerDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerEventsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;

/**
 * 
 * @author proumya
 *
Acts as an observer which always observs an events from all observable objects */
public class EventTriggerEventsObserver implements Observer{



	/**
	 *Job is to take the input values,</BR> 
	 * find the qualified events.</BR>
	 * insert into a table from which the timer will take the events to trigger an action. 
	 * Parameters : 'inputObjArr' here is an Object Array holding all the inputs required.
	 */
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private EventTriggerDao eventTriggerDao;
	//private EventTriggerDaoForDML eventTriggerDaoForDML;
	private EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML;
	
	
	
	/*public EventTriggerDaoForDML getEventTriggerDaoForDML() {
		return eventTriggerDaoForDML;
	}

	public void setEventTriggerDaoForDML(EventTriggerDaoForDML eventTriggerDaoForDML) {
		this.eventTriggerDaoForDML = eventTriggerDaoForDML;
	}*/

	public EventTriggerEventsDaoForDML getEventTriggerEventsDaoForDML() {
		return eventTriggerEventsDaoForDML;
	}

	public void setEventTriggerEventsDaoForDML(
			EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML) {
		this.eventTriggerEventsDaoForDML = eventTriggerEventsDaoForDML;
	}

	public EventTriggerDao getEventTriggerDao() {
		return eventTriggerDao;
	}

	public void setEventTriggerDao(EventTriggerDao eventTriggerDao) {
		this.eventTriggerDao = eventTriggerDao;
	}

	
	private MailingListDao mailingListDao;
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}


	/*private ContactsDao contactsDao;
	
	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
*/
	private static Map<Integer, String> eventTimeConvertionQry;
	
	private static final String COMMA = Constants.DELIMETER_COMMA;
	private static final String NILL = Constants.STRING_NILL;
	
	private static Map<Integer, String> eventTriggerQueryMap;
	private static String eventInsertQuery =  "INSERT IGNORE INTO event_trigger_events " +
												"( event_trigger_id,trigger_type, created_time, event_time, user_id, email_status, sms_status," +
												" event_category, source_id, contact_id, tr_condition ) (<SELECTQRY> )";
	
	private static String ET_QUERY_ON_PRODUCT =  " SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"rs.sales_date"+COMMA+
													" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"rs.sales_id, rs.cid , '<TRCONDITION>'" +
													" FROM retail_pro_sales rs, contacts c WHERE c.user_id=<USERID> AND rs.user_id=<USERID>"+
													" AND rs.cid is NOT NULL AND c.cid=rs.cid AND (c.mlbits&<ETMLBITS>)>0 AND rs.sales_id BETWEEN <STARTID>"+ 
													" AND <ENDID> AND rs.sales_date IS NOT NULL AND <ETCONDITION> GROUP BY rs.doc_sid";
													
	private static String ET_QUERY_ON_PURCHASE = " SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"rs.sales_date"+COMMA+
													" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"rs.sales_id, rs.cid , 'On Purchase' " +
													" FROM retail_pro_sales rs, contacts c WHERE c.user_id=<USERID> AND rs.user_id=<USERID>"+
													" AND rs.cid is NOT NULL AND c.cid=rs.cid AND (c.mlbits&<ETMLBITS>)>0 AND rs.sales_id BETWEEN <STARTID>"+ 
													" AND <ENDID> AND rs.sales_date IS NOT NULL GROUP BY rs.doc_sid";
	
	private static String ET_QUERY_ON_PURCHASE_AMOUNT = " SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"rs.sales_date"+COMMA+
														" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"rs.sales_id, rs.cid , '<TRCONDITION>' " +
														" FROM retail_pro_sales rs,contacts c WHERE c.user_id=<USERID> AND rs.user_id=<USERID>"+
													" AND rs.cid is NOT NULL AND c.cid=rs.cid AND (c.mlbits&<ETMLBITS>)>0 AND rs.sales_id BETWEEN <STARTID>"+ 
														" AND <ENDID> AND rs.sales_date IS NOT NULL GROUP BY rs.doc_sid HAVING <ETCONDITION>";
	
	
	private static String ET_QUERY_ON_CONTACT_DATE = " SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"<CONDATEFIELD>"+COMMA+
													" <USERID> "+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"c.cid, c.cid, '<TRCONDITION>' " +
													" FROM contacts c WHERE  c.user_id=<USERID>"+
													" AND c.cid BETWEEN <STARTID>"+ 
													" AND <ENDID> AND (c.mlbits&<ETMLBITS>)>0";
	
	
	private static String ET_QUERY_ON_CONTACT_OPTIN_MEDIUM = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"c.created_date"+COMMA+
															"<USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"c.cid, c.cid, '<TRCONDITION>'" +
															" FROM contacts c WHERE  c.user_id=<USERID>"+
															" AND c.cid BETWEEN <STARTID>"+ 
															" AND <ENDID> AND (c.mlbits&<ETMLBITS>)>0 AND <ETCONDITION>";
	
	
	private static String ET_QUERY_ON_CONTACT_ADDED = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"c.created_date"+COMMA+
													"<USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"c.cid, c.cid, 'On Contact Add'" +
													" FROM contacts c WHERE  c.user_id=<USERID>"+
													" AND c.cid BETWEEN <STARTID>"+ 
													" AND <ENDID> AND (c.mlbits&<ETMLBITS>)>0 ";


	/*private static String ET_QUERY_ON_CAMPAIGN_OPEN = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"o.open_date"+COMMA+
														" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"cs.sent_id, cs.contact_id, '<TRCONDITION>'" +
														" FROM contacts c, campaign_sent cs, opens o WHERE c.user_id=<USERID> AND " +
														" c.cid=cs.contact_id AND cs.sent_id=o.sent_id "+
														" AND (c.mlbits&<ETMLBITS>)>0 AND o.open_id BETWEEN <STARTID>"+ 
														" AND <ENDID> AND <ETCONDITION>";
*/
	
	private static String ET_QUERY_ON_CAMPAIGN_OPEN = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"<DATE_ADD_QRY>(o.open_date,INTERVAL <TIMEZONEVAL> MINUTE )"+COMMA+
														" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"cs.sent_id, cs.contact_id, '<TRCONDITION>'" +
														" FROM contacts c, campaign_sent cs, opens o WHERE c.user_id=<USERID> AND " +
														" c.cid=cs.contact_id AND cs.sent_id=o.sent_id "+
														" AND (c.mlbits&<ETMLBITS>)>0 AND o.open_id BETWEEN <STARTID>"+ 
														" AND <ENDID> AND <ETCONDITION>";

	
/*	private static String ET_QUERY_ON_CAMPAIGN_CLICK = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"cl.click_date"+COMMA+
													" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"cs.sent_id, cs.contact_id,  '<TRCONDITION>'" +
													" FROM contacts c, campaign_sent cs, clicks cl WHERE c.user_id=<USERID> AND " +
													" c.cid=cs.contact_id AND cs.sent_id=cl.sent_id "+
													"  AND (c.mlbits&<ETMLBITS>)>0 AND cl.click_id BETWEEN <STARTID>"+ 
													" AND <ENDID> AND <ETCONDITION>";
	*/					
	
	private static String ET_QUERY_ON_CAMPAIGN_CLICK = "SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"<DATE_ADD_QRY>(cl.click_date,INTERVAL <TIMEZONEVAL> MINUTE )"+COMMA+
														" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"cs.sent_id, cs.contact_id,  '<TRCONDITION>'" +
														" FROM contacts c, campaign_sent cs, clicks cl WHERE c.user_id=<USERID> AND " +
														" c.cid=cs.contact_id AND cs.sent_id=cl.sent_id "+
														"  AND (c.mlbits&<ETMLBITS>)>0 AND cl.click_id BETWEEN <STARTID>"+ 
														" AND <ENDID> AND <ETCONDITION>";
	
	private static String ET_QUERY_ON_LOYALTY = " SELECT <ETID>"+COMMA+" <ETTYPE> "+COMMA+" '<NOW>' "+COMMA+"<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )"+COMMA+
												" <USERID>"+COMMA+"<EMAILSTATUS>"+COMMA+"<SMSSTATUS>"+COMMA+"'<CATEGORY>'"+COMMA+"lc.trans_child_id, lc.contact_id, '<TRCONDITION>'" +
												" FROM loyalty_transaction_child lc,contacts c WHERE c.user_id=<USERID> AND lc.user_id=<USERID>"+
												" AND lc.contact_id is NOT NULL AND c.cid=lc.contact_id AND (c.mlbits&<ETMLBITS>)>0 AND lc.trans_child_id BETWEEN <STARTID>"+ 
												" AND <ENDID> <ETCONDITION>";
	
	static{
		
		eventTriggerQueryMap = new HashMap<Integer, String>();
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PRODUCT, ET_QUERY_ON_PRODUCT);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PURCHASE, ET_QUERY_ON_PURCHASE);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PURCHASE_AMOUNT, ET_QUERY_ON_PURCHASE_AMOUNT);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CONTACT_DATE, ET_QUERY_ON_CONTACT_DATE);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM, ET_QUERY_ON_CONTACT_OPTIN_MEDIUM);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CONTACT_ADDED, ET_QUERY_ON_CONTACT_ADDED);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CAMPAIGN_OPENED, ET_QUERY_ON_CAMPAIGN_OPEN);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CAMPAIGN_CLICKED, ET_QUERY_ON_CAMPAIGN_CLICK);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_LOYALTY_ISSUANCE, ET_QUERY_ON_LOYALTY);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_LOYALTY_REDEMPTION, ET_QUERY_ON_LOYALTY);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT, ET_QUERY_ON_LOYALTY);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_GIFT_ISSUANCE, ET_QUERY_ON_LOYALTY);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_GIFT_REDEMPTION, ET_QUERY_ON_LOYALTY);
		eventTriggerQueryMap.put(Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE, ET_QUERY_ON_LOYALTY);
		
	
		eventTimeConvertionQry = new HashMap<Integer, String>();
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_CAMPAIGN_OPENED, "<DATE_ADD_QRY>(o.open_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_CAMPAIGN_CLICKED, "<DATE_ADD_QRY>(cl.click_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_LOYALTY_ISSUANCE, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_LOYALTY_REDEMPTION, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_GIFT_ISSUANCE, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_GIFT_REDEMPTION, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		eventTimeConvertionQry.put(Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE, "<DATE_ADD_QRY>(lc.created_date,INTERVAL <TIMEZONEVAL> MINUTE )");
		
	}
	
	
	@Override
	public void update(Observable o, Object inputObj) {
		// TODO Auto-generated method stub
	
		//grab all the inputs by the notifier
		Object[] inputObjArr = (Object[])inputObj;
		List<EventTrigger> etList = (List<EventTrigger>)inputObjArr[0];
		Long startId = (Long)inputObjArr[1];
		Long endId = (Long)inputObjArr[2];
		Long userId = (Long)inputObjArr[3];
		String category = (String)inputObjArr[4];//depend on this we can go to that table
		
		logger.debug("-----size of array ::-------"+inputObjArr.length);
		int trType = 0;
		if(inputObjArr.length > 5) {
			trType = (Integer)inputObjArr[5];
		}
		
		
		//for each event trigger find the set of sales events and insert into event_trigger_events
		MyCalendar myCal = new MyCalendar(); 
		for (EventTrigger eventTrigger : etList) {
			
			try {
				long optionsFlag = eventTrigger.getOptionsFlag();
				
				if( (eventTrigger.getTrType().intValue() & Constants.ET_TYPE_ON_CONTACT_DATE ) == Constants.ET_TYPE_ON_CONTACT_DATE) continue;
				
				//accept the events only if the trigger is active
				if( (optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) != Constants.ET_TRIGGER_IS_ACTIVE_FLAG) continue;
				
				//Set<MailingList> mlSet = eventTrigger.getMailingLists();
				List<MailingList> configuredList = mailingListDao.findAllTriggerLists(eventTrigger.getId());
				
				if(configuredList == null) {
					
					logger.debug("No lists are configured to this trigger");
					continue;
				}
				
			//if the contacts(event's corresponding) are not 
			//belongs to the trigger configured list then no need to consider that event.
				Set<MailingList> mlSet = new HashSet<MailingList>();
				mlSet.addAll(configuredList);
				long mlbits = Utility.getMlsBit(mlSet);
				
				if(mlbits <= 0) {
					
					logger.debug("no mlbits are found for this trigger");
					continue;
				}
				
				
				String selectQRY = eventTriggerQueryMap.get(eventTrigger.getTrType());
				if(selectQRY == null){
					if(trType != 0) {
						selectQRY = eventTriggerQueryMap.get(trType);
						if(selectQRY == null) {
							logger.debug("no trigger condition found");
							continue;
						}
					}
				}
				
				
				byte eventEmailStatus = Constants.ET_EMAIL_STATUS_NO_ACTION;
				byte eventSMSStatus = Constants.ET_SMS_STATUS_NO_ACTION;
				
				if( (optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG ) == Constants.ET_SEND_CAMPAIGN_FLAG){
					eventEmailStatus +=  Constants.ET_EMAIL_STATUS_ACTIVE;
				}
				
				if( (optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG) {
					eventSMSStatus +=  Constants.ET_SMS_STATUS_ACTIVE;
				}
				
				
				//don't take cid is not null as cid may available at times
				/*	String selectQRY = "SELECT " +eventTrigger.getId().longValue()+COMMA+
									" '"+myCal.toString()+"' "+COMMA+"rs.sales_date"+COMMA+
									userId.longValue()+COMMA+"0"+COMMA+"'"+category+"'"+COMMA+"rs.sales_id " +
									"FROM retail_pro_sales rs WHERE  rs.user_id="+userId.longValue()+
									" AND rs.cid is NOT NULL AND rs.sales_id BETWEEN "+startId.longValue()+ 
									" AND "+endId.longValue()+" AND "+ eTCondition;*/
				
				if(eventTimeConvertionQry.containsKey(eventTrigger.getTrType()) 
						&& selectQRY.contains(eventTimeConvertionQry.get(eventTrigger.getTrType())) ) {
					
					
					Users etOwner = eventTrigger.getUsers();
					String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
					int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
					String timezoneDiffrenceMinutes = etOwner.getClientTimeZone();
					int timezoneDiffrenceMinutesInt = 0;
					
					if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
					
					timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
				
					String Date_Add_subQry = timezoneDiffrenceMinutesInt < 0 ? "DATE_SUB" : "DATE_ADD";
					
					timezoneDiffrenceMinutesInt = Math.abs(timezoneDiffrenceMinutesInt);
					
					String tempStr = eventTimeConvertionQry.get(eventTrigger.getTrType());
					tempStr = tempStr.replace("<TIMEZONEVAL>", timezoneDiffrenceMinutesInt+Constants.STRING_NILL).replace("<DATE_ADD_QRY>", Date_Add_subQry);
					
					selectQRY = selectQRY.replace(eventTimeConvertionQry.get(eventTrigger.getTrType()), tempStr);
					
					
				}else {
					if(trType != 0 && eventTimeConvertionQry.containsKey(trType) 
							&& selectQRY.contains(eventTimeConvertionQry.get(trType))) {

						Users etOwner = eventTrigger.getUsers();
						String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
						int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
						String timezoneDiffrenceMinutes = etOwner.getClientTimeZone();
						int timezoneDiffrenceMinutesInt = 0;
						
						if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
						
						timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
					
						String Date_Add_subQry = timezoneDiffrenceMinutesInt < 0 ? "DATE_SUB" : "DATE_ADD";
						
						timezoneDiffrenceMinutesInt = Math.abs(timezoneDiffrenceMinutesInt);
						
						String tempStr = eventTimeConvertionQry.get(trType);
						tempStr = tempStr.replace("<TIMEZONEVAL>", timezoneDiffrenceMinutesInt+Constants.STRING_NILL).replace("<DATE_ADD_QRY>", Date_Add_subQry);
						
						selectQRY = selectQRY.replace(eventTimeConvertionQry.get(trType), tempStr);
					
					}
				}
					
				
				
				
				
				selectQRY = selectQRY.replace("<ETID>", eventTrigger.getId().longValue()+NILL).replace("<ETTYPE>", eventTrigger.getTrType()+NILL).replace("<NOW>", myCal.toString())
								.replace("<USERID>", userId.longValue()+NILL).replace("<CATEGORY>", category).replace("<STARTID>", startId.longValue()+NILL)
									.replace("<ENDID>", endId.longValue()+NILL).replace("<ETMLBITS>", mlbits+NILL)
									.replace("<EMAILSTATUS>", eventEmailStatus+NILL).replace("<SMSSTATUS>", eventSMSStatus+NILL);
				
				if(selectQRY.contains("<ETCONDITION>")) {

					String inputStr = NILL;
					if(eventTrigger.getInputStr() != null) {
						inputStr = eventTrigger.getInputStr();
					}
					/*if( (eventTrigger.getTrType() & Constants.ET_TYPE_ON_CONTACT_DATE) == Constants.ET_TYPE_ON_CONTACT_DATE) {

						selectQRY = selectQRY.replace("<CONDATEFIELD>", inputStr.split("\\|")[0]);
						selectQRY = selectQRY.replace("<ETCONDITION>", "");

					}else {*/
					String eTCondition = NILL;
					if(eventTrigger.getInputStr() != null) {

						if(trType != 0 && eventTriggerQueryMap.get(trType).equalsIgnoreCase(ET_QUERY_ON_LOYALTY)) {
							eTCondition += "AND ";
						}
						eTCondition += getConditionQuery(eventTrigger.getInputStr());
					}
					selectQRY = selectQRY.replace("<ETCONDITION>", eTCondition).replace("<TRCONDITION>", inputStr);

					//}

				}//if condition exit
				String queryToBeExecuted = eventInsertQuery.replace("<SELECTQRY>", selectQRY);
				
				logger.info("qry is >>>>>>>>>>>"+queryToBeExecuted);
				//int count = eventTriggerDao.executeJdbcUpdateQuery(queryToBeExecuted);//TODO need to find
				//int count = eventTriggerDaoForDML.executeJdbcUpdateQuery(queryToBeExecuted);
				int count = eventTriggerEventsDaoForDML.executeJdbcUpdateQuery(queryToBeExecuted);
				//logger.info("count>>>>>>>>>"+count);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				continue;
			}
			
		}//for
		
	}//update
	
	public String getConditionQuery(String inputStr) {
		if(inputStr.contains("<AND>")) {
			String[] outerTokenArr = null;
			String retStr = "";
			
			outerTokenArr = inputStr.split("<AND>");
			for (String token : outerTokenArr) {
				
				
				String[] tokenArr = token.split("\\|");
				String conditionQry = SalesQueryGenerator.replaceToken(tokenArr[0], tokenArr, true);
				if(!retStr.isEmpty()) retStr += " AND ";
				
				retStr += conditionQry;
				
			}
			
			return retStr;
		}else{
			
			String[] tokenArr = inputStr.split("\\|");
			String conditionQry = SalesQueryGenerator.replaceToken(tokenArr[0], tokenArr, true);
			return conditionQry;
			
		}
		
	}
	
	
	
/*	public void observeInteractionEvents(Object inputObj) {
		
		Object[] inputObjArr = (Object[])inputObj;
		List<EventTrigger> etList = (List<EventTrigger>)inputObjArr[0];
		Long startId = (Long)inputObjArr[1];
		Long endId = (Long)inputObjArr[2];
		Long userId = (Long)inputObjArr[3];
		String category = (String)inputObjArr[4];//depend on this we can go to that table
		String 
		
		
		
		
	}//observeInteractionEvents()
	*/
	
	
	

 
 
 
 
}