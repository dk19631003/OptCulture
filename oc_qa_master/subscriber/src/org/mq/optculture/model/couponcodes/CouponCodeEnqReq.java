package org.mq.optculture.model.couponcodes;

import java.util.List;

import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.couponcodes.PurchasedItems;

public class CouponCodeEnqReq {
	
	private HeaderInfo HEADERINFO;
	private CouponCodeInfo COUPONCODEINFO;
	private List<PurchasedItems> PURCHASEDITEMS;
	private UserDetails USERDETAILS;
	
	public CouponCodeEnqReq() {
	}
	public CouponCodeEnqReq(HeaderInfo hEADERINFO,
			CouponCodeInfo cOUPONCODEINFO, List<PurchasedItems> pURCHASEDITEMS,
			UserDetails uSERDETAILS) {
		HEADERINFO = hEADERINFO;
		COUPONCODEINFO = cOUPONCODEINFO;
		PURCHASEDITEMS = pURCHASEDITEMS;
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
	public List<PurchasedItems> getPURCHASEDITEMS() {
		return PURCHASEDITEMS;
	}
	public void setPURCHASEDITEMS(List<PurchasedItems> pURCHASEDITEMS) {
		PURCHASEDITEMS = pURCHASEDITEMS;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	
}
