package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransactionalTemplatesDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(TransactionalTemplates transactionalTemplates){
        super.saveOrUpdate(transactionalTemplates);
    }
    
    public void delete(TransactionalTemplates transactionalTemplates){
        super.delete(transactionalTemplates);
    }
	
	/*public  boolean isTemplateNameExistByUserId(Long userId,String tempName){
		
		try {
			List<TransactionalTemplates> templateList = null;
			
			templateList = getHibernateTemplate().find("FROM TransactionalTemplates where userId="+userId+" AND templateName='"+tempName+"'");
			if(templateList != null && templateList.size()>0)
				return true;
			else	return false;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return false;
		}
	}
	
	
	public  List<TransactionalTemplates> findTemplatesByUserId(Long userId){
		
		try {
			List<TransactionalTemplates> templateList = null;
			
			return templateList = getHibernateTemplate().find("FROM TransactionalTemplates where userId="+userId);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public  List<TransactionalTemplates> findTemplatesByOrgId(Long orgId) throws Exception{
		
		try {
			List<TransactionalTemplates> templateList = null;
			
			String qry = "FROM TransactionalTemplates where orgId="+orgId;
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

	public List<TransactionalTemplates> findTemplatesByType(Long userId,int status) {
		
		try {
			List<TransactionalTemplates> templateList = null;
			
			String qry = "FROM TransactionalTemplates where userId="+userId+" AND type='"+OCConstants.TRANSACTIONAL_TEMPLATE_TYPE_AS +"' AND status ="+status;
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

	public TransactionalTemplates findTemplatesByName(Long userId,String templateName) {
		
		try {
			List<TransactionalTemplates> templateObj = null;
			
			String qry = "FROM TransactionalTemplates where userId="+userId+" AND type='"+OCConstants.TRANSACTIONAL_TEMPLATE_TYPE_AS +"' "
					     + "AND templateName = '"+templateName+"'";
			templateObj =  getHibernateTemplate().find(qry);
			
			if(templateObj != null && templateObj.size() > 0){
				return templateObj.get(0);
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	public TransactionalTemplates findTempByName(Long userId,String templateName) {
		
		try {
			List<TransactionalTemplates> templateObj = null;
			
			String qry = "FROM TransactionalTemplates where userId="+userId+" AND type='"+OCConstants.TRANSACTIONAL_TEMPLATE_TYPE_AS +"' "
					     + "AND templateName LIKE '"+ templateName +" Version%"+"' ORDER BY 1 DESC";
			templateObj =  getHibernateTemplate().find(qry);
			
			if(templateObj != null && templateObj.size() > 0){
				return templateObj.get(0);
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}*/

}
