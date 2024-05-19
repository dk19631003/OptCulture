package org.mq.optculture.model.couponcodes;

import java.util.List;

public class CouponCodeResponse {
	private List<CouponDiscountInfo> COUPONDISCOUNTINFO;
	private StatusInfo STATUSINFO;
	private HeaderInfo HEADERINFO;
	private LoyaltyInfo LOYALTYINFO;

	

	public CouponCodeResponse(List<CouponDiscountInfo> cOUPONDISCOUNTINFO,
			StatusInfo sTATUSINFO, HeaderInfo hEADERINFO) {
		COUPONDISCOUNTINFO = cOUPONDISCOUNTINFO;
		STATUSINFO = sTATUSINFO;
		HEADERINFO = hEADERINFO;
	}

	public CouponCodeResponse(List<CouponDiscountInfo> cOUPONDISCOUNTINFO,
			StatusInfo sTATUSINFO, HeaderInfo hEADERINFO,LoyaltyInfo LOYALTYINFO) {
		COUPONDISCOUNTINFO = cOUPONDISCOUNTINFO;
		STATUSINFO = sTATUSINFO;
		HEADERINFO = hEADERINFO;
		this.LOYALTYINFO = LOYALTYINFO;
	}

	
	public List<CouponDiscountInfo> getCOUPONDISCOUNTINFO() {
		return COUPONDISCOUNTINFO;
	}

	public void setCOUPONDISCOUNTINFO(List<CouponDiscountInfo> cOUPONDISCOUNTINFO) {
		COUPONDISCOUNTINFO = cOUPONDISCOUNTINFO;
	}

	public StatusInfo getSTATUSINFO() {
		return STATUSINFO;
	}

	public void setSTATUSINFO(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}

	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}

	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public LoyaltyInfo getLOYALTYINFO() {
		return LOYALTYINFO;
	}

	public void setLOYALTYINFO(LoyaltyInfo lOYALTYINFO) {
		LOYALTYINFO = lOYALTYINFO;
	}
}
