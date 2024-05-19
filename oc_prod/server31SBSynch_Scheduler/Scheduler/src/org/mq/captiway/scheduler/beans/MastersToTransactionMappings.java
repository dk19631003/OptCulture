package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class MastersToTransactionMappings {
	private long id;
	private Long userId;
	private Long listId;
	private POSMapping parentId;
	private POSMapping childId;
	private String type;
	private Calendar createdDate;
	private Calendar lastModifieddDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getListId() {
		return listId;
	}
	public void setListId(Long listId) {
		this.listId = listId;
	}
	public POSMapping getParentId() {
		return parentId;
	}
	public void setParentId(POSMapping parentId) {
		this.parentId = parentId;
	}
	public POSMapping getChildId() {
		return childId;
	}
	public void setChildId(POSMapping childId) {
		this.childId = childId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getLastModifieddDate() {
		return lastModifieddDate;
	}
	public void setLastModifieddDate(Calendar lastModifieddDate) {
		this.lastModifieddDate = lastModifieddDate;
	}
	

}
