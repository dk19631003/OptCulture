package org.mq.captiway.scheduler.services;

import java.util.List;

import org.mq.captiway.scheduler.services.EquenceTextList;;

public class PrepareEquenceJsonRequest {

	/*{
	"username":"****",
	"password":"****",
	"from":"SMSTST",
	"textlist":[                                                                                 
	                   {"to":"9206774674","text":"hi test message1"},
	                   {"to":"8895801942","text":"hi test message2"},
	                   {"to":"7377069728","text":"hi test message3"}]
}*/
	private String username; 
	private String password;
	private String from;
	private List<EquenceTextList> textlist;
	private String peId;
	private String tmplId;
	
	public PrepareEquenceJsonRequest(){
	//Default Constructor
	}
	
	public String getUsername() {
	return username;
	}
	
	public void setUsername(String username) {
	this.username = username;
	}
	
	public String getPassword() {
	return password;
	}
	
	public void setPassword(String password) {
	this.password = password;
	}
	
	public String getFrom() {
	return from;
	}
	
	public void setFrom(String from) {
	this.from = from;
	}
	
	public List<EquenceTextList> getTextlist() {
	return textlist;
	}
	
	public void setTextlist(List<EquenceTextList> textlist) {
	this.textlist = textlist;
	}

	public String getPeId() {
		return peId;
	}

	public void setPeId(String peId) {
		this.peId = peId;
	}

	public String getTmplId() {
		return tmplId;
	}

	public void setTmplId(String tmplId) {
		this.tmplId = tmplId;
	}
	}
