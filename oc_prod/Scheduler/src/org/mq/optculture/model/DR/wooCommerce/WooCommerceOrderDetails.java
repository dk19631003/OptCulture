package org.mq.optculture.model.DR.wooCommerce;

public class WooCommerceOrderDetails {
	
	private String order_id;
	private String created_at;
	private String updated_at;
	private String completed_at;
	private String status;
	private String currecny;
	private String total;
	private String subtotal;
	private String total_line_items_quantity;
	private String total_tax;
	private String total_shipping;
	private String cart_tax;
	private String shipping_tax;
	private String total_discount;
	private String shipping_methods;
	private String order_key;
	//List<WooCommerceShippingLines> shipping_lines;
	private String tax_lines;
	private String fee_lines;
	//List<WooCommerceCouponLines> coupon_lines;
	//List<WooCommercePaymentDetails> payment_details;
	private String increment_id;
	private String store_id;
	private WooCommerceRefundDetails refund_details;
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getCompleted_at() {
		return completed_at;
	}
	public void setCompleted_at(String completed_at) {
		this.completed_at = completed_at;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCurrecny() {
		return currecny;
	}
	public void setCurrecny(String currecny) {
		this.currecny = currecny;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getTotal_line_items_quantity() {
		return total_line_items_quantity;
	}
	public void setTotal_line_items_quantity(String total_line_items_quantity) {
		this.total_line_items_quantity = total_line_items_quantity;
	}
	public String getTotal_tax() {
		return total_tax;
	}
	public void setTotal_tax(String total_tax) {
		this.total_tax = total_tax;
	}
	public String getTotal_shipping() {
		return total_shipping;
	}
	public void setTotal_shipping(String total_shipping) {
		this.total_shipping = total_shipping;
	}
	public String getCart_tax() {
		return cart_tax;
	}
	public void setCart_tax(String cart_tax) {
		this.cart_tax = cart_tax;
	}
	public String getShipping_tax() {
		return shipping_tax;
	}
	public void setShipping_tax(String shipping_tax) {
		this.shipping_tax = shipping_tax;
	}
	public String getTotal_discount() {
		return total_discount;
	}
	public void setTotal_discount(String total_discount) {
		this.total_discount = total_discount;
	}
	public String getShipping_methods() {
		return shipping_methods;
	}
	public void setShipping_methods(String shipping_methods) {
		this.shipping_methods = shipping_methods;
	}
	public String getOrder_key() {
		return order_key;
	}
	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}
	public String getTax_lines() {
		return tax_lines;
	}
	public void setTax_lines(String tax_lines) {
		this.tax_lines = tax_lines;
	}
	public String getFee_lines() {
		return fee_lines;
	}
	public void setFee_lines(String fee_lines) {
		this.fee_lines = fee_lines;
	}
	public String getIncrement_id() {
		return increment_id;
	}
	public void setIncrement_id(String increment_id) {
		this.increment_id = increment_id;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public WooCommerceRefundDetails getRefund_details() {
		return refund_details;
	}
	public void setRefund_details(WooCommerceRefundDetails refund_details) {
		this.refund_details = refund_details;
	}

}
