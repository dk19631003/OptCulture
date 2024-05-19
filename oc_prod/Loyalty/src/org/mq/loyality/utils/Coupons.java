package org.mq.loyality.utils;

import java.util.Calendar;

public class Coupons implements java.io.Serializable{
	
	private Long couponId;
	private String couponName;
	private String couponDescription;
	private String status;
	private String couponGeneratedType;
	private String couponCode;
	private String discountType;
	private String discountCriteria;
	private Long totalQty;
	private Long generatedQty;
//	private String createUser;
	private Calendar userCreatedDate;
	private String lastModifiedUser;
	private Calendar userLastModifiedDate;
	private Calendar couponCreatedDate;
	private Calendar couponExpiryDate;
	
	
	
	private Boolean autoIncrCheck;
	private Long orgId;
	
	
	private Long issued;
	private Long redeemed;
	private Long available;
	private Double totDiscount;
	private Double totRevenue;
	
	private Long redeemdCount;
	private Boolean redemedAutoChk;
	private Long userId;
	
	private Byte loyaltyPoints;
	private Integer requiredLoyltyPoits;
	/*private Boolean barCodeCheck;
	private Byte barCodeType;
	private String barCodeDimensions;*/
	private Double usedLoyaltyPoints;
	//coupon barcode type
	private String barcodeType;
	private Long barcodeWidth;
	private Long barcodeHeight;
	private Boolean enableBarcode;
	private Boolean singPromoContUnlimitedRedmptChk;
	private Long singPromoContRedmptLimit;
	private Boolean allStoreChk;
	private String selectedStores;
	
	
	public Coupons(){}
	
	public Coupons(String couponName,String couponDescription,String status,String couponGeneratedType,String couponCode,
			String discountType,String discountCriteria,Long  totalQty,Long userId,Calendar userCreatedDate,
			Calendar couponCreatedDate,Calendar couponExpiryDate,Boolean  autoIncrCheck,Long redeemdCount, Boolean redemdAutoChk ){
		this.couponName = couponName;
		this.couponDescription= couponDescription;
		this.status=status;
		this.couponGeneratedType= couponGeneratedType;
		this.couponCode=couponCode;
		this.discountType=discountType;
		this.discountCriteria=discountCriteria;
		this.totalQty=totalQty;
		this.userId=userId;
		this.userCreatedDate=userCreatedDate;
		this.couponCreatedDate=couponCreatedDate;
		this.couponExpiryDate=couponExpiryDate;
		this.autoIncrCheck = autoIncrCheck;
		this.redeemdCount = redeemdCount;
		this.redemedAutoChk = redemdAutoChk;
		
	}
	
	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponDescription() {
		return couponDescription;
	}

	public void setCouponDescription(String couponDescription) {
		this.couponDescription = couponDescription;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCouponGeneratedType() {
		return couponGeneratedType;
	}

	public void setCouponGeneratedType(String couponGeneratedType) {
		this.couponGeneratedType = couponGeneratedType;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Long getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Long totalQty) {
		this.totalQty = totalQty;
	}

	public Long getGeneratedQty() {
		return generatedQty;
	}

	public void setGeneratedQty(Long generatedQty) {
		this.generatedQty = generatedQty;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	/*public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}*/

	public Calendar getUserCreatedDate() {
		return userCreatedDate;
	}

	public void setUserCreatedDate(Calendar userCreatedDate) {
		this.userCreatedDate = userCreatedDate;
	}

	public String getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public Calendar getUserLastModifiedDate() {
		return userLastModifiedDate;
	}

	public void setUserLastModifiedDate(Calendar userLastModifiedDate) {
		this.userLastModifiedDate = userLastModifiedDate;
	}

	public Calendar getCouponCreatedDate() {
		return couponCreatedDate;
	}

	public void setCouponCreatedDate(Calendar couponCreatedDate) {
		this.couponCreatedDate = couponCreatedDate;
	}

	public Calendar getCouponExpiryDate() {
		return couponExpiryDate;
	}

	public void setCouponExpiryDate(Calendar couponExpiryDate) {
		this.couponExpiryDate = couponExpiryDate;
	}
	
	//
	public Boolean getAutoIncrCheck() {
		return autoIncrCheck;
	}

	public void setAutoIncrCheck(Boolean autoIncrCheck) {
		this.autoIncrCheck = autoIncrCheck;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public String getDiscountCriteria() {
		return discountCriteria;
	}

	public void setDiscountCriteria(String discountCriteria) {
		this.discountCriteria = discountCriteria;
	}

	public Long getIssued() {
		return issued;
	}

	public void setIssued(Long issued) {
		this.issued = issued;
	}

	public Long getRedeemed() {
		return redeemed;
	}

	public void setRedeemed(Long redeemed) {
		this.redeemed = redeemed;
	}

	public Long getAvailable() {
		return available;
	}

	public void setAvailable(Long available) {
		this.available = available;
	}

	public Double getTotDiscount() {
		return totDiscount;
	}

	public void setTotDiscount(Double totDiscount) {
		this.totDiscount = totDiscount;
	}

	public Double getTotRevenue() {
		return totRevenue;
	}

	public void setTotRevenue(Double totRevenue) {
		this.totRevenue = totRevenue;
	}
	
	
	public Boolean getRedemedAutoChk() {
		return redemedAutoChk;
	}

	public void setRedemedAutoChk(Boolean redemedAutoChk) {
		this.redemedAutoChk = redemedAutoChk;
	}

	public Long getRedeemdCount() {
		return redeemdCount;
	}

	public void setRedeemdCount(Long redeemdCount) {
		this.redeemdCount = redeemdCount;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBarcodeType() {
		return barcodeType;
	}

	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}

	public Long getBarcodeWidth() {
		return barcodeWidth;
	}

	public void setBarcodeWidth(Long barcodeWidth) {
		this.barcodeWidth = barcodeWidth;
	}

	public Long getBarcodeHeight() {
		return barcodeHeight;
	}

	public void setBarcodeHeight(Long barcodeHeight) {
		this.barcodeHeight = barcodeHeight;
	}

	public Boolean getEnableBarcode() {
		return enableBarcode;
	}

	public void setEnableBarcode(Boolean enableBarcode) {
		this.enableBarcode = enableBarcode;
	}

	public Byte getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(Byte loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}
	
	
	public Integer getRequiredLoyltyPoits() {
		return requiredLoyltyPoits;
	}

	public void setRequiredLoyltyPoits(Integer requiredLoyltyPoits) {
		this.requiredLoyltyPoits = requiredLoyltyPoits;
	}

	public Double getUsedLoyaltyPoints() {
		return usedLoyaltyPoints;
	}

	public void setUsedLoyaltyPoints(Double usedLoyaltyPoints) {
		this.usedLoyaltyPoints = usedLoyaltyPoints;
	}

	public Boolean getSingPromoContUnlimitedRedmptChk() {
		return singPromoContUnlimitedRedmptChk;
	}

	public void setSingPromoContUnlimitedRedmptChk(
			Boolean singPromoContUnlimitedRedmptChk) {
		this.singPromoContUnlimitedRedmptChk = singPromoContUnlimitedRedmptChk;
	}

	public Long getSingPromoContRedmptLimit() {
		return singPromoContRedmptLimit;
	}

	public void setSingPromoContRedmptLimit(Long singPromoContRedmptLimit) {
		this.singPromoContRedmptLimit = singPromoContRedmptLimit;
	}

	public Boolean getAllStoreChk() {
		return allStoreChk;
	}

	public void setAllStoreChk(Boolean allStoreChk) {
		this.allStoreChk = allStoreChk;
	}

	public String getSelectedStores() {
		return selectedStores;
	}

	public void setSelectedStores(String selectedStores) {
		this.selectedStores = selectedStores;
	}
	
	

}
