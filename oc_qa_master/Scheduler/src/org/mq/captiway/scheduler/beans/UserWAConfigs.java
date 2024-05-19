package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class UserWAConfigs {

	private Long id;
	private Long userId;
	private Long orgId;
	private String provider;
	private String waAPIEndPoint;
	private String accessToken;
	private String fromId;
	
	private Calendar createdDate;
	private Calendar modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getWaAPIEndPoint() {
		return waAPIEndPoint;
	}

	public void setWaAPIEndPoint(String waAPIEndPoint) {
		this.waAPIEndPoint = waAPIEndPoint;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public UserWAConfigs() {}

}
