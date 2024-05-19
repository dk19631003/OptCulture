package org.mq.marketer.campaign.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

import org.mq.marketer.campaign.beans.WACampaignsSchedule;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignScheduleDaoForDML extends AbstractSpringDaoForDML {

private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
		public WACampaignScheduleDaoForDML() {
			
		}

	public void saveOrUpdate(WACampaignsSchedule waCampaignSchedule) {
		super.saveOrUpdate(waCampaignSchedule);
	}

	public void delete(WACampaignsSchedule waCampaignSchedule) {
	    super.delete(waCampaignSchedule);
	}
	
	
	public void deleteByCollection(List<WACampaignsSchedule> waCampScheduleList) {
		logger.debug(">>>>>>> Started WACampaignScheduleDao :: deleteByCollection <<<<<<< ");
		getHibernateTemplate().deleteAll(waCampScheduleList);
		logger.debug(">>>>>>> Completed WACampaignScheduleDao :: deleteByCollection <<<<<<< ");
		
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
	
	public int deleteByCampaignId(String waCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM WACampaignsSchedule WHERE waCampaignId in("+waCampaignId+")");
	}

	public void deleteByCampSchId(Long waCsId) {
		logger.debug(">>>>>>> Started WACampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
		 String qry  = " DELETE FROM WACampaignsSchedule where waCsId ="+waCsId+ " or parentId ="+waCsId;
		 logger.info(">>>>>>>>>>> qry is  ::"+qry);
		 getHibernateTemplate().bulkUpdate(qry);
		 logger.debug(">>>>>>> Completed WACampaignScheduleDao :: deleteByCampSchId <<<<<<< ");
	 }
	
}
