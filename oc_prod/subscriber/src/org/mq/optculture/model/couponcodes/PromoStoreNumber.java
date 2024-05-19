package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class PromoStoreNumber {
	
	public PromoStoreNumber(){}
	
	private List<String> storeList;
	
	@XmlElement(name="STORE")
	public List<String> getStoreList() {
		return storeList;
	}
	public void setStoreList(List<String> storeList) {
		this.storeList = storeList;
	}

}
