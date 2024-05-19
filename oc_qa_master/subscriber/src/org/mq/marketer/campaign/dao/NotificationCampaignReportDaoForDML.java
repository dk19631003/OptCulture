package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignReportDaoForDML extends AbstractSpringDaoForDML {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public NotificationCampaignReportDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
 	
    public void saveOrUpdate(NotificationCampaignReport notificationCampaignReport) {
        super.saveOrUpdate(notificationCampaignReport);
    }

    public void delete(NotificationCampaignReport notificationCampaignReport) {
        super.delete(notificationCampaignReport);
    }

    public void deleteByCollection(Collection notificationCampaignReportCollection){
    	getHibernateTemplate().deleteAll(notificationCampaignReportCollection);
    }
    
    public int updateBounceReport(Long notificationCrId) {
    	
    	try {
			String qry = "UPDATE notification_campaign_report cs " +
						" JOIN (SELECT count(sent_id) cnt,notification_cr_id FROM Notification_campaign_sent" +
			    				" WHERE notification_cr_id =" + notificationCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
			    				"') o " +
						" ON cs.notification_cr_id = o.notification_cr_id" +
						" SET cs.bounces=o.cnt where cs.notification_cr_id="+notificationCrId.longValue();

			return jdbcTemplate.update(qry);
			
		} catch (DataAccessException e) {
			logger.error("Exception ",e);
			return 0;
			
		}
    	
    	
    	
    }
    
    public int updateNoticationCampaignReport(Long crId, String type) {
	   	try {
			    	String queryStr = null;
			    	
			    	
			    	if(type.equals(Constants.CR_TYPE_CLICKS))
			    		queryStr = "update NotificationCampaignReport set " + Constants.CR_TYPE_CLICKS + 
			    		" = (SELECT count(sentId) FROM NotificationCampaignSent WHERE notificationCampaignReport =" + crId + 
			    		" AND " + Constants.CS_TYPE_CLICKS + " > 0) WHERE notificationCrId =" + crId;
			    	
			    	logger.info("Ravai click Report"+queryStr);
			    	
			    	
			    	
			
			    	return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
	   	} catch(Exception e) {
	   		logger.error("** Exception while updateing the reports : crId : "+crId, e);

	   		return 0;
	   	}
	   }
}
