package org.mq.captiway.scheduler.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Users;

public class TriggerQueryGenerator {

	/*public static String ET_TO_CONTACTS_QUERY_ON_PRODUCT = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
														" FROM contacts c, retail_pro_sales rs, event_trigger_events et"+ 
														"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 AND rs.user_id in(<USERIDS>) " +
														"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID> AND rs.cid IS NOT NULL " +
														"	AND c.cid=rs.cid AND et.source_id=rs.sales_id AND et.event_category='Sales' AND et.status=0  AND (c.email_id is not null " +
														"	AND c.email_id!='') AND c.email_status='Active'  <TRTIMECOND> GROUP BY c.email_id ";
							
	*/
	
	/*public static String ET_TO_CONTACTS_QUERY_ON_CONTACT_DATE = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
																" FROM contacts c, event_trigger_events et"+ 
																"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
																"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID> " +
																"	AND et.source_id=c.cid AND et.event_category='Contacts' AND et.status=0  AND (c.email_id is not null " +
																"	AND c.email_id!='') AND c.email_status='Active'  <TRTIMECOND> GROUP BY c.email_id ";

	public static String ET_TO_CONTACTS_QUERY_ON_CAMPAIGN_OPEN = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
																	" FROM contacts c, event_trigger_events et"+ 
																	"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
																	"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID> " +
																	"	AND et.contact_id=c.cid AND et.event_category='Opens' AND et.status=0  AND (c.email_id is not null " +
																	"	AND c.email_id!='') AND c.email_status='Active'  <TRTIMECOND> GROUP BY c.email_id ";

	*/
	/*public static String ET_TO_EMAIL_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
															" FROM contacts c, event_trigger_events et"+ 
															"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
															"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
															"	AND c.cid=et.contact_id AND (et.status&"+Constants.ET_TRIGGER_EVENTS_SEND_EMAIL_CAMPAIGN_FLAG+")>0  AND (c.email_id is not null " +
															"	AND c.email_id!='') AND c.email_status='Active'  <TRTIMECOND> GROUP BY c.email_id ";
*/
	
	
	public static String ET_TO_EMAIL_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
														" FROM contacts c, event_trigger_events et"+ 
														"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
														"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
														"	AND c.cid=et.contact_id AND (et.email_status="+Constants.ET_EMAIL_STATUS_RUNNING+") AND (c.email_id is not null " +
														"	AND c.email_id!='') AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"')   GROUP BY c.email_id ";


	/*public static String ET_TO_MOBILE_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
														" FROM contacts c, event_trigger_events et"+ 
														"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
														"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
														"	AND c.cid=et.contact_id AND (et.status&"+Constants.ET_TRIGGER_EVENTS_SEND_SMS_CAMPAIGN_FLAG+")>0  AND (c.mobile_phone IS NOT NULL " +
														"	AND c.mobile_phone!='') AND c.mobile_status='Active' <MOBILEOPTIN> <TRTIMECOND> GROUP BY c.mobile_phone ";
*/
	
	
	public static String ET_TO_MOBILE_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
														" FROM contacts c, event_trigger_events et"+ 
														"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
														"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
														"	AND c.cid=et.contact_id AND (et.sms_status="+Constants.ET_SMS_STATUS_RUNNING+")  AND (c.mobile_phone IS NOT NULL " +
														"	AND c.mobile_phone!='') AND c.mobile_status='Active' <MOBILEOPTIN>  GROUP BY c.mobile_phone ";

	
	/*public static String EMAIL_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), null" +
														" FROM contacts c WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
														"	AND c.email_id!='' AND c.email_status='Active'   <TRTIMECOND> GROUP BY c.email_id ";


public static String MOBILE_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), null" +
												" FROM contacts c WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
												"	AND c.mobile_phone IS NOT NULL AND c.mobile_phone!='' AND c.mobile_status='Active' " +
												" <MOBILEOPTIN> <TRTIMECOND> GROUP BY c.mobile_phone ";

*/
	
	/*public static String EMAIL_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
													" FROM contacts c, contact_specific_date_events et"+ 
													"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
													"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
													"	AND c.cid=et.contact_id AND (et.status&"+Constants.ET_TRIGGER_EVENTS_SEND_EMAIL_CAMPAIGN_FLAG+")>0  AND (c.email_id is not null " +
													"	AND c.email_id!='') AND c.email_status='Active'  <TRTIMECOND> GROUP BY c.email_id ";
*/
	public static String EMAIL_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
													" FROM contacts c, contact_specific_date_events et"+ 
													"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
													"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
													"	AND c.cid=et.contact_id AND (et.email_status="+Constants.ET_EMAIL_STATUS_RUNNING+")  AND (c.email_id is not null " +
													"	AND c.email_id!='') AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"')   GROUP BY c.email_id ";


/*public static String MOBILE_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
													" FROM contacts c, contact_specific_date_events et"+ 
													"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
													"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
													"	AND c.cid=et.contact_id AND (et.status&"+Constants.ET_TRIGGER_EVENTS_SEND_SMS_CAMPAIGN_FLAG+")>0  AND (c.mobile_phone IS NOT NULL " +
													"	AND c.mobile_phone!='') AND c.mobile_status='Active' <MOBILEOPTIN> <TRTIMECOND> GROUP BY c.mobile_phone ";
*/
	
public static String MOBILE_CONTACTS_QUERY = " SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+", null, SUBSTRING_INDEX(c.email_id, '@', -1), et.event_id " +
												" FROM contacts c, contact_specific_date_events et"+ 
												"	WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  " +
												"	AND et.user_id in(<USERIDS>) AND et.event_trigger_id=<ETID>  " +
												"	AND c.cid=et.contact_id AND (et.sms_status="+Constants.ET_SMS_STATUS_RUNNING+")  AND (c.mobile_phone IS NOT NULL " +
												"	AND c.mobile_phone!='') AND c.mobile_status='Active' <MOBILEOPTIN>  GROUP BY c.mobile_phone ";


	
	//private static Map<Integer, String> eventTriggerQueryMap;
	/*static{
			
			eventTriggerQueryMap = new HashMap<Integer, String>();
			eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PRODUCT, ET_TO_CONTACTS_QUERY_ON_PRODUCT);
			//eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PURCHASE, ET_QUERY_ON_PURCHASE);
			//eventTriggerQueryMap.put(Constants.ET_TYPE_ON_PURCHASE_AMOUNT, ET_QUERY_ON_PURCHASE_AMOUNT);
			eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CONTACT_DATE, ET_TO_CONTACTS_QUERY_ON_CONTACT_DATE);
			eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM, ET_TO_CONTACTS_QUERY_ON_CONTACT_DATE);
			//eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CAMPAIGN_OPENED, ET_TO_CONTACTS_QUERY_ON_CAMPAIGN_OPEN);
			//eventTriggerQueryMap.put(Constants.ET_TYPE_ON_CAMPAIGN_CLICKED, ET_QUERY_ON_CAMPAIGN_CLICK);
			
	}*/
	
	
	
	private static final String STRING_NILL = Constants.STRING_NILL;
	
	public static String prepareTriggerQuery(long userId, EventTrigger eventTrigger) {
		
		//TODO need to get this based on trigger type.
		//String triggerQuery = eventTriggerQueryMap.get(eventTrigger.getTrType());
		String triggerQuery = "";
		String colName = "";
		boolean isYearIgnored = false;
		if( (eventTrigger.getTrType().intValue() & Constants.ET_TYPE_ON_CONTACT_DATE ) <= 0){
			
			triggerQuery = ET_TO_EMAIL_CONTACTS_QUERY;
			colName = "et.event_time";
		}
		else {
			
			triggerQuery = EMAIL_CONTACTS_QUERY;
			String tokenArr[] = eventTrigger.getInputStr().split("\\|"); 
			//colName = tokenArr[0];
			colName = "et.event_time";
			isYearIgnored = (tokenArr.length > 1);
		}
		
		//get mlbits of configured lists
		Set<MailingList> trConfiguredLists = eventTrigger.getMailingLists();
		long mlbits = Utility.getMlsBit(trConfiguredLists);
		
		//get event trigger id
		long etId = eventTrigger.getId().longValue();
		
		//get the trigger time condition
		//String timeCondition = getTriggerTimeDiffCond(eventTrigger, colName, isYearIgnored);
		
		/*triggerQuery = triggerQuery.replace("<USERIDS>", userId+STRING_NILL)
						.replace("<MLBITS>", mlbits+STRING_NILL).replace("<ETID>", etId+STRING_NILL).replace("<TRTIMECOND>", timeCondition);
		*/
		triggerQuery = triggerQuery.replace("<USERIDS>", userId+STRING_NILL)
		.replace("<MLBITS>", mlbits+STRING_NILL).replace("<ETID>", etId+STRING_NILL);

		return triggerQuery;
		
		
		
	}
	
	
	public static String prepareTriggerQueryForSMS(long userId, EventTrigger eventTrigger) {
		
		

		
		//TODO need to get this based on trigger type.
		//String triggerQuery = eventTriggerQueryMap.get(eventTrigger.getTrType());
		
		String triggerQuery = "";
		String colName = "";
		boolean isYearIgnored = false;
		if( (eventTrigger.getTrType().intValue() & Constants.ET_TYPE_ON_CONTACT_DATE ) <= 0){
			
			triggerQuery = ET_TO_MOBILE_CONTACTS_QUERY;
			colName = "et.event_time";
		}
		else {
			
			triggerQuery = MOBILE_CONTACTS_QUERY;
			String tokenArr[] = eventTrigger.getInputStr().split("\\|"); 
			//colName = tokenArr[0];
			colName = "et.event_time";
			isYearIgnored = (tokenArr.length > 1);
		}
		
		//get mlbits of configured lists
		Set<MailingList> trConfiguredLists = eventTrigger.getMailingLists();
		long mlbits = Utility.getMlsBit(trConfiguredLists);
		
		//get event trigger id
		long etId = eventTrigger.getId().longValue();
		
		//get the trigger time condition
		//String timeCondition = getTriggerTimeDiffCond(eventTrigger, colName, isYearIgnored);
		
		/*triggerQuery = triggerQuery.replace("<USERIDS>", userId+STRING_NILL)
						.replace("<MLBITS>", mlbits+STRING_NILL).replace("<ETID>", etId+STRING_NILL)
						.replace("<TRTIMECOND>", timeCondition);
		*/
		
		triggerQuery = triggerQuery.replace("<USERIDS>", userId+STRING_NILL)
						.replace("<MLBITS>", mlbits+STRING_NILL).replace("<ETID>", etId+STRING_NILL);
		

		
		return triggerQuery;
		
		
		
		
		
	}//prepareTriggerQueryForSMS
	
	
	
	/*public static String getTriggerTimeCondition(EventTrigger eventTrigger, String custOrGenFieldName, boolean isYearIgnored) {
		
		String retTimeConditionStr = "";
		
		long offset = eventTrigger.getMinutesOffset();
		
		if(offset == 0) {
			
			retTimeConditionStr += " AND "
			
		}
		
		
		
		return retTimeConditionStr;
		
	}//getTriggerTimeCondition
*/	
	
	public static String getTriggerTimeDiffCondForSpecificEvents(EventTrigger eventTrigger, 
			String custOrGenFieldName, boolean isYearIgnored , String createdTime) {
		

		
		String generatedQryStr = "";
		long offset = eventTrigger.getMinutesOffset();
		
		int hour = 0;
		int minute = 0;
		String dayflag = null;
		Date targetDate = eventTrigger.getTargetTime();
		if(targetDate != null) {
			
			hour = targetDate.getHours();
			minute = targetDate.getMinutes();
			dayflag = eventTrigger.getTargetDaysFlag();
			
			
			
		}//if
		
		//(like current time < (created time + 1 day))
		generatedQryStr += " AND DATEDIFF(now(), DATE_ADD("+createdTime+", INTERVAL 1 DAY) ) >= 0";
		
		if(offset == 0) {
			
			if(isYearIgnored) {
				
				generatedQryStr += " AND MONTH(now())=MONTH("+custOrGenFieldName+ ") AND DAY(NOW())=DAY("+custOrGenFieldName+")";
				
			}else{
				
				generatedQryStr += " AND DATEDIFF(now(),"+custOrGenFieldName+ ") >= 0  "; 
				
			}
		}
		else if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day
			
			offset = offset/1440; //converting minutes into days
			
			if(offset > 0) { //difference in future days  
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				" AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" DAY),'%m-%d') " +
				" = DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d') ";*/
				
				if(isYearIgnored){
					
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					//String firstYear = "0001-"+currMonthDate;
					
					//tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
					//tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
					
					
					generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 >="+offset;
					
				}else{
					
					//add offset to column date and that should be greater than or equal to the current time   
					generatedQryStr += " AND DATEDIFF(now(), DATE_ADD("+custOrGenFieldName+", INTERVAL "+offset+" DAY) ) >= 0";//TODO >= / =?
					
					
				}
				
			
			}
			else {
				
				generatedQryStr += " AND NOW() <"+custOrGenFieldName;//is this needed?
				
				offset = -1 * offset;
				
				if(isYearIgnored) {
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String firstYear = "0001-"+currMonthDate;
					
					generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 >=" +offset;

					
				}
				else{
					
					generatedQryStr += " AND DATEDIFF("+custOrGenFieldName+", DATE_ADD(now(), INTERVAL "+offset+" DAY) ) = 0";
					
				}
				
			}
			
			if(targetDate != null) {
				
				
				generatedQryStr += " AND HOUR(NOW()) >= "+hour +" AND MINUTE(NOW()) BETWEEN "+minute+ " AND "+(minute+30)+
									" AND DAYOFWEEK(NOW()) IN("+dayflag+") ";//TODO add days flag
				
				
			}
			
			
			
			
		}
		else { //Hours offset 
			// AND DATE_FORMAT(DATE_SUB(now(), INTERVAL 12 HOUR),'%m-%d %H') = DATE_FORMAT(STR_TO_DATE(cfd.cust_3,'%d/%m/%Y %H:%i:%s'),'%m-%d %H');
			
			//hours
			
			/*
			 * Since the custom date is stored as a string in DB
			 *  and most of the entries don't have time part...
			 *  adding offset hours to current time and checking
			 *  with zeroth hour of the day.(this logic is different
			 *   than the other trigger types which have datetime values)
			 *  ( %H:%i:%s will give 00:00:00)
			 */
			
			if(offset < 0) {
				
				generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				
				offset = -1 * offset/60; //converting minutes to hours
				
				if(isYearIgnored) {
					
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String firstYear = "0001-"+currMonthDate;
					
					generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 >=0";
					
					
				}
					
					//timediff() will give difference in hours
					generatedQryStr += " AND HOUR( TIMEDIFF("+custOrGenFieldName+", NOW()) ) = "+offset;
					/*generatedQryStr += "  AND DATE_ADD(now(), INTERVAL "+offset+" HOUR) = " +
				" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
					
					
				
			}
			else if(offset > 0){
				
				//generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				offset = offset/60; //converting minutes to hours
				
				if(isYearIgnored) {
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 >=0";
					
				}
				generatedQryStr += " AND HOUR( TIMEDIFF(NOW(), "+custOrGenFieldName+") ) >= "+offset; //how much pat we need to check is already there with the query
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				"  AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
				"  DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
			}
			
		} // hours offset 
		
		return generatedQryStr;
	
		
		
		
	}//getTriggerTimeDiffCondForSpecificEvents
	
	
	public static String getTriggerTimeDiffPausedCond(EventTrigger eventTrigger, String custOrGenFieldName, boolean isYearIgnored){
		

		
		String generatedQryStr = "";
		long offset = eventTrigger.getMinutesOffset();
		
		
		boolean isBirthDayTrigger = (eventTrigger.getTrType() & Constants.ET_TYPE_ON_CONTACT_DATE) > 0;
		
		
		int hour = 0;
		//int minute = 0;
		String dayflag = null;
		Date targetDate = eventTrigger.getTargetTime();
		if(targetDate != null) {
			
			hour = targetDate.getHours();
			//minute = targetDate.getMinutes();
			dayflag = eventTrigger.getTargetDaysFlag();
			
			
			
		}//if
		
		Calendar currCal = Calendar.getInstance();
		String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
		
		String zeroYear  = "0000-"+currMonthDate;
		
		String firstYear = "0001-"+currMonthDate;
		
		if(offset == 0) {
			//check for the 'on the same day case', if date/event added on the same day but due to target time has passed this event should anyway fire on the same day
			//so same day condition should meet
			if(isYearIgnored) {
				
				generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 ="+offset;
				
			}else{
				
				generatedQryStr += " AND DATEDIFF(now(),"+custOrGenFieldName+ ") = 0  "; 
				
			}
			
			
			//on the same day may also have only the target time 
			if(targetDate != null) {
				
				if(isBirthDayTrigger) {
					
					//if date type trigger check the target time within the 2hours from timer running time
					//but on the same day case, only target time will exist no days flag will be there
					//should check along with the  delta??????????????/
					generatedQryStr += " AND HOUR(NOW()) > "+hour+2; 
					
					
				}else{
					
					generatedQryStr += " AND (TIME_TO_SEC(HOUR(NOW())) * 60) >"+((hour*60)+30);
					
					
				}
				
			}//if
			
			
		}
		else if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day
			
			offset = offset/1440; //converting minutes into days
			
			if(offset > 0) { //difference in future days  
				
			
				
				if(isYearIgnored){
					
					
					generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 ="+offset;
					
				}else{
					
					//add offset to column date and that should be greater than or equal to the current time   
					generatedQryStr += " AND DATEDIFF(now(), DATE_ADD("+custOrGenFieldName+", INTERVAL "+offset+" DAY) ) = 0";//TODO >= / =?
					
					
				}
				
			
			}
			else {// difference in earlier
				
				generatedQryStr += " AND NOW() <"+custOrGenFieldName;//is this needed?
				
				offset = -1 * offset;
				
				if(isYearIgnored) {
					//replace zero year and first year
					
					
					generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 =" +offset;

					
				}
				else{
					
					generatedQryStr += " AND DATEDIFF("+custOrGenFieldName+", DATE_ADD(now(), INTERVAL "+offset+" DAY) ) = 0";
					
				}
				
			}
			
			if(targetDate != null) {
				
				if(isBirthDayTrigger) {
					
					//if date type trigger check the target time within the 2hours from timer running time
					generatedQryStr += " HOUR(NOW()) > "+(hour+2)+dayflag != null ? ( " AND DAYOFWEEK(NOW()) NOT IN("+dayflag+") "):"";//TODO add days flag
					
					
				}else{
					
					generatedQryStr += " AND (TIME_TO_SEC(HOUR(NOW())) * 60) >"+((hour*60)+30)+
					dayflag!= null ? ( " AND DAYOFWEEK(NOW()) NOT IN("+dayflag+") "):"";//TODO add days flag
					
					
					
				}
				
			}//if
			
			
		}
		else { //Hours offset NO need to consider hours offset in this case
			
		
			
			/*as hours offset only be given for auto responder events, that to 'after' option only be there,
			 * no need to consider <0 and TIME_TO_SEC(TIMEDIFF(time1, time2))/60 will give the difference in minutes.
			 * check for the 30 minutes of delta period also ( BETWEEN offset and offset+30). 
			 * 
			 */
			
			if(offset < 0) { //this will never be used only events are comes under this.
				
				generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				
				//offset = -1 * offset/60; //converting minutes to hours
				
				offset = -1 * offset; 
				
				/*if(isYearIgnored) {
					
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String firstYear = "0001-"+currMonthDate;
					
					generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 =0";
					
					
					
				}*/
					
					//timediff() will give difference in hours
				
					generatedQryStr += " AND ( TIME_TO_SEC(TIMEDIFF("+custOrGenFieldName+", NOW()) )/60 ) BETWEEN "+offset+" AND "+(offset+30);
					/*generatedQryStr += "  AND DATE_ADD(now(), INTERVAL "+offset+" HOUR) = " +
				" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
					
					
				
			}
			else if(offset > 0){
				
				//generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				//offset = offset/60; //converting minutes to hours
				
				
				/*if(isYearIgnored) {
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 = 0";
					
					
				}*/
				
			
				generatedQryStr += " AND( TIME_TO_SEC( TIMEDIFF(NOW(), "+custOrGenFieldName+") ) /60) BETWEEN "+offset+" AND "+(offset+30); //how much pat we need to check is already there with the query
				
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				"  AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
				"  DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
			}
			
		} // hours offset 
		
		return generatedQryStr;
	
		
		
		
		
		
		
	}
	
	
	
public  static String getTriggerTimeDiffCond(EventTrigger eventTrigger, String custOrGenFieldName, boolean isYearIgnored ) {
		
		String generatedQryStr = "";
		long offset = eventTrigger.getMinutesOffset();
		Users etOwner = eventTrigger.getUsers();
		String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
		int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
		String timezoneDiffrenceMinutes = etOwner.getClientTimeZone();
		int timezoneDiffrenceMinutesInt = 0;
		
		if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
		
		timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
		
		boolean isBirthDayTrigger = (eventTrigger.getTrType() & Constants.ET_TYPE_ON_CONTACT_DATE) > 0;
		
		/*int minuteDeltaOffset = 30;
		//int hourDeltaOffSet = 
		if(isBirthDayTrigger) {
			
			minuteDeltaOffset = 120;
			hourDeltaOffSet = 2;
		}*/
		
		String Date_Add_subQry = timezoneDiffrenceMinutesInt < 0 ? "DATE_SUB" : "DATE_ADD";
		String Date_Add_subQry_for_ignoreyear = timezoneDiffrenceMinutesInt < 0 ? "DATE_SUB" : "DATE_ADD";
		
		
		
		int hour = 0;
		//int minute = 0;
		//String dayflag = null;
		Date targetDate = eventTrigger.getTargetTime();
		if(targetDate != null) {
			
			hour = targetDate.getHours();//no need to convert as this is same from client & server perspective
			//minute = targetDate.getMinutes();
			//dayflag = eventTrigger.getTargetDaysFlag();
			
			
			
		}//if
		
		Calendar currCal = Calendar.getInstance();
		
			
		//boolean isMinus = (timezoneDiffrenceMinutesInt < 0);
		
		//to convert into client time zone
		currCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
		
		timezoneDiffrenceMinutesInt = Math.abs(timezoneDiffrenceMinutesInt);
		
		String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
		
		String zeroYear  = "0000-"+currMonthDate;
		
		String firstYear = "0001-"+currMonthDate;
		
		if(offset == 0) {
			if(isYearIgnored) {
				
				//this will execute
				generatedQryStr += " AND DATEDIFF(DATE_ADD("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE ), INTERVAL -YEAR("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE ))+1 YEAR), '"+zeroYear+"' ) % 365 ="+offset;
				
			}else{
				
				//generatedQryStr += " AND DATEDIFF(now(),"+custOrGenFieldName+ ") = 0  "; 
				generatedQryStr += " AND DATEDIFF("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE),"+custOrGenFieldName+ ") = 0  ";
				
			}
			//on the same day may also have the target time 
			if(targetDate != null) {
				
				if(isBirthDayTrigger) {
					
					//if date type trigger check the target time within the 2hours from timer running time
					//generatedQryStr += " AND HOUR(NOW()) BETWEEN "+hour+" AND "+(hour+2);
					generatedQryStr += " AND HOUR("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE)) BETWEEN "+hour+" AND "+(hour+2);					
					//generatedQryStr += " AND IF(HOUR(NOW()) != "+hour +", HOUR(NOW()) BETWEEN "+hour+" AND "+(hour+2)+
					//" , HOUR(NOW()) = "+hour+")";//+ dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					
					
				}else{
					
					/*generatedQryStr += " AND IF(HOUR(NOW()) != "+hour +", (TIME_TO_SEC(HOUR(NOW())) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*30)+30)+
					" ,HOUR(NOW()) = "+hour+")";//+ dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					*/
					//generatedQryStr += " AND (TIME_TO_SEC(HOUR(NOW())) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*60)+30);//+ dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					int currTimeVal = (currCal.get(Calendar.HOUR_OF_DAY)*60)+currCal.get(Calendar.MINUTE);
					int targetTimeVal = hour*60;
					if(currTimeVal < targetTimeVal) {
						generatedQryStr += " AND (TIME_TO_SEC(HOUR("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE))) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*60)+30);//+ dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					}
					
					
				}
				
			}//if
			
			
		}
		else if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day
			
			offset = offset/1440; //converting minutes into days
			
			if(offset > 0) { //difference in future days  
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				" AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" DAY),'%m-%d') " +
				" = DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d') ";*/
				
				if(isYearIgnored){
					
					//replace zero year and first year
					
					//String firstYear = "0001-"+currMonthDate;
					
					//tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
					//tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
					
					generatedQryStr += " AND  DATEDIFF('"+firstYear+"', DATE_ADD(Date("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE )), INTERVAL -YEAR(Date("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE ))) YEAR) ) % 365 ="+offset; 
					//generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 ="+offset;
					
				}else{
					
					//add offset to column date and that should be greater than or equal to the current time   
					//generatedQryStr += " AND DATEDIFF(now(), DATE_ADD("+custOrGenFieldName+", INTERVAL "+offset+" DAY) ) = 0";//TODO >= / =?
					generatedQryStr += " AND DATEDIFF("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE), DATE_ADD("+custOrGenFieldName+", INTERVAL "+offset+" DAY) ) = 0";//TODO >= / =?
					
				}
				
			
			}
			else {
				
				generatedQryStr += " AND "+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE) <"+custOrGenFieldName;//is this needed?
				
				offset = -1 * offset;
				
				if(isYearIgnored) {
					//replace zero year and first year
					generatedQryStr += " AND  DATEDIFF(DATE_ADD("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), " +
							"INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE ), INTERVAL -YEAR("+Date_Add_subQry_for_ignoreyear+"(DATE("+custOrGenFieldName+"), INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE ))+1 YEAR), '"+zeroYear+"' ) % 365 ="+offset;
					
					//generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 =" +offset;

					
				}
				else{
					
					//generatedQryStr += " AND DATEDIFF("+custOrGenFieldName+", DATE_ADD(now(), INTERVAL "+offset+" DAY) ) = 0";
					generatedQryStr += " AND DATEDIFF("+custOrGenFieldName+", DATE_ADD("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE), INTERVAL "+offset+" DAY) ) = 0";
					
				}
				
			}
			
			if(targetDate != null) {
				
				if(isBirthDayTrigger) {
					
					//if date type trigger check the target time within the 2hours from timer running time
					/*generatedQryStr += " AND IF(HOUR(NOW()) != "+hour +", HOUR(NOW()) BETWEEN "+hour+" AND "+(hour+2)+
					" , HOUR(NOW()) = "+hour+")";//+ dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					*/
					//generatedQryStr += " AND  HOUR(NOW()) BETWEEN "+hour+" AND "+(hour+2);
					generatedQryStr += " AND  HOUR("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE)) BETWEEN "+hour+" AND "+(hour+2);
					
					
					
				}else{
					
				/*	generatedQryStr += " AND IF(HOUR(NOW()) != "+hour +", (TIME_TO_SEC(HOUR(NOW())) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*60)+30)+
					" ,HOUR(NOW()) = "+hour+")";// dayflag!= null ? ( " AND DAYOFWEEK(NOW()) IN("+dayflag+") "):"";//TODO add days flag
					
					*/
					
					//generatedQryStr += " AND  (TIME_TO_SEC(HOUR(NOW())) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*60)+30);
					generatedQryStr += " AND  (TIME_TO_SEC(HOUR("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE))) * 60) BETWEEN "+(hour*60)+ " AND "+((hour*60)+30);
					
					
				}
				
			}//if
			
			
		}
		else { //Hours offset 
			// AND DATE_FORMAT(DATE_SUB(now(), INTERVAL 12 HOUR),'%m-%d %H') = DATE_FORMAT(STR_TO_DATE(cfd.cust_3,'%d/%m/%Y %H:%i:%s'),'%m-%d %H');
			
			//hours
			
			/*as hours offset only be given for auto responder events, that to 'after' option only be there,
			 * no need to consider <0 and TIME_TO_SEC(TIMEDIFF(time1, time2))/60 will give the difference in minutes.
			 * check for the 30 minutes of delta period also ( BETWEEN offset and offset+30). 
			 * 
			 */
			
			if(offset < 0) { //this will never be used only events are comes under this.
				
				//generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				generatedQryStr += " AND "+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE) <"+custOrGenFieldName;
				
				
				//offset = -1 * offset/60; //converting minutes to hours
				
				offset = -1 * offset; 
				
				/*if(isYearIgnored) {
					
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String firstYear = "0001-"+currMonthDate;
					
					generatedQryStr += " AND DATEDIFF('"+firstYear+"', DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+") YEAR) ) % 365 =0";
					
					
					
				}*/
					
					//timediff() will give difference in hours
				
					//generatedQryStr += " AND ( TIME_TO_SEC(TIMEDIFF("+custOrGenFieldName+", NOW()) )/60 ) BETWEEN "+offset+" AND "+(offset+30);
				generatedQryStr += " AND ( TIME_TO_SEC(TIMEDIFF("+custOrGenFieldName+", "+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE)) )/60 ) BETWEEN "+offset+" AND "+(offset+30);
					/*generatedQryStr += "  AND DATE_ADD(now(), INTERVAL "+offset+" HOUR) = " +
				" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
					
					
				
			}
			else if(offset > 0){
				
				//generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				//offset = offset/60; //converting minutes to hours
				
				
				/*if(isYearIgnored) {
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					generatedQryStr += " AND DATEDIFF(DATE_ADD("+custOrGenFieldName+", INTERVAL -YEAR("+custOrGenFieldName+")+1 YEAR), '"+zeroYear+"' ) % 365 = 0";
					
					
				}*/
				
			generatedQryStr += " AND ( TIME_TO_SEC( TIMEDIFF("+Date_Add_subQry+"(NOW(),INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE), "+custOrGenFieldName+") ) /60) BETWEEN "+offset+" AND "+(offset+30); //how much pat we need to check is already there with the query
				//generatedQryStr += " AND ( TIME_TO_SEC( TIMEDIFF(NOW(), "+custOrGenFieldName+") ) /60) BETWEEN "+offset+" AND "+(offset+30); //how much pat we need to check is already there with the query
				
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				"  AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
				"  DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
			}
			
		} // hours offset 
		
		return generatedQryStr;
	}
	

	public static void main(String[] strARr) {
		
		
		
		
		
		
		
	}
	
}
