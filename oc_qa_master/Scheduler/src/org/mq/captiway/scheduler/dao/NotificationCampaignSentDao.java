package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationCampaignSent;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignSentDao extends AbstractSpringDao {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	private Long currentSentId;

	public NotificationCampaignSentDao() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NotificationCampaignSent find(Long id) {
		return (NotificationCampaignSent) super.find(NotificationCampaignSent.class, id);
	}

	public List findAll() {
		return super.findAll(NotificationCampaignSent.class);
	}

	public synchronized Long getCurrentSentId() {
		
		try {
			Long currentSentId = null;
			 currentSentId =  (Long)executeQuery("SELECT MAX(sentId) FROM NotificationCampaignSent ").get(0);
			if(logger.isDebugEnabled()) logger.debug("current sent id in dao is=====>"+currentSentId);
			if(currentSentId == null) {
				currentSentId = 0l;
			}
			this.currentSentId = currentSentId;
			return currentSentId;
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error(" ** Exception : while getting the current sentId from the Database so returning" +
					"currentSentId value as -"+this.currentSentId+100000, e);
			return this.currentSentId+100000;
		}
		
	}
	
	public long getSentCount(Long notificationCrId) {
		String qry = "select count(sentId) from NotificationCampaignSent where notificationCampaignReport="+notificationCrId+ "AND status NOT IN ('"+OCConstants.SMS_NOT_SUBMITTED+"')";
		try{
			return ((Long)executeQuery(qry).get(0)).longValue();
		}catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception",e);
			return 0;
		}
	}
	
}
