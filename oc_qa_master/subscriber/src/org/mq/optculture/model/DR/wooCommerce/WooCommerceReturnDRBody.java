package org.mq.optculture.model.DR.wooCommerce;

public class WooCommerceReturnDRBody {
	private WooCommerceOrderDetails orderdetails;
	private WooCommerceReturnOrderItems orderitems;
	private WooCommerceCustomerDetails customerdetails;
	public WooCommerceOrderDetails getOrderdetails() {
		return orderdetails;
	}
	public void setOrderdetails(WooCommerceOrderDetails orderdetails) {
		this.orderdetails = orderdetails;
	}
	public WooCommerceReturnOrderItems getOrderitems() {
		return orderitems;
	}
	public void setOrderitems(WooCommerceReturnOrderItems orderitems) {
		this.orderitems = orderitems;
	}
	public WooCommerceCustomerDetails getCustomerdetails() {
		return customerdetails;
	}
	public void setCustomerdetails(WooCommerceCustomerDetails customerdetails) {
		this.customerdetails = customerdetails;
	}
}
