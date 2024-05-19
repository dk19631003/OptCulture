package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.POSMapping;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


public class POSMappingDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public POSMappingDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public POSMapping find(Long id) {
        return (POSMapping) super.find(POSMapping.class, id);
    }

    /*public void saveOrUpdate(POSMapping posCustField) {
        super.saveOrUpdate(posCustField);
    }*/
    
   /* public void delete(POSMapping posCustField) {
        super.delete(posCustField);
    }*/

    public List<POSMapping> findPOSMappingListByStr(String mappingTypeStr , long userId) {
    	try {
			List<POSMapping> posList = null;
			posList = getHibernateTemplate().find("FROM POSMapping where  mappingType='"+mappingTypeStr+"' and userId = "+userId);
			
			if(posList.size() > 0) return posList;
			else return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    // added for digital receipt extarction
    
    
 public List<POSMapping> findByType(String mappingType, Long userId) {
    	
    	try {
			return getHibernateTemplate().find("FROM POSMapping Where userId="+userId+ " AND mappingType in("+mappingType+")");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
    	
    	
    }
 
 
 public List<POSMapping> getPriorityMapByUserId(Long userId, String mappingType) {
 	
 	try {
 		
 		logger.info("userId is ::" +userId);
			List<POSMapping> list = getHibernateTemplate().find("from POSMapping where userId = " + userId +" and  " +
																"mappingType  in ("+mappingType+") and uniquePriority is not null ");
			if(list.size() >0) return list;
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
 	
 } // getPriorityMapByUserId
 
    
}
