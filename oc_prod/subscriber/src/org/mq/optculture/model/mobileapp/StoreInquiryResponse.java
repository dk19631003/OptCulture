package org.mq.optculture.model.mobileapp;


import org.mq.optculture.business.mobileapp.Filter;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Status;

public class StoreInquiryResponse extends BaseResponseObject{
	private ResponseHeader header;
	private String inquiryType;
	private StoreSortedBy sortBy;
	private StoreInquiry storeInquiry;
	private FilterBy filterBy;
	private Status status;
	private StoreInfo storeInfo;
	private Filter filter;
	public ResponseHeader getHeader() {
		return header;
	}
	public void setHeader(ResponseHeader header) {
		this.header = header;
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
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
	public void setFilterBy(FilterBy filterBy2) {
		this.filterBy = filterBy2;
	}
	public StoreInfo getStoreInfo() {
		return storeInfo;
	}
	public void setStoreInfo(StoreInfo storeInfo) {
		this.storeInfo = storeInfo;
	}
	public String getInquiryType() {
		return inquiryType;
	}
	public void setInquiryType(String inquiryType) {
		this.inquiryType = inquiryType;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public StoreSortedBy getSortBy() {
		return sortBy;
	}
	public void setSortBy(StoreSortedBy sortBy) {
		this.sortBy = sortBy;
	}
	

}
