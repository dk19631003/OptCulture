package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.ClickaTellSMSInbound;



public class ClickaTellSMSInboundDaoForDML extends AbstractSpringDaoForDML{

	public ClickaTellSMSInboundDaoForDML() {
		
	}
	
	
	/*public List<ClickaTellSMSInbound> getUserOrgSMSKeyWords(Long orgId) {
		
		List<ClickaTellSMSInbound> keywordsList = null;
		
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		
		keywordsList = getHibernateTemplate().find(qry);
		
		return keywordsList;
		
		
		
	}
	*/
	public void saveOrUpdate(ClickaTellSMSInbound clickaTellSMSInbound) {
        super.saveOrUpdate(clickaTellSMSInbound);
    }

    public void delete(ClickaTellSMSInbound clickaTellSMSInbound) {
        super.delete(clickaTellSMSInbound);
    }

    /*public List findAll() {
        return super.findAll(ClickaTellSMSInbound.class);
    }
	*/
	
	
}
