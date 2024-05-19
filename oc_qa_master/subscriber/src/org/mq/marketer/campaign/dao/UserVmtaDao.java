package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.UserVmta;
import org.mq.marketer.campaign.general.Constants;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserVmtaDao extends AbstractSpringDao  {
		
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
		
		public UserVmtaDao() {
		}

		public UserVmta find(Long id) {
			return (UserVmta) super.find(UserVmta.class, id);
		}

		public List<UserVmta> findAll() {
			return super.findAll(UserVmta.class);
		}

		public List<UserVmta> getAllUserVmtas() {
			return super.find("from UserVmta order by id");
		}


}
