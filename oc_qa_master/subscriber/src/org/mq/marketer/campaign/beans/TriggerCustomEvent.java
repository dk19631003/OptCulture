package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class TriggerCustomEvent implements java.io.Serializable{
	
	private Long id;
	private String eventName;
	private Calendar eventDate;
	private String userName;
	
	
	public TriggerCustomEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public TriggerCustomEvent(String eventName,Calendar eventDate, String userName) {
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.userName = userName;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEventName() {
		return eventName;
	}


	public void setEventName(String eventName) {
		this.eventName = eventName;
	}


	public Calendar getEventDate() {
		return eventDate;
	}


	public void setEventDate(Calendar eventDate) {
		this.eventDate = eventDate;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
