package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.BaseRequestObject;

public class CouponsHistoryRequest extends BaseRequestObject{
	
	private  RequestHeader header;
	private Lookup lookup;
	private User user;
	
	public RequestHeader getHeader() {
		return header;
	}
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
	public Lookup getLookup() {
		return lookup;
	}
	public void setLookup(Lookup lookup) {
		this.lookup = lookup;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	

}
