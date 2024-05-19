package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It handles data of loyalty cards.
 */
public class LoyaltyCards {

	private Long cardId;
	private Long programId;
	private Long cardSetId;
	private String cardNumber;
	private String cardPin;
	private Long orgId;
	private Long userId;
	private String status; // Activated/Enrolled/Inventory/Selected
	//private Calendar createdDate; 
	//private String createdBy;
	private Calendar activationDate;
	private char registeredFlag;// registered through web-form flag
	private Long membershipId;// linked membership contact_loyalty obj id

	public LoyaltyCards() {
	}

	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getCardSetId() {
		return cardSetId;
	}

	public void setCardSetId(Long cardSetId) {
		this.cardSetId = cardSetId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardPin() {
		return cardPin;
	}

	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}*/

	public Calendar getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Calendar activationDate) {
		this.activationDate = activationDate;
	}

	public char getRegisteredFlag() {
		return registeredFlag;
	}

	public void setRegisteredFlag(char registeredFlag) {
		this.registeredFlag = registeredFlag;
	}

	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long membershipId) {
		this.membershipId = membershipId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

}
