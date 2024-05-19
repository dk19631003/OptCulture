package org.mq.captiway.scheduler;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.utility.Constants;

public class PMTAQueue {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private PriorityQueue<Object> queue;
	public PMTAQueue(){
		queue = new PriorityQueue<Object>();
	}
	
	private synchronized void addObjToQueue(Object obj) {
		
		queue.offer(obj);
	}
	
	private boolean isObjectExistInQue(Object inObj) {
		
		try {
			// Check for the current running object
			if(isObjectMatch(inObj, PmtaMailmergeSubmitter.getCurrentRunningObj())) {
				if(logger.isInfoEnabled()) logger.info("Same Object is under running... So returing...");
				return true;
			}
			
			// Check the object with the Queue objects
			
			Iterator<Object> queIterator = queue.iterator();

			Object eachObj;
			
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
			
			if(inObj instanceof CampaignSchedule && eachObj instanceof CampaignSchedule) {
				
				CampaignSchedule inCampSh = (CampaignSchedule)inObj;
				CampaignSchedule eachCampSh = (CampaignSchedule)eachObj;
				
				//logger.debug("-------------------- 1   ------------------"+inCampSh.getCampaignId()+"=="+ eachCampSh.getCampaignId());
				if((inCampSh.getCampaignId().longValue()== eachCampSh.getCampaignId().longValue())) {
					return true;
				}
			}
			else if(inObj instanceof MailingList && eachObj instanceof MailingList) {
				
				MailingList inMlList = (MailingList)inObj;
				MailingList eachMlList = (MailingList)eachObj;
				//logger.debug("-------------------- 2 ------------------"+inMlList.getListId() +"=="+ eachMlList.getListId());
				if((inMlList.getListId().longValue() == eachMlList.getListId().longValue())) {
					return true;
				}
			}
			else if(inObj instanceof EventTrigger && eachObj instanceof EventTrigger) {
				
				EventTrigger inEvtTrigger = (EventTrigger)inObj;
				EventTrigger eachEvtTrigger = (EventTrigger)eachObj;
				//logger.debug("-------------------- 3 ------------------"+inEvtTrigger.getCampaignId()+"=="+ eachEvtTrigger.getCampaignId());
				if((inEvtTrigger.getId().longValue()== eachEvtTrigger.getId().longValue())) {
					return true;
				}
				/*if(inEvtTrigger.getCampaignId() != null && eachEvtTrigger.getCampaignId() != null)
				if((inEvtTrigger.getCampaignId().longValue()== eachEvtTrigger.getCampaignId().longValue())) {
					return true;
				}*/
			}
			else if(inObj instanceof AutoProgramComponents && eachObj instanceof AutoProgramComponents) {
				
				AutoProgramComponents inProgComponent = (AutoProgramComponents)inObj;
				AutoProgramComponents eachProgComponent = (AutoProgramComponents)eachObj;
				//logger.debug("-------------------- 4 ------------------"+inProgComponent.getCompId() +"=="+ eachProgComponent.getCompId());
				if((inProgComponent.getCompId().longValue() == eachProgComponent.getCompId().longValue() )) {
					return true;
				}
			}
			
			// else { 	logger.error("Error ** New Object Found, (Add to PMTA Queue is Ignored) : "+inObj); }
			
			return false;
			
		} catch (Exception e) {
			logger.error("Error ** ",e);
			return true;
		}
	}
	

	public synchronized void addCollection(Collection collection) {
		
		for (Object object : collection) {

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
