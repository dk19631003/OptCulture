package org.mq.optculture.model.generalMailSender;

import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.UserDetails;

public class GeneralMailSenderRequest {
	
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private MailInfo MAILINFO;
	public GeneralMailSenderRequest() {
	}
	public GeneralMailSenderRequest(HeaderInfo hEADERINFO,
			UserDetails uSERDETAILS, MailInfo mAILINFO) {
		HEADERINFO = hEADERINFO;
		USERDETAILS = uSERDETAILS;
		MAILINFO = mAILINFO;
	}
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	public MailInfo getMAILINFO() {
		return MAILINFO;
	}
	public void setMAILINFO(MailInfo mAILINFO) {
		MAILINFO = mAILINFO;
	}
	
	
}
