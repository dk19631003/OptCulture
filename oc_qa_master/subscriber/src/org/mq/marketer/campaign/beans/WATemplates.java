package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class WATemplates implements Serializable {
	
	private Long templateId; //auto increment
	public String templateName;
	private String provider;
	private String type; // just like auto_sms_type eg CNI,confirmOrder
	private String jsonContent;
	private String placeholders; // comma seperated
	private String templateRegisteredId; //template namespace
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
	public String getTemplateName() {
		return templateName;
	}
	public String getJsonContent() {
		return jsonContent;
	}
	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}
	public String getPlaceholders() {
		return placeholders;
	}
	public String getProvider() {
		return provider;
	}
	public String getType() {
		return type;
	}
	public Long getUserId() {
		return userId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public String getStatus() {
		return status;
	}
	public String getHeaders() {
		return headers;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public Calendar getCreatedBy() {
		return createdBy;
	}
	public Calendar getModifiedBy() {
		return modifiedBy;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}
	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
	}
	public void setPlaceholders(String placeholders) {
		this.placeholders = placeholders;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public void setCreatedBy(Calendar createdBy) {
		this.createdBy = createdBy;
	}
	public void setModifiedBy(Calendar modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public void setStatus(String status) {
		// TODO Auto-generated method stub
		this.status = status;

	}
	
	
}
