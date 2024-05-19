package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class POSFileLogs implements java.io.Serializable {
	
	//user_id,file_type,file_name,fetched_time
	private Long posLogId;
	private Long userId;
	private String fileType;
	private String fileName;
	private Calendar fetchedTime;
	
	public POSFileLogs() {}
	
	public POSFileLogs (Long userId,String fileType,String fileName,Calendar fetchedTime) {
		this.userId = userId;
		this.fileType = fileType;
		this.fileName = fileName;
		this.fetchedTime = fetchedTime;
	}
	
	public Long getPosLogId() {
		return posLogId;
	}
	public void setPosLogId(Long posLogId) {
		this.posLogId = posLogId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public Calendar getFetchedTime() {
		return fetchedTime;
	}
	public void setFetchedTime(Calendar fetchedTime) {
		this.fetchedTime = fetchedTime;
	}
	

}
