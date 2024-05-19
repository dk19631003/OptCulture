package org.mq.marketer.campaign.beans;

import java.util.Calendar;

	
public class CountryReceivingNumbers {
	
	private Long recvNumId;
	private String country;
	private String receivingNumber;
	private String recvNumType;
	private Long createdBy;
	private Calendar createdDate;
	private Long gatewayId;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getReceivingNumber() {
		return receivingNumber;
	}
	public void setReceivingNumber(String receivingNumber) {
		this.receivingNumber = receivingNumber;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getRecvNumType() {
		return recvNumType;
	}
	public void setRecvNumType(String recvNumType) {
		this.recvNumType = recvNumType;
	}
	public Long getRecvNumId() {
		return recvNumId;
	}
	public void setRecvNumId(Long recvNumId) {
		this.recvNumId = recvNumId;
	}
	public Long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}
}
