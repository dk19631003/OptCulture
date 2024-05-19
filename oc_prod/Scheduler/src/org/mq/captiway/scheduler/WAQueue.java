package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignsSchedule;
import org.mq.captiway.scheduler.utility.Constants;

public class WAQueue {

	private PriorityQueue<Object> queue;

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public WAQueue(){
		queue = new PriorityQueue<Object>();
	}

	public synchronized void addObjToQueue(Object obj) {		
		queue.offer(obj);
	}

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
			if(isObjectMatch(inObj, WACampaignSubmitter.getCurrentRunningObj())) {
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

			if(inObj instanceof WACampaignsSchedule && eachObj instanceof WACampaignsSchedule) {

				WACampaignsSchedule inCampSh = (WACampaignsSchedule)inObj;
				WACampaignsSchedule eachCampSh = (WACampaignsSchedule)eachObj;

				if((inCampSh.getWaCampaignId().longValue()== eachCampSh.getWaCampaignId().longValue())) {
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
			if(obj instanceof WACampaignsSchedule)
				scheduleIdList.add(((WACampaignsSchedule)obj).getWaCsId());

		}//for

		if(object instanceof WACampaignsSchedule) {

			isContain = scheduleIdList.contains(((WACampaignsSchedule)object).getWaCsId());
		}else 
			isContain = false;

		return isContain;


	}
}
