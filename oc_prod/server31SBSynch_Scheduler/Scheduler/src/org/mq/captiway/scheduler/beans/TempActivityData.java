package org.mq.captiway.scheduler.beans;

public class TempActivityData {

	private Long tempActId;
	private Long contactId;
	private Long componentId;
	private Long programId;
	private String label;
	private String componentWinId;
	private int modeAttribute;
	
	public TempActivityData() {}
	
	
	
	
	public Long getTempActId() {
		return tempActId;
	}
	public void setTempActId(Long tempActId) {
		this.tempActId = tempActId;
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
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getComponentWinId() {
		return componentWinId;
	}
	public void setComponentWinId(String componentWinId) {
		this.componentWinId = componentWinId;
	}
	public int getModeAttribute() {
		return modeAttribute;
	}
	public void setModeAttribute(int modeAttribute) {
		this.modeAttribute = modeAttribute;
	}
	
	
	
	
}
