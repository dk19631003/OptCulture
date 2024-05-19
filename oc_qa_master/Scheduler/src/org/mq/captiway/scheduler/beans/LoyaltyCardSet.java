package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D 
 * It handles basic data of loyalty card set description.
 *
 */
public class LoyaltyCardSet {

	private Long cardSetId;
	private String cardSetName;
	private Long quantity;
	private String generationType;
	private String status;
	private Long programId;
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private char migratedFlag;
	private String cardSetType;
	private int linkedTierLevel;

	public LoyaltyCardSet() {
	}

	public Long getCardSetId() {
		return cardSetId;
	}

	public void setCardSetId(Long cardSetId) {
		this.cardSetId = cardSetId;
	}

	public String getCardSetName() {
		return cardSetName;
	}

	public void setCardSetName(String cardSetName) {
		this.cardSetName = cardSetName;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getGenerationType() {
		return generationType;
	}

	public void setGenerationType(String generationType) {
		this.generationType = generationType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Calendar getCreatedDate() {
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
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public char getMigratedFlag() {
		return migratedFlag;
	}

	public void setMigratedFlag(char migratedFlag) {
		this.migratedFlag = migratedFlag;
	}

	public String getCardSetType() {
		return cardSetType;
	}

	public void setCardSetType(String cardSetType) {
		this.cardSetType = cardSetType;
	}

	public int getLinkedTierLevel() {
		return linkedTierLevel;
	}

	public void setLinkedTierLevel(int linkedTierLevel) {
		this.linkedTierLevel = linkedTierLevel;
	}

	
}
