package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyAutoCommDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	public LoyaltyAutoCommDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyAutoComm loyaltyAutoComm) {
		super.saveOrUpdate(loyaltyAutoComm);
	}
	/*
	public LoyaltyAutoComm findById(Long prgmId) {
		List<LoyaltyAutoComm> list = getHibernateTemplate().find("FROM LoyaltyAutoComm WHERE programId="+ prgmId.longValue() );
    	
    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
	}*/

	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyAutoComm WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
/*
	public List<LoyaltyAutoComm> findByUserId(Long userId) {
		List<LoyaltyAutoComm> list = getHibernateTemplate().find("FROM LoyaltyAutoComm WHERE createdBy="+ userId);
    	
    	if(list != null && list.size() == 1) {
    		return list;
    	}
    	return null;
	}
	
	public List<LoyaltyAutoComm> multipleLoyaltyAutoCommfindByUserId(Long userId) {
		List<LoyaltyAutoComm> list = getHibernateTemplate().find("FROM LoyaltyAutoComm WHERE createdBy="+ userId);
    	
    	if(list != null && list.size() > 0) {
    		return list;
    	}
    	return null;
	}
*/
}
