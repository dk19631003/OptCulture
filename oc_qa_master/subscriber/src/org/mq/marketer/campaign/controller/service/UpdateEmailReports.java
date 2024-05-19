package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.beans.EmailClient;
import org.mq.marketer.campaign.beans.NotificationClicks;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.beans.UserAgentJSON;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.ClicksDaoForDML;
import org.mq.marketer.campaign.dao.EmailClientDao;
import org.mq.marketer.campaign.dao.EmailClientDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.NotificationCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.NotificationCampaignSentDao;
import org.mq.marketer.campaign.dao.NotificationCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.NotificationClicksDaoForDML;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.OpensDaoForDML;
import org.mq.marketer.campaign.dao.UserAgentJSONDao;
import org.mq.marketer.campaign.dao.UserAgentJSONDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LRUCache;
import org.mq.marketer.campaign.general.XML;
import org.mq.optculture.business.campaign.CampaignReportBusinessServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UpdateEmailReports extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ApplicationContext context;
	/*private EmailClickedQueue emailClickedQueue;
	private EmailOpenedQueue emailOpenedQueue;*/
	private OpensDao opensDao;
	private OpensDaoForDML opensDaoForDML;
	private ClicksDao clicksDao;
	private ClicksDaoForDML clicksDaoForDML;
	private NotificationClicksDaoForDML notificationClicksDaoForDML;
	private CampaignSentDao campaignSentDao;
	private NotificationCampaignSentDao notificationCampaignSentDao;
	private CampaignSentDaoForDML campaignSentDaoForDML;
	private NotificationCampaignSentDaoForDML notificationCampaignSentDaoForDML;
	private CampaignReportDao campaignReportDao;
	private CampaignReportDaoForDML campaignReportDaoForDML;
	private NotificationCampaignReportDaoForDML notificationCampaignReportDaoForDML;
	 private EmailClientDao emailClientDao;
	 private EmailClientDaoForDML emailClientDaoForDML;
	 private EventTriggerEventsObservable eventTriggerEventsObservable;
	 private EventTriggerEventsObserver eventTriggerEventsObserver;
	 private EventTriggerDao eventTriggerDao;
	 private UserAgentJSONDao userAgentJSONDao;
	 private UserAgentJSONDaoForDML userAgentJSONDaoForDML;
	
	//private List<EmailClient> emailClientList;
	 private static Map<String, Long> emailClientsMap ;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
		
		/*emailClickedQueue = (EmailClickedQueue)context.getBean("emailClickedQueue");
		emailOpenedQueue = (EmailOpenedQueue)context.getBean("emailOpenedQueue");*/
		opensDao = (OpensDao)context.getBean("opensDao");
		opensDaoForDML = (OpensDaoForDML)context.getBean("opensDaoForDML");
		clicksDao = (ClicksDao)context.getBean("clicksDao");
		clicksDaoForDML = (ClicksDaoForDML)context.getBean("clicksDaoForDML");
		notificationClicksDaoForDML = (NotificationClicksDaoForDML)context.getBean("notificationClicksDaoForDML");
		campaignReportDao = (CampaignReportDao)context.getBean("campaignReportDao");
		campaignReportDaoForDML = (CampaignReportDaoForDML)context.getBean("campaignReportDaoForDML");
		notificationCampaignReportDaoForDML = (NotificationCampaignReportDaoForDML)context.getBean("notificationCampaignReportDaoForDML");
		emailClientDao = (EmailClientDao)context.getBean("emailClientDao");
		emailClientDaoForDML = (EmailClientDaoForDML)context.getBean("emailClientDaoForDML");
		campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
		notificationCampaignSentDao = (NotificationCampaignSentDao)context.getBean("notificationCampaignSentDao");
		campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
		notificationCampaignSentDaoForDML = (NotificationCampaignSentDaoForDML)context.getBean("notificationCampaignSentDaoForDML");
		
		//emailClientList  = emailClientDao.findAll();
		emailClientsMap = new HashMap<String, Long>();
		eventTriggerEventsObservable = (EventTriggerEventsObservable)context.getBean("eventTriggerEventsObservable");
		eventTriggerEventsObserver = (EventTriggerEventsObserver)context.getBean("eventTriggerEventsObserver");
		
		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
		eventTriggerDao = (EventTriggerDao)context.getBean("eventTriggerDao");
		userAgentJSONDao = (UserAgentJSONDao)context.getBean("userAgentJSONDao");
		userAgentJSONDaoForDML = (UserAgentJSONDaoForDML)context.getBean("userAgentJSONDaoForDML");
	}
	
	private volatile boolean isRunning;
	public boolean isRunning() {
		//logger.debug("isRunning ::"+isRunning);
		return isRunning;
	}
	
	
	
	
	@Override
	public void run() {
		
		try {
			this.isRunning = true;
			
			
			
			 if(emailClientsMap.isEmpty()) {
		        	reloadEmailClientMap();
		        }
			/*PriorityQueue< Opens> emailOpenedQueue = CampaignReportBusinessServiceImpl.opensQueue;
			PriorityQueue< Clicks> emailClickedQueue = CampaignReportBusinessServiceImpl.clicksQueue;*/
			//
			 Long startOpensId = null;
			 Long endOpenId = null;
			 Long startClickId = null;
			 Long endClickId = null;
			 
			 //better take all into some temp list and process because this queue will be growable.
			 List<Opens> opensList = new ArrayList<Opens>();
			 List<Clicks> clicksList = new ArrayList<Clicks>();
			 List<NotificationClicks> notificationClicksList = new ArrayList<NotificationClicks>();
			 List<Clicks> clickListToTraverse = new ArrayList<Clicks>();//needed becuase no mapping for useragent
			 List<NotificationClicks> notificationClickListToTraverse = new ArrayList<NotificationClicks>();
			 
			if(CampaignReportBusinessServiceImpl.opensQueue.size() > 0) {
				
				opensList.addAll(CampaignReportBusinessServiceImpl.opensQueue);
				Set<Long> openSentIdsSet = new HashSet<Long>();
				
				//PriorityQueue< Opens> openQueue = emailOpenedQueue.getQueue();
				
				//TODO need to update the EmailClient for all these opens
				//logger.info("opensDao "+opensDao+"  queue "+CampaignReportBusinessServiceImpl.opensQueue.size());
				
				//need to parse the user-agent and save 
				//List<Opens> opensToBeSaved = new ArrayList<Opens>();
			//	for (Opens opens : CampaignReportBusinessServiceImpl.opensQueue) {
				for (Opens opens : opensList) {	
					try {
						updateOpenUADetails(opens, opens.getUserAgent());
						opensDaoForDML.saveOrUpdate(opens);
						if(startOpensId == null){
							startOpensId = opens.getOpenId().longValue();
						}
						endOpenId = opens.getOpenId().longValue();
						//opensToBeSaved.add(opens);
						/*if(opensToBeSaved.size() >= 200) {
							
							//opensDao.saveByCollection(opensToBeSaved);
							opensDaoForDML.saveByCollection(opensToBeSaved);
							endOpenId = opensToBeSaved.get(opensToBeSaved.size()-1).getOpenId().longValue();
							opensToBeSaved.clear();
						}*/
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception while saving opens");
					}
					
				}//
				/*if(opensToBeSaved.size() > 0) {
					
					//opensDao.saveByCollection(opensToBeSaved);
					try {
						opensDaoForDML.saveByCollection(opensToBeSaved);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception while saving "+opensToBeSaved.size());
						for (Opens opens : opensToBeSaved) {
							
						}
					}
					if(startOpensId == null){
						startOpensId = opensToBeSaved.get(0).getOpenId().longValue();
					}
					endOpenId = opensToBeSaved.get(opensToBeSaved.size()-1).getOpenId().longValue();
					opensToBeSaved.clear();
				}*/
				
				//opensDao.saveByCollection(CampaignReportBusinessServiceImpl.opensQueue);
				
				
				int i = 0;
				for (Opens opens : opensList) {
					
					if(i >= 200 ) break;
					i ++;
					
					openSentIdsSet.add(opens.getSentId());
					
					
				}
				
				/*List<Object[]> opensWithNoClients = opensDao.findAllOpensWithNoClient(openSentIdsSet);
				
				if(opensWithNoClients != null) {
					
					List<Opens> opensToBeUpdated = new ArrayList<Opens>();
					for (Object[] objects : opensWithNoClients) {
						
						String emailId = ( (String)objects[0]).toString();
						Opens open = (Opens)objects[1];
						open.setEmailClient(emailClientCheck(emailId));
						
						opensToBeUpdated.add(open);
						
					}
					if(opensToBeUpdated.size() > 0) {
						
						opensDao.saveByCollection(opensToBeUpdated);
					}
				
				}*/
				
				List<Long> crIdStr = campaignSentDao.findCrIdsBySent(openSentIdsSet); //need to check hw many cr objects are fetching
				
				if(crIdStr != null && crIdStr.size() > 0)  {
					
					for (Long crId : crIdStr) {
						
						//campaignSentDao.updateCampaignSent(Constants.CS_TYPE_OPENS, crId, startOpensId, endOpenId, openSentIdsSet );
						campaignSentDaoForDML.updateCampaignSent(Constants.CS_TYPE_OPENS, crId, startOpensId, endOpenId, openSentIdsSet );
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
						campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
						//campaignSentDao.finalUpdateForUnsubCampaignSent(crId);
						campaignSentDaoForDML.finalUpdateForUnsubCampaignSent(crId);
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						
						
						eventTriggerEventsObservable.notifyForInteractionEvents(crId, startOpensId, endOpenId, Constants.CS_TYPE_OPENS);
						
						
						
					}//for
					
				}//if
				synchronized (CampaignReportBusinessServiceImpl.opensQueue) {
					
					CampaignReportBusinessServiceImpl.opensQueue.removeAll(opensList);
				}
				
				
			}//if
			
			
			if(CampaignReportBusinessServiceImpl.clicksQueue.size() > 0) {
				
				//PriorityQueue<Clicks> clicksQueue = emailClickedQueue.getQueue();
				clicksList.addAll(CampaignReportBusinessServiceImpl.clicksQueue);
				clickListToTraverse.addAll(clicksList);
				//clicksDao.saveByCollection(CampaignReportBusinessServiceImpl.clicksQueue);
				//clicksDao.saveByCollection(clicksList);
				try {
					clicksDaoForDML.saveByCollection(clicksList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
					for (Clicks clicks : clicksList) {
						try {
							clicksDaoForDML.saveOrUpdate(clicks);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							
						}
					}
				}
				
				//no need to check for startID 'null'
				startClickId = clicksList.get(0).getClickId();
				endClickId = clicksList.get(clicksList.size()-1).getClickId();
				
				int i = 0;
				Set<Long> sentIdsSet = new HashSet<Long>();
				Map<Long, String> clicksMap = new HashMap<Long, String>();
				
				//for (Clicks clicks : CampaignReportBusinessServiceImpl.clicksQueue) {
				for (Clicks clicks : clickListToTraverse) {
					
					if(i >= 200 ) break;
					i ++;
					
					sentIdsSet.add(clicks.getSentId());
					clicksMap.put(clicks.getSentId(), clicks.getUserAgent());//require for update client info for opens where clicks>0,opens=0
					
					
				}//for
				
				//**** START ADDED by KRISHNA ***********
				
				List<Object[]> sentAndcrIds = campaignSentDao.findSentAndCrIdsBySent(sentIdsSet);
				Map<Long, Long> sentIdMap = new HashMap<Long, Long>();
				
				for(Object[] arr : sentAndcrIds) {
					logger.info("sentAndcrIds="+arr[0]+" = "+arr[1]);
					sentIdMap.put(Long.parseLong(arr[0].toString()), Long.parseLong(arr[1].toString()));
				}
				
				for (Clicks clicks : clicksList) {
					clicks.setCrId(sentIdMap.get(clicks.getSentId()));
				}
				//clicksDao.saveByCollection(clicksList);
				try {
					clicksDaoForDML.saveByCollection(clicksList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					for (Clicks clicks : clicksList) {
						try {
							clicksDaoForDML.saveOrUpdate(clicks);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						}
					}
				}
				
				//**** END ADDED by KRISHNA ***********

				
				List<Long> crIdStr = campaignSentDao.findCrIdsBySent(sentIdsSet); 
				
				logger.info("cdIdStr ::"+crIdStr.size());
				
				if(crIdStr != null && crIdStr.size() > 0)  {
					
					for (Long crId : crIdStr) {
						
						//campaignSentDao.updateCampaignSent(Constants.CS_TYPE_CLICKS, crId, startClickId, endClickId, sentIdsSet);
						campaignSentDaoForDML.updateCampaignSent(Constants.CS_TYPE_CLICKS, crId, startClickId, endClickId, sentIdsSet);
						
						//insert opens with clicks in the clicks table with no opens(i.e. clicks >0 opens=0)
						//TODO what about the new opens related events???????????
						List<Object[]> sentIds = campaignSentDao.findNoOpens(sentIdsSet); //need to check how many records for the same click
						
						if(sentIds != null && sentIds.size() > 0) {
							Set<Long> noOpendSentIdSet = new HashSet<Long>();
							startOpensId = null;
							endOpenId = null;
							
							List<Opens> noOpensList = new ArrayList<Opens>();
								
								for (Object[] objects : sentIds) {
									
									Long sentId = (Long)objects[0];
									String uAgent = clicksMap.get(sentId);
									noOpendSentIdSet.add(sentId);
									Opens open = new Opens(sentId, (Calendar)objects[1], uAgent);
									updateOpenUADetails(open, uAgent);
									noOpensList.add(open);
									opensDaoForDML.saveOrUpdate(open);
									if(startOpensId == null){
										startOpensId = open.getOpenId().longValue();
									}
									endOpenId = open.getOpenId().longValue();
									
									
									
									
								}//for
								
								
							if(noOpensList.size() > 0) {
								
								//opensDao.saveByCollection(noOpensList);
								/*opensDaoForDML.saveByCollection(noOpensList);
								
								if(startOpensId == null){
									startOpensId = noOpensList.get(0).getOpenId().longValue();
								}
								endOpenId = noOpensList.get(noOpensList.size()-1).getOpenId().longValue();
								
*/								//campaignSentDao.updateCampaignSent(Constants.CS_TYPE_OPENS, crId, startOpensId, endOpenId, noOpendSentIdSet );
								campaignSentDaoForDML.updateCampaignSent(Constants.CS_TYPE_OPENS, crId, startOpensId, endOpenId, noOpendSentIdSet );
								//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
								campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
								eventTriggerEventsObservable.notifyForInteractionEvents(crId, startOpensId,	endOpenId, Constants.CS_TYPE_OPENS);
									
							}//if
							
							
							
						}//sentId not null
						/*List<Opens> noOpensList = new ArrayList<Opens>();
						if(sentIds != null) {
							
							logger.info("sentIds ::"+sentIds.size());
							
							for (Object[] objects : sentIds) {
								
								Opens open = new Opens((Long)objects[0], (Calendar)objects[1], emailClientCheck((String)objects[2]));
								noOpensList.add(open);
								
							}//for
							
							
							
						}//if
*/						
						//update cs with open=0 and clicks>0 after above insertion
						//campaignSentDao.finalUpdateCampaignSent(crId);
						campaignSentDaoForDML.finalUpdateCampaignSent(crId);
							
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_CLICKS);
						campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_CLICKS);
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						eventTriggerEventsObservable.notifyForInteractionEvents(crId, startClickId, endClickId, Constants.CS_TYPE_CLICKS);
						
						
					}//for
					
					
				}//if
				
				synchronized (CampaignReportBusinessServiceImpl.clicksQueue) {
					
					//CampaignReportBusinessServiceImpl.clicksQueue.clear();
					CampaignReportBusinessServiceImpl.clicksQueue.removeAll(clicksList);
				}
			}//if
			
			
			if(CampaignReportBusinessServiceImpl.notificationClicksQueue.size() > 0) {
				
				//PriorityQueue<Clicks> clicksQueue = emailClickedQueue.getQueue();
				notificationClicksList.addAll(CampaignReportBusinessServiceImpl.notificationClicksQueue);
				notificationClickListToTraverse.addAll(notificationClicksList);
				//clicksDao.saveByCollection(CampaignReportBusinessServiceImpl.clicksQueue);
				//clicksDao.saveByCollection(clicksList);
				notificationClicksDaoForDML.saveByCollection(clicksList);
				
				//no need to check for startID 'null'
				startClickId = notificationClicksList.get(0).getClickId();
				endClickId = notificationClicksList.get(notificationClicksList.size()-1).getClickId();
				
				int i = 0;
				Set<Long> sentIdsSet = new HashSet<Long>();
				Map<Long, String> notificationClicksMap = new HashMap<Long, String>();
				
				//for (Clicks clicks : CampaignReportBusinessServiceImpl.clicksQueue) {
				for (NotificationClicks notificationClicks : notificationClickListToTraverse) {
					
					if(i >= 200 ) break;
					i ++;
					
					sentIdsSet.add(notificationClicks.getSentId());
					notificationClicksMap.put(notificationClicks.getSentId(), notificationClicks.getUserAgent());//require for update client info for opens where clicks>0,opens=0
					
					
				}//for
				
				//**** START ADDED by KRISHNA ***********
				
				List<Object[]> sentAndcrIds = notificationCampaignSentDao.findSentAndNotificationCrIdsBySent(sentIdsSet);
				Map<Long, Long> sentIdMap = new HashMap<Long, Long>();
				
				for(Object[] arr : sentAndcrIds) {
					logger.info("sentAndcrIds="+arr[0]+" = "+arr[1]);
					sentIdMap.put(Long.parseLong(arr[0].toString()), Long.parseLong(arr[1].toString()));
				}
				
				for (NotificationClicks notificationClicks : notificationClicksList) {
					notificationClicks.setNotificationCrId(sentIdMap.get(notificationClicks.getSentId()));
				}
				//clicksDao.saveByCollection(clicksList);
				notificationClicksDaoForDML.saveByCollection(notificationClicksList);
				
				//**** END ADDED by KRISHNA ***********

				
				List<Long> notificationCrIdStr = notificationCampaignSentDao.findNotificationCrIdsBySent(sentIdsSet); 
				
				logger.info("notificationCrIdStr ::"+notificationCrIdStr.size());
				
				if(notificationCrIdStr != null && notificationCrIdStr.size() > 0)  {
					
					for (Long notificationCrId : notificationCrIdStr) {
						
						//campaignSentDao.updateCampaignSent(Constants.CS_TYPE_CLICKS, crId, startClickId, endClickId, sentIdsSet);
						notificationCampaignSentDaoForDML.updateNotificationCampaignSent(Constants.CS_TYPE_CLICKS, notificationCrId, startClickId, endClickId, sentIdsSet);
						
						//insert opens with clicks in the clicks table with no opens(i.e. clicks >0 opens=0)
						//TODO what about the new opens related events???????????
						/*
						 * List<Object[]> sentIds = campaignSentDao.findNoOpens(sentIdsSet); //need to
						 * check how many records for the same click
						 * 
						 * if(sentIds != null && sentIds.size() > 0) { Set<Long> noOpendSentIdSet = new
						 * HashSet<Long>(); startOpensId = null; endOpenId = null;
						 * 
						 * List<Opens> noOpensList = new ArrayList<Opens>();
						 * 
						 * for (Object[] objects : sentIds) {
						 * 
						 * Long sentId = (Long)objects[0]; String uAgent = clicksMap.get(sentId);
						 * noOpendSentIdSet.add(sentId); Opens open = new Opens(sentId,
						 * (Calendar)objects[1], uAgent); updateOpenUADetails(open, uAgent);
						 * noOpensList.add(open);
						 * 
						 * 
						 * }//for
						 * 
						 * 
						 * if(noOpensList.size() > 0) {
						 * 
						 * //opensDao.saveByCollection(noOpensList);
						 * opensDaoForDML.saveByCollection(noOpensList);
						 * 
						 * if(startOpensId == null){ startOpensId =
						 * noOpensList.get(0).getOpenId().longValue(); } endOpenId =
						 * noOpensList.get(noOpensList.size()-1).getOpenId().longValue();
						 * 
						 * //campaignSentDao.updateCampaignSent(Constants.CS_TYPE_OPENS, crId,
						 * startOpensId, endOpenId, noOpendSentIdSet );
						 * campaignSentDaoForDML.updateCampaignSent(Constants.CS_TYPE_OPENS, crId,
						 * startOpensId, endOpenId, noOpendSentIdSet );
						 * //campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
						 * campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_OPENS);
						 * eventTriggerEventsObservable.notifyForInteractionEvents(crId, startOpensId,
						 * endOpenId, Constants.CS_TYPE_OPENS);
						 * 
						 * }//if
						 * 
						 * 
						 * 
						 * }//sentId not null
						 */						/*List<Opens> noOpensList = new ArrayList<Opens>();
						if(sentIds != null) {
							
							logger.info("sentIds ::"+sentIds.size());
							
							for (Object[] objects : sentIds) {
								
								Opens open = new Opens((Long)objects[0], (Calendar)objects[1], emailClientCheck((String)objects[2]));
								noOpensList.add(open);
								
							}//for
							
							
							
						}//if
*/						
						//update cs with open=0 and clicks>0 after above insertion
						//campaignSentDao.finalUpdateCampaignSent(crId);
						//notificationCampaignSentDaoForDML.finalUpdateCampaignSent(notificationCrId);
							
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_CLICKS);
						notificationCampaignReportDaoForDML.updateNoticationCampaignReport(notificationCrId, Constants.CR_TYPE_CLICKS);
						//campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						//campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_UNSUBSCRIBES);
						//eventTriggerEventsObservable.notifyForInteractionEvents(crId, startClickId, endClickId, Constants.CS_TYPE_CLICKS);
						
						
					}//for
					
					
				}//if
				
				synchronized (CampaignReportBusinessServiceImpl.notificationClicksQueue) {
					
					//CampaignReportBusinessServiceImpl.clicksQueue.clear();
					CampaignReportBusinessServiceImpl.notificationClicksQueue.removeAll(notificationClicksList);
				}
			}//if
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally{
			
			isRunning = false;
			
		}
		
	} // run
	
	 /*private static final String rawXml = "<?xml version=\"1.0\"?><methodCall>" +
	 									  "<methodName>ua.search</methodName>" +
		 									"<params><param><value>" +
		 									  "<string>{0}</string>" +
		 									"</value></param>" +
		 									"<param><value>" +
		 									  "<string>{1}</string>" +
		 									"</value></param></params>" +
		 								  "</methodCall>";*/
	
	 //private static final String key = "free";  
	
	static LRUCache<String, JSONObject> uaLRUCache = new LRUCache<String, JSONObject>(1000);
	
	public void updateOpenUADetails(Opens opens, String userAgent) {

    	try {
    		
    		// logger.info("---------->> UADetails update <<-----------");
    		if(opens==null || userAgent==null) {
    			return;
    		}
    		//logger.debug("userAgent =====>"+userAgent);
    		JSONObject tempJson = uaLRUCache.get(userAgent);
    		
    		//logger.debug("tempJson =====>"+tempJson);
    		//UserAgentJSON uaJsonObj = uaLRUCache.get(userAgent);
    		if(tempJson == null) {
    		//if(uaJsonObj == null) {
    			UserAgentJSON uaJsonObj = userAgentJSONDao.findByUAStr(userAgent);
    			if(uaJsonObj!=null) {
    				logger.debug("uaJsonObj =====>"+uaJsonObj.getUajsonId());
    				tempJson = new JSONObject(uaJsonObj.getJsonStr());
    				uaLRUCache.put(userAgent, tempJson);
    			} // if
    		//} // if
    		}
    		
    		/*String ua_type = null;
    		String os_family = null;
    		String ua_family = null;
		*/
    		if(tempJson==null) {
    			
    			UserAgentStringParser parser = UADetectorServiceFactory.getOnlineUpdatingParser();
    			ReadableUserAgent agent = parser.parse(userAgent);
    			
    			String newJson = "{\"ua_type\":\""+ agent.getType()+"\"," +
    					"\"os_family\":\""+ agent.getOperatingSystem().getFamilyName()+"\"," +
    					"\"ua_family\":\""+agent.getFamily() +"\"}";
    		
    			//logger.debug("uaJsonObj =====>"+newJson);
    			
    			tempJson  = new JSONObject(newJson);
    			UserAgentJSON uaJsonObj = new UserAgentJSON(userAgent, newJson);
	    		userAgentJSONDaoForDML.saveOrUpdate(uaJsonObj);
	    		uaLRUCache.put(userAgent, tempJson);
    			/*
    			byte[] encoded = Base64.encodeBase64(userAgent.getBytes());
    			String uaBase64 = new String(encoded);
    			
    			//uaBase64 = "TW96aWxsYS81LjAgKEFuZHJvaWQ7IE1vYmlsZTsgcnY6MjIuMCkgR2Vja28vMjIuMCBGaXJlZm94LzIyLjA="; //TODO need to remove
    			
    			// logger.info(" uaBase64 is ::"+uaBase64);
    			String requestBody = MessageFormat.format(rawXml, uaBase64, key);  
    			//logger.info(" >>> requestBody is ::"+requestBody);
    			
    			URL url = new URL("http://user-agent-string.info/rpc/rpcxml.php");
    	 			
    	 		HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
    	 			
    	 		urlconnection.setRequestMethod("POST");
    	 		urlconnection.setRequestProperty("Content-Type","application/xml");
    	 		urlconnection.setDoOutput(true);

    	 		OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
    	 		out.write(requestBody);
    	 		out.flush();
    	 		out.close();
    	 			
    	 		BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
    	 		String response = "";
    	 		String decodedString;
    	 		while ((decodedString = in.readLine()) != null) {
    	 			response += decodedString;
    	 		}
    	 		in.close();
    	 		
    	 		//logger.info("Response = "+response);
    	 		response = response.replace("<?xml version=\"1.0\"?>", "").trim();
    	 		
    	 		if(response!=null) {
    	    		// Store in DB and Cache

    	 			tempJson = XML.toJSONObject(response);
    	    		JSONArray memberArrJson = tempJson.getJSONObject("methodResponse").getJSONObject("params").
    	    				getJSONObject("param").getJSONObject("value").getJSONObject("struct").getJSONArray("member");
    	    		
    	    		//logger.info(":: memberJson Arry is ::"+memberArrJson);
    	    		
    	    		response = "{\"ua_type\":\""+ memberArrJson.getJSONObject(1).getJSONObject("value").getString("string")+"\"," +
    	    					"\"os_family\":\""+ memberArrJson.getJSONObject(8).getJSONObject("value").getString("string")+"\"," +
    	    					"\"ua_family\":\""+ memberArrJson.getJSONObject(2).getJSONObject("value").getString("string")+"\"}";
    	    		
    	    		tempJson = new JSONObject(response);
    	 			
    	    		//response = tempJson.toString();
    	 			uaLRUCache.put(userAgent, tempJson);
    	    			
    	    		UserAgentJSON uaJsonObj = new UserAgentJSON(userAgent, response);
    	    		userAgentJSONDao.saveOrUpdate(uaJsonObj);
    	    		//logger.info("Cache is  ::"+uaLRUCache.getAll());
    	 		} // if
    		*/} // if
    		
    		/*if(tempJson==null) {
    			logger.info("Unable to find the JSON obj from UserAgentString URL, Returning...");
    			return;
    		}*/
    		
    		/*if(uasParser==null) uasParser = new CachingOnlineUpdateUASparser();
    		
			UserAgentInfo uai = uasParser.parse(userAgent);*/
    		
    		/*JSONArray memberArrJson = (JSONArray)((JSONObject)((JSONObject)((JSONObject)((JSONObject)((JSONObject)tempJson
    				.get("methodResponse")).get("params")).get("param")).get("value")).get("struct")).get("member");
    		logger.info(":: memberJson Arry is ::"+memberArrJson);
    		*/
			
			String ua_type = tempJson.getString("ua_type");
    		String os_family = tempJson.getString("os_family");
    		String ua_family = tempJson.getString("ua_family");
			
    		
    		/*String ua_type = ag;
    		String os_family = tempJson.getString("os_family");
    		String ua_family = tempJson.getString("ua_family");*/
			//EmailClient clientType = emailClientsMap.get(ua_type);
    		Long clientTypeId = emailClientsMap.get(Constants.UA_TYPE+Constants.DELIMETER_COLON+ua_type);
			
			if(clientTypeId==null) {
				EmailClient newEmailClient = new EmailClient(Constants.UA_TYPE, ua_type,ua_type, userAgent);
				//emailClientDao.saveOrUpdate(newEmailClient);
				emailClientDaoForDML.saveOrUpdate(newEmailClient);
				reloadEmailClientMap();
				clientTypeId = emailClientsMap.get(Constants.UA_TYPE+Constants.DELIMETER_COLON+ua_type);
			}
			
			
			//EmailClient clientOSF = emailClientsMap.get(os_family);
			Long clientOSFId = emailClientsMap.get(Constants.UA_OSFAMILY+Constants.DELIMETER_COLON+os_family);
			
			if(clientOSFId==null) {
				EmailClient newEmailClient = new EmailClient(Constants.UA_OSFAMILY, os_family,os_family, userAgent);
				//emailClientDao.saveOrUpdate(newEmailClient);
				emailClientDaoForDML.saveOrUpdate(newEmailClient);
				reloadEmailClientMap();
				clientOSFId = emailClientsMap.get(Constants.UA_OSFAMILY+Constants.DELIMETER_COLON+os_family);
			}
			
			
			//EmailClient clientUAF = emailClientsMap.get(ua_family);
			Long clientUAFId = emailClientsMap.get(Constants.UA_UAFAMILY+Constants.DELIMETER_COLON+ua_family);
			
			if(clientUAFId == null) {
				EmailClient newEmailClient = new EmailClient(Constants.UA_UAFAMILY, ua_family,ua_family, userAgent);
				//emailClientDao.saveOrUpdate(newEmailClient);
				emailClientDaoForDML.saveOrUpdate(newEmailClient);
				reloadEmailClientMap();
				clientUAFId = emailClientsMap.get(Constants.UA_UAFAMILY+Constants.DELIMETER_COLON+ua_family);
			}
			
			
			// logger.info("MAP=="+emailClientsMap);
			
			opens.setEmailClient(clientTypeId);
			opens.setOsFamily(clientOSFId);
			opens.setUaFamily(clientUAFId);
			
			
		}
    	catch (Exception e) {
			logger.error("Exception ::" , e);
		}
    
	}
	
	  
	private void reloadEmailClientMap() {
		try {
			List<EmailClient> emailClientList = emailClientDao.findAll();
        	for (EmailClient emailClient : emailClientList) {
        		emailClientsMap.put(emailClient.getEmailClient()+Constants.DELIMETER_COLON+emailClient.getUserAgent(), emailClient.getEmailClientId());
        	} // for
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
	} // reloadEmailClientMap
	  
	
	/*public EmailClient emailClientCheck(String toEmailId){
		EmailClient client = null;
		
		if(toEmailId!=null) {
			
			String domain = toEmailId.substring(toEmailId.indexOf('@')+1, toEmailId.indexOf('.',toEmailId.indexOf('@')));
			
			for (EmailClient emailClient : emailClientList) {
				if(emailClient.getEmailClient().toLowerCase().contains(domain.toLowerCase())) {
					return emailClient;
				}
				else if(emailClient.getEmailClient().equals("Others")) {
					client = emailClient;
				}
			} // for
		} // if toEmailId
		
		return client;
		
	}*/
	
	
	
	
	
}
