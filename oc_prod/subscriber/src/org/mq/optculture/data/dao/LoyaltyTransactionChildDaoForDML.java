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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionChildDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionChildDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyTransactionChild loyaltyTransactionChild) {
		super.saveOrUpdate(loyaltyTransactionChild);
	}
	
	/**
	 * It fetches all the transactions of the points activation date between given dates;
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws LoyaltyProgramException
	 *//*
	public List<LoyaltyProgramTrans> fetchActiveTransOftheHour(Calendar startTime, Calendar endTime) throws LoyaltyProgramException{
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
	}
	
	public List<LoyaltyTransactionChild> fetchCurrentInActiveTrans(String earnStatus, String date, String amountType){
		
		List<LoyaltyTransactionChild> transList = null;
		try{
			
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//String date = format.format(cal.getTime());
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " AND valueActivationDate <= '"+date+"' AND status = 'New'";
			
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
	
	public List<Object[]> findTotTransactionsRateforAll(Long userId,
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
	}
	
	public List<Object[]> findTotTransactionsRateforReturn(Long userId,
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
	}
	
	public List<Object[]> findTotTransactionsRate(Long userId,
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
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, Long cardsetId, String destLtyIds,String employeeIdStr,Long tierId) {

		String subQry = "";
		if(key != null){

			subQry += " AND ( membershipNumber LIKE '%"+key+"%' OR transferedTo in ("+destLtyIds+") )";
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
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, Long cardsetId, String employeeIdStr,Long tierId) {

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
			Long prgmId, String startDateStr, String endDateStr, int firstResult, int size,String key, String transType, String storeNo, Long cardsetId, String destLtyIds,String employeeIdStr,Long tierId) {
		String subQry = "";
		if(key != null){
//			subQry += " AND membershipNumber LIKE '%"+key+"%'";
			subQry += "  AND  (cl.card_number LIKE '%"+key+"%' OR tc.transfered_to in ("+destLtyIds+") ) ";
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
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, tc.description2, tc.transfered_to,tc.description, tc.amount_difference, gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id" +
				" FROM loyalty_transaction_child tc, contacts_loyalty cl" +
				" WHERE cl.loyalty_id = tc.loyalty_id " +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.created_date DESC";
		
		List<Object[]> tempList = null;
		
		tempList= jdbcTemplate.query(qry+" LIMIT "+firstResult+", "+size, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[22];
	        	
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
//	        	object[1] = rs.getString(2); //
	        	return object;
	        }
	        
		});
		
		return tempList;
		
		List<Object[]> tempList = executeQuery(qry, firstResult, size);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;

//		return executeQuery(qry, firstResult, size);
	}
	public List<Object[]> getAllTransactions(Long userId,
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
		
		List<Object[]> tempList = null;
		
		tempList= jdbcTemplate.query(qry+" LIMIT "+firstResult+", "+size, new RowMapper() {

	        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
	        
	        	Object[] object = new Object[22];
	        	
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
//	        	object[1] = rs.getString(2); //
	        	return object;
	        }
	        
		});
		
		return tempList;
		
		List<Object[]> tempList = executeQuery(qry, firstResult, size);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;

//		return executeQuery(qry, firstResult, size);
	}

	public int getIssuanceCount(Long programId) {

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}

	public int getRedemptionCount(Long programId) {

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	public int getReversalCount(Long programId) {

		String query="SELECT COUNT(DISTINCT docSID) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"'" +
				" AND (enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR "+
				" enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";

		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}
	
	public int getStoreCreditCount(Long programId) {

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
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

	
	public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId, String type,String employeeIdStr,Long tierId) {
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
	}
	
	public Object[] getReversalTrans(Long prgmId, String startDateStr,
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
		String query  = " SELECT count(DISTINCT docSID), SUM(amountDifference), SUM(pointsDifference) FROM LoyaltyTransactionChild " +
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
	
	public Object[] getStoreCreditTrans(Long prgmId, String startDateStr,
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
	}

	public Object[] getRedemptionTransAmt(Long prgmId, String startDateStr,
			String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
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
			String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
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
	
	public Object[] getBonusTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		
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
   }
	
public Object[] getAdjustmentTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		
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
	public int getEnrollementTrans(Long prgmId, String startDateStr,
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
	}
	
	public int getChangeTierTrans(Long prgmId, String startDateStr,
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
	}

	public int getTransferTrans(Long prgmId, String startDateStr,
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
	}
	
	
	 public int getInquiryTrans(Long prgmId, String startDateStr,
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
	}

	public List<Object[]> getStoresTransData(Long prgmId, Long userId,
			String startDate, String endDate) {
		
		String query  = " SELECT storeNumber, transactionType, COUNT(transChildId),enteredAmountType FROM LoyaltyTransactionChild  " +
			        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
			        	" AND storeNumber IS NOT NULL"
			        	//+ " AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL ) "
	        			+ " AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"'  GROUP BY  storeNumber, transactionType,enteredAmountType";

		List<Object[]> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;
	}

	public int getEnrollmentsCount(Long programId) {

		String query="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild" +
				" WHERE programId = " + programId.longValue() +
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

	*//**
	 * 
	 * @param transactionType
	 * @param docSid
	 * @return
	 *//*
	
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
			logger.error("returns empty list...", e);
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

	public long getAllCountOfIssuance(Long userId,String loyaltyTransType, Long loyaltyId) {
		
		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND ( loyaltyId ='"+loyaltyId+"' OR transferedTo ='"+loyaltyId+"')"
				+ " AND transactionType ='"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry ::"+query);
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;
		
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

	public long getAllCountOfRedemption(Long userId,String loyaltyTransType, Long loyaltyId) {

		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE  userId ="+userId +" AND (loyaltyId ='"+loyaltyId+"' OR transferedTo ='"+loyaltyId+"')"
				+ "AND transactionType ='"+loyaltyTransType+"'";
		logger.info(" >>>>  qry ::"+query);
	//	Long itemsCount = jdbcTemplate.queryForLong(query);
		
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;
		
	}//getCountOfRedemption

	public List<LoyaltyTransactionChild> findAllTransactionsByCardNumber(Long cardNumber, Long userId) {
		List<LoyaltyTransactionChild> list = null;
		String query=" FROM LoyaltyTransactionChild " +
				" WHERE membershipNumber ="+cardNumber+" AND userId ="+userId +
				" ORDER BY date(createdDate) DESC";

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
	
	public int getMaxTransactionsByMembershipnumberCount(Long userId,String loyaltyId, String startDateStr, String endDateStr) {

		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND loyaltyId in ("+loyaltyId+ ")" +
				" AND (transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
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

	public int getAllTransactionsByMembershipnumberCount(Long userId,Long loyaltyId, String startDateStr, String endDateStr) {

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

	public List<LoyaltyTransactionChild> getAllTransactionsByMembershipnumber(Long userId, Long loyaltyId, String startDateStr,String endDateStr, int firstResult, int size) {
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId ="+ userId.longValue() + " AND loyaltyId ='"+loyaltyId+"'" +
				" AND (transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType ='"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";

		//chaanged by proumya
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId ="+ userId.longValue() + " AND loyaltyId ='"+loyaltyId+"'" +
				" AND transactionType IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
				//" AND (sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' OR sourceType IS NULL )"
				 " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY createdDate DESC";

		logger.info("---------------------------------------------Pagination Query ......:"+qry);
		return executeQuery(qry, firstResult, size);
	}//getAllTransactionsByMembershipnumber
	
	*//**
	 * 
	 * @param membershipNumber
	 * @param transactionType
	 * @return LoyaltyTransactionChild
	 *//*
	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId,String transactionType){
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE   (loyaltyId = " +loyaltyId +" OR transferedTo="+loyaltyId +")"+
				" AND transactionType ='"+transactionType+"' ORDER BY transChildId DESC";
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}//getTransByMembershipNoAndTransType

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

	public List<LoyaltyTransactionChild> findByDocSID(String docSID, Long userId, String transType) {
		
		String subQry = "";
		
		if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)) {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		}else {
			subQry += " AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' ORDER BY enteredAmount";
		}
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				    " AND transactionType = '"+transType+"' AND docSID = '"+docSID +"'"+subQry;
		List<LoyaltyTransactionChild> tempList = getHibernateTemplate().find(qry);
		
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
	public List<LoyaltyTransactionChild> getTotReversalAmt(Long userId, String docSID, String transType) {
		
		String qry= " FROM LoyaltyTransactionChild  WHERE userId = "+userId+
					" AND transactionType = '"+transType+"' AND enteredAmountType NOT IN ('" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "','" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')"+
				    " AND description2 = '"+docSID+"'";
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

	public double getTotPurchaseAmt(Long userId, String docSID,	String transType) {
		String qry= " SELECT SUM(enteredAmount) FROM LoyaltyTransactionChild  WHERE userId = "+userId+
				" AND transactionType = '"+transType+"' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"'"+
				" AND docSID = '"+docSID+"'";

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
*/
	public int transferSourceTrxnsToDestMembership(Long sourceMembershipID, Long destMembershipID, String transferedOn, Long userId) throws Exception{//APP-4728 tune-up
		
		String query = " UPDATE LoyaltyTransactionChild SET transferedTo="+destMembershipID.longValue()+", transferedOn='"+transferedOn+"'"+ 
				" WHERE loyaltyId="+sourceMembershipID.longValue() +" AND userId="+userId;
		
		logger.info("transferSourceTrxnsToDestMembership == "+query);
		int count = executeUpdate(query);
		logger.info("updated trx Count ="+count);
		
		return count;
	}
	public int updateAllChildTrxnsToDestMembership(Long sourceMembershipID, Long destMembershipID, String transferedOn, Long userId) throws Exception{//APP-4728 tune-up
		
		String query = " UPDATE LoyaltyTransactionChild SET transferedTo="+destMembershipID.longValue()+", transferedOn='"+transferedOn+"'"+
				" WHERE transferedTo="+sourceMembershipID.longValue()+" AND userId="+userId;
		
		logger.info("updateAllChildTrxnsToDestMembership == "+query);
		int count = executeUpdate(query);
		logger.info("updated trx Count ="+count);
		
		return count;
	}
}
