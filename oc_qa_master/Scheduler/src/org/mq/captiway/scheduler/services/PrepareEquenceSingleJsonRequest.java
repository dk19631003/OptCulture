package org.mq.captiway.scheduler.services;

import java.util.ArrayList;
import java.util.List;

public class PrepareEquenceSingleJsonRequest {

	
	    /*{
		"username":"*****",
		"password":"******",
		"from":"SMSTST",
		"to":"920677****",
		"text":"hi",
		"MSGID":"1234567890abcd"
		}*/
	
	private String username; 
	private String password;
	private String from;
	private String peId;
	private String to;
	private String text;
	private String tmplId;
	private String MSGID;
	
	
	
	
	
	
	public static List<PrepareEquenceSingleJsonRequest> convert(PrepareEquenceJsonRequest pj) {
		
		List<PrepareEquenceSingleJsonRequest> msglist = new ArrayList<PrepareEquenceSingleJsonRequest>();
	
		for (EquenceTextList msg: pj.getTextlist() ) {
			
			PrepareEquenceSingleJsonRequest equobj=new PrepareEquenceSingleJsonRequest();
			
			equobj.setUsername(pj.getUsername());
			equobj.setPassword(pj.getPassword());
			equobj.setFrom(pj.getFrom());
			equobj.setPeId(pj.getPeId());
		//	equobj.setTmplId(pj.getTmplId());
			equobj.setTo(msg.getTo());
			equobj.setText(msg.getText());
			equobj.setTmplId(msg.getTmplId());

			
			msglist.add(equobj);
			
		}
		
	
	return msglist;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public PrepareEquenceSingleJsonRequest(){
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

	public String getPeId() {
		return peId;
	}

	public void setPeId(String peId) {
		this.peId = peId;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMSGID() {
		return MSGID;
	}

	public void setMSGID(String mSGID) {
		MSGID = mSGID;
	}

	public String getTmplId() {
		return tmplId;
	}

	public void setTmplId(String tmplId) {
		this.tmplId = tmplId;
	}
	
	}
