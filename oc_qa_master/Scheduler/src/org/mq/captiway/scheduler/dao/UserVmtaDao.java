package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserVmta;
import org.mq.captiway.scheduler.utility.Constants;

@SuppressWarnings("unchecked")
public class UserVmtaDao extends AbstractSpringDao  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public UserVmtaDao() {
	}

	public UserVmta find(Long id) {
		return (UserVmta) super.find(UserVmta.class, id);
	}

	public List<UserVmta> findAll() {
		return super.findAll(UserVmta.class);
	}
	
	public UserVmta findById(Long id) {
		UserVmta userVmta = null;
		try {
		userVmta =(UserVmta) getHibernateTemplate().find("from UserVmta where userId ="+id+" and enabled = 1");
	
		return userVmta;
		}
		catch(Exception e) {
			logger.error("** Exception : "+e);
			return null;
		}
	}
	
	public List<UserVmta> getAllUserVmtas() {
		return super.find("from UserVmta order by id");
	}
}
