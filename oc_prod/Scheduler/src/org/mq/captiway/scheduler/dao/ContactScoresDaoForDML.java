package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactScores;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class ContactScoresDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public ContactScoresDaoForDML(){}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/* public ContactScores find(Long id) {
	        return (ContactScores) super.find(ContactScores.class, id);
    }*/

    public void saveOrUpdate(ContactScores contactScores) {
        super.saveOrUpdate(contactScores);
    }

    public void delete(ContactScores contactScores) {
        super.delete(contactScores);
    }
	
   /* public ContactScores getContactScoreByEmailId(Long userId, String emailId) {
    	if(logger.isDebugEnabled()) logger.debug("user id is :"+userId + " email id :"+ emailId);
    	List<ContactScores> list = getHibernateTemplate().find("FROM ContactScores WHERE user="+ userId + " and emailId='" + emailId + "'" );
    	if(list == null || list.size() <1) {
    		return null;
    	}
    	return list.get(0);
    }
    */
    /*public void saveOrUpdateContact(Long userId,String emailId,int userSettingCount) {
    	
		String query = "select * from contact_score where user_id="+ userId +" and  email_id ='"+emailId+ "'";
		int existedRecord=super.executeQuery(query);
		
		if(existedRecord==0) {
			
			query="insert into contact_score(email_Id,user_id) values('"+emailId +"',"+userId+")";
			int count=super.executeQuery(query);
			query ="update contact_score set "+userSettingCount+" ="+1 +" where user_id="+ userId +" and  email_id ='"+emailId+ "'";
			count=super.executeQuery(query);
			if(logger.isDebugEnabled()) logger.debug("Updated  ContactScoreSetting Record(1)");
		}
		else {
			query ="update contact_score set "+userSettingCount+"="+userSettingCount+"+"+1 +
					"where user_id="+ userId +" and  email_id ='"+emailId+ "'";
			if(logger.isDebugEnabled()) logger.debug("Updated  ContactScoreSetting Record(2)");
		}  
    	
    }*/
	
}
