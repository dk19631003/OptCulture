package org.mq.captiway.scheduler.services;

import java.util.ArrayList;
import java.util.List;

public class PrepareMessageBirdSingleJsonRequest {

	private String recipients;
	private String originator;
	private String body;
	private String reference;
	private String reportUrl;
	
	
	
	public static List<PrepareMessageBirdSingleJsonRequest> convert(PrepareMessageBirdJsonRequest pj) {
		
		List<PrepareMessageBirdSingleJsonRequest> msgbirdlist = new ArrayList<PrepareMessageBirdSingleJsonRequest>();
	
		for (MessageBirdTextList msgobj: pj.getMessages() ) {
			
			PrepareMessageBirdSingleJsonRequest msgbirdobj=new PrepareMessageBirdSingleJsonRequest();
			
						
			//msgbirdobj.setAccesskey(pj.getAccesskey()); //take from password
		
			msgbirdobj.setRecipients(msgobj.getRecipients());
			msgbirdobj.setOriginator(msgobj.getOriginator());
			msgbirdobj.setBody(msgobj.getBody());
			msgbirdobj.setReference(msgobj.getReference());
			msgbirdobj.setReportUrl(msgobj.getReportUrl());
			
			msgbirdlist.add(msgbirdobj);
			
		}
		
	
	return msgbirdlist;
	}
	
	
	
	
	public String getOriginator() {
		return originator;
	}


	public void setOriginator(String originator) {
		this.originator = originator;
	}

	
	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
	public  PrepareMessageBirdSingleJsonRequest() {
	
	}
	
	
	
}
