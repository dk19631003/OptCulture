package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class UpdateOptSyncDataDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	/*public UpdateOptSyncData find(Long id) {
		return (UpdateOptSyncData) super.find(UpdateOptSyncData.class, id);
	}*/

	public void saveOrUpdate(UpdateOptSyncData updateOptSyncData) {
		super.saveOrUpdate(updateOptSyncData);
	}

	/*public UpdateOptSyncData  findBy(Long optSyncId, Long userId){
		
		UpdateOptSyncData updateOptSyncData= null;
		
		String qry="FROM UpdateOptSyncData WHERE optSyncId ="+optSyncId.longValue()+ " AND userId = "+userId.longValue()+"  ";
		
		List list =getHibernateTemplate().find(qry);
		
		if(list.size() > 0){
			updateOptSyncData= (UpdateOptSyncData)list.get(0);
			
		}
		
		return updateOptSyncData;
	}
	
	public List<UpdateOptSyncData> findAllBy() throws Exception {
		
		List<UpdateOptSyncData> updateOptSyncList = null;
		String qry = "FROM UpdateOptSyncData  where status in('A' , 'D') ORDER BY 1 DESC " ;
		updateOptSyncList= getHibernateTemplate().find(qry);
		
		if(updateOptSyncList != null && updateOptSyncList.size() > 0){
			return updateOptSyncList;
		}else{
			
			return null;
		}
		
	}
	
	
	
public List<UpdateOptSyncData> findAllByUserId(long userId) throws Exception {
		
		List<UpdateOptSyncData> updateOptSyncList= null;
		String qry = "FROM UpdateOptSyncData  where userId = "+userId ;
		updateOptSyncList= getHibernateTemplate().find(qry);
		
		if(updateOptSyncList.size() > 0){
			return updateOptSyncList;
		}else{
			
			return null;
		}
		
	}

public List<UpdateOptSyncData> findAllByUserId(long userId)  {
	
	List<UpdateOptSyncData> updateOptSyncList= null;
	String qry = "FROM UpdateOptSyncData  where userId = "+userId ;
	try{
	
	updateOptSyncList= getHibernateTemplate().find(qry);
	
	if(updateOptSyncList.size() > 0){
		return updateOptSyncList;
	}else{
		
		return null;
	}
	}
	catch(Exception exception){
		return null;
	}
	
}
	
	
	public Long findTotalBySearch(long userId, String searchByKey, String searchByValue){
		
		try{
			String searchByQry ="";
			if(searchByKey!=null && searchByValue!=null) {
				if(searchByKey.equalsIgnoreCase("OC_id")) {
					searchByQry = " ="+searchByValue+" ";
				}
				else if(searchByKey.equalsIgnoreCase("optSync_name") ||
						searchByKey.equalsIgnoreCase("email_id")) {
					searchByQry = ""+searchByKey+" LIKE '%"+searchByValue+"%' ";
			}
			}
			String query = null;

			//query = "SELECT count(cl.loyalty_id) "+
				//	" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "+
					//" where cl.user_id = "+ userId+ searchByQry ;
			//SELECT COUNT(id) FROM opt_sync_data WHERE user_id3 AND  =1001 
			query="SELECT COUNT(id) FROM opt_sync_data WHERE user_id = "+userId+" AND "+searchByKey +" = "+searchByValue;
			
			//logger.info("my qry::"+query);
			
			Long tempCount = jdbcTemplate.queryForLong(query);
			  
			if(tempCount != null) {
				return tempCount;			
			}
			return null;
		} catch (Exception e) {
			logger.info("Error while fetching loyalty cards report options count...."+e);
			return null;
		}
	
		
	}

	public List<UserOrganization> findAccountId(Long orgId) {

		try {
			List orgList = getHibernateTemplate().find("FROM UserOrganization where userOrgId ="+ orgId); 
			
			if(orgList == null || orgList.size() == 0) return null;
			else return orgList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}


	public List<UserOrganization> findAllOrganizations() {
		try {
			List orgList = getHibernateTemplate().find("FROM UserOrganization ORDER BY orgExternalId ASC "); 
			
			if(orgList == null || orgList.size() == 0) return null;
			else return orgList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	

	public List<UpdateOptSyncData> findByOrganization(Long userOrgId,
			int startIndex, int size, String key) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
	
	  public void delete(UpdateOptSyncData optSyncData) throws BaseDAOException{

		  String qry = "DELETE from UpdateOptSyncData WHERE id="+optSyncData.getId().longValue();
		  
		  executeUpdate(qry);
      }

	 //Uniques based on user id 
	/*public UpdateOptSyncData findAllByOptSynId(long userId,long generateNumber) {

		try {
    		List<UpdateOptSyncData> list = null;
			list = getHibernateTemplate().find("FROM UpdateOptSyncData  where optSyncId  = "+generateNumber +"AND  userId ="+userId );
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    
	}//findAllByOptSynId
	
	
	//unique entire application
	public List<UpdateOptSyncData> findAllByOptSynId(long generatedNumber) {
		List<UpdateOptSyncData> list = null;
		try {
    		
			list = getHibernateTemplate().find("FROM UpdateOptSyncData  where optSyncId  = "+generatedNumber  );
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    
	}//findAllByOptSynId
	

	public List<UserOrganization> findAllByOptSyncName(String generatedString) {
		try {
    		List<UserOrganization> list = null;
			list = getHibernateTemplate().find("FROM UserOrganization  where optSyncKey = '"+generatedString+"'");
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	
	

	public List<UserOrganization> findAllByOptSyncName(long userOrgId , String generatedString ) {
		try {
    		List<UserOrganization> list = null;
			list = getHibernateTemplate().find("FROM UserOrganization  where optSyncKey = '"+generatedString+"'" +" AND userOrgId ="+userOrgId);
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	
	public List<UserOrganization> findOptSynKey(long userOrgId) {

		
		try {
			List orgList = getHibernateTemplate().find("FROM UserOrganization WHERE userOrgId = "+ userOrgId ); 
			
			if(orgList == null || orgList.size() == 0) return null;
			else return orgList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	

	public UserOrganization findByOptSynKey(long userOrgId) {

		UserOrganization userOrganization=null;
		try {
			userOrganization = (UserOrganization) getHibernateTemplate().find("FROM UserOrganization WHERE userOrgId = "+ userOrgId ); 
			
			if(userOrganization == null ) return null;
			else return userOrganization;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	public UpdateOptSyncData findByUser(Long userId){
		UpdateOptSyncData updateOptSyncData = null;
		try {
    		updateOptSyncData = (UpdateOptSyncData) getHibernateTemplate().find("FROM UpdateOptSyncData  where userId = "+userId);
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		return updateOptSyncData;
	}*/
	
	public int updatePluginStatus(String pluginStatus,Long optSyncId){
		logger.debug(">>>>>>> Started  updatePluginStatus :: ");
		String query = "UPDATE UpdateOptSyncData SET pluginStatus= '"+pluginStatus+"' WHERE optSyncId ="+optSyncId;
		logger.debug("<<<<< Completed updatePluginStatus .");
		 return	executeUpdate(query);
		
	}
	// Added after 2.3.9
	/**
	 * This method update Alert Sending Status.
	 * @param sendingAlertsBy
	 * @param optSyncId
	 * @return
	 */
	public int updateAlertSendingStatus(String sendingAlertsBy,Long optSyncId){
		logger.debug(">>>>>>> Started  updateAlertSendingStatus :: ");
		String query = "UPDATE UpdateOptSyncData SET onAlertsBy = '"+sendingAlertsBy+"' WHERE optSyncId ="+optSyncId;
		//logger.info("qry is::"+query);
		logger.debug("<<<<< Completed updateAlertSendingStatus .");
		return	executeUpdate(query);

	}//updateAlertSendingStatus

	/*public List<UpdateOptSyncData> findByOrgId(Long orgId){
		List<UpdateOptSyncData> updateOptSyncData = null;
		try {
			updateOptSyncData = (List<UpdateOptSyncData>) getHibernateTemplate().find("FROM UpdateOptSyncData  where orgId = "+orgId);

		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		return updateOptSyncData;
	}

	public UpdateOptSyncData findOptSyncByPluginId(Long optSyncId) {
		logger.debug(">>>>>>> Started  findOptSyncByPluginId :: ");
		UpdateOptSyncData updateOptSyncData = null;
		List<UpdateOptSyncData> list = getHibernateTemplate().find("FROM UpdateOptSyncData  where optSyncId= "+optSyncId);
		
		if(list != null && list.size() >0){
			updateOptSyncData = list.get(0);
		}
		logger.debug("<<<<< Completed findOptSyncByPluginId .");
		return updateOptSyncData;
	}

	public List<UpdateOptSyncData> findOptSyncByUserId(Long userId) {
		logger.debug(">>>>>>> Started  findOptSyncByUserId :: ");
		List<UpdateOptSyncData> list = getHibernateTemplate().find("FROM UpdateOptSyncData  where userId= "+userId);
		if(list != null && list.size() > 0)	{
			logger.debug("<<<<< Completed findOptSyncByUserId .");
			return list;
		}
		else{
			logger.debug("<<<<< Completed findOptSyncByUserId .");
			return null;
		}
		
	}*///findOptSyncByUserId

	public int updateOptSyncMonitoring(Long userId,	String optSyncEnableMointoring) {
		String query = "UPDATE UpdateOptSyncData SET enabledOptSyncFlag= '"+optSyncEnableMointoring+"' WHERE userId ="+userId;
		logger.debug("<<<<< Completed updatePluginStatus .");
		 return	executeUpdate(query);
	}


}

