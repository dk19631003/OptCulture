package com.optculture.launchpad.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.optculture.launchpad.dto.CustomExpiryResult;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionExpiry;

public interface LoyaltyTransactionExpiryRepository extends JpaRepository<LoyaltyTransactionExpiry, Long>{
	
	/*List<Object[]> expireList = null;
	 * 
	 * String queryStr =
	 * " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
	 * + " FROM loyalty_transaction_expiry WHERE (loyalty_id = "
	 * +loyaltyId+" OR transfered_to="+loyaltyId+")" +
	 * " AND reward_flag = '"+rewardFlag+"'" +
	 * " AND (expiry_points > 0 OR expiry_amount > 0)" +
	 * " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"
	 * +expDate+"', '%Y-%m')"; logger.info("query String : "+queryStr); expireList =
	 * jdbcTemplate.query(queryStr, new RowMapper() {
	 * 
	 * @Override public Object mapRow(ResultSet rs, int rowNum) throws SQLException
	 * {
	 * 
	 * Object[] objArr = new Object[3]; 
	 * 
	 * objArr[0] =rs.getString("membership_number");
	 * objArr[1] = rs.getLong("aggExpPoints");
	 * objArr[2] = rs.getDouble("aggExpAmt"); 
	 * return objArr; } });
	 * 
	 * if(expireList != null && expireList.size() > 0 && expireList.get(0) != null){
	 * return expireList.get(0); }
	 */
	
	
	@Query("SELECT l.membershipNumber, SUM(l.expiryPoints) AS aggExpPoints, SUM(l.expiryAmount) AS aggExpAmt   "
			+ "FROM LoyaltyTransactionExpiry l "
			+ "WHERE (l.loyaltyId = :loyaltyId OR l.transferedTo = :loyaltyId)   "
			+ "AND l.rewardFlag = :rewardFlag   AND (l.expiryPoints > 0 OR l.expiryAmount > 0)  "
			+ " AND DATE_FORMAT(l.createdDate, '%Y-%m') <= :expiryDate   GROUP BY l.membershipNumber")
	List<Object[]> findByTransOnCondition(Long loyaltyId,LocalDateTime expiryDate,String rewardFlag);
}
