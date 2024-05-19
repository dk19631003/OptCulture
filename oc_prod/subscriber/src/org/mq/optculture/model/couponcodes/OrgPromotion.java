package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ORG")
public class OrgPromotion {
	
	private String orgName;
	private List<OrgPromos> promoNameList;
	
	public OrgPromotion(){}
	
	 @XmlElement(name="ORG_NAME")
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	@XmlElement(name="PROMO")
	public List<OrgPromos> getPromoNameList() {
		return promoNameList;
	}

	public void setPromoNameList(List<OrgPromos> promoNameList) {
		this.promoNameList = promoNameList;
	}
	
	
	
	
}
