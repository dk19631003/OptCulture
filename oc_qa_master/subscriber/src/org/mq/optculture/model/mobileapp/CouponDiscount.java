package org.mq.optculture.model.mobileapp;

public class CouponDiscount {

	private String value;
	private String valueCode;
	private String minimumPurchaseAmount;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public String getMinimumPurchaseAmount() {
		return minimumPurchaseAmount;
	}
	public void setMinimumPurchaseAmount(String minimumPurchaseAmount) {
		this.minimumPurchaseAmount = minimumPurchaseAmount;
	}
	
}
