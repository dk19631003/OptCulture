package org.mq.optculture.model.DR.wooCommerce;

import java.util.List;

public class WooCommerceReturnOrderItems {
	private List<WooCommerceOrderData> orderdata;
	private List<WooCommerceRefundData> refunddata;
	public List<WooCommerceOrderData> getOrderdata() {
		return orderdata;
	}
	public void setOrderdata(List<WooCommerceOrderData> orderdata) {
		this.orderdata = orderdata;
	}
	public List<WooCommerceRefundData> getRefunddata() {
		return refunddata;
	}
	public void setRefunddata(List<WooCommerceRefundData> refunddata) {
		this.refunddata = refunddata;
	}
}
