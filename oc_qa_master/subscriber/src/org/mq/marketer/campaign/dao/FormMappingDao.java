package org.mq.marketer.campaign.dao;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;

public class FormMappingDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public FormMappingDao() {
	}

	public FormMapping find(Long id) {
		
		return findById(id);
		
//		return (FormMapping) super.find(FormMapping.class, id);
	}

	/*public void saveOrUpdate(FormMapping formMapping) {
		super.saveOrUpdate(formMapping);
	}

	public void delete(FormMapping formMapping) {
		super.delete(formMapping);
	}*/

	public List<FormMapping> findAll() {
		return super.findAll(FormMapping.class);
	}
	
	public List<FormMapping> findByUserAndURL(Set<Long> userIds, String URL) {
		
		String userIdsStr = Utility.getUserIdsAsString(userIds);
		List list = getHibernateTemplate().find("from FormMapping where user_id In(" + userIdsStr + ") and url='" + URL + "'");
		if(list.size() > 0) {
			return list;
		}
		return null;
	}
	
	public List<FormMapping> findByUserAndURLAndFormName(Set<Long> listIds, String URL, String formName) {
		
		String listIdsStr = Utility.getIdsAsString(listIds);
		List list = getHibernateTemplate().find("SELECT DISTINCT f FROM FormMapping f,MailingList m WHERE f.users= m.users and f.listId=m.listId AND m.list_id  IN("+listIdsStr+") and f.url='" + URL + "'" +"and f.formName='"+formName+"'" );
		if(list.size() > 0) {
			return list;
		}
		return null;
	}

	public List<FormMapping> findAllByUser(Set<Long> listIds) {
		
		String listIdsStr = Utility.getIdsAsString(listIds);
		List list = getHibernateTemplate().find("SELECT DISTINCT f FROM FormMapping f,MailingList m WHERE f.users= m.users and f.listId=m.listId AND m.list_id  IN("+listIdsStr+")");
		if(list.size() > 0) {
			return list;
		}
		return null;
	}
	
public FormMapping findById(long hid) {
		
		String query = "FROM FormMapping WHERE id="+hid;
		List<FormMapping> list = getHibernateTemplate().find(query);
		if(list.size() > 0) {
			
			return list.get(0);
		}
		else return null;
		
		
	}
public int  findAllUserId(long userId) {
	
	try {
		String query = " SELECT count(*) FROM FormMapping WHERE users="+userId;
		List list = getHibernateTemplate().find(query);
		if(list.size() > 0) 
			
			return ((Long)list.get(0)).intValue();
		else return 0;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return 0;
	}
	
	
}//findAllUSerId

public List<FormMapping> findAllByUserId(long userId) {
	
	try {
		String query = " FROM FormMapping WHERE users="+userId;
		List<FormMapping> list = executeQuery(query);
		if(list != null && list.size() > 0) return list; 
		else return null;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	
	
}//findAllUSerId


@SuppressWarnings("unchecked")
public List<FormMapping> findByUserId(long userId,int startFrom, int count,String orderby_colName,String desc_Asc) {
	try {
		String queryStr= " FROM FormMapping WHERE users ="+userId +" ORDER BY "+orderby_colName+" "+desc_Asc;
		return executeQuery(queryStr, startFrom, count);
	} catch (DataAccessException ex) {
		logger.error("Exception ::" , ex);
		return null;
	}catch (Exception ex) {
		logger.error("Exception ::" , ex);
		return null;
	}
}
public boolean findAllByUrl(String URL){
	try {
		String queryStr= "from FormMapping where URL='"+URL+"' ";
		List formNameList = getHibernateTemplate().find(queryStr);
		if(formNameList.size() > 0) {
			
			return true;
		}
		return false;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return false;
	}
	
}
public boolean findAllByFormName(long userId,String formName){
	try {
		String queryStr= "from FormMapping where users ="+userId +" and formMappingName='"+formName+"' ";
		List formNameList = getHibernateTemplate().find(queryStr);
		if(formNameList.size() > 0) {
			
			return true;
		}
		return false;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return false;
	}
	
}
	
	
}
