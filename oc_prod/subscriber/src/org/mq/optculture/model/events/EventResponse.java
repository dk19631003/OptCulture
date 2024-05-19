package org.mq.optculture.model.events;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;


public class EventResponse extends BaseResponseObject {
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public List<Event> getMatchedEvents() {
		return matchedEvents;
	}
	public void setMatchedEvents(List<Event> matchedEvents) {
		this.matchedEvents = matchedEvents;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	private Header header;
	private List<Event> matchedEvents;
	private Status status;

}
