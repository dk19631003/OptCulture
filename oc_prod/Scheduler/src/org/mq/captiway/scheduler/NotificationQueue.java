package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationSchedule;
import org.mq.captiway.scheduler.utility.Constants;

public class NotificationQueue {
	
	private PriorityQueue<Object> queue;
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public NotificationQueue(){
		queue = new PriorityQueue<Object>();
	}
	
	
	
	public synchronized void addObjToQueue(Object obj) {
		
		queue.offer(obj);
	}
	
	public synchronized void addCollection(Collection<?> collection) {	
		for (Object object : collection) {
	
			if(isObjectExistInQue(object)) continue; // Should not add if already exist in Queue.
			
			if(queue.contains(object)) continue;
			
			addObjToQueue(object);
			if(logger.isInfoEnabled()) logger.info("Added an Object into QUEUE ::"+object +" :: QUEUE Size="+queue.size());
		}
	}
	
	private boolean isObjectExistInQue(Object inObj) {
		
		try {
			if(isObjectMatch(inObj, NotificationCampSubmitter.getCurrentRunningObj())) {
				if(logger.isInfoEnabled()) logger.info("Same Object is under running... So returing...");
				return true;
			}
			
			// Check the object with the Queue objects
			
			Iterator<Object> queIterator = queue.iterator();

			Object eachObj;
			
			while(queIterator.hasNext()) {
			
				eachObj = queIterator.next();
				
				if(isObjectMatch(inObj, eachObj)) return true;
				
			} // while
			
			return false;
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return true;
		}
	} // isObjectExistInQue
	
	
	private boolean isObjectMatch(Object inObj, Object eachObj) {
		
		try {
			
			if(inObj instanceof NotificationSchedule && eachObj instanceof NotificationSchedule) {
				
				NotificationSchedule inCampSh = (NotificationSchedule)inObj;
				NotificationSchedule eachCampSh = (NotificationSchedule)eachObj;
				
				if((inCampSh.getNotificationId().longValue()== eachCampSh.getNotificationId().longValue())) {
					return true;
				}
			}
			
			return false;
			
		} catch (Exception e) {
			logger.error("Error ** ",e);
			return true;
		}
	}
	
	public synchronized Object getObjFromQueue() {
		
		return queue.poll();
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
	public synchronized void removeObjectFromQueue(Object object) {
		
		queue.remove(object);
		
	}
	
	public boolean containsObj(Object object) {
		boolean isContain = false;
		
		List<Long> scheduleIdList = new ArrayList<Long>();
		List<Object> objList = new ArrayList<Object>();
		objList.addAll(queue);
		
		for (Object obj : objList) {
			if(obj instanceof NotificationSchedule)
				scheduleIdList.add(((NotificationSchedule)obj).getNotificationCsId());
		}//for
		
		if(object instanceof NotificationSchedule) {
			isContain = scheduleIdList.contains(((NotificationSchedule)object).getNotificationCsId());
		}
		else 
			isContain = false;
		
		return isContain;
	}
}
