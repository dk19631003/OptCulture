package org.mq.captiway.scheduler.utility;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryGenerator {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private static String generalSql = "SELECT *,null, SUBSTRING_INDEX(email_id, '@', -1) FROM contacts WHERE list_id IN (<LISTIDS>) AND email_status='Active' AND (<COND>) GROUP BY email_id ORDER BY cid";
	private static String cfExistSql=" (cid IN (SELECT contact_id FROM customfield_data WHERE (<CFCOND>) ))";	
	private static String oneCFSqlFormat = "SELECT * FROM contacts WHERE list_id = <LISTID> AND email_status='Active' ORDER BY cid "; 
	
	private static HashMap<String, String> tokensHashMap=new HashMap<String, String>();
	
	static {
		tokensHashMap.put("string:is", " = '<VAL1>'");
		tokensHashMap.put("string:startswith", " like '<VAL1>%'");
		tokensHashMap.put("string:endswith", " like '%<VAL1>'");
		tokensHashMap.put("string:contains", " like '%<VAL1>%'");
		tokensHashMap.put("string:starts with", " like '<VAL1>%'");
		tokensHashMap.put("string:ends with", " like '%<VAL1>'");
		tokensHashMap.put("string:notcontains", " not like '%<VAL1>%'");

		/*tokensHashMap.put("number:is", " = <VAL1>");
		tokensHashMap.put("number:gt", " > <VAL1>");
		tokensHashMap.put("number:lt", " < <VAL1>");
		tokensHashMap.put("number:ge", " >= <VAL1>");
		tokensHashMap.put("number:le", " <= <VAL1>");
		tokensHashMap.put("number:ne", " != <VAL1>");
		tokensHashMap.put("number:between", " between <VAL1> and <VAL2>");*/
		
		tokensHashMap.put("number:=", " = <VAL1>");
		tokensHashMap.put("number:>", " > <VAL1>");
		tokensHashMap.put("number:<", " < <VAL1>");
		tokensHashMap.put("number:>=", " >= <VAL1>");
		tokensHashMap.put("number:<=", " <= <VAL1>");
		tokensHashMap.put("number:!=", " != <VAL1>");
		tokensHashMap.put("number:between", " between <VAL1> and <VAL2>");

		tokensHashMap.put("date:is", " = '<VAL1>'");
		tokensHashMap.put("date:before", " <= '<VAL1>'");
		tokensHashMap.put("date:after", " >= '<VAL1>'");
		tokensHashMap.put("date:between", " between '<VAL1>' and '<VAL2>'");

		tokensHashMap.put("boolean:is", " = '<VAL1>'");
	}
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @return
	 */
	public static String generateListSegmentQuery(String queryString) {
		String returnStr=null;		
		if(logger.isDebugEnabled()) logger.debug("--just entered--"+queryString);
		try {
			HashMap<String, String> filterMap = new HashMap<String, String>();
			String listIdStr=null;
			String[] tokensArr = queryString.split("\\|\\|");
			String finalStr="";

			String token=null;
			String tokenFilterStr="";
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				if(logger.isDebugEnabled()) logger.debug("token :"+ token);

				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) return null;
					listIdStr = token.substring(token.indexOf(':')+1).trim();
					token = token.substring(0, token.indexOf(':')).trim();
					tokenFilterStr =(token.equalsIgnoreCase("all")) ? "AND" : "OR";
					continue;
				}
				//************************************
				
				String fieldsArr[] = token.split("\\|");
				if(fieldsArr.length < 3) {
					if(logger.isDebugEnabled()) logger.debug("Invalid token="+token);
					continue;
				}
				
				String keyStr=null;
				String valStr=null;
				if(logger.isDebugEnabled()) logger.debug("fieldsArr[0] :" + fieldsArr[0]);
				if(fieldsArr[0].startsWith("CF:")) {
					String[] cfTokenArr = fieldsArr[0].split(":");
					keyStr = cfTokenArr[1].trim();
					valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
				} 
				else {
					keyStr="GENERAL";
					valStr = replaceToken(fieldsArr[0].trim(), fieldsArr);
				}
				
				if(valStr==null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr)) {
					valStr = filterMap.get(keyStr) + tokenFilterStr + valStr; 
				}				
				filterMap.put(keyStr, valStr);
				//*******************************************************
				
			}// for i
			
			if(filterMap.size()==0) {
				return null;
			}
			else if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				if(logger.isDebugEnabled()) logger.debug(">>> NO Cfs");
				String tempStr = generalSql.replace("<LISTIDS>", listIdStr);
				tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL"));
				finalStr=tempStr;
			}
			else {
				if(logger.isDebugEnabled()) logger.debug(">>> with CFs");
				String[] listIdArr = listIdStr.split(",");
				
				String tempStr="";
				for (String listId : listIdArr) {
					listId = listId.trim();
					if(!tempStr.equals("")) tempStr +=" UNION ";
					
					tempStr = tempStr + oneCFSqlFormat.replace("<LISTID>", listId);
					
					if(filterMap.containsKey("GENERAL") && filterMap.containsKey(listId)) {
						tempStr = tempStr + "AND ("+filterMap.get("GENERAL") +tokenFilterStr+ cfExistSql.replace("<CFCOND>", filterMap.get(listId)) +")";
					}
					else if(filterMap.containsKey("GENERAL")) {
						tempStr = tempStr + "AND ("+filterMap.get("GENERAL") + ")";
					}
					else if(filterMap.containsKey(listId)) {
						tempStr = tempStr + "AND (" + cfExistSql.replace("<CFCOND>", filterMap.get(listId)) +")";	
					}

				} // for
				
				finalStr = "SELECT * FROM ( " + tempStr + " ) AS tempTab GROUP BY email_id";
				
			} // else
			
//			logger.debug("==="+filterMap);
//			logger.debug(listIdStr+"=FN="+finalStr);
			return finalStr;
		} catch (Exception e) {
			logger.debug("Exception : " , e);
			
		}
		return returnStr;
		
	} // generateQuery

	

	/**
	 * Converts to the SQL Query form
	 * @param fieldName
	 * @param tokenArr
	 * @return
	 */
	private static String replaceToken(String fieldName, String[] tokenArr) {
		String outStr=null;
		try {
			String mapStr = tokensHashMap.get(tokenArr[1].toLowerCase().trim());
			if (mapStr==null) return null;
			
			String tempStr = mapStr.replace("<VAL1>", tokenArr[2].trim());
			
			if(tokenArr.length>3 && tempStr.contains("<VAL2>"))
				tempStr = tempStr.replace("<VAL2>", tokenArr[3].trim());
			
			outStr=" ("+ fieldName+ tempStr +") ";
			
		} catch (Exception e) {
			logger.error("Exception ::::", e);
		}
		return outStr;
	}
	
}

