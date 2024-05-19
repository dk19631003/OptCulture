package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;

public class ComponentsAndContacts implements Serializable{

	
	private Long ccId;
	private Long programId;
	private Long userId;
	private Long contactId;
	private Long componentId;
	private int stage;
	private String componentWinId;
	private String path;
	private String status;
	private Calendar activityDate;
	private Long mobile;
	private String emailId;
	
	public ComponentsAndContacts() {}
	
	public ComponentsAndContacts(Long programId, Long userId, Long contactId, Long componentId, int stage, String componentWinId, String path, Calendar activityDate) {
		
		this.programId = programId;
		this.userId = userId;
		this.contactId = contactId;
		this.componentId = componentId;
		this.stage = stage;
		this.componentWinId = componentWinId;
		this.path = path;
		this.activityDate = activityDate;
		
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCcId() {
		return ccId;
	}

	public void setCcId(Long ccId) {
		this.ccId = ccId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public String getComponentWinId() {
		return componentWinId;
	}

	public void setComponentWinId(String componentWinId) {
		this.componentWinId = componentWinId;
	}

	public Calendar getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Calendar activityDate) {
		this.activityDate = activityDate;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	
	
	
}
