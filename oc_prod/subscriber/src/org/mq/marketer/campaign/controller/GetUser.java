package org.mq.marketer.campaign.controller;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.core.userdetails.UserDetails;
import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkplus.spring.SpringUtil;

public class GetUser {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	//private Session session;

	public static void getInfoFromRequest() {
		String userName = getUserInfo();
		checkUserFolders(userName);
	}
		
	
	public static void getUserAuthentication() {
		
		Set<String> userRoleSet = new HashSet<String>();
		Collection userRoles = SecurityUtil.getAuthentication().getAuthorities();
		Iterator it = userRoles.iterator();
		while(it.hasNext()) {
			
			userRoleSet.add(it.next().toString());
			
		}
		Sessions.getCurrent().setAttribute("userRoleSet", userRoleSet);
			
		
	}
	
	public static String getUserInfo() {
		String userName = null;
        try {
        	logger.debug("-- Just entered --");
//			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        	Object principal = SecurityUtil.getAuthentication().getPrincipal();
			logger.debug("Got Principal object");
			
			if (principal instanceof UserDetails) { 
			    userName = ((UserDetails) principal).getUsername(); 
			} else { 
			    userName = principal.toString(); 
			}
			logger.debug("Got user Name :" + userName);
			if (userName == null) {
			    logger.error("getUser: User is null, PROBLEM, THROW EXCEPTION");    
			    return null;
			}
			
			Session session = Sessions.getCurrent();
			session.setAttribute("userName", userName);
			
			if(session.getAttribute("userRoleSet") == null) {
				getUserAuthentication();
			}
			
			setUserInfo();
		} catch (Exception e) {
			logger.error("** Exception : ", e);
		}
		logger.debug("-- Exit --");
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/*if(userActivitiesDao != null) {
    		userActivitiesDao.addToActivityList(ActivityEnum.USR_LOGGED_IN, getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
    		userActivitiesDaoForDML.addToActivityList(ActivityEnum.USR_LOGGED_IN, GetUser.getLoginUserObj());
		}
		return userName;
	}

	public static void setUserInfo(){
		try {
			logger.debug("-- Just entered --");
			String userName = null;
			Session session = Sessions.getCurrent();
			userName = (String)session.getAttribute("userName");
			logger.debug("userName :" + userName);
			if(userName == null){
				
				logger.debug("got user name as null , returning....");
				return;
			}
			
			Users users = null;
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
				logger.debug("Got usersDao object :" + usersDao);
			try{
				logger.debug("finding user name "+userName);
				users = usersDao.findByUsername(userName);
				logger.debug("Got user object: "+users.toString());
				if(users!=null) {
					logger.debug("session is exist :: "+session);
					session.setAttribute("userObj", users);
					//setUsersSet(null);
					setListIdsSet(null);
					setSegmentIdsSet(null);
					
				}
				
			}catch(Exception e){
				logger.error("** Exception ", e);
			}
			
			logger.debug("-- Exit --");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception while setting the user info....",e);
		}
	}
	
	public static void setLoginUserInfo(){
		try {
			logger.debug("-- Just entered --");
			String userName = null;
			Session session = Sessions.getCurrent();
			userName = (String)session.getAttribute("userName");
			logger.debug("userName :" + userName);
			if(userName == null){
				
				logger.debug("got user name as null , returning....");
				return;
			}
			
			Users users = null;
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
				logger.debug("Got usersDao object :" + usersDao);
			try{
				logger.debug("finding user name "+userName);
				users = usersDao.findByUsername(userName);
				logger.debug("Got user object: "+users.toString());
				if(users!=null) {
					logger.debug("session is exist :: "+session);
					session.setAttribute("LoginUser", users);
					
					//setUsersSet(null);
					setListIdsSet(null);
					setSegmentIdsSet(null);
					
				}
				
			}catch(Exception e){
				logger.error("** Exception ", e);
			}
			
			logger.debug("-- Exit --");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception while setting the user info....",e);
		}
	}
	
	/*public static String getUser(){
		return getUserName();
	}
	*/
	
	public static String getUserName(){
		String userName = null;
		try{
			userName = (String)Sessions.getCurrent().getAttribute("userName");
			if (userName == null) {
				userName = getUserInfo();
				if(userName == null)
					logger.error("** User is null **");      
			}
		}catch(Exception e){
			logger.error("** Exception : ", e);
		}
		return userName;
	}

	public static String getOnlyUserName() {
		String userName = getUserName();
		if (userName == null ) return null;
		
		try{
			userName = userName.substring(0, userName.lastIndexOf("__org__"));
		}
		catch(Exception e) {
			logger.error("** Exception : ", e);
		}
		return userName;
	}

	public static Users getUserObj() {
		/*Session session = Sessions.getCurrent();
		Users users = (Users)session.getAttribute("userObj");
		if(users==null){
			setUserInfo();
			users = (Users)session.getAttribute("userObj");
		}
		return users;*/
		return getUserObj(false);
	}
	public static Users getLoginUserObj() {
		/*Session session = Sessions.getCurrent();
		Users users = (Users)session.getAttribute("userObj");
		if(users==null){
			setUserInfo();
			users = (Users)session.getAttribute("userObj");
		}
		return users;*/
		return getLoginUserObj(false);
	}
	public static Users getLoginUserObj(boolean setLastLoginFlag) {
		Session session = Sessions.getCurrent();
		Users users = (Users)session.getAttribute("LoginUser");
		//String isStandardUser = users.getAccountType();
		if(users==null){
			setUserInfo();
			users = (Users)session.getAttribute("userObj");
		}
		session.setAttribute("LoginUser", users);
		if(setLastLoginFlag){
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
			users.setLastLoggedInTime(Calendar.getInstance());
			//usersDao.saveOrUpdate(users);
			usersDaoForDML.saveOrUpdate(users);
			session.setAttribute("LoginUser", users);
		}
		//boolean isStandardUser;
		//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
	//	Long domanID = usersDao.findDomainByUserId(users.getUserId());
		//isStandardUser	= usersDao.isStandardUser(users.getUserId(),domanID);
		//if(isStandardUser(users)) { //this method must check that the users is standuser or not
		/*
		 * if(isStandardUser) { //this method must check that the users is standuser or
		 * not
		 * 
		 * //find the owner id //UsersDao usersDao = (UsersDao)
		 * ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); Long
		 * orgOwnerId =
		 * usersDao.getOwnerofOrg(users.getUserOrganization().getUserOrgId()); Users
		 * orgOwner = usersDao.findByUserId(orgOwnerId); if(orgOwner.getUserId() !=
		 * users.getUserId()){ users = orgOwner;//?for APP - 1015
		 * session.setAttribute("userObj", users); } }
		 */
		return users;
	}
	
	public static Users getUserObj(boolean setLastLoginFlag) {
		Session session = Sessions.getCurrent();
		Users users = (Users)session.getAttribute("userObj");
		//String isStandardUser = users.getAccountType();
		if(users==null){
			setUserInfo();
			users = (Users)session.getAttribute("userObj");
		}
		session.setAttribute("LoginUser", users);
		if(setLastLoginFlag){
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
			users.setLastLoggedInTime(Calendar.getInstance());
			//usersDao.saveOrUpdate(users);
			usersDaoForDML.saveOrUpdate(users);
			//session.setAttribute("userObj", users);
		}
		
		logger.info("userId====="+users.getUserId());
		boolean isStandardUser;
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		Long domanID = usersDao.findDomainByUserId(users.getUserId());
		isStandardUser	= usersDao.isStandardUser(users.getUserId(),domanID);
		//if(isStandardUser(users)) { //this method must check that the users is standuser or not
		if(isStandardUser) { //this method must check that the users is standuser or not	
			
			//find the owner id
			//UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long orgOwnerId = usersDao.getOwnerofOrg(users.getUserOrganization().getUserOrgId());
			Users orgOwner = usersDao.findByUserId(orgOwnerId);
			logger.info("OwnerId"+orgOwner);
			if(orgOwner.getUserId() != users.getUserId()){
				users = orgOwner;//?for APP - 1015
				//session.setAttribute("userObj", users);
			}
		}
		return users;
	}
	
	/*public static void setUsersSet(String filterOptionStr, Users user) {
		logger.debug("-- Just entered --");
		String userName = null;
		Session session = Sessions.getCurrent();
		userName = (String)session.getAttribute("userName");
		logger.debug("userName :" + userName);
		if(userName == null) return;
		
		Users users = null;
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			logger.debug("Got usersDao object :" + usersDao);
			
		try{
			users = usersDao.findByUsername(userName);
			if(users != null) {
				Set<Long> userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);
				
				if(userIdsSet ==null) {
					userIdsSet = new HashSet<Long>();
				}
				userIdsSet.clear();
				
				if(filterOptionStr!=null && filterOptionStr.equals("all") && user == null) { // For All users
					List<Users> domainUsers = usersDao.getUsersByDomain(users.getUserDomain().getDomainId());
					
					for (Users eachUsers : domainUsers) {
						userIdsSet.add(eachUsers.getUserId());
					} // for
				}
				else if(user != null){ // For only Selected User
					userIdsSet.add(user.getUserId());
				}
				
				session.setAttribute(Constants.USERIDS_SET, userIdsSet);
			} // if
			
		}catch(Exception e){
			logger.error("** Exception ", e);
		}
		
		logger.debug("-- Exit --");
	}*/

	public static void setListIdsSet(List<Long> mlList) {
		Set<Long> listIdsSet = (Set<Long>)Sessions.getCurrent().getAttribute(Constants.LISTIDS_SET);
		
		if(listIdsSet == null) {
			listIdsSet = new HashSet<Long>();
		}
		listIdsSet.clear();
		if(mlList != null && mlList.size() > 0) {
			for(Long listId: mlList ) {
				listIdsSet.add(listId);
			}
		}
		else {
			//listIdsSet.add(getUserId());
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			
			listIdsSet.addAll(mailingListDao.findAllIdsByUser(getUserObj().getUserId()) );
			//segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
				
		}
		 
		logger.debug("listIdsSet==="+listIdsSet.size());
		Sessions.getCurrent().setAttribute(Constants.LISTIDS_SET, listIdsSet);//what happens if basic user?????
		
		
		
	}
	public static void setSegmentIdsSet(List<Long> segList) {
		
		Set<Long> segmentIdsSet = (Set<Long>)Sessions.getCurrent().getAttribute(Constants.SEGMENTIDS_SET);
		
		if(segmentIdsSet == null) {
			segmentIdsSet = new HashSet<Long>();
		}
		segmentIdsSet.clear();
		if(segList != null && segList.size() > 0) {
			for(Long segId: segList ) {
				segmentIdsSet.add(segId);
			}
		}
		else {
			//listIdsSet.add(getUserId());
			
			SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
			segmentIdsSet.addAll(segmentRulesDao.findAllIdsByCurrentUser(getUserObj().getUserId()) );
				
		}
		 
		logger.debug("segmentIdsSet==="+segmentIdsSet.size());
		Sessions.getCurrent().setAttribute(Constants.SEGMENTIDS_SET, segmentIdsSet);//what happens if basic user?????
		
		
		
	}
	
	/*public static void setUsersSet(List<Long> userList) {
		
		try {
			logger.debug(">>>>>>>>>>>>>>>>> setUsersSet "+userList);
			
			Session session = Sessions.getCurrent();
			
			Set<Long> userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);
			
			if(userIdsSet ==null) {
				userIdsSet = new HashSet<Long>();
			}
			userIdsSet.clear();
			
			if(userList != null && userList.size() > 0) {
				for(Long eachUserId: userList ) {
					userIdsSet.add(eachUserId);
				}
			}
			
			else {
				userIdsSet.add(getUserId());
				UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
				List<Users> childShUser = usersDao.getSharedUsersByParentUserId(getUserId());
				logger.debug("childShUser==="+childShUser);
				for (Users eachShUser : childShUser) {
					userIdsSet.add(eachShUser.getUserId());
				} // for 
			}
			 
			logger.debug("userIdsSet.Size()==="+userIdsSet.size());
			session.setAttribute(Constants.USERIDS_SET, userIdsSet);
			
			
		} catch (Exception e) {
			logger.error("** Exception ", e);
		}
		
	}*/
	
	
	

	/*public static Set<Long> getUsersSet() {
		Session session = Sessions.getCurrent();
		Set<Long> userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);
		
		if(userIdsSet==null) {
			setUsersSet(null);
			userIdsSet = (Set<Long>)session.getAttribute(Constants.USERIDS_SET);
		}
		return userIdsSet;
	}*/
	
	
	public static Long getUserId(){
		Long userId= null;
		try{
			Users user = (Users)Sessions.getCurrent().getAttribute("userObj");
			userId = null;
			if (user == null) {
				setUserInfo();
				user = (Users)Sessions.getCurrent().getAttribute("userObj");
				if(user == null) {
					logger.error("** UserId is null **");
					return null;
				}
			} 
			
			boolean isStandardUser;
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			Long domanID = usersDao.findDomainByUserId(user.getUserId());
			isStandardUser	= usersDao.isStandardUser(user.getUserId(),domanID);
			//if(isStandardUser(users)) { //this method must check that the users is standuser or not
			if(isStandardUser) { //this method must check that the users is standuser or not	
				
				//find the owner id
				//UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Long orgOwnerId = usersDao.getOwnerofOrg(user.getUserOrganization().getUserOrgId());
				Users orgOwner = usersDao.findByUserId(orgOwnerId);
				logger.info("OwnerId"+orgOwner);
				if(orgOwner.getUserId() != user.getUserId()){
					user = orgOwner;//?for APP - 1015
					//session.setAttribute("userObj", users);
				}
			} 
			userId = user.getUserId();
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
		return userId;
	}
	
	public static void checkUserFolders(String userName){
		if(userName == null){
			logger.info("Invalid User: userName is null ");
			return;
		}
		try {
			String usersParentDirectory =  PropertyUtil.getPropertyValue("usersParentDirectory");
			File userDataDir =  new File(usersParentDirectory);
			if(!userDataDir.exists()){
				userDataDir.mkdir();
				userDataDir.setWritable(true);
			}
			String userDirectories = PropertyUtil.getPropertyValue("userDirectories");
			String[] dirs = userDirectories.split(",");
			
			String userDataPath = usersParentDirectory + "/" + userName;
			File dir = new File(userDataPath);
			if(dir.exists()){
				for (String dirName : dirs) {
					File sub = new File(userDataPath + File.separator + dirName);
					if(!sub.exists() ){	sub.mkdirs(); sub.setWritable(true);}
				}
/*				File sub = new File(userDataPath + "/Email");
				if(!sub.exists() ){	sub.mkdir(); sub.setWritable(true);}
				sub = new File(userDataPath + "/Gallery");
				if(!sub.exists() ){	sub.mkdir(); sub.setWritable(true);}
				sub = new File(userDataPath + "/List");
				if(!sub.exists() ){	sub.mkdir(); sub.setWritable(true);}
				sub = new File(userDataPath + "/Coupon");
				if(!sub.exists() ){	sub.mkdir(); sub.setWritable(true);}*/
			}else{
				dir.mkdirs(); dir.setWritable(true);
				for (String dirName : dirs) {
					File sub = new File(userDataPath + File.separator + dirName);
					sub.mkdirs(); sub.setWritable(true);
				}
				
				//crete User folder has given full permission 
				String permissionStr = "chmod -R 777 "+userDataPath;
				logger.info(">>>permissionStr ::"+permissionStr);
				Runtime rt = Runtime.getRuntime();
				
				Process proc = rt.exec(permissionStr);
				int sucess = proc.waitFor();
				if(sucess == 0) {
					logger.info(" >>>>>>> permisson has given ");
				}else {
					logger.info(" >>>>>>>problem occur with permissions  ");
				}
				
				
				/*File sub = new File(userDataPath + "/Email");
				sub.mkdir(); sub.setWritable(true);
				sub = new File(userDataPath + "/Gallery");
				sub.mkdir(); sub.setWritable(true);
				sub = new File(userDataPath + "/List");
				sub.mkdir(); sub.setWritable(true);
				sub = new File(userDataPath + "/Coupon");
				sub.mkdir(); sub.setWritable(true);*/
			}
		} catch (Exception e) {
			logger.error("** Exception : " + e + " **");
		}
	}

}
