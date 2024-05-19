package org.mq.marketer.campaign.dao;


import java.util.List;

import org.mq.marketer.campaign.beans.CampaignSegmentFields;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class CampaignSegmentFieldsDaoForDML extends AbstractSpringDaoForDML {

    public CampaignSegmentFieldsDaoForDML() {}
	
   /* public CampaignSegmentFields find(Long id) {
        return (CampaignSegmentFields) super.find(CampaignSegmentFields.class, id);
    }*/

    public void saveOrUpdate(CampaignSegmentFields campaignSegmentFields) {
        super.saveOrUpdate(campaignSegmentFields);
    }

    public void delete(CampaignSegmentFields campaignSegmentFields) {
        super.delete(campaignSegmentFields);
    }

   /* public List<CampaignSegmentFields> findAll() {
        return super.findAll(CampaignSegmentFields.class);
    }*/
    
}

