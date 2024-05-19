package org.mq.optculture.model.mobileapp;

import java.util.List;

public class RequestReport {

	
	 private String startDate;
	 private String endDate;
	 private String offset;
	 private String maxRecords;
	 private String store;
	 private String source;				//'Store', 'Webform', 'eCommerce' and 'All'
	 private String transactionType;//: Detailed,		//'Enrollment', 'Issuance', 'Redemption' and 'Return'
	 private String mode;//: All,				//'Online, 'Offline' and 'All
	 private String serviceType;//: All,			//'OC, 'SB' and 'All'
	 private  List<String> transactions;
		public List<String> getTransactions() {
			return transactions;
		}

		public void setTransactions(List<String> transactions) {
			this.transactions = transactions;
		}
	 public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getMaxRecords() {
		return maxRecords;
	}
	public void setMaxRecords(String maxRecords) {
		this.maxRecords = maxRecords;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
}
