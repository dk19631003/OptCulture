package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It handles loyalty program exclusion data. This data is considered for issuance and redemption.
 */
public class LoyaltyProgramExclusion {
	private Long exclusionId;
	private Long programId;
	private char issuanceWithPromoFlag;
	private String issuancePromoIdStr;
	private char redemptionWithPromoFlag;
	private String redemptionPromoIdStr;
	private String storeNumberStr;
	private String itemCatStr;
	private String deptCodeStr;
	private String classStr;
	private String subClassStr;
	private String dcsStr;
	private String vendorStr;
	private String skuNumStr;
	private String dateStr;
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private Boolean strRedempChk;
	private Boolean allStrChk;
	private String selectedStoreStr;
	private String exclRedemDateStr;

	public LoyaltyProgramExclusion() {
	}
	
	public String getExclRedemDateStr() {
		return exclRedemDateStr;
	}

	public void setExclRedemDateStr(String exclRedemDateStr) {
		this.exclRedemDateStr = exclRedemDateStr;
	}

	public String getSelectedStoreStr() {
		return selectedStoreStr;
	}

	public void setSelectedStoreStr(String selectedStoreStr) {
		this.selectedStoreStr = selectedStoreStr;
	}

	public Boolean getAllStrChk() {
		return allStrChk;
	}

	public void setAllStrChk(Boolean allStrChk) {
		this.allStrChk = allStrChk;
	}

	public Boolean getStrRedempChk() {
		return strRedempChk;
	}

	public void setStrRedempChk(Boolean strRedempChk) {
		this.strRedempChk = strRedempChk;
	}

	public Long getExclusionId() {
		return exclusionId;
	}

	public void setExclusionId(Long exclusionId) {
		this.exclusionId = exclusionId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public char getIssuanceWithPromoFlag() {
		return issuanceWithPromoFlag;
	}

	public void setIssuanceWithPromoFlag(char issuanceWithPromoFlag) {
		this.issuanceWithPromoFlag = issuanceWithPromoFlag;
	}

	public String getIssuancePromoIdStr() {
		return issuancePromoIdStr;
	}

	public void setIssuancePromoIdStr(String issuancePromoIdStr) {
		this.issuancePromoIdStr = issuancePromoIdStr;
	}

	public char getRedemptionWithPromoFlag() {
		return redemptionWithPromoFlag;
	}

	public void setRedemptionWithPromoFlag(char redemptionWithPromoFlag) {
		this.redemptionWithPromoFlag = redemptionWithPromoFlag;
	}

	public String getRedemptionPromoIdStr() {
		return redemptionPromoIdStr;
	}

	public void setRedemptionPromoIdStr(String redemptionPromoIdStr) {
		this.redemptionPromoIdStr = redemptionPromoIdStr;
	}

	public String getStoreNumberStr() {
		return storeNumberStr;
	}

	public void setStoreNumberStr(String storeNumberStr) {
		this.storeNumberStr = storeNumberStr;
	}

	public String getItemCatStr() {
		return itemCatStr;
	}

	public void setItemCatStr(String itemCatStr) {
		this.itemCatStr = itemCatStr;
	}

	public String getDeptCodeStr() {
		return deptCodeStr;
	}

	public void setDeptCodeStr(String deptCodeStr) {
		this.deptCodeStr = deptCodeStr;
	}

	public String getClassStr() {
		return classStr;
	}

	public void setClassStr(String classStr) {
		this.classStr = classStr;
	}

	public String getSubClassStr() {
		return subClassStr;
	}

	public void setSubClassStr(String subClassStr) {
		this.subClassStr = subClassStr;
	}

	public String getDcsStr() {
		return dcsStr;
	}

	public void setDcsStr(String dcsStr) {
		this.dcsStr = dcsStr;
	}

	public String getVendorStr() {
		return vendorStr;
	}

	public void setVendorStr(String vendorStr) {
		this.vendorStr = vendorStr;
	}

	public String getSkuNumStr() {
		return skuNumStr;
	}

	public void setSkuNumStr(String skuNumStr) {
		this.skuNumStr = skuNumStr;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
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
	
	
}
