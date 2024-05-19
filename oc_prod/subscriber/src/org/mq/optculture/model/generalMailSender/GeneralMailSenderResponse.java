package org.mq.optculture.model.generalMailSender;

import org.mq.optculture.model.StatusInfo;

public class GeneralMailSenderResponse {
	private StatusInfo STATUSINFO;

	public GeneralMailSenderResponse() {
	}

	public GeneralMailSenderResponse(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}

	public StatusInfo getSTATUSINFO() {
		return STATUSINFO;
	}

	public void setSTATUSINFO(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}
}
