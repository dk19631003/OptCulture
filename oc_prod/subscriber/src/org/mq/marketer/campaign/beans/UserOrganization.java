package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class UserOrganization implements Serializable {

	private Long userOrgId;
	private String organizationName;
	private String orgExternalId;
	private Calendar createdDate;
	private String clientType;
	private String  msgReceivingNumbers;
	private boolean multiUser;
	private String loyaltyDisplayTemplate;
	private String FBPTemplate;


	public String getBannerPath() {
		return bannerPath;
	}
	public void setBannerPath(String bannerPath) {
		this.bannerPath = bannerPath;
	}
	private String bannerPath;
	
	public boolean isMultiUser() {
		return multiUser;
	}
	public void setMultiUser(boolean multiUser) {
		this.multiUser = multiUser;
	}
	private String branding;
	
	
	//added for  optSync
	private String optSyncKey;
	
	//private String enabledOptSyncFlag;
	
	//  to denote the users and user organizations "Deleted".
	private String orgStatus;
	
	// added for SMS
	private String toEmailId;
	private int maxKeywords;
	private Long cardId;
	private Long nextCardSeqNo;
	private String cardGenerateFlag;
	private Long cardSeqPrefix;
	private Long cardRandPrefix;
	//private String msgReceivingNumber;
	
	//private boolean enableAlerts;
	
	/*public boolean isEnableAlerts() {
		return enableAlerts;
	}
	public void setEnableAlerts(boolean enableAlerts) {
		this.enableAlerts = enableAlerts;
	}*/
	private int minNumberOfDigits;
	private int maxNumberOfDigits;
	private boolean mobilePattern; 
	private boolean requireMobileValidation=true;
	public boolean sendRealtimeLoyaltyStatus=true;
	
	public boolean isSendRealtimeLoyaltyStatus() {
		return sendRealtimeLoyaltyStatus;
	}
	public void setSendRealtimeLoyaltyStatus(boolean sendRealtimeLoyaltyStatus) {
		this.sendRealtimeLoyaltyStatus = sendRealtimeLoyaltyStatus;
	}
	public boolean isRequireMobileValidation() {
		return requireMobileValidation;
	}
	public void setRequireMobileValidation(boolean requireMobileValidation) {
		this.requireMobileValidation = requireMobileValidation;
	}
	private boolean crossProgramCardTransfer;
	public boolean isCrossProgramCardTransfer() {
		return crossProgramCardTransfer;
	}
	public void setCrossProgramCardTransfer(boolean crossProgramCardTransfer) {
		this.crossProgramCardTransfer = crossProgramCardTransfer;
	}
	/*public boolean isSuspendedCardTransfer() {
		return suspendedCardTransfer;
	}
	public void setSuspendedCardTransfer(boolean suspendedCardTransfer) {
		this.suspendedCardTransfer = suspendedCardTransfer;
	}

	public boolean suspendedCardTransfer;
	*/
	public boolean suspendedProgramTransfer;

	
	public boolean isSuspendedProgramTransfer() {
		return suspendedProgramTransfer;
	}
	public void setSuspendedProgramTransfer(boolean suspendedProgramTransfer) {
		this.suspendedProgramTransfer = suspendedProgramTransfer;
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
	
	
	public String getOptSyncKey() {
		return optSyncKey;
	}
	public void setOptSyncKey(String optSyncKey) {
		this.optSyncKey = optSyncKey;
	}
	
	
	
	/*public String getEnabledOptSyncFlag() {
		return enabledOptSyncFlag;
	}
	public void setEnabledOptSyncFlag(String enabledOptSyncFlag) {
		this.enabledOptSyncFlag = enabledOptSyncFlag;
	}*/

	public String getOrgStatus() {
		return orgStatus;
	}
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
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
	public String getLoyaltyDisplayTemplate() {
		return loyaltyDisplayTemplate;
	}
	public void setLoyaltyDisplayTemplate(String loyaltyDisplayTemplate) {
		this.loyaltyDisplayTemplate = loyaltyDisplayTemplate;
	}
	public String getFBPTemplate() {
		return FBPTemplate;
	}
	public void setFBPTemplate(String fBPTemplate) {
		FBPTemplate = fBPTemplate;
	}

}//EOF
