
package org.mq.marketer.campaign.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.DRSMSChannelSent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSMSChannelSentDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(DRSMSChannelSent drSmsChannelSent) {
		super.saveOrUpdate(drSmsChannelSent);
	}

	public void delete(DRSMSChannelSent drSmsChannelSent) {
		super.delete(drSmsChannelSent);
	}
		/**
	/**
	 * update click count whenever user clicks on link in DR
	 * @param sentId
	 * @return
	 */
	public int updateClickCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSChannelSent SET clicks = (clicks+1) WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return 0;
		}
	
	}//updateClickCount()

	
	public synchronized void updateSentCount(Long existingObjId) throws BaseDAOException{
		
		String qry = "UPDATE dr_sms_channel_sent set sent_count=(sent_count+1) WHERE id="+existingObjId.longValue();
		
		executeJdbcUpdateQuery(qry);
		
	}
	
	public int updateOpenCount(Long sentId) {
		try {
			String qry=" UPDATE DRSMSChannelSent SET opens = (opens+1) WHERE id = "+ sentId.longValue();
	
			return executeUpdate(qry);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return 0;
		}
	
	}//updateOpenCount
	
	public int updateDeliveryStatus(Long sentId) {
		try {
			String qry=" UPDATE DRSMSChannelSent SET status = '"+Constants.SMS_STATUS_DELIVERED+"' WHERE id = "+ sentId.longValue();
					
			return executeUpdate(qry);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return 0;
		}
		
	}//updateOpenCount
	
	public void updateStatus(String status,Long sentId) {	
		
		String updateQry = " UPDATE auto_sms_queue SET dlr_status='"+status+"' WHERE id="+sentId+"";				
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
	
}
