package org.mq.optculture.data.dao;
																										
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiryUtil;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyTransactionExpiryDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
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
	
	public List<LoyaltyTransactionExpiry> fetchExpLoyaltyAmtTrans(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId
				+ " AND expiryAmount != NULL AND expiryAmount > 0 AND rewardFlag = 'L' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
public List<LoyaltyTransactionExpiryUtil> fetchExpLoyaltyAmtTransUtil(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionExpiryUtil> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiryUtil WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId
				+ " AND expiryAmount != NULL AND expiryAmount > 0 AND rewardFlag = 'L' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	
	public List<LoyaltyTransactionExpiry> fetchExpRewardAmtTrans(Long loyaltyId, int size, Long userId,String valueCode){

	       List<LoyaltyTransactionExpiry> expireList = null;

	       String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId
	               + " AND expiryReward != NULL AND expiryReward > 0 AND rewardFlag = 'L' AND valueCode= '"+valueCode+"' ORDER BY createdDate ASC ";

	       expireList = executeQuery(queryStr, 0, size);

	       if(expireList != null && expireList.size() > 0){
	           return expireList;
	       }
	       else return null;

	   }
	
	public List<LoyaltyTransactionExpiry> fetchExpLoyaltyPtsTrans(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND (loyaltyId = "+loyaltyId+
				 " OR transferedTo="+loyaltyId+")  AND expiryPoints > 0 AND rewardFlag = 'L' ORDER BY createdDate ASC ";
		
		logger.info("query for fetchExpLoyaltyPtsTrans"+queryStr);
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
public List<LoyaltyTransactionExpiryUtil> fetchExpLoyaltyPtsTransFromUtil(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionExpiryUtil> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiryUtil WHERE userId = "+userId+" AND (loyaltyId = "+loyaltyId+
				 " OR transferedTo="+loyaltyId+") AND expiryPoints != NULL AND expiryPoints > 0 AND rewardFlag = 'L' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	public List<LoyaltyTransactionExpiry> fetchExpGiftAmtTrans(Long loyaltyId, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND loyaltyId = "+loyaltyId
				+ " AND expiryAmount != NULL AND expiryAmount > 0 AND rewardFlag = 'G' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}
	
	//@SuppressWarnings({ "rawtypes", "unchecked" })
	/*public List<Object[]> fetchCurrentActiveTransForExpiry(String currentDateStr, Long programId, Long tierId){
		
		List<Object[]> expireList = null;
		
		String queryStr = " select membership_number, sum(expiry_points) as aggExpPoints, sum(expiry_amount) as aggExpAmt "
				+ " from loyalty_transaction_expiry where program_id = "+programId.longValue()
				+ " AND tier_id = "+tierId.longValue()
				+ " AND (expiry_points > 0 or expiry_amount > 0)"
				+ " AND STR_TO_DATE(concat(year(created_date),'-', month(created_date)), '%Y-%m') <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m')"
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
	}*/
	
	//added for transfer
	@SuppressWarnings("unchecked")
	public Object[] fetchOnlyExpiryValues(Long loyaltyId, String expDate, String rewardFlag){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE  (loyalty_id = " +loyaltyId +" OR transfered_to="+loyaltyId +")"
				+ " AND  special_reward_id IS NULL AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.info("query String : "+queryStr);
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
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
			logger.error("returns empty list...", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, boolean considerBonus){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE  loyalty_id = " +loyaltyId 
				+ " AND special_reward_id IS NULL "+(!considerBonus ? " AND bonus_id IS NULL" : "")+" AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.info("query String : "+queryStr);
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
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
			logger.error("returns empty list...", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, String bonusIds ){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE  loyalty_id = " +loyaltyId 
				+ " AND special_reward_id IS NULL AND bonus_id  in("+bonusIds+") AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.info("query String : "+queryStr);
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
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
			logger.error("returns empty list...", e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag, Long BonusID){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE  loyalty_id = " +loyaltyId 
				+ " AND special_reward_id IS NULL AND bonus_id ="+BonusID+" AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		logger.info("query String : "+queryStr);
		expireList = jdbcTemplate.query(queryStr, new RowMapper() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				
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
			logger.error("returns empty list...", e);
			return null;
		}
	}
	
	/*public void resetAmountExpiryTransValues(Long loyaltyId, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_amount = 0 WHERE loyalty_id = "+loyaltyId
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(concat(year(created_date),'-', month(created_date)), '%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}*/
	
	/*public void resetPointsExpiryTransValues(Long loyaltyId, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0 WHERE loyalty_id = "+loyaltyId
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(concat(year(created_date),'-', month(created_date)), '%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}*/
	
	/*public void resetAmtAndPtsExpiryTransValues(Long loyaltyId, String expDate, String rewardFlag){
		
		String queryStr = " UPDATE loyalty_transaction_expiry "
				+ " SET expiry_points = 0, expiry_amount = 0 WHERE loyalty_id = "+loyaltyId
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(concat(year(created_date),'-', month(created_date)), '%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		
		jdbcTemplate.update(queryStr);
		
	}*/
	
	/*public List<LoyaltyTransactionExpiry> fetchExpPointsTrans(String membshipNo, String rewardType, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryPoints != null AND expiryPoints > 0 AND rewardFlag = '"+rewardType+"' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}*/
	
	/*public List<LoyaltyTransactionExpiry> fetchExpAmtTrans(String membshipNo, String rewardType, int size, Long userId){
		
		List<LoyaltyTransactionExpiry> expireList = null;
		
		String queryStr = " FROM LoyaltyTransactionExpiry WHERE userId = "+userId+" AND membershipNumber = '"+membshipNo+"'"
				+ " AND expiryAmount != null AND expiryAmount > 0 AND rewardFlag = '"+rewardType+"' ORDER BY createdDate ASC ";
		
		expireList = executeQuery(queryStr, 0, size);
		
		if(expireList != null && expireList.size() > 0){
			return expireList;
		}
		else return null;
		
	}*/
	
	/*public  int transferSourceExpiryTrxnsToDestMembership(Long sourceMembershipLoyaltyId, Long destMembershipLoyaltyId, String transferedOn) throws Exception{
		
		String qry = " UPDATE LoyaltyTransactionExpiry SET "
				+ "transferedTo="+destMembershipLoyaltyId+", transferedOn='"+transferedOn+"'   WHERE loyaltyId="+sourceMembershipLoyaltyId;
		
		int count = executeUpdate(qry);
		
		return count;
		
	}
	*/
	/*public  int updateAllChildTrxnsToDestMembership(Long sourceMembershipLoyaltyId, Long destMembershipLoyaltyId, String transferedOn) throws Exception{
		
		String qry = " UPDATE LoyaltyTransactionExpiry SET transferedTo="+destMembershipLoyaltyId+", transferedOn='"+transferedOn+"' WHERE transferedTo="+sourceMembershipLoyaltyId;
		
		int count = executeUpdate(qry);
		
		return count;
		
	}*/
	public List<LoyaltyTransactionExpiryUtil> getAllOCMembershipsBonusExpiry(Long userId, Long programId, int currentSizeInt, int offset){
		

		
		List<LoyaltyTransactionExpiryUtil> loyaltyList = null;
		try {
			String queryStr = null;
			queryStr = " FROM LoyaltyTransactionExpiryUtil "; //LIMIT "+currentSizeInt+","+offset;
			
			/*Query query = getSession().createQuery(queryStr);
			query.setFirstResult(currentSizeInt);
			query.setMaxResults(offset);*/
			
			//return (List<ContactsLoyalty>)query.list();
			
			loyaltyList = executeQuery(queryStr,currentSizeInt, offset);
			/*try{
				
				queryStr = "SELECT * FROM ContactsLoyalty LIMIT "+currentSizeInt+", "+offset;
				loyaltyList = jdbcTemplate.query(queryStr, new RowMapper() {
					
					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						ContactsLoyalty loyalty = new ContactsLoyalty();
						loyalty.setCardNumber(rs.getLong(""));
						loyalty.setContact(rs.getl)
						
						
						return null;
					}
				});
				
			}catch(Exception e){
				logger.error("Exception in getting all memberships ...", e);
			}*/
			
			//return loyaltyList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return loyaltyList;

		
		
	}
	public List<Object[]> getTheExpiryAmount(Long userID,  Long loyaltyId, Long bonusId, int days, String interval){
		try{
			List<Object[]> expireList = null;
			String query =  " Select SUM(expiry_amount) as expAmount, "
					+ " STR_TO_DATE(Date_add(created_date, interval  "+days+ " "+interval+"),'%Y-%m-%d') as expirity"
					+ " from loyalty_transaction_expiry WHERE  user_id="+userID+" AND loyalty_id="+loyaltyId 
				+ " AND special_reward_id IS NULL AND bonus_id ="+bonusId
				+ " AND ( expiry_amount > 0) group by  STR_TO_DATE(Date_add(created_date, interval  "+days+ " "+interval+"),'%Y-%m-%d') ";
	
			expireList = jdbcTemplate.query(query, new RowMapper() {
				@Override
				public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					Object[] objArr = new Object[2];
					objArr[0] = rs.getString("expAmount");
					objArr[1] = rs.getString("expirity");
					return objArr;
				}
			});
			
			if(expireList != null && expireList.size() > 0 && expireList.get(0) != null){
				return expireList; //.get(0);
			}
			else return null;
		}catch(Exception e){
			logger.error("returns empty list...", e);
			return null;
		}
	}
}
