package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReIssuePerksOnExpiryDao extends AbstractSpringDao {
	
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public ReIssuePerksOnExpiryDao () {
	}
	
	public long findTheMembersToReissue(Long prgmId, Long tierId) {
		
		try {
    		String countQuery =" SELECT COUNT(distinct loyaltyId) FROM ReIssuePerksOnExpiry WHERE programId="+prgmId+" "
    				+ " AND tierId="+tierId+" ";
			
			long count = getCountByCountQuery(countQuery);
			
			return count;
    		
    	} catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	}
	
	public List<Long> findLoyaltyIdList(Long prgmId, Long tierId, int initial, int max) {
		
		try {
		String qury = " SELECT DISTINCT(loyaltyId) FROM ReIssuePerksOnExpiry WHERE programId="+prgmId+" "
				+" AND tierId="+tierId+" ";
		
		List<Long> list = executeQuery(qury, initial, max);
		
		if(list== null || list.isEmpty()) {
			return null;
		}
		
		return list;
        } catch (Exception e) {
			
			logger.error("Exception ", e);
			return null;
		}
		
	}
	
	/*public long updateReIssueExpTrxIdAndStatus (Long loyaltyId, Long transactionId, String status) {
		
		try {
		String qury = "UPDATE reissue_perks_on_expiry SET transaction_id="+transactionId+", status='"+status+"'"
				+ " WHERE loyalty_id="+loyaltyId+"";
		long updatedCnt = getCountByCountQuery(qury);
		return updatedCnt;
        } catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	}*/
	

}
