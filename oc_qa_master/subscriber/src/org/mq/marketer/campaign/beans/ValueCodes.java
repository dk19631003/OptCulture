package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ValueCodes {

	 private Long Id;
	 private String ValuCode;
	 private String Description;
	 private Long  OrgId;
	 private String CreatedBy;
	 private String ModifiedBy;
	 private Calendar CreatedDate;
	 private Calendar ModifiedDate;
	 private boolean associatedWithFBP;
	 
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getValuCode() {
		return ValuCode;
	}
	public void setValuCode(String valuCode) {
		ValuCode = valuCode;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public long getOrgId() {
		return OrgId;
	}
	public void setOrgId(Long orgId) {
		OrgId = orgId;
	}
	public String getCreatedBy() {
		return CreatedBy;
	}
	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}
	public Calendar getCreatedDate() {
		return CreatedDate;
	}
	public String getModifiedBy() {
		return ModifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		ModifiedBy = modifiedBy;
	}
	public void setCreatedDate(Calendar createdDate) {
		CreatedDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return ModifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		ModifiedDate = modifiedDate;
	}
	public boolean isAssociatedWithFBP() {
		return associatedWithFBP;
	}
	public void setAssociatedWithFBP(boolean associatedWithFBP) {
		this.associatedWithFBP = associatedWithFBP;
	}
	 	 	
}
