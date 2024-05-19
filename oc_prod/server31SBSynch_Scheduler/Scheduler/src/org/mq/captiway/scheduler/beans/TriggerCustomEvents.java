package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

@SuppressWarnings ({})
public class TriggerCustomEvents {
	
	private int id;
	private String eventName;
	private Calendar eventDate;
	private String userName;

	public int getId() {
		return id;
	}
	public void setId(int id) {
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
