package org.mq.optculture.model.DR.wooCommerce;

public class WooCommerceRefundData {
	private String refund_item_id;
	private String qty; //coming as negative value 
	public String getRefund_item_id() {
		return refund_item_id;
	}
	public void setRefund_item_id(String refund_item_id) {
		this.refund_item_id = refund_item_id;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
}
