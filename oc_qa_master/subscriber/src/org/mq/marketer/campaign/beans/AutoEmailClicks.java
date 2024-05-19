package org.mq.marketer.campaign.beans;

public class AutoEmailClicks {
private Long clickId;
private Long eqId;
private String clickUrl;
private int clickCount;
public Long getClickId() {
	return clickId;
}

public void setClickId(Long clickId) {
	this.clickId = clickId;
}

public AutoEmailClicks(Long sentId, String url) {
	this.eqId = sentId;
	this.clickUrl = url;
}

public AutoEmailClicks() {
}

public Long getEqId() {
	return eqId;
}
public void setEqId(Long eqId) {
	this.eqId = eqId;
}
public String getClickUrl() {
	return clickUrl;
}
public void setClickUrl(String clickUrl) {
	this.clickUrl = clickUrl;
}
public int getClickCount() {
	return clickCount;
}
public void setClickCount(int clickCount) {
	this.clickCount = clickCount;
}
}
