package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.SMSCampaignUrls;


public class SMSCampaignUrlDaoForDML extends AbstractSpringDaoForDML{

	 public void saveOrUpdate(SMSCampaignUrls SMSCampaignUrl) {
        super.saveOrUpdate(SMSCampaignUrl);
    }
	
	 
	 public void saveByCollection(List<SMSCampaignUrls> SMSCampaignUrlList) {
        super.saveOrUpdateAll(SMSCampaignUrlList);
    }
	 
}
