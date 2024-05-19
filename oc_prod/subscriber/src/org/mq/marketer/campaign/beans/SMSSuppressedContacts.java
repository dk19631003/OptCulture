package org.mq.marketer.campaign.beans;

import java.util.Calendar;


public class SMSSuppressedContacts implements java.io.Serializable{

	private Long id;
	private String mobile;
	private Users user;
	private String type;
	private Calendar suppressedtime;
	private String reason;
	private String source;//changes 2.5.3.0
	
	public String getReason() {
		return reason;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}

	
	public Calendar getSuppressedtime() {
		return suppressedtime;
	}



	public void setSuppressedtime(Calendar suppressedtime) {
		this.suppressedtime = suppressedtime;
	}
	
	
	
	public SMSSuppressedContacts() { }
	
	
	
	public SMSSuppressedContacts(Users user, String mobile, String type) {
		// TODO Auto-generated constructor stub
		this.mobile = mobile;
		this.user = user;
		this.type = type;
		
		
	}



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

//changes 2.5.3.0 start

	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}
	
//changes 2.5.3.0 end	
}
