package org.mq.captiway.scheduler.beans;
import java.util.Calendar;

public class UserEmailAlert implements java.io.Serializable {

	private Long userEmailAlertId;
	private Long userId;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Long createdBy;
	private Long modifiedBy;
	private String type;
	private String emailId;
	private String frequency;
	private Calendar lastSentOn;
	private String triggerAt;
	private boolean enabled;
	
	
	
	public Long getUserEmailAlertId() {
		return userEmailAlertId;
	}
	public void setUserEmailAlertId(Long userEmailAlertId) {
		this.userEmailAlertId = userEmailAlertId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
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
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Calendar getLastSentOn() {
		return lastSentOn;
	}
	public void setLastSentOn(Calendar lastSentOn) {
		this.lastSentOn = lastSentOn;
	}
	public String getTriggerAt() {
		return triggerAt;
	}
	public void setTriggerAt(String triggerAt) {
		this.triggerAt = triggerAt;
	}
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}


