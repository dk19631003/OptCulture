package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class PromoItemCodeInfo {

	public PromoItemCodeInfo(){}
	
	
	private List<String> itemCodesList;

	@XmlElement(name="ITEMCODE")
	public List<String> getItemCodesList() {
		return itemCodesList;
	}


	public void setItemCodesList(List<String> itemCodesList) {
		this.itemCodesList = itemCodesList;
	}
	
	
	
}
