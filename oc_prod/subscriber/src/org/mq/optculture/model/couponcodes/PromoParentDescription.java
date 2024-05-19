package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


public class PromoParentDescription {
	
	
	private String promoName;//Attribute
	private String type;//Attribute
	private String status;//Attribute
	private String orgName;//Attribute
	private String ValidFrom;//Element
	private String validTo;//Element
	private String loyaltypoints;//Element
	private PromoGeneration promoGenreation;//Element
	private String discountCriteria;//Element
	
	private DiscountInformation discountInformation;
	private String storeNumFlag;
	private PromoStoreNumber promoStoreNumber;
	
	public PromoParentDescription(){}

	@XmlAttribute(name="name")
	public String getPromoName() {
		return promoName;
	}
	public void setPromoName(String promoName) {
		this.promoName = promoName;
	}


	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name="status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@XmlAttribute(name="orgId")
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@XmlElement(name="VALIDFROM")
	public String getValidFrom() {
		return ValidFrom;
	}
	public void setValidFrom(String validFrom) {
		ValidFrom = validFrom;
	}

	@XmlElement(name="VALIDTO")
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

	@XmlElement(name="LOYALTYPOINTS")
	public String getLoyaltypoints() {
		return loyaltypoints;
	}
	public void setLoyaltypoints(String loyaltypoints) {
		this.loyaltypoints = loyaltypoints;
	}

	@XmlElement(name="PROMO_GENERATION")
	public PromoGeneration getPromoGenreation() {
		return promoGenreation;
	}
	public void setPromoGenreation(PromoGeneration promoGenreation) {
		this.promoGenreation = promoGenreation;
	}

	@XmlElement(name="DISCOUNTCRITERIA")
	public String getDiscountCriteria() {
		return discountCriteria;
	}
	public void setDiscountCriteria(String discountCriteria) {
		this.discountCriteria = discountCriteria;
	}

	
	@XmlElement(name="DISCOUNTINFORMATION")
	public DiscountInformation getDiscountInformation() {
		return discountInformation;
	}
	public void setDiscountInformation(DiscountInformation discountInformation) {
		this.discountInformation = discountInformation;
	}
	
	
	@XmlElement(name="STORENUMBER_FLAG")
	public String getStoreNumFlag() {
		return storeNumFlag;
	}

	public void setStoreNumFlag(String storeNumFlag) {
		this.storeNumFlag = storeNumFlag;
	}
	
	@XmlElement(name="STORE_NUMBERS")
	public PromoStoreNumber getPromoStoreNumber() {
		return promoStoreNumber;
	}

	public void setPromoStoreNumber(PromoStoreNumber promoStoreNumber) {
		this.promoStoreNumber = promoStoreNumber;
	}
	
	
	
	
	
	
}
