package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class DiscountInformation {
	
	public DiscountInformation(){}
	
	private List<OptSyncPromoDiscountInfo> promoDiscountInfoList;

	@XmlElement(name="DISCOUNTINFO")
	public List<OptSyncPromoDiscountInfo> getPromoDiscountInfoList() {
		return promoDiscountInfoList;
	}

	public void setPromoDiscountInfoList(
			List<OptSyncPromoDiscountInfo> promoDiscountInfoList) {
		this.promoDiscountInfoList = promoDiscountInfoList;
	}//Element

	
	
}
