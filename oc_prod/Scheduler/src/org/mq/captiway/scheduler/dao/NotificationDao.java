package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Notification;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationDao extends AbstractSpringDao{
		
		private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

		public NotificationDao(){}

		private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}


	    @SuppressWarnings("unchecked")
		public List<Notification> findAll() {
	        return super.findAll(Notification.class);
	    }
		
	    /**
	     * this method checks whether the given Notification name is already exists or not.
	     * @param Notification specifies the given Notification name.
	     * @param userId specifies the logged in user id.
	     * @return a boolean variable true if name already exists false if not.
	     */
	    public boolean checkName(String notificationCampName, Long userId){
	    	try{
	    		 List<Notification> list = executeQuery("From Notification where notificationName = '" + notificationCampName + "' and userId = " + userId);
	    		
				if(list.size()>0){
					return true;
				}else{
					return false;
				}
	    	}catch (Exception e) {
	    		logger.error("execption while verifying the notification campaign name"+e);
	    		return false;
			}
	    }
	    
	    
	    public Notification findByCampaignId(Long notificationId) {
	    	try{
		    	List<Notification> campList =  executeQuery(" FROM Notification WHERE notificationId = "+notificationId);
		    	if(campList == null || campList.size() == 0) {
		    		return null;
		    	}
		    	else {
		    		return campList.get(0);
		    	}
	    	}catch (Exception e) {
	    		logger.error("execption :"+e);
	    		return null;
			}
	    }

}
