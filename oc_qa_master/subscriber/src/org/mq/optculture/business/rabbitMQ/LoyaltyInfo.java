package org.mq.optculture.business.rabbitMQ;

public class LoyaltyInfo {
	
	private String cardNumber;
	private String cardPin;
	private String valueCode;
	private String trxType;
	private String trxEarnings;
	private String trxRedemption;
	private String expiryDate;
	
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
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public String getTrxType() {
		return trxType;
	}
	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}
	public String getTrxEarnings() {
		return trxEarnings;
	}
	public void setTrxEarnings(String trxEarnings) {
		this.trxEarnings = trxEarnings;
	}
	public String getTrxRedemption() {
		return trxRedemption;
	}
	public void setTrxRedemption(String trxRedemption) {
		this.trxRedemption = trxRedemption;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

}
