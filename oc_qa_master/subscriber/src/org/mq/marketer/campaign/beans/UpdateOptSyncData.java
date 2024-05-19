package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class UpdateOptSyncData implements Serializable {
	
	private Long id;
	private Long optSyncId;
	private Long userId;
	private String optSyncName;
	private Calendar optSyncHitTime;
	private Calendar optSyncModifiedTime;
	private int count;
	private String status;
	private String pluginStatus;
	private String emailId;
	private String enabledOptSyncFlag;
	private Long orgId;
	
	private String onAlertsBy;
	
	private Calendar downAlertSentTime;
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getOptSyncId() {
		return optSyncId;
	}

	public void setOptSyncId(Long optSyncId) {
		this.optSyncId = optSyncId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(String pluginStatus) {
		this.pluginStatus = pluginStatus;
	}
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOptSyncName() {
		return optSyncName;
	}

	public void setOptSyncName(String optSyncName) {
		this.optSyncName = optSyncName;
	}

	public Calendar getOptSyncHitTime() {
		return optSyncHitTime;
	}

	public void setOptSyncHitTime(Calendar optSyncHitTime) {
		this.optSyncHitTime = optSyncHitTime;
	}

	public Calendar getOptSyncModifiedTime() {
		return optSyncModifiedTime;
	}

	public void setOptSyncModifiedTime(Calendar optSyncModifiedTime) {
		this.optSyncModifiedTime = optSyncModifiedTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public String getEnabledOptSyncFlag() {
		return enabledOptSyncFlag;
	}
	public void setEnabledOptSyncFlag(String enabledOptSyncFlag) {
		this.enabledOptSyncFlag = enabledOptSyncFlag;
	}

	public String getOnAlertsBy() {
		return onAlertsBy;
	}

	public void setOnAlertsBy(String onAlertsBy) {
		this.onAlertsBy = onAlertsBy;
	}

	public Calendar getDownAlertSentTime() {
		return downAlertSentTime;
	}

	public void setDownAlertSentTime(Calendar downAlertSentTime) {
		this.downAlertSentTime = downAlertSentTime;
	}
}//EOF
