package org.mq.captiway.scheduler.services;

import java.util.List;
import java.util.Observable;

import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.utility.Constants;

public class EventTriggerEventsObservable extends Observable  {

	private EventTriggerEventsObserver eventTriggerEventsObserver;
	
	private EventTriggerDao eventTriggerDao;
	
	
	public EventTriggerDao getEventTriggerDao() {
		return eventTriggerDao;
	}


	public void setEventTriggerDao(EventTriggerDao eventTriggerDao) {
		this.eventTriggerDao = eventTriggerDao;
	}

	
	/*public SalesObservable() {
		
		this.addObserver(eventTriggerObserver);
		
		
	}*/
	
	public EventTriggerEventsObserver getEventTriggerEventsObserver() {
		return eventTriggerEventsObserver;
	}




	public void setEventTriggerEventsObserver(
			EventTriggerEventsObserver eventTriggerEventsObserver) {
		this.eventTriggerEventsObserver = eventTriggerEventsObserver;
	}




	public void notifyToObserver(List<EventTrigger> etList, Long startSalesId, Long endSalesId, Long userId, String category ) {
		
		Object[] inputArray = new Object[] {etList, startSalesId, endSalesId, userId, category};
		
		setChanged();
		notifyObservers(inputArray);
		
		
	}
	
	public void notifyForWebEvents(Long userId, Long startId, Long endId) {
		
		
		
		List<EventTrigger> retList = eventTriggerDao.findAllUserAutoRespondTriggers(userId,
										Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED);
		
		if( retList == null) return ;
		
		if(startId == null || endId == null) return;
		notifyToObserver(retList, startId, endId, userId, Constants.POS_MAPPING_TYPE_CONTACTS);
		
		
		
	}
	
	
}
