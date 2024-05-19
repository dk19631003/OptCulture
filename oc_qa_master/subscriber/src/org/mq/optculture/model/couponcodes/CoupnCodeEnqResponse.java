package org.mq.optculture.model.couponcodes;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.StatusInfo;


public class CoupnCodeEnqResponse extends BaseResponseObject{
	private CouponCodeResponse COUPONCODERESPONSE;
//	private StatusInfo STATUSINFO;
//	private HeaderInfo HEADERINFO;
	
	public CoupnCodeEnqResponse() {
	}
	public CoupnCodeEnqResponse(CouponCodeResponse COUPONCODERESPONSE,
			StatusInfo STATUSINFO, HeaderInfo HEADERINFO) {
		this.COUPONCODERESPONSE = COUPONCODERESPONSE;
		/*this.STATUSINFO = STATUSINFO;
		this.HEADERINFO = HEADERINFO;*/
	}
	public CouponCodeResponse getCOUPONCODERESPONSE() {
		return COUPONCODERESPONSE;
	}
	public void setCOUPONCODERESPONSE(CouponCodeResponse cOUPONCODERESPONSE) {
		COUPONCODERESPONSE = cOUPONCODERESPONSE;
	}
	/*public StatusInfo getSTATUSINFO() {
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
	}*/
	
	

}
