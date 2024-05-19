package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class AutoSMS implements Serializable  {
	
	private Long autoSmsId;
	private String autoSmsType;
	private String templateName;
	private String messageContent;
	private String messageType;
	private Long userId;
	private Long orgId;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Long createdBy;
	private Long modifiedBy;
	private String senderId;
	private String status;
	private String templateRegisteredId;
	
	public Long getAutoSmsId() {
		return autoSmsId;
	}
	public void setAutoSmsId(Long autoSmsId) {
		this.autoSmsId = autoSmsId;
	}
	public String getAutoSmsType() {
		return autoSmsType;
	}
	public void setAutoSmsType(String autoSmsType) {
		this.autoSmsType = autoSmsType;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
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
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}
	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
	}

	

}
