package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class UserPosFTPSettings {

	private Long userFTPId;
	private Long userId;
	private String hostAddress;
	private String directoryPath;
	private String ftpUserName;
	private String ftpPassword;
	private String fileFormat;
	private String fileType;
	private Calendar lastFetchedTime;
	private Long scheduledFreqInMintues;
	private String scheduleType;
	private Boolean enabled;
	private int checkProcessPeriod;
	private String alertEmailAddress;
	private boolean checkAlert;
	

	public boolean isCheckAlert() {
		return checkAlert;
	}

	public void setCheckAlert(boolean checkAlert) {
		this.checkAlert = checkAlert;
	}

	

	public int getCheckProcessPeriod() {
		return checkProcessPeriod;
	}

	public void setCheckProcessPeriod(int checkProcessPeriod) {
		this.checkProcessPeriod = checkProcessPeriod;
	}

	public String getAlertEmailAddress() {
		return alertEmailAddress;
	}

	public void setAlertEmailAddress(String alertEmailAddress) {
		this.alertEmailAddress = alertEmailAddress;
	}

	public UserPosFTPSettings() { }

	public Long getUserFTPId() {
		return userFTPId;
	}

	public void setUserFTPId(Long userFTPId) {
		this.userFTPId = userFTPId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}
	
	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Calendar getLastFetchedTime() {
		return lastFetchedTime;
	}

	public void setLastFetchedTime(Calendar lastFetchedTime) {
		this.lastFetchedTime = lastFetchedTime;
	}

	

	public Long getScheduledFreqInMintues() {
		return scheduledFreqInMintues;
	}

	public void setScheduledFreqInMintues(Long scheduledFreqInMintues) {
		this.scheduledFreqInMintues = scheduledFreqInMintues;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	
	
	
	
	
}
