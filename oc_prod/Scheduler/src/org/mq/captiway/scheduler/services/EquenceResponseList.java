package org.mq.captiway.scheduler.services;

public class EquenceResponseList {
	private String mrId; 
	private String mrid; 
	public String getMrid() {
		return mrid;
	}

	public void setMrid(String mrid) {
		this.mrid = mrid;
	}

	private String id;
	private String seq;
	private String destination;
	private String dispatch;
	private String segment;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
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
