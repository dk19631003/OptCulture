package org.mq.optculture.model.couponcodes;

import org.mq.optculture.model.BaseResponseObject;

public class CouponCodeRedeemedResponse extends BaseResponseObject{

	private CouponCodeRedeemResponse COUPONCODEREDEEMRESPONSE;

	public CouponCodeRedeemedResponse() {
	}

	public CouponCodeRedeemedResponse(
			CouponCodeRedeemResponse cOUPONCODEREDEEMRESPONSE) {
		COUPONCODEREDEEMRESPONSE = cOUPONCODEREDEEMRESPONSE;
	}

	public CouponCodeRedeemResponse getCOUPONCODEREDEEMRESPONSE() {
		return COUPONCODEREDEEMRESPONSE;
	}

	public void setCOUPONCODEREDEEMRESPONSE(
			CouponCodeRedeemResponse cOUPONCODEREDEEMRESPONSE) {
		COUPONCODEREDEEMRESPONSE = cOUPONCODEREDEEMRESPONSE;
	}
	
}
