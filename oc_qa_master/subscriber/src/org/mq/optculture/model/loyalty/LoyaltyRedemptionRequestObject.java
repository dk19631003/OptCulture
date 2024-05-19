package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;
/**
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyRedemptionRequestObject extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 715680117494985318L;
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private RedemptionInfo REDEMPTIONINFO;
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
	public RedemptionInfo getREDEMPTIONINFO() {
		return REDEMPTIONINFO;
	}
	@XmlElement(name = "REDEMPTIONINFO")
	public void setREDEMPTIONINFO(RedemptionInfo rEDEMPTIONINFO) {
		REDEMPTIONINFO = rEDEMPTIONINFO;
	}
}
