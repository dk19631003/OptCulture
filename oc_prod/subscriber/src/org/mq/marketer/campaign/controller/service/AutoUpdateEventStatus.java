package org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.EventsDao;
import org.mq.optculture.data.dao.EventsDaoForDML;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.utils.OCConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutoUpdateEventStatus extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	EventsDao eventDao;
	EventsDaoForDML eventsDaoForDML;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			eventDao = (EventsDao) context.getBean(OCConstants.EVENTS_DAO);
			eventsDaoForDML = (EventsDaoForDML) context.getBean(OCConstants.EVENTS_DAO_ForDML);
			int totalEventsCount = eventDao.getAllEventsCount();
			logger.info("totalEventsCount is :: "+totalEventsCount);
			if( totalEventsCount == 0){
				logger.info(">>> No  Events exists for Upadting the Status");
				return;
			}
			int i = 0;
			while(i <= totalEventsCount) {
				int k =100;
				autoUpdateEventsStatus(i, k);
				
				if(i == 0) i = k;	else  i= i+k;
				
				
			}
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}

	private void autoUpdateEventsStatus(int i, int k) {
		// TODO Auto-generated method stub
		List<Events> list =  eventDao.getAllEvents(i,k);
		if(list == null || list.size() == 0 ) {
			logger.info(">>> No  Events exists for Upadting the Status");
			return;
		}
		List<Events> modifyEventStausLst = new ArrayList<Events>();
		Calendar currntDateCal = Calendar.getInstance();
		
		for(Events eventObj : list) {
			if(eventObj.getEventStatus().equals(Constants.EVENTS_STATUS_ACTIVE)) {			// CheckIf Event Status Active
				
				if(currntDateCal.after(eventObj.getEventStartDate()) && currntDateCal.before(eventObj.getEventEndDate())) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_RUNNING);
					modifyEventStausLst.add(eventObj);
				}
				else if(currntDateCal.after(eventObj.getEventEndDate())) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyEventStausLst.add(eventObj);
				}
				
				
			}
			else  if(eventObj.getEventStatus().equals(Constants.EVENTS_STATUS_RUNNING)) {	// CheckIf Event Status Running
				
				if(currntDateCal.before(eventObj.getEventStartDate())) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_ACTIVE);
					modifyEventStausLst.add(eventObj);
				}
				else if(currntDateCal.after(eventObj.getEventEndDate())) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyEventStausLst.add(eventObj);
				}
				
			} else  if(eventObj.getEventStatus().equals(Constants.EVENTS_STATUS_SUSPENDED)) {	// CheckIf Event Status Suspende
				
				if(currntDateCal.after(eventObj.getEventEndDate())) {

					eventObj.setEventStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyEventStausLst.add(eventObj);
				}
				
				
			} else if(eventObj.getEventStatus().equals(Constants.EVENTS_STATUS_EXPIRED)) {	// CheckIf Event Status Expired
				
				if(currntDateCal.before(eventObj.getEventStartDate()) ) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_ACTIVE);
					modifyEventStausLst.add(eventObj);
				}
				else if(currntDateCal.before(eventObj.getEventEndDate())) {
					
					eventObj.setEventStatus(Constants.EVENTS_STATUS_RUNNING);
					modifyEventStausLst.add(eventObj);
				}
				
			}
			
			
			if(modifyEventStausLst.size() == 100) {
				logger.info("for Every 100 coup Obj status updated");
				//couponsDao.saveByCollection(modifyCoupStausLst);
				eventsDaoForDML.saveByCollection(modifyEventStausLst);

				modifyEventStausLst.clear();
			}
			
			
		} // for
		
		
		if(modifyEventStausLst.size() > 0) {
			logger.info(" finally :: "+modifyEventStausLst.size()+" ::   Promo-code Obj status updated");
			//couponsDao.saveByCollection(modifyCoupStausLst);
			eventsDaoForDML.saveByCollection(modifyEventStausLst);
			modifyEventStausLst.clear();
		}

			
		}

	}
	
	
	

