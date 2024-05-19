package org.mq.captiway.scheduler.beans;

import java.io.Serializable;

public class TempComponentsData implements Serializable{

	private Long tempId;
	private Long programId;
	private Long contactId;
	private Long componentId;
	private String componentWinId;
	private int stage;
	private String label;
	private String modeAttribute;
	
	public TempComponentsData(Long programId, Long contactId, Long componentId, String componentWinId, int stage, String label) {
		this.programId = programId;
		this.contactId = contactId;
		this.componentId = componentId;
		this.componentWinId = componentWinId;
		this.stage = stage;
		this.label = label;
		
	}
	
	
	public Long getTempId() {
		return tempId;
	}
	public void setTempId(Long tempId) {
		this.tempId = tempId;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
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
	public String getComponentWinId() {
		return componentWinId;
	}
	public void setComponentWinId(String componentWinId) {
		this.componentWinId = componentWinId;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getModeAttribute() {
		return modeAttribute;
	}


	public void setModeAttribute(String modeAttribute) {
		this.modeAttribute = modeAttribute;
	}
	
	
	
	
}
