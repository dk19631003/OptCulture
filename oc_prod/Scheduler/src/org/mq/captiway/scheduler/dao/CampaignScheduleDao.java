package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.EmailContent;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({"unchecked"})
public class CampaignScheduleDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
    public CampaignScheduleDao() {}
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    
    public CampaignSchedule find(Long id) {
        return (CampaignSchedule) super.find(CampaignSchedule.class, id);
    }

    /*public void saveOrUpdate(CampaignSchedule campaignSchedule) {
        super.saveOrUpdate(campaignSchedule);
    }*/

    /*public void delete(CampaignSchedule campaignSchedule) {
        super.delete(campaignSchedule);
    }*/

	public List findAll() {
        return super.findAll(CampaignSchedule.class);
    }
	
	public List<CampaignSchedule> getByCampaignId(Long campaignId) {
		return getHibernateTemplate().find(
				" FROM CampaignSchedule WHERE campaignId="+campaignId+" ORDER BY scheduledDate");
	}
	
	
	
	private Long id;
	
	public synchronized Long getCurrentId() {
		
		try {
			if(id == null) {
				
				List list = getHibernateTemplate().find("SELECT MAX(csId) FROM CampaignSchedule ") ;
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}
	
	/*public int deleteByCampaignId(Long campaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM CampaignSchedule WHERE campaignId="+campaignId);
	}*/
	
	public List<CampaignSchedule> getActiveList(String currentDateStr) {
		
		return getHibernateTemplate().find(
				" SELECT DISTINCT cs " +
				" FROM CampaignSchedule cs, Users usr " +
				" WHERE cs.user.userId = usr.userId AND cs.scheduledDate <= '" + currentDateStr + "' " +
				" AND cs.status = 0 AND DATE(cs.scheduledDate) <= DATE(usr.packageExpiryDate) AND usr.enabled = 1 ");
	}
	
	public List<CampaignSchedule> getInactiveList(String currentDateStr) {
		
		return getHibernateTemplate().find(
				" SELECT DISTINCT cs " +
				" FROM CampaignSchedule cs, Users usr " +
				" WHERE cs.user.userId = usr.userId AND cs.scheduledDate <= '" + currentDateStr + "' " +
				" AND cs.status = 0 AND ( DATE(cs.scheduledDate) > DATE(usr.packageExpiryDate) OR usr.enabled = 0) ");
	}
	
	public List<CampaignSchedule> getActiveForLongCampList(String currentDateStr,int hrs){
		String qry= " SELECT * FROM campaign_schedule cs, users usr " +
				" WHERE cs.user_id = usr.user_id AND cs.status = 0 "
				+ "AND (DATE_ADD(cs.scheduled_date,INTERVAL "+hrs+" HOUR)) < '" + currentDateStr + "' "
						+ "AND DATE(cs.scheduled_date) <= DATE(usr.package_expiry_date) AND usr.enabled = 1";
		logger.info("jdbcTemplate>>>>>>>"+jdbcTemplate);
		logger.info("qry>>>>>>>"+qry);
	
	List<CampaignSchedule> list = null;
	try {
		
		list = jdbcTemplate.query(qry, new RowMapper() {

	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	CampaignSchedule campaignSchedule = new CampaignSchedule();
	            
	        	campaignSchedule.setCsId(rs.getLong("cs_id"));
	        	campaignSchedule.setStatus(rs.getByte("status"));
	        	campaignSchedule.setCampaignId(rs.getLong("campaign_id"));
	        	
	        	 if(rs.getDate("scheduled_date") != null) {
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("scheduled_date"));
		            	campaignSchedule.setScheduledDate(cal);
		            	
		            } 
	        	
	        	Users user  = new Users();
				user.setUserId(rs.getLong("user_id"));
				campaignSchedule.setUser(user);
	    		
	            
	            return campaignSchedule;
	        }
	    });
	} catch (Exception e) {
		logger.error("Exception ***", e);
	}
	return list;
}
	public Long getCrIdbyCSId(Long csId) {
		try {
			List<Long> list = getHibernateTemplate().find(
					" SELECT crId FROM CampaignSchedule WHERE csId="+csId);
			if( list == null || list.size() == 0) {
				return null;
			}
			return list.get(0);
		} catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while getting the crId for csId :"+csId, e);
			return null;
		}
		
	}
	
	public EmailContent getEmailContentByCsId(Long scheduleId) {
		
		List<EmailContent> list = getHibernateTemplate().find(
									" SELECT cs.emailContent FROM CampaignSchedule cs" +
									" WHERE cs.csId="+scheduleId);
		if(list == null || list.size() == 0) {
			return null;
		}
		else {
			return list.get(0);
		}
	}
	
	public boolean isScheduledCampaignByCSId(Long csId) {
		
		List<CampaignSchedule> list = getHibernateTemplate().find("FROM CampaignSchedule WHERE csId="+csId +" AND status !=2");
		if(list != null && list.size() >0) {
			return true;
		}
		return false;
	}

	/*public int updateDisabledUsersCampaignStatus(String currentDateStr) {
		try {
			String queryStr = "UPDATE campaign_schedule cs "+
							" JOIN users usr " +
							" ON cs.user_id = usr.user_id " +
							" SET cs.status = 3 " +
							" WHERE cs.scheduled_date <= ' " + currentDateStr + " ' AND cs.status = 0 " +
							" AND (DATE(cs.scheduled_date) > DATE(usr.package_expiry_date) OR usr.enabled = 0 );";
			return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr) );
		} catch(Exception e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while updating the campaign status of expired/disabled users", e);
			return 0;
		}
	}*/
	
	public List<CampaignSchedule> getCampaignSentLastWeek(String fromDate, String tillDate, Long userId) {

		try{
    		String query = null;
    		query="FROM CampaignSchedule WHERE status >= 1 and user = " +userId + " and  scheduledDate between '" + fromDate + "' AND '" + tillDate + "'";
    		
    		List<CampaignSchedule> list = executeQuery(query);
    		
    		
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
