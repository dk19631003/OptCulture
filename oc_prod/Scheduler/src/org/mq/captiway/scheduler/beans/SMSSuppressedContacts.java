package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class SMSSuppressedContacts implements java.io.Serializable{

	private Long id;
	private String mobile;
	private Users user;
	private String type;
	private Calendar suppressedtime;
	private String reason;
	
	
	public Calendar getSuppressedtime() {
		return suppressedtime;
	}



	public void setSuppressedtime(Calendar suppressedtime) {
		this.suppressedtime = suppressedtime;
	}



	public SMSSuppressedContacts() { }
	
	
	
	public Long getId() {
		return id;
		
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public Users getUser() {
		return user;
	}


	public void setUser(Users user) {
		this.user = user;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) 
			return false;

		if(obj.getClass() != getClass()){
			return false;
		}

		SMSSuppressedContacts tempCS = (SMSSuppressedContacts)obj;
		
		if(this.mobile.equalsIgnoreCase(tempCS.mobile) ) {
			
			
				
				return true;
			}
			
		
		return false;

	}
	
	@Override
	public int hashCode() {
		
		return this.mobile.hashCode();
		
	}

public String getReason() {
		return reason;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
	
	
}
