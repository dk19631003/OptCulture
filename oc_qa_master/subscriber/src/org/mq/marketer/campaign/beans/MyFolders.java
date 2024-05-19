package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class MyFolders implements Serializable{
	
	private Long folderId;
	private String folderName;
	
	
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Long createdBy;
	//private Long modifiedBy;
	
	private Long orgId;
	
	public MyFolders(){
		
	}
	public MyFolders(String folderName, Calendar createdDate, Long createdBy, Long orgId, String type ) {
		this.folderName = folderName;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.orgId = orgId;
		this.type = type;
	}
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	private String type;
	
	//private Account account;
	
	
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
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
/*	public Long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}*/
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
/*	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}*/
	

}
