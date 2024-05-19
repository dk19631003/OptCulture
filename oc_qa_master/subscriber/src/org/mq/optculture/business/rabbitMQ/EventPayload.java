package org.mq.optculture.business.rabbitMQ;

public class EventPayload {
	
	private String userId;
	private String cid;
	private CustomerInfo customerInfo;
	private LoyaltyInfo loyaltyInfo;
	private SaleInfo saleInfo;
	
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public LoyaltyInfo getLoyaltyInfo() {
		return loyaltyInfo;
	}
	public void setLoyaltyInfo(LoyaltyInfo loyaltyInfo) {
		this.loyaltyInfo = loyaltyInfo;
	}
	public SaleInfo getSaleInfo() {
		return saleInfo;
	}
	public void setSaleInfo(SaleInfo saleInfo) {
		this.saleInfo = saleInfo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
