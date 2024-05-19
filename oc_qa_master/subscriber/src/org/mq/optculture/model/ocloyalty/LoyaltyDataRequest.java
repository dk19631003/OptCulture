package org.mq.optculture.model.ocloyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyDataRequest extends BaseRequestObject{
	
	private static final long serialVersionUID = 5190524497983077963L;
	
	private RequestHeader header;
	private LoyaltyReport report;
	private LoyaltyUser user;
	
	public RequestHeader getHeader() {
		return header;
	}
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
	public LoyaltyReport getReport() {
		return report;
	}
	public void setReport(LoyaltyReport report) {
		this.report = report;
	}
	public LoyaltyUser getUser() {
		return user;
	}
	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

}
