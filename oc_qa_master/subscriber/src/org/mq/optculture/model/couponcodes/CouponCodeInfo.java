package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class CouponCodeInfo {
	private String COUPONNAME;
	private String COUPONCODE;
	private String STORENUMBER;
	
	private String SUBSIDIARYNUMBER;
	
	private String RECEIPTNUMBER;
	
	private String ENTEREDAMOUNT;//for inquiry
	private String RECEIPTAMOUNT;//for redemption
	
	
	private String CUSTOMERID;
	private String PHONE;
	private String EMAIL;
	private String DOCSID;
	
	private String DISCOUNTAMOUNT;//for smart promotion 
	
	private String CARDNUMBER;
	private List<String> APPLIEDCOUPONS;
	private String CHECKREWARDS;

	public String getCHECKREWARDS() {
		return CHECKREWARDS;
	}

	public void setCHECKREWARDS(String cHECKREWARDS) {
		CHECKREWARDS = cHECKREWARDS;
	}
	
	//private String SOURCETYPE;
	
	/*public String getSOURCETYPE() {
		return SOURCETYPE;
	}
	@XmlElement(name = "SOURCETYPE")
	public void setSOURCETYPE(String sOURCETYPE) {
		SOURCETYPE = sOURCETYPE;
	}*/

	public String getCARDNUMBER() {
		return CARDNUMBER;
	}

	@XmlElement(name = "CARDNUMBER")
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}

	public String getDISCOUNTAMOUNT() {
		return DISCOUNTAMOUNT;
	}
	
	@XmlElement(name = "DISCOUNTAMOUNT")
	public void setDISCOUNTAMOUNT(String dISCOUNTAMOUNT) {
		DISCOUNTAMOUNT = dISCOUNTAMOUNT;
	}

	public CouponCodeInfo() {
	}
	
	public CouponCodeInfo(String cOUPONNAME, String cOUPONCODE, String sTORENUMBER,
			String cUSTOMERID, String pHONE, String eMAIL, String dOCSID, String sUBSIDIRYNUMBER, 
			String rECEIPTNUMBER, String eNTEREDAMOUNT) {
		COUPONNAME = cOUPONNAME;
		COUPONCODE = cOUPONCODE;
		STORENUMBER = sTORENUMBER;
		CUSTOMERID = cUSTOMERID;
		PHONE = pHONE;
		EMAIL = eMAIL;
		DOCSID = dOCSID;
		this.SUBSIDIARYNUMBER = sUBSIDIRYNUMBER;
		this.RECEIPTNUMBER = rECEIPTNUMBER;
		this.ENTEREDAMOUNT = eNTEREDAMOUNT;
	}

	public String getCOUPONNAME() {
		return COUPONNAME;
	}
	@XmlElement(name = "COUPONNAME")
	public void setCOUPONNAME(String cOUPONNAME) {
		COUPONNAME = cOUPONNAME;
	}

	public String getCOUPONCODE() {
		return COUPONCODE;
	}
	@XmlElement(name = "COUPONCODE")
	public void setCOUPONCODE(String cOUPONCODE) {
		COUPONCODE = cOUPONCODE;
	}
	public String getSTORENUMBER() {
		return STORENUMBER;
	}
	@XmlElement(name = "STORENUMBER")
	public void setSTORENUMBER(String sTORENUMBER) {
		STORENUMBER = sTORENUMBER;
	}
	public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	@XmlElement(name = "CUSTOMERID")
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getPHONE() {
		return PHONE;
	}
	@XmlElement(name = "PHONE")
	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}
	public String getEMAIL() {
		return EMAIL;
	}
	@XmlElement(name = "EMAIL")
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getDOCSID() {
		return DOCSID;
	}
	@XmlElement(name = "DOCSID")
	public void setDOCSID(String dOCSID) {
		DOCSID = dOCSID;
	}
	public String getSUBSIDIARYNUMBER() {
		return SUBSIDIARYNUMBER;
	}
	@XmlElement(name = "SUBSIDIARYNUMBER")
	public void setSUBSIDIARYNUMBER(String sUBSIDIARYNUMBER) {
		SUBSIDIARYNUMBER = sUBSIDIARYNUMBER;
	}
	
	public String getRECEIPTNUMBER() {
		return RECEIPTNUMBER;
	}
	@XmlElement(name = "RECEIPTNUMBER")
	public void setRECEIPTNUMBER(String rECEIPTNUMBER) {
		RECEIPTNUMBER = rECEIPTNUMBER;
	}

	public String getENTEREDAMOUNT() {
		return ENTEREDAMOUNT;
	}
	@XmlElement(name = "ENTEREDAMOUNT")
	public void setENTEREDAMOUNT(String eNTEREDAMOUNT) {
		ENTEREDAMOUNT = eNTEREDAMOUNT;
	}
	
	public String getRECEIPTAMOUNT() {
		return RECEIPTAMOUNT;
	}
	@XmlElement(name = "RECEIPTAMOUNT")
	public void setRECEIPTAMOUNT(String rECEIPTAMOUNT) {
		RECEIPTAMOUNT = rECEIPTAMOUNT;
	}

	public List<String> getAPPLIEDCOUPONS() {
		return APPLIEDCOUPONS;
	}

	public void setAPPLIEDCOUPONS(List<String> aPPLIEDCOUPONS) {
		APPLIEDCOUPONS = aPPLIEDCOUPONS;
	}
}
