package org.mq.optculture.business.helper.DRToLty;


public class DRToLoyaltyExtractionItems {

	private String itemCategory; 
	private String departmentCode;
	private String itemClass;
	private String itemSubClass; 
	private String DCS;
	private String vendorCode;
	private String skuNumber;
	private String billedUnitPrice;
	private String quantity;
	private String itemPromoDiscount;
	public String getItemPromoDiscount() {
		return itemPromoDiscount;
	}


	public void setItemPromoDiscount(String itemPromoDiscount) {
		this.itemPromoDiscount = itemPromoDiscount;
	}
	//newly added
	private String tax;
	private String discount;
	private String discount_reason;
	private String departmentName;
	private String itemClassName;
	private String itemSubClassName;
	private String vendorName;
	private String itemSID;
	private String RefStoreCode;
	private String RefSubsidiaryNumber;
	private String RefReceipt;
	private String RefDocSID;
	private String DocItemOrigPrc;
	private String DocItemPrc;
	private String InvcItemPrc;
	private String itemNote;
	private boolean isNonInventory;
private String itemType;
	
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	public boolean isNonInventory() {
		return isNonInventory;
	}
	public void setNonInventory(boolean isNonInventory) {
		this.isNonInventory = isNonInventory;
	}
	public String getItemCategory() {
		return itemCategory;
	}
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getItemClass() {
		return itemClass;
	}
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}
	public String getItemSubClass() {
		return itemSubClass;
	}
	public void setItemSubClass(String itemSubClass) {
		this.itemSubClass = itemSubClass;
	}
	public String getDCS() {
		return DCS;
	}
	public void setDCS(String dCS) {
		DCS = dCS;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getSkuNumber() {
		return skuNumber;
	}
	public void setSkuNumber(String skuNumber) {
		this.skuNumber = skuNumber;
	}
	public String getBilledUnitPrice() {
		return billedUnitPrice;
	}
	public void setBilledUnitPrice(String billedUnitPrice) {
		this.billedUnitPrice = billedUnitPrice;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
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
	public String getDiscount_reason() {
		return discount_reason;
	}
	public void setDiscount_reason(String discount_reason) {
		this.discount_reason = discount_reason;
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
	public String getItemSID() {
		return itemSID;
	}
	public void setItemSID(String itemSID) {
		this.itemSID = itemSID;
	}
	
	public DRToLoyaltyExtractionItems(String itemCategory, String departmentCode, String itemClass, String itemSubClass,
			String dCS, String vendorCode, String skuNumber, String billedUnitPrice, String quantity, String tax,
			String discount, String departmentName, String itemClassName, String itemSubClassName, String vendorName,
			String itemSID, String refStoreCode, String refSubsidiaryNumber, String refReceipt, String refDocSID,String discount_reason) {
		super();
		this.itemCategory = itemCategory;
		this.departmentCode = departmentCode;
		this.itemClass = itemClass;
		this.itemSubClass = itemSubClass;
		DCS = dCS;
		this.vendorCode = vendorCode;
		this.skuNumber = skuNumber;
		this.billedUnitPrice = billedUnitPrice;
		this.quantity = quantity;
		this.tax = tax;
		this.discount = discount;
		this.departmentName = departmentName;
		this.itemClassName = itemClassName;
		this.itemSubClassName = itemSubClassName;
		this.vendorName = vendorName;
		this.itemSID = itemSID;
		RefStoreCode = refStoreCode;
		RefSubsidiaryNumber = refSubsidiaryNumber;
		RefReceipt = refReceipt;
		RefDocSID = refDocSID;
		this.discount_reason = discount_reason;
	}
	public String getRefStoreCode() {
		return RefStoreCode;
	}
	public void setRefStoreCode(String refStoreCode) {
		RefStoreCode = refStoreCode;
	}
	public String getRefSubsidiaryNumber() {
		return RefSubsidiaryNumber;
	}
	public void setRefSubsidiaryNumber(String refSubsidiaryNumber) {
		RefSubsidiaryNumber = refSubsidiaryNumber;
	}
	public String getRefReceipt() {
		return RefReceipt;
	}
	public void setRefReceipt(String refReceipt) {
		RefReceipt = refReceipt;
	}
	public String getRefDocSID() {
		return RefDocSID;
	}
	public void setRefDocSID(String refDocSID) {
		RefDocSID = refDocSID;
	}
	public String getDocItemOrigPrc() {
		return DocItemOrigPrc;
	}
	public void setDocItemOrigPrc(String docItemOrigPrc) {
		DocItemOrigPrc = docItemOrigPrc;
	}
	public String getDocItemPrc() {
		return DocItemPrc;
	}
	public void setDocItemPrc(String docItemPrc) {
		DocItemPrc = docItemPrc;
	}
	public String getInvcItemPrc() {
		return InvcItemPrc;
	}
	public void setInvcItemPrc(String invcItemPrc) {
		InvcItemPrc = invcItemPrc;
	}
	public String getItemNote() {
		return itemNote;
	}
	public void setItemNote(String itemNote) {
		this.itemNote = itemNote;
	}
}
