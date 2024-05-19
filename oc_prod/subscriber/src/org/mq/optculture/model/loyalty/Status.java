package org.mq.optculture.model.loyalty;

public class Status {
	private String ERRORCODE;
	private String MESSAGE;
	private String STATUS;
	
	public Status(String eRRORCODE, String mESSAGE, String sTATUS) {
		ERRORCODE = eRRORCODE;
		MESSAGE = mESSAGE;
		STATUS = sTATUS;
	}
	public String getERRORCODE() {
		return ERRORCODE;
	}
	public void setERRORCODE(String eRRORCODE) {
		ERRORCODE = eRRORCODE;
	}
	public String getMESSAGE() {
		return MESSAGE;
	}
	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	
}
