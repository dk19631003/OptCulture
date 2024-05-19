package org.mq.optculture.model.loyalty;

public class LoyaltyBasicInfo {
	private Long TOTALCOUNT;
	private String LOYALTYTYPE;
	private String STARTDATE;
	private String ENDDATE;
	private String MODE;
	private String STORENUMBER;
	private String SUBSIDIARYNUMBER;
	
	public Long getTOTALCOUNT() {
		return TOTALCOUNT;
	}
	public void setTOTALCOUNT(Long tOTALCOUNT) {
		TOTALCOUNT = tOTALCOUNT;
	}
	public String getLOYALTYTYPE() {
		return LOYALTYTYPE;
	}
	public void setLOYALTYTYPE(String lOYALTYTYPE) {
		LOYALTYTYPE = lOYALTYTYPE;
	}
	public String getSTARTDATE() {
		return STARTDATE;
	}
	public void setSTARTDATE(String sTARTDATE) {
		STARTDATE = sTARTDATE;
	}
	public String getENDDATE() {
		return ENDDATE;
	}
	public void setENDDATE(String eNDDATE) {
		ENDDATE = eNDDATE;
	}
	public String getMODE() {
		return MODE;
	}
	public void setMODE(String mODE) {
		MODE = mODE;
	}
	public String getSTORENUMBER() {
		return STORENUMBER;
	}
	public void setSTORENUMBER(String sTORENUMBER) {
		STORENUMBER = sTORENUMBER;
	}
	public String getSUBSIDIARYNUMBER() {
		return SUBSIDIARYNUMBER;
	}
	public void setSUBSIDIARYNUMBER(String sUBSIDIARYNUMBER) {
		SUBSIDIARYNUMBER = sUBSIDIARYNUMBER;
	}
}