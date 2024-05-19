package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class PromoGeneration {
	
	
	public PromoGeneration(){}
	
	private String  redeemCount;// PRMO_REDEEM_COUNT
	private String redemtionflag;//PROMO_REDEEM_FLAG
	private String redemtionLimit;//PROMO_REDEMTION_LIMIT
	private String perSubcriptionFlag;//PER_SUBSCRIPTION_FLAG
	private String perSubscriptionLimit; //PER_SUBSCRPTION_LIMIT
	
	@XmlElement(name="PRMO_REDEEM_COUNT")
	public String getRedeemCount() {
		return redeemCount;
	}
	public void setRedeemCount(String redeemCount) {
		this.redeemCount = redeemCount;
	}
	
	@XmlElement(name="PROMO_REDEEM_FLAG")
	public String getRedemtionflag() {
		return redemtionflag;
	}
	public void setRedemtionflag(String redemtionflag) {
		this.redemtionflag = redemtionflag;
	}
	
	
	@XmlElement(name="PROMO_REDEMTION_LIMIT")
	public String getRedemtionLimit() {
		return redemtionLimit;
	}
	public void setRedemtionLimit(String redemtionLimit) {
		this.redemtionLimit = redemtionLimit;
	}
	
	
	@XmlElement(name="PER_SUBSCRIPTION_FLAG")
	public String getPerSubcriptionFlag() {
		return perSubcriptionFlag;
	}
	public void setPerSubcriptionFlag(String perSubcriptionFlag) {
		this.perSubcriptionFlag = perSubcriptionFlag;
	}
	
	@XmlElement(name="PER_SUBSCRPTION_LIMIT")
	public String getPerSubscriptionLimit() {
		return perSubscriptionLimit;
	}
	public void setPerSubscriptionLimit(String perSubscriptionLimit) {
		this.perSubscriptionLimit = perSubscriptionLimit;
	}
}
