package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyThresholdBonusDao extends AbstractSpringDao implements Serializable{

	/*public void saveOrUpdate(LoyaltyThresholdBonus loyaltyThresholdBonus) {
	        getHibernateTemplate().saveOrUpdate(loyaltyThresholdBonus);
		
	}*/

	public List<LoyaltyThresholdBonus> getBonusListByPrgmId(Long programId) {
		List<LoyaltyThresholdBonus> bonusList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE programId = "+programId.longValue());
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
	}
	
	public List<LoyaltyThresholdBonus> getBonusListByPrgmIdAndOrder(Long programId, String desc_Asc,String bonusEarnType) {
		List<LoyaltyThresholdBonus> bonusList = executeQuery(" FROM LoyaltyThresholdBonus WHERE programId = "+programId.longValue() + 
				"And extraBonusType = '"+bonusEarnType+"' ORDER BY extraBonusValue "+desc_Asc);
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
	}
	
	public List<LoyaltyThresholdBonus> getBonusListByBonusEarnType(Long programId,String earnType) {
		List<LoyaltyThresholdBonus> bonusList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE programId ="+programId.longValue()+" "
				+ "AND extraBonusType = '"+earnType+"'"  );
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
	}
	
	
	
	public List<LoyaltyThresholdBonus> getBonusListByEarnType(Long programId,String earnType) {
		List<LoyaltyThresholdBonus> bonusList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE programId ="+programId.longValue()+" "
				+ "AND earnedLevelType LIKE '%"+earnType+"%'"  );
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
	}
	
	 /* public void delete(LoyaltyThresholdBonus loyaltyThresholdBonus) {
          super.delete(loyaltyThresholdBonus);
      }*/

	public LoyaltyThresholdBonus getThresholdById(Long thresholdId) {
		List<LoyaltyThresholdBonus> thresholdList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE thresholdBonusId = "+thresholdId.longValue());
		if(thresholdList != null && thresholdList.size() > 0)return thresholdList.get(0);
		else return null;
	}

	public LoyaltyThresholdBonus getRegistrationBonusByPrgmId(Long programId) {
		
		List<LoyaltyThresholdBonus> thresholdList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE registrationFlag = '"+OCConstants.FLAG_YES+"' AND programId = "+programId.longValue());
		if(thresholdList != null && thresholdList.size() > 0)return thresholdList.get(0);
		else return null;
	}
      
	public List<LoyaltyThresholdBonus> getBonusListByPrgmId(Long programId, char regFlag){
		
		String queryStr = " FROM LoyaltyThresholdBonus WHERE programId = "+programId.longValue()+" AND registrationFlag = '"+regFlag+"'";
		
		List<LoyaltyThresholdBonus> bonusList = getHibernateTemplate().find(queryStr);
		
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
		
	}
	public List<LoyaltyThresholdBonus>  getAllExpireBy(Long ProgramId, boolean withExpirity){
		
		try {
			String query = "FROM LoyaltyThresholdBonus WHERE programId ="+ProgramId+" AND "
					+ (withExpirity ? "bonusExpiryDateValue IS NOT NULL AND bonusExpiryDateType IS  NOT NULL" : 
						"bonusExpiryDateValue IS  NULL AND bonusExpiryDateType IS   NULL");
			
			List<LoyaltyThresholdBonus> retList = executeQuery(query);
			
			return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
		
	}
	
	public List<LoyaltyThresholdBonus> getAllOnlyAutoCommExpireBy() {
		try {
			List<LoyaltyThresholdBonus> programList = null;
			String queryStr = " SELECT DISTINCT lb FROM LoyaltyThresholdBonus lb ,LoyaltyProgram lp WHERE lb.programId=lp.programId "
					+ " AND lp.userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) "
					+ " AND ((lp.rewardExpiryFlag!='Y' ) OR (lp.rewardExpiryFlag='Y' AND lp.programId  IN(SELECT programId FROM LoyaltyAutoComm WHERE rewardExpiryEmailTmpltId is NULL AND rewardExpirySmsTmpltId IS  NULL ))) AND "
					+ "lp.status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' "
							+ "AND lp.rewardType != '"+OCConstants.REWARD_TYPE_PERK+"'";
			programList = executeQuery(queryStr);
			
			if(programList != null && programList.size() > 0){
				return programList;
			}
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
public List<LoyaltyThresholdBonus>  getAllExpireComm(Long ProgramId){
		
		try {
			String query = "FROM LoyaltyThresholdBonus WHERE programId ="+ProgramId+" AND "
					+ " bonusExpiryDateValue IS NOT NULL AND bonusExpiryDateType IS NOT NULL AND "
					+ "(emailExpiryTempId IS NOT NULL OR smsExpiryTempId IS NOT NULL)";
			
			List<LoyaltyThresholdBonus> retList = executeQuery(query);
			
			return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
		
	}
	
/*public List<LoyaltyThresholdBonus>  getAllOnlyExpireBy(){
		
		try {
			
			String query = "SELECT DISTINCT lb FROM LoyaltyThresholdBonus lb, LoyaltyProgram lp WHERE "
					+ "lb.programId=lp.programId AND lp.rewardExpiryFlag='N' AND lp.userId IN(SELECT userId FROM Users WHERE "
					+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND lp.status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND lb.bonusExpiryDateValue IS NOT NULL AND lb.bonusExpiryDateType IS NOT NULL";
			
			
			List<LoyaltyThresholdBonus> retList = executeQuery(query);
			
			return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
		
	}*/

public List<LoyaltyThresholdBonus>  getAllOnlyExpireBy(){
	
	try {
		
		String query = "SELECT DISTINCT lb FROM LoyaltyThresholdBonus lb, LoyaltyProgram lp WHERE "
				+ "lb.programId=lp.programId  AND lp.userId IN(SELECT userId FROM Users WHERE "
				+ "Date(now())<=Date(packageExpiryDate) AND enabled=true  ) AND "
				+ "lp.status = '" +OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE+ "' AND "
						+ "lb.bonusExpiryDateValue IS NOT NULL AND lb.bonusExpiryDateType IS NOT NULL AND lp.rewardType != '"+OCConstants.REWARD_TYPE_PERK+"'";
		
		
		List<LoyaltyThresholdBonus> retList = executeQuery(query);
		
		return retList;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}
	return null;
	
}

	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyThresholdBonus WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/
	
}
