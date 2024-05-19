package org.mq.optculture.model.couponcodes;

public class PurchasedItems {
	private String ITEMCODE;
	
	private String ITEMPRICE;
	private String ITEMDISCOUNT;
	private String QUANTITY;
	
	public PurchasedItems() {
	}
	
	public String getQUANTITY() {
		return QUANTITY;
	}
	
	
	public void setQUANTITY(String qUANTITY) {
		QUANTITY = qUANTITY;
	}
	
	public String getITEMDISCOUNT() {
		return ITEMDISCOUNT;
	}
	
	
	public void setITEMDISCOUNT(String iTEMDISCOUNT) {
		ITEMDISCOUNT = iTEMDISCOUNT;
	}
	
	public String getITEMPRICE() {
		return ITEMPRICE;
	}
	
	
	public void setITEMPRICE(String iTEMPRICE) {
		ITEMPRICE = iTEMPRICE;
	}
	
	public String getITEMCODE() {
		return ITEMCODE;
	}
	public void setITEMCODE(String iTEMCODE) {
		ITEMCODE = iTEMCODE;
	}
}
