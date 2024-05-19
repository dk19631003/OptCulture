package org.mq.optculture.model.DR.wooCommerce;

public class WooCommerceReturnDRBody {
	private WooCommerceCustomerDetails customerdetails;
	private String Membership;
	private WooCommerceOrderDetails orderdetails;
	private WooCommerceReturnOrderItems orderitems;
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
	public String getMembership() {
		return Membership;
	}
	public void setMembership(String membership) {
		Membership = membership;
	}
}
