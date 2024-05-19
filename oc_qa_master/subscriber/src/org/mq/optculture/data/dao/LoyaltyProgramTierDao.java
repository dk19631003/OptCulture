package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyProgramTierDao extends AbstractSpringDao implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	//private JdbcTemplate jdbcTemplate;

	
	public LoyaltyProgramTierDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyProgramTier loyaltyProgramTier) {
		super.saveOrUpdate(loyaltyProgramTier);
	}*/
	
	public List<LoyaltyProgramTier> getTierListByPrgmId(Long prgmId) {
		
		List<LoyaltyProgramTier> tierList = executeQuery(" FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue());
		if(tierList != null && tierList.size() > 0) return tierList;
		else return null;
	}
	public String getTierName(String prgmId) {
		
	
		String queryStr = "SELECT tierName FROM LoyaltyProgramTier WHERE tierId ="+prgmId;
		List<String> userNameList = executeQuery(queryStr);
		return (String)userNameList.get(0);

	}
	
public List<LoyaltyProgramTier> getTierListByPrgmIdAsc(Long prgmId) {
		
		List<LoyaltyProgramTier> tierList = getHibernateTemplate().find(" FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue()+" order by tierId asc");
		logger.info("===="+"FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue());
		if(tierList != null && tierList.size() > 0) return tierList;
		else return null;
	}
	
	
	
	 /* public void delete(LoyaltyProgramTier loyaltyProgramTier) {
          super.delete(loyaltyProgramTier);
      }*/

	public LoyaltyProgramTier getTierById(Long tierId) {
		try{
			List<LoyaltyProgramTier> tierList= executeQuery(" FROM LoyaltyProgramTier WHERE tierId = "+tierId.longValue());
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
			return executeQuery(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}
public List<LoyaltyProgramTier> fetchByTier(Long programId,Long tierId) throws LoyaltyProgramException{
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			String subQry="";
			if(tierId != null) {
				subQry += " AND tierId =" + tierId.longValue();
			}
			String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId+ subQry;
			return executeQuery(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}
	
	
	public List<LoyaltyProgramTier> fetchTiersBytierIdStr(String tierIdStr) throws LoyaltyProgramException{
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE tierId IN ("+tierIdStr+")";
			return executeQuery(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}

	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyProgramTier WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/
	
	public LoyaltyProgramTier findTierByType(Long programId, String type){
		
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = "from LoyaltyProgramTier where programId = "+programId+" and tierType = '"+type+"'";
		tierList = executeQuery(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	public LoyaltyProgramTier findByTierId(Long tierId) {
		
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = "FROM LoyaltyProgramTier WHERE tierId = "+tierId;
		tierList = executeQuery(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	public List<Long> findAllTierIdsByProgamId(Long programId){
		
		String queryStr = "SELECT tierId FROM LoyaltyProgramTier WHERE programId = "+programId;
		return executeQuery(queryStr);
		
	}
	
	public List<LoyaltyProgramTier> findAllTiers(){
		
		String queryStr = " FROM LoyaltyProgramTier ";
		return executeQuery(queryStr);
		
	}

	public LoyaltyProgramTier getTierByPrgmAndType(Long programId, String type) {
		List<LoyaltyProgramTier> tierList = null;
		String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId +" AND tierType = '"+type+"'";
		tierList = executeQuery(queryStr);
		
		if(tierList != null && tierList.size() > 0){
			return tierList.get(0);
		}
		else return null;
	}
	
	public List<LoyaltyProgramTier> getAllTierByProgramId(Long programId){
		String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId;
		return executeQuery(queryStr);
		
	}
	
	public int findLiabilityCountByProgram(Long userId,String subQuery) {
    	try {
    		String query ="SELECT COUNT(*) from LoyaltyProgramTier where createdBy ="+userId+" "+subQuery+"";
    						//+ " GROUP BY programId,tierId";
			return ((Long) find(query).get(0)).intValue();
			
		} catch (DataAccessException e) { 
			logger.error("Exception ::" , e);
			return 0;
		}
    }
	
	public List<LoyaltyProgramTier> findLiabilityByProgram(Long userId,String prgmIds,String subQuery,int start,int end) {
			if(subQuery==null || subQuery.isEmpty()) subQuery= " AND programId IN("+prgmIds+") ";
    		String query ="FROM LoyaltyProgramTier where createdBy ="+userId+" "+subQuery+"";
    						//+ " GROUP BY programId,tierId";
			return executeQuery(query,start,end);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
