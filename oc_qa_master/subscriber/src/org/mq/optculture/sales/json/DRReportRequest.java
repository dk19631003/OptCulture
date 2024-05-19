package org.mq.optculture.sales.json;

import org.mq.optculture.model.BaseRequestObject;

public class DRReportRequest extends BaseRequestObject {
	private static final long serialVersionUID = 1L;
	String requestedAction;
	Long sentId;
	String url;
	
	public String getRequestedAction() {
		return requestedAction;
	}
	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}
	public Long getSentId() {
		return sentId;
	}
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
