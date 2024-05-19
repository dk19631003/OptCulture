package org.mq.marketer.campaign.general;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TriggerQueryGenerator {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//as sales_date is of future dates,only check with the past data of one day
private static String PRODUCT_QRY = " SELECT DISTINCT c.cid "+
									" FROM contacts c, retail_pro_sales rs  WHERE c.user_id=<USERIDS> AND" +
									"  (c.mlbits&<MLBITS> )>0 AND (c.trbits&<ETBITS>)<=0" +
									" AND rs.user_id in(<SALESUSERID>) AND  rs.cid IS NOT NULL" +
									" AND c.cid=rs.cid AND (c.email_id is not null AND c.email_id!='') " +
									" AND c.email_status='Active' AND <COND> " +
									" AND ( NOW() >= rs.sales_date)  <TIMECOND> AND <COND> GROUP BY c.email_id " ;
	
	
	
private static String generalSqlWithEmailStatus =
		
		" SELECT DISTINCT c.cid "+
		" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 AND (c.trbits&<ETBITS>)<=0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active' AND <COND> GROUP BY c.email_id ";
			
private static HashMap<String, String> tokensHashMap=new HashMap<String, String>();

static {
	tokensHashMap.put("string:is", " = '<VAL1>'");
	
}

	
	/*public static String getTriggerQuery(String event_input_str, EventTriggerEnum etEnum,
			String userIdsStr, long mlBit, long trBit, long offSet) {
		String fromTables = etEnum.getFromTable();
		String fromTableFilters = etEnum.getTableFilter();
		
		HashMap<String, String> filterMap = new HashMap<String, String>();
		String[] fieldsArr = event_input_str.split("\\|");
		
		String token=null;
		String tokenFilterStr="";
		String valStr = "";
		StringBuffer orHavingSB = new StringBuffer();//not required
	
		String replacedVal = replaceToken(fieldsArr[0].trim(), fieldsArr); 
		
		String timeDiffCondition = getTriggerTimeDiffCond(offSet,etEnum.getTargetDateColumn());
			
		String tempStr = PRODUCT_QRY.replace("<USERIDS>", userIdsStr)
						.replace("<MLBITS>", mlBit+"")
						.replace("<ETBITS>", trBit+"");
						
		tempStr = tempStr.replace("<COND>", replacedVal)
				.replace("<SALESUSERID>", userIdsStr).replace("<TIMECOND>", getTriggerTimeDiffCond(offSet,"rs.sales_date") );
		
		
		
		
		return tempStr;
		
	}
	*/
	public static String getTriggerTimeDiffCond(long offset,String custOrGenFieldName) {
		
		String generatedQryStr = "";
		
		if(offset == 0) {
			
			generatedQryStr += " AND DATEDIFF(now(),"+custOrGenFieldName+ ") >= 0  "; 
			
		}
		else if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day
			
			offset = offset/1440; //converting minutes into days
			
			if(offset > 0) { //difference in future days  
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				" AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" DAY),'%m-%d') " +
				" = DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d') ";*/
				
				//add offset to column date and that should be greater than or equal to the current time   
				generatedQryStr += " AND DATEDIFF(now(), DATE_ADD("+custOrGenFieldName+", INTERVAL "+offset+" DAY) ) >= 0";//TODO >= / =?
			
			
			}
			else {
				
				generatedQryStr += " AND NOW() <"+custOrGenFieldName;//is this needed?
				
				offset = -1 * offset;
				generatedQryStr += " AND DATEDIFF("+custOrGenFieldName+", DATE_ADD(now(), INTERVAL "+offset+" DAY) ) = 0";
				
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
				
				//timediff() will give difference in hours
				generatedQryStr += " AND HOUR( TIMEDIFF("+custOrGenFieldName+", NOW()) ) = "+offset;
				/*generatedQryStr += "  AND DATE_ADD(now(), INTERVAL "+offset+" HOUR) = " +
				" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
				
			}
			else if(offset > 0){
				
				//generatedQryStr += " AND NOW() <"+custOrGenFieldName;
				
				offset = offset/60; //converting minutes to hours
				generatedQryStr += " AND HOUR( TIMEDIFF(NOW(), "+custOrGenFieldName+") ) >= "+offset; //how much pat we need to check is already there with the query
				
				/*generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
				"  AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
				"  DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";*/
			}
			
		} // hours offset 
		
		return generatedQryStr;
	}
	
	
	
	
	/**
	 * Converts to the SQL Query form
	 * @param fieldName
	 * @param tokenArr
	 * @return
	 */
	private static String replaceToken(String fieldName, String[] tokenArr) {
		String outStr=null;
		try {
			//String fitreStr = "<OR>";
			
			
			
			String token = tokenArr[1].toLowerCase().trim();
			String value = tokenArr[2].trim();
			
			if(token.contains("is in") || token.contains("is not in")) {
				//if(.equalsIgnoreCase("string"))
				value = SalesQueryGenerator.prepareValueStr(value, token.split(":")[0]);
				
				
			}
			
			
			String mapStr = tokensHashMap.get(token);
			mapStr = mapStr.replace("<FIELD>", fieldName);
			
			if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast") ||
					token.contains("before_between") || token.contains("after_between") || token.contains("range_between") ) fieldName = "";
			// logger.info(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

			if (mapStr==null) {
				logger.info("MAP Entry not found:"+tokenArr[1].toLowerCase().trim());
				return null;
			}
			
			
			
			
			String tempStr = mapStr.replace("<VAL1>", value );
			String opensClicksSubCond = "";
			
			if(fieldName.trim().startsWith("cs.")) {
				if(tokenArr.length>4) {
					tempStr = tempStr.replace("<VAL2>", tokenArr[3].trim());
					opensClicksSubCond = " cs.campaign_id="+tokenArr[4].trim()+" AND ";
					
				}else if(tokenArr.length>3) {
					
					opensClicksSubCond = " cs.campaign_id="+tokenArr[3].trim()+" AND ";
					
				}
				
				
			}
			
			if(tokenArr.length>3 && tempStr.contains("<VAL2>")) {
					
					tempStr = tempStr.replace("<VAL2>", tokenArr[3].trim());
				
			}
			
			
			outStr=" ("+opensClicksSubCond + fieldName+ tempStr +") ";
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		logger.info("outStr ::"+outStr);
		return outStr;
	}
	
	
	
	
}
