package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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
	private String CTCouponCode;
	private String CTCouponValue;
	private Long CTCouponSMStempltId;
	private Long generatedQty;
//	private String createUser;
	private Calendar userCreatedDate;
	private String lastModifiedUser;
	private Calendar userLastModifiedDate;
	private Calendar couponCreatedDate;
	private Calendar couponExpiryDate;
	
	private String expiryType;
	private String expiryDetails;

	private Boolean autoIncrCheck;
	private Long orgId;
	private Double purchaseQty;
	private Long specialRewadId;
	
	
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
	private boolean stackable;
	
	private Boolean excludeItems;
	private Boolean accumulateOtherPromotion;
	private String noOfEligibleItems;
	
	//New Mobile-APP offers field 
	private boolean enableOffer;
	private String bannerImage;
	private String bannerUrlRedirect;
	private String offerHeading;
	private String offerDescription;
	
	private Set<OrganizationZone> brand = new HashSet<OrganizationZone>(0);
	private String valueCode;
	private CouponCodes couponCodeObj;
	private boolean highlightedOffer;
	
	private String tempDesc;
	private boolean mappedOnZone =false;
	private String rewardExpiryType;
	private String rewardExpiryValue;
	private boolean deductItemPrice;
	private boolean excludeDiscountedItems;
	private boolean applyDefault = false;
	private boolean otpAuthenCheck;
	private String coupCodeGenType;
	private Double multiplierValue;
	
	public Double getMultiplierValue() {
		return multiplierValue;
	}

	public void setMultiplierValue(Double multiplierValue) {
		this.multiplierValue = multiplierValue;
	}

	public String getCoupCodeGenType() {
		return coupCodeGenType;
	}

	public void setCoupCodeGenType(String coupCodeGenType) {
		this.coupCodeGenType = coupCodeGenType;
	}
	
	private boolean useasReferralCode;
	
	
	
	public boolean isUseasReferralCode() {
		return useasReferralCode;
	}

	public void setUseasReferralCode(boolean useasReferralCode) {
		this.useasReferralCode = useasReferralCode;
	}

	public boolean isOtpAuthenCheck() {
		return otpAuthenCheck;
	}

	public void setOtpAuthenCheck(boolean otpAuthenCheck) {
		this.otpAuthenCheck = otpAuthenCheck;
	}
	
	public boolean isApplyDefault() {
		return applyDefault;
	}

	public void setApplyDefault(boolean applyDefault) {
		this.applyDefault = applyDefault;
	}

	public CouponCodes getCouponCodeObj() {
		return couponCodeObj;
	}

	public void setCouponCodeObj(CouponCodes couponCodeObj) {
		this.couponCodeObj = couponCodeObj;
	}

	public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}

	public String getNoOfEligibleItems() {
		return noOfEligibleItems;
	}

	public void setNoOfEligibleItems(String noOfEligibleItems) {
		this.noOfEligibleItems = noOfEligibleItems;
	}

	public Boolean isAccumulateOtherPromotion() {
		return accumulateOtherPromotion;
	}

	public void setAccumulateOtherPromotion(Boolean accumulateOtherPromotion) {
		this.accumulateOtherPromotion = accumulateOtherPromotion;
	}

	public Boolean isExcludeItems() {
		return excludeItems;
	}

	public void setExcludeItems(Boolean excludeItems) {
		this.excludeItems = excludeItems;
	}
	
	public boolean isStackable() {
		return stackable;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public Coupons(){}
	
	public Coupons(String couponName,String couponDescription,String status,String couponGeneratedType,String couponCode,
			String discountType,String discountCriteria,Long  totalQty,Long userId,Calendar userCreatedDate,
			Calendar couponCreatedDate,Calendar userLastModifiedDate,Calendar couponExpiryDate,Boolean  autoIncrCheck,Long redeemdCount, Boolean redemdAutoChk ){
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
		this.userLastModifiedDate=userLastModifiedDate;
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

	public String getCTCouponCode() {
		return CTCouponCode;
	}

	public void setCTCouponCode(String cTCouponCode) {
		CTCouponCode = cTCouponCode;
	}

	public String getCTCouponValue() {
		return CTCouponValue;
	}

	public void setCTCouponValue(String cTCouponValue) {
		CTCouponValue = cTCouponValue;
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
	
	public String getExpiryType() {
		return expiryType;
	}

	public void setExpiryType(String expiryType) {
		this.expiryType = expiryType;
	}

	public String getExpiryDetails() {
		return expiryDetails;
	}

	public void setExpiryDetails(String expiryDetails) {
		this.expiryDetails = expiryDetails;
	}
	//for promo conjunction check between OR  / AND
	
		public boolean combineItemAttributes;

	public boolean isCombineItemAttributes() {
			return combineItemAttributes;
		}

		public void setCombineItemAttributes(boolean combineItemAttributes) {
			this.combineItemAttributes = combineItemAttributes;
		}
		
		public String descType;

		

	public boolean isEnableOffer() {
		return enableOffer;
	}

	public void setEnableOffer(boolean enableOffer) {
		this.enableOffer = enableOffer;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public String getBannerUrlRedirect() {
		return bannerUrlRedirect;
	}

	public void setBannerUrlRedirect(String bannerUrlRedirect) {
		this.bannerUrlRedirect = bannerUrlRedirect;
	}

	public String getOfferHeading() {
		return offerHeading;
	}

	public void setOfferHeading(String offerHeading) {
		this.offerHeading = offerHeading;
	}

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}

	public Set<OrganizationZone> getBrand() {
		return brand;
	}

	public void setBrand(Set<OrganizationZone> brand) {
		this.brand = brand;
	}

	public boolean isHighlightedOffer() {
		return highlightedOffer;
	}

	public void setHighlightedOffer(boolean highlightedOffer) {
		this.highlightedOffer = highlightedOffer;
	}

	public String getDescType() {
		return descType;
	}

	public void setDescType(String descType) {
		this.descType = descType;
	}

	public String getTempDesc() {
		return tempDesc;
	}

	public void setTempDesc(String tempDesc) {
		this.tempDesc = tempDesc;
	}

	public Double getPurchaseQty() {
		return purchaseQty;
	}

	public void setPurchaseQty(Double purchaseQty) {
		this.purchaseQty = purchaseQty;
	}

	public Long getSpecialRewadId() {
		return specialRewadId;
	}

	public void setSpecialRewadId(Long specialRewadId) {
		this.specialRewadId = specialRewadId;
	}

	public String getRewardExpiryType() {
		return rewardExpiryType;
	}
	public void setRewardExpiryType(String rewardExpiryType) {
		this.rewardExpiryType = rewardExpiryType;
	}
	public String getRewardExpiryValue() {
		return rewardExpiryValue;
	}
	public void setRewardExpiryValue(String rewardExpiryValue) {
		this.rewardExpiryValue = rewardExpiryValue;
	}
	public boolean isDeductItemPrice() {
		return deductItemPrice;
	}
	public void setDeductItemPrice(boolean deductItemPrice) {
		this.deductItemPrice = deductItemPrice;
	}

	public boolean isExcludeDiscountedItems() {
		return excludeDiscountedItems;
	}

	public void setExcludeDiscountedItems(boolean excludeDiscountedItems) {
		this.excludeDiscountedItems = excludeDiscountedItems;
	}
	public boolean isMappedOnZone() {
		return mappedOnZone;
	}

	public void setMappedOnZone(boolean mappedOnZone) {
		this.mappedOnZone = mappedOnZone;
	}
	public Long getCTCouponSMStempltId() {
		return CTCouponSMStempltId;
	}

	public void setCTCouponSMStempltId(Long cTCouponSMStempltId) {
		CTCouponSMStempltId = cTCouponSMStempltId;
	}



}
