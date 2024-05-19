package org.mq.marketer.campaign.beans;

import java.util.Calendar;


public class WAOpens {

	private Long openId;
	private WACampaignSent sentId;
	private Calendar openDate;


	public WAOpens() {
	}


	public WAOpens(WACampaignSent sentId) {
		this.sentId = sentId;
	}
	public WAOpens(WACampaignSent sentId, Calendar openDate) {
		this.sentId = sentId;
		this.openDate = openDate;
	}


	public Long getOpenId() {
		return openId;
	}


	public void setOpenId(Long openId) {
		this.openId = openId;
	}


	public WACampaignSent getSentId() {
		return sentId;
	}


	public void setSentId(WACampaignSent sentId) {
		this.sentId = sentId;
	}


	public Calendar getOpenDate() {
		return openDate;
	}


	public void setOpenDate(Calendar openDate) {
		this.openDate = openDate;
	}



}
