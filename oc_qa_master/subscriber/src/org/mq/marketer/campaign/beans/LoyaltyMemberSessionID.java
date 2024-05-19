package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class LoyaltyMemberSessionID {

	private Long id;
	private String sessionID;
	private Calendar createdDate;
	private String status;//active , invalid
	private Long orgID;
	private String deviceID;
	private Long userId;
	private String cardNumber;
	
	public LoyaltyMemberSessionID(){}
	
	public LoyaltyMemberSessionID(String sessionID, Calendar createdDate, String status,
			Long orgID, String deviceID, Long userId,String cardNumber ) {
		this.sessionID = sessionID;
		this.createdDate = createdDate;
		this.status = status;
		this.orgID = orgID;
		this.deviceID = deviceID;
		this.userId = userId;
		this.cardNumber = cardNumber;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getOrgID() {
		return orgID;
	}
	public void setOrgID(Long orgID) {
		this.orgID = orgID;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
}
