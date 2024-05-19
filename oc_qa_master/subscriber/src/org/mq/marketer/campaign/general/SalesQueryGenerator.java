package org.mq.marketer.campaign.general;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.controller.contacts.SegmentEnum;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class SalesQueryGenerator {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//private static String generalSql = "SELECT * FROM contacts WHERE list_id IN (<LISTIDS>) AND email_status='Active' AND (<COND>) GROUP BY email_id";
	//private static String generalSqlWithEmailStatus = "SELECT list_id, email_id, first_name, last_name, created_date, purged, email_status, last_status_change, last_mail_date, address_one, address_two, city, state, country, pin, phone, optin, subscription_type, optin_status FROM contacts WHERE list_id IN (<LISTIDS>) AND (<COND>) GROUP BY email_id";
	
	private static String generalSql = 
		
		"SELECT * FROM contacts c , contacts_mlists cm " +
		"<FROM_TABLES>" +
		"" +
		"" +
		" WHERE c.cid = cm.cid and cm.list_id IN (<LISTIDS>) AND c.email_status='Active' " +
		"" +
		"<FROM_TABLE_FILTERS>" +
		"AND (<COND>) GROUP BY email_id";
	
	
	
	private static String qry_HomePassed = " SELECT h.* FROM homespassed h WHERE h.user_id IN(<USERIDS>) AND (<COND>)  ";
	
	
	
	private static String tot_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
						" <FROM_TABLE_FILTERS>  AND (<COND>) ";
	
	private static String email_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
						" <FROM_TABLE_FILTERS>  AND (c.email_id is not null AND c.email_id!='') AND  (<COND>)  ";
	
	private static String mobile_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
						" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='') AND (<COND>)   ";
	
	
	private static String general_email_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
										" <FROM_TABLE_FILTERS>  AND (c.email_id is not null AND c.email_id!='' AND c.email_status='Active') AND  (<COND>)  ";
	
private static String general_mobile_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) AND (<COND>)   ";


private static String not_received_total =  OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
											" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
											"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
											+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
											"  WHERE o.contact_id is null  "+
											"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
											" <FROM_TABLE_FILTERS>  <COND> " +
											" <GROUPBYSTR> <HAVINGSTR>";

private static String not_received_totalCount =  " SELECT DISTINCT c.cid "+
												" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
												"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
												+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
												"  WHERE o.contact_id is null  "+
												"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
												" <FROM_TABLE_FILTERS> <COND>  " +
												"  <GROUPBYSTR> <HAVINGSTR>";



	private static String not_received_email =  OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
												" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
												"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
												+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
												"  WHERE o.contact_id is null  "+
												"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
												" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') <COND> " +
												" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	
	private static String not_received_emailCount =  " SELECT DISTINCT c.cid "+
													" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
													"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
													+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
													"  WHERE o.contact_id is null  "+
													"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
													" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') <COND>  " +
													" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	
	private static String not_received_email_status =  OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
														" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
														"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
														+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
														"  WHERE o.contact_id is null  "+
														"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
														" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active' <COND> " +
														" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";

private static String not_received_email_Count_status =  " SELECT DISTINCT c.cid "+
														" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
														"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
														+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
														"  WHERE o.contact_id is null  "+
														"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
														" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active' <COND>  " +
														" GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	

private static String not_received_mobile =  OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
											" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
											"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
											+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
											"  WHERE o.contact_id is null  "+
											"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
											" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' )  <COND> " +
											" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";

private static String not_received_mobileCount =  " SELECT DISTINCT c.cid "+
												" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
												"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
												+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
												"  WHERE o.contact_id is null  "+
												"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
												" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='') <COND>  " +
												" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";

private static String not_received_mobile_status =  OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
												" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
												"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
												+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
												"  WHERE o.contact_id is null  "+
												"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
												" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN>) <COND> " +
												" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";

private static String not_received_mobile_Count_status =  " SELECT DISTINCT c.cid "+
													" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
													"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
													+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
													"  WHERE o.contact_id is null  "+
													"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
													" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN>) <COND>  " +
													" GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";
	//TODO sales date condition need to be think
	/*private static String aggr_query_template = " ( SELECT cid, SUM((sales_price*quantity)+tax) as totAmt FROM retail_pro_sales"
												+ " WHERE user_id IN(<USERIDS>) AND cid IS NOT NULL <SALESDATECOND> GROUP BY doc_sid) AS totSal ";*/

/*private static String aggr_query_template = " ( SELECT sal.*, SUM((sal.sales_price*sal.quantity)+sal.tax) as totAmt, max((sal.sales_price*sal.quantity)+sal.tax) as maxAmt, avg((sal.sales_price*sal.quantity)+sal.tax) as avgAmt, count(sal.doc_sid) as itemCnt FROM retail_pro_sales sal <SALESFROMTABLES> "
											+ " WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL <SALESFROMTABLESFILTERS> <SALESCOND>  GROUP BY sal.doc_sid) AS totSal ";
*/
//APP-4227
//private static String department_and_item_category_sub_query = " (DATEDIFF(NOW(), DATE(sal.sales_date)) >= 0 AND DATEDIFF(NOW(), DATE(sal.sales_date)) <= 365) AND sal.cid NOT IN (SELECT DISTINCT sal.cid FROM retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>, retail_pro_sku sku WHERE sal.user_id IN(<SALESUSERID>) AND sal.cid IS NOT NULL AND sku.user_id in(<SALESUSERID>)"
//		+ " AND sku.sku_id = sal.inventory_id <SALES_COND>  AND ((DATEDIFF(now(),Date(sal.sales_date) ) >= 0 AND DATEDIFF(now(),Date(sal.sales_date) ) <= 365)) AND <DEPT_ITEM_CAT_ONLYLIKE>)";
private static String department_and_item_category_sub_query = " sal.cid NOT IN (SELECT DISTINCT sal.cid FROM retail_pro_sales sal, retail_pro_sku sku WHERE sal.user_id IN(<SALESUSERID>) AND sal.cid IS NOT NULL AND sku.user_id in(<SALESUSERID>)"
		+ " AND sku.sku_id = sal.inventory_id <SALES_COND> AND (DATEDIFF(now(),Date(sal.sales_date) ) >= 0 AND DATEDIFF(now(),Date(sal.sales_date) ) <= 365) AND <DEPT_ITEM_CAT_ONLYLIKE>)";

//APP-4201
//private static String last_purchase_qry_template = "( SELECT sal.* FROM retail_pro_sales sal WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL GROUP BY sal.sales_id HAVING <RPS_LAST_PURCHASE_DATE_COND>  ) sal ";
private static String last_purchase_qry_template = "( SELECT sal.* FROM retail_pro_sales sal, ( SELECT sal.cid, MAX(date(sales_date)) last_purchase_date  FROM retail_pro_sales sal WHERE sal.user_id IN(<SALESUSERID>)"
		+ " AND  sal.cid IS NOT NULL GROUP BY sal.cid HAVING <RPS_LAST_PURCHASE_DATE_COND>  ) maxsal WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL AND sal.cid = maxsal.cid AND date(sal.sales_date) =  maxsal.last_purchase_date ) sal ";
private static String aggr_query_template = " ( SELECT sal.* <SALESSELECTFIELDS> FROM retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP> <SALESFROMTABLES> "
		+ " WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL <SALESFROMTABLESFILTERS> <SALESCOND> <SALESGROUPBY> ) AS totSal ";

private static String sales_not_purchase_qry_template = "  sal.cid NOT IN(SELECT sal.cid FROM retail_pro_sales sal "
		+ " WHERE sal.user_id IN(<SALESUSERID>) AND  sal.cid IS NOT NULL AND <SALESCOND>)";

private static String lty_not_purchase_qry_template = "  ltytrxch.contact_id NOT IN(SELECT distinct ltytrxch.contact_id FROM loyalty_transaction_child ltytrxch"
		+ " WHERE ltytrxch.user_id IN(<SALESUSERID>) AND ltytrxch.contact_id IS NOT NULL AND ltytrxch.transaction_type IN('Issuance','Redemption','Return') AND <SALESCOND>)";

private static String loyaltyaggr_query_template = " ( SELECT ltytrxch.* <LTYTRXCHSELECTFIELDS> FROM loyalty_transaction_child ltytrxch  "
		+ " WHERE ltytrxch.user_id IN(<LTYTRXCHUSERID>) AND ltytrxch.loyalty_id IS NOT NULL "
		//+ " AND ltytrxch.contact_id IS NOT NULL "
		+ "<LTYTRXCHCOND> <LTYTRXCHGROUPBY> "
		+ " <HAVING> ) AS totloyal ";

private static String loyaltyvalucode_query_template = " ( SELECT ltybalance.* <LTYTRXCHSELECTFIELDS> FROM loyalty_balance ltybalance  "
		+ " WHERE ltybalance.user_id IN(<LTYTRXCHUSERID>) AND ltybalance.loyalty_id IS NOT NULL AND ltybalance.value_code IN( <Reward> )"
		//+ " AND ltytrxch.contact_id IS NOT NULL "
		+ "<LTYTRXCHCOND> <LTYBALGROUPBY> "
		+ " <HAVING> ) AS totloyalbal ";

private static String promotion_qury_template="(SELECT  * from coupon_codes cd WHERE <COUPONID>) as cd ,(select * from coupons cp  )as cp ";

private static String promotion_have_not_issued_qury_template = "(SELECT contact_id  from coupon_codes cd where <COUPONID> and cd.contact_id is not null) ";
	
private static String generalSqlWithEmailStatusCountForHavenotIsuued ="SELECT DISTINCT c.cid  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 <FROM_TABLE_FILTERS> <COND> AND c.cid not in "+promotion_have_not_issued_qury_template+"  <GROUPBYSTR> <HAVINGSTR>";

private static String email_generalSqlWithEmailStatusCountForHavenotIsuued ="SELECT DISTINCT c.cid  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0  <FROM_TABLE_FILTERS> <COND>  AND c.cid not in  "+promotion_have_not_issued_qury_template
		    + "  AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active'  GROUP BY c.email_id <GROUPBYSTR> <HAVINGSTR>";

private static String mobile_generalSqlWithEmailStatusCountForHavenotIsuued ="SELECT DISTINCT c.cid  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 <FROM_TABLE_FILTERS> <COND>  AND c.cid not in "+promotion_have_not_issued_qury_template
		    + " AND (c.mobile_phone is not null AND c.mobile_phone !='') GROUP BY c.mobile_phone <GROUPBYSTR> <HAVINGSTR>";

private static String generalSqlWithEmailStatusForHavenotIsuued = OCConstants.segmentQryPrefiX+"  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 <FROM_TABLE_FILTERS> <COND>  AND c.cid not in "+promotion_have_not_issued_qury_template+"  <GROUPBYSTR> <HAVINGSTR>";

private static String email_generalSqlWithEmailStatusForHavenotIsuued = OCConstants.segmentQryPrefiX+"  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 <FROM_TABLE_FILTERS> <COND>  AND c.cid not in  "+promotion_have_not_issued_qury_template
		    + "  AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active'  GROUP BY c.email_id <GROUPBYSTR> <HAVINGSTR>";

private static String mobile_generalSqlWithEmailStatusForHavenotIsuued = OCConstants.segmentQryPrefiX+"  FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0 <FROM_TABLE_FILTERS> <COND>  AND c.cid not in "+promotion_have_not_issued_qury_template
		    + " AND (c.mobile_phone is not null AND c.mobile_phone !='') GROUP BY c.mobile_phone <GROUPBYSTR> <HAVINGSTR>";
 
/*private static String Old_generalSqlWithEmailStatus =
		
		" SELECT DISTINCT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits   " +
		" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active'  <COND> GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	*/

		private static String Old_generalSqlWithEmailStatus =
		
		OCConstants.segmentQryPrefiX+//FROM contacts WHERE cid IN(SELECT DISTINCT c.cid" +
		" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active'  <COND> GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR> ";


		private static String Old_generalSqlWithEmailStatusCount =
		
		" SELECT DISTINCT c.cid" +
		" FROM contacts c " +
		/*		" , retail_pro_sales sal, retail_pro_sku sku WHERE c.list_id IN (<LISTIDS>) " +
		" AND c.external_id=sal.customer_id AND (<COND>) GROUP BY c.email_id";
		*/		
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') AND c.email_status='Active'  <COND> GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";

		
	/*private static String generalSqlWithEmailStatus =
		
		" SELECT DISTINCT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits   " +
		" FROM contacts c " +
	" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS>   <COND> <GROUPBYSTR> <HAVINGSTR>";*/
		
		
		private static String generalSqlWithEmailStatus =
				
				OCConstants.segmentQryPrefiX+//FROM contacts WHERE cid IN(SELECT DISTINCT c.cid " +
				" FROM contacts c " +
			" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
			" <FROM_TABLE_FILTERS>   <COND> <GROUPBYSTR> <HAVINGSTR>";
	
	private static String generalSqlWithEmailStatusCount =
			
			" SELECT DISTINCT c.cid " +
			" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS>   <COND> <GROUPBYSTR> <HAVINGSTR>";
	/*	
	private static String email_generalSqlWithEmailStatus =
		
		" SELECT DISTINCT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits   "+
		" FROM contacts c "+
	" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') <COND>  GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";*/
	
	
	private static String email_generalSqlWithEmailStatus =
		
			OCConstants.segmentQryPrefiX+//FROM contacts WHERE cid IN(SELECT DISTINCT c.cid"+
		" FROM contacts c "+
	" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') <COND>  GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
	
	
	private static String email_generalSqlWithEmailStatusCount =
			
			" SELECT DISTINCT c.cid"+
			" FROM contacts c "+
		" <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.email_id is not null AND c.email_id!='') <COND>  GROUP BY c.email_id <SALESFIELDS> <HAVINGSTR>";
		
		
	/*private static String mobile_generalSqlWithEmailStatus =
		
		" SELECT DISTINCT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits   "+
		" FROM contacts c " +
	" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='') <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";*/
	
	private static String mobile_generalSqlWithEmailStatus =
			
			OCConstants.segmentQryPrefiX+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid"+
			" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='') <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>";
		
	private static String mobile_generalSqlWithEmailStatusCount =
			
			" SELECT DISTINCT c.cid FROM(SELECT  c.cid"+
			" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.mobile_phone is not null AND c.mobile_phone !='') <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>) as c";
		
/*private static String mobile_generalSqlWithMobileStatus =
		
		" SELECT DISTINCT c.cid,  c.email_id, c.first_name, " +
		" c.last_name, c.created_date, c.purged, c.email_status," +
		" c.last_status_change, c.last_mail_date, c.address_one, " +
		" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
		" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
		" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
		" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
		" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in,c.mlbits   "+
		" FROM contacts c " +
	" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
	" <FROM_TABLE_FILTERS> AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR> ";*/
						
	private static String mobile_generalSqlWithMobileStatus =
			
			OCConstants.segmentQryPrefiX+//FROM contacts WHERE cid IN(SELECT DISTINCT c.cid"+
			" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR> ";
		
	
	private static String mobile_generalSqlWithMobileStatusCount =
			
			" SELECT DISTINCT c.cid FROM ( SELECT c.cid "+
			" FROM contacts c " +
		" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND (c.mobile_phone IS NOT NULL AND c.mobile_phone !='' AND c.mobile_status='Active'  <MOBILEOPTIN> ) <COND>  GROUP BY c.mobile_phone <SALESFIELDS> <HAVINGSTR>) as c ";
		
	
	private static String cfExistSql=" (cid IN (SELECT contact_id FROM customfield_data WHERE (<CFCOND>) ))";
	
	private static String oneCFSqlFormat = "SELECT * FROM contacts c, contacts_mlists cm WHERE c.cid=cm.cid and cm.list_id = <LISTID> AND c.email_status='Active' "; 
	private static String oneCFSqlFormatWithoutEmailStatus = "SELECT  c.email_id, c.first_name, c.last_name, c.created_date, c.purged, c.email_status, c.last_status_change, c.last_mail_date, c.address_one, c.address_two, c.city, c.state, c.country, c.zip, c.mobile_phone, c.optin, c.subscription_type, c.optin_status FROM contacts c, contacts_mlists cm WHERE c.cid=cm.cid and cm.list_id = <LISTID> ";	
//private static String oneCFSqlFormat = "SELECT * FROM contacts WHERE list_id = <LISTID> AND email_status='Active' "; 
	//private static String oneCFSqlFormatWithoutEmailStatus = "SELECT list_id, email_id, first_name, last_name, created_date, purged, email_status, last_status_change, last_mail_date, address_one, address_two, city, state, country, pin, phone, optin, subscription_type, optin_status FROM contacts WHERE list_id = <LISTID> ";
	
	public static HashMap<String, String> tokensHashMap=new HashMap<String, String>();
	public static HashMap<String, String> salesFieldsMap=new HashMap<String, String>();
	public static HashMap<String, String> ltyFieldsMap=new HashMap<String, String>();
	public static HashMap<String, String> ltyConditionMap=new HashMap<String, String>();
	
	
		private static String notification_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
					" <FROM_TABLE_FILTERS> AND c.push_notification = 1 AND ( c.instance_id is not null AND c.instance_id!='') AND (c.device_Type IS NOT NULL AND c.device_Type !='') AND (<COND>) ";
					
		private static String general_notification_subQuery = " SELECT DISTINCT c.cid FROM contacts c <FROM_TABLES> WHERE c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
		" <FROM_TABLE_FILTERS> AND c.push_notification = 1 AND (c.instance_id is not null AND c.instance_id !='' ) AND (c.device_Type IS NOT NULL AND c.device_Type !='') AND (<COND>)   ";

		private static String not_received_notification =  "  SELECT DISTINCT c.* "+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid "+
				" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
				"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")" 
				+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
				"  WHERE o.contact_id is null  "+
				"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
				" <FROM_TABLE_FILTERS> AND push_notification = 1  AND (c.instance_id is not null AND c.instance_id !='')  AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND> " +
				" GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR>";

		private static String not_received_notificationCount =  " SELECT DISTINCT c.cid "+
														"  FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
														"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
														+  Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
														"  WHERE o.contact_id is null  "+
														"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
														" <FROM_TABLE_FILTERS> AND push_notification = 1  AND (c.instance_id is not null AND c.instance_id !='') AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND>  " +
														" GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR>";

		private static String not_received_notification_Count_status =  " SELECT DISTINCT c.cid "+
															" FROM (contacts c <FROM_TABLES>) LEFT OUTER JOIN (SELECT contact_id  " +
															"  FROM campaign_sent WHERE campaign_id IN(" +Constants.INTERACTION_CAMPAIGN_IDS_PH +")"
															+Constants.INTERACTION_CAMPAIGN_CRID_PH +" GROUP BY email_id , campaign_id   )o on o.contact_id=c.cid " +
															"  WHERE o.contact_id is null  "+
															"  AND c.user_id=<USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
															" <FROM_TABLE_FILTERS> AND push_notification = 1  AND (c.instance_id is not null AND c.instance_id !='') AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND>  " +
															" GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR>";

			private static String notification_generalSqlWithNotificationStatus =
					
					"SELECT DISTINCT c.* "+// FROM contacts WHERE cid IN(SELECT DISTINCT c.cid"+
					" FROM contacts c " +
				" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
				" <FROM_TABLE_FILTERS> AND push_notification=1 (c.instance_id is not null AND c.instance_id !='') AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND>  GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR>";
				
				
				
		private static String notification_generalSqlWithStatusCount =
					
					" SELECT DISTINCT c.cid"+
					" FROM contacts c " +
				" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
				" <FROM_TABLE_FILTERS> AND push_notification = 1 AND (c.instance_id is not null AND c.instance_id !='') AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND>  GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR>";
				
		private static String notification_generalSqlWithNotificationStatusCount =
					
					" SELECT DISTINCT c.cid"+
					" FROM contacts c " +
				" <FROM_TABLES> WHERE c.user_id = <USERIDS> AND  (c.mlbits&<MLBITS> )>0" +
				" <FROM_TABLE_FILTERS> AND push_notification = 1 AND (c.instance_id IS NOT NULL AND c.instance_id !='' ) AND (c.device_Type IS NOT NULL AND c.device_Type !='') <COND>  GROUP BY c.instance_id <SALESFIELDS> <HAVINGSTR> ";
				
	
	
	public static void main(String[] args) {
		String qry = "all:761||sal.sales_date|date:isToday|now()<OR>sal.sales_date|date:is|07/05/2012<OR>sal.sales_date|date:between|03/05/2012|07/05/2012<OR>||sal.sales_date|date:onOrAfter|06/05/2012";
		
	
		
		qry = "all:761||CF:761:cust_1:dob|date:is|06/05/2012||sal.sales_date|date:onOrAfter|06/05/2012||cs.opens|number:=|100";
		
		//logger.debug("Qry:"+ generateListSegmentQuery(qry,true, null));
	}
	
	static {
		salesFieldsMap.put("aggr_tot", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr.tot_purchase_amt", "aggr.tot_purchase_amt as totAmt");
		salesFieldsMap.put("aggr.avg_purchase_amt", "aggr.avg_purchase_amt as avgAmt");
		salesFieldsMap.put("aggr.max_purchase_amt", "aggr.max_purchase_amt as maxAmt");
		salesFieldsMap.put("aggr.tot_reciept_count", "aggr.tot_reciept_count as totvisits");
		//salesFieldsMap.put("aggr.avg_in_year", "(aggr.tot_purchase_amt/year(now())-year(c.create_date)) as avg_in_an_yr");
		salesFieldsMap.put("aggr_avg", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr_max", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("aggr_count", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("new_tot", "SUM((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as totAmt");
		salesFieldsMap.put("new_avg", "avg((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as avgAmt");
		salesFieldsMap.put("new_max", "max((sal.sales_price*sal.quantity)+sal.tax-(IF(sal.discount is null,0,sal.discount))) as maxAmt");
		salesFieldsMap.put("aggr_boughtqty", "SUM(if(sal.quantity > 0, sal.quantity, 0)) as boughtQty");
		salesFieldsMap.put("aggr_returnedqty", "SUM(if(sal.quantity < 0, sal.quantity, 0)) as returnedQty");
		salesFieldsMap.put("new_count", "count(sal.doc_sid) as itemCnt");
		
		salesFieldsMap.put("aggr_tot_withouttax", "SUM((sal.sales_price*sal.quantity)-(IF(sal.discount is null,0,sal.discount))) as totAmtWithOutTax");
		salesFieldsMap.put("aggr_avg_withouttax", "SUM((sal.sales_price*sal.quantity)-(IF(sal.discount is null,0,sal.discount))) as totAmtWithOutTax");
		salesFieldsMap.put("aggr_max_withouttax", "SUM((sal.sales_price*sal.quantity)-(IF(sal.discount is null,0,sal.discount))) as totAmtWithOutTax");
		salesFieldsMap.put("aggr_count_withouttax", "SUM((sal.sales_price*sal.quantity)-(IF(sal.discount is null,0,sal.discount))) as totAmtWithOutTax");
		salesFieldsMap.put("new_tot_withouttax", "SUM((sal.sales_price*sal.quantity)-(IF(sal.discount is null,0,sal.discount))) as totAmtWithOutTax");
		
		ltyFieldsMap.put("aggrlty_count", "SUM(if(ltytrxch.transaction_type in('Issuance','Redemption','Return') , 1, 0)) as totaltrx");
		ltyFieldsMap.put("aggrlty_totrewardcount", "SUM(if(ltytrxch.transaction_type in('Issuance','Redemption','Return') AND ltytrxch.earn_type IN( <Reward>), 1, 0)) as totalrewardtrx");
		ltyFieldsMap.put("aggrlty_totissued", "SUM(if(ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Purchase' , 1, 0)) as issued");
		ltyFieldsMap.put("aggrlty_totissued_gift", "SUM(if(ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Gift' , 1, 0)) as giftIssued");
		ltyFieldsMap.put("aggrlty_totredeemed", "SUM(if(ltytrxch.transaction_type ='Redemption' , 1, 0)) as redeemed");
		ltyFieldsMap.put("aggrlty_totreturned", "SUM(if(ltytrxch.transaction_type ='Return' , 1, 0)) as returntrx");

		ltyFieldsMap.put("aggrlty_totamtissued", "SUM(ltytrxch.entered_amount) as totIssuedAmt");
		ltyFieldsMap.put("aggrlty_totamtissued_gift", "SUM(ltytrxch.entered_amount) as totIssuedGiftAmt");
		ltyFieldsMap.put("aggrlty_totamtredeemed", "SUM(ltytrxch.entered_amount) as totredeemedAmt");
		ltyFieldsMap.put("aggrlty_totamtreturned", "SUM(ltytrxch.entered_amount) as totreturnedAmt");
		
		ltyFieldsMap.put("aggrlty_totrewardissued", "SUM(if(ltytrxch.transaction_type ='Issuance' AND ltytrxch.earn_type IN(<Reward>) , 1, 0)) as rewardissued");
		ltyFieldsMap.put("aggrlty_totrewardredeemed", "SUM(ltytrxch.entered_amount) as totredeemedReward");
		ltyFieldsMap.put("aggrlty_totrewardreturned", "SUM(if(ltytrxch.transaction_type ='Return' AND ltytrxch.entered_amount_type='ReturnReversal' , 1, 0)) as rewardreturntrx");
		
		ltyFieldsMap.put("aggrlty_rewardtot", "SUM(ltybalance.total_earn_balance) as valuecodetotal");
		ltyFieldsMap.put("aggrlty_rewardbalance", "SUM(ltybalance.balance) as valuecodebalance");
		ltyFieldsMap.put("aggrlty_totrewardsredeemed", "SUM(if(ltytrxch.transaction_type ='Redemption' AND ltytrxch.reward_difference is not NULL AND value_code is not NULL , 1, 0)) as rewardredeemed");
		
		//ltyConditionMap.put("aggrlty_count", "ltytrxch.transaction_type !='Enrollment'");
		ltyConditionMap.put("aggrlty_totamtissued", "ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Purchase'");
		ltyConditionMap.put("aggrlty_totamtissued_gift", "ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Gift'");
		ltyConditionMap.put("aggrlty_totamtredeemed", "ltytrxch.transaction_type ='Redemption' AND ltytrxch.entered_amount_type='AmountRedeem'");
		ltyConditionMap.put("aggrlty_totamtreturned", "ltytrxch.transaction_type ='Return' AND ltytrxch.entered_amount_type='IssuanceReversal'");
		ltyConditionMap.put("aggrlty_totrewardredeemed", "ltytrxch.transaction_type ='Redemption' AND ltytrxch.entered_amount_type='PointsRedeem'");
		//ltyConditionMap.put("aggrlty_totissued", "ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Purchase'");
		//ltyConditionMap.put("aggrlty_totissued_gift", "ltytrxch.transaction_type ='Issuance' AND ltytrxch.entered_amount_type='Gift'");
		//ltyConditionMap.put("aggrlty_totredeemed", "ltytrxch.transaction_type ='Redemption'");
		//ltyConditionMap.put("aggrlty_totreturned", "ltytrxch.transaction_type ='Return'");
		
		//ltyConditionMap.put("aggrlty_rewardbalance", "ltybalance.value_code IN( <Reward>)");
		ltyConditionMap.put("date:notafter","ltytrxch.transaction_type IN('Issuance','Redemption','Return')");
		ltyConditionMap.put("date:notbefore","ltytrxch.transaction_type IN('Issuance','Redemption','Return')");
		
		tokensHashMap.put("string:is", " = '<VAL1>'");
		tokensHashMap.put("string:equal", " = '<VAL1>'");
		tokensHashMap.put("string:equals", " = '<VAL1>'");
		tokensHashMap.put("string:equal to", " = '<VAL1>'");
		tokensHashMap.put("string:is equal", " = '<VAL1>'");
		tokensHashMap.put("string:is equals", " = '<VAL1>'");
		tokensHashMap.put("string:is equal to", " = '<VAL1>'");
		tokensHashMap.put("string:is equals to", " = '<VAL1>'");
		tokensHashMap.put("string:is in", " in (<VAL1>)");
		tokensHashMap.put("string:only like", " <VAL1> "); //APP-4227
		tokensHashMap.put("string:is not in", " not in (<VAL1>)");
		tokensHashMap.put("string:is not", " != '<VAL1>'");

		tokensHashMap.put("string:not equal", " != '<VAL1>'");//added for app-3385
		tokensHashMap.put("string:not equals", " != '<VAL1>'");
		tokensHashMap.put("string:not equal to", " != '<VAL1>'");
		tokensHashMap.put("string:is not equal", " != '<VAL1>'");

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
		tokensHashMap.put("string:is value", " <VAL1>");
		tokensHashMap.put("string:is in active", " (<VAL1>)");
		tokensHashMap.put("string:is in redeem", " in (<VAL1>)");
		tokensHashMap.put("string:is not in active", " not in (<VAL1>)");
		tokensHashMap.put("string:is not in redeem", " in (<VAL1>)");
		tokensHashMap.put("string:is issued", " in ('<VAL1>')");
		tokensHashMap.put("string:is redeemed", " in ('<VAL1>')");
		tokensHashMap.put("string:is expired", " in ('<VAL1>')");
		
		
		
		/*tokensHashMap.put("number:is", " = <VAL1>");
		tokensHashMap.put("number:gt", " > <VAL1>");
		tokensHashMap.put("number:lt", " < <VAL1>");
		tokensHashMap.put("number:ge", " >= <VAL1>");
		tokensHashMap.put("number:le", " <= <VAL1>");
		tokensHashMap.put("number:ne", " != <VAL1>");
		tokensHashMap.put("number:between", " between <VAL1> and <VAL2>");*/
		
		tokensHashMap.put("number:=", " = '<VAL1>'");
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
		tokensHashMap.put("number:date_diff_equal_to", " DATEDIFF(NOW(), MAX(<FIELD>)) = <VAL1>  "); //APP-4201

		tokensHashMap.put("date:is", " = '<VAL1>'");
		tokensHashMap.put("date:istoday", " DATEDIFF(now(),<FIELD> ) = 0 " );
		tokensHashMap.put("date:before", " < '<VAL1>'");
		tokensHashMap.put("date:onorbefore", " <= '<VAL1>'");
		tokensHashMap.put("date:none", " <VAL1>");
		tokensHashMap.put("date:equal to", " = '<VAL1>'");
		tokensHashMap.put("date:withinnext_days", "<= '<VAL1>'");
		tokensHashMap.put("date:withinnext_weeks", " <= '<VAL1>'");
		tokensHashMap.put("date:withinnext_months", " <= '<VAL1>'");
		
		
/*		tokensHashMap.put("date:before_between", " DATEDIFF(now(),DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR) ) >= <VAL1> " +
		"AND DATEDIFF(now(),DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR)  ) <= <VAL2> ");
*/
		// DATEDIFF('0001-10-01', DATE_ADD(c.birth_day, INTERVAL -YEAR(c.birth_day) YEAR) ) % 365
		
		tokensHashMap.put("date:before_between", " DATEDIFF('<FIRSTYEAR_DATE>', DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>) YEAR) ) % 365 >= <VAL1> " +
			"AND DATEDIFF('<FIRSTYEAR_DATE>', DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>) YEAR) ) % 365 <= <VAL2> ");
		//DATEDIFF(now(), s.sales_date ) >= 6 AND DATEDIFF(now(), s.sales_date ) <= 7
		tokensHashMap.put("date:range_between", " DATEDIFF(now(),<FIELD> ) >= <VAL1> AND  DATEDIFF(now(),<FIELD> ) <= <VAL2> ");
		
		tokensHashMap.put("date:onorafter", " >= '<VAL1>'");
		tokensHashMap.put("date:after", " > '<VAL1>'");

/*		tokensHashMap.put("date:after_between", " DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR), now() ) >= <VAL1> " +
		" AND DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+YEAR(now()) YEAR), now() ) <= <VAL2> ");
*/
		//DATEDIFF(DATE_ADD(c.birth_day, INTERVAL -YEAR(c.birth_day)+1 YEAR), '0000-07-27' ) % 365 
		tokensHashMap.put("date:after_between", " DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+1 YEAR), '<ZEROYEAR_DATE>' ) % 365 >= <VAL1> " +
		" AND DATEDIFF(DATE_ADD(<FIELD>, INTERVAL -YEAR(<FIELD>)+1 YEAR), '<ZEROYEAR_DATE>' ) % 365 <= <VAL2> ");
		
		tokensHashMap.put("date:between", " between '<VAL1>' and '<VAL2>'");
		
		tokensHashMap.put("date:withinlast", "DATEDIFF(now(),<FIELD> ) >=0 AND DATEDIFF(now(),<FIELD> ) <= <VAL1>");
		tokensHashMap.put("date:notwithinlast", "DATEDIFF(now(),<FIELD> ) <= <VAL1>");
		tokensHashMap.put("date:notafter", "> '<VAL1>'");
		tokensHashMap.put("date:notbefore", "< '<VAL1>'");
		

		tokensHashMap.put("boolean:is", " = <VAL1>");
		tokensHashMap.put("boolean:equal", " = <VAL1>");
		tokensHashMap.put("date:is date", " '<VAL1>'");
		tokensHashMap.put("date:is within last", "DATEDIFF(now(),<FIELD> ) <= <VAL1>");
		tokensHashMap.put("date:not within last", "DATEDIFF(now(),<FIELD> ) <= <VAL1>");
		tokensHashMap.put("date:is within next", "DATEDIFF(now(),<FIELD> ) >= <VAL1>");
		//tokensHashMap.put("date:iswithinnext", "DATEDIFF(<FIELD>,now()) = <VAL1>");
		tokensHashMap.put("date:iswithinnext", "<EXP> - (DATEDIFF(now(),<FIELD>)) = <VAL1>");
		tokensHashMap.put("date:iswithinnextS", "(DATEDIFF(<FIELD>,now())) = <VAL1>");
		tokensHashMap.put("date:withinnext_days_ignoreyear","DATE_FORMAT(<FIELD>, '%m-%d')");
		tokensHashMap.put("date:withinnext_days","DATE_FORMAT('<Val1>', '%m-%d')") ;
		tokensHashMap.put("date:isetoday","  DATE_ADD(<FIELD> , interval <Dval> day)=(Date(now()))");
		tokensHashMap.put("date:isequal","  DATE_ADD(<FIELD> , interval <Dval> day)=(Date('<VAL1>'))");
		tokensHashMap.put("date:isebetween","  DATE_ADD(<FIELD> , interval <Dval> day) between (Date('<VAL1>')) and  (Date('<VAL2>'))");
		tokensHashMap.put("date:iseonorafter","  DATE_ADD(<FIELD> , interval <Dval> day)>=(Date('<VAL1>'))");
		tokensHashMap.put("date:iseonorbefore","  DATE_ADD(<FIELD> , interval <Dval> day)<=(Date('<VAL1>'))");
		tokensHashMap.put("date:iseafter","  DATE_ADD(<FIELD> , interval <Dval> day)>(Date('<VAL1>'))");
		tokensHashMap.put("date:isebefore","  DATE_ADD(<FIELD> , interval <Dval> day)<(Date('<VAL1>'))");
		
		tokensHashMap.put("date:isiytoday","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')=DATE_FORMAT(now(), '%m-%d')");
		tokensHashMap.put("date:isiyequal","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')=DATE_FORMAT('<VAL1>', '%m-%d')");
		tokensHashMap.put("date:isiybetween","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d') between DATE_FORMAT('<VAL1>', '%m-%d') and  DATE_FORMAT('<VAL2>', '%m-%d')");
		tokensHashMap.put("date:isiyonorafter","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')>=DATE_FORMAT('<VAL1>', '%m-%d')");
		tokensHashMap.put("date:isiyonorbefore","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')<=DATE_FORMAT('<VAL1>', '%m-%d')");
		tokensHashMap.put("date:isiyafter","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')>DATE_FORMAT('<VAL1>', '%m-%d')");
		tokensHashMap.put("date:isiybefore","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')<DATE_FORMAT('<VAL1>', '%m-%d')");
		
		tokensHashMap.put("date:iseqwithinnext","  DATE_ADD(<FIELD> , interval <Dval> day)<(Date(DATE_ADD(now(),interval <DDval1> day)))");
		tokensHashMap.put("date:iseqiywithinnext","  DATE_FORMAT(DATE_ADD(<FIELD> , interval <Dval> day),'%m-%d')<(DATE_FORMAT(DATE_ADD(now() ,interval <DDval1> day), '%m-%d'))");
		
		//for is ignore year
		tokensHashMap.put("date:ignoreyear_is", "(DATE_FORMAT(<FIELD>,'%m-%d') = DATE_FORMAT('<VAL1>','%m-%d'))");
		tokensHashMap.put("date:ignoreyear_between", "(DATE_FORMAT(<FIELD>,'%m-%d') between DATE_FORMAT('<VAL1>','%m-%d') AND DATE_FORMAT('<VAL2>','%m-%d'))");
		tokensHashMap.put("date:ignoreyear_after", "(DATE_FORMAT(<FIELD>,'%m-%d') > DATE_FORMAT('<VAL1>','%m-%d'))");
		tokensHashMap.put("date:ignoreyear_before", "(DATE_FORMAT(<FIELD>,'%m-%d') < DATE_FORMAT('<VAL1>','%m-%d'))");
		tokensHashMap.put("date:ignoreyear_onorafter", "(DATE_FORMAT(<FIELD>,'%m-%d') >= DATE_FORMAT('<VAL1>','%m-%d'))");
		tokensHashMap.put("date:ignoreyear_onorbefore", "(DATE_FORMAT(<FIELD>,'%m-%d') <= DATE_FORMAT('<VAL1>','%m-%d'))");
		//tokensHashMap.put("date:ignoreyear_none", " <VAL1>");
	}

	
	public static boolean isNotOnlyHomePassed(String queryString) {
		
		String[] tokensArr = queryString.split("\\|\\|");
		
		
		String userIdStr=null;
		String finalStr="";

		String token=null;
		String tokenFilterStr="";
		String valStr = "";
		boolean foundFlag = false;
		for (int i = 0; i < tokensArr.length; i++) {
			token=tokensArr[i].trim();
			 //logger.info("TOKEN="+token);
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
					(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_") || fieldsArr[1].toLowerCase().trim().startsWith("date(aggrlty.") ) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.")) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("ltytrxch.") || fieldsArr[1].toLowerCase().trim().startsWith("count(ltytrxch.")) ||
					(fieldsArr[1].toLowerCase().trim().startsWith("date(ltytrxch.")) 
					|| fieldsArr[1].toLowerCase().trim().startsWith("cd.")||
					(fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem() ) 
							|| fieldsArr[0].equalsIgnoreCase(SegmentEnum.INTERACTION_RECEIVED.getItem()) || fieldsArr[1].toLowerCase().trim().startsWith("new_")
							|| fieldsArr[1].toLowerCase().trim().startsWith("aggr.") || fieldsArr[1].toLowerCase().trim().startsWith("if((year(now())") 
							|| fieldsArr[1].trim().startsWith("ROUND((sal.quantity*sal.sales_price)+sal.tax-(IF(sal.discount") //app-3827
					)) ) {
					
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
	 * @param queryString
	 * @return
	 */
	public static String generateListSegmentQuery(String queryString, long mlBit) {
		return generateListSegmentQuery(queryString, false, Constants.SEGMENT_ON_EMAIL, mlBit);
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
	 * @param queryString
	 * @param ignoreEmailStatus
	 * @return
	 */
	public static String generateListSegmentQuery(String queryString, boolean ignoreEmailStatus, String qryType, long mlBit) {
		
		/*long starttimer = System.currentTimeMillis();
		
		logger.debug("query string to be made  "+queryString);*/
		
		//String returnStr=null;		
		try {
			
			
			String finalStr="";
			String replaceValueCode=Constants.STRING_NILL;
			if(isNotOnlyHomePassed(queryString)) {
			
			//APP-4201
			boolean isLastPurchaseDateTemplateReplaced = false; 
			boolean chkSalesColumnsFound=false;
			boolean chkSKUColumnsFound=false;
			boolean chkAggrColumnsFound = false;
			boolean chkAggrLtyColumnsFound = false;
			boolean chkLoyaltyFlagFound = false;
			String ltySelectStr =Constants.STRING_NILL;
			StringBuffer ltygroupBySB = new StringBuffer();
			StringBuffer ltyConditionSB = new StringBuffer();
			boolean chkLoyaltyTrxFlagFound = false;
			boolean chkOpensClicksFound = false;
			//boolean chkLoyaltyFlagFound = false;
			boolean chkHomePassedFound = false;
			boolean chkAggregateFlagFound = false;
			StringBuffer aggrFieldsSB = new StringBuffer();
			StringBuffer groupBySB = new StringBuffer();
			StringBuffer HavingSB = new StringBuffer();
			StringBuffer ltyHavingSB = new StringBuffer();
			StringBuffer salesConditionSB = new StringBuffer();
			StringBuffer maxSalesConditionSB = new StringBuffer();
			StringBuffer deptItemCatSalesConditionSB = new StringBuffer();//APP-4227
			StringBuffer csConditionSB = new StringBuffer();
			StringBuffer csInnerConditionSB = new StringBuffer();
			boolean isCsNotReceivedUnionExist = false;
			String salesSelectFields = Constants.STRING_NILL;
			String ltySelectFields = Constants.STRING_NILL;
			//StringBuffer csNotReceivedUnionQrySB = new StringBuffer();
			//String csColumnsStr = "";
			String csInnerColumnsStr = "";
			
			
			String fromTables = "";
			String fromTableFilters = "";
			
			String salesFromTables = "";
			String salesFromTableFilters = "";
			
			String ltyFromTables = "";
			String ltyFromTableFilters = "";
			boolean isHaveNotFlag=false;
			String couponIdStr="";
			String couponCondIdStr="";
			String promoName=Constants.STRING_NILL;
			
			/**
			 * 761
			 * ||sal.sales_date|date:is|03/05/2012<OR>sal.sales_date|date:is|07/05/2012<OR>
			 * sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
			 * ||sal.sales_date|date:isToday|now()<AND>
			 */
			
			
			HashMap<String, String> filterMap = new HashMap<String, String>();
			//String listIdStr=null;
			String userIdStr=null;//to allow segment
			String[] tokensArr = queryString.split("\\|\\|");
			
		

			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			
			boolean isAggr = false;
			boolean isltyAggr = false;
			//boolean isAggrOnRcpt = false;
			
			//boolean isCSANDConjuction = false;
			String csCampIDs = "";
			//String crIds = "";
			boolean isLatestCampaigns = false;
			int numOfCampaigns = 0;
			boolean chkPromoflag=false;
			
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				 logger.info("TOKEN="+token);
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				/*if(i==0) {
					if(token.indexOf(':')==-1) return null;
					userIdStr = token.substring(token.indexOf(':')+1).trim();
					token = token.substring(0, token.indexOf(':')).trim();
					tokenFilterStr =(token.equalsIgnoreCase("all") || token.equalsIgnoreCase("AND")) ? "AND" : "OR";
					continue;
				}*/
				 //Changes for interaction attributes
				 if(i==0) {
					if(token.indexOf(':')==-1) return null;
					
					String[] firstTokenArr = token.split(":");
					tokenFilterStr = (firstTokenArr[0].equalsIgnoreCase("all") || firstTokenArr[0].equalsIgnoreCase("AND")) ? " AND " : " OR ";
					userIdStr = firstTokenArr[1];
					if(firstTokenArr.length > 2) {
						
						csCampIDs = firstTokenArr[2];
						//if(firstTokenArr.length == 4) crIds = firstTokenArr[3];
						if(firstTokenArr.length == 4) isLatestCampaigns = true;
						
						StringTokenizer tokenizerr = new StringTokenizer(csCampIDs, Constants.DELIMETER_COMMA);
						numOfCampaigns = tokenizerr.countTokens();
					}//if
					
					continue;
				}//if
				 
				//************************************
				
				//now my token sal.sales_date|date:is|03/05/2012<OR>
				//sal.sales_date|date:is|07/05/2012<OR>
				//sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
				
				String[] tempTokenArr = token.split("<OR>");
				StringBuffer orHavingSB = new StringBuffer();
				StringBuffer orSalesConditionSB = new StringBuffer();
				StringBuffer ltyORHavingSB = new StringBuffer();
				StringBuffer orLtyConditionSB = new StringBuffer();
				
				StringBuffer csConditionQry = new StringBuffer();//.STRING_NILL;
				String csInnerCondition = Constants.STRING_NILL;
				
				for (int tokenIndex = 0;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
					String fieldsArr[] = tempToken.split("\\|");
					logger.info("tempToken :: "+tempToken);
					if(tempToken.contains("OptCulture Promotion Name")) promoName=fieldsArr[3].split(Constants.ADDR_COL_DELIMETER)[0];
					//include aggr fields in the select clause
					
					if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count") ) {
						
						//isAggr = true;
						
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
					}//if
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_max") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty")){
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(groupBySB.length() == 0 || isAggr == false) {
							
							if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_max"))
										groupBySB.append(",totSal.doc_sid");//becuase purchase amount is receipt level app-3906
							
							else if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ||
									fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
									fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty"))
											groupBySB.append(",totSal.cid");//doc_sid will not always points to udf10
							
							isAggr = true;
							
						}
						
					}//if
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_max") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_" )){
						
						
						if(!ltySelectFields.contains(ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!ltySelectFields.isEmpty()) ltySelectFields += Constants.DELIMETER_COMMA;
							ltySelectFields += ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(ltyConditionMap.get(fieldsArr[1].toLowerCase().trim())!=null){
							if(!(ltyConditionSB.length()==0)) ltyConditionSB.append(tokenFilterStr);
							ltyConditionSB.append(ltyConditionMap.get(fieldsArr[1].toLowerCase().trim()));
						}
						
						if(ltygroupBySB.length() == 0 || isltyAggr == false) {
							
							ltygroupBySB.append(",totloyal.contact_id");
							isltyAggr = true;
							
						}
						
					}//if
					if(chkAggrColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggr_") 
							//|| (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") ) || 
							|| fieldsArr[1].toLowerCase().trim().startsWith("new_") || 
							fieldsArr[1].toLowerCase().trim().startsWith("sal.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						
						chkAggrColumnsFound = true;
						
						//APP-4201
						if (queryString.contains("number:date_diff_equal_to") && isLastPurchaseDateTemplateReplaced == false) {
							isLastPurchaseDateTemplateReplaced = true;
							String rps_aggr_query_template = aggr_query_template.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
							fromTables += Constants.DELIMETER_COMMA + rps_aggr_query_template;
						} else {
						 	fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
						}
						fromTableFilters  += " AND c.cid=totSal.cid  ";
						
					}
					if(chkAggrLtyColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_") 
							|| fieldsArr[1].toLowerCase().trim().startsWith("ltytrxch.") || fieldsArr[1].toLowerCase().trim().startsWith("date(ltytrxch."))) {
						chkAggrLtyColumnsFound = true;
						if(fieldsArr[1].toLowerCase().trim().contains("aggrlty_reward")) {
							fromTables += Constants.DELIMETER_COMMA + loyaltyvalucode_query_template;
							fromTables += " , loyalty_balance ltybalance , contacts_loyalty loyalty ";
							fromTableFilters  += "   AND ltybalance.user_id = <CONTLTYUSERID> AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.loyalty_id=totloyalbal.loyalty_id "
									+ " AND loyalty.contact_id=c.cid";
						}else {
						 fromTables += Constants.DELIMETER_COMMA + loyaltyaggr_query_template;
						 //fromTableFilters  += " AND c.cid=totloyal.contact_id  ";
							 if(chkLoyaltyFlagFound == false ){
								 if(ltySelectStr.isEmpty()) ltySelectStr = Constants.DELIMETER_COMMA+OCConstants.replaceLtySelectStr;
									chkLoyaltyFlagFound = true;
									if(!fromTables.contains("contacts_loyalty loyalty")) fromTables += " , contacts_loyalty loyalty ";
									/*if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_reward")){
										fromTableFilters  += " AND loyalty.cid=totloyalbal.contact_id "; 
									}*/
									fromTableFilters  += " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid ";
									/*if((fromTableFilters.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.contact_id=totloyal.contact_id"))){
										fromTableFilters  += " AND loyalty.contact_id=totloyal.contact_id ";
									}*/
									if((fromTableFilters.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
										fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
									}
									if((fromTableFilters.contains("loyalty.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
										fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
									}
									
									if(fieldsArr[1].toLowerCase().trim().contains("aggrlty_rewardbalance")) {
										fromTables += " , loyalty_balance ltybalance ";
										fromTableFilters  += " AND ltybalance.user_id = <CONTLTYUSERID> AND ltybalance.loyalty_id=loyalty.loyalty_id ";
									}
									
								}else if((fromTableFilters.contains("loyalty.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
									fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
								}
							 if(ltyConditionMap.get(fieldsArr[2].toLowerCase().trim())!=null){
									if(!(ltyConditionSB.length()==0)) ltyConditionSB.append(tokenFilterStr);
									ltyConditionSB.append(ltyConditionMap.get(fieldsArr[2].toLowerCase().trim()));
							 }
						}
					}
					if(chkLoyaltyFlagFound == false && 
							( fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") )){
						if(ltySelectStr.isEmpty()) ltySelectStr = Constants.DELIMETER_COMMA+OCConstants.replaceLtySelectStr;
						chkLoyaltyFlagFound = true;
						fromTables += " , contacts_loyalty loyalty ";
						fromTableFilters  += " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid "; 
						
						/*if((fromTableFilters.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.contact_id=totloyal.contact_id"))){
							fromTableFilters  += " AND loyalty.contact_id=totloyal.contact_id ";
						}*/
						/*if((fromTableFilters.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
							fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
						}*/
						
					}//if
					if(chkSKUColumnsFound==false && (fieldsArr[1].toLowerCase().trim().startsWith("sku.") 
							|| fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") )) {
						
						chkSKUColumnsFound=true;
						if(chkAggrColumnsFound==false) {
							
							chkAggrColumnsFound = true;
							// fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							// fromTableFilters  += " AND c.cid=totSal.cid  ";
							
							//APP-4201
							if (queryString.contains("number:date_diff_equal_to") && isLastPurchaseDateTemplateReplaced == false) {
								isLastPurchaseDateTemplateReplaced = true;
								String rps_aggr_query_template = aggr_query_template.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
								fromTables += Constants.DELIMETER_COMMA + rps_aggr_query_template;
							} else {
							 	fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							}
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
					
					if(chkAggregateFlagFound == false && !(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty")) && ( fieldsArr[1].toLowerCase().trim().startsWith("aggr.") || fieldsArr[1].toLowerCase().trim().startsWith("if((year(now()")) ) {
						
						chkAggregateFlagFound = true;
						fromTables += " , sales_aggregate_data aggr";
						fromTableFilters  += " AND aggr.user_id in(<SALESAGGRUSERID>) AND c.cid=aggr.cid"; 
						
					}//if
					
					if(chkSalesColumnsFound == false && ( fieldsArr[1].trim().startsWith("ROUND((sal.quantity*sal.sales_price)+sal.tax-(IF(sal.discount") //app-3827
							//fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							//fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							//fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							//fieldsArr[1].toLowerCase().trim().startsWith("new_count")
							)  ) {
						
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";
						
					}
									
					if(chkLoyaltyFlagFound == false &&
							( fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") )){
						if(!ltySelectStr.isEmpty()) ltySelectStr = Constants.DELIMETER_COMMA+OCConstants.replaceLtySelectStr;
						chkLoyaltyFlagFound = true;
						fromTables += " , contacts_loyalty loyalty ";
						fromTableFilters  += " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid "; 
						
						/*if((fromTables.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.contact_id=totloyal.contact_id"))){
							fromTableFilters  += " AND loyalty.contact_id=totloyal.contact_id ";
						}*/
						/*if((fromTableFilters.contains("totloyal.")) &&(!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
							fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
						}*/
						
					}//if
					if( fieldsArr[1].toLowerCase().trim().startsWith("cd.status")) {
						
						if(!isHaveNotFlag && fieldsArr[3].toLowerCase().trim().contains("notactive")) {
							//fromTables="";
							//fromTables +=promotion_have_not_issued_qury_template;
							isHaveNotFlag=true;
							couponIdStr += " AND cd.status IN('Active','Redeemed')";
							continue;
						}else if(!isHaveNotFlag && !chkPromoflag){
							chkPromoflag=true;
							fromTables +=Constants.DELIMETER_COMMA + promotion_qury_template;
							
							fromTableFilters +=" AND c.cid=cd.contact_id "; //OR c.external_id=cd.redeem_cust_id ) ";
							
						}
								//+ "OR c.email_id=cd.redeem_email_id OR c.mobile_phone=cd.redeem_phn_id)";
					}
					
					//if
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
									userIdStr, qryType, ignoreEmailStatus, mlBit);
						
							
							
							
						}//if
						
						else {
							
							String replacedVal = replaceToken(fieldsArr[1].trim(), fieldsArr); 
							if(replacedVal.contains("<EXP>")) {
								//replace expiry value
								Coupons coupon=null;
								try {
									CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName("couponsDao");
									 coupon=couponsDao.getConpounByName(userIdStr, promoName);
									
									if(coupon!=null) {
										String[] expArr = coupon.getExpiryDetails().split(Constants.ADDR_COL_DELIMETER);
										replacedVal = replacedVal.replace("<EXP>",expArr[1]);
									}
									
									//ruleSB.append(valStr);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ",e);
								}
							}
							if(replacedVal.contains("cp.coupon_id") || replacedVal.contains("cd.coupon_id")) {
								couponCondIdStr += (!couponCondIdStr.isEmpty() ? " AND "+replacedVal : replacedVal);
								if(replacedVal.contains("cd.coupon_id") ) {
									//couponIdStr = replacedVal;
									couponIdStr += (!couponIdStr.isEmpty() ? " AND "+replacedVal : replacedVal);
									//replacedVal = "";
								}
								replacedVal = "";
							}
							
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
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_max") || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ||
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty")) {//TODO no need of another condition 
							
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
								
								
								
							}else if (fieldsArr[1].toLowerCase().trim().startsWith("date(sal.")
									&& fieldsArr[2].toLowerCase().trim().equals("number:date_diff_equal_to")) { // APP-4201

								if (maxSalesConditionSB.length() > 0)	maxSalesConditionSB.append(" OR ");

								maxSalesConditionSB.append(replacedVal);

							}else if(fieldsArr[1].toLowerCase().trim().startsWith("sal.") ||
									fieldsArr[1].toLowerCase().trim().startsWith("date(sal.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(sku.")||
									fieldsArr[1].toLowerCase().trim().startsWith("sku.")) {
								
								
								if(orSalesConditionSB.length() > 0) orSalesConditionSB.append(" OR ");
								
								orSalesConditionSB.append(replacedVal);
								
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("ltytrxch.") ||
									(fieldsArr[1].toLowerCase().trim().startsWith("date(ltytrxch."))) {
								
								//if(orLtyConditionSB.length() > 0) orLtyConditionSB.append("OR");
								//orLtyConditionSB.append(replacedVal);
								//new
								//if(ltyORHavingSB.length() > 0) ltyORHavingSB.append("OR");
								//ltyORHavingSB.append(replacedVal);
								if(ltyConditionSB.length() > 0) ltyConditionSB.append(tokenFilterStr);
								
								ltyConditionSB.append("("+replacedVal+")");
								
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_tot") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("aggrlty_avg") || 
									fieldsArr[1].trim().toLowerCase().trim().startsWith("aggrlty_max") || 
									fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_" ) ) {//TODO no need of another condition 
								
									if(ltyORHavingSB.length() > 0) ltyORHavingSB.append("OR");
									
									SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
									if(selectFieldEnum != null) {
										
										replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
										
									}
								
									ltyORHavingSB.append(replacedVal);
								
							
							
								}//if
							else{
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
							
							}
						}
					}
					if(fieldsArr.length>4 && fieldsArr[4]!=null && !fieldsArr[4].isEmpty()) {
					if(!replaceValueCode.isEmpty()) replaceValueCode += Constants.DELIMETER_COMMA;
					replaceValueCode +="'"+fieldsArr[4]+"'";
					}
					
					
				} // for 
				
				
				logger.debug("csConditionQry "+csConditionQry);
				if( orHavingSB.length() > 0 ) {
					
					if(HavingSB.length() > 0) HavingSB.append(tokenFilterStr);
					
					HavingSB.append("("+orHavingSB.toString()+")");
					
					
				}//if
				if( ltyORHavingSB.length() > 0 ) {
					
					if(ltyHavingSB.length() > 0) ltyHavingSB.append(tokenFilterStr);
					
					ltyHavingSB.append("("+ltyORHavingSB.toString()+")");
					
					
				}//if
				if(orSalesConditionSB.length() > 0) {
					
					// APP-4227
					if (token.contains("String:only like")) {
						
						// if(deptItemCatSalesConditionSB.length() > 0) deptItemCatSalesConditionSB.append(tokenFilterStr);
						
						// deptItemCatSalesConditionSB.append("("+orSalesConditionSB.toString()+")");
						
						if (deptItemCatSalesConditionSB.length() > 0) {
							String temp = deptItemCatSalesConditionSB.toString();
							deptItemCatSalesConditionSB = new StringBuffer();
							temp = " (" + temp + " OR (" + orSalesConditionSB.toString() + ")) ";
							deptItemCatSalesConditionSB.append(temp);
						} else {
							deptItemCatSalesConditionSB.append("(" + orSalesConditionSB.toString() + ")");
						}
						
					} else {
						
						if (salesConditionSB.length() > 0) salesConditionSB.append(tokenFilterStr);

						salesConditionSB.append("(" + orSalesConditionSB.toString() + ")");
					}
					
				}
				
				if(orLtyConditionSB.length() > 0) {
					
					if(ltyConditionSB.length() > 0) ltyConditionSB.append(tokenFilterStr);
					
					ltyConditionSB.append("("+orLtyConditionSB.toString()+")");
					
					
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
				
				logger.info("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				boolean isHaving = (isAggr || isltyAggr || chkAggrColumnsFound || chkOpensClicksFound || isCsNotReceivedUnionExist|| chkAggrLtyColumnsFound);
				if(isHaving) {
					logger.debug("isHaving ::"+isHaving );
					filterMap.put("GENERAL", "");
				}
				
				else{
					
					return null;
				}
				
			}
			if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.info(">>> NO Cfs  1");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
				String tempStr =null;
				String csNotReceivedUnionQryStr = null;//csNotReceivedUnionQrySB.length() > 0 ? csNotReceivedUnionQrySB.toString() : "";
					if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_EXTERNALID)) {
						
						tempStr = generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString())
									.replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
									.replace("<SELECTFIELDS>",ltySelectStr);
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_total.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
													.replace("<SELECTFIELDS>",ltySelectStr);
										
							
						}
						
						
					}
					else if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						tempStr = email_generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
									.replace("<SELECTFIELDS>",ltySelectStr);
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_email.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
													.replace("<SELECTFIELDS>",ltySelectStr);
										
							
						}
						
					}
					else if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						tempStr = mobile_generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
									.replace("<SELECTFIELDS>",ltySelectStr);
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobile.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
													.replace("<SELECTFIELDS>",ltySelectStr);
										
							
						}
						
					}
					else if(ignoreEmailStatus==false && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						tempStr = Old_generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).
									replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
									.replace("<SELECTFIELDS>",ltySelectStr);
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_email_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
													.replace("<SELECTFIELDS>",ltySelectStr);
										
							
						}
						
					}
					else if(ignoreEmailStatus==false && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						tempStr = mobile_generalSqlWithMobileStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
									.replace("<SELECTFIELDS>",ltySelectStr);
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobile_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
													.replace("<SELECTFIELDS>",ltySelectStr);
										
							
						}
						
					}else if(qryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
						tempStr = notification_generalSqlWithNotificationStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_notification.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
					}
					if(isHaveNotFlag  ) {
						tempStr = generalSqlWithEmailStatusForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).
											replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
						.replace("<SELECTFIELDS>",ltySelectStr);
					}
				  if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
					  tempStr = email_generalSqlWithEmailStatusForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).
											replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
					  						.replace("<SELECTFIELDS>",ltySelectStr);
				  }
				 if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
					  tempStr = mobile_generalSqlWithEmailStatusForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).
											replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString())
											.replace("<SELECTFIELDS>",ltySelectStr);
					  
				 }
					
				//logger.info("fromTables ::"+fromTableFilters);
					fromTables = fromTables.replace("<SALESUSERID>", userIdStr).
							replace("<SALESFROMTABLES>", salesFromTables).replace("<SALESFROMTABLESFILTERS>", salesFromTableFilters).
							replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
							.replace("<HPUSERID>", userIdStr).replace("<LTYTRXCHUSERID>", userIdStr)
							.replace("<HPUSERID>", userIdStr).replace("<COUPONID>", couponIdStr);
					/*if(chkAggrColumnsFound && chkSalesColumnsFound) {
						fromTableFilters  += " AND totSal.cid=sal.cid ";
					}*/
					fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).
					replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
					.replace("<HPUSERID>", userIdStr).replace("<CONTLTYUSERID>",userIdStr);
					
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
					//((!crIds.isEmpty()) ? "AND cr_id in("+crIds+")" : "")
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
					
					logger.debug("salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "+(salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : ""));
					// APP-4227
					if (queryString.contains("String:only like") && deptItemCatSalesConditionSB.length() > 0) {
						String salesDateCond = salesConditionSB.toString();
						if (salesConditionSB.length() > 0) salesConditionSB.append(" AND ");
						salesConditionSB.append(department_and_item_category_sub_query.replace("<DEPT_ITEM_CAT_ONLYLIKE>", deptItemCatSalesConditionSB.toString()));
						
						String temp = salesConditionSB.toString();
						if (salesDateCond.length() > 0 && temp.contains("<SALES_COND>"))
							temp = temp.replace("<SALES_COND>", "AND " + salesDateCond);
						else
							temp = temp.replace("<SALES_COND>", "");
						/*
						if (queryString.contains("number:date_diff_equal_to")) {
							temp = temp.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
						}*/
						salesConditionSB = new StringBuffer();
						salesConditionSB.append(temp);
						
					}
					tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL") != null && !filterMap.get("GENERAL").isEmpty()	? " AND ("+ filterMap.get("GENERAL") + (!isHaveNotFlag && !couponCondIdStr.isEmpty()? " AND " + couponCondIdStr : "") +") " : "" );
					tempStr = tempStr.replace("<SALESCOND>", salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "");
					//APP-4201
					//tempStr = tempStr.replace("<SALESCOND>", (salesConditionSB.length() > 0 && !salesConditionSB.toString().contains("MAX"))? tokenFilterStr + salesConditionSB.toString() : "");
					tempStr = tempStr.replace("<RPS_LAST_PURCHASE_DATE_COND>", maxSalesConditionSB.length() > 0 ? maxSalesConditionSB.toString() : "");
					tempStr = tempStr.replace("<SALESSELECTFIELDS>", !salesSelectFields.isEmpty() ? Constants.DELIMETER_COMMA+salesSelectFields : "");
					tempStr = tempStr.replace("<LTYTRXCHCOND>", ltyConditionSB.length() > 0 ? tokenFilterStr + ltyConditionSB.toString() : "");
					tempStr = tempStr.replace("<LTYTRXCHSELECTFIELDS>", !ltySelectFields.isEmpty() ? Constants.DELIMETER_COMMA+ltySelectFields : "");
					//tempStr = tempStr.replace("<LTYTRXCHGROUPBY>", !ltySelectFields.isEmpty() ? " GROUP BY ltytrxch.loyalty_id " : "");
					tempStr = tempStr.replace("<LTYTRXCHGROUPBY>", !ltySelectFields.isEmpty() ? " GROUP BY ltytrxch.loyalty_id " : "");
					tempStr = tempStr.replace("<LTYBALGROUPBY>", !ltySelectFields.isEmpty() ? " GROUP BY ltybalance.loyalty_id " : "");
					tempStr = tempStr.replace("<SALESGROUPBY>", !salesSelectFields.isEmpty() ? " GROUP BY sal.doc_sid " : "");
					tempStr = tempStr.replace("<HAVING>", ltyHavingSB.length() > 0 ? " HAVING " + ltyHavingSB.toString() : "");
					tempStr = tempStr.replace("<RPS_LAST_PURCHASE_DATE_TEMP>", ""); //APP-4201
					
					logger.debug("tempStr ::"+tempStr);
					//replace zero year and first year
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					String firstYear = "0001-"+currMonthDate;
					
					tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
					tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
					tempStr = tempStr.replace("<SALESUSERID>", userIdStr);
					
					
				finalStr=tempStr;
			}
			else {
				logger.info(">>> with CFs");
				//String[] listIdArr = listIdStr.split(",");
				String[] userIdsArr = userIdStr.split(",");
				
				String tempStr="";
				for (String listId : userIdsArr) {
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
				
				
				
				finalStr = "SELECT * FROM ( " + tempStr + " ) AS tempTab GROUP BY email_id";
				
			} // else
			
//			logger.info("==="+filterMap);
//			logger.info(listIdStr+"=FN="+finalStr);
			}
			else {
				
				finalStr = generateHomePassedQry(queryString, ignoreEmailStatus, qryType, mlBit);
				
			}
		
			/*logger.fatal("Total Time Taken :: SalesQueryGenerator ::  generateListSegmentQuery "
							+ (System.currentTimeMillis() - starttimer));*/
			if(finalStr.contains("<Reward>")) finalStr = finalStr.replace("<Reward>", replaceValueCode);
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		//return returnStr;
		
	} // generateQuery
	
	
	
	
	/**
	 * Generates the mySql query string from the given string
	 * @param queryString
	 * @param ignoreEmailStatus
	 * @return
	 */
	public static String generateListSegmentCountQuery(String queryString, boolean ignoreEmailStatus, String qryType, long mlBit) {
		
		/*long starttimer = System.currentTimeMillis();
		
		logger.debug("query string to be made  "+queryString);*/
		
		//String returnStr=null;		
		try {
			
			
			String finalStr="";
			String replaceValueCode = Constants.STRING_NILL;
			if(isNotOnlyHomePassed(queryString)) {
			
			//APP-4201
			boolean isLastPurchaseDateTemplateReplaced = false; 
			boolean chkSalesColumnsFound=false;
			boolean chkSKUColumnsFound=false;
			boolean chkAggrColumnsFound = false;
			boolean chkOpensClicksFound = false;
			//boolean chkLoyaltyFlagFound = false;
			//boolean chkLoyaltyTrxFlagFound = false;
			boolean chkAggrLtyColumnsFound = false;
			boolean chkLoyaltyFlagFound = false;
			String ltySelectStr = Constants.STRING_NILL;
			StringBuffer ltygroupBySB = new StringBuffer();
			StringBuffer ltyConditionSB = new StringBuffer();
			
			boolean chkHomePassedFound = false;
			boolean chkAggregateFlagFound = false;
			StringBuffer aggrFieldsSB = new StringBuffer();
			StringBuffer groupBySB = new StringBuffer();
			StringBuffer HavingSB = new StringBuffer();
			StringBuffer ltyHavingSB = new StringBuffer();
			StringBuffer salesConditionSB = new StringBuffer();
			StringBuffer maxSalesConditionSB = new StringBuffer();
			StringBuffer deptItemCatSalesConditionSB = new StringBuffer();//APP-4227
			StringBuffer csConditionSB = new StringBuffer();
			StringBuffer csInnerConditionSB = new StringBuffer();
			boolean isCsNotReceivedUnionExist = false;
			String salesSelectFields = Constants.STRING_NILL;
			String ltySelectFields = Constants.STRING_NILL;
			//StringBuffer csNotReceivedUnionQrySB = new StringBuffer();
			//String csColumnsStr = "";
			String csInnerColumnsStr = "";
			
			
			String fromTables = "";
			String fromTableFilters = "";
			
			String salesFromTables = "";
			String salesFromTableFilters = "";
			boolean isHaveNotFlag=false;
			String couponIdStr="";
			String couponCondIdStr = "";
			/**
			 * 761
			 * ||sal.sales_date|date:is|03/05/2012<OR>sal.sales_date|date:is|07/05/2012<OR>
			 * sal.sales_date|date:between|03/05/2012|07/05/2012<OR><AND>
			 * ||sal.sales_date|date:isToday|now()<AND>
			 */
			
			
			HashMap<String, String> filterMap = new HashMap<String, String>();
			//String listIdStr=null;
			String userIdStr=null;//to allow segment
			String[] tokensArr = queryString.split("\\|\\|");
			
		

			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			
			boolean isAggr = false;
			boolean isltyAggr = false;
			//boolean isAggrOnRcpt = false;
			
			
			
			String csCampIDs = "";
			boolean isLatestCampaigns = false;
			//String crIds = "";
			int numOfCampaigns = 0;
			boolean chkPromoflag=false;
			String promoName=Constants.STRING_NILL;
			
			for (int i = 0; i < tokensArr.length; i++) {
				
				token=tokensArr[i].trim();
				 logger.info("TOKEN="+token);
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) return null;
					
					String[] firstTokenArr = token.split(":");
					tokenFilterStr = (firstTokenArr[0].equalsIgnoreCase("all") || firstTokenArr[0].equalsIgnoreCase("AND")) ? " AND " : " OR ";
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
				StringBuffer ltyORHavingSB = new StringBuffer();
				StringBuffer orLtyConditionSB = new StringBuffer();
				StringBuffer csConditionQry = new StringBuffer();
				String csInnerCondition = Constants.STRING_NILL;
				//String csColumnsStr = "";
				//String csInnerColumnsStr = "";
				
				for (int tokenIndex = 0;tokenIndex<tempTokenArr.length ;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
					String fieldsArr[] = tempToken.split("\\|");
					logger.info("tempToken :: "+tempToken);
					if(tempToken.contains("OptCulture Promotion Name")) promoName=fieldsArr[3].split(Constants.ADDR_COL_DELIMETER)[0];
					//include aggr fields in the select clause
					logger.debug("fieldsArr[1] ================="+fieldsArr[1]);
					if(fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							fieldsArr[1].toLowerCase().trim().startsWith("new_count")
							)  {
						
						//isAggr = true;
						
						
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
					}//if
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_max") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty")){
						
						logger.debug("ieldsArr[1] ================="+fieldsArr[1]);
						if(!salesSelectFields.contains(salesFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!salesSelectFields.isEmpty()) salesSelectFields += Constants.DELIMETER_COMMA;
							salesSelectFields += salesFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(groupBySB.length() == 0 || isAggr == false) {
							
							if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_tot") ||
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_avg") || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_max"))
										groupBySB.append(",totSal.doc_sid");//becuase purchase amount is receipt level app-3906
							
							else if(fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) ||
									fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
									fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty"))
											groupBySB.append(",totSal.cid");//doc_sid will not always points to udf10
							
							isAggr = true;
							
						}
						
					}//if
					
					if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_tot") ||
							fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_avg") || 
							fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_max") || fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_" )){
						
						logger.info("ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim()) "+ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim()));
						if(!ltySelectFields.contains(ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim()))) {
							
							if(!ltySelectFields.isEmpty()) ltySelectFields += Constants.DELIMETER_COMMA;
							ltySelectFields += ltyFieldsMap.get(fieldsArr[1].toLowerCase().trim());
							
						}
						
						if(ltyConditionMap.get(fieldsArr[1].toLowerCase().trim())!=null){
							if(!(ltyConditionSB.length()==0)) ltyConditionSB.append(tokenFilterStr);
							ltyConditionSB.append(ltyConditionMap.get(fieldsArr[1].toLowerCase().trim()));
						}
						
						
						if(ltygroupBySB.length() == 0 || isltyAggr == false) {
							
							ltygroupBySB.append(",totloyal.contact_id");
							isltyAggr = true;
							
						}
							
					}//if
					if(chkAggrColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggr_") 
							//|| (fieldsArr[1].toLowerCase().trim().startsWith("sku.") || fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") ) || 
							|| fieldsArr[1].toLowerCase().trim().startsWith("new_") || 
							fieldsArr[1].toLowerCase().trim().startsWith("sal.") || 
							fieldsArr[1].toLowerCase().trim().startsWith("date(sal."))) {
						
						chkAggrColumnsFound = true;
						
						//APP-4201
						if (queryString.contains("number:date_diff_equal_to") && isLastPurchaseDateTemplateReplaced == false) {
							isLastPurchaseDateTemplateReplaced = true;
							String rps_aggr_query_template = aggr_query_template.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
							fromTables += Constants.DELIMETER_COMMA + rps_aggr_query_template;
						} else {
						 	fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
						}
						fromTableFilters  += " AND c.cid=totSal.cid  ";
						
						
					}
					if(chkAggrLtyColumnsFound == false && (fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_") 
							|| fieldsArr[1].toLowerCase().trim().startsWith("ltytrxch.") || fieldsArr[1].toLowerCase().trim().startsWith("count(ltytrxch.")
						 || fieldsArr[1].toLowerCase().trim().startsWith("date(ltytrxch."))) {
						chkAggrLtyColumnsFound = true;
						if(fieldsArr[1].toLowerCase().trim().contains("aggrlty_reward")) {
							fromTables += Constants.DELIMETER_COMMA + loyaltyvalucode_query_template;
							fromTables += " , loyalty_balance ltybalance , contacts_loyalty loyalty ";
							fromTableFilters  += " AND ltybalance.user_id = <CONTLTYUSERID> AND   loyalty.loyalty_id=totloyalbal.loyalty_id "
									+ " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid";
						}else {
						 fromTables += Constants.DELIMETER_COMMA + loyaltyaggr_query_template;
						 //fromTableFilters  += " AND c.cid=totloyal.contact_id  ";
						 if(chkLoyaltyFlagFound == false ){
								if(ltySelectStr.isEmpty()) ltySelectStr = Constants.DELIMETER_COMMA+OCConstants.replaceLtySelectStr;
								chkLoyaltyFlagFound = true;
								if(!fromTables.contains("contacts_loyalty loyalty")) fromTables += " , contacts_loyalty loyalty ";
								/*if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_reward")){
									fromTableFilters  += " AND loyalty.cid=totloyalbal.contact_id "; 
								}*/
								fromTableFilters  += " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid ";
								if((fromTableFilters.contains("loyalty.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
									fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
								}
								
								}else if((fromTableFilters.contains("loyalty.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
									fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
								}
						 
						 	if(ltyConditionMap.get(fieldsArr[2].toLowerCase().trim())!=null){
								if(!(ltyConditionSB.length()==0)) ltyConditionSB.append(tokenFilterStr);
								ltyConditionSB.append(ltyConditionMap.get(fieldsArr[2].toLowerCase().trim()));
							}
						}
					}
					if(chkSKUColumnsFound==false && (fieldsArr[1].toLowerCase().trim().startsWith("sku.") ||
							fieldsArr[1].toLowerCase().trim().startsWith("date(sku.") )) {
						
						chkSKUColumnsFound=true;
						if(chkAggrColumnsFound==false) {
							
							chkAggrColumnsFound = true;
							// fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							// fromTableFilters  += " AND c.cid=totSal.cid  ";
							
							//APP-4201
							if (queryString.contains("number:date_diff_equal_to") && isLastPurchaseDateTemplateReplaced == false) {
								isLastPurchaseDateTemplateReplaced = true;
								String rps_aggr_query_template = aggr_query_template.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
								fromTables += Constants.DELIMETER_COMMA + rps_aggr_query_template;
							} else {
							 	fromTables += Constants.DELIMETER_COMMA + aggr_query_template;
							}
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
					
					if(chkAggregateFlagFound == false && !(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty")) && ( fieldsArr[1].toLowerCase().trim().startsWith("aggr.") || fieldsArr[1].toLowerCase().trim().startsWith("if((year(now()")) ) {
						
						chkAggregateFlagFound = true;
						fromTables += " , sales_aggregate_data aggr";
						fromTableFilters  += " AND aggr.user_id in(<SALESAGGRUSERID>) AND c.cid=aggr.cid"; 
						
					}//if
					
					if(chkSalesColumnsFound == false && ( fieldsArr[1].trim().startsWith("ROUND((sal.quantity*sal.sales_price)+sal.tax-(IF(sal.discount")	//app-3827
							//fieldsArr[1].toLowerCase().trim().startsWith("new_tot") ||
							//fieldsArr[1].toLowerCase().trim().startsWith("new_avg") || 
							//fieldsArr[1].toLowerCase().trim().startsWith("new_max") ||
							//fieldsArr[1].toLowerCase().trim().startsWith("new_count") 
							)  ) {
						
						chkSalesColumnsFound=true;
						fromTables +=  " , retail_pro_sales sal ";
						fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";
						
					}
									
					if(chkLoyaltyFlagFound == false &&
							( fieldsArr[1].toLowerCase().trim().startsWith("loyalty.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(loyalty.") )){
						
						chkLoyaltyFlagFound = true;
						if(ltySelectStr.isEmpty()) ltySelectStr = Constants.DELIMETER_COMMA+OCConstants.replaceLtySelectStr;
						//fromTables += " , contacts_loyalty loyalty ";
						if(!fromTables.contains("contacts_loyalty loyalty")) fromTables += " , contacts_loyalty loyalty ";
						fromTableFilters  += " AND loyalty.user_id = <CONTLTYUSERID> AND loyalty.contact_id=c.cid ";
						
						/*if((fromTables.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.contact_id=totloyal.contact_id"))){
							fromTableFilters  += " AND loyalty.contact_id=totloyal.contact_id ";
						}*/
						/*if((fromTables.contains("totloyal.")) && (!fromTableFilters.contains("loyalty.loyalty_id=totloyal.loyalty_id"))){
							fromTableFilters  += " AND loyalty.loyalty_id=totloyal.loyalty_id ";
						}*/
					}
					if(fieldsArr[1].toLowerCase().trim().startsWith("cd.status")) {
						
						if(!isHaveNotFlag && fieldsArr[3].toLowerCase().trim().contains("notactive")) {

							//fromTables +=promotion_have_not_issued_qury_template;
							isHaveNotFlag=true;
							couponIdStr += " AND cd.status IN('Active','Redeemed')";
							continue;
						}else if(!isHaveNotFlag && !chkPromoflag){
							chkPromoflag=true;
							fromTables +=Constants.DELIMETER_COMMA + promotion_qury_template;
							
							fromTableFilters +=" AND c.cid=cd.contact_id "; //OR c.external_id=cd.redeem_cust_id ) ";
							
						}
								//+ "OR c.email_id=cd.redeem_email_id OR c.mobile_phone=cd.redeem_phn_id)";
					}
					
					/*if(promoflag && fieldsArr[1].toLowerCase().trim().startsWith("cd.status")) {
						fromTables +=Constants.DELIMETER_COMMA + promotion_qury_template;
						 fromTableFilters +=" and c.cid=cd.contact_id ";
						 promoflag=false;
					}
					
					if(fieldsArr[1].toLowerCase().trim().contains("active")) {
						fromTables="";
						fromTables +=promotion_have_not_issued_qury_template;
						isHaveNotFlag=true;
					}*/
					//if
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
									userIdStr, qryType, ignoreEmailStatus, mlBit);
						
							
							
							
						}//if
						else {
							
							String replacedVal = replaceToken(fieldsArr[1].trim(), fieldsArr); 
							if(replacedVal.contains("<EXP>")) {
								//replace expiry value
								Coupons coupon=null;
								try {
									CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName("couponsDao");
									 coupon=couponsDao.getConpounByName(userIdStr, promoName);
									
									if(coupon!=null) {
										String[] expArr = coupon.getExpiryDetails().split(Constants.ADDR_COL_DELIMETER);
										replacedVal = replacedVal.replace("<EXP>",expArr[1]);
									}
									
									//ruleSB.append(valStr);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									
								}
							}
							if(replacedVal.contains("cp.coupon_id") || replacedVal.contains("cd.coupon_id")) {
								couponCondIdStr += (!couponCondIdStr.isEmpty() ? " AND "+replacedVal : replacedVal);
								if(replacedVal.contains("cd.coupon_id") ) {
									//couponIdStr = replacedVal;
									couponIdStr += (!couponIdStr.isEmpty() ? " AND "+replacedVal : replacedVal);
									//replacedVal = "";
								}
								replacedVal = "";
							}
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
								fieldsArr[1].trim().toLowerCase().trim().startsWith("aggr_max") ||
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_count" ) || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_boughtqty") || 
								fieldsArr[1].toLowerCase().trim().startsWith("aggr_returnedqty")) {//TODO no need of another condition 
							
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
								
								
							}else if (fieldsArr[1].toLowerCase().trim().startsWith("date(sal.")
									&& fieldsArr[2].toLowerCase().trim().equals("number:date_diff_equal_to")) { // APP-4201

								if (maxSalesConditionSB.length() > 0)	maxSalesConditionSB.append(" OR ");

								maxSalesConditionSB.append(replacedVal);

							}else if(fieldsArr[1].toLowerCase().trim().startsWith("sal.") ||
									fieldsArr[1].toLowerCase().trim().startsWith("date(sal.") || 
									fieldsArr[1].toLowerCase().trim().startsWith("date(sku.")||
									fieldsArr[1].toLowerCase().trim().startsWith("sku.")) {
								
								
								if(orSalesConditionSB.length() > 0) orSalesConditionSB.append(" OR ");
								
								orSalesConditionSB.append(replacedVal);
								
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("ltytrxch.") ||
									(fieldsArr[1].toLowerCase().trim().startsWith("date(ltytrxch."))) {
								
								//if(orLtyConditionSB.length() > 0) orLtyConditionSB.append("OR");
								//orLtyConditionSB.append(replacedVal);
								//new
								/*if(ltyORHavingSB.length() > 0) ltyORHavingSB.append("OR");
								ltyORHavingSB.append(replacedVal);*/
								if(ltyConditionSB.length() > 0) ltyConditionSB.append(tokenFilterStr);
								
								ltyConditionSB.append("("+replacedVal+")");
							}else if(fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_tot") ||
									fieldsArr[1].trim().toLowerCase().trim().startsWith("aggrlty_avg") || 
									fieldsArr[1].trim().toLowerCase().trim().startsWith("aggrlty_max") || 
									fieldsArr[1].toLowerCase().trim().startsWith("aggrlty_" ) ) {//TODO no need of another condition 
								
									if(ltyORHavingSB.length() > 0) ltyORHavingSB.append("OR");
									
									SegmentEnum selectFieldEnum = SegmentEnum.getEnumByColumn(fieldsArr[1].trim()); 
									if(selectFieldEnum != null) {
										
										replacedVal = replacedVal.replace(fieldsArr[1], selectFieldEnum.getSelectFieldName());
										
									}
								
									ltyORHavingSB.append(replacedVal);
								
							
							
								}//if
							else{
								if(valStr.length() > 0){
									valStr += " OR ";
								}
								valStr += replacedVal ;
							
							}
						}
					}
					if(fieldsArr.length >4 && fieldsArr[4]!=null && !fieldsArr[4].isEmpty()) {
					if(!replaceValueCode.isEmpty()) replaceValueCode += Constants.DELIMETER_COMMA;
					replaceValueCode += "'"+fieldsArr[4]+"'";
					}
				} // for 
				
				if( orHavingSB.length() > 0 ) {
					
					if(HavingSB.length() > 0) HavingSB.append(tokenFilterStr);
					
					HavingSB.append("("+orHavingSB.toString()+")");
					
					
				}//if
				if( ltyORHavingSB.length() > 0 ) {
					
					if(ltyHavingSB.length() > 0) ltyHavingSB.append(tokenFilterStr);
					
					ltyHavingSB.append("("+ltyORHavingSB.toString()+")");
					
					
				}//if
				if(orSalesConditionSB.length() > 0) {
					
					// APP-4227
					if (token.contains("String:only like")) {
						
						// if(deptItemCatSalesConditionSB.length() > 0) deptItemCatSalesConditionSB.append(tokenFilterStr);
						
						// deptItemCatSalesConditionSB.append("("+orSalesConditionSB.toString()+")");
						
						if (deptItemCatSalesConditionSB.length() > 0) {
							String temp = deptItemCatSalesConditionSB.toString();
							deptItemCatSalesConditionSB = new StringBuffer();
							temp = " (" + temp + " OR (" + orSalesConditionSB.toString() + ")) ";
							deptItemCatSalesConditionSB.append(temp);
						} else {
							deptItemCatSalesConditionSB.append("(" + orSalesConditionSB.toString() + ")");
						}
						
					} else {
						
						if (salesConditionSB.length() > 0) salesConditionSB.append(tokenFilterStr);

						salesConditionSB.append("(" + orSalesConditionSB.toString() + ")");
					}
					
				}
				if(orLtyConditionSB.length() > 0) {
					
					if(ltyConditionSB.length() > 0) ltyConditionSB.append(tokenFilterStr);
					
					ltyConditionSB.append("("+orLtyConditionSB.toString()+")");
					//HavingSB.append("("+orLtyConditionSB.toString()+")");
					
				}
				if(csConditionQry.length() > 0) {
					
					if(csConditionSB.length() > 0) csConditionSB.append(tokenFilterStr);
					
					csConditionSB.append("("+csConditionQry.toString()+")");
					
					
				}
				
				/*if(csInnerCondition.length() > 0) {
					
					if(csInnerConditionSB.length() > 0) csInnerConditionSB.append(tokenFilterStr);
					
					csInnerConditionSB.append("("+csInnerCondition+")");
					
					
				}*/
				
				logger.debug(valStr);
				if(valStr == null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr) && !valStr.isEmpty() ) {
					
					valStr = " ("+filterMap.get(keyStr)+") " + tokenFilterStr +" ("+ valStr +") "; 
				}	
				
				
				if(!valStr.isEmpty()) { filterMap.put(keyStr, valStr); }
				
				logger.info("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				boolean isHaving = (isAggr || isltyAggr || chkAggrColumnsFound || chkOpensClicksFound || isCsNotReceivedUnionExist || chkAggrLtyColumnsFound);
				if(isHaving) {
					logger.debug("isHaving ::"+isHaving );
					filterMap.put("GENERAL", "");
				}
				
				else{
					
					return null;
				}
				
			}
			if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.info(">>> NO Cfs  1");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
				String tempStr =null;
				String csNotReceivedUnionQryStr = null;//csNotReceivedUnionQrySB.length() > 0 ? csNotReceivedUnionQrySB.toString() : "";
				
					if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_EXTERNALID)) {
						tempStr = generalSqlWithEmailStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						//TODO
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_totalCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
					}
					else if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						tempStr = email_generalSqlWithEmailStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						//TODO
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_emailCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
					}
					else if(ignoreEmailStatus==true && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						tempStr = mobile_generalSqlWithEmailStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobileCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
					;
					}else if(qryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
						tempStr = notification_generalSqlWithStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_notificationCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
					;
					}					
					else if(ignoreEmailStatus==false && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
						tempStr = Old_generalSqlWithEmailStatusCount.replace("<USERIDS>", userIdStr).
									replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_email_Count_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
						
						
						
					}
					else if(ignoreEmailStatus==false && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
						tempStr = mobile_generalSqlWithMobileStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_mobile_Count_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
						
					
					
					}else if(qryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
						tempStr = notification_generalSqlWithNotificationStatusCount.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
						.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
									replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
									replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
						if(isCsNotReceivedUnionExist ) {
							
							logger.debug("==================entered to replace the PHS =================");
							csNotReceivedUnionQryStr = not_received_notification_Count_status.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"")
													.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
													replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
													replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
										
							
						}
					}
					if(isHaveNotFlag  ) {
						tempStr = generalSqlWithEmailStatusCountForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
						
					}
				  if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
					  tempStr = email_generalSqlWithEmailStatusCountForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
					  
				  }
				 if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
					  tempStr = mobile_generalSqlWithEmailStatusCountForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
					  
				 }
					
				  if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
					  tempStr = email_generalSqlWithEmailStatusCountForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
					  
				  }
				 if(isHaveNotFlag && qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
					  tempStr = mobile_generalSqlWithEmailStatusCountForHavenotIsuued.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"").replace("<COUPONID>", couponIdStr)
								.replace("<MLBITS>", mlBit+"").replace("<AGGRFIELDS>", aggrFieldsSB.toString()).
											replace("<HAVINGSTR>", HavingSB.length()>0 ? " HAVING "+HavingSB.toString() : HavingSB.toString() ).
											replace("<SALESFIELDS>", groupBySB.toString()).replace("<GROUPBYSTR>", groupBySB.length() > 0 ? "GROUP BY "+groupBySB.toString().substring(1) : groupBySB.toString());
					  
				 }
				logger.info("fromTables ::"+fromTableFilters);
				fromTables = fromTables.replace("<SALESUSERID>", userIdStr).replace("<LTYTRXCHUSERID>", userIdStr).
						replace("<SALESFROMTABLES>", salesFromTables).replace("<SALESFROMTABLESFILTERS>", salesFromTableFilters).
						replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
						.replace("<HPUSERID>", userIdStr).replace("<COUPONID>", couponIdStr);
	
					/*if(chkAggrColumnsFound && chkSalesColumnsFound) {
						fromTableFilters  += " AND totSal.cid=sal.cid ";
					}*/
					fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).replace("<LTYTRXCHUSERID>", userIdStr).
					replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
					.replace("<HPUSERID>", userIdStr).replace("<CONTLTYUSERID>", userIdStr);
					
					
					
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
					/*if(chkOpensClicksFound) {
						
						csFromTables =   " , ( " +
										" SELECT DISTINCT contact_id , t.email_id " +
											" FROM( SELECT contact_id, COUNT(email_id) AS cnt , email_id <INNERCOLUMNNAMES> " +
											" FROM campaign_sent WHERE campaign_id IN("+csCampIDs+") " +
											" "+(isLatestCampaigns ? Constants.INTERACTION_CAMPAIGN_CRID_PH :"")+" <CSINNERCONDITION> GROUP BY email_id , campaign_id   ) AS t "+
										" GROUP BY t.email_id " +
										"  <CSCONDITIONQRY>  )o ";//ON o.contact_id=c.cid ";
						
						csTableFilter = " AND o.contact_id=c.cid ";
						
					}*/
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
					logger.debug("salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "+(salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : ""));
					// APP-4227
					if (queryString.contains("String:only like") && deptItemCatSalesConditionSB.length() > 0) {
						String salesDateCond = salesConditionSB.toString();
						if (salesConditionSB.length() > 0) salesConditionSB.append(" AND ");
						salesConditionSB.append(department_and_item_category_sub_query.replace("<DEPT_ITEM_CAT_ONLYLIKE>", deptItemCatSalesConditionSB.toString()));
						
						String temp = salesConditionSB.toString();
						if (salesDateCond.length() > 0 && temp.contains("<SALES_COND>"))
							temp = temp.replace("<SALES_COND>", "AND " + salesDateCond);
						else
							temp = temp.replace("<SALES_COND>", "");
						/*
						if (queryString.contains("number:date_diff_equal_to")) {
							temp = temp.replace("retail_pro_sales sal<RPS_LAST_PURCHASE_DATE_TEMP>", last_purchase_qry_template);
						}*/
						salesConditionSB = new StringBuffer();
						salesConditionSB.append(temp);
						
					}
					tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL") != null && !filterMap.get("GENERAL").isEmpty()	? " AND ("+ filterMap.get("GENERAL") + (!isHaveNotFlag && !couponCondIdStr.isEmpty()? " AND " + couponCondIdStr : "") +") " : "" );
					//APP-4201
					//tempStr = tempStr.replace("<SALESCOND>", (salesConditionSB.length() > 0 && !salesConditionSB.toString().contains("MAX"))? tokenFilterStr + salesConditionSB.toString() : "");
					tempStr = tempStr.replace("<SALESCOND>", salesConditionSB.length() > 0 ? tokenFilterStr + salesConditionSB.toString() : "");
					//tempStr = tempStr.replace("<RPS_LAST_PURCHASE_DATE_COND>", (salesConditionSB.length() > 0 && salesConditionSB.toString().contains("MAX")) ? ((isLastPurchaseDateFound) ? "" : tokenFilterStr) + salesConditionSB.toString() : "");
					tempStr = tempStr.replace("<RPS_LAST_PURCHASE_DATE_COND>", maxSalesConditionSB.length() > 0 ? maxSalesConditionSB.toString() : "");
					tempStr = tempStr.replace("<SALESSELECTFIELDS>", !salesSelectFields.isEmpty() ? Constants.DELIMETER_COMMA+salesSelectFields : "");
					tempStr = tempStr.replace("<SALESGROUPBY>", !salesSelectFields.isEmpty() ? " GROUP BY sal.doc_sid " : "");//replace zero year and first year
					tempStr = tempStr.replace("<LTYTRXCHCOND>", ltyConditionSB.length() > 0 ? tokenFilterStr + ltyConditionSB.toString() : "");
					tempStr = tempStr.replace("<LTYTRXCHSELECTFIELDS>", !ltySelectFields.isEmpty() ? Constants.DELIMETER_COMMA+ltySelectFields : "");
					tempStr = tempStr.replace("<LTYTRXCHGROUPBY>", !ltySelectFields.isEmpty() ? " GROUP BY ltytrxch.loyalty_id " : "");
					tempStr = tempStr.replace("<LTYBALGROUPBY>", !ltySelectFields.isEmpty() ? " GROUP BY ltybalance.loyalty_id " : "");
					tempStr = tempStr.replace("<HAVING>", ltyHavingSB.length() > 0 ? " HAVING " + ltyHavingSB.toString() : "");
					tempStr = tempStr.replace("<RPS_LAST_PURCHASE_DATE_TEMP>", ""); //APP-4201
					
					Calendar currCal = Calendar.getInstance();
					String currMonthDate = (currCal.get(Calendar.MONTH)+1) +"-"+(currCal.get(Calendar.DATE));
					
					String zeroYear  = "0000-"+currMonthDate;
					String firstYear = "0001-"+currMonthDate;
					
					tempStr = tempStr.replace("<ZEROYEAR_DATE>", zeroYear);
					tempStr = tempStr.replace("<FIRSTYEAR_DATE>", firstYear);
					
					tempStr = tempStr.replace("<SALESUSERID>", userIdStr);
					
				finalStr=tempStr;
			}
			else {
				logger.info(">>> with CFs");
				//String[] listIdArr = listIdStr.split(",");
				String[] userIdsArr = userIdStr.split(",");
				
				String tempStr="";
				for (String listId : userIdsArr) {
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
				
				
				
				finalStr = "SELECT * FROM ( " + tempStr + " ) AS tempTab GROUP BY email_id";
				
			} // else
			
//			logger.info("==="+filterMap);
//			logger.info(listIdStr+"=FN="+finalStr);
			}
			else {
				
				finalStr = generateHomePassedQry(queryString, ignoreEmailStatus, qryType, mlBit);
				
			}
		
			/*logger.fatal("Total Time Taken :: SalesQueryGenerator ::  generateListSegmentQuery "
							+ (System.currentTimeMillis() - starttimer));*/
			if(finalStr.contains("<Reward>")) finalStr = finalStr.replace("<Reward>",replaceValueCode);
			logger.info("finalStr-----"+finalStr);
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		//return returnStr;
		
	} // generateQuery
	
	
	

	
	public static String generateHomePassedQry(String queryString, boolean ignoreEmailStatus, String queryType, long mlBit) {
		
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
				 logger.info("TOKEN="+token);
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
				
					
					logger.info("tempToken :: "+tempToken);
					
					
					
					logger.info("table filter  is====>"+fromTableFilters);
					
					String fieldsArr[] = tempToken.split("\\|");
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
						
						if(valStr.length() > 0){
							valStr += " OR ";
						}
						
						String mapToken = fieldsArr[1].toLowerCase().trim();
						
						
							
							valStr += replaceToken(fieldsArr[1].trim(), fieldsArr);
						
					}
					
					
				} // for 
				logger.debug(valStr);
				if(valStr == null) return null;
				//*********** Store Entry in Hash Map *******************
				
				if(filterMap.containsKey(keyStr)) {
					valStr = " ("+filterMap.get(keyStr)+") " + tokenFilterStr +" ("+ valStr +") "; 
				}				
				logger.info("valStr ::"+valStr);
				filterMap.put(keyStr, valStr);
				
				logger.info("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
				
			}// for i
			
			if(filterMap.size()==0) {
				return null;
			}
			else if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.info(">>> NO Cfs  2");
				
				/*String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);*/
					
				String tempStr =null;
				
				if(ignoreEmailStatus == false && queryType.equals(Constants.SEGMENT_ON_EMAIL)) {
					
					tempStr = Old_generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
					fromTables += " , homespassed h ";
					fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
				}else  if(ignoreEmailStatus == false && queryType.equals(Constants.SEGMENT_ON_MOBILE)) {
					
					tempStr = mobile_generalSqlWithEmailStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");;
					fromTables += " , homespassed h ";
					fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
				}else  if(ignoreEmailStatus == false && queryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
					
					tempStr = notification_generalSqlWithNotificationStatus.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");;
					fromTables += " , homespassed h ";
					fromTableFilters  += " AND h.user_id in(<HPUSERID>) AND c.hp_id=h.address_unit_id "; 
				}
				else {
					
					tempStr = qry_HomePassed.replace("<USERIDS>", userIdStr);
					
				}
				
					
				//logger.info("fromTables ::"+fromTableFilters);
				fromTables = fromTables.replace("<SALESUSERID>", userIdStr).
						replace("<SKUUSERID>", userIdStr).replace("<SALESAGGRUSERID>", userIdStr)
						.replace("<HPUSERID>", userIdStr);
	
				fromTableFilters = fromTableFilters.replace("<SALESUSERID>", userIdStr).replace("<SALESUSERID>", userIdStr).replace("<HPUSERID>", userIdStr);
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
				tempStr = tempStr.replace("<SALESUSERID>", userIdStr);
				finalStr=tempStr;
			}
			
			return finalStr;
//		logger.info("==="+filterMap);
//		logger.info(listIdStr+"=FN="+finalStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
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
				// logger.info("TOKEN="+token);
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
				
					
					logger.info("tempToken :: "+tempToken);
					String fieldsArr[] = tempToken.split("\\|");
					if(fieldsArr.length < 4) {
						logger.info("Invalid token="+token);
						continue;
					}
					
					String keyStr=null;
					
					
					if(fieldsArr[1].startsWith("CF:")) {
						logger.info("in if");
						String[] cfTokenArr = fieldsArr[1].split(":");
						keyStr = cfTokenArr[1].trim();
						valStr = replaceToken(cfTokenArr[2].trim(), fieldsArr);
					} 
					else {
						logger.info("in else");
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
					logger.info("valStr ::"+valStr);
					filterMap.put(keyStr, valStr);
					
					
					
				}
				
				logger.info("filterMap :: "+filterMap);
				
				
				//*******************************************************
				
			}// for i
			
			if(filterMap.size()==0) {
				return null;
			}
			else if(filterMap.size()==1 && filterMap.containsKey("GENERAL")) {
				logger.info(">>> NO Cfs 3");
				
				String tempStr = (ignoreEmailStatus==true) ?
					generalSqlWithEmailStatus.replace("<LISTIDS>", listIdStr)
					: generalSql.replace("<LISTIDS>", listIdStr);
				
				tempStr = tempStr.replace("<COND>", filterMap.get("GENERAL"));
				finalStr=tempStr;
			}
			else {
				logger.info(">>> with CFs");
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
			
//			logger.info("==="+filterMap);
//			logger.info(listIdStr+"=FN="+finalStr);
			return finalStr;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return returnStr;
		
	
		
	}

	public static String prepareValueStr(String ValToBePrepare, String type) {
		
		String valStr = "";
		String[] strArr = ValToBePrepare.split(",");
		for (String valToken : strArr) {
			
			
			if(valStr.length() > 0) valStr += ",";
			
			if(type.equalsIgnoreCase("string")) {
				valStr += "\"" + valToken.trim() + "\"";
			}
			else if(type.equalsIgnoreCase("number") ){

				valStr += valToken.trim();
				
			}
		}
		
		return valStr;
		
	}

	// APP-4227
	public static String prepareLikeValueStr(String ValToBePrepare, String fieldName) {
		String valStr = "";
		String[] strArr = ValToBePrepare.split(",");
		for (int index = 0; index < strArr.length; index++) {
			if (index != 0) valStr += " AND ";
			
			String valToken = strArr[index].trim();
			valStr += fieldName + " not like \"%" + valToken + "%\" ";
		}
		return valStr;
	}
	
	 /**
	 * Converts to the SQL Query form
	 * @param fieldName
	 * @param tokenArr
	 * @return
	 */
	public static String replaceToken(String fieldName, String[] tokenArr, boolean fromEventTriggers) {
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
					token.contains("before_between") || token.contains("after_between") || token.contains("range_between") || token.contains("number:date_diff_equal_to")) fieldName = "";
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
			
			String notDateStr = "";
			String DVal = "";
			
			String token = tokenArr[2].toLowerCase().trim();
			String value = tokenArr[3].trim();
			
			if( value.contains(";=;")) { // value contains id and name of the object
				value=value.split(";=;")[1];
			}
			
			if(fieldName.contains("<DVal>")) {
				String fieldNameVal = fieldName;
				fieldName = fieldNameVal.split("<DVal>")[0];
				DVal = fieldNameVal.split("<DVal>")[1];
			}
			
			//APP-4227
			if(token.contains("string:only like")) {
				value = prepareLikeValueStr(value, fieldName);
			}
			if(token.contains("is in") || token.contains("is not in")) {
				//if(.equalsIgnoreCase("string"))
				
				if(fieldName.equalsIgnoreCase("c.zip")) {
					value = prepareValueStr(value, "string");
				}else{
					
					value = prepareValueStr(value, token.split(":")[0]);
				}
				
				
			}
			if(token.contains("_") && (token.contains("withinlast") ||token.contains("iswithinnext") ||token.contains("iseqiywithinnext")|| token.contains("iseqwithinnext"))){
				token = token.substring(0, token.indexOf("_"));
			}
			logger.info("token ::"+token);
			
			if(tokenArr[1].contains("cp.coupon_expiry_date")) token = ("date:iswithinnextS");
			String mapStr = tokensHashMap.get(token);
			mapStr = mapStr.replace("<Dval>", DVal);
			mapStr = mapStr.replace("<FIELD>", fieldName);
			
			if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast") ||
					token.contains("before_between") || token.contains("after_between") || 
					token.contains("range_between") || (token.contains("iswithinnext")) ||token.contains("isiy") ||token.contains("ise") || 
					token.contains("number:date_diff_equal_to") || token.contains("string:only like")) 
				fieldName = "";
			// logger.info(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

			if (mapStr==null) {
				logger.info("MAP Entry not found:"+tokenArr[2].toLowerCase().trim());
				return null;
			}
			
			if(fieldName.toLowerCase().trim().startsWith("aggrlty_") || fieldName.toLowerCase().trim().startsWith("date(ltytrxch.")){
				if(token.toLowerCase().startsWith("date:notbefore") || 
						 token.toLowerCase().startsWith("date:notafter")){
					
					 notDateStr = lty_not_purchase_qry_template;
				}
			}else{
				 if(token.toLowerCase().startsWith("date:notbefore") || 
						 token.toLowerCase().startsWith("date:notafter")){
					
					 notDateStr = sales_not_purchase_qry_template;
				}
			}
			String tempStr="";
				if(value.contains("notactive")) value="('Active')"; 
				if (fieldName.startsWith("sku.") && value.contains("'")) {
					value = value.replace("'", "\\'");
				}
				tempStr = mapStr.replace("<VAL1>", value );
				if(mapStr.contains("<DDval1> "))
					tempStr = mapStr.replace("<DDval1>", value );
			String opensClicksSubCond = "";
			
			/*if(fieldName.trim().startsWith("cs.")) {
				if(tokenArr.length>5) {
					tempStr = tempStr.replace("<VAL2>", tokenArr[4].trim());
					opensClicksSubCond = " cs.campaign_id in("+tokenArr[5].trim()+") AND ";
					
				}else if(tokenArr.length>4) {
					
					opensClicksSubCond = " cs.campaign_id in("+tokenArr[4].trim()+") AND ";
					
				}
				
				
			}*/
			
			if(tokenArr.length>4 && tempStr.contains("<VAL2>")) {
				
					tempStr = tempStr.replace("<VAL2>", tokenArr[4].trim());
				
			}
			
			if(token.contains("date:ignoreyear_")) outStr=" ("+opensClicksSubCond + tempStr +") ";
			else if (token.contains("number:date_diff_equal_to")) outStr= opensClicksSubCond + tempStr;
			else if (token.contains("string:only like")) outStr = " ("+ tempStr +") OR "+tokenArr[1] + " is NULL "; //APP-4227
			else outStr=" ("+opensClicksSubCond + fieldName+ tempStr +") ";
			
			outStr =  !notDateStr.isEmpty() ? notDateStr.replace("<SALESCOND>", outStr) : outStr.trim();
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
			logger.error("Exception ::" , e);
		}
		logger.info("outStr ::"+outStr);
		
		return outStr;
	}
	
	public static String prepareDateSubQuery(String fieldName, String[] tokenArr, String userIdStr,
			String qryType, boolean ignoreEmailStatus, long mlBit) {
		
		String fromTables = "";
		String fromTableFilters = "";
		
		String subQry = "c.cid NOT IN(<SUBQRY>)";
		String tempStr =null;
		
		if(ignoreEmailStatus==true){
			
			if( qryType.equals(Constants.SEGMENT_ON_EXTERNALID)) {
				tempStr = tot_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			else if(qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
				tempStr = email_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			else if(qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
				tempStr = mobile_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
				
			}else if(qryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
				tempStr = notification_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			
		
		}
		else if(ignoreEmailStatus == false) {
			
			
			if(qryType.equals(Constants.SEGMENT_ON_EMAIL)) {
				tempStr = general_email_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			else if(qryType.equals(Constants.SEGMENT_ON_MOBILE)) {
				tempStr = general_mobile_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			else if(qryType.equals(Constants.SEGMENT_ON_NOTIFICATION)) {
				tempStr = general_notification_subQuery.replace("<USERIDS>", userIdStr).replace("<MLBITS>", mlBit+"");
			}
			
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
			fromTableFilters  += " AND loyalty.user_id=<CONTLTYUSERID> AND loyalty.contact_id=c.cid"; 
			
		}//if
		
		//logger.info("found sales flag........."+tempToken.toLowerCase().trim().startsWith("Date(sal."));
		else if(	(fieldName.toLowerCase().trim().startsWith("sal.") || fieldName.toLowerCase().trim().startsWith("date(sal."))) {
			
			fromTables +=  " , retail_pro_sales sal ";
			fromTableFilters  += " AND sal.user_id in(<SALESUSERID>)  AND sal.cid IS NOT NULL AND c.cid=sal.cid";  
		} // if
		else if(	(fieldName.toLowerCase().trim().startsWith("ltytrxch.") )) {
			
			fromTables +=  " , loyalty_transaction_child ltytrxch ";
			fromTableFilters  += " AND ltytrxch.user_id in(<SALESUSERID>)  AND ltytrxch.contact_id IS NOT NULL AND c.cid=ltytrxch.contact_id";
			//fromTableFilters  += " AND ltytrxch.user_id in(<SALESUSERID>) ";
		} else if(	(fieldName.toLowerCase().trim().startsWith("date(ltytrxch."))) {
			
			fromTables +=  " , loyalty_transaction_child ltytrxch ";
			fromTableFilters  += " AND ltytrxch.user_id in(<SALESUSERID>) AND c.cid=ltytrxch.contact_id";
			//fromTableFilters  += " AND ltytrxch.user_id in(<SALESUSERID>) ";
		}
		
		/*else if(( fieldName.toLowerCase().trim().startsWith("cs.") || fieldName.toLowerCase().trim().startsWith("date(cs."))) {
			
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
		//logger.debug("tokenArr[1] ::"+token);
		
		if(token.contains("_") && token.contains("withinlast") ){
			token = token.substring(0, token.indexOf("_"));
		}
		String mapStr = tokensHashMap.get(token);
		mapStr = mapStr.replace("<FIELD>", fieldName);
		
		//if(token.equalsIgnoreCase("date:istoday") || token.contains("withinlast")) fieldName = "";
		// logger.info(tokenArr[1].toLowerCase().trim()+"::MAPSTR::"+mapStr);

		if (mapStr==null) {
			logger.info("MAP Entry not found:"+token);
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
