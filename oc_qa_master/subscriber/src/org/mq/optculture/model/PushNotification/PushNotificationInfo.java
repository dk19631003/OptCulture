package org.mq.optculture.model.PushNotification;

public class PushNotificationInfo {

	private String sentId;
	private String header;
	private String description;
	private String redirectUrl;
	private String logoImage; 
	private String bannerImage;
	private PushNotificationInfo pushNotificationInfo;
	private boolean isRead;

	public PushNotificationInfo() {
		
	}

	public PushNotificationInfo(String sentId, String header,
			String description, String redirectUrl, String logoImage,
			String bannerImage, PushNotificationInfo pushNotificationInfo) {
		super();
		this.sentId  = sentId;
		this.header   = header;
		this.description = description;
		this.redirectUrl= redirectUrl;
		this.logoImage = logoImage;
		this.bannerImage = bannerImage;
		this.pushNotificationInfo = pushNotificationInfo;
	}

	public String getSentId() {
		return sentId;
	}

	public void setSentId(String sentId) {
		this.sentId = sentId;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public PushNotificationInfo getPushNotificationInfo() {
		return pushNotificationInfo;
	}

	public void setPushNotificationInfo(PushNotificationInfo pushNotificationInfo) {
		this.pushNotificationInfo = pushNotificationInfo;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
}
