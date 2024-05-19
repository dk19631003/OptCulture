package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ValueCodesDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Long findCountByValueCodeByOrgIdAndKey(Long OrgID,String Key) {
    	try {
    		Long listCount = null;
			listCount =((Long) executeQuery("SELECT  COUNT(Id) from ValueCodes where OrgId='"+OrgID+"' and ValuCode like'%"+Key+"%'").get(0)).longValue();
			
			return listCount;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	public Long findNonFbpCountByValueCodeByOrgIdAndKey(Long OrgID,String Key) {
    	try {
    		Long listCount = null;
			listCount =((Long) executeQuery("SELECT  COUNT(Id) from ValueCodes where OrgId='"+OrgID+"'  AND associatedWithFBP=false and ValuCode like'%"+Key+"%'").get(0)).longValue();
			
			return listCount;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	public List<ValueCodes> findByValueCodeByOrgIdandSearchKey(Long OrgID,String Key,int startIndex, int endIndex) {
    	try {
    		List<ValueCodes> list = null;
			list = executeQuery("from ValueCodes where OrgId='"+OrgID+"' and ValuCode like'%"+Key+"%'",startIndex,endIndex);
			
			return list;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	public List<ValueCodes> findByNonFbpValueCodeByOrgIdandSearchKey(Long OrgID,String Key,int startIndex, int endIndex) {
    	try {
    		List<ValueCodes> list = null;
			list = executeQuery("from ValueCodes where OrgId='"+OrgID+"' AND associatedWithFBP=false and ValuCode like'%"+Key+"%'",startIndex,endIndex);
			
			return list;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
	
	//Had to be organization id?
	public List<ValueCodes> findValueCode(Long OrgID,String ValueCode){
		try {
    		List<ValueCodes> list = null;
			list = executeQuery("from ValueCodes where OrgId='"+OrgID+"' and ValuCode ='"+ValueCode+"'");
			return list;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	public int findValueCodeCount(Long OrgID,String valueCode){
		try {
			if(valueCode!=null && !valueCode.isEmpty()) {
				String query ="SELECT COUNT(*) from ValueCodes where OrgId='"+OrgID+"' and ValuCode ='"+valueCode+"' ";
			}
			String query ="SELECT COUNT(*) from ValueCodes where OrgId='"+OrgID+"' ";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return 0;
		}
	}
	public List<ValueCodes> findValueCodeBy(Long orgID){
		List<ValueCodes> list = null;
		list = executeQuery("from ValueCodes where OrgId="+orgID);
		return list;
	}
	
	public long getCountOfFBP(Long orgID){
		
		try {
			String query ="SELECT COUNT(*) FROM ValueCodes WHERE OrgId="+orgID+" AND associatedWithFBP=true";
			List<Long> list = null;
			
			list = executeQuery(query);
			
			return list.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return 0;
		
	}
	public List<ValueCodes> findNonFBPValueCodes(Long orgID){
		List<ValueCodes> list = null;
		list = executeQuery("from ValueCodes where OrgId="+orgID+" AND associatedWithFBP=false");
		return list;
	}

}
