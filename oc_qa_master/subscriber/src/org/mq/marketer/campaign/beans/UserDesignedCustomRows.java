package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class UserDesignedCustomRows implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long templateRowId;
	private Long userId;
	private String rowCategory;
	private String templateName;
	private String rowJsonData;
	private String rowHtmlData;
	private Calendar createdDate;
	private Calendar modifiedDate;
	
	public Long getTemplateRowId() {
		return templateRowId;
	}
	public void setTemplateRowId(Long templateRowId) {
		this.templateRowId = templateRowId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getRowCategory() {
		return rowCategory;
	}
	public void setRowCategory(String rowCategory) {
		this.rowCategory = rowCategory;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getRowJsonData() {
		return rowJsonData;
	}
	public void setRowJsonData(String rowJsonData) {
		this.rowJsonData = rowJsonData;
	}
	public String getRowHtmlData() {
		return rowHtmlData;
	}
	public void setRowHtmlData(String rowHtmlData) {
		this.rowHtmlData = rowHtmlData;
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
	
	
	

}
