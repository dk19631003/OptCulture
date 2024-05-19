package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyInquiryRequestObject extends BaseRequestObject{
	private HeaderInfo HEADERINFO;
	private UserDetails	USERDETAILS;
	private InquiryInfo INQUIRYINFO;
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	public InquiryInfo getINQUIRYINFO() {
		return INQUIRYINFO;
	}
	public void setINQUIRYINFO(InquiryInfo iNQUIRYINFO) {
		INQUIRYINFO = iNQUIRYINFO;
	}
	
}
