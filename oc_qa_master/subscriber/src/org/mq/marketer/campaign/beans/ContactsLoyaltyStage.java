package org.mq.marketer.campaign.beans;

public class ContactsLoyaltyStage implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6963544406213866782L;
	private Long loyaltyStageId;
	private Long contactId;
	private String customerId;
	private String emailId;
	private String phoneNumber;
	private String cardNumber;
	private String cardPin;
	private String locationId;
	private String userName;
	private String status;
	private Long trxId;
	private String serviceType;//to differentiate between oc/SB
	private String reqType;//to differentiate between enrollment & transfer

	public Long getTrxId() {
		return trxId;
	}
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}
	public ContactsLoyaltyStage() {
	}
	public Long getLoyaltyStageId() {
		return loyaltyStageId;
	}
	public void setLoyaltyStageId(Long loyaltyStageId) {
		this.loyaltyStageId = loyaltyStageId;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getCardPin() {
		return cardPin;
	}
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getReqType() {
		return reqType;
	}
	public void setReqType(String reqType) {
		this.reqType = reqType;
	}
}
