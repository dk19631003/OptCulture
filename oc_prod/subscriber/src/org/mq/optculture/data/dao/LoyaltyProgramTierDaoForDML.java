package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyProgramTierDaoForDML extends AbstractSpringDaoForDML implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	//private JdbcTemplate jdbcTemplate;

	
	public LoyaltyProgramTierDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyProgramTier loyaltyProgramTier) {
		super.saveOrUpdate(loyaltyProgramTier);
	}
	/*
	public List<LoyaltyProgramTier> getTierListByPrgmId(Long prgmId) {
		
		List<LoyaltyProgramTier> tierList = getHibernateTemplate().find(" FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue());
		if(tierList != null && tierList.size() > 0) return tierList;
		else return null;
	}*/
	
	  public void delete(LoyaltyProgramTier loyaltyProgramTier) {
          super.delete(loyaltyProgramTier);
      }

	/*public LoyaltyProgramTier getTierById(Long tierId) {
		try{
			List<LoyaltyProgramTier> tierList= getHibernateTemplate().find(" FROM LoyaltyProgramTier WHERE tierId = "+tierId.longValue());
			if(tierList != null && tierList.size() > 0) return tierList.get(0);
			else return null;
		}catch(Exception e){
			logger.error("Exception in get tier by id...", e);
		}
		return null;
	}

	public List<LoyaltyProgramTier> fetchTiersByProgramId(Long programId) throws LoyaltyProgramException{
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId;
			return getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}
	
	
	public List<LoyaltyProgramTier> fetchTiersBytierIdStr(String tierIdStr) throws LoyaltyProgramException{
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE tierId IN ("+tierIdStr+")";
			return getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}*/

	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
	
	/*public LoyaltyProgramTier findTierByType(Long programId, String type){
		
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

	public LoyaltyProgramTier getTierByPrgmAndType(Long programId, String type) {
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId +" AND tierType = '"+type+"'";
		tierList = getHibernateTemplate().find(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
