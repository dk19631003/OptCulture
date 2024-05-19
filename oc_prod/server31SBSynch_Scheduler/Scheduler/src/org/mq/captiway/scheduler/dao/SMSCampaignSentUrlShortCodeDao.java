package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.optculture.exception.BaseServiceException;


public class SMSCampaignSentUrlShortCodeDao extends AbstractSpringDao{

	/* public void saveOrUpdate(SMSCampaignSentUrlShortCode SMSCampaignSentUrlShortCode) {
        super.saveOrUpdate(SMSCampaignSentUrlShortCode);
    }*/
	
	 
	/* public void saveByCollection(List<SMSCampaignSentUrlShortCode> SMSCampaignSentUrlShortCodeList) {
        super.saveOrUpdateAll(SMSCampaignSentUrlShortCodeList);
    }*/
	
	public SMSCampaignSentUrlShortCode findByShortCode(String shortCode) throws BaseServiceException{
		
		try {
			String qry = "FROM SMSCampaignSentUrlShortCode WHERE generatedShortCode='"+shortCode+"'";
			
			List<SMSCampaignSentUrlShortCode> retList = executeQuery(qry);
			
			if(retList != null && retList.size() > 0) return retList.get(0);
			
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception while getting the required object by code "+shortCode);
		}
		
	}
	 
	 
}
