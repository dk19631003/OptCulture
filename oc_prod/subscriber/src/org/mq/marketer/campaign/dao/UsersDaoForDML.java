package org.mq.marketer.campaign.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.Authorities;
import org.mq.marketer.campaign.beans.GetTokenRequestLog;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "unused"})
public class UsersDaoForDML extends AbstractSpringDaoForDML{


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public UsersDaoForDML(){}

   /* public Users find(Long id){
    	
    	return findByUserId(id);*/
    	
//        return (Users) super.find(Users.class, id);
  /*  }*/

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(Users user){
        super.saveOrUpdate(user);
    }
    
    public void saveOrUpdate(Authorities authority){
        super.saveOrUpdate(authority);
    }
    
    
    public void saveOrUpdate(UserOrganization userOrganization){
        super.saveOrUpdate(userOrganization);
    }
    
    public void saveOrUpdate(GetTokenRequestLog getTokenRequestLog){
        super.saveOrUpdate(getTokenRequestLog);
    }
    
    public void saveOrUpdate(UserSMSSenderId userSMSSenderId){
        super.saveOrUpdate(userSMSSenderId);
    }
    
    public void delete(Users user){
    	
    	getHibernateTemplate().bulkUpdate("delete FROM Authorities WHERE username = '" + user.getUserName() + "'");
    	getHibernateTemplate().bulkUpdate("delete FROM UserSMSSenderId WHERE username = '" + user.getUserName() + "'");
        super.delete(user);
    }
    
    public void saveByCollection(List<Users> userList) {
    	super.saveByCollection(userList);
    	
    	
    }

   /* public List<UsersDomains> findDomainsByIds(String domString) {
    	
    	return getHibernateTemplate().find("From UsersDomains WHERE domainId="+domString);
    	
    	
    }
    
    public Users findByToken(String userName,String token) {

    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	   Calendar cal = Calendar.getInstance();
  	   String datestr = dateFormat.format(cal.getTime());
    	
    	
    	logger.info("Ented findByToken method with value::::::::   " + userName + "   ::::::::token:::::::::::  " + token);
    	List<Users> list = getHibernateTemplate().find("FROM Users WHERE userName='"+ userName +"' AND  token='"+ token+"' AND enabled = true AND packageExpiryDate >= '"+datestr+"'");

    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	logger.info("user object with token not found::::: so trying with optsync key::::::::::::");

    	list = getHibernateTemplate().find("FROM Users WHERE userName='"+ userName +"' AND  userOrganization.optSyncKey='"+ token+"' AND enabled = true AND packageExpiryDate >= '"+datestr+"'");

    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
    }*/
    
   /* public Users findUserByToken(String userName,String token) {
*/
    /*	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	   Calendar cal = Calendar.getInstance();
  	   String datestr = dateFormat.format(cal.getTime());
    	*/
    	
    	/*logger.info("Ented findByToken method with value::::::::   " + userName + "   ::::::::token:::::::::::  " + token);
    	List<Users> list = getHibernateTemplate().find("FROM Users WHERE userName='"+ userName +"' AND  token='"+ token+"'");

    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	logger.info("user object with token not found::::: so trying with optsync key::::::::::::");

    	list = getHibernateTemplate().find("FROM Users WHERE userName='"+ userName +"' AND  userOrganization.optSyncKey='"+ token+"'");

    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
    }/**/
    
   /* public Users findActiveUser(String userName, String token) {
    	
       DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 	   Calendar cal = Calendar.getInstance();
 	   String datestr = dateFormat.format(cal.getTime());
    	
 	   String queryStr = "FROM Users WHERE userName='"+ userName +"' AND  token='"+
			   token+"' AND enabled = true AND packageExpiryDate >= '"+datestr+"'";
 	   logger.info("active user query = "+queryStr);
    	List<Users> list = getHibernateTemplate().find(queryStr);
    	
    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
    }*/
    
    public void saveAuthoritiesByCollection(List<Authorities> authoritiesList) {
    	logger.info("----just entered5656---");
    	super.saveByCollection(authoritiesList);
    }
    
    public void delete(Long id) {
    	String queryStr = "DELETE FROM UserSMSSenderId where id="+id;
    	
    	executeUpdate(queryStr);
    }
    
   /* public List<Users> findByAuthority(String role, Long orgId) {
    	
    	String qry = "FROM Users WHERE userOrganization="+orgId+" " +
    			"AND userName IN(SELECT username FROM Authorities WHERE authority='"+role+"') ";
    	return getHibernateTemplate().find(qry);
    	
    	
    	
    }*/
     
    
    
    
  /*  public List<Users> findByStatus(boolean status, Long orgId) {
    	
    	String qry = "FROM Users WHERE userOrganization="+orgId+" AND enabled="+status+" AND " +
				" username IN(SELECT username FROM Authorities WHERE authority IN('"+Constants.ROLE_USER_BASIC+"','"+Constants.ROLE_USER_POWER+"'))"; 
;
    	return getHibernateTemplate().find(qry);
    	
    	
    	
    }*/
    
    
    
	public void delete(int id)
	{
		Session session = getSessionFactory().openSession();
		Query query = session.createQuery("from Users where userId='" + id +"'");
		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			Users users = (Users) list.get(i);
			delete(users);
		}
        session.close();
	}

   /* public List findAll(){
        return super.findAll(Users.class);
    }*/

    /*public List<Users> findOrderbyAllUsers(){
    	logger.info("just entered-----");
    	return getHibernateTemplate().find("from Users order by userName");
    }*/
  /*  
    public List<Users> findOrderbyAllUsers(String searchStr, int firstResult, int size){
    	logger.info("just entered-----");
    	String appendQuery = "";
    	if(searchStr != null && !searchStr.isEmpty()){
    		
    		appendQuery = " WHERE userName LIKE '%" + searchStr + "%__org__%' ";
    	}else {
    		appendQuery = " WHERE accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared' ";
    	}
    	String qryStr = "from Users " + appendQuery +" AND enabled = true AND accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared' order by userName";
		*///    	return getHibernateTemplate().find("from Users order by userName");
    	/*List<Users> list = executeQuery(qryStr, firstResult, size);
    	
    	return list;
    }
    */
   /* public int getTotalCountOfAllUsers (String searchStr) {
    	
    	String appendQuery = "";
    	if(searchStr != null && !searchStr.isEmpty()){
    		
    		appendQuery = " WHERE userName LIKE '%" + searchStr + "%__org__%' ";
    	} else {
    		appendQuery = " WHERE accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared' ";
    	}
    	String query = " select count(*) from Users "+ appendQuery;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }*/
    
   /* public Users findByUsername (String name){
    	Users users = null;
         List usersList = getHibernateTemplate().find("from Users where userName = '"+name+"' ");
         if(usersList.size()>0){
				users = (Users)usersList.get(0);
		 }
         return users; 
         
    }*/
    
   /* public List<Users>  findUsersByAccType(String accType) {
		
		String qry = "FROM Users WHERE accountType='"+accType+"'";
		
		return getHibernateTemplate().find(qry);
		
	}
    */
    
   /* public Users findByUsernameAndPassword(String name, String password) {
    	
    	String qry = "SELECT userName FROM Users WHERE userName='"+name+"'";
    	
    	List<String> userNameList = executeQuery(qry);
    	if(userNameList == null || userNameList.size() == 0) return null;
    	name = userNameList.get(0);
    	
    	password = Utility.encryptPassword(name,password);
    	
    	Users users = null;
        List usersList = getHibernateTemplate().find("from Users where userName = '"+  name + "' AND password='"+ password + "'");
        
        if(usersList.size()>0){
				users = (Users)usersList.get(0);
		}
        return users; 
         
    }
    */
    /*public UserOrganization findByOrgName(String orgShortName) {
    	UserOrganization userOrg = null;
    	
    	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE orgExternalId = '"+orgShortName+"'"); 
    	
    	 if(userOrgList.size()>0){
    		 userOrg = (UserOrganization)userOrgList.get(0);
		 }
      return userOrg; 
      
    	
    }*/
    
    
   /* public Users findByUsername (String name, Long orgnizationId){
    	Users users = null;
         List usersList = getHibernateTemplate().find("from Users where userName = '"+  name + "' AND userOrganization="+orgnizationId);
         if(usersList.size()>0){
				users = (Users)usersList.get(0);
		 }
         return users; 
         
    }
    */
    //added for multi user acc
    
   /* public List<Users> findAllByIds(Set<Long> userIds) {
    	
    	String userIdsStr = Utility.getUserIdsAsString(userIds);
    	
    	return getHibernateTemplate().find("FROM Users Where userId IN ("+userIdsStr+")");
    	
    }
*/
    
    public int setAuthorities(String userName, String existedRole, String userRole) {
    	
    	
    	String qry = "UPDATE Authorities SET authority='"+userRole+"' WHERE userName='"+userName+"' AND authority='"+existedRole+"'";
    	
    	return getHibernateTemplate().bulkUpdate(qry);
    	
    }
    
    
    
   /* public List getUserIdsByDomain(String domainIds) {
		String qry ="SELECT DISTINCT user_id, username from users where account_type='shared' AND user_id in ( " +
				" SELECT  DISTINCT user_id FROM users_domains WHERE domain_id IN ( "+domainIds +" ) )";
		
		return jdbcTemplate.queryForList(qry);
	}*/
    
   /* public List<Users> getSharedUsersByParentUserId(Long userId) {
		return getHibernateTemplate().find("FROM Users where parentUser = "+ userId );
	}*/
    
	/*public String getUsernmeById(int id){
		List list = getHibernateTemplate().find("select userName from Users where userId = "+  id );
		if(list.size()>0)
			return (String)list.get(0);
		else return "";
	}*/

	/*public List<Users> getUserInfoByEmail(String email) {
		return getHibernateTemplate().find("from Users where emailId = '"+ email +"' AND password IS NOT NULL");
	}

	public List<Users> getUsersByDomain(Long domainId) {
		return getHibernateTemplate().find("FROM Users WHERE userDomain = "+ domainId );
	}
*/
	//String Query ="SELECT  DISTINCT ud FROM MailingList m JOIN m.sharedToDomain  ud WHERE m.listId IN("+listId.longValue()+")";
	/*public List<UsersDomains> getAllDomainsByUser(Long userId) {
		
		String Query = " SELECT  DISTINCT ud FROM Users u JOIN u.userDomains ud WHERE u.userId IN("+userId.longValue()+")";
		
		 List<UsersDomains> domainList = getHibernateTemplate().find(Query);
		 
		 if(domainList != null && domainList.size() > 0) return domainList;
		 else return null;
		
		
		
	}*/
	/*
	public List getUsersByDomain(String domainIds) {
		
		logger.info("domain ids::"+domainIds);
		
		String qry ="SELECT ud.user_id , sr.name  FROM users_domains ud, users u, users_roles ur, sec_roles sr  WHERE ud.domain_id IN("+domainIds+") and " +
					" u.user_id=ur.user_id AND u.user_id=ud.user_id AND ur.role_id = sr.role_id  AND "+
					" sr.type IN('All') ";
					
		
		return jdbcTemplate.queryForList(qry);
		*/
		/*return jdbcTemplate.queryForList("SELECT user_id FROM users_domains WHERE domain_id IN("+domainIds+") AND " +
				" user_id IN(SELECT user_id FROM users where username IN( SELECT username FROM authorities WHERE authority IN('"+Constants.ROLE_USER_BASIC+"','"+Constants.ROLE_USER_POWER+"')))"); 
*/
	/*}*/
	
	
	/*public List<Users> getUsersByOrg(Long userOrgId) {
		
		return getHibernateTemplate().find("FROM Users WHERE userOrganization="+userOrgId+" AND parentUser " +
				" IS NULL AND username IN(SELECT username FROM Authorities WHERE authority IN('"+Constants.ROLE_USER_BASIC+"','"+Constants.ROLE_USER_POWER+"'))");
		
		
	}*/
	/**
	 * @deprecated
	 * @param userName
	 * @return
	 */
	/*public List<Authorities> findRole(String userName) {
		
		String qry = "FROM Authorities WHERE userName='"+userName+"'";
		
		return getHibernateTemplate().find(qry);
		
	}*/
	
	/**
	 * @return
	 */
	/* public List findAllAdmins() {
		 
		 try {
		*/		/*String countQry = "SELECT user_id FROM authorities, users " +
						" where users.username=authorities.username " +
						" and authority='ROLE_ADMIN' ";*/
			 
			 /*String countQry = "SELECT u.user_id FROM  users u, users_roles ur, sec_roles sr  " +
				" where u.user_id=ur.user_id AND ur.role_id = sr.role_id AND sr.type in('"+Constants.SECROLE_TYPE_OPT_ADMIN+"')" ;
				

				
				return jdbcTemplate.queryForList(countQry);
				
			} catch (DataAccessException e) {
				logger.error(" Exception : ",(Throwable)e);
				return null;
			}
			
	    }*/
	/* public List findAllUsers() {
		 try {
				String queryStr = "SELECT userId,userName FROM Users ORDER BY userName";
				return getHibernateTemplate().find(queryStr);
			 } catch (DataAccessException e) {
				 logger.error(" Exception : ",(Throwable)e);
				 return null;
			 }*/ //catch
	/* }
	 */
	 
	 
	/* public List<UserSMSSenderId> getSenderIds(String userName) {
		 
		 String qry = "FROM UserSMSSenderId where userName='"+userName+"'";
		 
		 List<UserSMSSenderId> tempSenderId = executeQuery(qry);
		 
		 return tempSenderId;
		 
		 
	 }
	 */
	 
	/* public List<String> getSenderIdByUserName(String userName) {
		 String qry = "SELECT senderId FROM UserSMSSenderId WHERE userName='"+userName+"'";
		 
		 
		 return getHibernateTemplate().find(qry);
		 
		 
		 
	 }
	 */
	 
	 
	/*public List<Map<String,  Object>> findPowerUsersOfSelDomain(Long domainId){
		
		String qry = "SELECT ud.domain_id,ud.user_id FROM users_domains ud,users u, users_roles ur, sec_roles sr" +
				"  WHERE u.user_id =ud.user_id  AND ud.domain_id="+domainId.longValue()+" AND "+
		
					" u.user_id=ur.user_id AND ur.role_id = sr.role_id AND    sr.name in('Power User')";
					
		*/
		/*String qry = "SELECT domain_id FROM users_domains WHERE user_id IN( SELECT user_id FROM users where user_organization=" +userOrgId+
					" AND username IN( SELECT username FROM authorities WHERE authority='ROLE_USER_POWER'))";*/
	/*	
		return jdbcTemplate.queryForList(qry);
		
	}
	
	*/
	/*public List<UserOrganization> findAllOrganizations() {
		try {
			List orgList = getHibernateTemplate().find("FROM UserOrganization Where orgStatus='A' ORDER BY orgExternalId"); 
			
			if(orgList == null || orgList.size() == 0) return null;
			else return orgList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
*/
	
	
	/*public List<UserOrganization> findAccountId(Long orgId) {

		try {
			List orgList = getHibernateTemplate().find("FROM UserOrganization where userOrgId ="+ orgId); 
			
			if(orgList == null || orgList.size() == 0) return null;
			else return orgList;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
*/
	
	
	//can be deprecated
	/**@deprecated
	 * 
	 */
	/*public List<Users> getPrimaryUsersByOrg(Long userOrgId) {
		
		return getHibernateTemplate().find("FROM Users WHERE userOrganization="+userOrgId+" AND parentUser  IS NULL ORDER BY userName ");
		
	}
	*/
	
	/*public Users findMlUser(Long userId) {
		
		String qry = "";*/
		
		//List<Users> list = (List<Users>)getHibernateTemplate().find("FROM Users WHERE userId="+userId+" AND parentUser is null OR userId in(SELECT parentUser From Users WHERE userId="+userId+")" );
		
		/*List<Users> list = (List<Users>)getHibernateTemplate().find("FROM Users WHERE userId="+userId+"");
		
		if(list != null && list.size() > 0) {
			
			
			return list.get(0);
		}
		
		return null;
	}
*/
	
	/*public List<Users> getPOSListUsers() {
		
		return getHibernateTemplate().find(" SELECT u FROM Users u, MailingList ml WHERE ml.listType='POS' " +
				" AND ml.users.userId=u.userId " );
		
	}
	*/
	/*public List<Users> getPOSListUsersByOrgId(Long orgId) {
		
		return getHibernateTemplate().find(" SELECT u FROM Users u, MailingList ml WHERE u.userOrganization = "+orgId+" AND  ml.listType='POS' " +
				" AND ml.users.userId=u.userId " );
		
	}
	
	public List<Map<String, Object>> getSalesLatestDetails() {
		try {
			
			String qry="SELECT  distinct t1.user_id, t1.earliest, t1.latest, t1.rcptcount, pos.last_fetched_time " +
					" FROM ( " +
					" SELECT user_id, min(sales_date) as earliest, max(sales_date) as latest, count(distinct reciept_number) as rcptcount " +
					" FROM retail_pro_sales " +
					" group by user_id " +
					" ) AS t1, mailing_lists ml, user_pos_ftp_settings pos " +
					" where ml.user_id=t1.user_id AND ml.list_type='POS' " +
					" AND " +
					" pos.user_id=ml.user_id AND pos.file_type='Sales' ";
			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(qry);
			logger.info(list);
			return list;
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return null;
	}*/
	
   /* public List<Users> findByRoleId(Long roleId) {
        String query = "SELECT DISTINCT u FROM Users u JOIN u.roles r where r.role_id= " + roleId;
        return getHibernateTemplate().find(query);
    }
    
    public Users findByUserId(Long userId) {
    	String qry ="FROM Users WHERE userId="+userId.longValue();
    	
    	List<Users> list = (List<Users>)getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			
			return list.get(0);
		}
		
		return null;
    }
    */
    //added for sharing
 /*   public String findNameById(String userId) {
    	
    	String qry = "SELECT userName FROM Users WHERE userId="+userId;
    	
    	List<String> userNameList = executeQuery(qry);
    	
    	if(userNameList != null && userNameList.size() > 0) {
    		
    		return userNameList.get(0);
    		
    	}*///if
    	
    /*	return null;
    }*/
    //===================newly added  methods==================================
    /*public List<Users> findByuserId(String userIdStr) {
    	
    	try {
    		String qry = "FROM Users WHERE userId in ("+userIdStr+")";
    		List<Users> list = (List<Users>) getHibernateTemplate().find(qry);
    		if(list == null || list.size() == 0) return null;
    		else return list;
    	} catch (DataAccessException e) {
    		// TODO Auto-generated catch block
    		logger.error("Exception ::" , e);
    		return null;
    	}
    	
    }
    
    public List<Users> findByRoleName(String roleName) {
    	
    	String qry = "SELECT DISTINCT u FROM Users u join u.roles ur WHERE ur.name='"+roleName+"'";
    	
    	return getHibernateTemplate().find(qry);
    	
    }
    
    public List<SecRoles> findByRole(String roleName) {
		
		String qry = "FROM SecRoles WHERE name='"+roleName+"'";
		
		return getHibernateTemplate().find(qry);
		
	}
    
    public List<Users> findByDigiReceptExtraction(boolean drflag){
    	
    	
    	try {
    		String query = "FROM Users WHERE digitalReceiptExtraction = "+drflag;
    		List<Users> list = (List<Users>) getHibernateTemplate().find(query);
    		if(list == null || list.size() == 0) return null;
    		else return list;
    	} catch (DataAccessException e) {
    		// TODO Auto-generated catch block
    		logger.error("Exception ::" , e);
    		return null;
    	}
    	
    }
    */
    //find by orgnazion level of users
   /* public List<Users> getUsersListByOrg(Long  orgId){
    	
    	
    	try {
    		String query = "FROM Users WHERE userOrganization = "+orgId.longValue();
    		List<Users> list = (List<Users>) getHibernateTemplate().find(query);
    		if(list == null || list.size() == 0) return null;
    		else return list;
    	} catch (DataAccessException e) {
    		// TODO Auto-generated catch block
    		logger.error("Exception ::" , e);
    		return null;
    	}
    	*/
    /*}*/ // getUsersListByOrg
    
   /* public List<Users> getUsersByOrgIdAndUserId(Long orgId,Long userId){
    	
    	String appendQuery = "";
    	
    	if(userId != null){
    		appendQuery = " AND userId = " +userId.longValue()+" AND enabled = true AND accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared'";
    	}
    	
    	String query = " FROM Users WHERE userOrganization = " + orgId.longValue() + appendQuery+" ORDER BY userName ASC";
    	List<Users> list =(List<Users>) getHibernateTemplate().find(query);//(List<Users>) getHibernateTemplate().find(query);
		return list;
    }*/
    
    /*public List<Users> getUsersByOrgIdAndUserId(Long orgId,Long userId,int firstResult, int size,String searchStr) {
    	
    	String appendSearchQuery = "";
    	if(searchStr != null && !searchStr.isEmpty()){
    		
    		appendSearchQuery = " AND userName LIKE '%" + searchStr + "%__org__%' ";
    	}
    	
    	String appendQuery = "";
    	
    	if(userId != null){
    		appendQuery = " AND userId = " +userId.longValue()+" AND accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared'";
    	}
    	
    	String query = " FROM Users WHERE userOrganization = " + orgId.longValue() + appendQuery + appendSearchQuery;
    	List<Users> list = executeQuery(query, firstResult, size);//(List<Users>) getHibernateTemplate().find(query);
		return list;
    }
    */
  /*  public int getTotalCountOfUsersByOrgIdAndUserId (Long orgId,Long userId,String searchStr) {
*///    	String query = " select count(*) from Users ";
    	
    /*	String appendSearchQuery = "";
    	if(searchStr != null && !searchStr.isEmpty()){
    		
    		appendSearchQuery = " AND userName LIKE '%" + searchStr + "%__org__%' ";
    	}
    	
    	String appendQuery = "";
    	
    	if(userId != null){
    		appendQuery = " AND userId = " +userId.longValue()+" AND accountType NOT LIKE 'Shared' AND userName NOT LIKE '%shared'";
    	}
    	
    	String query = " SELECT count(*) FROM Users WHERE userOrganization = " + orgId.longValue() + appendQuery + appendSearchQuery;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }
    
    */
    //=====================END ==============================================
    
    public int updateUsedSMSCount(Long userId, int usedCount) {
		
		return jdbcTemplate.update(
				" UPDATE users SET used_sms_count = (used_sms_count + "+usedCount+") " +
				" WHERE user_id= "+userId.longValue());
	}
    public int updateUsedEmailCount(Long userId, int usedCount) {
		
		return jdbcTemplate.update(
				" UPDATE users SET used_email_count = (used_email_count + "+usedCount+") " +
				" WHERE user_id= "+userId.longValue());
	}

	public int updateOptSynKey(Long userOrgId,String optSyncAuthkey) {
		
		
		String query = "UPDATE UserOrganization SET optSyncKey= '"+optSyncAuthkey+"' WHERE userOrgId ="+userOrgId;
		
		
	 return	executeUpdate(query);
		
	}

	/*public UserOrganization findByOrgId(Long orgId) {
		
		UserOrganization userOrg = null;
    	
    	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE userOrgId ="+orgId); 
    	
    	 if(userOrgList.size()>0){
    		 userOrg = (UserOrganization)userOrgList.get(0);
		 }
      return userOrg; 
		
		
	}
	*/
	 /**
     * This method find's userOrganization details by optSyncAuthenticationKey.
     * @param generatedOptSyncKey
     * @return userOrganization
     */
   /* public UserOrganization findUserOrgByOptSyncAuthKey(String generatedOptSyncKey) {
    	logger.debug(">>>>>>>>>>>>> entered in findUserOrgByOptSyncAuthKey");
    	UserOrganization userOrganization = null;
    	List<UserOrganization> userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE optSyncKey ='"+generatedOptSyncKey+"'"); 

    	if(userOrgList !=null && userOrgList.size()>0){
    		userOrganization = userOrgList.get(0);
    	}
    	else{
    		userOrganization = null;
    	}
    	logger.debug("<<<<<<<<<<<<< completed findUserOrgByOptSyncAuthKey ");
    	return userOrganization;
    }*///findUserOrgByOptSyncAuthKey

  /*  public Long findByCardId(Long cardId){
    	Long userOrgId = null;
    	List<Long> userOrgIdList = null;
    	try{
    		userOrgIdList = getHibernateTemplate().find("SELECT userOrgId FROM UserOrganization WHERE cardId ="+cardId); 
    		if(userOrgIdList != null && userOrgIdList.size()>0){
    			userOrgId = userOrgIdList.get(0);
    		}
    		return userOrgId;
    	} catch (Exception e) {
    		logger.error("exception ...", e);
    		return null;
    	}
    }
    
    public Long findNextSeqNumber(Long orgId){
    	Long userOrgId = null;
    	List<Long> userOrgIdList = null;
    	try{
    		userOrgIdList = getHibernateTemplate().find("SELECT nextCardSeqNo FROM UserOrganization WHERE userOrgId ="+orgId); 
    		if(userOrgIdList != null && userOrgIdList.size()>0){
    			userOrgId = userOrgIdList.get(0);
    		}
    		return userOrgId;
    	} catch (Exception e) {
    		logger.error("exception ...", e);
    		return null;
    	}
    }
    
    public Long getOwnerofOrg(Long orgId){
    	List<Long> minUsers=null;
    	try{
    	minUsers=getHibernateTemplate().find(" SELECT MIN(userId) FROM Users where userOrganization="+orgId);
    	if(minUsers != null && minUsers.size()>0){
			return minUsers.get(0);
		}
    	}catch (Exception e) {
			logger.info(" Exception while getting minimum user_id of organigation :: ",e);
		}
    	return null;
    }
    
    public Users getUserByGenericUnsubUrl(String unsuburl){
    	List<Users> genericUnsubUsersList;
    	try{
    	String query = "FROM Users where unsuburl='"+unsuburl+"'";
    	genericUnsubUsersList = getHibernateTemplate().find(query);
    	if(genericUnsubUsersList != null && genericUnsubUsersList.size()>0){
			return genericUnsubUsersList.get(0);
		}
    	}catch (Exception e) {
    		logger.info(" Exception while getting minimum user_id of organigation :: ",e);
		}
    	return null;
    }

*/}

