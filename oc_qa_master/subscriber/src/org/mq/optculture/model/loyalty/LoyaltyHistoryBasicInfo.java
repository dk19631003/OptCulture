package org.mq.optculture.model.loyalty;

public class LoyaltyHistoryBasicInfo {

	

	private Long TOTALCOUNT;
	private String SOURCETYPE;
	private String STARTDATE;
	private String ENDDATE;
	private String MODE;
	
	public Long getTOTALCOUNT() {
		return TOTALCOUNT;
	}
	public void setTOTALCOUNT(Long tOTALCOUNT) {
		TOTALCOUNT = tOTALCOUNT;
	}
	public String getSOURCETYPE() {
		return SOURCETYPE;
	}
	public void setSOURCETYPE(String sOURCETYPE) {
		SOURCETYPE = sOURCETYPE;
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

}
