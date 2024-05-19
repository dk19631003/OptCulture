package org.mq.optculture.model.DR.shopify;

public class RefundItems {
	private String quantity;
	private String line_item_id;
	private String subtotal;
	private String total_tax;
	private ShopifyItems line_item;
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getLine_item_id() {
		return line_item_id;
	}
	public void setLine_item_id(String line_item_id) {
		this.line_item_id = line_item_id;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getTotal_tax() {
		return total_tax;
	}
	public void setTotal_tax(String total_tax) {
		this.total_tax = total_tax;
	}
	public ShopifyItems getLine_item() {
		return line_item;
	}
	public void setLine_item(ShopifyItems line_item) {
		this.line_item = line_item;
	} 
}
