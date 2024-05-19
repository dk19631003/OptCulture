package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class TempContactSpecificDateEvents {

	private Long eventId;
	private Long eventTriggerId;
	private Calendar createdTime;
	private Calendar eventTime;
	private Long userId;
	private String eventCategory;
	private byte emailStatus;
	
	private byte smsStatus;
	
	private Long sourceId;
	private Long contactId;
	private int triggerType;
	private String triggerCondition;
	private Long campSentId;
	private Long campCrId;
	private Long smsSentId;
	private Long smsCrId;
	private Calendar campaignSentDate;
	public Calendar getCampaignSentDate() {
		return campaignSentDate;
	}
	public void setCampaignSentDate(Calendar campaignSentDate) {
		this.campaignSentDate = campaignSentDate;
	}
	public Calendar getSmsSentDate() {
		return smsSentDate;
	}
	public void setSmsSentDate(Calendar smsSentDate) {
		this.smsSentDate = smsSentDate;
	}
	private Calendar smsSentDate;
	
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public Long getEventTriggerId() {
		return eventTriggerId;
	}
	public void setEventTriggerId(Long eventTriggerId) {
		this.eventTriggerId = eventTriggerId;
	}
	public Calendar getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Calendar createdTime) {
		this.createdTime = createdTime;
	}
	public Calendar getEventTime() {
		return eventTime;
	}
	public void setEventTime(Calendar eventTime) {
		this.eventTime = eventTime;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public byte getEmailStatus() {
		return emailStatus;
	}
	public void setEmailStatus(byte emailStatus) {
		this.emailStatus = emailStatus;
	}
	public byte getSmsStatus() {
		return smsStatus;
	}
	public void setSmsStatus(byte smsStatus) {
		this.smsStatus = smsStatus;
	}
	
	public String getEventCategory() {
		return eventCategory;
	}
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	
	public int getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}
	public String getTriggerCondition() {
		return triggerCondition;
	}
	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}
	
	public Long getCampSentId() {
		return campSentId;
	}
	public void setCampSentId(Long campSentId) {
		this.campSentId = campSentId;
	}
	public Long getCampCrId() {
		return campCrId;
	}
	public void setCampCrId(Long campCrId) {
		this.campCrId = campCrId;
	}
	public Long getSmsSentId() {
		return smsSentId;
	}
	public void setSmsSentId(Long smsSentId) {
		this.smsSentId = smsSentId;
	}
	public Long getSmsCrId() {
		return smsCrId;
	}
	public void setSmsCrId(Long smsCrId) {
		this.smsCrId = smsCrId;
	}
	
	

}
