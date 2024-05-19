package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class DigitalReceiptMyTemplate  implements java.io.Serializable  {
	
	  private Long myTemplateId;
	  private String name;
	  private String content;
	  private Calendar createdDate;
	  private Calendar modifiedDate;
	  private Long userId;
	  
	  private Long orgId;
	  private String jsonContent;
	  private String editorType;
	  private String folderName;
	  private String autoSaveJsonContent;
	  private String autoSaveHtmlContent;
	  private Long modifiedby;
	  private Calendar onAutoModifiedDate;
	  
	  private Long createdBy;
  
	  public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public DigitalReceiptMyTemplate() {
		// TODO Auto-generated constructor stub
	  }
	  
	public DigitalReceiptMyTemplate(String name, String content, Calendar createdDate, Long userId) {
	       this.name = name;
	       this.content = content;
	       this.createdDate = createdDate;
	       this.userId = userId;
	}
	
	public DigitalReceiptMyTemplate(String name, String content, Calendar createdDate, Long userId, Long orgId) {
	       this.name = name;
	       this.content = content;
	       this.createdDate = createdDate;
	       this.userId = userId;
	       this.orgId = orgId;
	}

	  public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getAutoSaveJsonContent() {
		return autoSaveJsonContent;
	}

	public void setAutoSaveJsonContent(String autoSaveJsonContent) {
		this.autoSaveJsonContent = autoSaveJsonContent;
	}

	public String getAutoSaveHtmlContent() {
		return autoSaveHtmlContent;
	}

	public void setAutoSaveHtmlContent(String autoSaveHtmlContent) {
		this.autoSaveHtmlContent = autoSaveHtmlContent;
	}

	public Long getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(Long modifiedby) {
		this.modifiedby = modifiedby;
	}

	public Calendar getOnAutoModifiedDate() {
		return onAutoModifiedDate;
	}

	public void setOnAutoModifiedDate(Calendar onAutoModifiedDate) {
		this.onAutoModifiedDate = onAutoModifiedDate;
	}

	public Long getMyTemplateId() {
		return myTemplateId;
	}

	public void setMyTemplateId(Long myTemplateId) {
		this.myTemplateId = myTemplateId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
  

}
