package org.mq.optculture.model.DR.shopify;

public class ShopifyClientDetails {
	
	private String accept_language;	
	private String browser_height;	
	private String browser_ip;	
	private String browser_width;
	private String session_hash;
	
	public String getAccept_language() {
		return accept_language;
	}
	public void setAccept_language(String accept_language) {
		this.accept_language = accept_language;
	}
	public String getBrowser_height() {
		return browser_height;
	}
	public void setBrowser_height(String browser_height) {
		this.browser_height = browser_height;
	}
	public String getBrowser_ip() {
		return browser_ip;
	}
	public void setBrowser_ip(String browser_ip) {
		this.browser_ip = browser_ip;
	}
	public String getBrowser_width() {
		return browser_width;
	}
	public void setBrowser_width(String browser_width) {
		this.browser_width = browser_width;
	}
	public String getSession_hash() {
		return session_hash;
	}
	public void setSession_hash(String session_hash) {
		this.session_hash = session_hash;
	}
	public String getUser_agent() {
		return user_agent;
	}
	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}
	public String getLanding_site_ref() {
		return landing_site_ref;
	}
	public void setLanding_site_ref(String landing_site_ref) {
		this.landing_site_ref = landing_site_ref;
	}
	public String getOrder_number() {
		return order_number;
	}
	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
	private String user_agent;	
	private String landing_site_ref;
	private String order_number;	

}
