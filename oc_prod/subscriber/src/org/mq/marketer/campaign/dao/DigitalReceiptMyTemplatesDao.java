package org.mq.marketer.campaign.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DigitalReceiptMyTemplatesDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	JdbcTemplate jdbcTemplate;
	
	public DigitalReceiptMyTemplatesDao() {
	}
	
    public DigitalReceiptMyTemplate find(Long id) {
	    return (DigitalReceiptMyTemplate) super.find(DigitalReceiptMyTemplate.class, id);
	}
	    
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public void saveOrUpdate(DigitalReceiptMyTemplate digitalReceiptMyTemplate) {
	    super.saveOrUpdate(digitalReceiptMyTemplate);
	}
*/
	/*public void delete(DigitalReceiptMyTemplate digitalReceiptMyTemplate) {
	    super.delete(digitalReceiptMyTemplate);
	}*/
	
	public List findAll() {
	    return super.findAll(DigitalReceiptMyTemplate.class);
	}
	
	public List findAllByUserId(Long userId) {
		return getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " ORDER BY modifiedDate DESC ");
	}
	
	 public DigitalReceiptMyTemplate findByUserNameAndTemplateName(long userId, String name){
		 	
		 String templateName = StringEscapeUtils.escapeSql(name);
	    	List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " and name= '" + templateName + "'");
	    	if(templateList.size() > 0)
	    		return templateList.get(0);
	    	else 
	    		return null;
	 }
	 
	
	 public List<DigitalReceiptMyTemplate> findByUserId_PublishTemplatesonly(long userId, String name){
		 	
		 try {
			//String templateName = StringEscapeUtils.escapeSql(name);
			if(name!=null && !(name.isEmpty())){	
				List<DigitalReceiptMyTemplate> templateList = null;
				if(name.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT))
					templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " and jsonContent is not null and folderName='"+name+"'" );
				else 
					templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " and folderName='"+name+"'");
				if(templateList!=null)
					return templateList;
				else 
					return null;
			}
			else 
				return null;
		 } catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
		}
	  }
	 
	 public List<DigitalReceiptMyTemplate> findByOrgId_PublishTemplatesonly(long orgId, String name){
		 	
		 //String templateName = StringEscapeUtils.escapeSql(name);
	    if(name!=null && !(name.isEmpty())){	
	    	List<DigitalReceiptMyTemplate> templateList = null;
	    	if(name.equals(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT))
	    		templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where orgId = " + orgId + " and jsonContent is not null and folderName='"+name+"'" );
	    	else 
	    		templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where orgId = " + orgId + " and folderName='"+name+"'");
	    	if(templateList!=null)
	    		return templateList;
	    	else 
	    		return null;
	    }
	    else 
    		return null;
	    	
	 }
	 
	 
		public List<DigitalReceiptMyTemplate> findDRMyTemplatesUserID(Long userId, String folderName, String editorType){

			try {
				List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " "
						+ "AND folderName='"+folderName+"'  AND editorType='"+editorType+"' ORDER BY  name ");
				logger.info("queryyy : from DigitalReceiptMyTemplate where userId = " + userId + " "
						+ "AND folderName='"+folderName+"'  AND editorType='"+editorType+"' ORDER BY  name ");
				return templateList;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}

		}
		
		
		public boolean isDRwithTemplateName_EditorType(Long userId, String templateName, String editorType){

			try {
				List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find("from DigitalReceiptMyTemplate where userId = " + userId + " "
						+ "AND name='"+templateName+"'  AND editorType='"+editorType+"' ORDER BY  name ");
				logger.info("queryyy : from DigitalReceiptMyTemplate where userId = " + userId + " "
						+ "AND name='"+templateName+"'  AND editorType='"+editorType+"' ORDER BY  name ");
				if(templateList!=null && templateList.size()>0)
					return true;
				else 
					return false;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return false;
			}

		}
	 
		public int findDRMyTemplatesCountByUserID(Long userId, String folderName, String editorType,String tempName){

			try {
				
				String query="from DigitalReceiptMyTemplate where userId = " + userId + " "
						+ "AND folderName='"+folderName+"'  AND editorType='"+editorType+"' ORDER BY  name ";
				String tempQuery=null;
	      		if(tempName !=null && tempName.length()>0)
	      			tempQuery	=" AND name like '"+tempName+"%'";
	            if(tempQuery!=null)
	            	query="from DigitalReceiptMyTemplate where userId = " + userId + " "
							+ "AND folderName='"+folderName+"'  AND editorType='"+editorType+"'"+tempQuery; 
	            logger.info("Template query :"+query);
				List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find(query);
				
				return templateList.size();
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}

		}
		
		public List<DigitalReceiptMyTemplate> findDRMyTemplatesByEditorType(Long userId, String editorType,int startIndx,int endIndx,String tempName){
			try {
	             
	            //String query="from DigitalReceiptMyTemplate where userId = " + userId + "  AND editorType='"+editorType+"' ORDER BY  modifiedDate desc";
	        	
	            String query="from DigitalReceiptMyTemplate where userId = " + userId + "  AND editorType='"+editorType+"' ORDER BY  name";

				
				String tempQuery=null;
	      		if(tempName !=null && tempName.length()>0)
	      			tempQuery	=" AND name like '"+tempName+"%'";
	            if(tempQuery!=null)
	            	query="from DigitalReceiptMyTemplate where userId = " + userId +tempQuery
					+" AND editorType='"+editorType+"' ORDER BY  modifiedDate desc";
	            logger.info("Template query :"+query);
				//List<DigitalReceiptMyTemplate> templateList = getHibernateTemplate().find(query);
		
				return  executeQuery(query, startIndx, endIndx);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}

		}
		
		
		public DigitalReceiptMyTemplate findByUserNameAndTemplateName(long orgID, Long id){

			// String templateName = StringEscapeUtils.escapeSql(name);
			logger.info("from DigitalReceiptMyTemplate where orgId = " + orgID + " and myTemplateId= " + id);
			List<DigitalReceiptMyTemplate> templateList = executeQuery("from DigitalReceiptMyTemplate where orgId = " + orgID + " and myTemplateId= " + id);
			if(templateList.size() > 0)
				return templateList.get(0);
			else 
				return null;
		}
		
		public DigitalReceiptMyTemplate findByUserId_TemplateId(long userId, Long id){

			// String templateName = StringEscapeUtils.escapeSql(name);
			logger.info("from DigitalReceiptMyTemplate where userId = " + userId + " and myTemplateId= " + id);
			List<DigitalReceiptMyTemplate> templateList = executeQuery("from DigitalReceiptMyTemplate where userId = " + userId + " and myTemplateId= " + id);
			if(templateList.size() > 0)
				return templateList.get(0);
			else 
				return null;
		}
		
		
/*		public DigitalReceiptMyTemplate findByOrgIdAndTemplateName(long orgID, Long id){

			// String templateName = StringEscapeUtils.escapeSql(name);
			logger.info("from DigitalReceiptMyTemplate where orgId = " + orgID + " and myTemplateId= " + id);
			List<DigitalReceiptMyTemplate> templateList = executeQuery("from DigitalReceiptMyTemplate where orgId = " + orgID + " and myTemplateId= " + id);
			if(templateList.size() > 0)
				return templateList.get(0);
			else 
				return null;
		}
		
		
		public DigitalReceiptMyTemplate findByOrgIdAndTemplateId(Long orgId, Long id){

			// String templateName = StringEscapeUtils.escapeSql(name);
			logger.info(" query : from DigitalReceiptMyTemplate where orgId = "+ orgId +"  and myTemplateId= "+ id);
			List<DigitalReceiptMyTemplate> templateList = executeQuery("from DigitalReceiptMyTemplate where orgId = "+ orgId +"  and myTemplateId= " + id);
			if(templateList.size() > 0)
				return templateList.get(0);
			else 
				return null;
		}
*/		
		
	/* public void setNewTemplateName(Long userId, String newTemplateName, String selectedTemplate , String now){
		  newTemplateName = StringEscapeUtils.escapeSql(newTemplateName);
		  selectedTemplate = StringEscapeUtils.escapeSql(selectedTemplate);
			String  query = "Update DigitalReceiptMyTemplate set name='"+newTemplateName+"',  modifiedDate='"+now+"' WHERE name='"+selectedTemplate+"' AND userId="+userId;
			 int count= executeUpdate(query);
			
		 
	 }*///setNewTemplateName()
	
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
