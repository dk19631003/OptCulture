package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class PromoTrxLog {

	private Long id;
	private String requestId;
	private String jsonRequest;
	private String jsonResponse;
	private String type; //issuance, enrollment
	private String mode; //online, offline
	private Boolean pcFlag; //possibly completed flag.
	private Calendar requestDate;
	private String userDetail;
	private String docSID;
	private String storeNumber;
	private String customerId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getJsonRequest() {
		return jsonRequest;
	}
	public void setJsonRequest(String jsonRequest) {
		this.jsonRequest = jsonRequest;
	}
	public String getJsonResponse() {
		return jsonResponse;
	}
	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Boolean getPcFlag() {
		return pcFlag;
	}
	public void setPcFlag(Boolean pcFlag) {
		this.pcFlag = pcFlag;
	}
	public Calendar getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}
	public String getUserDetail() {
		return userDetail;
	}
	public void setUserDetail(String userDetail) {
		this.userDetail = userDetail;
	}
	public String getDocSID() {
		return docSID;
	}
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
	
}
