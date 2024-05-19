package org.mq.loyality.common.hbmbean;

import java.io.Serializable;
import java.util.Calendar;

public class UserOrganization implements Serializable {

	private Long userOrgId;
	private String organizationName;
	private String orgExternalId;
	private Calendar createdDate;
	private String clientType;
	private String  msgReceivingNumbers;
	
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
	private int minNumberOfDigits;
	private int maxNumberOfDigits;
	private boolean mobilePattern; 
	//private String msgReceivingNumber;
	
	//private boolean enableAlerts;
	
	private boolean requireMobileValidation=true;
	
	/*public boolean isEnableAlerts() {
		return enableAlerts;
	}
	public void setEnableAlerts(boolean enableAlerts) {
		this.enableAlerts = enableAlerts;
	}*/
	
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
	public int getMinNumberOfDigits() {
		return minNumberOfDigits;
	}
	public void setMinNumberOfDigits(int minNumberOfDigits) {
		this.minNumberOfDigits = minNumberOfDigits;
	}
	public int getMaxNumberOfDigits() {
		return maxNumberOfDigits;
	}
	public void setMaxNumberOfDigits(int maxNumberOfDigits) {
		this.maxNumberOfDigits = maxNumberOfDigits;
	}
	public boolean isMobilePattern() {
		return mobilePattern;
	}
	public void setMobilePattern(boolean mobilePattern) {
		this.mobilePattern = mobilePattern;
	}
	public boolean isRequireMobileValidation() {
		return requireMobileValidation;
	}
	public void setRequireMobileValidation(boolean requireMobileValidation) {
		this.requireMobileValidation = requireMobileValidation;
	}
	
}
