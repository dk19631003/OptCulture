package org.mq.marketer.campaign.beans;

public class TempPromoDump {
private long id;
private Double discount;
private String itemSid;
private Long ownerId;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public Double getDiscount() {
	return discount;
}
public void setDiscount(Double discount) {
	this.discount = discount;
}
public String getItemSid() {
	return itemSid;
}
public void setItemSid(String itemSid) {
	this.itemSid = itemSid;
}
public Long getOwnerId() {
	return ownerId;
}
public void setOwnerId(Long ownerId) {
	this.ownerId = ownerId; 
}

}
