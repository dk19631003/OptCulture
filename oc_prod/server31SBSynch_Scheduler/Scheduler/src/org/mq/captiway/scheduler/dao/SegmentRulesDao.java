package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.SegmentRules;

public class SegmentRulesDao extends AbstractSpringDao {

	

	
	public SegmentRulesDao() {}
	
	
	public SegmentRules find(Long id) {
		return (SegmentRules) super.find(SegmentRules.class, id);
	}

	

	public List<SegmentRules> findAll() {
		return super.findAll(SegmentRules.class);
	}
	
	public List<SegmentRules> findByUser(Long userId) {
		
		return getHibernateTemplate().find("FROM SegmentRules WHERE userId="+userId);
		
		
	}
	
	/**
	 * Checks for theSegmentetion Rule exist with the segRuleName for that userId
	 * 
	 * @param segRuleName
	 * @param userId
	 * @return - boolean : Returns true if Campaign exists with the provided name for the user, else returns false 
	 */
	public boolean checkName(String segRuleName,Long userId){
		List list = getHibernateTemplate().find("From SegmentRules where segRuleName = '" + segRuleName + "' and userId= " + userId);
		if(list.size()>0){
			return true;
		}else{
			return false;
		}
		
	}
	
	public List<SegmentRules> findById(String segRuleId) {
		
		List<SegmentRules> segList = getHibernateTemplate().find("FROM SegmentRules WHERE segRuleId in("+segRuleId+")");
		
		if(segList != null && segList.size() >0) {
			
			return segList;
			
		}else{
			
			return null;
		}
		
		
		
		
	}
	
	
}
