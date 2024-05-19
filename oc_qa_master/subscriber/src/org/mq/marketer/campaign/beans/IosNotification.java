package org.mq.marketer.campaign.beans;

public class IosNotification {
	
	private String title;
	private String body;
	private String sound;
	private boolean mutable_content;
	private String icon; //web 
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	private String image; //web
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public boolean isMutable_content() {
		return mutable_content;
	}
	public void setMutable_content(boolean mutable_content) {
		this.mutable_content = mutable_content;
	}
}
