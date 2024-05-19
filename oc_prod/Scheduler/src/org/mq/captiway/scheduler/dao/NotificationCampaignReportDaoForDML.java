package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationCampaignReport;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignReportDaoForDML extends AbstractSpringDaoForDML {
	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	 

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
	    
	    public int updateReport(Long crId,String type,int num){
	    	return jdbcTemplate.update("update notification_campaign_report set  " + type + " = " + type + "+"+num+" where notification_cr_id = "+crId);
	     }
	    

	    public int updateClickReport(Long notificationCrId) throws BaseServiceException{
	    	try {
				String qry = "UPDATE notification_campaign_report cs " +
							" JOIN (SELECT count(sent_id) cnt,notification_cr_id FROM Notification_campaign_sent" +
				    				" WHERE notification_cr_id =" + notificationCrId.longValue() + " AND clicks > 0) o " +
							" ON cs.notification_cr_id = o.notification_cr_id" +
							"	SET cs.clicks=o.cnt where cs.notification_cr_id="+notificationCrId.longValue();

				return jdbcTemplate.update(qry);
				
			} catch (DataAccessException e) {
				throw new BaseServiceException("Exception in updating the report");
				
			}
	    }


}
