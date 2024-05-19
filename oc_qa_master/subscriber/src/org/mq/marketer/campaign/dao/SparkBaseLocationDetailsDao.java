package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SparkBaseLocationDetailsDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

    public SparkBaseLocationDetailsDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public SparkBaseLocationDetails find(Long id) {
        return (SparkBaseLocationDetails) super.find(SparkBaseLocationDetails.class, id);
    }

   /* public void saveOrUpdate(SparkBaseLocationDetails sparkBaseLocationDetails) {
        super.saveOrUpdate(sparkBaseLocationDetails);
    }
    
    public void delete(SparkBaseLocationDetails sparkBaseLocationDetails) {
    	logger.info("delete the objet");
        super.delete(sparkBaseLocationDetails);
    }*/
    
    public List<SparkBaseLocationDetails> findAll() {
    	// TODO Auto-generated method stub
    	return super.findAll(SparkBaseLocationDetails.class);
    }
    
    public List<SparkBaseLocationDetails> findAllActiveLoc() {
		 try {
				String queryStr = " FROM SparkBaseLocationDetails WHERE enabled="+ true;
				return getHibernateTemplate().find(queryStr);
			 } catch (DataAccessException e) {
				 logger.error(" Exception : ",(Throwable)e);
				 return null;
			 } //catch
	 }
    
    public List<SparkBaseLocationDetails> findAllActiveLocByTimeDiff() {
		 try {
				String queryStr = " FROM SparkBaseLocationDetails A WHERE A.enabled = true AND " +
						" NOW() > TIMESTAMPADD (MINUTE, A.fetchFreqInMin,  A.lastFetchedTime) ";
				
				return getHibernateTemplate().find(queryStr);
			 } catch (Exception e) {
				 logger.error(" Exception : ",(Throwable)e);
				 return null;
			 } //catch
	 }
    
    public List findAllLocOrg() {
		 try {
				String queryStr = "SELECT DISTINCT userOrganization FROM SparkBaseLocationDetails";
				return getHibernateTemplate().find(queryStr);
			 } catch (DataAccessException e) {
				 logger.error(" Exception : ",(Throwable)e);
				 return null;
			 } //catch
	 }
    
    
    public List<SparkBaseLocationDetails> findAllByOrganizationId(long organizationId) {
    	try {
    		logger.info("organizationId is :"+organizationId);
    		List<SparkBaseLocationDetails> list = null;
			list = getHibernateTemplate().find("from SparkBaseLocationDetails where userOrganization = " + organizationId);
			
			if(list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    public List<SparkBaseLocationDetails> findActiveSBLocByOrgId(long organizationId) {
    	try {
    		logger.info("organizationId is :"+organizationId);
    		List<SparkBaseLocationDetails> list = null;
			list = getHibernateTemplate().find("FROM SparkBaseLocationDetails WHERE userOrganization = " + organizationId +" AND enabled = true ");
			
			if(list != null && list.size() >0) return list;
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    
    public SparkBaseLocationDetails findBylocationId(long organizationId, String locationId) {
    	try {
    		
    		List<SparkBaseLocationDetails> list = null;
    		logger.info("from SparkBaseLocationDetails where userOrganization = " + 
					organizationId +" AND locationId='"+locationId+"' ");
			list = getHibernateTemplate().find("from SparkBaseLocationDetails where userOrganization = " + 
					organizationId +" AND locationId='"+locationId+"' ");
			
			if(list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    public SparkBaseLocationDetails findByOrgId(long orgId) {
    	try {
    		logger.info("organizationId is :"+orgId);
    		List<SparkBaseLocationDetails> list = null;
			list = getHibernateTemplate().find("from SparkBaseLocationDetails where userOrganization = " + orgId);
			
			if(list != null && list.size() >0) return list.get(0);
			else return null;
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
}
