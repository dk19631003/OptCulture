package org.mq.marketer.campaign.beans;

import java.util.Calendar;


public class OrgSMSkeywords implements java.io.Serializable {

	
	private Long keywordId;
	private Long orgId;
	private Calendar validUpto;
	private Calendar createdDate;
	
	private Calendar startFrom;
	private String keyword;
	private String status;
	private String autoResponse;
	private Users user;
	private String shortCode;
	private String senderId;
	private Long modifiedBy;
	private Calendar modifiedDate;
	
	public OrgSMSkeywords() {}
	
	public OrgSMSkeywords(Long orgId, Calendar createdDate, String keyword, 
			String status, Users user) {
		
		this.orgId = orgId;
		this.createdDate = createdDate;
		this.keyword = keyword;
		this.status = status;
		/*this.autoResponse = autoResponse;
		this.shortCode = shortCode;*/
		this.user = user;
		/*this.startFrom = startFrom;
		this.validUpto = validUpto;*/
		//this.senderId =senderId;
		/*this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;*/
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
	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Calendar getStartFrom() {
		return this.startFrom;
	}

	public void setStartFrom(Calendar startFrom) {
		this.startFrom = startFrom;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
