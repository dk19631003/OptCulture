package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class UserCampaignExpiration {

	private Long id;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Calendar sentOn;
	private Long userId;
	private Long campaignId;
	private String status;
	private String typeOfAlert;//for future sake
	private String toEmailId;
	private String msgContent;
	//private String freequency;
	private Calendar lastSentOn;
	
	public Calendar getLastSentOn() {
		return lastSentOn;
	}
	public void setLastSentOn(Calendar lastSentOn) {
		this.lastSentOn = lastSentOn;
	}
	/*public String getFreequency() {
		return freequency;
	}
	public void setFreequency(String freequency) {
		this.freequency = freequency;
	}*/
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	private Calendar startDate;
	private Calendar endDate;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Calendar getSentOn() {
		return sentOn;
	}
	public void setSentOn(Calendar sentOn) {
		this.sentOn = sentOn;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTypeOfAlert() {
		return typeOfAlert;
	}
	public void setTypeOfAlert(String typeOfAlert) {
		this.typeOfAlert = typeOfAlert;
	}
	
	public String getToEmailId() {
		return toEmailId;
	}
	public void setToEmailId(String toEmailId) {
		this.toEmailId = toEmailId;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
}
