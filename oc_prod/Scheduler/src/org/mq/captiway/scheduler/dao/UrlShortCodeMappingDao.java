package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactScores;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class UrlShortCodeMappingDao extends AbstractSpringDao {
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public UrlShortCodeMappingDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public UrlShortCodeMapping find(Long id) {
        return (UrlShortCodeMapping) super.find(UrlShortCodeMapping.class, id);
    }

   /* public void saveOrUpdate(UrlShortCodeMapping UrlShortCodeMapping) {
        super.saveOrUpdate(UrlShortCodeMapping);
    }

    public void delete(UrlShortCodeMapping UrlShortCodeMapping) {
        super.delete(UrlShortCodeMapping);
    }*/
	
    
    public UrlShortCodeMapping findlongUrlByShortCode(String shortCode) {
    	try {
    		
    		
    		String query = "FROM UrlShortCodeMapping WHERE  shortCode='" + shortCode + "'";
        	List<UrlShortCodeMapping> list = executeQuery(query);
        	if(list == null || list.size() <1) {
        		
        		return null;
        	}
        	return list.get(0);
    		
    		
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    
    public List<UrlShortCodeMapping> findBy(Long userId, String shortCodeStr) {
    	
    	List<UrlShortCodeMapping> list = executeQuery("FROM UrlShortCodeMapping WHERE userId="+userId.longValue()+" AND  shortCode IN(" + shortCodeStr + ")" );
    	if(list == null || list.size() <= 0) {
    		return null;
    	}
    	return list;
		
		
    	
    	
    }
    
}
