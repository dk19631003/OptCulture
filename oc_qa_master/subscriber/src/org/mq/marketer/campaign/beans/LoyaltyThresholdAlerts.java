package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class LoyaltyThresholdAlerts {
	
	private Long alertId;
	private Long userId;
	private Long orgId;
	private String ltySecurityPwd; // program edit pwd
	private char enableAlerts;
	private String alertEmailId;
	private String alertMobilePhn;
	private String countType;  //percentage/ value
	private String countValue;
	private Calendar enrollAlertLastSentDate;
	private Calendar webformAlertLastSentDate;
	
	public Long getAlertId() {
		return alertId;
	}
	public void setAlertId(Long alertId) {
		this.alertId = alertId;
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
	
	public String getLtySecurityPwd() {
		return ltySecurityPwd;
	}
	public void setLtySecurityPwd(String ltySecurityPwd) {
		this.ltySecurityPwd = ltySecurityPwd;
	}
	
	public char getEnableAlerts() {
		return enableAlerts;
	}
	public void setEnableAlerts(char enableAlerts) {
		this.enableAlerts = enableAlerts;
	}
	
	public String getAlertEmailId() {
		return alertEmailId;
	}
	public void setAlertEmailId(String alertEmailId) {
		this.alertEmailId = alertEmailId;
	}
	
	public String getAlertMobilePhn() {
		return alertMobilePhn;
	}
	public void setAlertMobilePhn(String alertMobilePhn) {
		this.alertMobilePhn = alertMobilePhn;
	}
	
	public String getCountType() {
		return countType;
	}
	public void setCountType(String countType) {
		this.countType = countType;
	}
	
	public String getCountValue() {
		return countValue;
	}
	public void setCountValue(String countValue) {
		this.countValue = countValue;
	}
	public Calendar getEnrollAlertLastSentDate() {
		return enrollAlertLastSentDate;
	}
	public void setEnrollAlertLastSentDate(Calendar enrollAlertLastSentDate) {
		this.enrollAlertLastSentDate = enrollAlertLastSentDate;
	}
	public Calendar getWebformAlertLastSentDate() {
		return webformAlertLastSentDate;
	}
	public void setWebformAlertLastSentDate(Calendar webformAlertLastSentDate) {
		this.webformAlertLastSentDate = webformAlertLastSentDate;
	}
	
}
