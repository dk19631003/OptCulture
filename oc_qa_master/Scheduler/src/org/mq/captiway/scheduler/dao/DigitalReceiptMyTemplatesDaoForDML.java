package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.DigitalReceiptMyTemplate;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptMyTemplatesDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	JdbcTemplate jdbcTemplate;
	
	public DigitalReceiptMyTemplatesDaoForDML() {
	}
	/*
    public DigitalReceiptMyTemplate find(Long id) {
	    return (DigitalReceiptMyTemplate) super.find(DigitalReceiptMyTemplate.class, id);
	}*/
	    
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(DigitalReceiptMyTemplate digitalReceiptMyTemplate) {
	    super.saveOrUpdate(digitalReceiptMyTemplate);
	}

	public void delete(DigitalReceiptMyTemplate digitalReceiptMyTemplate) {
	    super.delete(digitalReceiptMyTemplate);
	}
	
	/*public List findAll() {
	    return super.findAll(DigitalReceiptMyTemplate.class);
	}
	
	public List findAllByUserId(Long userId) {
		return getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId );
	}
	
	 public DigitalReceiptMyTemplate findByUserNameAndTemplateName(long userId, String name){
	    	List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " and name= '" + name + "'");
	    	if(templateList.size() > 0)
	    		return templateList.get(0);
	    	else 
	    		return null;
	 }*/
	
	/* public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
	    	String sql ="insert into digital_reciepts_user_settings(user_id,selected_template_name) values ("+ userId +",'"+ templateName  +"')";
	    	jdbcTemplate.execute(sql);
	 } 
	    
	 public String findUserSelectedTemplate(Long userId) {
	   try {
	    		logger.debug("************** User id is  : "+ userId);
	    		int i = getJdbcTemplate().queryForInt("select count(*) from digital_reciepts_user_settings where user_id="+userId);
	    		if(i==1) {
	    			String sql = "select selected_template_name from digital_reciepts_user_settings where user_id=?" ;
	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, String.class);
	    			return name;
	    		} else return null;	
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    
	    public int updateDigiUserSettings(Long userId,String templateName) {
	    	String sql ="update digital_reciepts_user_settings set selected_template_name='" +templateName  +"' where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }*/
	
	    
}
