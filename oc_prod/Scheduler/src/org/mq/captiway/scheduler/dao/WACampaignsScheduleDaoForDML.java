package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignsSchedule;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignsScheduleDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public void saveOrUpdate(WACampaignsSchedule waCampaignsSchedule) {
		super.saveOrUpdate(waCampaignsSchedule);
	}

	public void delete(WACampaignsSchedule waCampaignsSchedule) {
		super.delete(waCampaignsSchedule);
	}



	public int deleteByCampaignId(Long waCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM WACampaignsSchedule WHERE waCampaignId="+waCampaignId);
	}



	public int updateDisabledUsersWACampaignStatus(String currentDateStr) {
		try {
			String queryStr = "UPDATE wa_campaign_schedule wcs "+
					" JOIN users usr " +
					" ON wcs.user_id = usr.user_id " +
					" SET wcs.status = 3 " +
					" WHERE wcs.scheduled_date <= ' " + currentDateStr + " ' AND wcs.status = 0 " +
					" AND (DATE(wcs.scheduled_date) > DATE(usr.package_expiry_date) OR usr.enabled = 0 );";
			return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr) );
		} catch(Exception e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while updating the wa campaign status of expired/disabled users", e);
			return 0;
		}
	}



}
