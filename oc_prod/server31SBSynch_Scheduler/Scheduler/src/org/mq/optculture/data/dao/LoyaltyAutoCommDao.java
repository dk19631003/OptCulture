package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyAutoComm;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;

public class LoyaltyAutoCommDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	public LoyaltyAutoCommDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyAutoComm loyaltyAutoComm) {
		super.saveOrUpdate(loyaltyAutoComm);
	}*/
	
	public LoyaltyAutoComm findById(Long prgmId) {
		List<LoyaltyAutoComm> list = getHibernateTemplate().find("FROM LoyaltyAutoComm WHERE programId="+ prgmId.longValue() );
    	
    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
	}

	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyAutoComm WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/

}
