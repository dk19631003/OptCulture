package org.mq.optculture.model.events;

import java.util.Calendar;

public class Events {

	private Long eventId;
	private Long userId;
	private String eventTitle;
	private String eventStatus;
	private Calendar eventCreateDate;
	private Calendar eventStartDate;
	private Calendar eventEndDate;
	private String subtitle;
	private String description;
	private String addressLine1;
	private String addressLine2;
	private String state;
	private Long zipCode;
	private String store;
	private String city;
	private Boolean isOneDay;
	private String dirId;
	private String leftLable;
	private String leftLableURL;
	private String rightLable;
	private String rightLableURL;
	private String eventStartDateAPI;//Only for APIs
	private String eventEndDateAPI;
	
	
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getEventTitle() {
		return eventTitle;
	}
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}
	public Calendar getEventCreateDate() {
		return eventCreateDate;
	}
	public void setEventCreateDate(Calendar eventCreateDate) {
		this.eventCreateDate = eventCreateDate;
	}
	public Calendar getEventStartDate() {
		return eventStartDate;
	}
	public void setEventStartDate(Calendar eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
	public Calendar getEventEndDate() {
		return eventEndDate;
	}
	public void setEventEndDate(Calendar eventEndDate) {
		this.eventEndDate = eventEndDate;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getZipCode() {
		return zipCode;
	}
	public void setZipCode(Long zipCode) {
		this.zipCode = zipCode;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getEventStatus() {
		return eventStatus;
	}
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Boolean getIsOneDay() {
		return isOneDay;
	}
	public void setIsOneDay(Boolean isOneDay) {
		this.isOneDay = isOneDay;
	}
	public String getDirId() {
		return dirId;
	}
	public void setDirId(String dirId) {
		this.dirId = dirId;
	}
	public String getLeftLable() {
		return leftLable;
	}
	public void setLeftLable(String leftLable) {
		this.leftLable = leftLable;
	}
	public String getLeftLableURL() {
		return leftLableURL;
	}
	public void setLeftLableURL(String leftLableURL) {
		this.leftLableURL = leftLableURL;
	}
	public String getRightLable() {
		return rightLable;
	}
	public void setRightLable(String rightLable) {
		this.rightLable = rightLable;
	}
	public String getRightLableURL() {
		return rightLableURL;
	}
	public void setRightLableURL(String rightLableURL) {
		this.rightLableURL = rightLableURL;
	}
	public String getEventStartDateAPI() {
		return eventStartDateAPI;
	}
	public void setEventStartDateAPI(String eventStartDateAPI) {
		this.eventStartDateAPI = eventStartDateAPI;
	}
	public String getEventEndDateAPI() {
		return eventEndDateAPI;
	}
	public void setEventEndDateAPI(String eventEndDateAPI) {
		this.eventEndDateAPI = eventEndDateAPI;
	}
	
	
}
