package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class LtySettingsActivityLogs {

	private Long logId;
	private Long userId;
	private Long programId;
	private Calendar createdDate;
	private String logType;
	private String logDetails;
	private char sendEmailFlag;
	private Calendar lastEmailSentDate;

	public LtySettingsActivityLogs(Long userId, Long programId,	Calendar createdDate, String logType, String logDetails, char sendEmailFlag) {
		super();
		this.userId = userId;
		this.programId = programId;
		this.createdDate = createdDate;
		this.logType = logType;
		this.logDetails = logDetails;
		this.sendEmailFlag = sendEmailFlag;
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
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getLogDetails() {
		return logDetails;
	}
	public void setLogDetails(String logDetails) {
		this.logDetails = logDetails;
	}
	public char getSendEmailFlag() {
		return sendEmailFlag;
	}
	public void setSendEmailFlag(char sendEmailFlag) {
		this.sendEmailFlag = sendEmailFlag;
	}
	public Calendar getLastEmailSentDate() {
		return lastEmailSentDate;
	}
	public void setLastEmailSentDate(Calendar lastEmailSentDate) {
		this.lastEmailSentDate = lastEmailSentDate;
	}

}
