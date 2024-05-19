package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyBaseRequestObject extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8982004019665597885L;
	private HeaderInfo HEADERINFO;
	private EnrollmentInfo ENROLLMENTINFO;
	private CustomerInfo CUSTOMERINFO;
	private AmountDetails AMOUNTDETAILS;
	private UserDetails USERDETAILS;
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public EnrollmentInfo getENROLLMENTINFO() {
		return ENROLLMENTINFO;
	}
	public void setENROLLMENTINFO(EnrollmentInfo eNROLLMENTINFO) {
		ENROLLMENTINFO = eNROLLMENTINFO;
	}
	public CustomerInfo getCUSTOMERINFO() {
		return CUSTOMERINFO;
	}
	public void setCUSTOMERINFO(CustomerInfo cUSTOMERINFO) {
		CUSTOMERINFO = cUSTOMERINFO;
	}
	public AmountDetails getAMOUNTDETAILS() {
		return AMOUNTDETAILS;
	}
	public void setAMOUNTDETAILS(AmountDetails aMOUNTDETAILS) {
		AMOUNTDETAILS = aMOUNTDETAILS;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	
}
