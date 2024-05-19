package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptMyTemplatesDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	JdbcTemplate jdbcTemplate;
	
	public DigitalReceiptMyTemplatesDaoForDML() {
	}
	
    /*public DigitalReceiptMyTemplate find(Long id) {
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
	
	public void deleteTempLabelById(Long myTemplateId){
		String  query = "DELETE from digital_receipt_my_templates WHERE my_template_id="+myTemplateId;
		 jdbcTemplate.execute(query);
	}
	
	/*
	public List findAll() {
	    return super.findAll(DigitalReceiptMyTemplate.class);
	}*/
	
/*	public List findAllByUserId(Long userId) {
		return getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " ORDER BY modifiedDate DESC ");
	}
	
	 public DigitalReceiptMyTemplate findByUserNameAndTemplateName(long userId, String name){
		 	
		 String templateName = StringEscapeUtils.escapeSql(name);
	    	List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " and name= '" + templateName + "'");
	    	if(templateList.size() > 0)
	    		return templateList.get(0);
	    	else 
	    		return null;
	 }*/
	 
	 public void setNewTemplateName(Long userId, String newTemplateName, String selectedTemplate , String now){
		  newTemplateName = StringEscapeUtils.escapeSql(newTemplateName);
		  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);
			String  query = "Update DigitalReceiptMyTemplate set name='"+newTemplateName+"',  modifiedDate='"+now+"' WHERE name='"+selectedTemplate+"' AND userId="+userId;
			 int count= executeUpdate(query);
			
		 
	 }//setNewTemplateName()
	 
	 
	 public void deleteByUserIdAndName(long userId,String myTemplateName, String folderName,String editorType) {
		 if(folderName!=null) folderName="'"+folderName+"'";
		 getHibernateTemplate().bulkUpdate("DELETE FROM DigitalReceiptMyTemplate WHERE userId="+userId+" AND folderName="+folderName+" AND name ='"+myTemplateName+"' AND editorType='"+editorType+"'");
	 }
	
	/* public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
	    	String sql ="insert into digital_receipts_user_settings(user_id,selected_template_name) values ("+ userId +",'"+ templateName  +"')";
	    	jdbcTemplate.execute(sql);
	 } 
	    
	 public String findUserSelectedTemplate(Long userId) {
	   try {
	    		logger.info("************** User id is  : "+ userId);
	    		int i = getJdbcTemplate().queryForInt("select count(*) from digital_receipts_user_settings where user_id="+userId);
	    		if(i==1) {
	    			String sql = "select selected_template_name from digital_receipts_user_settings where user_id=?" ;
	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, String.class);
	    			return name;
	    		} else return null;	
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
	    
	    public int updateDigiUserSettings(Long userId,String templateName) {
	    	String sql ="update digital_receipts_user_settings set selected_template_name='" +templateName  +"' where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }*/
	
	    
}
