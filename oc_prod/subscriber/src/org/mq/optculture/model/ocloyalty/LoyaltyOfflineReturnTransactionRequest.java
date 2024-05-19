package org.mq.optculture.model.ocloyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyOfflineReturnTransactionRequest extends BaseRequestObject{

	private RequestHeader header;
	private OriginalReceipt originalReceipt;
	private String creditRedeemedAmount;
	//private String creditRedeemedGift;
	private MembershipRequest membership;
	private Amount amount;
	private Customer customer;
	private LoyaltyUser user;
	private Items items;
	/*private List<SkuDetails> items;
	
	
	public Items getReturnItems() {
		return returnItems;
	}
	
	public void setReturnItems(Items returnItems) {
		this.returnItems = returnItems;
	}*/
	public Items getItems() {
		return items;
	}
	@XmlElement(name = "items")
	public void setItems(Items items) {
		this.items = items;
	}

	public LoyaltyOfflineReturnTransactionRequest(){
		//Default Constructor
	}

	public RequestHeader getHeader() {
		return header;
	}
	@XmlElement(name="header")
	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	public OriginalReceipt getOriginalReceipt() {
		return originalReceipt;
	}
	@XmlElement(name="originalReceipt")
	public void setOriginalReceipt(OriginalReceipt originalReceipt) {
		this.originalReceipt = originalReceipt;
	}

	/*public String getCreditRedeemedReward() {
		return creditRedeemedReward;
	}

	public void setCreditRedeemedReward(String creditRedeemedReward) {
		this.creditRedeemedReward = creditRedeemedReward;
	}

	public String getCreditRedeemedGift() {
		return creditRedeemedGift;
	}

	public void setCreditRedeemedGift(String creditRedeemedGift) {
		this.creditRedeemedGift = creditRedeemedGift;
	}*/
	
	public String getCreditRedeemedAmount() {
		return creditRedeemedAmount;
	}
	@XmlElement(name="creditRedeemedAmount")
	public void setCreditRedeemedAmount(String creditRedeemedAmount) {
		this.creditRedeemedAmount = creditRedeemedAmount;
	}

	public LoyaltyUser getUser() {
		return user;
	}
	@XmlElement(name = "user")
	public void setUser(LoyaltyUser user) {
		this.user = user;
	}
	public Amount getAmount() {
		return amount;
	}
	@XmlElement(name = "amount")
	public void setAmount(Amount amount) {
		this.amount = amount;
	}
	public Customer getCustomer() {
		return customer;
	}
	@XmlElement(name = "customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public MembershipRequest getMembership() {
		return membership;
	}
	@XmlElement(name = "membership")
	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}

}
