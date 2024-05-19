package org.mq.captiway.scheduler.beans;

import java.io.Serializable;

public class SwitchCondition implements Serializable {
	
	private Long switchCondId;
	private Long componentId;
	private String conditionQuery;
	private String modeAttribute;
	private String lineMessage;
	private String condition;
	private String switchCompWinId;
	private Long programId;
	private String modeFlag;
	private String openCampWinIds;
	private String clickCampWinIds;
	
	
	
	public SwitchCondition() {}
	
	public SwitchCondition(Long componentId, String conditionQuery,String condition, 
			String modeAttribute, String lineMessage, String switchCompWinId, Long programId, 
			String modeFlag, String openCampWinIds, String clickCampWinIds) {
		this.componentId = componentId;
		this.conditionQuery = conditionQuery;
		this.condition = condition;
		this.modeAttribute = modeAttribute;
		this.lineMessage = lineMessage;
		this.switchCompWinId = switchCompWinId;
		this.programId = programId;
		this.modeFlag = modeFlag;
		this.openCampWinIds = openCampWinIds;
		this.clickCampWinIds = clickCampWinIds;
	}
	
	public Long getSwitchCondId() {
		return switchCondId;
	}
	public void setSwitchCondId(Long switchCondId) {
		this.switchCondId = switchCondId;
	}
	public Long getComponentId() {
		return componentId;
	}
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}
	public String getConditionQuery() {
		return conditionQuery;
	}
	public void setConditionQuery(String conditionQuery) {
		this.conditionQuery = conditionQuery;
	}
	public String getModeAttribute() {
		return modeAttribute;
	}
	public void setModeAttribute(String modeAttribute) {
		this.modeAttribute = modeAttribute;
	}
	public String getLineMessage() {
		return lineMessage;
	}
	public void setLineMessage(String lineMessage) {
		this.lineMessage = lineMessage;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getSwitchCompWinId() {
		return switchCompWinId;
	}
	
	public void setSwitchCompWinId(String switchCompWinId) {
		this.switchCompWinId = switchCompWinId;
	}
	
	public Long getProgramId() {
		return programId;
	}
	
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	
	public String getModeFlag() {
		return modeFlag;
	}
	
	public void setModeFlag(String modeFlag) {
		this.modeFlag = modeFlag;
	}
	public String getOpenCampWinIds() {
		return openCampWinIds;
	}

	public void setOpenCampWinIds(String openCampWinIds) {
		this.openCampWinIds = openCampWinIds;
	}

	public String getClickCampWinIds() {
		return clickCampWinIds;
	}

	public void setClickCampWinIds(String clickCampWinIds) {
		this.clickCampWinIds = clickCampWinIds;
	}

	

}
