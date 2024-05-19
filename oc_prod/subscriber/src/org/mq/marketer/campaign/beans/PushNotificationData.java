package org.mq.marketer.campaign.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PushNotificationData {
	private String title;
	private String message;
	private String image;
	private String style;
	private String body;
	private String summaryText;
	 @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int visibility;
	 @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int notId;
	 @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int priority;
	private String url;
	private String image_url_jpg;
	private String image_url_png;
	private String image_url_gif;
	private boolean mutable_content;



	public boolean isMutable_content() {
		return mutable_content;
	}

	public void setMutable_content(boolean mutable_content) {
		this.mutable_content = mutable_content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSummaryText() {
		return summaryText;
	}

	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getNotId() {
		return notId;
	}

	public void setNotId(int notId) {
		this.notId = notId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage_url_jpg() {
		return image_url_jpg;
	}

	public void setImage_url_jpg(String image_url_jpg) {
		this.image_url_jpg = image_url_jpg;
	}

	public String getImage_url_png() {
		return image_url_png;
	}

	public void setImage_url_png(String image_url_png) {
		this.image_url_png = image_url_png;
	}

	public String getImage_url_gif() {
		return image_url_gif;
	}

	public void setImage_url_gif(String image_url_gif) {
		this.image_url_gif = image_url_gif;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
