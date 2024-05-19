package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
/**
 * This object is defined for enrollment JSON response component of ENROLLMENTRESPONSE
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyEnrollResponseObject extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7846399642266050529L;
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private EnrollmentInfo ENROLLMENTINFO;
	private AmountDetails AMOUNTDETAILS;
	private StatusInfo STATUS;
	private CustomerInfo CUSTOMERINFO;
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
	public EnrollmentInfo getENROLLMENTINFO() {
		return ENROLLMENTINFO;
	}
	public void setENROLLMENTINFO(EnrollmentInfo eNROLLMENTINFO) {
		ENROLLMENTINFO = eNROLLMENTINFO;
	}
	public StatusInfo getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(StatusInfo sTATUS) {
		STATUS = sTATUS;
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
	
}
