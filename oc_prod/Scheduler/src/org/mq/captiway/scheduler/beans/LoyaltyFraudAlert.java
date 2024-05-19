package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

/**
 * 
 * @author Pravendra
 * It is handle fraud alert trasaction 
 */
public class LoyaltyFraudAlert {

	
private Long fraudAlertId;
private String ruleName;
private Long createdByUserId;
private Long modifiedByUserId;
private Calendar createdDate;
private Calendar modifiedDate;
private String trxRule;
private String dateRule;
private String emailId;
private String frequency;
private Calendar lastSentOn;
private String triggerAt;
private boolean enabled;


public Long getFraudAlertId() {
	return fraudAlertId;
}
public void setFraudAlertId(Long fraudAlertId) {
	this.fraudAlertId = fraudAlertId;
}
public String getRuleName() {
	return ruleName;
}
public void setRuleName(String ruleName) {
	this.ruleName = ruleName;
}
public Long getCreatedByUserId() {
	return createdByUserId;
}
public void setCreatedByUserId(Long createdByUserId) {
	this.createdByUserId = createdByUserId;
}
public Long getModifiedByUserId() {
	return modifiedByUserId;
}
public void setModifiedByUserId(Long modifiedByUserId) {
	this.modifiedByUserId = modifiedByUserId;
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
public String getTrxRule() {
	return trxRule;
}
public void setTrxRule(String trxRule) {
	this.trxRule = trxRule;
}
public String getDateRule() {
	return dateRule;
}
public void setDateRule(String dateRule) {
	this.dateRule = dateRule;
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
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}



}
