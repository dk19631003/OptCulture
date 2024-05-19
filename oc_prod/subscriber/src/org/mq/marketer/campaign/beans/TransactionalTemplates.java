package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class TransactionalTemplates implements Serializable {
	
	private Long transactionId;
	private Long userId;
	public String templateName;
	private String templateContent;
	private int status;
	
	
	private Long orgId;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Long createdBy;
	private Long modifiedBy;
	private String type;
	private String templateRegisteredId; //added for Equence
	
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getTemplateContent() {
		return templateContent;
	}
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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
	
	
	public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getTemplateRegisteredId() {
			return templateRegisteredId;
		}
		public void setTemplateRegisteredId(String templateRegisteredId) {
			this.templateRegisteredId = templateRegisteredId;
		}
}
