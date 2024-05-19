package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class OrgSMSkeywords {

	
	private Long keywordId;
	private Long orgId;
	private Calendar validUpto;
	private String keyword;
	
	private String shortCode;
	
	private Calendar createdDate;
	private Calendar startFrom;
	private String status;
	private String autoResponse;
	private Users user;
	
	private String toEmailId;
	private String clientTimeZone;
	
	public String getToEmailId() {
		return toEmailId;
	}
	public void setToEmailId(String toEmailId) {
		this.toEmailId = toEmailId;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	private String senderId;
	
	
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(Calendar startFrom) {
		this.startFrom = startFrom;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAutoResponse() {
		return autoResponse;
	}
	public void setAutoResponse(String autoResponse) {
		this.autoResponse = autoResponse;
	}
	
	public Long getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(Long keywordId) {
		this.keywordId = keywordId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Calendar getValidUpto() {
		return validUpto;
	}
	public void setValidUpto(Calendar validUpto) {
		this.validUpto = validUpto;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getClientTimeZone() {
		return clientTimeZone;
	}
	public void setClientTimeZone(String clientTimeZone) {
		this.clientTimeZone = clientTimeZone;
	}
	
	
}
