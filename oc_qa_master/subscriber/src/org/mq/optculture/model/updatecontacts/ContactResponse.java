package org.mq.optculture.model.updatecontacts;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.MembershipResponse;

public class ContactResponse extends BaseResponseObject{
	private Header header;
	private Status  status;
	private MembershipResponse membership;
	public ContactResponse() {
	}

	public ContactResponse(Header header,Status status) {
		this.header = header;
		this.status = status;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public MembershipResponse getMembership() {
		return membership;
	}

	public void setMembership(MembershipResponse membership) {
		this.membership = membership;
	}

}
