package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mq.marketer.campaign.general.Constants;

public class WACampaignReportDaoForDML extends AbstractSpringDaoForDML {
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public WACampaignReportDaoForDML() {}

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
 	
	private WACampaignSentDaoForDML waCampaignSentDao = null;
	

public WACampaignSentDaoForDML getWaCampaignSentDao() {
		return waCampaignSentDao;
	}

	public void setWaCampaignSentDao(WACampaignSentDaoForDML waCampaignSentDao) {
		this.waCampaignSentDao = waCampaignSentDao;
	}

	public void saveOrUpdate(WACampaignReport waCampaignReport) {
		super.saveOrUpdate(waCampaignReport);
	}

	public void delete(WACampaignReport waCampaignReport) {
		super.delete(waCampaignReport);
	}	    

 public void deleteByCollection(Collection waCampaignReportCollection){
    	getHibernateTemplate().deleteAll(waCampaignReportCollection);
    }
    
	public int updateBounceReport(Long waCrId) {

		try {
			String qry = "UPDATE wa_campaign_report cs " +
					" JOIN (SELECT count(sent_id) cnt,wa_cr_id FROM wa_campaign_sent" +
					" WHERE wa_cr_id =" + waCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
					"') o " +
					" ON cs.wa_cr_id = o.wa_cr_id" +
					"	SET cs.bounces=o.cnt where cs.wa_cr_id="+waCrId.longValue();

			return jdbcTemplate.update(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
			return 0;

		}
	}

	public int updateClickReport(Long waCrId) throws BaseServiceException{

		try {
			String qry = "UPDATE wa_campaign_report cs " +
					" JOIN (SELECT count(sent_id) cnt,wa_cr_id FROM wa_campaign_sent" +
					" WHERE wa_cr_id =" + waCrId.longValue() + " AND clicks > 0) o " +
					" ON cs.wa_cr_id = o.wa_cr_id" +
					"	SET cs.clicks=o.cnt where cs.wa_cr_id="+waCrId.longValue();

			return jdbcTemplate.update(qry);

		} catch (DataAccessException e) {
			throw new BaseServiceException("Exception in updating the report");

		}    	

	}


}
