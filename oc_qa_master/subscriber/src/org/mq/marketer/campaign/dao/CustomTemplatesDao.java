package org.mq.marketer.campaign.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings( { "unchecked", "serial", "unused", "deprecation" })
public class CustomTemplatesDao extends AbstractSpringDao{
	
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public void saveOrUpdate(CustomTemplates customTemplates) {
		super.saveOrUpdate(customTemplates);
	}
	*/
	/*public void delete(CustomTemplates customTemplates) {
		super.delete(customTemplates);
	}*/

	
	public List<CustomTemplates> findAllByUser(Long userId, String type) {
	    try {
			return executeQuery("FROM CustomTemplates where userId = " + userId+" AND  type='" + type + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	public List<CustomTemplates> findByTemplateName(Long userId, String name) {
	    try {
			return executeQuery("FROM CustomTemplates where userId = " + userId+" AND  templateName='" + name + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	public List<CustomTemplates> getTemplatesByType(Set<Long> listIds,String type) {
		
		try {
			String listIdsStr = Utility.getIdsAsString(listIds);
			
			if(listIdsStr.isEmpty()) return null;
			return executeQuery("SELECT DISTINCT c FROM CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ") and c.type='" + type + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	public int getCustomTemplateCount(Set<Long> listIds) {
		
		try {
			String listIdsStr = Utility.getUserIdsAsString(listIds);
			
			if(listIdsStr.isEmpty()) return 0;
			
			 List<Long> countList = executeQuery("SELECT COUNT(c.templateId) from CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ")" );
			    if(countList.size()>0) {
			    	return countList.get(0).intValue();
			    }else {
			    	return 0;
			    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return 0;
		}
	}
	
	public List<CustomTemplates> getSubscriptionFormById(Long templateId) {
		try {
			return executeQuery("from CustomTemplates where templateId=" + templateId);
		} catch (Exception e) {

			logger.error("Exception ::", e);
			return null;
		}
	}
	
	public String getTemplateHTMLById(Long templateId) {
		try {
			return executeQuery("select htmlText from CustomTemplates where templateId = " + templateId).get(0).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	public boolean checkTemplateStatusByName(Set<Long> listIds, String templateName) {
		
		try {
			String listIdsStr = Utility.getUserIdsAsString(listIds);
			
			if(listIdsStr.isEmpty()) return false;
			List list = executeQuery("SELECT DISTINCT c FROM CustomTemplates c, MailingList m where c.userId=m.users AND m.listId IN( " + listIdsStr + ") and c.templateName='" + templateName +"'");
			if(list!=null && list.size() > 0) {
				return true;
			} 
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return false;
		}
	}
	
	/*public void deleteFormById(Long templateId) {
		 jdbcTemplate.execute("delete from custom_templates where template_id="+templateId);
	}*/
	
	public CustomTemplates findCustTemplateById(Long id) {
		try {
			String hql = "FROM CustomTemplates WHERE templateId=" + id;
			List<CustomTemplates> tempList = executeQuery(hql);
			if(tempList != null && tempList.size() > 0)return tempList.get(0);
			else return null;
			//return (CustomTemplates)executeQuery(hql).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	public CustomTemplates findByUserRefereeEmail(Long userId){
		
		 try {
			String qry=" FROM CustomTemplates WHERE userId="+userId+"  AND type='"+Constants.CUST_TEMP_WELCOME_TYPE+"'  " +
					   " ANd templateName='"+Constants.REFERRAL_TEMPLATE_NAME+"' ";
			List<CustomTemplates> custList= executeQuery(qry);
			if(custList != null && custList.size() > 0){
				
				return custList.get(0);
			}else{
				return null;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
			
		
	}
	
	
	
	public boolean iscustomtypeExistByUserId(Long userId) {
		
		try {
			List<AutoSMS> autoemailList = null;
			
			autoemailList = getHibernateTemplate().find("FROM CustomTemplates WHERE userId="+userId+"  AND type='"+Constants.EQ_TYPE_OTP_MAILS+"' " );
			logger.info("autoemailList string"+autoemailList);
			if(autoemailList!= null && autoemailList.size()>0)
				return true;
			else	return false;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return false;
		}
	}
	
public  CustomTemplates emailotpmsg(Long userId) {
		
		try {
			List<CustomTemplates> autoemailList = null;
			
			String query = " FROM CustomTemplates WHERE userId="+userId+"  AND type='"+Constants.EQ_TYPE_OTP_MAILS+"' " ;
			autoemailList=executeQuery(query);
			if(autoemailList!= null && autoemailList.size()>0)
			
			return autoemailList.get(0);
			
			else return null;
			
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
	
		}
			return null;
	}
	
	
	
	
	
	
	
	
	public List<CustomTemplates> findAllByUser(Long userId) {
	    return executeQuery("FROM CustomTemplates where userId = " + userId +" and htmlText is not null");
	}
	
	
	
	 /*public List executeQuery(String queryStr, int startFrom, int count ) {
	    	Session session = null;
	    	List resList = new ArrayList();
	    	try{
	    		session = getSession();
	    		Query query = session.createQuery(queryStr);
	    		query.setFirstResult(startFrom);
				query.setMaxResults(count);
	    		resList = query.list();
	    		return resList;
	    		
	    	}catch(Exception e) {
	    		logger.error("** Exception ", e);
	    		return resList;
	    	}finally {
	    		if(session != null) {
		    		session.flush();
		    		session.close();
	    		}
	    	}
	    	
	    }*/
	 
	 public List<CustomTemplates> findAllByUserIdPagingCount(Long userId,Map<String, String> searchMap) {
		 String query = null;
		 if(searchMap!=null && !searchMap.isEmpty()) {
			 if(searchMap.get("autoEmailType")!= null) {
				 String autoEmailType = searchMap.get("autoEmailType");
				 query = "FROM CustomTemplates where userId = " + userId+" AND  type='" + autoEmailType + "'";
			 }else if(searchMap.get("createdDate")!= null){
				String autoEmailType = searchMap.get("createdDate");
 				String[] date = autoEmailType.split(",");
	 			query = "FROM CustomTemplates where userId= "+userId+" and createdDate BETWEEN '"+date[0]+"' AND '"+date[1]+"'  and (myTemplateId is not null or htmlText is not null)";
			 }
		 }else {
			 query = "FROM CustomTemplates where userId = " + userId + " and (myTemplateId is not null or htmlText is not null)";
		 }
		 List<CustomTemplates> listResult =  executeQuery(query);
		return listResult;
		 
	 }
	
	public List<CustomTemplates> findAllByUserIdPaging(Long userid,int first,int maxResult, Map<String, String> searchMap, String orderby_colName, String desc_Asc) {
    	try {
    		List<CustomTemplates> list = null;
    		String queryStr  = null;
    		List query = null;
    		Session session = getSession();
    		if((searchMap == null || searchMap.isEmpty()) && orderby_colName == null) {
    			queryStr = "FROM CustomTemplates where userId="+userid+" and (myTemplateId is not null or htmlText is not null) order by templateId desc";
    			list = executeQuery(queryStr, first, maxResult);
    		}else if((searchMap == null || searchMap.isEmpty()) && orderby_colName != null && desc_Asc !=null) {
    			queryStr = "FROM CustomTemplates where userId= "+userid+" and (myTemplateId is not null or htmlText is not null) order by "+orderby_colName+" "+desc_Asc+"";
    			list = executeQuery(queryStr, first, maxResult);
    		}	
    		else if(searchMap.get("autoEmailType")!= null && !searchMap.isEmpty()) {
	    			String autoEmailType = searchMap.get("autoEmailType");
	    			if(orderby_colName != null && desc_Asc !=null) {
	    				queryStr = "FROM CustomTemplates where userId= "+userid+" and type= '"+autoEmailType+"' and (myTemplateId is not null or htmlText is not null) order by "+orderby_colName+" "+desc_Asc+"";
	    			}else {
	    				queryStr = "FROM CustomTemplates where userId= "+userid+" and type= '"+autoEmailType+"' and (myTemplateId is not null or htmlText is not null) order by templateId desc";
	    			}
	    			list = executeQuery(queryStr, first, maxResult);
    		}else if(searchMap.get("createdDate")!=null) {
    				String autoEmailType = searchMap.get("createdDate");
    				String[] date = autoEmailType.split(",");
    				if(orderby_colName != null && desc_Asc !=null) {
	    				queryStr = "FROM CustomTemplates where userId= "+userid+" and createdDate BETWEEN '"+date[0]+"' AND '"+date[1]+"'  AND (myTemplateId IS NOT NULL OR htmlText IS NOT NULL) ORDER BY "+orderby_colName+" "+desc_Asc+"";
	    			}else {
	    				queryStr = "FROM CustomTemplates where userId= "+userid+" and createdDate BETWEEN '"+date[0]+"' AND '"+date[1]+"'  and (myTemplateId IS NOT null OR htmlText IS NOT NULL) ORDER BY templateId desc";
	    			}
    				list = executeQuery(queryStr, first, maxResult);
    		}
			return list;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		} catch(Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	
	public CustomTemplates findAllByUserIdAndTemplateId(Long userid,Long templateId) {
    	try {
    		//Session session = getSession();
    		String queryStr = "FROM CustomTemplates where userId="+userid+" and templateId= "+templateId+" and (htmlText is not null or myTemplateId is not null)";
    		List<CustomTemplates> list = executeQuery(queryStr);
    		if(list!= null && !list.isEmpty()) {
    			return list.get(0);
    		}else {
    			return null;
    		}
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }

	public List<CustomTemplates> findTemplatesFromUserId(Long userId) {
		try {
    		//Session session = getSession();
    		String queryStr = "FROM CustomTemplates where userId= "+userId+" and htmlText is not null";
    		List<CustomTemplates> list = executeQuery(queryStr);
    		if(list!= null && list.size() > 0) {
    			return list;
    		}else {
    			return null;
    		}
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
}
