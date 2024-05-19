package org.mq.captiway.scheduler.campaign;

import java.util.PriorityQueue;

public class CampaignReportEventsQueue {
	
	private PriorityQueue<String> queue;
	
	public CampaignReportEventsQueue() {
		
		queue = new PriorityQueue<String>();
	}
	
	public synchronized void addObjToQueue(String reqUrlStr) {
		//if(logger.isDebugEnabled()) logger.debug("adding the URL ::"+reqUrlStr);
		queue.offer(reqUrlStr);
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
public synchronized String getObjFromQueue() {
		
		return queue.poll();
	}

	
	
	

	
	
	

}
