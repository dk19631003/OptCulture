package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GetTokenRequestLog {

	private Long id;
	private String jsonRequest;
	private Calendar requestDate;
	private String jsonResponse;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJsonRequest() {
		return jsonRequest;
	}
	public void setJsonRequest(String jsonRequest) {
		this.jsonRequest = jsonRequest;
	}
	public Calendar getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}
	public String getJsonResponse() {
		return jsonResponse;
	}
	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
	
	
}
