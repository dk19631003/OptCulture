package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.utility.Constants;

public class SMSQueue {
	
	private PriorityQueue<Object> queue;
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public SMSQueue(){
		queue = new PriorityQueue<Object>();
	}
	
	
	
	public synchronized void addObjToQueue(Object obj) {
		/*List<Object> objList = new ArrayList<Object>();
		objList.addAll(queue);
		AutoProgramComponents tempComp = null;
		AutoProgramComponents compInQueue = null;
		for (Object object : objList) {
			
			
			if(obj instanceof AutoProgramComponents && object instanceof AutoProgramComponents) {
				compInQueue = (AutoProgramComponents)object;
				tempComp = (AutoProgramComponents)obj;
				
				if()
				
			}else if(obj instanceof SMSCampaignSchedule && object instanceof SMSCampaignSchedule) {
				
				
				
			}
			
		}*/
		/*if(queue.size() > 0) {
			if(containsObj(obj)) return;
		}*/
		
		queue.offer(obj);
	}
	
	/*@SuppressWarnings("unchecked")
	public synchronized void addCollection(Collection collection) {
		//queue.addAll(collection);
		
		for (Object object : collection) {
			
			addObjToQueue(object);
				
		}//for
		
		
	}//addCollection
	*/
	
	@SuppressWarnings("unchecked")
	public synchronized void addCollection(Collection collection) {	
	
	for (Object object : collection) {

		if(isObjectExistInQue(object)) continue; // Should not add if already exist in Queue.
		
		if(queue.contains(object)) continue;
		
		addObjToQueue(object);
		if(logger.isInfoEnabled()) logger.info("Added an Object into QUEUE ::"+object +" :: QUEUE Size="+queue.size());
	} // for
	
	}
	
private boolean isObjectExistInQue(Object inObj) {
		
		try {
			// Check for the current running object
			if(isObjectMatch(inObj, SMSCampaignSubmitter.getCurrentRunningObj())) {
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
		
		if(inObj instanceof SMSCampaignSchedule && eachObj instanceof SMSCampaignSchedule) {
			
			SMSCampaignSchedule inCampSh = (SMSCampaignSchedule)inObj;
			SMSCampaignSchedule eachCampSh = (SMSCampaignSchedule)eachObj;
			
			//logger.debug("-------------------- 1   ------------------"+inCampSh.getSmsCampaignId()+"=="+ eachCampSh.getSmsCampaignId());
			if((inCampSh.getSmsCampaignId().longValue()== eachCampSh.getSmsCampaignId().longValue())) {
				return true;
			}
		}
		
		else if(inObj instanceof EventTrigger && eachObj instanceof EventTrigger) {
			
			EventTrigger inEvtTrigger = (EventTrigger)inObj;
			EventTrigger eachEvtTrigger = (EventTrigger)eachObj;
			//logger.debug("-------------------- 3 ------------------"+inEvtTrigger.getCampaignId()+"=="+ eachEvtTrigger.getCampaignId());
			/*if((inEvtTrigger.getCampaignId().longValue() == eachEvtTrigger.getCampaignId().longValue())) {
				return true;
			}*/
			if((inEvtTrigger.getId().longValue() == eachEvtTrigger.getId().longValue())) {
				return true;
			}
			
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
		
		List<Long> compIdList = new ArrayList<Long>();
		List<Long> scheduleIdList = new ArrayList<Long>();
		List<Long> triggerList = new ArrayList<Long>();
		List<Object> objList = new ArrayList<Object>();
		objList.addAll(queue);
		
		for (Object obj : objList) {
			if(object instanceof AutoProgramComponents)
				compIdList.add(((AutoProgramComponents)obj).getCompId());
			else if(obj instanceof SMSCampaignSchedule)
				scheduleIdList.add(((SMSCampaignSchedule)obj).getSmsCsId());
			else if(obj instanceof EventTrigger)
				triggerList.add(( (EventTrigger)obj ).getId() );
				
			
		}//for
		
		if(object instanceof AutoProgramComponents) {
			
			isContain = compIdList.contains(((AutoProgramComponents)object).getCompId());
			
		}
		else if(object instanceof SMSCampaignSchedule) {
			
			isContain = scheduleIdList.contains(((SMSCampaignSchedule)object).getSmsCsId());
		}
		else if(object instanceof EventTrigger) {
			isContain = triggerList.contains(((EventTrigger)object).getId());
		}
		else 
			isContain = false;
		
		return isContain;
		
		
	}
}
