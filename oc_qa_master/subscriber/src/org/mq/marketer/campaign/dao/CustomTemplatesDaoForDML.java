package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings( { "unchecked", "serial", "unused", "deprecation" })
public class CustomTemplatesDaoForDML extends AbstractSpringDaoForDML{
	
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(CustomTemplates customTemplates) {
		super.saveOrUpdate(customTemplates);
	}
	
	public void delete(CustomTemplates customTemplates) {
		super.delete(customTemplates);
	}

	
	/*public List<CustomTemplates> findAllByUser(Long userId, String type) {
	    return getHibernateTemplate().find("FROM CustomTemplates where userId = " + userId+" AND  type='" + type + "'");
	}
	
	public List<CustomTemplates> getTemplatesByType(Set<Long> listIds,String type) {
		
		String listIdsStr = Utility.getIdsAsString(listIds);
		
		if(listIdsStr.isEmpty()) return null;
		return getHibernateTemplate().find("SELECT DISTINCT c FROM CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ") and c.type='" + type + "'");
	}
	
	public int getCustomTemplateCount(Set<Long> listIds) {
		
		String listIdsStr = Utility.getUserIdsAsString(listIds);
		
		if(listIdsStr.isEmpty()) return 0;
		
		 List<Long> countList = getHibernateTemplate().find("SELECT COUNT(c.templateId) from CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ")" );
	        if(countList.size()>0) {
	        	return countList.get(0).intValue();
	        }else {
	        	return 0;
	        }
	}
	
	public List<CustomTemplates> getSubscriptionFormById(Long templateId) {
		return getHibernateTemplate().find("from CustomTemplates where templateId=" + templateId);
	}
	
	public String getTemplateHTMLById(Long templateId) {
		return getHibernateTemplate().find("select htmlText from CustomTemplates where templateId = " + templateId).get(0).toString();
	}
	
	public boolean checkTemplateStatusByName(Set<Long> listIds, String templateName) {
		
		String listIdsStr = Utility.getUserIdsAsString(listIds);
		
		if(listIdsStr.isEmpty()) return false;
		List list = getHibernateTemplate().find("SELECT DISTINCT c FROM CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ") and c.templateName='" + templateName +"'");
		if(list!=null && list.size() > 0) {
			return true;
		} 
		return false;
	}*/
	
	public void deleteFormById(Long templateId) {
		 jdbcTemplate.execute("delete from custom_templates where template_id="+templateId);
	}
	
/*	public CustomTemplates findCustTemplateById(Long id) {
		String hql = "FROM CustomTemplates WHERE templateId=" + id;
		List<CustomTemplates> tempList = getHibernateTemplate().find(hql);
		if(tempList != null && tempList.size() > 0)return tempList.get(0);
		else return null;
		//return (CustomTemplates)getHibernateTemplate().find(hql).get(0);
	}
	
	public CustomTemplates findByUserRefereeEmail(Long userId){
		
		 try {
			String qry=" FROM CustomTemplates WHERE userId="+userId+"  AND type='"+Constants.CUST_TEMP_WELCOME_TYPE+"'  " +
					   " ANd templateName='"+Constants.REFERRAL_TEMPLATE_NAME+"' ";
			List<CustomTemplates> custList= getHibernateTemplate().find(qry);
			if(custList != null && custList.size() > 0){
				
				return custList.get(0);
			}else{
				return null;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
			
		
	}*/
	
}
