package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptUserSettingsDaoForDML extends AbstractSpringDaoForDML {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
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
	  
	  public void delete(DigitalReceiptUserSettings digitalReceiptUserSettings) {
	        super.delete(digitalReceiptUserSettings);
	    }
	
	
	 public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
		 templateName = StringEscapeUtils.escapeSql(templateName);
	    	String sql ="insert into digital_receipt_user_settings(user_id,selected_template_name) values ("+ userId +",'"+ templateName  +"')";
	    	jdbcTemplate.execute(sql);
	 } 
	    
	/* public String findUserSelectedTemplate(Long userId) {
	   try {
	    		logger.info("************** User id is  : "+ userId);
	    		int i = getJdbcTemplate().queryForInt("select count(*) from digital_receipt_user_settings where user_id="+userId);
	    		if(i==1) {
	    			String sql = "select selected_template_name from digital_receipt_user_settings where user_id=?" ;
	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { userId }, String.class);
	    			logger.info(""+name);
	    			return name;
	    		} else return null;	
	    	} catch(Exception e) {
	    		return null;
	    	}
	    }
*/	    
	    public int updateDigiUserSettings(Long userId,String templateName) {
	    	 templateName = StringEscapeUtils.escapeSql(templateName);
	    	String sql ="update digital_receipt_user_settings  set selected_template_name='" +templateName  +"' where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }
	
	/*public String findByUserId(Long UserId){
		String sql="select selected_template_name from digital_receipt_user_settings where user_id=?";
		getHibernateTemplate().find(sql);
		return null;
	}*/
	   /* public DigitalReceiptUserSettings findByUserId(Long userId){
	    	logger.info("User Id  >>"+userId);
	    	List<DigitalReceiptUserSettings> list= getHibernateTemplate().find("FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	logger.info("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return list.get(0);
	    	}
	    	return null;
	    }
	   
	    
	    
	    public boolean findIsUserEnabled(Long userId) {
	    	
	    	List<Boolean> list= getHibernateTemplate().find(" SELECT enabled FROM DigitalReceiptUserSettings WHERE userId="+userId);
	    	//logger.info("list >>"+list.size());
	    	if(list != null && list.size() > 0 ){
	    		return (list.get(0)).booleanValue();
	    	}
	    	return false;
	    	
	    	
	    	
	    	
	    }
	    
	    //unused
	    public Long findTemplateByName(Long userId, String name){
	    	name = StringEscapeUtils.escapeSql(name);
	    	String qry = "SELECT id FROM DigitalReceiptUserSettings WHERE userId = "+userId+"  AND selectedTemplateName='"+name+"' ";
	    	return ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
	    }
	    
	    //unused
	    public String findUserSelTemplate(Long templateId ) {
	 	   try {
	 	    		
	 	    			String sql = "select selected_template_name from digital_receipt_user_settings where id=?" ;
	 	    			String name = (String)getJdbcTemplate().queryForObject(sql, new Object[] { templateId }, String.class);
	 	    			logger.info(""+name);
	 	    			return name;
	 	    		
	 	    	} catch(Exception e) {
	 	    		return null;
	 	    	}
	 	    }
	    
	    */
	    public void setNewTemplateName(long userId, String newTemplateName, String selectedTemplate){
	    	 newTemplateName = StringEscapeUtils.escapeSql(newTemplateName);
			  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);

					String query = "Update DigitalReceiptUserSettings set selectedTemplateName='MY_TEMPLATE:"+newTemplateName+"' WHERE selectedTemplateName='"+selectedTemplate+"' AND userId="+userId;
					int count= executeUpdate(query);
				
	    		 
	    	 }//setNewTemplateName()
	    
	    public int updateDigiUserSetting(Long userId,boolean enabled,boolean smsEnabled, boolean include_shipping, boolean include_fee, boolean include_tax, boolean global_discount,boolean total_amount,String dateFormat) {//app-3706
	    	 
	    	String sql ="update digital_receipt_user_settings  set enabled="+enabled+", sms_enabled="+smsEnabled+", include_tax="+include_tax+", include_fee="+include_fee+", include_shipping="+include_shipping+", global_discount="+global_discount+", total_amount="+total_amount+", date_format= '"+dateFormat+"'  where user_id=" + userId +" ";
	    	return jdbcTemplate.update(sql);
	    }
	    
	    
}
