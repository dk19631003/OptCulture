package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CharacterCodes;
import org.mq.captiway.scheduler.utility.Constants;


public class CharacterCodesDaoForDML extends AbstractSpringDaoForDML{
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
	public CharacterCodesDaoForDML() {}
	
	
	public void saveOrUpdate(CharacterCodes characterCodes) {
        super.saveOrUpdate(characterCodes);
    }
	
/*	public List<CharacterCodes> findAll() throws Exception{
		
		String query = "FROM CharacterCodes";
		
		return executeQuery(query);
		
    }*/
}
