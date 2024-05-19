package org.mq.optculture.model.DR.magento;

import java.util.List;

public class MagentoProductOptions {
	
	//private InfoBuyRequest info_buyRequest;
	private String qty;
	private List<MagentoAttributesInfo> attributes_info;
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public List<MagentoAttributesInfo> getAttributes_info() {
		return attributes_info;
	}
	public void setAttributes_info(List<MagentoAttributesInfo> attributes_info) {
		this.attributes_info = attributes_info;
	}
}
