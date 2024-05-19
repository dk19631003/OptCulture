package org.mq.optculture.model.couponcodes;

import org.mq.optculture.model.BaseRequestObject;

public class CouponCodeEnquObj extends BaseRequestObject{
	
	private CouponCodeEnqReq COUPONCODEENQREQ;

	public CouponCodeEnquObj() {
	}

	public CouponCodeEnquObj(CouponCodeEnqReq cOUPONCODEENQREQ) {
		COUPONCODEENQREQ = cOUPONCODEENQREQ;
	}

	public CouponCodeEnqReq getCOUPONCODEENQREQ() {
		return COUPONCODEENQREQ;
	}

	public void setCOUPONCODEENQREQ(CouponCodeEnqReq cOUPONCODEENQREQ) {
		COUPONCODEENQREQ = cOUPONCODEENQREQ;
	}

	
}
