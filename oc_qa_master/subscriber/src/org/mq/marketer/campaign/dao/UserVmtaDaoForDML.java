package org.mq.marketer.campaign.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserVmta;
import org.mq.marketer.campaign.beans.Vmta;
import org.mq.marketer.campaign.general.Constants;

	
public class UserVmtaDaoForDML extends AbstractSpringDaoForDML {

		
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

		public UserVmtaDaoForDML() {
		}

		/*public Vmta find(Long id) {
			return (Vmta) super.find(Vmta.class, id);
		}*/

		public void saveOrUpdate(UserVmta userVmta) {
			super.saveOrUpdate(userVmta);
		}

		public void delete(UserVmta userVmta) {
			super.delete(userVmta);
		}


}
