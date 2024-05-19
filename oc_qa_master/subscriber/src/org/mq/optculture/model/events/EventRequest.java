package org.mq.optculture.model.events;

import org.mq.optculture.model.events.Header;

public class EventRequest {
	private Header header;
	private EventInfo eventInfo;
	private SortBy sortBy;
	private User user;

	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public EventInfo getEventInfo() {
		return eventInfo;
	}
	public void setEventInfo(EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public SortBy getSortBy() {
		return sortBy;
	}
	public void setSortBy(SortBy sortBy) {
		this.sortBy = sortBy;
	}
}
