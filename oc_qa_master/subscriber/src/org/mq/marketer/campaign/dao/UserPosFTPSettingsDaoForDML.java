package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;
import org.mq.marketer.campaign.beans.UserPosFTPSettings;
import org.springframework.jdbc.core.JdbcTemplate;


public class UserPosFTPSettingsDaoForDML extends AbstractSpringDaoForDML{
	
	 public UserPosFTPSettingsDaoForDML(){}

	    private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
		
	    /*public UserPosFTPSettings find(Long id){
	        return (UserPosFTPSettings) super.find(UserPosFTPSettings.class, id);
	    }*/

	    public void saveOrUpdate(UserPosFTPSettings userPosFTPSettings){
	        super.saveOrUpdate(userPosFTPSettings);
	    }
	    
	    public void saveByCollection(List<UserPosFTPSettings> posFTPSettings) {
	    	super.saveByCollection(posFTPSettings);
	    }
	    
	    public void delete(UserPosFTPSettings userPosFTPSettings){
	        super.delete(userPosFTPSettings);
	    }
	    
	    /*public List<UserPosFTPSettings> findByUserId(long userId) {
	    	List<UserPosFTPSettings> posSettingList = null;
	    	posSettingList = getHibernateTemplate().find("FROM UserPosFTPSettings WHERE userId="+userId);
	    	
	    	if(posSettingList != null && posSettingList.size() >0) {
	    		return posSettingList;
	    	}else{
	    		return null;
	    	}
	    }
	    
	    public List<UserPosFTPSettings> salesSettingsFindByUserId(long userId) {
	    	List<UserPosFTPSettings> posSettingList = null;
	    	posSettingList = getHibernateTemplate().find("FROM UserPosFTPSettings WHERE fileType = 'Sales' and userId="+userId);
	    	
	    	if(posSettingList != null && posSettingList.size() >0) {
	    		return posSettingList;
	    	}else{
	    		return null;
	    	}
	    }
	    
	    public UserPosFTPSettings findByName(String ftpName){
	    	List<UserPosFTPSettings> posSettingList = null;
	    	posSettingList = getHibernateTemplate().find("FROM UserPosFTPSettings WHERE  fileFormat='"+ftpName+"'");
	    	
	    	if(posSettingList != null && posSettingList.size() >0) {
	    		return posSettingList.get(0);
	    	}else{
	    		return null;
	    	}
	    }
	    
	    */
}
