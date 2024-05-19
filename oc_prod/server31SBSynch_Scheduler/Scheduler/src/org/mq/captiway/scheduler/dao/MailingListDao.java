package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked" })
public class MailingListDao extends AbstractSpringDao {

    private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public MailingListDao() {}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public MailingList find(Long id) {
        return (MailingList) super.find(MailingList.class, id);
    }

   

    public List findAll() {
        return super.findAll(MailingList.class);
    }

    public MailingList findByListName(String listName, String username) {
        
        List list = getHibernateTemplate().find("from MailingList where listName = '" + listName + "'and users.username like '" + username + "'");
        if(logger.isDebugEnabled()) logger.debug("Size of the list is : " + list.size());
        if (list.size() != 1) {
            if(logger.isErrorEnabled()) logger.error("** Problem: Size of List is not 1; It is actually "+ list.size()+" **");
            return null;
        }else{
        	return (MailingList) list.get(0);
        }
	}
    
    public List<MailingList> findConfirmOptInList() {

    	try {
			String qryStr = 
				" SELECT m FROM MailingList m WHERE m.checkDoubleOptin = true" +
				" AND 0 < ( SELECT count(c.contactId) FROM Contacts c WHERE c.users = m.users "+
				" AND bitwise_and(c.mlBits, m.mlBit) > 0 " +
				" AND (c.lastMailDate is null OR (DATEDIFF(now(), c.lastMailDate) >7))" +
				" AND (c.optin IS null OR c.optin < 3) AND (c.emailStatus ='"+Constants.CONT_STATUS_OPTIN_PENDING+"'))";
			return getHibernateTemplate().find(qryStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    }

    public List<String> getCFList(String cfNamesStr, long listId) {
    	String queryStr = "SELECT concat(custfield_name,'" + Constants.DELIMETER_DOUBLECOLON
    						+ "' ,field_index ,'" + Constants.DELIMETER_DOUBLECOLON 
    						+ "', ifnull(default_value,' ')) mlcf " 
    						+ "FROM ml_customFileds " 
    						+ "WHERE list_id = " + listId + " AND custfield_name IN ( " + cfNamesStr + ")"; 
    	if(logger.isDebugEnabled()) logger.debug("Query :" + queryStr);
    	return jdbcTemplate.query(queryStr,new RowMapper() {
			
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				return rs.getString("mlcf");
			}
		});
    }
    
    public MailingList findListTypeMailingList(String listType ,long userId) {
    	try {
        	/*List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in(SELECT userId FROM Users " +
        			" WHERE userId="+userId+" OR parentUser="+userId+" )  AND listType LIKE '" + listType + "%'" );*/
    		
    		List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in("+userId+" )  AND listType LIKE '" + listType + "%'" );
    		
        	if(list == null || list.size() ==0) {
        		return null;
        	}
        	return list.get(0);
		} catch (DataAccessException e) {
			return null;
		}
    }
    
    
    public List<MailingList> findListTypeMailingList(long userId) {
    	try {
        	/*List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in(SELECT userId FROM Users " +
        			" WHERE userId="+userId+" OR parentUser="+userId+" )  AND (listType LIKE '" + Constants.MAILINGLIST_TYPE_POS + "%' OR listType LIKE '"+Constants.MAILINGLIST_TYPE_HOMESPASSED+"%')" );
        	*/
    		
    		List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in("+userId+" )  AND (listType LIKE '" + Constants.MAILINGLIST_TYPE_POS + "%' OR listType LIKE '"+Constants.MAILINGLIST_TYPE_HOMESPASSED+"%')" );
        	
    		
    		
    		
    		if(list == null || list.size() ==0) {
        		return null;
        	}
        	return list;
		} catch (DataAccessException e) {
			return null;
		}
    }
    
    public MailingList findById(long id){
		List list = getHibernateTemplate().find("from MailingList where listId = "+  id );
		if(list.size()>0)
			return (MailingList)list.get(0);
		else return null;
	}
    
    public List<MailingList> findByContactBit(Long userId, Long contactBit) {
		try {
			
			String qry=" FROM MailingList  WHERE users = " + userId +" AND bitwise_and(mlBit,"+contactBit.longValue()+") > 0 ";
			
			return getHibernateTemplate().find(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
    
    
    /**
     * called in Schedulers and is used to retrieve the
     * mailingList objects
     * @param mlId
     * @return list of MailingList object of an user
     */
    public List<MailingList> findByIds(String mlId) {
    	try {
			List<MailingList> mlList = null;
			mlList=getHibernateTemplate().find("SELECT DISTINCt m from MailingList m where listId in ("+mlId+")");
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    	
    }
    
	public MailingList findPOSMailingList(Users  users) {
		try {
			List<MailingList> mList = null;
			logger.info("userId is ::"+users.getUserId() + ":: list Type is :: POS" );
			//mList = getHibernateTemplate().find("from MailingList where users in(select userId from Users where parentUser = " + users.getUserId() + " OR userId="+users.getUserId()+")  AND listType='POS'");
			
			mList = getHibernateTemplate().find("from MailingList where users in("+ users.getUserId() + " )  AND listType='POS'");
			if(mList == null || mList.size() == 0) return null;
			else  return mList.get(0);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
	
	public List<MailingList> findAllTriggerLists(Long etId) {
		
		 String query = " SELECT DISTINCT m FROM MailingList m join m.triggersSet et WHERE et.id="+etId ;

		 return executeQuery(query);
		
	}
    
}

