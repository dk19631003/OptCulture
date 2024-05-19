package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked", "serial" })
public class MailingListDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
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
    
    public MailingList findById(long id){
		List list = getHibernateTemplate().find("from MailingList where listId = "+  id );
		if(list.size()>0)
			return (MailingList)list.get(0);
		else return null;
	}
    
    /**
     * called in ViewContactListController and is used to retrieve the
     * mailingList objects
     * @param mlId
     * @return list of MailingList object of an user
     */
    public List<MailingList> findByIds(String mlId) {
    	try {
    		if(mlId == null ) return null;
    		
			List<MailingList> mlList = null;
			mlList=getHibernateTemplate().find("SELECT DISTINCt m from MailingList m where listId in ("+mlId+")");
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    public List<String> findListNamesByIdstr(String mlId) {
    	try {
    		if(mlId == null ) return null;
    		
			List<String> mlList = null;
			mlList=getHibernateTemplate().find("SELECT DISTINCt m.listName FROM MailingList m WHERE listId in ("+mlId+")");
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    
    
    /**
     * called in various Controllers and is used to retrieve the
     * the mailingList objects which are in current session.</BR>
     * added for sharing.
     * @param mlId
     * @return list of MailingList object of an user
     */
    public List<MailingList> findByIds(Set<Long> mlIdsSet) {
    	try {
    		if(mlIdsSet == null ) return null;
    		
    		
    		String mlIds = Utility.getIdsAsString(mlIdsSet);
    		
    		if(mlIds.isEmpty()) return null;
			List<MailingList> mlList = null;
			mlList=getHibernateTemplate().find("SELECT DISTINCt m from MailingList m where listId in ("+mlIds+")");
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    public List<MailingList> searchMailBy(Set<Long> mlIdsSet,String searchStr) {
    	try {
    		if(mlIdsSet == null ) return null;
    		
    		
    		String mlIds = Utility.getIdsAsString(mlIdsSet);
    		
    		if(mlIds.isEmpty()) return null;
			List<MailingList> mlList = null;
			String subQry = "";
			if(searchStr != null && !searchStr.isEmpty()){
				subQry = " AND listName LIKE '%"+ searchStr +"%' ";
			}
			mlList=getHibernateTemplate().find("SELECT DISTINCt m from MailingList m where listId in ("+mlIds+") "+subQry);
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    
    public List<MailingList> getMailingListByIds(Set<Long> mlIdsSet) {
    	try {
    		if(mlIdsSet == null ) return null;
    		
    	
    		String mlIds = Utility.getIdsAsString(mlIdsSet);
    		
    		if(mlIds.isEmpty()) return null;
			List<MailingList> mlList = null;
			
			String qryStr = " SELECT new MailingList(m.listId,m.listName,m.description,m.createdDate,"
					+ " m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField,m.lastModifiedDate,"
					+ " m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit, m.listSize) "
					+ " FROM MailingList m where m.listId in ("+mlIds+")";
			
			
			mlList=getHibernateTemplate().find(qryStr);
			if(mlList.size()>0){
				return mlList;
			}
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :: ",e);
			return null;
		}
    	
    }
    
    
    
    
    //added for Power USer related objects fetching
    
    public List<MailingList> findAllByCurrentUser(Long currUserId) {
    	
    	//String qry = "FROM MailingList WHERE users IN(SELECT userId FROM Users WHERE userId="+currUserId+" OR parentUser="+currUserId+")";
    	String qry = "FROM MailingList WHERE users IN("+currUserId.longValue()+")";
    	return getHibernateTemplate().find(qry);
    }
    
    /**
     * called in ContactList
     * 
     * @param userId
     * @return list of MailingList object of an user
     */
   /* public List<MailingList> findAllByUser(Set<Long> userIds) {
    	String userIdsStr = Utility.getUserIdsAsString(userIds);
       return getHibernateTemplate().find("from MailingList where users in ( " + userIdsStr + " )  ORDER BY listName");
    }*/
    
    
    public List<Long> findAllIdsByUser(Long userIds) {
    	//String userIdsStr = Utility.getUserIdsAsString(userIds);
       return getHibernateTemplate().find("SELECT listId from MailingList where users in ( " + userIds.longValue() + " ) ");
    }
    
    /**
     * called in ManageSegmentContacts
     * 
     * @param userId
     * @return list of MailingList object of an user
     */
    public List<MailingList>findListsByType(Set<Long> userIds, String listType) {
    	String userIdsStr = Utility.getUserIdsAsString(userIds);
       return getHibernateTemplate().find("from MailingList where users in ( " + userIdsStr + " ) AND listType='"+listType+"'  ORDER BY listName");
    }
    
   /* public List<MailingList> findAllBySharedUser(Long shUserId){
    	
    	return getHibernateTemplate().find("FROM MailingList WHERE users="+shUserId+"ORDER BY listName");
    	
    	
    	
    }*/
    
    
    
    /**
     * called in the ViewListController
     * @param userId
     * @return number of mailig lists of a user
     */
    public int getCountByUser(Set<Long> userIds) {
    	
    	String userIdsStr = Utility.getUserIdsAsString(userIds);
    	
        List<Long> countList = getHibernateTemplate().find("select count(listId) from MailingList where users in ( " + userIdsStr+" ) ");
        if(countList.size()>0) {
        	return countList.get(0).intValue();
        }else {
        	return 0;
        }
     }
    
    
    
    
    /**
     * called in the ViewListController
     * @param userId
     * @return number of mailing lists of a user
     */
    public int getCountByListIds(Set<Long> listIds) {
    	
    	try {
			String listIdsStr = Utility.getIdsAsString(listIds);
			if(listIdsStr.isEmpty() ) return 0;
			List<Long> countList = getHibernateTemplate().find("select count(listId) from MailingList where listId in ( " + listIdsStr+" )");
			if(countList.size()>0) {
				return countList.get(0).intValue();
			}else {
				return 0;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return 0;
		
		}
     }
    
 	//ISH
    public List<String> findStatusByMlists(String List, long user_id, String type)
    {
    	try {
		    if(type.equals("campaign"))
		    {
    		logger.debug("Ids for campaign are :" + List);
    		String queryStr = "SELECT distinct mc.list_id FROM mlists_campaigns mc, campaign_schedule cs WHERE cs.user_id IN (" + user_id + ") AND  mc.list_id IN(" + List +") " +
    				" AND cs.campaign_id=mc.campaign_id  AND cs.status=0";
    		logger.info("--------->"+queryStr);	
    		logger.debug("JdbcTemplate : " + jdbcTemplate);
    		
				List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

					@Override
					public Object mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
						return arg0!=null?arg0.getString("list_id"):"";
					} });
				return list;
		    }else if(type.equals("SMS"))
		    {
		    	logger.debug("Ids for sms are :" + List);
		    	String queryStr="SELECT distinct msc.list_id FROM mlists_sms_campaigns msc, SMS_campaign_schedule scs  " +
		    			"WHERE scs.user_id IN (" + user_id + ") " +
		    					"AND msc.list_id IN("+List+") " +
		    							"AND msc.sms_campaign_id=scs.sms_campaign_id AND scs.status=0";
		    	logger.info("--------->"+queryStr);
		    	logger.debug("JdbcTemplate : " + jdbcTemplate);
				List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

					@Override
					public Object mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
						return arg0!=null?arg0.getString("list_id"):"";
					} });
				return list;
		    }else if(type.equals("ET"))
		    {
		    	logger.debug("Ids for ET are :" + List);
		    	String queryStr="SELECT distinct mt.list_id FROM mlists_trigger mt, event_trigger et " +
		    			"WHERE et.user_id IN (" + user_id + ") " +
		    					"AND mt.list_id IN("+List+")" +
		    							"AND mt.id=et.id AND " + "et.options_flag&"+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+">0";
		    	logger.info("--------->"+queryStr);
		    	logger.debug("JdbcTemplate : " + jdbcTemplate);
				List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

					@Override
					public Object mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
						return arg0!=null?arg0.getString("list_id"):"";
					} });
				return list;
		    }
    	}
			 catch (Exception e) {
				 e.printStackTrace();
				logger.info(" ** Exception :", (Throwable) e);
			}
			return null;
	 	}

    
    public List<String>  getCampaignCountByMlists(String mListIds) {
		
 		try {
			logger.debug(" Email String :" + mListIds);
			String queryStr = "SELECT distinct list_id FROM mlists_campaigns where list_id in (" + mListIds + ")";
			logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("list_id"):"";
				} });
			return list;
		} catch (Exception e) {
			logger.error(" ** Exception :", (Throwable) e);
		}
		return null;
 	}
    
 	public List<String> getSMSCountByMlists(String mListIds) {
 		

		
 		try {
			logger.debug(" Email String :" + mListIds);
			String queryStr = "SELECT distinct list_id FROM mlists_sms_campaigns where list_id in (" + mListIds + ")";
			logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("list_id"):"";
				} });
			return list;
		} catch (Exception e) {
			logger.error(" ** Exception :", (Throwable) e);
		}
		return null;
 	
 		
 		
 		
 	}
 	
 	public List<String> getProgramCountByMlists(String listIdStr) {
		
 		try {
			logger.debug(" Email String :" + listIdStr);
			String queryStr = "SELECT distinct list_id FROM mlists_programs where list_id in (" + listIdStr + ")";
			logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("list_id"):"";
				} });
			return list;
		} catch (Exception e) {
			logger.error(" ** Exception :", (Throwable) e);
		}
		return null;
 	}//getProgramCountByMlists
	
 	public List<String> getEventTriggerCountByMlist(String mListIds){
 		

		
 		try {
			logger.debug(" Email String :" + mListIds);
			String queryStr = "SELECT distinct list_id FROM mlists_trigger where list_id in (" + mListIds + ")";
			logger.debug("JdbcTemplate : " + jdbcTemplate);
			List<String> list = jdbcTemplate.query(queryStr, new RowMapper(){

				@Override
				public Object mapRow(ResultSet arg0, int arg1)
						throws SQLException {
					logger.debug("arg0 :" + arg0 + " arg1 :" + arg1);
					return arg0!=null?arg0.getString("list_id"):"";
				} });
			return list;
		} catch (Exception e) {
			logger.error(" ** Exception :", (Throwable) e);
		}
		return null;
 	
 		
 		
 		
 		
 	}
 	
 	
    /**
     * Called in UploadContactController
     * This method returs the MailingList object
     */
    public MailingList findByListName(String listName, long userId) {
       /* List list = getHibernateTemplate().find("from MailingList where listName = '" + listName + 
        		"' AND users IN(SELECT userId FROM Users WHERE parentUser = " + userId+ " OR userId="+userId+")");*/
    	
    	
    	 List list = getHibernateTemplate().find("from MailingList where listName = '" + listName + 
         		"' AND users IN("+userId+")");
        
        logger.debug("Size of the list is : " + list.size());
        if (list.size() != 1) {
            logger.warn("Size of List is not 1; It is actually "+ list.size());
            return null;
        }else{
        	return (MailingList) list.get(0);
        }
	}

	/**
	 * This method is called in CampMlistController 
	 * This method returns MailingList objects with size
	 * size means number of contacts which are having Active status 
	 * @param userId
	 * @return
	 */
	/*public List findActiveLists(Long userId){
		return getHibernateTemplate().find("select new MailingList(ml.listId,ml.listName,count(c.contactId),ml.custField) " +
				" from MailingList as ml,Contacts as c where ml.users=" + userId + 
				" and c.mailingList = ml.listId  and c.emailStatus='Active' group by ml.listId order by ml.createdDate");
	}*/

	/**
	 * This method is called in ViewListController
	 * This method returns MailingList objects with size(number of contacts)
	 * in the given range
	 * @param userIds
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List findAllBySize(Set<Long> listIds, String userIDs, int firstResult, int maxResults) {
		logger.debug("Just Entered  ");
		//SessionFactory sessionFactory = getSessionFactory();
		//Session sess = sessionFactory.openSession();
		
		if(userIDs == null || userIDs.isEmpty()) return null;
		String listIdsStr = Utility.getIdsAsString(listIds);
		if(listIdsStr.isEmpty()) return null;
		
		try {
			//String query = 
			/*"SELECT  new MailingList(m.listId,m.listName," +
			"(SELECT count(c.contactId) FROM Contacts c WHERE c.users=m.users AND  bitwise_and(m.mlBit,c.mlBits) > 0 )," +
			"(SELECT COUNT(c1.contactId) FROM Contacts c1 WHERE c1.users=m.users AND  bitwise_and(m.mlBit,c1.mlBits) > 0 AND c1.emailId IS NOT NULL AND c1.emailId != '' and (c1.purged=false))"+
			",m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
			" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit) FROM MailingList m  " +
			" WHERE m.users in (" + userIdsStr + ") order by m.createdDate desc";
			*/
			
			/*String query = "SELECT  new MailingList(m.listId,m.listName," +
							"(SELECT count(c.contactId) FROM Contacts c WHERE c.users=m.users AND  bitwise_and(m.mlBit,c.mlBits) > 0 )," +
							"(SELECT COUNT(c1.contactId) FROM Contacts c1 WHERE c1.users= m.users AND  bitwise_and(m.mlBit,c1.mlBits) > 0 AND c1.emailId IS NOT NULL AND c1.emailId != '' and (c1.purged=false))"+
							",m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
							" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit) FROM MailingList m  " +
							" WHERE m.listId in (" + listIdsStr + ") order by m.createdDate desc";
			*/
		
			
			
/*			String query = 
			"SELECT  new MailingList(m.listId,m.status,m.listName," +
			"(select COUNT(ml.listId) from Contacts c JOIN c.mlSet ml WHERE m.listId=ml.listId)," +
			"(SELECT COUNT(c1.purged) FROM Contacts c1 JOIN c1.mlSet mls WHERE m.listId=mls.listId and c1.emailId IS NOT NULL AND c1.emailId != '' and (c1.purged=false))"+
			",m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
			" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId) FROM MailingList m  " +
			"where m.users in (" + userIdsStr + ") order by m.createdDate desc";
*/			
			
			/*	Commented for performance 
			 * String query = "SELECT  new MailingList(m.listId,m.listName," +
						"(SELECT count(c.contactId) FROM Contacts c WHERE c.users IN("+userIDs+") AND  bitwise_and(m.mlBit,c.mlBits) > 0 )" +
						",m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
						" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit) FROM MailingList m  " +
						" WHERE m.listId in (" + listIdsStr + ") order by m.createdDate desc";*/
				
				/*
				String query = "SELECT  new MailingList(m.listId,m.listName,m.listSize," +
						"m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
						" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit,m.listSize) FROM MailingList m  " +
						" WHERE m.listId in (" + listIdsStr + ") and m.users in ("+userIDs+")order by m.createdDate desc";*/
			
			
			/*String query = "SELECT m.listId,m.listName,m.listSize," +
			"m.description,m.createdDate,m.lastStatusChangeDate,m.checkDoubleOptin,m.consent,m.custField," +
			" m.lastModifiedDate,m.users,m.listType,m.custTemplateId,m.checkParentalConsent, m.consentCutomTempId, m.mlBit FROM MailingList m  " +
			" WHERE m.listId in (" + listIdsStr + ") and m.users in ("+userIDs+") order by m.createdDate desc";*/
			
			//Query without reflection
			/*String query = " FROM MailingList WHERE listId IN(" + listIdsStr + ") AND users IN ("+userIDs+") ORDER BY createdDate DESC ";*/
			String query = " FROM MailingList WHERE listId IN(" + listIdsStr + ") AND users IN ("+userIDs+") ORDER BY lastModifiedDate DESC ";
	
			logger.info("query ::" +query);
			
			
			List<MailingList> cList = executeQuery(query, firstResult, maxResults);
			
			logger.debug("Exiting before return");
			return cList;
		} catch (Exception e) {
			logger.error("** Error : ", (Throwable)e);
			return null;
		} 
	}
	
	/**
	 * This method is called in dashboardcontroller
	 * This method returns MailingList objects with size(number of contacts) and not only specific to the single user 
	 */
	public List<MailingList> getLatestMailingLists( int firstResult, int maxResults ) {
		logger.debug("Just Entered  ");
		List<MailingList> numOfmlList=null;
		
		try {
			String query = 
				"SELECT  new MailingList(m.listId,m.listName," +
				"(SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users=m.users AND  bitwise_and(m.mlBit,c.mlBits) > 0 )," +
				"(SELECT COUNT(c1.contactId) FROM Contacts c1 WHERE c1.users=m.users AND  bitwise_and(m.mlBit,c1.mlBits) > 0 and (c1.purged=false))"+
				",m.description,m.custField, m.lastModifiedDate,m.users) FROM MailingList m  " +
				 " order by m.createdDate desc"; 
			
			numOfmlList=executeQuery(query, firstResult, maxResults);
			
			logger.debug("Exiting before return");
			return numOfmlList;
		} catch (Exception e) {
			logger.error("** Error : ", (Throwable)e);
			return null;
		} 
	}
	/**
	 * his method is called from dashboardcontroller and retrieves the mllists specific to a single user
	 * @param userId
	 * @return
	 */
	public List<MailingList> getLatestMailingLists( String userId, int firstResult, int maxResults ) {
		logger.debug("Just Entered  ");
		List<MailingList> numOfmlList=null;
		
		try {
			String query = 
				"SELECT  new MailingList(m.listId,m.listName," +
				"(SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users=m.users AND  bitwise_and(m.mlBit,c.mlBits) > 0 )," +
				"(SELECT COUNT(c1.contactId) FROM Contacts c1 WHERE c1.users=m.users AND  bitwise_and(m.mlBit,c1.mlBits) > 0 AND (c1.purged=false))"+
				",m.description,m.custField, m.lastModifiedDate,m.users) FROM MailingList m where m.users IN ( "+userId + " )" +
				 " order by m.createdDate desc"; 
			
			numOfmlList=executeQuery(query, firstResult, maxResults);
			
			logger.debug("Exiting before return");
			return numOfmlList;
		} catch (Exception e) {
			logger.error("** Error : ", (Throwable)e);
			return null;
		} 
	}
	
	public List<MailingList> findDoubleOptinByUserId(Long userId) {
		try {
			return getHibernateTemplate().find("from MailingList  where checkDoubleOptin=true and users = " + userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
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
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	public Long findPOSListIdByUserId(Users users) {
		
		try{
			List<Long> mList = null;
			
			mList = getHibernateTemplate().find("SELECT listId FROM MailingList WHERE users.userId = "+ users.getUserId() + "  AND listType='POS'");
			if(mList == null || mList.size() == 0) return null;
			else  return mList.get(0);
			
		}catch(Exception e){
			logger.error("Exception :: ", e);
			return null;
		}
	}
	
	public MailingList findUserBCRMList(Users  users) {
		try {
			List<MailingList> mList = null;
			logger.info("userId is ::"+users.getUserId() + ":: list Type is :: POS" );
			//mList = getHibernateTemplate().find("FROM MailingList WHERE listType='"+Constants.MAILINGLIST_TYPE_HOMESPASSED+"' AND users IN(SELECT userId FROM Users WHERE parentUser = " + users.getUserId() + " OR userId="+users.getUserId()+")");
			mList = getHibernateTemplate().find("FROM MailingList WHERE listType='"+Constants.MAILINGLIST_TYPE_HOMESPASSED+"' AND users IN("+ users.getUserId() + " )");
			if(mList == null || mList.size() == 0) return null;
			else  return mList.get(0);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	
	 /*public List<MailingList> findAllByUser(Set<Long> userIds) {
	    	String userIdsStr = Utility.getUserIdsAsString(userIds);*/
	
	/*public List<Map<String, Object>> findContListsByUserId(Set<Long> userIds, String emailId) {
			try {
			List<Object[]> mListName = null;
			String userIdsStr = Utility.getUserIdsAsString(userIds);
			logger.info("userId is ::"+userIdsStr + ":: emailId is ::" +emailId);
			//====================already commented code=========================
			String qry = "SELECT ml.list_name FROM contacts c, mailing_lists ml where ml.user_id  in ("+userIdsStr+")" +
						 " and c.email_id = '"+emailId +"' and c.list_id = ml.list_id";
			String qry = "SELECT m.listName FROM MailingList m where m.users in ("+userIdsStr+")" +
					 " and m.listId in (select distinct cm.listId from Contacts c join c.mlSet cm where c.emailId = '"+emailId +"'"+")";
			
			
			String qry = "SELECT list_name FROM  mailing_lists ml, WHERE user_id  in ("+userIdsStr+")" +
					" AND (SELECT mlbits FROM contacts WHERE user_id  in ("+userIdsStr+") " +
							" AND email_id = '"+emailId +"') & mlbit >0 ";
			//==================END OF COMMENT================================================
			String qry = "select distinct ml.list_name from contacts c , mailing_lists ml " +
					"where c.user_id in ("+userIdsStr+") and ml.user_id in ("+userIdsStr+") and (c.mlbits & ml.mlbit) >0 and c.email_id = '"+emailId +"'";
			logger.info(">>>>"+qry);
			return jdbcTemplate.queryForList(qry);
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}*/
	
	
	
	public List<Map<String, Object>> findContListsByListId(Set<Long> listIds, String emailId) {
		try {
		List<Object[]> mListName = null;
		String listIdsStr = Utility.getIdsAsString(listIds);
		logger.info("list is ::"+listIdsStr + ":: emailId is ::" +emailId);
		/*String qry = "SELECT ml.list_name FROM contacts c, mailing_lists ml where ml.user_id  in ("+userIdsStr+")" +
					 " and c.email_id = '"+emailId +"' and c.list_id = ml.list_id";*/
		/*String qry = "SELECT m.listName FROM MailingList m where m.users in ("+userIdsStr+")" +
				 " and m.listId in (select distinct cm.listId from Contacts c join c.mlSet cm where c.emailId = '"+emailId +"'"+")";
		*/
		
		/*String qry = "SELECT list_name FROM  mailing_lists ml, WHERE user_id  in ("+userIdsStr+")" +
				" AND (SELECT mlbits FROM contacts WHERE user_id  in ("+userIdsStr+") " +
			
						" AND email_id = '"+emailId +"') & mlbit >0 ";*/
		if(listIdsStr.isEmpty()) return null;
		
		String qry = "select distinct ml.list_name from contacts c , mailing_lists ml " +
				"where c.user_id = ml.user_id AND ml.list_id in ("+listIdsStr+") and (c.mlbits & ml.mlbit) >0 and c.email_id = '"+emailId +"'";
		logger.info(">>>>"+qry);
		return jdbcTemplate.queryForList(qry);
		
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
}

	
	
	
	public int findParentalAssociateCount(Long templateId) {
		String qry = "SELECT COUNT(listId) FROM MailingList WHERE consentCutomTempId="+templateId;
		
		int count = ((Long)getHibernateTemplate().find(qry).get(0) ).intValue();
		return count;
		
		
	}

	public int findTemplateAssociateCount(Long templateId, String type) {

		String subqry = "";
		int count = 0;
		if(type == null || type.trim().length() == 0) {
			
			logger.info(" got type as null...");
			return count;
		}
		else  {
			if(type.equalsIgnoreCase("welcomemail")) {
				
				subqry = " checkDoubleOptin=1 AND custTemplateId="+templateId;
				
				
			}else if(type.equalsIgnoreCase("parentalConsent")) {
				
				subqry = " checkParentalConsent=1 AND consentCutomTempId="+templateId;
				
			}
			else if(type.equalsIgnoreCase("loyaltyOptin")) {
				
				subqry = " checkLoyaltyOptin=1 AND loyaltyCutomTempId="+templateId;
				
			}else if(type.equalsIgnoreCase("webformWelcomeEmail")) {
				
				subqry = " checkWelcomeMsg=1 AND welcomeCustTempId="+templateId;
				
			}
			
			
			String qry = "SELECT COUNT(listId) FROM MailingList WHERE "+subqry;
			
			count = ((Long)getHibernateTemplate().find(qry).get(0) ).intValue();
			return count;
			
		}
		
		
	}

	
	
	public int findOptInAssociateCount(Long templateId) {
		String qry = "SELECT COUNT(listId) FROM MailingList WHERE custTemplateId="+templateId;
		
		int count = ((Long)getHibernateTemplate().find(qry).get(0) ).intValue();
		return count;
		
		
	}
	
	
	 public MailingList findListTypeMailingList(String listType ,long userId) {
		 
	    	try {
	        	/*List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in(SELECT userId FROM Users " +
	        			" WHERE userId="+userId+" OR parentUser="+userId+" )  AND listType LIKE '" + listType + "%'" );*/
	        	
	        	
	        	List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in("+userId+" )  AND listType ='"+listType+"'" );
	        	
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
	    		List<MailingList> list = getHibernateTemplate().find("FROM MailingList where users in("+userId+" )  AND (listType LIKE '" + Constants.MAILINGLIST_TYPE_POS + "%' OR listType LIKE '"+Constants.MAILINGLIST_TYPE_HOMESPASSED+"%')" );
	    		if(list == null || list.size() ==0) {
	        		return null;
	        	}
	        	return list;
	       	} catch (DataAccessException e) {
				return null;
	       	}
	  }
	 
	 
		 public long findTotOnlyEmail(MailingList mList) {
			 
//			 logger.info("mList.getUsers().getUserId()="+mList.getUsers().getUserId());
			 String qry = " SELECT COUNT(DISTINCT contactId) FROM Contacts WHERE " +
		 		" users = " + mList.getUsers().getUserId() +" AND bitwise_and(mlBits,"+mList.getMlBit().longValue()+") > 0 " +
		 		" AND emailId IS NOT NULL AND emailId != '' ";
				
			long count = ((Long)getHibernateTemplate().find(qry).get(0) ).longValue();
			return count;
			 
		 }
		 
	public List<MailingList> findByContactBit(Set<Long> listIdSet, Long contactBit) {
		try {
			String listIdsStr = Utility.getIdsAsString(listIdSet);
			if(listIdsStr.isEmpty()) return null;
			//String qry=" FROM MailingList  WHERE users = " + userId +" AND bitwise_and(mlBit,"+contactBit.longValue()+") > 0 ";
			String qry="select DISTINCT m FROM MailingList m,Contacts c  WHERE c.users = m.users AND m.listId IN("  + listIdsStr +") AND bitwise_and(m.mlBit,"+contactBit.longValue()+") > 0 ";
			return getHibernateTemplate().find(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
			
	
	
	public List<String> findListNamesByContactBit(Set<Long> listIdSet, Long contactBit) {
		try {
			String listIdsStr = Utility.getIdsAsString(listIdSet);
			if(listIdsStr.isEmpty()) return null;
			//String qry=" FROM MailingList  WHERE users = " + userId +" AND bitwise_and(mlBit,"+contactBit.longValue()+") > 0 ";
			String qry="SELECT DISTINCT m.listName FROM MailingList m,Contacts c  WHERE c.users = m.users AND m.listId IN("  + listIdsStr +") AND bitwise_and(m.mlBit,"+contactBit.longValue()+") > 0 ";
			return getHibernateTemplate().find(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	public List<MailingList> findByContactBit(Long userId, Long contactBit) {
		try {
			/*String listIdsStr = Utility.getIdsAsString(listIdSet);
			if(listIdsStr.isEmpty()) return null;*/
			String qry=" FROM MailingList  WHERE users = " + userId +" AND bitwise_and(mlBit,"+contactBit.longValue()+") > 0 ";
			logger.info("findByContactBit query----->"+qry);
			//String qry="select DISTINCT m FROM MailingList m,Conatcts c  WHERE c.users = m.users AND m.listId IN("  + listIdsStr +") AND bitwise_and(m.mlBit,"+contactBit.longValue()+") > 0 ";
			return getHibernateTemplate().find(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
			
	
	public Long getLastMbit(Long userId) {
    	
    	String query = "SELECT MAX(mlBit) FROM MailingList WHERE users= "+userId ;
    	List<Long> bitList = getHibernateTemplate().find(query);
    	if(bitList.size()>0){
    		logger.info("bitlist size :"+bitList.size());
    		
    		return bitList.get(0);
    	}
    	else{
    		return null;
    	}
    }

	public Long getNextAvailableMbit(Long userId) {
    	
    	String query = "SELECT mlBit FROM MailingList WHERE users= "+userId +" ORDER BY mlBit ASC ";
    	List<Long> bitList = getHibernateTemplate().find(query);
    	
    	if(bitList==null || bitList.isEmpty()) {
    		return 1l;
    	}
    	
		logger.info("bitlist size :"+bitList.size());
		long tempLong=0;
		int i=0;
		
		if(bitList.size() >=60){
			return 0l;
		}
		
		for (; i < bitList.size(); i++) {
			tempLong = (long)Math.pow(2, i);
			if(tempLong != bitList.get(i)) {
				return tempLong;
			}
		} // for
		tempLong = (long)Math.pow(2, i);
		return tempLong;
   }

	public MailingList findByListId(long listId){

    	try {
        	List<MailingList> list = getHibernateTemplate().find("FROM MailingList where listId ="+listId );
        	if(list == null || list.size() ==0) {
        		return null;
        	}
        	return list.get(0);
		} catch (DataAccessException e) {
			return null;
		}
		
	} 
	
	public List<Long> findNonSharedLists(Long userID, String sharedListIds) {
		
		String subQry = "";
		
		if(sharedListIds != null && sharedListIds.length() > 0 ) {
			
			
			subQry = " AND listId NOT IN("+sharedListIds+")";
		}
		
		List<Long> mlList = getHibernateTemplate().find("SELECT listId FROM MailingList WHERE users="+userID.longValue()+subQry);
				
		
		if(mlList != null && mlList.size() > 0) {
			
			return mlList;
		}else{
			return null;
		}
		
	}
	
	public List<UsersDomains> findSharedToDomainsByListID(Long listId) {
		
		String Query ="SELECT  DISTINCT ud FROM MailingList m JOIN m.sharedToDomain  ud WHERE m.listId IN("+listId.longValue()+")";//+subQry;
    	return executeQuery(Query);
    	
		
		
		
		
		
	}
	
	
	
	
	public List<MailingList> findAllTriggerLists(Long etId) {
		
		
		 String query = " SELECT DISTINCT m FROM MailingList m join m.triggersSet et WHERE et.id="+etId ;

		 return executeQuery(query);
		
		
		
	}

	public MailingList getMailingListByName(Long userId, String listName) {
		List list = getHibernateTemplate().find("from MailingList where listName = '"+ listName +"' AND users="+userId);
		if(list.size()>0)
			return (MailingList)list.get(0);
		else return null;
	}
	
	
	public List<Long> findUsersBy(Set<Long> mlSet) throws BaseDAOException {
		String listIdsStr = Utility.getIdsAsString(mlSet);
		if(listIdsStr.isEmpty()) return null;
		
		String qry = "SELECT DISTINCT users.userId FROM MailingList WHERE listId IN("+listIdsStr+")";
		
		List<Long> retList = executeQuery(qry);
		
		return retList;
		
	}
}

