package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class SkuDetails {

	private String itemCategory; 
	private String departmentCode;
	private String itemClass;
	private String itemSubClass; 
	private String DCS;
	private String vendorCode;
	private String skuNumber;
	private String billedUnitPrice;
	private String quantity;
	
	//newly added
	private String tax;
	private String discount;
	private String departmentName;
	private String itemClassName;
	private String itemSubClassName;
	private String vendorName;
	private String itemSID;
	private OriginalReceipt originalReceipt;//added w.r.t to revised API ability to handle returned items of various receipts
	private String itemNote;
	public OriginalReceipt getOriginalReceipt() {
		return originalReceipt;
	}

	public void setOriginalReceipt(OriginalReceipt originalReceipt) {
		this.originalReceipt = originalReceipt;
	}

	public String getItemSID() {
		return itemSID;
	}

	public void setItemSID(String itemSID) {
		this.itemSID = itemSID;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}
	
	
	
	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
		
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getItemClassName() {
		return itemClassName;
	}

	public void setItemClassName(String itemClassName) {
		this.itemClassName = itemClassName;
	}

	public String getItemSubClassName() {
		return itemSubClassName;
	}

	public void setItemSubClassName(String itemSubClassName) {
		this.itemSubClassName = itemSubClassName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public SkuDetails() {
		//Default Constructor.
	}

	public String getItemCategory() {
		return itemCategory;
	}
	@XmlElement(name = "itemCategory")
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}
	@XmlElement(name = "departmentCode")
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getItemClass() {
		return itemClass;
	}
	@XmlElement(name = "itemClass")
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public String getItemSubClass() {
		return itemSubClass;
	}
	@XmlElement(name = "itemSubClass")
	public void setItemSubClass(String itemSubClass) {
		this.itemSubClass = itemSubClass;
	}

	public String getDCS() {
		return DCS;
	}
	@XmlElement(name = "DCS")
	public void setDCS(String DCS) {
		this.DCS = DCS;
	}

	public String getVendorCode() {
		return vendorCode;
	}
	@XmlElement(name = "vendorCode")
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getSkuNumber() {
		return skuNumber;
	}
	@XmlElement(name = "skuNumber")
	public void setSkuNumber(String skuNumber) {
		this.skuNumber = skuNumber;
	}

	public String getBilledUnitPrice() {
		return billedUnitPrice;
	}
	@XmlElement(name = "billedUnitPrice")
	public void setBilledUnitPrice(String billedUnitPrice) {
		this.billedUnitPrice = billedUnitPrice;
	}

	public String getQuantity() {
		return quantity;
	}
	@XmlElement(name = "quantity")
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getItemNote() {
		return itemNote;
	}

	public void setItemNote(String itemNote) {
		this.itemNote = itemNote;
	}

	
}