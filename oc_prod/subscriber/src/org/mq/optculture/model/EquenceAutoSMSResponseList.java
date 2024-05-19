package org.mq.optculture.model;

public class EquenceAutoSMSResponseList {



	//{"response":[{"destination":"917330819589","id":0,"mrid":"8906211255150155480","segment":0,"status":"success"}]}
	
	private String destination;
	private String id;
	private String mrId; 
	private String segment;
	private String dispatch;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getMrId() {
		return mrId;
	}

	public void setMrId(String mrId) {
		this.mrId = mrId;
	}


}
