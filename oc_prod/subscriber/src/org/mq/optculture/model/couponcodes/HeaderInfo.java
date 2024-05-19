package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class HeaderInfo {
	
	private String REQUESTID;
	private String PCFLAG;
	private String STORENUMBER;
	private String SOURCETYPE;
	private String SUBSIDIARYNUMBER;
	
	private String RECEIPTNUMBER;
	
	private String ENTEREDAMOUNT;//for inquiry
	private String RECEIPTAMOUNT;//for redemption
	
	
	public HeaderInfo(){}
	public HeaderInfo(String rEQUESTID) {
		REQUESTID = rEQUESTID;
	}
	
	public String getREQUESTID() {
		return REQUESTID;
	}
	@XmlElement(name = "REQUESTID")
	public void setREQUESTID(String rEQUESTID) {
		REQUESTID = rEQUESTID;
	}
	
	
	public String getPCFLAG() {
		return PCFLAG;
	}
	@XmlElement(name = "PCFLAG")
	public void setPCFLAG(String pCFLAG) {
		PCFLAG = pCFLAG;
	}
	public String getSTORENUMBER() {
		return STORENUMBER;
	}
	@XmlElement(name = "STORENUMBER")
	public void setSTORENUMBER(String sTORENUMBER) {
		STORENUMBER = sTORENUMBER;
	}
	public String getSOURCETYPE() {
		return SOURCETYPE;
	}
	@XmlElement(name = "SOURCETYPE")
	public void setSOURCETYPE(String sOURCETYPE) {
		SOURCETYPE = sOURCETYPE;
	}
	public String getSUBSIDIARYNUMBER() {
		return SUBSIDIARYNUMBER;
	}
	public void setSUBSIDIARYNUMBER(String sUBSIDIARYNUMBER) {
		SUBSIDIARYNUMBER = sUBSIDIARYNUMBER;
	}
	public String getRECEIPTNUMBER() {
		return RECEIPTNUMBER;
	}
	public void setRECEIPTNUMBER(String rECEIPTNUMBER) {
		RECEIPTNUMBER = rECEIPTNUMBER;
	}
	public String getENTEREDAMOUNT() {
		return ENTEREDAMOUNT;
	}
	
	public void setENTEREDAMOUNT(String eNTEREDAMOUNT) {
		ENTEREDAMOUNT = eNTEREDAMOUNT;
	}
	
	public String getRECEIPTAMOUNT() {
		return RECEIPTAMOUNT;
	}
	public void setRECEIPTAMOUNT(String rECEIPTAMOUNT) {
		RECEIPTAMOUNT = rECEIPTAMOUNT;
	}
	
}
