package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserFromEmailId;
import org.mq.captiway.scheduler.utility.Constants;

public class UserFromEmailIdDaoForDML extends AbstractSpringDaoForDML  {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public UserFromEmailIdDaoForDML () {}
	/*
	public UserFromEmailId find(Long id){
        return (UserFromEmailId) super.find(UserFromEmailId.class, id);
    }
*/
	
    public void saveOrUpdate(UserFromEmailId userFromEmailId){
        super.saveOrUpdate(userFromEmailId);
    }
    

    public void delete(UserFromEmailId userFromEmailId){
        super.delete(userFromEmailId);
    }
    
   /* public List<UserFromEmailId> getEmailIdByUserId(long userId,String email) {
    	List list = getHibernateTemplate().find("from UserFromEmailId where users="+ userId+" and emailId='"+ email +"'");
    	if(list.size() > 0) {
    		return list;
    	} else {
    		if(logger.isDebugEnabled()) logger.debug("No record found to update.");
    		return null;
    	}
    }*/
    
   
   
}
