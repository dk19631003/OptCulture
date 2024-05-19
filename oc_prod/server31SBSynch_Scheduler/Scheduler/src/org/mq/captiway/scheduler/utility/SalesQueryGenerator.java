package org.mq.captiway.scheduler.utility;

import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SalesQueryGenerator {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	//private static String generalSql = "SELECT * FROM contacts WHERE list_id IN (<LISTIDS>) AND email_status='Active' AND (<COND>) GROUP BY email_id";
	//private static String generalSqlWithoutEmailStatus = "SELECT list_id, email_id, first_name, last_name, created_date, purged, email_status, last_status_change, last_mail_date, address_one, address_two, city, state, country, pin, phone, optin, subscription_type, optin_status FROM contacts WHERE list_id IN (<LISTIDS>) AND (<COND>) GROUP BY email_id";
	
	private static String generalSql = 
		
		"SELECT * FROM contacts c " +
		"<FROM_TABLES>" +
		"" +
		"" +
		" WHERE c.list_id IN (<LISTIDS>) AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') " +
		"" +
		"<FROM_TABLE_FILTERS>" +
		"AND (<COND>) GROUP BY email_id";
	
	
	
	private static String qry_HomePassed = " SELECT h.* FROM homespassed h WHERE h.user_id IN(<USERIDS>)  AND (<COND>)  ";
	
	
	
	private static String tot_subQuery = " SELECT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS>  AND (c.mlbits&<MLBITS> )>0 " +
										" <FROM_TABLE_FILTERS> AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') AND c.email_id is not null AND c.email_id!='' AND (<COND>) ";

	
	private static String tot_subQuery_mobile = " SELECT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0" +
											" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) AND (<COND>) ";
	
	private static String general_mobile_subQuery = " SELECT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) AND (<COND>)   ";

	
	private static String not_received_email_status =  " SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits, c.categories, c.last_mail_span, c.last_sms_span,  " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
			"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
			+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
			"  WHERE o.contact_id is null  "+
			"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
			" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') <COND> " +
			" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	
	
	
private static String not_received_email_count_status =
		
		" SELECT DISTINCT c.cid" +
		" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
			"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
			+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
			"  WHERE o.contact_id is null  "+
			"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
			" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') <COND> " +
			" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	
//TODO sales date condition need to be think
	/*private static String aggr_query_template = " ( SELECT cid, SUM((sales_price*quantity)+tax) as totAmt FROM retail_pro_sales"
												+ " WHERE user_id IN(<USERIDS>) AND cid IS NOT NULL <SALESDATECOND> GROUP BY doc_sid) AS totSal ";*/

//GROUP BY sal.doc_sid
	private static String aggr_query_template = " ( SELECT sal.* <SALESSELECTFIELDS> FROM retail_pro_sales sal <SALESFROMTABLES> "
		+ " WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL <SALESFROMTABLESFILTERS> <SALESCOND> <SALESGROUPBY> ) AS totSal ";




/*private static String aggr_query_template = " ( SELECT sal.*, SUM((sal.sales_price*sal.quantity)+sal.tax) as totAmt, max((sal.sales_price*sal.quantity)+sal.tax) as maxAmt, avg((sal.sales_price*sal.quantity)+sal.tax) as avgAmt, count(sal.doc_sid) as itemCnt FROM retail_pro_sales sal <SALESFROMTABLES> "
		+ " WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL <SALESFROMTABLESFILTERS> <SALESCOND>  GROUP BY sal.doc_sid) AS totSal ";

*/	
	private static String not_received_mobile_status =  " SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits, c.categories, c.last_mail_span, c.last_sms_span, " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
			"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
			+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
			"  WHERE o.contact_id is null  "+
			"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
			" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN>) <COND> " +
			" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";

	private static String not_received_mobile_count_status =	
		" SELECT DISTINCT c.cid"+
		" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
			"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
			+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
			"  WHERE o.contact_id is null  "+
			"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
			" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN>) <COND> " +
			" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";
	

	
	/*private static String generalSql_mobile=
		
		" SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits,   " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null  " +
		" FROM contacts c  " +
		" , retail_pro_sales sal, retail_pro_sku sku WHERE c.list_id IN (<LISTIDS>) " +
		" AND c.external_id=sal.customer_id AND (<COND>) GROUP BY c.email_id";
		
	" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0 " +
	" <FROM_TABLE_FILTERS>  AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active' <MOBILEOPTIN> )  <COND>   GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";
	*/
	
	private static String generalSql_mobile=
			
		" SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits, c.categories, c.last_mail_span, c.last_sms_span,  " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c "+
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0 " +
		" <FROM_TABLE_FILTERS>  AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active' <MOBILEOPTIN> )  <COND>   GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR> ";
			
	
	private static String mobile_generalSqlWithMobileStatusCount =	
		" SELECT DISTINCT c.cid"+
		" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' " +
		"AND c.mobile_status='Active'  <MOBILEOPTIN> ) <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR> ";

		
		
		/*" SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits,  " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c WHERE c.cid IN( SELECT c.cid" +
			" FROM contacts c  " +		
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0 " +
		" <FROM_TABLE_FILTERS>  AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active' <MOBILEOPTIN> )  <COND>   GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>)";
	*/
		
	/*private static String generalSqlWithoutEmailStatus =
		
		" SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits,  " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null  " +
		" FROM contacts c " +
		" , retail_pro_sales sal, retail_pro_sku sku WHERE c.list_id IN (<LISTIDS>) " +
		" AND c.external_id=sal.customer_id AND (<COND>) GROUP BY c.email_id";
		
	" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0 " +
	" <FROM_TABLE_FILTERS>  AND c.email_status='Active' AND c.email_id is not null AND c.email_id!='' <COND> GROUP BY c.email_id  <SALESFIELDS> <HAVINGSTR> ";
		*/
private static String email_generalSqlWithEmailStatusCount =
		
		" SELECT DISTINCT c.cid" +
		" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"')  <COND> GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";

	
	
	private static String generalSqlWithoutEmailStatus =
			
		" SELECT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id, " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits, c.categories, c.last_mail_span, c.last_sms_span,  " +
		" null, SUBSTRING_INDEX(c.email_id, '@', -1), null  FROM contacts c "+
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND (c.mlbits&<MLBITS> )>0 " +
		" <FROM_TABLE_FILTERS>   AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') AND c.email_id!='' <COND> GROUP BY c.email_id  <SALESFIELDS> <HAVINGSTR>  ";
			

	
	
	private static String cfExistSql=" (cid IN (SELECT contact_id FROM customfield_data WHERE (<CFCOND>) ))";
	
	private static String oneCFSqlFormat = "SELECT * FROM contacts WHERE list_id = <LISTID> AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') "; 
	private static String oneCFSqlFormatWithoutEmailStatus = "SELECT list_id, email_id, first_name, last_name, created_date, purged, email_status, last_status_change, last_mail_date, address_one, address_two, city, state, country, pin, phone, optin, subscription_type, optin_status FROM contacts WHERE list_id = <LISTID> ";

	public static HashMap<String, String> tokensHashMap=new HashMap<String, String>();
	public static HashMap<String, String> salesFieldsMap=new HashMap<String, String>();
	
	
	public static void main(String[] args) {
		String qry = "all:23||Date(c.created_date)|date:onOrBefore|2013-02-08<OR>||Date(sal.sales_date)|date:before_between|20|40<OR>||aggr.tot_purchase_amt|number:>|20<OR>||sku.udf11|String:is|asadsd<OR>";
		
		//qry = "all:761||CF:761:cust_1:dob|date:is|06/05/2012||sal.sales_date|date:onOrAfter|06/05/2012||cs.opens|number:=|100";
		//logger.debug("Qry:"+ generateListSegmentQuery(qry,true, Constants.SEGMENT_ON_EMAIL));
	}
	
	static {
		
		salesFieldsMap.put("aggr_tot", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr_avg", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr_max", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr_count", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("new_tot", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("new_avg", "avg((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as avgAmt");
		salesFieldsMap.put("new_max", "max((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as maxAmt");
		salesFieldsMap.put("new_count", "count(sal.doc_sid) as itemCnt");
		
		
		tokensHashMap.put("string:is", " = '<VAL1>'");
		tokensHashMap.put("string:equal", " = '<VAL1>'");
		tokensHashMap.put("string:equals", " = '<VAL1>'");
		tokensHashMap.put("string:equal to", " = '<VAL1>'");
		tokensHashMap.put("string:is equal", " = '<VAL1>'");
		tokensHashMap.put("string:is equals", " = '<VAL1>'");
		tokensHashMap.put("string:is equal to", " = '<VAL1>'");
		tokensHashMap.put("string:is equals to", " = '<VAL1>'");
		tokensHashMap.put("string:is in", " in (<VAL1>)");
		tokensHashMap.put("string:is not in", " not in (<VAL1>)");
		tokensHashMap.put("string:is not", " != '<VAL1>'");
		tokensHashMap.put("string:startswith", " like '<VAL1>%'");
		tokensHashMap.put("string:endswith", " like '%<VAL1>'");
		tokensHashMap.put("string:contains", " like '%<VAL1>%'");
		tokensHashMap.put("string:starts with", " like '<VAL1>%'");
		tokensHashMap.put("string:ends with", " like '%<VAL1>'");
		tokensHashMap.put("string:does not contain", " not like '%<VAL1>%'");
		tokensHashMap.put("string:=", " = <VAL1>");
		tokensHashMap.put("string:==", " = <VAL1>");
		tokensHashMap.put("string:>", " > <VAL1>");
		tokensHashMap.put("string:not null", " <VAL1>");
		tokensHashMap.put("string:is not null", " <VAL1>");
		tokensHashMap.put("string:not equal to null", " <VAL1>");
		tokensHashMap.put("string:none", " <VAL1>");
		
		
		/*tokensHashMap.put("number:is", " = <VAL1>");
		tokensHashMap.put("number:gt", " > <VAL1>");
		tokensHashMap.put("number:lt", " < <VAL1>");
		tokensHashMap.put("number:ge", " >= <VAL1>");
		tokensHashMap.put("number:le", " <= <VAL1>");
		tokensHashMap.put("number:ne", " != <VAL1>");
		tokensHashMap.put("number:between", " between <VAL1> and <VAL2>");*/
		
		tokensHashMap.put("number:=", " = <VAL1>");
		tokensHashMap.put("number:is in", " in (<VAL1>)");
		tokensHashMap.put("number:is not in", " not in (<VAL1>)");
		tokensHashMap.put("number:>", " > <VAL1>");
		tokensHashMap.put("number:<", " < <VAL1>");
		tokensHashMap.put("number:>=", " >= <VAL1>");
		tokensHashMap.put("number:<=", " <= <VAL1>");
		tokensHashMap.put("number:!=", " != <VAL1>");
		tokensHashMap.put("number:between", " between <VAL1> and <VAL2>");
		tokensHashMap.put("number:range", " between <VAL1> and <VAL2>");
		tokensHashMap.put("number:none", " <VAL1>");

		tokensHashMap.put("date:is", " = '<VAL1>'");
		tokensHashMap.put("date:istoday", " DATEDIFF(now(),<FIELD> ) = 0 " );
		tokensHashMap.put("date:before", " < '<VAL1>'");
		tokensHashMap.put("date:onorbefore", " <= '<VAL1>'");
		tokensHashMap.put("date:none", " <VAL1>");
		
		tokensHashMap.put("date:range_between", " DATEDIFF(now(),<FIELD> ) >= <VAL1> AND  DATEDIFF(now(),<FIELD> ) <= <VAL2> ");
		
		/*tokensHashMap.put("date:before_between", " DATEDIFF(now(),DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR) ) >= <VAL1> " +
							"AND DATEDIFF(now(),DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR)  ) <= <VAL2> ");
		*/
		
		tokensHashMap.put("date:before_between", " DATEDIFF('<FIRSTYEAR_DATE>', DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>) YEAR) ) % 365 >= <VAL1> " +
		"AND DATEDIFF('<FIRSTYEAR_DATE>', DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>) YEAR) ) % 365 <= <VAL2> ");
	
		
		tokensHashMap.put("date:onorafter", " >= '<VAL1>'");
		tokensHashMap.put("date:after", " > '<VAL1>'");
		/*tokensHashMap.put("date:after_between", " DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR), now() ) >= <VAL1> " +
				" AND DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR), now() ) <= <VAL2> ");
		*/
		
		
		tokensHashMap.put("date:after_between", " DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+1 YEAR), '<ZEROYEAR_DATE>' ) % 365 >= <VAL1> " +
		" AND DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+1 YEAR), '<ZEROYEAR_DATE>' ) % 365 <= <VAL2> ");
		
		
		tokensHashMap.put("date:between", " between '<VAL1>' and '<VAL2>'");
		
		tokensHashMap.put("date:withinlast", "DATEDIFF(now(),<FIELD> ) <= <VAL1>");
		tokensHashMap.put("date:notwithinlast", "DATEDIFF(now(),<FIELD> ) <= <VAL1>");

		tokensHashMap.put("boolean:is", " = <VAL1>");
		tokensHashMap.put("boolean:equal", " = <VAL1>");
	}

	
	public static boolean isNotOnlyHomePassed(String queryString) {
		
		String[] tokensArr = queryString.split("\\|\\|");
		
		
		//String listIdStr=null;
		String userIdStr=null;
		String finalStr="";

		String token=null;
		String tokenFilterStr="";
		String valStr = "";
		boolean foundFlag = false;
		for (int i = 0; i < tokensArr.length; i++) {
			token=tokensArr[i].trim();
			 if(logger.isDebugEnabled()) logger.debug("TOKEN="+token);
			 String keyStr=null;
			 
			 valStr = "";
			 
			//****** Get the first token *********
			if(i==0) {
				if(token.indexOf(':')==-1) return false;
				userIdStr = token.substring(token.indexOf(':')+1).trim();
				token = token.substring(0, token.indexOf(':')).trim();
				tokenFilterStr =(token.equalsIgnoreCase("all") || token.equalsIgnoreCase("AND")) ? "AND" : "OR";
				continue;
			}
			String[] tempTokenArr = token.split("<OR>");
			for (int tokenIndex=0; tokenIndex<tempTokenArr.length;tokenIndex++) {
				String tempToken = tempTokenArr[tokenIndex];
				String[] fieldsArr = tempToken.split("\\|");
				if((fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") ) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal.") ||
					(fieldsArr[1].toLowerCase().trim().startsWith("c.") || fieldsArr[1].toLowerCase().trim().startsWith("date(c.") ) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("aggr_") || fieldsArr[1].toLowerCase().trim().startsWith("date(aggr.") ) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") ) ||
					(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem() ) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) || fieldsArr[1].toLowerCase().trim().startsWith("new_"))) ) {
					
					foundFlag = true;
					break;
				}
				
				
			}//for
				
			if(foundFlag) {
				
				return true;
			}
			
		}//for
		
		
		return false;
		
		
	}//isNotOnlyHomePassed()
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString is segment rule 
	 * @return
	 */
	public static String generateListSegmentQuery(String queryString, long mlBit) {
		return generateListSegmentQuery(queryString, true, Constants.SEGMENT_ON_EMAIL, mlBit);
	}
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @return
	 */
	public static String generateSMSListSegmentQuery(String queryString, long mlBit) {
		return generateListSegmentQuery(queryString, false, Constants.SEGMENT_ON_MOBILE, mlBit);
	}
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString is segment rule 
	 * @return
	 */
	public static String generateListSegmentCountQuery(String queryString, long mlBit) {
		return generateListSegmentCountQuery(queryString, true, Constants.SEGMENT_ON_EMAIL, mlBit);
	}
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @return
	 */
	public static String generateSMSListSegmentCountQuery(String queryString, long mlBit) {
		return generateListSegmentCountQuery(queryString, false, Constants.SEGMENT_ON_MOBILE, mlBit);
	}
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @param considerEmailStatus
	 * @return
	 */
	public static String generateListSegmentQuery(String queryString, boolean considerEmailStatus, String qryType,long mlBit) {
		String returnStr=null;		
		try {
			
			String finalStr="";
			if(isNotOnlyHomePassed(queryString)) {
			
			
			//boolean chkSalesColumnsFound=false;
			boolean chkSKUColumnsFound=false;
			boolean chkAggrColumnsFound = false;
			boolean chkOpensClicksFound = false;
			boolean chkLoyaltyFlagFound = false;
			boolean chkHomePassedFound = false;
			//boolean chkAggregateFlagFound = false;
			StringBuffer aggrFieldsSB = new StringBuffer();
			StringBuffer groupBySB = new StringBuffer();
			StringBuffer HavingSB = new StringBuffer();
			StringBuffer salesConditionSB = new StringBuffer();
			StringBuffer csConditionSB = new StringBuffer();
			StringBuffer csInnerConditionSB = new StringBuffer();
			boolean isCsNotReceivedUnionExist = false;
			String salesSelectFields = Constants.STRING_NILL;
			//StringBuffer csNotReceivedUnionQrySB = new StringBuffer();
			//String csColumnsStr = "";
			String csInnerColumnsStr = "";
			
			
			String fromTables = "";
			String fromTableFilters = "";
			
			String salesFromTables = "";
			String salesFromTableFilters = "";
			
			/**
			 * 761
			 * ||sal.sales_date|date:is|03/05/2012<OR>sal.sales_date|date:is|07/05/2012<OR>
			 * sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
			 * ||sal.sales_date|date:isToday|now()<AND>
			 */
			
			
			
			
			HashMap<String, String> filterMap = new HashMap<String, String>();
			//String listIdStr=null;
			String userIdStr=null;
			String[] tokensArr = queryString.split("\\|\\|");
			
		

			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			
			boolean isAggr = false;
			//boolean isAggrOnRcpt = false;
			
			String csCampIDs = "";
			//String crIds = "";
			boolean isLatestCampaigns = false;
			int numOfCampaigns = 0;
			
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				 if(logger.isDebugEnabled()) logger.debug("TOKEN="+token);
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) return null;
					
					String[] firstTokenArr = token.split(":");
					tokenFilterStr = (firstTokenArr[0].equalsIgnoreCase("all") || firstTokenArr[0].equalsIgnoreCase("AND")) ? "AND" : "OR";
					userIdStr = firstTokenArr[1];
					if(firstTokenArr.length > 2) {
						
						csCampIDs = firstTokenArr[2];
						//if(firstTokenArr.length == 4) crIds = firstTokenArr[3];
						if(firstTokenArr.length == 4) isLatestCampaigns = true;
						
						StringTokenizer tokenizerr = new StringTokenizer(csCampIDs, Constants.DELIMETER_COMMA);
						numOfCampaigns = tokenizerr.countTokens();
					}//if
					
					continue;
				}
				//************************************
				
				//now my token sal.sales_date|date:is|03/05/2012<OR>
				//sal.sales_date|date:is|07/05/2012<OR>
				//sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
				
				String[] tempTokenArr = token.split("<OR>");
				StringBuffer orHavingSB = new StringBuffer();
				StringBuffer orSalesConditionSB = new StringBuffer();
				StringBuffer csConditionQry = new StringBuffer();//.STRING_NILL;
				String csInnerCondition = Constants.STRING_NILL;
				
				for (int tokenIndex = 0;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
					String fieldsArr[] = tempToken.split("\\|");
					logger.info("tempToken :: "+tempToken);
					//include aggr fields in the select clause
					
					if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count")) {
						
						//isAggr = true;
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
					}//if
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_max") || fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" )){
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(groupBySB.length() == 0 || isAggr == false) {
							
							groupBySB.append(",totSal.cid");//doc_sid will not always points to udf10
							isAggr = true;
							
						}
						
					}//if
					
					
					if(chkAggrColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggr_") 
							//|| (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") ) || 
							|| fieldsArr[1].toLowerCase().trim().startsWith("new_") || 
							fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						
						chkAggrColumnsFound = true;
						 fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
						fromTableFilters  += " AND c.cid=totSal.cid  ";
						
						
					}
					
					if(chkSKUColumnsFound==false && (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") )) {
						
						chkSKUColumnsFound=true;
						if(chkAggrColumnsFound==false) {
							
							chkAggrColumnsFound = true;
							 fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							fromTableFilters  += " AND c.cid=totSal.cid  ";
							
							
						}
						salesFromTables += "  , retail_pro_sku sku ";
						salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
						
						/*salesFromTables += "  , retail_pro_sku sku ";
							salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
							
						}
						else if(chkAggrColumnsFound==true) {
							
							salesFromTables += "  , retail_pro_sku sku ";
							salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
							
						}*/
					} // if
					
					/*if(chkAggregateFlagFound == false &&( tempToken.toLowerCase().trim().startsWith("aggr.") || tempToken.toLowerCase().trim().startsWith("date(aggr.") )) {
						
						chkAggregateFlagFound = true;
						fromTables += " , sales_aggregate_data aggr";
						fromTableFilters  += " AND aggr.user_id in(<SALESAGGRUSERID>) AND c.cid=aggr.cid"; 
						
					}*///if
					
					/*if(chkSalesColumnsFound == false && ( fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count") )  ) {
						
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";
						
					}*/
									
					if(chkLoyaltyFlagFound == false &&( fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") )) {
						
						chkLoyaltyFlagFound = true;
						fromTables += " , contacts_loyalty loyalty ";
						fromTableFilters  += " AND loyalty.contact_id=c.cid "; 
						
					}//if
					
					//logger.info("found sales flag........."+tempToken.toLowerCase().trim().startsWith("Date(sal."));
					/*if(chkSKUColumnsFound==false && chkSalesColumnsFound==false &&
							(fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid ";  
					} // if
					 */					
					
					if(chkOpensClicksFound == false &&( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
							|| ( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem() ) 
									&& fieldsArr[1].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_RECEIVED.getColumnName()) ) ) ) {
						
						chkOpensClicksFound = true;
						/*fromTables += " , campaign_sent cs ";
						fromTableFilters  += " AND c.cid=cs.contact_id "; 
						*/
						
					}//if
					//chkHomePassedFound
					if(chkHomePassedFound == false &&( fieldsArr[1].toLowerCase().trim().startsWith("h.") || fieldsArr[1].toLowerCase().trim().startsWith("date(h."))) {
						
						chkHomePassedFound = true;
						fromTables += " , homespassed h ";
						fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
						
					}//if
					
					
					
					
					if(fieldsArr.length < 4) {
						logger.info("Invalid token="+token);
						continue;
					}
					
					if(fieldsArr[1].startsWith("CF:")) {
						logger.info("in if");
						String[] cfTokenArr = fieldsArr[1].split(":");
						keyStr = cfTokenArr[1].trim();
						valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
					} 
					else {
						keyStr="GENERAL";
						
						
						String mapToken = fieldsArr[2].toLowerCase().trim();
						//logger.debug("maptoken ::"+mapToken);
						
						if(mapToken.toLowerCase().startsWith("date:notwithinlast")) {
							
							if(valStr.length() > 0){
								valStr += " OR ";
							}
							//TODO need to replace the sub query(Changes made for getting data of past 60 prob)
							valStr += prepareDateSubQuery(fieldsArr[1].trim(), fieldsArr,
									userIdStr, qryType,  mlBit);
						
							
							
							
						}//if
						else {
							
							String replacedVal = replaceToken(fieldsArr[1].trim(), fieldsArr); 
							
							if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_avg") || 
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_max") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_count")) {
								
								//if(orHavingSB.length() > 0) orHavingSB.append("OR");
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								if(selectFieldEnum != null) {
									
									replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
									
								}
								
								//orHavingSB.append(replacedVal);
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
								
							}//if
							else if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_avg") || 
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_max") || fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ) {//TODO no need of another condition 
							
								if(orHavingSB.length() > 0) orHavingSB.append("OR");
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								if(selectFieldEnum != null) {
									
									replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
									
								}
							
								orHavingSB.append(replacedVal);
							
						
						
							}//if
							else if(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
									|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
									  || fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) ){
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								logger.debug("=====1=========="+selectFieldEnum.getColumnName());
								
								if(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
										|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem())
												|| (fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem())
														&& selectFieldEnum != null && selectFieldEnum.getColumnName().equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_RECEIVED.getColumnName()) ) ){
									logger.debug("=====2=========="+selectFieldEnum.getSelectFieldName());

									if(selectFieldEnum != null) {
										
										replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
										
									}
									
									
									if(csConditionQry.length() > 0) csConditionQry.append("OR");
									
									csConditionQry.append(replacedVal);
									
									/*if(!csColumnsStr.contains(selectFieldEnum.getColumnName())) {
										
										if(!csColumnsStr.isEmpty()) csColumnsStr += Constants.DELIMETER_COMMA;
										
										csColumnsStr += selectFieldEnum.getColumnName();
										
									}//if
	*/								
									if( (fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
											|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem())) && !csInnerColumnsStr.contains(selectFieldEnum.getInnerQryColumnName()) ) {
									
										
										//if(!csInnerColumnsStr.isEmpty()) csInnerColumnsStr += Constants.DELIMETER_COMMA;
										logger.debug("=====2=========="+selectFieldEnum.getInnerQryColumnName());
										csInnerColumnsStr += Constants.DELIMETER_COMMA+selectFieldEnum.getInnerQryColumnName();
										
									}//if
									
								}//if
								else if( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) ) { 
									
									if((selectFieldEnum.getColumnName() != null && 
											selectFieldEnum.getColumnName().equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_NOT_RECEIVED.getColumnName()))) {
									
										/*if(csInnerCondition.length() > 0) csInnerCondition += "OR";
											
										csInnerCondition += replaceToken(fieldsArr[1].trim(), fieldsArr); */
										replacedVal = Constants.STRING_NILL;
										
										//as all the interaction attributes appear only once you can 
										//create new union SB everytime u found received_no rule 
										//csNotReceivedUnionQrySB = new StringBuffer(" UNION ");//+not_received_email); 
										isCsNotReceivedUnionExist = true;
										
									}/*else {
										
										replacedVal = Constants.STRING_NILL;
										
										//as all the interaction attributes appear only once you can 
										//create new union SB everytime u found received_no rule 
										//csNotReceivedUnionQrySB = new StringBuffer(" UNION ");//+not_received_email); 
										isCsNotReceivedUnionExist = true;
										
									}*/
									
								}//else if
								
								
								
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("sal.") ||
									fieldsArr[1].toLowerCase().trim().startsWith("date(sal.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(sku.")||
									fieldsArr[1].toLowerCase().trim().startsWith("sku.")) {
								
								
								if(orSalesConditionSB.length() > 0) orSalesConditionSB.append("OR");
								
								orSalesConditionSB.append(replacedVal);
								
							}
							else{
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
							
							}
						}
					}
					
				} // for 
				
				
				if( orHavingSB.length() > 0 ) {
					
					if(HavingSB.length() > 0) HavingSB.append(tokenFilterStr);
					
					HavingSB.append("("+orHavingSB.toString()+")");
					
					
				}//if
				
				if(orSalesConditionSB.length() > 0) {
					
					if(salesConditionSB.length() > 0) salesConditionSB.append(tokenFilterStr);
					
					salesConditionSB.append("("+orSalesConditionSB.toString()+")");
					
					
				}
				
				if(csConditionQry.length() > 0) {
					
					if(csConditionSB.length() > 0) csConditionSB.append(tokenFilterStr);
					
					csConditionSB.append("("+csConditionQry.toString()+")");
					
					
				}
				/*if(csInnerCondition.length() > 0) {
					
					if(csInnerConditionSB.length() > 0) csInnerConditionSB.append(tokenFilterStr);
					
					csInnerConditionSB.append("("+csInnerCondition+")");
					
					
				}*/
				
				if(valStr == null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr) && !valStr.isEmpty() ) {
					
					valStr = " ("+filterMap.get(keyStr)+") " + tokenFilterStr +" ("+ valStr +") "; 
				}	
				
				
				if(!valStr.isEmpty()) { filterMap.put(keyStr, valStr); }
				
				if(logger.isDebugEnabled()) logger.debug("filterMap :: "+filterMap);
				
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				boolean isHaving = (isAggr || chkAggrColumnsFound || chkOpensClicksFound || isCsNotReceivedUnionExist);
				if(isHaving) {
					logger.debug("isHaving ::"+isHaving );
					filterMap.put("GENERAL", "");
				}
				
				else{
					
					return null;
				}
				
			}
			if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.debug(">>> NO Cfs");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithoutEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
					String tempStr =null; 
					String csNotReceivedUnionQryStr = null;//csNotReceivedUnionQrySB.length() > 0 ? csNotReceivedUnionQrySB.toString() : "";
					
					if(considerEmailStatus == true && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						
						tempStr = generalSqlWithoutEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
						replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
						replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_email_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
					}else if(considerEmailStatus == false && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						
						tempStr = generalSql_mobile.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
						replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
						replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
		
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobile_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
					}
					
				//logger.info("fromTables ::"+fromTableFilters);
					fromTables = fromTables.replace("<SALESUSERID>", userIdStr).
							replace("<SALESFROMTABLES>", salesFromTables).replace("<SALESFROMTABLESFILTERS>", salesFromTableFilters).
							replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
							.replace("<HPUSERID>", userIdStr);
		
					/*if(chkAggrColumnsFound && chkSalesColumnsFound) {
						fromTableFilters  += " AND totSal.cid=sal.cid ";
					}*/
					fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).
					replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
					.replace("<HPUSERID>", userIdStr);
					
					/************REFERENCE QUERY*******************************
					 * 1.SELECT SUM(IF(t.some >0, 1,0)) AS tot, t.email_id, t.some
					 *  FROM( SELECT COUNT(email_id) AS cnt , email_id, SUM(IF(opens >0, 1,0)) AS some 
					 *  	FROM campaign_sent 
					 *  	WHERE campaign_id in(3377, 2287) 
					 *  	GROUP BY email_id , campaign_id   ) AS t 
				 *  	WHERE t.some >0 
				 *  	GROUP BY t.email_id 
				 *  	HAVING COUNT(t.email_id) = SUM(IF(t.some >0, 1,0)) AND COUNT(t.email_id) >=2  
						
						2.SELECT  t.email_id 
						FROM( SELECT  email_id 
							FROM campaign_sent 
							WHERE campaign_id IN(3377, 2287) AND status='Success'  
							GROUP BY email_id , campaign_id having sum(opens) >0  ) as t
						 GROUP BY t.email_id 
						 HAVING COUNT(t.email_id) >=2   

					 */
					
					
					
					String csTableFilter = "";
					String csFromTables = "";
					if(chkOpensClicksFound) {
						
						csFromTables =   " , ( " +
										" SELECT DISTINCT contact_id , t.email_id, t.status " +
											" FROM( SELECT contact_id, COUNT(email_id) AS cnt , email_id, status <INNERCOLUMNNAMES> " +
											" FROM campaign_sent WHERE campaign_id IN("+csCampIDs+") " +
											" "+(isLatestCampaigns ? Constants.INTERACTION_CAMPAIGN_CRID_PH :"")+"  GROUP BY email_id , campaign_id   ) AS t "+
										" GROUP BY t.email_id " +
										"  <CSCONDITIONQRY>  )o ";//ON o.contact_id=c.cid ";
						
						csTableFilter = " AND o.contact_id=c.cid ";
						
					}
					//csFromTables = csFromTables.replace("<CSINNERCONDITION>", csInnerConditionSB.length() > 0 ? (" AND "+csInnerConditionSB.toString()) : "" );
					csFromTables = csFromTables.replace("<CSCONDITIONQRY>", csConditionSB.toString().length() > 0 ? " HAVING "+csConditionSB.toString() : "");
					//fromTables = fromTables.replace("<COLUMNNAMES>", csColumnsStr);
					csFromTables = csFromTables.replace("<INNERCOLUMNNAMES>", csInnerColumnsStr);
					
					tempStr = tempStr.replace("<FROM_TABLES>", fromTables+csFromTables);
					tempStr = tempStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters+csTableFilter);
					
					if(isCsNotReceivedUnionExist && !csNotReceivedUnionQryStr.isEmpty()) {
						
						csNotReceivedUnionQryStr = csNotReceivedUnionQryStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters)
															.replace("<FROM_TABLES>", fromTables);
						
						csNotReceivedUnionQryStr = csNotReceivedUnionQryStr.replace(Constants.INTERACTION_CAMPAIGN_IDS_PH, csCampIDs)
													.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, (isLatestCampaigns ? Constants.INTERACTION_CAMPAIGN_CRID_PH :""));
													
						tempStr = ( (!chkOpensClicksFound && isCsNotReceivedUnionExist) ? csNotReceivedUnionQryStr : (tempStr +" UNION "+ csNotReceivedUnionQryStr) );//tempStr ++ csNotReceivedUnionQryStr ;
						
					}
					
					
					
					//tempStr = tempStr.replace("<FROM_TABLES>", fromTables);
					
					tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL") != null && !filterMap.get("GENERAL").isEmpty()	? " AND ("+ filterMap.get("GENERAL")+") " : "" );
					tempStr = tempStr.replace("<SALESCOND>", salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "");
					tempStr = tempStr.replace("<SALESSELECTFIELDS>", !salesSelectFields.isEmpty() ? Constants.DELIMETER_COMMA+salesSelectFields : "");
					tempStr = tempStr.replace("<SALESGROUPBY>", !salesSelectFields.isEmpty() ? " GROUP BY sal.doc_sid " : "");
				
				//replace zero year and first year
				Calendar currCal = Calendar.getInstance();
				String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
				
				String zeroYear  = "0000-"+currMonthDate;
				String firstYear = "0001-"+currMonthDate;
				
				tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
				tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
				
				
				
				finalStr=tempStr;
			}
			else {
				if(logger.isDebugEnabled()) logger.debug(">>> with CFs");
				String[] listIdArr = userIdStr.split(",");
				
				String tempStr="";
				for (String listId : listIdArr) {
					listId = listId.trim();
					if(!tempStr.equals("")) tempStr +=" UNION ";
					
					if(considerEmailStatus==true) 
						tempStr = tempStr + oneCFSqlFormatWithoutEmailStatus.replace("<LISTID>", listId);
					else
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
			}
			else {
				
				finalStr = generateHomePassedQry(queryString, qryType, mlBit);
				
			}
			
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::::", e);
			return null;
		}
		//return returnStr;
		
	} // generateQuery
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @param considerEmailStatus
	 * @return
	 */
	public static String generateListSegmentCountQuery(String queryString, boolean considerEmailStatus, String qryType,long mlBit) {
		String returnStr=null;		
		try {
			
			String finalStr="";
			if(isNotOnlyHomePassed(queryString)) {
			
			
			//boolean chkSalesColumnsFound=false;
			boolean chkSKUColumnsFound=false;
			boolean chkAggrColumnsFound = false;
			boolean chkOpensClicksFound = false;
			boolean chkLoyaltyFlagFound = false;
			boolean chkHomePassedFound = false;
			//boolean chkAggregateFlagFound = false;
			StringBuffer aggrFieldsSB = new StringBuffer();
			StringBuffer groupBySB = new StringBuffer();
			StringBuffer HavingSB = new StringBuffer();
			StringBuffer salesConditionSB = new StringBuffer();
			StringBuffer csConditionSB = new StringBuffer();
			StringBuffer csInnerConditionSB = new StringBuffer();
			boolean isCsNotReceivedUnionExist = false;
			String salesSelectFields = Constants.STRING_NILL;
			//StringBuffer csNotReceivedUnionQrySB = new StringBuffer();
			//String csColumnsStr = "";
			String csInnerColumnsStr = "";
			
			
			String fromTables = "";
			String fromTableFilters = "";
			
			String salesFromTables = "";
			String salesFromTableFilters = "";
			
			
			/**
			 * 761
			 * ||sal.sales_date|date:is|03/05/2012<OR>sal.sales_date|date:is|07/05/2012<OR>
			 * sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
			 * ||sal.sales_date|date:isToday|now()<AND>
			 */
			
			
			
			
			HashMap<String, String> filterMap = new HashMap<String, String>();
			//String listIdStr=null;
			String userIdStr=null;
			String[] tokensArr = queryString.split("\\|\\|");
			
		

			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			
			boolean isAggr = false;
			//boolean isAggrOnRcpt = false;
			
			
			
			String csCampIDs = "";
			//String crIds = "";
			boolean isLatestCampaigns = false;
			int numOfCampaigns = 0;
			
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				 if(logger.isDebugEnabled()) logger.debug("TOKEN="+token);
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) return null;
					
					String[] firstTokenArr = token.split(":");
					tokenFilterStr = (firstTokenArr[0].equalsIgnoreCase("all") || firstTokenArr[0].equalsIgnoreCase("AND")) ? "AND" : "OR";
					userIdStr = firstTokenArr[1];
					if(firstTokenArr.length > 2) {
						
						csCampIDs = firstTokenArr[2];
						//if(firstTokenArr.length == 4) crIds = firstTokenArr[3];
						if(firstTokenArr.length == 4) isLatestCampaigns = true;
						
						StringTokenizer tokenizerr = new StringTokenizer(csCampIDs, Constants.DELIMETER_COMMA);
						numOfCampaigns = tokenizerr.countTokens();
					}//if
					
					continue;
				}
				//************************************
				
				//now my token sal.sales_date|date:is|03/05/2012<OR>
				//sal.sales_date|date:is|07/05/2012<OR>
				//sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
				
				String[] tempTokenArr = token.split("<OR>");
				StringBuffer orHavingSB = new StringBuffer();
				StringBuffer orSalesConditionSB = new StringBuffer();
				StringBuffer csConditionQry = new StringBuffer();//.STRING_NILL;
				String csInnerCondition = Constants.STRING_NILL;
				
				for (int tokenIndex = 0;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
					String fieldsArr[] = tempToken.split("\\|");
					logger.info("tempToken :: "+tempToken);
					//include aggr fields in the select clause
					
					if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count")) {
						
						//isAggr = true;
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
					}//if
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_max") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" )){
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(groupBySB.length() == 0 || isAggr == false) {
							
							groupBySB.append(",totSal.cid");//doc_sid will not always points to udf10
							isAggr = true;
							
						}
						
					}//if
					
					
					if(chkAggrColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggr_") 
							//|| (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") ) || 
							|| fieldsArr[1].toLowerCase().trim().startsWith("new_") || 
							fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						
						chkAggrColumnsFound = true;
						 fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
						fromTableFilters  += " AND c.cid=totSal.cid  ";
						
						
					}
					
					if(chkSKUColumnsFound==false && (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") )) {
						
						chkSKUColumnsFound=true;
						if(chkAggrColumnsFound==false) {
							
							chkAggrColumnsFound = true;
							 fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							fromTableFilters  += " AND c.cid=totSal.cid  ";
							
							
						}
						salesFromTables += "  , retail_pro_sku sku ";
						salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
						
						/*salesFromTables += "  , retail_pro_sku sku ";
							salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
							
						}
						else if(chkAggrColumnsFound==true) {
							
							salesFromTables += "  , retail_pro_sku sku ";
							salesFromTableFilters  += "  AND sku.user_id in(<SKUUSERID>) AND  sku.sku_id=sal.inventory_id "; 
							
						}*/
					} // if
					
					/*if(chkAggregateFlagFound == false &&( tempToken.toLowerCase().trim().startsWith("aggr.") || tempToken.toLowerCase().trim().startsWith("date(aggr.") )) {
						
						chkAggregateFlagFound = true;
						fromTables += " , sales_aggregate_data aggr";
						fromTableFilters  += " AND aggr.user_id in(<SALESAGGRUSERID>) AND c.cid=aggr.cid"; 
						
					}*///if
					
					/*if(chkSalesColumnsFound == false && ( fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count") )  ) {
						
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";
						
					}*/
									
					if(chkLoyaltyFlagFound == false &&( fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") )) {
						
						chkLoyaltyFlagFound = true;
						fromTables += " , contacts_loyalty loyalty ";
						fromTableFilters  += " AND loyalty.contact_id=c.cid "; 
						
					}//if
					
					//logger.info("found sales flag........."+tempToken.toLowerCase().trim().startsWith("Date(sal."));
					/*if(chkSKUColumnsFound==false && chkSalesColumnsFound==false &&
							(fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid ";  
					} // if
					 */					
					
					if(chkOpensClicksFound == false &&( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
							|| ( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem() ) 
									&& fieldsArr[1].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_RECEIVED.getColumnName()) ) ) ) {
						
						chkOpensClicksFound = true;
						/*fromTables += " , campaign_sent cs ";
						fromTableFilters  += " AND c.cid=cs.contact_id "; 
						*/
						
					}//if
					//chkHomePassedFound
					if(chkHomePassedFound == false &&( fieldsArr[1].toLowerCase().trim().startsWith("h.") || fieldsArr[1].toLowerCase().trim().startsWith("date(h."))) {
						
						chkHomePassedFound = true;
						fromTables += " , homespassed h ";
						fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
						
					}//if
					
					
					
					
					if(fieldsArr.length < 4) {
						logger.info("Invalid token="+token);
						continue;
					}
					
					if(fieldsArr[1].startsWith("CF:")) {
						logger.info("in if");
						String[] cfTokenArr = fieldsArr[1].split(":");
						keyStr = cfTokenArr[1].trim();
						valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
					} 
					else {
						keyStr="GENERAL";
						
						
						String mapToken = fieldsArr[2].toLowerCase().trim();
						//logger.debug("maptoken ::"+mapToken);
						
						if(mapToken.toLowerCase().startsWith("date:notwithinlast")) {
							
							if(valStr.length() > 0){
								valStr += " OR ";
							}
							//TODO need to replace the sub query(Changes made for getting data of past 60 prob)
							valStr += prepareDateSubQuery(fieldsArr[1].trim(), fieldsArr,
									userIdStr, qryType,  mlBit);
						
							
							
							
						}//if
						else {
							
							String replacedVal = replaceToken(fieldsArr[1].trim(), fieldsArr); 
							
							if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_avg") || 
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_max") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("new_count")) {
								
								//if(orHavingSB.length() > 0) orHavingSB.append("OR");
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								if(selectFieldEnum != null) {
									
									replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
									
								}
								
								//orHavingSB.append(replacedVal);
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
								
							}//if
							else if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_avg") || 
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_max") || fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ) {//TODO no need of another condition 
							
								if(orHavingSB.length() > 0) orHavingSB.append("OR");
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								if(selectFieldEnum != null) {
									
									replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
									
								}
							
								orHavingSB.append(replacedVal);
							
						
						
							}//if
							else if(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
									|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
									  || fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) ){
								
								SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
								logger.debug("=====1=========="+selectFieldEnum.getColumnName());
								
								if(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
										|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem())
												|| (fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem())
														&& selectFieldEnum != null && selectFieldEnum.getColumnName().equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_RECEIVED.getColumnName()) ) ){
									logger.debug("=====2=========="+selectFieldEnum.getSelectFieldName());

									if(selectFieldEnum != null) {
										
										replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
										
									}
									
									
									if(csConditionQry.length() > 0) csConditionQry.append("OR");
									
									csConditionQry.append(replacedVal);
									
									/*if(!csColumnsStr.contains(selectFieldEnum.getColumnName())) {
										
										if(!csColumnsStr.isEmpty()) csColumnsStr += Constants.DELIMETER_COMMA;
										
										csColumnsStr += selectFieldEnum.getColumnName();
										
									}//if
	*/								
									if( (fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
											|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem())) && !csInnerColumnsStr.contains(selectFieldEnum.getInnerQryColumnName()) ) {
									
										
										//if(!csInnerColumnsStr.isEmpty()) csInnerColumnsStr += Constants.DELIMETER_COMMA;
										logger.debug("=====2=========="+selectFieldEnum.getInnerQryColumnName());
										csInnerColumnsStr += Constants.DELIMETER_COMMA+selectFieldEnum.getInnerQryColumnName();
										
									}//if
									
								}//if
								else if( fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) ) { 
									
									if((selectFieldEnum.getColumnName() != null && 
											selectFieldEnum.getColumnName().equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED_IS_NOT_RECEIVED.getColumnName()))) {
									
										/*if(csInnerCondition.length() > 0) csInnerCondition += "OR";
											
										csInnerCondition += replaceToken(fieldsArr[1].trim(), fieldsArr); */
										replacedVal = Constants.STRING_NILL;
										
										//as all the interaction attributes appear only once you can 
										//create new union SB everytime u found received_no rule 
										//csNotReceivedUnionQrySB = new StringBuffer(" UNION ");//+not_received_email); 
										isCsNotReceivedUnionExist = true;
										
									}/*else {
										
										replacedVal = Constants.STRING_NILL;
										
										//as all the interaction attributes appear only once you can 
										//create new union SB everytime u found received_no rule 
										//csNotReceivedUnionQrySB = new StringBuffer(" UNION ");//+not_received_email); 
										isCsNotReceivedUnionExist = true;
										
									}*/
									
								}//else if
								
								
								
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("sal.") ||
									fieldsArr[1].toLowerCase().trim().startsWith("date(sal.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(sku.")||
									fieldsArr[1].toLowerCase().trim().startsWith("sku.")) {
								
								
								if(orSalesConditionSB.length() > 0) orSalesConditionSB.append("OR");
								
								orSalesConditionSB.append(replacedVal);
								
							}
							else{
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
							
							}
						}
					}
					
					
					
					
					
				} // for 
				
				
				if( orHavingSB.length() > 0 ) {
					
					if(HavingSB.length() > 0) HavingSB.append(tokenFilterStr);
					
					HavingSB.append("("+orHavingSB.toString()+")");
					
					
				}//if
				
				if(orSalesConditionSB.length() > 0) {
					
					if(salesConditionSB.length() > 0) salesConditionSB.append(tokenFilterStr);
					
					salesConditionSB.append("("+orSalesConditionSB.toString()+")");
					
					
				}
				
				if(csConditionQry.length() > 0) {
					
					if(csConditionSB.length() > 0) csConditionSB.append(tokenFilterStr);
					
					csConditionSB.append("("+csConditionQry.toString()+")");
					
					
				}
				/*if(csInnerCondition.length() > 0) {
					
					if(csInnerConditionSB.length() > 0) csInnerConditionSB.append(tokenFilterStr);
					
					csInnerConditionSB.append("("+csInnerCondition+")");
					
					
				}*/
				
								if(valStr == null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr) && !valStr.isEmpty() ) {
					
					valStr = " ("+filterMap.get(keyStr)+") " + tokenFilterStr +" ("+ valStr +") "; 
				}	
				
				
				if(!valStr.isEmpty()) { filterMap.put(keyStr, valStr); }
				
				if(logger.isDebugEnabled()) logger.debug("filterMap :: "+filterMap);
				
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				boolean isHaving = (isAggr || chkAggrColumnsFound || chkOpensClicksFound || isCsNotReceivedUnionExist);
				if(isHaving) {
					logger.debug("isHaving ::"+isHaving );
					filterMap.put("GENERAL", "");
				}
				
				else{
					
					return null;
				}
				
			}
			if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.debug(">>> NO Cfs");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithoutEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
					String tempStr =null; 
					String csNotReceivedUnionQryStr = null;//csNotReceivedUnionQrySB.length() > 0 ? csNotReceivedUnionQrySB.toString() : "";
					if(considerEmailStatus == true && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						
						tempStr = email_generalSqlWithEmailStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
						replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
						replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_email_count_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
					}else if(considerEmailStatus == false && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						
						tempStr = mobile_generalSqlWithMobileStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
						replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
						replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
		
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobile_count_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
					
					
					
					
					}
				
					logger.info("fromTables ::"+fromTableFilters);
					fromTables = fromTables.replace("<SALESUSERID>", userIdStr).
							replace("<SALESFROMTABLES>", salesFromTables).replace("<SALESFROMTABLESFILTERS>", salesFromTableFilters).
							replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
							.replace("<HPUSERID>", userIdStr);
		
					/*if(chkAggrColumnsFound && chkSalesColumnsFound) {
						fromTableFilters  += " AND totSal.cid=sal.cid ";
					}*/
					fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).
					replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
					.replace("<HPUSERID>", userIdStr);
					/************REFERENCE QUERY*******************************
					 * 1.SELECT SUM(IF(t.some >0, 1,0)) AS tot, t.email_id, t.some
					 *  FROM( SELECT COUNT(email_id) AS cnt , email_id, SUM(IF(opens >0, 1,0)) AS some 
					 *  	FROM campaign_sent 
					 *  	WHERE campaign_id in(3377, 2287) 
					 *  	GROUP BY email_id , campaign_id   ) AS t 
				 *  	WHERE t.some >0 
				 *  	GROUP BY t.email_id 
				 *  	HAVING COUNT(t.email_id) = SUM(IF(t.some >0, 1,0)) AND COUNT(t.email_id) >=2  
						
						2.SELECT  t.email_id 
						FROM( SELECT  email_id 
							FROM campaign_sent 
							WHERE campaign_id IN(3377, 2287) AND status='Success'  
							GROUP BY email_id , campaign_id having sum(opens) >0  ) as t
						 GROUP BY t.email_id 
						 HAVING COUNT(t.email_id) >=2   

					 */
					
					
					
					String csTableFilter = "";
					String csFromTables = "";
					if(chkOpensClicksFound) {
						
						csFromTables =   " , ( " +
										" SELECT DISTINCT contact_id , t.email_id, t.status " +
											" FROM( SELECT contact_id, COUNT(email_id) AS cnt , email_id, status <INNERCOLUMNNAMES> " +
											" FROM campaign_sent WHERE campaign_id IN("+csCampIDs+") " +
											" "+(isLatestCampaigns ? Constants.INTERACTION_CAMPAIGN_CRID_PH :"")+"  GROUP BY email_id , campaign_id   ) AS t "+
										" GROUP BY t.email_id " +
										"  <CSCONDITIONQRY>  )o ";//ON o.contact_id=c.cid ";
						
						csTableFilter = " AND o.contact_id=c.cid ";
						
					}
					//csFromTables = csFromTables.replace("<CSINNERCONDITION>", csInnerConditionSB.length() > 0 ? (" AND "+csInnerConditionSB.toString()) : "" );
					csFromTables = csFromTables.replace("<CSCONDITIONQRY>", csConditionSB.toString().length() > 0 ? " HAVING "+csConditionSB.toString() : "");
					//fromTables = fromTables.replace("<COLUMNNAMES>", csColumnsStr);
					csFromTables = csFromTables.replace("<INNERCOLUMNNAMES>", csInnerColumnsStr);
					
					tempStr = tempStr.replace("<FROM_TABLES>", fromTables+csFromTables);
					tempStr = tempStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters+csTableFilter);
					
					if(isCsNotReceivedUnionExist && !csNotReceivedUnionQryStr.isEmpty()) {
						
						csNotReceivedUnionQryStr = csNotReceivedUnionQryStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters)
															.replace("<FROM_TABLES>", fromTables);
						
						csNotReceivedUnionQryStr = csNotReceivedUnionQryStr.replace(Constants.INTERACTION_CAMPAIGN_IDS_PH, csCampIDs)
													.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, (isLatestCampaigns ? Constants.INTERACTION_CAMPAIGN_CRID_PH :""));
													
						tempStr = ( (!chkOpensClicksFound && isCsNotReceivedUnionExist) ? csNotReceivedUnionQryStr : (tempStr +" UNION "+ csNotReceivedUnionQryStr) );//tempStr ++ csNotReceivedUnionQryStr ;
						
					}
					
					
					
					//tempStr = tempStr.replace("<FROM_TABLES>", fromTables);
					tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL") != null && !filterMap.get("GENERAL").isEmpty()	? " AND ("+ filterMap.get("GENERAL")+") " : "" );
					tempStr = tempStr.replace("<SALESCOND>", salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "");
					tempStr = tempStr.replace("<SALESSELECTFIELDS>", !salesSelectFields.isEmpty() ? Constants.DELIMETER_COMMA+salesSelectFields : "");
					tempStr = tempStr.replace("<SALESGROUPBY>", !salesSelectFields.isEmpty() ? " GROUP BY sal.doc_sid " : "");
				
					
				//replace zero year and first year
				Calendar currCal = Calendar.getInstance();
				String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
				
				String zeroYear  = "0000-"+currMonthDate;
				String firstYear = "0001-"+currMonthDate;
				
				tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
				tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
				
				
				
				finalStr=tempStr;
			}
			else {
				if(logger.isDebugEnabled()) logger.debug(">>> with CFs");
				String[] listIdArr = userIdStr.split(",");
				
				String tempStr="";
				for (String listId : listIdArr) {
					listId = listId.trim();
					if(!tempStr.equals("")) tempStr +=" UNION ";
					
					if(considerEmailStatus==true) 
						tempStr = tempStr + oneCFSqlFormatWithoutEmailStatus.replace("<LISTID>", listId);
					else
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
			}
			else {
				
				finalStr = generateHomePassedQry(queryString, qryType, mlBit);
				
			}
			
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::::", e);
			return null;
		}
		//return returnStr;
		
	} // generateQuery
	
	
	public static String generateHomePassedQry(String queryString, String queryType, long mlBit) {
		
		
		try {
			
			String finalStr = "";
			
			boolean chkSalesColumnsFound=false;
			boolean chkSKUColumnsFound=false;
			boolean chkOpensClicksFound = false;
			boolean chkAggregateFlagFound = false;
			boolean chkLoyaltyFlagFound = false;
			
			String fromTables = "";
			String fromTableFilters = "";
			
			/**
			 * 761
			 * ||sal.sales_date|date:is|03/05/2012<OR>sal.sales_date|date:is|07/05/2012<OR>
			 * sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
			 * ||sal.sales_date|date:isToday|now()<AND>
			 */
			
			
			
			
			HashMap<String, String> filterMap = new HashMap<String, String>();
			//String listIdStr=null;
			String userIdStr=null;
			String[] tokensArr = queryString.split("\\|\\|");
			


			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				 if(logger.isDebugEnabled()) logger.debug("TOKEN="+token);
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) return null;
					userIdStr = token.substring(token.indexOf(':')+1).trim();
					token = token.substring(0, token.indexOf(':')).trim();
					tokenFilterStr =(token.equalsIgnoreCase("all") || token.equalsIgnoreCase("AND")) ? "AND" : "OR";
					continue;
				}
				//************************************
				
				//now my token sal.sales_date|date:is|03/05/2012<OR>
				//sal.sales_date|date:is|07/05/2012<OR>
				//sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
				
				String[] tempTokenArr = token.split("<OR>");
				for (int tokenIndex = 0;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
				
					
					if(logger.isDebugEnabled()) logger.debug("tempToken :: "+tempToken);
					
					
					
					if(logger.isDebugEnabled()) logger.debug("table filter  is====>"+fromTableFilters);
					
					String fieldsArr[] = tempToken.split("\\|");
					if(fieldsArr.length < 4) {
						if(logger.isDebugEnabled()) logger.debug("Invalid token="+token);
						continue;
					}
					
					
					
					
					if(fieldsArr[1].startsWith("CF:")) {
						if(logger.isDebugEnabled()) logger.debug("in if");
						String[] cfTokenArr = fieldsArr[0].split(":");
						keyStr = cfTokenArr[1].trim();
						valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
					} 
					else {
						keyStr="GENERAL";
						
						if(valStr.length() > 0){
							valStr += " OR ";
						}
						
						String mapToken = fieldsArr[1].toLowerCase().trim();
						
						
							
							valStr += replaceToken(fieldsArr[1].trim(), fieldsArr);
						
					}
					
					
				} // for 
				
				if(valStr == null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr)) {
					valStr = " ("+filterMap.get(keyStr)+") " + tokenFilterStr +" ("+ valStr +") "; 
				}				
				if(logger.isDebugEnabled()) logger.debug("valStr ::"+valStr);
				filterMap.put(keyStr, valStr);
				
				if(logger.isDebugEnabled()) logger.debug("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				return null;
			}
			else if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				if(logger.isDebugEnabled()) logger.debug(">>> NO Cfs");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithoutEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
				String tempStr =null;
				
				if(queryType.equals(Constants.SEGMENT_ON_EMAIL)) {
					
					tempStr = generalSqlWithoutEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
					fromTables += " , homespassed h ";
					fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
				}
				else if(queryType.equals(Constants.SEGMENT_ON_MOBILE)) {
					
					tempStr = generalSql_mobile.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
					fromTables += " , homespassed h ";
					fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
				}
				
					
				//logger.debug("fromTables ::"+fromTableFilters);
				fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).replace("<SKUUSERID>", userIdStr).replace("<HPUSERID>", userIdStr);
				tempStr = tempStr.replace("<FROM_TABLES>", fromTables);
				tempStr = tempStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters);
					
				tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL"));
				
				
				//replace zero year and first year
				Calendar currCal = Calendar.getInstance();
				String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
				
				String zeroYear  = "0000-"+currMonthDate;
				String firstYear = "0001-"+currMonthDate;
				
				tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
				tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
				
				finalStr=tempStr;
			}
			
			return finalStr;
//		logger.debug("==="+filterMap);
//		logger.debug(listIdStr+"=FN="+finalStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::", e);
			return null;
		}
		}//generateHomePassedQry
	

	public static String generateSwitchConditionQuery(String queryString, boolean ignoreEmailStatus) {
		
		

		String returnStr=null;		
		try {
			HashMap<String, String> filterMap = new HashMap<String, String>();
			String listIdStr=null;
			String[] tokensArr = queryString.split("\\|\\|");
			
			String finalStr="";
			String valStr="";
			String token=null;
			String tokenFilterStr="";
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				// logger.debug("TOKEN="+token);
				//****** Get the first token *********
				if(i==0) {
					
					if(token.indexOf(':')==-1) return null;
					
					listIdStr = token.substring(token.indexOf(':')+1).trim();
					token = token.substring(0, token.indexOf(':')).trim();
					tokenFilterStr =(token.equalsIgnoreCase("all")) ? "AND" : "OR";
					continue;
				}
				//************************************
				//now my token sal.sales_date|date:is|03/05/2012<OR>
				//sal.sales_date|date:is|07/05/2012<OR>
				//sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
				
				String[] tempTokenArr = token.split("<OR>");
				for (int tokenIndex = 1;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
				
					
					if(logger.isDebugEnabled()) logger.debug("tempToken :: "+tempToken);
					String fieldsArr[] = tempToken.split("\\|");
					if(fieldsArr.length < 4) {
						if(logger.isDebugEnabled()) logger.debug("Invalid token="+token);
						continue;
					}
					
					String keyStr=null;
					
					
					if(fieldsArr[1].startsWith("CF:")) {
						if(logger.isDebugEnabled()) logger.debug("in if");
						String[] cfTokenArr = fieldsArr[0].split(":");
						keyStr = cfTokenArr[1].trim();
						valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
					} 
					else {
						if(logger.isDebugEnabled()) logger.debug("in else");
						keyStr="GENERAL";
						
						if(valStr.length() > 0){
							valStr += "OR";
						}
						
						
						valStr += "("+replaceToken(fieldsArr[1].trim(), fieldsArr)+")";
					}
					
					
					
					if(valStr==null) return null;
					//*********** Store Entry in Hash Map *******************
					
					if(filterMap.containsKey(keyStr)) {
						valStr = filterMap.get(keyStr) + tokenFilterStr + valStr; 
					}				
					if(logger.isDebugEnabled()) logger.debug("valStr ::"+valStr);
					filterMap.put(keyStr, valStr);
					
					
					
				}
				
				if(logger.isDebugEnabled()) logger.debug("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
			}// for i
			
			if(filterMap.size()==0) {
				return null;
			}
			else if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				if(logger.isDebugEnabled()) logger.debug(">>> NO Cfs");
				
				String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithoutEmailStatus.replace("<USERIDS>", listIdStr)
					: generalSql.replace("<USERIDS>", listIdStr);
				
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
					
					if(ignoreEmailStatus==true) 
						tempStr = tempStr + oneCFSqlFormatWithoutEmailStatus.replace("<LISTID>", listId);
					else
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
				
				finalStr = "SELECT * FROM ( " + tempStr + " GROUP BY email_id";
				
			} // else
			
//			logger.debug("==="+filterMap);
//			logger.debug(listIdStr+"=FN="+finalStr);
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::::", e);
		}
		return returnStr;
		
	
		
	}

	public static String prepareValueStr(String ValToBePrepare, String type) {
		
		String valStr = "";
		String[] strArr = ValToBePrepare.split(",");
		for (String valToken : strArr) {
			
			
			if(valStr.length() > 0) valStr += ",";
			
			if(type.equalsIgnoreCase("string")) {
				valStr += "'" + valToken.trim() + "'";
			}
			else if(type.equalsIgnoreCase("number") ){

				valStr += valToken.trim();
				
			}
			
			
		}
		
		
		
		
		return valStr;
		
	}
	
	 /**
	 * Converts to the SQL Query form
	 * @param fieldName
	 * @param tokenArr
	 * @return
	 */
	public static String replaceToken(String fieldName, String[] tokenArr, boolean fromEventTrigger) {
		String outStr=null;
		try {
			//String fitreStr = "<OR>";
			
			
			
			String token = tokenArr[1].toLowerCase().trim();
			String value = tokenArr[2].trim();
			
			if(token.contains("is in") || token.contains("is not in")) {
				//if(.equalsIgnoreCase("string"))
				value = prepareValueStr(value, token.split(":")[0]);
				
				
			}
			
			
			String mapStr = tokensHashMap.get(token);
			mapStr = mapStr.replace("<FIELD>", fieldName);
			
			if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast") ||
					token.contains("before_between") || token.contains("after_between") || token.contains("range_between") ) fieldName = "";
			// logger.debug(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

			if (mapStr==null) {
				if(logger.isDebugEnabled()) logger.debug("MAP Entry not found:"+tokenArr[1].toLowerCase().trim());
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
			logger.error("Exception ::::", e);
		}
		if(logger.isDebugEnabled()) logger.debug("outStr ::"+outStr);
		return outStr;
	}

	/**
	 * Converts to the SQL Query form
	 * @param fieldName
	 * @param tokenArr
	 * @return
	 */
	public static String replaceToken(String fieldName, String[] tokenArr) {
		String outStr=null;
		try {
			//String fitreStr = "<OR>";
				
			
			String token = tokenArr[2].toLowerCase().trim();
			String value = tokenArr[3].trim();
			
			
			
			if(token.contains("is in") || token.contains("is not in")) {
				//if(.equalsIgnoreCase("string"))
				value = prepareValueStr(value, token.split(":")[0]);
				
				
			}
			
			
			
			if(token.contains("_") && token.contains("withinlast") ){
				token = token.substring(0, token.indexOf("_"));
			}
			
			String mapStr = tokensHashMap.get(token);
			mapStr = mapStr.replace("<FIELD>", fieldName);
			
			
			if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast") ||
					token.contains("before_between") || token.contains("after_between") || token.contains("range_between") ) fieldName = "";
			// logger.debug(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

			if (mapStr==null) {
				if(logger.isDebugEnabled()) logger.debug("MAP Entry not found:"+tokenArr[1].toLowerCase().trim());
				return null;
			}
			
			
			String tempStr = mapStr.replace("<VAL1>", value );
			String opensClicksSubCond = "";
			
			/*if(fieldName.trim().startsWith("cs.")) {
				if(tokenArr.length>5) {
					tempStr = tempStr.replace("<VAL2>", tokenArr[4].trim());
					opensClicksSubCond = " cs.campaign_id="+tokenArr[5].trim()+" AND ";
					
				}else if(tokenArr.length>4) {
					
					opensClicksSubCond = " cs.campaign_id="+tokenArr[4].trim()+" AND ";
					
				}
				
				
			}*/
			
			
			if(tokenArr.length>4 && tempStr.contains("<VAL2>")) {
					
					tempStr = tempStr.replace("<VAL2>", tokenArr[4].trim());
				
			}
			
			
			outStr=" ("+opensClicksSubCond + fieldName+ tempStr +") ";
			
			outStr =  outStr.trim();
			if(outStr.equalsIgnoreCase("(c.gender = 'Male')"))
				{
					outStr = "c.gender='male' || c.gender='m'";
				}
			else{	
			if(outStr.equalsIgnoreCase("(c.gender = 'Female')"))
					outStr = "c.gender='female' || c.gender='f'";
			}
			logger.info("updated for gender outStr ::"+outStr);
			
		} catch (Exception e) {
			logger.error("Exception ::::", e);
		}
		if(logger.isDebugEnabled()) logger.debug("outStr ::"+outStr);
		return outStr;
	}
	
	/*public static void main(String[] args) {
		String segmentStr = "Any:1||email_id|STRING:contains|@";
		logger.debug(generateListSegmentQuery(segmentStr));
	}*/

	public static String prepareDateSubQuery(String fieldName, String[] tokenArr, String userIdStr , String qryType,long mlBit) {
		

		String fromTables = "";
		String fromTableFilters = "";
		String subQry = "c.cid NOT IN(<SUBQRY>)";
		String tempStr =null;
		
		
		if(qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
			
			tempStr = tot_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			
		}else if(qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
			
			tempStr = tot_subQuery_mobile.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");;
		}
		
		
		if((fieldName.toLowerCase().trim().startsWith("sku.") || fieldName.toLowerCase().trim().startsWith("date(sku.") )) {
			
			
				fromTables += "  , retail_pro_sales sal , retail_pro_sku sku ";
				fromTableFilters  += " AND sal.user_id in(<SALESUSERID>) AND sku.user_id in(<SKUUSERID>) AND  sal.cid IS NOT NULL AND c.cid=sal.cid AND sku.sku_id=sal.inventory_id"; 
			
		} // if
		
		else if(fieldName.toLowerCase().trim().startsWith("aggr.") || fieldName.toLowerCase().trim().startsWith("date(aggr.") ) {
			
			fromTables += " , sales_aggregate_data aggr";
			fromTableFilters  += " AND aggr.user_id in(<SALESAGGRUSERID>) AND c.cid=aggr.cid"; 
			
		}//if
		else if(( fieldName.toLowerCase().trim().startsWith("loyalty.") || fieldName.toLowerCase().trim().startsWith("date(loyalty.") )) {
			
			fromTables += " , contacts_loyalty loyalty";
			fromTableFilters  += " AND loyalty.contact_id=c.cid"; 
			
		}//if
		
		//logger.debug("found sales flag........."+tempToken.toLowerCase().trim().startsWith("Date(sal."));
		else if(	(fieldName.toLowerCase().trim().startsWith("sal.") || fieldName.toLowerCase().trim().startsWith("date(sal."))) {
			
			fromTables +=  " , retail_pro_sales sal ";
			fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";  
		} // if
		
		
	/*	else if(( fieldName.toLowerCase().trim().startsWith("cs.") || fieldName.toLowerCase().trim().startsWith("date(cs."))) {
			
			fromTables += " , campaign_sent cs ";
			fromTableFilters  += " AND c.cid=cs.contact_id "; 
			
		}//if
*/		
		else if(fieldName.toLowerCase().trim().startsWith("h.") || fieldName.toLowerCase().trim().startsWith("date(h.")) {
			
			fromTables += " , homespassed h ";
			fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
			
		}//if
		
		fromTables = fromTables.replace("<SALESUSERID>", userIdStr).
				replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
				.replace("<HPUSERID>", userIdStr);

		fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).
							replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr);
		tempStr = tempStr.replace("<FROM_TABLES>", fromTables);
		tempStr = tempStr.replace("<FROM_TABLE_FILTERS>", fromTableFilters);
		
		
		String token = tokenArr[2].toLowerCase().trim();
		if(token.contains("_") && token.contains("withinlast") ){
			token = token.substring(0, token.indexOf("_"));
		}
		String mapStr = tokensHashMap.get(token);
		mapStr = mapStr.replace("<FIELD>", fieldName);
		
		//if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast")) fieldName = "";
		// logger.debug(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

		if (mapStr==null) {
			if(logger.isDebugEnabled()) logger.debug("MAP Entry not found:"+token);
			return null;
		}
		
		String value = tokenArr[3].trim();
		
		
		String valueStr = mapStr.replace("<VAL1>", value );
		
		tempStr = tempStr.replace("<COND>", valueStr );
		
			
		
		
		
		subQry = subQry.replace("<SUBQRY>", tempStr);
		
		
		
		
		return subQry;
		
		
	}
	
public static String getCamapignIdsFroFirstToken(String rule) {
		
		String[] tokensArr = rule.split("\\|\\|");
		
		String token=tokensArr[0];
		String tokenFilterStr="";
		String csCampIDs = null;
		
			
				
		String[] firstTokenArr = token.split(":");
		if(firstTokenArr.length > 2) {
			
			csCampIDs = firstTokenArr[2];
			//if(firstTokenArr.length == 4) crIds = firstTokenArr[3];
			return csCampIDs;
		}//if
				
			
		return null;	
		
		
	}
public static boolean CheckForIsLatestCamapignIdsFlag(String rule) {
		
		String[] tokensArr = rule.split("\\|\\|");
		
		String token=tokensArr[0];
		String tokenFilterStr="";
		String csCampIDs = null;
		
			
				
		String[] firstTokenArr = token.split(":");
		if(firstTokenArr.length > 2) {
			
			//csCampIDs = firstTokenArr[2];
			if(firstTokenArr.length == 4 && firstTokenArr[3].equals(Constants.INTERACTION_CAMPAIGN_CRID_PH) ) return true;
		}//if
				
			
		return false;	
		
		
	}
	
	
	
}
