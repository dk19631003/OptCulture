package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.captiway.scheduler.beans.LoyaltyCardSet;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.optculture.utils.OCConstants;

public class LoyaltyCardSetDao  extends AbstractSpringDao implements Serializable {

	/*public void saveOrUpdate(LoyaltyCardSet loyaltyCardSet) {
        getHibernateTemplate().saveOrUpdate(loyaltyCardSet);
    }*/

	public List<LoyaltyCardSet> findByProgramId(Long prgmId) {
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue());
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}

	public LoyaltyCardSet findByCardSetId(long cardSetId) {
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(" FROM LoyaltyCardSet WHERE cardSetId = "+cardSetId);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList.get(0);
		else return null;
	}
	
	public List<Long> findInSequenceByProgramId(Long prgmId) {
		
		List<Long> cardSetList = getHibernateTemplate().find(" SELECT cardSetId FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" ORDER BY cardSetId ASC ");
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findActiveByProgramId(Long prgmId) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue()+" AND cardSetType ='"+
		OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL+"' AND status = '"+OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE+"' ORDER BY cardSetId ASC ";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}
	
	public List<LoyaltyCardSet> findByProgramIdStr(String prgmIdStr, String status) {
		
		String queryStr = " FROM LoyaltyCardSet WHERE programId IN ("+prgmIdStr+") AND status = '"+status+"'";
		
		List<LoyaltyCardSet> cardSetList = getHibernateTemplate().find(queryStr);
		if(cardSetList != null && cardSetList.size() > 0) return cardSetList;
		else return null;
	}

	/*public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyCardSet WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}*/
}
