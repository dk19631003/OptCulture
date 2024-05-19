package org.mq.marketer.campaign.beans;

public class SKUTemp {
private long skuTempId;
private String skuAttribute;
private String skuValue;
private Long userId;
private Long couponId;
private Double discount;
private String discountType;
private String quantity;
private String limitQuantity;
private Double itemPrice;
private String itemPriceCriteria;
private String noOfEligibleItems; // ALLEI , HPIWD , HPIWOD , LPIWD , LPIWOD
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

/*public String getDiscItem() {
	return DiscItem;
}

public void setDiscItem(String discItem) {
	DiscItem = discItem;
} 

public String getDateTime() {
	return DateTime;
}

public void setDateTime(String dateTime) {
	DateTime = dateTime;
} */
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


public String getQuantity() {
	return quantity;
}

public void setQuantity(String quantity) {
	this.quantity = quantity;
}
private Long totPurchaseAmount;
private Long ownerId;

private Double maxDiscount;


public Double getMaxDiscount() {
	return maxDiscount;
}

public void setMaxDiscount(Double maxDiscount) {
	this.maxDiscount = maxDiscount;
}

public long getSkuTempId() {
	return skuTempId;
}
public void setSkuTempId(long skuTempId) {
	this.skuTempId = skuTempId;
}
public String getSkuAttribute() {
	return skuAttribute;
}
public void setSkuAttribute(String skuAttribute) {
	this.skuAttribute = skuAttribute;
}
public String getSkuValue() {
	return skuValue;
}
public void setSkuValue(String skuValue) {
	this.skuValue = skuValue;
}
public Long getUserId() {
	return userId;
}
public void setUserId(Long userId) {
	this.userId = userId;
}
public Long getCouponId() {
	return couponId;
}
public void setCouponId(Long couponId) {
	this.couponId = couponId;
}
public Double getDiscount() { 
	return discount;
}
public void setDiscount(Double discount) {
	this.discount = discount;
}
public String getDiscountType() {
	return discountType;
}
public void setDiscountType(String discountType) {
	this.discountType = discountType;
}
public Long getTotPurchaseAmount() {
	return totPurchaseAmount;
}
public void setTotPurchaseAmount(Long totPurchaseAmount) {
	this.totPurchaseAmount = totPurchaseAmount;
}
public Long getOwnerId() {
	return ownerId;
}
public void setOwnerId(Long ownerId) {
	this.ownerId = ownerId;
}

public String getLimitQuantity() {
	return limitQuantity;
}

public void setLimitQuantity(String limitQuantity) {
	this.limitQuantity = limitQuantity;
}

public Double getItemPrice() {
	return itemPrice;
}

public void setItemPrice(Double itemPrice) {
	this.itemPrice = itemPrice;
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

}
