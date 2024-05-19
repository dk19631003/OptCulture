package org.mq.optculture.data.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.ReIssuePerksOnExpiry;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReIssuePerksOnExpiryDaoForDML extends AbstractSpringDaoForDML {
	
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public ReIssuePerksOnExpiryDaoForDML () {
	}
	
	public void saveOrUpdate(ReIssuePerksOnExpiry reIssuePerksOnExpiry) {
		super.saveOrUpdate(reIssuePerksOnExpiry);
	}
	
    public long updateReIssueExpTrxIdAndStatus (Long loyaltyId, Long transactionId, String status, Long prgmId, Long tierId) {
		
    	try {
		String qury = "UPDATE reissue_perks_on_expiry SET transaction_id="+transactionId+", status='"+status+"'"
				+ " WHERE program_id="+prgmId+" AND tier_id ="+tierId+" AND loyalty_id="+loyaltyId+"";
		long updatedCnt = executeJdbcUpdateQuery(qury);
		return updatedCnt;
        } catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
	}
    
    public long deleteSuccessfullTrx(Long prgmId, Long tierId, String status) {
    	
    	try {
    	String deleteQuery = "DELETE FROM reissue_perks_on_expiry WHERE program_id="+prgmId+" AND tier_id="+tierId+" "
    			+ "AND status='"+status+"'";
    	
    	long count = executeJdbcUpdateQuery(deleteQuery);
		
		return count;
        } catch (Exception e) {
			
			logger.error("Exception ", e);
			return 0;
		}
    	
    }

}
