package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class CouponCodeRedeemReq {
	
	private HeaderInfo HEADERINFO;
	private CouponCodeInfo COUPONCODEINFO;
	private  PurchaseCouponInfo PURCHASECOUPONINFO;
	private  UserDetails USERDETAILS;
	
	public CouponCodeRedeemReq() {
	}
	public CouponCodeRedeemReq(HeaderInfo hEADERINFO,
			CouponCodeInfo cOUPONCODEINFO,
			PurchaseCouponInfo pURCHASECOUPONINFO, UserDetails uSERDETAILS) {
		HEADERINFO = hEADERINFO;
		COUPONCODEINFO = cOUPONCODEINFO;
		PURCHASECOUPONINFO = pURCHASECOUPONINFO;
		USERDETAILS = uSERDETAILS;
	}
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	
	@XmlElement(name = "HEADERINFO")
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public CouponCodeInfo getCOUPONCODEINFO() {
		return COUPONCODEINFO;
	}
	@XmlElement(name = "COUPONCODEINFO")
	public void setCOUPONCODEINFO(CouponCodeInfo cOUPONCODEINFO) {
		COUPONCODEINFO = cOUPONCODEINFO;
	}
	public PurchaseCouponInfo getPURCHASECOUPONINFO() {
		return PURCHASECOUPONINFO;
	}
	
	@XmlElement(name = "PURCHASECOUPONINFO")
	public void setPURCHASECOUPONINFO(PurchaseCouponInfo pURCHASECOUPONINFO) {
		PURCHASECOUPONINFO = pURCHASECOUPONINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	
	@XmlElement(name = "USERDETAILS")
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	

}
