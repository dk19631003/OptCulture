package org.mq.optculture.sales.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", skipFirstLine = true)
public class SalesDetails {
	
	
	@DataField(pos = 1)
	private String storeNumber;
	@DataField(pos = 2)
	private String saleDate;
	@DataField(pos = 3)
	private String receiptNumber;
	@DataField(pos = 4)
	private String TenderType;
	@DataField(pos = 5)
	private String Discount;
	@DataField(pos = 6)
	private String udf1;
	@DataField(pos = 7)
	private String itemSid;
	@DataField(pos = 8)
	private String Description;
	@DataField(pos = 9)
	private String udf2;
	@DataField(pos = 10)
	private String qty;
	@DataField(pos = 11)
	private String salePrice;
	@DataField(pos = 12)
	private String createdDate;
	
	@DataField(pos = 13)
	private String homeStore;
	@DataField(pos = 14)
	private String customerId;
	@DataField(pos = 15)
	private String firstName;
	@DataField(pos = 16)
	private String lastName;
	@DataField(pos = 17)
	private String email;
	@DataField(pos = 18)
	private String mobile;
	@DataField(pos = 19)
	private String subsidiary;
	@DataField(pos = 20)
	private String category;
		
	
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getTenderType() {
		return TenderType;
	}
	public void setTenderType(String tenderType) {
		TenderType = tenderType;
	}
	public String getDiscount() {
		return Discount;
	}
	public void setDiscount(String discount) {
		Discount = discount;
	}
	public String getUdf1() {
		return udf1;
	}
	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}
	public String getItemSid() {
		return itemSid;
	}
	public void setItemSid(String itemSid) {
		this.itemSid = itemSid;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getUdf2() {
		return udf2;
	}
	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getHomeStore() {
		return homeStore;
	}
	public void setHomeStore(String homeStore) {
		this.homeStore = homeStore;
	}

	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSubsidiary() {
		return subsidiary;
	}
	public void setSubsidiary(String subsidiary) {
		this.subsidiary = subsidiary;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	@Override
	public String toString() {
		return "Sales [storeNumber=" + storeNumber + ", saleDate=" + saleDate + ", receiptNumber=" + receiptNumber
				+ ", TenderType=" + TenderType + ", Discount=" + Discount + ", udf1=" + udf1 + ", itemSid=" + itemSid
				+ ", Description=" + Description + ", udf2=" + udf2 + ", qty=" + qty + ", salePrice=" + salePrice
				+ ", createdDate=" + createdDate + ", customerId=" + customerId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", mobile=" + mobile + ", subsidiary=" + subsidiary
				+ ", category=" + category + "]";
	}
	
}
