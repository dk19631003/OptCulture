package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoProgram implements Serializable,Comparable<Object>{

	
	private static final Logger logger = LogManager.getLogger(AutoProgram.class);
	
	private Long programId;
	private String programName;
	private String description;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String category;
	private Set<MailingList> mailingLists = new HashSet<MailingList>();
	private Users user;
	private String status;
	
	
	public AutoProgram() {
		
		
	}
	
	public AutoProgram(String programName, Calendar createdDate, Calendar modifiedDate, Users user) {
		this.programName = programName;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.user = user;
		
		
	}
	
	public AutoProgram(String programName, String description, Calendar createdDate, Calendar modifiedDate, Users user) {
		this.programName = programName;
		this.description = description;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.user = user;
		
		
	}
	
	
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Set<MailingList> getMailingLists() {
		return mailingLists;
	}
	public void setMailingLists(Set<MailingList> mailingLists) {
		this.mailingLists = mailingLists;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public int compareTo(Object obj) {
		
		if(obj == null) 
			return 1;
		
		if(obj instanceof AutoProgram) {
			
			AutoProgram autoProgram = (AutoProgram)obj;
			return autoProgram.createdDate.compareTo(this.createdDate);
		} 
		else {
			return 1;
		}
		
	}
	
}
