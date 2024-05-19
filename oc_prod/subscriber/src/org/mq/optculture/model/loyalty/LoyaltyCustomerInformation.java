package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="LOYALTYCUSTOMERS")
public class LoyaltyCustomerInformation {
	
	public LoyaltyCustomerInformation(){}
	
	private List<LoyaltyCustomer> loyaltyCustomerList;

	@XmlElement(name="LOYALTYCUSTOMER")
	public List<LoyaltyCustomer> getLoyaltyCustomerList() {
		return loyaltyCustomerList;
	}

	public void setLoyaltyCustomerList(List<LoyaltyCustomer> loyaltyCustomerList) {
		this.loyaltyCustomerList = loyaltyCustomerList;
	}	
}