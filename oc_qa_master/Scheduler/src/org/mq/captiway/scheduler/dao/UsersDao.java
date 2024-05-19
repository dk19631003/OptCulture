package org.mq.captiway.scheduler.dao;
 
import java.util.List;

import org.apache.catalina.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.UserVmta;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class UsersDao extends AbstractSpringDao{


	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    public UsersDao(){}

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public Users find(Long id){
        return (Users) super.find(Users.class, id);
    }

   /* public void saveOrUpdate(Users email){
        super.saveOrUpdate(email);
    }*/

/*    public void delete(Users email){
        super.delete(email);
    }

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
*/
    public List findAll(){
        return super.findAll(Users.class);
    }

    public Users findByUsername (String name){
    	Users users = null;
        List usersList = getHibernateTemplate().find("from Users where userName = '"+  name + "' ");
        if(usersList.size()>0){
				users = (Users)usersList.get(0);
		 }
        return users; 
    }

	public String getUserNameById(Long id){
		List list = getHibernateTemplate().find("select userName from Users where userId = "+  id );
		if(list.size()>0)
			return (String)list.get(0);
		else return "";
	}
	
	
	/*
	public int updateUsedEmailCount(Long userId, int usedCount) {
		
		return jdbcTemplate.update(
				" UPDATE users SET used_email_count = (used_email_count + "+usedCount+") " +
				" WHERE user_id= "+userId.longValue());
		
	}
	*/
	public List<Integer> getAvailableCountOfUser(Long userId) {
		return getHibernateTemplate().find(
				" SELECT (emailCount-usedEmailCount) FROM Users " +
				" WHERE userId = "+userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @param usedCount
	 * @return
	 */
	
	/*public int updateUsedSMSCount(Long userId, int usedCount) {
		
		return jdbcTemplate.update(
				" UPDATE users SET used_sms_count = (used_sms_count + "+usedCount+") " +
				" WHERE user_id= "+userId.longValue());
	}*/
	
	/**
	 * this method counts the total available SMS count for the user
	 * @param userId
	 */
	
	public List<Integer> getAvailableSMSCountOfUser(Long userId) {
		return getHibernateTemplate().find(
				" SELECT (smsCount-ifnull(usedSmsCount,0)) FROM Users " +
				" WHERE userId = "+userId);
	}
	
	
	
	public List<Users> getAboutToExpireUsers(int daysLimit, int usageLimit) {
		return getHibernateTemplate().find("FROM Users " +
				" WHERE (packageExpiryDate - NOW()) <= "+ daysLimit );
	}
	
	public List findAllAdmins() {
		 
		try {
				/*String countQry = "SELECT user_id FROM authorities, users " +
						" where users.username=authorities.username " +
						" and authority='ROLE_ADMIN' ";*/
			
			
			 String countQry = "SELECT u.user_id FROM  users u, users_roles ur, sec_roles sr  " +
				" where u.user_id=ur.user_id AND ur.role_id = sr.role_id AND sr.type in('OC Admin')" ;
				

				
				
				return jdbcTemplate.queryForList(countQry);
				
			} catch (DataAccessException e) {
				logger.error(" Exception : ",(Throwable)e);
				return null;
			}
			
	}
	
	public List<Users>  findUsersByAccType(String accType) {
		
		String qry = "FROM Users WHERE accountType='"+accType+"'";
		
		return getHibernateTemplate().find(qry);
		
	}
	
	
	
public List<UsersDomains> getAllDomainsByUser(Long userId) {
		
		String Query = " SELECT  DISTINCT ud FROM Users u JOIN u.userDomains ud WHERE u.userId IN("+userId.longValue()+")";
		
		 List<UsersDomains> domainList = getHibernateTemplate().find(Query);
		 
		 if(domainList != null && domainList.size() > 0) return domainList;
		 else return null;
		
		
		
	}

// added for digital receipt extraction

public List<Users> findByDigiReceptExtraction(boolean drflag){
	
	
	try {
		//String query = "FROM Users WHERE digitalReceiptExtraction = "+drflag;
		//String query = "FROM Users WHERE enabled=true AND digitalReceiptExtraction = "+drflag;
		//String query = "FROM Users WHERE enabled=true AND (digitalReceiptExtraction = "+drflag+" OR enableLoyaltyExtraction = "+drflag+")";
		String query = "FROM Users WHERE enabled=true AND (digitalReceiptExtraction = "+drflag+" OR enableLoyaltyExtraction = "+drflag+" OR enablePromoRedemption = "+drflag+")";
		List<Users> list = (List<Users>) getHibernateTemplate().find(query);
		if(list == null || list.size() == 0) return null;
		else return list;
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::::" , e);
		return null;
	}
	
}
// Added for spark base settings 

public Users findByToken(String userName,String token) {
	
	List<Users> list = getHibernateTemplate().find("FROM Users WHERE userName='"+ userName +"' AND  token='"+ token+"'");
	
	if(list != null && list.size() == 1) {
		return list.get(0);
	}
	return null;
}

	public Users findByUsernameAndPassword(String name, String password) {
		
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
	
	public List<Users> getPrimaryUsersByOrg(Long userOrgId) {
		
		return getHibernateTemplate().find("FROM Users WHERE userOrganization="+userOrgId+" AND parentUser  IS NULL ORDER BY userName ");
		
	}
	
	 public Users findByUserId(Long userId) {
		 
	    	String qry ="FROM Users WHERE userId="+userId.longValue();
	    	
	    	List<Users> list = (List<Users>)getHibernateTemplate().find(qry);
			
			if(list != null && list.size() > 0) {
				
				
				return list.get(0);
			}
			
			return null;
	    }
	
	 public List<Users> getAllUsers(){
		 
		 String qry ="FROM Users ";
		 List<Users> list = (List<Users>)getHibernateTemplate().find(qry);
		 return list;
	 }

	 public UserOrganization findByOrgId(Long orgId) {
			
			UserOrganization userOrg = null;
	    	
	    	List userOrgList = getHibernateTemplate().find("FROM UserOrganization WHERE userOrgId ="+orgId); 
	    	
	    	 if(userOrgList.size()>0){
	    		 userOrg = (UserOrganization)userOrgList.get(0);
			 }
	      return userOrg; 
			
			
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
	 
	 public List<Users> findAnySpeciDirBasedUsersExists () throws Exception{
		 String qry = "FROM Users WHERE specificDir=true";
		 return executeQuery(qry);
	 }
	 
	 public List<Users> findActiveUsers() throws Exception{
		 String qry = "FROM Users WHERE enabled=1 AND DATE(packageExpiryDate) >= DATE(NOW())";
		 return executeQuery(qry);
	 }
	 
	 //findby(userid, typeassingle); from uservamta where user_id= and enabled=1 and type='Single';
	 //added for specific vmta selected.
	 public UserVmta findBy(Long userId, String typeAsSingle) {
		 UserVmta userVmtaObj = null;
		 String qry="FROM UserVmta where userId = "+userId+" and enabled = true and emailType ='"+typeAsSingle+"'";
		 try {
			 List userVmta = getHibernateTemplate().find(qry);
		 if(userVmta != null)
			 return (UserVmta) userVmta.get(0);
		 }
		 catch(Exception e) {
			 logger.error("**Exception caused : "+e);
		 }
		 return null;
	 }
}
