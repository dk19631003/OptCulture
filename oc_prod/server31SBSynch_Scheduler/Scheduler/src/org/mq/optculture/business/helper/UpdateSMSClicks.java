package org.mq.optculture.business.helper;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.captiway.scheduler.beans.SMSClicks;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSClicksDao;
import org.mq.captiway.scheduler.dao.SMSClicksDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


public class UpdateSMSClicks extends Thread{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode;
	
	public UpdateSMSClicks(){}
	
	public UpdateSMSClicks(SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode) {
		
		this.smsCampaignSentUrlShortCode = smsCampaignSentUrlShortCode;
		
	}
	
	@Override
	public void run() {
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL);
		
		Long smscampUrlId = smsCampaignSentUrlShortCode.getSmsCampaignUrlId();
		Long sentID = smsCampaignSentUrlShortCode.getSentId();
		String urlcode = smsCampaignSentUrlShortCode.getGeneratedShortCode();
		String url = smsCampaignSentUrlShortCode.getClickedurl();
		
		if(smscampUrlId == null || sentID == null || urlcode == null) {
			
			logger.error("required param(s) ");
			return;
		}
		String actualurl = (url == null ? appShortUrl + urlcode : url);
		
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML  smsCampaignSentDaoForDML = null;
		SMSCampaignReportDao smsCampaignReportDao = null;
		SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
		SMSClicksDao smsClicksDao = null;
		SMSClicksDaoForDML smsClicksDaoForDML= null;

		ServiceLocator locator = ServiceLocator.getInstance();
		
		try {
			smsCampaignReportDao = (SMSCampaignReportDao)locator.getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_ForDML);
			smsCampaignSentDao = (SMSCampaignSentDao)locator.getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML);

			smsClicksDao = (SMSClicksDao)locator.getDAOByName(OCConstants.SMS_CLICKS_DAO);
			smsClicksDaoForDML = (SMSClicksDaoForDML )locator.getDAOForDMLByName(OCConstants.SMS_CLICKS_DAO_ForDML);

		} catch (Exception e) {
			logger.error("Exception ", e);
			return;
		}
		
		List<SMSCampaignSent> retList = smsCampaignSentDao.findByIds(sentID.longValue()+Constants.STRING_NILL);
		if(retList == null){
			logger.error("no sent record with "+sentID.longValue());
			return;
		}
		SMSCampaignSent sentObj = retList.get(0);
		
		
		Long smsCrID = sentObj.getSmsCampaignReport().getSmsCrId();
		
		SMSClicks clicked = new SMSClicks(sentObj, actualurl, smscampUrlId, Calendar.getInstance());
		
		try {
			
			//smsClicksDao.saveOrUpdate(clicked);
			smsClicksDaoForDML.saveOrUpdate(clicked);
			//smsCampaignSentDao.updateClicks(sentID, smsCrID);//check the time taking for this query
			smsCampaignSentDaoForDML.updateClicks(sentID, smsCrID);//check the time taking for this query

			smsCampaignReportDaoForDML.updateClickReport(smsCrID);
			
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			logger.error("exception in update ", e);
		}
	}
	
	
	
}

