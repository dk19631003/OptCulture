package org.mq.marketer.campaign.beans;

public class UsersAdditionalContactDetails  implements java.io.Serializable {
	Long additionalContactId;
	String name;
	String number;
	String email;
	String position;
	int priorityLevel;
	Long userId;
	public Long getAdditionalContactId() {
		return additionalContactId;
	}
	public void setAdditionalContactId(Long additionalContactId) {
		this.additionalContactId = additionalContactId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
