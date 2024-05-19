package org.mq.optculture.model.couponcodes;

import java.util.List;

public class ItemCodeInfo {
	
	private String ITEMCODE;
	private String ITEMPRICE;
	public String getITEMPRICE() {
		return ITEMPRICE;
	}

	public void setITEMPRICE(String iTEMPRICE) {
		ITEMPRICE = iTEMPRICE;
	}
	private String QUANTITY;
	private String ITEMDISCOUNT;
	private String MAXITEMDISCOUNT;
	 private  List<ItemAttribute> ITEMATTRIBUTE;
	 private String REWARDRATIO;
	 private String QUANTITYCRITERIA;
	 
	 public String getREWARDRATIO() {
			return REWARDRATIO;
		}

		public void setREWARDRATIO(String rEWARDRATIO) {
			REWARDRATIO = rEWARDRATIO;
		}

	public List<ItemAttribute> getITEMATTRIBUTE() {
		return ITEMATTRIBUTE;
	}

	public void setITEMATTRIBUTE(List<ItemAttribute> iTEMATTRIBUTE) {
		ITEMATTRIBUTE = iTEMATTRIBUTE;
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

	public String getMAXITEMDISCOUNT() {
		return MAXITEMDISCOUNT;
	}

	public void setMAXITEMDISCOUNT(String mAXITEMDISCOUNT) {
		MAXITEMDISCOUNT = mAXITEMDISCOUNT;
	}
	
	
	public ItemCodeInfo() {
	}
	
	public ItemCodeInfo(String iTEMCODE) {
		ITEMCODE = iTEMCODE;
	}
	
	public String getITEMCODE() {
		return ITEMCODE;
	}
	
	public void setITEMCODE(String iTEMCODE) {
		ITEMCODE = iTEMCODE;
	}

	public String getQUANTITYCRITERIA() {
		return QUANTITYCRITERIA;
	}

	public void setQUANTITYCRITERIA(String qUANTITYCRITERIA) {
		QUANTITYCRITERIA = qUANTITYCRITERIA;
	}

	

}
