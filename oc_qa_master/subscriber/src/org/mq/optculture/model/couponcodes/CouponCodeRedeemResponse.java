package org.mq.optculture.model.couponcodes;

public class CouponCodeRedeemResponse {

	private StatusInfo STATUSINFO;
	private HeaderInfo HEADERINFO;
	private CouponCodeInfo COUPONCODEINFO;
	private  PurchaseCouponInfo PURCHASECOUPONINFO;
	private  UserDetails USERDETAILS;
	
	public CouponCodeRedeemResponse() {
	}
	public CouponCodeRedeemResponse(StatusInfo sTATUSINFO,
			HeaderInfo hEADERINFO, CouponCodeInfo cOUPONCODEINFO,
			PurchaseCouponInfo pURCHASECOUPONINFO, UserDetails uSERDETAILS) {
		STATUSINFO = sTATUSINFO;
		HEADERINFO = hEADERINFO;
		COUPONCODEINFO = cOUPONCODEINFO;
		PURCHASECOUPONINFO = pURCHASECOUPONINFO;
		USERDETAILS = uSERDETAILS;
	}
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public CouponCodeInfo getCOUPONCODEINFO() {
		return COUPONCODEINFO;
	}
	public void setCOUPONCODEINFO(CouponCodeInfo cOUPONCODEINFO) {
		COUPONCODEINFO = cOUPONCODEINFO;
	}
	public PurchaseCouponInfo getPURCHASECOUPONINFO() {
		return PURCHASECOUPONINFO;
	}
	public void setPURCHASECOUPONINFO(PurchaseCouponInfo pURCHASECOUPONINFO) {
		PURCHASECOUPONINFO = pURCHASECOUPONINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	public StatusInfo getSTATUSINFO() {
		return STATUSINFO;
	}
	public void setSTATUSINFO(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}
	
}
