package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ContactParentalConsent implements java.io.Serializable {

	
	private Long parentalId;
	private Long contactId;
	private String email;
	private String status;
	private String childFirstName;
	
	private Calendar childDOB;
	

	private Long userId;
	private Calendar sentDate; 
	private String ConsentMedium;
	private String contactEmail;
	
	
	
	
	
	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public ContactParentalConsent(){}
	
	public ContactParentalConsent(Long contactId, String email, String status, Long userId, Calendar sentDate, String contactEmail){
		
		this.contactId = contactId;
		this.email = email;
		this.userId = userId;
		this.status = status;
		this.sentDate = sentDate;
		this.contactEmail = contactEmail;
		
		
		
	}
	
	
	public String getChildFirstName() {
		return childFirstName;
	}

	public void setChildFirstName(String childFirstName) {
		this.childFirstName = childFirstName;
	}

	public Calendar getChildDOB() {
		return childDOB;
	}

	public void setChildDOB(Calendar childDOB) {
		this.childDOB = childDOB;
	}

	public String getConsentMedium() {
		return ConsentMedium;
	}

	public void setConsentMedium(String consentMedium) {
		ConsentMedium = consentMedium;
	}

	public Long getParentalId() {
		return parentalId;
	}
	public void setParentalId(Long parentalId) {
		this.parentalId = parentalId;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
