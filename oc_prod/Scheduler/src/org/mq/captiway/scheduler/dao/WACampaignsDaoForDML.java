package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaign;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class WACampaignsDaoForDML extends AbstractSpringDaoForDML {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public WACampaignsDaoForDML(){}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public void saveOrUpdate(WACampaign waCampaign) {
		super.saveOrUpdate(waCampaign);
	}

	public void delete(WACampaign waCampaign) {
		super.delete(waCampaign);
	}



	/**
	 * Updates the  WA Campaign's status to either 'Sent' or 'Running' based on 
	 * the no of active schedules for this WA  campaign in wa_campaign_schedule.<BR><BR> 
	 * If for this waCampaignId in wa_campaign_schedule table the number of 
	 * rows exists with status as '0' that means active schedules then the 
	 * WA campaign will be updated as 'Running' WA campaign if no rows exists with
	 * the status as '0' then the campaign will be updated as 'Sent'
	 * 
	 * @param waCampaignId -Id(Long) of the campaign. 
	 */

	public void updateWACampaignStatus(Long waCampaignId) {

		String qryStr = 
				" UPDATE wa_campaign c SET c.status = ( SELECT IF(count(cs.status)>0,'Running','Sent')" +
						" FROM wa_campaign_schedule cs WHERE cs.wa_campaign_id="+waCampaignId+" AND cs.status=0)" +
						" WHERE c.wa_campaign_id="+waCampaignId ;
		int result = executeJdbcUpdateQuery(qryStr);

		if(result <= 0) {
			if(logger.isWarnEnabled()) logger.warn("WA Campaign status could not be updated for the wa campaign id :"+waCampaignId);
		}

	}

}
