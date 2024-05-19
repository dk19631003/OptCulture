package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SwitchCondition;
import org.mq.marketer.campaign.general.Constants;

public class SwitchConditionDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	
	public SwitchConditionDaoForDML() {}
	
	public void saveOrUpdate(SwitchCondition switchCondition) {
		try {
			logger.info("------saving into table------");
			super.saveOrUpdate(switchCondition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
    }
/*	
	
	public List<SwitchCondition> findByComponentId(Long compId, Long progId) {
		
		String qry = "FROM SwitchCondition WHERE componentId="+compId+" AND programId="+progId;
		
		return getHibernateTemplate().find(qry); 
		
		
		
	}*/
	
	
	
}
