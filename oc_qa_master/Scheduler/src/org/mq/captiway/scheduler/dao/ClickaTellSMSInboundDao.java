package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.ClickaTellSMSInbound;


public class ClickaTellSMSInboundDao extends AbstractSpringDao{

	public ClickaTellSMSInboundDao() {
		
	}
	
	
	/*public List<ClickaTellSMSInbound> getUserOrgSMSKeyWords(Long orgId) {
		
		List<ClickaTellSMSInbound> keywordsList = null;
		
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		
		keywordsList = getHibernateTemplate().find(qry);
		
		return keywordsList;
		
		
		
	}
	*/
	/*public void saveOrUpdate(ClickaTellSMSInbound clickaTellSMSInbound) {
        super.saveOrUpdate(clickaTellSMSInbound);
    }*/

    /*public void delete(ClickaTellSMSInbound clickaTellSMSInbound) {
        super.delete(clickaTellSMSInbound);
    }*/

    public List findAll() {
        return super.findAll(ClickaTellSMSInbound.class);
    }
	
    public ClickaTellSMSInbound findBy(String msgID) {
       
    	List<ClickaTellSMSInbound> inboundList = null;
		
		String qry = "FROM ClickaTellSMSInbound WHERE msgID='"+msgID+"'";
		
		inboundList = executeQuery(qry);
		
		if(inboundList != null && inboundList.size() > 0) return inboundList.get(0);
		else return null;
    	
    	
    }
	
}
