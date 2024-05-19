package org.mq.optculture.business.helper;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.campaign.CampaignReportRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * This helper class perform's the task to unSubscribe the contact 
 * @author vinod.bokare
 *
 */
public class UpdateUnsubscribeHelper extends Thread {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public static Queue<Opens> opensQueue  =  new LinkedList<Opens>();


	private CampaignReportRequest campaignReportRequest = null;

	/**
	 * UpdateUnsubscribeHelper
	 * @param campaignReportRequest
	 */
	public UpdateUnsubscribeHelper(CampaignReportRequest campaignReportRequest){

		this.campaignReportRequest = campaignReportRequest;
	}

	/**
	 * run
	 */
	@Override
	public void run() {
		try {
			logger.info("UpdateUnsubscribeHelper Started run method");

			CampaignSent campaignSent =null;
			// As the Value is From request parameter the as per coding standard it should be trim 
			Long sentId =  Long.parseLong(campaignReportRequest.getSentId().trim());
			Long userId =  Long.parseLong(campaignReportRequest.getUserId().trim());
			
			String emailId = campaignReportRequest.getEmailId();
			String reasonStr = campaignReportRequest.getReason();
								
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
			CampaignSentDaoForDML campaignSentDaoForDML = (CampaignSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CAMPAIGN_SENT_DAO_FOR_DML);
			campaignSent = campaignSentDao .findById(sentId);

			if(campaignSent == null) {
				logger.error("returning as campaignSent is null,");
				return;
			}

			if(campaignSent.getOpens() == 0) {
				updateOpens(campaignReportRequest, sentId);
			}

			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, "Unsubscribed");
			
			
			//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
			campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
			if(logger.isDebugEnabled()) {
				logger.debug(" CampaignSent is updated as unsubscribed for sentId :"+sentId);
			}
			
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);			
			Unsubscribes unsubscribes = new Unsubscribes();
			unsubscribes.setEmailId(emailId);
			unsubscribes.setUserId(userId);
			unsubscribes.setReason(reasonStr);
			unsubscribes.setUnsubcategoriesWeight((short)0);
			unsubscribes.setDate(Calendar.getInstance());
			unsubscribesDaoForDML.saveOrUpdate(unsubscribes);
			

		} catch (NumberFormatException e) {
			logger.error("NumberFormatException while processing unSubscribe ..:"+e);
		} catch (BaseServiceException e) {
			logger.error("BaseServiceException while processing unSubscribe ..:"+e);
		} catch (Exception e) {
			logger.error("Exception while processing unSubscribe ..:"+e);
		}		

		logger.info("Completed UpdateUnsubscribeHelper  run method");

	}//run

	/**
	 * 
	 * @param campaignReportRequest
	 * @param sentId
	 * @throws BaseServiceException
	 */
	private void updateOpens(CampaignReportRequest campaignReportRequest,
			Long sentId) throws BaseServiceException {

		synchronized (opensQueue) {

			Opens opens = new Opens(sentId, Calendar.getInstance(), campaignReportRequest.getUserAgent());
			opensQueue.offer(opens);

		}

	}


}//EOF
