package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialCampaignSchedule;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SocialCampaignScheduleDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public SocialCampaignScheduleDao() {
		// TODO Auto-generated constructor stub
	}
	
	private JdbcTemplate jdbcTemplate;
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<SocialCampaignSchedule> findActiveSchedulesByCampId(Long socialCampaignId) {
		return getHibernateTemplate().find(" FROM SocialCampaignSchedule WHERE campaignId="+ socialCampaignId +" AND  NOW() >= scheduleDate AND (scheduleStatus='"+
				Constants.SOCIAL_SCHEDULE_STATUS_POSTNOW +"' OR scheduleStatus='"+ Constants.SOCIAL_SCHEDULE_STATUS_SCHEDULE +"') ");
	}
	
   /* public void saveOrUpdate(SocialCampaignSchedule socialCampaignSchedule) {
        super.saveOrUpdate(socialCampaignSchedule);
    }

    public void delete(SocialCampaignSchedule socialCampaignSchedule) {
        super.delete(socialCampaignSchedule);
    }*/

    public List findAll() {
        return super.findAll(SocialCampaignSchedule.class);
    }
    
    public List<SocialCampaignSchedule> findFailedScheduleByCampaignId(Long campaignId) {
    	
    	List list = getHibernateTemplate().find(" FROM SocialCampaignSchedule WHERE campaignId='"+campaignId +"' AND  scheduleStatus='"+ Constants.SOCIAL_SCHEDULE_STATUS_FAILED+"'");
    	return list;
     }
    
   /* public void addScheduleFailure(Long scheduleId, Long campaignId, String provider,
    		String fbPageIds,String errorMsg,int retry_count,String failureAction) {
    	
    	String qry = "INSERT INTO social_campaign_schedule_failed(schedule_id,campaign_id,provider_type,fb_page_ids,retry_count,error_message,failure_action) VALUES ("+ scheduleId +","+ campaignId +
    				 ",'"+ provider + "','" + fbPageIds  + "', "+ retry_count +" ,'" + errorMsg  + "','" + failureAction + "')";
    	logger.info("*** UPDATE failed status SQL QUERY : "+ qry);
    	jdbcTemplate.execute(qry);
    }
    
    public int updateScheduleFailure(Long failureId,Long scheduleId, Long campaignId, String provider,
    		String fbPageIds,int retryCount,String errorMsg,String failureAction) {
    	
    	String qry = "UPDATE social_campaign_schedule_failed SET schedule_id=" + scheduleId +
    			", campaign_id=" + campaignId +
    			", provider_type='" + provider + "'" +
    			", fb_page_ids='" + fbPageIds + "'" +
    			", error_message='" +  errorMsg + "'" +
    			", retry_count="+ retryCount +
    			", failure_action='" + failureAction  + "' " +
    			" WHERE id="+failureId;
    			
    			" ("+ scheduleId +","+ campaignId +
    				 ",'"+ provider + "','" + fbPageIds  + "','" + errorMsg  + "','" + failureAction + "')";
    	logger.info("*** UPDATE failed status SQL QUERY : "+ qry);
    	int rowsUpdated = jdbcTemplate.update(qry);
    	return rowsUpdated;
    }
    
    public void deleteScheduleFailureRecord(Long id) {
    	String qry ="delete from social_campaign_schedule_failed where id="+ id;
    	jdbcTemplate.execute(qry);
    }
    
    public void updateScheduleFailureAction(Long id,String status) {
    	String qry ="update social_campaign_schedule_failed SET failure_action='" + status + "' where id="+ id ;
    	jdbcTemplate.execute(qry);
    }*/
    
    public List getScheduleFailures(Long scheduleId) {
    	
    	String qry = "SELECT * FROM social_campaign_schedule_failed WHERE schedule_id="+ scheduleId + " AND failure_action='"+ Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY+"'";
    	
    	logger.info("<<<<!!!!! qyery : "+ qry);
    	
    	List<Object[]> scheduleFailedList = jdbcTemplate.query(qry,new RowMapper(){
			Object[] obj;
			@Override
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				obj = new Object[8];
				obj[0] = rs.getLong(1);
				obj[1] = rs.getLong(2);
				obj[2] = rs.getLong(3);
				obj[3] = rs.getString(4);
				obj[4] = rs.getString(5);
				obj[5] = rs.getString(6);
				obj[6] = rs.getInt(7);
				obj[7] = rs.getString(8);
				
				return obj;
			}
			
		});
		
		return scheduleFailedList;
    	
    }
    
}
