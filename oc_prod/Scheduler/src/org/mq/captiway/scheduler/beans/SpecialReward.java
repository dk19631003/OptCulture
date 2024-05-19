package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class SpecialReward {


	 private Long rewardId;
	 private String rewardName;
	 private String rewardRule;
	 private String description;
	 private String rewardType;
	 private String rewardValueCode;
	 private String rewardValue;
	 private String rewardExpiryType;
	 private String rewardExpiryValue;
	 private String autoCommEmail;
	 private String autoCommSMS;
	 private Set<LoyaltyProgram> loyaltyPrograms= new HashSet<>();
	 private String orgId;
	 private String createdBy;
	 private Calendar createdDate;
	 private String statusSpecialReward;
	 private Boolean deductItemPrice;
	 private Double totItemPrice;
	 private Boolean enableReturnOnCurrentRule=false;
	 public Boolean getEnableReturnOnCurrentRule() {
		return enableReturnOnCurrentRule;
	}
	public void setEnableReturnOnCurrentRule(Boolean enableReturnOnCurrentRule) {
		this.enableReturnOnCurrentRule = enableReturnOnCurrentRule;
	}
	private int countMultiplier;
public SpecialReward(){}
   public SpecialReward(String rewardName, String rewardRule, String description, String rewardType,
			String rewardValueCode, String rewardValue, String rewardExpiryType, String rewardExpiryValue,
			String autoCommEmail, String autoCommSMS, Set<LoyaltyProgram> loyaltyPrograms, String orgId,
			String createdBy, Calendar createdDate, String statusSpecialReward, Boolean deductItemPrice,
			Boolean enableReturnOnCurrentRule) {
		super();
		this.rewardName = rewardName;
		this.rewardRule = rewardRule;
		this.description = description;
		this.rewardType = rewardType;
		this.rewardValueCode = rewardValueCode;
		this.rewardValue = rewardValue;
		this.rewardExpiryType = rewardExpiryType;
		this.rewardExpiryValue = rewardExpiryValue;
		this.autoCommEmail = autoCommEmail;
		this.autoCommSMS = autoCommSMS;
		this.loyaltyPrograms = loyaltyPrograms;
		this.orgId = orgId;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.statusSpecialReward = statusSpecialReward;
		this.deductItemPrice = deductItemPrice;
		this.totItemPrice = totItemPrice;
		this.enableReturnOnCurrentRule = enableReturnOnCurrentRule;
		this.countMultiplier = countMultiplier;
	}
	public int getCountMultiplier() {
		return countMultiplier;
	}
	public void setCountMultiplier(int countMultiplier) {
		this.countMultiplier = countMultiplier;
	}
	
	
	public Double getTotItemPrice() {
       return totItemPrice;
   }
   public void setTotItemPrice(Double totItemPrice) {
       this.totItemPrice = totItemPrice;
   }
	public Long getRewardId() {
		return rewardId;
	}
	public void setRewardId(Long rewardId) {
		this.rewardId = rewardId;
	}
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	public String getRewardRule() {
		return rewardRule;
	}
	public void setRewardRule(String rewardRule) {
		this.rewardRule = rewardRule;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRewardType() {
		return rewardType;
	}
	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}
	public String getRewardValueCode() {
		return rewardValueCode;
	}
	public void setRewardValueCode(String rewardValueCode) {
		this.rewardValueCode = rewardValueCode;
	}
	public String getRewardValue() {
		return rewardValue;
	}
	public void setRewardValue(String rewardValue) {
		this.rewardValue = rewardValue;
	}
	public String getRewardExpiryType() {
		return rewardExpiryType;
	}
	public void setRewardExpiryType(String rewardExpiryType) {
		this.rewardExpiryType = rewardExpiryType;
	}
	public String getRewardExpiryValue() {
		return rewardExpiryValue;
	}
	public void setRewardExpiryValue(String rewardExpiryValue) {
		this.rewardExpiryValue = rewardExpiryValue;
	}
	
	public Set<LoyaltyProgram> getLoyaltyPrograms() {
		return loyaltyPrograms;
	}
	public void setLoyaltyPrograms(Set<LoyaltyProgram> loyaltyPrograms) {
		this.loyaltyPrograms = loyaltyPrograms;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatusSpecialReward() {
		return statusSpecialReward;
	}
	public void setStatusSpecialReward(String statusSpecialReward) {
		this.statusSpecialReward = statusSpecialReward;
	}
	public String getAutoCommEmail() {
		return autoCommEmail;
	}
	public void setAutoCommEmail(String autoCommEmail) {
		this.autoCommEmail = autoCommEmail;
	}
	public String getAutoCommSMS() {
		return autoCommSMS;
	}
	public void setAutoCommSMS(String autoCommSMS) {
		this.autoCommSMS = autoCommSMS;
	}
	public Boolean isDeductItemPrice() {
		return deductItemPrice;
	}
	public void setDeductItemPrice(Boolean deductItemPrice) {
		this.deductItemPrice = deductItemPrice;
	}
	 
	

}