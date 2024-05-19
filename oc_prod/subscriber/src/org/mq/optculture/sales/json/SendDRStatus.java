package org.mq.optculture.sales.json;

public class SendDRStatus {
	private String STATUS;
	private String MESSAGE;
	private String ERRORCODE;

	public SendDRStatus(String errorCode, String message,	String status) {
		this.ERRORCODE = errorCode;
		this.MESSAGE = message;
		this.STATUS = status;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getMESSAGE() {
		return MESSAGE;
	}

	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}

	public String getERRORCODE() {
		return ERRORCODE;
	}

	public void setERRORCODE(String eRRORCODE) {
		ERRORCODE = eRRORCODE;
	}

		
}
