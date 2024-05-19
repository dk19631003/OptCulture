package org.mq.optculture.model;

public class MessagebirdResponse {
	
	
	
	
	private String id;
	private String href;
	private String direction;
	private String type;
	private String originator;
	private String body;
	private String reference;
	
	
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
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


}
