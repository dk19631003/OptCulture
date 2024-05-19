package org.mq.marketer.campaign.beans;

public class UserAgentJSON implements java.io.Serializable {
	
	private Long uajsonId;
	private String userAgentStr;
	private String jsonStr;
	
	public UserAgentJSON(){}
  
	
	public UserAgentJSON(String userAgentStr, String jsonStr) {
		super();
		this.userAgentStr = userAgentStr;
		this.jsonStr = jsonStr;
	}


	public Long getUajsonId() {
		return uajsonId;
	}

	public void setUajsonId(Long uajsonId) {
		this.uajsonId = uajsonId;
	}

	public String getUserAgentStr() {
		return userAgentStr;
	}

	public void setUserAgentStr(String userAgentStr) {
		this.userAgentStr = userAgentStr;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}
}
