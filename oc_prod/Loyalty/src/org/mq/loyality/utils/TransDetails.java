package org.mq.loyality.utils;




public class TransDetails  {
	private String recieptAmount;
	private Long recieptNumber;
	private String storeName;
	private String recieptDate;
	private String loyalityBal;
	private String docSid;
	private String transactionType;
	private String pointBal;
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getDocSid() {
		return docSid;
	}
	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}
	
	public String getRecieptAmount() {
		return recieptAmount;
	}
	public void setRecieptAmount(String recieptAmount) {
		this.recieptAmount = recieptAmount;
	}
	public Long getRecieptNumber() {
		return recieptNumber;
	}
	public void setRecieptNumber(Long recieptNumber) {
		this.recieptNumber = recieptNumber;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getRecieptDate() {
		return recieptDate;
	}
	public void setRecieptDate(String recieptDate) {
		this.recieptDate = recieptDate;
	}
	
	
	public String getLoyalityBal() {
		return loyalityBal;
	}
	public void setLoyalityBal(String loyalityBal) {
		this.loyalityBal = loyalityBal;
	}
	@Override
	public String toString() {
		return "TransDetails [recieptAmount=" + recieptAmount
				+ ", recieptNumber=" + recieptNumber + ", storeName="
				+ storeName + ", recieptDate=" + recieptDate + ", loyalityBal="
				+ loyalityBal + ", docSid=" + docSid + "]";
	}
	public String getPointBal() {
		return pointBal;
	}
	public void setPointBal(String pointBal) {
		this.pointBal = pointBal;
	}

	
	
	
	
	
	
	
	
	

}
