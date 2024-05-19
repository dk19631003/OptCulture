package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SMSCampaignScheduleDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private CampaignReportDao campaignReportDao = null;
	public SMSCampaignScheduleDao() {
			// TODO Auto-generated constructor stub
		}
	 private JdbcTemplate jdbcTemplate;
	    
	    public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	
	/*public void saveOrUpdate(SMSCampaignSchedule smsCampaignSchedule) {
		super.saveOrUpdate(smsCampaignSchedule);
	}*/

	/*public void delete(SMSCampaignSchedule smsCampaignSchedule) {
	    super.delete(smsCampaignSchedule);
	}*/
	
	public List findAll() {
	    return super.findAll(SMSCampaigns.class);
	}	
	 private Long id;
	
	public synchronized Long getCurrentId() {
		
		try {
			if(id == null) {
				
				List list = getHibernateTemplate().find("SELECT MAX(smsCsId) FROM SMSCampaignSchedule ") ;
				if(logger.isDebugEnabled()) logger.debug(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}
	 
	/*public int deleteByCampaignId(Long smsCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM SMSCampaignSchedule WHERE smsCampaignId="+smsCampaignId);
	}*/
	
	
	public List<SMSCampaignSchedule> getActiveList(String currentDateStr) {
		
		return getHibernateTemplate().find(
				" SELECT DISTINCT scs " +
				" FROM SMSCampaignSchedule scs, Users usr " +
				" WHERE scs.userId = usr.userId AND scs.scheduledDate <= '" + currentDateStr + "' " +
				" AND scs.status = 0 AND DATE(scs.scheduledDate) <= DATE(usr.packageExpiryDate) AND usr.enabled = 1 ");
		
	}
	
	public List<SMSCampaignSchedule> getInactiveList(String currentDateStr) {
		
		return executeQuery(
				" SELECT DISTINCT scs " +
				" FROM SMSCampaignSchedule scs, Users usr " +
				" WHERE scs.userId = usr.userId AND scs.userId IS NOT NULL AND scs.scheduledDate <= '" + currentDateStr + "' " +
				" AND scs.status = 0 AND ( DATE(scs.scheduledDate) > DATE(usr.packageExpiryDate) OR usr.enabled = 0) ");
		
	}
	
	public List<SMSCampaignSchedule> getActiveForLongCampList(String currentDateStr,int hrs){
		String qry= " SELECT * FROM SMS_campaign_schedule scs, users usr " +
						" WHERE scs.user_id = usr.user_id AND scs.status = 0 "
						+ "AND (DATE_ADD(scs.scheduled_date,INTERVAL "+hrs+" HOUR)) < '" + currentDateStr + "' "
								+ "AND ( DATE(scs.scheduled_date) <= DATE(usr.package_expiry_date) AND usr.enabled = 1)";
		logger.info("jdbcTemplate>>>>>>>"+jdbcTemplate);
	 	logger.info("qry>>>>>>>"+qry);
	 	
	 	List<SMSCampaignSchedule> list = null;
			try {
				
				list = jdbcTemplate.query(qry, new RowMapper() {

			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	SMSCampaignSchedule smsCampaignSchedule = new SMSCampaignSchedule();
			            
			        	smsCampaignSchedule.setSmsCsId(rs.getLong("sms_cs_id"));
			        	smsCampaignSchedule.setStatus(rs.getByte("status"));
			        	smsCampaignSchedule.setSmsCampaignId(rs.getLong("sms_campaign_id"));
			        	
			        	 if(rs.getDate("scheduled_date") != null) {
				            	Calendar cal = Calendar.getInstance();
				            	cal.setTime(rs.getTimestamp("scheduled_date"));
				            	smsCampaignSchedule.setScheduledDate(cal);
				            	
				            } 
			        	
			        	Users user  = new Users();
						user.setUserId(rs.getLong("user_id"));
						smsCampaignSchedule.setUserId(user.getUserId());
			    		
			            
			            return smsCampaignSchedule;
			        }
			    });
			} catch (Exception e) {
				logger.error("Exception ***", e);
			}
			return list;
}
	/*public int updateDisabledUsersSMSCampaignStatus(String currentDateStr) {
		try {
			String queryStr = "UPDATE SMS_campaign_schedule scs "+
							" JOIN users usr " +
							" ON scs.user_id = usr.user_id " +
							" SET scs.status = 3 " +
							" WHERE scs.scheduled_date <= ' " + currentDateStr + " ' AND scs.status = 0 " +
							" AND (DATE(scs.scheduled_date) > DATE(usr.package_expiry_date) OR usr.enabled = 0 );";
			return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr) );
		} catch(Exception e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while updating the sms campaign status of expired/disabled users", e);
			return 0;
		}
	}*/

	public List<SMSCampaignSchedule> getSMSCampaignSentLastWeek(String fromDate, String tillDate, Long userId) {

		try{
    		String query = null;
    		query="FROM SMSCampaignSchedule WHERE status >= 1 and userId = " +userId + " and  scheduledDate between '" + fromDate + "' AND '" + tillDate + "'";
    		
    		List<SMSCampaignSchedule> list = executeQuery(query);
    		
    		
    		if(list != null && list.size() >0) {
    			return list;
    		}
    		return null;
    		
    	}catch(Exception e) {
    		logger.error("**Exception :", e );
    		return null;
    	}
	}


}
