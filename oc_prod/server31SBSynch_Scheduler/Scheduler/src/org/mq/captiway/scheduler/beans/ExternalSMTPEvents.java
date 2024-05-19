package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class ExternalSMTPEvents {

	private Long eventId;
	private String eventSource;
	private String eventType;
	private Long sentId;
	private Long userId;
	private Long crId;
	private Calendar requestTime;
	private String emailId;
	private String statusCode;
	private String type;
	private String reason;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public ExternalSMTPEvents(String eventSource, String eventType,
			Long sentId, Long userId,  Long crId, Calendar requestTime, String emailId) {
		
		this.eventSource = eventSource;
		this.eventType = eventType;
		this.sentId = sentId;
		this.userId = userId;
		this.crId = crId;
		this.requestTime = requestTime;
		this.emailId = emailId;
		
	}
	
	
	public ExternalSMTPEvents(String eventSource, String eventType,
			 Long userId,  Long crId, Calendar requestTime, String emailId) {
		
		this(eventSource, eventType, null, userId, crId,  requestTime, emailId);
		
	}
	
	
	public ExternalSMTPEvents() {
		
	}
	
	
	
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getEventSource() {
		return eventSource;
	}
	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public Long getSentId() {
		return sentId;
	}

	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}

	public Calendar getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Calendar requestTime) {
		this.requestTime = requestTime;
	}
	
	
	
	
	
}
