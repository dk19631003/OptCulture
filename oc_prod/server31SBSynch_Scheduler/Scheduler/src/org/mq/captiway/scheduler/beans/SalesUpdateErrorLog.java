package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class SalesUpdateErrorLog {

private Long id;
private Long userId;
private Calendar cretedOn;
private Calendar lastFetchedTime;
private String status;
private String query;
private int count;
	
	
	public SalesUpdateErrorLog() {}
	
	public SalesUpdateErrorLog( Long userId, Calendar cretedOn,
			Calendar lastFetchedTime, String status, String query, int count) {
		
		
		this.userId= userId;
		this.cretedOn = cretedOn;
		this.lastFetchedTime= lastFetchedTime; 
		this.status= status; 
		this.query = query;
		this.count = count;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Calendar getCretedOn() {
		return cretedOn;
	}

	public void setCretedOn(Calendar cretedOn) {
		this.cretedOn = cretedOn;
	}

	public Calendar getLastFetchedTime() {
		return lastFetchedTime;
	}

	public void setLastFetchedTime(Calendar lastFetchedTime) {
		this.lastFetchedTime = lastFetchedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
