package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class OrgPromos {
	
	private String name;
	private List<String> promoCodeList;
	private String type;
//	private String promoCode;
	public OrgPromos(){}

	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="PROMO_CODE")
	public List<String> getPromoCodeList() {
		return promoCodeList;
	}


	public void setPromoCodeList(List<String> promoCodeList) {
		this.promoCodeList = promoCodeList;
	}
	
	
}
