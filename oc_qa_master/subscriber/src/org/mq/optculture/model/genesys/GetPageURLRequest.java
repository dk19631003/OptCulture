package org.mq.optculture.model.genesys;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseResponseObject;


public class GetPageURLRequest extends BaseResponseObject {
	private Header header;
	private BodyParams bodyParams;
	private User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public BodyParams getBodyParams() {
		return bodyParams;
	}
	public void setBodyParams(BodyParams bodyParams) {
		this.bodyParams = bodyParams;
	}
	
}
