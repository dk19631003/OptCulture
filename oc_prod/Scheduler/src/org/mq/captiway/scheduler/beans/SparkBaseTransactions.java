package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class SparkBaseTransactions {
	
	
	private Long id;
	private String transactionId;
	private String locationId;
//	private String cardNumber;
	private String locationName;
	private String cardId;
	private String type;
	private Double amountEntered;
	private Calendar processedTime;
	private Calendar serverTime;
	private Double difference;
	private Double loyaltyBalance;
	private Double giftcardBalance;
	private Calendar createdDate;
	private String status;
	private Long contactId;
	private Long userId; 
	private String triggerIds;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	/*public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}*/
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getAmountEntered() {
		return amountEntered;
	}
	public void setAmountEntered(Double amountEntered) {
		this.amountEntered = amountEntered;
	}
	public Calendar getProcessedTime() {
		return processedTime;
	}
	public void setProcessedTime(Calendar processedTime) {
		this.processedTime = processedTime;
	}
	public Double getDifference() {
		return difference;
	}
	public void setDifference(Double difference) {
		this.difference = difference;
	}
	
	public Double getLoyaltyBalance() {
		return loyaltyBalance;
	}
	public void setLoyaltyBalance(Double loyaltyBalance) {
		this.loyaltyBalance = loyaltyBalance;
	}
	public Double getGiftcardBalance() {
		return giftcardBalance;
	}
	public void setGiftcardBalance(Double giftcardBalance) {
		this.giftcardBalance = giftcardBalance;
	}
	
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Calendar getServerTime() {
		return serverTime;
	}
	public void setServerTime(Calendar serverTime) {
		this.serverTime = serverTime;
	}
	
	
	public String getTriggerIds() {
		return triggerIds;
	}
	public void setTriggerIds(String triggerIds) {
		this.triggerIds = triggerIds;
	}
	
	

}
