package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Array;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionChild;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionChildDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static Logger getLogger() {
		return logger;
	}

	public LoyaltyTransactionChildDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyTransactionChild loyaltyTransactionChild) {
		super.saveOrUpdate(loyaltyTransactionChild);
	}
	*/
	/*public void saveByCollection(Collection<LoyaltyTransactionChild> collectionObj) {
		getHibernateTemplate().saveOrUpdateAll(collectionObj);
	}
	*/
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
	
	public List<LoyaltyTransactionChild> fetchCurrentInActiveTrans(String earnStatus, String transType, String date, String amountType){
		
		List<LoyaltyTransactionChild> transList = null;
		try{
			
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//String date = format.format(cal.getTime());
			
			/*String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " AND valueActivationDate <= '"+date+"' AND status = 'New'";*/
			
			String queryStr = " FROM LoyaltyTransactionChild WHERE transactionType = '"+transType+"' "     
					+ " AND valueActivationDate = '"+date+"' AND earnStatus = '"+earnStatus+"'";
			
			transList  = (List<LoyaltyTransactionChild>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentInActiveTrans dao >>>", e);
			//throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return transList;
		
	}
	
	/*public Double getLifeTimeLoyaltyPurchaseValue(Long userId, Long prgmId, Long loyaltyId){
		
		String subQry = "";
		if(loyaltyId != null){

			subQry += " AND (loyalty_id = "+loyaltyId+" or transfered_to = "+loyaltyId+")";
		}
			
		String qry="SELECT SUM(IF(entered_amount_type='issuancereversal', -description, entered_amount)) FROM loyalty_transaction_child " +
				" WHERE user_id = " + userId.longValue() + subQry +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type not in('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"', '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_GIFT+"', '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD+"')"; 
		logger.info("The query:"+qry);
		
		Double count = jdbcTemplate.queryForObject(qry, Double.class);	
		logger.info("qry==>"+qry);
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;
	}*/
		/*String qry="SELECT SUM(CASE WHEN enteredAmountType='issuancereversal' THEN CONVERT(description, UNSIGNED) ELSE enteredAmount END) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND transactionType in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"; 
		logger.info("The query:"+qry);*/
		
		/*List tempList = getHibernateTemplate().find(qry);
		if(tempList != null && tempList.size() == 1) {
			if(tempList.get(0) != null) {
				return (Double)tempList.get(0);
			}else return 0.0;
		}
		else return 0.0;*/
		
		
	
	
	
	public Double getLoyaltyCumulativePurchase(Long userId, Long prgmId, Long loyaltyId, String startDate, String endDate){//APP-4728
		
		String subQry = "";
		/*if(loyaltyId != null){

			subQry += " AND (loyalty_id = "+loyaltyId+" or transfered_to = "+loyaltyId+")";
		}
		
		String qry="SELECT SUM(IF(entered_amount_type='issuancereversal', -description, entered_amount)) FROM loyalty_transaction_child " +
				" WHERE user_id = " + userId.longValue() + subQry +
				" AND transaction_type in('"+OCConstants.LOYALTY_TRANSACTION_ISSUANCE+"', '"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')" +
				" AND entered_amount_type !='" +OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT +"'"+
				" AND created_date <='" + startDate + "' AND created_date >='"+ endDate + "'";  
		logger.info("The query:"+qry);
		
		Double count = jdbcTemplate.queryForObject(qry, Double.class);	 
	 	logger.info("Sum===>:"+count);
	 	
	 	if(count != null)
	 		return count;
	 	else 
	 		return 0.0;*/
		
		/*if(loyaltyId != null){

			subQry += " AND (loyalty_id = "+loyaltyId+")";// or transfered_to = "+loyaltyId+")";
		}*/
		
		String qry="SELECT SUM(IF(entered_amount_type='issuancereversal', -description, entered_amount)) FROM loyalty_transaction_child " +
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
	 	
	 	qry="SELECT SUM(IF(entered_amount_type='issuancereversal', -description, entered_amount)) FROM loyalty_transaction_child " +
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
	
	public List<Object[]> findTotTransactionsRate(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff) {
		
		String subQry = "";
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND transactionType ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo+"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		String qry = "" ;
		if(typeDiff.equalsIgnoreCase("days")) { 
			qry="SELECT COUNT(transChildId), DATE(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					" AND sourceType = '" + OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE + "' " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  DATE(createdDate) ORDER BY DATE(createdDate)";
		}else if(typeDiff.equalsIgnoreCase("months")) {
			qry="SELECT COUNT(transChildId), MONTH(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					" AND sourceType = '" + OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE + "' " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  MONTH(createdDate) ORDER BY MONTH(createdDate)";
		}
		else if(typeDiff.equalsIgnoreCase("years")) {
			qry="SELECT COUNT(transChildId), YEAR(createdDate) FROM LoyaltyTransactionChild " +
					" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
					" AND sourceType = '" + OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE + "' " + 
					" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  " +
					" GROUP BY  YEAR(createdDate) ORDER BY YEAR(createdDate)";
		}
			
		return  getHibernateTemplate().find(qry);
	}

	public int getAllTransactionsCount(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String key, String transType, String storeNo, Long cardsetId) {

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
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
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
	
	public List<LoyaltyTransactionChild> getAllTransactions(Long userId,
			Long prgmId, String startDateStr, String endDateStr, int firstResult, int size,String key, String transType, String storeNo, Long cardsetId) {
		
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
			else {
				subQry += " AND transactionType ='" + transType + "' ";
			}
		}
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo+"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
		}
		
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND programId = " + prgmId.longValue() + subQry +
				" AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";

		return executeQuery(qry, firstResult, size);
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
	
	public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId, String type) {
			String subQry = "";
			if(type.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(type.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND enteredAmountType = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			
			if(storeNo != null) {
				subQry += " AND storeNumber ='" + storeNo +"' ";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
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

	public Object[] getRedemptionTransAmt(Long prgmId, String startDateStr,
			String endDateStr, String storeNo, Long cardsetId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
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
			String endDateStr, String storeNo, Long cardsetId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
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
	
	public int getEnrollementTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId) {
			String subQry = "";
			/*if(transType != null) {
				subQry += " AND transactionType ='" + transType + "' ";
			}*/
			if(storeNo != null) {
				subQry += " AND storeNumber ='" + storeNo +"' ";
			}
			if(cardsetId != null) {
				subQry += " AND cardSetId =" + cardsetId.longValue();
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

	public int getInquiryTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId) {
		String subQry = "";
		/*if(transType != null) {
			subQry += " AND transactionType ='" + transType + "' ";
		}*/
		if(storeNo != null) {
			subQry += " AND storeNumber ='" + storeNo +"' ";
		}
		if(cardsetId != null) {
			subQry += " AND cardSetId =" + cardsetId.longValue();
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
			        	" AND  sourceType = '"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"' "
	        			+ " AND created_date BETWEEN '" + startDate + "' AND '"+ endDate +"' AND storeNumber IS NOT NULL GROUP BY  storeNumber, transactionType";

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
			        	" AND  transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+ "' AND storeNumber IS NOT NULL GROUP BY  storeNumber";

		List<Object[]> tempList = getHibernateTemplate().find(query);
		if(tempList != null && tempList.size() > 0) return tempList;
		return null;
	}

	public List<Object[]> getStoresRedeemLiabilityData(Long prgmId, Long userId) {
		
		String query  = " SELECT storeNumber, SUM(enteredAmount) FROM LoyaltyTransactionChild  " +
			        	" WHERE programId = "+prgmId+ " AND userId = "+userId+" " +
			        	" AND  transactionType = '" + OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION + 
			        	"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM+"' AND storeNumber IS NOT NULL GROUP BY  storeNumber";

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
				" AND storeNumber IS NOT NULL" +
				" GROUP BY storeNumber " +
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
	
	public LoyaltyTransactionChild findLtyTransByIssuanceAndDocSid(	String transactionType, String docSid,Long userId) {
		List<LoyaltyTransactionChild> list=null;
		String query=" FROM LoyaltyTransactionChild" +
				" WHERE docSID = '" +docSid +"'"+
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+
				" AND userId="+userId;

		list = getHibernateTemplate().find(query);

		if(list != null && list.size()>0)
			return list.get(0);
		else
			return null;
		
	}//findLtyTransByIssuanceAndDocSid

	public List<LoyaltyTransactionChild> findLtyTransByRedemptionAndDocSid(	String transactionType, String docSid,Long userId) {
		List<LoyaltyTransactionChild> list=null;
		String query=" FROM LoyaltyTransactionChild" +
				" WHERE docSID = '" +docSid +"'"+
				" AND transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'"+
				" AND userId="+userId;
		logger.info("LoyaltyTransaction***********"+query);
		list = getHibernateTemplate().find(query);

		if(list != null && list.size()>0)
			return list;
		else
			return null;
	}

	public long getCountOfIssuance(Long userId, Long cardNumber,String loyaltyTransType) {
		
		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE membershipNumber ='"+cardNumber+"' AND userId ="+userId +""
				+ " AND transactionType = '"+loyaltyTransType+"' AND enteredAmountType = '"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE+"'";
		logger.info(" >>>>  qry ::"+query);
		List tempList = getHibernateTemplate().find(query);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0));
		else
			return 0;
		
	}//getCountOfIssuance
	
	public long getCountOfRedemption(Long userId, Long cardNumber,String loyaltyTransType) {

		String query = "Select count(transChildId) FROM LoyaltyTransactionChild WHERE membershipNumber ='"+cardNumber+"' "
				+ "AND transactionType = '"+loyaltyTransType+"' AND userId ="+userId;
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
				" WHERE membershipNumber = "+cardNumber+" AND userId = "+userId +
				" ORDER BY date(createdDate) DESC";

		list = getHibernateTemplate().find(query);

		return list;
	}

	public LoyaltyTransactionChild findLoyaltyTransactionChild(	Long membershipNumber, Long userId) {
		try {
    		List<LoyaltyTransactionChild> list = null;
			list = getHibernateTemplate().find("from LoyaltyTransactionChild where membershipNumber = " + membershipNumber +" and userId = "+userId);
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	
	}

	public int getAllTransactionsByMembershipnumberCount(Long userId,String membershipNumber, String startDateStr, String endDateStr) {

		String qry="SELECT COUNT(transChildId) FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND membershipNumber = '"+membershipNumber+"' " +
				" AND (transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
				" AND sourceType ='"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"'"
				+ " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";
		List tempList = getHibernateTemplate().find(qry);

		if(tempList != null && tempList.size()>0)
			return ((Long) tempList.get(0)).intValue();
		else
			return 0;
	}//getAllTransactionsByMembershipnumberCount

	public List<LoyaltyTransactionChild> getAllTransactionsByMembershipnumber(Long userId, String membershipNumber, String startDateStr,String endDateStr, int firstResult, int size) {
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId = " + userId.longValue() + " AND membershipNumber = " +membershipNumber +
				" AND (transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' OR transactionType = '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')"+
				" AND sourceType ='"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"'"
				+ " AND createdDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY date(createdDate) DESC";

		logger.info("---------------------------------------------Pagination Query ......:"+qry);
		return executeQuery(qry, firstResult, size);
	}//getAllTransactionsByMembershipnumber

	/**
	 * 
	 * @param membershipNumber
	 * @param transActionType
	 * @return LoyaltyTransactionChild
	 */
	/*public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long useId,Long loyaltyId,String transactionType){
		
		String qry=" FROM LoyaltyTransactionChild " +
				" WHERE userId="+useId+" AND (loyaltyId = " +loyaltyId +" OR transferedTo="+loyaltyId +
				") AND transactionType = '"+transactionType+"' ORDER BY transChildId DESC ";
		List<LoyaltyTransactionChild> tempList = executeQuery(qry, 0, 1);//getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}//getTransByMembershipNoAndTransType*/
	
	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long userId,Long loyaltyId,String transactionType) {//APP-4728

		String qry = " FROM LoyaltyTransactionChild " + " WHERE  userId = " + userId +" AND loyaltyId = " + loyaltyId + " "
				+ " AND transactionType ='" + transactionType + "' ORDER BY transChildId DESC";
		logger.info("query for getTransByMembershipNoAndTransTypeAndLtyId is"+qry);
		List<LoyaltyTransactionChild> tempList = executeQuery(qry, 0, 1);//getHibernateTemplate().find(qry);
		
		if(tempList != null && tempList.size()>0)
			return tempList.get(0);
		else
			return null;
	}// getTransByMembershipNoAndTransTypeAndLtyId

	public List<LoyaltyTransactionChild> findAllNewEtStatusTransaction() {

		List<LoyaltyTransactionChild> listOfnewTransaction = null;
		String typeStr= "'"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"'";
		listOfnewTransaction = getHibernateTemplate().find("FROM LoyaltyTransactionChild WHERE eventTriggStatus= '"+OCConstants.LOYALTY_TRANSACTION_ET_STATUS_NEW+"' "
															+ " AND  membershipNumber is not null AND transactionType in ("+typeStr+")");
		if(listOfnewTransaction != null && listOfnewTransaction.size()>0)
			return listOfnewTransaction;
		else
			return null;

	}//findAllNewEtStatusTransaction()

	public long getKpiSummaryReportEnrollementCount(Long userId, String fromDate, String toDate, Long prgId) {
		String qry = "SELECT COUNT(trans_child_id) FROM loyalty_transaction_child where transaction_type='"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"'"
				+ "And user_id = " + userId.longValue() + " AND created_date BETWEEN '"+fromDate +"' AND '"+toDate+"' AND program_id = "+prgId+" " ;
		logger.info("---Query for enrollments is---"+qry);
		int val = jdbcTemplate.queryForInt(qry);
		logger.info("---Value for no of enrollments is---"+val);
		if(val != 0 && val > 0){
			return val;
		}
		return 0;
	}
	
	public long getKpiSummaryReportGiftCardIssuanceCount(Long userId, String fromDate, String toDate, Long prgId){
		String qry = "SELECT COUNT(trans_child_id) FROM loyalty_transaction_child where transaction_type= '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"
				+ "And entered_amount_type = '" + OCConstants.LOYALTY_TYPE_GIFT + "' And user_id = " + userId.longValue() + " AND created_date BETWEEN '"+fromDate +"' AND '"+toDate+
						 "' AND program_id = "+prgId+"";
		
		int val = jdbcTemplate.queryForInt(qry);
		if(val != 0 && val > 0){
			return val;
		}
		return 0;
	}

	public long getKpiSummaryReportVisitLoyaltyCount(Long userId, String fromDate, String toDate, Long prgId){
		String qry = "SELECT COUNT(DISTINCT(docsid)) FROM loyalty_transaction_child where user_id = " + userId.longValue() + " AND created_date BETWEEN '"+fromDate +"' AND '"+toDate+
				 "' AND program_id = "+prgId+" AND transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND entered_amount_type ='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"'";
		
		int val = jdbcTemplate.queryForInt(qry);
		if(val != 0 && val > 0){
			return val;
		}
		return 0;
	}
	
	public Double getKpiSummaryReportRevenueLtyCount(Long userId, String fromDate, String toDate, Long prgId){
		String qry = "SELECT ROUND(SUM((sales.quantity*sales.sales_price) + sales.tax - if(discount is null, 0, discount)),2) as revenue FROM  retail_pro_sales sales, (SELECT distinct docsid FROM loyalty_transaction_child"
				+ " WHERE user_id = " + userId + " AND program_id = "+prgId+" AND transaction_type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND entered_amount_type ='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"' AND created_date BETWEEN '"+fromDate +"' AND '"+toDate+"' ) ltc where sales.doc_sid = ltc.docsid AND sales.sales_date BETWEEN '"+fromDate +"' AND '"+toDate+"' ";
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(qry);
		//Object[] obj = new Object[0];
		double returnVal = 0;
		if(list !=null && list.size() > 0){
			
			try{
				logger.info("value is --------"+list.get(0));
				logger.info("value is --------"+list.get(0).get("revenue"));
				returnVal = list.get(0).get("revenue") == null?new Double(0): (Double) (list.get(0).get("revenue"));
			}catch(Exception e){
				logger.error("Object to Integer casting exception",e);
			}
		}
		
		return returnVal;
	}

	public List<Object[]> getEnrollmentAndRedemptionCount(Long userId, String fromDate, String toDate, Long prgmId) {
		try{
		String query = "SELECT transaction_type, COUNT(trans_child_id), entered_amount_type FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND transaction_type IN ('"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"
				+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"') AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' GROUP BY transaction_type,entered_amount_type ";
			
		List list = getJdbcTemplate().query(query, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Object[] obj = new Object[3];
				
				obj[0] = rs.getString(1);
				obj[1] = rs.getLong(2);
				obj[2] = rs.getString(3);
				 return obj;
			}
			
		});
		logger.info("===List size of  getEnrollmentAndRedemptionCount==="+ list.size());
		return list;
		
		
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		
		return null;
	}

	public List<Object[]> getReturnCount(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT transaction_type, COUNT(distinct docsid), entered_amount_type FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL+"') AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' GROUP BY transaction_type,entered_amount_type ";
				
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getString(3);
					 return obj;
				}
				
			});
			logger.info("===List size of  getReturnCount==="+ list.size());
			return list;
			
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return null;
	}
	public List<Object[]> getStoreCreditCount(Long userId,String fromDate, String toDate, Long prgmId) {
		try{ 
			String query = "SELECT transaction_type, COUNT(trans_child_id), entered_amount_type FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"') AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' GROUP BY transaction_type,entered_amount_type ";
				
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getString(3);
					 return obj;
				}
				
			});
			logger.info("===List size of  getReturnCount==="+ list.size());
			return list;
			
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return null;
	}
	
	public Double getPointsIssuance(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT SUM(earnedPoints) FROM LoyaltyTransactionChild WHERE userId="+userId+" AND programId = "+prgmId+" AND createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND earnType ='"+OCConstants.LOYALTY_TYPE_POINTS+"'   ";
				
			List l= getHibernateTemplate().find(query);
			if(l != null && l.size() > 0)
				return (Double) l.get(0);
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0.0;
	}
	
	public Double getPointsLiability(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT SUM(ltc.earnedPoints) FROM LoyaltyTransactionChild ltc , ContactsLoyalty cl  WHERE cl.serviceType='OC' AND cl.membershipStatus ='Active' AND cl.loyaltyId=ltc.loyaltyId AND ltc.userId="+userId+" AND ltc.programId = "+prgmId+" AND ltc.createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ltc.transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND ltc.earnType ='"+OCConstants.LOYALTY_TYPE_POINTS+"' AND ( ltc.earnStatus is null or ltc.earnStatus ='"+OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED+"' )  ";
				
			List l= getHibernateTemplate().find(query);
			if(l != null && l.size() > 0)
				return (Double) l.get(0);
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0.0;
	}
	
	public Double getPointsRedemption(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT SUM(pointsDifference) FROM LoyaltyTransactionChild WHERE userId="+userId+" AND programId = "+prgmId+" AND createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'"+") ";
				
			List l= getHibernateTemplate().find(query);
			if(l != null && l.size() > 0 && l.get(0) != null)
				return Double.valueOf(l.get(0).toString()).doubleValue();
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0.0;
	}
	
	public List<Object[]> getCurrencyIssuance(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT transaction_type,entered_amount_type,SUM(earned_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND earn_type = '"+OCConstants.LOYALTY_TYPE_AMOUNT+"' GROUP BY transaction_type,entered_amount_type ";
				
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getDouble(3);
					 return obj;
				}
				
			});
			logger.info("===List size of  getCurrencyIssuance==="+ list.size());
			return list;
			
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return null;
	}
	
	public Double getCurrencyRedeemed(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT SUM(if(amount_difference is null, 0, amount_difference)+if(gift_difference is null, 0, gift_difference)) as sum FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"'"+") ";
				
			 Map<String, Object> m = getJdbcTemplate().queryForMap(query);
			if(m!=null && m.size() > 0)
			return ((Double) m.get("sum")) == null ? 0D : (Double) m.get("sum");
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0D;
	}
	public Double getStoreCredit(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT  SUM(earnedAmount) FROM LoyaltyTransactionChild WHERE userId="+userId+" AND programId = "+prgmId+" AND createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transactionType IN ('"
					+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND enteredAmountType in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"')  GROUP BY transactionType,enteredAmountType ";
				
			List a = getHibernateTemplate().find(query);
			if(a!=null && a.size() > 0)
				return (Double) a.get(0);
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0D;
	}
	
	public Double getCurrencyLiability(Long userId, Long prgmId,String fromDate, String toDate) {
		try{
			String query = "SELECT SUM(ltc.earnedAmount) FROM LoyaltyTransactionChild ltc, ContactsLoyalty cl  WHERE cl.serviceType='OC' AND cl.membershipStatus ='Active' AND cl.loyaltyId=ltc.loyaltyId AND ltc.userId="+userId+" AND ltc.programId = "+prgmId+" AND ltc.createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ltc.transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND ltc.earnType ='"+OCConstants.LOYALTY_TYPE_AMOUNT+"' AND ( ltc.earnStatus is null or ltc.earnStatus = '"+OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED+"' )  ";
				
			List l= getHibernateTemplate().find(query);
			if(l != null && l.size() > 0)
				return (Double) l.get(0);
			
			}catch(Exception e){
				logger.error("Exception :: ",e);
			}
			
			return 0D;
	}
	
	//Tier KPIsDetailed Report
	public List<Object[]> getTierMemberships(Long userId,String fromDate, String toDate, Long prgmId) {//APP-4728
		try{
		
		String query = "select tier_name,tier_type,count(cl.loyalty_id), lpt.tier_id from loyalty_program_tier lpt "
				+ " left join  contacts_loyalty cl on program_tier_id = tier_id and cl.user_id="+userId+" where cl.created_date between '"+fromDate+"' and '"+toDate+"' AND  lpt.program_id ="+prgmId+" group by tier_id order  by tier_type asc";
		
		List list = getJdbcTemplate().query(query, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Object[] obj = new Object[4];
				
				obj[0] = rs.getString(1);
				obj[1] = rs.getString(2);
				obj[2] = rs.getLong(3);
				obj[3] = rs.getLong(4);
				 return obj;
			}
			
		});
		
		return list;
	}catch(Exception e){
		logger.error("Exception :: ",e);
	}
		 return null;
	}
	
	public List<Object[]> getAllTiers(Long prgmId, Long userId) {
		try{
			
			String query = "select tier_name,tier_type, lpt.tier_id from loyalty_program_tier lpt "
					+ " left join  contacts_loyalty cl on program_tier_id = tier_id "
					+ "where lpt.created_by="+userId+" and cl.user_id="+userId+" and lpt.program_id ="+prgmId+" group by tier_id order  by tier_type asc";
			
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getLong(3);
					 return obj;
				}
				
			});
			
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
			 return null;
	}
	
	/*public List getVisitsByTier(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT tierId,COUNT(transChildId), SUM(enteredAmount) FROM LoyaltyTransactionChild WHERE userId="+userId+" AND programId = "+prgmId+" AND createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"') AND enteredAmountType='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"' GROUP BY tierId ";
			return getHibernateTemplate().find(query);
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}*/
	
	public List<Object[]> getVisitsByTier(Long userId,String fromDate, String toDate, Long prgmId, List<LoyaltyProgramTier> tiers) {
		try{
			List<Object[]> tempList = new ArrayList<Object[]>();
			for(LoyaltyProgramTier tier:tiers){
				
				String query = "SELECT "+tier.getTierId()+", COUNT(DISTINCT sales.doc_sid),ROUND(SUM((sales.quantity*sales.sales_price)+ sales.tax -(IF(sales.discount is null,0,sales.discount))),2) " +
				   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id,program_tier_id FROM contacts_loyalty " +
				   " WHERE user_id = " + userId + " AND program_id = " + prgmId + "  AND program_tier_id IS NOT NULL AND program_tier_id = " + tier.getTierId() +") cl " +
				   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + 
				   " AND sales.sales_date BETWEEN '" + fromDate + "' AND '"+ toDate + "'" ;
				
		
				   
				   tempList.addAll(jdbcTemplate.query(query, new RowMapper() {
					   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

						   Object[] object =  new Object[3];
						   object[0] = rs.getLong(1);;
						   object[1] = rs.getLong(2);
						   object[2] = rs.getDouble(3);

						   return object;
					   }
				   }));
			}
				   return tempList;
			/*String query = "SELECT tier_id,COUNT(trans_child_id), SUM(entered_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"') AND entered_amount_type='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"' GROUP BY tier_id ";*/
			
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	//Store KPIs Detailed Report
	
	public List<Object[]> getStoresByType(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			logger.info("entering getStoresByType method");
			
			String query = "SELECT store_number, count(trans_child_id) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+"  AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"') AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
			
			logger.info("query for getStoresByType is"+query);
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[2];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					 return obj;
				}
				
			});
			logger.info("===List size of  getStoresByType==="+ list.size());
			return list;
			
		}catch(Exception e){
			logger.error("Exception in getStoresByType method:: ",e);
		}
		logger.info("exit getStoresByType method");

		return null;
	}
	
	public List<Object[]> getEnrollmensByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			logger.info("entered into getEnrollmensByStore method");

			String query = "SELECT store_number, count(trans_child_id) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"')  AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
		
			logger.info("query for getEnrollmensByStore method is"+query);
	
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[2];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					 return obj;
				}
				
			});
			logger.info("===List size of  getEnrollmensByStore==="+ list.size());
			return list;
		}catch(Exception e){
			logger.error("Exception in getEnrollmensByStore method:: ",e);
		}
		logger.info("exit getEnrollmensByStore method");

		return null;
	}
	
	public List<Object[]> getGiftIssuanceByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			logger.info("entered into getGiftIssuanceByStore method ");
	
			
			String query = "SELECT store_number, count(trans_child_id) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type ='"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' AND entered_amount_type='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT +"' AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
		
			logger.info("query for getGiftIssuanceByStore method is "+query);
	
			
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[2];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					 return obj;
				}
				
			});
			logger.info("===List size of  getGiftIssuanceByStore==="+ list.size());
			return list;
		}catch(Exception e){
			logger.error("Exception in getGiftIssuanceByStore method :: ",e);
		}
		logger.info("exit getGiftIssuanceByStore method ");

		return null;
	}
	
	public List<Object[]> getVisitsByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
		
			logger.info("entered into getVisitsByStore method ");
	
			String query = "SELECT store_number,COUNT(trans_child_id), SUM(entered_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND entered_amount_type='"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE +"' AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
		
			logger.info("query for getVisitsByStore method "+query);

			
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getDouble(3);
					 return obj;
				}
				
			});
			logger.info("===List size of  getVisitsByStore==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception in getVisitsByStore method :: ",e);
		}
		logger.info("exit getVisitsByStore method ");

		return null;
	}

	//Store Transactions Detailed Report
	
	public List<Object[]> getStoreByTransaction(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			logger.info("entered into getStoreByTransaction method ");
			String query = "SELECT store_number, count(trans_child_id) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANSACTION_RETURN+"') AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
			
			logger.info("query for  getStoreByTransaction method "+query);

			
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[2];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					 return obj;
				}
				
			});
			logger.info("===List size of  getStoreByTransaction==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception in getStoreByTransaction method :: ",e);
		}
		
		logger.info("exit into getStoreByTransaction method ");

		return null;
	
	
	}
	
	public List<Object[]> getEnrollmentsByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			logger.info("entered into getEnrollmentsByStore method");
	
			String query = "SELECT store_number,transaction_type,COUNT(trans_child_id), SUM(points_difference), SUM(amount_difference), SUM(gift_difference) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"') AND store_number IS NOT NULL GROUP BY store_number,transaction_type ";
		
			logger.info("query for getEnrollmentsByStore method"+query);

			
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[6];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getDouble(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					 return obj;
				}
				
			});
			logger.info("===List size of  getEnrollmentsByStore==="+ list.size()+"  list is"+list);
			
			return list;
		}catch(Exception e){
			logger.error("Exception in getEnrollmentsByStore method :: ",e);
		}
		
		logger.info("exit  getEnrollmentsByStore method");

		return null;
	}
	
	public List<Object[]> getLiabilityByStore(Long userId,String fromDate, String toDate, Long prgmId) {//APP-4728
		try{
			
			String query = "SELECT ltc.store_number, SUM(earned_points), SUM(earned_amount) FROM loyalty_transaction_child ltc, contacts_loyalty cl  WHERE cl.service_type='OC' AND cl.membership_status ='Active' AND cl.loyalty_id=ltc.loyalty_id AND ltc.user_id="+userId+" AND cl.user_id="+userId+" AND ltc.program_id = "+prgmId+" AND ltc.created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ltc.transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND ltc.earn_type ='"+OCConstants.LOYALTY_TYPE_POINTS+"' AND ( ltc.earn_status is null or ltc.earn_status ='"+OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED+"' ) AND ltc.store_number IS NOT NULL GROUP BY ltc.store_number  ORDER by ltc.store_number";
				
		
			logger.info("query for getLiabilityByStore"+query);
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[3];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getDouble(3);
					 return obj;
				}
				
			});
			logger.info("===List size of  getLiabilityByStore==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception in getLiabilityByStore method  :: ",e);
		}
		return null;
	}
	
	public List<Object[]> getIssuanceAndStoreCreditByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
		logger.info("entered into getIssuanceAndStoreCreditByStore method ");	
			String query = "SELECT store_number,transaction_type,entered_amount_type,COUNT(trans_child_id), SUM(points_difference), SUM(amount_difference), SUM(gift_difference) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ( transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"') OR (transaction_type IN ('"+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"'))) AND store_number IS NOT NULL GROUP BY store_number,transaction_type, entered_amount_type  ";
			
			logger.info("query for  getIssuanceAndStoreCreditByStore method "+query);	

			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[7];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getString(3);
					obj[3] = rs.getLong(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					obj[6] = rs.getDouble(7);
					 return obj;
				}
				
			});
			logger.info("===List size of  getIssuanceAndStoreCreditByStore==="+ list.size()+"  list is"+list);
			
			return list;
		}catch(Exception e){
			logger.error("Exception in getIssuanceAndStoreCreditByStore:: ",e);
		}
		
		logger.info("exit getIssuanceAndStoreCreditByStore method ");	

		return null;
	}
	public List<Object[]> getReturnByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT store_number,transaction_type,entered_amount_type,COUNT(distinct docsid), SUM(points_difference), SUM(amount_difference), SUM(gift_difference) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ( "
					+ " (transaction_type IN ('"+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL+"'))) AND store_number IS NOT NULL GROUP BY store_number,transaction_type, entered_amount_type  ";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[7];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getString(3);
					obj[3] = rs.getLong(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					obj[6] = rs.getDouble(7);
					 return obj;
				}
				
			});
			logger.info("===List size of  getReturnByStore==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	//getweeklyTransactionSummary
	
	public List<Object[]> getEnrollmentsTransactionSummaryWeekly(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT transaction_type,COUNT(trans_child_id), SUM(points_difference), SUM(amount_difference), SUM(gift_difference) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"') GROUP BY transaction_type ";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[5];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getLong(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getDouble(4);
					obj[4] = rs.getDouble(5);
					 return obj;
				}
				
			});
			logger.info("===List size of  getEnrollmentsTransactionSummaryWeekly==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	public List<Object[]> getIssuanceAndReturnTransactionSummaryWeekly(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT transaction_type,entered_amount_type,COUNT(trans_child_id), SUM(points_difference), SUM(amount_difference), SUM(gift_difference), SUM(entered_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ( transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"') OR (transaction_type IN ('"+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"'))) GROUP BY transaction_type, entered_amount_type  ";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[7];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getLong(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					obj[6] = rs.getDouble(7);
					 return obj;
				}
				
			});
			logger.info("===List size of  getIssuanceAndReturnTransactionSummaryWeekly==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	public List<Object[]> getReturnCountWeekly(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT transaction_type,entered_amount_type,COUNT(distinct docsid), SUM(points_difference), SUM(amount_difference), SUM(gift_difference) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND  transaction_type IN ('"
					+OCConstants.LOYALTY_TRANSACTION_RETURN+"'"+") AND entered_amount_type in ('"+ OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL+"') GROUP BY transaction_type, entered_amount_type  ";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[6];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getLong(3);
					obj[3] = rs.getDouble(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					 return obj;
				}
				
			});
			logger.info("===List size of  getReturnCountWeekly==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	public List<Object[]> getTrnscAmntByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT store_number, transaction_type, entered_amount_type, SUM(points_difference), SUM(amount_difference),"
					+ " SUM(gift_difference), SUM(earned_points), SUM(earned_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ( transaction_type IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"' , '"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"')) AND store_number IS NOT NULL GROUP BY store_number, transaction_type, entered_amount_type  ORDER by store_number";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[8];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getString(2);
					obj[2] = rs.getString(3);
					obj[3] = rs.getDouble(4);
					obj[4] = rs.getDouble(5);
					obj[5] = rs.getDouble(6);
					obj[6] = rs.getLong(7);
					obj[7] = rs.getDouble(8);
					 return obj;
				}
				
			});
			logger.info("===List size of  getTrnscAmntByStore==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	public List<Object[]> getStoreCreditByStore(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			String query = "SELECT store_number, SUM(earned_amount) FROM loyalty_transaction_child WHERE user_id="+userId+" AND program_id = "+prgmId+" AND created_date BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ( transaction_type IN ('"
					+OCConstants.LOYALTY_TRANSACTION_RETURN+"') AND entered_amount_type in ('"+OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT+"')) AND store_number IS NOT NULL GROUP BY store_number ORDER by store_number";
			List list = getJdbcTemplate().query(query, new RowMapper(){

				@Override
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Object[] obj = new Object[2];
					
					obj[0] = rs.getString(1);
					obj[1] = rs.getDouble(2);
					 return obj;
				}
				
			});
			logger.info("===List size of  getStoreCreditByStore==="+ list.size()+"  list is"+list);
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return null;
	}
	
	
	public Object[] getLiability(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			String query = "SELECT  SUM(ltc.earnedPoints), SUM(ltc.earnedAmount) FROM LoyaltyTransactionChild ltc, ContactsLoyalty cl  WHERE cl.serviceType='OC' AND cl.membershipStatus ='Active' AND cl.loyaltyId=ltc.loyaltyId AND ltc.userId="+userId+" AND ltc.programId = "+prgmId+" AND ltc.createdDate BETWEEN '"+fromDate+"' AND '"+toDate+"' AND ltc.transactionType IN ('"
					+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"+") AND ltc.earnType ='"+OCConstants.LOYALTY_TYPE_POINTS+"' AND ( ltc.earnStatus is null or ltc.earnStatus ='"+OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED+"' ) ";
				
			List<Object[]> list = getHibernateTemplate().find(query);
			if(list != null && list.size() > 0){
				return  list.get(0);
			}
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return  new Double[2];
	}
	
	
	/*public List getTierKpiDetailedReport(Long userId,String fromDate, String toDate, Long prgmId) {
		try{
			
			String query = "select tier_name,tier_type,lpt.tier_id from loyalty_program_tier lpt  left join  contacts_loyalty cl on program_tier_id = tier_id where  "
					+ "lpt.program_id ="+prgmId+" group by tier_id order  by tier_type asc";
			
			List list = getJdbcTemplate().queryForList(query);
			
			return list;
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
			 return null;
	}*/
	
	
}
