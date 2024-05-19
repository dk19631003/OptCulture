package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.List;

import org.mq.captiway.scheduler.beans.Coupons;
/**
 * 
 * @author Adarsh Kumar G
 * It handles data of Referral program settings.
 */
public class ReferralcodesIssued implements java.io.Serializable, Comparable<ReferralcodesIssued>{

	private Long referralCodeId;

	private String RefprogramName;
	
	private String Refcode;
	
	private Calendar issuedDate;

	private Long userId;
	private Long  orgId;

	private Coupons couponId;

	private Long  referredCId;

	
	//private Long  couponId;
	
	public Long getReferredCId() {
		return referredCId;
	}
	public void setReferredCId(Long referredCId) {
		this.referredCId = referredCId;
	}
	public Coupons getCouponId() {
		return couponId;
	}
	public void setCouponId(Coupons couponId) {
		this.couponId = couponId;
	}






	private String status;

	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}






	private String  campaignType;
	private String sentTo;
	
	
	public String getRefcode() {
		return Refcode;
	}
	public void setRefcode(String refcode) {
		Refcode = refcode;
	}
	public Long getReferralCodeId() {
		return referralCodeId;
	}
	public void setReferralCodeId(Long referralCodeId) {
		this.referralCodeId = referralCodeId;
	}
	public String getRefprogramName() {
		return RefprogramName;
	}
	public void setRefprogramName(String refprogramName) {
		RefprogramName = refprogramName;
	}
	public Calendar getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(Calendar issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getCampaignType() {
		return campaignType;
	}
	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}
	public String getSentTo() {
		return sentTo;
	}
	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}







	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
	
	
	
	public ReferralcodesIssued() {
	
	}
	@Override
	public int compareTo(ReferralcodesIssued cc) {
		// TODO Auto-generated method stub
		
		if(cc.getRefcode()==null || this.Refcode==null) return 0;
		return cc.getRefcode().compareTo(this.Refcode);	
	}

	
	

	
}



