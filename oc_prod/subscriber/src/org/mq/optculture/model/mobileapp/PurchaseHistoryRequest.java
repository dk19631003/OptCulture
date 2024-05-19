package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.BaseRequestObject;

public class PurchaseHistoryRequest extends BaseRequestObject{

	private  RequestHeader header;
	private Lookup lookup;
	private RequestReport report;
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
	public RequestReport getReport() {
		return report;
	}
	public void setReport(RequestReport report) {
		this.report = report;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
