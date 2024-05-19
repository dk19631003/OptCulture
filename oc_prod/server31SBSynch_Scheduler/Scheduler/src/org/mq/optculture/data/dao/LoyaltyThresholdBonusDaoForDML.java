package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.captiway.scheduler.beans.LoyaltyThresholdBonus;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyThresholdBonusDaoForDML extends AbstractSpringDaoForDML implements Serializable{

	public void saveOrUpdate(LoyaltyThresholdBonus loyaltyThresholdBonus) {
	        getHibernateTemplate().saveOrUpdate(loyaltyThresholdBonus);
		
	}

	/*public List<LoyaltyThresholdBonus> getBonusListByPrgmId(Long programId) {
		List<LoyaltyThresholdBonus> bonusList = getHibernateTemplate().find(" FROM LoyaltyThresholdBonus WHERE programId = "+programId.longValue());
		if(bonusList != null && bonusList.size() > 0)return bonusList;
		else return null;
	}*/
	
	  public void delete(LoyaltyThresholdBonus loyaltyThresholdBonus) {
          super.delete(loyaltyThresholdBonus);
      }

	/*public LoyaltyThresholdBonus getThresholdById(Long thresholdId) {
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
		
	}*/

	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyThresholdBonus WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
	
}
