package org.mq.optculture.model.loyalty;

import java.util.List;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
/**
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyIssuanceResponseObject extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7953447351938619547L;
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private IssuanceInfo ISSUANCEINFO;
	private List<Balances> BALANCES;
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
	public IssuanceInfo getISSUANCEINFO() {
		return ISSUANCEINFO;
	}
	public void setISSUANCEINFO(IssuanceInfo iSSUANCEINFO) {
		ISSUANCEINFO = iSSUANCEINFO;
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
	public CustomerInfo getCUSTOMERINFO() {
		return CUSTOMERINFO;
	}
	public void setCUSTOMERINFO(CustomerInfo cUSTOMERINFO) {
		CUSTOMERINFO = cUSTOMERINFO;
	}
	
}
