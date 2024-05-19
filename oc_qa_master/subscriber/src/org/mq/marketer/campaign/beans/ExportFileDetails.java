package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ExportFileDetails {
	
	private Long exportFileId;
	private Long userId;
	private Long orgId;
	private String fileType;
	private String fileName;
	private String filePath;
	private String status;
	private Calendar createdTime;
	private Calendar deletedTime;
	
	
	
	public Long getExportFileId() {
		return exportFileId;
	}
	public void setExportFileId(Long exportFileId) {
		this.exportFileId = exportFileId;
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
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Calendar getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Calendar createdTime) {
		this.createdTime = createdTime;
	}
	public Calendar getDeletedTime() {
		return deletedTime;
	}
	public void setDeletedTime(Calendar deletedTime) {
		this.deletedTime = deletedTime;
	}

}
