package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class UserSMSSenderId implements Serializable{
	
	
	private Long id;
	private Long userId;
	private String senderId;
	private String userName;
	private String smsType;
	
	private Long modifiedBy;
	private Long createdBy;
	private Calendar modifiedDate;
	
	public UserSMSSenderId() {
		
	}
	
	public UserSMSSenderId(Long userId, String senderId, String userName, Long createdBy, Long modifiedBy, Calendar modifiedDate) {
		
		this.userId = userId;
		this.userName = userName;
		this.senderId = senderId;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


}
