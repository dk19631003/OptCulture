package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

public class EnrollmentInfo {
	private String STORELOCATIONID;
	private String CARDNUMBER;
	private String CARDPIN;
	private String CARDTYPE;
	private String EMPID;
	private String TIERNAME;

	public String getTIERNAME() {
		return TIERNAME;
	}
	//@XmlElement(name = "TIERNAME")
	public void setTIERNAME(String tIERNAME) {
		TIERNAME = tIERNAME;
	}
	public String getSTORELOCATIONID() {
		return STORELOCATIONID;
	}
	@XmlElement(name = "STORELOCATIONID")
	public void setSTORELOCATIONID(String sTORELOCATIONID) {
		STORELOCATIONID = sTORELOCATIONID;
	}
	public String getCARDNUMBER() {
		return CARDNUMBER;
	}
	@XmlElement(name = "CARDNUMBER")
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}
	public String getCARDPIN() {
		return CARDPIN;
	}
	@XmlElement(name = "CARDPIN")
	public void setCARDPIN(String cARDPIN) {
		CARDPIN = cARDPIN;
	}
	public String getCARDTYPE() {
		return CARDTYPE;
	}
	@XmlElement(name = "CARDTYPE")
	public void setCARDTYPE(String cARDTYPE) {
		CARDTYPE = cARDTYPE;
	}
	public String getEMPID() {
		return EMPID;
	}
	@XmlElement(name = "EMPID")
	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}
}
