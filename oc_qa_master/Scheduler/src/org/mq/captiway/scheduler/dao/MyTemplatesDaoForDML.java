package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.MyTemplates;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class MyTemplatesDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	JdbcTemplate jdbcTemplate;
	
	public MyTemplatesDaoForDML() {}
	
   /* public MyTemplates find(Long id) {
        return (MyTemplates) super.find(MyTemplates.class, id);
    }*/
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(MyTemplates myTemplates) {
        super.saveOrUpdate(myTemplates);
    }

    public void delete(MyTemplates myTemplates) {
        super.delete(myTemplates);
    }
/*
    public List findAll() {
        return super.findAll(MyTemplates.class);
    }
    
    public List findByUserName(String userName){
    	return getHibernateTemplate().find("from MyTemplates where users.userName = '" + userName + "'");
    }
    
    public MyTemplates findByUserNameAndTemplateName(long userId, String name){
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and name = '" + name + "'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;
    }
    */
    public void deleteByUserIdAndName(long userId, String myTemplateName) {
    	getHibernateTemplate().bulkUpdate("delete from MyTemplates where users="+userId+" and name ='"+myTemplateName+"'");
    }
    
    /*public String getEditorTypeByNameAndUser(long userId, String myTemplateName) {
    	MyTemplates myTemplate = findByUserNameAndTemplateName(userId, myTemplateName);
    	return myTemplate.getEditorType();
    }*/
    
  /*  public List<MyTemplates> getAllByEditorType(Long userId,String editorType) {
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and editorType = '" + editorType + "'");
    	return templateList;
    }
    */
    /*public void addDigitalRecieptUserSettings(Long userId,String userSettings) {
    	String sql ="insert into digital_reciept_user_settings() values("+ userId +",'"+ userSettings  +"')";
    	jdbcTemplate.execute(sql);
    }*/ 
    
  /*  public void addDigiRecptUserSelectedTemplate(Long userId,String templateName) {
    	String sql ="insert into digital_reciepts_user_settings(user_id,selected_templ ate_name) values ("+ userId +",'"+ templateName  +"')";
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


