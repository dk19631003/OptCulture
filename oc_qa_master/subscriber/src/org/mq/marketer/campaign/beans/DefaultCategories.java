package org.mq.marketer.campaign.beans;

public class DefaultCategories implements java.io.Serializable{
	private long categoryId;
	private String categoryName;
	private String description;
	
	public DefaultCategories(){
		
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
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
	

}
