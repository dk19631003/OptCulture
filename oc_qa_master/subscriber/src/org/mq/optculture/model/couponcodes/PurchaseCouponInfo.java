package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class PurchaseCouponInfo {
	String  TOTALAMOUNT;
	String TOTALDISCOUNT;
	String USEDLOYALTYPOINTS;
	String TYPE;
	

	public PurchaseCouponInfo() {
	}
	
	public PurchaseCouponInfo(String tOTALAMOUNT, String tOTALDISCOUNT,
			String uSEDLOYALTYPOINTS, String tYPE) {
		TOTALAMOUNT = tOTALAMOUNT;
		TOTALDISCOUNT = tOTALDISCOUNT;
		USEDLOYALTYPOINTS = uSEDLOYALTYPOINTS;
		TYPE = tYPE;
	}
	
	public String getTYPE() {
		return TYPE;
	}
	
	@XmlElement(name = "TYPE")
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	
	public String getTOTALAMOUNT() {
		return TOTALAMOUNT;
	}
	
	@XmlElement(name = "TOTALAMOUNT")
	public void setTOTALAMOUNT(String tOTALAMOUNT) {
		TOTALAMOUNT = tOTALAMOUNT;
	}
	public String getTOTALDISCOUNT() {
		return TOTALDISCOUNT;
	}
	
	
	@XmlElement(name = "TOTALDISCOUNT")
	public void setTOTALDISCOUNT(String tOTALDISCOUNT) {
		TOTALDISCOUNT = tOTALDISCOUNT;
	}
	public String getUSEDLOYALTYPOINTS() {
		return USEDLOYALTYPOINTS;
	}
	
	
	@XmlElement(name = "USEDLOYALTYPOINTS")
	public void setUSEDLOYALTYPOINTS(String uSEDLOYALTYPOINTS) {
		USEDLOYALTYPOINTS = uSEDLOYALTYPOINTS;
	}

}
