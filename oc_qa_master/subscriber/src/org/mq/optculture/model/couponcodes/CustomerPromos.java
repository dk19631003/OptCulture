package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PROMOS")
public class CustomerPromos {

	public CustomerPromos(){}
	
//	private Promo promoObj;
	private CustomerPromo custmerPromo;

	@XmlElement(name="PROMO")
	public CustomerPromo getCustmerPromo() {
		return custmerPromo;
	}

	public void setCustmerPromo(CustomerPromo custmerPromo) {
		this.custmerPromo = custmerPromo;
	}
	
	
	/*public List<Promo> getPromoCustInfoList() {
		return promoList;
	}
	public void setPromoCustInfoList(List<Promo> promoCustInfoList) {
		this.promoList = promoCustInfoList;
	}*/
	
}
