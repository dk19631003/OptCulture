package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;

public class SessionCounter implements HttpSessionListener {
    public static List sessions = new ArrayList();
    public static Map<String, Users> activeUsersMap = new HashMap<String, Users>();
    private List<Users> activeUsers = new ArrayList<Users>();
    
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
    

    public SessionCounter() {
    }

    public void sessionCreated(HttpSessionEvent event) {
    	
    	
        HttpSession session = event.getSession();
        sessions.add(session.getId());
        
       /* Object principal = SecurityUtil.getAuthentication().getPrincipal();
		logger.info("Got Principal object");
		String userName = null;
		
		if (principal instanceof UserDetails) { 
		    userName = ((UserDetails) principal).getUsername(); 
		} else { 
		    userName = principal.toString(); 
		}
        
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		
		Users user = usersDao.findByUsername(userName);
		activeUsers.add(user);*/
         /**
         * Object principal = SecurityUtil.getAuthentication().getPrincipal();
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
         * 
         * 
         */
        session.setAttribute("counter", this);
        logger.info("session created ========>"+sessions.size()+" the desired session id is===>"+(String)sessions.get(0));
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.remove(session.getId());
        activeUsersMap.remove(session.getId());
        session.setAttribute("counter", this);
    }

    public int getActiveSessionNumber() {
        return sessions.size();
    }
    
    public List<Users> getActiveUsers() {
    	return activeUsers;
    	
    	
    }
}
