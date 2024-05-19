package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;

public class WATemplates implements Serializable {
	
	private Long templateId; //auto increment
	public String templateName;
	private String provider;
	private String type;
	private String jsonContent;
	private String placeholders; // comma seperated
	private String templateRegisteredId; //from WA
	private Long userId;
	private Long orgId;
	private String status;
	private String headers;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Calendar createdBy;
	private Calendar modifiedBy;
	
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getJsonContent() {
		return jsonContent;
	}
	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}
	public String getPlaceholders() {
		return placeholders;
	}
	public void setPlaceholders(String placeholders) {
		this.placeholders = placeholders;
	}
	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}
	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHeaders() {
		return headers;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
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
	public Calendar getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Calendar createdBy) {
		this.createdBy = createdBy;
	}
	public Calendar getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Calendar modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	
}
