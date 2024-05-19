package org.mq.optculture.model.couponcodes;
import javax.xml.bind.annotation.XmlElement;
public class Coupon {
	private String coupon_code;
	private String friendly_name;
	private String title;
	private String subtitle;
	private String tags;
	private String coupon_value;
	private String custom_validation_code;
	private String customid;
	private String timezone;
	
	public String getCoupon_code() {
		return coupon_code;
	}
	@XmlElement(name ="coupon_code")
	public void setCoupon_code(String coupon_code) {
		this.coupon_code = coupon_code;
	}
	public String getFriendly_name() {
		return friendly_name;
	}
	@XmlElement(name = "friendly_name")
	public void setFriendly_name(String friendly_name) {
		this.friendly_name = friendly_name;
	}
	public String getTitle() {
		return title;
	}
	@XmlElement(name = "title")
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	@XmlElement(name = "subtitle")
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getTags() {
		return tags;
	}
	@XmlElement(name = "tags")
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getCoupon_value() {
		return coupon_value;
	}
	@XmlElement(name = "coupon_value")
	public void setCoupon_value(String coupon_value) {
		this.coupon_value = coupon_value;
	}
	public String getCustom_validation_code() {
		return custom_validation_code;
	}
	@XmlElement(name = "custom_validation_code")
	public void setCustom_validation_code(String custom_validation_code) {
		this.custom_validation_code = custom_validation_code;
	}
	public String getCustomid() {
		return customid;
	}
	@XmlElement(name = "customid")
	public void setCustomid(String customid) {
		this.customid = customid;
	}
	public String getTimezone() {
		return timezone;
	}
	@XmlElement(name = "timezone")
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
}
