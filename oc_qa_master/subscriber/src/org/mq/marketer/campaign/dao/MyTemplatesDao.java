package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class MyTemplatesDao extends AbstractSpringDao{


	JdbcTemplate jdbcTemplate;
	
	public MyTemplatesDao() {}
	
    public MyTemplates find(Long id) {
        return (MyTemplates) super.find(MyTemplates.class, id);
    }
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

/*	public void saveOrUpdate(MyTemplates myTemplates) {
        super.saveOrUpdate(myTemplates);
    }

    public void delete(MyTemplates myTemplates) {
        super.delete(myTemplates);
    }
*/
    public List findAll() {
        return super.findAll(MyTemplates.class);
    }
    
    public List findByUserName(String userName){
    	return getHibernateTemplate().find("FROM MyTemplates WHERE users.userName = '" + userName + "'");
    }
    
    public MyTemplates findByUserNameAndTemplateName(long userId, String name,String folderName){
    	/*List<MyTemplates> templateList = getHibernateTemplate().find("FROM MyTemplates WHERE users = " + userId + " AND name = '" + name + "' AND folderName='"+folderName+"'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;*/
    	
    	return findByUserNameAndTemplateName(userId, name, folderName, Constants.MYTEMPATES_PARENT);
    }
    
    
    
    public MyTemplates findByUserNameAndTemplateName(long userId, String name,String folderName, String parentDir){
    	List<MyTemplates> templateList = getHibernateTemplate().find("FROM MyTemplates WHERE users = " + userId + 
    			" AND name = '" + name + "' AND folderName='"+folderName+"' AND parentDir='"+parentDir+"'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;
    	
    }
    
  /*  
    public void deleteByUserIdAndName(long userId, String myTemplateName) {
    	getHibernateTemplate().bulkUpdate("DELETE FROM MyTemplates WHERE users="+userId+" AND name ='"+myTemplateName+"'");
    }
    
    public void deleteByFolderName(long userId, String folderName) {
    	
    	try {
			executeUpdate("DELETE FROM MyTemplates WHERE users="+userId+" AND folderName ='"+folderName+"'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    }
    */
    
   /* public String getEditorTypeByNameAndUser(long userId, String myTemplateName) {
    	MyTemplates myTemplate = findByUserNameAndTemplateName(userId, myTemplateName);
    	return myTemplate.getEditorType();
    }*/
    
    public List<MyTemplates> getAllByEditorType(Long userId,String editorType) {
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and editorType = '" + editorType + "'");
    	return templateList;
    }
    
    /*public void addDigitalRecieptUserSettings(Long userId,String userSettings) {
    	String sql ="insert into digital_reciept_user_settings() values("+ userId +",'"+ userSettings  +"')";
    	jdbcTemplate.execute(sql);
    }*/ 
    public List<MyTemplates> findByUser(String userName){
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users.userName = '" + userName + "'");
    	return templateList;
    
    }
    public List<MyTemplates> findDefaultMyTemplates(Long userId, String folderName, String parentDirName){
    	
    	try {
			List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " "
					+ "AND folderName='"+folderName+"'  AND parentDir='"+parentDirName+"' ORDER BY  name ");
			return templateList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    
    }
   /* public int findTotDefaultMyTemplates(Long userId,String folderName){
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
    }*/
    
    public MyTemplates findByUserNameAndTempNameInFolder(long userId, String name,String folderName){
    	/*List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " and name = '" + name + "'"+
     "AND folderName = '"+folderName+"'");
    	if(templateList.size() > 0)
    		return templateList.get(0);
    	else 
    		return null;*/
    	return findByUserNameAndTempNameInFolder(userId, name, folderName, Constants.MYTEMPATES_PARENT);
    	
    }
    
    public MyTemplates findByUserNameAndTempNameInFolder(long userId, String name,String folderName, String parentDir){
    	List<MyTemplates> templateList = getHibernateTemplate().find("from MyTemplates where users = " + userId + " AND parentDir='"+parentDir+"' and name = '" + name + "'"+
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
    
    
    
public List<String> findDistinctMyTemplatesFolders(Long userId){
    	try {
			List<String> templateList = getHibernateTemplate().find("SELECT DISTINCT folderName from MyTemplates where users = " + userId + "  and folderName is not null");
			return templateList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    
    }

public List<MyTemplates> findTemplatesFromUserIdAndFolderName(Long userId,String folderName){
	try {
		String query = "FROM MyTemplates mytemp WHERE mytemp.users = "+userId+" AND mytemp.folderName like '"+folderName+"'";
		List<MyTemplates> templateNameList = getHibernateTemplate().find(query);
		if(templateNameList!=null && !templateNameList.isEmpty()) {
			return templateNameList;
		}else {
			return null;
		}
	} catch (DataAccessException e) {
		logger.error("Exception ::" , e);
		return null;
	}

}

	public MyTemplates getTemplateByMytemplateIdandUserId(Long templateId, Long userId) {
		String query = "FROM MyTemplates mytemp WHERE mytemp.users = "+userId+" AND mytemp.myTemplateId like '"+templateId+"'";
		List<MyTemplates> templateNameList = getHibernateTemplate().find(query);
		if(templateNameList!=null && !templateNameList.isEmpty()) {
			return templateNameList.get(0);
		}else {
			return null;
		}
	}
	
	public MyTemplates getTemplateByMytemplateId(Long templateId) {
		String query = "FROM MyTemplates mytemp WHERE mytemp.myTemplateId like '"+templateId+"'";
		List<MyTemplates> templateNameList = getHibernateTemplate().find(query);
		if(templateNameList!=null && !templateNameList.isEmpty()) {
			return templateNameList.get(0);
		}else {
			return null;
		}
	}
   
/*    
    public int updateFolderName(Long userId, String folderName, String folderNameAttr) {
    	
    	try {
			String qry = "UPDATE MyTemplates SET folderName ='"+folderName+"' WHERE   users = " + userId + " AND folderName ='"+folderNameAttr+"' ";
			
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
    	
    	
    }
    public void deleteByUserIdAndNameAndFolder(long userId, String myTemplateName, String folderNameAttr) {
    	try {
			getHibernateTemplate().bulkUpdate("DELETE FROM MyTemplates WHERE users="+userId+" AND name ='"+myTemplateName+"' AND folderName ='"+folderNameAttr+"'");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    }*/
	
	public List<MyTemplates> findTemplatesFromUserIdAndFolderName(Long userId,String folderName,int start,int maxResult){
		try {
			String query = "FROM MyTemplates mytemp WHERE mytemp.users = "+userId+" AND mytemp.folderName like '"+folderName+"' ORDER BY 1 DESC";
			List<MyTemplates> templateNameList = executeQuery(query, start, maxResult);
			if(templateNameList!=null && !templateNameList.isEmpty()) {
				return templateNameList;
			}else {
				return null;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}

	}
 
}

