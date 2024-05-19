package org.mq.optculture.data.dao;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jasper.tagplugins.jstl.core.Catch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionChildDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate; 

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionChildDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyTransactionChild loyaltyTransactionChild) {
		super.saveOrUpdate(loyaltyTransactionChild);
	}*/
	
	/**
	 * It fetches all the transactions of the points activation date between given dates;
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws LoyaltyProgramException
	 */
	/*public List<LoyaltyProgramTrans> fetchActiveTransOftheHour(Calendar startTime, Calendar endTime) throws LoyaltyProgramException{
		List<LoyaltyProgramTrans> transList = null;
		try{
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " ptsActivationDate BETWEEN "+startTime+" AND "+endTime;
			
			transList  = (List<LoyaltyProgramTrans>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchActiveTransOftheHour dao >>>");
			throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return transList;
	}*/
	  public LoyaltyTransactionChild findTransactionByTrxId(long transChildId) {
	    	try {
	    		List<LoyaltyTransactionChild> list = null;
				list = executeQuery("from LoyaltyTransactionChild where transChildId = " + transChildId);
				
				if(list.size() >0) return list.get(0);
				else return null;
				
			} catch (DataAccessException e) {
				logger.error("Exception ::" , e);
				return null;
			}
	    }
	
	public List<LoyaltyTransactionChild> fetchCurrentInActiveTrans(String earnStatus, String date, String amountType){
		
		List<LoyaltyTransactionChild> transList = null;
		try{
			
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//String date = format.format(cal.getTime());
			
			/*String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " AND valueActivationDate <= '"+date+"' AND status = 'New'";*/
			
			String queryStr = " FROM LoyaltyTransactionChild WHERE (transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR"
								+ " transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"')"     
								+ " AND valueActivationDate = '"+date+"' AND earnStatus = '"+earnStatus+"'";
			logger.info(" Child transactions queryStr = "+queryStr);
			transList  = (List<LoyaltyTransactionChild>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentInActiveTrans dao >>>", e);
			//throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return transList;
		
	}
	
	public List<LoyaltyProgramTrans> fetchCurrentActiveTransForExpiry(Calendar cal){
		
		List<LoyaltyProgramTrans> transList = null;
		try{
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-mm HH:mm::ss");
			String date = format.format(cal.getTime());
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'Active' "
					+ " AND type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"     
					+ " AND valueExpirationDate <= '"+date+"' AND expiryStatus = 'New'";
			
			transList  = (List<LoyaltyProgramTrans>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentActiveTransForExpiry dao >>>");
		}
		return transList;
		
	}
	
	/*public List<Object[]> findTotTransactionsRateforAll(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		
		if(transType == null) {
			subQry += " AND transactionType in ('Enrollment','Issuance','Redemption','Inquiry','Transfer','Bonus','Adjustment')";
			
			}
	
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(transChildId), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(transChildId), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(transChildId), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		
			return  getHibernateTemplate().find(qry);
	}*/
	public List<Object[]> findTotTransactionsRateforAll(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		
		if(transType == null) {
			subQry += " AND transactionType in ('Enrollment','Issuance','Return','Redemption','Transfer','Inquiry','Bonus','Adjustment','Tier Adjustment')";
			
			}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(transChildId), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(transChildId), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(transChildId), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		
			return  getHibernateTemplate().find(qry);
	}
	
	/*public List<Object[]> findTotTransactionsRateforReturn(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		
		if(transType == null) {
			subQry += " AND transactionType in ('Return')";
			
			}
        if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(DISTINCT docSID), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(DISTINCT docSID), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(DISTINCT docSID), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		
			return  getHibernateTemplate().find(qry);
	}*/
	public List<Object[]> findTotTransactionsRateforReturn(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		
		if(transType == null) {
			logger.info(" inside return type transaction ");
			subQry += " AND transactionType in ('Return')";
			
			}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
        if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(DISTINCT docSID), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(DISTINCT docSID), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(DISTINCT docSID), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		logger.info("Return query "+qry);
			return  getHibernateTemplate().find(qry);
	}
	
	/*public List<Object[]> findTotTransactionsRate(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"+
						  " enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		if(transType.equalsIgnoreCase("Returns") || transType.equalsIgnoreCase("StoreCredit"))
		{
			if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(DISTINCT docSID), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(DISTINCT docSID), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(DISTINCT docSID), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		}else{
		if(typeDiff.equalsIgnoreCase("days")) { 
			qry="SELECT COUNT(transChildId), DATE(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
		}else if(typeDiff.equalsIgnoreCase("months")) {
			qry="SELECT COUNT(transChildId), MONTH(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
		}
		else if(typeDiff.equalsIgnoreCase("years")) {
			qry="SELECT COUNT(transChildId), YEAR(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
		}
		}
			
		return  getHibernateTemplate().find(qry);
	}*/
	public List<Object[]> findTotTransactionsRate(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"+
						  " enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String qry = "" ;
		/*if(transType.equalsIgnoreCase("Returns") || transType.equalsIgnoreCase("StoreCredit"))
		{
			if(typeDiff.equalsIgnoreCase("days")) { 
				qry="SELECT COUNT(DISTINCT docSID), DATE(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
			}else if(typeDiff.equalsIgnoreCase("months")) {
				qry="SELECT COUNT(DISTINCT docSID), MONTH(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
			}
			else if(typeDiff.equalsIgnoreCase("years")) {
				qry="SELECT COUNT(DISTINCT docSID), YEAR(createdDate) FROM LoyaltyTransactionChild " +
						" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
						" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
						" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
			}
		}else{*/
		if(typeDiff.equalsIgnoreCase("days")) { 
			qry="SELECT COUNT(transChildId), DATE(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
		}else if(typeDiff.equalsIgnoreCase("months")) {
			qry="SELECT COUNT(transChildId), MONTH(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
		}
		else if(typeDiff.equalsIgnoreCase("years")) {
			qry="SELECT COUNT(transChildId), YEAR(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
		}
			
		return  getHibernateTemplate().find(qry);
	}
	public String getAllDestCards(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, Long cardsetId,String employeeIdStr) {

		String subQry = "";
		if(key != null){

			subQry += " AND membershipNumber LIKE '%"+key+"%'";
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"+
						  " enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
		String qry="SELECT DISTINCT loyaltyId FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
				" ORDER BY date(createdDate)";

		

		List tempList = getHibernateTemplate().find(qry);
		String destCards = Constants.STRING_NILL;
		if(tempList != null && tempList.size()>0){
			for(Object each:tempList){
				destCards += destCards.length() > 0? ", ":"";
				destCards += each.toString();
			}
		}
		return destCards;	

	}
	public int getAllTransactionsCount(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String destLtyIds,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(key != null){

			subQry += " AND ( membershipNumber LIKE '%"+key+"%' OR transferedTo in ('"+destLtyIds+"') )";//APP - 1392
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"+
						  " enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null){
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
				" ORDER BY date(createdDate)";

		

		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;

	}
	
	public int getAllTransactionsCount(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String employeeIdStr,Long tierId) {

		String subQry = "";
		if(key != null){

			subQry += " AND membershipNumber LIKE '%"+key+"%'";
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"+
						  " enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
				" ORDER BY date(createdDate)";

		

		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;

	}
	
	
	public List<Object[]> getAllTransactions(Long userId,
			Long prgmId, String startDateStr, String endDateStr, int firstResult, int size,String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String destLtyIds,String employeeIdStr,Long tierId) {
		String subQry = "";
		if(key != null &&key.trim().length()>0 ){
			subQry += "  AND  cl.card_number LIKE '%"+key.trim()+"%' ";
			 if(destLtyIds!=null && destLtyIds.trim().length()>0){
				 subQry="";
				 subQry += "  AND  (cl.card_number LIKE '%"+key.trim()+"%' OR tc.transfered_to in ("+destLtyIds+") ) ";
			 }
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"
						+ " tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND tc.transaction_type ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND tc.store_number in ("+storeNo+")";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND tc.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND tc.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tc.tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND tc.employee_id in ("+employeeIdStr+")";
		}
		
		/*String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY createdDate DESC";*/
		String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to,tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id, tc.subsidiary_number," +
				" cl.membership_status,cl.contact_id,tc.receipt_number FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";
		
		List<Object[]> tempList = null;
		
		tempList= jdbcTemplate.query(qry+" LIMIT "+firstResult+", "+size, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[26];
	        	
	        	object[0] = rs.getString(1); //card_number
	        	object[1] = rs.getLong(2); //trans_child_id
	        	object[2] = rs.getString(3); //store_number
	        	object[3] = rs.getString(4); //transaction_type
	        	object[4] = rs.getString(5); //entered_amount_type
	        	object[5] = rs.getString(6) !=null ?rs.getDouble(6):null; //entered_amount
	        	object[6] = rs.getDouble(7); //excluded_amount
	        	object[7] = rs.getDouble(8); //amount_balance
	        	object[8] = rs.getDouble(9); //gift_balance
	        	object[9] = rs.getLong(10); //points_balance
	        	object[10] = rs.getString(11); //created_date
	        	object[11] = rs.getDouble(12); //hold_points_balance
	        	object[12] = rs.getDouble(13); //hold_currency_balance
	        	object[13] = rs.getString(14); // earn_type
	        	object[14] = rs.getString(15);//description2
	        	object[15] = rs.getString(16);//transfered_to
	        	object[16] = rs.getString(17);//description
	        	object[17] = rs.getString(18); //amount_difference
	        	object[18] = rs.getString(19); //gift_difference
	        	object[19] = rs.getString(20); //points_difference
	        	object[20] = rs.getString(21); //total_loyalty_earned
	        	object[21] = rs.getString(22); //tier_id
	        	object[22] = rs.getString(23); //subsidiary_number
	        	object[23] = rs.getString(24); //membership status
	        	object[24] = rs.getLong(25); //contact id
	        	object[25] = rs.getString(26);//receipt_number APP-3192
//	        	object[1] = rs.getString(2); //
	        	return object;
	        }
	        
		});
		
		return tempList;
		
		/*List<Object[]> tempList = executeQuery(qry, firstResult, size);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;*/

//		return executeQuery(qry, firstResult, size);
	}
	/*public List<Object[]> getAllTransactions(Long userId,
			Long prgmId, String startDateStr, String endDateStr, int firstResult, int size,String key, String transType, String storeNo, Long cardsetId,String employeeIdStr,Long tierId ) {
		String subQry = "";
		if(key != null){
//			subQry += " AND membershipNumber LIKE '%"+key+"%'";
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"
						+ " tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND tc.transaction_type ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND tc.store_number in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND tc.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tc.tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND tc.employee_id in ("+employeeIdStr+")";
		}
		
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY createdDate DESC";
		String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to, tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id " +
				" FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";
		String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to, tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id,tc.subsidiary_number " +
				" FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";
		
		logger.info("getAllTransactions query ::::::"+ qry);
		
		List<Object[]> tempList = null;
		
		tempList= jdbcTemplate.query(qry+" LIMIT "+firstResult+", "+size, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[23];
	        	
	        	object[0] = rs.getString(1); //card_number
	        	object[1] = rs.getLong(2); //trans_child_id
	        	object[2] = rs.getString(3); //store_number
	        	object[3] = rs.getString(4); //transaction_type
	        	object[4] = rs.getString(5); //entered_amount_type
	        	object[5] = rs.getString(6) !=null ?rs.getDouble(6):null; //entered_amount
	        	object[6] = rs.getDouble(7); //excluded_amount
	        	object[7] = rs.getDouble(8); //amount_balance
	        	object[8] = rs.getDouble(9); //gift_balance
	        	object[9] = rs.getLong(10); //points_balance
	        	object[10] = rs.getString(11); //created_date
	        	object[11] = rs.getDouble(12); //hold_points_balance
	        	object[12] = rs.getDouble(13); //hold_currency_balance
	        	object[13] = rs.getString(14); // earn_type
	        	object[14] = rs.getString(15);//description2
	        	object[15] = rs.getString(16);//transfered_to
	        	object[16] = rs.getString(17);//description
	        	object[17] = rs.getString(18); //amount_difference
	        	object[18] = rs.getString(19); //gift_difference
	        	object[19] = rs.getString(20); //points_difference
	        	object[20] = rs.getString(21); //total_loyalty_earned
	        	object[21] = rs.getString(22); //tier_id
	        	object[22] = rs.getString(23); //subsidiary_number
//	        	object[1] = rs.getString(2); //
	        	return object;
	        }
	        
		});
		
		return tempList;
		
		List<Object[]> tempList = executeQuery(qry, firstResult, size);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;

//		return executeQuery(qry, firstResult, size);
	}*/
	
	public List<Object[]> getAllTransactions(Long userId,
			Long prgmId, String startDateStr, String endDateStr, int firstResult, int size,String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId,String employeeIdStr,Long tierId ) {
		String subQry = "";
		if(key != null){
//			subQry += " AND membershipNumber LIKE '%"+key+"%'";
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"
						+ " tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND tc.transaction_type ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND tc.store_number in ("+storeNo+")";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND tc.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND tc.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tc.tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND tc.employee_id in ("+employeeIdStr+")";
		}
		
		
		/*String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY createdDate DESC";*/
		/*String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to, tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id " +
				" FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";*/
		String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to, tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id,tc.subsidiary_number, " +
				"cl.membership_status,cl.contact_id,tc.receipt_number FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";
		
		logger.info("getAllTransactions query ::::::"+ qry);
		
		List<Object[]> tempList = null;
		
		tempList= jdbcTemplate.query(qry+" LIMIT "+firstResult+", "+size, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[26];
	        	
	        	object[0] = rs.getString(1); //card_number
	        	object[1] = rs.getLong(2); //trans_child_id
	        	object[2] = rs.getString(3); //store_number
	        	object[3] = rs.getString(4); //transaction_type
	        	object[4] = rs.getString(5); //entered_amount_type
	        	object[5] = rs.getString(6) !=null ?rs.getDouble(6):null; //entered_amount
	        	object[6] = rs.getDouble(7); //excluded_amount
	        	object[7] = rs.getDouble(8); //amount_balance
	        	object[8] = rs.getDouble(9); //gift_balance
	        	object[9] = rs.getLong(10); //points_balance
	        	object[10] = rs.getString(11); //created_date
	        	object[11] = rs.getDouble(12); //hold_points_balance
	        	object[12] = rs.getDouble(13); //hold_currency_balance
	        	object[13] = rs.getString(14); // earn_type
	        	object[14] = rs.getString(15);//description2
	        	object[15] = rs.getString(16);//transfered_to
	        	object[16] = rs.getString(17);//description
	        	object[17] = rs.getString(18); //amount_difference
	        	object[18] = rs.getString(19); //gift_difference
	        	object[19] = rs.getString(20); //points_difference
	        	object[20] = rs.getString(21); //total_loyalty_earned
	        	object[21] = rs.getString(22); //tier_id
	        	object[22] = rs.getString(23); //subsidiary_number
	        	object[23] = rs.getString(24); //membership status
	        	object[24] = rs.getLong(25); //contact id
	        	object[25] = rs.getString(26);//receipt_number APP-3192
//	        	object[1] = rs.getString(2); //
	        	return object;
	        }
	        
		});
		
		return tempList;
		
		/*List<Object[]> tempList = executeQuery(qry, firstResult, size);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;*/

//		return executeQuery(qry, firstResult, size);
	}
	
	/*public Double getLifeTimeLoyaltyPurchaseValue(Long userId, Long prgmId, Long loyaltyId){
		
		String subQry = "";
		if(loyaltyId != null){

			subQry += " AND (loyalty_id = "+loyaltyId+" or transfered_to = "+loyaltyId+")";
		}
			
		String qry="SELECT SUM(IF(entered_amount_type='issuancereversal', -description, entered_amount)) FROM loyalty_transaction_child " +
				" WHERE user_id = " + userId.longValue() + subQry +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type not in('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"', '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"', '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_GIFT+"')"; 
		logger.info("The query:"+qry);
		
		Double count = jdbcTemplate.queryForObject(qry, Double.class);	
		logger.info("qry==>"+qry);
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;
		String qry="SELECT SUM(CASE WHEN enteredAmountType='issuancereversal' THEN CONVERT(description, UNSIGNED) ELSE enteredAmount END) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND transactionType in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"; 
		logger.info("The query:"+qry);
		
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
		
		
	
	}
*/
 public Double getCumulativePoints(Long userId, Long prgmId, Long loyaltyId, String startDate, String endDate) {
	
		String subQry = "";
		if(loyaltyId != null){
	
			//subQry += " AND (loyalty_id = "+loyaltyId+" or transfered_to = "+loyaltyId+")";
			subQry += " AND (loyalty_id = "+loyaltyId+") ";// or transfered_to = "+loyaltyId+")";
		}
		String query="SELECT SUM(IF(entered_amount_type='issuancereversal' ,points_difference, IF(entered_amount_type='Reward',0,earned_points)))"
				+ " FROM loyalty_transaction_child " +//Changes APP-1998 
				" WHERE user_id = " + userId.longValue() + subQry +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type !='" +OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT +"'"+
				" AND created_date >'" + startDate + "' AND created_date <='"+ endDate + "'";  
		//logger.info("The query:"+query);

		logger.info("findAmountIssued ...query >>>> .."+query);
		/*List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;*/
		Double count = jdbcTemplate.queryForObject(query, Double.class);	 
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;
	}
	public Double getLoyaltyCumulativePurchase(Long userId, Long prgmId, Long loyaltyId, String startDate, String endDate){//APP-4728
		
		String subQry = "";
		/*if(loyaltyId != null){

			subQry += " AND (loyalty_id = "+loyaltyId+")";// or transfered_to = "+loyaltyId+")";
		}*/
		
		String qry="SELECT SUM(IF(entered_amount_type='issuancereversal' ,-description, IF(entered_amount_type='Reward',0,entered_amount)))"
				+ " FROM loyalty_transaction_child " +//Changes APP-1998 
				" WHERE user_id = " + userId.longValue() + " AND loyalty_id = "+loyaltyId+" " +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type !='" +OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT +"'"+
				" AND created_date <='" + startDate + "' AND created_date >='"+ endDate + "'";  
		logger.info("The query:"+qry);
				
		Double count = jdbcTemplate.queryForObject(qry, Double.class);	 
	 	logger.info("Sum===>:"+count);
	 	count = count != null ? count : 0.0;
	 	
	 	/*if(loyaltyId != null){

			subQry = " AND (transfered_to = "+loyaltyId+")";// or transfered_to = "+loyaltyId+")";
		}*/
		
		 qry="SELECT SUM(IF(entered_amount_type='issuancereversal' ,-description, IF(entered_amount_type='Reward',0,entered_amount)))"
				+ " FROM loyalty_transaction_child " +//Changes APP-1998 
				" WHERE user_id = " + userId.longValue() + " AND transfered_to = "+loyaltyId+" " +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type !='" +OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT +"'"+
				" AND created_date <='" + startDate + "' AND created_date >='"+ endDate + "'";  
		logger.info("The query:"+qry);
		Double count1 = jdbcTemplate.queryForObject(qry, Double.class);	 
	 	logger.info("Sum===>:"+count);
		count += count1 != null ? count1 : 0.0;
	 	
	 		return count;
			
		/*String qry="SELECT SUM(CASE WHEN enteredAmountType='issuancereversal' THEN -description ELSE enteredAmount END) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND transactionType in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"') AND createdDate <='" + startDate + "' AND createdDate >='"+ endDate + "'"; 
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;*/
	
	}
	public int getIssuanceCount(Long programId,Long userId) {//APP-4728

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() + " AND userId="+userId+" " +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	public int getRedemptionCount(Long programId,Long userId) {//APP-4728

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() + " AND userId="+userId+" " +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	public int getReversalCount(Long programId,Long userId) {//APP-4728 add user id

		/*String query="SELECT COUNT(DISTINCT docSID) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"'" +
				" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
				" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";*/
		
		String query="SELECT COUNT(docSID) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() + " AND userId="+userId+" " +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"'" +
				" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
				" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
          logger.info("reversal count:"+query);
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	public int getStoreCreditCount(Long programId,Long userId) {//APP-4728

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() + " AND userId="+userId+" "+
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"'" +
				" AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	public Long findTotGiftIssuance(Long userId,String startDate,String endDate) {
		
		String query  = " SELECT count(transChildId)  FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND userId = " + userId  + " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' " +
				" AND createdDate BETWEEN '"+startDate+"' AND '"+endDate+"'  ";


		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).longValue();
		else
			return 0L;
	}

	
	/*public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId, String type,String employeeIdStr,Long tierId) {
			String subQry = "";
			if(type.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(type.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
		
		String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
	}*/
	public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId, String type,String employeeIdStr,Long tierId) {
		String subQry = "";
		if(type.equalsIgnoreCase("loyaltyIssuance")) {
			subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
		}
		else if(type.equalsIgnoreCase("giftIssuance")) {
			subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
	
	String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List<Object[]> tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0) {
		return tempList.get(0);
	}
	else
		return null;
}
	
	public Object[] getReversalTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
		/*String query  = " SELECT count(DISTINCT docSID), SUM(amountDifference), SUM(pointsDifference) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
				" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
				" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')"+
				" AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";*/
		
			String query  = " SELECT count(docSID), SUM(amountDifference), SUM(pointsDifference) FROM LoyaltyTransactionChild " +
					" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
					" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
					" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')"+
					" AND programId = " + prgmId.longValue() + subQry +
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);
	     if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}
	
	public Object[] getIssRedReversalTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId, String enteredAmountType) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			if(enteredAmountType != null && enteredAmountType.length() != 0){
				subQry += " AND enteredAmountType = '"+ enteredAmountType + "'";
			}
		/*String query  = " SELECT count(DISTINCT docSID), SUM(amountDifference), SUM(pointsDifference) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
				" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
				" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')"+
				" AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";*/
		
			String query  = " SELECT count(docSID), SUM(amountDifference), SUM(pointsDifference) FROM LoyaltyTransactionChild " +
					" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
					" AND programId = " + prgmId.longValue() + subQry +
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);
	     if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}
	
	/*public Object[] getStoreCreditTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
		String query  = " SELECT count(transChildId), SUM(earnedAmount) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
				" AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' "+
				" AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}*/
	public Object[] getStoreCreditTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
		String query  = " SELECT count(transChildId), SUM(earnedAmount) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "'"+
				" AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' "+
				" AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}

	public Object[] getRedemptionTransAmt(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
		String query  = " SELECT count(transChildId), SUM(enteredAmount) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + "' AND programId = " + prgmId.longValue() +
				" AND enteredAmountType ='"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM +"' "+ subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
	}

	public Object[] getRedemptionTransPts(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String query  = " SELECT count(transChildId), SUM(enteredAmount) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + "' AND programId = " + prgmId.longValue() +
				" AND enteredAmountType ='"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM +"' "+ subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
	}
	
	/*public Object[] getBonusTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		
		String subQry = "";
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
	
	String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_BONUS + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List<Object[]> tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0) {
		return tempList.get(0);
	}
	else
		return null;
   }*/
	public Object[] getBonusTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}

		String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_BONUS + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0) {
			return tempList.get(0);
		}
		else
			return null;
	}

	/*public Object[] getAdjustmentTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
	
	String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List<Object[]> tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0) {
		return tempList.get(0);
	}
	else
		return null;
   }*/
	public Object[] getAdjustmentTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
	
	String query  = " SELECT count(transChildId), SUM(earnedAmount), SUM(earnedPoints) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List<Object[]> tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0) {
		return tempList.get(0);
	}
	else
		return null;
   }
public Long findTotalOCLoyaltyOptins(long userId,String startDate, String endDate) {
	  
	String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT + "' AND userId = " + userId + 
			" AND createdDate BETWEEN '"+startDate+"' AND '"+endDate+"'  ";
//	  logger.info("query for loyalty Opt-in is..."+query);
	  List tempList = getHibernateTemplate().find(query);
	  
	  if(tempList != null && tempList.size() >0) {
		
		  return ((Long) tempList.get(0)).longValue();
	  }else return null;
	  
//	 return((Long) (getHibernateTemplate().find(qury).get(0))).longValue();
}
	/*public int getEnrollementTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}*/
public Object[] getTrxSummery(Long userID, Long prgmId, String startDateStr,
		String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId){
	
	String subQry = "";

	if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
		subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
	}
	if(storeNo != null && storeNo.length() != 0) {
		subQry += " AND storeNumber in ("+storeNo+")";
	}
	if(cardsetId != null) {
		subQry += " AND cardSetId =" + cardsetId.longValue();
	}
	if(tierId != null) {
		subQry += " AND tierId =" + tierId.longValue();
	}
	if(employeeIdStr != null && employeeIdStr.length() != 0 ){
		subQry += " AND employeeId in ("+employeeIdStr+")";
	}
	
	
	String query ="SELECT SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"', 1,0)) as enrolls,"
			+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND ("
					+ "(entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"' AND (earned_amount >0 OR earned_points >0)  ) OR"
							+ " (entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"' AND earned_reward>0)), 1,0)) as issuances, "
									+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND "
					+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"' AND earned_amount >0,earned_amount, 0  )) as earnedamt , "
									+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND "
									+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"' AND earned_points >0,earned_points, 0  )) as earnedpts ,  "
									+" SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND "
											+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT+"' AND earned_amount >0 , 1,0)) as giftissuances,"
													+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND "
					+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT+"' AND earned_amount >0,earned_amount, 0  )) as giftamt , "
										
					+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"', 1,0)) as redeems,"
							+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"' AND entered_amount_type ='"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM +"' AND entered_amount>0 , entered_amount,0)) as redeemedAmt,"
									+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"' AND entered_amount_type ='"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM +"' AND entered_amount>0 , entered_amount,0)) as redeemedPts,"
							+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_INQUIRY+"', 1,0)) as inquiries, "
							+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
									+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "'  AND (amount_difference >0 OR points_difference ), 1,0)) as returns, "
				+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
				+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' AND amount_difference >0, amount_difference,0) ) as reversalAmount, "
				+  " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
				+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' AND points_difference >0, points_difference,0) ) as reversalPts,"
				+" SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
				+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "' AND (amount_difference >0 OR points_difference) , 1,0) ) as redemptionreversal, "
				+" SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
				+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "' AND amount_difference >0  , amount_difference,0) ) as redemptionreversalamt, "
					+" SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
				+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "' AND  points_difference>0 , points_difference,0) ) as redemptionreversalpts, "
				+" SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_TRANSFER+"', 1,0)) as trans,"
						+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"' AND (earned_amount > 0 OR earned_points>0), 1,0)) as bonus,"
						+"SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"' AND earned_amount > 0,earned_amount ,0)) as bonusAmt,"
						+"SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"' AND earned_points>0, earned_points,0)) as bonusPts,"
						+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"' AND (earned_amount > 0 OR earned_points>0), 1,0)) as adj,"
				+"SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"' AND earned_amount > 0,earned_amount ,0)) as adjAmt,"
						+"SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"' AND earned_points>0, earned_points,0)) as adjPts,"
						+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER+"' , 1,0)) as adjTier"
						+ " FROM "
				+ "loyalty_transaction_child" +
			" WHERE user_id="+userID.longValue()+" AND program_id = " + prgmId.longValue() ;
	logger.debug("query ===> "+query);
	
	//List tempList = getHibernateTemplate().find(query);
	
	List<Object[]> tempList= jdbcTemplate.query(query, new RowMapper() {
		 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			 Object obj[]=new Object[24];
			 obj[0]=rs.getInt("enrolls");
			 obj[1]=rs.getInt("issuances");
			 obj[2]=rs.getDouble("earnedamt");
			 obj[3]=rs.getLong("earnedpts");
			 obj[4]=rs.getInt("giftissuances");
			 obj[5]=rs.getDouble("giftamt");
			 obj[6]=rs.getInt("redeems");
			 obj[7]=rs.getDouble("redeemedAmt");
			 obj[8]=rs.getLong("redeemedPts");
			 obj[9]=rs.getInt("inquiries");
			 obj[10]=rs.getInt("returns");
			 obj[11]=rs.getDouble("reversalAmount");
			 obj[12]=rs.getLong("reversalPts");
			 obj[13]=rs.getInt("redemptionreversal");
			 obj[14]=rs.getDouble("redemptionreversalamt");
			 obj[15]=rs.getLong("redemptionreversalpts");
			 obj[16]=rs.getInt("trans");
			 obj[17]=rs.getInt("bonus");
			 obj[18]=rs.getDouble("bonusAmt");
			 obj[19]=rs.getLong("bonusPts");
			 obj[20]=rs.getInt("adj");
			 obj[21]=rs.getDouble("adjAmt");
			 obj[22]=rs.getLong("adjPts");
			 obj[23]=rs.getInt("adjTier");
			 //obj[2]=rs.getLong("Revenue");
			 return obj;
		 }
	 } );
	if(tempList != null && tempList.size()>0) {
		return tempList.get(0);
	}
	else
		return null;

	

}
public int getEnrollementTrans(Long prgmId, String startDateStr,
		String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
	String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0)
		return ((Long) tempList.get(0)).intValue();
	else
		return 0;
}
	
	/*public int getChangeTierTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}*/
	public int getChangeTierTrans(Long prgmId, String startDateStr,
		String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER + "' AND programId = " + prgmId.longValue() + subQry +
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

	List tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0)
		return ((Long) tempList.get(0)).intValue();
	else
		return 0;
	}

	/*public int getTransferTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_TRANSFER + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}*/
	public int getTransferTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_TRANSFER + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	
	/* public int getInquiryTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		String subQry = "";
		if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND storeNumber in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tierId =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND employeeId in ("+employeeIdStr+")";
		}
		String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_INQUIRY + "' AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}*/
	 public int getInquiryTrans(Long prgmId, String startDateStr,
				String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
				subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
			}
			if(storeNo != null && storeNo.length() != 0) {
				subQry += " AND storeNumber in ("+storeNo+")";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
			}
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			if(employeeIdStr != null && employeeIdStr.length() != 0 ){
				subQry += " AND employeeId in ("+employeeIdStr+")";
			}
			String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
					" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_INQUIRY + "' AND programId = " + prgmId.longValue() + subQry +
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  ";

			List tempList = getHibernateTemplate().find(query);

			if(tempList != null && tempList.size()>0)
				return ((Long) tempList.get(0)).intValue();
			else
				return 0;
		}

	/*public List<Object[]> getStoresTransData(Long prgmId, Long userId,
			String startDate, String endDate) {
		
		String query  = " SELECT storeNumber, transactionType, COUNT(transChildId),enteredAmountType FROM LoyaltyTransactionChild  " +
			        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
			        	" AND storeNumber IS NOT NULL"
			        	//+ " AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) "
	        			+ " AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"'  GROUP BY  storeNumber, transactionType,enteredAmountType";

		List<Object[]> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;
	}*/
	 public List<Object[]> getStoresTransData(Long prgmId, Long userId,
				String startDate, String endDate) {
			
			String query  = " SELECT storeNumber, transactionType, COUNT(transChildId),enteredAmountType, subsidiaryNumber FROM LoyaltyTransactionChild  " +
				        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
				        	" AND storeNumber IS NOT NULL AND subsidiaryNumber IS NOT NULL"
				        	//+ " AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) "
		        			+ " AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"'  GROUP BY  subsidiaryNumber,storeNumber, transactionType,enteredAmountType";

			List<Object[]> tempList = getHibernateTemplate().find(query);
			if(tempList != null && tempList.size() > 0) return tempList;
			return null;
		}

	 
	 public Object[] getHighLevelMetrics(Long userID, Long programId) {

			/*String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
					" WHERE programId = " + programId.longValue() +
					" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'";
			*/
			
			String query ="SELECT SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"', 1,0)) as enrolls,"
					+ "SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND ("
							+ "(entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"' AND (earned_amount >0 OR earned_points >0)  ) OR"
									+ " (entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"' AND earned_reward>0)), 1,0)) as issuances,"
							+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"', 1,0)) as redeems,"
									+ " SUM(IF(transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND "
											+ " (entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "
											+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL + "' OR "+
				" entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "'), 1,0)) as returns FROM "
						+ "loyalty_transaction_child" +
					" WHERE user_id="+userID.longValue()+" AND program_id = " + programId.longValue() ;
			logger.debug("query ===> "+query);
			
			//List tempList = getHibernateTemplate().find(query);
			
			List<Object[]> tempList= jdbcTemplate.query(query, new RowMapper() {
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 Object obj[]=new Object[4];
					 obj[0]=rs.getLong("enrolls");
					 obj[1]=rs.getLong("issuances");
					 obj[2]=rs.getLong("redeems");
					 obj[3]=rs.getLong("returns");
					 //obj[2]=rs.getLong("Revenue");
					 return obj;
				 }
			 } );
			if(tempList != null && tempList.size()>0) {
				return tempList.get(0);
			}
			else
				return null;

			
		}
	public int getEnrollmentsCount(Long programId,Long userId) {//APP-4728

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() + " AND userId="+userId+" " +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	public List<Object[]> getStoresIssLiabilityData(Long prgmId, Long userId) {
		
		String query  = " SELECT storeNumber, SUM(earnedAmount), SUM(conversionAmt) FROM LoyaltyTransactionChild  " +
			        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
			        	" AND storeNumber IS NOT NULL AND  transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+ "' GROUP BY  storeNumber";

		List<Object[]> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;
	}

	public List<Object[]> getStoresRedeemLiabilityData(Long prgmId, Long userId) {
		
		String query  = " SELECT storeNumber, SUM(enteredAmount) FROM LoyaltyTransactionChild  " +
			        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
			        	" AND storeNumber IS NOT NULL AND  transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + 
			        	"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' GROUP BY  storeNumber";

		List<Object[]> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;
	}



	public long getAllTransactionsCountByPrgmId(Long userId, Long prgmId,
			String startDate, String endDate) {
		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + prgmId.longValue() +
				" AND userId = " + userId +
				" AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"' ";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).longValue();
		else
			return 0;
	}

	public List<Object[]> getTransactionCountByStores(Long userId, Long prgmId,
			String startDate, String endDate) {
		String query="SELECT storeNumber, COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + prgmId.longValue() +
				" AND userId = " + userId +
				" AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"' " +
			    " AND storeNumber IS NOT NULL GROUP BY storeNumber " +
				" ORDER BY 2 DESC";

		List<Object[]> tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}

	/**
	 * 
	 * @param transactionType
	 * @param docSid
	 * @return
	 */
	
	public LoyaltyTransactionChild findLtyTransByIssuanceAndDocSid(String docSid,Long userId) {
		List<LoyaltyTransactionChild> list=null;
		String query=" FROM LoyaltyTransactionChild" +
				" WHERE docSID = '" +docSid +"'"+
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+
				" AND userId="+userId.longValue();

		list = getHibernateTemplate().find(query);

		if(list != null && list.size()>0)
			return list.get(0);
		else
			return null;
		
	}//findLtyTransByIssuanceAndDocSid

	public Object[] findRedemptionByDocSid(String docSid,Long userId){
		List<Object[]> redeemList = null;
		try{
		String queryStr = " SELECT SUM(points_difference) as aggLtyPoints, SUM(amount_difference) as aggLtyAmt, SUM(gift_difference) as aggGftAmt "
				+ " FROM loyalty_transaction_child WHERE docsid = '"+docSid +"' "
				+ " AND transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'"
				+ " AND user_id = "+userId.longValue();
		logger.info("query String : "+queryStr);
		redeemList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[3];
				objArr[0] = rs.getLong("aggLtyPoints");
				objArr[1] = rs.getDouble("aggLtyAmt");
				objArr[2] = rs.getDouble("aggGftAmt");
				return objArr;
			}
		});
		
		if(redeemList != null && redeemList.size() > 0 && redeemList.get(0) != null){
			return redeemList.get(0);
		}
		else return null;
		}catch(Exception e){
			logger.error("returns empty list....", e);
			return null;
		}
	}
	
public long getCountOfIssuance(Long userId, String cardNumber,String loyaltyTransType) {
		
		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE membershipNumber ='"+cardNumber+"' AND userId ="+userId +""
				+ " AND transactionType ='"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry ::"+query);
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;
		
	}//getCountOfIssuance	

	public long getAllCountOfIssuance(Long userId,String loyaltyTransType, Long loyaltyId) {//APP-4728 look up
		
		/*String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND ( loyaltyId ='"+loyaltyId+"' OR transferedTo ='"+loyaltyId+"')"
				+ " AND transactionType ='"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry ::"+query);
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;*/
		
		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND loyaltyId ="+loyaltyId+" "
				+ " AND transactionType ='"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry ::"+query);
		List tempList = getHibernateTemplate().find(query);
		long count = 0;
		if(tempList != null && tempList.size()>0)
			count = ((Long) tempList.get(0));
		
		logger.info("loyalty id count === "+count);
		//count = jdbcTemplate.queryForObject(query, Long.class); 
		
		query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND transferedTo ="+loyaltyId+" "
				+ " AND transactionType ='"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry1 ::"+query);
		tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size()>0)
			count += ((Long) tempList.get(0));
		
		//count += jdbcTemplate.queryForObject(query, Long.class);
		logger.info("loyalty id+transferdTo count === "+count);
		
		return count;
		
	}//getCountOfIssuance
	
public long getCountOfRedemption(Long userId, String cardNumber,String loyaltyTransType) {

		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE membershipNumber ='"+cardNumber+"' "
				+ "AND transactionType ='"+loyaltyTransType+"' AND userId ="+userId;
		logger.info(" >>>>  qry ::"+query);
	//	Long itemsCount = jdbcTemplate.queryForLong(query);
		
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;
		
	}//getCountOfRedemption

	public long getAllCountOfRedemption(Long userId,String loyaltyTransType, Long loyaltyId) {//APP-4728 look up

		/*String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND (loyaltyId ='"+loyaltyId+"' OR transferedTo ='"+loyaltyId+"')"
				+ "AND transactionType ='"+loyaltyTransType+"'";
		logger.info(" >>>>  qry ::"+query);
	//	Long itemsCount = jdbcTemplate.queryForLong(query);
		
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;*/
		
		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND loyaltyId ="+loyaltyId+" "
				+ "AND transactionType ='"+loyaltyTransType+"'";
		logger.info(" >>>>  qry ::"+query);
		long count = 0;
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size()>0)
			count = ((Long) tempList.get(0));
		
		//count = jdbcTemplate.queryForObject(query, Long.class); 
		logger.info("loyalty id count === "+count);
		
		query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND transferedTo ="+loyaltyId+" "
				+ "AND transactionType ='"+loyaltyTransType+"'";
		logger.info(" >>>>  qry1 ::"+query);
		tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size()>0)
			count += ((Long) tempList.get(0));
		//count += jdbcTemplate.queryForObject(query, Long.class);
		logger.info("loyalty id+transferdTo count === "+count);
		
		return count;
		
	}//getCountOfRedemption

	public List<LoyaltyTransactionChild> findAllTransactionsByCardNumber(Long cardNumber, Long userId) {
		List<LoyaltyTransactionChild> list = null;
		String query=" FROM LoyaltyTransactionChild " +
				" WHERE membershipNumber ="+cardNumber+" AND userId ="+userId +
				" ORDER BY date(createdDate) DESC";

		list = getHibernateTemplate().find(query);

		return list;
	}
	
	public List<String> findAllTransactionsToShow(Long loyaltyId, Long userId) {
		List<String> list = null;
		String query=" SELECT DISTINCT transactionType FROM LoyaltyTransactionChild " +
				" WHERE loyaltyId ="+loyaltyId+" AND userId ="+userId +
				" AND transactionType NOT IN('Inquiry','Expiry','OTP', 'Tier adjustment','Transfer')";

		list = getHibernateTemplate().find(query);

		return list;
	}
	
	
	public List<String> findAllempIds(Long orgId) {
		List<String> employeeIdList = null;
		String query=" SELECT DISTINCT employeeId FROM LoyaltyTransactionChild " +
				" WHERE orgId = "+orgId.longValue() + " AND employeeId IS NOT NULL";
		employeeIdList = getHibernateTemplate().find(query);
		if(employeeIdList != null && employeeIdList.size() > 0) return employeeIdList;
		else return null;
		}
	
	public List<String> findAllSubsidiaries(Long userId,String serviceType ){
		try{
			List<String> list = null;
			String query = "SELECT DISTINCT subsidiaryNumber from LoyaltyTransactionChild WHERE subsidiaryNumber IS NOT NULL AND userId = "+userId.longValue();
			list = executeQuery(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return new ArrayList<String>();
	}
	
	public List<Object[]> findAllStoresForSubsdiaries(Long userId,String subsidiaryNo ){
		try{
			List<Object[]> list = null;
			subsidiaryNo = StringEscapeUtils.escapeSql(subsidiaryNo);
			String query = "SELECT subsidiaryNumber, storeNumber from LoyaltyTransactionChild WHERE storeNumber IS NOT NULL AND userId = " +userId.longValue()+
					" AND subsidiaryNumber IN ('" +subsidiaryNo+ "') GROUP BY subsidiaryNumber, storeNumber";
			list = getHibernateTemplate().find(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return null;
	}
	
	public List<String> findAllStores(Long userId,String serviceType ){
		try{
			List<String> list = null;
			String query = "SELECT DISTINCT storeNumber from LoyaltyTransactionChild WHERE storeNumber IS NOT NULL AND userId = "+userId.longValue();
			list = executeQuery(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return new ArrayList<String>();
	}
	
	public List<Object[]> findAllStoresAndSbs(Long userId,String serviceType ){
		try{
			List<Object[]> list = null;
			String query = "SELECT storeNumber, subsidiaryNumber from LoyaltyTransactionChild WHERE storeNumber IS NOT NULL AND subsidiaryNumber IS NOT NULL "
					+ "AND userId = "+userId.longValue()+" GROUP BY storeNumber, subsidiaryNumber";
			list = getHibernateTemplate().find(query);
			return list;
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return null;
	}
	
	public int getMaxTransactionsByMembershipnumberCount(Long userId,String loyaltyId, String startDateStr, String endDateStr) {

		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND loyaltyId in ("+loyaltyId+ ")" +
				" AND transactionType IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" GROUP BY loyaltyId ORDER BY date(createdDate) DESC";
		List<Long> tempList = getHibernateTemplate().find(qry);
		long max = 0;
		for(long i:tempList){
			if(max<i)max=i;
		}
		logger.info("Max count as "+max);
		return Integer.valueOf(max+"");
	}//getAllTransactionsByMembershipnumberCount

	/*public int getAllTransactionsByMembershipnumberCount(Long userId,Long loyaltyId, String startDateStr, String endDateStr) {

		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND loyaltyId ='"+loyaltyId+"' " +
				" AND (transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}//getAllTransactionsByMembershipnumberCount
*/
	public int getAllTransactionsByMembershipnumberCount(Long userId,Long loyaltyId, String startDateStr, String endDateStr) {

		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND loyaltyId ='"+loyaltyId+"' " +
				" AND transactionType IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";
		logger.info("---------------------------------------------Count Query ......:"+qry);
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0){
			logger.info("---------------------------------------------Count Value ......:"+((Long) tempList.get(0)).intValue());
			return ((Long) tempList.get(0)).intValue();
		}
		else
			return 0;
	}//getAllTransactionsByMembershipnumberCount
	public List<LoyaltyTransactionChild> getAllTransactionsByMembershipnumber(Long userId, Long loyaltyId, String startDateStr,String endDateStr, int firstResult, int size) {
		
		/*String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId ="+ userId.longValue() + " AND loyaltyId ='"+loyaltyId+"'" +
				" AND (transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";
*/
		//changed by proumya
		/*String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId ="+ userId.longValue() + " AND loyaltyId ='"+loyaltyId+"'" +
				" AND transactionType IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY createdDate DESC";*/
		
		//changed by harshitha
				String qry=" FROM LoyaltyTransactionChild " +
						" WHERE userId ="+ userId.longValue() + " AND loyaltyId ='"+loyaltyId+"'" +
						" AND transactionType IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
						OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"','"+
						OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
						//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
						 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
						" ORDER BY createdDate DESC,transChildId DESC";

		logger.info("---------------------------------------------Pagination Query ......:"+qry);
		return executeQuery(qry, firstResult, size);
	}//getAllTransactionsByMembershipnumber
	
	/**
	 * 
	 * @param membershipNumber
	 * @param transactionType
	 * @parm userId
	 * @return LoyaltyTransactionChild
	 */
	
	
	/*ublic LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId, String transactionType,Long userId) {

		String qry = " FROM LoyaltyTransactionChild " + " WHERE  userId = " + userId +" AND (loyaltyId = " + loyaltyId + " OR transferedTo="
				+ loyaltyId + ")" + " AND transactionType ='" + transactionType + "' ORDER BY transChildId DESC";
		logger.info("query for getTransByMembershipNoAndTransType is"+qry);
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);

		if (tempList != null && tempList.size() > 0)
			return tempList.get(0);
		else
			return null;
	}// getTransByMembershipNoAndTransType*/
	
	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId, String transactionType,Long userId) {//APP-4728

		String qry = " FROM LoyaltyTransactionChild " + " WHERE  userId = " + userId +" AND loyaltyId = " + loyaltyId + " "
				+ " AND transactionType ='" + transactionType + "' ORDER BY transChildId DESC";
		logger.info("query for getTransByMembershipNoAndTransType loyaltyId is"+qry);
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);

		if (tempList != null && tempList.size() > 0)
			return tempList.get(0);
		else {
			String transferQry = " FROM LoyaltyTransactionChild " + " WHERE  userId = " + userId +" AND transferedTo = " + loyaltyId + " "
					+ " AND transactionType ='" + transactionType + "' ORDER BY transChildId DESC";
			logger.info("query for getTransByMembershipNoAndTransType transferedTo is"+transferQry);
			List<LoyaltyTransactionChild> transferTempList = getHibernateTemplate().find(transferQry);

			if (transferTempList != null && transferTempList.size() > 0)
				return transferTempList.get(0);
			else
				return null;
		}
	}// getTransByMembershipNoAndTransType
	
	public LoyaltyTransactionChild getTransByMembershipNoAndTransTypeAndLtyId(Long loyaltyId, String transactionType,Long userId) {//APP-4728

		String qry = " FROM LoyaltyTransactionChild " + " WHERE  userId = " + userId +" AND loyaltyId = " + loyaltyId + " "
				+ " AND transactionType ='" + transactionType + "' ORDER BY transChildId DESC";
		logger.info("query for getTransByMembershipNoAndTransTypeAndLtyId is"+qry);
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);

		if (tempList != null && tempList.size() > 0)
			return tempList.get(0);
		else
			return null;
	}// getTransByMembershipNoAndTransType
	
	
	public LoyaltyTransactionChild getTransactionChild(Long userId, Long transactionId){

		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId +"AND transactionId = "+ transactionId;
		List<LoyaltyTransactionChild> tempList = executeQuery(qry);

		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}//getEnrollmentTransaction

	public Double getTotAmtIssued(Long userId, String transactionType,String startDate, String endDate) {

		String query = "SELECT SUM(earnedAmount) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "'"
				+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findAmountIssued ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
	}
	
public Double getTotPointsIssued(Long userId, String transactionType,String startDate, String endDate) {
		
		String query = "SELECT SUM(earnedPoints) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "'"
				+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findAmountIssued ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
	}
	
public Long findLtyPtsearnedFromIssuance(Long userId, String transactionType,String startDate, String endDate) {
		
		String query = "SELECT SUM(earnedPoints) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "'"
				+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findPointsIssued ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (new Double(tempList.get(0).toString())).longValue();
			}else return 0L;
		}
		else return 0L;
	}

public Long findLtyPtsearnedFromBonus(Long userId, String transactionType,String startDate, String endDate) {
	
	String query = "SELECT SUM(earnedPoints) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
			+ "AND transactionType ='"+transactionType+"'"
			+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

	logger.info("findPointsIssuedfromBonus ...query >>>> .."+query);
	List tempList = getHibernateTemplate().find(query);
	if(tempList != null && tempList.size() == 1) {
		if(tempList.get(0) != null) {
			return (new Double(tempList.get(0).toString())).longValue();
		}else return 0L;
	}
	else return 0L;
}

public Long findLtyPtsearnedFromAdjustment(Long userId, String transactionType,String startDate, String endDate) {
	
	String query = "SELECT SUM(earnedPoints) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
			+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD + "'"
			+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

	logger.info("findPointsIssuedfromAdjustment ...query >>>> .."+query);
	List tempList = getHibernateTemplate().find(query);
	if(tempList != null && tempList.size() == 1) {
		if(tempList.get(0) != null) {
			return (new Double(tempList.get(0).toString())).longValue();
		}else return 0L;
	}
	else return 0L;
}

public Double findLtyAmtearnedFromIssuance(Long userId, String transactionType,String startDate, String endDate) {
	
	String query = "SELECT SUM(earnedAmount) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
			+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "'"
			+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

	logger.info("findAmountIssued ...query >>>> .."+query);
	List tempList = getHibernateTemplate().find(query);
	if(tempList != null && tempList.size() == 1) {
		if(tempList.get(0) != null) {
			return ((Double) tempList.get(0));
		}else return 0.0;
	}
	else return 0.0;
}

public Double findLtyAmtearnedFromBonus(Long userId, String transactionType,String startDate, String endDate) {

String query = "SELECT SUM(earnedAmount) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
		+ "AND transactionType ='"+transactionType+"'"
		+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

logger.info("findAmountIssuedfromBonus ...query >>>> .."+query);
List tempList = getHibernateTemplate().find(query);
if(tempList != null && tempList.size() == 1) {
	if(tempList.get(0) != null) {
		return ((Double) tempList.get(0));
	}else return 0.0;
}
else return 0.0;
}

public Double findLtyAmtearnedFromAdjustment(Long userId, String transactionType,String startDate, String endDate) {

String query = "SELECT SUM(earnedAmount) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
		+ "AND transactionType ='"+transactionType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD + "'"
		+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

logger.info("findAmountIssuedfromAdjustment ...query >>>> .."+query);
List tempList = getHibernateTemplate().find(query);
if(tempList != null && tempList.size() == 1) {
	if(tempList.get(0) != null) {
		return ((Double) tempList.get(0));
	}else return 0.0;
}
else return 0.0;
}

	public Double findTotalGiftRevenue(Long userId, String startDate,String endDate) {
		
		String query = "SELECT SUM(enteredAmount) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "'"
				+ " AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findAmountIssued ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
				}else return 0.0;
		}
		else return 0.0;
	}
	
	public String findTopLocation(Long userId, String startDate,String endDate) {

		String query = "SELECT count(trans_child_id) as tot,store_number FROM loyalty_transaction_child"+
				" WHERE user_id="+userId+" " +
				" AND created_date BETWEEN '"+ startDate +"' AND '"+endDate+"' AND transaction_type ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' "
				+ "AND entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "'" +
				" AND store_number IS NOT NULL GROUP BY store_number order by tot desc limit 1";

		List<Map<String, Object>>  retMap = jdbcTemplate.queryForList(query);

		if(retMap != null && retMap.size() > 0) {
			Map<String, Object> innerMap = retMap.get(0);
			return (String)innerMap.get("store_number");			
		}
		return null;
	}
	
	public Long findPointsRedemptionForOcType(Long userId,String transactionType, String startDate,String endDate) {
		String query = "SELECT ABS(SUM(pointsDifference)) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"' "
				+ "AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findPointsRedemptionForOcType ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
			return Long.valueOf(tempList.get(0).toString()).longValue();
		    }else return 0L;
		}
		else return 0L;
		}
		
	//findPointsRedemptionForOcType

	public Double findLtyAmountRedemptionForOcType(Long userId,String transactionType, String startDate,String endDate) {
		String query = "SELECT ABS(SUM(amountDifference)) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"'"
				+ "AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findLtyAmountRedemptionForOcType ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return Double.valueOf(tempList.get(0).toString());
			}else return 0.0;
		}
		else return 0.0;
	}//findLtyAmountRedemptionForOcType
	
	public Double findGiftAmountRedemptionForOcType(Long userId,String transactionType, String startDate,String endDate) {
		String query = "SELECT ABS(SUM(giftDifference)) FROM LoyaltyTransactionChild WHERE userId ="+userId+" "
				+ "AND transactionType ='"+transactionType+"'"
				+ "AND createdDate  BETWEEN '"+startDate+"' AND '"+endDate+"'  ";

		logger.info("findGiftAmountRedemptionForOcType ...query >>>> .."+query);
		List tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return Double.valueOf(tempList.get(0).toString());
			}else return 0.0;
		}
		else return 0.0;
	}//findGiftAmountRedemptionForOcType
	
	public List<LoyaltyTransactionChild> fetchHoldPtsTrans(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionChild> transList = null;
		
		String queryStr = " FROM LoyaltyTransactionChild WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId+
				 " AND holdPoints != NULL AND holdPoints > 0 AND valueActivationDate IS NOT NULL ORDER BY valueActivationDate ";
		
		transList = executeQuery(queryStr, 0, size);
		
		if(transList != null && transList.size() > 0){
			return transList;
		}
		else return null;
		
	}
	
	public List<LoyaltyTransactionChild> fetchHoldAmtTrans(Long loyaltyId, int size, Long userId){
		List<LoyaltyTransactionChild> transList = null;
		
		String queryStr = " FROM LoyaltyTransactionChild WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId+
				 " AND holdAmount != NULL AND holdAmount > 0  AND valueActivationDate IS NOT NULL ORDER BY valueActivationDate ";
		
		transList = executeQuery(queryStr, 0, size);
		
		if(transList != null && transList.size() > 0){
			return transList;
		}
		else return null;
		
	}

	public List<LoyaltyTransactionChild> findByDocSID(String docSID, Long userId, String transType,String receiptNumber,String storeNumber ,String subsidiaryNumber) {
		
		String receiptSubQry = "";
		
		String subQry="";
		/*if(docSID != null && !docSID.isEmpty()) {
			receiptSubQry = "docSID = '"+docSID +"'";
		}
		
		else*/ if((docSID ==null || docSID.isEmpty()) && 
				receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0") && storeNumber!=null &&
				!storeNumber.isEmpty() ) {
			receiptSubQry += " (( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		receiptSubQry +=" ) )";
		}
		
		if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)) {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		}else {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' ORDER BY enteredAmount";
		}
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+" AND "+(receiptSubQry.isEmpty() ? "docSID = '"+docSID +"'" : receiptSubQry)+
				    " AND transactionType = '"+transType+"' "  +subQry;
		List<LoyaltyTransactionChild> tempList = executeQuery(qry);
		
		
		
		logger.info("qry==>"+qry);
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
public List<LoyaltyTransactionChild> findSpecialRewardByDocSID(String docSID, Long userId, String transType,String receiptNumber,String storeNumber ,String subsidiaryNumber) {
		
		String subQry = "";
		
		String receiptSubQry = "";
		
		/*if(receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0")  && storeNumber!=null && !storeNumber.isEmpty() ) {
			receiptSubQry += " (docSID = '"+docSID +"' OR ( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		receiptSubQry +=" ) )";
		}*/
		
		//APP-4728
		if((docSID ==null || docSID.isEmpty()) && receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0")  && storeNumber!=null && !storeNumber.isEmpty() ) {
			receiptSubQry += " ( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		receiptSubQry +=" )";
		}
		
		/*if(receiptNumber != null && !receiptNumber.isEmpty() && Long.parseLong(receiptNumber)>0  ) {
			receiptSubQry += " (docSID = '"+docSID +"' OR ( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		receiptSubQry +=" ) )";
		}*/
		
		if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)) {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"'";
		}/*else {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' ORDER BY enteredAmount";
		}*/
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				    " AND transactionType = '"+transType+"' AND specialRewardId IS NOT NULL AND "+(receiptSubQry.isEmpty() ? "docSID = '"+docSID +"'" : receiptSubQry)  +subQry;
		logger.info("qry==>"+qry);
		List<LoyaltyTransactionChild> tempList = executeQuery(qry);
		
		
		
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
public List<LoyaltyTransactionChild> findSpecialRewardBy(String docSID, Long userId, String transType,String receiptNumber,String storeNumber ,String subsidiaryNumber) {
	
	String receiptSubQry = "";
	String subQry = "";
	/*if(receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0") && storeNumber!=null && !storeNumber.isEmpty() ) {
		receiptSubQry += " (docSID = '"+docSID +"' OR ( receiptNumber = '"+receiptNumber+"' ";
	if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
		receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

		if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
			receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
		}
	}
	receiptSubQry +=" ) )";
	}*/
	
	//APP-4728
	if((docSID ==null || docSID.isEmpty()) && receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0") && storeNumber!=null && !storeNumber.isEmpty() ) {
		receiptSubQry += " ( receiptNumber = '"+receiptNumber+"' ";
	if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
		receiptSubQry += " AND storeNumber = '"+storeNumber+"' ";

		if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
			receiptSubQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
		}
	}
	receiptSubQry +=" ) ";
	}
	
	/*if(receiptNumber != null && !receiptNumber.isEmpty() && Long.parseLong(receiptNumber)>0 ) {
		subQry += " OR ( receiptNumber = '"+receiptNumber+"' ";
	if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
		subQry += " AND storeNumber = '"+storeNumber+"' ";

		if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
			subQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
		}
	}
		subQry +=" ) ";
	}*/
	
	if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)) {
		subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"'";
	}/*else {
		subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' ORDER BY enteredAmount";
	}*/
	String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
		    " AND transactionType = '"+transType+"' AND specialRewardId IS NOT NULL AND itemInfo IS NOT NULL AND itemInfo != '' AND "+(receiptSubQry.isEmpty() ? "docSID = '"+docSID +"'" : receiptSubQry)  +subQry;

	/*String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
			    " AND transactionType = '"+transType+"' AND specialRewardId IS NOT NULL AND docSID = '"+docSID +"' AND itemInfo IS NOT NULL AND itemInfo != '' "+subQry;
	*/List<LoyaltyTransactionChild> tempList = executeQuery(qry);
	
	
	
	logger.info("qry==>"+qry);
	if(tempList != null && tempList.size()>0)
		return tempList;
	else
		return null;
}
	public List<LoyaltyTransactionChild> findByDocSID(String docSID, Long userId) {
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				    "  AND docSID = '"+docSID +"'";
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
	public List<LoyaltyTransactionChild> getTotReversalAmt(Long userId, String docSID, String transType,String Key) {
		String subQry = "";
		
		if(Key!=null && !Key.isEmpty() &&  docSID.equals("OR-")) {
			String docsID[]= Key.split(";=;");
			docSID=docsID[0];
		}
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+transType+"' AND enteredAmountType IN ('" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "') "+
				    " AND ( description2 ='"+docSID+"' OR description2 = '"+Key+"' )";
        logger.info("Query is"+qry);
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
	public List<LoyaltyTransactionChild> getTotRewarReversalAmt(Long userId, String docSID, String transType,String Key, Long spReawrdID) {
		String subQry = "";
		
		if(Key!=null && !Key.isEmpty() &&  docSID.equals("OR-")) {
			String docsID[]= Key.split(";=;");
			docSID=docsID[0];
		}
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+transType+"' AND enteredAmountType='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL+"' AND enteredAmountType NOT IN ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL+"','" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "','" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "') "+
				    " AND ( description2 ='"+docSID+"' OR description2 = '"+Key+"') AND specialRewardId="+spReawrdID.longValue();
        logger.info("Query is"+qry);
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
	
	public List<LoyaltyTransactionChild> getTotReversalAmtByOrignalReceiptParams(Long userId, String receiptNumber,String storeNumber ,String subsidiaryNumber, String transType,String Key) {//APP-2084
		
		String subQry = "";
		
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			subQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				subQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}

		
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+transType+"' AND enteredAmountType NOT IN ('" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "','" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')"+
				    " AND description2 = '"+receiptNumber+"' "+subQry;
		logger.info("Query is"+qry);
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	}
	
	
	
	public int getTotReturnTransactionsCount(Long userId, String docSID, String transType) {

		String qry= " SELECT COUNT(DISTINCT docSID) FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				" AND transactionType = '"+transType+"' AND enteredAmountType != '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "'"+
			    " AND description2 = '"+docSID+"'";
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	public double getTotRedeemedAmt(Long userId, String docSID,	String transType) {


		String qry= " SELECT SUM(enteredAmount) FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+transType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM + "'"+
					" AND docSID = '"+docSID+"'";
		
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
}

	public double getTotPurchaseAmt(Long userId, String docSID,	String transType, String receiptNumber,String storeNumber,String subsidiaryNumber) {
		String subQry = "";
		
		/*if(receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0") && storeNumber!=null && !storeNumber.isEmpty() ) {
			subQry += " OR ( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			subQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				subQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		subQry+=" )";
		}*/
		
		//APP-4728
		if((docSID == null || docSID.isEmpty()) && receiptNumber != null && !receiptNumber.isEmpty() && !receiptNumber.equals("0") && storeNumber!=null && !storeNumber.isEmpty() ) {
			subQry += " ( receiptNumber = '"+receiptNumber+"' ";
		if(storeNumber!=null && !storeNumber.isEmpty()) {//According to the flow mention in APP-2084.
			subQry += " AND storeNumber = '"+storeNumber+"' ";

			if(subsidiaryNumber!=null && !subsidiaryNumber.isEmpty()) {
				subQry += " AND subsidiaryNumber = '"+subsidiaryNumber+"' ";
			}
		}
		subQry+=" )";
		}
		
		//+(receiptSubQry.isEmpty() ? "docSID = '"+docSID +"'" : receiptSubQry)
		String qry= " SELECT SUM(enteredAmount) FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				" AND transactionType = '"+transType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"'"+
				//" AND ( docSID = '"+docSID+"' " + subQry+ " )";
				" AND "+(subQry.isEmpty() ? "docSID = '"+docSID +"'" : subQry);

		logger.info("qry==>"+qry);
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
}

	public List<LoyaltyTransactionChild> getReturnList(Long userId,	String redeemedOnIdStr) {
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL+"'"+
					" AND redeemedOn IN ("+redeemedOnIdStr+")";
		logger.info("qry ::"+qry);
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
}

	/*public int transferSourceTrxnsToDestMembership(Long sourceMembershipID, Long destMembershipID, String transferedOn) throws Exception{
		
		String query = " UPDATE LoyaltyTransactionChild SET transferedTo="+destMembershipID.longValue()+", transferedOn='"+transferedOn+"'"+ 
				" WHERE loyaltyId="+sourceMembershipID.longValue();
		
		int count = executeUpdate(query);
		logger.info("updated trx Count ="+count);
		
		return count;
	}*/
	/*public int updateAllChildTrxnsToDestMembership(Long sourceMembershipID, Long destMembershipID, String transferedOn) throws Exception{
		
		String query = " UPDATE LoyaltyTransactionChild SET transferedTo="+destMembershipID.longValue()+", transferedOn='"+transferedOn+"'"+
				" WHERE transferedTo="+sourceMembershipID.longValue();
		
		int count = executeUpdate(query);
		logger.info("updated trx Count ="+count);
		
		return count;
	}*/
	public int getTotalVisitBy(Long cardNumber, Long userId) {
		
		String qry="SELECT COUNT(docSID) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND membershipNumber = " + cardNumber;
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;

	}
	public int getCumulativeVisitBy(Long loyaltyId, Long userId, String startDate, String endDate) {
		String subQry = "";
		if(loyaltyId != null){
	
			subQry += " AND (loyaltyId = "+loyaltyId+") " ; // OR transferedTo = "+loyaltyId+")";
		}
		String qry="SELECT COUNT(DISTINCT docSID) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() +subQry+" AND "
						+ "transactionType ='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND "
								+ " createdDate >'" + startDate + "' AND createdDate <='"+ endDate + "'";
		logger.info("cumulative visits query ==== "+qry);
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;

	}
	public double getTotalLifeTimePurchaseValue(Long cardNumber, Long userId){
		String qry= " SELECT SUM(enteredAmount) FROM LoyaltyTransactionChild  WHERE userId = "+userId+" AND membershipNumber = " + cardNumber+
				" AND transactionType = '"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"'";
		List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size()>0) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;
	}
	
	public List<LoyaltyTransactionChild> findBy(Long cid, Long loyaltyId,String startDate, 
			String endDate, String recptNumber, Long userID){
		
		List<LoyaltyTransactionChild> retList = null;
		try {
			String subQuery = (startDate != null && !startDate.isEmpty()) ? (" AND createdDate >= '"+startDate+"' "): Constants.STRING_NILL;
			
			 subQuery += (endDate != null && !endDate.isEmpty()) ? (" AND createdDate <= '"+endDate+"' "): Constants.STRING_NILL;
			 
			subQuery += (recptNumber != null && !recptNumber.isEmpty()) ? (" and (docSID='"+recptNumber+"' OR description2='"+recptNumber+"')") : Constants.STRING_NILL;
			
			subQuery += (cid != null ) ? (" and contactId= "+cid ) : Constants.STRING_NILL;
			
			
			String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userID+
					" AND loyaltyId = "+loyaltyId + subQuery;
			
			retList = executeQuery(qry);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
		
	}
	
	
	public List<Object[]> findTotalRewardsEarnByMembershipNumber(String cardNumber,Long userId,String earnType){
		String query="SELECT sum(points_difference),sum(amount_difference) FROM loyalty_transaction_child where user_id = "+userId+" AND membership_number = '"+cardNumber+"' and entered_amount_type = '"+earnType+"'";
		List<Object[]> totalRewardsEarned = null;

		totalRewardsEarned = jdbcTemplate.query(query, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				Object[] object =  new Object[2];
				object[0] = rs.getDouble(1); // points diffeerence
				object[1] = rs.getDouble(2); // amount difference
				return object;
			}

		});
		if(totalRewardsEarned!=null) {
			return totalRewardsEarned;
		}
		else {
			return null;
		}
	}
	public List<LoyaltyTransactionChild> findBy(Long cid, Long loyaltyId,String startDate, 
			String endDate,  Long userID, int offset, int maxRecords, String transactionType,  String store , 
			String loyaltyEnrolledSource ){
		
		List<LoyaltyTransactionChild> retList = null;
		try {
			String subQuery = (startDate != null && !startDate.isEmpty()) ? (" AND createdDate >= '"+startDate+"' "): Constants.STRING_NILL;
			
			 subQuery += (endDate != null && !endDate.isEmpty()) ? (" AND createdDate <= '"+endDate+"' "): Constants.STRING_NILL;
			 subQuery += (transactionType != null && !transactionType.isEmpty() &&
                     !transactionType.equalsIgnoreCase("Detailed") &&
                     !transactionType.equalsIgnoreCase("All")) ? (" AND transactionType in("+transactionType+ ")"): Constants.STRING_NILL;
			// subQuery += (modeOfTrx != null && !modeOfTrx.isEmpty()) ? (" AND createdDate <= '"+modeOfTrx+"' "): Constants.STRING_NILL;
			 subQuery += (store != null && !store.isEmpty()) ? (" AND storeNumber = '"+store+"' "): Constants.STRING_NILL;
			 subQuery += (loyaltyEnrolledSource != null && !loyaltyEnrolledSource.isEmpty()
					 && !loyaltyEnrolledSource.equalsIgnoreCase("All")) ? (" AND sourceType = '"+loyaltyEnrolledSource+"' "): Constants.STRING_NILL;
			 //subQuery += (serviceType != null && !serviceType.isEmpty()) ? (" AND createdDate <= '"+endDate+"' "): Constants.STRING_NILL;
			//subQuery += (recptNumber != null && !recptNumber.isEmpty()) ? (" and (docSID='"+recptNumber+"' OR description2='"+recptNumber+"')") : Constants.STRING_NILL;
			
			subQuery += (cid != null ) ? (" and contactId= "+cid ) : Constants.STRING_NILL;
			subQuery +=" order by 1 desc " ;
			
			String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userID+
					" AND loyaltyId = "+loyaltyId + subQuery;
			
			
			
			retList = executeQuery(qry,offset, maxRecords );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return retList;
		
	}
public Integer getCountOfPurchases(Long userId, Long programId, Long loyaltyId, String LoyaltyType) {

		try {
			List list = null;
			list = executeQuery("select count(transChildId) from LoyaltyTransactionChild where userId='"+userId+"' and programId="+programId+" and loyaltyId="+loyaltyId+" and enteredAmountType ='"+LoyaltyType+"' ");
			return ((Long) list.get(0)).intValue();
			
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
			return 0;
		}
		
	}

	public Object[] getLatestDocsidByCreatedDateAndTransctionType(Long userID, Long loyaltyID,
			int timezoneDiffrenceMinutesInt, String currentDateString, String loyaltyTransactionType,
			String loyaltyTypeReward) {
		List<Object[]> LoyaltyTransactionChildList = null;
		try {
			String nativeSqlQuery = "SELECT ltc.trans_child_id,ltc.docsid AS dSid , ltc.created_date as cDate, ltc.store_number as st FROM loyalty_transaction_child ltc "
					+ " WHERE user_id =" + userID + " AND loyalty_id = '" + loyaltyID + "' AND "
					+ " docsid IS NOT NULL AND transaction_type = '" + loyaltyTransactionType + "'"
					//+ " AND datediff(DATE_ADD(created_date , INTERVAL " + timezoneDiffrenceMinutesInt + " MINUTE),'"
					//+ currentDateString + "') = '0'" 
					+ " AND entered_amount_type = '" + loyaltyTypeReward
					+ "' ORDER BY 1 DESC LIMIT 1";

			// System.out.println(nativeSqlQuery);

			LoyaltyTransactionChildList = jdbcTemplate.query(nativeSqlQuery, new RowMapper<Object[]>() {
				@Override
				public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {

					Object[] objArr = new Object[3];
					objArr[0] = rs.getString("dSid");
					objArr[1] = rs.getObject("cDate");
					objArr[2] = rs.getObject("st");
					return objArr;
				}
			});

			if (LoyaltyTransactionChildList != null && LoyaltyTransactionChildList.size() > 0
					&& LoyaltyTransactionChildList.get(0) != null) {
				return LoyaltyTransactionChildList.get(0);
			} else
				return null;
		} catch (Exception e) {
			logger.error("Object to Integer casting exception", e);
			return null;
		}
		
		
		/*String query = "from LoyaltyTransactionChild where userId="+userID+" AND loyaltyId="+loyaltyID+" AND docSID IS NOT NULL AND transactionType='"+loyaltyTransactionType+"'  AND date(createdDate)='"+timezoneDiffrenceMinutesInt+"' AND enteredAmountType = '"+loyaltyTypeReward+"' order by 1 desc limit 1";
		List<LoyaltyTransactionChild> retList = executeQuery(nativeSqlQuery);
		if(retList!=null && !retList.isEmpty()) {
			return retList.get(0);
		}else {
			return null;
		}*/
	}
	
	
	public Object[] getLatestDocsidByCreatedDateAndTransctionTypeAndDocSId(Long userID, Long loyaltyID,String curentdateString, String loyaltyTypeReward,String docSId,String source,int timezoneDiffrenceMinutesInt) {
	List<Object[]> LoyaltyTransactionChildList = null;
	try {
	String nativeSqlQuery = "SELECT ltc.trans_child_id,ltc.docsid as dSid , ltc.created_date as cDate FROM loyalty_transaction_child ltc WHERE user_id ="+userID+" AND loyalty_id = '"+loyaltyID+"' AND "
				+ " docsid IS NOT NULL  AND datediff(DATE_ADD(created_date , INTERVAL "+timezoneDiffrenceMinutesInt+" MINUTE),'"+curentdateString+"') = 0"
				+ " AND entered_amount_type = '"+loyaltyTypeReward+"' AND docsid ='"+docSId+"' AND source_type ='"+source+"' ORDER BY 1 DESC LIMIT 1";	
	LoyaltyTransactionChildList = jdbcTemplate.query(nativeSqlQuery, new RowMapper<Object[]>() {
		@Override
		public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {

			Object[] objArr = new Object[1];
			objArr[0] = rs.getString("dSid");
			return objArr;
		}
	});
			if (LoyaltyTransactionChildList != null && LoyaltyTransactionChildList.size() > 0 && LoyaltyTransactionChildList.get(0) != null) {
				return LoyaltyTransactionChildList.get(0);
			} else
				return null;
		} catch (Exception e) {
			logger.error("Object to Integer casting exception", e);
			return null;
		}	
	}
	
	public List<LoyaltyTransactionChild> findTransactionsByMemebership(Long sprwardid,Long userId,int startIndx,int endIndx,
			String subQuery,String orderby_colName,String desc_Asc) {
		/*String Query=" FROM LoyaltyTransactionChild WHERE userId ="+userId +" AND specialRewardId="+sprwardid+" "+subQuery+" "
				+ "AND transactionType='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"' "
						+ "order by "+ orderby_colName +" "+desc_Asc+" ";*/
		String Query=" FROM LoyaltyTransactionChild WHERE userId ="+userId +" AND specialRewardId="+sprwardid+" "+subQuery+" "
				+ "AND transactionType IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANSACTION_RETURN+"') "
						//+ "AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"'"
						+ "order by "+ orderby_colName +" "+desc_Asc+" ";
		logger.info("query===>"+Query);
		return executeQuery(Query,startIndx,endIndx);
	}

	public List<LoyaltyTransactionChild> findTransactionsByMemebershipSp(Long sprwardid,Long userId,int startIndx,int endIndx,
			String subQuery,String orderby_colName,String desc_Asc) {
		/*String Query=" FROM LoyaltyTransactionChild WHERE userId ="+userId +" AND specialRewardId="+sprwardid+" "+subQuery+" "
				+ "AND transactionType='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"' "
						+ "order by "+ orderby_colName +" "+desc_Asc+" ";*/
		String Query=" FROM LoyaltyTransactionChild WHERE userId ="+userId +" AND specialRewardId="+sprwardid+" "+subQuery+" "
				+ "AND transactionType IN ('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"') "
						//+ "AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"'"
						+ "order by "+ orderby_colName +" "+desc_Asc+" ";
		logger.info("query===>"+Query);
		return executeQuery(Query,startIndx,endIndx);
	}
	
	
	
	
	
	
	
	public int findCountBySpecialReward(Long sprwardid,Long userId) {
    	try {
    		String query ="SELECT COUNT(*) from LoyaltyTransactionChild where userId ="+userId+" AND specialRewardId="+sprwardid+" "
    				+ "AND transactionType='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"' ";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }

public int findCountBySpReward(Long sprwardid,Long userId) {
    	try {
    		String query ="SELECT COUNT(*) from LoyaltyTransactionChild where userId ="+userId+" AND specialRewardId="+sprwardid+" "
    				+ "AND transactionType IN ('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANSACTION_RETURN+"') AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"'   ";
			logger.info("query is"+query );

    		return ((Long) find(query).get(0)).intValue();
		//	logger.info("query is"+query );
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	public boolean findBySpecialReward(Long sprwardid,Long userId, String issuanceWindow, Long loyaltyId) {
    	try {
    		//search only within a day, check it includes only date or hours 
    		//String query =" select * from loyalty_transaction_child where user_id ="+userId+" AND special_reward_id="+sprwardid+" "
    		//search only within a day, check it includes only date or hours 
    		//String query =" select * from loyalty_transaction_child where user_id ="+userId+" AND special_reward_id="+sprwardid+" "
    		String now = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR);
    		String query =" select * from loyalty_transaction_child where user_id ="+userId+" AND loyalty_id=" +loyaltyId+ 
    				" AND special_reward_id IS NOT NULL"//="+sprwardid+" "
    				+ " AND transaction_type='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' "
    						+ "AND     (TIMESTAMPDIFF(MINUTE, created_date, '"+now+"'))/60 <= "+issuanceWindow;
    		
    		logger.info("query==>"+query);
    		List<Map<String, Object>> retList = jdbcTemplate.queryForList(query);
    		if(retList != null && !retList.isEmpty()) return true;
			
			return false;
			
		} catch (Exception e) { 
			logger.error("Exception ::" , e);
			return false;
		}
    }
	public int findCountByValueCode(String valueCode,Long userId,String subQuery) {
    	try {
    		String query ="SELECT COUNT(*) from LoyaltyTransactionChild where userId ="+userId+" AND earnType='"+valueCode+"' "+subQuery+""
    				+ "AND transactionType IN ('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"'," 
    								+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"', "
    								+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
    						+ " GROUP BY programId,tierId";
    		logger.info("query "+query);
			return find(query).size();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	/*public int getCountByValueCode(Long userId,String valueCode) {
    	try {
    		String subQuery=Constants.STRING_NILL;
    		if(valueCode!=null && !valueCode.isEmpty()) {
    			subQuery += " AND earnType = '"+valueCode+"' ";
    		}
    		String query ="SELECT COUNT(*) from LoyaltyTransactionChild where userId ="+userId+" "
    				+ "AND transactionType IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"'," 
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
    						+ " GROUP BY earnType";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }*/
	public int getTotalCountByValueCode(Long userId,String valueCode) {
    	try {
    		String subQuery=Constants.STRING_NILL;
    		if(valueCode!=null && !valueCode.isEmpty()) {
    			subQuery += " AND earnType = '"+valueCode+"' ";
    		}
    		String query ="SELECT COUNT(distinct earnType) from LoyaltyTransactionChild where userId ="+userId+" "
    				+ "AND transactionType IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"'," 
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"') "+subQuery+" ";
    						//+ " GROUP BY earnType";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	public List<String> getTotalValueCodes(Long userId) {
    		
    		String query ="SELECT distinct earnType from LoyaltyTransactionChild where userId ="+userId+" "
    				+ "AND transactionType IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"'," 
    				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"') "
    				+ " AND earnType IS NOT NULL";
    		return executeQuery(query);
			
    }
	public List<LoyaltyTransactionChild> findTransactionsByValueCode(Long userId,int startIndx,int endIndx,
			String orderby_colName,String desc_Asc,String valueCode) {
		String subQuery =Constants.STRING_NILL;
		if(valueCode!=null && !valueCode.isEmpty()) {
			subQuery += " AND earnType = '"+valueCode+"' ";
		}
		String Query=" FROM LoyaltyTransactionChild WHERE userId ="+userId +" "
				+ "AND transactionType IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"'," 
										+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"'," 
										+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
						+ " AND earnType IS NOT NULL "+subQuery+" GROUP BY earnType "
						+ "order by "+ orderby_colName +" "+desc_Asc+" ";
		logger.info("query===>"+Query);
		return executeQuery(Query,startIndx,endIndx);
	}
	public int findCountByMemebershipNumber(Long sprwardid,Long userId,String subQuery) {
    	try {
    		String query ="SELECT COUNT(*) from LoyaltyTransactionChild where userId ="+userId+" AND specialRewardId="+sprwardid+" "+subQuery+" "
    				+ "AND transactionType='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"' AND enteredAmountType='"+OCConstants.LOYALTY_TYPE_REWARD+"' ";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	public List<Object[]> getltyTransactionByValueCode(String valueCode,Long userId,String column){
		String issuedValueSubQry = "";
		String liabilitySubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) {
			issuedValueSubQry ="sum(earned_points)";
			liabilitySubQry ="sum(points_difference)";
		}else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) {
			issuedValueSubQry ="sum(earned_amount)";
			liabilitySubQry ="sum(amount_difference)";
		}else {
			//issuedValueSubQry ="sum(earned_reward)";
			issuedValueSubQry ="SUM(IF(entered_amount_type='Sub', -earned_reward, earned_reward))";
			liabilitySubQry ="sum(reward_difference)";
		}
		String trxType="";
		if(column.equalsIgnoreCase("Issued")) trxType="IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"'," + 
											          "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"')";
		else if(column.equalsIgnoreCase("Liability")) trxType="IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"'," + 
								"'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"'," + 
								"'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')";
		String query="SELECT program_id,"+issuedValueSubQry+" as Issued,"
				+ " "+liabilitySubQry+" as liability "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' "
				+ "AND transaction_type "+trxType+"";
		logger.info("query:"+query);
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[2];
				 obj[0]=rs.getLong("Issued");
				 obj[1]=rs.getLong("liability");
				 //obj[2]=rs.getLong("Revenue");
				 return obj;
			 }
		 } );
		return list;
				
	}
	public List<Object[]> getRevenueByValueCode(String valueCode,Long userId){
		String revenueSubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) {
			revenueSubQry = "SUM(IF(entered_amount_type='IssuanceReversal', -description, entered_amount))";
		}else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) {
			revenueSubQry = "SUM(IF(entered_amount_type='IssuanceReversal', -description, entered_amount))"; 
		}else {
			revenueSubQry = "SUM(IF(entered_amount_type='RewardReversal', -description, issuance_amount))"; 
		}
		String query="SELECT program_id,"+revenueSubQry+" as Revenue "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')";
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[1];
				 obj[0]=rs.getLong("Revenue");
				 return obj;
			 }
		 } );
		return list;
				
	}
	public List<Object[]> getROIByProgramAndValueCode(String valueCode,Long userId,int start,int end,String subQuery){
		
		String query="SELECT program_id,tier_id "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' "+subQuery+" "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"',"
				+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
		+ " GROUP BY program_id,tier_id";
		logger.info("ROI"+query);
		List<Object[]> list= jdbcTemplate.query(query+" LIMIT "+start+", "+end, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[2];
				 obj[0]=rs.getLong("program_id");
				 obj[1]=rs.getLong("tier_id");
				 return obj;
			 }
		 } );
		return list;
				
	}
	public Object[] getIssuedProgramAndValueCode(String valueCode,Long userId,String prog,String tier){
		String issuedValueSubQry = "";
		String subQry="";
		if(valueCode.equalsIgnoreCase("Points")) {
			issuedValueSubQry ="sum(earned_points)";
		}
		else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) {
			issuedValueSubQry ="sum(earned_amount)";
		}
		else {
			issuedValueSubQry ="sum(earned_reward)";
			//subQry = "AND entered_amount_type!='Sub'";
			issuedValueSubQry ="SUM(IF(entered_amount_type='Sub', -earned_reward, earned_reward))";
		}
		String query="SELECT "+issuedValueSubQry+" as Issued "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' AND program_id="+prog+" "
						+ "AND tier_id="+tier+" "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
						+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"')"
				+ " GROUP BY program_id,tier_id"
						;
		logger.info("Issued"+query);
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[1];
				 obj[0]=rs.getLong("Issued");
				 return obj;
			 }
		 } );
		if(list!=null && !list.isEmpty()) {
			return list.get(0);
		}else {
			return null;
		}
				
	}
	public Object[] getLiabilityByProgramAndValueCode(String valueCode,Long userId,String prog,String tier){
		String liabilitySubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) {
			liabilitySubQry ="sum(points_difference)";
		}
		else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) {
			liabilitySubQry ="sum(amount_difference)";
		}
		else {
			liabilitySubQry ="sum(reward_difference)";
		}
		String query="SELECT "+liabilitySubQry+" as liability "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' AND program_id="+prog+" "
						+ "AND tier_id="+tier+" "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
						+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"',"
						+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
				+ " GROUP BY program_id,tier_id"
						;
		logger.info("Liability"+query);
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[1];
				 obj[0]=rs.getLong("liability");
				 return obj;
			 }
		 } );
		if(list!=null && !list.isEmpty()) {
			return list.get(0);
		}else {
			return null;
		}
				
	}
	public Object[] getRevenueByProgramAndValueCode(String valueCode,Long userId,String prog,String tier){
		String revenueSubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) {
			revenueSubQry = "SUM(IF(entered_amount_type='IssuanceReversal', -description, entered_amount))";
		}
		else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) {
			revenueSubQry = "SUM(IF(entered_amount_type='IssuanceReversal', -description, entered_amount))";
		}
		else {
			revenueSubQry = "SUM(IF(entered_amount_type='RewardReversal', -description, issuance_amount))";
		}
		String query="SELECT program_id,tier_id,"
				+ " " +revenueSubQry+" as Revenue "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' AND program_id="+prog+" "
						+ "AND tier_id="+tier+" "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
				+ " GROUP BY program_id,tier_id"
				;
		logger.info("Revenue"+query);
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[1];
				 obj[0]=rs.getLong("Revenue");
				 return obj;
			 }
		 } );
		if(list!=null && !list.isEmpty()) {
			return list.get(0);
		}else {
			return null;
		}
				
	}
	public List<Object[]> getltyTransactionBysprewarid(Long userId,Long sprwardid){
		String query="SELECT count(IF(transaction_type='"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"',trans_child_id,0)) as issuedcount,SUM(IF(earn_type='"+OCConstants.LOYALTY_POINTS+"',earned_points,IF(earn_type='"+OCConstants.LOYALTY_TYPE_AMOUNT+"',earned_amount,earned_reward))) as reward,"
				+ "SUM(IF(entered_amount_type='RewardReversal', -description, issuance_amount)) as revenue "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND special_reward_id="+sprwardid+" AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANSACTION_RETURN+"')";
		//logger.info("queryy "+query);
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[3];
				 obj[0]=rs.getLong("issuedcount");
				 obj[1]=rs.getLong("reward");
				 obj[2]=rs.getLong("revenue");
				 return obj;
			 }
		 } );
		return list;
				
	}
	/*public List<Object[]> getltyTransactionByProgramAndValueCode(String valueCode,Long userId,int start,int end){
		String issuedValueSubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) issuedValueSubQry ="sum(earned_points)";
		else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) issuedValueSubQry ="sum(earned_amount)";
		else issuedValueSubQry ="sum(earned_reward)";
		String liabilitySubQry = "";
		if(valueCode.equalsIgnoreCase("Points")) liabilitySubQry ="sum(points_difference)";
		else if(valueCode.equalsIgnoreCase("Currency")||valueCode.equalsIgnoreCase("Amount")) liabilitySubQry ="sum(amount_difference)";
		else liabilitySubQry ="sum(reward_difference)";
		String query="SELECT program_id,tier_id,"+issuedValueSubQry+" as Issued, "
				+ "SUM(IF(entered_amount_type='IssuanceReversal', -description, entered_amount)) as Revenue,"
				+ " "+liabilitySubQry+" as liability "
				+ "from loyalty_transaction_child "
				+ "where user_id="+userId+" AND earn_type='"+valueCode+"' "
				+ "AND transaction_type IN('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"',"
						+ "'"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"',"
						+ "'"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"
				+ " GROUP BY program_id,tier_id";
		List<Object[]> list= jdbcTemplate.query(query+" LIMIT "+start+", "+end, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[5];
				 obj[0]=rs.getLong("Issued");
				 obj[1]=rs.getLong("liability");
				 obj[2]=rs.getLong("Revenue");
				 obj[3]=rs.getLong("program_id");
				 obj[4]=rs.getLong("tier_id");
				 return obj;
			 }
		 } );
		return list;
				
	}*/
public List<LoyaltyTransactionChild> findFBPRedemptions(Long orgID, Long userID, String startDate, String endDate){
		
		
		try {
			String query = "SELECT lc FROM LoyaltyTransactionChild lc, ValueCodes vc WHERE "
					+ " lc.userId="+userID+" AND vc.OrgId="+orgID+" AND lc.valueCode=vc.ValuCode "
					+ " AND lc.createdDate BETWEEN '"+startDate+"' AND '"+endDate+"' AND"
							+ " lc.transactionType='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"' "
									+ " AND lc.earnedReward IS NOT NULL AND vc.associatedWithFBP=true" ;
			
			List<LoyaltyTransactionChild> redemptions = executeQuery(query);
			return redemptions;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
		
	}
public LoyaltyTransactionChild findLastPurchaseTransaction(Long userID, Long programID, Long loyaltyID){
		
		String query = "FROM LoyaltyTransactionChild WHERE userId="+userID+" AND programId = " + programID+" AND loyaltyId="+loyaltyID+
				" AND transactionType='Issuance' AND enteredAmountType='Purchase' ORDER BY createdDate DESC ";		
		List<LoyaltyTransactionChild> issuances = executeQuery(query, 0, 1);
		if(issuances != null && !issuances.isEmpty()){
			return issuances.get(0);
		}
		return null;
	}
public boolean findARewardInLast12Hours(Long userId, String loyaltyTransTypeIssuanc, Long programID,
			Long loyaltyID, String docSid) {
		try {
			// TODO Auto-generated method stub
			String qry = " SELECT count(*) FROM loyalty_transaction_child  WHERE user_id = " + userId
					+ " AND program_id=" + programID + " AND loyalty_id=" + loyaltyID + "  AND transaction_type ='"
					+ OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' " + "AND entered_amount_type='"
					+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD + "' "
					+ "and TIMESTAMPDIFF(HOUR, created_date,now())<=12  AND docsid != '"+docSid+"' ";
logger.debug("qry=="+qry);
			int retCount = jdbcTemplate.queryForInt(qry);
			return retCount > 0;
		} catch (Exception e) {
			logger.error("Exception", e);

		}
		return false;
	}

public Double findPurchaseAmountInLast24Hours(Long userId, Long programID,
		Long loyaltyID) {
	
		// TODO Auto-generated method stub
	
		String qry = " SELECT SUM(entered_amount) FROM loyalty_transaction_child  WHERE user_id = " + userId
				+ " AND program_id=" + programID + " AND loyalty_id=" + loyaltyID + "  AND transaction_type ='"
				+ OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' " + "AND entered_amount_type='"
				+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' "
				+ "and TIMESTAMPDIFF(HOUR, created_date,now())<=24 ";
        logger.debug("qry=="+qry);
        Double count = jdbcTemplate.queryForObject(qry, Double.class);	 
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;
	
}


public List<LoyaltyTransactionChild> findByDocSIDAndUserId(Long userId, String docSID, 
		String loyaltyTransTypeIssuanc,  Long programID, Long loyaltyID) {
	try {
		// TODO Auto-generated method stub
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+ " AND programId="+programID+" AND loyaltyId="+loyaltyID+
			    "  AND docSID = '"+docSID+"'";// +"' AND transactionType IN ('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND enteredAmountType='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size()>0)
			return tempList;
		else
			return null;
	} catch (Exception e) {
		logger.error("Exception", e);
		
	}
	return null;
}




//APP-3221
public LoyaltyTransactionChild getLatestTransactionByLoyaltyId(Long userId,String loyaltyId) {
	
	String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+" AND loyaltyId='"+loyaltyId+"' ORDER BY createdDate DESC" ;
	List<LoyaltyTransactionChild> tempList = executeQuery(qry,0,1);
	
	if(tempList != null && tempList.size()>0)
		return tempList.get(0);
	else
		return null;}
// Expiry Transaction //
public Object getExpiryTierTrans1(Long prgmId, String startDateStr, String endDateStr,Long user_id, Object subsidiaryNo,
		Object storeNo) {
	// TODO Auto-generated method s
	String subQry = "";
	if(subsidiaryNo != null && ((String) subsidiaryNo).length() != 0) {
		subQry += " AND subsidiaryNumber in ("+subsidiaryNo+")";
	}
	if(storeNo != null && ((String) storeNo).length() != 0) {
		subQry += " AND storeNumber in ("+storeNo+")";
	}
	
	//String query  = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
		//	" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+ "' AND programId ='" + prgmId +subQry+
		//	"' AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'AND '"+user_id+"'";//
	
	String query = " SELECT count(transChildId) FROM LoyaltyTransactionChild " +
			" WHERE transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_EXPIRY+"' AND userId= "+user_id+ " AND programId =" + prgmId +subQry+
			" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'";
	
	
		
	
	logger.info("Expiry query"+query);
	List tempList = getHibernateTemplate().find(query);

	if(tempList != null && tempList.size()>0)
		return ((Long) tempList.get(0)).intValue();
	else
		return 0;
}
//APP-2624
public Object[] getTrxSummeryByDate(Long userID, Long prgmId, String startDateStr, String endDateStr,
		String subsidiaryNo, String storeNo, Long cardsetId, String employeeIdStr, Long tierId) {

	String subQry = "";

	if (subsidiaryNo != null && subsidiaryNo.length() != 0) {
		subQry += " AND subsidiary_number in (" + subsidiaryNo + ")";
	}
	if (storeNo != null && storeNo.length() != 0) {
		subQry += " AND store_number in (" + storeNo + ")";
	}
	if (cardsetId != null) {
		subQry += " AND card_set_id =" + cardsetId.longValue();
	}
	if (tierId != null) {
		subQry += " AND tier_id =" + tierId.longValue();
	}
	if (employeeIdStr != null && employeeIdStr.length() != 0) {
		subQry += " AND employee_id in (" + employeeIdStr + ")";
	}

	String query = "SELECT SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT
			+ "', 1,0)) as enrolls," + "SUM(IF((transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE
			+ "' AND " + "(entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "'"
			+ " OR entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD
			+ "')) , 1,0)) as issuances, "

			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND "
			+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE
			+ "' AND earned_amount >0,earned_amount, 0  )) as earnedamt , " + " SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND " + "entered_amount_type = '"
			+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE
			+ "' AND earned_points >0,earned_points, 0  )) as earnedpts ,  " + " SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND " + "entered_amount_type = '"
			+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' AND earned_amount >0 , 1,0)) as giftissuances,"
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND "
			+ "entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT
			+ "' AND earned_amount >0,earned_amount, 0  )) as giftamt , "

			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + "', 1,0)) as redeems,"
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION
			+ "' AND entered_amount_type ='" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
			+ "' AND entered_amount>0 , entered_amount,0)) as redeemedAmt," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + "' AND entered_amount_type ='"
			+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM
			+ "' AND entered_amount>0 , entered_amount,0)) as redeemedPts," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_INQUIRY + "', 1,0)) as inquiries, " + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND " + " entered_amount_type = '"
			+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL
			+ "'  AND (amount_difference >0 OR points_difference ), 1,0)) as returns, "
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND "
			+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL
			+ "' AND amount_difference >0, amount_difference,0) ) as reversalAmount, "
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND "
			+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL
			+ "' AND points_difference >0, points_difference,0) ) as reversalPts," + " SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND " + " entered_amount_type = '"
			+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL
			+ "' AND (amount_difference >0 OR points_difference) , 1,0) ) as redemptionreversal, "
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND "
			+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL
			+ "' AND amount_difference >0  , amount_difference,0) ) as redemptionreversalamt, "
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND "
			+ " entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL
			+ "' AND  points_difference>0 , points_difference,0) ) as redemptionreversalpts, "
			+ " SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_TRANSFER + "', 1,0)) as trans,"
			+ "SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_BONUS
			+ "' AND (earned_amount > 0 OR earned_points>0), 1,0)) as bonus," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_BONUS + "' AND earned_amount > 0,earned_amount ,0)) as bonusAmt,"
			+ "SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_BONUS
			+ "' AND earned_points>0, earned_points,0)) as bonusPts," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT
			+ "' AND (earned_amount > 0 OR earned_points>0), 1,0)) as adj," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT + "' AND earned_amount > 0,earned_amount ,0)) as adjAmt,"
			+ "SUM(IF(transaction_type = '" + OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT
			+ "' AND earned_points>0, earned_points,0)) as adjPts," + "SUM(IF(transaction_type = '"
			+ OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER + "' , 1,0)) as adjTier" + " FROM "
			+ "loyalty_transaction_child" + " WHERE user_id=" + userID.longValue() + " AND program_id = "
			+ prgmId.longValue() + " " + subQry + " AND created_date BETWEEN '" + startDateStr + "' AND '"
			+ endDateStr + "'  ";
	logger.debug("query ===> " + query);

	// List tempList = getHibernateTemplate().find(query);

	List<Object[]> tempList = jdbcTemplate.query(query, new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Object obj[] = new Object[24];
			obj[0] = rs.getInt("enrolls");
			obj[1] = rs.getInt("issuances");
			obj[2] = rs.getDouble("earnedamt");
			obj[3] = rs.getLong("earnedpts");
			obj[4] = rs.getInt("giftissuances");
			obj[5] = rs.getDouble("giftamt");
			obj[6] = rs.getInt("redeems");
			obj[7] = rs.getDouble("redeemedAmt");
			obj[8] = rs.getLong("redeemedPts");
			obj[9] = rs.getInt("inquiries");
			obj[10] = rs.getInt("returns");
			obj[11] = rs.getDouble("reversalAmount");
			obj[12] = rs.getLong("reversalPts");
			obj[13] = rs.getInt("redemptionreversal");
			obj[14] = rs.getDouble("redemptionreversalamt");
			obj[15] = rs.getLong("redemptionreversalpts");
			obj[16] = rs.getInt("trans");
			obj[17] = rs.getInt("bonus");
			obj[18] = rs.getDouble("bonusAmt");
			obj[19] = rs.getLong("bonusPts");
			obj[20] = rs.getInt("adj");
			obj[21] = rs.getDouble("adjAmt");
			obj[22] = rs.getLong("adjPts");
			obj[23] = rs.getInt("adjTier");
			// obj[2]=rs.getLong("Revenue");
			return obj;
		}
	});
	if (tempList != null && tempList.size() > 0) {
		return tempList.get(0);
	} else
		return null;

}
	public Double fetchLastPerkRedemption(Long userId,Long programId,Long loyaltyId, String valueCode) {
		
		String qury = "SELECT SUM(entered_amount) FROM loyalty_transaction_child where user_id="+userId+""
				+ " AND program_id="+programId+" AND loyalty_id="+loyaltyId+" "
						+ "AND transaction_type='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'"
								+ " AND value_code='"+valueCode+"'"
										+ " AND month(created_date) = month(now()) ";
		
		
		Double count = jdbcTemplate.queryForObject(qury, Double.class);	 
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;
	}

}

