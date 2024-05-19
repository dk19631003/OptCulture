package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.http.HttpSessionListener;

public class ActiveUsers extends HttpSessionListener{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	
	public static List sessions = new ArrayList();
    public static Map<String, Users> activeUsersMap = new HashMap<String, Users>();
    private List<Users> activeUsers = new ArrayList<Users>();

	 
	@Override
	public void sessionCreated(HttpSessionEvent evt) {
		super.sessionCreated(evt);
		
		HttpSession session = evt.getSession();
		sessions.add(session.getId());
		
		logger.info("session Id is  :: "+session.getId());
		
		logger.info("the logged in users are====>"+sessions.size());
		
	}

	public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        logger.info("session Id is  :: "+session.getId());
        sessions.remove(session.getId());
        activeUsersMap.remove(session.getId());
        session.setAttribute("counter", this);
    }
	
	
	
	
}
