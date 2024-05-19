package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.SwitchCondition;
import org.springframework.dao.DataAccessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;

public class SwitchConditionDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public List<SwitchCondition> findByComponentId(Long compId) {
	
		try {
			return 	getHibernateTemplate().find("FROM SwitchCondition WHERE componentId="+compId);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		
	}
	
	
	
}
