package org.mq.captiway.scheduler.dao;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserVmta;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
//import org.mq.captiway.scheduler.utility.Constants;

public class UserVmtaDaoForDML extends AbstractSpringDaoForDML {

//	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

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
