package org.mq.marketer.campaign.controller.campaign;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CampaignReportManager  implements ApplicationContextAware {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ApplicationContext context;
	private UsersDao usersDao;
	
	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public CampaignReportManager() {
		
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	/**
	 * Get the current status in the campaign_sent table for this sentId and <BR>
	 * List of unsubscribed categories by this recipient (user base) if he is <BR>
	 * already unsubscribed.
	 * @param sentId
	 * @return Map contains Status and List of unsubscribed categories if any.
	 */
	public Map<Object, Object> getStatusAndCategories(Long sentId, Long userId) {
		
		Map<Object, Object> resultMap = new HashMap<Object, Object>();
		
		try {
			
			CampaignSentDao campaignSentDao = (CampaignSentDao)context.
														getBean("campaignSentDao");
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			resultMap.put(Constants.QS_CRID, campaignSent.getCampaignReport().getCrId());
			resultMap.put(Constants.QS_EMAIL, campaignSent.getEmailId());
			resultMap.put(Constants._STATUS, campaignSent.getStatus());
			
			//for old campaigns userId can be null because userId is not passed in the unsub url
			if(userId == null) {
				
				userId = campaignSentDao.getUserIdBySentId(sentId);
				resultMap.put(Constants.QS_USERID, userId);
				
			}
			UnsubscribesDao unsubscribesDao = 
						       (UnsubscribesDao)context.getBean("unsubscribesDao");
			resultMap.put(Constants._CATEGORIES, unsubscribesDao.
					getUnsubscribedCategories(campaignSent.getEmailId(), userId));
			
		} catch (BeansException e) {
			logger.error("** Exception: while getting the bean from the context", e);
		}
		return resultMap;
	}
	
	/**
	 * Updates the recipient as unsubscribe/resubscribe and returns updated categories map
	 * @param userId
	 * @param crId
	 * @param sentId
	 * @param reasonStr
	 * @param emailId
	 * @param categoryWeight
	 * @return List of categories
	 */
	public List<TemplateCategory> updateUnsubscribe(Long userId, Long crId, Long sentId,
			String reasonStr, String emailId, Short categoryWeight) {
		
		logger.info("---- just entered ----");
		
		CampaignSentDao campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
		CampaignSentDaoForDML campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
		
		if(categoryWeight > 0 ) {
			
			//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
			campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
			if(logger.isDebugEnabled()) {
				logger.debug(" CampaignSent is updated as unsubscribed for sentId :"+sentId);
			}
		}
		else {
			
			//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
			campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
			if(logger.isDebugEnabled()) {
				logger.debug(" CampaignSent is updated as re subscribed for sentId :"+sentId);
			}
		}
		
		
	/*	CampaignReportDao campaignReportDao = (CampaignReportDao)
										context.getBean("campaignReportDao");
		campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);*/
		
		if(logger.isDebugEnabled()) {
			logger.debug(" CampaignReport is updated for sentId :"+sentId);
		}
		
		UnsubscribesDao unsubscribesDao = (UnsubscribesDao)
											context.getBean("unsubscribesDao");
		UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML)
				context.getBean("unsubscribesDaoForDML");
		
		if(categoryWeight == 0 ) {
			unsubscribesDaoForDML.deleteByEmailIdUserId(emailId, userId);
		}
		else{
			
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>>>> Unsubscribe object is creating");
			}
			Unsubscribes unsubscribe = new Unsubscribes();
			unsubscribe.setUserId(userId);
			unsubscribe.setEmailId(emailId);
			unsubscribe.setReason(reasonStr);
			unsubscribe.setUnsubcategoriesWeight(categoryWeight);
			unsubscribe.setDate(Calendar.getInstance());
			
			try {
				unsubscribesDaoForDML.saveOrUpdate(unsubscribe);
				if(logger.isDebugEnabled()) {
					logger.debug(" unsubscribe object is created");
				}
			} 
			catch (RuntimeException e) {
				
				logger.error("** Exception while updating the unsubscribe", e);
				
				if(logger.isDebugEnabled()) {
					logger.debug(" The recipeint of this user is already " +
							"existed in the unsubscribes table");
				}
				if(unsubscribesDaoForDML.updateUnsubscribe(emailId, userId, 
						reasonStr, categoryWeight, Calendar.getInstance()) == 0) {
					logger.warn(" Could not update the unsubscribe for (emailId, userId)"
							+emailId+", "+userId);
				}
				
			}
		}
		return unsubscribesDao.getUnsubscribedCategories(emailId, userId);
		
	}
	
	

}
