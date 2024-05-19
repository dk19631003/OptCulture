package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignReport;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignReportDaoForDML extends AbstractSpringDaoForDML {
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);


	public WACampaignReportDaoForDML() {}

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(WACampaignReport waCampaignReport) {
		super.saveOrUpdate(waCampaignReport);
	}

	public void delete(WACampaignReport waCampaignReport) {
		super.delete(waCampaignReport);
	}	    

	public int updateBounceReportNew(Long waCrId) {

		try {
			/*String qry = "UPDATE wa_campaign_report cs " +
					" JOIN (SELECT count(sent_id) cnt,wa_cr_id FROM wa_campaign_sent" +
					" WHERE wa_cr_id =" + waCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
					"') o " +
					" ON cs.wa_cr_id = o.wa_cr_id" +
					"	SET cs.bounces=o.cnt where cs.wa_cr_id="+waCrId.longValue();*/
			
			String qry = "UPDATE wa_campaign_report "
					+ "SET bounces=bounces+1 where wa_cr_id="+waCrId.longValue();
			
			logger.debug("updateBounceReport qry : "+qry);
			return jdbcTemplate.update(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
			return 0;

		}
	}//updateBounceReportNew
	
	public int updateBounceReport(Long waCrId) {

		try {
			String qry = "UPDATE wa_campaign_report cs " +
					" JOIN (SELECT count(sent_id) cnt,wa_cr_id FROM wa_campaign_sent" +
					" WHERE wa_cr_id =" + waCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
					"') o " +
					" ON cs.wa_cr_id = o.wa_cr_id" +
					"	SET cs.bounces=o.cnt where cs.wa_cr_id="+waCrId.longValue();
			
			/*String qry = "UPDATE wa_campaign_report "
					+ "SET bounces=bounces+1 where wa_cr_id="+waCrId.longValue();*/
			
			logger.debug("updateBounceReport qry : "+qry);
			return jdbcTemplate.update(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
			return 0;

		}
	}//updateBounceReport

	/*public int updateClickReport(Long waCrId) throws BaseServiceException{
	
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
	
	}*/


}
