package org.mq.optculture.model.account;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.ocloyalty.RequestHeader;

public class OCUserLoginRequest extends BaseRequestObject{

	private RequestHeader header;
	public RequestHeader getHeader() {
		return header;
	}
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
	public OCUser getUser() {
		return user;
	}
	public void setUser(OCUser user) {
		this.user = user;
	}
	private OCUser user;
	
	
}
