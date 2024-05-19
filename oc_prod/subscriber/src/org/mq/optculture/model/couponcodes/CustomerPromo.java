package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class CustomerPromo {
	
	private String promoName;
	private String orgId;
	private String promoCode;
	private PromoCustomerInformation promoCustomerInformation;
	
	
	@XmlAttribute(name="promoName")
	public String getPromoName() {
		return promoName;
	}
	public void setPromoName(String promoName) {
		this.promoName = promoName;
	}
	
	@XmlAttribute(name="orgId")
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	
	@XmlAttribute(name="promoCode")
	public String getPromoCode() {
		return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	
	public PromoCustomerInformation getPromoCustomerInformation() {
		return promoCustomerInformation;
	}
	
	@XmlElement(name="CUSTOMER_INFORMATION",type=PromoCustomerInformation.class)
	public void setPromoCustomerInformation(PromoCustomerInformation promoCustomerInformation) {
		this.promoCustomerInformation = promoCustomerInformation;
	}
	
	
}
