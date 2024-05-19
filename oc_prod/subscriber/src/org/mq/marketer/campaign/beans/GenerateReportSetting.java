package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.Date;

public class GenerateReportSetting {
	
	
	private Long id;
	private Long orgId;
	private Long userId;
	private boolean enable;
	private String type;
	private String fileFormat;
	private Date generateAt;
	private String freequency;
	private Calendar createdOn;
	private Long createdBy;
	private Calendar modifiedOn;
	private Long modifiedBy;
	private Calendar lastGeneratedOn;
	private String lastGeneratedFile;
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String targetDir;
	private String selectedTimeZone;
	private String timeZoneName;
	public String getTimeZoneName() {
		return timeZoneName;
	}
	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}
	private Long accountId;
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getSelectedTimeZone() {
		return selectedTimeZone;
	}
	public void setSelectedTimeZone(String selectedTimeZone) {
		this.selectedTimeZone = selectedTimeZone;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public Date getGenerateAt() {
		return generateAt;
	}
	public void setGenerateAt(Date generateAt) {
		this.generateAt = generateAt;
	}
	public String getFreequency() {
		return freequency;
	}
	public void setFreequency(String freequency) {
		this.freequency = freequency;
	}
	public Calendar getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Calendar getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Calendar getLastGeneratedOn() {
		return lastGeneratedOn;
	}
	public void setLastGeneratedOn(Calendar lastGeneratedOn) {
		this.lastGeneratedOn = lastGeneratedOn;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getLastGeneratedFile() {
		return lastGeneratedFile;
	}
	public void setLastGeneratedFile(String lastGeneratedFile) {
		this.lastGeneratedFile = lastGeneratedFile;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTargetDir() {
		return targetDir;
	}
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
	
}
