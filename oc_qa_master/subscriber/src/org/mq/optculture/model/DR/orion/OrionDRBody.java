package org.mq.optculture.model.DR.orion;

import java.util.List;

public class OrionDRBody {

	private List<OrionDRItem> Items;
	private OrionReceipt Receipt;
	private OrionMasterCard MASTER_CARD;//TODO need to confirm _ is there or not
	
	public List<OrionDRItem> getItems() {
		return Items;
	}
	public void setItems(List<OrionDRItem> items) {
		Items = items;
	}
	public OrionReceipt getReceipt() {
		return Receipt;
	}
	public void setReceipt(OrionReceipt receipt) {
		Receipt = receipt;
	}
	public OrionMasterCard getMASTER_CARD() {
		return MASTER_CARD;
	}
	public void setMASTER_CARD(OrionMasterCard mASTER_CARD) {
		MASTER_CARD = mASTER_CARD;
	}

}
