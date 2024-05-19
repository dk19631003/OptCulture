package org.mq.marketer.campaign.beans;

import java.io.Serializable;

public class SocialAccountPageSettings implements Serializable{
	
	Long id;
	String userId;
	String profilePageName;
	String profilePageId;
	String profilePageType;
	
	public SocialAccountPageSettings() {
		// TODO Auto-generated constructor stub
	}
	
	public SocialAccountPageSettings(String userId,String profilePageName, String profilePageId,String profilePageType) {
		
		this.userId = userId;
		this.profilePageName = profilePageName;
		this.profilePageId = profilePageId;
		this.profilePageType = profilePageType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProfilePageName() {
		return profilePageName;
	}

	public void setProfilePageName(String profilePageName) {
		this.profilePageName = profilePageName;
	}

	public String getProfilePageId() {
		return profilePageId;
	}

	public void setProfilePageId(String profilePageId) {
		this.profilePageId = profilePageId;
	}

	public String getProfilePageType() {
		return profilePageType;
	}

	public void setProfilePageType(String profilePageType) {
		this.profilePageType = profilePageType;
	}
	
	
	
	
}
