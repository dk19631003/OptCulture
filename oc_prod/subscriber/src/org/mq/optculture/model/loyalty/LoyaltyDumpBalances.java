package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class LoyaltyDumpBalances {

	private List<LtyBalanceType> balanceTypes;
	
	public LoyaltyDumpBalances() {
		// TODO Auto-generated constructor stub
	}

	public List<LtyBalanceType> getBalanceTypes() {
		return balanceTypes;
	}
	@XmlElement(name="BALANCE")
	public void setBalanceTypes(List<LtyBalanceType> balanceTypes) {
		this.balanceTypes = balanceTypes;
	}
	
	
}
