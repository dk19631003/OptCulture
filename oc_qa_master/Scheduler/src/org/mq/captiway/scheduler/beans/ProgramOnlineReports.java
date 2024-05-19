package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Date;

public class ProgramOnlineReports implements Serializable {
	
	private Long progRepId;
	private ComponentsAndContacts compContactsId;
	private String componentWinId;
	private Long componentId;
	
	
	private Date activityDate;
	private Long programId;
	private Long contactId;
	
	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public ProgramOnlineReports() {	}
	
	public ProgramOnlineReports(ComponentsAndContacts compContactsId, String componentWinId,
			Date activityDate, Long programId, Long componentId, Long contactId) {
		
		this.compContactsId = compContactsId;
		this.componentWinId = componentWinId;
		this.activityDate = activityDate;
		this.programId = programId;
		this.componentId = componentId;
		this.contactId = contactId;
		
	}
	
	
	public Long getProgRepId() {
		return progRepId;
	}
	public void setProgRepId(Long progRepId) {
		this.progRepId = progRepId;
	}
	public ComponentsAndContacts getCompContactsId() {
		return compContactsId;
	}
	public void setCompContactsId(ComponentsAndContacts compContactsId) {
		this.compContactsId = compContactsId;
	}
	public String getComponentWinId() {
		return componentWinId;
	}
	public void setComponentWinId(String componentWinId) {
		this.componentWinId = componentWinId;
	}
	public Date getActivityDate() {
		return activityDate;
	}
	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}
	
	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	
	
	
	
	
	
}
