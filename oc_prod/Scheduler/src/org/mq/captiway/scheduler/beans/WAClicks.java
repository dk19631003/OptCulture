package org.mq.captiway.scheduler.beans;

import java.util.Calendar;


public class WAClicks {

	private Long clickId;
	private WACampaignSent sentId;
	private String clickUrl;
	private Calendar clickDate;
	private Long waCampUrlId;


	public WAClicks() {

	}

	public WAClicks(WACampaignSent sentId) {

		this.sentId = sentId;
	}

	public WAClicks(WACampaignSent sentId, String clickUrl, Long waCampUrlId, Calendar clickDate) {

		this.sentId = sentId;
		this.clickUrl = clickUrl;
		this.clickDate = clickDate;
		this.waCampUrlId = waCampUrlId;
	}



	public Long getClickId() {
		return clickId;
	}
	public void setClickId(Long clickId) {
		this.clickId = clickId;
	}
	public WACampaignSent getSentId() {
		return sentId;
	}
	public void setSentId(WACampaignSent sentId) {
		this.sentId = sentId;
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}
	public Calendar getClickDate() {
		return clickDate;
	}
	public void setClickDate(Calendar clickDate) {
		this.clickDate = clickDate;
	}

	public Long getWaCampUrlId() {
		return waCampUrlId;
	}

	public void setWaCampUrlId(Long waCampUrlId) {
		this.waCampUrlId = waCampUrlId;
	}




}
