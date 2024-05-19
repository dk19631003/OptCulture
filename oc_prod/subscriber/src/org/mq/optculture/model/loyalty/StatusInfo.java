package org.mq.optculture.model.loyalty;

public class StatusInfo {
	private String ERRORCODE;
	private String MESSAGE;
	private String STATUS;
	
	public StatusInfo() {
	}
	
	public StatusInfo(String errorCode, String message, String status) {
		super();
		this.ERRORCODE = errorCode;
		this.MESSAGE = message;
		this.STATUS = status;
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
