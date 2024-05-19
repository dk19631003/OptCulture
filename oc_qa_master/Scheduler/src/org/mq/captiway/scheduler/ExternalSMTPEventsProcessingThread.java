package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.campaign.ExternalSMTPSpamEventsQueue;
import org.mq.captiway.scheduler.dao.BouncesDao;
import org.mq.captiway.scheduler.dao.BouncesDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDao;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDaoForDML;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.BounceCategories;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.ServiceLocator;

public class ExternalSMTPEventsProcessingThread extends Thread{

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	public void run() {

		try {
			logger.debug("===started ExternalSMTPEventsProcessingThread===");
			int i = 0;
			int size = 2000;
			List<ExternalSMTPEvents> eventsList;
			List<ExternalSMTPEvents> deleventList = new ArrayList<ExternalSMTPEvents>();
			CampaignSent campaignSent = null;
			
			ServiceLocator context = ServiceLocator.getInstance();
			 ExternalSMTPEventsDao externalSMTPEventsDao = (ExternalSMTPEventsDao)context.getDAOByName("externalSMTPEventsDao");
			 ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML = (ExternalSMTPEventsDaoForDML)context.getDAOForDMLByName("externalSMTPEventsDaoForDML");
			 CampaignSentDao campaignSentDao = (CampaignSentDao)context.getDAOByName("campaignSentDao");
			 CampaignSentDaoForDML campaignSentDaoForDML = (CampaignSentDaoForDML)context.getDAOForDMLByName("campaignSentDaoForDML");
			 CampaignReportDao campaignReportDao = (CampaignReportDao)context.getDAOByName("campaignReportDao");
			 CampaignReportDaoForDML campaignReportDaoForDML = (CampaignReportDaoForDML)context.getDAOForDMLByName("campaignReportDaoForDML");
			 BouncesDao bouncesDao = (BouncesDao)context.getDAOByName("bouncesDao");
			 BouncesDaoForDML bouncesDaoForDML = (BouncesDaoForDML)context.getDAOForDMLByName("bouncesDaoForDML");

			 ContactsDao contactsDao = (ContactsDao)context.getDAOByName("contactsDao");
			 ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)context.getDAOForDMLByName("contactsDaoForDML");
			 SuppressedContactsDao suppressedContactsDao = (SuppressedContactsDao)context.getDAOByName("suppressedContactsDao");
			 SuppressedContactsDaoForDML suppressedContactsDaoForDML = (SuppressedContactsDaoForDML)context.getDAOForDMLByName("suppressedContactsDaoForDML");
			 UsersDao usersDao = (UsersDao)context.getDAOByName("usersDao");
			 ExternalSMTPSpamEventsQueue externalSMTPSpamEventsQueue = (ExternalSMTPSpamEventsQueue)context.getBeanByName("externalSMTPSpamEventsQueue");
			 ExternalSMTPSpamEventsDestructor externalSMTPSpamEventsDestructor = (ExternalSMTPSpamEventsDestructor)context.getBeanByName("externalSMTPSpamEventsDestructor");
			
			do {
				
				eventsList = externalSMTPEventsDao.findAllEvents(Calendar.getInstance(), i, 2000);
				if(eventsList == null || eventsList.isEmpty()) {
					
					if(logger.isInfoEnabled()) logger.info(">>>> Num of Events to  be processed found: 0");
					break;
				} 
				else {
					
					String eventType = null;
					String type = null;
					String statusCode = null;
					String reason = null;
					
					for (ExternalSMTPEvents externalSMTPEvents : eventsList) {
						try {
							deleventList.add(externalSMTPEvents);
							if(externalSMTPEvents.getEmailId() == null ||
									externalSMTPEvents.getCrId() == null || 
									externalSMTPEvents.getUserId() == null) {
								
								continue;
							}
							
							eventType = externalSMTPEvents.getEventType();
							reason = externalSMTPEvents.getReason();
						//	logger.info("Processing the reports for the campaign of event type : "+eventType+"email :"+externalSMTPEvents.getEmailId());
							type = externalSMTPEvents.getType();
							statusCode = externalSMTPEvents.getStatusCode();
							
							campaignSent = campaignSentDao.findSentByEmailId(externalSMTPEvents.getEmailId(), externalSMTPEvents.getCrId());
							if(campaignSent == null) {
								
								continue;
								
							}
							//logger.info("campaignSent ::"+campaignSent.getEmailId());
							//delivered
							if( eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED) ) {
								updateCampaignSent(campaignSent, Constants.CS_STATUS_SUCCESS, campaignSentDaoForDML);
								//updateCampaignReport(campaignSent.getCampaignReport().getCrId(),  campaignReportDaoForDML, Constants.CR_TYPE_SENT);
							}
							else if( eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DROPPED) ) {
								
								try {
									Bounces bounce = new Bounces(campaignSent,BounceCategories.DROPPED,externalSMTPEvents.getReason(),
											new Date(),externalSMTPEvents.getCrId());
									//TODO Dropped status does not have status code.It is completely dropped.
									//bouncesDao.saveOrUpdate(bounce);
									bouncesDaoForDML.saveOrUpdate(bounce);

									addToSuppressedContacts(externalSMTPEvents.getUserId(), 
											campaignSent.getEmailId(), Constants.SUPP_TYPE_BOUNCED, externalSMTPEvents.getReason(), usersDao, suppressedContactsDaoForDML);
									contactsDaoForDML.updateEmailStatusByUserId(campaignSent.getEmailId(), externalSMTPEvents.getUserId(), Constants.CONT_STATUS_BOUNCED);
								} catch (Exception e) {
									if(logger.isErrorEnabled()) logger.error("Exception : Error occured while updating bounce status ***",e);
									continue;
								}
								//updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDao);
								updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDaoForDML);
								//updateCampaignReport(campaignSent.getCampaignReport().getCrId(),  campaignReportDao, Constants.CR_TYPE_BOUNCES);
							//	updateCampaignReport(campaignSent.getCampaignReport().getCrId(),  campaignReportDaoForDML, Constants.CR_TYPE_BOUNCES);
								//addToSuppressedContacts(externalSMTPEvents.getUserId(), campaignSent.getEmailId(), Constants.SUPP_TYPE_BOUNCED,	usersDao, suppressedContactsDao);
							}//if
							else if( eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE) ){
								
								try {
									
									if(type == null || statusCode == null) {
										logger.info("no statuscode was given in the flow of the event processor  "+externalSMTPEvents.getEmailId());

										continue;
									}
									
									if( reason.contains("550") || (type.equalsIgnoreCase("10") && (statusCode.startsWith("550")||statusCode.startsWith("552")))
											|| type.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE) 
											||	statusCode.startsWith("5")
									) {
										

										Bounces bounce = new Bounces(campaignSent,BounceCategories.BOUNCE,externalSMTPEvents.getReason(),
												new Date(),externalSMTPEvents.getCrId());
										bounce.setStatusCode(statusCode);
										//bouncesDao.saveOrUpdate(bounce);
										bouncesDaoForDML.saveOrUpdate(bounce);

										addToSuppressedContacts(externalSMTPEvents.getUserId(), 
												campaignSent.getEmailId(), Constants.SUPP_TYPE_BOUNCED, externalSMTPEvents.getReason(), usersDao, suppressedContactsDaoForDML);
										contactsDaoForDML.updateEmailStatusByUserId(campaignSent.getEmailId(), externalSMTPEvents.getUserId(), Constants.CONT_STATUS_BOUNCED);
										//contactsDaoForDML.updateEmailStatusByUserId(campaignSent.getEmailId(), externalSMTPEvents.getUserId(), Constants.CONT_STATUS_BOUNCED, Constants.CONT_STATUS_ACTIVE);
									}
									else if(type.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BLOCKED)) {
										Bounces bounce = new Bounces(campaignSent,BounceCategories.BLOCKED,externalSMTPEvents.getReason(),
												new Date(),externalSMTPEvents.getCrId());
										bounce.setStatusCode(statusCode);
										//bouncesDao.saveOrUpdate(bounce);
										bouncesDaoForDML.saveOrUpdate(bounce);

										
										//addToSuppressedContacts(externalSMTPEvents.getUserId(),campaignSent.getEmailId(), Constants.SUPP_TYPE_BOUNCED, externalSMTPEvents.getReason(), usersDao, suppressedContactsDaoForDML);
										//contactsDaoForDML.updateEmailStatusByUserId(campaignSent.getEmailId(), externalSMTPEvents.getUserId(), Constants.CONT_STATUS_ACTIVE);
										contactsDaoForDML.updateEmailStatusByUserId(campaignSent.getEmailId(), externalSMTPEvents.getUserId(), Constants.CONT_STATUS_ACTIVE ,Constants.CONT_STATUS_PURGE_PENDING);
									}
									else if(type.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_EXPIRED)) {
											Bounces bounce = new Bounces(campaignSent,BounceCategories.OTHERS,externalSMTPEvents.getReason(),
													new Date(),externalSMTPEvents.getCrId());
											bounce.setStatusCode(statusCode);
											//bouncesDao.saveOrUpdate(bounce);
											bouncesDaoForDML.saveOrUpdate(bounce);

									}
									else {
										if((type.startsWith("22") && statusCode.startsWith("452")) || (statusCode.startsWith("4") || statusCode.startsWith("451"))){ //451 
											Bounces bounce = new Bounces(campaignSent,BounceCategories.SOFTBOUNCE,externalSMTPEvents.getReason(),
													new Date(),externalSMTPEvents.getCrId());
											bounce.setStatusCode(statusCode);
											//bouncesDao.saveOrUpdate(bounce);
											bouncesDaoForDML.saveOrUpdate(bounce);

										}
										else {
											Bounces bounce = new Bounces(campaignSent,BounceCategories.OTHERS,externalSMTPEvents.getReason(),
													new Date(),externalSMTPEvents.getCrId());
											bounce.setStatusCode(statusCode);
											//bouncesDao.saveOrUpdate(bounce);
											bouncesDaoForDML.saveOrUpdate(bounce);

										}
									}
//								logger.debug("request type is "+ eventType + " request obj : "+ request+" for "+campaignSent.getSentId());
								} catch (Exception e) {
									if(logger.isErrorEnabled()) logger.error("Exception : Error occured while updating bounce status ***",e);
									continue;
								}
									//updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDao);
									updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDaoForDML);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDao, Constants.CR_TYPE_BOUNCES);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDaoForDML, Constants.CR_TYPE_BOUNCES);
							}
							else if( eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT) ){
								
								try {
									//campaignSentDao.setStatusBySentId(sentId, "spammed", crId);
									//updateCampaignSent(campaignSent, Constants.CS_STATUS_SPAMMED, campaignSentDao);
									updateCampaignSent(campaignSent, Constants.CS_STATUS_SPAMMED, campaignSentDaoForDML);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDao, Constants.CR_TYPE_SPAM);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDaoForDML, Constants.CR_TYPE_SPAM);
									addToSuppressedContacts(externalSMTPEvents.getUserId(), 
											campaignSent.getEmailId(), Constants.CS_STATUS_SPAMMED, externalSMTPEvents.getReason(), usersDao, suppressedContactsDaoForDML);
									
									externalSMTPSpamEventsQueue.addObjToQueue(campaignSent.getEmailId());
									if(externalSMTPSpamEventsQueue.getQueueSize() >= 100) {
										
										if(!externalSMTPSpamEventsDestructor.isRunning()) {
											logger.info("processor is not running , try to ping it....");
											externalSMTPSpamEventsDestructor.run();
											continue;
										}
										
									}
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception");
									continue;
								}
							}
							else {
								try {
									Bounces bounce = new Bounces(campaignSent,BounceCategories.OTHERS,externalSMTPEvents.getReason(),
											new Date(),externalSMTPEvents.getCrId());
									bounce.setStatusCode(statusCode);
									//bouncesDao.saveOrUpdate(bounce);
									bouncesDaoForDML.saveOrUpdate(bounce);

									//updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDao);
									updateCampaignSent(campaignSent, Constants.CS_STATUS_BOUNCED, campaignSentDaoForDML);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDao, Constants.CR_TYPE_BOUNCES);
									//updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDaoForDML, Constants.CR_TYPE_BOUNCES);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception");
									continue;
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception");
							continue;
						}
						
						
					}//for
					if(externalSMTPSpamEventsQueue.getQueueSize() > 0) {
						if(!externalSMTPSpamEventsDestructor.isRunning()) {
							logger.info("processor is not running , try to ping it....");
							externalSMTPSpamEventsDestructor.run();
						}
					}
					
				}//else
				
				i += size;
				
				//eventsList.clear();
				logger.debug("eventsList.size() == size" +eventsList.size() +" and "+ size);
				//need to delete the processed records...
				if(deleventList.size() > 0) {
					updateCampaignReport(campaignSent.getCampaignReport().getCrId(),  campaignReportDaoForDML, Constants.CR_TYPE_BOUNCES);
					updateCampaignReport(campaignSent.getCampaignReport().getCrId(), campaignReportDaoForDML, Constants.CR_TYPE_SPAM);
					logger.info("to delete th eevents....");
					//externalSMTPEventsDao.deleteByCollection(deleventList);
					externalSMTPEventsDaoForDML.deleteByCollection(deleventList);

					deleventList.clear();
				}
				
			}while(eventsList.size() == size);
			//Utility.isrunning = false;
			//logger.info("made the utility to false.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in processing the files==="+e);
			//Utility.isrunning = false; // if failed then it not updtaing to false to run this thread. 

		}finally {
			
			Utility.isrunning = false;
		}
		
		
	
	}
	public static void addToSuppressedContacts(Long userId, String emailId,String type, String reason,
    		UsersDao usersDao, SuppressedContactsDaoForDML suppressedContactsDao ) {
		try {
			SuppressedContacts suppressedContact = new SuppressedContacts();
			Users users = usersDao.find(userId); 
			suppressedContact.setUser(users);
			suppressedContact.setSuppressedtime(Calendar.getInstance());
			suppressedContact.setEmail(emailId);
			suppressedContact.setType(type);
			suppressedContact.setReason(reason);
			suppressedContactsDao.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to suppress contacts .");
		} catch (Exception e) {
			logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	
	 /**
     * Updates the CampaignSent and CampaignReport tables for the specified type
     * @param campaignSent - CampaignSent Object
     * @param type - Specifies for which type to update the report 
     * 			(Opens, Clicks, Bounces, Unsubscribes ...)
     * @return
     */
    public static int updateCampaignSent(CampaignSent campaignSent, String type, /*CampaignSentDao campaignSentDao*/ CampaignSentDaoForDML campaignSentDaoForDML) {
       // if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        
        int updateCount = 0;
		if(campaignSent == null) {
			if(logger.isWarnEnabled()) logger.warn("CampiagnSent Object is null");
			return updateCount;
		}
		try {
			//updateCount = campaignSentDao.updateCampaignSent(campaignSent.getSentId(), type);
			updateCount = campaignSentDaoForDML.updateCampaignSent(campaignSent.getSentId(), type);
			if(updateCount == 0 ) return updateCount;
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception : Problem while updating the "+ type +" in CampaignSent \n", e);
		}
		
		
		
       // if(logger.isDebugEnabled()) logger.debug("-- Exit --");
        return updateCount;
    
    }
	 public  int updateCampaignReport(Long crId, /*CampaignReportDao campaignReportDao*/ CampaignReportDaoForDML campaignReportDaoForDML, String type) {
	    	
			try {
				
				//Long  crId = campaignSentDao.getCrIdBySentId(campaignSent.getSentId());
				int resp = campaignReportDaoForDML.updateCampaignReport(crId, type);
				//resp = campaignReportDao.updateCampaignReport(crId, type);
				/*resp = campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
				resp = campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_RESUBSCRIBES);*/
				return resp;
			} catch (Exception e) {
				logger.error("Exception : Problem while updating  in CampaignReport \n", e);
				return 0;
			}
	    	
	    }
	 public static int updateCampaignBounceReport(Long crId, /*CampaignReportDao campaignReportDao*/CampaignReportDaoForDML campaignReportDaoForDML) {
	    	
			try {
				
				//Long  crId = campaignSentDao.getCrIdBySentId(campaignSent.getSentId());
				int resp = campaignReportDaoForDML.updateCampaignBounceReport(crId, Constants.CR_TYPE_BOUNCES);
				resp = campaignReportDaoForDML.updateCampaignBounceReport(crId, Constants.CR_TYPE_SPAM);
				return resp;
			} catch (Exception e) {
				logger.error("Exception : Problem while updating  in CampaignReport \n", e);
				return 0;
			}
	    	
	    }
	    
	   
	    public static void finalUpdateCampaignSent(/*CampaignSentDao campaignSentDao*/CampaignSentDaoForDML campaignSentDaoForDML, Long campRepId) {
	    	
	    	try {
				
				//int resp = campaignSentDao.finalUpdateCampaignSent(campRepId);
	    		int resp = campaignSentDaoForDML.finalUpdateCampaignSent(campRepId);
				
			} catch (Exception e) {
				logger.error("Exception : Problem while updating  in CampaignReport \n", e);
			}
	    	
	    	
	    	
	    	
	    }
	    
	    
	    
	    public  void updateBulkCampaignSent(/*CampaignSentDao campaignSentDao*/CampaignSentDaoForDML campaignSentDaoFoDML, Long campRepId, String type) {
	    	
	    	
	    	try {
				
				//Long  crId = campaignSentDao.getCrIdBySentId(campaignSent.getSentId());
	    		
	    		//campaignSentDao.updateBulkCampaignSent(campRepId, type);
	    		
	    		campaignSentDaoFoDML.updateBulkCampaignSent(campRepId, type);
	    		
	    		
	    		
			} catch (Exception e) {
				logger.error("Exception : Problem while updating  in CampaignReport \n", e);
				return;
				
			}
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    }//updateBulkCampaignSent
	    
	    
}
