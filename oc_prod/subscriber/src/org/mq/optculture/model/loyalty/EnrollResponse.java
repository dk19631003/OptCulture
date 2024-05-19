package org.mq.optculture.model.loyalty;

public class EnrollResponse {
	private String cardNumber;
	private String cardPin;
	private String storeLocationId;
	private String errorCode;
	private String errorMessage;
	private String status;
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
	public String getStoreLocationId() {
		return storeLocationId;
	}
	public void setStoreLocationId(String storeLocationId) {
		this.storeLocationId = storeLocationId;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
