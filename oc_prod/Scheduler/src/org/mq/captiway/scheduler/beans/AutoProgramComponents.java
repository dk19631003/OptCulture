package org.mq.captiway.scheduler.beans;

import java.io.Serializable;

import org.mq.captiway.scheduler.ProgramEnum;


public class AutoProgramComponents implements Serializable,Comparable<Object> {



	private Long compId;
	private String compType;
	private String title;
	private String message;
	private String footer;
	private String coordinates;
	private String previousId;
	private String nextId;
	private AutoProgram autoProgram;
	private Long supportId;
	private String componentWinId;
	private int stage;
	private String label;
	//private boolean populated;

	public AutoProgramComponents() {
		
	}
	
	
	
	public AutoProgramComponents(String compType, String coordinates, String previousId,
					String nextId, AutoProgram autoProgram, String componentWinId, int stage) {
		
		this.compType = compType;
		this.coordinates = coordinates;
		this.previousId = previousId;
		this.nextId = nextId;
		this.autoProgram = autoProgram;
		this.componentWinId = componentWinId; 
		this.stage = stage;
	}
	
	public AutoProgramComponents(String compType, String title, String message, String footer, String coordinates, String previousId,
			String nextId, AutoProgram autoProgram, Long supportId, int stage) {

	this.compType = compType;
	this.title = title;
	this.message = message;
	this.footer = footer;
	this.coordinates = coordinates;
	this.previousId = previousId;
	this.nextId = nextId;
	this.autoProgram = autoProgram;
	this.supportId = supportId; 
	this.stage = stage;
	
	
	}
	public Long getCompId() {
		return compId;
	}
	public void setCompId(Long compId) {
		this.compId = compId;
	}
	public String getCompType() {
		return compType;
	}
	public void setCompType(String compType) {
		this.compType = compType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public String getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	public String getPreviousId() {
		return previousId;
	}
	public void setPreviousId(String previousId) {
		this.previousId = previousId;
	}
	public String getNextId() {
		return nextId;
	}
	public void setNextId(String nextId) {
		this.nextId = nextId;
	}
	public AutoProgram getAutoProgram() {
		return autoProgram;
	}
	public void setAutoProgram(AutoProgram autoProgram) {
		this.autoProgram = autoProgram;
	}
	public Long getSupportId() {
		return supportId;
	}
	public void setSupportId(Long supportId) {
		this.supportId = supportId;
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



	/*public boolean isPopulated() {
		return populated;
	}*/



	/*public void setPopulated(boolean populated) {
		this.populated = populated;
	}*/

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
