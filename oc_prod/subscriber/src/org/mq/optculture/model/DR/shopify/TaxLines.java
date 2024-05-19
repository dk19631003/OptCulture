package org.mq.optculture.model.DR.shopify;

public class TaxLines {
	
	private String title;	
	private String price;	
	private String rate;
	private PriceSet price_set;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public PriceSet getPrice_set() {
		return price_set;
	}
	public void setPrice_set(PriceSet price_set) {
		this.price_set = price_set;
	}
}
