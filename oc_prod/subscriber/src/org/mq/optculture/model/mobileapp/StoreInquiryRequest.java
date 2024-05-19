package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.BaseRequestObject;


public class StoreInquiryRequest extends BaseRequestObject {
	private RequestHeader header;
	private String inquiryType;
	private StoreSortedBy sortBy;
	private StoreInquiry storeInquiry;
	private FilterBy filterBy;
	private User user;
	public RequestHeader getHeader() {
		return header;
	}
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
	public StoreInquiry getStoreInquiry() {
		return storeInquiry;
	}
	public void setStoreInquiry(StoreInquiry storeInquiry) {
		this.storeInquiry = storeInquiry;
	}
	public FilterBy getFilterBy() {
		return filterBy;
	}
	public void setFilterBy(FilterBy filterBy) {
		this.filterBy = filterBy;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getInquiryType() {
		return inquiryType;
	}
	public void setInquiryType(String inquiryType) {
		this.inquiryType = inquiryType;
	}
	public StoreSortedBy getSortBy() {
		return sortBy;
	}
	public void setSortBy(StoreSortedBy sortBy) {
		this.sortBy = sortBy;
	}
   }
