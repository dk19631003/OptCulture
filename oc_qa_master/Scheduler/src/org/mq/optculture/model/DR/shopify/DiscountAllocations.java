package org.mq.optculture.model.DR.shopify;

public class DiscountAllocations {
	
	private String amount;	
	private String discount_application_index;	
	private AmountSet amount_set;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDiscount_application_index() {
		return discount_application_index;
	}
	public void setDiscount_application_index(String discount_application_index) {
		this.discount_application_index = discount_application_index;
	}
	public AmountSet getAmount_set() {
		return amount_set;
	}
	public void setAmount_set(AmountSet amount_set) {
		this.amount_set = amount_set;
	}

}
