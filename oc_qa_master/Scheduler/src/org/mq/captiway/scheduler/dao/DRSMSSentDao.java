package org.mq.captiway.scheduler.dao;


import java.util.List;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.DRSMSSent;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSMSSentDao extends AbstractSpringDao {

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public DRSMSSent find(Long id) {
		return (DRSMSSent) super.find(DRSMSSent.class, id);
	}
	
	/*public DRSMSSent findByShortCode(String shortCode) throws BaseServiceException{
		try {
			String qry = "FROM DRSMSSent WHERE generatedShortCode='"+shortCode+"'";
			List<DRSMSSent> retList = executeQuery(qry);
				if(retList != null && retList.size() > 0) return retList.get(0);
				return null;
		} catch (Exception e) {
			throw new BaseServiceException("Exception while getting the required object by code "+shortCode);
		}
		
	}*/
	 public DRSMSSent findlongUrlByShortCode(String shortCode) {
	    	try {
	    		
	    		
	    		String query = "FROM DRSMSSent WHERE  originalShortCode='" + shortCode + "' ORDER BY id DESC";
	        	List<DRSMSSent> list = executeQuery(query, 0,1);
	        	if(list == null || list.size() <1) {
	        		
	        		return null;
	        	}
	        	return list.get(0);
	    		
	    		
				
			} catch (Exception e) {
				return null;
			}
	    }
}
