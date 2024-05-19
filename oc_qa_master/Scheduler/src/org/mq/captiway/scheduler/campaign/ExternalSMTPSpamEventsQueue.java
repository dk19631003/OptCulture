package org.mq.captiway.scheduler.campaign;

import java.util.PriorityQueue;

import org.mq.captiway.scheduler.beans.CampaignSent;

public class ExternalSMTPSpamEventsQueue {
	
	private PriorityQueue<String> queue;
	public ExternalSMTPSpamEventsQueue() {
		
		queue = new PriorityQueue<String>();
	}
	
	public synchronized void addObjToQueue(String emailId) {
		//logger.debug("adding the URL ::"+reqUrlStr);
		queue.offer(emailId);
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
public synchronized String getObjFromQueue() {
		
		return queue.poll();
	}

}
