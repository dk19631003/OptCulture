package org.mq.captiway.scheduler.dao;


import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.DRSMSChannelSent;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSMSChannelSentDao extends AbstractSpringDao {

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public DRSMSChannelSent find(Long id) {
		return (DRSMSChannelSent) super.find(DRSMSChannelSent.class, id);
	}
	
	 public DRSMSChannelSent findlongUrlByShortCode(String shortCode) {
	    	try {
	    		
	    		String query = "FROM DRSMSChannelSent WHERE  originalShortCode='" + shortCode + "' ORDER BY id DESC";
	        	List<DRSMSChannelSent> list = executeQuery(query);
	        	if(list == null || list.size() <1) {
	        		
	        		return null;
	        	}
	        	return list.get(0);
	    		
	    		
				
			} catch (Exception e) {
				return null;
			}
	    }
}
