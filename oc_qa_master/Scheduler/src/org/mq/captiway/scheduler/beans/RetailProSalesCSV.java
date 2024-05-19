package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Set;

public class RetailProSalesCSV implements java.io.Serializable {
	
	private Long salesId;

	private String customerId;
	private String recieptNumber;
	private Calendar salesDate;

	private Double quantity = 0.0;
	private Double salesPrice = 0.0;
	private Double tax = 0.0;
	private String promoCode;
	private String storeNumber;
	private String sku;
	private String tenderType;
	private Long listId;//need to make this as user_id
	private Long userId;//changes made to remove the list based segment creation...

private String subsidiaryNumber;

	


	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}


	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;
	private String udf6;
	private String udf7;
	private String udf8;
	private String udf9;
	private String udf10;
	private String udf11;
	private String udf12;
	private String udf13;
	private String udf14;
	private String udf15;
	
	private Long externalId;
	private Long cid;
	private Long inventoryId;
	private String itemSid;
	private String docSid;
	private Double discount = 0.0;
	public Double getDiscount() {
		return discount;
	}


	public void setDiscount(Double discount) {
		this.discount = discount;
	}


	public RetailProSalesCSV() {} 
	
	
	public RetailProSalesCSV(String customerId,String recieptNumber, Calendar salesDate,Double quantity, Double salesPrice, Double tax, 
							String promoCode, String storeNumber, String sku, String tenderType ,Long listId, String udf1, String udf2, 
							String udf3, String udf4, String udf5, String udf6, String udf7, String udf8, String udf9 , String udf10, String udf11, String udf12,
							String udf13, String udf14, String udf15) {
		
		this.customerId = customerId;
		this.recieptNumber = recieptNumber;
		this.salesDate=salesDate;
		this.quantity= quantity;
		this.salesPrice = salesPrice;
		this.tax 		= tax;
		this.promoCode  = promoCode;
		this.storeNumber = storeNumber;
		this.sku	= sku;
		this.tenderType = tenderType;
		this.listId = listId;
		this.udf1 = udf1;
		this.udf2 = udf2;
		this.udf3 = udf3;
		this.udf4 = udf4;
		this.udf5 = udf5;
		this.udf6 = udf6;
		this.udf7 = udf7;
		this.udf8 =udf8;
		this.udf9 = udf9;
		this.udf10 = udf10;
		this.udf11=udf11;
		this.udf12=udf12;
		this.udf13=udf13;
		this.udf14=udf14;
		this.udf15=udf15;
		
	}


	public Long getSalesId() {
		return salesId;
	}


	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getRecieptNumber() {
		return recieptNumber;
	}


	public void setRecieptNumber(String recieptNumber) {
		this.recieptNumber = recieptNumber;
	}


	public Calendar getSalesDate() {
		return salesDate;
	}


	public void setSalesDate(Calendar salesDate) {
		this.salesDate = salesDate;
	}

	public Double getQuantity() {
		return quantity;
	}


	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}


	public Double getSalesPrice() {
		return salesPrice;
	}


	public void setSalesPrice(Double salesPrice) {
		this.salesPrice = salesPrice;
	}


	public Double getTax() {
		return tax;
	}


	public void setTax(Double tax) {
		this.tax = tax;
	}


	public String getPromoCode() {
		return promoCode;
	}


	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}


	public String getStoreNumber() {
		return storeNumber;
	}


	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}


	public String getSku() {
		return sku;
	}


	public void setSku(String sku) {
		this.sku = sku;
	}


	public String getTenderType() {
		return tenderType;
	}


	public void setTenderType(String tenderType) {
		this.tenderType = tenderType;
	}



	public Long getListId() {
		return listId;
	}


	public void setListId(Long listId) {
		this.listId = listId;
	}


	//added for CC
	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getUdf1() {
		return udf1;
	}


	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}


	public String getUdf2() {
		return udf2;
	}


	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}


	public String getUdf3() {
		return udf3;
	}


	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}


	public String getUdf4() {
		return udf4;
	}


	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}


	public String getUdf5() {
		return udf5;
	}


	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}


	public String getUdf6() {
		return udf6;
	}


	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}


	public String getUdf7() {
		return udf7;
	}


	public void setUdf7(String udf7) {
		this.udf7 = udf7;
	}


	public String getUdf8() {
		return udf8;
	}


	public void setUdf8(String udf8) {
		this.udf8 = udf8;
	}


	public String getUdf9() {
		return udf9;
	}


	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}


	public String getUdf10() {
		return udf10;
	}


	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}


	public String getUdf11() {
		return udf11;
	}


	public void setUdf11(String udf11) {
		this.udf11 = udf11;
	}


	public String getUdf12() {
		return udf12;
	}


	public void setUdf12(String udf12) {
		this.udf12 = udf12;
	}


	public String getUdf13() {
		return udf13;
	}


	public void setUdf13(String udf13) {
		this.udf13 = udf13;
	}


	public String getUdf14() {
		return udf14;
	}


	public void setUdf14(String udf14) {
		this.udf14 = udf14;
	}


	public String getUdf15() {
		return udf15;
	}


	public void setUdf15(String udf15) {
		this.udf15 = udf15;
	}
	
	public Long getExternalId() {
		return externalId;
	}


	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}


	public Long getCid() {
		return cid;
	}


	public void setCid(Long cid) {
		this.cid = cid;
	}


	public Long getInventoryId() {
		return inventoryId;
	}


	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}


	public String getItemSid() {
		return itemSid;
	}


	public void setItemSid(String itemSid) {
		this.itemSid = itemSid;
	}
	
	
	public String getDocSid() {
		return docSid;
	}


	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}

	
}
