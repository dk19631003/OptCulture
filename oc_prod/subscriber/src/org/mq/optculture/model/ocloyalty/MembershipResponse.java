package org.mq.optculture.model.ocloyalty;

import java.util.List;

public class MembershipResponse {

	private String cardNumber;
	//private String password;
	private String sessionID;
	private String cardPin;
	private String fingerprintValidation;
	private String phoneNumber;
	private String tierLevel;
	private String tierName;
	private String currentTierValue;
	private String nextTierName;
	private String nextTierMilestone;
	private String tierUpgradeCriteria;
	private String expiry;
	private List<String> transactions;
	public List<String> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<String> transactions) {
		this.transactions = transactions;
	}
	private String emailAddress;
	
	
	
	public MembershipResponse() {
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardPin() {
		return cardPin;
	}

	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTierLevel() {
		return tierLevel;
	}

	public void setTierLevel(String tierLevel) {
		this.tierLevel = tierLevel;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	
	/*public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}*/

	public String getFingerprintValidation() {
		return fingerprintValidation;
	}

	public void setFingerprintValidation(String fingerprintValidation) {
		this.fingerprintValidation = fingerprintValidation;
	}

	public String getNextTierMilestone() {
		return nextTierMilestone;
	}

	public void setNextTierMilestone(String nextTierMilestone) {
		this.nextTierMilestone = nextTierMilestone;
	}

	public String getTierUpgradeCriteria() {
		return tierUpgradeCriteria;
	}

	public void setTierUpgradeCriteria(String tierUpgradeCriteria) {
		this.tierUpgradeCriteria = tierUpgradeCriteria;
	}

	public String getCurrentTierValue() {
		return currentTierValue;
	}

	public void setCurrentTierValue(String currentTierValue) {
		this.currentTierValue = currentTierValue;
	}

	public String getNextTierName() {
		return nextTierName;
	}

	public void setNextTierName(String nextTierName) {
		this.nextTierName = nextTierName;
	}
public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	
	
}
