package org.mq.optculture.model.autoEmail;

import org.mq.optculture.model.BaseRequestObject;

public class AutoEmailReportRequest extends BaseRequestObject  {
private String action;
private String sentId;
private String url;
public String getAction() {
	return action;
}
public void setAction(String action) {
	this.action = action;
}
public String getSentId() {
	return sentId;
}
public void setSentId(String sentId) {
	this.sentId = sentId;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
}
