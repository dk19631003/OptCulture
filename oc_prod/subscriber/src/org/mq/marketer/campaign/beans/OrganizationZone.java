package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class OrganizationZone {


    private Long zoneId;
	private String zoneName;
	private String Description;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String createdBy;
	private String modifiedBy;
	private Long domainId;
	private boolean deleteStatus;
		
	private Set<OrganizationStores> subsidiaries = new HashSet<OrganizationStores>(0);
	private Set<OrganizationStores> stores = new HashSet<OrganizationStores>(0);
	public Long getZoneId() {
		return zoneId;
	}
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
		
	public Set<OrganizationStores> getSubsidiaries() {
		return subsidiaries;
	}
	public void setSubsidiaries(Set<OrganizationStores> subsidiaries) {
		this.subsidiaries = subsidiaries;
	}
	public Set<OrganizationStores> getStores() {
		return stores;
	}
	public void setStores(Set<OrganizationStores> stores) {
		this.stores = stores;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public boolean isDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
	
	
	
}

