package org.mq.optculture.data.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;

public class LoyaltyProgramExclusionDaoForDML extends AbstractSpringDaoForDML{
	public  void saveOrUpdate(LoyaltyProgramExclusion loyaltyProgramExclusion) {
        getHibernateTemplate().saveOrUpdate(loyaltyProgramExclusion);
    }

	/*public LoyaltyProgramExclusion getExlusionByProgId(Long prgmId) {
		
		List<LoyaltyProgramExclusion> exclusionList = getHibernateTemplate().find(" FROM LoyaltyProgramExclusion WHERE programId = "+prgmId.longValue());
		if(exclusionList != null && exclusionList.size() >0) return exclusionList.get(0);
		else return null;
	}*/

	public void deleteByPrgmId(Long prgmId) {
		String queryStr = " DELETE FROM LoyaltyProgramExclusion WHERE programId = "+prgmId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
}
