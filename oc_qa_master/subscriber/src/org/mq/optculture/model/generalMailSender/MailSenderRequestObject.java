package org.mq.optculture.model.generalMailSender;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.UserDetails;

public class MailSenderRequestObject extends BaseRequestObject {
	
	
	
	private HeaderInfo header;
	private UserDetails user;
	private MailInfo mailInfo;
	public MailSenderRequestObject() {
	}
	
	public MailSenderRequestObject(HeaderInfo header, UserDetails user,
			MailInfo mailInfo) {
		super();
		this.header = header;
		this.user = user;
		this.mailInfo = mailInfo;
	}

	public HeaderInfo getHeader() {
		return header;
	}
	public void setHeader(HeaderInfo header) {
		this.header = header;
	}
	public UserDetails getUser() {
		return user;
	}
	
	public void setUser(UserDetails user) {
		this.user = user;
	}
	public MailInfo getMailInfo() {
		return mailInfo;
	}
	public void setMailInfo(MailInfo mailInfo) {
		this.mailInfo = mailInfo;
	}
	
	//added for new json format, will be removed in future
	
	private GeneralMailSenderRequest GENERALMAILSENDERREQUEST;
	public MailSenderRequestObject(
			GeneralMailSenderRequest gENERALMAILSENDERREQUEST) {
		GENERALMAILSENDERREQUEST = gENERALMAILSENDERREQUEST;
	}

	public GeneralMailSenderRequest getGENERALMAILSENDERREQUEST() {
		return GENERALMAILSENDERREQUEST;
	}

	public void setGENERALMAILSENDERREQUEST(
			GeneralMailSenderRequest gENERALMAILSENDERREQUEST) {
		GENERALMAILSENDERREQUEST = gENERALMAILSENDERREQUEST;
	}
	
}


