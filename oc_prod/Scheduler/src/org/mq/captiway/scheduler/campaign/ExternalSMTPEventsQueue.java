package org.mq.captiway.scheduler.campaign;

import java.util.PriorityQueue;

import org.json.simple.JSONArray;

public class ExternalSMTPEventsQueue {

	
	private PriorityQueue<String> queue;
	public ExternalSMTPEventsQueue() {
		
		queue = new PriorityQueue<String>();
	}
	
	public synchronized void addObjToQueue(String jsonRootObjectStr) {
		//logger.debug("adding the URL ::"+reqUrlStr);
		queue.offer(jsonRootObjectStr);
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
public synchronized String getObjFromQueue() {
		
		return queue.poll();
	}

	
	
	
}//ExternalSMTPEventsQueue