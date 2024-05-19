package org.mq.optculture.business.nexon;

import java.util.List;

public class NexonStoreEnquiryResponse {
	
	private String companyId;
	private List<StoreInfo> stores;
	private Status status;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public List<StoreInfo> getStores() {
		return stores;
	}
	public void setStores(List<StoreInfo> stores) {
		this.stores = stores;
	}
	
}
