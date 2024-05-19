package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class NotificationSuppressedContacts implements java.io.Serializable{

	private Long id;
	private Long userId;
	private Long orgId;
	private Long contactId;
	private String instanceId;
	private String type;
	private Calendar suppressedtime;
	private String reason;
	
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
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Calendar getSuppressedtime() {
		return suppressedtime;
	}
	public void setSuppressedtime(Calendar suppressedtime) {
		this.suppressedtime = suppressedtime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
