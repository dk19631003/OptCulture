package org.mq.optculture.model.couponcodes;

import org.mq.optculture.model.BaseRequestObject;

public class CouponCodeRedeemedObj extends BaseRequestObject{
	
	private CouponCodeRedeemReq COUPONCODEREDEEMREQ;
	
	public CouponCodeRedeemedObj() {
	}

	public CouponCodeRedeemedObj(CouponCodeRedeemReq cOUPONCODEREDEEMREQ) {
		COUPONCODEREDEEMREQ = cOUPONCODEREDEEMREQ;
	}

	public CouponCodeRedeemReq getCOUPONCODEREDEEMREQ() {
		return COUPONCODEREDEEMREQ;
	}

	public void setCOUPONCODEREDEEMREQ(CouponCodeRedeemReq cOUPONCODEREDEEMREQ) {
		COUPONCODEREDEEMREQ = cOUPONCODEREDEEMREQ;
	}
	

}
