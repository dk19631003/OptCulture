package org.mq.optculture.model.autoEmail;

import org.mq.optculture.model.BaseResponseObject;

public class AutoEmailReportResponse extends BaseResponseObject {
	private String responseType;
	private String status;
	private String urlStr;
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

}
