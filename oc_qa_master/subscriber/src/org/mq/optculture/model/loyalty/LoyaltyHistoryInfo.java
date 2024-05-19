package org.mq.optculture.model.loyalty;

public class LoyaltyHistoryInfo {
	private String SOURCETYPE;
	private String GETONLYCOUNT;
	private String STARTDATE;
	private String ENDDATE;
	private String MODE;
	public String getSOURCETYPE() {
		return SOURCETYPE;
	}
	public void setSOURCETYPE(String lOYALTYTYPE) {
		SOURCETYPE = lOYALTYTYPE;
	}
	public String getGETONLYCOUNT() {
		return GETONLYCOUNT;
	}
	public void setGETONLYCOUNT(String gETONLYCOUNT) {
		GETONLYCOUNT = gETONLYCOUNT;
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
