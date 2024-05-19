package org.mq.optculture.model.DR.wooCommerce;

import java.util.List;

public class WooCommerceCustomerDetails {
	private String customer_id;
	private String email;
	private String first_name;
	private String last_name;
	private String note;
	private WooCommerceBillingAddress billing_address;
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public WooCommerceBillingAddress getBilling_address() {
		return billing_address;
	}
	public void setBilling_address(WooCommerceBillingAddress billing_address) {
		this.billing_address = billing_address;
	}
	
}
