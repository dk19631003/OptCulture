package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class SalesLiteralHashCode implements java.io.Serializable {
	private long salesLiteralId;
	private String salesLiteralHashCode ;
	private long listId;
	private Long userId;
	
	private Calendar createdDate;
	private Boolean currentFile;
	
	
	public SalesLiteralHashCode() {
	}
	
	public long getSalesLiteralId() {
		return salesLiteralId;
	}


	public void setSalesLiteralId(long salesLiteralId) {
		this.salesLiteralId = salesLiteralId;
	}
	
	
	
	
	public String getSalesLiteralHashCode() {
		return salesLiteralHashCode;
	}

	public void setSalesLiteralHashCode(String salesLiteralHashCode) {
		this.salesLiteralHashCode = salesLiteralHashCode;
	}



	
	
	
	public long getListId() {
		return listId;
	}
	public void setListId(long listId) {
		this.listId = listId;
	}


	public Calendar getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean isCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(Boolean currentFile) {
		this.currentFile = currentFile;
	}

	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

}
