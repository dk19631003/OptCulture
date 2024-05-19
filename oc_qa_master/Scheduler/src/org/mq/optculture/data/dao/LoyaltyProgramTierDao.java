package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;

public class LoyaltyProgramTierDao extends AbstractSpringDao implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	//private JdbcTemplate jdbcTemplate;

	
	public LoyaltyProgramTierDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyProgramTier loyaltyProgramTier) {
		super.saveOrUpdate(loyaltyProgramTier);
	}*/
	
	public List<LoyaltyProgramTier> getTierListByPrgmId(Long prgmId) {
		
		List<LoyaltyProgramTier> tierList = getHibernateTemplate().find(" FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue());
		if(tierList != null && tierList.size() > 0) return tierList;
		else return null;
	}
	
	  /*public void delete(LoyaltyProgramTier loyaltyProgramTier) {
          super.delete(loyaltyProgramTier);
      }*/

	public LoyaltyProgramTier getTierById(Long tierId) {
		
		List<LoyaltyProgramTier> tierList= getHibernateTemplate().find(" FROM LoyaltyProgramTier WHERE tierId = "+tierId.longValue());
		if(tierList != null && tierList.size() > 0) return tierList.get(0);
		else return null;
	}

	public List<LoyaltyProgramTier> fetchTiersByProgramId(Long programId) {
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId;
			listOfTiers = getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error("fetch tiers failed",e);
		}
		return listOfTiers;
	}
	
	
	public List<LoyaltyProgramTier> fetchTiersBytierIdStr(String tierIdStr) {
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE tierId IN ("+tierIdStr+")";
			listOfTiers = getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error("fetch tiers failed",e);
		}
		return listOfTiers;
	}

	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/
	
	public LoyaltyProgramTier findTierByType(Long programId, String type){
		
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = "from LoyaltyProgramTier where programId = "+programId+" and tierType = '"+type+"'";
		tierList = getHibernateTemplate().find(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	public LoyaltyProgramTier findByTierId(Long tierId) {
		
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = "FROM LoyaltyProgramTier WHERE tierId = "+tierId;
		tierList = getHibernateTemplate().find(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	public List<Long> findAllTierIdsByProgamId(Long programId){
		
		String queryStr = "SELECT tierId FROM LoyaltyProgramTier WHERE programId = "+programId;
		return getHibernateTemplate().find(queryStr);
		
	}
	
	public List<LoyaltyProgramTier> findAllTiers(){
		
		String queryStr = " FROM LoyaltyProgramTier ";
		return getHibernateTemplate().find(queryStr);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
