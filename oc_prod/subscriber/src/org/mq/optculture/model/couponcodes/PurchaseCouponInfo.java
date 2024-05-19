package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class PurchaseCouponInfo {
	String  TOTALAMOUNT;
	String TOTALDISCOUNT;
	String USEDLOYALTYPOINTS;
	
	public PurchaseCouponInfo() {
	}
	
	public PurchaseCouponInfo(String tOTALAMOUNT, String tOTALDISCOUNT,
			String uSEDLOYALTYPOINTS) {
		TOTALAMOUNT = tOTALAMOUNT;
		TOTALDISCOUNT = tOTALDISCOUNT;
		USEDLOYALTYPOINTS = uSEDLOYALTYPOINTS;
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
