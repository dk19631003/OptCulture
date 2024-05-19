package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;

public class CustomerSalesUpdatedData implements Serializable{
	
	private Long aggrId;
	private String customerId;
	private Double totPurchaseAmt;
	private Long userId;
	private Double basketSize;
	private Integer totInvoice;
	private Integer totVisits;
	private Calendar firstPurchaseDate;
	private Calendar lastPurchaseDate;

	
	
	
	
	public Long getAggrId() {
		return aggrId;
	}
	public void setAggrId(Long aggrId) {
		this.aggrId = aggrId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/*public Integer getTotReceiptCount() {
		return totReceiptCount;
	}
	public void setTotReceiptCount(Integer totReceiptCount) {
		this.totReceiptCount = totReceiptCount;
	}*/
	public Double getTotPurchaseAmt() {
		return totPurchaseAmt;
	}
	public void setTotPurchaseAmt(Double totPurchaseAmt) {
		this.totPurchaseAmt = totPurchaseAmt;
	}
	
	/*public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}*/
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Double getBasketSize() {
		return basketSize;
	}
	public void setBasketSize(Double basketSize) {
		this.basketSize = basketSize;
	}
	public Integer getTotInvoice() {
		return totInvoice;
	}
	public void setTotInvoice(Integer totInvoice) {
		this.totInvoice = totInvoice;
	}
	public Integer getTotVisits() {
		return totVisits;
	}
	public void setTotVisits(Integer totVisits) {
		this.totVisits = totVisits;
	}
	public Calendar getFirstPurchaseDate() {
		return firstPurchaseDate;
	}
	public void setFirstPurchaseDate(Calendar firstPurchaseDate) {
		this.firstPurchaseDate = firstPurchaseDate;
	}
	public Calendar getLastPurchaseDate() {
		return lastPurchaseDate;
	}
	public void setLastPurchaseDate(Calendar lastPurchaseDate) {
		this.lastPurchaseDate = lastPurchaseDate;
	}


}
