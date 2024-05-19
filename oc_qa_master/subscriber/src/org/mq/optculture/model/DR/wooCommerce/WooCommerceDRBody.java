package org.mq.optculture.model.DR.wooCommerce;

import java.util.List;

public class WooCommerceDRBody {
	private WooCommerceOrderDetails orderdetails;
	private List<WooCommerceOrderItems> orderitems;
	private WooCommerceCustomerDetails customerdetails;
	private String Membership;
	public WooCommerceOrderDetails getOrderdetails() {
		return orderdetails;
	}
	public void setOrderdetails(WooCommerceOrderDetails orderdetails) {
		this.orderdetails = orderdetails;
	}
	public List<WooCommerceOrderItems> getOrderitems() {
		return orderitems;
	}
	public void setOrderitems(List<WooCommerceOrderItems> orderitems) {
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
