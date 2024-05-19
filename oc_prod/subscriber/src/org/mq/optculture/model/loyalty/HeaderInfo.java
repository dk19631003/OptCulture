package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;
public class HeaderInfo {
	private String REQUESTID;
	private String PCFLAG;
	private String STORENUMBER;
	private String SOURCETYPE;
	private String DOCSID;
	private String SUBSIDIARYNUMBER;
	
	public String getSOURCETYPE() {
		return SOURCETYPE;
	}
	@XmlElement(name = "SOURCETYPE")
	public void setSOURCETYPE(String sOURCETYPE) {
		SOURCETYPE = sOURCETYPE;
	}
	public String getREQUESTID() {
		return REQUESTID;
	}
	@XmlElement(name = "REQUESTID")
	public void setREQUESTID(String rEQUESTID) {
		REQUESTID = rEQUESTID;
	}
	
	public String getSUBSIDIARYNUMBER() {
		return SUBSIDIARYNUMBER;
	}
	@XmlElement(name = "SUBSIDIARYNUMBER")
	public void setSUBSIDIARYNUMBER(String sUBSIDIARYNUMBER) {
		SUBSIDIARYNUMBER = sUBSIDIARYNUMBER;
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
	public String getDOCSID() {
		return DOCSID;
	}
	@XmlElement(name = "DOCSID")
	public void setDOCSID(String dOCSID) {
		DOCSID = dOCSID;
	}
	
}
