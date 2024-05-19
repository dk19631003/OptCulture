package org.mq.marketer.campaign.beans;

public class CouponDiscountGeneration implements java.io.Serializable{
	
	private Long couponDisGenId;
	private Coupons coupons;
//	private String typeOfDiscount;
	private Double discount;
	private String itemCategory;
	private Long totPurchaseAmount;
	private String storeNumber;
	private String SkuValue;
	private String SkuAttribute;
	private Long ownerId;
	private String quantity;
	private String limitQuantity; //LTE , ET , GTE , M
	private Double itemPrice;
	private String itemPriceCriteria;
	private String noOfEligibleItems; // ALLEI , HPIWD , HPIWOD , LPIWD , LPIWOD
	private String itemListPrice;//item's price as in invntory table

	public String getItemListPrice() {
		return itemListPrice;
	}

	public void setItemListPrice(String itemListPrice) {
		this.itemListPrice = itemListPrice;
	}
	private String shippingFee;
	private String shippingFeeFree;
	public String getShippingFeeFree() {
		return shippingFeeFree;
	}

	public void setShippingFeeFree(String shippingFeeFree) {
		this.shippingFeeFree = shippingFeeFree;
	}

	public String getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(String shippingFee) {
		this.shippingFee = shippingFee;
	}

	public String getShippingFeeType() {
		return shippingFeeType;
	}

	public void setShippingFeeType(String shippingFeeType) {
		this.shippingFeeType = shippingFeeType;
	}


	private String shippingFeeType;
	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}


	private Double maxDiscount;
	private String Program;
	private String tierNum;
	private String CardSetNum;
	//private String DateTime;
	//private String DiscItem;
	private String eliRule;
	public String getEliRule() {
		return eliRule;
	}

	public void setEliRule(String eliRule) {
		this.eliRule = eliRule;
	}

	public String getProgram() {
		return Program;
	}

	public void setProgram(String program) {
		Program = program;
	}
	public String getTierNum() {
		return tierNum;
	}

	public void setTierNum(String tierNum) {
		this.tierNum = tierNum;
	}


	public String getCardSetNum() {
		return CardSetNum;
	}

	public void setCardSetNum(String cardSetNum) {
		CardSetNum = cardSetNum;
	}	
	public CouponDiscountGeneration(){}
	
	public CouponDiscountGeneration(Double discount, String itemCategory, Long totPurchaseAmount, String storeNumber,
			String skuValue, String skuAttribute, Long ownerId, Double itemPrice, String quantity, Double maxDiscount) {
		super();
		this.discount = discount;
		this.itemCategory = itemCategory;
		this.totPurchaseAmount = totPurchaseAmount;
		this.storeNumber = storeNumber;
		SkuValue = skuValue;
		SkuAttribute = skuAttribute;
		this.ownerId = ownerId;
		this.itemPrice = itemPrice;
		this.quantity = quantity;
		this.maxDiscount = maxDiscount;
	}

	public CouponDiscountGeneration(Coupons coupons){
		this.coupons=coupons;
	}
	
	public Double getMaxDiscount() {
		return maxDiscount;
	}

	public void setMaxDiscount(Double maxDiscount) {
		this.maxDiscount = maxDiscount;
	}
		
	public Long getCouponDisGenId() {
		return couponDisGenId;
	}
	public void setCouponDisGenId(Long couponDisGenId) {
		this.couponDisGenId = couponDisGenId;
	}
	public Coupons getCoupons() {
		return coupons;
	}
	public void setCoupons(Coupons coupons) {
		this.coupons = coupons;
	}
	/*public String getTypeOfDiscount() {
		return typeOfDiscount;
	}
	public void setTypeOfDiscount(String typeOfDiscount) {
		this.typeOfDiscount = typeOfDiscount;
	}*/
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public String getItemCategory() {
		return itemCategory;
	}
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}
	public Long getTotPurchaseAmount() {
		return totPurchaseAmount;
	}
	public void setTotPurchaseAmount(Long totPurchaseAmount) {
		this.totPurchaseAmount = totPurchaseAmount;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	

	public String getSkuValue() {
		return SkuValue;
	}

	public void setSkuValue(String skuValue) {
		SkuValue = skuValue;
	}

	public String getSkuAttribute() {
		return SkuAttribute;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setSkuAttribute(String skuAttribute) {
		SkuAttribute = skuAttribute;
	}

	public String getLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(String limitQuantity) {
		this.limitQuantity = limitQuantity;
	}

	public String getItemPriceCriteria() {
		return itemPriceCriteria;
	}

	public void setItemPriceCriteria(String itemPriceCriteria) {
		this.itemPriceCriteria = itemPriceCriteria;
	}


	public String getNoOfEligibleItems() {
		return noOfEligibleItems;
	}

	public void setNoOfEligibleItems(String noOfEligibleItems) {
		this.noOfEligibleItems = noOfEligibleItems;
	}

	public Double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}
	

}
