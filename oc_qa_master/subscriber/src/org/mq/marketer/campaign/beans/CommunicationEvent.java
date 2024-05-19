package org.mq.marketer.campaign.beans;


import java.io.Serializable;
import java.time.LocalDateTime;


public class CommunicationEvent  implements Serializable{

	private static final long serialVersionUID = 1L;
	private long crId;
	private long campaignId;
	private String recipient;
	private String eventType;
	private LocalDateTime eventDate;
	
	private String apiMsgId;
	private String channelType;
	private long userId;
	private long contactId;


	public CommunicationEvent(String recipient, String eventType, LocalDateTime eventDate) {
		super();
		this.recipient = recipient;
		this.eventType = eventType;
		this.eventDate = eventDate;
	}
	public CommunicationEvent(long reportId,long campaignId,String emailId, String action, LocalDateTime now, 
			String apiMsgId,String channelType,long userId,long contactId) {
		this.crId = reportId;
		this.campaignId = campaignId;
		this.recipient = emailId;
		this.eventType = action;
		this.eventDate = now;
		this.channelType = channelType;
		this.apiMsgId = apiMsgId;
		this.userId = userId;
		this.contactId = contactId;
	}


	public long getCrId() {
		return crId;
	}


	public void setCrId(long crId) {
		this.crId = crId;
	}


	public long getCampaignId() {
		return campaignId;
	}


	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}


	public String getRecipient() {
		return recipient;
	}


	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}


	public LocalDateTime getEventDate() {
		return eventDate;
	}


	public void setEventDate(LocalDateTime eventDate) {
		this.eventDate = eventDate;
	}


	public String getApiMsgId() {
		return apiMsgId;
	}


	public void setApiMsgId(String apiMsgId) {
		this.apiMsgId = apiMsgId;
	}


	public String getChannelType() {
		return channelType;
	}


	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}


	public long getContactId() {
		return contactId;
	}


	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
}
