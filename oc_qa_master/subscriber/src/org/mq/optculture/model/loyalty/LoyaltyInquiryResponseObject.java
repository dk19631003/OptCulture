package org.mq.optculture.model.loyalty;

import java.util.List;

/**
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyInquiryResponseObject {
	private CustomerInfo CUSTOMERINFO;
	private List<Balances> BALANCES;
	private StatusInfo STATUS;
	private HeaderInfo HEADERINFO;
	private InquiryInfo INQUIRYINFO;
	private UserDetails USERDETAILS;
	public CustomerInfo getCUSTOMERINFO() {
		return CUSTOMERINFO;
	}
	public void setCUSTOMERINFO(CustomerInfo cUSTOMERINFO) {
		CUSTOMERINFO = cUSTOMERINFO;
	}
	public List<Balances> getBALANCES() {
		return BALANCES;
	}
	public void setBALANCES(List<Balances> bALANCES) {
		BALANCES = bALANCES;
	}
	public StatusInfo getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(StatusInfo sTATUS) {
		STATUS = sTATUS;
	}
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public InquiryInfo getINQUIRYINFO() {
		return INQUIRYINFO;
	}
	public void setINQUIRYINFO(InquiryInfo iNQUIRYINFO) {
		INQUIRYINFO = iNQUIRYINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
}
