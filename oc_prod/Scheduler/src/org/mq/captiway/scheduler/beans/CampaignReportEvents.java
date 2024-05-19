package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class CampaignReportEvents {

	private Long eventId;
	private String requestedAction;
	private Long cId;
	private Long unSubId;
	private String url;
	private Long sentId;
	private Long userId;
	private Long crId;
	private Calendar requestTime;
	private String userAgent;
	
	
	public CampaignReportEvents() {}



	public CampaignReportEvents(String requestedAction,
			  Long sentId, Long userId, Long cId, Long unSubId, String url, Calendar requestTime, String userAgent) {
		
		this.requestedAction = requestedAction;
		this.sentId = sentId;
		this.cId = cId;
		this.url = url;
		this.unSubId = unSubId;
		this.requestTime = requestTime;
		this.userId = userId;
		this.userAgent = userAgent;
		
		
	}
	
	
	
	
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getRequestedAction() {
		return requestedAction;
	}
	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}
	public Long getcId() {
		return cId;
	}
	public void setcId(Long cId) {
		this.cId = cId;
	}
	public Long getUnSubId() {
		return unSubId;
	}
	public void setUnSubId(Long unSubId) {
		this.unSubId = unSubId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	
}
