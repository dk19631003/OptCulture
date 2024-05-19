package org.mq.captiway.scheduler.services;

import java.util.List;

public class Recipients {

	private String totalCount;
	private String totalSentCount;
	private String totalDeliveredCount;
	private String totalDeliveryFailedCount;
	private List<Item> items;
	
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getTotalSentCount() {
		return totalSentCount;
	}
	public void setTotalSentCount(String totalSentCount) {
		this.totalSentCount = totalSentCount;
	}
	public String getTotalDeliveredCount() {
		return totalDeliveredCount;
	}
	public void setTotalDeliveredCount(String totalDeliveredCount) {
		this.totalDeliveredCount = totalDeliveredCount;
	}
	public String getTotalDeliveryFailedCount() {
		return totalDeliveryFailedCount;
	}
	public void setTotalDeliveryFailedCount(String totalDeliveryFailedCount) {
		this.totalDeliveryFailedCount = totalDeliveryFailedCount;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
		
}
