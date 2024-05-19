package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.SegmentRules;

public class SegmentRulesDaoForDML extends AbstractSpringDaoForDML {

	

	
	public SegmentRulesDaoForDML() {}
	
	public void saveOrUpdate(SegmentRules segmentRules) {
		super.saveOrUpdate(segmentRules);
	}
	public void delete(SegmentRules segmentRules) {
		super.delete(segmentRules);
	}
}
