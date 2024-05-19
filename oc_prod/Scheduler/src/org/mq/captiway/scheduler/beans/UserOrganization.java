package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class UserOrganization {

	


	private Long userOrgId;
	private String organizationName;
	private String orgExternalId;
	private Calendar createdDate;
	
	private String branding;
	private String clientType;
	
	// added for SMS
	private String  msgReceivingNumbers;
	private String toEmailId;
	private int maxKeywords;
	
	private Long cardId;
	private Long nextCardSeqNo;
	private String cardGenerateFlag;
	private Long cardSeqPrefix;
	private Long cardRandPrefix;
	private int minNumberOfDigits;
	private int maxNumberOfDigits;
	private boolean mobilePattern;
	private boolean requireMobileValidation;
	private String bannerPath;
	public boolean sendRealtimeLoyaltyStatus=true;
	private String loyaltyDisplayTemplate;
	
	public String getLoyaltyDisplayTemplate() {
		return loyaltyDisplayTemplate;
	}
	public void setLoyaltyDisplayTemplate(String loyaltyDisplayTemplate) {
		this.loyaltyDisplayTemplate = loyaltyDisplayTemplate;
	}
	public boolean isSendRealtimeLoyaltyStatus() {
		return sendRealtimeLoyaltyStatus;
	}
	public void setSendRealtimeLoyaltyStatus(boolean sendRealtimeLoyaltyStatus) {
		this.sendRealtimeLoyaltyStatus = sendRealtimeLoyaltyStatus;
	}
	public String getBannerPath() {
		return bannerPath;
	}
	public void setBannerPath(String bannerPath) {
		this.bannerPath = bannerPath;
	}
	public boolean isRequireMobileValidation() {
		return requireMobileValidation;
	}
	public void setRequireMobileValidation(boolean requireMobileValidation) {
		this.requireMobileValidation = requireMobileValidation;
	}
	public Long getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(Long userOrgId) {
		this.userOrgId = userOrgId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getOrgExternalId() {
		return orgExternalId;
	}
	public void setOrgExternalId(String orgExternalId) {
		this.orgExternalId = orgExternalId;
	}
	public String getBranding() {
		return branding;
	}
	public void setBranding(String branding) {
		this.branding = branding;
	}
	
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	
	public String getMsgReceivingNumbers() {
		return msgReceivingNumbers;
	}
	public void setMsgReceivingNumbers(String msgReceivingNumbers) {
		this.msgReceivingNumbers = msgReceivingNumbers;
	}
	
	public String getToEmailId() {
		return toEmailId;
	}
	public void setToEmailId(String toEmailId) {
		this.toEmailId = toEmailId;
	}
	
	public int getMaxKeywords() {
		return maxKeywords;
	}
	public void setMaxKeywords(int maxKeywords) {
		this.maxKeywords = maxKeywords;
	}
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	public Long getNextCardSeqNo() {
		return nextCardSeqNo;
	}
	public void setNextCardSeqNo(Long nextCardSeqNo) {
		this.nextCardSeqNo = nextCardSeqNo;
	}
	
	public String getCardGenerateFlag() {
		return cardGenerateFlag;
	}
	public void setCardGenerateFlag(String cardGenerateFlag) {
		this.cardGenerateFlag = cardGenerateFlag;
	}
	public Long getCardSeqPrefix() {
		return cardSeqPrefix;
	}
	public void setCardSeqPrefix(Long cardSeqPrefix) {
		this.cardSeqPrefix = cardSeqPrefix;
	}
	public Long getCardRandPrefix() {
		return cardRandPrefix;
	}
	public void setCardRandPrefix(Long cardRandPrefix) {
		this.cardRandPrefix = cardRandPrefix;
	}
	/**
	 * @return the minNumberOfDigits
	 */
	public int getMinNumberOfDigits() {
		return minNumberOfDigits;
	}

	/**
	 * @param minNumberOfDigits the minNumberOfDigits to set
	 */
	public void setMinNumberOfDigits(int minNumberOfDigits) {
		this.minNumberOfDigits = minNumberOfDigits;
	}

	/**
	 * @return the maxNumberOfDigits
	 */
	public int getMaxNumberOfDigits() {
		return maxNumberOfDigits;
	}

	/**
	 * @param maxNumberOfDigits the maxNumberOfDigits to set
	 */
	public void setMaxNumberOfDigits(int maxNumberOfDigits) {
		this.maxNumberOfDigits = maxNumberOfDigits;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isMobilePattern() {
		return mobilePattern;
	}

	/**
	 * 
	 * @param mobilePattern
	 */
	public void setMobilePattern(boolean mobilePattern) {
		this.mobilePattern = mobilePattern;
	}

	
}//EOF
