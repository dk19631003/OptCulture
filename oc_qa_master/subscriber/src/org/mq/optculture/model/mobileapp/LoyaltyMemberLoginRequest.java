package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.BaseRequestObject;

import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.RequestHeader;

public class LoyaltyMemberLoginRequest extends BaseRequestObject{
	
	private static final long serialVersionUID = 6831961210923846896L;
	//private InquiryHeadRequest header;
	private RequestHeader header;
	private Membership membership;
	private LoyaltyUser user;
	//private Amount amount;
	
	public LoyaltyMemberLoginRequest(){
		//Default Constructor
	}

	public RequestHeader getHeader() {
		return header;
	}

	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	public LoyaltyUser getUser() {
		return user;
	}

	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	


}
