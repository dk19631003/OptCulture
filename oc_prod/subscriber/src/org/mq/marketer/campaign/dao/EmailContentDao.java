package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.EmailContent;

public class EmailContentDao extends AbstractSpringDao {

    public EmailContentDao() {}
	
    public EmailContent find(Long contentId) {
        return (EmailContent) super.find(EmailContent.class, contentId);
    }

   /* public void saveOrUpdate(EmailContent emailContent) {
        super.saveOrUpdate(emailContent);
    }*/
    
    @SuppressWarnings("unchecked")
	public List<EmailContent> getByCampaignId(Long campaignId) {
    	return getHibernateTemplate().find("FROM EmailContent WHERE campaignId ="+campaignId);
    }
}

