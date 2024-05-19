package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.poi.ss.util.CellReference.NameType;


public class PromoCustomerInformation {
	
	public PromoCustomerInformation(){}
	
	private List<PromoCustomerInfo> promoCustomList;

	@XmlElement(name="CUSTOMER_INFO")
	public List<PromoCustomerInfo> getPromoCustomList() {
		return promoCustomList;
	}

	public void setPromoCustomList(List<PromoCustomerInfo> promoCustomList) {
		this.promoCustomList = promoCustomList;
	}
	
}
