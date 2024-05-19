package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyIssuanceRequestObject extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831961210923846896L;
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private IssuanceInfo ISSUANCEINFO;
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
	public IssuanceInfo getISSUANCEINFO() {
		return ISSUANCEINFO;
	}
	@XmlElement(name = "ISSUANCEINFO")
	public void setISSUANCEINFO(IssuanceInfo iSSUANCEINFO) {
		ISSUANCEINFO = iSSUANCEINFO;
	}
	
}
