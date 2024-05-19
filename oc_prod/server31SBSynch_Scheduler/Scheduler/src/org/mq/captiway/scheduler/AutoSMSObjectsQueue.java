package org.mq.captiway.scheduler;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.utility.Constants;

public class AutoSMSObjectsQueue {

	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private PriorityQueue<AutoSmsQueue> queue;
	public AutoSMSObjectsQueue(){
		queue = new PriorityQueue<AutoSmsQueue>();
	}
	
	
	private synchronized void addObjToQueue(AutoSmsQueue obj) {
		
		queue.offer(obj);
	}
	
	private boolean isObjectExistInQue(AutoSmsQueue inObj) {
		
		try {
			// Check for the current running object
			if(isObjectMatch(inObj, AutoSmsSender.getCurrentRunningObj())) {
				if(logger.isInfoEnabled()) logger.info("Same Object is under running... So returing...");
				return true;
			}
			
			// Check the object with the Queue objects
			
			Iterator<AutoSmsQueue> queIterator = queue.iterator();

			AutoSmsQueue eachObj;
			
			while(queIterator.hasNext()) {
			
				eachObj = queIterator.next();
				
				//logger.debug("eachObj22="+eachObj+" : "+eachObj.getClass()+" , inObj="+inObj+" : "+inObj.getClass());
				
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
			
			if(inObj instanceof AutoSmsQueue && eachObj instanceof AutoSmsQueue) {
				
				AutoSmsQueue inAutoSMS = (AutoSmsQueue)inObj;
				AutoSmsQueue eachAutoSMS = (AutoSmsQueue)eachObj;
				
				if((inAutoSMS.getId() == eachAutoSMS.getId())) {
					return true;
				}
			}
			
			return false;
			
		} catch (Exception e) {
			logger.error("Error ** ",e);
			return true;
		}
	}
	

	public synchronized void addCollection(Collection<AutoSmsQueue> collection) {
		
		for (AutoSmsQueue object : collection) {

			if(isObjectExistInQue(object)) continue; // Should not add if already exist in Queue.
			
			if(queue.contains(object)) continue;
			
			addObjToQueue(object);
			if(logger.isInfoEnabled()) logger.info("Added an Object into QUEUE ::"+object +" :: QUEUE Size="+queue.size());
		} // for
		if(logger.isInfoEnabled()) logger.info(" :: Finally QUEUE Size is ="+queue.size());
	}
	
	
	
	public synchronized Object getObjFromQueue() {
		
		return queue.poll();
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
	
	

}
