package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionExpiry;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionExpiryDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionExpiryDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyTransactionExpiry loyaltyTransactionExpiry) {
		super.saveOrUpdate(loyaltyTransactionExpiry);
	}*/
	
	public List<LoyaltyTransactionExpiry> fetchExpLoyaltyAmtTrans(String membshipNo, int size, Long userId){
		
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
	}
	
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
	
	public void resetAmtAndPtsExpiryTransValues(Long cardNumber, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0, expiry_amount = 0 WHERE membership_number = "+cardNumber
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}*/
	
	public List<LoyaltyTransactionExpiry> fetchExpPointsTrans(String membshipNo, String rewardType, int size, Long userId){
		
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
	
	
	
	
	

}
