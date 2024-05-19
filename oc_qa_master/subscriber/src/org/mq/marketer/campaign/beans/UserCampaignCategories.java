package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class UserCampaignCategories implements java.io.Serializable{
	private Long id;
	private String categoryName;
	private String description;
	private String parentStr;
	private boolean isVisible;
	private long userId;
	private Calendar createdDate;
	private String type;
	private long parentPositionId;
	
	public UserCampaignCategories(){
		
	}
	public UserCampaignCategories(Long userId){
		this.userId =userId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getParentStr() {
		return parentStr;
	}
	public void setParentStr(String parentStr) {
		this.parentStr = parentStr;
	}
	public boolean getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public long getParentPositionId() {
		return parentPositionId;
	}
	public void setParentPositionId(long parentPositionId) {
		this.parentPositionId = parentPositionId;
	}

}
