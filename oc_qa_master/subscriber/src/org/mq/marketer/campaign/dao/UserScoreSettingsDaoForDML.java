package org.mq.marketer.campaign.dao;


import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.EmailClient;
import org.mq.marketer.campaign.beans.UserScoreSettings;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserScoreSettingsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public UserScoreSettingsDaoForDML(){}
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
   /* public UserScoreSettings find(Long id) {
        return (UserScoreSettings) super.find(UserScoreSettings.class, id);
    }*/

    public void saveOrUpdate(UserScoreSettings userScoreSettings) {
        super.saveOrUpdate(userScoreSettings);
    }

    public void delete(UserScoreSettings userScoreSettings) {
        super.delete(userScoreSettings);
    }
/*
    public List<EmailClient> findAll() {
        return super.findAll(UserScoreSettings.class);
    }
    
    public List getUserScoreSetting(Long userId){
    	logger.debug("just enter userScoreSettingDao");
    	return getHibernateTemplate().find("from UserScoreSettings where user = " + userId +" ORDER BY id");
		
    }*/
    
    public void deleteByCollection(Collection list){
		getHibernateTemplate().deleteAll(list);
	}
    
  /*  
	public  UserScoreSettings findByUserGropNameCount(Long userId, String scoreGroupName, String dataOne) {
		dataOne = StringEscapeUtils.escapeSql(dataOne);
	    	List<UserScoreSettings> list = getHibernateTemplate().find("from UserScoreSettings us where us.groupName='"
							+scoreGroupName+"' and us.user="+ userId +" and us.dataOne ='"+ dataOne + "'");
	    	
	    	if(list != null && list.size() > 0) {
	    		return list.get(0);
	    	} else 
	    		return null;
	    	
	    }
	public List<UserScoreSettings> findByUserGroupNameRecrd(Long userId, String scoreGroupName){
	     
	     return getHibernateTemplate().find("from UserScoreSettings us  where us.user="+ userId +" and us.groupName='"
					+scoreGroupName+"' ");
	    }*/
}
