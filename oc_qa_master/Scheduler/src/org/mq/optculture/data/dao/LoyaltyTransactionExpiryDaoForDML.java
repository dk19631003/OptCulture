package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionExpiry;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionExpiryDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionExpiryDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyTransactionExpiry loyaltyTransactionExpiry) {
		super.saveOrUpdate(loyaltyTransactionExpiry);
	}
	
	/*public List<LoyaltyTransactionExpiry> fetchExpLoyaltyAmtTrans(String membshipNo, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryPoints != null AND expiryPoints > 0 AND rewardFlag = 'L' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	public List<LoyaltyTransactionExpiry> fetchExpGiftAmtTrans(String membshipNo, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryAmount != null AND expiryAmount > 0 AND rewardFlag = 'G' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object[]> fetchCurrentActiveTransForExpiry(String currentDateStr, Long programId, Long tierId){
		
		List<Object[]> expireList = null;
		
		String queryStr = " select membership_number, sum(expiry_points) as aggExpPoints, sum(expiry_amount) as aggExpAmt "
				+ " from loyalty_transaction_expiry where program_id = "+programId.longValue()
				+ " AND tier_id = "+tierId.longValue()
				+ " AND (expiry_points > 0 or expiry_amount > 0)"
				
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m')"
						+ " group by membership_number ";
		
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[3];
				objArr[0] = rs.getString("membership_number");
				objArr[1] = rs.getLong("aggExpPoints");
				objArr[2] = rs.getDouble("aggExpAmt");
				return null;
			}
		});
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE (loyalty_id = "+loyaltyId+" OR transfered_to="+loyaltyId+")"
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.info("query String : "+queryStr);
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] objArr = new Object[3];
				objArr[0] = rs.getString("membership_number");
				objArr[1] = rs.getLong("aggExpPoints");
				objArr[2] = rs.getDouble("aggExpAmt");
				return objArr;
			}
		});
		
		if(expireList != null && expireList.size() > 0 && expireList.get(0) != null){
			return expireList.get(0);
		}
		else return null;
		}catch(Exception e){
			logger.error("returns empty list...",e);
			return null;
		}
	}*/
	
	/*public void resetAmountExpiryTransValues(Long cardNumber, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_amount = 0 WHERE membership_number = "+cardNumber
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}
	
	public void resetPointsExpiryTransValues(Long cardNumber, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0 WHERE membership_number = "+cardNumber
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}
	*/
	
	/*queryStr = 
			"UPDATE campaign_sent cs " +
			" JOIN (SELECT count("+tableId+") as cnt, sent_id as sent_id FROM "+fromtable+
			" WHERE sent_id IN("+sentIdStr+") GROUP BY sent_id) o " +
			" ON cs.sent_id = o.sent_id" +
			"	SET cs."+fromtable+"=o.cnt where cs.cr_id="+cr_id.longValue();*/
	
	/*INSERT INTO loyalty_transaction_child 
	(membership_number,membership_type,transaction_type,program_id,card_set_id,tier_id,
	user_id,org_id,entered_amount,entered_amount_type,points_balance,amount_balance,
	gift_balance,created_date,source_type,contact_id,loyalty_id,special_reward_id,
	reward_balance,value_code)( select  cl.card_number,cl.membership_type,'Expiry',
	cl.program_id,cl.card_set_id,cl.tier_id,999,462,cl.aggAmt,'Amount',
	cl.loyalty_balance,cl.giftcard_balance,cl.gift_balance,now(),'Auto',
	cl.contact_id,cl.loyalty_id,5117, cl.balance,cl.value_code from  
	(SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,
	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id,
	lb.value_code,  lb.balance,
	SUM(expiry_amount) as aggAmt FROM loyalty_transaction_expiry e, contacts_loyalty l, 
	loyalty_balance lb WHERE l.user_id=999 and lb.user_id=999 AND l.loyalty_id=lb.loyalty_id 
	AND l.loyalty_id=e.loyalty_id AND e.special_reward_id=5117 
	AND e.reward_flag = 'L' AND (expiry_amount > 0 ) AND 
	STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('2019-12', '%Y-%m') group by e.loyalty_id) as cl ) 
	*/
	public long insertChildRecordOnLtyPointsExpiry(Long rewardID, String expDate, Long userId, Long orgID) {
		
		
		try {
			/*String insertQuery = "INSERT INTO loyalty_transaction_child "
					+ "(membership_number,membership_type,transaction_type,program_id,card_set_id,tier_id,"
					+ "	user_id,org_id,entered_amount,entered_amount_type,points_balance,amount_balance,"
					+ "	gift_balance,created_date,source_type,contact_id,loyalty_id,special_reward_id,"
					+ "	reward_balance,value_code)( select  cl.card_number,cl.membership_type,'Expiry',"
					+ "	cl.program_id,cl.card_set_id,cl.tier_id,"+userId+","+orgID+",cl.aggPts,'Points',cl.loyalty_balance,"
					+ "cl.giftcard_balance,cl.gift_balance,now(),'Auto',cl.contact_id,cl.loyalty_id,"+rewardID+","
					+ " cl.balance,cl.value_code FROM (SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,"
					+ "	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id,"
					+ "	lb.value_code,  lb.balance, "
					+ "SUM(expiry_points) as aggPts"
						+ " FROM loyalty_transaction_expiry e, contacts_loyalty l, loyalty_balance lb WHERE l.user_id="+userId+" AND "
						+ "lb.user_id="+userId 	+ " AND l.loyalty_id=lb.loyalty_id AND l.loyalty_id=e.loyalty_id AND "
								+ "e.special_reward_id="+rewardID 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_points > 0 )"
						+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by e.loyalty_id) as cl )";
			*/
			String insertQuery = "INSERT INTO loyalty_transaction_child "
					+ "(membership_number,membership_type,transaction_type,program_id,card_set_id,tier_id,"
					+ "	user_id,org_id,entered_amount,entered_amount_type,points_balance,amount_balance,"
					+ "	gift_balance,created_date,source_type,contact_id,loyalty_id,special_reward_id"
					+ "	)( select  cl.card_number,cl.membership_type,'Expiry',"
					+ "	cl.program_id,cl.card_set_id,cl.tier_id,"+userId+","+orgID+",cl.aggPts,'Points',cl.loyalty_balance,"
					+ "cl.giftcard_balance,cl.gift_balance,now(),'Auto',cl.contact_id,cl.loyalty_id,"+rewardID+""
					+ "  FROM (SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,"
					+ "	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id,"
					+ "	 "
					+ "SUM(expiry_points) as aggPts"
						+ " FROM loyalty_transaction_expiry e, contacts_loyalty l  WHERE l.user_id="+userId+" "
						+ "  AND l.loyalty_id=e.loyalty_id AND "
								+ "e.special_reward_id="+rewardID 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_points > 0 )"
						+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by e.loyalty_id) as cl )";
			
			logger.debug("insert query "+insertQuery);

			long insertedCount = executeJDBCInsertQuery(insertQuery);
			
			return insertedCount;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	
	}
	
	
public long insertChildRecordOnLtyAmountExpiry(Long rewardID, String expDate, Long userId, Long orgID) {
		
		
		try {
			String insertQuery = "INSERT INTO loyalty_transaction_child (membership_number,membership_type,transaction_type,"
					+ "program_id,card_set_id,tier_id,user_id,org_id,entered_amount,entered_amount_type,"
					+ "points_balance,amount_balance,gift_balance,"
					+ "created_date,source_type,contact_id,loyalty_id,"
					+"special_reward_id"
					+") (select  cl.card_number,cl.membership_type,'Expiry',cl.program_id,"
					+ "cl.card_set_id,cl.tier_id,"+userId+","+orgID+",cl.aggAmt,'Amount',cl.loyalty_balance,"
					+ "cl.giftcard_balance,cl.gift_balance,now(),'Auto',cl.contact_id,cl.loyalty_id,"+rewardID+""
					+ "  FROM (SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,"
					+ "	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id"
					+ "	,	SUM(expiry_amount) as aggAmt FROM loyalty_transaction_expiry e, contacts_loyalty l "
					+ "	 WHERE l.user_id="+userId 
								+ "  AND l.loyalty_id=e.loyalty_id AND e.special_reward_id="+rewardID 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_amount > 0 )"
						+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') "
								+ "group by e.loyalty_id) as cl )";
			
			logger.debug("insert query "+insertQuery);

			long insertedCount = executeJDBCInsertQuery(insertQuery);
			
			return insertedCount;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	}
	public long insertChildRecordOnRewardExpiry(Long rewardID, String expDate, Long userId, Long orgID) {
		
		
		try {
			String insertQuery = "INSERT INTO loyalty_transaction_child (membership_number,membership_type,transaction_type,"
					+ "program_id,card_set_id,tier_id,user_id,org_id,entered_amount,entered_amount_type,"
					+ "points_balance,amount_balance,gift_balance,"
					+ "created_date,source_type,contact_id,loyalty_id,"
					+"special_reward_id,reward_balance,value_code"
					+") (select  cl.card_number,cl.membership_type,'Expiry',cl.program_id,"
					+ "cl.card_set_id,cl.tier_id,"+userId+","+orgID+",cl.aggReward,'Reward',cl.loyalty_balance,"
					+ "cl.giftcard_balance,cl.gift_balance,now(),'Auto',cl.contact_id,cl.loyalty_id,"+rewardID+","
					+ " cl.balance,cl.value_code FROM (SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,"
					+ "	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id,"
					+ "	lb.value_code,  lb.balance, SUM(expiry_reward) as aggReward"
						+ " FROM loyalty_transaction_expiry e, contacts_loyalty l, loyalty_balance lb WHERE l.user_id="+userId+" AND "
						+ "lb.user_id="+userId 	+ " AND l.loyalty_id=lb.loyalty_id AND l.loyalty_id=e.loyalty_id "
						+ "AND e.special_reward_id="+rewardID 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_reward > 0 )"
						+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by e.loyalty_id, e.value_code) as cl )";
			
			logger.debug("insert query "+insertQuery);

			long insertedCount = executeJDBCInsertQuery(insertQuery);
			
			return insertedCount;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	
	}
public long insertChildRecordOnPerksExpiry(Long tierID, Long userId, Long orgID, Long prgmId) {
		
		
		try {
			/*String insertQuery = "INSERT INTO loyalty_transaction_child (membership_number,membership_type,transaction_type,"
					+ "program_id,card_set_id,tier_id,user_id,org_id,entered_amount,entered_amount_type,"
					+ "points_balance,amount_balance,gift_balance,"
					+ "created_date,source_type,contact_id,loyalty_id,"
					+"special_reward_id,reward_balance,value_code"
					+") (select  cl.card_number,cl.membership_type,'Expiry',cl.program_id,"
					+ "cl.card_set_id,"+tierID+","+userId+","+orgID+",cl.aggReward,'Reward',cl.loyalty_balance,"
					+ "cl.giftcard_balance,cl.gift_balance,now(),'Auto',cl.contact_id,cl.loyalty_id,cl.special_reward_id,"
					+ " cl.balance,cl.value_code FROM (SELECT l.card_number,l.contact_id, l.membership_type, l.loyalty_id,l.loyalty_balance,"
					+ "	l.giftcard_balance,l.gift_balance, l.program_id, l.card_set_id, l.program_tier_id as tier_id,"
					+ " e.special_reward_id,lb.value_code,  lb.balance, SUM(expiry_reward) as aggReward"
						+ " FROM loyalty_transaction_expiry e, contacts_loyalty l, loyalty_balance lb WHERE l.user_id="+userId+""
						+ " AND l.program_id="+prgmId+" AND lb.program_id="+prgmId+" "
						+ " AND lb.user_id="+userId+ " AND l.loyalty_id=lb.loyalty_id AND l.loyalty_id=e.loyalty_id "
						+ "AND e.tier_id="+tierID+"" 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_reward > 0 ) group by e.loyalty_id, e.value_code) as cl )";
						//+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by e.loyalty_id, e.value_code) as cl )";*/
			
			String insertQuery = "INSERT INTO loyalty_transaction_child (membership_number,membership_type,transaction_type,"
					+ "program_id,card_set_id,tier_id,user_id,org_id,entered_amount,entered_amount_type,"
					+ "points_balance,amount_balance,gift_balance,"
					+ "created_date,source_type,contact_id,loyalty_id,"
					+"reward_balance,value_code"
					+") (SELECT l.card_number,l.membership_type,'Expiry',l.program_id,l.card_set_id,"+tierID+","+userId+","+orgID+","
						+ "SUM(expiry_reward),'Reward',l.loyalty_balance, l.giftcard_balance,l.gift_balance,now(),'Auto',l.contact_id,"
						+ "l.loyalty_id,lb.balance,lb.value_code "
						+ " FROM loyalty_transaction_expiry e, contacts_loyalty l, loyalty_balance lb WHERE l.user_id="+userId+""
						+ " AND l.program_id="+prgmId+" AND lb.program_id="+prgmId+" "
						+ " AND lb.user_id="+userId+ " AND l.loyalty_id=lb.loyalty_id AND l.loyalty_id=e.loyalty_id "
						+ "AND e.tier_id="+tierID+"" 
						+ " AND e.reward_flag = 'L'"
						+ " AND (expiry_reward > 0 ) group by e.loyalty_id, e.value_code)";
						//+ " AND STR_TO_DATE(CONCAT(YEAR(e.created_date),'-', MONTH(e.created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by e.loyalty_id, e.value_code) as cl )";
			
			logger.debug("insert query "+insertQuery);

			long insertedCount = executeJDBCInsertQuery(insertQuery);
			
			return insertedCount;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	
	}
	public long expireSPRewardTrxForVC(Long rewardID, String expDate, Long userId ){
		
		try {
			String queryStr = " UPDATE loyalty_balance lb JOIN ( SELECT loyalty_id, value_code, SUM(expiry_reward) as aggReward"
					+ " FROM loyalty_transaction_expiry WHERE special_reward_id="+rewardID 
					+ " AND reward_flag = 'L'"
					+ " AND (expiry_reward > 0 )"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by loyalty_id, value_code) exp "
					+"   ON lb.loyalty_id=exp.loyalty_id and lb.value_code=exp.value_code SET lb.balance = if(lb.balance >= exp.aggReward , (lb.balance-exp.aggReward),0), "
					+ "lb.total_expired_balance= if(lb.total_expired_balance IS NULL, exp.aggReward, (lb.total_expired_balance+exp.aggReward )) "
					+ "WHERE lb.user_id="+userId;
			logger.debug("query ==="+queryStr);
			long updatedCnt = executeJdbcUpdateQuery(queryStr);
			return updatedCnt;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return 0;
		}
		
		
		
	}
	
    public long expirePerks(Long tierID, String valueCode, Long prgmId, Long userId ){
		
		try {
			/*String queryStr = " UPDATE loyalty_balance lb JOIN ( SELECT loyalty_id, value_code, SUM(expiry_reward) as aggReward"
					+ " FROM loyalty_transaction_expiry WHERE user_id="+userId+" AND program_id= "+prgmId+"  AND tier_id="+tierID+" "
					+ " AND value_code ='"+valueCode+"'"
					+ " AND reward_flag = 'L'"
					+ " AND (expiry_reward > 0 ) group by loyalty_id, value_code) exp"
					//+ " AND STR_TO_DATE(CONCAT('"+expDate+"'),'%Y-%m') = STR_TO_DATE(now(), '%Y-%m') group by loyalty_id, value_code) exp "
					+"   ON lb.loyalty_id=exp.loyalty_id and lb.value_code=exp.value_code SET lb.balance = if(lb.balance >= exp.aggReward , (lb.balance-exp.aggReward),0), "
					+ "lb.total_expired_balance= if(lb.total_expired_balance IS NULL, exp.aggReward, (lb.total_expired_balance+exp.aggReward )) "
					+ "WHERE lb.user_id="+userId+" AND lb.program_id= "+prgmId+" ";*/
			
			String queryStr = " UPDATE loyalty_balance lb JOIN contacts_loyalty cl ON cl.user_id="+userId+" "
					+ "AND cl.program_id="+prgmId+" AND cl.program_tier_id="+tierID+" AND cl.loyalty_id=lb.loyalty_id  "
					+ "SET lb.total_expired_balance= ifnull(lb.total_expired_balance,0)+lb.balance, "
					+ "lb.balance = 0 "
					+ "WHERE lb.user_id="+userId+" AND lb.program_id= "+prgmId+" AND lb.value_code='"+valueCode+"' AND lb.balance>0";
			
			logger.debug("query ==="+queryStr);
			long updatedCnt = executeJdbcUpdateQuery(queryStr);
			return updatedCnt;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return 0;
		}
		
		
		
	}
    
    public long updateChildTrxForExpiredPerks(Long tierID, String valueCode, String expDate, Long prgmId, Long userId ){
		
		try {
			String queryStr = " UPDATE loyalty_transaction_child ltc JOIN ( SELECT loyalty_id, value_code, SUM(expiry_reward) as aggReward"
					+ " FROM loyalty_transaction_expiry WHERE user_id='"+userId+"'"
					+ " AND program_id= '"+prgmId+"'"
					+ " AND tier_id='"+tierID+"'" 
					+ " AND value_code ='"+valueCode+"'"
					+ " AND reward_flag = 'L'"
					+ " AND (expiry_reward > 0 ) group by loyalty_id, value_code) exp"
					//+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by loyalty_id, value_code) exp "
					+"   ON ltc.loyalty_id=exp.loyalty_id and ltc.user_id=exp.user_id, and ltc.program_id=exp.program_id, and ltc.value_code=exp.value_code SET ltc.reward_balance = if(ltc.reward_balance >= exp.aggReward , (ltc.reward_balance-exp.aggReward),0), "
					+ "WHERE ltc.user_id='"+userId+"' AND ltc.program_id= '"+prgmId+"' ";
			logger.debug("query ==="+queryStr);
			long updatedCnt = executeJdbcUpdateQuery(queryStr);
			return updatedCnt;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return 0;
		}
		
		
		
	}
	
	public int expireSPRewardTrxForReward(Long rewardID, String expDate, Long userId ){
		
	try{	String queryStr = " UPDATE contacts_loyalty cl JOIN ( SELECT loyalty_id,  SUM(expiry_points) as aggPts,  SUM(expiry_amount) as aggAmt"
				+ " FROM loyalty_transaction_expiry WHERE special_reward_id="+rewardID 
				//+ " AND reward_flag = 'L'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m') group by loyalty_id) exp "
				+"   ON cl.loyalty_id=exp.loyalty_id SET cl.loyalty_balance = if(cl.loyalty_balance >= exp.aggPts , (cl.loyalty_balance-exp.aggPts),0),"
				+ " cl.giftcard_balance = if(cl.giftcard_balance >= exp.aggAmt , (cl.giftcard_balance-exp.aggAmt),0), "
				+ "cl.expired_points= if(cl.expired_points IS NULL, exp.aggPts, (cl.expired_points+exp.aggPts )),"
				+ " cl.expired_reward_amount= if(cl.expired_reward_amount IS NULL, exp.aggAmt, (cl.expired_reward_amount+exp.aggAmt )) WHERE cl.user_id="+userId;
		logger.debug("query ==="+queryStr);
		int updatedCnt = executeJdbcUpdateQuery(queryStr);
		return updatedCnt;
	} catch (Exception e) {
		logger.error("Exception ", e);
		return 0;
	}
		
	}
	public long movePointsExpiredToBackupTable(Long rewardID, String expDate){
		
		try {
			String insertQuery = " INSERT INTO loyalty_transaction_expiry_backup (trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id, expired_on)  (select trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id,now() from loyalty_transaction_expiry WHERE special_reward_id="+rewardID
					//+ " AND reward_flag = 'L'"
					+ " AND (expiry_points > 0 )"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m'))";
			
			logger.debug("query ==="+insertQuery);
			long count = executeJDBCInsertQuery(insertQuery);
			
			String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE special_reward_id="+rewardID
					//+ " AND reward_flag = 'L'"
					+ " AND (expiry_points > 0 )"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
			
			logger.debug("query ==="+deleteQuery);
			count = executeJdbcUpdateQuery(deleteQuery);
			
			return count;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
		
		
	}
public long moveAmountExpiredToBackupTable(Long rewardID, String expDate){
		
		try {
			String insertQuery = " INSERT INTO loyalty_transaction_expiry_backup (trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id, expired_on)  (select trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id,now() from loyalty_transaction_expiry WHERE special_reward_id="+rewardID
					//+ " AND reward_flag = 'L'"
					+ " AND (expiry_amount > 0 )"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m'))";
			
			logger.debug("query ==="+insertQuery);
			long count = executeJDBCInsertQuery(insertQuery);
			
			String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE special_reward_id="+rewardID
					//+ " AND reward_flag = 'L'"
					+ " AND (  expiry_amount > 0 )"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
			
			logger.debug("query ==="+deleteQuery);
			count = executeJdbcUpdateQuery(deleteQuery);
			
			return count;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
		
		
	}
public long moveRewardExpiredToBackupTable(Long rewardID, String expDate){
	
	try {
		String insertQuery = " INSERT INTO loyalty_transaction_expiry_backup (trans_child_id,membership_number,"
				+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
				+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
				+ "value_code,special_reward_id, expired_on)  (select trans_child_id,membership_number,"
				+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
				+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
				+ "value_code,special_reward_id,now() from loyalty_transaction_expiry WHERE special_reward_id="+rewardID
				//+ " AND reward_flag = 'L'"
				+ " AND (expiry_reward > 0 )"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m'))";
		
		logger.debug("query ==="+insertQuery);
		long count = executeJDBCInsertQuery(insertQuery);
		
		String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE special_reward_id="+rewardID
				//+ " AND reward_flag = 'L'"
				+ " AND ( expiry_reward >0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		logger.debug("query ==="+deleteQuery);
		count = executeJdbcUpdateQuery(deleteQuery);
		
		return count;
	} catch (Exception e) {
		
		logger.error("Exception ", e);
		return 0;
	}
	
	
}
	
    public long moveExpiredPerksToBackupTable(Long tierID, String valueCode, Long prgmId, Long userId){
		
		try {
			String insertQuery = " INSERT INTO loyalty_transaction_expiry_backup (trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id, expired_on)  (select trans_child_id,membership_number,"
					+ "membership_type,reward_flag,user_id,org_id,program_id,tier_id,expiry_points,"
					+ "expiry_amount,created_date,loyalty_id,transfered_to,transfered_on,expiry_reward,"
					+ "value_code,special_reward_id,now() from loyalty_transaction_expiry WHERE user_id="+userId+""
					+ " AND program_id="+prgmId+""
					+ " AND tier_id="+tierID+""
					//+ " AND reward_flag = 'L'"
					+ " AND value_code='"+valueCode+"'"
					+ " AND expiry_reward >0)";
					//+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m'))";
			
			logger.debug("query ==="+insertQuery);
			long count = executeJDBCInsertQuery(insertQuery);
			//do this after reissuance split into new mthod call this method after reissuance.
			//like these who are going to be delted.
			/*String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE user_id="+userId+" AND program_id="+prgmId+""
					+ " AND tier_id="+tierID+""
					//+ " AND reward_flag = 'L'"
					+ " AND value_code='"+valueCode+"'"
					+ " AND expiry_reward >0"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
			
			logger.debug("query ==="+deleteQuery);
			count = executeJdbcUpdateQuery(deleteQuery);*/
			
			return count;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
		
		
	}
public long reIssuePerksOnExpiry(Long tierID, String valueCode, Long prgmId, Long userId){
		
		try {
			String insertQuery = " INSERT INTO reissue_perks_on_expiry (user_id,program_id,loyalty_id, tier_id, created_date,value_code)"
					+ " (select user_id,program_id,loyalty_id,tier_id, now(), value_code"
					+ " from loyalty_transaction_expiry WHERE user_id="+userId+""
					+ " AND program_id="+prgmId+""
					+ " AND tier_id="+tierID+""
					//+ " AND reward_flag = 'L'"
					+ " AND value_code='"+valueCode+"'"
					+ " AND expiry_reward >0)";
					//+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m'))";
			
			logger.debug("query ==="+insertQuery);
			long count = executeJDBCInsertQuery(insertQuery);
			//do this after reissuance split into new mthod call this method after reissuance.
			//like these who are going to be delted.
			/*String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE user_id="+userId+" AND program_id="+prgmId+""
					+ " AND tier_id="+tierID+""
					//+ " AND reward_flag = 'L'"
					+ " AND value_code='"+valueCode+"'"
					+ " AND expiry_reward >0"
					+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
			
			logger.debug("query ==="+deleteQuery);
			count = executeJdbcUpdateQuery(deleteQuery);*/
			
			return count;
		} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
		
		
	}
    public long deleteCount (Long prgmId, Long userId) {
    	
    	try {
    		String deleteCountQuery =" SELECT COUNT(distinct(loyalty_id)) FROM loyalty_transaction_expiry WHERE user_id="+userId+" "
    				+ "AND program_id="+prgmId+" ";
			
			logger.debug("query ==="+deleteCountQuery);
			long count = executeJdbcUpdateQuery(deleteCountQuery);
			
			return count;
    		
    	} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
    }
    
    public long deletePerkRewardTrx (Long tierID, String valueCode, Long prgmId, Long userId) {
    	
    	try {
    		String deleteQuery =" DELETE FROM loyalty_transaction_expiry  WHERE user_id="+userId+" "
    				+ "AND program_id="+prgmId+""
					+ " AND tier_id="+tierID+""
					//+ " AND reward_flag = 'L'"
					+ " AND value_code='"+valueCode+"'"
					+ " AND expiry_reward >0";
					//+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
			
			logger.debug("query ==="+deleteQuery);
			long count = executeJdbcUpdateQuery(deleteQuery);
			
			return count;
    		
    	} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
    }
	
	/*public void resetAmtAndPtsExpiryTransValuesForSPR(Long rewardID, String expDate){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0, expiry_amount = 0, expiry_reward = 0 WHERE special_reward_id="+rewardID
				//+ " AND reward_flag = 'L'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0 OR expiry_reward >0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.debug("query ==="+queryStr);
		jdbcTemplate.update(queryStr);
		
	}
	public void resetAmtAndPtsExpiryTransValues(Long cardNumber, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0, expiry_amount = 0 WHERE membership_number = "+cardNumber
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}
*/	
	/*public List<LoyaltyTransactionExpiry> fetchExpPointsTrans(String membshipNo, String rewardType, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryPoints != null AND expiryPoints > 0 AND rewardFlag = '"+rewardType+"' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	public List<LoyaltyTransactionExpiry> fetchExpAmtTrans(String membshipNo, String rewardType, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryAmount != null AND expiryAmount > 0 AND rewardFlag = '"+rewardType+"' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	*/
	
	
	
	

}
