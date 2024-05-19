package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class PurgeQueue {
	private Long id;
	private Long userId;
	private Long listId;
	private String status;
	private Calendar createdDate;
	private Calendar purgedDate;
	public PurgeQueue(){
		
	}
	public PurgeQueue(Long userId, Long listId, String status,Calendar createdDate) {
		this.userId = userId;
		this.listId=listId;
		this.status=status;
		this.createdDate=createdDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getPurgedDate() {
		return purgedDate;
	}
	public void setPurgedDate(Calendar purgedDate) {
		this.purgedDate = purgedDate;
	}
	
}
