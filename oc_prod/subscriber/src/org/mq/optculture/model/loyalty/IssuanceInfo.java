package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

/**
 * Loyalty issuance request data.
 * @author Venkata Rathnam D
 *
 */
public class IssuanceInfo {

	private String EMPID;
	private String CUSTOMERID;
	private String STORELOCATIONID;
	private String DOCSID;
	private String RECEIPTNUMBER;
	
	private String CARDNUMBER;
	private String CARDPIN;
	private String VALUECODE;
	private String ENTEREDAMOUNT;
	private String TIERNAME;
	
	
	private String CREATEDDATE; //  is created for setting created_date while implementing bulk transaction utility
	
	
	public String getCREATEDDATE() {
		return CREATEDDATE;
	}
	public void setCREATEDDATE(String cREATEDDATE) {
		CREATEDDATE = cREATEDDATE;
	}
	public String getTIERNAME() {
		return TIERNAME;
	}
	//@XmlElement(name = "TIERNAME")
	public void setTIERNAME(String tIERNAME) {
		TIERNAME = tIERNAME;
	}
	
	public String getEMPID() {
		return EMPID;
	}
	@XmlElement(name = "EMPID")
	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}
	public String getSTORELOCATIONID() {
		return STORELOCATIONID;
	}
	@XmlElement(name = "STORELOCATIONID")
	public void setSTORELOCATIONID(String sTORELOCATIONID) {
		STORELOCATIONID = sTORELOCATIONID;
	}
	
	public String getDOCSID() {
		return DOCSID;
	}
	@XmlElement(name = "DOCSID")
	public void setDOCSID(String dOCSID) {
		DOCSID = dOCSID;
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
	public String getVALUECODE() {
		return VALUECODE;
	}
	@XmlElement(name = "VALUECODE")
	public void setVALUECODE(String vALUECODE) {
		VALUECODE = vALUECODE;
	}
	public String getENTEREDAMOUNT() {
		return ENTEREDAMOUNT;
	}
	@XmlElement(name = "ENTEREDAMOUNT")
	public void setENTEREDAMOUNT(String eNTEREDAMOUNT) {
		ENTEREDAMOUNT = eNTEREDAMOUNT;
	}
	public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	@XmlElement(name = "CUSTOMERID")
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getRECEIPTNUMBER() {
		return RECEIPTNUMBER;
	}
	@XmlElement(name = "RECEIPTNUMBER")
	public void setRECEIPTNUMBER(String rECEIPTNUMBER) {
		RECEIPTNUMBER = rECEIPTNUMBER;
	}
}
