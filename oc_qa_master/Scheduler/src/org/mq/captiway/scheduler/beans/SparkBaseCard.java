package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

import org.mq.captiway.scheduler.beans.SparkBaseLocationDetails;

public class SparkBaseCard {
	
	private Long sparkBaseCard_id;
	private SparkBaseLocationDetails sparkBaseLocationId;
	private String cardId;
	private String cardPin;
	private String cardType;
	private String fromSource;
	private String status;
	private Calendar activationDate;
	private String comments;
	
	public SparkBaseCard() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SparkBaseCard(String cardId, String cardPin, SparkBaseLocationDetails sparkBaseLocationId) {
		this.cardId = cardId;
		this.cardPin = cardPin;
		this.sparkBaseLocationId = sparkBaseLocationId;
	}

	
	public SparkBaseCard(String cardId,  SparkBaseLocationDetails sparkBaseLocationId) {
		this.cardId = cardId;
		this.sparkBaseLocationId = sparkBaseLocationId;
	}

	public Long getSparkBaseCard_id() {
		return sparkBaseCard_id;
	}
	public void setSparkBaseCard_id(Long sparkBaseCard_id) {
		this.sparkBaseCard_id = sparkBaseCard_id;
	}
	public SparkBaseLocationDetails getSparkBaseLocationId() {
		return sparkBaseLocationId;
	}
	public void setSparkBaseLocationId(SparkBaseLocationDetails sparkBaseLocationId) {
		this.sparkBaseLocationId = sparkBaseLocationId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardPin() {
		return cardPin;
	}
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getFromSource() {
		return fromSource;
	}

	public void setFromSource(String fromSource) {
		this.fromSource = fromSource;
	}


	public Calendar getActivationDate() {
		return activationDate;
	}


	public void setActivationDate(Calendar activationDate) {
		this.activationDate = activationDate;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	
	

}
