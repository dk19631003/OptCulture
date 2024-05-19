package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="LOYALTYINFOS")
public class LoyaltyDumpData {

	private String orgId;
	private String userName;
	private String locationId;
	private List<LoyaltyDumpDataRecord> loyaltyInfoRecords;
	
	public LoyaltyDumpData() {
		// TODO Auto-generated constructor stub
	}
	
	public String getOrgId() {
		return orgId;
	}
	@XmlAttribute(name="orgId")
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getUserName() {
		return userName;
	}
	@XmlAttribute(name="userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLocationId() {
		return locationId;
	}
	@XmlAttribute(name="locationId")
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public List<LoyaltyDumpDataRecord> getLoyaltyInfoRecords() {
		return loyaltyInfoRecords;
	}
	@XmlElement(name="LOYALTYINFO")
	public void setLoyaltyInfoRecords(List<LoyaltyDumpDataRecord> loyaltyInfoRecords) {
		this.loyaltyInfoRecords = loyaltyInfoRecords;
	}
	
}
