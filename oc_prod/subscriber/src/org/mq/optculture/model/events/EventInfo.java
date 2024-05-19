package org.mq.optculture.model.events;
import java.util.Calendar;

public class EventInfo {
	private String date;
	private String eventStatus; //All/Active/Inactive   // If empty we consider all.
	private String searchEvent;    
	private String zipCode;
	private String city;
	private String state;
	private String store;
	private String isOneDay;

	
	public String getIsOneDay() {
		return isOneDay;
	}
	public void setIsOneDay(String isOneDay) {
		this.isOneDay = isOneDay;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	public String getSearchEvent() {
		return searchEvent;
	}
	public void setSearchEvent(String searchEvent) {
		this.searchEvent = searchEvent;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	
	
}
