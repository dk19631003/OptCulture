package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;
import org.mq.optculture.model.BaseRequestObject;
/**
 * This object defined for enrollment json request component of ENROLLMENTREQ
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyEnrollRequestObject extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3305772280008307211L;
	private HeaderInfo HEADERINFO;
	private EnrollmentInfo ENROLLMENTINFO;
	private CustomerInfo CUSTOMERINFO;
	private AmountDetails AMOUNTDETAILS;
	private UserDetails USERDETAILS;
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	@XmlElement(name = "HEADERINFO")
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	@XmlElement(name = "USERDETAILS")
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	public EnrollmentInfo getENROLLMENTINFO() {
		return ENROLLMENTINFO;
	}
	@XmlElement(name = "ENROLLMENTINFO")
	public void setENROLLMENTINFO(EnrollmentInfo eNROLLMENTINFO) {
		ENROLLMENTINFO = eNROLLMENTINFO;
	}
	public CustomerInfo getCUSTOMERINFO() {
		return CUSTOMERINFO;
	}
	@XmlElement(name = "CUSTOMERINFO")
	public void setCUSTOMERINFO(CustomerInfo cUSTOMERINFO) {
		CUSTOMERINFO = cUSTOMERINFO;
	}
	public AmountDetails getAMOUNTDETAILS() {
		return AMOUNTDETAILS;
	}
	@XmlElement(name = "AMOUNTDETAILS")
	public void setAMOUNTDETAILS(AmountDetails aMOUNTDETAILS) {
		AMOUNTDETAILS = aMOUNTDETAILS;
	}
	
}
