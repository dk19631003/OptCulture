package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PROMOS")
public class PromoParentRootObject {
	
	public  PromoParentRootObject(){}
	
	private PromoParentDescription promoParentDescObj;
	
	@XmlElement(name="PROMO")
	public PromoParentDescription getPromoParentDescObj() {
		return promoParentDescObj;
	}

	public void setPromoParentDescObj(PromoParentDescription promoParentDescObj) {
		this.promoParentDescObj = promoParentDescObj;
	}
}
