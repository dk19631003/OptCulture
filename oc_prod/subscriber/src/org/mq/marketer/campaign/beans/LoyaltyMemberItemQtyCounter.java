package org.mq.marketer.campaign.beans;

public class LoyaltyMemberItemQtyCounter {
	private Long id;
    private Long loyaltyID;
    private Long SPRuleID;
    private String itemStr;
    private Long userId;
    private Long orgId;
    private double qty;
    
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLoyaltyID() {
		return loyaltyID;
	}
	public void setLoyaltyID(Long loyaltyID) {
		this.loyaltyID = loyaltyID;
	}
	public Long getSPRuleID() {
		return SPRuleID;
	}
	public void setSPRuleID(Long sPRuleID) {
		SPRuleID = sPRuleID;
	}
	public String getItemStr() {
		return itemStr;
	}
	public void setItemStr(String itemStr) {
		this.itemStr = itemStr;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
}