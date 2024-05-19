package org.mq.captiway.scheduler;

import java.util.Collection;
import java.util.PriorityQueue;

public class ProgramQueue {
	
	private PriorityQueue<Object> queue;
	public ProgramQueue(){
		queue = new PriorityQueue<Object>();
	}
	
	public synchronized void addObjToQueue(Object obj) {
		queue.offer(obj);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void addCollection(Collection collection) {
		for (Object object : collection) {
			
			if(queue.contains(object)) continue;
			addObjToQueue(object);
			
		}
	}
	
	public synchronized Object getObjFromQueue() {
		
		return queue.peek();
	}
	
	public synchronized int getQueueSize() {
		return queue.size();
	}
	
	
	public synchronized void removeObjFromQueue(Object object) {
		
		queue.remove(object);
		
	}
	
	
}
