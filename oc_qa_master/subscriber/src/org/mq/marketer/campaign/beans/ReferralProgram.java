package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.List;
/**
 * 
 * @author Adarsh Kumar G
 * It handles data of Referral program settings.
 */
public class ReferralProgram {

	private Long referralId;
	private String programName;
	private String modifiedBy;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Calendar startDate;
	private Calendar endDate;
	private Long userId;
	private Long  orgId;
	private Long  couponId;
	
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	private String  discountforReferrertype;
	private Double  discountforReferrervalue;
	
	private String rewardonReferraltype;
	public String getRewardonReferraltype() {
		return rewardonReferraltype;
	}
	public void setRewardonReferraltype(String rewardonReferraltype) {
		this.rewardonReferraltype = rewardonReferraltype;
	}
	public String getRewardonReferralVC() {
		return rewardonReferralVC;
	}
	public void setRewardonReferralVC(String rewardonReferralVC) {
		this.rewardonReferralVC = rewardonReferralVC;
	}
	public String getRewardonReferralValue() {
		return rewardonReferralValue;
	}
	public void setRewardonReferralValue(String rewardonReferralValue) {
		this.rewardonReferralValue = rewardonReferralValue;
	}

	private String rewardonReferralVC;
	private String rewardonReferralValue;

	

	
	
	
	public Double getDiscountforReferrervalue() {
		return discountforReferrervalue;
	}
	public void setDiscountforReferrervalue(Double discountforReferrervalue) {
		this.discountforReferrervalue = discountforReferrervalue;
	}

	private String  refereeMPV;
	
	private String  status;
	private boolean noLimit;

	

	

	
	/*
	 * public boolean getnoLimit() { return noLimit; }
	 */
	
	
	public boolean isNoLimit() {
		return noLimit;
	}
	public void setNoLimit(boolean noLimit) {
		this.noLimit = noLimit;
	}
	/*
	 * public void setNoLimit(boolean noLimit) { this.noLimit = noLimit; }
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getDiscountforReferrertype() {
		return discountforReferrertype;
	}
	public void setDiscountforReferrertype(String discountforReferrertype) {
		this.discountforReferrertype = discountforReferrertype;
	}
	
	
	
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public String getRefereeMPV() {
		return refereeMPV;
	}
	public void setRefereeMPV(String refereeMPV) {
		this.refereeMPV = refereeMPV;
	}

	
	
	

	//private Long discountValue;
	
	
	
	
	
	
	/*
	 * public Long getDiscountValue() { return discountValue; } public void
	 * setDiscountValue(Long discountValue) { this.discountValue = discountValue; }
	 */	/*public Double getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}
	public Double getMinimumAmount() {
		return minimumAmount;
	}
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}*/
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	
	
	
	
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}

	
	public Long getReferralId() {
		return referralId;
	}
	public void setReferralId(Long referralId) {
		this.referralId = referralId;
	}

	public ReferralProgram() {
	
	}

	
	

	
}


