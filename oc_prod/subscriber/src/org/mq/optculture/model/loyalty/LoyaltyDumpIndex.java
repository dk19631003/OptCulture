package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="LOYALTYINDEXES")
public class LoyaltyDumpIndex {

	private String orgId;
	private String userName;
	private String locationId;
	private List<LoyaltyIndex> dumpFileName;
	
	public LoyaltyDumpIndex() {
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
	public List<LoyaltyIndex> getDumpFileName() {
		return dumpFileName;
	}
	@XmlElement(name="LOYALTYINDEX")
	public void setDumpFileName(List<LoyaltyIndex> dumpFileName) {
		this.dumpFileName = dumpFileName;
	}
	
}
