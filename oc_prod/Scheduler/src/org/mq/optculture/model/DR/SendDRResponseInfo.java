package org.mq.optculture.model.DR;

public class SendDRResponseInfo {
	
	private SendDRStatus STATUS;
	
	public SendDRResponseInfo(SendDRStatus status) {
		this.STATUS = status;
	}

	public SendDRStatus getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(SendDRStatus sTATUS) {
		STATUS = sTATUS;
	}

	
	
}
