package org.mq.optculture.model.loyalty;

import java.util.List;

import org.mq.optculture.model.loyalty.OTPRedeemLimit;

public class InquiryInfo {
	private String EMPID;
	private String STORELOCATIONID;
	private String RECEIPTNUMBER;
	private String RECEIPTAMOUNT;
	private String CUSTOMERID;
	private String CARDNUMBER;
	private String CARDPIN;
	private String OTPENABLED;
	private List<OTPRedeemLimit> OTPREDEEMLIMIT;
	
	 
	private String CARDTYPE;
	private String TIERNAME;
	
	public String getTIERNAME() {
		return TIERNAME;
	}
	
	public void setTIERNAME(String tIERNAME) {
		TIERNAME = tIERNAME;
	}
	
	public String getEMPID() {
		return EMPID;
	}
	public void setEMPID(String eMPID) {
		EMPID = eMPID;
	}
	public String getSTORELOCATIONID() {
		return STORELOCATIONID;
	}
	public void setSTORELOCATIONID(String sTORELOCATIONID) {
		STORELOCATIONID = sTORELOCATIONID;
	}
	public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getCARDNUMBER() {
		return CARDNUMBER;
	}
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}
	public String getCARDPIN() {
		return CARDPIN;
	}
	public void setCARDPIN(String cARDPIN) {
		CARDPIN = cARDPIN;
	}
	public String getCARDTYPE() {
		return CARDTYPE;
	}
	public void setCARDTYPE(String cARDTYPE) {
		CARDTYPE = cARDTYPE;
	}
	
	
	public String getOTPENABLED() {
		return OTPENABLED;
	}

	public void setOTPENABLED(String oTPENABLED) {
		OTPENABLED = oTPENABLED;
	}

	public List<OTPRedeemLimit> getOTPREDEEMLIMIT() {
		return OTPREDEEMLIMIT;
	}

	public void setOTPREDEEMLIMIT(List<OTPRedeemLimit> oTPREDEEMLIMIT) {
		OTPREDEEMLIMIT = oTPREDEEMLIMIT;
	}

	public String getRECEIPTNUMBER() {
		return RECEIPTNUMBER;
	}

	public void setRECEIPTNUMBER(String rECEIPTNUMBER) {
		RECEIPTNUMBER = rECEIPTNUMBER;
	}

	public String getRECEIPTAMOUNT() {
		return RECEIPTAMOUNT;
	}

	public void setRECEIPTAMOUNT(String rECEIPTAMOUNT) {
		RECEIPTAMOUNT = rECEIPTAMOUNT;
	}
}
