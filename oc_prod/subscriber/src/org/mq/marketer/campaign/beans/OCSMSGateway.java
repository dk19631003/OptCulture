package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class OCSMSGateway {

	private Long id;
	private String gatewayName;
	private String userId;
	private String pwd;
	private String systemId;
	
	private String systemPwd;
	private String ip;
	
	private String port;
	private String accountType;
	private String mode;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Long createdBy;
	private Long modifiedBy;
	private boolean postPaid;
	private String status;
	private String server;
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	private String countryName;
	private boolean pullReports;
	
	private String pullReportsURL;
	private String postpaidBalURL;
	
	private String systemType;
	
	private String senderId;
	private String APIId;
	private String principalEntityId; //peId in Equence request 
	
	//Added for MultiThreaded submission
		private boolean enableMultiThreadSub;
		
		//Added to Check Session's
		private boolean enableSessionAlive;
		
		private String twoWaySenderID;
	
	public String getTwoWaySenderID() {
			return twoWaySenderID;
		}
		public void setTwoWaySenderID(String twoWaySenderID) {
			this.twoWaySenderID = twoWaySenderID;
		}
		
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
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
	public boolean isPostPaid() {
		return postPaid;
	}
	public void setPostPaid(boolean postPaid) {
		this.postPaid = postPaid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getSystemPwd() {
		return systemPwd;
	}
	public void setSystemPwd(String systemPwd) {
		this.systemPwd = systemPwd;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;

	}
	public boolean isPullReports() {
		return pullReports;
	}
	public void setPullReports(boolean pullReports) {
		this.pullReports = pullReports;
	}
	public String getPullReportsURL() {
		return pullReportsURL;
	}
	public void setPullReportsURL(String pullReportsURL) {
		this.pullReportsURL = pullReportsURL;
	}
	public String getPostpaidBalURL() {
		return postpaidBalURL;
	}
	public void setPostpaidBalURL(String postpaidBalURL) {
		this.postpaidBalURL = postpaidBalURL;
	}
	
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getAPIId() {
		return APIId;
	}
	public void setAPIId(String aPIId) {
		APIId = aPIId;
	}
	

	/**
	 * Checks weather MultiThreadedSubmission allowed
	 * @return true/false
	 */
	public boolean isEnableMultiThreadSub() {
		return enableMultiThreadSub;
	}
	
	/**
	 * 
	 * @param enableMultiThreadSub
	 */
	public void setEnableMultiThreadSub(boolean enableMultiThreadSub) {
		this.enableMultiThreadSub = enableMultiThreadSub;
	}
	
	/**
	 * Weather session is alive or not
	 * @return  true/false
	 */
	public boolean isEnableSessionAlive() {
		return enableSessionAlive;
	}
	/**
	 * 
	 * @param enableSessionAlive
	 */
	public void setEnableSessionAlive(boolean enableSessionAlive) {
		this.enableSessionAlive = enableSessionAlive;
	}
	public String getPrincipalEntityId() {
		return principalEntityId;
	}
	public void setPrincipalEntityId(String principalEntityId) {
		this.principalEntityId = principalEntityId;
	}
}
