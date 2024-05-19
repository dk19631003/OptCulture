package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ETLFileUploadLogs implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long logId;
	private Long userId;
	private String fileName;
	private Calendar uploadTime;
	private int recordCount;
	private String fileStatus;
	private String comments;
	
	private String processedFilePath;
	private String receiptDetailsPath;
		
	public ETLFileUploadLogs() {}

	public String getProcessedFilePath() {
		return processedFilePath;
	}
	public void setProcessedFilePath(String processedFilePath) {
		this.processedFilePath = processedFilePath;
	}

	public String getReceiptDetailsPath() {
		return receiptDetailsPath;
	}
	public void setReceiptDetailsPath(String receiptDetailsPath) {
		this.receiptDetailsPath = receiptDetailsPath;
	}
	public String getComments() {
		return comments;
	}



	public void setComments(String comments) {
		this.comments = comments;
	}



	public String getFileStatus() {
		return fileStatus;
	}


	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}



	public int getRecordCount() {
		return recordCount;
	}



	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}


	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Calendar getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Calendar uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	
	

}
