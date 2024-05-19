package org.mq.optculture.model.couponcodes;

import java.util.List;

public class DiscountInfo {
	private String VALUE;
	private String VALUECODE;
	private String MINPURCHASEVALUE;
	private List<ItemCodeInfo> ITEMCODEINFO;
	
	private String RECEIPTDISCOUNT;
	private String MAXRECEIPTDISCOUNT;
	private String ITEMPRICE;
	private String ELIGIBILITY;	
	
	private String SHIPPINGFEE;
	private String SHIPPINGFEEFREE;
	public String getSHIPPINGFEEFREE() {
		return SHIPPINGFEEFREE;
	}
	public void setSHIPPINGFEEFREE(String sHIPPINGFEEFREE) {
		SHIPPINGFEEFREE = sHIPPINGFEEFREE;
	}
	public String getSHIPPINGFEE() {
		return SHIPPINGFEE;
	}
	public void setSHIPPINGFEE(String sHIPPINGFEE) {
		SHIPPINGFEE = sHIPPINGFEE;
	}
	public String getSHIPPINGFEETYPE() {
		return SHIPPINGFEETYPE;
	}
	public void setSHIPPINGFEETYPE(String sHIPPINGFEETYPE) {
		SHIPPINGFEETYPE = sHIPPINGFEETYPE;
	}
	private String SHIPPINGFEETYPE;
	public DiscountInfo() {
	}
	public DiscountInfo(String VALUE, String VALUECODE,
			String MINPURCHASEVALUE, List<ItemCodeInfo> ITEMCODEINFO) {
		this.VALUE = VALUE;
		this.VALUECODE = VALUECODE;
		this.MINPURCHASEVALUE = MINPURCHASEVALUE;
		this.ITEMCODEINFO = ITEMCODEINFO;
	}
	

	public String getMAXRECEIPTDISCOUNT() {
		return MAXRECEIPTDISCOUNT;
	}
	public void setMAXRECEIPTDISCOUNT(String mAXALLOWEDDISCOUNT) {
		MAXRECEIPTDISCOUNT = mAXALLOWEDDISCOUNT;
	}
	public String getRECEIPTDISCOUNT() {
		return RECEIPTDISCOUNT;
	}
	public void setRECEIPTDISCOUNT(String rECEIPTDISCOUNT) {
		RECEIPTDISCOUNT = rECEIPTDISCOUNT;
	}
	
	
	public String getVALUE() {
		return VALUE;
	}
	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	
	public String getVALUECODE() {
		return VALUECODE;
	}
	public void setVALUECODE(String vALUECODE) {
		VALUECODE = vALUECODE;
	}
	
	public String getMINPURCHASEVALUE() {
		return MINPURCHASEVALUE;
	}
	public void setMINPURCHASEVALUE(String mINPURCHASEVALUE) {
		MINPURCHASEVALUE = mINPURCHASEVALUE;
	}
	
	public List<ItemCodeInfo> getITEMCODEINFO() {
		return ITEMCODEINFO;
	}
	public void setITEMCODEINFO(List<ItemCodeInfo> iTEMCODEINFO) {
		ITEMCODEINFO = iTEMCODEINFO;
	}
	public String getITEMPRICE() {
		return ITEMPRICE;
	}
	public void setITEMPRICE(String iTEMPRICE) {
		ITEMPRICE = iTEMPRICE;
	}
	/*public String getITEMPRICECRITERIA() {
		return ITEMPRICECRITERIA;
	}
	public void setITEMPRICECRITERIA(String iTEMPRICECRITERIA) {
		ITEMPRICECRITERIA = iTEMPRICECRITERIA;
	}*/
	public String getELIGIBILITY() {
		return ELIGIBILITY;
	}
	public void setELIGIBILITY(String eLIGIBILITY) {
		ELIGIBILITY = eLIGIBILITY;
	}

	
}
