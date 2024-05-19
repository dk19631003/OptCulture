package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class MyTemplatesDaoForDML extends AbstractSpringDaoForDML{


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
    	return getHibernateTemplate().find("FROM MyTemplates WHERE users.userName = '" + userName + "'");
    }
    
    public MyTemplates findByUserNameAndTemplateName(long userId, String name,String folderName){
    	List<MyTemplates> templateList = getHibernateTemplate().find("FROM MyTemplates WHERE users = " + userId + " AND name = '" + name + "' AND folderName='"+folderName+"'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;
    }
    */
    public void deleteByUserIdAndName(long userId, String myTemplateName, String parentDir) {
    	getHibernateTemplate().bulkUpdate("DELETE FROM MyTemplates WHERE users="+userId+" AND parentDir='"+parentDir+"' AND name ='"+myTemplateName+"'");
    }
    
    public void deleteByFolderName(long userId, String folderName, String parentDir) {
    	
    	try {
			executeUpdate("DELETE FROM MyTemplates WHERE users="+userId+" AND parentDir='"+parentDir+"' AND folderName ='"+folderName+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    }
    
    
   /* public String getEditorTypeByNameAndUser(long userId, String myTemplateName) {
    	MyTemplates myTemplate = findByUserNameAndTemplateName(userId, myTemplateName);
    	return myTemplate.getEditorType();
    }*/
    
  /*  public List<MyTemplates> getAllByEditorType(Long userId,String editorType) {
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and editorType = '" + editorType + "'");
    	return templateList;
    }*/
    
    /*public void addDigitalRecieptUserSettings(Long userId,String userSettings) {
    	String sql ="insert into digital_reciept_user_settings() values("+ userId +",'"+ userSettings  +"')";
    	jdbcTemplate.execute(sql);
    }*/ 
 /*   public List<MyTemplates> findByUser(String userName){
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users.userName = '" + userName + "'");
    	return templateList;
    
    }
    public List<MyTemplates> findDefaultMyTemplates(Long userId, String folderName){
    	
    	try {
			List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " AND folderName='"+folderName+"'  ORDER BY  name ");
			return templateList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    
    }
    public int findTotDefaultMyTemplates(Long userId,String folderName){
    	try {
			String qry = "SELECT  COUNT(myTemplateId) FROM  MyTemplates  WHERE users ="+userId+" " +
					" AND folderName = '"+folderName+"'";
			int count =   ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
    }
    
    public MyTemplates findByUserNameAndTempNameInFolder(long userId, String name,String folderName){
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and name = '" + name + "'"+
     "AND folderName = '"+folderName+"'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;
    }
    public List findMyTemplatesFolders(Long userId){
    	
    	try {
			List templateList = getHibernateTemplate().find("SELECT  folderName from MyTemplates where users = " + userId + "  ");
			return templateList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    
    }
   */
    
    public int updateFolderName(Long userId, String folderName, String folderNameAttr, String parentDir) {
    	
    	try {
			String qry = "UPDATE MyTemplates SET folderName ='"+folderName+"' WHERE   users = " + userId + " AND parentDir='"+parentDir+"' AND  folderName ='"+folderNameAttr+"' ";
			
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
    	
    	
    }
    public void deleteByUserIdAndNameAndFolder(long userId, String myTemplateName, String folderNameAttr, String parentDir) {
    	try {
			getHibernateTemplate().bulkUpdate("DELETE FROM MyTemplates WHERE users="+userId+ " AND parentDir='"+parentDir+"' AND name ='"+myTemplateName+"' AND folderName ='"+folderNameAttr+"'");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    }
 
}

