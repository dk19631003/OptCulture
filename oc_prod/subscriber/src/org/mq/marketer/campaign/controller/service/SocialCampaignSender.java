package org.mq.marketer.campaign.controller.service;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.SocialCampaign;
import org.mq.marketer.campaign.beans.SocialCampaignReport;
import org.mq.marketer.campaign.beans.SocialCampaignSchedule;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.SocialAccountPageSettingsDao;
import org.mq.marketer.campaign.dao.SocialAccountPageSettingsDaoForDML;
import org.mq.marketer.campaign.dao.SocialCampaignDao;
import org.mq.marketer.campaign.dao.SocialCampaignDaoForDML;
import org.mq.marketer.campaign.dao.SocialCampaignReportDao;
import org.mq.marketer.campaign.dao.SocialCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.SocialCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SocialCampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.social.DuplicateStatusException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.connect.LinkedInAdapter;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;

public class SocialCampaignSender  extends TimerTask implements ApplicationContextAware {
	
	public SocialCampaignSender() {
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	ApplicationContext context;
	
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	private UsersDao usersDao;
	public UsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	private SessionFactory sessionFactory = null;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private SocialCampaignDao socialCampaignDao;
	public SocialCampaignDao getSocialCampaignDao() {
		return socialCampaignDao;
	}
	public void setSocialCampaignDao(SocialCampaignDao socialCampaignDao) {
		this.socialCampaignDao = socialCampaignDao;
	}
	private SocialCampaignDaoForDML socialCampaignDaoForDML;

	public SocialCampaignDaoForDML getSocialCampaignDaoForDML() {
		return socialCampaignDaoForDML;
	}
	public void setSocialCampaignDaoForDML(
			SocialCampaignDaoForDML socialCampaignDaoForDML) {
		this.socialCampaignDaoForDML = socialCampaignDaoForDML;
	}

	private SocialCampaignScheduleDao socialCampaignScheduleDao;
	public SocialCampaignScheduleDao getSocialCampaignScheduleDao() {
		return socialCampaignScheduleDao;
	}
	public void setSocialCampaignScheduleDao(
			SocialCampaignScheduleDao socialCampaignScheduleDao) {
		this.socialCampaignScheduleDao = socialCampaignScheduleDao;
	}
	private SocialCampaignScheduleDaoForDML socialCampaignScheduleDaoForDML;

	public SocialCampaignScheduleDaoForDML getSocialCampaignScheduleDaoForDML() {
		return socialCampaignScheduleDaoForDML;
	}
	public void setSocialCampaignScheduleDaoForDML(
			SocialCampaignScheduleDaoForDML socialCampaignScheduleDaoForDML) {
		this.socialCampaignScheduleDaoForDML = socialCampaignScheduleDaoForDML;
	}

	private SocialCampaignReportDao socialCampaignReportDao;
	public SocialCampaignReportDao getSocialCampaignReportDao() {
		return socialCampaignReportDao;
	}
	public void setSocialCampaignReportDao(
			SocialCampaignReportDao socialCampaignReportDao) {
		this.socialCampaignReportDao = socialCampaignReportDao;
	}
	private SocialCampaignReportDaoForDML socialCampaignReportDaoForDML;
	public SocialCampaignReportDaoForDML getSocialCampaignReportDaoForDML() {
		return socialCampaignReportDaoForDML;
	}
	public void setSocialCampaignReportDaoForDML(
			SocialCampaignReportDaoForDML socialCampaignReportDaoForDML) {
		this.socialCampaignReportDaoForDML = socialCampaignReportDaoForDML;
	}

	private SocialAccountPageSettingsDao socialAccountPageSettingsDao;
	public SocialAccountPageSettingsDao getSocialAccountPageSettingsDao() {
		return socialAccountPageSettingsDao;
	}
	public void setSocialAccountPageSettingsDao(
			SocialAccountPageSettingsDao socialAccountPageSettingsDao) {
		this.socialAccountPageSettingsDao = socialAccountPageSettingsDao;
	}
	
	private SocialAccountPageSettingsDaoForDML socialAccountPageSettingsDaoForDML;
	
	public SocialAccountPageSettingsDaoForDML getSocialAccountPageSettingsDaoForDML() {
		return socialAccountPageSettingsDaoForDML;
	}
	public void setSocialAccountPageSettingsDaoForDML(
			SocialAccountPageSettingsDaoForDML socialAccountPageSettingsDaoForDML) {
		this.socialAccountPageSettingsDaoForDML = socialAccountPageSettingsDaoForDML;
	}

	MessagesDao messagesDao;
	MessagesDaoForDML messagesDaoForDML;

	public MessagesDaoForDML getMessagesDaoForDML() {
		return messagesDaoForDML;
	}
	public void setMessagesDaoForDML(MessagesDaoForDML messagesDaoForDML) {
		this.messagesDaoForDML = messagesDaoForDML;
	}
	public MessagesDao getMessagesDao() {
		return messagesDao;
	}
	public void setMessagesDao(MessagesDao messagesDao) {
		this.messagesDao = messagesDao;
	}
	
	Users user;
	
	@Override
	public void run() {
		try {
			
			List<SocialCampaign> socialCampList = socialCampaignDao.findAllCampToBeSent();
			
			if(socialCampList == null || socialCampList.size() == 0) {
				
				logger.debug("** Currently there are no ACTIVE/RUNNING campaigns , Returning **  ");
				return;
			}
			
			for (SocialCampaign socialCampaign : socialCampList) {
				
				user = usersDao.find(socialCampaign.getUserId());
				logger.debug("******** Social Campaign User :" +  user.getUserName());
				
				List<SocialCampaignSchedule> scheduleList = socialCampaignScheduleDao.findActiveSchedulesByCampId(socialCampaign.getCampaignId());
				
				UsersConnectionRepository newUcr =  (UsersConnectionRepository)context.getBean("usersConnectionRepository");
				logger.debug("Got UserConnRepository ="+newUcr);
				ConnectionRepository connectionRepository = newUcr.createConnectionRepository(user.getUserName());
				logger.debug("Got connection repository "+ connectionRepository);
				Connection<Facebook> fbConn = connectionRepository.findPrimaryConnection(Facebook.class);
				Connection<Twitter> twConn =  connectionRepository.findPrimaryConnection(Twitter.class);
				Connection<LinkedIn> lnConn =  connectionRepository.findPrimaryConnection(LinkedIn.class);
				SocialCampaignReport socialCampaignReport;
			
				if(scheduleList != null || scheduleList.size() > 0) {
					//logger.debug("** The Current social campaign is not having any ACTIVE schedules  ... Campaign Name : "+ socialCampaign.getCampaignName());
					//continue;
				
								
								
								
								for (SocialCampaignSchedule socialCampaignSchedule : scheduleList) {
									
									byte providersByte = socialCampaign.getProviders();
									
									String fbErrorMsg = "";
									String twErrorMsg = "";
									
									String lnkinErrorMsg = "";
									String fbFailedPageIds = null; // Init value is null
									String fbActionStatus = "";
									
									// Sending Campaign to Facebook
									if((providersByte & Constants.SOCIAL_ADD_FB) == Constants.SOCIAL_ADD_FB) {
										
										fbErrorMsg = facebookOperation(fbConn, fbFailedPageIds, socialCampaign,socialCampaignSchedule);
									} 
									
									// Sending Campaign to Twitter
									
									if((providersByte & Constants.SOCIAL_ADD_TWITTER) == Constants.SOCIAL_ADD_TWITTER) {
										
										twErrorMsg = twitterOperation(twConn, socialCampaign ,socialCampaignSchedule);
									} 
									
									// Sending Campaign to Linkedin
									if((providersByte & Constants.SOCIAL_ADD_LINKEDIN) == Constants.SOCIAL_ADD_LINKEDIN) {
										lnkinErrorMsg = linkedInOperations(lnConn, socialCampaignSchedule, socialCampaign);
									} // if Linkedin
									
									
									
									
									// Check if there are any failures in current schedule and
									// change schedule status accordingly.
								
									boolean failureFlag = false;
									
									if(fbErrorMsg.length() > 1) {
										failureFlag = true;
										
										/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_FACEBOOK_FB,fbFailedPageIds,fbErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
										socialCampaignScheduleDaoForDML.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_FACEBOOK_FB,fbFailedPageIds,fbErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
									} 
									
									if(twErrorMsg.length() > 1) {
										
										failureFlag = true;
										/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,twErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
										socialCampaignScheduleDaoForDML.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,twErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
									} 
									
									if(lnkinErrorMsg.length() > 1) {
										
										failureFlag = true;
										/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,lnkinErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
										socialCampaignScheduleDaoForDML.addScheduleFailure(socialCampaignSchedule.getScheduleId()
												,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,lnkinErrorMsg,
												0,Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
									}
															
									
									if(failureFlag) {
										// Set schedule status to FAILED.
										socialCampaignSchedule.setScheduleStatus(Constants.SOCIAL_SCHEDULE_STATUS_FAILED);
										Messages message = new Messages("Social Campaign","Social Campaign Schedule FAILED","Your Social campaign "+socialCampaign.getCampaignName()+
												" schedule for post "+ socialCampaignSchedule.getPostType()  +
												" configured for date " + socialCampaignSchedule.getScheduleDate().getTime() +
												" has Failed.", Calendar.getInstance(),"Inbox",false,"INFO",user);
										
										//messagesDao.saveOrUpdate(message);
										messagesDaoForDML.saveOrUpdate(message);
										//socialCampaignScheduleDao.saveOrUpdate(socialCampaignSchedule);
										socialCampaignScheduleDaoForDML.saveOrUpdate(socialCampaignSchedule);

									} else {
										
										// Set schedule status to sent.
										socialCampaignSchedule.setScheduleStatus(Constants.SOCIAL_SCHEDULE_STATUS_SENT);
										Messages message = new Messages("Social Campaign","Social Campaign Schedule","Your Social campaign "+socialCampaign.getCampaignName()+
												" schedule for post "+ socialCampaignSchedule.getPostType()  +
												" configured for date " + socialCampaignSchedule.getScheduleDate().getTime() +
												" has been sent successfully.", Calendar.getInstance(),"Inbox",false,"INFO",user);
										
										//messagesDao.saveOrUpdate(message);
										messagesDaoForDML.saveOrUpdate(message);
										//socialCampaignScheduleDao.saveOrUpdate(socialCampaignSchedule);
										socialCampaignScheduleDaoForDML.saveOrUpdate(socialCampaignSchedule);

									}
									
									
														
								}  // For Schedules
				
				}
				
				
				
				// RESEND FAILED SCHEDULES AGAIN....
				logger.info("<<<<<<<<<< Starting to send failed schedules >>>>>>>>>>>>>>>>>>>>>>>>>>>");
				
				 List<SocialCampaignSchedule> failedSchedulesList = socialCampaignScheduleDao.findFailedScheduleByCampaignId(socialCampaign.getCampaignId());
				 logger.info(" Failed Campaign --->schedles list size : "+ failedSchedulesList.size());
				 
				 if(failedSchedulesList != null && failedSchedulesList.size() > 0) {
					 
					 for (SocialCampaignSchedule failedSchedule : failedSchedulesList) {
						
						 
						 List<Object[]> scheduleFailedList = socialCampaignScheduleDao.getScheduleFailures(failedSchedule.getScheduleId());
						 
						 logger.info("******** Failured Campaign--> Schedule --> destinatins size : "+ scheduleFailedList.size());
						 
						 if(scheduleFailedList != null && scheduleFailedList.size() > 0) {
							 
							 // Contains campaign_schedule_failed Tables vallues as objects
							 // 0 - id ; 1 - schedule_id; 2 - campaign_id ; 3 -  provider_type; 4 - fb_page_ids;
							 // 5 - retry_count ;  6 - failure_action 
							 
							 for (Object[] object : scheduleFailedList) {
								 Long id = (Long)object[0];
								 Long scheduleId = (Long)object[1];
								 Long campaignId = (Long)object[2];
								 String providerType = (String)object[3];
								 String fbPagesIds = (String)object[4];
								 String errorMsg = (String)object[5];
								 int retryCount = (Integer)object[6];
								 String action = (String)object[7];
								 
								 logger.info(" FIELDS are -----> id "+ id + " scheduleId: "+ scheduleId + "campaignId "+ campaignId +  " : " + providerType);
								 
								 // Stop trying if retryCount has reached 3
								 retryCount = retryCount + 1;
								 if(retryCount >= 4) {
									 //socialCampaignScheduleDao.updateScheduleFailureAction(id,Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);
									 socialCampaignScheduleDaoForDML.updateScheduleFailureAction(id,Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);

									 continue;
								 }
								 
								 if(providerType.equals(Constants.SOCIAL_FACEBOOK_FB)) {
									 
									 String retryError = facebookOperation(fbConn, socialCampaign.getFbPageIds(), socialCampaign ,failedSchedule);  
										
									  if(retryError.length() < 1) { 
										   // TODO : socialCampaignScheduleDao.deleteSchedleFailure() ;
										   //socialCampaignScheduleDao.deleteScheduleFailureRecord(id);
										   socialCampaignScheduleDaoForDML.deleteScheduleFailureRecord(id);

									   } else if(retryError.length() > 1) {
										  
											/*int rowsUpdated = socialCampaignScheduleDao.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_FACEBOOK_FB,socialCampaign.getFbPageIds(),
													retryCount,retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
											int rowsUpdated = socialCampaignScheduleDaoForDML.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_FACEBOOK_FB,socialCampaign.getFbPageIds(),
													retryCount,retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
											
											if(rowsUpdated == 1) {
												logger.info(" Twitter schedule resend failure recorded .");
											}
											
									   } 
									 
								 } else if(providerType.equals(Constants.SOCIAL_TWITTER_TWIT)) {
									 
									  String retryError = twitterOperation(twConn,socialCampaign ,failedSchedule);  
									
									  if(retryError.length() < 1) { 
										   // TODO : socialCampaignScheduleDao.deleteSchedleFailure() ;
										   //socialCampaignScheduleDao.deleteScheduleFailureRecord(id);
										   socialCampaignScheduleDaoForDML.deleteScheduleFailureRecord(id);

									   } else if(retryError.length() > 1) {
										  
											/*int rowsUpdated = socialCampaignScheduleDao.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,retryCount,
													retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
											int rowsUpdated = socialCampaignScheduleDaoForDML.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,retryCount,
													retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
											
											if(rowsUpdated == 1) {
												logger.info(" Twitter schedule resend failure recorded .");
											}
											
									   } 
								 } else if(providerType.equals(Constants.SOCIAL_LINKEDIN_LNKIN)) {
									 
									  String retryError = linkedInOperations(lnConn,failedSchedule,socialCampaign);  
										
									  if(retryError.length() < 1) { 
										   // TODO : socialCampaignScheduleDao.deleteSchedleFailure() ;
										  // socialCampaignScheduleDao.deleteScheduleFailureRecord(id);
										   socialCampaignScheduleDaoForDML.deleteScheduleFailureRecord(id);

									   } else if(retryError.length() > 1) {
										  
											/*int rowsUpdated = socialCampaignScheduleDao.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,retryCount,
													retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);*/
											int rowsUpdated = socialCampaignScheduleDaoForDML.updateScheduleFailure(id,failedSchedule.getScheduleId()
													,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,retryCount,
													retryError,	Constants.SOCIAL_FAILURE_ACTION_STATUS_RETRY);
											
											if(rowsUpdated == 1 ) {
												logger.info("Linked in schedule resend failure recorded .");
											}
											
									   } 
									 
								 }
								 
								 
							 }

							 // TODO: modiy schedule if all failures hav passed in the last loop
							 List<Object[]> failedList = socialCampaignScheduleDao.getScheduleFailures(failedSchedule.getScheduleId());
							 if(failedList != null && failedList.size() == 0) {
								 failedSchedule.setScheduleStatus(Constants.SOCIAL_SCHEDULE_STATUS_SENT);
							 }
							 
						 } // if
					} // for
					 
				 }
				 
				
				// TODO: UPdate campaing status finally .
				//socialCampaign.setCampaignStatus("SENT");
				//String campaignUpdated = socialCampaignDao.updateCampaignStatusById(socialCampaign.getCampaignId());
				String campaignUpdated = socialCampaignDaoForDML.updateCampaignStatusById(socialCampaign.getCampaignId());

				if(campaignUpdated.equals(Constants.SOCIAL_CAMP_STATUS_SENT)) {
					logger.info("messageDao "+ messagesDao);
					Messages message = new Messages("Social Campaign","Social Campaign","Your Social campaign "+socialCampaign.getCampaignName()+
							" has been sent successfully.",Calendar.getInstance(),"Inbox",false,"INFO",user);
					 //messagesDao.saveOrUpdate(message);
					 messagesDaoForDML.saveOrUpdate(message);
				}
				
				logger.info("********* Campaign Updated Value is : "+ campaignUpdated);
			}	   // social campaigns	
			
			
			
			
						
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} 
	}
	
	/*  
	 *  This method  performs FB operations based on given page ids 
	 *  returns error msgs, if any.
	 */
	private String facebookOperation(Connection<Facebook> fbConn, String fbFailedPageIds,
			SocialCampaign socialCampaign,SocialCampaignSchedule socialCampaignSchedule) {
		
		String fbFailureMsg = "";
		if(fbConn !=null) {
			
			fbFailedPageIds = "";	
			Facebook fb = fbConn.getApi();
			String fbPageIds = socialCampaign.getFbPageIds();
		
				// Get FB pages or page.
				if(fbPageIds != null && fbPageIds.length() > 0) {

					String pageIdArr[] = fbPageIds.split(",");
					SocialCampaignReport socialCampaignReport;
												
				  // POST on Facebook
					
					// <<<<<< send to all the PAGE IDS within loop and create report .
					for(int i=0 ; i<pageIdArr.length ; i++) {
						try {
						
							if(pageIdArr[i].trim().length()==0) {
								continue;
							}
							
							String reportToken = fb.feedOperations().post(pageIdArr[i],
									socialCampaignSchedule.getCampaignContent()); 
							
							logger.debug("****** Report Token is :"+ reportToken);
							socialCampaignReport = new SocialCampaignReport(socialCampaign.getCampaignId(),socialCampaignSchedule.getScheduleId(),
									socialCampaign.getCampaignName(),"FB",socialCampaignSchedule.getScheduleDate(),
									reportToken,user.getUserId(),"SENT"); 
							//socialCampaignReportDao.saveOrUpdate(socialCampaignReport);
							socialCampaignReportDaoForDML.saveOrUpdate(socialCampaignReport);

						} catch (Exception e) {
							logger.debug("Exception : Error occured while sending to FB ",e);
							fbFailureMsg += "FACEBOOK PAGE ID : "+ pageIdArr[i] + " - " + e.getMessage() + " ^|^ ";
							if(fbFailedPageIds.length() == 0) {
								fbFailedPageIds = pageIdArr[i];
							} else {
								fbFailedPageIds += ","+pageIdArr[i];
							}	
							
						} 
					} // FB pages 
					
				}
			
			}
			else {
				
				logger.debug("** Facebook Connection is null for the user, Hence returning ...");
				//fbFailureMsg = "FACEBOOK ERROR : Connection is null.";
			/*	socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
						,socialCampaign.getCampaignId(),Constants.SOCIAL_FACEBOOK_FB,null,
						"FACEBOOK ERROR : Connection is null.",	Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);*/
				fbFailureMsg =  "FACEBOOK ERROR : Connection is null.";
			}
		return fbFailureMsg;
	}
	
	private String twitterOperation(Connection<Twitter> twConn,SocialCampaign socialCampaign ,
			SocialCampaignSchedule socialCampaignSchedule) {
		    String twitFailureMsg = "";
		try {
			if(twConn !=null) {
				
				Twitter tw = twConn.getApi();
				 String twitterStr = socialCampaignSchedule.getCampaignContent();
				 if(socialCampaign.getTwitterContent() != null && socialCampaign.getTwitterContent().length() > 0) {
					 
					 twitterStr = socialCampaign.getTwitterContent();
				 }
				 Tweet tweet = tw.timelineOperations().updateStatus(twitterStr);
				 String twitReportToken = String.valueOf(tweet.getId());
				 logger.debug("****** Report Token is :"+ twitReportToken);
			 
				 SocialCampaignReport socialCampaignReport = new SocialCampaignReport(socialCampaign.getCampaignId(),socialCampaignSchedule.getScheduleId(),
						socialCampaign.getCampaignName(),"TWIT",socialCampaignSchedule.getScheduleDate(),
						twitReportToken,user.getUserId(),"SENT"); 
			    //socialCampaignReportDao.saveOrUpdate(socialCampaignReport);
			    socialCampaignReportDaoForDML.saveOrUpdate(socialCampaignReport);

			} else {
			
				logger.debug("** Twitter Connection is null for the user, Hence returning ...");
				//twitFailureMsg = "TWITTER ERROR : Connection is null .";
				/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
						,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,
						"TWITTER ERROR : Connection is null.",	Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);*/
				twitFailureMsg = "TWITTER ERROR : Connection is null.";
			}	
				return twitFailureMsg;
		} catch(DuplicateStatusException e) {
			
			//twitFailureMsg = "TWITTER ERROR!!!! : Duplicate Status, status update operation aborted .";
			logger.error("****** Duplicate Status, status update operation aborted .");
			/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
					,socialCampaign.getCampaignId(),Constants.SOCIAL_TWITTER_TWIT,null,
					"TWITTER ERROR!!!! : Duplicate Status, status update operation aborted .",
					Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);*/
			twitFailureMsg = "TWITTER ERROR!!!! : Duplicate Status, status update operation aborted .";
			return twitFailureMsg;
		}
		catch (Exception e) {
			
			logger.debug("** Error while sending a twit campaing",e);
			twitFailureMsg = "TWITTER ERROR !!! : "+ e.getMessage();
			return twitFailureMsg;
		}
	}
	
	
	private String linkedInOperations(Connection<LinkedIn> lnConn, SocialCampaignSchedule socialCampaignSchedule,SocialCampaign socialCampaign) {
		String linkedInFailureMsg ="";
		try {
			LinkedInAdapter lnAdv = new LinkedInAdapter();
			// TODO: linked not working...
			if(lnConn !=null) {
				LinkedIn ln = lnConn.getApi();
				lnAdv.updateStatus(ln, socialCampaignSchedule.getCampaignContent());
				 
			}
			else {
				logger.debug("** Linked Connection is null for the user, Hence returning ...");
				//linkedInFailureMsg = "LinkedIn ERROR : Connection is null .";
				/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
						,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,
						"LinkedIn ERROR : Connection is null .",Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);*/
				linkedInFailureMsg = "LinkedIn ERROR : Connection is null .";
			}
			return linkedInFailureMsg;
		} catch(DuplicateStatusException e) {
			logger.error("****** Duplicate Status, status update operation aborted .");
			//linkedInFailureMsg = "Linkedin ERROR!!!! : Duplicate Status, status update operation aborted .";
			/*socialCampaignScheduleDao.addScheduleFailure(socialCampaignSchedule.getScheduleId()
					,socialCampaign.getCampaignId(),Constants.SOCIAL_LINKEDIN_LNKIN,null,
					"Linkedin ERROR!!!! : Duplicate Status, status update operation aborted .",
					Constants.SOCIAL_FAILURE_ACTION_STATUS_INFO);*/
			linkedInFailureMsg = "Linkedin ERROR!!!! : Duplicate Status, status update operation aborted .";
			return linkedInFailureMsg; 
		} catch (Exception e) {
			logger.debug("*** Error while sending Linked in campaign ",e);
			linkedInFailureMsg = "Linkedin ERROR!!!!  "+ e.getMessage();
			return  linkedInFailureMsg;
		}	
	}

}
