package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.WATemplates;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class WATemplatesDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	public WATemplates find(Long id) {
		try {
		List<WATemplates> templateList = null;
		String query = "FROM WATemplates WHERE templateId="+id+"";
		templateList =	executeQuery(query );
			if(templateList != null && !templateList.isEmpty() && templateList.size()>0) {
				return templateList.get(0);
			}else {
				return null;
			}
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public List<WATemplates> findAll(Long id) {
		try {
		List<WATemplates> templateList = null;
		String query = "FROM WATemplates WHERE templateId="+id+"";
		templateList =	executeQuery(query );
		if(templateList != null && !templateList.isEmpty()) {
			return templateList;
		}else {
			return null;
		}
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public  boolean isTemplateNameExistByUserId(Long userId,String tempName){
		
		try {
			List<WATemplates> templateList = null;
			
			templateList = getHibernateTemplate().find("FROM WATemplates where userId="+userId+" AND templateName='"+tempName+"'");
			if(templateList != null && templateList.size()>0)
				return true;
			else	return false;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return false;
		}
	}
	
	
	public  List<WATemplates> findTemplatesByUserId(Long userId){
		
		try {
			List<WATemplates> templateList = null;
			
			return templateList = getHibernateTemplate().find("FROM WATemplates where userId="+userId);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public  List<WATemplates> findTemplatesByOrgId(Long orgId) throws Exception{
		
		try {
			List<WATemplates> templateList = null;
			
			String qry = "FROM WATemplates where orgId="+orgId;
			templateList =  executeQuery(qry);
			
			if(templateList != null && templateList.size() > 0 ){
				return templateList;
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public  WATemplates findTemplateByType(Long userId, String type) throws Exception{
		
		try {
			List<WATemplates> templateList = null;
			
			String qry = "FROM WATemplates where userId="+userId+" and type='"+type +"' order by 1 desc";
			logger.debug("WATemplatesDao.findTemplateByType qry :: "+qry);
			templateList =  executeQuery(qry);
			
			if(templateList != null && templateList.size() > 0 ){
				return templateList.get(0);
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}//findTemplateByType
	
	public  WATemplates findTemplateByTypeAndName(Long userId, String type, String tempName) throws Exception{
		
		try {
			List<WATemplates> templateList = null;
			String subQry = tempName!=null && !tempName.isEmpty() ? " and templateName='"+tempName+"' " : "" ;
			String qry = "FROM WATemplates where userId="+userId+" and type='"+type + "'"+ subQry +" order by 1 desc";
			logger.debug("WATemplatesDao.findTemplateByType qry :: "+qry);
			templateList =  executeQuery(qry);
			
			if(templateList != null && templateList.size() > 0 ){
				return templateList.get(0);
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}//findTemplateByTypeAndName


}
