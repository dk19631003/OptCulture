package org.mq.optculture.model.generalMailSender;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;


public class MailSenderResponseObject extends BaseResponseObject{
	
	private HeaderInfo header;
	private StatusInfo status;
	
	public MailSenderResponseObject() {
	}

	public MailSenderResponseObject(HeaderInfo header, StatusInfo status) {
		super();
		this.header = header;
		this.status = status;
	}

	public HeaderInfo getHeader() {
		return header;
	}

	public void setHeader(HeaderInfo header) {
		this.header = header;
	}

	public StatusInfo getStatus() {
		return status;
	}

	public void setStatus(StatusInfo status) {
		this.status = status;
	}
	
	//added for new json format, will be removed in future
	

	private GeneralMailSenderResponse GENERALMAILSENDERRESPONSE;

	public MailSenderResponseObject(
			GeneralMailSenderResponse gENERALMAILSENDERRESPONSE) {
		GENERALMAILSENDERRESPONSE = gENERALMAILSENDERRESPONSE;
	}

	public GeneralMailSenderResponse getGENERALMAILSENDERRESPONSE() {
		return GENERALMAILSENDERRESPONSE;
	}

	public void setGENERALMAILSENDERRESPONSE(
			GeneralMailSenderResponse gENERALMAILSENDERRESPONSE) {
		GENERALMAILSENDERRESPONSE = gENERALMAILSENDERRESPONSE;
	}




	

}
