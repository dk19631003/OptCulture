package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.SocialCampaignReport;


public class SocialCampaignReportDaoForDML extends AbstractSpringDaoForDML {

	public SocialCampaignReportDaoForDML() {
		// TODO Auto-generated constructor stub
	}
	
	/*public SocialCampaignReport findReportByCampId(Long socialCampaignId) {
		List list = getHibernateTemplate().find(" from SocialCampaignReport where campaignId="+ socialCampaignId);
		return (SocialCampaignReport)list.get(0);
	}*/

    public void saveOrUpdate(SocialCampaignReport socialCampaignReport) {
        super.saveOrUpdate(socialCampaignReport);
    }

    public void delete(SocialCampaignReport socialCampaignReport) {
        super.delete(socialCampaignReport);
    }

   /* public List findAll() {
        return super.findAll(SocialCampaignReport.class);
    }
    
    public List<SocialCampaignReport> findAllByUserId(Long userId) {
    	
    	List list = getHibernateTemplate().find(" FROM SocialCampaignReport WHERE userId='"+ userId+"'");
    	return list;
     }*/
}
