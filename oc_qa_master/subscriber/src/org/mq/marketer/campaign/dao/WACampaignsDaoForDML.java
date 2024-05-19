package org.mq.marketer.campaign.dao;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.WACampaign;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;


public class WACampaignsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
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
    
    
    public void deleteByCollection(Collection list){
		getHibernateTemplate().deleteAll(list);
	}
    
    
 
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
 public int deleteByWACampaignId(String WACampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM WACampaign WHERE waCampaignId in("+WACampaignId+")");
	}
 
 public int deleteByCampaignIdFromIntermediateTable(String waCampaignId) {
 	
 	String qry = "DELETE FROM mlists_wa_campaigns WHERE wa_campaign_id in("+waCampaignId+")";
 	return getJdbcTemplate().update(qry);
 	
 }
    
}
