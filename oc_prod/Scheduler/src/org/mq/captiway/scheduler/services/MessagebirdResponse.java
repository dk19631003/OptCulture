package org.mq.captiway.scheduler.services;

public class MessagebirdResponse {

	
	
	private String id;
	private String href;
	private String direction;
	private String type;
	private String originator;
	private String body;
	private String reference;
	private String gateway;
	private String datacoding;
	private String mclass;
	private String createdDatetime;
	private Recipients recipients;
	
	public Recipients getRecipients() {
		return recipients;
	}
	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDatacoding() {
		return datacoding;
	}
	public void setDatacoding(String datacoding) {
		this.datacoding = datacoding;
	}
	public String getMclass() {
		return mclass;
	}
	public void setMclass(String mclass) {
		this.mclass = mclass;
	}

	
	
	public String getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(String createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
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
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	
	
	
	
	
	
	
	
}
