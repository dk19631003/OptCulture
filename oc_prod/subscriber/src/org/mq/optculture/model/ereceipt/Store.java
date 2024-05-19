package org.mq.optculture.model.ereceipt;

public class Store {

	private String erpStoreCode;
	private String brand;
	private String country;
	private String subsidiaryNumber;
	private String storeNumber;
	private String subsidiaryName;
	private String storeName;
	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getSubsidiaryName() {
		return subsidiaryName;
	}
	public void setSubsidiaryName(String subsidiaryName) {
		this.subsidiaryName = subsidiaryName;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public String getErpStoreCode() {
		return erpStoreCode;
	}
	public void setErpStoreCode(String erpStoreCode) {
		this.erpStoreCode = erpStoreCode;
	}
	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	
}
