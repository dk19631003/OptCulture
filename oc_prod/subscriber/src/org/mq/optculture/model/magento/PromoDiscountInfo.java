package org.mq.optculture.model.magento;


public class PromoDiscountInfo {

	private String tpaTotalDiscountAmount;
	private String itemsTotalDiscountAmount;
	
	public PromoDiscountInfo() {
	}
	public PromoDiscountInfo(String tpaTotalDiscountAmount,
			String itemsTotalDiscountAmount) {
		this.tpaTotalDiscountAmount = tpaTotalDiscountAmount;
		this.itemsTotalDiscountAmount = itemsTotalDiscountAmount;
	}
	public String getTpaTotalDiscountAmount() {
		return tpaTotalDiscountAmount;
	}
	public void setTpaTotalDiscountAmount(String tpaTotalDiscountAmount) {
		this.tpaTotalDiscountAmount = tpaTotalDiscountAmount;
	}
	public String getItemsTotalDiscountAmount() {
		return itemsTotalDiscountAmount;
	}
	public void setItemsTotalDiscountAmount(String itemsTotalDiscountAmount) {
		this.itemsTotalDiscountAmount = itemsTotalDiscountAmount;
	}

}
