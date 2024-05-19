package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.EventTriggerEvents;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.EventTriggerEventsDao;
import org.mq.captiway.scheduler.dao.EventTriggerEventsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.TriggerQueryGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EventTriggerScheduler extends TimerTask  implements ApplicationContextAware {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
	
		this.applicationContext = applicationContext;
		
	}
	
	private EventTriggerDao eventTriggerDao;
	private PMTAQueue pmtaQueue ;
	private SMSQueue smsQueue;
	private EventTriggerEventsDao eventTriggerEventsDao;
	private EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(eventTriggerDao == null || pmtaQueue == null || smsQueue == null) {
			
			eventTriggerDao = (EventTriggerDao)applicationContext.getBean("eventTriggerDao");
			pmtaQueue = (PMTAQueue)applicationContext.getBean("pmtaQueue");
			smsQueue = (SMSQueue)applicationContext.getBean("smsQueue");
			eventTriggerEventsDao = (EventTriggerEventsDao)applicationContext.getBean("eventTriggerEventsDao");
			eventTriggerEventsDaoForDML = (EventTriggerEventsDaoForDML)applicationContext.getBean("eventTriggerEventsDaoForDML");
			
		}//if
		
		MyCalendar myCal = new MyCalendar();
		
		List<EventTrigger> activeTriggerList = eventTriggerDao.findAllActiveTriggerList(myCal.toString(), false);
		if(activeTriggerList == null) {
			
			logger.info("no Active triggers found");
			return;
		}
		
		logger.info("got an active trigger >>>>>>>>>"+activeTriggerList.size());
		
		List<EventTrigger> activeTriggersListOfActionEmail = new ArrayList<EventTrigger>();
		List<EventTrigger> activeTriggersListOfActionSMS = new ArrayList<EventTrigger>();
		int size = 500;
		for (EventTrigger eventTrigger : activeTriggerList) {
			int totalRunningCount = 0;
			try {
				
				if( (eventTrigger.getTrType().intValue() & Constants.ET_TYPE_ON_CONTACT_DATE ) > 0) continue;
				
				
				Users etOwner = eventTrigger.getUsers();
				if(etOwner == null || etOwner.getClientTimeZone() == null) {
					
					logger.debug("No user / time zone found for user "+etOwner.getUserId()+ " For trigger "+eventTrigger.getTriggerName());
					continue;
					
				}
				
				//String etOwnerTimeZone = etOwner.getClientTimeZone();
				
				String countQry = " SELECT Distinct event_id FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
							" AND event_trigger_id="+eventTrigger.getId().longValue()+
							TriggerQueryGenerator.getTriggerTimeDiffCond(eventTrigger, "event_time", false)+
							" AND (email_status="+Constants.ET_EMAIL_STATUS_ACTIVE+" OR "+"sms_status="+Constants.ET_SMS_STATUS_ACTIVE+")" ;
				
				int count = eventTriggerEventsDao.findEventsCount(countQry);
				
				
				logger.debug("count qry ::"+countQry);
				//if(count == 0) continue;//TODO
				
				//if(count > 0)  {
				
				String qry = " SELECT * FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
							" AND event_trigger_id="+eventTrigger.getId().longValue()+
							TriggerQueryGenerator.getTriggerTimeDiffCond(eventTrigger, "event_time", false)+
							" AND (email_status="+Constants.ET_EMAIL_STATUS_ACTIVE+" OR "+"sms_status="+Constants.ET_SMS_STATUS_ACTIVE+")" ;
				
				logger.debug(" qry ::"+qry);
				
				List<EventTriggerEvents> eventsList = null;
				List<EventTriggerEvents> activeEventsList = new ArrayList<EventTriggerEvents>();
				List<EventTriggerEvents> pausedEventsList = new ArrayList<EventTriggerEvents>();
				
				
				
				for(int i=0;i < count; i+=size) {//as this is about the count,events may add at times
					
					//TODO need to avoid RowMapper
					eventsList = eventTriggerEventsDao.findAllActiveEvents(qry, i, size);
					
					if(eventsList == null) continue;//need to reassign the size and count values					
					
					
					for (EventTriggerEvents eventTriggerEvents : eventsList) {
						
						byte eventEmailStatus = eventTriggerEvents.getEmailStatus();
						byte eventSmsStatus = eventTriggerEvents.getSmsStatus();
						String daysFlag =  eventTrigger.getTargetDaysFlag();
						//TODO
						if( daysFlag != null) {
							
							Calendar currentDate = Calendar.getInstance();
							
							
							//to convert into client time zone
							String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
							int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
							String timezoneDiffrenceMinutes = etOwner.getClientTimeZone();
							int timezoneDiffrenceMinutesInt = 0;
							
							if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
							
							timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
							
							//boolean isMinus = (timezoneDiffrenceMinutesInt < 0);
							
							currentDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							/*if(isMinus) {
								
								
								
								
							}else{
								
								timezoneDiffrenceMinutesInt = Math.abs(timezoneDiffrenceMinutesInt);
								
								currentDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								
								
								
							}*/
							
							int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
							
							if(daysFlag.contains(dayOfWeek+Constants.STRING_NILL)) {
								
								eventTriggerEvents.setEmailStatus( eventEmailStatus  == Constants.ET_EMAIL_STATUS_ACTIVE ? Constants.ET_EMAIL_STATUS_RUNNING : eventEmailStatus );
								
								
								eventTriggerEvents.setSmsStatus( eventSmsStatus  == Constants.ET_SMS_STATUS_ACTIVE ? Constants.ET_SMS_STATUS_RUNNING : eventSmsStatus );
								
								activeEventsList.add(eventTriggerEvents);
								totalRunningCount++;
							}
							
							else{
								
								eventTriggerEvents.setEmailStatus( eventEmailStatus == Constants.ET_EMAIL_STATUS_ACTIVE ? Constants.ET_EMAIL_STATUS_PAUSED: eventEmailStatus );
								
								
								eventTriggerEvents.setSmsStatus( eventSmsStatus == Constants.ET_SMS_STATUS_ACTIVE ? Constants.ET_SMS_STATUS_PAUSED : eventSmsStatus );
								
								pausedEventsList.add(eventTriggerEvents);
								
							}
							
						}else{
							
							eventTriggerEvents.setEmailStatus( eventEmailStatus  == Constants.ET_EMAIL_STATUS_ACTIVE ? Constants.ET_EMAIL_STATUS_RUNNING : eventEmailStatus );
							
							
							eventTriggerEvents.setSmsStatus( eventSmsStatus  == Constants.ET_SMS_STATUS_ACTIVE ? Constants.ET_SMS_STATUS_RUNNING : eventSmsStatus );
							
							activeEventsList.add(eventTriggerEvents);
							totalRunningCount++;
							
						}
						
						
						
					}//for
					
					if(activeEventsList.size() > 0) {
						
						//eventTriggerEventsDao.saveByCollection(activeEventsList);
						eventTriggerEventsDaoForDML.saveByCollection(activeEventsList);
						activeEventsList.clear();
					}
					if(pausedEventsList.size() > 0) {
						
						//eventTriggerEventsDao.saveByCollection(pausedEventsList);
						eventTriggerEventsDaoForDML.saveByCollection(pausedEventsList);
						pausedEventsList.clear();
						
					}
					
					eventsList=null;
					
					
				}//end for total count
				
				
				//check for the paused events which are matched for the target date condition
				if(eventTrigger.getTargetDaysFlag() != null) {
					
					String pausedCountQry = " SELECT Distinct event_id FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
								" AND event_trigger_id="+eventTrigger.getId().longValue()+
								" AND (email_status="+Constants.ET_EMAIL_STATUS_PAUSED+" OR "+"sms_status="+Constants.ET_SMS_STATUS_PAUSED+")" ;
					
					int pausedCount = eventTriggerEventsDao.findEventsCount(pausedCountQry);
					
					logger.debug("paused count qry ::"+pausedCountQry +" COUNT ::"+pausedCount);
					
					if(pausedCount > 0) {
						
						eventsList = null;
						activeEventsList = new ArrayList<EventTriggerEvents>();
						
						String pausedQry = " SELECT * FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
											" AND event_trigger_id="+eventTrigger.getId().longValue()+
											" AND (email_status="+Constants.ET_EMAIL_STATUS_PAUSED+" OR "+
											" sms_status="+Constants.ET_EMAIL_STATUS_PAUSED+")" ;
								
						
						for(int i=0;i < pausedCount; i+=size) {//as this is about the count,events may add at times
							
							//TODO need to avoid RowMapper
							eventsList = eventTriggerEventsDao.findAllActiveEvents(pausedQry, i, size);
							

							if(eventsList == null) continue;//need to reassign the size and count values					
							
							for (EventTriggerEvents eventTriggerEvents : eventsList) {
								
								byte eventEmailStatus = eventTriggerEvents.getEmailStatus();
								byte eventSmsStatus = eventTriggerEvents.getSmsStatus();
								String daysFlag =  eventTrigger.getTargetDaysFlag();
							
								Calendar currentDate = Calendar.getInstance();
								
								
								//to convert into client time zone
								String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
								int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
								String timezoneDiffrenceMinutes = etOwner.getClientTimeZone();
								int timezoneDiffrenceMinutesInt = 0;
								
								if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
								
								timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
								
								//boolean isMinus = (timezoneDiffrenceMinutesInt < 0);
								
								currentDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
								
								if(daysFlag.contains(dayOfWeek+Constants.STRING_NILL)) {
									
									logger.debug("days flag of client ::"+dayOfWeek+" flag ::"+daysFlag);
									
									eventTriggerEvents.setEmailStatus( eventEmailStatus == Constants.ET_EMAIL_STATUS_PAUSED ? Constants.ET_EMAIL_STATUS_RUNNING : eventEmailStatus );
									
									
									eventTriggerEvents.setSmsStatus( eventSmsStatus  == Constants.ET_SMS_STATUS_PAUSED  ? Constants.ET_SMS_STATUS_RUNNING : eventSmsStatus );
									
									activeEventsList.add(eventTriggerEvents);
									totalRunningCount++;
									
								}//if
							
							}//for
							
							if(activeEventsList.size() > 0) {
								
								//eventTriggerEventsDao.saveByCollection(activeEventsList);
								eventTriggerEventsDaoForDML.saveByCollection(activeEventsList);
								activeEventsList.clear();
								
							}//if
							
							eventsList = null;
							
						}//for
						
						
					}//only if paused events exist
					
					
				}//if
				
			//}
				
				int triggerOptionsFlag = eventTrigger.getOptionsFlag();
				
				//logger.info("got an Events >>>>>>>>>"+count);
				if(totalRunningCount == 0) {
					
					/*String runningCountQry = " SELECT Distinct event_id FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
					" AND event_trigger_id="+eventTrigger.getId().longValue()+
					TriggerQueryGenerator.getTriggerTimeDiffCond(eventTrigger, "event_time", false)+
					" AND (email_status="+Constants.ET_EMAIL_STATUS_RUNNING+" OR "+"sms_status="+Constants.ET_SMS_STATUS_RUNNING+")" ;
		*/
			
					//running is eligible for sending so no need to consider the running time here
					String runningCountQry = " SELECT Distinct event_id FROM event_trigger_events WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
					" AND event_trigger_id="+eventTrigger.getId().longValue()+					
					" AND (email_status="+Constants.ET_EMAIL_STATUS_RUNNING+" OR "+"sms_status="+Constants.ET_SMS_STATUS_RUNNING+")" ;
		
					
					logger.info("runningCountQry >>>>"+runningCountQry);
					
					totalRunningCount = eventTriggerEventsDao.findEventsCount(runningCountQry);
					
					
				}
				
				//logger.info("total running count ::"+totalRunningCount);
				
				if(totalRunningCount > 0) {
					if( (triggerOptionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG) == Constants.ET_SEND_CAMPAIGN_FLAG) {
						
						activeTriggersListOfActionEmail.add(eventTrigger);
						
					}//if
					if( (triggerOptionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG ) {
						
						activeTriggersListOfActionSMS.add(eventTrigger);
					}
					
				}//if
				
				
				/*String updateQry = " UPDATE event_trigger_events SET IF(email_status=1,"+Constants.ET_EMAIL_STATUS_PAUSED+", email_status)," +
									" IF(sms_status=1,"+Constants.ET_SMS_STATUS_PAUSED+", sms_status) WHERE user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
									" AND event_trigger_id="+eventTrigger.getId().longValue()+
									TriggerQueryGenerator.getTriggerTimeDiffPausedCond(eventTrigger, "event_time", false)+
									" AND (email_status="+Constants.ET_EMAIL_STATUS_ACTIVE+" OR "+"sms_status="+Constants.ET_SMS_STATUS_ACTIVE+")" ;
				*/
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::",e);
				continue;
			}
			
			
		}//for
		if(activeTriggersListOfActionEmail.size() > 0) {
			
			pmtaQueue.addCollection(activeTriggersListOfActionEmail);
			if(pmtaQueue.getQueueSize() > 0 ) {
				
				PmtaMailmergeSubmitter.startPmtaMailmergeSubmitter(applicationContext);
				
			}//if
			
			
		}
		
		if(activeTriggersListOfActionSMS.size() > 0) {
			
			smsQueue.addCollection(activeTriggersListOfActionSMS);
			if(smsQueue.getQueueSize() > 0) {
				
				SMSCampaignSubmitter.startSMSCampaignSubmitter(applicationContext);
				
			}//if
		}
		
	
		
		
		
		
	}//run
	

	public static void main(String args[]) {
		
		Calendar mycal = Calendar.getInstance();
		
		logger.info(mycal.get(Calendar.DAY_OF_WEEK));
		
	}
	
	
	
}
