package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;

public class UserFromEmailIdDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public UserFromEmailIdDao() {}
	
	public UserFromEmailId find(Long id){
        return (UserFromEmailId) super.find(UserFromEmailId.class, id);
    }

	public boolean checkEmailId(UserFromEmailId userFromEmailId) {
		try {
			List<UserFromEmailId> list = getHibernateTemplate().find("from UserFromEmailId where emailId='" + userFromEmailId.getEmailId()+"'");
			if(list.size()>0) {
				return true;
			}	
			else {
				return false;
			}	
		} catch(Exception e) {
			logger.error("Exception : An Error has occured while fetching Data . "+e);
			return false;
		}
	}
/*
    public void saveOrUpdate(UserFromEmailId userFromEmailId){
        super.saveOrUpdate(userFromEmailId);
    }
    */

    /*public void delete(UserFromEmailId userFromEmailId){
        super.delete(userFromEmailId);
    }
    */
    public List<UserFromEmailId> getEmailsByUserId(Long userId) {
    	logger.debug("--Just Entered--:UserId :"+userId);
    	try {
			return (List<UserFromEmailId>)getHibernateTemplate().find("FROM UserFromEmailId WHERE  users= "+ userId+" AND status=1");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<UserFromEmailId> getEmailsWithoutConsideringStatusByUserId(Long userId) {
    	logger.debug("--Just Entered--:UserId :"+userId);
    	try {
			return (List<UserFromEmailId>)getHibernateTemplate().find("FROM UserFromEmailId WHERE  users= "+ userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public UserFromEmailId checkEmailId(String emailId,Long userId) {
		try {
			List list = getHibernateTemplate().find("FROM UserFromEmailId WHERE emailId='" + emailId +"' AND users="+ userId);
			if(list.size()>0) {
				return (UserFromEmailId)list.get(0);
			}	
			else {
				return null;
			}	
		} catch(Exception e) {
			logger.error("Exception : An Error has occured while fetching Data . "+e);
			return null;
		}
	}
    
    
    public UserFromEmailId checkFromEmailIdForStore(String emailId,String homeStoreId, Long userId) {
		try {
			List list = getHibernateTemplate().find("FROM UserFromEmailId WHERE users="+userId+" AND storeFromEmailId='" + emailId +"' AND homeStoreId='"+ homeStoreId+"'");
			if(list.size()>0) {
				return (UserFromEmailId)list.get(0);
			}	
			else {
				return null;
			}	
		} catch(Exception e) {
			logger.error("Exception : An Error has occured while fetching Data . "+e);
			return null;
		}
	}
    
    public UserFromEmailId checkToEmailIdForStore(String emailId,String homeStoreId, Long userId) {
		try {
			List list = getHibernateTemplate().find("FROM UserFromEmailId WHERE users="+userId+" AND storeReplyToEmailId='" + emailId +"' AND homeStoreId='"+ homeStoreId+"'");
			if(list.size()>0) {
				return (UserFromEmailId)list.get(0);
			}	
			else {
				return null;
			}	
		} catch(Exception e) {
			logger.error("Exception : An Error has occured while fetching Data . "+e);
			return null;
		}
	}
    
    public List<UserFromEmailId> getFromAndReplyToEmailsOfStoreByUserId(Long userId) {
    	logger.debug("--Just Entered--:UserId :"+userId);
    	try {
			return (List<UserFromEmailId>)getHibernateTemplate().find("FROM UserFromEmailId WHERE  users= "+ userId+" AND status=1");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<Object> findPosLocId (Long userId){
   	 try {
   			String query = " SELECT  DISTINCT homeStoreId FROM UserFromEmailId  WHERE users ="+userId.longValue()+" AND  homeStoreId IS NOT null";
   				List<Object> posLocList = getHibernateTemplate().find(query);
   				if(posLocList.size() < 0){
   					
   					return null;
   				}else{
   					return posLocList;
   				}
   		} catch (DataAccessException e) {
   			// TODO Auto-generated catch block
   			logger.error("Exception ::" , e);
   			return null;
   		}
   	
   }
}
