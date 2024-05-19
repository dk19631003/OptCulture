package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ResetPasswordToken implements java.io.Serializable {

	private Long tokenId;
	private String tokenValue;
	private Long userId;
	private Calendar createdDate;
	private String status;
	
	public ResetPasswordToken() { }
	
	public ResetPasswordToken(String tokenValue,Long userId,Calendar createdDate,String status){
		this.tokenValue = tokenValue;
		this.userId = userId;
		this.createdDate = createdDate;
		this.status = status;
	}
	
	public Long getTokenId() {
		return tokenId;
	}
	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}
	
	public String getTokenValue() {
		return tokenValue;
	}
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
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
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
