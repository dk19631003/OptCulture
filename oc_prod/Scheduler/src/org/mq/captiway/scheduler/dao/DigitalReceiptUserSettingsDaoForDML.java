package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.DigitalReceiptUserSettings;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptUserSettingsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	JdbcTemplate jdbcTemplate;
	public DigitalReceiptUserSettingsDaoForDML(){
		
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	  public void saveOrUpdate(DigitalReceiptUserSettings digitalReceiptUserSettings){
	        super.saveOrUpdate(digitalReceiptUserSettings);
	    }
	
	
	 public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
	    	String sql ="insert into digital_receipt_user_settings(user_id,selected_template_name) values ("+ userId +",'"+ templateName  +"')";
	    	jdbcTemplate.execute(sql);
	 } 
	/*    
	 public String findUserSelectedTemplate(Long userId) {
	   try {
	    		if(logger.isDebugEnabled()) logger.debug("************** User id is  : "+ userId);
	    		int i = getJdbcTemplate().queryForInt("select count(*) from digital_receipt_user_settings where user_id="+userId);
	    		if(i==1) {
	    			String sql = "select selected_template_name from digital_receipt_user_settings where user_id=?" ;
	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, String.class);
	    			return name;
	    		} else return null;	
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    */
	    public int updateDigiUserSettings(Long userId,String templateName) {
	    	String sql ="update digital_receipt_user_settings  set selected_template_name='" +templateName  +"' where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }
	
	/*public String findByUserId(Long UserId){
		String sql="select selected_template_name from digital_receipt_user_settings where user_id=?";
		getHibernateTemplate().find(sql);
		return null;
	}*/
	   /* public DigitalReceiptUserSettings findByUserId(Long userId){
	    	//logger.debug("User Id  >>"+userId);
	    	List<DigitalReceiptUserSettings> list= getHibernateTemplate().find("FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	//logger.debug("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return list.get(0);
	    	}
	    	return null;
	    }
	   
	    
	    
	    public boolean findIsUserEnabled(Long userId) {
	    	
	    	List<Boolean> list= getHibernateTemplate().find(" SELECT enabled FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	//logger.debug("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return (list.get(0)).booleanValue();
	    	}
	    	return false;
	    	
	    	
	    	
	    	
	    }*/
}
