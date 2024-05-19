package org.mq.optculture.business.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.campaign.CampaignReportRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 *  This helper class perform's the task to reSubscribe the contact 
 * @author vinod.bokare
 *
 */
public class UpdateResubscribeHelper extends Thread {
	
	private CampaignReportRequest campaignReportRequest = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	public UpdateResubscribeHelper(CampaignReportRequest campaignReportRequest ){
		this.campaignReportRequest = campaignReportRequest;
	}
	
	@Override
	public void run(){
		logger.info("UpdateResubscribeHelper started run method");
		try {
			Long userId = Long.parseLong(campaignReportRequest.getUserId().trim());
			Long sentId =  Long.parseLong(campaignReportRequest.getSentId().trim());
			String emailId = campaignReportRequest.getEmailId().trim();
			
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Active");
			
			//TODO do we need to update unSubscribe table ??
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
			CampaignSentDaoForDML campaignSentDaoForDML = (CampaignSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CAMPAIGN_SENT_DAO_FOR_DML);
			
			//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
			campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
			if(logger.isDebugEnabled()) {
				logger.debug(" CampaignSent is updated as re subscribed for sentId :"+sentId);
			}
			
			//Delete from UnSubscribe 
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);
			unsubscribesDaoForDML.deleteByEmailIdUserId(emailId, userId);
			logger.debug(" DELETE FROM Unsubscribes WHERE emailId"+emailId);
			
			
		} catch (NumberFormatException e) {
			logger.error("Exception while processing unSubscribe ..:"+e);
		} catch (Exception e) {
			logger.error("Exception while processing unSubscribe ..:"+e);
		}		
		logger.info("UpdateResubscribeHelper completed run method");
	}

}//EOF
