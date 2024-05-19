package org.mq.optculture.model.DR.shopify;

import java.util.List;

public class ShippingLine {
	private long id;
	private String carrier_identifier;
    private String code;
    private String delivery_category;
    private String discounted_price;
    private PriceSet discounted_price_set;
    private String phone;
    private String price;
    private PriceSet price_set;
    private String requested_fulfillment_service_id;
    private String source;
    private String title;
    private List<TaxLines> tax_lines;
    private List<DiscountAllocations> discount_allocations;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCarrier_identifier() {
		return carrier_identifier;
	}
	public void setCarrier_identifier(String carrier_identifier) {
		this.carrier_identifier = carrier_identifier;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDelivery_category() {
		return delivery_category;
	}
	public void setDelivery_category(String delivery_category) {
		this.delivery_category = delivery_category;
	}
	public String getDiscounted_price() {
		return discounted_price;
	}
	public void setDiscounted_price(String discounted_price) {
		this.discounted_price = discounted_price;
	}
	public PriceSet getDiscounted_price_set() {
		return discounted_price_set;
	}
	public void setDiscounted_price_set(PriceSet discounted_price_set) {
		this.discounted_price_set = discounted_price_set;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public PriceSet getPrice_set() {
		return price_set;
	}
	public void setPrice_set(PriceSet price_set) {
		this.price_set = price_set;
	}
	public String getRequested_fulfillment_service_id() {
		return requested_fulfillment_service_id;
	}
	public void setRequested_fulfillment_service_id(String requested_fulfillment_service_id) {
		this.requested_fulfillment_service_id = requested_fulfillment_service_id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<TaxLines> getTax_lines() {
		return tax_lines;
	}
	public void setTax_lines(List<TaxLines> tax_lines) {
		this.tax_lines = tax_lines;
	}
	public List<DiscountAllocations> getDiscount_allocations() {
		return discount_allocations;
	}
	public void setDiscount_allocations(List<DiscountAllocations> discount_allocations) {
		this.discount_allocations = discount_allocations;
	}
}