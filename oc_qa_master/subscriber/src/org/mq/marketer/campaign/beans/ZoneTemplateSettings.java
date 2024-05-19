package org.mq.marketer.campaign.beans;
// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1


import java.util.Calendar;


public class ZoneTemplateSettings  implements java.io.Serializable {


	private Long id;
	private Long zoneId;
	private String channel;
	private String senderORfrom;
	private String autoCommType;
	private String templateId;
	private Users userId;
	private UserOrganization orgId;
	private Calendar createdDate;
	private Calendar modifiedDate;

	public ZoneTemplateSettings() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSenderORfrom() {
		return senderORfrom;
	}

	public void setSenderORfrom(String senderORfrom) {
		this.senderORfrom = senderORfrom;
	}

	public String getAutoCommType() {
		return autoCommType;
	}

	public void setAutoCommType(String autoCommType) {
		this.autoCommType = autoCommType;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public Users getUserId() {
		return userId;
	}

	public void setUserId(Users userId) {
		this.userId = userId;
	}

	public UserOrganization getOrgId() {
		return orgId;
	}

	public void setOrgId(UserOrganization orgId) {
		this.orgId = orgId;
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

}


