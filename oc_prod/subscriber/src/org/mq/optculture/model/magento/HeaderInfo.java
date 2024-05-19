package org.mq.optculture.model.magento;

public class HeaderInfo {

	private String requestId;
	private String requestType;
	private String sourceType;
	
	public HeaderInfo(String requestId, String requestType) {
		super();
		this.requestId = requestId;
		this.requestType = requestType;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
}
