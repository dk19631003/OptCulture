package org.mq.captiway.scheduler.beans;

import java.util.Calendar;


public class CouponCodes implements java.io.Serializable, Comparable<CouponCodes>{
	
	private Long ccId;
	private String couponCode;
	private Coupons couponId;
	private Long sentId;
	private String campaignType;
	private Long orgId;
	private String status;
	
	private Calendar issuedOn;
	private Calendar redeemedOn;
	private Double totDiscount;
	private Double totRevenue;
	private String issuedTo; 
	private String campaignName;
	
	private Double usedLoyaltyPoints; 
	private Long contactId;
	private String docSid;
	private String redeemedTo;
	private Calendar expiredOn;


	public CouponCodes() {}
	
	
	
	public Long getCcId() {
		return ccId;
	}

	public void setCcId(Long ccId) {
		this.ccId = ccId;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Coupons getCouponId() {
		return couponId;
	}


	public void setCouponId(Coupons couponId) {
		this.couponId = couponId;
	}


	public Long getSentId() {
		return sentId;
	}

	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}

	public String getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getIssuedOn() {
		return issuedOn;
	}

	public void setIssuedOn(Calendar issuedOn) {
		this.issuedOn = issuedOn;
	}

	public Calendar getRedeemedOn() {
		return redeemedOn;
	}

	public void setRedeemedOn(Calendar redeemedOn) {
		this.redeemedOn = redeemedOn;
	}

	public Double getTotDiscount() {
		return totDiscount;
	}

	public void setTotDiscount(Double totDiscount) {
		this.totDiscount = totDiscount;
	}

	public Double getTotRevenue() {
		return totRevenue;
	}

	public void setTotRevenue(Double totRevenue) {
		this.totRevenue = totRevenue;
	}

	public String getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	
	public Double getUsedLoyaltyPoints() {
		return usedLoyaltyPoints;
	}

	public void setUsedLoyaltyPoints(Double usedLoyaltyPoints) {
		this.usedLoyaltyPoints = usedLoyaltyPoints;
	}

	@Override
	public int compareTo(CouponCodes cc) {
		if(cc.getCouponCode()==null || this.couponCode==null) return 0;
		return cc.getCouponCode().compareTo(this.couponCode);	
	}



	public Long getContactId() {
		return contactId;
	}



	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}



	public String getDocSid() {
		return docSid;
	}



	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}



	public String getRedeemedTo() {
		return redeemedTo;
	}



	public void setRedeemedTo(String redeemedTo) {
		this.redeemedTo = redeemedTo;
	}	
	
	public Calendar getExpiredOn() {
		return expiredOn;
	}



	public void setExpiredOn(Calendar expiredOn) {
		this.expiredOn = expiredOn;
	}

	
}
